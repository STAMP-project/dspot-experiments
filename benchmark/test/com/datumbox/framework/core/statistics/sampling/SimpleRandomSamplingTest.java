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
package com.datumbox.framework.core.statistics.sampling;


import Constants.DOUBLE_ACCURACY_HIGH;
import com.datumbox.framework.common.dataobjects.AssociativeArray;
import com.datumbox.framework.common.dataobjects.FlatDataCollection;
import com.datumbox.framework.common.dataobjects.FlatDataList;
import com.datumbox.framework.tests.abstracts.AbstractTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for SimpleRandomSampling.
 *
 * @author Vasilis Vryniotis <bbriniotis@datumbox.com>
 */
public class SimpleRandomSamplingTest extends AbstractTest {
    /**
     * Test of weightedSampling method, of class SimpleRandomSampling.
     */
    @Test
    public void testWeightedProbabilitySampling() {
        logger.info("weightedProbabilitySampling");
        AssociativeArray frequencyTable = new AssociativeArray();
        frequencyTable.put(1, 0.2);
        frequencyTable.put(2, 0.3);
        frequencyTable.put(3, 0.25);
        frequencyTable.put(4, 0.25);
        int n = 100;
        boolean withReplacement = true;
        double expResult = n;
        FlatDataCollection sampledIds = SimpleRandomSampling.weightedSampling(frequencyTable, n, withReplacement);
        double result = sampledIds.size();
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of weightedSampling method, of class SimpleRandomSampling.
     */
    @Test
    public void testWeightedSampling() {
        logger.info("weightedSampling");
        AssociativeArray frequencyTable = new AssociativeArray();
        frequencyTable.put(1, 10);
        frequencyTable.put(2, 20);
        frequencyTable.put(3, 30);
        frequencyTable.put(4, 40);
        int n = 100;
        boolean withReplacement = true;
        double expResult = n;
        FlatDataCollection sampledIds = SimpleRandomSampling.weightedSampling(frequencyTable, n, withReplacement);
        double result = sampledIds.size();
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of randomSampling method, of class SimpleRandomSampling.
     */
    @Test
    public void testRandomSampling() {
        logger.info("randomSampling");
        FlatDataList idList = new FlatDataList();
        idList.add("a");
        idList.add("0");
        idList.add("c");
        idList.add("1");
        idList.add("5");
        int n = 100;
        boolean withReplacement = true;
        double expResult = n;
        FlatDataCollection sampledIds = SimpleRandomSampling.randomSampling(idList, n, withReplacement);
        double result = sampledIds.size();
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of mean method, of class SimpleRandomSampling.
     */
    @Test
    public void testMean() {
        logger.info("mean");
        FlatDataCollection flatDataCollection = generateFlatDataCollection();
        double expResult = 14.595333333333;
        double result = SimpleRandomSampling.mean(flatDataCollection);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of variance method, of class SimpleRandomSampling.
     */
    @Test
    public void testVariance() {
        logger.info("variance");
        FlatDataCollection flatDataCollection = generateFlatDataCollection();
        double expResult = 52.621426666667;
        double result = SimpleRandomSampling.variance(flatDataCollection);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of std method, of class SimpleRandomSampling.
     */
    @Test
    public void testStd() {
        logger.info("std");
        FlatDataCollection flatDataCollection = generateFlatDataCollection();
        double expResult = 7.2540627696944;
        double result = SimpleRandomSampling.std(flatDataCollection);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of xbarVariance method, of class SimpleRandomSampling.
     */
    @Test
    public void testXbarVariance_3args() {
        logger.info("xbarVariance");
        double variance = 52.621426666667;
        int sampleN = 15;
        int populationN = 2147483647;
        double expResult = 3.50809508661;
        double result = SimpleRandomSampling.xbarVariance(variance, sampleN, populationN);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of xbarStd method, of class SimpleRandomSampling.
     */
    @Test
    public void testXbarStd_3args() {
        logger.info("xbarStd");
        double std = 7.2540627696944;
        int sampleN = 15;
        int populationN = 2147483647;
        double expResult = 1.87299094675;
        double result = SimpleRandomSampling.xbarStd(std, sampleN, populationN);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of pbarVariance method, of class SimpleRandomSampling.
     */
    @Test
    public void testPbarVariance_3args() {
        logger.info("pbarVariance");
        double pbar = 0.19;
        int sampleN = 200;
        int populationN = 3042;
        double expResult = 7.2252088E-4;
        double result = SimpleRandomSampling.pbarVariance(pbar, sampleN, populationN);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of pbarStd method, of class SimpleRandomSampling.
     */
    @Test
    public void testPbarStd_3args() {
        logger.info("pbarStd");
        double pbar = 0.19;
        int sampleN = 200;
        int populationN = 3042;
        double expResult = 0.026879748668207;
        double result = SimpleRandomSampling.pbarStd(pbar, sampleN, populationN);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of minimumSampleSizeForMaximumXbarStd method, of class SimpleRandomSampling.
     */
    @Test
    public void testMinimumSampleSizeForMaximumXbarStd_3args() {
        logger.info("minimumSampleSizeForMaximumXbarStd");
        double maximumXbarStd = 1.2;
        double populationStd = 7.25;
        int populationN = 2147483647;
        int expResult = 37;
        int result = SimpleRandomSampling.minimumSampleSizeForMaximumXbarStd(maximumXbarStd, populationStd, populationN);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of minimumSampleSizeForGivenDandMaximumRisk method, of class SimpleRandomSampling.
     */
    @Test
    public void testMinimumSampleSizeForGivenDandMaximumRisk_4args() {
        logger.info("minimumSampleSizeForGivenDandMaximumRisk");
        double d = 0.323;
        double aLevel = 0.1;
        double populationStd = 1.7289303051309;
        int populationN = 7000;
        int expResult = 77;
        int result = SimpleRandomSampling.minimumSampleSizeForGivenDandMaximumRisk(d, aLevel, populationStd, populationN);
        Assert.assertEquals(expResult, result);
    }
}

