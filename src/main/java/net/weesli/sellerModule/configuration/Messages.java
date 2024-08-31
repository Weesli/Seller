package net.weesli.sellerModule.configuration;

import com.google.common.base.Charsets;
import net.weesli.sellerModule.SellerModule;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Messages {

    private static File file;
    private static FileConfiguration configuration;

    public static void setup() {
        file = new File(SellerModule.getInstance().plugin.getDataFolder(), "modules/" + SellerModule.getInstance().getAddonName() + "/messages.yml");
        if (!file.exists()) {
            try (InputStream inputStream = SellerModule.getInstance().getClass().getClassLoader().getResourceAsStream("messages.yml")) {
                if (inputStream == null) {
                    SellerModule.getInstance().plugin.getLogger().severe("messages.yml not found in resources");
                    return;
                }
                configuration = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
                return;
            } catch (Exception e) {
                SellerModule.getInstance().plugin.getLogger().severe("Failed to load messages.yml: " + e.getMessage());
            }
        }
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration getFile(){
        return configuration;
    }

    public static void save(){
        try {
            configuration.save(file);
        } catch (Exception e) {
            SellerModule.getInstance().plugin.getLogger().severe("Failed to save messages.yml: " + e.getMessage());
        }
    }

    public static void reloadFile() {
        configuration = YamlConfiguration.loadConfiguration(file);
        InputStream defConfigStream = SellerModule.getInstance().getClass().getClassLoader().getResourceAsStream("messages.yml");
        if (defConfigStream != null) {
            configuration.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
        }
    }
}
