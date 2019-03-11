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
public class QuickSortTest {
    double[] big = new double[1000000];

    public QuickSortTest() {
        for (int i = 0; i < (big.length); i++)
            big[i] = Math.random();

    }

    /**
     * Test of sort method, of class QuickSort.
     */
    @Test
    public void testSortInt() {
        System.out.println("sort int");
        int[] data1 = new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        int[] order1 = new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Assert.assertTrue(Math.equals(order1, QuickSort.sort(data1)));
        int[] data2 = new int[]{ 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };
        int[] order2 = new int[]{ 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };
        Assert.assertTrue(Math.equals(order2, QuickSort.sort(data2)));
        int[] data3 = new int[]{ 0, 1, 2, 3, 5, 4, 6, 7, 8, 9 };
        int[] order3 = new int[]{ 0, 1, 2, 3, 5, 4, 6, 7, 8, 9 };
        Assert.assertTrue(Math.equals(order3, QuickSort.sort(data3)));
        int[] data4 = new int[]{ 4, 1, 2, 3, 0, 5, 6, 7, 8, 9 };
        int[] order4 = new int[]{ 4, 1, 2, 3, 0, 5, 6, 7, 8, 9 };
        Assert.assertTrue(Math.equals(order4, QuickSort.sort(data4)));
    }

    /**
     * Test of sort method, of class QuickSort.
     */
    @Test
    public void testSortFloat() {
        System.out.println("sort float");
        float[] data1 = new float[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        int[] order1 = new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Assert.assertTrue(Math.equals(order1, QuickSort.sort(data1)));
        float[] data2 = new float[]{ 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };
        int[] order2 = new int[]{ 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };
        Assert.assertTrue(Math.equals(order2, QuickSort.sort(data2)));
        float[] data3 = new float[]{ 0, 1, 2, 3, 5, 4, 6, 7, 8, 9 };
        int[] order3 = new int[]{ 0, 1, 2, 3, 5, 4, 6, 7, 8, 9 };
        Assert.assertTrue(Math.equals(order3, QuickSort.sort(data3)));
        float[] data4 = new float[]{ 4, 1, 2, 3, 0, 5, 6, 7, 8, 9 };
        int[] order4 = new int[]{ 4, 1, 2, 3, 0, 5, 6, 7, 8, 9 };
        Assert.assertTrue(Math.equals(order4, QuickSort.sort(data4)));
    }

    /**
     * Test of sort method, of class QuickSort.
     */
    @Test
    public void testSortDouble() {
        System.out.println("sort double");
        double[] data1 = new double[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        int[] order1 = new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Assert.assertTrue(Math.equals(order1, QuickSort.sort(data1)));
        double[] data2 = new double[]{ 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };
        int[] order2 = new int[]{ 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };
        Assert.assertTrue(Math.equals(order2, QuickSort.sort(data2)));
        double[] data3 = new double[]{ 0, 1, 2, 3, 5, 4, 6, 7, 8, 9 };
        int[] order3 = new int[]{ 0, 1, 2, 3, 5, 4, 6, 7, 8, 9 };
        Assert.assertTrue(Math.equals(order3, QuickSort.sort(data3)));
        double[] data4 = new double[]{ 4, 1, 2, 3, 0, 5, 6, 7, 8, 9 };
        int[] order4 = new int[]{ 4, 1, 2, 3, 0, 5, 6, 7, 8, 9 };
        Assert.assertTrue(Math.equals(order4, QuickSort.sort(data4)));
    }

    /**
     * Test of sort method, of class QuickSort.
     */
    @Test
    public void testSortObject() {
        System.out.println("sort object");
        Integer[] data1 = new Integer[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        int[] order1 = new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Assert.assertTrue(Math.equals(order1, QuickSort.sort(data1)));
        Integer[] data2 = new Integer[]{ 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };
        int[] order2 = new int[]{ 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };
        Assert.assertTrue(Math.equals(order2, QuickSort.sort(data2)));
        Integer[] data3 = new Integer[]{ 0, 1, 2, 3, 5, 4, 6, 7, 8, 9 };
        int[] order3 = new int[]{ 0, 1, 2, 3, 5, 4, 6, 7, 8, 9 };
        Assert.assertTrue(Math.equals(order3, QuickSort.sort(data3)));
        Integer[] data4 = new Integer[]{ 4, 1, 2, 3, 0, 5, 6, 7, 8, 9 };
        int[] order4 = new int[]{ 4, 1, 2, 3, 0, 5, 6, 7, 8, 9 };
        Assert.assertTrue(Math.equals(order4, QuickSort.sort(data4)));
    }

    /**
     * Test of sort method, of class QuickSort.
     */
    @Test
    public void testSortBig() {
        System.out.println("sort big array");
        QuickSort.sort(big);
    }
}

