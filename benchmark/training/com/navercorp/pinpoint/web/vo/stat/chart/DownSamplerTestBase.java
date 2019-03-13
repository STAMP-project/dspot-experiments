/**
 * Copyright 2016 Naver Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.web.vo.stat.chart;


import com.google.common.math.DoubleMath;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author HyunGil Jeong
 */
public abstract class DownSamplerTestBase<T extends Number & Comparable<? super T>> {
    public static final int DEFAULT_VALUE = -1;

    public static final int DOUBLE_COMPARISON_DELTA = 4;

    public static final int NUM_DECIMALS_FOR_ROUNDED_AVG = 1;

    protected static final Random RANDOM = new Random();

    private DownSampler<T> sampler = getSampler();

    @Test
    public void sampler_should_return_default_value() {
        // Given
        final List<T> samples = Collections.emptyList();
        // When
        T min = sampler.sampleMin(samples);
        T max = sampler.sampleMax(samples);
        double avg = sampler.sampleAvg(samples);
        double roundedAvg = sampler.sampleAvg(samples, DownSamplerTestBase.NUM_DECIMALS_FOR_ROUNDED_AVG);
        // Then
        Assert.assertEquals(DownSamplerTestBase.DEFAULT_VALUE, min.intValue());
        Assert.assertEquals(DownSamplerTestBase.DEFAULT_VALUE, max.intValue());
        Assert.assertEquals(DownSamplerTestBase.DEFAULT_VALUE, avg, DownSamplerTestBase.DOUBLE_COMPARISON_DELTA);
        Assert.assertEquals(DownSamplerTestBase.DEFAULT_VALUE, roundedAvg, DownSamplerTestBase.NUM_DECIMALS_FOR_ROUNDED_AVG);
    }

    @Test
    public void sampler_should_sample_correctly() {
        // Given
        final List<T> samples = createSamples(RandomUtils.nextInt(1, 21));
        final T expectedMin = Collections.min(samples);
        final T expectedMax = Collections.max(samples);
        final double expectedMean = DoubleMath.mean(samples);
        // When
        T min = sampler.sampleMin(samples);
        T max = sampler.sampleMax(samples);
        double avg = sampler.sampleAvg(samples);
        double roundedAvg = sampler.sampleAvg(samples, DownSamplerTestBase.NUM_DECIMALS_FOR_ROUNDED_AVG);
        // Then
        assertEquals(expectedMin, min);
        assertEquals(expectedMax, max);
        Assert.assertEquals(expectedMean, avg, DownSamplerTestBase.DOUBLE_COMPARISON_DELTA);
        Assert.assertEquals(expectedMean, roundedAvg, DownSamplerTestBase.NUM_DECIMALS_FOR_ROUNDED_AVG);
    }

    @Test
    public void sampler_should_sample_correctly_for_single_sample() {
        // Given
        final T sample = createSample();
        final List<T> samples = Arrays.asList(sample);
        final T expectedMin = sample;
        final T expectedMax = sample;
        final double expectedMean = sample.doubleValue();
        // When
        T min = sampler.sampleMin(samples);
        T max = sampler.sampleMax(samples);
        double avg = sampler.sampleAvg(samples);
        double roundedAvg = sampler.sampleAvg(samples, DownSamplerTestBase.NUM_DECIMALS_FOR_ROUNDED_AVG);
        // Then
        assertEquals(expectedMin, min);
        assertEquals(expectedMax, max);
        Assert.assertEquals(expectedMean, avg, DownSamplerTestBase.DOUBLE_COMPARISON_DELTA);
        Assert.assertEquals(expectedMean, roundedAvg, DownSamplerTestBase.NUM_DECIMALS_FOR_ROUNDED_AVG);
    }
}

