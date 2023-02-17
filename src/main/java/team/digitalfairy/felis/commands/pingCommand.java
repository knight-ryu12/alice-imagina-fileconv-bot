package team.digitalfairy.felis.commands;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import team.digitalfairy.felis.abs.Command;
import team.digitalfairy.felis.abs.SlashCommandContext;

public class pingCommand extends Command {

    @Override
    public SlashCommandData getSlashCommand() {
        return Commands.slash("ping","Calc Ping");
    }

    @Override
    public boolean onInvoke(SlashCommandContext data) {
        long time = System.currentTimeMillis();
        data.e.reply("Pong!")
                .flatMap(v -> data.e.getHook().editOriginalFormat("Pong %d ms",System.currentTimeMillis() - time)
                ).queue();

        return true;
    }

    @Override
    public String getCommandName() {
        return "ping";
    }
}
