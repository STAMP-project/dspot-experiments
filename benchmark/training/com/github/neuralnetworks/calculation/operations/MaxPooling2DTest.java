package com.github.neuralnetworks.calculation.operations;


import com.github.neuralnetworks.tensor.Tensor;
import com.github.neuralnetworks.test.AbstractTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by chass on 25.11.14.
 */
public class MaxPooling2DTest extends AbstractTest {
    @Test
    public void testRandomMaxPoolingFF() {
        long seed = 123456789;
        int inputSquareSize = 200;
        int inputFeatures = 3;
        int pollingAreaSquareSize = 5;
        int batchSize = 2;
        int stride = 3;
        int paddingSize = 4;
        Tensor seqResult = testRandomMaxPoolingFF(AbstractTest.Runtime.CPU_SEQ, seed, inputSquareSize, inputFeatures, pollingAreaSquareSize, batchSize, stride, paddingSize);
        Tensor openclResult = testRandomMaxPoolingFF(AbstractTest.Runtime.OPENCL, seed, inputSquareSize, inputFeatures, pollingAreaSquareSize, batchSize, stride, paddingSize);
        Assert.assertTrue(isEqual(seqResult, openclResult));
    }

    @Test
    public void testRandomMaxPoolingBP() {
        long seed = 123456789;
        int inputSquareSize = 200;
        int inputFeatures = 1;
        int pollingAreaSquareSize = 6;
        int batchSize = 1;
        int stride = 1;
        int paddingSize = 0;
        testRandomMaxPoolingBP(AbstractTest.Runtime.CPU_SEQ, seed, inputSquareSize, inputFeatures, pollingAreaSquareSize, batchSize, stride, paddingSize);
        testRandomMaxPoolingBP(AbstractTest.Runtime.OPENCL, seed, inputSquareSize, inputFeatures, pollingAreaSquareSize, batchSize, stride, paddingSize);
    }

    @Test
    public void testNominalMaxPoolingBFStrideOne() {
        testMaxPoolingBPStrideOne(AbstractTest.Runtime.CPU_SEQ);
        testMaxPoolingBPStrideOne(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testNominalMaxPoolingBPChainRule() {
        testMaxPoolingBPChainRule(AbstractTest.Runtime.CPU_SEQ);
        testMaxPoolingBPChainRule(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testNominalMaxPoolingFF() {
        testMaxPooling(AbstractTest.Runtime.CPU_SEQ);
        testMaxPooling(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testNominalMaxPoolingBP() {
        testMaxPoolingBackpropagation(AbstractTest.Runtime.CPU_SEQ);
        testMaxPoolingBackpropagation(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testSubsamplingStride() {
        testSubsamplingStride(AbstractTest.Runtime.CPU_SEQ);
        testSubsamplingStride(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testSubsamplingStrideBackpropagation() {
        testSubsamplingStrideBackpropagation(AbstractTest.Runtime.CPU_SEQ);
        testSubsamplingStrideBackpropagation(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testMaxPoolingOverlapping() {
        testMaxPoolingOverlapping(AbstractTest.Runtime.CPU_SEQ);
        testMaxPoolingOverlapping(AbstractTest.Runtime.OPENCL);
    }
}

