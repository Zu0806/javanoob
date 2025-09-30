
package main;

import java.util.HashMap;
import java.util.Map;

/**
 * ResourceManager 負責管理遊戲資源，例如：
 * - 背景圖片
 * - 人物圖片
 * - BGM 音樂檔
 */
public class ResourceManager {
    private Map<String, String> backgroundImages;
    private Map<String, String> characterImages;
    private Map<String, String> bgmTracks;

    public ResourceManager() {
        backgroundImages = new HashMap<>();
        characterImages = new HashMap<>();
        bgmTracks = new HashMap<>();

        loadDefaultResources();
    }

    private void loadDefaultResources() {
        // 預設背景圖片
        backgroundImages.put("campus_morning", "assets/backgrounds/campus_morning.png");
        backgroundImages.put("campus_noon", "assets/backgrounds/campus_noon.png");
        backgroundImages.put("campus_night", "assets/backgrounds/campus_night.png");

        // 預設人物圖片
        characterImages.put("hero", "assets/characters/hero.png");
        characterImages.put("heroine", "assets/characters/heroine.png");

        // 預設音樂
        bgmTracks.put("main_theme", "assets/music/main_theme.mp3");
        bgmTracks.put("sad_theme", "assets/music/sad_theme.mp3");
    }

    public String getBackgroundImage(String key) {
        return backgroundImages.getOrDefault(key, "assets/backgrounds/default.png");
    }

    public String getCharacterImage(String key) {
        return characterImages.getOrDefault(key, "assets/characters/default.png");
    }

    public String getBGM(String key) {
        return bgmTracks.getOrDefault(key, "assets/music/default.mp3");
    }

    public void addBackgroundImage(String key, String path) {
        backgroundImages.put(key, path);
    }

    public void addCharacterImage(String key, String path) {
        characterImages.put(key, path);
    }

    public void addBGM(String key, String path) {
        bgmTracks.put(key, path);
    }
}

