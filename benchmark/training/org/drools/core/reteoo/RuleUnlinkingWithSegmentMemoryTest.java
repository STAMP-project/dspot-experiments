/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.core.reteoo;


import LeftInputAdapterNode.LiaNodeMemory;
import java.util.List;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.phreak.SegmentUtilities;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.PropagationContext;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;


public class RuleUnlinkingWithSegmentMemoryTest {
    InternalKnowledgeBase kBase;

    BuildContext buildContext;

    PropagationContext context;

    LeftInputAdapterNode lian;

    BetaNode n1;

    BetaNode n2;

    BetaNode n3;

    BetaNode n4;

    BetaNode n5;

    BetaNode n6;

    BetaNode n7;

    BetaNode n8;

    BetaNode n9;

    BetaNode n10;

    RuleTerminalNode rtn1;

    RuleTerminalNode rtn2;

    RuleTerminalNode rtn3;

    RuleImpl rule1;

    RuleImpl rule2;

    RuleImpl rule3;

    static final int JOIN_NODE = 0;

    static final int EXISTS_NODE = 1;

    static final int NOT_NODE = 2;

    static final int RULE_TERMINAL_NODE = 3;

    @Test
    public void testRuleSegmentsAllLinkedTestMasks() {
        setUp(RuleUnlinkingWithSegmentMemoryTest.JOIN_NODE);
        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        InternalKnowledgeBase kBase = ((InternalKnowledgeBase) (KnowledgeBaseFactory.newKnowledgeBase(kconf)));
        StatefulKnowledgeSessionImpl wm = new StatefulKnowledgeSessionImpl(1L, kBase);
        PathMemory rs = ((PathMemory) (wm.getNodeMemory(rtn1)));
        Assert.assertFalse(rs.isRuleLinked());
        Assert.assertEquals(1, rs.getAllLinkedMaskTest());
        rs = ((PathMemory) (wm.getNodeMemory(rtn2)));
        Assert.assertFalse(rs.isRuleLinked());
        Assert.assertEquals(3, rs.getAllLinkedMaskTest());
        rs = ((PathMemory) (wm.getNodeMemory(rtn3)));
        Assert.assertFalse(rs.isRuleLinked());
        Assert.assertEquals(7, rs.getAllLinkedMaskTest());
    }

