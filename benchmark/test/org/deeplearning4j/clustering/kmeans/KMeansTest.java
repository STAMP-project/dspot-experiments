/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.deeplearning4j.clustering.kmeans;


import java.util.List;
import org.deeplearning4j.clustering.cluster.ClusterSet;
import org.deeplearning4j.clustering.cluster.Point;
import org.deeplearning4j.clustering.cluster.PointClassification;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.factory.Nd4j;


/**
 * Created by agibsonccc on 7/2/17.
 */
public class KMeansTest {
    @Test
    public void testKMeans() {
        Nd4j.getRandom().setSeed(7);
        KMeansClustering kMeansClustering = KMeansClustering.setup(5, 5, "euclidean");
        List<Point> points = Point.toPoints(Nd4j.randn(5, 5));
        ClusterSet clusterSet = kMeansClustering.applyTo(points);
        PointClassification pointClassification = clusterSet.classifyPoint(points.get(0));
        System.out.println(pointClassification);
    }

    @Test
    public void testKmeansCosine() {
        Nd4j.getRandom().setSeed(7);
        int numClusters = 5;
        KMeansClustering kMeansClustering = KMeansClustering.setup(numClusters, 1000, "cosinesimilarity", true);
        List<Point> points = Point.toPoints(Nd4j.rand(5, 5));
        ClusterSet clusterSet = kMeansClustering.applyTo(points);
        PointClassification pointClassification = clusterSet.classifyPoint(points.get(0));
        KMeansClustering kMeansClusteringEuclidean = KMeansClustering.setup(numClusters, 1000, "euclidean");
        ClusterSet clusterSetEuclidean = kMeansClusteringEuclidean.applyTo(points);
        PointClassification pointClassificationEuclidean = clusterSetEuclidean.classifyPoint(points.get(0));
        System.out.println(("Cosine " + pointClassification));
        System.out.println(("Euclidean " + pointClassificationEuclidean));
        Assert.assertEquals(pointClassification.getCluster().getPoints().get(0), pointClassificationEuclidean.getCluster().getPoints().get(0));
    }
}

