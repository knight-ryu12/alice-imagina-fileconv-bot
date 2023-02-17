package team.digitalfairy.felis.abs;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public abstract class Command implements ICommand {
    public abstract SlashCommandData getSlashCommand();
    public abstract boolean onInvoke(SlashCommandContext data);

    public abstract String getCommandName();
}

