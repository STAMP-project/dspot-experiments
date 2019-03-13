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
package com.datumbox.framework.core.statistics.parametrics.onesample;


import com.datumbox.framework.tests.abstracts.AbstractTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for NormalOneSample.
 *
 * @author Vasilis Vryniotis <bbriniotis@datumbox.com>
 */
public class NormalOneSampleTest extends AbstractTest {
    /**
     * Test of testMean method, of class NormalOneSample.
     */
    @Test
    public void testTestMean() {
        logger.info("testMean");
        double xbar = 215.0;
        int n = 60;
        double H0mean = 200.0;
        double std = 55.0;
        boolean is_twoTailed = false;
        double aLevel = 0.05;
        boolean expResult = true;
        boolean result = NormalOneSample.testMean(xbar, n, H0mean, std, is_twoTailed, aLevel);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of testSum method, of class NormalOneSample.
     */
    @Test
    public void testTestSum() {
        logger.info("testSum");
        double xsum = 65.3;
        int n = 100;
        double H0sum = 0.65;
        double std = 0.02;
        boolean is_twoTailed = false;
        double aLevel = 0.05;
        boolean expResult = false;
        boolean result = NormalOneSample.testSum(xsum, n, H0sum, std, is_twoTailed, aLevel);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of testPercentage method, of class NormalOneSample.
     */
    @Test
    public void testTestPercentage() {
        logger.info("testPercentage");
        double pbar = 0.6;
        int n = 100;
        double H0p = 0.65;
        boolean is_twoTailed = false;
        double aLevel = 0.05;
        boolean expResult = false;
        boolean result = NormalOneSample.testPercentage(pbar, n, H0p, is_twoTailed, aLevel);
        Assert.assertEquals(expResult, result);
    }
}

