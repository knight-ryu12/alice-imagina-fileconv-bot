package team.digitalfairy.felis.event;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ModalListener extends ListenerAdapter {
    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        // Check the eventId... Do you have a data?
        if(event.getModalId().equals("tag")) {
            String sb = event.getValue("tagName").getAsString();
            String b = event.getValue("tagBody").getAsString();

            event.reply("Thanks!").queue();
        }

    }
}
