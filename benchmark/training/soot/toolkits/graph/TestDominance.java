/**
 * -
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (c) 2014 Tim Henderson, Case Western Reserve University
 *   Cleveland, Ohio 44106
 *   All Rights Reserved.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package soot.toolkits.graph;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import soot.toolkits.graph.pdg.MHGDominatorTree;


public class TestDominance {
    @Test
    public void TestSimpleDiamond() {
        Node x = new Node(4);
        Node n = new Node(1).addkid(new Node(2).addkid(x)).addkid(new Node(3).addkid(x));
        Graph g = new Graph(n);
        MHGDominatorsFinder<Node> finder = new MHGDominatorsFinder<Node>(g);
        DominatorTree<Node> tree = new DominatorTree<Node>(finder);
        MatcherAssert.assertThat(tree.getHeads().size(), Matchers.is(1));
        DominatorNode<Node> head = tree.getHeads().get(0);
        MatcherAssert.assertThat(head.getGode().id, Matchers.is(1));
        Set<Integer> kids = kid_ids(head);
        MatcherAssert.assertThat(kids.size(), Matchers.is(3));
        MatcherAssert.assertThat(kids, Matchers.contains(2, 3, 4));
    }

    @Test
    public void TestAcyclicCFG() {
        Node n1 = new Node(1);
        Node n2 = new Node(2);
        Node n3 = new Node(3);
        Node n4 = new Node(4);
        Node n5 = new Node(5);
        Node n6 = new Node(6);
        Node n7 = new Node(7);
        Node n8 = new Node(8);
        Node n9 = new Node(9);
        Node n10 = new Node(10);
        Node n11 = new Node(11);
        n1.addkid(n2).addkid(n3);
        n2.addkid(n9);
        n3.addkid(n4).addkid(n5);
        n4.addkid(n9);
        n5.addkid(n6).addkid(n10);
        n6.addkid(n7).addkid(n8);
        n7.addkid(n10);
        n8.addkid(n10);
        n9.addkid(n11);
        n10.addkid(n11);
        Graph g = new Graph(n1);
        MHGDominatorsFinder<Node> finder = new MHGDominatorsFinder<Node>(g);
        DominatorTree<Node> tree = new DominatorTree<Node>(finder);
        MatcherAssert.assertThat(tree.getHeads().size(), Matchers.is(1));
        DominatorNode<Node> n = tree.getHeads().get(0);
        MatcherAssert.assertThat(n.getGode().id, Matchers.is(1));
        Set<Integer> kids = kid_ids(n);
        MatcherAssert.assertThat(kids.size(), Matchers.is(4));
        MatcherAssert.assertThat(kids, Matchers.contains(2, 3, 9, 11));
        Map<Integer, DominatorNode<Node>> KM = kid_map(n);
        DominatorNode<Node> m = KM.get(2);
        kids = kid_ids(m);
        MatcherAssert.assertThat(kids.size(), Matchers.is(0));
        m = KM.get(9);
        kids = kid_ids(m);
        MatcherAssert.assertThat(kids.size(), Matchers.is(0));
        m = KM.get(11);
        kids = kid_ids(m);
        MatcherAssert.assertThat(kids.size(), Matchers.is(0));
        n = KM.get(3);
        kids = kid_ids(n);
        MatcherAssert.assertThat(kids.size(), Matchers.is(2));
        MatcherAssert.assertThat(kids, Matchers.contains(4, 5));
        KM = kid_map(n);
        m = KM.get(4);
        kids = kid_ids(m);
        MatcherAssert.assertThat(kids.size(), Matchers.is(0));
        n = KM.get(5);
        kids = kid_ids(n);
        MatcherAssert.assertThat(kids.size(), Matchers.is(2));
        MatcherAssert.assertThat(kids, Matchers.contains(6, 10));
        KM = kid_map(n);
        m = KM.get(10);
        kids = kid_ids(m);
        MatcherAssert.assertThat(kids.size(), Matchers.is(0));
        n = KM.get(6);
        kids = kid_ids(n);
        MatcherAssert.assertThat(kids.size(), Matchers.is(2));
        MatcherAssert.assertThat(kids, Matchers.contains(7, 8));
        KM = kid_map(n);
        m = KM.get(7);
        kids = kid_ids(m);
        MatcherAssert.assertThat(kids.size(), Matchers.is(0));
        m = KM.get(8);
        kids = kid_ids(m);
        MatcherAssert.assertThat(kids.size(), Matchers.is(0));
    }

    @Test
    public void TestMultiTailedPostDom() {
        Node n1 = new Node(1);
        Node n2 = new Node(2);
        Node n3 = new Node(3);
        Node n4 = new Node(4);
        Node n5 = new Node(5);
        Node n6 = new Node(6);
        n1.addkid(n2).addkid(n3);
        n3.addkid(n4).addkid(n5);
        n4.addkid(n6);
        n5.addkid(n6);
        Graph g = new Graph(n1);
        MHGDominatorsFinder<Node> finder = new MHGDominatorsFinder<Node>(g);
        MHGDominatorTree<Node> tree = new MHGDominatorTree<Node>(finder);
        MatcherAssert.assertThat(tree.getHeads().size(), Matchers.is(1));
        DominatorNode<Node> n = tree.getHeads().get(0);
        MatcherAssert.assertThat(n.getGode().id, Matchers.is(1));
        Set<Integer> kids = kid_ids(n);
        MatcherAssert.assertThat(kids.size(), Matchers.is(2));
        MatcherAssert.assertThat(kids, Matchers.contains(2, 3));
        Map<Integer, DominatorNode<Node>> KM = kid_map(n);
        DominatorNode<Node> m = KM.get(2);
        kids = kid_ids(m);
        MatcherAssert.assertThat(kids.size(), Matchers.is(0));
        n = KM.get(3);
        kids = kid_ids(n);
        MatcherAssert.assertThat(kids.size(), Matchers.is(3));
        MatcherAssert.assertThat(kids, Matchers.contains(4, 5, 6));
        KM = kid_map(n);
        m = KM.get(4);
        kids = kid_ids(m);
        MatcherAssert.assertThat(kids.size(), Matchers.is(0));
        m = KM.get(5);
        kids = kid_ids(m);
        MatcherAssert.assertThat(kids.size(), Matchers.is(0));
        m = KM.get(6);
        kids = kid_ids(m);
        MatcherAssert.assertThat(kids.size(), Matchers.is(0));
        // ---------- now post-dom --------------
        MHGPostDominatorsFinder<Node> pfinder = new MHGPostDominatorsFinder<Node>(g);
        tree = new MHGDominatorTree<Node>(pfinder);
        Map<Integer, DominatorNode<Node>> heads = new HashMap<Integer, DominatorNode<Node>>();
        for (DominatorNode<Node> dhead : tree.getHeads()) {
            Node head = dhead.getGode();
            heads.put(head.id, dhead);
        }
        Set<Integer> head_ids = heads.keySet();
        MatcherAssert.assertThat(head_ids.size(), Matchers.is(3));
        MatcherAssert.assertThat(head_ids, Matchers.contains(1, 2, 6));
        m = heads.get(1);
        kids = kid_ids(m);
        MatcherAssert.assertThat(kids.size(), Matchers.is(0));
        m = heads.get(2);
        kids = kid_ids(m);
        MatcherAssert.assertThat(kids.size(), Matchers.is(0));
        n = heads.get(6);
        kids = kid_ids(n);
        MatcherAssert.assertThat(kids.size(), Matchers.is(3));
        MatcherAssert.assertThat(kids, Matchers.contains(3, 4, 5));
        KM = kid_map(n);
        m = KM.get(3);
        kids = kid_ids(m);
        MatcherAssert.assertThat(kids.size(), Matchers.is(0));
        m = KM.get(4);
        kids = kid_ids(m);
        MatcherAssert.assertThat(kids.size(), Matchers.is(0));
        m = KM.get(5);
        kids = kid_ids(m);
        MatcherAssert.assertThat(kids.size(), Matchers.is(0));
    }
}

