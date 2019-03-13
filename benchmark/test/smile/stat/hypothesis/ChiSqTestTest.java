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
package smile.stat.hypothesis;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class ChiSqTestTest {
    public ChiSqTestTest() {
    }

    /**
     * Test of test method, of class ChiSqTest.
     */
    @Test
    public void testTest() {
        System.out.println("one sample test");
        int[] bins = new int[]{ 20, 22, 13, 22, 10, 13 };
        double[] prob = new double[]{ 1.0 / 6, 1.0 / 6, 1.0 / 6, 1.0 / 6, 1.0 / 6, 1.0 / 6 };
        ChiSqTest result = ChiSqTest.test(bins, prob);
        Assert.assertEquals(8.36, result.chisq, 0.01);
        Assert.assertEquals(5, result.df, 1.0E-10);
        Assert.assertEquals(0.1375, result.pvalue, 1.0E-4);
    }

    /**
     * Test of test method, of class ChiSqTest.
     */
    @Test
    public void testTest2() {
        System.out.println("two sample test");
        int[] bins1 = new int[]{ 8, 13, 16, 10, 3 };
        int[] bins2 = new int[]{ 4, 9, 14, 16, 7 };
        ChiSqTest result = ChiSqTest.test(bins1, bins2);
        Assert.assertEquals(5.179, result.chisq, 0.01);
        Assert.assertEquals(4, result.df, 1.0E-10);
        Assert.assertEquals(0.2695, result.pvalue, 1.0E-4);
    }
}

