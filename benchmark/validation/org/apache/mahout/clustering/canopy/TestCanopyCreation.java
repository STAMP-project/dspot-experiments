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
package org.apache.mahout.clustering.canopy;


import CanopyConfigKeys.CF_KEY;
import CanopyConfigKeys.DISTANCE_MEASURE_KEY;
import CanopyConfigKeys.T1_KEY;
import CanopyConfigKeys.T2_KEY;
import CanopyConfigKeys.T3_KEY;
import CanopyConfigKeys.T4_KEY;
import DefaultOptionCreator.CLUSTERING_OPTION;
import DefaultOptionCreator.DISTANCE_MEASURE_OPTION;
import DefaultOptionCreator.INPUT_OPTION;
import DefaultOptionCreator.METHOD_OPTION;
import DefaultOptionCreator.OUTLIER_THRESHOLD;
import DefaultOptionCreator.OUTPUT_OPTION;
import DefaultOptionCreator.OVERWRITE_OPTION;
import DefaultOptionCreator.T1_OPTION;
import DefaultOptionCreator.T2_OPTION;
import Mapper.Context;
import SequenceFile.Reader;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.Closeables;
import java.util.List;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.clustering.ClusteringTestUtils;
import org.apache.mahout.clustering.iterator.ClusterWritable;
import org.apache.mahout.common.DummyRecordWriter;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.MahoutTestCase;
import org.apache.mahout.common.Pair;
import org.apache.mahout.common.commandline.DefaultOptionCreator;
import org.apache.mahout.common.distance.DistanceMeasure;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.common.distance.ManhattanDistanceMeasure;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.junit.Test;


@Deprecated
public final class TestCanopyCreation extends MahoutTestCase {
    private static final double[][] RAW = new double[][]{ new double[]{ 1, 1 }, new double[]{ 2, 1 }, new double[]{ 1, 2 }, new double[]{ 2, 2 }, new double[]{ 3, 3 }, new double[]{ 4, 4 }, new double[]{ 5, 4 }, new double[]{ 4, 5 }, new double[]{ 5, 5 } };

    private List<Canopy> referenceManhattan;

    private final DistanceMeasure manhattanDistanceMeasure = new ManhattanDistanceMeasure();

    private List<Vector> manhattanCentroids;

    private List<Canopy> referenceEuclidean;

    private final DistanceMeasure euclideanDistanceMeasure = new EuclideanDistanceMeasure();

    private List<Vector> euclideanCentroids;

    private FileSystem fs;

    /**
     * Story: User can cluster points using a ManhattanDistanceMeasure and a
     * reference implementation
     */
    @Test
    public void testReferenceManhattan() throws Exception {
        // see setUp for cluster creation
        TestCanopyCreation.printCanopies(referenceManhattan);
        assertEquals("number of canopies", 3, referenceManhattan.size());
        for (int canopyIx = 0; canopyIx < (referenceManhattan.size()); canopyIx++) {
            Canopy testCanopy = referenceManhattan.get(canopyIx);
            int[] expectedNumPoints = new int[]{ 4, 4, 3 };
            double[][] expectedCentroids = new double[][]{ new double[]{ 1.5, 1.5 }, new double[]{ 4.0, 4.0 }, new double[]{ 4.666666666666667, 4.666666666666667 } };
            assertEquals(("canopy points " + canopyIx), testCanopy.getNumObservations(), expectedNumPoints[canopyIx]);
            double[] refCentroid = expectedCentroids[canopyIx];
            Vector testCentroid = testCanopy.computeCentroid();
            for (int pointIx = 0; pointIx < (refCentroid.length); pointIx++) {
                assertEquals((((("canopy centroid " + canopyIx) + '[') + pointIx) + ']'), refCentroid[pointIx], testCentroid.get(pointIx), MahoutTestCase.EPSILON);
            }
        }
    }

