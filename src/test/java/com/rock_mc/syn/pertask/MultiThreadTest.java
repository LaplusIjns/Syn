package com.rock_mc.syn.pertask;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.rock_mc.syn.PluginTest;
import com.rock_mc.syn.api.GenCode;
import com.rock_mc.syn.log.Log;
import com.rock_mc.syn.log.LogPlugin;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.Test;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MultiThreadTest extends PluginTest {


    @Test
    void loadTesting() throws InterruptedException {

        PlayerJoinEvent.getHandlerList().unregister(plugin);

        PlayerMock opPlayer = server.addPlayer();
        opPlayer.setOp(true);

        int codeCount = plugin.dbManager.countCode();
        Log log = new LogPlugin();

        long startTime = System.currentTimeMillis();

        String[] args = new String[]{};

        ThreadPoolExecutor tp = new ThreadPoolExecutor(
                100,
                100,
                200, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(1000000));
//        final Random random = new Random();
        for (int i = 0; i < 500; i++) {
            tp.execute(() -> {

                // 50s 20k pass
                GenCode.run(plugin, log, null, args);
                // plugin.dbManager.addCode(String.valueOf(random.nextInt())); // 100k pass
                // opPlayer.performCommand("syn gencode 5"); // no support

            });
        }
        tp.shutdown();

        boolean allTasksCompleted = tp.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        if (!allTasksCompleted) {
            System.err.println("Not all tasks completed execution after shutdown request");
        }

        System.out.println((System.currentTimeMillis() - startTime) + "ms");

        codeCount = plugin.dbManager.countCode();

        System.out.println("codeCount: " + codeCount);
    }
}
