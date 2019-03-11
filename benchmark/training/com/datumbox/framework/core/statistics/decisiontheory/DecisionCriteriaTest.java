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
package com.datumbox.framework.core.statistics.decisiontheory;


import com.datumbox.framework.common.dataobjects.AssociativeArray;
import com.datumbox.framework.common.dataobjects.DataTable2D;
import com.datumbox.framework.tests.abstracts.AbstractTest;
import java.util.AbstractMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for DecisionCriteria.
 *
 * @author Vasilis Vryniotis <bbriniotis@datumbox.com>
 */
public class DecisionCriteriaTest extends AbstractTest {
    /**
     * Test of maxMin method, of class DecisionCriteria.
     */
    @Test
    public void testMaxMin() {
        logger.info("maxMin");
        DataTable2D payoffMatrix = generatePayoffMatrix();
        Map.Entry<Object, Object> expResult = new AbstractMap.SimpleEntry<>("A1", 400.0);
        Map.Entry<Object, Object> result = DecisionCriteria.maxMin(payoffMatrix);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of maxMax method, of class DecisionCriteria.
     */
    @Test
    public void testMaxMax() {
        logger.info("maxMax");
        DataTable2D payoffMatrix = generatePayoffMatrix();
        Map.Entry<Object, Object> expResult = new AbstractMap.SimpleEntry<>("A4", 1000.0);
        Map.Entry<Object, Object> result = DecisionCriteria.maxMax(payoffMatrix);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of savage method, of class DecisionCriteria.
     */
    @Test
    public void testSavage() {
        logger.info("savage");
        DataTable2D payoffMatrix = generatePayoffMatrix();
        Map.Entry<Object, Object> expResult = new AbstractMap.SimpleEntry<>("A2", (-400.0));
        Map.Entry<Object, Object> result = DecisionCriteria.savage(payoffMatrix);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of laplace method, of class DecisionCriteria.
     */
    @Test
    public void testLaplace() {
        logger.info("laplace");
        DataTable2D payoffMatrix = generatePayoffMatrix();
        Map.Entry<Object, Object> expResult = new AbstractMap.SimpleEntry<>("A2", 475.0);
        Map.Entry<Object, Object> result = DecisionCriteria.laplace(payoffMatrix);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of hurwiczAlpha method, of class DecisionCriteria.
     */
    @Test
    public void testHurwiczAlpha() {
        logger.info("hurwiczAlpha");
        DataTable2D payoffMatrix = generatePayoffMatrix();
        double alpha = 0.5;
        Map.Entry<Object, Object> expResult = new AbstractMap.SimpleEntry<>("A1", 400.0);
        Map.Entry<Object, Object> result = DecisionCriteria.hurwiczAlpha(payoffMatrix, alpha);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of maximumLikelihood method, of class DecisionCriteria.
     */
    @Test
    public void testMaximumLikelihood() {
        logger.info("maximumLikelihood");
        DataTable2D payoffMatrix = generatePayoffMatrix();
        AssociativeArray eventProbabilities = generateEventProbabilities();
        Map.Entry<Object, Object> expResult = new AbstractMap.SimpleEntry<>("A2", 600.0);
        Map.Entry<Object, Object> result = DecisionCriteria.maximumLikelihood(payoffMatrix, eventProbabilities);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of bayes method, of class DecisionCriteria.
     */
    @Test
    public void testBayes() {
        logger.info("bayes");
        DataTable2D payoffMatrix = generatePayoffMatrix();
        AssociativeArray eventProbabilities = generateEventProbabilities();
        Map.Entry<Object, Object> expResult = new AbstractMap.SimpleEntry<>("A2", 450.0);
        Map.Entry<Object, Object> result = DecisionCriteria.bayes(payoffMatrix, eventProbabilities);
        Assert.assertEquals(expResult, result);
    }
}