    @Test
    public void testSegmentNodeReferencesToSegments() {
        setUp(RuleUnlinkingWithSegmentMemoryTest.JOIN_NODE);
        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        InternalKnowledgeBase kBase = ((InternalKnowledgeBase) (KnowledgeBaseFactory.newKnowledgeBase(kconf)));
        StatefulKnowledgeSessionImpl wm = new StatefulKnowledgeSessionImpl(1L, kBase);
        BetaMemory bm = null;
        List<PathMemory> list;
        PathMemory rtn1Rs = ((PathMemory) (wm.getNodeMemory(rtn1)));
        PathMemory rtn2Rs = ((PathMemory) (wm.getNodeMemory(rtn2)));
        PathMemory rtn3Rs = ((PathMemory) (wm.getNodeMemory(rtn3)));
        // lian
        SegmentUtilities.createSegmentMemory(lian, wm);
        LeftInputAdapterNode.LiaNodeMemory lmem = ((LeftInputAdapterNode.LiaNodeMemory) (wm.getNodeMemory(lian)));
        Assert.assertEquals(1, lmem.getNodePosMaskBit());
        // n1
        SegmentUtilities.createSegmentMemory(n1, wm);
        bm = ((BetaMemory) (wm.getNodeMemory(n1)));
        Assert.assertEquals(2, bm.getNodePosMaskBit());
        Assert.assertEquals(15, bm.getSegmentMemory().getAllLinkedMaskTest());
        Assert.assertEquals(1, bm.getSegmentMemory().getSegmentPosMaskBit());
        list = bm.getSegmentMemory().getPathMemories();
        Assert.assertEquals(3, list.size());
        Assert.assertTrue(list.contains(rtn1Rs));
        Assert.assertTrue(list.contains(rtn2Rs));
        Assert.assertTrue(list.contains(rtn3Rs));
        // n2
        bm = ((BetaMemory) (wm.getNodeMemory(n2)));
        Assert.assertEquals(4, bm.getNodePosMaskBit());
        Assert.assertEquals(15, bm.getSegmentMemory().getAllLinkedMaskTest());
        Assert.assertEquals(1, bm.getSegmentMemory().getSegmentPosMaskBit());
        list = bm.getSegmentMemory().getPathMemories();
        Assert.assertEquals(3, list.size());
        Assert.assertTrue(list.contains(rtn1Rs));
        Assert.assertTrue(list.contains(rtn2Rs));
        Assert.assertTrue(list.contains(rtn3Rs));
        // n3
        bm = ((BetaMemory) (wm.getNodeMemory(n3)));
        Assert.assertEquals(8, bm.getNodePosMaskBit());
        Assert.assertEquals(15, bm.getSegmentMemory().getAllLinkedMaskTest());
        Assert.assertEquals(1, bm.getSegmentMemory().getSegmentPosMaskBit());
        list = bm.getSegmentMemory().getPathMemories();
        Assert.assertEquals(3, list.size());
        Assert.assertTrue(list.contains(rtn1Rs));
        Assert.assertTrue(list.contains(rtn2Rs));
        Assert.assertTrue(list.contains(rtn3Rs));
        // n4
        SegmentUtilities.createSegmentMemory(n4, wm);
        bm = ((BetaMemory) (wm.getNodeMemory(n4)));
        Assert.assertEquals(1, bm.getNodePosMaskBit());
        Assert.assertEquals(3, bm.getSegmentMemory().getAllLinkedMaskTest());
        Assert.assertEquals(2, bm.getSegmentMemory().getSegmentPosMaskBit());
        list = bm.getSegmentMemory().getPathMemories();
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains(rtn2Rs));
        Assert.assertTrue(list.contains(rtn3Rs));
        // n5
        bm = ((BetaMemory) (wm.getNodeMemory(n5)));
        Assert.assertEquals(2, bm.getNodePosMaskBit());
        Assert.assertEquals(3, bm.getSegmentMemory().getAllLinkedMaskTest());
        Assert.assertEquals(2, bm.getSegmentMemory().getSegmentPosMaskBit());
        list = bm.getSegmentMemory().getPathMemories();
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains(rtn2Rs));
        Assert.assertTrue(list.contains(rtn3Rs));
        // n6
        SegmentUtilities.createSegmentMemory(n6, wm);
        bm = ((BetaMemory) (wm.getNodeMemory(n6)));
        Assert.assertEquals(1, bm.getNodePosMaskBit());
        Assert.assertEquals(7, bm.getSegmentMemory().getAllLinkedMaskTest());
        Assert.assertEquals(4, bm.getSegmentMemory().getSegmentPosMaskBit());
        list = bm.getSegmentMemory().getPathMemories();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(rtn3Rs));
        // n7
        bm = ((BetaMemory) (wm.getNodeMemory(n7)));
        Assert.assertEquals(2, bm.getNodePosMaskBit());
        Assert.assertEquals(7, bm.getSegmentMemory().getAllLinkedMaskTest());
        Assert.assertEquals(4, bm.getSegmentMemory().getSegmentPosMaskBit());
        list = bm.getSegmentMemory().getPathMemories();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(rtn3Rs));
        // n8
        bm = ((BetaMemory) (wm.getNodeMemory(n8)));
        Assert.assertEquals(4, bm.getNodePosMaskBit());
        Assert.assertEquals(7, bm.getSegmentMemory().getAllLinkedMaskTest());
        Assert.assertEquals(4, bm.getSegmentMemory().getSegmentPosMaskBit());
        list = bm.getSegmentMemory().getPathMemories();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(rtn3Rs));
    }

    @Test
    public void testRuleSegmentLinking() {
        setUp(RuleUnlinkingWithSegmentMemoryTest.JOIN_NODE);
        InternalKnowledgeBase kBase = ((InternalKnowledgeBase) (KnowledgeBaseFactory.newKnowledgeBase()));
        StatefulKnowledgeSessionImpl wm = new StatefulKnowledgeSessionImpl(1L, kBase);
        BetaMemory bm = null;
        List<PathMemory> list;
        PathMemory rtn1Rs = ((PathMemory) (wm.getNodeMemory(rtn1)));
        PathMemory rtn2Rs = ((PathMemory) (wm.getNodeMemory(rtn2)));
        PathMemory rtn3Rs = ((PathMemory) (wm.getNodeMemory(rtn3)));
        DefaultFactHandle f1 = ((DefaultFactHandle) (wm.insert("test1")));
        lian.assertObject(f1, context, wm);
        n1.assertObject(f1, context, wm);
        n3.assertObject(f1, context, wm);
        n4.assertObject(f1, context, wm);
        n8.assertObject(f1, context, wm);
        Assert.assertFalse(rtn1Rs.isRuleLinked());
        Assert.assertFalse(rtn2Rs.isRuleLinked());
        Assert.assertFalse(rtn3Rs.isRuleLinked());
        // Link in Rule1
        bm = ((BetaMemory) (wm.getNodeMemory(n2)));
        Assert.assertFalse(bm.getSegmentMemory().isSegmentLinked());
        DefaultFactHandle f2 = ((DefaultFactHandle) (wm.insert("test2")));
        n2.assertObject(f2, context, wm);
        Assert.assertTrue(bm.getSegmentMemory().isSegmentLinked());
        Assert.assertTrue(rtn1Rs.isRuleLinked());
        Assert.assertFalse(rtn2Rs.isRuleLinked());
        Assert.assertFalse(rtn3Rs.isRuleLinked());
        // Link in Rule2
        bm = ((BetaMemory) (wm.getNodeMemory(n5)));
        Assert.assertFalse(bm.getSegmentMemory().isSegmentLinked());
        n5.assertObject(f1, context, wm);
        Assert.assertTrue(bm.getSegmentMemory().isSegmentLinked());
        Assert.assertTrue(rtn1Rs.isRuleLinked());
        Assert.assertTrue(rtn2Rs.isRuleLinked());
        Assert.assertFalse(rtn3Rs.isRuleLinked());
        // Link in Rule3
        n6.assertObject(f1, context, wm);
        n7.assertObject(f1, context, wm);
        Assert.assertTrue(bm.getSegmentMemory().isSegmentLinked());
        Assert.assertTrue(rtn1Rs.isRuleLinked());
        Assert.assertTrue(rtn2Rs.isRuleLinked());
        Assert.assertTrue(rtn3Rs.isRuleLinked());
        // retract n2, should unlink all rules
        n2.retractRightTuple(f2.getFirstRightTuple(), context, wm);
        Assert.assertFalse(rtn1Rs.isRuleLinked());
        Assert.assertFalse(rtn2Rs.isRuleLinked());
        Assert.assertFalse(rtn3Rs.isRuleLinked());
    }
}

