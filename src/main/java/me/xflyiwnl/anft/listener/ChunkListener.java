package me.xflyiwnl.anft.listener;

import me.xflyiwnl.anft.ANFT;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkListener implements Listener {

    @EventHandler
    public void onUnload(ChunkUnloadEvent event) {

        Chunk chunk = event.getChunk();

        if (ANFT.getInstance().getChunks().contains(chunk)) {
            event.setSaveChunk(true);
        }

    }

}
