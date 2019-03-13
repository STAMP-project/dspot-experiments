/**
 * Copyright (C) 2013-2018 Vasilis Vryniotis <bbriniotis@datumbox.com>
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
package com.datumbox.framework.core.statistics.descriptivestatistics;


import Constants.DOUBLE_ACCURACY_HIGH;
import com.datumbox.framework.common.dataobjects.AssociativeArray;
import com.datumbox.framework.common.dataobjects.FlatDataCollection;
import com.datumbox.framework.common.dataobjects.FlatDataList;
import com.datumbox.framework.common.dataobjects.TransposeDataList;
import com.datumbox.framework.core.common.utilities.PHPMethods;
import com.datumbox.framework.tests.abstracts.AbstractTest;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for Descriptives.
 *
 * @author Vasilis Vryniotis <bbriniotis@datumbox.com>
 */
public class DescriptivesTest extends AbstractTest {
    /**
     * Test of count method, of class Descriptives.
     */
    @Test
    public void testCount() {
        logger.info("count");
        FlatDataCollection flatDataCollection = generateFlatDataCollection();
        int expResult = 40;
        int result = Descriptives.count(flatDataCollection);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of sum method, of class Descriptives.
     */
    @Test
    public void testSum() {
        logger.info("sum");
        FlatDataCollection flatDataCollection = generateFlatDataCollection();
        double expResult = 214.71;
        double result = Descriptives.sum(flatDataCollection);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of mean method, of class Descriptives.
     */
    @Test
    public void testMean() {
        logger.info("mean");
        FlatDataCollection flatDataCollection = generateFlatDataCollection();
        double expResult = 5.36775;
        double result = Descriptives.mean(flatDataCollection);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of meanSE method, of class Descriptives.
     */
    @Test
    public void testMeanSE() {
        logger.info("meanSE");
        FlatDataCollection flatDataCollection = generateFlatDataCollection();
        double expResult = 3.8698920757412;
        double result = Descriptives.meanSE(flatDataCollection);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of median method, of class Descriptives.
     */
    @Test
    public void testMedian() {
        logger.info("median");
        FlatDataCollection flatDataCollection = generateFlatDataCollection();
        double expResult = 2.565;
        double result = Descriptives.median(flatDataCollection);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of min method, of class Descriptives.
     */
    @Test
    public void testMin() {
        logger.info("min");
        FlatDataCollection flatDataCollection = generateFlatDataCollection();
        double expResult = -36.4;
        double result = Descriptives.min(flatDataCollection);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of max method, of class Descriptives.
     */
    @Test
    public void testMax() {
        logger.info("max");
        FlatDataCollection flatDataCollection = generateFlatDataCollection();
        double expResult = 50.85;
        double result = Descriptives.max(flatDataCollection);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of min method, of class Descriptives.
     */
    @Test
    public void testMinAbsolute() {
        logger.info("minAbsolute");
        FlatDataCollection flatDataCollection = generateFlatDataCollection();
        double expResult = 0.99;
        double result = Descriptives.minAbsolute(flatDataCollection);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of max method, of class Descriptives.
     */
    @Test
    public void testMaxAbsolute() {
        logger.info("maxAbsolute");
        FlatDataCollection flatDataCollection = generateFlatDataCollection();
        double expResult = 50.85;
        double result = Descriptives.maxAbsolute(flatDataCollection);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of range method, of class Descriptives.
     */
    @Test
    public void testRange() {
        logger.info("range");
        FlatDataCollection flatDataCollection = generateFlatDataCollection();
        double expResult = 87.25;
        double result = Descriptives.range(flatDataCollection);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of geometricMean method, of class Descriptives.
     */
    @Test
    public void testGeometricMean() {
        logger.info("geometricMean");
        FlatDataCollection flatDataCollection = new FlatDataCollection(Arrays.asList(new Object[]{ 56.0, 75.0, 45.0, 71.0, 61.0, 64.0, 58.0, 80.0, 76.0, 61.0 }));
        double expResult = 63.85415130126;
        double result = Descriptives.geometricMean(flatDataCollection);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of harmonicMean method, of class Descriptives.
     */
    @Test
    public void testHarmonicMean() {
        logger.info("harmonicMean");
        FlatDataCollection flatDataCollection = generateFlatDataCollection();
        double expResult = 9.7666088743776;
        double result = Descriptives.harmonicMean(flatDataCollection);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of variance method, of class Descriptives.
     */
    @Test
    public void testVariance() {
        logger.info("variance");
        FlatDataCollection flatDataCollection = generateFlatDataCollection();
        boolean isSample = true;
        double expResult = 599.04258711538;
        double result = Descriptives.variance(flatDataCollection, isSample);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of std method, of class Descriptives.
     */
    @Test
    public void testStd() {
        logger.info("std");
        FlatDataCollection flatDataCollection = generateFlatDataCollection();
        boolean isSample = true;
        double expResult = 24.475346516758;
        double result = Descriptives.std(flatDataCollection, isSample);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of cv method, of class Descriptives.
     */
    @Test
    public void testCv() {
        logger.info("cv");
        double std = 24.475346516758;
        double mean = 5.36775;
        double expResult = 4.5597031375824;
        double result = Descriptives.cv(std, mean);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of moment method, of class Descriptives.
     */
    @Test
    public void testMoment_Collection_int() {
        logger.info("moment");
        FlatDataCollection flatDataCollection = generateFlatDataCollection();
        int r = 3;
        double expResult = 3484.6106601128;
        double result = Descriptives.moment(flatDataCollection, r);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of moment method, of class Descriptives.
     */
    @Test
    public void testMoment_3args() {
        logger.info("moment");
        FlatDataCollection flatDataCollection = generateFlatDataCollection();
        int r = 3;
        double mean = 5.36775;
        double expResult = 3484.6106601128;
        double result = Descriptives.moment(flatDataCollection, r, mean);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of kurtosis method, of class Descriptives.
     */
    @Test
    public void testKurtosis() {
        logger.info("kurtosis");
        FlatDataCollection flatDataCollection = generateFlatDataCollection();
        double expResult = -0.74454696650836;
        double result = Descriptives.kurtosis(flatDataCollection);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of kurtosisSE method, of class Descriptives.
     */
    @Test
    public void testKurtosisSE() {
        logger.info("kurtosisSE");
        FlatDataCollection flatDataCollection = generateFlatDataCollection();
        double expResult = 0.77459666924148;
        double result = Descriptives.kurtosisSE(flatDataCollection);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of skewness method, of class Descriptives.
     */
    @Test
    public void testSkewness() {
        logger.info("skewness");
        FlatDataCollection flatDataCollection = generateFlatDataCollection();
        double expResult = 0.24686572127408;
        double result = Descriptives.skewness(flatDataCollection);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of skewnessSE method, of class Descriptives.
     */
    @Test
    public void testSkewnessSE() {
        logger.info("skewnessSE");
        FlatDataCollection flatDataCollection = generateFlatDataCollection();
        double expResult = 0.37378336538587;
        double result = Descriptives.skewnessSE(flatDataCollection);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of percentiles method, of class Descriptives.
     */
    @Test
    public void testPercentiles() {
        logger.info("percentiles");
        FlatDataCollection flatDataCollection = generateFlatDataCollection();
        int cutPoints = 4;
        AssociativeArray expResult = new AssociativeArray();
        expResult.put(25.0, (-12.5725));
        expResult.put(50.0, 2.565);
        expResult.put(75.0, 26.6925);
        AssociativeArray result = Descriptives.percentiles(flatDataCollection, cutPoints);
        for (Object key : result.keySet()) {
            double rounded = PHPMethods.round(result.getDouble(key), 5);
            result.put(key, rounded);
        }
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of quartiles method, of class Descriptives.
     */
    @Test
    public void testQuartiles() {
        logger.info("quartiles");
        FlatDataCollection flatDataCollection = new FlatDataCollection(Arrays.asList(new Object[]{ 0, 1, 2, 3 }));
        AssociativeArray expResult = new AssociativeArray();
        expResult.put(25.0, 0.25);
        expResult.put(50.0, 1.5);
        expResult.put(75.0, 2.75);
        AssociativeArray result = Descriptives.quartiles(flatDataCollection);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of covariance method, of class Descriptives.
     */
    @Test
    public void testCovariance() {
        logger.info("covariance");
        TransposeDataList transposeDataList = new TransposeDataList();
        transposeDataList.put(0, new FlatDataList(Arrays.asList(new Object[]{ 56, 75, 45, 71, 61, 64, 58, 80, 76, 61 })));
        transposeDataList.put(1, new FlatDataList(Arrays.asList(new Object[]{ 66, 70, 40, 60, 65, 56, 59, 77, 67, 63 })));
        boolean isSample = false;
        double expResult = 76.39;
        double result = Descriptives.covariance(transposeDataList, isSample);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of autocorrelation method, of class Descriptives.
     */
    @Test
    public void testAutocorrelation() {
        logger.info("autocorrelation");
        FlatDataList flatDataList = generateFlatDataCollection().toFlatDataList();
        int lags = 1;
        double expResult = -0.014242212135952;
        double result = Descriptives.autocorrelation(flatDataList, lags);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of frequencies method, of class Descriptives.
     */
    @Test
    public void testFrequencies() {
        logger.info("frequencies");
        FlatDataCollection flatDataCollection = new FlatDataCollection(Arrays.asList(new Object[]{ 1.0, 1.0, 1.0, 2.0, 3.0, 4.3, 2.0 }));
        AssociativeArray expResult = new AssociativeArray();
        expResult.put(1.0, 3);
        expResult.put(2.0, 2);
        expResult.put(3.0, 1);
        expResult.put(4.3, 1);
        AssociativeArray result = Descriptives.frequencies(flatDataCollection);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of mode method, of class Descriptives.
     */
    @Test
    public void testMode() {
        logger.info("mode");
        FlatDataCollection flatDataCollection = generateFlatDataCollection();
        flatDataCollection.add(4.65);
        flatDataCollection.add(4.65);
        FlatDataCollection expResult = new FlatDataCollection(new ArrayList());
        expResult.add(4.65);
        FlatDataCollection result = Descriptives.mode(flatDataCollection);
        Assert.assertEquals(expResult, result);
    }
}

