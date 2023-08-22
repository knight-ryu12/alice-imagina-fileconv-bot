package team.digitalfairy.felis.init;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.digitalfairy.felis.abs.Command;
import team.digitalfairy.felis.commands.*;
import team.digitalfairy.felis.commands.sw25.cDiceRollCommand;
import team.digitalfairy.felis.commands.sw25.ptableQueryCommand;
import team.digitalfairy.felis.commands.sw25.randomCharacterCreationCommand;
import team.digitalfairy.felis.event.SlashCommandListener;

import java.util.*;
import java.util.stream.Collectors;

public class SlashCommandRegistry {

    public static Map<String,Command> commands = new HashMap<>();
    private static Logger log = LoggerFactory.getLogger(SlashCommandRegistry.class);

    private static void initCommands() {
        commands.put("ping",new pingCommand());
        //commands.put("vgmconv", new vgmConvCommand());
        commands.put("gogsearch", new GOGSearchCommand());
        //commands.put("steamsearch", new SteamSearchCommand());
        commands.put("dice", new diceCommand());
        commands.put("exch", new getExceptionCommand());
        commands.put("tag", new tagCommand());

        // SW2.5
        commands.put("sw2damagedice", new cDiceRollCommand());
        commands.put("queryptable", new ptableQueryCommand());
        commands.put("sw25randomchar",new randomCharacterCreationCommand());
    }

    public static void registerCommands(JDA jda) {
        initCommands();
        // create list of SlashCommandData
        List<SlashCommandData> data = commands.values().stream()
                .map(Command::getSlashCommand)
                .collect(Collectors.toList());

        // actually check key against SlashCommandData
        for (SlashCommandData sda: data) {
            log.info("Adding command: "+sda.getName());
            if(commands.get(sda.getName()) == null) {
                throw new NoSuchElementException("Stray Command Detected; found "+sda.getName());
            }
        }

        jda.updateCommands().addCommands(data).queue();
    }
}
