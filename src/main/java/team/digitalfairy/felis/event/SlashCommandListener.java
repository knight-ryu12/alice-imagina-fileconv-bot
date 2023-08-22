package team.digitalfairy.felis.event;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.digitalfairy.felis.abs.Command;
import team.digitalfairy.felis.abs.SlashCommandContext;
import team.digitalfairy.felis.init.SlashCommandRegistry;

import java.util.Arrays;

public class SlashCommandListener extends ListenerAdapter {

    private final Logger log = LoggerFactory.getLogger(SlashCommandListener.class);

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        // search the event data

        Command cmd = SlashCommandRegistry.commands.get(event.getName());
        if(cmd == null) return;

        // Prepare SlashCommandContext
        SlashCommandContext ctx = new SlashCommandContext(
                null,
                event.getGuild(),
                event.getMember(),
                event.getHook(),
                event.getOptions(),
                event.getName(),
                event,
                event.getJDA()
        );

        try {
            boolean result = cmd.onInvoke(ctx);
        } catch (Exception e) {
            log.error("Fatal Error",e);
            event.deferReply(true).queue();

            event.getHook().setEphemeral(true);
            event.getHook().editOriginal(
                    "An error has occurred!\n"+
                            "Error: " + e.getMessage() + "\n" +
                    "```" +
                            Arrays.toString(e.getStackTrace())
                    + "```"
            ).queue();
        }


    }
}
