package com.tagtraum.perf.gcviewer.math;


import org.junit.Assert;
import org.junit.Test;


/**
 * Date: Jan 30, 2002
 * Time: 5:53:55 PM
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 */
public class TestDoubleData {
    @Test
    public void testSimpleAverage() throws Exception {
        double[] x = new double[]{ 1.0, 2.0 };
        Assert.assertEquals("Simple average", 1.5, DoubleData.average(x), 0.0);
    }
}

