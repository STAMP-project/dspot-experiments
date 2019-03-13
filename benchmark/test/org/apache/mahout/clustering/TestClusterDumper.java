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
package org.apache.mahout.clustering;


import ClusterDumper.OUTPUT_FORMAT.JSON;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.clustering.fuzzykmeans.FuzzyKMeansDriver;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.clustering.kmeans.RandomSeedGenerator;
import org.apache.mahout.common.MahoutTestCase;
import org.apache.mahout.common.distance.DistanceMeasure;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.utils.clustering.ClusterDumper;
import org.junit.Test;

import static Cluster.CLUSTERS_DIR;
import static Cluster.FINAL_ITERATION_SUFFIX;


public final class TestClusterDumper extends MahoutTestCase {
    private static final String[] DOCS = new String[]{ "The quick red fox jumped over the lazy brown dogs.", "The quick brown fox jumped over the lazy red dogs.", "The quick red cat jumped over the lazy brown dogs.", "The quick brown cat jumped over the lazy red dogs.", "Mary had a little lamb whose fleece was white as snow.", "Mary had a little goat whose fleece was white as snow.", "Mary had a little lamb whose fleece was black as tar.", "Dick had a little goat whose fleece was white as snow.", "Moby Dick is a story of a whale and a man obsessed.", "Moby Bob is a story of a walrus and a man obsessed.", "Moby Dick is a story of a whale and a crazy man.", "The robber wore a black fleece jacket and a baseball cap.", "The robber wore a red fleece jacket and a baseball cap.", "The robber wore a white fleece jacket and a baseball cap.", "The English Springer Spaniel is the best of all dogs." };

    private List<VectorWritable> sampleData;

    private String[] termDictionary;

    @Test
    public void testKmeans() throws Exception {
        DistanceMeasure measure = new EuclideanDistanceMeasure();
        Path input = getTestTempFilePath("input");
        Path output = getTestTempDirPath("output");
        Path initialPoints = new Path(output, (((CLUSTERS_DIR) + '0') + (FINAL_ITERATION_SUFFIX)));
        Configuration conf = getConfiguration();
        FileSystem fs = FileSystem.get(conf);
        // Write test data to file
        ClusteringTestUtils.writePointsToFile(sampleData, input, fs, conf);
        // Select initial centroids
        RandomSeedGenerator.buildRandom(conf, input, initialPoints, 8, measure, 1L);
        // Run k-means
        Path kMeansOutput = new Path(output, "kmeans");
        KMeansDriver.run(conf, getTestTempDirPath("testdata"), initialPoints, kMeansOutput, 0.001, 10, true, 0.0, false);
        // Print out clusters
        ClusterDumper clusterDumper = new ClusterDumper(TestClusterDumper.finalClusterPath(conf, output, 10), new Path(kMeansOutput, "clusteredPoints"));
        clusterDumper.printClusters(termDictionary);
    }

    @Test
    public void testJsonClusterDumper() throws Exception {
        DistanceMeasure measure = new EuclideanDistanceMeasure();
        Path input = getTestTempFilePath("input");
        Path output = getTestTempDirPath("output");
        Path initialPoints = new Path(output, (((CLUSTERS_DIR) + '0') + (FINAL_ITERATION_SUFFIX)));
        Configuration conf = getConfiguration();
        FileSystem fs = FileSystem.get(conf);
        // Write test data to file
        ClusteringTestUtils.writePointsToFile(sampleData, input, fs, conf);
        // Select initial centroids
        RandomSeedGenerator.buildRandom(conf, input, initialPoints, 8, measure, 1L);
        // Run k-means
        Path kmeansOutput = new Path(output, "kmeans");
        KMeansDriver.run(conf, getTestTempDirPath("testdata"), initialPoints, kmeansOutput, 0.001, 10, true, 0.0, false);
        // Print out clusters
        ClusterDumper clusterDumper = new ClusterDumper(TestClusterDumper.finalClusterPath(conf, output, 10), new Path(kmeansOutput, "clusteredPoints"));
        clusterDumper.setOutputFormat(JSON);
        clusterDumper.printClusters(termDictionary);
    }

    @Test
    public void testFuzzyKmeans() throws Exception {
        DistanceMeasure measure = new EuclideanDistanceMeasure();
        Path input = getTestTempFilePath("input");
        Path output = getTestTempDirPath("output");
        Path initialPoints = new Path(output, (((CLUSTERS_DIR) + '0') + (FINAL_ITERATION_SUFFIX)));
        Configuration conf = getConfiguration();
        FileSystem fs = FileSystem.get(conf);
        // Write test data to file
        ClusteringTestUtils.writePointsToFile(sampleData, input, fs, conf);
        // Select initial centroids
        RandomSeedGenerator.buildRandom(conf, input, initialPoints, 8, measure, 1L);
        // Run k-means
        Path kMeansOutput = new Path(output, "kmeans");
        FuzzyKMeansDriver.run(conf, getTestTempDirPath("testdata"), initialPoints, kMeansOutput, 0.001, 10, 1.1F, true, true, 0, true);
        // run ClusterDumper
        ClusterDumper clusterDumper = new ClusterDumper(TestClusterDumper.finalClusterPath(conf, output, 10), new Path(kMeansOutput, "clusteredPoints"));
        clusterDumper.printClusters(termDictionary);
    }
}

