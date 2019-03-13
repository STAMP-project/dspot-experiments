package com.github.neuralnetworks.calculation.operations;


import com.github.neuralnetworks.test.AbstractTest;
import org.junit.Test;


/**
 * Created by akohl on 08.12.2014.
 */
public class BernoulliTest extends AbstractTest {
    @Test
    public void testBernoulliDistribution() {
        // testBernoulliDistribution(Runtime.OPENCL);
        testBernoulliDistribution(AbstractTest.Runtime.CPU_SEQ);
    }
}

