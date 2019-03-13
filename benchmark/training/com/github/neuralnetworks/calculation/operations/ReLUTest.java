package com.github.neuralnetworks.calculation.operations;


import com.github.neuralnetworks.test.AbstractTest;
import org.junit.Test;


/**
 * Created by chass on 02.12.14.
 */
public class ReLUTest extends AbstractTest {
    @Test
    public void testReLU() {
        testReLU(AbstractTest.Runtime.CPU_SEQ);
        testReLU(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testReLUDerivative() {
        testReLUDerivative(AbstractTest.Runtime.CPU_SEQ);
        testReLUDerivative(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testSoftReLU() {
        testSoftReLU(AbstractTest.Runtime.CPU_SEQ);
        testSoftReLU(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testSoftReLUDerivative() {
        testSoftReLUDerivative(AbstractTest.Runtime.CPU_SEQ);
        testSoftReLUDerivative(AbstractTest.Runtime.OPENCL);
    }
}

