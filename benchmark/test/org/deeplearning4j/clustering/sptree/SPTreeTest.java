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
package org.deeplearning4j.clustering.sptree;


import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.memory.MemoryWorkspace;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


/**
 *
 *
 * @author Adam Gibson
 */
public class SPTreeTest {
    @Test
    public void testStructure() {
        INDArray data = Nd4j.create(new double[][]{ new double[]{ 1, 2, 3 }, new double[]{ 4, 5, 6 } });
        SpTree tree = new SpTree(data);
        try (MemoryWorkspace ws = tree.workspace().notifyScopeEntered()) {
            Assert.assertEquals(Nd4j.create(new double[]{ 2.5, 3.5, 4.5 }), tree.getCenterOfMass());
            Assert.assertEquals(2, tree.getCumSize());
            Assert.assertEquals(8, tree.getNumChildren());
            Assert.assertTrue(tree.isCorrect());
        }
    }
}

