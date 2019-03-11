/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.util.statistic;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/* ------------------------------------------------------------ */
public class SampleStatisticTest {
    private static long[][] data = new long[][]{ new long[]{ 100, 100, 100, 100, 100, 100, 100, 100, 100, 100 }, new long[]{ 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 90, 110 }, new long[]{ 100, 100, 100, 100, 100, 100, 100, 100, 90, 110, 95, 105, 97, 103 }, new long[]{ 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 90, 110, 95, 105, 97, 103 } };

    private static double[][] results = new double[][]{ /* {mean,stddev} */
    new double[]{ 100.0, 0.0 }, new double[]{ 100.0, Math.sqrt((((10 * 10) + (10 * 10)) / 12.0)) }, new double[]{ 100.0, Math.sqrt((((((((10 * 10) + (10 * 10)) + (5 * 5)) + (5 * 5)) + (3 * 3)) + (3 * 3)) / 14.0)) }, new double[]{ 100.0, Math.sqrt((((((((10 * 10) + (10 * 10)) + (5 * 5)) + (5 * 5)) + (3 * 3)) + (3 * 3)) / 24.0)) }, new double[]{ 100.0, Math.sqrt((((((((10 * 10) + (10 * 10)) + (5 * 5)) + (5 * 5)) + (3 * 3)) + (3 * 3)) / 104.0)) } };

    @Test
    public void testData() throws Exception {
        SampleStatistic stats = new SampleStatistic();
        for (int d = 0; d < (SampleStatisticTest.data.length); d++) {
            stats.reset();
            for (long x : SampleStatisticTest.data[d])
                stats.record(x);

            Assertions.assertEquals(SampleStatisticTest.data[d].length, ((int) (stats.getCount())), ("count" + d));
            assertNearEnough(("mean" + d), SampleStatisticTest.results[d][0], stats.getMean());
            assertNearEnough(("stddev" + d), SampleStatisticTest.results[d][1], stats.getStdDev());
        }
        System.err.println(stats);
    }
}

