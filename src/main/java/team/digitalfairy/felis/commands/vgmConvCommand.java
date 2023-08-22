package team.digitalfairy.felis.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import team.digitalfairy.felis.abs.Command;
import team.digitalfairy.felis.abs.SlashCommandContext;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class vgmConvCommand extends Command {
    @Override
    public SlashCommandData getSlashCommand() {
        return Commands.slash("vgmconv","Convert VGM to FLAC")
                .addOption(OptionType.ATTACHMENT,"vgm","Attach valid VGM file here", true);
    }

    @Override
    public boolean onInvoke(SlashCommandContext data) {
        File ff = null;
        try {
            ff = File.createTempFile("vgm-",".vgm");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        data.e.deferReply().queue();

        Message.Attachment file = data.om.get(0).getAsAttachment();
        CompletableFuture<File> f = file.getProxy().downloadToFile(ff)
                .exceptionally(
                e -> {
                    e.printStackTrace();
                    data.ih.setEphemeral(true).sendMessage(e.getMessage()).queue();
                    return null;
                });

        if(f == null) return false;
        data.ih.setEphemeral(true).sendMessage("Got file...!").queue();

        // pass to vgmplay.exe

        // then delete file
        boolean res = ff.delete();
        if(!res) return false;

        // convert that to FLAC

        // upload to discord

        // delete both WAV and FLAC

        return true;
    }

    @Override
    public String getCommandName() {
        return null;
    }
}
