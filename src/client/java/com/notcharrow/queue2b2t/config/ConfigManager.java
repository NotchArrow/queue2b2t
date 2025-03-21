package com.notcharrow.queue2b2t.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final File CONFIG_FILE = new File("config", "queue2b2t.json");

	public static QueueConfig config;

	public static void loadConfig() {
		if (!CONFIG_FILE.exists()) {
			config = new QueueConfig();
			saveConfig();
			return;
		}
		try (FileReader reader = new FileReader(CONFIG_FILE)) {
			config = GSON.fromJson(reader, QueueConfig.class);
		} catch (IOException e) {
			e.printStackTrace();
			config = new QueueConfig();
		}
	}

	public static void saveConfig() {
		try {
			CONFIG_FILE.getParentFile().mkdirs();
			try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
				GSON.toJson(config, writer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
