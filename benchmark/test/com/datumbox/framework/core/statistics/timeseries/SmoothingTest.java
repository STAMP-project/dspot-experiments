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
package com.datumbox.framework.core.statistics.timeseries;


import Constants.DOUBLE_ACCURACY_HIGH;
import com.datumbox.framework.common.dataobjects.FlatDataList;
import com.datumbox.framework.tests.abstracts.AbstractTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for Smoothing.
 *
 * @author Vasilis Vryniotis <bbriniotis@datumbox.com>
 */
public class SmoothingTest extends AbstractTest {
    /**
     * Test of simpleMovingAverage method, of class Smoothing.
     */
    @Test
    public void testSimpleMovingAverage() {
        logger.info("simpleMovingAverage");
        FlatDataList flatDataList = generateFlatDataList();
        int N = 3;
        double expResult = 23.0;
        double result = Smoothing.simpleMovingAverage(flatDataList, N);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of simpleMovingAverageQuick method, of class Smoothing.
     */
    @Test
    public void testSimpleMovingAverageQuick() {
        logger.info("simpleMovingAverageQuick");
        double Yt = 23.5;
        double YtminusN = 20.0;
        double Ft = 23.0;
        int N = 3;
        double expResult = 24.166666666667;
        double result = Smoothing.simpleMovingAverageQuick(Yt, YtminusN, Ft, N);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of weightedMovingAverage method, of class Smoothing.
     */
    @Test
    public void testWeightedMovingAverage() {
        logger.info("weightedMovingAverage");
        FlatDataList flatDataList = generateFlatDataList();
        int N = 3;
        double expResult = 22.0;
        double result = Smoothing.weightedMovingAverage(flatDataList, N);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of simpleExponentialSmoothing method, of class Smoothing.
     */
    @Test
    public void testSimpleExponentialSmoothing() {
        logger.info("simpleExponentialSmoothing");
        FlatDataList flatDataList = generateFlatDataList();
        double a = 0.9;
        double expResult = 23.240433133179;
        double result = Smoothing.simpleExponentialSmoothing(flatDataList, a);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of simpleExponentialSmoothingQuick method, of class Smoothing.
     */
    @Test
    public void testSimpleExponentialSmoothingQuick() {
        logger.info("simpleExponentialSmoothingQuick");
        double Ytminus1 = 23.5;
        double Stminus1 = 23.240433133179;
        double a = 0.9;
        double expResult = 23.474043313318;
        double result = Smoothing.simpleExponentialSmoothingQuick(Ytminus1, Stminus1, a);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of holtWintersSmoothing method, of class Smoothing.
     */
    @Test
    public void testHoltWintersSmoothing() {
        logger.info("holtWintersSmoothing");
        FlatDataList flatDataList = generateFlatDataList();
        int season_length = 3;
        double alpha = 0.2;
        double beta = 0.01;
        double gamma = 0.01;
        double dev_gamma = 0.1;
        double expResult = 30.631118229653282;
        double result = Smoothing.holtWintersSmoothing(flatDataList, season_length, alpha, beta, gamma, dev_gamma);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }
}

