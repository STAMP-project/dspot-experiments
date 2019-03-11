package com.github.neuralnetworks.calculation.operations;


import com.github.neuralnetworks.test.AbstractTest;
import org.junit.Test;


/**
 * Created by chass on 28.11.14.
 */
public class SoftmaxTest extends AbstractTest {
    @Test
    public void testSoftMax() {
        testSoftMax(AbstractTest.Runtime.CPU_SEQ);
        testSoftMax(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testSoftMax2() {
        testSoftMax2(AbstractTest.Runtime.CPU_SEQ);
        testSoftMax2(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testSoftMaxRandom() {
        testSoftMaxRandom(AbstractTest.Runtime.CPU_SEQ);
        testSoftMaxRandom(AbstractTest.Runtime.OPENCL);
    }
}

