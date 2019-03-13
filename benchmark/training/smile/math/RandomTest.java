/**
 * *****************************************************************************
 * Copyright (c) 2010 Haifeng Li
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
 * *****************************************************************************
 */
package smile.math;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class RandomTest {
    public RandomTest() {
    }

    /**
     * Test of random method, of class Random.
     */
    @Test
    public void testRandom() {
        System.out.println("random");
        smile.math.Random instance = new Random(System.currentTimeMillis());
        for (int i = 0; i < 1000000; i++) {
            double result = instance.nextDouble();
            Assert.assertTrue((result >= 0.0));
            Assert.assertTrue((result < 1.0));
        }
    }

    /**
     * Test of random method, of class Random.
     */
    @Test
    public void testRandom_double_double() {
        System.out.println("nextDouble");
        double lo = -10.0;
        double hi = 20.0;
        smile.math.Random instance = new Random(System.currentTimeMillis());
        for (int i = 0; i < 1000000; i++) {
            double result = instance.nextDouble(lo, hi);
            Assert.assertTrue((result >= lo));
            Assert.assertTrue((result < hi));
        }
    }

    /**
     * Test of randomInt method, of class Random.
     */
    @Test
    public void testRandomInt_int() {
        System.out.println("nextInt");
        smile.math.Random instance = new Random(System.currentTimeMillis());
        for (int i = 0; i < 1000000; i++) {
            int n = (instance.nextInt(1000000)) + 1;
            int result = instance.nextInt(n);
            Assert.assertTrue((result >= 0));
            Assert.assertTrue((result < n));
        }
    }

    /**
     * Test of randomInt method, of class Random.
     */
    @Test
    public void testRandomInt_int_int() {
        System.out.println("nextInt");
        int lo = -10;
        int hi = 20;
        smile.math.Random instance = new Random(System.currentTimeMillis());
        for (int i = 0; i < 1000000; i++) {
            double result = instance.nextDouble(lo, hi);
            Assert.assertTrue((result >= lo));
            Assert.assertTrue((result < hi));
        }
    }
}

