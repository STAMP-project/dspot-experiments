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
public class ARMTest {
    int[][] itemsets = new int[][]{ new int[]{ 1, 3 }, new int[]{ 2 }, new int[]{ 4 }, new int[]{ 2, 3, 4 }, new int[]{ 2, 3 }, new int[]{ 2, 3 }, new int[]{ 1, 2, 3, 4 }, new int[]{ 1, 3 }, new int[]{ 1, 2, 3 }, new int[]{ 1, 2, 3 } };

    public ARMTest() {
    }

    /**
     * Test of learn method, of class ARM.
     */
    @Test
    public void testLearn() {
        System.out.println("learn");
        ARM instance = new ARM(itemsets, 3);
        instance.learn(0.5, System.out);
        List<AssociationRule> rules = instance.learn(0.5);
        Assert.assertEquals(9, rules.size());
        Assert.assertEquals(0.6, rules.get(0).support, 0.01);
        Assert.assertEquals(0.75, rules.get(0).confidence, 0.01);
        Assert.assertEquals(1, rules.get(0).antecedent.length);
        Assert.assertEquals(3, rules.get(0).antecedent[0]);
        Assert.assertEquals(1, rules.get(0).consequent.length);
        Assert.assertEquals(2, rules.get(0).consequent[0]);
        Assert.assertEquals(0.3, rules.get(4).support, 0.01);
        Assert.assertEquals(0.6, rules.get(4).confidence, 0.01);
        Assert.assertEquals(1, rules.get(4).antecedent.length);
        Assert.assertEquals(1, rules.get(4).antecedent[0]);
        Assert.assertEquals(1, rules.get(4).consequent.length);
        Assert.assertEquals(2, rules.get(4).consequent[0]);
        Assert.assertEquals(0.3, rules.get(8).support, 0.01);
        Assert.assertEquals(0.6, rules.get(8).confidence, 0.01);
        Assert.assertEquals(1, rules.get(8).antecedent.length);
        Assert.assertEquals(1, rules.get(8).antecedent[0]);
        Assert.assertEquals(2, rules.get(8).consequent.length);
        Assert.assertEquals(3, rules.get(8).consequent[0]);
        Assert.assertEquals(2, rules.get(8).consequent[1]);
    }

    /**
     * Test of learn method, of class ARM.
     */
    @Test
    public void testLearnPima() {
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
        ARM instance = new ARM(data, 20);
        long numRules = instance.learn(0.9, System.out);
        System.out.format("%d association rules discovered%n", numRules);
        Assert.assertEquals(6803, numRules);
        Assert.assertEquals(6803, instance.learn(0.9).size());
    }

    /**
     * Test of learn method, of class ARM.
     */
    @Test
    public void testLearnKosarak() {
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
        ARM instance = new ARM(data, 0.003);
        long numRules = instance.learn(0.5, System.out);
        System.out.format("%d association rules discovered%n", numRules);
        Assert.assertEquals(17932, numRules);
    }
}

