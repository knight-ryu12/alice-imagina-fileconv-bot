package team.digitalfairy.felis.commands.sw25;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import team.digitalfairy.felis.abs.Command;
import team.digitalfairy.felis.abs.SlashCommandContext;

import java.util.Arrays;

public class ptableQueryCommand extends Command {
    @Override
    public SlashCommandData getSlashCommand() {
        return Commands.slash("queryptable", "威力表をクエリする")
                .addOption(OptionType.INTEGER,"pvalue", "威力値",true);
    }

    @Override
    public boolean onInvoke(SlashCommandContext data) {
        int pValue = 0;
        for(OptionMapping mapping : data.om) {
            if (mapping.getName().equals("pvalue")) {
                pValue = mapping.getAsInt();
            }
        }

        data.e.reply(Arrays.toString(SW2cConstant.C[pValue])).setEphemeral(true).queue();

        return true;
    }

    @Override
    public String getCommandName() {
        return "queryptable";
    }
}
