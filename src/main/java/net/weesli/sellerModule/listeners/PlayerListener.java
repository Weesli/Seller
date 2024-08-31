package net.weesli.sellerModule.listeners;

import net.weesli.rClaim.api.RClaimAPI;
import net.weesli.rClaim.utils.Claim;
import net.weesli.rozsLib.events.BlockRightClickEvent;
import net.weesli.sellerModule.SellerModule;
import net.weesli.sellerModule.ui.VerifyMenu;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerListener implements Listener {

    @EventHandler
    public void onClickBedrock(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null || e.getClickedBlock().getType().equals(Material.AIR)){return;}
        if (e.getClickedBlock().getType().equals(Material.BEDROCK)){
            Claim claim = RClaimAPI.getInstance().getClaim(e.getPlayer().getLocation().getChunk());
            if (claim == null){return;}
            if (e.getClickedBlock().getLocation().equals(claim.getCenter())){
                if (!claim.isOwner(e.getPlayer().getUniqueId())){
                    if (SellerModule.getInstance().getDatabase().isValid(claim.getID())){
                        VerifyMenu.openVerifyMenu(e.getPlayer(), claim);
                    }else {
                        e.getPlayer().sendMessage(SellerModule.getInstance().getMessage("THIS_AREA_NOT_SELLING"));
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e){
        if (e.getMessage().startsWith("/claim sell")){
            try {
                Claim selected_claim = RClaimAPI.getInstance().getClaim(e.getPlayer().getLocation().getChunk());
                if (selected_claim == null){return;}
                Claim claim = (selected_claim.isCenter() ? selected_claim : RClaimAPI.getInstance().getClaim(selected_claim.getCenterId()));
                if (!claim.isOwner(e.getPlayer().getUniqueId())){
                    e.getPlayer().sendMessage(SellerModule.getInstance().getMessage("NO_OWNER"));
                    e.setCancelled(true);
                    return;
                }
                int amount = Integer.parseInt(e.getMessage().split(" ")[2]);
                if (SellerModule.getInstance().getDatabase().isValid(claim.getID())){
                    e.getPlayer().sendMessage(SellerModule.getInstance().getMessage("ALREADY_SELLING_CLAIM"));
                } else {
                    SellerModule.getInstance().getDatabase().insert(claim.getID(), amount);
                    e.getPlayer().sendMessage(SellerModule.getInstance().getMessage("SELL_CLAIM").replaceAll("%price%", String.valueOf(amount)));
                }
                e.setCancelled(true);
            }catch (ArrayIndexOutOfBoundsException | NumberFormatException error){
                e.getPlayer().sendMessage(SellerModule.getInstance().getMessage("USAGE"));
                e.setCancelled(true);
            }
        }
        if (e.getMessage().startsWith("/claim unsell")){
            Claim claim = RClaimAPI.getInstance().getClaim(e.getPlayer().getLocation().getChunk());
            if (claim == null){return;}
            if (!claim.isOwner(e.getPlayer().getUniqueId())){
                e.getPlayer().sendMessage(SellerModule.getInstance().getMessage("NO_OWNER"));
                e.setCancelled(true);
                return;
            }
            if (SellerModule.getInstance().getDatabase().isValid(claim.getID())){
                SellerModule.getInstance().getDatabase().delete(claim.getID());
                e.getPlayer().sendMessage(SellerModule.getInstance().getMessage("SALE_STOPPED"));
                e.setCancelled(true);
            } else {
                e.getPlayer().sendMessage(SellerModule.getInstance().getMessage("THIS_AREA_NOT_SELLING"));
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onTabComp(TabCompleteEvent e){
        if (e.getBuffer().contains("/claim")){
            List<String> completions = new ArrayList<>(e.getCompletions());
            completions.add("sell");
            completions.add("unsell");
            e.setCompletions(completions);
        }
        if (e.getBuffer().startsWith("/claim sell")) {
            e.setCompletions(List.of("<price>"));
        }
    }
}
