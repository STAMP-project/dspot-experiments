/**
 * Copyright (c) 2015, Cloudera and Intel, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.app.batch.mllib.kmeans;


import com.cloudera.oryx.app.kmeans.ClusterInfo;
import com.cloudera.oryx.lambda.AbstractSparkIT;
import java.util.List;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.linalg.Vector;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class KMeansEvalIT extends AbstractSparkIT {
    private static final Logger log = LoggerFactory.getLogger(KMeansEvalIT.class);

    @Test
    public void testFetchSampleEvalData() {
        JavaRDD<Vector> evalData = SilhouetteCoefficient.fetchSampleData(KMeansEvalIT.getRddOfVectors());
        assertEquals(6, evalData.count());
    }

    @Test
    public void testDunnIndexForClustering() {
        List<ClusterInfo> clusters = KMeansEvalIT.getClusters();
        DunnIndex dunnIndex = new DunnIndex(clusters);
        double eval = dunnIndex.evaluate(KMeansEvalIT.getRddOfVectors());
        KMeansEvalIT.log.info("Dunn Index for {} clusters: {}", clusters.size(), eval);
        assertEquals(1.3110480733464633, eval);
    }

    @Test
    public void testDaviesBouldinIndexForClustering() {
        List<ClusterInfo> clusters = KMeansEvalIT.getClusters();
        DaviesBouldinIndex daviesBouldinIndex = new DaviesBouldinIndex(clusters);
        double eval = daviesBouldinIndex.evaluate(KMeansEvalIT.getRddOfVectors());
        KMeansEvalIT.log.info("Davies Bouldin Index for {} clusters: {}", clusters.size(), eval);
        assertEquals(0.9702216688254247, eval);
    }

    @Test
    public void testSilhouetteCoefficientForClustering() {
        List<ClusterInfo> clusters = KMeansEvalIT.getClusters();
        SilhouetteCoefficient silhouetteCoefficient = new SilhouetteCoefficient(clusters);
        double eval = silhouetteCoefficient.evaluate(KMeansEvalIT.getRddOfVectors());
        KMeansEvalIT.log.info("Silhouette Coefficient for {} clusters: {}", clusters.size(), eval);
        assertEquals(0.30648167401009796, eval);
    }

    @Test
    public void testSSEForClustering() {
        List<ClusterInfo> clusters = KMeansEvalIT.getClusters();
        SumSquaredError sse = new SumSquaredError(clusters);
        double eval = sse.evaluate(KMeansEvalIT.getRddOfVectors());
        KMeansEvalIT.log.info("SSE for {} clusters: {}", clusters.size(), eval);
        assertEquals(5.5, eval);
    }

    @Test
    public void testComputeSilhouetteCoefficient() {
        assertEquals(5.0, SilhouetteCoefficient.silhouetteCoefficient((-0.8), 0.2));
        assertEquals((-1.25), SilhouetteCoefficient.silhouetteCoefficient(0.8, (-0.2)));
        assertEquals(0.0, SilhouetteCoefficient.silhouetteCoefficient(1.5, 1.5));
        assertEquals(1.0, SilhouetteCoefficient.silhouetteCoefficient(1.5, Double.POSITIVE_INFINITY));
        assertEquals((-1.0), SilhouetteCoefficient.silhouetteCoefficient(Double.POSITIVE_INFINITY, 1.5));
    }
}

