package team.digitalfairy.felis.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.graph.Graph;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.digitalfairy.felis.abs.Command;
import team.digitalfairy.felis.abs.SlashCommandContext;

import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GOGSearchCommand extends Command {
    OkHttpClient client;
    ObjectMapper om;

    Logger log = LoggerFactory.getLogger(GOGSearchCommand.class);

    public static Cache<String, JsonNode> listQueried;
    public GOGSearchCommand() {
        client = new OkHttpClient();
        om = new ObjectMapper();

        listQueried = CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build();

    }

    @Override
    public SlashCommandData getSlashCommand() {
        return Commands.slash("gogsearch","Search GOG Games")
                .addOption(OptionType.STRING,"search","Search Query", true);
    }

    @Override
    public boolean onInvoke(SlashCommandContext data) {
        data.e.deferReply().queue();
        String newId = "gog_menu:id/"+data.e.getId();
        log.info("Got message, created Id = "+newId);


        // Build HttpUrl
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("https")
                .host("www.gog.com")
                .addPathSegments("games/ajax/filtered")
                .addQueryParameter("mediaType","game")
                .addQueryParameter("search",data.om.get(0).getAsString())
                .addQueryParameter("limit","10")
                .build();

        Request request = new Request.Builder()
                .url(httpUrl)
                .build();

        try (Response r = client.newCall(request).execute()) {
            if (r.body() != null) {
                StringSelectMenu.Builder ssm = StringSelectMenu.create(newId);
                JsonNode rootNode = om.readTree(r.body().byteStream());
                int totalResult = rootNode.get("totalGamesFound").asInt();
                JsonNode products = rootNode.get("products");
                listQueried.put(newId,rootNode);

                for(int i=0; i<totalResult; i++) {
                    ssm.addOption(products.get(i).get("title").asText(), String.valueOf(i));

                }

                StringSelectMenu sm = ssm.build();
                /*

                 */
                data.ih.sendMessage("Please choose one from the list.").addActionRow(sm).queue();
            } else {
                data.ih.sendMessage("Empty reply!").queue();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    @Override
    public String getCommandName() {
        return null;
    }
}
