package util;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

public class IconCache {
    private static final Map<String, Image> cache = new HashMap<>();
    private static final String BASE_URL = "https://openweathermap.org/img/wn/%s@2x.png";
    private static final Image DEFAULT_ICON;

    static {
        try {
            DEFAULT_ICON = new Image(IconCache.class.getResourceAsStream("/images/unknown.png"));
        } catch (Exception e) {
            throw new RuntimeException("Missing default weather icon at /src/main/resources/images/unknown.png", e);
        }
    }

    public static Image getIcon(String iconCode) {
        return cache.computeIfAbsent(iconCode, code -> {
            try {
                return new Image(String.format(BASE_URL, code));
            } catch (Exception e) {
                return DEFAULT_ICON;
            }
        });
    }
}