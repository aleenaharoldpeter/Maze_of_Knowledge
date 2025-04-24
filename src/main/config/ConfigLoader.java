package src.main.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    public static Properties loadConfig() {
        Properties config = new Properties();
        try (FileInputStream input = new FileInputStream("src/main/config/config.properties")) {
            config.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }
}