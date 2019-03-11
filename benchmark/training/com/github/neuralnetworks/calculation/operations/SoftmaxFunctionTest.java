package com.github.neuralnetworks.calculation.operations;


import com.github.neuralnetworks.test.AbstractTest;
import org.junit.Test;


/**
 * Created by chass on 28.11.14.
 */
public class SoftmaxFunctionTest extends AbstractTest {
    @Test
    public void testSoftMaxPrecision() {
        testSoftMaxPrecision(AbstractTest.Runtime.CPU_SEQ);
        testSoftMaxPrecision(AbstractTest.Runtime.OPENCL);
    }
}

