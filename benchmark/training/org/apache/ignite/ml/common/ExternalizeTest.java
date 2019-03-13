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


import org.apache.ignite.ml.math.distances.EuclideanDistance;
import org.apache.ignite.ml.math.distances.HammingDistance;
import org.apache.ignite.ml.math.distances.ManhattanDistance;
import org.apache.ignite.ml.math.primitives.matrix.impl.DenseMatrix;
import org.apache.ignite.ml.math.primitives.vector.impl.DenseVector;
import org.apache.ignite.ml.structures.DatasetRow;
import org.apache.ignite.ml.structures.FeatureMetadata;
import org.junit.Test;


/**
 * Tests for externalizable classes.
 */
public class ExternalizeTest {
    /**
     *
     */
    @Test
    @SuppressWarnings("unchecked")
    public void test() {
        externalizeTest(new org.apache.ignite.ml.math.primitives.vector.impl.DelegatingVector(new DenseVector(1)));
        externalizeTest(new org.apache.ignite.ml.math.primitives.vector.impl.VectorizedViewMatrix(new DenseMatrix(2, 2), 1, 1, 1, 1));
        externalizeTest(new ManhattanDistance());
        externalizeTest(new HammingDistance());
        externalizeTest(new EuclideanDistance());
        externalizeTest(new FeatureMetadata());
        externalizeTest(new org.apache.ignite.ml.math.primitives.vector.impl.VectorizedViewMatrix(new DenseMatrix(2, 2), 1, 1, 1, 1));
        externalizeTest(new DatasetRow(new DenseVector()));
        externalizeTest(new org.apache.ignite.ml.structures.LabeledVector(new DenseVector(), null));
        externalizeTest(new org.apache.ignite.ml.structures.Dataset<DatasetRow<org.apache.ignite.ml.math.primitives.vector.Vector>>(new DatasetRow[]{  }, new FeatureMetadata[]{  }));
    }
}