    /**
     * Story: User can cluster points using a EuclideanDistanceMeasure and a
     * reference implementation
     */
    @Test
    public void testReferenceEuclidean() throws Exception {
        // see setUp for cluster creation
        TestCanopyCreation.printCanopies(referenceEuclidean);
        assertEquals("number of canopies", 3, referenceEuclidean.size());
        int[] expectedNumPoints = new int[]{ 5, 5, 3 };
        double[][] expectedCentroids = new double[][]{ new double[]{ 1.8, 1.8 }, new double[]{ 4.2, 4.2 }, new double[]{ 4.666666666666667, 4.666666666666667 } };
        for (int canopyIx = 0; canopyIx < (referenceEuclidean.size()); canopyIx++) {
            Canopy testCanopy = referenceEuclidean.get(canopyIx);
            assertEquals(("canopy points " + canopyIx), testCanopy.getNumObservations(), expectedNumPoints[canopyIx]);
            double[] refCentroid = expectedCentroids[canopyIx];
            Vector testCentroid = testCanopy.computeCentroid();
            for (int pointIx = 0; pointIx < (refCentroid.length); pointIx++) {
                assertEquals((((("canopy centroid " + canopyIx) + '[') + pointIx) + ']'), refCentroid[pointIx], testCentroid.get(pointIx), MahoutTestCase.EPSILON);
            }
        }
    }

    /**
     * Story: User can produce initial canopy centers using a
     * ManhattanDistanceMeasure and a CanopyMapper which clusters input points to
     * produce an output set of canopy centroid points.
     */
    @Test
    public void testCanopyMapperManhattan() throws Exception {
        CanopyMapper mapper = new CanopyMapper();
        Configuration conf = getConfiguration();
        conf.set(DISTANCE_MEASURE_KEY, manhattanDistanceMeasure.getClass().getName());
        conf.set(T1_KEY, String.valueOf(3.1));
        conf.set(T2_KEY, String.valueOf(2.1));
        conf.set(CF_KEY, "0");
        DummyRecordWriter<Text, VectorWritable> writer = new DummyRecordWriter();
        Context context = DummyRecordWriter.build(mapper, conf, writer);
        mapper.setup(context);
        List<VectorWritable> points = TestCanopyCreation.getPointsWritable();
        // map the data
        for (VectorWritable point : points) {
            mapper.map(new Text(), point, context);
        }
        mapper.cleanup(context);
        assertEquals("Number of map results", 1, writer.getData().size());
        // now verify the output
        List<VectorWritable> data = writer.getValue(new Text("centroid"));
        assertEquals("Number of centroids", 3, data.size());
        for (int i = 0; i < (data.size()); i++) {
            assertEquals("Centroid error", manhattanCentroids.get(i).asFormatString(), data.get(i).get().asFormatString());
        }
    }

    /**
     * Story: User can produce initial canopy centers using a
     * EuclideanDistanceMeasure and a CanopyMapper/Combiner which clusters input
     * points to produce an output set of canopy centroid points.
     */
    @Test
    public void testCanopyMapperEuclidean() throws Exception {
        CanopyMapper mapper = new CanopyMapper();
        Configuration conf = getConfiguration();
        conf.set(DISTANCE_MEASURE_KEY, euclideanDistanceMeasure.getClass().getName());
        conf.set(T1_KEY, String.valueOf(3.1));
        conf.set(T2_KEY, String.valueOf(2.1));
        conf.set(CF_KEY, "0");
        DummyRecordWriter<Text, VectorWritable> writer = new DummyRecordWriter();
        Context context = DummyRecordWriter.build(mapper, conf, writer);
        mapper.setup(context);
        List<VectorWritable> points = TestCanopyCreation.getPointsWritable();
        // map the data
        for (VectorWritable point : points) {
            mapper.map(new Text(), point, context);
        }
        mapper.cleanup(context);
        assertEquals("Number of map results", 1, writer.getData().size());
        // now verify the output
        List<VectorWritable> data = writer.getValue(new Text("centroid"));
        assertEquals("Number of centroids", 3, data.size());
        for (int i = 0; i < (data.size()); i++) {
            assertEquals("Centroid error", euclideanCentroids.get(i).asFormatString(), data.get(i).get().asFormatString());
        }
    }

