/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.policies;


import Resource.VCORES_INDEX;
import ResourceInformation.MEMORY_MB;
import ResourceInformation.VCORES;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FakeSchedulable;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.Schedulable;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.policies.DominantResourceFairnessPolicy.DominantResourceFairnessComparator2;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.policies.DominantResourceFairnessPolicy.DominantResourceFairnessComparatorN;
import org.apache.hadoop.yarn.util.resource.ResourceUtils;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.junit.Assert;
import org.junit.Test;


/**
 * comparator.compare(sched1, sched2) < 0 means that sched1 should get a
 * container before sched2
 */
public class TestDominantResourceFairnessPolicy {
    @Test
    public void testSameDominantResource() {
        Comparator c = createComparator(8000, 4);
        Schedulable s1 = createSchedulable(1000, 1);
        Schedulable s2 = createSchedulable(2000, 1);
        Assert.assertTrue("Comparison didn't return a value less than 0", ((c.compare(s1, s2)) < 0));
    }

    @Test
    public void testSameDominantResource2() {
        ResourceUtils.resetResourceTypes(new Configuration());
        testSameDominantResource();
    }

    @Test
    public void testDifferentDominantResource() {
        Comparator c = createComparator(8000, 8);
        Schedulable s1 = createSchedulable(4000, 3);
        Schedulable s2 = createSchedulable(2000, 5);
        Assert.assertTrue("Comparison didn't return a value less than 0", ((c.compare(s1, s2)) < 0));
    }

    @Test
    public void testDifferentDominantResource2() {
        ResourceUtils.resetResourceTypes(new Configuration());
        testDifferentDominantResource();
    }

    @Test
    public void testOneIsNeedy() {
        Comparator c = createComparator(8000, 8);
        Schedulable s1 = createSchedulable(2000, 5, 0, 6);
        Schedulable s2 = createSchedulable(4000, 3, 0, 0);
        Assert.assertTrue("Comparison didn't return a value less than 0", ((c.compare(s1, s2)) < 0));
    }

    @Test
    public void testOneIsNeedy2() {
        ResourceUtils.resetResourceTypes(new Configuration());
        testOneIsNeedy();
    }

    @Test
    public void testBothAreNeedy() {
        Comparator c = createComparator(8000, 100);
        // dominant share is 2000/8000
        Schedulable s1 = createSchedulable(2000, 5);
        // dominant share is 4000/8000
        Schedulable s2 = createSchedulable(4000, 3);
        Assert.assertTrue("Comparison didn't return a value less than 0", ((c.compare(s1, s2)) < 0));
        // dominant min share is 2/3
        s1 = createSchedulable(2000, 5, 3000, 6);
        // dominant min share is 4/5
        s2 = createSchedulable(4000, 3, 5000, 4);
        Assert.assertTrue("Comparison didn't return a value less than 0", ((c.compare(s1, s2)) < 0));
    }

    @Test
    public void testBothAreNeedy2() {
        ResourceUtils.resetResourceTypes(new Configuration());
        testBothAreNeedy();
    }

    @Test
    public void testEvenWeightsSameDominantResource() {
        Assert.assertTrue(((createComparator(8000, 8).compare(createSchedulable(3000, 1, 2.0F), createSchedulable(2000, 1))) < 0));
        Assert.assertTrue(((createComparator(8000, 8).compare(createSchedulable(1000, 3, 2.0F), createSchedulable(1000, 2))) < 0));
    }

    @Test
    public void testEvenWeightsSameDominantResource2() {
        ResourceUtils.resetResourceTypes(new Configuration());
        testEvenWeightsSameDominantResource();
    }

    @Test
    public void testEvenWeightsDifferentDominantResource() {
        Assert.assertTrue(((createComparator(8000, 8).compare(createSchedulable(1000, 3, 2.0F), createSchedulable(2000, 1))) < 0));
        Assert.assertTrue(((createComparator(8000, 8).compare(createSchedulable(3000, 1, 2.0F), createSchedulable(1000, 2))) < 0));
    }

