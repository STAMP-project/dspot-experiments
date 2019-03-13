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
package smile.validation;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class RSSTest {
    public RSSTest() {
    }

    /**
     * Test of measure method, of class RSS.
     */
    @Test
    public void testMeasure() {
        System.out.println("measure");
        double[] truth = new double[]{ 83.0, 88.5, 88.2, 89.5, 96.2, 98.1, 99.0, 100.0, 101.2, 104.6, 108.4, 110.8, 112.6, 114.2, 115.7, 116.9 };
        double[] prediction = new double[]{ 83.60082, 86.94973, 88.09677, 90.73065, 96.53551, 97.83067, 98.12232, 99.87776, 103.20861, 105.08598, 107.33369, 109.57251, 112.98358, 113.92898, 115.50214, 117.54028 };
        RSS instance = new RSS();
        double expResult = 12.844;
        double result = instance.measure(truth, prediction);
        Assert.assertEquals(expResult, result, 0.001);
    }
}

