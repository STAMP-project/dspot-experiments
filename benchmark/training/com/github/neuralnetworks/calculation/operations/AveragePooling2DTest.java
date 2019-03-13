package com.github.neuralnetworks.calculation.operations;


import com.github.neuralnetworks.tensor.Tensor;
import com.github.neuralnetworks.test.AbstractTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by chass on 25.11.14.
 */
public class AveragePooling2DTest extends AbstractTest {
    @Test
    public void testRandomAveragePoolingFF() {
        long seed = 123456789;
        int inputSquareSize = 200;
        int inputFeatures = 3;
        int pollingAreaSquareSize = 5;
        int batchSize = 2;
        int stride = 1;
        int paddingSize = 0;
        Tensor seqResult = testRandomAveragePoolingFF(AbstractTest.Runtime.CPU_SEQ, seed, inputSquareSize, inputFeatures, pollingAreaSquareSize, batchSize, stride, paddingSize);
        Tensor openclResult = testRandomAveragePoolingFF(AbstractTest.Runtime.OPENCL, seed, inputSquareSize, inputFeatures, pollingAreaSquareSize, batchSize, stride, paddingSize);
        Assert.assertTrue(isEqual(seqResult, openclResult));
    }

    @Test
    public void testAveragePoolingBackpropagationOverlapping() {
        testAveragePoolingBackpropagationOverlapping(AbstractTest.Runtime.CPU_SEQ);
        testAveragePoolingBackpropagationOverlapping(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testRandomAveragePoolingBP() {
        long seed = 33456386;
        int inputSquareSize = 200;
        int batchSize = 5;
        int poolingAreaSquareSize = 5;// has to be a divider of inputSqareSize

        int features = 3;
        Tensor seqResult = testRandomAveragePoolingBP(AbstractTest.Runtime.CPU_SEQ, seed, inputSquareSize, batchSize, poolingAreaSquareSize, features);
        Tensor openclResult = testRandomAveragePoolingBP(AbstractTest.Runtime.OPENCL, seed, inputSquareSize, batchSize, poolingAreaSquareSize, features);
        Assert.assertTrue(isEqual(seqResult, openclResult));
    }

    @Test
    public void testAveragePooling() {
        testAveragePooling(AbstractTest.Runtime.CPU_SEQ);
        testAveragePooling(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testAveragePoolingBackpropagation() {
        testAveragePoolingBackpropagation(AbstractTest.Runtime.CPU_SEQ);
        testAveragePoolingBackpropagation(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testAveragePoolingOverlapping() {
        testAveragePoolingOverlapping(AbstractTest.Runtime.CPU_SEQ);
        testAveragePoolingOverlapping(AbstractTest.Runtime.OPENCL);
    }
}