    /**
     * Story: User can produce final canopy centers using a
     * ManhattanDistanceMeasure and a CanopyReducer which clusters input centroid
     * points to produce an output set of final canopy centroid points.
     */
    @Test
    public void testCanopyReducerManhattan() throws Exception {
        CanopyReducer reducer = new CanopyReducer();
        Configuration conf = getConfiguration();
        conf.set(DISTANCE_MEASURE_KEY, "org.apache.mahout.common.distance.ManhattanDistanceMeasure");
        conf.set(T1_KEY, String.valueOf(3.1));
        conf.set(T2_KEY, String.valueOf(2.1));
        conf.set(CF_KEY, "0");
        DummyRecordWriter<Text, ClusterWritable> writer = new DummyRecordWriter();
        Reducer.Context context = DummyRecordWriter.build(reducer, conf, writer, Text.class, VectorWritable.class);
        reducer.setup(context);
        List<VectorWritable> points = TestCanopyCreation.getPointsWritable();
        reducer.reduce(new Text("centroid"), points, context);
        Iterable<Text> keys = writer.getKeysInInsertionOrder();
        assertEquals("Number of centroids", 3, Iterables.size(keys));
        int i = 0;
        for (Text key : keys) {
            List<ClusterWritable> data = writer.getValue(key);
            ClusterWritable clusterWritable = data.get(0);
            Canopy canopy = ((Canopy) (clusterWritable.getValue()));
            assertEquals((((manhattanCentroids.get(i).asFormatString()) + " is not equal to ") + (canopy.computeCentroid().asFormatString())), manhattanCentroids.get(i), canopy.computeCentroid());
            i++;
        }
    }

    /**
     * Story: User can produce final canopy centers using a
     * EuclideanDistanceMeasure and a CanopyReducer which clusters input centroid
     * points to produce an output set of final canopy centroid points.
     */
    @Test
    public void testCanopyReducerEuclidean() throws Exception {
        CanopyReducer reducer = new CanopyReducer();
        Configuration conf = getConfiguration();
        conf.set(DISTANCE_MEASURE_KEY, "org.apache.mahout.common.distance.EuclideanDistanceMeasure");
        conf.set(T1_KEY, String.valueOf(3.1));
        conf.set(T2_KEY, String.valueOf(2.1));
        conf.set(CF_KEY, "0");
        DummyRecordWriter<Text, ClusterWritable> writer = new DummyRecordWriter();
        Reducer.Context context = DummyRecordWriter.build(reducer, conf, writer, Text.class, VectorWritable.class);
        reducer.setup(context);
        List<VectorWritable> points = TestCanopyCreation.getPointsWritable();
        reducer.reduce(new Text("centroid"), points, context);
        Iterable<Text> keys = writer.getKeysInInsertionOrder();
        assertEquals("Number of centroids", 3, Iterables.size(keys));
        int i = 0;
        for (Text key : keys) {
            List<ClusterWritable> data = writer.getValue(key);
            ClusterWritable clusterWritable = data.get(0);
            Canopy canopy = ((Canopy) (clusterWritable.getValue()));
            assertEquals((((euclideanCentroids.get(i).asFormatString()) + " is not equal to ") + (canopy.computeCentroid().asFormatString())), euclideanCentroids.get(i), canopy.computeCentroid());
            i++;
        }
    }

