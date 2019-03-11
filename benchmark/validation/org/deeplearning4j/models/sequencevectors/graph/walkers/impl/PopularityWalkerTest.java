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
import PopularityMode.MAXIMUM;
import PopularityMode.MINIMUM;
import SpreadSpectrum.PLAIN;
import SpreadSpectrum.PROPORTIONAL;
import WalkDirection.FORWARD_ONLY;
import java.util.concurrent.atomic.AtomicBoolean;
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
public class PopularityWalkerTest {
    private static Graph<VocabWord, Double> graph;

    @Test
    public void testPopularityWalkerCreation() throws Exception {
        GraphWalker<VocabWord> walker = new PopularityWalker.Builder<>(PopularityWalkerTest.graph).setWalkDirection(FORWARD_ONLY).setWalkLength(10).setNoEdgeHandling(CUTOFF_ON_DISCONNECTED).build();
        Assert.assertEquals("PopularityWalker", walker.getClass().getSimpleName());
    }

    @Test
    public void testPopularityWalker1() throws Exception {
        GraphWalker<VocabWord> walker = new PopularityWalker.Builder<>(PopularityWalkerTest.graph).setWalkDirection(FORWARD_ONLY).setNoEdgeHandling(CUTOFF_ON_DISCONNECTED).setWalkLength(10).setPopularityMode(MAXIMUM).setPopularitySpread(3).setSpreadSpectrum(PLAIN).build();
        System.out.println(("Connected [3] size: " + (PopularityWalkerTest.graph.getConnectedVertices(3).size())));
        System.out.println(("Connected [4] size: " + (PopularityWalkerTest.graph.getConnectedVertices(4).size())));
        Sequence<VocabWord> sequence = walker.next();
        Assert.assertEquals("0", sequence.getElements().get(0).getLabel());
        System.out.println((("Position at 1: [" + (sequence.getElements().get(1).getLabel())) + "]"));
        Assert.assertTrue((((sequence.getElements().get(1).getLabel().equals("4")) || (sequence.getElements().get(1).getLabel().equals("7"))) || (sequence.getElements().get(1).getLabel().equals("9"))));
    }

    @Test
    public void testPopularityWalker2() throws Exception {
        GraphWalker<VocabWord> walker = new PopularityWalker.Builder<>(PopularityWalkerTest.graph).setWalkDirection(FORWARD_ONLY).setNoEdgeHandling(CUTOFF_ON_DISCONNECTED).setWalkLength(10).setPopularityMode(MINIMUM).setPopularitySpread(3).build();
        System.out.println(("Connected [3] size: " + (PopularityWalkerTest.graph.getConnectedVertices(3).size())));
        System.out.println(("Connected [4] size: " + (PopularityWalkerTest.graph.getConnectedVertices(4).size())));
        Sequence<VocabWord> sequence = walker.next();
        Assert.assertEquals("0", sequence.getElements().get(0).getLabel());
        System.out.println((("Position at 1: [" + (sequence.getElements().get(1).getLabel())) + "]"));
        Assert.assertTrue(((((sequence.getElements().get(1).getLabel().equals("8")) || (sequence.getElements().get(1).getLabel().equals("3"))) || (sequence.getElements().get(1).getLabel().equals("9"))) || (sequence.getElements().get(1).getLabel().equals("7"))));
    }

    @Test
    public void testPopularityWalker3() throws Exception {
        GraphWalker<VocabWord> walker = new PopularityWalker.Builder<>(PopularityWalkerTest.graph).setWalkDirection(FORWARD_ONLY).setNoEdgeHandling(CUTOFF_ON_DISCONNECTED).setWalkLength(10).setPopularityMode(MAXIMUM).setPopularitySpread(3).setSpreadSpectrum(PROPORTIONAL).build();
        System.out.println(("Connected [3] size: " + (PopularityWalkerTest.graph.getConnectedVertices(3).size())));
        System.out.println(("Connected [4] size: " + (PopularityWalkerTest.graph.getConnectedVertices(4).size())));
        AtomicBoolean got4 = new AtomicBoolean(false);
        AtomicBoolean got7 = new AtomicBoolean(false);
        AtomicBoolean got9 = new AtomicBoolean(false);
        for (int i = 0; i < 50; i++) {
            Sequence<VocabWord> sequence = walker.next();
            Assert.assertEquals("0", sequence.getElements().get(0).getLabel());
            System.out.println((("Position at 1: [" + (sequence.getElements().get(1).getLabel())) + "]"));
            got4.compareAndSet(false, sequence.getElements().get(1).getLabel().equals("4"));
            got7.compareAndSet(false, sequence.getElements().get(1).getLabel().equals("7"));
            got9.compareAndSet(false, sequence.getElements().get(1).getLabel().equals("9"));
            Assert.assertTrue((((sequence.getElements().get(1).getLabel().equals("4")) || (sequence.getElements().get(1).getLabel().equals("7"))) || (sequence.getElements().get(1).getLabel().equals("9"))));
            walker.reset(false);
        }
        Assert.assertTrue(got4.get());
        Assert.assertTrue(got7.get());
        Assert.assertTrue(got9.get());
    }

    @Test
    public void testPopularityWalker4() throws Exception {
        GraphWalker<VocabWord> walker = new PopularityWalker.Builder<>(PopularityWalkerTest.graph).setWalkDirection(FORWARD_ONLY).setNoEdgeHandling(CUTOFF_ON_DISCONNECTED).setWalkLength(10).setPopularityMode(MINIMUM).setPopularitySpread(3).setSpreadSpectrum(PROPORTIONAL).build();
        System.out.println(("Connected [3] size: " + (PopularityWalkerTest.graph.getConnectedVertices(3).size())));
        System.out.println(("Connected [4] size: " + (PopularityWalkerTest.graph.getConnectedVertices(4).size())));
        AtomicBoolean got3 = new AtomicBoolean(false);
        AtomicBoolean got8 = new AtomicBoolean(false);
        AtomicBoolean got9 = new AtomicBoolean(false);
        for (int i = 0; i < 50; i++) {
            Sequence<VocabWord> sequence = walker.next();
            Assert.assertEquals("0", sequence.getElements().get(0).getLabel());
            System.out.println((("Position at 1: [" + (sequence.getElements().get(1).getLabel())) + "]"));
            got3.compareAndSet(false, sequence.getElements().get(1).getLabel().equals("3"));
            got8.compareAndSet(false, sequence.getElements().get(1).getLabel().equals("8"));
            got9.compareAndSet(false, sequence.getElements().get(1).getLabel().equals("9"));
            Assert.assertTrue((((sequence.getElements().get(1).getLabel().equals("8")) || (sequence.getElements().get(1).getLabel().equals("3"))) || (sequence.getElements().get(1).getLabel().equals("9"))));
            walker.reset(false);
        }
        Assert.assertTrue(got3.get());
        Assert.assertTrue(got8.get());
        Assert.assertTrue(got9.get());
    }
}