    @Test
    public void testEvenWeightsDifferentDominantResource2() {
        ResourceUtils.resetResourceTypes(new Configuration());
        testEvenWeightsDifferentDominantResource();
    }

    @Test
    public void testSortShares() {
        float[][] ratios1 = new float[][]{ new float[]{ 0.3F, 2.0F }, new float[]{ 0.2F, 1.0F }, new float[]{ 0.4F, 0.1F } };
        float[][] ratios2 = new float[][]{ new float[]{ 0.2F, 9.0F }, new float[]{ 0.3F, 2.0F }, new float[]{ 0.25F, 0.1F } };
        float[][] expected1 = new float[][]{ new float[]{ 0.4F, 0.1F }, new float[]{ 0.3F, 2.0F }, new float[]{ 0.2F, 1.0F } };
        float[][] expected2 = new float[][]{ new float[]{ 0.3F, 2.0F }, new float[]{ 0.25F, 0.1F }, new float[]{ 0.2F, 9.0F } };
        DominantResourceFairnessComparatorN comparator = new DominantResourceFairnessComparatorN();
        comparator.sortRatios(ratios1, ratios2);
        for (int i = 0; i < (ratios1.length); i++) {
            Assert.assertArrayEquals(("The shares array was not sorted into the " + "expected order: incorrect inner array encountered"), expected1[i], ratios1[i], 1.0E-5F);
            Assert.assertArrayEquals(("The shares array was not sorted into the " + "expected order: incorrect inner array encountered"), expected2[i], ratios2[i], 1.0E-5F);
        }
    }

    @Test
    public void testCalculateClusterAndFairRatios() {
        Map<String, Integer> index = ResourceUtils.getResourceTypeIndex();
        Resource used = Resources.createResource(10, 5);
        Resource capacity = Resources.createResource(100, 10);
        float[][] shares = new float[3][2];
        DominantResourceFairnessComparatorN comparator = new DominantResourceFairnessComparatorN();
        used.setResourceValue("test", 2L);
        capacity.setResourceValue("test", 5L);
        int dominant = comparator.calculateClusterAndFairRatios(used, capacity, shares, 1.0F);
        Assert.assertEquals(("Calculated usage ratio for memory (10MB out of 100MB) is " + "incorrect"), 0.1, shares[index.get(MEMORY_MB.getName())][0], 1.0E-5);
        Assert.assertEquals(("Calculated usage ratio for vcores (5 out of 10) is " + "incorrect"), 0.5, shares[index.get(VCORES.getName())][0], 1.0E-5);
        Assert.assertEquals(("Calculated usage ratio for test resource (2 out of 5) is " + "incorrect"), 0.4, shares[index.get("test")][0], 1.0E-5);
        Assert.assertEquals("The wrong dominant resource index was returned", index.get(VCORES.getName()).intValue(), dominant);
    }

    @Test
    public void testCalculateClusterAndFairRatios2() {
        ResourceUtils.resetResourceTypes(new Configuration());
        Resource used = Resources.createResource(10, 5);
        Resource capacity = Resources.createResource(100, 10);
        double[] shares = new double[2];
        DominantResourceFairnessComparator2 comparator = new DominantResourceFairnessComparator2();
        int dominant = comparator.calculateClusterAndFairRatios(used.getResources(), 1.0F, capacity.getResources(), shares);
        Assert.assertEquals(("Calculated usage ratio for memory (10MB out of 100MB) is " + "incorrect"), 0.1, shares[Resource.MEMORY_INDEX], 1.0E-5);
        Assert.assertEquals(("Calculated usage ratio for vcores (5 out of 10) is " + "incorrect"), 0.5, shares[Resource.VCORES_INDEX], 1.0E-5);
        Assert.assertEquals("The wrong dominant resource index was returned", VCORES_INDEX, dominant);
    }