    /**
     * Story: User can produce final canopy centers using a Hadoop map/reduce job
     * and a ManhattanDistanceMeasure.
     */
    @Test
    public void testCanopyGenManhattanMR() throws Exception {
        List<VectorWritable> points = TestCanopyCreation.getPointsWritable();
        Configuration config = getConfiguration();
        ClusteringTestUtils.writePointsToFile(points, getTestTempFilePath("testdata/file1"), fs, config);
        ClusteringTestUtils.writePointsToFile(points, getTestTempFilePath("testdata/file2"), fs, config);
        // now run the Canopy Driver
        Path output = getTestTempDirPath("output");
        CanopyDriver.run(config, getTestTempDirPath("testdata"), output, manhattanDistanceMeasure, 3.1, 2.1, false, 0.0, false);
        // verify output from sequence file
        Path path = new Path(output, "clusters-0-final/part-r-00000");
        FileSystem fs = FileSystem.get(path.toUri(), config);
        SequenceFile.Reader reader = new SequenceFile.Reader(fs, path, config);
        try {
            Writable key = new Text();
            ClusterWritable clusterWritable = new ClusterWritable();
            assertTrue("more to come", reader.next(key, clusterWritable));
            assertEquals("1st key", "C-0", key.toString());
            List<Pair<Double, Double>> refCenters = Lists.newArrayList();
            refCenters.add(new Pair(1.5, 1.5));
            refCenters.add(new Pair(4.333333333333334, 4.333333333333334));
            Pair<Double, Double> c = new Pair(clusterWritable.getValue().getCenter().get(0), clusterWritable.getValue().getCenter().get(1));
            assertTrue((("center " + c) + " not found"), TestCanopyCreation.findAndRemove(c, refCenters, MahoutTestCase.EPSILON));
            assertTrue("more to come", reader.next(key, clusterWritable));
            assertEquals("2nd key", "C-1", key.toString());
            c = new Pair(clusterWritable.getValue().getCenter().get(0), clusterWritable.getValue().getCenter().get(1));
            assertTrue((("center " + c) + " not found"), TestCanopyCreation.findAndRemove(c, refCenters, MahoutTestCase.EPSILON));
            assertFalse("more to come", reader.next(key, clusterWritable));
        } finally {
            Closeables.close(reader, true);
        }
    }

    /**
     * Story: User can produce final canopy centers using a Hadoop map/reduce job
     * and a EuclideanDistanceMeasure.
     */
    @Test
    public void testCanopyGenEuclideanMR() throws Exception {
        List<VectorWritable> points = TestCanopyCreation.getPointsWritable();
        Configuration config = getConfiguration();
        ClusteringTestUtils.writePointsToFile(points, getTestTempFilePath("testdata/file1"), fs, config);
        ClusteringTestUtils.writePointsToFile(points, getTestTempFilePath("testdata/file2"), fs, config);
        // now run the Canopy Driver
        Path output = getTestTempDirPath("output");
        CanopyDriver.run(config, getTestTempDirPath("testdata"), output, euclideanDistanceMeasure, 3.1, 2.1, false, 0.0, false);
        // verify output from sequence file
        Path path = new Path(output, "clusters-0-final/part-r-00000");
        FileSystem fs = FileSystem.get(path.toUri(), config);
        SequenceFile.Reader reader = new SequenceFile.Reader(fs, path, config);
        try {
            Writable key = new Text();
            ClusterWritable clusterWritable = new ClusterWritable();
            assertTrue("more to come", reader.next(key, clusterWritable));
            assertEquals("1st key", "C-0", key.toString());
            List<Pair<Double, Double>> refCenters = Lists.newArrayList();
            refCenters.add(new Pair(1.8, 1.8));
            refCenters.add(new Pair(4.433333333333334, 4.433333333333334));
            Pair<Double, Double> c = new Pair(clusterWritable.getValue().getCenter().get(0), clusterWritable.getValue().getCenter().get(1));
            assertTrue((("center " + c) + " not found"), TestCanopyCreation.findAndRemove(c, refCenters, MahoutTestCase.EPSILON));
            assertTrue("more to come", reader.next(key, clusterWritable));
            assertEquals("2nd key", "C-1", key.toString());
            c = new Pair(clusterWritable.getValue().getCenter().get(0), clusterWritable.getValue().getCenter().get(1));
            assertTrue((("center " + c) + " not found"), TestCanopyCreation.findAndRemove(c, refCenters, MahoutTestCase.EPSILON));
            assertFalse("more to come", reader.next(key, clusterWritable));
        } finally {
            Closeables.close(reader, true);
        }
    }

