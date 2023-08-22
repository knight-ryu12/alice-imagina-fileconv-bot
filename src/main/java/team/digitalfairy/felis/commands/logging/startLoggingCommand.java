package team.digitalfairy.felis.commands.logging;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import team.digitalfairy.felis.abs.Command;
import team.digitalfairy.felis.abs.SlashCommandContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class startLoggingCommand extends Command {

    private final Path logPath = Path.of(".\\log");

    public startLoggingCommand() {
        // If "log" folder does not exist, create one
        if(!Files.exists(logPath)) {
            try {
                Files.createDirectory(logPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Recover the log list from somewhere; when bot dies.
        // Add a sync on shutdown hook.
        // Add a periodic sync
    }

    @Override
    public SlashCommandData getSlashCommand() {
        return Commands.slash("startlog","starts Logging at set channel, with reactive messages, and timeout"); //
    }

    @Override
    public boolean onInvoke(SlashCommandContext data) {

        // Do we have a channel listed, as a target? if not so, add to the list and sync
        // Do we have a file to log to? create one if not exists
        // Do we have to rotate log? if so, gzip compress them.
        // prepare some kind of "log stop"; maybe when called stoplog, gzip immediately?
        // Append log at certain format; to be determined.

        return true;
    }

    @Override
    public String getCommandName() {
        return "startlog";
    }
}
