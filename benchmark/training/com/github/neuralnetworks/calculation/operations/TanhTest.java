package com.github.neuralnetworks.calculation.operations;


import com.github.neuralnetworks.test.AbstractTest;
import org.junit.Test;


/**
 * Created by akohl on 08.12.2014.
 */
public class TanhTest extends AbstractTest {
    @Test
    public void testTanh() {
        testTanh(AbstractTest.Runtime.CPU_SEQ);
        testTanh(AbstractTest.Runtime.OPENCL);
    }

    @Test
    public void testTanhDerivative() {
        testTanhDerivative(AbstractTest.Runtime.CPU_SEQ);
        testTanhDerivative(AbstractTest.Runtime.OPENCL);
    }
}