    /**
     * Story: User can cluster points using sequential execution
     */
    @Test
    public void testClusteringManhattanSeq() throws Exception {
        List<VectorWritable> points = TestCanopyCreation.getPointsWritable();
        Configuration config = getConfiguration();
        ClusteringTestUtils.writePointsToFile(points, getTestTempFilePath("testdata/file1"), fs, config);
        // now run the Canopy Driver in sequential mode
        Path output = getTestTempDirPath("output");
        CanopyDriver.run(config, getTestTempDirPath("testdata"), output, manhattanDistanceMeasure, 3.1, 2.1, true, 0.0, true);
        // verify output from sequence file
        Path path = new Path(output, "clusters-0-final/part-r-00000");
        int ix = 0;
        for (ClusterWritable clusterWritable : new org.apache.mahout.common.iterator.sequencefile.SequenceFileValueIterable<ClusterWritable>(path, true, config)) {
            assertEquals((("Center [" + ix) + ']'), manhattanCentroids.get(ix), clusterWritable.getValue().getCenter());
            ix++;
        }
        path = new Path(output, "clusteredPoints/part-m-0");
        long count = HadoopUtil.countRecords(path, config);
        assertEquals("number of points", points.size(), count);
    }

    /**
     * Story: User can cluster points using sequential execution
     */
    @Test
    public void testClusteringEuclideanSeq() throws Exception {
        List<VectorWritable> points = TestCanopyCreation.getPointsWritable();
        Configuration config = getConfiguration();
        ClusteringTestUtils.writePointsToFile(points, getTestTempFilePath("testdata/file1"), fs, config);
        // now run the Canopy Driver in sequential mode
        Path output = getTestTempDirPath("output");
        String[] args = new String[]{ MahoutTestCase.optKey(INPUT_OPTION), getTestTempDirPath("testdata").toString(), MahoutTestCase.optKey(OUTPUT_OPTION), output.toString(), MahoutTestCase.optKey(DISTANCE_MEASURE_OPTION), EuclideanDistanceMeasure.class.getName(), MahoutTestCase.optKey(T1_OPTION), "3.1", MahoutTestCase.optKey(T2_OPTION), "2.1", MahoutTestCase.optKey(CLUSTERING_OPTION), MahoutTestCase.optKey(OVERWRITE_OPTION), MahoutTestCase.optKey(METHOD_OPTION), DefaultOptionCreator.SEQUENTIAL_METHOD };
        ToolRunner.run(config, new CanopyDriver(), args);
        // verify output from sequence file
        Path path = new Path(output, "clusters-0-final/part-r-00000");
        int ix = 0;
        for (ClusterWritable clusterWritable : new org.apache.mahout.common.iterator.sequencefile.SequenceFileValueIterable<ClusterWritable>(path, true, config)) {
            assertEquals((("Center [" + ix) + ']'), euclideanCentroids.get(ix), clusterWritable.getValue().getCenter());
            ix++;
        }
        path = new Path(output, "clusteredPoints/part-m-0");
        long count = HadoopUtil.countRecords(path, config);
        assertEquals("number of points", points.size(), count);
    }

    /**
     * Story: User can remove outliers while clustering points using sequential execution
     */
    @Test
    public void testClusteringEuclideanWithOutlierRemovalSeq() throws Exception {
        List<VectorWritable> points = TestCanopyCreation.getPointsWritable();
        Configuration config = getConfiguration();
        ClusteringTestUtils.writePointsToFile(points, getTestTempFilePath("testdata/file1"), fs, config);
        // now run the Canopy Driver in sequential mode
        Path output = getTestTempDirPath("output");
        String[] args = new String[]{ MahoutTestCase.optKey(INPUT_OPTION), getTestTempDirPath("testdata").toString(), MahoutTestCase.optKey(OUTPUT_OPTION), output.toString(), MahoutTestCase.optKey(DISTANCE_MEASURE_OPTION), EuclideanDistanceMeasure.class.getName(), MahoutTestCase.optKey(T1_OPTION), "3.1", MahoutTestCase.optKey(T2_OPTION), "2.1", MahoutTestCase.optKey(OUTLIER_THRESHOLD), "0.5", MahoutTestCase.optKey(CLUSTERING_OPTION), MahoutTestCase.optKey(OVERWRITE_OPTION), MahoutTestCase.optKey(METHOD_OPTION), DefaultOptionCreator.SEQUENTIAL_METHOD };
        ToolRunner.run(config, new CanopyDriver(), args);
        // verify output from sequence file
        Path path = new Path(output, "clusters-0-final/part-r-00000");
        int ix = 0;
        for (ClusterWritable clusterWritable : new org.apache.mahout.common.iterator.sequencefile.SequenceFileValueIterable<ClusterWritable>(path, true, config)) {
            assertEquals((("Center [" + ix) + ']'), euclideanCentroids.get(ix), clusterWritable.getValue().getCenter());
            ix++;
        }
        path = new Path(output, "clusteredPoints/part-m-0");
        long count = HadoopUtil.countRecords(path, config);
        int expectedPointsHavingPDFGreaterThanThreshold = 6;
        assertEquals("number of points", expectedPointsHavingPDFGreaterThanThreshold, count);
    }

