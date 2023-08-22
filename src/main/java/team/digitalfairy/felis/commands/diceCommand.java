package team.digitalfairy.felis.commands;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.digitalfairy.felis.abs.Command;
import team.digitalfairy.felis.abs.SlashCommandContext;

import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Map;

public class diceCommand extends Command {
    private Logger log = LoggerFactory.getLogger(diceCommand.class);
    private SecureRandom sr;
    public diceCommand() {
        for(String a : Security.getAlgorithms("SecureRandom")) {
            log.info("SR Provider {}",a);
        }

        for(Provider pr : Security.getProviders()) {
            for(Map.Entry<Object, Object> e : pr.entrySet()) {
                if(e.getKey().toString().startsWith("SecureRandom")) {
                    log.info("[p:{}] {} = {}", pr, e.getKey(), e.getValue());
                }
            }
        }

        try {
            sr = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        log.info("Using p{} alg{}",sr.getProvider(),sr.getAlgorithm());
    }

    @Override
    public SlashCommandData getSlashCommand() {
        return Commands.slash("dice","Run 1d6 dice")
                .addOption(OptionType.INTEGER, "count", "Count of dices", false)
                .addOption(OptionType.INTEGER,"side","Choose a side",false);
    }

    @Override
    public boolean onInvoke(SlashCommandContext data) {
        StringBuilder sb = new StringBuilder();
        // If there is any arguments; parse them
        // if there is none, default to 1d6.
        // data.om.get(0).getAsString()
        int count = 1;
        int value = 6;

        log.debug("OM {}", data.om);
        for(OptionMapping mapping : data.om) {
            switch (mapping.getName()) {
                case "count" -> count = mapping.getAsInt();
                case "value" -> value = mapping.getAsInt();
            }
        }

        if(count >= 20) {
            count = 20;
            // Limit;
        }

        if(value >= 100) {
            // Limit Value:
            value = 100;
        }


        for(int i=0; i<count; i++) {
            int v = sr.nextInt(0,value) + 1;
            // Prepare message to send
            sb.append(String.format("Roll count%d [%dd%d] %d\n",i,count,value,v));
        }

        data.e.reply(sb.toString()).queue();

        return true;
    }

    @Override
    public String getCommandName() {
        return "dice";
    }
}
