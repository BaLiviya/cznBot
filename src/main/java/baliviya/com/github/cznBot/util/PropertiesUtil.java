package baliviya.com.github.cznBot.util;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

    public static String getProperty(String key) {
        try (InputStream stream = PropertiesUtil.class.getResourceAsStream("/config.properties")) {
            Properties properties = new Properties();
            properties.load(stream);
            return properties.getProperty(key);
        } catch (IOException e) {
            LoggerFactory.getLogger(PropertiesUtil.class).info("Can't get property for: " + key, e);
        }
        return null;
    }
}
