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
package org.apache.mahout.clustering.streaming.mapreduce;


import DefaultOptionCreator.METHOD_OPTION;
import DefaultOptionCreator.SEQUENTIAL_METHOD;
import StreamingKMeansDriver.SEARCHER_CLASS_OPTION;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.apache.mahout.clustering.ClusteringUtils;
import org.apache.mahout.clustering.streaming.cluster.StreamingKMeans;
import org.apache.mahout.common.MahoutTestCase;
import org.apache.mahout.common.Pair;
import org.apache.mahout.common.distance.SquaredEuclideanDistanceMeasure;
import org.apache.mahout.math.Centroid;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.math.neighborhood.BruteSearch;
import org.apache.mahout.math.random.WeightedThing;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class StreamingKMeansTestMR extends MahoutTestCase {
    private static final int NUM_DATA_POINTS = 1 << 15;

    private static final int NUM_DIMENSIONS = 8;

    private static final int NUM_PROJECTIONS = 3;

    private static final int SEARCH_SIZE = 5;

    private static final int MAX_NUM_ITERATIONS = 10;

    private static final double DISTANCE_CUTOFF = 1.0E-6;

    private static Pair<List<Centroid>, List<Centroid>> syntheticData;

    private final String searcherClassName;

    private final String distanceMeasureClassName;

    public StreamingKMeansTestMR(String searcherClassName, String distanceMeasureClassName) {
        this.searcherClassName = searcherClassName;
        this.distanceMeasureClassName = distanceMeasureClassName;
    }

    @Test
    public void testHypercubeMapper() throws IOException {
        MapDriver<Writable, VectorWritable, IntWritable, CentroidWritable> mapDriver = MapDriver.newMapDriver(new StreamingKMeansMapper());
        configure(mapDriver.getConfiguration());
        System.out.printf("%s mapper test\n", mapDriver.getConfiguration().get(SEARCHER_CLASS_OPTION));
        for (Centroid datapoint : StreamingKMeansTestMR.syntheticData.getFirst()) {
            mapDriver.addInput(new IntWritable(0), new VectorWritable(datapoint));
        }
        List<org.apache.hadoop.mrunit.types.Pair<IntWritable, CentroidWritable>> results = mapDriver.run();
        BruteSearch resultSearcher = new BruteSearch(new SquaredEuclideanDistanceMeasure());
        for (org.apache.hadoop.mrunit.types.Pair<IntWritable, CentroidWritable> result : results) {
            resultSearcher.add(result.getSecond().getCentroid());
        }
        System.out.printf("Clustered the data into %d clusters\n", results.size());
        for (Vector mean : StreamingKMeansTestMR.syntheticData.getSecond()) {
            WeightedThing<Vector> closest = resultSearcher.search(mean, 1).get(0);
            Assert.assertTrue((("Weight " + (closest.getWeight())) + " not less than 0.5"), ((closest.getWeight()) < 0.5));
        }
    }

    @Test
    public void testMapperVsLocal() throws IOException {
        // Clusters the data using the StreamingKMeansMapper.
        MapDriver<Writable, VectorWritable, IntWritable, CentroidWritable> mapDriver = MapDriver.newMapDriver(new StreamingKMeansMapper());
        Configuration configuration = mapDriver.getConfiguration();
        configure(configuration);
        System.out.printf("%s mapper vs local test\n", mapDriver.getConfiguration().get(SEARCHER_CLASS_OPTION));
        for (Centroid datapoint : StreamingKMeansTestMR.syntheticData.getFirst()) {
            mapDriver.addInput(new IntWritable(0), new VectorWritable(datapoint));
        }
        List<Centroid> mapperCentroids = Lists.newArrayList();
        for (org.apache.hadoop.mrunit.types.Pair<IntWritable, CentroidWritable> pair : mapDriver.run()) {
            mapperCentroids.add(pair.getSecond().getCentroid());
        }
        // Clusters the data using local batch StreamingKMeans.
        StreamingKMeans batchClusterer = new StreamingKMeans(StreamingKMeansUtilsMR.searcherFromConfiguration(configuration), mapDriver.getConfiguration().getInt("estimatedNumMapClusters", (-1)), StreamingKMeansTestMR.DISTANCE_CUTOFF);
        batchClusterer.cluster(StreamingKMeansTestMR.syntheticData.getFirst());
        List<Centroid> batchCentroids = Lists.newArrayList();
        for (Vector v : batchClusterer) {
            batchCentroids.add(((Centroid) (v)));
        }
        // Clusters the data using point by point StreamingKMeans.
        StreamingKMeans perPointClusterer = new StreamingKMeans(StreamingKMeansUtilsMR.searcherFromConfiguration(configuration), ((1 << (StreamingKMeansTestMR.NUM_DIMENSIONS)) * ((int) (Math.log(StreamingKMeansTestMR.NUM_DATA_POINTS)))), StreamingKMeansTestMR.DISTANCE_CUTOFF);
        for (Centroid datapoint : StreamingKMeansTestMR.syntheticData.getFirst()) {
            perPointClusterer.cluster(datapoint);
        }
        List<Centroid> perPointCentroids = Lists.newArrayList();
        for (Vector v : perPointClusterer) {
            perPointCentroids.add(((Centroid) (v)));
        }
        // Computes the cost (total sum of distances) of these different clusterings.
        double mapperCost = ClusteringUtils.totalClusterCost(StreamingKMeansTestMR.syntheticData.getFirst(), mapperCentroids);
        double localCost = ClusteringUtils.totalClusterCost(StreamingKMeansTestMR.syntheticData.getFirst(), batchCentroids);
        double perPointCost = ClusteringUtils.totalClusterCost(StreamingKMeansTestMR.syntheticData.getFirst(), perPointCentroids);
        System.out.printf(("[Total cost] Mapper %f [%d] Local %f [%d] Perpoint local %f [%d];" + "[ratio m-vs-l %f] [ratio pp-vs-l %f]\n"), mapperCost, mapperCentroids.size(), localCost, batchCentroids.size(), perPointCost, perPointCentroids.size(), (mapperCost / localCost), (perPointCost / localCost));
        // These ratios should be close to 1.0 and have been observed to be go as low as 0.6 and as low as 1.5.
        // A buffer of [0.2, 1.8] seems appropriate.
        Assert.assertEquals("Mapper StreamingKMeans / Batch local StreamingKMeans total cost ratio too far from 1", 1.0, (mapperCost / localCost), 0.8);
        Assert.assertEquals("One by one local StreamingKMeans / Batch local StreamingKMeans total cost ratio too high", 1.0, (perPointCost / localCost), 0.8);
    }

    @Test
    public void testHypercubeReducer() throws IOException {
        ReduceDriver<IntWritable, CentroidWritable, IntWritable, CentroidWritable> reduceDriver = ReduceDriver.newReduceDriver(new StreamingKMeansReducer());
        Configuration configuration = reduceDriver.getConfiguration();
        configure(configuration);
        System.out.printf("%s reducer test\n", configuration.get(SEARCHER_CLASS_OPTION));
        StreamingKMeans clusterer = new StreamingKMeans(StreamingKMeansUtilsMR.searcherFromConfiguration(configuration), ((1 << (StreamingKMeansTestMR.NUM_DIMENSIONS)) * ((int) (Math.log(StreamingKMeansTestMR.NUM_DATA_POINTS)))), StreamingKMeansTestMR.DISTANCE_CUTOFF);
        long start = System.currentTimeMillis();
        clusterer.cluster(StreamingKMeansTestMR.syntheticData.getFirst());
        long end = System.currentTimeMillis();
        System.out.printf("%f [s]\n", ((end - start) / 1000.0));
        List<CentroidWritable> reducerInputs = Lists.newArrayList();
        int postMapperTotalWeight = 0;
        for (Centroid intermediateCentroid : clusterer) {
            reducerInputs.add(new CentroidWritable(intermediateCentroid));
            postMapperTotalWeight += intermediateCentroid.getWeight();
        }
        reduceDriver.addInput(new IntWritable(0), reducerInputs);
        List<org.apache.hadoop.mrunit.types.Pair<IntWritable, CentroidWritable>> results = reduceDriver.run();
        testReducerResults(postMapperTotalWeight, results);
    }

    @Test
    public void testHypercubeMapReduce() throws IOException {
        MapReduceDriver<Writable, VectorWritable, IntWritable, CentroidWritable, IntWritable, CentroidWritable> mapReduceDriver = new MapReduceDriver(new StreamingKMeansMapper(), new StreamingKMeansReducer());
        Configuration configuration = mapReduceDriver.getConfiguration();
        configure(configuration);
        System.out.printf("%s full test\n", configuration.get(SEARCHER_CLASS_OPTION));
        for (Centroid datapoint : StreamingKMeansTestMR.syntheticData.getFirst()) {
            mapReduceDriver.addInput(new IntWritable(0), new VectorWritable(datapoint));
        }
        List<org.apache.hadoop.mrunit.types.Pair<IntWritable, CentroidWritable>> results = mapReduceDriver.run();
        testReducerResults(StreamingKMeansTestMR.syntheticData.getFirst().size(), results);
    }

    @Test
    public void testHypercubeMapReduceRunSequentially() throws Exception {
        Configuration configuration = getConfiguration();
        configure(configuration);
        configuration.set(METHOD_OPTION, SEQUENTIAL_METHOD);
        Path inputPath = new Path("testInput");
        Path outputPath = new Path("testOutput");
        StreamingKMeansUtilsMR.writeVectorsToSequenceFile(StreamingKMeansTestMR.syntheticData.getFirst(), inputPath, configuration);
        StreamingKMeansDriver.run(configuration, inputPath, outputPath);
        testReducerResults(StreamingKMeansTestMR.syntheticData.getFirst().size(), Lists.newArrayList(Iterables.transform(new org.apache.mahout.common.iterator.sequencefile.SequenceFileIterable<IntWritable, CentroidWritable>(outputPath, configuration), new com.google.common.base.Function<Pair<IntWritable, CentroidWritable>, org.apache.hadoop.mrunit.types.Pair<IntWritable, CentroidWritable>>() {
            @Override
            public org.apache.hadoop.mrunit.types.Pair<IntWritable, CentroidWritable> apply(Pair<IntWritable, CentroidWritable> input) {
                return new org.apache.hadoop.mrunit.types.Pair(input.getFirst(), input.getSecond());
            }
        })));
    }
}

