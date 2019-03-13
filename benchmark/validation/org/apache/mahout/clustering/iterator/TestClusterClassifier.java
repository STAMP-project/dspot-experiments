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
package org.apache.mahout.clustering.iterator;


import java.io.IOException;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.clustering.AbstractCluster;
import org.apache.mahout.clustering.Cluster;
import org.apache.mahout.clustering.ClusteringTestUtils;
import org.apache.mahout.clustering.classify.ClusterClassifier;
import org.apache.mahout.clustering.kmeans.TestKmeansClustering;
import org.apache.mahout.common.MahoutTestCase;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.junit.Test;


public final class TestClusterClassifier extends MahoutTestCase {
    @Test
    public void testDMClusterClassification() {
        ClusterClassifier classifier = TestClusterClassifier.newDMClassifier();
        Vector pdf = classifier.classify(new DenseVector(2));
        assertEquals("[0,0]", "[0.2,0.6,0.2]", AbstractCluster.formatVector(pdf, null));
        pdf = classifier.classify(new DenseVector(2).assign(2));
        assertEquals("[2,2]", "[0.493,0.296,0.211]", AbstractCluster.formatVector(pdf, null));
    }

    @Test
    public void testClusterClassification() {
        ClusterClassifier classifier = TestClusterClassifier.newKlusterClassifier();
        Vector pdf = classifier.classify(new DenseVector(2));
        assertEquals("[0,0]", "[0.2,0.6,0.2]", AbstractCluster.formatVector(pdf, null));
        pdf = classifier.classify(new DenseVector(2).assign(2));
        assertEquals("[2,2]", "[0.493,0.296,0.211]", AbstractCluster.formatVector(pdf, null));
    }

    @Test
    public void testSoftClusterClassification() {
        ClusterClassifier classifier = TestClusterClassifier.newSoftClusterClassifier();
        Vector pdf = classifier.classify(new DenseVector(2));
        assertEquals("[0,0]", "[0.0,1.0,0.0]", AbstractCluster.formatVector(pdf, null));
        pdf = classifier.classify(new DenseVector(2).assign(2));
        assertEquals("[2,2]", "[0.735,0.184,0.082]", AbstractCluster.formatVector(pdf, null));
    }

    @Test
    public void testDMClassifierSerialization() throws Exception {
        ClusterClassifier classifier = TestClusterClassifier.newDMClassifier();
        ClusterClassifier classifierOut = writeAndRead(classifier);
        assertEquals(classifier.getModels().size(), classifierOut.getModels().size());
        assertEquals(classifier.getModels().get(0).getClass().getName(), classifierOut.getModels().get(0).getClass().getName());
    }

    @Test
    public void testClusterClassifierSerialization() throws Exception {
        ClusterClassifier classifier = TestClusterClassifier.newKlusterClassifier();
        ClusterClassifier classifierOut = writeAndRead(classifier);
        assertEquals(classifier.getModels().size(), classifierOut.getModels().size());
        assertEquals(classifier.getModels().get(0).getClass().getName(), classifierOut.getModels().get(0).getClass().getName());
    }

    @Test
    public void testSoftClusterClassifierSerialization() throws Exception {
        ClusterClassifier classifier = TestClusterClassifier.newSoftClusterClassifier();
        ClusterClassifier classifierOut = writeAndRead(classifier);
        assertEquals(classifier.getModels().size(), classifierOut.getModels().size());
        assertEquals(classifier.getModels().get(0).getClass().getName(), classifierOut.getModels().get(0).getClass().getName());
    }

    @Test
    public void testClusterIteratorKMeans() {
        List<Vector> data = TestKmeansClustering.getPoints(TestKmeansClustering.REFERENCE);
        ClusterClassifier prior = TestClusterClassifier.newKlusterClassifier();
        ClusterClassifier posterior = ClusterIterator.iterate(data, prior, 5);
        assertEquals(3, posterior.getModels().size());
        for (Cluster cluster : posterior.getModels()) {
            System.out.println(cluster.asFormatString(null));
        }
    }

