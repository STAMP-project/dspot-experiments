package com.github.neuralnetworks.calculation.operations;


import com.github.neuralnetworks.tensor.Tensor;
import com.github.neuralnetworks.test.AbstractTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by chass on 20.11.14.
 */
public class WeightedSumTest extends AbstractTest {
    @Test
    public void testRandomSimpleFF() {
        long seed = 123456789;
        Tensor seqResult = testRandomSimpleFF(AbstractTest.Runtime.CPU_SEQ, seed);
        Tensor openclResult = testRandomSimpleFF(AbstractTest.Runtime.OPENCL, seed);
        Assert.assertTrue(isEqual(seqResult, openclResult));
    }

    @Test
    public void testRandomFFWithBias() {
        long seed = 123456789;
        Tensor seqResult = testRandomFFWithBias(AbstractTest.Runtime.CPU_SEQ, seed);
        Tensor openclResult = testRandomFFWithBias(AbstractTest.Runtime.OPENCL, seed);
        Assert.assertTrue(isEqual(seqResult, openclResult));
    }

    @Test
    public void testRandomFFCombinedLayers() {
        long seed = 123456789;
        Tensor seqResult = testRandomFFCombinedLayers(AbstractTest.Runtime.CPU_SEQ, seed);
        Tensor openclResult = testRandomFFCombinedLayers(AbstractTest.Runtime.OPENCL, seed);
        Assert.assertTrue(isEqual(seqResult, openclResult));
    }

    @Test
    public void testNominalFF() {
        testWeightedSumFF(AbstractTest.Runtime.CPU_SEQ);
        testWeightedSumFF(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testNominalBP() {
        testWeightedSumBP(AbstractTest.Runtime.CPU_SEQ);
        testWeightedSumBP(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testWeightedSumFFSimple() {
        testWeightedSumFFSimple(AbstractTest.Runtime.CPU_SEQ);
        testWeightedSumFFSimple(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testParallelNetworks() {
        testParallelNetworks(AbstractTest.Runtime.CPU_SEQ);
        testParallelNetworks(AbstractTest.Runtime.OPENCL);
    }
}

