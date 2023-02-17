package team.digitalfairy.felis.abs;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.List;

public class SlashCommandContext {

    public Message message;
    public Guild guild;
    public Member member;
    public InteractionHook ih;
    public String name;
    public List<OptionMapping> om;
    public SlashCommandInteractionEvent e; // Current Event
    public JDA jda;


    public SlashCommandContext(Message message, Guild guild, Member member, InteractionHook ih, List<OptionMapping> om, String name, SlashCommandInteractionEvent e, JDA jda) {
        this.message = message;
        this.guild = guild;
        this.member = member;
        this.ih = ih;
        this.name = name;
        this.e = e;
        this.om = om;
        this.jda = jda;
    }
}