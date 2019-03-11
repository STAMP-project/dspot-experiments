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
import com.datumbox.framework.common.dataobjects.TransposeDataCollection;
import com.datumbox.framework.common.dataobjects.TransposeDataList;
import com.datumbox.framework.tests.abstracts.AbstractTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for ClusterSampling.
 *
 * @author Vasilis Vryniotis <bbriniotis@datumbox.com>
 */
public class ClusterSamplingTest extends AbstractTest {
    /**
     * Test of nBar method, of class ClusterSampling.
     */
    @Test
    public void testNbar() {
        logger.info("Nbar");
        TransposeDataList clusterIdList = generateClusterIdList();
        double expResult = 10.0;
        double result = ClusterSampling.nBar(clusterIdList);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of randomSampling method, of class ClusterSampling.
     */
    @Test
    public void testRandomSampling() {
        logger.info("randomSampling");
        TransposeDataList clusterIdList = generateClusterIdList();
        int sampleM = 2;
        double expResult = sampleM;
        TransposeDataCollection sampledIds = ClusterSampling.randomSampling(clusterIdList, sampleM);
        double result = sampledIds.size();
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of mean method, of class ClusterSampling.
     */
    @Test
    public void testMean() {
        logger.info("mean");
        TransposeDataCollection sampleDataCollection = generateSampleDataCollection();
        double expResult = 17.5;
        double result = ClusterSampling.mean(sampleDataCollection);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of xbarVariance method, of class ClusterSampling.
     */
    @Test
    public void testXbarVariance() {
        logger.info("xbarVariance");
        TransposeDataCollection sampleDataCollection = generateSampleDataCollection();
        int populationM = 4;
        double Nbar = 10.0;
        double expResult = 2.0;
        double result = ClusterSampling.xbarVariance(sampleDataCollection, populationM, Nbar);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of xbarStd method, of class ClusterSampling.
     */
    @Test
    public void testXbarStd() {
        logger.info("xbarStd");
        TransposeDataCollection sampleDataCollection = generateSampleDataCollection();
        int populationM = 4;
        double Nbar = 10.0;
        double expResult = 1.4142135623731;
        double result = ClusterSampling.xbarStd(sampleDataCollection, populationM, Nbar);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }
}

