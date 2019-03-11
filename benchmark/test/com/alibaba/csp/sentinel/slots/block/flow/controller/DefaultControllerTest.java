package com.alibaba.csp.sentinel.slots.block.flow.controller;


import com.alibaba.csp.sentinel.node.Node;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.TrafficShapingController;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Eric Zhao
 */
public class DefaultControllerTest {
    @Test
    public void testCanPassForQps() {
        long threshold = 10;
        TrafficShapingController controller = new DefaultController(threshold, RuleConstant.FLOW_GRADE_QPS);
        Node node = Mockito.mock(Node.class);
        Mockito.when(node.passQps()).thenReturn((threshold - 1)).thenReturn(threshold);
        Assert.assertTrue(controller.canPass(node, 1));
        Assert.assertFalse(controller.canPass(node, 1));
    }

    @Test
    public void testCanPassForThreadCount() {
        int threshold = 8;
        TrafficShapingController controller = new DefaultController(threshold, RuleConstant.FLOW_GRADE_THREAD);
        Node node = Mockito.mock(Node.class);
        Mockito.when(node.curThreadNum()).thenReturn((threshold - 1)).thenReturn(threshold);
        Assert.assertTrue(controller.canPass(node, 1));
        Assert.assertFalse(controller.canPass(node, 1));
    }
}

