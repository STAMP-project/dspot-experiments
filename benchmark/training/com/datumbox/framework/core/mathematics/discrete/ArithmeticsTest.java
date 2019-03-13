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
package com.datumbox.framework.core.mathematics.discrete;


import Constants.DOUBLE_ACCURACY_HIGH;
import com.datumbox.framework.tests.abstracts.AbstractTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for Arithmetics.
 *
 * @author Vasilis Vryniotis <bbriniotis@datumbox.com>
 */
public class ArithmeticsTest extends AbstractTest {
    /**
     * Test of factorial method, of class Arithmetics.
     */
    @Test
    public void testFactorial() {
        logger.info("factorial");
        int k = 10;
        double expResult = 3628800.0;
        double result = Arithmetics.factorial(k);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }

    /**
     * Test of combination method, of class Arithmetics.
     */
    @Test
    public void testCombination() {
        logger.info("combination");
        int n = 52;
        int k = 5;
        double expResult = 2598960.0;
        double result = Arithmetics.combination(n, k);
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
    }
}

