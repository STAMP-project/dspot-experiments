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
package smile.association;


import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import smile.data.parser.IOUtils;


/**
 *
 *
 * @author Haifeng
 */
@SuppressWarnings("unused")
public class FPGrowthTest {
    int[][] itemsets = new int[][]{ new int[]{ 1, 3 }, new int[]{ 2 }, new int[]{ 4 }, new int[]{ 2, 3, 4 }, new int[]{ 2, 3 }, new int[]{ 2, 3 }, new int[]{ 1, 2, 3, 4 }, new int[]{ 1, 3 }, new int[]{ 1, 2, 3 }, new int[]{ 1, 2, 3 } };

    int[][] itemsets2 = new int[][]{ new int[]{ 1, 2, 3, 4 } };

    public FPGrowthTest() {
    }

    /**
     * Test of learn method, of class FPGrowth.
     */
    @Test
    public void testLearn_0args() {
        System.out.println("learn");
        FPGrowth fpgrowth = new FPGrowth(itemsets, 3);
        List<ItemSet> results = fpgrowth.learn();
        Assert.assertEquals(8, results.size());
        /* assertEquals(3, results.get(0).support);
        assertEquals(1, results.get(0).items.length);
        assertEquals(4, results.get(0).items[0]);

        assertEquals(5, results.get(1).support);
        assertEquals(1, results.get(1).items.length);
        assertEquals(1, results.get(1).items[0]);

        assertEquals(6, results.get(6).support);
        assertEquals(2, results.get(6).items.length);
        assertEquals(3, results.get(6).items[0]);
        assertEquals(2, results.get(6).items[1]);

        assertEquals(8, results.get(7).support);
        assertEquals(1, results.get(7).items.length);
        assertEquals(3, results.get(7).items[0]);
         */
    }

    /**
     * Test of learn method, of class FPGrowth.
     */
    @Test
    public void testLearn_PrintStream() {
        System.out.println("learn");
        FPGrowth fpgrowth = new FPGrowth(itemsets, 3);
        long n = fpgrowth.learn(System.out);
        Assert.assertEquals(8, n);
    }

    /**
     * Test of learn method, of class FPGrowth.
     */
    @Test
    public void testSinglePath() {
        System.out.println("singlePath");
        FPGrowth fpgrowth = new FPGrowth(itemsets2, 1);
        long n = fpgrowth.learn(System.out);
        Assert.assertEquals(15, n);
    }

    /**
     * Test of learn method, of class FPGrowth.
     */
    @Test
    public void testPima() {
        System.out.println("pima");
        List<int[]> dataList = new ArrayList<>(1000);
        try {
            BufferedReader input = IOUtils.getTestDataReader("transaction/pima.D38.N768.C2");
            String line;
            for (int nrow = 0; (line = input.readLine()) != null; nrow++) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] s = line.split(" ");
                int[] point = new int[s.length];
                for (int i = 0; i < (s.length); i++) {
                    point[i] = Integer.parseInt(s[i]);
                }
                dataList.add(point);
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
        int[][] data = dataList.toArray(new int[dataList.size()][]);
        int n = Math.max(data);
        System.out.format("%d transactions, %d items%n", data.length, n);
        long time = System.currentTimeMillis();
        FPGrowth fpgrowth = new FPGrowth(data, 20);
        System.out.format("Done building FP-tree: %.2f secs.%n", (((System.currentTimeMillis()) - time) / 1000.0));
        time = System.currentTimeMillis();
        long numItemsets = fpgrowth.learn(System.out);
        System.out.format("%d frequent item sets discovered: %.2f secs.%n", numItemsets, (((System.currentTimeMillis()) - time) / 1000.0));
        Assert.assertEquals(1803, numItemsets);
        Assert.assertEquals(1803, fpgrowth.learn().size());
    }

    /**
     * Test of learn method, of class FPGrowth.
     */
    @Test
    public void testKosarak() {
        System.out.println("kosarak");
        List<int[]> dataList = new ArrayList<>(1000);
        try {
            BufferedReader input = IOUtils.getTestDataReader("transaction/kosarak.dat");
            String line;
            for (int nrow = 0; (line = input.readLine()) != null; nrow++) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] s = line.split(" ");
                Set<Integer> items = new HashSet<>();
                for (int i = 0; i < (s.length); i++) {
                    items.add(Integer.parseInt(s[i]));
                }
                int j = 0;
                int[] point = new int[items.size()];
                for (int i : items) {
                    point[(j++)] = i;
                }
                dataList.add(point);
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
        int[][] data = dataList.toArray(new int[dataList.size()][]);
        int n = Math.max(data);
        System.out.format("%d transactions, %d items%n", data.length, n);
        long time = System.currentTimeMillis();
        FPGrowth fpgrowth = new FPGrowth(data, 1500);
        System.out.format("Done building FP-tree: %.2f secs.%n", (((System.currentTimeMillis()) - time) / 1000.0));
        time = System.currentTimeMillis();
        List<ItemSet> results = fpgrowth.learn();
        System.out.format("%d frequent item sets discovered: %.2f secs.%n", results.size(), (((System.currentTimeMillis()) - time) / 1000.0));
        Assert.assertEquals(219725, results.size());
    }
}

