package net.weesli.sellerModule.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerListener implements {


    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        e.getPlayer().sendMessage("AAA");
    }
}
