package dev.meridian.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

public final class Config {

    public static final Config INSTANCE = new Config();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path file = FabricLoader.getInstance().getConfigDir().resolve("meridian.json");

    private final Map<String, ModuleSettings> modules = new LinkedHashMap<>();
    private final List<Waypoint> waypoints = new ArrayList<>();
    private final List<Macro> macros = new ArrayList<>();

    private Config() {
    }

    public void load() {
        modules.clear();
        waypoints.clear();
        macros.clear();
        if (Files.exists(file)) {
            try {
                String json = Files.readString(file, StandardCharsets.UTF_8);
                Data data = GSON.fromJson(json, Data.class);
                if (data != null) {
                    if (data.modules != null) {
                        modules.putAll(data.modules);
                    }
                    if (data.waypoints != null) {
                        waypoints.addAll(data.waypoints);
                    }
                    if (data.macros != null) {
                        macros.addAll(data.macros);
                    }
                }
            } catch (Exception e) {
                modules.clear();
                waypoints.clear();
                macros.clear();
            }
        }
        for (ModuleSettings fallback : ModuleRegistry.defaults()) {
            modules.putIfAbsent(fallback.id, fallback);
        }
        save();
    }

    public void save() {
        saveTo(file);
    }

    public void saveTo(Path target) {
        try {
            Files.createDirectories(target.getParent());
            Data data = new Data();
            data.modules = modules;
            data.waypoints = waypoints;
            data.macros = macros;
            Files.writeString(target, GSON.toJson(data), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }

    public boolean loadFrom(Path source) {
        if (!Files.exists(source)) {
            return false;
        }
        try {
            String json = Files.readString(source, StandardCharsets.UTF_8);
            Data data = GSON.fromJson(json, Data.class);
            if (data == null) {
                return false;
            }
            modules.clear();
            waypoints.clear();
            macros.clear();
            if (data.modules != null) {
                modules.putAll(data.modules);
            }
            if (data.waypoints != null) {
                waypoints.addAll(data.waypoints);
            }
            if (data.macros != null) {
                macros.addAll(data.macros);
            }
            for (ModuleSettings fallback : ModuleRegistry.defaults()) {
                modules.putIfAbsent(fallback.id, fallback);
            }
            save();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void reset() {
        modules.clear();
        waypoints.clear();
        macros.clear();
        for (ModuleSettings fallback : ModuleRegistry.defaults()) {
            modules.put(fallback.id, fallback);
        }
        save();
    }

    public ModuleSettings module(String id) {
        ModuleSettings settings = modules.get(id);
        if (settings == null) {
            settings = ModuleRegistry.defaultsFor(id);
            modules.put(id, settings);
            save();
        }
        return settings;
    }

    public Map<String, ModuleSettings> modules() {
        return modules;
    }

    public List<Waypoint> waypoints() {
        return waypoints;
    }

    public List<Macro> macros() {
        return macros;
    }

    private static final class Data {
        Map<String, ModuleSettings> modules;
        List<Waypoint> waypoints;
        List<Macro> macros;
    }
}
