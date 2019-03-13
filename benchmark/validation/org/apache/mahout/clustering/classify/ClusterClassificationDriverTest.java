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
package org.apache.mahout.clustering.classify;


import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.clustering.ClusteringTestUtils;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.MahoutTestCase;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.junit.Test;


public class ClusterClassificationDriverTest extends MahoutTestCase {
    private static final double[][] REFERENCE = new double[][]{ new double[]{ 1, 1 }, new double[]{ 2, 1 }, new double[]{ 1, 2 }, new double[]{ 4, 4 }, new double[]{ 5, 4 }, new double[]{ 4, 5 }, new double[]{ 5, 5 }, new double[]{ 9, 9 }, new double[]{ 8, 8 } };

    private FileSystem fs;

    private Path clusteringOutputPath;

    private Configuration conf;

    private Path pointsPath;

    private Path classifiedOutputPath;

    private List<Vector> firstCluster;

    private List<Vector> secondCluster;

    private List<Vector> thirdCluster;

    @Test
    public void testVectorClassificationWithOutlierRemovalMR() throws Exception {
        List<VectorWritable> points = ClusterClassificationDriverTest.getPointsWritable(ClusterClassificationDriverTest.REFERENCE);
        pointsPath = getTestTempDirPath("points");
        clusteringOutputPath = getTestTempDirPath("output");
        classifiedOutputPath = getTestTempDirPath("classifiedClusters");
        HadoopUtil.delete(conf, classifiedOutputPath);
        conf = getConfiguration();
        ClusteringTestUtils.writePointsToFile(points, true, new Path(pointsPath, "file1"), fs, conf);
        runClustering(pointsPath, conf, false);
        runClassificationWithOutlierRemoval(false);
        collectVectorsForAssertion();
        assertVectorsWithOutlierRemoval();
    }

    @Test
    public void testVectorClassificationWithoutOutlierRemoval() throws Exception {
        List<VectorWritable> points = ClusterClassificationDriverTest.getPointsWritable(ClusterClassificationDriverTest.REFERENCE);
        pointsPath = getTestTempDirPath("points");
        clusteringOutputPath = getTestTempDirPath("output");
        classifiedOutputPath = getTestTempDirPath("classify");
        conf = getConfiguration();
        ClusteringTestUtils.writePointsToFile(points, new Path(pointsPath, "file1"), fs, conf);
        runClustering(pointsPath, conf, true);
        runClassificationWithoutOutlierRemoval();
        collectVectorsForAssertion();
        assertVectorsWithoutOutlierRemoval();
    }

    @Test
    public void testVectorClassificationWithOutlierRemoval() throws Exception {
        List<VectorWritable> points = ClusterClassificationDriverTest.getPointsWritable(ClusterClassificationDriverTest.REFERENCE);
        pointsPath = getTestTempDirPath("points");
        clusteringOutputPath = getTestTempDirPath("output");
        classifiedOutputPath = getTestTempDirPath("classify");
        conf = getConfiguration();
        ClusteringTestUtils.writePointsToFile(points, new Path(pointsPath, "file1"), fs, conf);
        runClustering(pointsPath, conf, true);
        runClassificationWithOutlierRemoval(true);
        collectVectorsForAssertion();
        assertVectorsWithOutlierRemoval();
    }
}

