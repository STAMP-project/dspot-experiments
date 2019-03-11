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
package smile.sort;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class QuickSelectTest {
    public QuickSelectTest() {
    }

    /**
     * Test of median method, of class QuickSelect.
     */
    @Test
    public void testMedianInt() {
        System.out.println("median int");
        int[] data1 = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Assert.assertEquals(5, QuickSelect.median(data1));
        int[] data2 = new int[]{ 5, 2, 3, 4, 1, 6, 7, 8, 9 };
        Assert.assertEquals(5, QuickSelect.median(data2));
        int[] data3 = new int[]{ 1, 2, 3, 4, 9, 6, 7, 8, 5 };
        Assert.assertEquals(5, QuickSelect.median(data3));
        int[] data4 = new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Assert.assertEquals(5, QuickSelect.median(data4));
        int[] data5 = new int[]{ 5, 1, 2, 3, 4, 0, 6, 7, 8, 9 };
        Assert.assertEquals(5, QuickSelect.median(data5));
        int[] data6 = new int[]{ 0, 1, 2, 3, 4, 9, 6, 7, 8, 5 };
        Assert.assertEquals(5, QuickSelect.median(data6));
    }

    /**
     * Test of median method, of class QuickSelect.
     */
    @Test
    public void testMedianFloat() {
        System.out.println("median float");
        float[] data1 = new float[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Assert.assertEquals(5, QuickSelect.median(data1), 1.0E-10);
        float[] data2 = new float[]{ 5, 2, 3, 4, 1, 6, 7, 8, 9 };
        Assert.assertEquals(5, QuickSelect.median(data2), 1.0E-10);
        float[] data3 = new float[]{ 1, 2, 3, 4, 9, 6, 7, 8, 5 };
        Assert.assertEquals(5, QuickSelect.median(data3), 1.0E-10);
        float[] data4 = new float[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Assert.assertEquals(5, QuickSelect.median(data4), 1.0E-10);
        float[] data5 = new float[]{ 5, 1, 2, 3, 4, 0, 6, 7, 8, 9 };
        Assert.assertEquals(5, QuickSelect.median(data5), 1.0E-10);
        float[] data6 = new float[]{ 0, 1, 2, 3, 4, 9, 6, 7, 8, 5 };
        Assert.assertEquals(5, QuickSelect.median(data6), 1.0E-10);
    }

    /**
     * Test of median method, of class QuickSelect.
     */
    @Test
    public void testMedianDouble() {
        System.out.println("median double");
        double[] data1 = new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Assert.assertEquals(5, QuickSelect.median(data1), 1.0E-10);
        double[] data2 = new double[]{ 5, 2, 3, 4, 1, 6, 7, 8, 9 };
        Assert.assertEquals(5, QuickSelect.median(data2), 1.0E-10);
        double[] data3 = new double[]{ 1, 2, 3, 4, 9, 6, 7, 8, 5 };
        Assert.assertEquals(5, QuickSelect.median(data3), 1.0E-10);
        double[] data4 = new double[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Assert.assertEquals(5, QuickSelect.median(data4), 1.0E-10);
        double[] data5 = new double[]{ 5, 1, 2, 3, 4, 0, 6, 7, 8, 9 };
        Assert.assertEquals(5, QuickSelect.median(data5), 1.0E-10);
        double[] data6 = new double[]{ 0, 1, 2, 3, 4, 9, 6, 7, 8, 5 };
        Assert.assertEquals(5, QuickSelect.median(data6), 1.0E-10);
    }

    /**
     * Test of median method, of class QuickSelect.
     */
    @Test
    public void testMedianObject() {
        System.out.println("median object");
        Integer[] data1 = new Integer[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Assert.assertEquals(5, QuickSelect.median(data1), 1.0E-10);
        Integer[] data2 = new Integer[]{ 5, 2, 3, 4, 1, 6, 7, 8, 9 };
        Assert.assertEquals(5, QuickSelect.median(data2), 1.0E-10);
        Integer[] data3 = new Integer[]{ 1, 2, 3, 4, 9, 6, 7, 8, 5 };
        Assert.assertEquals(5, QuickSelect.median(data3), 1.0E-10);
        Integer[] data4 = new Integer[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Assert.assertEquals(5, QuickSelect.median(data4), 1.0E-10);
        Integer[] data5 = new Integer[]{ 5, 1, 2, 3, 4, 0, 6, 7, 8, 9 };
        Assert.assertEquals(5, QuickSelect.median(data5), 1.0E-10);
        Integer[] data6 = new Integer[]{ 0, 1, 2, 3, 4, 9, 6, 7, 8, 5 };
        Assert.assertEquals(5, QuickSelect.median(data6), 1.0E-10);
    }
}