    @Test
    public void testCalculateMinShareRatios() {
        Map<String, Integer> index = ResourceUtils.getResourceTypeIndex();
        Resource used = Resources.createResource(10, 5);
        Resource minShares = Resources.createResource(5, 10);
        float[][] ratios = new float[3][3];
        DominantResourceFairnessComparatorN comparator = new DominantResourceFairnessComparatorN();
        used.setResourceValue("test", 2L);
        minShares.setResourceValue("test", 0L);
        comparator.calculateMinShareRatios(used, minShares, ratios);
        Assert.assertEquals(("Calculated min share ratio for memory (10MB out of 5MB) is " + "incorrect"), 2.0, ratios[index.get(MEMORY_MB.getName())][2], 1.0E-5F);
        Assert.assertEquals(("Calculated min share ratio for vcores (5 out of 10) is " + "incorrect"), 0.5, ratios[index.get(VCORES.getName())][2], 1.0E-5F);
        Assert.assertEquals(("Calculated min share ratio for test resource (0 out of 5) is " + "incorrect"), Float.POSITIVE_INFINITY, ratios[index.get("test")][2], 1.0E-5F);
    }

    @Test
    public void testCalculateMinShareRatios2() {
        ResourceUtils.resetResourceTypes(new Configuration());
        Resource used = Resources.createResource(10, 5);
        Resource minShares = Resources.createResource(5, 10);
        DominantResourceFairnessComparator2 comparator = new DominantResourceFairnessComparator2();
        double[] ratios = comparator.calculateMinShareRatios(used.getResources(), minShares.getResources());
        Assert.assertEquals(("Calculated min share ratio for memory (10MB out of 5MB) is " + "incorrect"), 2.0, ratios[Resource.MEMORY_INDEX], 1.0E-5F);
        Assert.assertEquals(("Calculated min share ratio for vcores (5 out of 10) is " + "incorrect"), 0.5, ratios[Resource.VCORES_INDEX], 1.0E-5F);
    }

    @Test
    public void testCompareShares() {
        float[][] ratios1 = new float[][]{ new float[]{ 0.4F, 0.1F, 2.0F }, new float[]{ 0.3F, 2.0F, 0.1F }, new float[]{ 0.2F, 1.0F, 9.0F } };
        float[][] ratios2 = new float[][]{ new float[]{ 0.3F, 2.0F, 1.0F }, new float[]{ 0.2F, 0.1F, 0.5F }, new float[]{ 0.2F, 1.0F, 2.0F } };
        float[][] ratios3 = new float[][]{ new float[]{ 0.3F, 2.0F, 1.0F }, new float[]{ 0.2F, 0.1F, 2.0F }, new float[]{ 0.1F, 2.0F, 1.0F } };
        DominantResourceFairnessComparatorN comparator = new DominantResourceFairnessComparatorN();
        int ret = comparator.compareRatios(ratios1, ratios2, 0);
        Assert.assertEquals(("Expected the first array to be larger because the first " + "usage ratio element is larger"), 1, ret);
        ret = comparator.compareRatios(ratios2, ratios1, 0);
        Assert.assertEquals(("Expected the first array to be smaller because the first " + "usage ratio element is smaller"), (-1), ret);
        ret = comparator.compareRatios(ratios1, ratios1, 0);
        Assert.assertEquals(("Expected the arrays to be equal, since they're the same " + "array"), 0, ret);
        ret = comparator.compareRatios(ratios2, ratios2, 0);
        Assert.assertEquals(("Expected the arrays to be equal, since they're the same " + "array"), 0, ret);
        ret = comparator.compareRatios(ratios3, ratios3, 0);
        Assert.assertEquals(("Expected the arrays to be equal, since they're the same " + "array"), 0, ret);
        ret = comparator.compareRatios(ratios2, ratios3, 0);
        Assert.assertEquals(("Expected the first array to be larger because the last " + "usage ratio element is larger, and all other elements are equal"), 1, ret);
        ret = comparator.compareRatios(ratios1, ratios2, 1);
        Assert.assertEquals(("Expected the first array to be smaller because the first " + "fair share ratio element is smaller"), (-1), ret);
        ret = comparator.compareRatios(ratios2, ratios1, 1);
        Assert.assertEquals(("Expected the first array to be larger because the first " + "fair share ratio element is larger"), 1, ret);
        ret = comparator.compareRatios(ratios1, ratios1, 1);
        Assert.assertEquals(("Expected the arrays to be equal, since they're the same " + "array"), 0, ret);
        ret = comparator.compareRatios(ratios2, ratios2, 1);
        Assert.assertEquals(("Expected the arrays to be equal, since they're the same " + "array"), 0, ret);
        ret = comparator.compareRatios(ratios3, ratios3, 1);
        Assert.assertEquals(("Expected the arrays to be equal, since they're the same " + "array"), 0, ret);
        ret = comparator.compareRatios(ratios2, ratios3, 1);
        Assert.assertEquals(("Expected the first array to be smaller because the last " + "usage ratio element is smaller, and all other elements are equal"), (-1), ret);
        ret = comparator.compareRatios(ratios1, ratios2, 2);
        Assert.assertEquals(("Expected the first array to be larger because the first " + "min share ratio element is larger"), 1, ret);
        ret = comparator.compareRatios(ratios2, ratios1, 2);
        Assert.assertEquals(("Expected the first array to be smaller because the first " + "min share ratio element is smaller"), (-1), ret);
        ret = comparator.compareRatios(ratios1, ratios1, 2);
        Assert.assertEquals(("Expected the arrays to be equal, since they're the same " + "array"), 0, ret);
        ret = comparator.compareRatios(ratios2, ratios2, 2);
        Assert.assertEquals(("Expected the arrays to be equal, since they're the same " + "array"), 0, ret);
        ret = comparator.compareRatios(ratios3, ratios3, 2);
        Assert.assertEquals(("Expected the arrays to be equal, since they're the same " + "array"), 0, ret);
        ret = comparator.compareRatios(ratios2, ratios3, 2);
        Assert.assertEquals(("Expected the first array to be smaller because the second " + ("min share ratio element is smaller, and all the first elements are " + "equal")), (-1), ret);
    }

