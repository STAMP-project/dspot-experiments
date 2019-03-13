package com.github.neuralnetworks.calculation.operations;


import com.github.neuralnetworks.test.AbstractTest;
import org.junit.Test;


/**
 * Created by chass on 28.11.14.
 */
public class StochasticPooling2DTest extends AbstractTest {
    @Test
    public void testStochasticPooling() {
        testStochasticPooling(AbstractTest.Runtime.CPU_SEQ);
        testStochasticPooling(AbstractTest.Runtime.OPENCL);
    }
}

