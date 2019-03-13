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
package org.apache.mahout.clustering.topdown.postprocessor;


import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.clustering.ClusteringTestUtils;
import org.apache.mahout.common.MahoutTestCase;
import org.apache.mahout.math.VectorWritable;
import org.junit.Assert;
import org.junit.Test;


public final class ClusterCountReaderTest extends MahoutTestCase {
    public static final double[][] REFERENCE = new double[][]{ new double[]{ 1, 1 }, new double[]{ 2, 1 }, new double[]{ 1, 2 }, new double[]{ 4, 4 }, new double[]{ 5, 4 }, new double[]{ 4, 5 }, new double[]{ 5, 5 } };

    private FileSystem fs;

    private Path outputPathForCanopy;

    private Path outputPathForKMeans;

    /**
     * Story: User wants to use cluster post processor after canopy clustering and then run clustering on the
     * output clusters
     */
    @Test
    public void testGetNumberOfClusters() throws Exception {
        List<VectorWritable> points = ClusterCountReaderTest.getPointsWritable(ClusterCountReaderTest.REFERENCE);
        Path pointsPath = getTestTempDirPath("points");
        Configuration conf = getConfiguration();
        ClusteringTestUtils.writePointsToFile(points, new Path(pointsPath, "file1"), fs, conf);
        ClusteringTestUtils.writePointsToFile(points, new Path(pointsPath, "file2"), fs, conf);
        outputPathForCanopy = getTestTempDirPath("canopy");
        outputPathForKMeans = getTestTempDirPath("kmeans");
        topLevelClustering(pointsPath, conf);
        int numberOfClusters = ClusterCountReader.getNumberOfClusters(outputPathForKMeans, conf);
        Assert.assertEquals(2, numberOfClusters);
        ClusterCountReaderTest.verifyThatNumberOfClustersIsCorrect(conf, new Path(outputPathForKMeans, new Path("clusteredPoints")));
    }
}

