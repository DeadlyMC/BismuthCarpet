package si.bismuth;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import net.ornithemc.osl.entrypoints.api.ModInitializer;
import net.ornithemc.osl.lifecycle.api.server.MinecraftServerEvents;
import net.ornithemc.osl.networking.api.server.ServerConnectionEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import si.bismuth.discord.DCBot;
import si.bismuth.logging.LoggerRegistry;
import si.bismuth.network.server.ServerNetworking;
import si.bismuth.utils.BismuthRecipeManager;
import si.bismuth.utils.HUDController;
import si.bismuth.utils.ScoreboardHelper;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.UUID;

public class BismuthServer implements ModInitializer {
	public static final String BISMUTH_SERVER_VERSION = "2.0.0";
	public static final Logger log = LogManager.getLogger("Bismuth");
	public static final ServerNetworking networking = new ServerNetworking();
	public static MinecraftServer server;
	public static DCBot bot;

	public static final ArrayList<UUID> joinedPlayers = new ArrayList<>();

	@Override
    public void init() {
		MinecraftServerEvents.START.register(BismuthServer::init);
		MinecraftServerEvents.LOAD_WORLD.register(BismuthServer::onServerLoaded);
		MinecraftServerEvents.PREPARE_WORLD.register(BismuthServer::onWorldLoaded);
		MinecraftServerEvents.STOP.register(BismuthServer::stop);
		MinecraftServerEvents.TICK_START.register(BismuthServer::tick);

		ServerConnectionEvents.LOGIN.register(BismuthServer::playerConnected);
		ServerConnectionEvents.DISCONNECT.register(BismuthServer::playerDisconnected);
	}

	public static void init(MinecraftServer server) {
		BismuthServer.server = server;
	}

	public static void onServerLoaded(MinecraftServer server) {
		server.setMotd("v" + BISMUTH_SERVER_VERSION + " \u2014 " + server.getServerMotd());
		LoggerRegistry.initLoggers(server);
		if (server.isDedicated()) {
			try {
				BismuthServer.bot = new DCBot(((DedicatedServer) server).getPropertyOrDefault("botToken", ""), server.isOnlineMode());
			} catch (LoginException | InterruptedException e) {
				throw new RuntimeException("Error setting up discord bot", e);
			}
		}
	}

	public static void onWorldLoaded(MinecraftServer minecraftServer) {
		ScoreboardHelper.init();
	}

	public static void stop(MinecraftServer server) {
		if (server.isDedicated()) {
			BismuthServer.bot.shutDownBot();
		}
		BismuthServer.server = null;
	}

	public static void tick(MinecraftServer server) {
		HUDController.update_hud(server);
	}

	public static void playerConnected(MinecraftServer server, ServerPlayerEntity player) {
		if (BismuthServer.server.isOnlineMode()) {
			final GameMode mode = player.interactionManager.getGameMode();
			if (mode == GameMode.CREATIVE) {
				player.setGameMode(GameMode.SPECTATOR);
			} else if (mode == GameMode.ADVENTURE) {
				player.setGameMode(GameMode.SURVIVAL);
			}
		}

		LoggerRegistry.playerConnected(player);
		BismuthRecipeManager.unlockCustomRecipes(player);

		// TODO make a proper implementation to keep logs persistent
		if (!joinedPlayers.contains(player.getUuid())) {
			joinedPlayers.add(player.getUuid());
			player.server.commandHandler.run(player.asEntity(), "/log tps");
			player.server.commandHandler.run(player.asEntity(), "/log mobcaps");
		}
	}

	public static void playerDisconnected(MinecraftServer server, ServerPlayerEntity player) {
		LoggerRegistry.playerDisconnected(player);
	}
}
