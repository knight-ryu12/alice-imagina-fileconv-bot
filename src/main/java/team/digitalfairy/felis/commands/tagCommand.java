package team.digitalfairy.felis.commands;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import team.digitalfairy.felis.abs.Command;
import team.digitalfairy.felis.abs.SlashCommandContext;

import javax.swing.*;

public class tagCommand extends Command {
    @Override
    public SlashCommandData getSlashCommand() {
        return Commands.slash("tag","Database in the thing");
    }

    @Override
    public boolean onInvoke(SlashCommandContext data) {
        // generate a modal
        TextInput tagName = TextInput.create("tagname","Tag Name", TextInputStyle.SHORT)
                .setPlaceholder("Tag Name")
                .setMinLength(5)
                .setMaxLength(20)
                .build();

        TextInput tagBody = TextInput.create("body", "Body", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Body")
                .setMinLength(20)
                .setMaxLength(1000)
                .setRequired(true)
                .build();

        Modal modal = Modal.create("tag","Tag Data")
                .addComponents(ActionRow.of(tagName), ActionRow.of(tagBody))
                .build();

        data.e.replyModal(modal).queue();

        return true;
    }

    @Override
    public String getCommandName() {
        return "tag";
    }
}
