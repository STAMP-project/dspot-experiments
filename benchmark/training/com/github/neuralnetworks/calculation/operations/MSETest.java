package com.github.neuralnetworks.calculation.operations;


import com.github.neuralnetworks.test.AbstractTest;
import org.junit.Test;


/**
 * Created by chass on 02.12.14.
 */
public class MSETest extends AbstractTest {
    @Test
    public void testMSE() {
        testMSE(AbstractTest.Runtime.CPU_SEQ);
        testMSE(AbstractTest.Runtime.OPENCL);
    }
}

