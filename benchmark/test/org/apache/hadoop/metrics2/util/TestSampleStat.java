/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.metrics2.util;


import SampleStat.MinMax.DEFAULT_MAX_VALUE;
import SampleStat.MinMax.DEFAULT_MIN_VALUE;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the running sample stat computation
 */
public class TestSampleStat {
    private static final double EPSILON = 1.0E-42;

    /**
     * Some simple use cases
     */
    @Test
    public void testSimple() {
        SampleStat stat = new SampleStat();
        Assert.assertEquals("num samples", 0, stat.numSamples());
        Assert.assertEquals("mean", 0.0, stat.mean(), TestSampleStat.EPSILON);
        Assert.assertEquals("variance", 0.0, stat.variance(), TestSampleStat.EPSILON);
        Assert.assertEquals("stddev", 0.0, stat.stddev(), TestSampleStat.EPSILON);
        Assert.assertEquals("min", DEFAULT_MIN_VALUE, stat.min(), TestSampleStat.EPSILON);
        Assert.assertEquals("max", DEFAULT_MAX_VALUE, stat.max(), TestSampleStat.EPSILON);
        stat.add(3);
        Assert.assertEquals("num samples", 1L, stat.numSamples());
        Assert.assertEquals("mean", 3.0, stat.mean(), TestSampleStat.EPSILON);
        Assert.assertEquals("variance", 0.0, stat.variance(), TestSampleStat.EPSILON);
        Assert.assertEquals("stddev", 0.0, stat.stddev(), TestSampleStat.EPSILON);
        Assert.assertEquals("min", 3.0, stat.min(), TestSampleStat.EPSILON);
        Assert.assertEquals("max", 3.0, stat.max(), TestSampleStat.EPSILON);
        stat.add(2).add(1);
        Assert.assertEquals("num samples", 3L, stat.numSamples());
        Assert.assertEquals("mean", 2.0, stat.mean(), TestSampleStat.EPSILON);
        Assert.assertEquals("variance", 1.0, stat.variance(), TestSampleStat.EPSILON);
        Assert.assertEquals("stddev", 1.0, stat.stddev(), TestSampleStat.EPSILON);
        Assert.assertEquals("min", 1.0, stat.min(), TestSampleStat.EPSILON);
        Assert.assertEquals("max", 3.0, stat.max(), TestSampleStat.EPSILON);
        stat.reset();
        Assert.assertEquals("num samples", 0, stat.numSamples());
        Assert.assertEquals("mean", 0.0, stat.mean(), TestSampleStat.EPSILON);
        Assert.assertEquals("variance", 0.0, stat.variance(), TestSampleStat.EPSILON);
        Assert.assertEquals("stddev", 0.0, stat.stddev(), TestSampleStat.EPSILON);
        Assert.assertEquals("min", DEFAULT_MIN_VALUE, stat.min(), TestSampleStat.EPSILON);
        Assert.assertEquals("max", DEFAULT_MAX_VALUE, stat.max(), TestSampleStat.EPSILON);
    }
}

