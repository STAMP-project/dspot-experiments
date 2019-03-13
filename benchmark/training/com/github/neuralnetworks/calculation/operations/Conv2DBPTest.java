package com.github.neuralnetworks.calculation.operations;


import com.github.neuralnetworks.tensor.Matrix;
import com.github.neuralnetworks.tensor.Tensor;
import com.github.neuralnetworks.test.AbstractTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by chass on 25.11.14.
 */
public class Conv2DBPTest extends AbstractTest {
    /**
     * Info:
     *      - disabled testing of Runtime.CPU_SEQ
     *      - comparing CPU with OpenCL results is enough
     */
    @Test
    public void testCNNBackpropagationValues() {
        Tensor seqResult = testCNNBackpropagationValues(AbstractTest.Runtime.CPU_SEQ);
        Tensor openclResult = testCNNBackpropagationValues(AbstractTest.Runtime.OPENCL);
        Assert.assertTrue(isEqual(seqResult, openclResult));
    }

    @Test
    public void testCNNBackpropagation() {
        Tensor seqResult = testCNNBackpropagation(AbstractTest.Runtime.CPU_SEQ);
        Tensor openclResult = testCNNBackpropagation(AbstractTest.Runtime.OPENCL);
        Assert.assertTrue(isEqual(seqResult, openclResult));
    }

    @Test
    public void testCNNBackpropagation2() {
        Matrix seqResult = testCNNBackpropagation2(AbstractTest.Runtime.CPU_SEQ);
        Matrix openclResult = testCNNBackpropagation2(AbstractTest.Runtime.OPENCL);
        Assert.assertTrue(isEqual(seqResult, openclResult));
    }

    @Test
    public void testCNNBackpropagation3() {
        Tensor seqResult = testCNNBackpropagation3(AbstractTest.Runtime.CPU_SEQ);
        Tensor openclResult = testCNNBackpropagation3(AbstractTest.Runtime.OPENCL);
        Assert.assertTrue(isEqual(seqResult, openclResult));
    }
}