    @Test
    public void testClusterIteratorDirichlet() {
        List<Vector> data = TestKmeansClustering.getPoints(TestKmeansClustering.REFERENCE);
        ClusterClassifier prior = TestClusterClassifier.newKlusterClassifier();
        ClusterClassifier posterior = ClusterIterator.iterate(data, prior, 5);
        assertEquals(3, posterior.getModels().size());
        for (Cluster cluster : posterior.getModels()) {
            System.out.println(cluster.asFormatString(null));
        }
    }

    @Test
    public void testSeqFileClusterIteratorKMeans() throws IOException {
        Path pointsPath = getTestTempDirPath("points");
        Path priorPath = getTestTempDirPath("prior");
        Path outPath = getTestTempDirPath("output");
        Configuration conf = getConfiguration();
        FileSystem fs = FileSystem.get(pointsPath.toUri(), conf);
        List<VectorWritable> points = TestKmeansClustering.getPointsWritable(TestKmeansClustering.REFERENCE);
        ClusteringTestUtils.writePointsToFile(points, new Path(pointsPath, "file1"), fs, conf);
        Path path = new Path(priorPath, "priorClassifier");
        ClusterClassifier prior = TestClusterClassifier.newKlusterClassifier();
        prior.writeToSeqFiles(path);
        assertEquals(3, prior.getModels().size());
        System.out.println("Prior");
        for (Cluster cluster : prior.getModels()) {
            System.out.println(cluster.asFormatString(null));
        }
        ClusterIterator.iterateSeq(conf, pointsPath, path, outPath, 5);
        for (int i = 1; i <= 4; i++) {
            System.out.println(("Classifier-" + i));
            ClusterClassifier posterior = new ClusterClassifier();
            String name = (i == 4) ? "clusters-4-final" : "clusters-" + i;
            posterior.readFromSeqFiles(conf, new Path(outPath, name));
            assertEquals(3, posterior.getModels().size());
            for (Cluster cluster : posterior.getModels()) {
                System.out.println(cluster.asFormatString(null));
            }
        }
    }

    @Test
    public void testMRFileClusterIteratorKMeans() throws Exception {
        Path pointsPath = getTestTempDirPath("points");
        Path priorPath = getTestTempDirPath("prior");
        Path outPath = getTestTempDirPath("output");
        Configuration conf = getConfiguration();
        FileSystem fs = FileSystem.get(pointsPath.toUri(), conf);
        List<VectorWritable> points = TestKmeansClustering.getPointsWritable(TestKmeansClustering.REFERENCE);
        ClusteringTestUtils.writePointsToFile(points, new Path(pointsPath, "file1"), fs, conf);
        Path path = new Path(priorPath, "priorClassifier");
        ClusterClassifier prior = TestClusterClassifier.newKlusterClassifier();
        prior.writeToSeqFiles(path);
        ClusteringPolicy policy = new KMeansClusteringPolicy();
        ClusterClassifier.writePolicy(policy, path);
        assertEquals(3, prior.getModels().size());
        System.out.println("Prior");
        for (Cluster cluster : prior.getModels()) {
            System.out.println(cluster.asFormatString(null));
        }
        ClusterIterator.iterateMR(conf, pointsPath, path, outPath, 5);
        for (int i = 1; i <= 4; i++) {
            System.out.println(("Classifier-" + i));
            ClusterClassifier posterior = new ClusterClassifier();
            String name = (i == 4) ? "clusters-4-final" : "clusters-" + i;
            posterior.readFromSeqFiles(conf, new Path(outPath, name));
            assertEquals(3, posterior.getModels().size());
            for (Cluster cluster : posterior.getModels()) {
                System.out.println(cluster.asFormatString(null));
            }
        }
    }

    @Test
    public void testCosineKlusterClassification() {
        ClusterClassifier classifier = TestClusterClassifier.newCosineKlusterClassifier();
        Vector pdf = classifier.classify(new DenseVector(2));
        assertEquals("[0,0]", "[0.333,0.333,0.333]", AbstractCluster.formatVector(pdf, null));
        pdf = classifier.classify(new DenseVector(2).assign(2));
        assertEquals("[2,2]", "[0.429,0.429,0.143]", AbstractCluster.formatVector(pdf, null));
    }
}

