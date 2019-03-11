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
package org.deeplearning4j.clustering.vptree;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.factory.Nd4j;


/**
 * VPTree java serialization tests
 *
 * @author raver119@gmail.com
 */
@Slf4j
public class VPTreeSerializationTests {
    @Test
    public void testSerialization_1() throws Exception {
        val points = Nd4j.rand(new int[]{ 10, 15 });
        val treeA = new VPTree(points, true, 2);
        try (val bos = new ByteArrayOutputStream()) {
            SerializationUtils.serialize(treeA, bos);
            try (val bis = new ByteArrayInputStream(bos.toByteArray())) {
                VPTree treeB = SerializationUtils.deserialize(bis);
                Assert.assertEquals(points, treeA.getItems());
                Assert.assertEquals(points, treeB.getItems());
                Assert.assertEquals(treeA.getWorkers(), treeB.getWorkers());
                val row = points.getRow(1).dup('c');
                val dpListA = new ArrayList<org.deeplearning4j.clustering.sptree.DataPoint>();
                val dListA = new ArrayList<Double>();
                val dpListB = new ArrayList<org.deeplearning4j.clustering.sptree.DataPoint>();
                val dListB = new ArrayList<Double>();
                treeA.search(row, 3, dpListA, dListA);
                treeB.search(row, 3, dpListB, dListB);
                Assert.assertTrue(((dpListA.size()) != 0));
                Assert.assertTrue(((dListA.size()) != 0));
                Assert.assertEquals(dpListA.size(), dpListB.size());
                Assert.assertEquals(dListA.size(), dListB.size());
                for (int e = 0; e < (dpListA.size()); e++) {
                    val rA = dpListA.get(e).getPoint();
                    val rB = dpListB.get(e).getPoint();
                    Assert.assertEquals(rA, rB);
                }
            }
        }
    }

    @Test
    public void testNewConstructor_1() {
        val points = Nd4j.rand(new int[]{ 10, 15 });
        val treeA = new VPTree(points, true, 2);
        val rows = Nd4j.tear(points, 1);
        val list = new ArrayList<org.deeplearning4j.clustering.sptree.DataPoint>();
        int idx = 0;
        for (val r : rows)
            list.add(new org.deeplearning4j.clustering.sptree.DataPoint((idx++), r));

        val treeB = new VPTree(list);
        Assert.assertEquals(points, treeA.getItems());
        Assert.assertEquals(points, treeB.getItems());
    }
}

