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
package org.deeplearning4j.models.sequencevectors.graph.walkers.impl;


import NoEdgeHandling.RESTART_ON_DISCONNECTED;
import WalkDirection.FORWARD_PREFERRED;
import org.deeplearning4j.models.sequencevectors.graph.primitives.Graph;
import org.deeplearning4j.models.sequencevectors.graph.walkers.GraphWalker;
import org.deeplearning4j.models.sequencevectors.sequence.Sequence;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author raver119@gmail.com
 */
public class WeightedWalkerTest {
    private static Graph<VocabWord, Integer> basicGraph;

    @Test
    public void testBasicIterator1() throws Exception {
        GraphWalker<VocabWord> walker = new WeightedWalker.Builder<>(WeightedWalkerTest.basicGraph).setWalkDirection(FORWARD_PREFERRED).setWalkLength(10).setNoEdgeHandling(RESTART_ON_DISCONNECTED).build();
        int cnt = 0;
        while (walker.hasNext()) {
            Sequence<VocabWord> sequence = walker.next();
            Assert.assertNotEquals(null, sequence);
            Assert.assertEquals(10, sequence.getElements().size());
            cnt++;
        } 
        Assert.assertEquals(WeightedWalkerTest.basicGraph.numVertices(), cnt);
    }
}

