package com.alibaba.csp.sentinel.slots.block.flow.controller;


import com.alibaba.csp.sentinel.node.Node;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author CarpenterLee
 */
public class WarmUpRateLimiterControllerTest {
    @Test
    public void testPace() throws InterruptedException {
        WarmUpRateLimiterController controller = new WarmUpRateLimiterController(10, 10, 1000, 3);
        Node node = Mockito.mock(Node.class);
        Mockito.when(node.passQps()).thenReturn(100L);
        Mockito.when(node.previousPassQps()).thenReturn(100L);
        Assert.assertTrue(controller.canPass(node, 1));
        long start = System.currentTimeMillis();
        Assert.assertTrue(controller.canPass(node, 1));
        long cost = (System.currentTimeMillis()) - start;
        Assert.assertTrue(((cost >= 100) && (cost <= 110)));
    }

    @Test
    public void testPaceCanNotPass() throws InterruptedException {
        WarmUpRateLimiterController controller = new WarmUpRateLimiterController(10, 10, 10, 3);
        Node node = Mockito.mock(Node.class);
        Mockito.when(node.passQps()).thenReturn(100L);
        Mockito.when(node.previousPassQps()).thenReturn(100L);
        Assert.assertTrue(controller.canPass(node, 1));
        Assert.assertFalse(controller.canPass(node, 1));
    }
}

