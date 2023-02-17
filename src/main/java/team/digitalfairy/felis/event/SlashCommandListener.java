package team.digitalfairy.felis.event;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import team.digitalfairy.felis.abs.Command;
import team.digitalfairy.felis.abs.SlashCommandContext;
import team.digitalfairy.felis.init.SlashCommandRegistry;

public class SlashCommandListener extends ListenerAdapter {
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
            cmd.onInvoke(ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
