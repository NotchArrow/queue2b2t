package com.notcharrow.queue2b2t;

import com.notcharrow.queue2b2t.config.QueueConfig;
import com.notcharrow.queue2b2t.config.ConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.CookieStorage;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.util.Identifier;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class QueueScheduler implements ClientModInitializer {
	private static final MinecraftClient client = MinecraftClient.getInstance();
	private static final String SERVER_ADDRESS = "localhost";//"2b2t.org";
	private static LocalTime JOIN_TIME;

	@Override
	public void onInitializeClient() {
		ConfigManager.loadConfig();

		new Thread(() -> {
			while (true) {
				JOIN_TIME = LocalTime.of(ConfigManager.config.queueHour, ConfigManager.config.queueMinute);
				int joinMinuteCutoff;
				int joinHourCutoff;

				joinMinuteCutoff = ConfigManager.config.queueMinute + 5;
				if (joinMinuteCutoff >= 60) {
					joinMinuteCutoff %= 60;
					joinHourCutoff = (ConfigManager.config.queueHour + 1) % 24;
				} else {
					joinHourCutoff = ConfigManager.config.queueHour;
				}

				if (LocalTime.now().isAfter(JOIN_TIME) && LocalTime.now().isBefore(LocalTime.of(joinHourCutoff, joinMinuteCutoff))
						&& client.getCurrentServerEntry() == null) {
					join2b2t();
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ignored) {
				}
			}
		}).start();
	}

	private void join2b2t () {
		if (client.currentScreen instanceof TitleScreen) {
			ServerInfo serverInfo = new ServerInfo("2b2t", SERVER_ADDRESS, ServerInfo.ServerType.OTHER);
			ServerAddress serverAddress = ServerAddress.parse(SERVER_ADDRESS);
			Map<Identifier, byte[]> cookieMap = new HashMap<>();
			client.execute(() ->
					ConnectScreen.connect(new MultiplayerScreen(null),
					client, serverAddress, serverInfo, true, new CookieStorage(cookieMap))
			);

			ConfigManager.config.afk = true;
			ConfigManager.saveConfig();

			ClientTickEvents.END_CLIENT_TICK.register(client -> {
				if (client.player != null && client.world != null
						&& !client.player.isInCreativeMode()
						&& ConfigManager.config.afk && ConfigManager.config.afkKick) {
					client.world.disconnect();
				}
			});
		}
	}
}