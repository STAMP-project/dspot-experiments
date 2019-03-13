/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.mahout.clustering.kmeans;


import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.mahout.clustering.Cluster;
import org.apache.mahout.clustering.ClusteringTestUtils;
import org.apache.mahout.clustering.iterator.ClusterWritable;
import org.apache.mahout.common.MahoutTestCase;
import org.apache.mahout.common.distance.ManhattanDistanceMeasure;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.junit.Test;


public final class TestRandomSeedGenerator extends MahoutTestCase {
    private static final double[][] RAW = new double[][]{ new double[]{ 1, 1 }, new double[]{ 2, 1 }, new double[]{ 1, 2 }, new double[]{ 2, 2 }, new double[]{ 3, 3 }, new double[]{ 4, 4 }, new double[]{ 5, 4 }, new double[]{ 4, 5 }, new double[]{ 5, 5 } };

    private FileSystem fs;

    /**
     * Story: test random seed generation generates 4 clusters with proper ids and data
     */
    @Test
    public void testRandomSeedGenerator() throws Exception {
        List<VectorWritable> points = TestRandomSeedGenerator.getPoints();
        Job job = new Job();
        Configuration conf = job.getConfiguration();
        job.setMapOutputValueClass(VectorWritable.class);
        Path input = getTestTempFilePath("random-input");
        Path output = getTestTempDirPath("random-output");
        ClusteringTestUtils.writePointsToFile(points, input, fs, conf);
        RandomSeedGenerator.buildRandom(conf, input, output, 4, new ManhattanDistanceMeasure());
        int clusterCount = 0;
        Collection<Integer> set = Sets.newHashSet();
        for (ClusterWritable clusterWritable : new org.apache.mahout.common.iterator.sequencefile.SequenceFileValueIterable<ClusterWritable>(new Path(output, "part-randomSeed"), true, conf)) {
            clusterCount++;
            Cluster cluster = clusterWritable.getValue();
            int id = cluster.getId();
            assertTrue(set.add(id));// Validate unique id's

            Vector v = cluster.getCenter();
            TestRandomSeedGenerator.assertVectorEquals(TestRandomSeedGenerator.RAW[id], v);// Validate values match

        }
        assertEquals(4, clusterCount);// Validate sample count

    }

    /**
     * Be sure that the buildRandomSeeded works in the same way as RandomSeedGenerator.buildRandom
     */
    @Test
    public void testRandomSeedGeneratorSeeded() throws Exception {
        List<VectorWritable> points = TestRandomSeedGenerator.getPoints();
        Job job = new Job();
        Configuration conf = job.getConfiguration();
        job.setMapOutputValueClass(VectorWritable.class);
        Path input = getTestTempFilePath("random-input");
        Path output = getTestTempDirPath("random-output");
        ClusteringTestUtils.writePointsToFile(points, input, fs, conf);
        RandomSeedGenerator.buildRandom(conf, input, output, 4, new ManhattanDistanceMeasure(), 1L);
        int clusterCount = 0;
        Collection<Integer> set = Sets.newHashSet();
        for (ClusterWritable clusterWritable : new org.apache.mahout.common.iterator.sequencefile.SequenceFileValueIterable<ClusterWritable>(new Path(output, "part-randomSeed"), true, conf)) {
            clusterCount++;
            Cluster cluster = clusterWritable.getValue();
            int id = cluster.getId();
            assertTrue(set.add(id));// validate unique id's

            Vector v = cluster.getCenter();
            TestRandomSeedGenerator.assertVectorEquals(TestRandomSeedGenerator.RAW[id], v);// validate values match

        }
        assertEquals(4, clusterCount);// validate sample count

    }

    /**
     * Test that initial clusters built with same random seed are reproduced
     */
    @Test
    public void testBuildRandomSeededSameInitalClusters() throws Exception {
        List<VectorWritable> points = TestRandomSeedGenerator.getPoints();
        Job job = new Job();
        Configuration conf = job.getConfiguration();
        job.setMapOutputValueClass(VectorWritable.class);
        Path input = getTestTempFilePath("random-input");
        Path output = getTestTempDirPath("random-output");
        ClusteringTestUtils.writePointsToFile(points, input, fs, conf);
        long randSeed = 1;
        RandomSeedGenerator.buildRandom(conf, input, output, 4, new ManhattanDistanceMeasure(), randSeed);
        int[] clusterIDSeq = new int[4];
        /**
         * run through all clusters once and set sequence of IDs
         */
        int clusterCount = 0;
        for (ClusterWritable clusterWritable : new org.apache.mahout.common.iterator.sequencefile.SequenceFileValueIterable<ClusterWritable>(new Path(output, "part-randomSeed"), true, conf)) {
            Cluster cluster = clusterWritable.getValue();
            clusterIDSeq[clusterCount] = cluster.getId();
            clusterCount++;
        }
        /* Rebuild cluster and run through again making sure all IDs are in the same random sequence
        Needs a better test because in this case passes when seeded with 1 and 2  fails with 1, 3
        passes when set to two
         */
        RandomSeedGenerator.buildRandom(conf, input, output, 4, new ManhattanDistanceMeasure(), randSeed);
        clusterCount = 0;
        for (ClusterWritable clusterWritable : new org.apache.mahout.common.iterator.sequencefile.SequenceFileValueIterable<ClusterWritable>(new Path(output, "part-randomSeed"), true, conf)) {
            Cluster cluster = clusterWritable.getValue();
            // Make sure cluster ids are in same random sequence
            assertEquals(clusterIDSeq[clusterCount], cluster.getId());
            clusterCount++;
        }
    }
}

