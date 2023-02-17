package team.digitalfairy.felis.init;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import team.digitalfairy.felis.abs.Command;
import team.digitalfairy.felis.commands.GOGSearchCommand;
import team.digitalfairy.felis.commands.pingCommand;
import team.digitalfairy.felis.commands.vgmConvCommand;

import java.util.*;
import java.util.stream.Collectors;

public class SlashCommandRegistry {

    public static Map<String,Command> commands = new HashMap<>();

    private static void initCommands() {
        commands.put("ping",new pingCommand());
        commands.put("vgmconv", new vgmConvCommand());
        commands.put("gogsearch", new GOGSearchCommand());
    }

    public static void registerCommands(JDA jda) {
        initCommands();
        // create list of SlashCommandData
        List<SlashCommandData> data = commands.values().stream()
                .map(Command::getSlashCommand)
                .collect(Collectors.toList());

        // actually check key against SlashCommandData
        for (SlashCommandData sda: data) {
            if(commands.get(sda.getName()) == null) {
                throw new NoSuchElementException("Stray Command Detected; found "+sda.getName());
            }
        }

        jda.updateCommands().addCommands(data).queue();
    }


}
