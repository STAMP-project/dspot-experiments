package com.github.neuralnetworks.calculation.operations;


import com.github.neuralnetworks.test.AbstractTest;
import org.junit.Test;


/**
 * Created by chass on 28.11.14.
 */
public class SigmoidTest extends AbstractTest {
    @Test
    public void testSigmoidBP4() {
        testSigmoidBP4(AbstractTest.Runtime.CPU_SEQ);
        testSigmoidBP4(AbstractTest.Runtime.OPENCL);
    }

    /**
     * Simple backpropagation test with specific values
     */
    @Test
    public void testSigmoidBP() {
        testSigmoidBP(AbstractTest.Runtime.CPU_SEQ);
        testSigmoidBP(AbstractTest.Runtime.OPENCL);
    }

    /**
     * Simple backpropagation test with specific values
     * https://blog.itu.dk/MAIG-E2013/files/2013/09/9point2-classification-by-backpropagation.pdf
     */
    @Test
    public void testSigmoidBP2() {
        testSigmoidBP2(AbstractTest.Runtime.CPU_SEQ);
        testSigmoidBP2(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testSigmoidBP3() {
        testSigmoidBP3(AbstractTest.Runtime.CPU_SEQ);
        testSigmoidBP3(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testSigmoid() {
        testSigmoid(AbstractTest.Runtime.CPU_SEQ);
        testSigmoid(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testSigmoidDerivative() {
        testSigmoidDerivative(AbstractTest.Runtime.CPU_SEQ);
        testSigmoidDerivative(AbstractTest.Runtime.OPENCL);
    }
}