    /**
     * Story: User can produce final point clustering using a Hadoop map/reduce
     * job and a ManhattanDistanceMeasure.
     */
    @Test
    public void testClusteringManhattanMR() throws Exception {
        List<VectorWritable> points = TestCanopyCreation.getPointsWritable();
        Configuration conf = getConfiguration();
        ClusteringTestUtils.writePointsToFile(points, true, getTestTempFilePath("testdata/file1"), fs, conf);
        ClusteringTestUtils.writePointsToFile(points, true, getTestTempFilePath("testdata/file2"), fs, conf);
        // now run the Job
        Path output = getTestTempDirPath("output");
        CanopyDriver.run(conf, getTestTempDirPath("testdata"), output, manhattanDistanceMeasure, 3.1, 2.1, true, 0.0, false);
        Path path = new Path(output, "clusteredPoints/part-m-00000");
        long count = HadoopUtil.countRecords(path, conf);
        assertEquals("number of points", points.size(), count);
    }

    /**
     * Story: User can produce final point clustering using a Hadoop map/reduce
     * job and a EuclideanDistanceMeasure.
     */
    @Test
    public void testClusteringEuclideanMR() throws Exception {
        List<VectorWritable> points = TestCanopyCreation.getPointsWritable();
        Configuration conf = getConfiguration();
        ClusteringTestUtils.writePointsToFile(points, true, getTestTempFilePath("testdata/file1"), fs, conf);
        ClusteringTestUtils.writePointsToFile(points, true, getTestTempFilePath("testdata/file2"), fs, conf);
        // now run the Job using the run() command. Others can use runJob().
        Path output = getTestTempDirPath("output");
        String[] args = new String[]{ MahoutTestCase.optKey(INPUT_OPTION), getTestTempDirPath("testdata").toString(), MahoutTestCase.optKey(OUTPUT_OPTION), output.toString(), MahoutTestCase.optKey(DISTANCE_MEASURE_OPTION), EuclideanDistanceMeasure.class.getName(), MahoutTestCase.optKey(T1_OPTION), "3.1", MahoutTestCase.optKey(T2_OPTION), "2.1", MahoutTestCase.optKey(CLUSTERING_OPTION), MahoutTestCase.optKey(OVERWRITE_OPTION) };
        ToolRunner.run(getConfiguration(), new CanopyDriver(), args);
        Path path = new Path(output, "clusteredPoints/part-m-00000");
        long count = HadoopUtil.countRecords(path, conf);
        assertEquals("number of points", points.size(), count);
    }

    /**
     * Story: User can produce final point clustering using a Hadoop map/reduce
     * job and a EuclideanDistanceMeasure and outlier removal threshold.
     */
    @Test
    public void testClusteringEuclideanWithOutlierRemovalMR() throws Exception {
        List<VectorWritable> points = TestCanopyCreation.getPointsWritable();
        Configuration conf = getConfiguration();
        ClusteringTestUtils.writePointsToFile(points, true, getTestTempFilePath("testdata/file1"), fs, conf);
        ClusteringTestUtils.writePointsToFile(points, true, getTestTempFilePath("testdata/file2"), fs, conf);
        // now run the Job using the run() command. Others can use runJob().
        Path output = getTestTempDirPath("output");
        String[] args = new String[]{ MahoutTestCase.optKey(INPUT_OPTION), getTestTempDirPath("testdata").toString(), MahoutTestCase.optKey(OUTPUT_OPTION), output.toString(), MahoutTestCase.optKey(DISTANCE_MEASURE_OPTION), EuclideanDistanceMeasure.class.getName(), MahoutTestCase.optKey(T1_OPTION), "3.1", MahoutTestCase.optKey(T2_OPTION), "2.1", MahoutTestCase.optKey(OUTLIER_THRESHOLD), "0.7", MahoutTestCase.optKey(CLUSTERING_OPTION), MahoutTestCase.optKey(OVERWRITE_OPTION) };
        ToolRunner.run(getConfiguration(), new CanopyDriver(), args);
        Path path = new Path(output, "clusteredPoints/part-m-00000");
        long count = HadoopUtil.countRecords(path, conf);
        int expectedPointsAfterOutlierRemoval = 8;
        assertEquals("number of points", expectedPointsAfterOutlierRemoval, count);
    }

