/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.ml.common;


import org.apache.ignite.ml.knn.ann.ANNClassificationTrainer;
import org.apache.ignite.ml.knn.classification.KNNClassificationModel;
import org.apache.ignite.ml.knn.classification.NNStrategy;
import org.apache.ignite.ml.math.distances.EuclideanDistance;
import org.apache.ignite.ml.math.distances.HammingDistance;
import org.apache.ignite.ml.math.distances.ManhattanDistance;
import org.apache.ignite.ml.math.primitives.matrix.impl.DenseMatrix;
import org.apache.ignite.ml.math.primitives.vector.Vector;
import org.apache.ignite.ml.math.primitives.vector.impl.DenseVector;
import org.apache.ignite.ml.structures.DatasetRow;
import org.apache.ignite.ml.structures.FeatureMetadata;
import org.apache.ignite.ml.svm.SVMLinearClassificationModel;
import org.junit.Test;


/**
 * Tests for equals and hashCode methods in classes that provide own implementations of these.
 */
public class CollectionsTest {
    /**
     *
     */
    @Test
    @SuppressWarnings("unchecked")
    public void test() {
        test(new org.apache.ignite.ml.math.primitives.vector.impl.VectorizedViewMatrix(new DenseMatrix(2, 2), 1, 1, 1, 1), new org.apache.ignite.ml.math.primitives.vector.impl.VectorizedViewMatrix(new DenseMatrix(3, 2), 2, 1, 1, 1));
        specialTest(new ManhattanDistance(), new ManhattanDistance());
        specialTest(new HammingDistance(), new HammingDistance());
        specialTest(new EuclideanDistance(), new EuclideanDistance());
        FeatureMetadata data = new FeatureMetadata("name2");
        data.setName("name1");
        test(data, new FeatureMetadata("name2"));
        test(new DatasetRow(new DenseVector()), new DatasetRow(new DenseVector(1)));
        test(new org.apache.ignite.ml.structures.LabeledVector(new DenseVector(), null), new org.apache.ignite.ml.structures.LabeledVector(new DenseVector(1), null));
        test(new org.apache.ignite.ml.structures.Dataset<DatasetRow<Vector>>(new DatasetRow[]{  }, new FeatureMetadata[]{  }), new org.apache.ignite.ml.structures.Dataset<DatasetRow<Vector>>(new DatasetRow[]{ new DatasetRow() }, new FeatureMetadata[]{ new FeatureMetadata() }));
        test(new org.apache.ignite.ml.regressions.logistic.LogisticRegressionModel(new DenseVector(), 1.0), new org.apache.ignite.ml.regressions.logistic.LogisticRegressionModel(new DenseVector(), 0.5));
        test(new org.apache.ignite.ml.clustering.kmeans.KMeansModelFormat(new Vector[]{  }, new ManhattanDistance()), new org.apache.ignite.ml.clustering.kmeans.KMeansModelFormat(new Vector[]{  }, new HammingDistance()));
        test(new org.apache.ignite.ml.clustering.kmeans.KMeansModel(new Vector[]{  }, new ManhattanDistance()), new org.apache.ignite.ml.clustering.kmeans.KMeansModel(new Vector[]{  }, new HammingDistance()));
        test(new org.apache.ignite.ml.knn.classification.KNNModelFormat(1, new ManhattanDistance(), NNStrategy.SIMPLE), new org.apache.ignite.ml.knn.classification.KNNModelFormat(2, new ManhattanDistance(), NNStrategy.SIMPLE));
        test(new KNNClassificationModel(null).withK(1), new KNNClassificationModel(null).withK(2));
        test(new SVMLinearClassificationModel(null, 1.0), new SVMLinearClassificationModel(null, 0.5));
        test(new org.apache.ignite.ml.knn.ann.ANNClassificationModel(new org.apache.ignite.ml.structures.LabeledVectorSet(), new ANNClassificationTrainer.CentroidStat()), new org.apache.ignite.ml.knn.ann.ANNClassificationModel(new org.apache.ignite.ml.structures.LabeledVectorSet(1, 1, true), new ANNClassificationTrainer.CentroidStat()));
        test(new org.apache.ignite.ml.knn.ann.ANNModelFormat(1, new ManhattanDistance(), NNStrategy.SIMPLE, new org.apache.ignite.ml.structures.LabeledVectorSet(), new ANNClassificationTrainer.CentroidStat()), new org.apache.ignite.ml.knn.ann.ANNModelFormat(2, new ManhattanDistance(), NNStrategy.SIMPLE, new org.apache.ignite.ml.structures.LabeledVectorSet(), new ANNClassificationTrainer.CentroidStat()));
    }
}

