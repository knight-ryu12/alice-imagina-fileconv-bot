package team.digitalfairy.felis.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.digitalfairy.felis.Main;
import team.digitalfairy.felis.abs.Command;
import team.digitalfairy.felis.abs.SlashCommandContext;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SteamSearchCommand extends Command {

    final OkHttpClient client;
    ObjectMapper om;
    final Logger log = LoggerFactory.getLogger(SteamSearchCommand.class);

    final HikariConfig conf = new HikariConfig();
    HikariDataSource ds;

    public static Cache<String, Long> listQueried; // Since it will be handled with appid, no need to store everything;
    // query the database to handle appid -> name conversion


    public SteamSearchCommand() {
        listQueried = CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build();

        log.info("Init");

        log.info("Creating FF cache directories");
        try {
            Files.createDirectories(Paths.get("./cache_steam"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("OK");

        // Init Okhttp
        client = new OkHttpClient();
        om = new ObjectMapper();



        // initialise table on boot... using the address, port and database...
        // TODO: Think about how to queue the sync timer
        log.info("Connecting to the server");
        String user = Main.pr.getProperty("user");
        log.info("Logging in as "+user);
        conf.setJdbcUrl("jdbc:postgresql://localhost:5432/"+Main.pr.getProperty("database"));
        conf.setUsername(user);
        conf.setPassword(Main.pr.getProperty("password"));
        ds = new HikariDataSource(conf);
        log.info("Creating table if you don't have any...");

        boolean tableExists = false;
        try(Connection con = ds.getConnection()) {
            Statement ss = con.createStatement();
            //ResultSet rs = ss.executeQuery("SHOW TABLES LIKE 'steam_appid'");
            ResultSet rs = ss.executeQuery("SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = '" + "steam_appid" + "')");
            if(rs.next()) {
                tableExists = rs.getBoolean(1);
            }
            rs.close();
            ss.close();
            if(!tableExists) {
                log.info("Table doesn't exist, creating one...");
                Statement sv = con.createStatement();
                sv.execute("CREATE TABLE steam_appid (appid INT PRIMARY KEY NOT NULL, name TEXT NOT NULL, store_name TEXT,last_modified BIGINT NOT NULL, price_change_number BIGINT NOT NULL)");
                sv.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        log.info("Syncing against the steam id");

        boolean haveMoreResults = false;
        int last_apppid = 0;
        int reqctr = 0;
        // Set the last modified, if -1, set to 0 to fullsync
        long lastMod = Integer.parseInt(Main.pr.getProperty("last_steamsync","0"));
        if(lastMod == 0) {
            Main.pr.setProperty("last_steamsync", String.valueOf(Instant.now().getEpochSecond()));
            try {
                Main.pr.store(new FileWriter("config.properties"),null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        do {
            // this will be fairly large...
            // GET https://api.steampowered.com/ISteamApps/GetAppList/v2/
            // GET https://api.steampowered.com/IStoreService/GetAppList/v1/ with key
            HttpUrl httpUrl = HttpUrl.parse("https://api.steampowered.com/IStoreService/GetAppList/v1").newBuilder()
                    .addQueryParameter("include_games", "true")
                    .addQueryParameter("max_results", "10000")
                    .addQueryParameter("if_modified_since", String.valueOf(lastMod))
                    .addQueryParameter("last_appid", String.valueOf(last_apppid))
                    .addQueryParameter("l","english")
                    .addQueryParameter("key", Main.pr.getProperty("steam_api_key"))
                    .build();

            Request rq = new Request.Builder().url(httpUrl).build();
            try (Response r = client.newCall(rq).execute()) {
                if (r.body() != null) {
                    JsonNode rootNode = om.readTree(r.body().byteStream());

                    JsonNode hmr_tt = rootNode.get("response").get("have_more_results");
                    if(hmr_tt == null) {
                        haveMoreResults = false;
                    } else {
                        haveMoreResults = hmr_tt.asBoolean(false);
                        last_apppid = rootNode.get("response").get("last_appid").asInt();
                    }

                    log.info("Query done, more results "+haveMoreResults+" lastappid "+last_apppid);
                    
                    JsonNode h_apps = rootNode.get("response").get("apps");
                    // TODO: Actually implement
                    AtomicInteger counter = new AtomicInteger();
                    h_apps.forEach((v) -> counter.getAndIncrement());
                    log.info("Got " + counter.get() + " items...");

                    Statement statement = null;
                    try (Connection con = ds.getConnection()) {
                        PreparedStatement ps = con.prepareStatement("INSERT INTO steam_appid(appid,name,last_modified,price_change_number) VALUES (?,?,?,?) " +
                                "on conflict(appid) do update set name = EXCLUDED.name, last_modified = EXCLUDED.last_modified, price_change_number = EXCLUDED.price_change_number");
                        statement = con.createStatement();
                        statement.execute("BEGIN");
                        log.info("Commit start!");
                        h_apps.forEach((v) -> {
                            try {
                                ps.setInt(1,v.get("appid").asInt());
                                ps.setString(2,v.get("name").asText());
                                ps.setInt(3,v.get("last_modified").asInt());
                                ps.setInt(4,v.get("price_change_number").asInt());

                                ps.executeUpdate();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        statement.execute("COMMIT");
                        log.info("Commit End!");
                    } catch (SQLException | RuntimeException e) {
                        assert statement != null;
                        statement.execute("ROLLBACK");
                        throw new RuntimeException(e);
                    }
                }
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        } while(haveMoreResults);

        // Add supplemental data
        // Prevent syncing::: Use steamapis.com to forcibly get a "english" data
        HttpUrl httpUrl = HttpUrl.parse("https://api.steampowered.com/ISteamApps/GetAppList/v2/").newBuilder()
                .addQueryParameter("l","english")
                .build(); // No param


        Request rq = new Request.Builder().url(httpUrl).build();
        try (Response r = client.newCall(rq).execute()) {
            if (r.body() != null) {
                JsonNode rootNode = om.readTree(r.body().byteStream());
                // json -> applist -> apps
                JsonNode apps = rootNode.get("applist").get("apps");

                Statement statement = null;
                try (Connection con = ds.getConnection()) {
                    // Modify a already created database, with supplimental data
                    PreparedStatement ps = con.prepareStatement("UPDATE steam_appid SET store_name = ? WHERE appid = ?");
                    statement = con.createStatement();
                    statement.execute("BEGIN");

                    log.info("Commit start!");
                    apps.forEach((v) -> {
                        try {
                            ps.setString(1,v.get("name").asText());
                            ps.setInt(2,v.get("appid").asInt());

                            ps.executeUpdate();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    statement.execute("COMMIT");
                    log.info("Commit End!");
                } catch (SQLException e) {
                    assert statement != null;
                    statement.execute("ROLLBACK");
                    throw new RuntimeException(e);
                }

            }


        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SlashCommandData getSlashCommand() {
        return Commands.slash("steamsearch","Search steam games")
                .addOption(OptionType.STRING,"search","Search Query", true);
    }

    @Override
    public boolean onInvoke(SlashCommandContext data) {
        data.e.deferReply().queue();
        String newId = "steam_menu:id/"+data.e.getId();
        log.info("Got message, created Id = "+newId);

        // Do you have information about the name?
        try (Connection con = ds.getConnection()) {
            PreparedStatement ps_size = con.prepareStatement("SELECT COUNT(*) FROM steam_appid WHERE name LIKE ?");

            log.info("Search "+String.valueOf(data.om.get(0).getAsString()));
            ps_size.setString(1, '%' + String.valueOf(data.om.get(0).getAsString()) + '%');

            ResultSet rs = ps_size.executeQuery();
            rs.next();

            data.ih.editOriginal("Got size "+rs.getInt(1)).queue();
        } catch (SQLException e) {
            // Error
            data.ih.setEphemeral(true).editOriginal("Error! "+e.getStackTrace()[0]+"\n"+e.getStackTrace()[1]).queue();
            throw new RuntimeException(e);
        }

        return true;
    }

    @Override
    public String getCommandName() {
        return null;
    }
}
