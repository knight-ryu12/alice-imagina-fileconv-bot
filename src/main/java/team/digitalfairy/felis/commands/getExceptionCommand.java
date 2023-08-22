package team.digitalfairy.felis.commands;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import team.digitalfairy.felis.abs.Command;
import team.digitalfairy.felis.abs.SlashCommandContext;

public class getExceptionCommand extends Command {

    @Override
    public SlashCommandData getSlashCommand() {
        return Commands.slash("exch","Cause an exception");
    }

    @Override
    public boolean onInvoke(SlashCommandContext data) {
        throw new RuntimeException("DEADDEAD");
        //return true;
    }

    @Override
    public String getCommandName() {
        return "exch";
    }
}
