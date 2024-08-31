package net.weesli.sellerModule.ui;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.management.ClaimManager;
import net.weesli.rClaim.utils.Claim;
import net.weesli.rClaim.utils.ClaimPlayer;
import net.weesli.rozsLib.color.ColorBuilder;
import net.weesli.rozsLib.configuration.YamlFileBuilder;
import net.weesli.rozsLib.inventory.ClickableItemStack;
import net.weesli.rozsLib.inventory.InventoryBuilder;
import net.weesli.sellerModule.SellerModule;
import net.weesli.sellerModule.configuration.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;

public class VerifyMenu {

    static YamlFileBuilder fileBuilder = new YamlFileBuilder(RClaim.getInstance(), "messages").setPath(new File(RClaim.getInstance().getDataFolder(), "modules/" + SellerModule.getInstance().getAddonName()));

    public static void openVerifyMenu(Player player, Claim claim){
        InventoryBuilder builder = new InventoryBuilder(RClaim.getInstance(), ColorBuilder.convertColors(Messages.getFile().getString("menus.title")), Messages.getFile().getInt("menus.size"));

        ClickableItemStack details = new ClickableItemStack(RClaim.getInstance(), new ItemStack(Material.PAPER), builder.build());
        details.setCancelled(true);
        details.setEvent(event -> {});
        ItemMeta meta = details.getItemStack().getItemMeta();
        meta.setDisplayName(ColorBuilder.convertColors("&b%cost%").replaceAll("%cost%", String.valueOf(SellerModule.getInstance().getDatabase().getPrice(claim.getID()))));
        details.getItemStack().setItemMeta(meta);
        builder.setItem(4, details);
        ClickableItemStack accept = new ClickableItemStack(RClaim.getInstance(), fileBuilder.getItemStack("menus.items.accept"), builder.build());
        accept.setEvent(event -> {
            int price = SellerModule.getInstance().getDatabase().getPrice(claim.getID());
            if (RClaim.getInstance().getEconomy().hasEnough(player, price)){
                RClaim.getInstance().getEconomy().withdraw(player, price);
                RClaim.getInstance().getEconomy().deposit(Bukkit.getOfflinePlayer(claim.getOwner()).getPlayer(), price);
                SellerModule.getInstance().getDatabase().delete(claim.getID());
                player.sendMessage(SellerModule.getInstance().getMessage("SELL_CLAIM_SUCCESSFULLY").replaceAll("%price%", String.valueOf(price)));
                ClaimPlayer claims = ClaimManager.getPlayerData(claim.getOwner());
                claims.getClaims().stream().forEach(targetClaim -> {
                    targetClaim.setOwner(player.getUniqueId());
                    targetClaim.getMembers().clear();
                    RClaim.getInstance().getStorage().updateClaim(targetClaim);
                });
                player.closeInventory();
            }else {
                player.sendMessage(SellerModule.getInstance().getMessage("NOT_ENOUGH_MONEY"));
                player.closeInventory();
            }
        });
        accept.setCancelled(true);

        ClickableItemStack decline = new ClickableItemStack(RClaim.getInstance(), fileBuilder.getItemStack("menus.items.deny"), builder.build());
        decline.setEvent(event -> {player.closeInventory();});
        decline.setCancelled(true);

        builder.setItem(Messages.getFile().getInt("menus.items.accept.slot"), accept);
        builder.setItem(Messages.getFile().getInt("menus.items.deny.slot"), decline);
        player.openInventory(builder.build());
    }
}
