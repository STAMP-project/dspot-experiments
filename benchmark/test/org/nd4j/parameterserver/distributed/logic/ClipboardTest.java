package org.nd4j.parameterserver.distributed.logic;


import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.parameterserver.distributed.logic.completion.Clipboard;
import org.nd4j.parameterserver.distributed.messages.VoidAggregation;
import org.nd4j.parameterserver.distributed.messages.aggregations.InitializationAggregation;
import org.nd4j.parameterserver.distributed.messages.aggregations.VectorAggregation;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Slf4j
public class ClipboardTest {
    @Test
    public void testPin1() throws Exception {
        Clipboard clipboard = new Clipboard();
        Random rng = new Random(12345L);
        for (int i = 0; i < 100; i++) {
            VectorAggregation aggregation = new VectorAggregation(rng.nextLong(), ((short) (100)), ((short) (i)), Nd4j.create(5));
            clipboard.pin(aggregation);
        }
        Assert.assertEquals(false, clipboard.hasCandidates());
        Assert.assertEquals(0, clipboard.getNumberOfCompleteStacks());
        Assert.assertEquals(100, clipboard.getNumberOfPinnedStacks());
    }

    @Test
    public void testPin2() throws Exception {
        Clipboard clipboard = new Clipboard();
        Random rng = new Random(12345L);
        Long validId = 123L;
        short shardIdx = 0;
        for (int i = 0; i < 300; i++) {
            VectorAggregation aggregation = new VectorAggregation(rng.nextLong(), ((short) (100)), ((short) (1)), Nd4j.create(5));
            // imitating valid
            if (((i % 2) == 0) && (shardIdx < 100)) {
                aggregation.setTaskId(validId);
                aggregation.setShardIndex((shardIdx++));
            }
            clipboard.pin(aggregation);
        }
        VoidAggregation aggregation = clipboard.getStackFromClipboard(0L, validId);
        Assert.assertNotEquals(null, aggregation);
        Assert.assertEquals(0, aggregation.getMissingChunks());
        Assert.assertEquals(true, clipboard.hasCandidates());
        Assert.assertEquals(1, clipboard.getNumberOfCompleteStacks());
    }

    /**
     * This test checks how clipboard handles singular aggregations
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPin3() throws Exception {
        Clipboard clipboard = new Clipboard();
        Random rng = new Random(12345L);
        Long validId = 123L;
        InitializationAggregation aggregation = new InitializationAggregation(1, 0);
        clipboard.pin(aggregation);
        Assert.assertTrue(clipboard.isTracking(0L, aggregation.getTaskId()));
        Assert.assertTrue(clipboard.isReady(0L, aggregation.getTaskId()));
    }
}

