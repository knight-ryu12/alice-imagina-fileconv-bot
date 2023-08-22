package team.digitalfairy.felis.commands.sw25;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.digitalfairy.felis.abs.Command;
import team.digitalfairy.felis.abs.SlashCommandContext;

public class randomCharacterCreationCommand extends Command {

    private final Logger log = LoggerFactory.getLogger(randomCharacterCreationCommand.class);

    private static final int[][] ROLL_TABLE = {
            {}
    };

    private enum SPECIES {
        HUMAN("human"),
        ELF("elf"),
        DWARF("dwarf"),TABBIT, RUNEFOLK, NIGHTMARE, LYCAN;


        SPECIES(String species_name) {
        }
    }

    @Override
    public SlashCommandData getSlashCommand() {
        return Commands.slash("sw25randomchar","Create random character with RNGing.")
                .addOptions(
                        new OptionData(OptionType.STRING,"species","種族",true)
                                .addChoice("人間","human")
                                .addChoice("エルフ", "elf")
                                .addChoice("ドワーフ", "dwarf")
                                .addChoice("タビット","tabbit")
                                .addChoice("ルーンフォーク","runefolk")
                                .addChoice("ナイトメア", "nightmare") // FIXME: ナイトメアをネストする
                                .addChoice("リカント","lycan")
                )
                .addOption(OptionType.STRING,"name","名前",true);
    }

    @Override
    public boolean onInvoke(SlashCommandContext data) {
        log.debug("OM {}", data.om);

        String spcId = "";
        String chrName = "";

        // Test for name
        for(OptionMapping mapping : data.om) {
            switch (mapping.getName()) {
                case "name" -> chrName = mapping.getAsString();
                case "species" -> spcId  = mapping.getAsString();

            }
        }

        return true;
    }

    @Override
    public String getCommandName() {
        return "sw25randomchar";
    }
}
