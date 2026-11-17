package com.classicclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

/** Settings saved to config/classicclient.json. */
public class ClassicConfig {
    public boolean hudEnabled = true;
    public boolean showFps = true;
    public boolean showPing = true;
    public boolean showCoords = true;
    public boolean showDirection = true;
    public boolean showClock = false;
    public boolean showArmor = true;
    public boolean showHealth = true;
    public boolean heartIndicator = true;
    public boolean freelookHold = false;       // false = tap to toggle (best for touch)
    public boolean freelookThirdPerson = true; // switch to third person while freelooking
    public int hudCorner = 0;                  // 0 top-left, 1 top-right, 2 bottom-left, 3 bottom-right

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ClassicConfig instance;

    public static ClassicConfig get() {
        if (instance == null) {
            load();
        }
        return instance;
    }

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("classicclient.json");
    }

    public static void load() {
        try {
            Path p = path();
            if (Files.exists(p)) {
                instance = GSON.fromJson(Files.readString(p), ClassicConfig.class);
            }
        } catch (Exception ignored) {
            // fall back to defaults below
        }
        if (instance == null) {
            instance = new ClassicConfig();
        }
    }

    public static void save() {
        try {
            Files.writeString(path(), GSON.toJson(get()));
        } catch (Exception ignored) {
            // config is best effort
        }
    }
}
