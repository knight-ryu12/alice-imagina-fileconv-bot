package team.digitalfairy.felis;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import team.digitalfairy.felis.event.SelectMenuInteractionListener;
import team.digitalfairy.felis.event.SlashCommandListener;
import team.digitalfairy.felis.init.SlashCommandRegistry;

//import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
//import java.util.ResourceBundle;

public class Main {
    public static void main(String[] args) {
        Properties pr = new Properties();
        try(FileInputStream fis = new FileInputStream("config.properties")) {
            pr.load(fis);
            System.out.println(pr.getProperty("version"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        JDABuilder jb = JDABuilder.createDefault(pr.getProperty("token"));
        // Register slash command
        jb.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        JDA jda = jb.build();

        SlashCommandRegistry.registerCommands(jda);
        jda.addEventListener(new SlashCommandListener());
        jda.addEventListener(new SelectMenuInteractionListener());
        Thread shutdownGracefully = new Thread(jda::shutdown);

        Runtime.getRuntime().addShutdownHook(shutdownGracefully);


    }
}

