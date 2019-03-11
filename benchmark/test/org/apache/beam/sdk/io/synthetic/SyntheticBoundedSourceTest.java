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
package org.apache.beam.sdk.io.synthetic;


import BoundedSource.BoundedReader;
import ProgressShape.LINEAR_REGRESSING;
import com.fasterxml.jackson.core.JsonParseException;
import java.util.List;
import org.apache.beam.sdk.io.synthetic.SyntheticSourceOptions.ProgressShape;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.testing.SourceTestUtils;
import org.apache.beam.sdk.values.KV;
import org.apache.commons.math3.distribution.ConstantRealDistribution;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link SyntheticBoundedSource}.
 */
@RunWith(JUnit4.class)
public class SyntheticBoundedSourceTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private SyntheticSourceOptions testSourceOptions = new SyntheticSourceOptions();

    @Test
    public void testInvalidSourceOptionsJsonFormat() throws Exception {
        thrown.expect(JsonParseException.class);
        String syntheticSourceOptions = "input:unknown URI";
        fromString(syntheticSourceOptions);
    }

    @Test
    public void testFromString() throws Exception {
        String syntheticSourceOptions = "{\"numRecords\":100,\"splitPointFrequencyRecords\":10,\"keySizeBytes\":10," + (((("\"valueSizeBytes\":20,\"numHotKeys\":3," + "\"hotKeyFraction\":0.3,\"seed\":123456,") + "\"bundleSizeDistribution\":{\"type\":\"const\",\"const\":42},") + "\"forceNumInitialBundles\":10,\"progressShape\":\"LINEAR_REGRESSING\"") + "}");
        SyntheticSourceOptions sourceOptions = fromString(syntheticSourceOptions);
        Assert.assertEquals(100, sourceOptions.numRecords);
        Assert.assertEquals(10, sourceOptions.splitPointFrequencyRecords);
        Assert.assertEquals(10, sourceOptions.keySizeBytes);
        Assert.assertEquals(20, sourceOptions.valueSizeBytes);
        Assert.assertEquals(3, sourceOptions.numHotKeys);
        Assert.assertEquals(0.3, sourceOptions.hotKeyFraction, 0);
        Assert.assertEquals(0, sourceOptions.nextDelay(sourceOptions.seed));
        Assert.assertEquals(123456, sourceOptions.seed);
        Assert.assertEquals(42, sourceOptions.bundleSizeDistribution.sample(123), 0.0);
        Assert.assertEquals(10, sourceOptions.forceNumInitialBundles.intValue());
        Assert.assertEquals(LINEAR_REGRESSING, sourceOptions.progressShape);
    }

    @Test
    public void testSourceOptionsWithNegativeNumRecords() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("numRecords should be a non-negative number, but found -100");
        testSourceOptions.numRecords = -100;
        testSourceOptions.validate();
    }

    /**
     * Test the reader and the source produces the same records.
     */
    @Test
    public void testSourceAndReadersWork() throws Exception {
        testSourceAndReadersWorkP(1);
        testSourceAndReadersWorkP((-1));
        testSourceAndReadersWorkP(3);
    }

    @Test
    public void testSplitAtFraction() throws Exception {
        testSplitAtFractionP(1);
        testSplitAtFractionP(3);
        // Do not test "-1" because then splits would be vacuous
    }

    @Test
    public void testSplitIntoBundles() throws Exception {
        testSplitIntoBundlesP(1);
        testSplitIntoBundlesP((-1));
        testSplitIntoBundlesP(5);
        PipelineOptions options = PipelineOptionsFactory.create();
        testSourceOptions.forceNumInitialBundles = 37;
        Assert.assertEquals(37, new SyntheticBoundedSource(testSourceOptions).split(42, options).size());
    }

    @Test
    public void testIncreasingProgress() throws Exception {
        PipelineOptions options = PipelineOptionsFactory.create();
        testSourceOptions.progressShape = ProgressShape.LINEAR;
        SyntheticBoundedSource source = new SyntheticBoundedSource(testSourceOptions);
        BoundedReader<KV<byte[], byte[]>> reader = source.createReader(options);
        // Reader starts at 0.0 progress.
        Assert.assertEquals(0, reader.getFractionConsumed(), 1.0E-5);
        // Set the lastFractionConsumed < 0.0 so that we can use strict inequality in the below loop.
        double lastFractionConsumed = -1.0;
        for (boolean more = reader.start(); more; more = reader.advance()) {
            Assert.assertTrue(((reader.getFractionConsumed()) > lastFractionConsumed));
            lastFractionConsumed = reader.getFractionConsumed();
        }
        Assert.assertEquals(1, reader.getFractionConsumed(), 1.0E-5);
    }

    @Test
    public void testRegressingProgress() throws Exception {
        PipelineOptions options = PipelineOptionsFactory.create();
        testSourceOptions.progressShape = ProgressShape.LINEAR_REGRESSING;
        SyntheticBoundedSource source = new SyntheticBoundedSource(testSourceOptions);
        BoundedReader<KV<byte[], byte[]>> reader = source.createReader(options);
        double lastFractionConsumed = reader.getFractionConsumed();
        for (boolean more = reader.start(); more; more = reader.advance()) {
            Assert.assertTrue(((reader.getFractionConsumed()) <= lastFractionConsumed));
            lastFractionConsumed = reader.getFractionConsumed();
        }
    }

    @Test
    public void testSplitIntoSingleRecordBundles() throws Exception {
        PipelineOptions options = PipelineOptionsFactory.create();
        SyntheticSourceOptions sourceOptions = new SyntheticSourceOptions();
        sourceOptions.numRecords = 10;
        sourceOptions.setSeed(123456);
        sourceOptions.bundleSizeDistribution = SyntheticOptions.fromRealDistribution(new ConstantRealDistribution(1.0));
        sourceOptions.forceNumInitialBundles = 10;
        SyntheticBoundedSource source = new SyntheticBoundedSource(sourceOptions);
        List<SyntheticBoundedSource> sources = source.split(42L, options);
        for (SyntheticBoundedSource recordSource : sources) {
            recordSource.validate();
            Assert.assertEquals(1, ((recordSource.getEndOffset()) - (recordSource.getStartOffset())));
        }
        SourceTestUtils.assertSourcesEqualReferenceSource(source, sources, options);
    }
}

