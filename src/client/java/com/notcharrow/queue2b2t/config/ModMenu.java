package com.notcharrow.queue2b2t.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class ModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return this::createConfigScreen;
	}

	private Screen createConfigScreen(Screen parent) {
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Text.of("2b2t Queue"));

		ConfigCategory general = builder.getOrCreateCategory(Text.of("General Settings"));

		addConfigEntryInteger(general, "Queue Hour", "Hour of the day to join queue at (military time format)", ConfigManager.config.queueHour,
				newValue -> ConfigManager.config.queueHour = (Integer) newValue,
				parent, 0, 23);

		addConfigEntryInteger(general, "Queue Minute", "Minute of the day to join queue at", ConfigManager.config.queueMinute,
				newValue -> ConfigManager.config.queueMinute = (Integer) newValue,
				parent, 0, 59);

		addConfigEntryBoolean(general, "Afk Leave", "If you should disconnect from 2b2t after getting to the end of the queue if " +
						"the setting below is true", ConfigManager.config.afkKick,
				newValue -> ConfigManager.config.afkKick = (Boolean) newValue,
				parent);

		addConfigEntryBoolean(general, "Afk", "Automatically toggles on when joining the queue. Toggle off " +
						"before reaching the end of the queue if the above setting is enabled", ConfigManager.config.afk,
				newValue -> ConfigManager.config.afk = (Boolean) newValue,
				parent);

		return builder.build();
	}

	private void addConfigEntryBoolean(ConfigCategory category, String label, String tooltip, Object value, Consumer<Object> saveConsumer, Screen parent) {
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Text.of("Queue 2b2t Configuration"));
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		category.addEntry(entryBuilder.startBooleanToggle(Text.of(label), (Boolean) value)
			.setTooltip(Text.of(tooltip))
			.setDefaultValue((Boolean) value)
			.setSaveConsumer(newValue -> {
			saveConsumer.accept(newValue);
			ConfigManager.saveConfig();
			})
			.build());
	}

	private void addConfigEntryInteger(ConfigCategory category, String label, String tooltip, Object value, Consumer<Object> saveConsumer, Screen parent, int min, int max) {
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Text.of("Queue 2b2t Configuration"));
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		category.addEntry(entryBuilder.startIntSlider(Text.of(label), (Integer) value, min, max)
			.setTooltip(Text.of(tooltip))
			.setDefaultValue((Integer) value)
			.setSaveConsumer(newValue -> {
			saveConsumer.accept(newValue);
			ConfigManager.saveConfig();
			})
			.build());
	}
}
