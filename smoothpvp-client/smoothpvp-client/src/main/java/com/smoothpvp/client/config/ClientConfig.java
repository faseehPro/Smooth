package com.smoothpvp.client.config;

import java.io.*;
import java.nio.file.*;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Very small, dependency-free JSON-ish config so we don't need
 * to pull in a config library just for a handful of booleans.
 * Stored at .minecraft/config/smoothpvp-client.properties
 */
public class ClientConfig {

    // --- HUD toggles ---
    public boolean showFps = true;
    public boolean showPing = true;
    public boolean showCoords = true;
    public boolean showPotionTimers = true;
    public boolean showKeystrokes = true;
    public boolean showHitIndicator = true;

    // --- Smoothness / feel toggles (fair, client-side only) ---
    public boolean disableViewBob = true;
    public boolean disableHurtCameraShake = true;
    public boolean uncapParticles = true;
    public int particleCap = 2000; // vanilla default is 16384 but most spam is at 0 distance

    // --- Crosshair ---
    public boolean cleanCrosshair = true;
    public int crosshairColor = 0xFFFFFF;

    private static final String FILE_NAME = "smoothpvp-client.properties";
    private static ClientConfig instance;

    public static ClientConfig get() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    private static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }

    public static ClientConfig load() {
        ClientConfig cfg = new ClientConfig();
        Path path = configPath();
        if (!Files.exists(path)) {
            cfg.save();
            return cfg;
        }
        try (InputStream in = Files.newInputStream(path)) {
            java.util.Properties props = new java.util.Properties();
            props.load(in);
            cfg.showFps = bool(props, "showFps", cfg.showFps);
            cfg.showPing = bool(props, "showPing", cfg.showPing);
            cfg.showCoords = bool(props, "showCoords", cfg.showCoords);
            cfg.showPotionTimers = bool(props, "showPotionTimers", cfg.showPotionTimers);
            cfg.showKeystrokes = bool(props, "showKeystrokes", cfg.showKeystrokes);
            cfg.showHitIndicator = bool(props, "showHitIndicator", cfg.showHitIndicator);
            cfg.disableViewBob = bool(props, "disableViewBob", cfg.disableViewBob);
            cfg.disableHurtCameraShake = bool(props, "disableHurtCameraShake", cfg.disableHurtCameraShake);
            cfg.uncapParticles = bool(props, "uncapParticles", cfg.uncapParticles);
            cfg.particleCap = Integer.parseInt(props.getProperty("particleCap", String.valueOf(cfg.particleCap)));
            cfg.cleanCrosshair = bool(props, "cleanCrosshair", cfg.cleanCrosshair);
            cfg.crosshairColor = Integer.decode(props.getProperty("crosshairColor", "0xFFFFFF"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cfg;
    }

    private static boolean bool(java.util.Properties p, String key, boolean def) {
        return Boolean.parseBoolean(p.getProperty(key, String.valueOf(def)));
    }

    public void save() {
        java.util.Properties props = new java.util.Properties();
        props.setProperty("showFps", String.valueOf(showFps));
        props.setProperty("showPing", String.valueOf(showPing));
        props.setProperty("showCoords", String.valueOf(showCoords));
        props.setProperty("showPotionTimers", String.valueOf(showPotionTimers));
        props.setProperty("showKeystrokes", String.valueOf(showKeystrokes));
        props.setProperty("showHitIndicator", String.valueOf(showHitIndicator));
        props.setProperty("disableViewBob", String.valueOf(disableViewBob));
        props.setProperty("disableHurtCameraShake", String.valueOf(disableHurtCameraShake));
        props.setProperty("uncapParticles", String.valueOf(uncapParticles));
        props.setProperty("particleCap", String.valueOf(particleCap));
        props.setProperty("cleanCrosshair", String.valueOf(cleanCrosshair));
        props.setProperty("crosshairColor", "0x" + Integer.toHexString(crosshairColor).toUpperCase());
        try (OutputStream out = Files.newOutputStream(configPath())) {
            props.store(out, "SmoothPVP Client config");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