    /**
     * Story: User can set T3 and T4 values to be used by the reducer for its T1
     * and T2 thresholds
     */
    @Test
    public void testCanopyReducerT3T4Configuration() throws Exception {
        CanopyReducer reducer = new CanopyReducer();
        Configuration conf = getConfiguration();
        conf.set(DISTANCE_MEASURE_KEY, "org.apache.mahout.common.distance.ManhattanDistanceMeasure");
        conf.set(T1_KEY, String.valueOf(3.1));
        conf.set(T2_KEY, String.valueOf(2.1));
        conf.set(T3_KEY, String.valueOf(1.1));
        conf.set(T4_KEY, String.valueOf(0.1));
        conf.set(CF_KEY, "0");
        DummyRecordWriter<Text, ClusterWritable> writer = new DummyRecordWriter();
        Reducer.Context context = DummyRecordWriter.build(reducer, conf, writer, Text.class, VectorWritable.class);
        reducer.setup(context);
        assertEquals(1.1, reducer.getCanopyClusterer().getT1(), MahoutTestCase.EPSILON);
        assertEquals(0.1, reducer.getCanopyClusterer().getT2(), MahoutTestCase.EPSILON);
    }

    /**
     * Story: User can specify a clustering limit that prevents output of small
     * clusters
     */
    @Test
    public void testCanopyMapperClusterFilter() throws Exception {
        CanopyMapper mapper = new CanopyMapper();
        Configuration conf = getConfiguration();
        conf.set(DISTANCE_MEASURE_KEY, manhattanDistanceMeasure.getClass().getName());
        conf.set(T1_KEY, String.valueOf(3.1));
        conf.set(T2_KEY, String.valueOf(2.1));
        conf.set(CF_KEY, "3");
        DummyRecordWriter<Text, VectorWritable> writer = new DummyRecordWriter();
        Context context = DummyRecordWriter.build(mapper, conf, writer);
        mapper.setup(context);
        List<VectorWritable> points = TestCanopyCreation.getPointsWritable();
        // map the data
        for (VectorWritable point : points) {
            mapper.map(new Text(), point, context);
        }
        mapper.cleanup(context);
        assertEquals("Number of map results", 1, writer.getData().size());
        // now verify the output
        List<VectorWritable> data = writer.getValue(new Text("centroid"));
        assertEquals("Number of centroids", 2, data.size());
    }

    /**
     * Story: User can specify a cluster filter that limits the minimum size of
     * canopies produced by the reducer
     */
    @Test
    public void testCanopyReducerClusterFilter() throws Exception {
        CanopyReducer reducer = new CanopyReducer();
        Configuration conf = getConfiguration();
        conf.set(DISTANCE_MEASURE_KEY, "org.apache.mahout.common.distance.ManhattanDistanceMeasure");
        conf.set(T1_KEY, String.valueOf(3.1));
        conf.set(T2_KEY, String.valueOf(2.1));
        conf.set(CF_KEY, "3");
        DummyRecordWriter<Text, ClusterWritable> writer = new DummyRecordWriter();
        Reducer.Context context = DummyRecordWriter.build(reducer, conf, writer, Text.class, VectorWritable.class);
        reducer.setup(context);
        List<VectorWritable> points = TestCanopyCreation.getPointsWritable();
        reducer.reduce(new Text("centroid"), points, context);
        Set<Text> keys = writer.getKeys();
        assertEquals("Number of centroids", 2, keys.size());
    }
}

