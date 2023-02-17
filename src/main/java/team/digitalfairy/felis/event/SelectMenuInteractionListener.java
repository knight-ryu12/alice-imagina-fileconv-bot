package team.digitalfairy.felis.event;

import com.fasterxml.jackson.databind.JsonNode;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.digitalfairy.felis.commands.GOGSearchCommand;

import java.util.List;
import java.util.Objects;

public class SelectMenuInteractionListener extends ListenerAdapter {
    Logger log = LoggerFactory.getLogger(SelectMenuInteractionListener.class);
    public SelectMenuInteractionListener() {

    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        // Got Interaction events? call whatever
        if(event.getComponentId().startsWith("menu:id")) {
            // got Id
            log.info("GotId "+ event.getComponentId());

            JsonNode jn = GOGSearchCommand.listQueried.getIfPresent(event.getComponentId());
            if(jn == null) {
                event.editMessage("No query is made; please issue new command!").setReplace(true).queue();

                return;
            }

            // Extracted. remove from listQueried
            List<String> selected = event.getValues();
            log.info("User queried "+ selected.get(0));

            JsonNode jn_sel = jn.get("products").get(Integer.parseInt(selected.get(0)));

            // Compose Embed
            EmbedBuilder eb = new EmbedBuilder()
                        .setTitle("GOG Search Result")
                        .addField("title",jn_sel.get("title").asText(),true)
                                .setImage("https:"+jn_sel.get("image").asText()+".png");

            event.editMessageEmbeds(eb.build()).setReplace(true).queue();


            // Do we have queued message?
            GOGSearchCommand.listQueried.invalidate(event.getComponentId());




        }


    }
}
