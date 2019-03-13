package com.alibaba.otter.canal.parse.index;


import com.alibaba.otter.canal.protocol.position.LogPosition;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;


public class FileMixedLogPositionManagerTest extends AbstractLogPositionManagerTest {
    private static final String tmp = System.getProperty("java.io.tmpdir", "/tmp");

    private static final File dataDir = new File(FileMixedLogPositionManagerTest.tmp, "canal");

    @Test
    public void testAll() {
        MemoryLogPositionManager memoryLogPositionManager = new MemoryLogPositionManager();
        FileMixedLogPositionManager logPositionManager = new FileMixedLogPositionManager(FileMixedLogPositionManagerTest.dataDir, 1000, memoryLogPositionManager);
        logPositionManager.start();
        LogPosition position2 = doTest(logPositionManager);
        sleep(1500);
        FileMixedLogPositionManager logPositionManager2 = new FileMixedLogPositionManager(FileMixedLogPositionManagerTest.dataDir, 1000, memoryLogPositionManager);
        logPositionManager2.start();
        LogPosition getPosition2 = logPositionManager2.getLatestIndexBy(destination);
        Assert.assertEquals(position2, getPosition2);
        logPositionManager.stop();
        logPositionManager2.stop();
    }
}

