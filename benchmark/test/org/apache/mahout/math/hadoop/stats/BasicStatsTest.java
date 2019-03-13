/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.mahout.math.hadoop.stats;


import SequenceFile.Writer;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.mahout.common.MahoutTestCase;
import org.apache.mahout.common.RandomUtils;
import org.apache.mahout.math.jet.random.Normal;
import org.junit.Test;


public final class BasicStatsTest extends MahoutTestCase {
    private Configuration conf;

    @Test
    public void testVar() throws Exception {
        Path input = getTestTempFilePath("stdDev/counts.file");
        Path output = getTestTempFilePath("stdDev/output.file");
        produceTestData(input);
        double v = BasicStats.variance(input, output, conf);
        assertEquals(2.44, v, 0.01);
    }

    @Test
    public void testStdDev() throws Exception {
        Path input = getTestTempFilePath("stdDev/counts.file");
        Path output = getTestTempFilePath("stdDev/output.file");
        produceTestData(input);
        double v = BasicStats.stdDev(input, output, conf);
        assertEquals(1.56, v, 0.01);// sample std dev is 1.563, std. dev from a discrete set is 1.48

    }

    @Test
    public void testStdDevForGivenMean() throws Exception {
        Path input = getTestTempFilePath("stdDev/counts.file");
        Path output = getTestTempFilePath("stdDev/output.file");
        produceTestData(input);
        double v = BasicStats.stdDevForGivenMean(input, output, 0.0, conf);
        assertEquals(10.65, v, 0.01);// sample std dev is 10.65

    }

    // Not entirely sure on this test
    @Test
    public void testStdDev2() throws Exception {
        Path input = getTestTempFilePath("stdDev/counts.file");
        Path output = getTestTempFilePath("stdDev/output.file");
        FileSystem fs = FileSystem.get(input.toUri(), conf);
        SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, input, IntWritable.class, DoubleWritable.class);
        Random random = RandomUtils.getRandom();
        Normal normal = new Normal(5, 3, random);
        for (int i = 0; i < 1000000; i++) {
            writer.append(new IntWritable(i), new DoubleWritable(((long) (normal.nextInt()))));
        }
        writer.close();
        double v = BasicStats.stdDev(input, output, conf);
        assertEquals(3, v, 0.02);
    }
}

