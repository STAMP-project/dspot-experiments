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
 * @author Haifeng Li
 */
@SuppressWarnings("unused")
public class TotalSupportTreeTest {
    int[][] itemsets = new int[][]{ new int[]{ 1, 3 }, new int[]{ 2 }, new int[]{ 4 }, new int[]{ 2, 3, 4 }, new int[]{ 2, 3 }, new int[]{ 2, 3 }, new int[]{ 1, 2, 3, 4 }, new int[]{ 1, 3 }, new int[]{ 1, 2, 3 }, new int[]{ 1, 2, 3 } };

    public TotalSupportTreeTest() {
    }

    /**
     * Test of getSupport method, of class TotalSupportTree.
     */
    @Test
    public void testGetSupport() {
        System.out.println("getSupport");
        FPGrowth fpgrowth = new FPGrowth(itemsets, 3);
        TotalSupportTree ttree = fpgrowth.buildTotalSupportTree();
        int[][] items = new int[][]{ new int[]{ 3, 2, 1 }, new int[]{ 3 }, new int[]{ 3, 1 }, new int[]{ 3, 2 }, new int[]{ 4 }, new int[]{ 2 } };
        Assert.assertEquals(3, ttree.getSupport(items[0]));
        Assert.assertEquals(8, ttree.getSupport(items[1]));
        Assert.assertEquals(5, ttree.getSupport(items[2]));
        Assert.assertEquals(6, ttree.getSupport(items[3]));
        Assert.assertEquals(3, ttree.getSupport(items[4]));
        Assert.assertEquals(7, ttree.getSupport(items[5]));
    }

    /**
     * Test of getFrequentMaximalItemsets method, of class TotalSupportTree.
     */
    @Test
    public void testGetFrequentItemsets_0args() {
        System.out.println("getFrequentItemsets");
        FPGrowth fpgrowth = new FPGrowth(itemsets, 3);
        TotalSupportTree ttree = fpgrowth.buildTotalSupportTree();
        List<ItemSet> results = ttree.getFrequentItemsets();
        Assert.assertEquals(8, results.size());
        Assert.assertEquals(8, results.get(0).support);
        Assert.assertEquals(1, results.get(0).items.length);
        Assert.assertEquals(3, results.get(0).items[0]);
        Assert.assertEquals(7, results.get(1).support);
        Assert.assertEquals(1, results.get(1).items.length);
        Assert.assertEquals(2, results.get(1).items[0]);
        Assert.assertEquals(3, results.get(6).support);
        Assert.assertEquals(3, results.get(6).items.length);
        Assert.assertEquals(3, results.get(6).items[0]);
        Assert.assertEquals(2, results.get(6).items[1]);
        Assert.assertEquals(1, results.get(6).items[2]);
        Assert.assertEquals(3, results.get(7).support);
        Assert.assertEquals(1, results.get(7).items.length);
        Assert.assertEquals(4, results.get(7).items[0]);
    }

    /**
     * Test of getFrequentMaximalItemsets method, of class TotalSupportTree.
     */
    @Test
    public void testGetFrequentItemsets_PrintStream() {
        System.out.println("getFrequentItemsets");
        FPGrowth fpgrowth = new FPGrowth(itemsets, 3);
        TotalSupportTree ttree = fpgrowth.buildTotalSupportTree();
        long n = ttree.getFrequentItemsets(System.out);
        Assert.assertEquals(8, n);
    }

    /**
     * Test of getFrequentItemsets method, of class TotalSupportTree.
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
        TotalSupportTree ttree = fpgrowth.buildTotalSupportTree();
        System.out.format("Done building total support tree: %.2f secs.%n", (((System.currentTimeMillis()) - time) / 1000.0));
        time = System.currentTimeMillis();
        long numItemsets = ttree.getFrequentItemsets(System.out);
        System.out.format("%d frequent item sets discovered: %.2f secs.%n", numItemsets, (((System.currentTimeMillis()) - time) / 1000.0));
        Assert.assertEquals(1803, numItemsets);
        Assert.assertEquals(1803, ttree.getFrequentItemsets().size());
    }

    /**
     * Test of getFrequentItemsets method, of class TotalSupportTree.
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
        TotalSupportTree ttree = fpgrowth.buildTotalSupportTree();
        System.out.format("Done building total support tree: %.2f secs.%n", (((System.currentTimeMillis()) - time) / 1000.0));
        time = System.currentTimeMillis();
        // long numItemsets = ttree.getFrequentItemsets(System.out);
        // System.out.format("%d frequent item sets discovered: %.2f secs.%n", numItemsets, (System.currentTimeMillis() - time) / 1000.0);
        Assert.assertEquals(219725, ttree.getFrequentItemsets().size());
    }
}

