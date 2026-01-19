package net.bloodic.config;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.bloodic.hack.Hack;
import net.bloodic.hack.HackManager;
import net.bloodic.settings.BooleanSetting;
import net.bloodic.settings.EnumSetting;
import net.bloodic.settings.NumberSetting;
import net.bloodic.settings.Setting;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ConfigManager
{
	private static final Logger LOGGER = LogManager.getLogger("Bloodic/Config");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final long SAVE_DEBOUNCE_MS = 300;

	private final HackManager hackManager;
	private final Path configFile;
	private volatile boolean suppressSave;
	private final ScheduledExecutorService saveExecutor =
		Executors.newSingleThreadScheduledExecutor(r -> {
			Thread t = new Thread(r, "bloodic-config-save");
			t.setDaemon(true);
			return t;
		});
	private ScheduledFuture<?> pendingSave;

	public ConfigManager(HackManager hackManager)
	{
		this.hackManager = hackManager;
		Path configDir = FabricLoader.getInstance().getConfigDir().resolve("bloodic");
		this.configFile = configDir.resolve("config.json");
	}

	public void load()
	{
		if (!Files.exists(configFile)) {
			return;
		}

		try (Reader reader = Files.newBufferedReader(configFile)) {
			JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
			JsonObject hacksJson = root.getAsJsonObject("hacks");
			if (hacksJson == null)
				return;

			withSuppressedSave(() -> {
				for (Map.Entry<String, JsonElement> entry : hacksJson.entrySet()) {
					String hackName = entry.getKey();
					Hack hack = hackManager.getHackByName(hackName);
					if (hack == null)
						continue;

					JsonObject hackObj = entry.getValue().getAsJsonObject();
					if (hackObj.has("enabled")) {
						boolean enabled = hackObj.get("enabled").getAsBoolean();
						hack.setEnabled(enabled);
					}
					if (hackObj.has("key")) {
						int key = hackObj.get("key").getAsInt();
						hack.setKey(key);
					}

					JsonObject settingsObj = hackObj.getAsJsonObject("settings");
					if (settingsObj == null)
						continue;

					applySettings(hack, settingsObj);
				}
			});
		} catch (Exception e) {
			LOGGER.error("Failed to load config", e);
		}
	}

	public void save()
	{
		try {
			Files.createDirectories(configFile.getParent());
			JsonObject root = new JsonObject();
			JsonObject hacksObj = new JsonObject();

			for (Hack hack : hackManager.getHacks()) {
				JsonObject hackObj = new JsonObject();
				hackObj.addProperty("enabled", hack.isEnabled());
				hackObj.addProperty("key", hack.getKey());

				JsonObject settingsObj = new JsonObject();
				for (Setting<?> setting : hack.getSettings()) {
					Object val = setting.getValue();
					if (val instanceof Enum<?> e) {
						settingsObj.addProperty(setting.getName(), e.name());
					} else if (val instanceof Boolean b) {
						settingsObj.addProperty(setting.getName(), b);
					} else if (val instanceof Number n) {
						settingsObj.addProperty(setting.getName(), n);
					} else if (val != null) {
						settingsObj.addProperty(setting.getName(), val.toString());
					}
				}
				hackObj.add("settings", settingsObj);
				hacksObj.add(hack.getName(), hackObj);
			}

			root.add("hacks", hacksObj);

			Path tempFile = configFile.resolveSibling(configFile.getFileName() + ".tmp");
			try (Writer writer = Files.newBufferedWriter(tempFile)) {
				GSON.toJson(root, writer);
			}
			atomicMove(tempFile, configFile);
		} catch (IOException e) {
			LOGGER.error("Failed to save config", e);
		}
	}

	public void saveAsync()
	{
		if (suppressSave)
			return;

		if (pendingSave != null)
			pendingSave.cancel(false);

		pendingSave = saveExecutor.schedule(() -> {
			try {
				save();
			} catch (Exception e) {
				LOGGER.error("Async config save failed", e);
			}
		}, SAVE_DEBOUNCE_MS, TimeUnit.MILLISECONDS);
	}

	public void reset()
	{
		withSuppressedSave(() -> {
			for (Hack hack : hackManager.getHacks()) {
				hack.setEnabled(false);
				hack.resetSettings();
			}
		});
		save();
	}

	private void withSuppressedSave(Runnable runnable)
	{
		boolean previous = suppressSave;
		suppressSave = true;
		try {
			runnable.run();
		} finally {
			suppressSave = previous;
		}
	}

	public boolean isSaveSuppressed()
	{
		return suppressSave;
	}

	private void atomicMove(Path source, Path target) throws IOException
	{
		try {
			Files.move(source, target,
				java.nio.file.StandardCopyOption.REPLACE_EXISTING,
				java.nio.file.StandardCopyOption.ATOMIC_MOVE);
		} catch (IOException e) {
			Files.move(source, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
		}
	}

	private void applySettings(Hack hack, JsonObject settingsObj)
	{
		Map<String, Setting<?>> settingMap = indexSettings(hack.getSettings());
		for (Map.Entry<String, JsonElement> entry : settingsObj.entrySet()) {
			Setting<?> setting = settingMap.get(entry.getKey());
			if (setting == null)
				continue;

			JsonElement value = entry.getValue();
			try {
				if (setting instanceof BooleanSetting boolSetting && value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
					boolSetting.setValue(value.getAsBoolean());
				} else if (setting instanceof NumberSetting numberSetting && value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
					numberSetting.setValue(value.getAsDouble());
				} else if (setting instanceof EnumSetting<?> enumSetting && value.isJsonPrimitive()) {
					String name = value.getAsString();
					setEnum(enumSetting, name);
				}
			} catch (Exception e) {
				LOGGER.warn("Could not apply setting {} for hack {}", entry.getKey(), hack.getName(), e);
			}
		}
	}

	private Map<String, Setting<?>> indexSettings(List<Setting<?>> settings)
	{
		Map<String, Setting<?>> map = new HashMap<>();
		for (Setting<?> setting : settings) {
			map.put(setting.getName(), setting);
		}

		return map;
	}

	@SuppressWarnings("unchecked")
	private <E extends Enum<E>> void setEnum(EnumSetting<?> setting, String name)
	{
		EnumSetting<E> enumSetting = (EnumSetting<E>)setting;
		Class<E> enumClass = enumSetting.getValue().getDeclaringClass();
		for (E constant : enumClass.getEnumConstants()) {
			if (constant.name().equalsIgnoreCase(name)) {
				enumSetting.setValue(constant);
				return;
			}
		}
	}
}
