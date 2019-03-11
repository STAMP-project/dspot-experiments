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


import NoEdgeHandling.CUTOFF_ON_DISCONNECTED;
import NoEdgeHandling.EXCEPTION_ON_DISCONNECTED;
import NoEdgeHandling.SELF_LOOP_ON_DISCONNECTED;
import WalkDirection.FORWARD_UNIQUE;
import org.deeplearning4j.models.sequencevectors.graph.exception.NoEdgesException;
import org.deeplearning4j.models.sequencevectors.graph.primitives.Graph;
import org.deeplearning4j.models.sequencevectors.graph.primitives.IGraph;
import org.deeplearning4j.models.sequencevectors.graph.primitives.Vertex;
import org.deeplearning4j.models.sequencevectors.graph.walkers.GraphWalker;
import org.deeplearning4j.models.sequencevectors.sequence.Sequence;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author raver119@gmail.com
 */
public class RandomWalkerTest {
    private static IGraph<VocabWord, Double> graph;

    private static IGraph<VocabWord, Double> graphBig;

    private static IGraph<VocabWord, Double> graphDirected;

    protected static final Logger logger = LoggerFactory.getLogger(RandomWalkerTest.class);

    @Test
    public void testGraphCreation() throws Exception {
        Graph<VocabWord, Double> graph = new Graph(10, false, new org.deeplearning4j.models.sequencevectors.graph.vertex.AbstractVertexFactory<VocabWord>());
        // we have 10 elements
        Assert.assertEquals(10, graph.numVertices());
        for (int i = 0; i < 10; i++) {
            Vertex<VocabWord> vertex = graph.getVertex(i);
            Assert.assertEquals(null, vertex.getValue());
            Assert.assertEquals(i, vertex.vertexID());
        }
        Assert.assertEquals(10, graph.numVertices());
    }

    @Test
    public void testGraphTraverseRandom1() throws Exception {
        RandomWalker<VocabWord> walker = ((RandomWalker<VocabWord>) (new RandomWalker.Builder<>(RandomWalkerTest.graph).setNoEdgeHandling(SELF_LOOP_ON_DISCONNECTED).setWalkLength(3).build()));
        int cnt = 0;
        while (walker.hasNext()) {
            Sequence<VocabWord> sequence = walker.next();
            Assert.assertEquals(3, sequence.getElements().size());
            Assert.assertNotEquals(null, sequence);
            for (VocabWord word : sequence.getElements()) {
                Assert.assertNotEquals(null, word);
            }
            cnt++;
        } 
        Assert.assertEquals(10, cnt);
    }

    @Test
    public void testGraphTraverseRandom2() throws Exception {
        RandomWalker<VocabWord> walker = ((RandomWalker<VocabWord>) (new RandomWalker.Builder<>(RandomWalkerTest.graph).setNoEdgeHandling(EXCEPTION_ON_DISCONNECTED).setWalkLength(20).setWalkDirection(FORWARD_UNIQUE).setNoEdgeHandling(CUTOFF_ON_DISCONNECTED).build()));
        int cnt = 0;
        while (walker.hasNext()) {
            Sequence<VocabWord> sequence = walker.next();
            Assert.assertTrue(((sequence.getElements().size()) <= 10));
            Assert.assertNotEquals(null, sequence);
            for (VocabWord word : sequence.getElements()) {
                Assert.assertNotEquals(null, word);
            }
            cnt++;
        } 
        Assert.assertEquals(10, cnt);
    }

    @Test
    public void testGraphTraverseRandom3() throws Exception {
        RandomWalker<VocabWord> walker = ((RandomWalker<VocabWord>) (new RandomWalker.Builder<>(RandomWalkerTest.graph).setNoEdgeHandling(EXCEPTION_ON_DISCONNECTED).setWalkLength(20).setWalkDirection(FORWARD_UNIQUE).setNoEdgeHandling(EXCEPTION_ON_DISCONNECTED).build()));
        try {
            while (walker.hasNext()) {
                Sequence<VocabWord> sequence = walker.next();
                RandomWalkerTest.logger.info(("Sequence: " + sequence));
            } 
            // if cycle passed without exception - something went bad
            Assert.assertTrue(false);
        } catch (NoEdgesException e) {
            // this cycle should throw exception
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testGraphTraverseRandom4() throws Exception {
        RandomWalker<VocabWord> walker = ((RandomWalker<VocabWord>) (new RandomWalker.Builder<>(RandomWalkerTest.graphBig).setNoEdgeHandling(EXCEPTION_ON_DISCONNECTED).setWalkLength(20).setWalkDirection(FORWARD_UNIQUE).setNoEdgeHandling(CUTOFF_ON_DISCONNECTED).build()));
        Sequence<VocabWord> sequence1 = walker.next();
        walker.reset(true);
        Sequence<VocabWord> sequence2 = walker.next();
        Assert.assertNotEquals(sequence1.getElements().get(0), sequence2.getElements().get(0));
    }

    @Test
    public void testGraphTraverseRandom5() throws Exception {
        RandomWalker<VocabWord> walker = ((RandomWalker<VocabWord>) (new RandomWalker.Builder<>(RandomWalkerTest.graphBig).setWalkLength(20).setWalkDirection(FORWARD_UNIQUE).setNoEdgeHandling(CUTOFF_ON_DISCONNECTED).build()));
        Sequence<VocabWord> sequence1 = walker.next();
        walker.reset(false);
        Sequence<VocabWord> sequence2 = walker.next();
        Assert.assertEquals(sequence1.getElements().get(0), sequence2.getElements().get(0));
    }

    @Test
    public void testGraphTraverseRandom6() throws Exception {
        GraphWalker<VocabWord> walker = new RandomWalker.Builder<>(RandomWalkerTest.graphDirected).setWalkLength(20).setWalkDirection(FORWARD_UNIQUE).setNoEdgeHandling(CUTOFF_ON_DISCONNECTED).build();
        Sequence<VocabWord> sequence = walker.next();
        Assert.assertEquals("0", sequence.getElements().get(0).getLabel());
        Assert.assertEquals("3", sequence.getElements().get(1).getLabel());
        Assert.assertEquals("6", sequence.getElements().get(2).getLabel());
        Assert.assertEquals("9", sequence.getElements().get(3).getLabel());
        Assert.assertEquals(4, sequence.getElements().size());
    }
}

