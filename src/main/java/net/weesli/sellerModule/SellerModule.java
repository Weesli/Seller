package net.weesli.sellerModule;

import net.weesli.rClaim.management.modules.Module;
import net.weesli.rozsLib.color.ColorBuilder;
import net.weesli.sellerModule.configuration.Messages;
import net.weesli.sellerModule.database.Database;
import net.weesli.sellerModule.database.MySQLDatabase;
import net.weesli.sellerModule.database.SQLiteDatabase;
import net.weesli.sellerModule.listeners.PlayerListener;
import org.bukkit.Bukkit;

public final class SellerModule implements Module {

    private Database database;

    private static SellerModule instance;

    @Override
    public void enable() {
        instance = this;
        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(), plugin);
        loadDatabase();
        loadFiles();
    }

    private void loadDatabase() {
        String type = plugin.getConfig().getString("options.storage-type");
        switch (type){
            case "MySQL":
                database = new MySQLDatabase();
                break;
            case "SQLite":
                database = new SQLiteDatabase();
                break;
            default:
                Bukkit.getConsoleSender().sendMessage(ColorBuilder.convertColors("[Seller] Invalid storage type!"));
                Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    private void loadFiles(){
        Messages.setup();
        Messages.getFile().options().copyDefaults(true);
        Messages.save();
    }

    @Override
    public String getAddonName() {
        return "Seller";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }
    public static SellerModule getInstance() {
        return instance;
    }
    public Database getDatabase() {
        return database;
    }

    public String getMessage(String path){
        return ColorBuilder.convertColors(plugin.getConfig().getString("options.prefix") + Messages.getFile().getString(path));
    }

}
