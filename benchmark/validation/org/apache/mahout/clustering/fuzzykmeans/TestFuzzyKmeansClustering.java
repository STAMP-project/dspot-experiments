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
package org.apache.mahout.clustering.fuzzykmeans;


import DefaultOptionCreator.CLUSTERING_OPTION;
import DefaultOptionCreator.CLUSTERS_IN_OPTION;
import DefaultOptionCreator.CONVERGENCE_DELTA_OPTION;
import DefaultOptionCreator.DISTANCE_MEASURE_OPTION;
import DefaultOptionCreator.EMIT_MOST_LIKELY_OPTION;
import DefaultOptionCreator.INPUT_OPTION;
import DefaultOptionCreator.MAX_ITERATIONS_OPTION;
import DefaultOptionCreator.METHOD_OPTION;
import DefaultOptionCreator.OUTPUT_OPTION;
import DefaultOptionCreator.OVERWRITE_OPTION;
import FuzzyKMeansDriver.M_OPTION;
import SequenceFile.Writer;
import com.google.common.io.Closeables;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.clustering.ClusteringTestUtils;
import org.apache.mahout.clustering.kmeans.TestKmeansClustering;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.MahoutTestCase;
import org.apache.mahout.common.commandline.DefaultOptionCreator;
import org.apache.mahout.common.distance.DistanceMeasure;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.junit.Test;


public final class TestFuzzyKmeansClustering extends MahoutTestCase {
    private FileSystem fs;

    private final DistanceMeasure measure = new EuclideanDistanceMeasure();

    @Test
    public void testFuzzyKMeansSeqJob() throws Exception {
        List<VectorWritable> points = TestKmeansClustering.getPointsWritable(TestKmeansClustering.REFERENCE);
        Path pointsPath = getTestTempDirPath("points");
        Path clustersPath = getTestTempDirPath("clusters");
        Configuration conf = getConfiguration();
        ClusteringTestUtils.writePointsToFile(points, new Path(pointsPath, "file1"), fs, conf);
        for (int k = 0; k < (points.size()); k++) {
            System.out.println(("testKFuzzyKMeansMRJob k= " + k));
            // pick k initial cluster centers at random
            SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, new Path(clustersPath, "part-00000"), Text.class, SoftCluster.class);
            try {
                for (int i = 0; i < (k + 1); i++) {
                    Vector vec = TestFuzzyKmeansClustering.tweakValue(points.get(i).get());
                    SoftCluster cluster = new SoftCluster(vec, i, measure);
                    /* add the center so the centroid will be correct upon output */
                    cluster.observe(cluster.getCenter(), 1);
                    // writer.write(cluster.getIdentifier() + '\t' + SoftCluster.formatCluster(cluster) + '\n');
                    writer.append(new Text(cluster.getIdentifier()), cluster);
                }
            } finally {
                Closeables.close(writer, false);
            }
            // now run the Job using the run() command line options.
            Path output = getTestTempDirPath(("output" + k));
            /* FuzzyKMeansDriver.runJob(pointsPath,
            clustersPath,
            output,
            EuclideanDistanceMeasure.class.getName(),
            0.001,
            2,
            k + 1,
            2,
            false,
            true,
            0);
             */
            String[] args = new String[]{ MahoutTestCase.optKey(INPUT_OPTION), pointsPath.toString(), MahoutTestCase.optKey(CLUSTERS_IN_OPTION), clustersPath.toString(), MahoutTestCase.optKey(OUTPUT_OPTION), output.toString(), MahoutTestCase.optKey(DISTANCE_MEASURE_OPTION), EuclideanDistanceMeasure.class.getName(), MahoutTestCase.optKey(CONVERGENCE_DELTA_OPTION), "0.001", MahoutTestCase.optKey(MAX_ITERATIONS_OPTION), "2", MahoutTestCase.optKey(M_OPTION), "2.0", MahoutTestCase.optKey(CLUSTERING_OPTION), MahoutTestCase.optKey(EMIT_MOST_LIKELY_OPTION), MahoutTestCase.optKey(OVERWRITE_OPTION), MahoutTestCase.optKey(METHOD_OPTION), DefaultOptionCreator.SEQUENTIAL_METHOD };
            FuzzyKMeansDriver.main(args);
            long count = HadoopUtil.countRecords(new Path(output, "clusteredPoints/part-m-0"), conf);
            assertTrue((count > 0));
        }
    }

    @Test
    public void testFuzzyKMeansMRJob() throws Exception {
        List<VectorWritable> points = TestKmeansClustering.getPointsWritable(TestKmeansClustering.REFERENCE);
        Path pointsPath = getTestTempDirPath("points");
        Path clustersPath = getTestTempDirPath("clusters");
        Configuration conf = getConfiguration();
        ClusteringTestUtils.writePointsToFile(points, new Path(pointsPath, "file1"), fs, conf);
        for (int k = 0; k < (points.size()); k++) {
            System.out.println(("testKFuzzyKMeansMRJob k= " + k));
            // pick k initial cluster centers at random
            SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, new Path(clustersPath, "part-00000"), Text.class, SoftCluster.class);
            try {
                for (int i = 0; i < (k + 1); i++) {
                    Vector vec = TestFuzzyKmeansClustering.tweakValue(points.get(i).get());
                    SoftCluster cluster = new SoftCluster(vec, i, measure);
                    /* add the center so the centroid will be correct upon output */
                    cluster.observe(cluster.getCenter(), 1);
                    // writer.write(cluster.getIdentifier() + '\t' + SoftCluster.formatCluster(cluster) + '\n');
                    writer.append(new Text(cluster.getIdentifier()), cluster);
                }
            } finally {
                Closeables.close(writer, false);
            }
            // now run the Job using the run() command line options.
            Path output = getTestTempDirPath(("output" + k));
            /* FuzzyKMeansDriver.runJob(pointsPath,
            clustersPath,
            output,
            EuclideanDistanceMeasure.class.getName(),
            0.001,
            2,
            k + 1,
            2,
            false,
            true,
            0);
             */
            String[] args = new String[]{ MahoutTestCase.optKey(INPUT_OPTION), pointsPath.toString(), MahoutTestCase.optKey(CLUSTERS_IN_OPTION), clustersPath.toString(), MahoutTestCase.optKey(OUTPUT_OPTION), output.toString(), MahoutTestCase.optKey(DISTANCE_MEASURE_OPTION), EuclideanDistanceMeasure.class.getName(), MahoutTestCase.optKey(CONVERGENCE_DELTA_OPTION), "0.001", MahoutTestCase.optKey(MAX_ITERATIONS_OPTION), "2", MahoutTestCase.optKey(M_OPTION), "2.0", MahoutTestCase.optKey(CLUSTERING_OPTION), MahoutTestCase.optKey(EMIT_MOST_LIKELY_OPTION), MahoutTestCase.optKey(OVERWRITE_OPTION) };
            ToolRunner.run(getConfiguration(), new FuzzyKMeansDriver(), args);
            long count = HadoopUtil.countRecords(new Path(output, "clusteredPoints/part-m-00000"), conf);
            assertTrue((count > 0));
        }
    }
}

