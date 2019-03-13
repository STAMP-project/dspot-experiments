package org.nd4j.linalg.profiling;


import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.profiler.OpProfiler;
import org.nd4j.linalg.profiler.data.StackAggregator;
import org.nd4j.linalg.profiler.data.primitives.StackDescriptor;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Slf4j
public class StackAggregatorTests {
    @Test
    public void testBasicBranching1() {
        StackAggregator aggregator = new StackAggregator();
        aggregator.incrementCount();
        aggregator.incrementCount();
        Assert.assertEquals(2, aggregator.getTotalEventsNumber());
        Assert.assertEquals(2, aggregator.getUniqueBranchesNumber());
    }

    @Test
    public void testBasicBranching2() {
        StackAggregator aggregator = new StackAggregator();
        for (int i = 0; i < 10; i++) {
            aggregator.incrementCount();
        }
        Assert.assertEquals(10, aggregator.getTotalEventsNumber());
        // simnce method is called in loop, there should be only 1 unique code branch
        Assert.assertEquals(1, aggregator.getUniqueBranchesNumber());
    }

    @Test
    public void testTrailingFrames1() {
        StackAggregator aggregator = new StackAggregator();
        aggregator.incrementCount();
        StackDescriptor descriptor = aggregator.getLastDescriptor();
        log.info("Trace: {}", descriptor.toString());
        // we just want to make sure that OpProfiler methods are NOT included in trace
        Assert.assertTrue(descriptor.getStackTrace()[((descriptor.size()) - 1)].getClassName().contains("StackAggregatorTests"));
    }

    @Test
    public void testTrailingFrames2() {
        INDArray x = Nd4j.create(new int[]{ 10, 10 }, 'f');
        INDArray y = Nd4j.create(new int[]{ 10, 10 }, 'c');
        x.assign(y);
        x.assign(y);
        Nd4j.getExecutioner().commit();
        StackAggregator aggregator = OpProfiler.getInstance().getMixedOrderAggregator();
        StackDescriptor descriptor = aggregator.getLastDescriptor();
        log.info("Trace: {}", descriptor.toString());
        Assert.assertEquals(2, aggregator.getTotalEventsNumber());
        Assert.assertEquals(2, aggregator.getUniqueBranchesNumber());
        aggregator.renderTree();
    }
}

