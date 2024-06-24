package si.bismuth.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.minecraft.text.*;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.StringUtils;
import si.bismuth.BismuthServer;

import javax.security.auth.login.LoginException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class DCBot extends ListenerAdapter {
	private final static long BismuthID = 635252849571266580L;
	private final static long ChannelID = 635254222916419590L;
	private final static String channelURL = String.format("https://discordapp.com/channels/%d/%d/", BismuthID, ChannelID);
	private final static String PREFIX = ";";
	private final boolean isTestServer;
	private final JDA jda;
	public static final LocalDateTime BOT_START_TIME = LocalDateTime.now();

	public DCBot(String token, boolean isOnlineMode) throws LoginException, InterruptedException {
		isTestServer = !isOnlineMode;

		EnumSet<GatewayIntent> intents = EnumSet.of(
				// Enables MessageReceivedEvent for guild (also known as servers)
				GatewayIntent.GUILD_MESSAGES,
				GatewayIntent.MESSAGE_CONTENT
		);

		this.jda = JDABuilder
				.createLight(token, intents)
				.addEventListeners(this)
				.build()
				.awaitReady();
		this.sendToDiscord("Server started!");
	}

	public void shutDownBot() {
		this.sendToDiscord("Server stopped!");
		this.jda.shutdown();
	}

	public void sendToDiscord(String message) {
		final TextChannel channel = this.jda.getTextChannelById(DCBot.ChannelID);
		if (channel != null) {
			if (isTestServer) {
				message = "\uD83E\uDDEA" + message;
			}

			channel.sendMessage(message).queue();
		}
	}

	public void sendAdvancementMessage(Text component) {
		this.sendToDiscord("**\uD83C\uDF8A " + component.getString() + "**");
	}

	public void sendDeathMessage(Text component) {
		this.sendToDiscord("**\uD83D\uDD71 " + component.getString() + "**");
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getMember() == null || event.getAuthor().isBot() || event.getGuild().getIdLong() != BismuthID) {
			return;
		}

		// TODO: handle DC2MC images
		final MessageChannel channel = event.getChannel();
		final Member member = event.getMember();
		final Message message = event.getMessage();
		final String content = message.getContentDisplay();
		final List<Message.Attachment> attachments = message.getAttachments();
		if (channel.getIdLong() == ChannelID && !content.startsWith(PREFIX)) {
			final String name = event.getMember().getEffectiveName();
			final Text symbol = new LiteralText("\u24B9");
			final HoverEvent hoverText = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(member.getUser().getName()));
			final ClickEvent clickText = new ClickEvent(ClickEvent.Action.OPEN_URL, channelURL);
			symbol.getStyle().setColor(Formatting.BLUE).setHoverEvent(hoverText).setClickEvent(clickText);
			final Text text = new TranslatableText("chat.type.text", name, content);
			for (Message.Attachment file : attachments) {
				final Text fileName = new LiteralText(" " + file.getFileName());
				fileName.getStyle()
						.setColor(Formatting.BLUE)
						.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Open attachment")))
						.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, file.getUrl()));
				text.append(fileName);
			}

			BismuthServer.server.submit(() -> BismuthServer.server.getPlayerManager().sendSystemMessage(new LiteralText("").append(symbol).append(text)));
			return;
		}

		final String[] args = content.split(" ");
		if (args.length == 0) {
			return;
		}

		final String command = args[0];

		if (this.isCommand(command, new String[]{"lazy"})) {
			BismuthServer.server.submit(() -> channel.sendMessage("no u").queue());
		}

		if (this.isCommand(command, new String[]{"tps"})) {
			BismuthServer.server.submit(() -> {
				final double MSPT = MathHelper.average(BismuthServer.server.averageTickTimes) * 1E-6D;
				final double TPS = 1000D / Math.max(50, MSPT);
				channel.sendMessage(String.format("**TPS: %.2f MSPT: %.2f**", TPS, MSPT)).queue();
			});
		}

		if (this.isCommand(command, new String[]{"list", "players", "online"})) {
			BismuthServer.server.submit(() -> {
				final String[] players = BismuthServer.server.getPlayerNames();
				final String title = String.format("%d player%s online:", players.length, players.length != 1 ? "s" : "");
				final MessageEmbed.AuthorInfo author = new MessageEmbed.AuthorInfo("BismuthBot", null, "https://i.imgur.com/a2w3DjI.png", null);
				final MessageEmbed embed = new MessageEmbed(null, title, StringUtils.join(players, "\n").replaceAll("_", "\\\\_"), EmbedType.RICH, null, 0x8665BD, null, null, author, null, null, null, null);
				channel.sendMessageEmbeds(embed).queue();
			});
		}

		if (this.isCommand(command, new String[]{"uptime"})) {
			channel.sendMessage(getUpTime()).queue();
		}
	}

	private boolean isCommand(String text, String[] command) {
		return Arrays.stream(command).anyMatch(s -> text.startsWith(PREFIX + s));
	}

	public String getUpTime() {
		LocalDateTime endTime = LocalDateTime.now();
		Duration botUpTime = Duration.between(BOT_START_TIME, endTime);
		// Didn't find any way to format it nicely so this will do fine
		String upTime = "Uptime: ";

		long days = botUpTime.toDays();
		botUpTime = botUpTime.minusDays(days);

		long hours = botUpTime.toHours();
		botUpTime = botUpTime.minusHours(hours);

		long minutes = botUpTime.toMinutes();
		botUpTime = botUpTime.minusMinutes(minutes);

		long seconds = botUpTime.getSeconds();
		if (days > 0) {
			upTime += String.format("%s day%s ", days, days > 1 ? "s" : "");
		}
		if (hours > 0) {
			upTime += String.format("%s hour%s ", hours, hours > 1 ? "s" : "");
		}
		if (minutes > 0) {
			upTime += String.format("%s minute%s ", minutes, minutes > 1 ? "s" : "");
		}
		if (seconds > 0) {
			upTime += String.format("%s second%s ", seconds, seconds > 1 ? "s" : "");
		}
		return upTime;
	}
}
