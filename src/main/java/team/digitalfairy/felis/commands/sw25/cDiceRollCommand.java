package team.digitalfairy.felis.commands.sw25;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.digitalfairy.felis.abs.Command;
import team.digitalfairy.felis.abs.SlashCommandContext;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class cDiceRollCommand extends Command {
    Logger log = LoggerFactory.getLogger(cDiceRollCommand.class);
    ThreadLocalRandom thr;
    public cDiceRollCommand() {
        thr = ThreadLocalRandom.current();
    }

    @Override
    public SlashCommandData getSlashCommand() {
        return Commands.slash("sw2damagedice", "SW2.0/SW2.5 crit-based dice roll command")
                .addOption(OptionType.INTEGER, "cvalue", "Critical Value (クリティカル値)",true)
                .addOption(OptionType.INTEGER,"pvalue","Power Value (威力値)",true)
                .addOption(OptionType.INTEGER,"offset", "Offset")
                .addOption(OptionType.BOOLEAN, "henten", "運命変転！");
        // /cdice 20 3
    }

    @Override
    public boolean onInvoke(SlashCommandContext data) {


        int pValue = -1;
        int cValue = -1;
        int offset = 0;

        boolean henten = false;

        log.debug("OM {}", data.om);
        for(OptionMapping mapping : data.om) {
            switch (mapping.getName()) {
                case "cvalue" -> cValue = mapping.getAsInt();
                case "pvalue" -> pValue = mapping.getAsInt();
                case "offset" -> offset = mapping.getAsInt();
                case "henten" -> henten = mapping.getAsBoolean();
            }
        }

        log.info("Use setting: cV{} pV{} ofs{} h{}",cValue,pValue,offset,henten);

        // Do not process if the crit value is not set, or illegal value.
        log.info("cValue sanity check");
        if(cValue == -1) {
            // shouldn't happen
            data.e.reply("Error: cValue is less than -1!").queue();
            return false;
        }
        if(cValue <= 7) {
            log.info("Fix: cV 8");
            cValue = 8; // 7以下はありえない
        } else if(cValue >= 13) cValue = 13; // 13以上はクリティカル発生しないが保護する。

        log.info("pValue sanity check");
        if(pValue >= 100) {
            log.info("Fix: pV 100");
            pValue = 100;
        }
        if(pValue <= -1) {
            // shouldn't happen
            data.e.reply("Error: pValue is less than -1!").queue();
            return false;
        }

        // Sanity Check All Ok!
        data.e.deferReply().queue();

        boolean doNext = false;
        int rollCounter = 0;
        int totalDamageTally = 0;

        StringBuilder sb = new StringBuilder();

        sb.append(String.format("威力ダイス: cV %d pV %d cT %s h %b\n",cValue,pValue, Arrays.toString(SW2cConstant.C[pValue]),henten));

        do {
            rollCounter++;
            doNext = false;
            int dice0 = thr.nextInt(0,6) + 1;
            int dice1 = thr.nextInt(0,6) + 1;

            sb.append(String.format("%d回目 ダイス1:%d ダイス2:%d\n",rollCounter,dice0,dice1));

            if(henten) {
                dice0 = SW2cConstant.UnnmeiHentenTable[dice0-1];
                dice1 = SW2cConstant.UnnmeiHentenTable[dice1-1];
                sb.append(String.format("運命変転！ ダイス1:%d ダイス2:%d\n",dice0,dice1));
                henten = false;
            }

            int diceResult = dice0 + dice1;
            int rolledDmg = SW2cConstant.C[pValue][diceResult];

            if(rolledDmg == -1 && dice0 == 1 && dice1 == 1) {
                // FIXME: もし人間のキャラクターが運命変転を使うとこのフェイルセーフは機能しない可能性がある。
                // 初回だったら
                if(rollCounter == 1) {
                    // 1回目のロール
                    log.info("Failed to roll");
                    sb.append("自動失敗 経験値50点\n");
                    // 50点
                } else {
                    sb.append("失敗！");
                }
                break;
            }
            sb.append(String.format("ダイスリザルト:%d ロールダメージ:%d\n",diceResult,rolledDmg));


            if(rolledDmg >= cValue) {
                sb.append("クリティカル！\n");
                // Critical
                doNext = true;
            }

            totalDamageTally += rolledDmg;

        } while(doNext);

        totalDamageTally += offset;

        sb.append(String.format("トータルダメージ %d",totalDamageTally));


        data.ih.editOriginal(sb.toString()).queue();

        return true;
    }

    @Override
    public String getCommandName() {
        return "cdice";
    }
    // Rolls Crit-Table based dice



}