    @Test
    public void testCompareSchedulablesWithClusterResourceChanges() {
        Schedulable schedulable1 = createSchedulable(2000, 1);
        Schedulable schedulable2 = createSchedulable(1000, 2);
        // schedulable1 has share weights [1/2, 1/5], schedulable2 has share
        // weights [1/4, 2/5], schedulable1 > schedulable2 since 1/2 > 2/5
        Assert.assertTrue(((createComparator(4000, 5).compare(schedulable1, schedulable2)) > 0));
        // share weights have changed because of the cluster resource change.
        // schedulable1 has share weights [1/4, 1/6], schedulable2 has share
        // weights [1/8, 1/3], schedulable1 < schedulable2 since 1/4 < 1/3
        Assert.assertTrue(((createComparator(8000, 6).compare(schedulable1, schedulable2)) < 0));
    }

    @Test
    public void testModWhileSorting() {
        final List<FakeSchedulable> schedulableList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            schedulableList.add(((FakeSchedulable) (createSchedulable(((i % 10) * 100), ((i % 3) * 2)))));
        }
        Comparator DRFComparator = createComparator(100000, 50000);
        /* The old sort should fail, but timing it makes testing to flaky.
        TimSort which is used does not handle the concurrent modification of
        objects it is sorting. This is the test that should fail:
         modThread.start();
         try {
           Collections.sort(schedulableList, DRFComparator);
         } catch (IllegalArgumentException iae) {
           // failed sort
         }
         */
        TreeSet<Schedulable> sortedSchedulable = new TreeSet(DRFComparator);
        Thread modThread = modificationThread(schedulableList);
        modThread.start();
        sortedSchedulable.addAll(schedulableList);
        try {
            modThread.join();
        } catch (InterruptedException ie) {
            Assert.fail(("ModThread join failed: " + (ie.getMessage())));
        }
    }
}

