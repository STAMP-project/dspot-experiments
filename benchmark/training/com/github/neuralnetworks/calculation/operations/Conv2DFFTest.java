package com.github.neuralnetworks.calculation.operations;


import com.github.neuralnetworks.tensor.Tensor;
import com.github.neuralnetworks.test.AbstractTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by chass on 25.11.14.
 */
public class Conv2DFFTest extends AbstractTest {
    /**
     * Info:
     * - disabled testing of Runtime.CPU_SEQ
     * - comparing CPU with OpenCL results is enough
     */
    @Test
    public void testConv2DFF() {
        Tensor seqResult = testConv2DFF(AbstractTest.Runtime.CPU_SEQ);
        Tensor openclResult = testConv2DFF(AbstractTest.Runtime.OPENCL);
        Assert.assertTrue(isEqual(seqResult, openclResult));
    }

    @Test
    public void testCNNStride() {
        Tensor seqResult = testCNNStride(AbstractTest.Runtime.CPU_SEQ);
        Tensor openclResult = testCNNStride(AbstractTest.Runtime.OPENCL);
        Assert.assertTrue(isEqual(seqResult, openclResult));
    }

    @Test
    public void testCNNMLPFF() {
        testCNNMLPFF(AbstractTest.Runtime.CPU_SEQ);
        testCNNMLPFF(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testConvolutions4() {
        Tensor seqResult = testConvolutions4(AbstractTest.Runtime.CPU_SEQ);
        Tensor openclResult = testConvolutions4(AbstractTest.Runtime.OPENCL);
        Assert.assertTrue(isEqual(seqResult, openclResult));
    }

    @Test
    public void testSimpleCNN() {
        Tensor seqResult = testSimpleCNN(AbstractTest.Runtime.CPU_SEQ);
        Tensor openclResult = testSimpleCNN(AbstractTest.Runtime.OPENCL);
        Assert.assertTrue(isEqual(seqResult, openclResult));
    }

    @Test
    public void testConvolutions() {
        testConvolutions(AbstractTest.Runtime.CPU_SEQ);
        testConvolutions(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testConvolutions2() {
        testConvolutions2(AbstractTest.Runtime.CPU_SEQ);
        testConvolutions2(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testConvolutions3() {
        testConvolutions3(AbstractTest.Runtime.CPU_SEQ);
        testConvolutions3(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testConvolutionsStride() {
        testConvolutionsStride(AbstractTest.Runtime.CPU_SEQ);
        testConvolutionsStride(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testConvolutionsStride2() {
        testConvolutionsStride2(AbstractTest.Runtime.CPU_SEQ);
        testConvolutionsStride2(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testDimensions() {
        testDimensions(AbstractTest.Runtime.CPU_SEQ);
        testDimensions(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testCNNConstruction() {
        testCNNConstruction(AbstractTest.Runtime.CPU_SEQ);
        testCNNConstruction(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testCNNConstruction2() {
        testCNNConstruction2(AbstractTest.Runtime.CPU_SEQ);
        testCNNConstruction2(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testCNNConstruction3() {
        testCNNConstruction3(AbstractTest.Runtime.CPU_SEQ);
        testCNNConstruction3(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testCNNLayerCalculatorConstruction() {
        testCNNLayerCalculatorConstruction(AbstractTest.Runtime.CPU_SEQ);
        testCNNLayerCalculatorConstruction(AbstractTest.Runtime.OPENCL);
    }
}

