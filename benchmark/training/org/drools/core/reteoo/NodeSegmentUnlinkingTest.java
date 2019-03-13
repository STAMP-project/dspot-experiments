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


import org.drools.core.base.ClassObjectType;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.EmptyBetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.phreak.PhreakNotNode;
import org.drools.core.phreak.SegmentUtilities;
import org.drools.core.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.PropagationContext;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;


public class NodeSegmentUnlinkingTest {
    InternalKnowledgeBase kBase;

    BuildContext buildContext;

    PropagationContext context;

    LeftInputAdapterNode liaNode;

    BetaNode n1;

    BetaNode n2;

    BetaNode n3;

    BetaNode n4;

    BetaNode n5;

    BetaNode n6;

    BetaNode n7;

    BetaNode n8;

    RuleImpl rule1;

    RuleImpl rule2;

    RuleImpl rule3;

    RuleImpl rule4;

    RuleImpl rule5;

    static final int JOIN_NODE = 0;

    static final int EXISTS_NODE = 1;

    static final int NOT_NODE = 2;

    @Test
    public void testSingleNodeinSegment() {
        rule1 = new RuleImpl("rule1");
        rule2 = new RuleImpl("rule2");
        rule3 = new RuleImpl("rule3");
        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kBase = ((InternalKnowledgeBase) (KnowledgeBaseFactory.newKnowledgeBase(kconf)));
        BuildContext buildContext = new BuildContext(kBase);
        MockObjectSource mockObjectSource = new MockObjectSource(8);
        MockTupleSource mockTupleSource = new MockTupleSource(9);
        // n2 is only node in it's segment
        ObjectTypeNode otn = new ObjectTypeNode(2, null, new ClassObjectType(String.class), buildContext);
        BetaNode n1 = new JoinNode(10, new LeftInputAdapterNode(3, otn, buildContext), mockObjectSource, new EmptyBetaConstraints(), buildContext);
        BetaNode n2 = new JoinNode(11, n1, mockObjectSource, new EmptyBetaConstraints(), buildContext);
        BetaNode n3 = new JoinNode(12, n1, mockObjectSource, new EmptyBetaConstraints(), buildContext);
        BetaNode n4 = new JoinNode(13, n2, mockObjectSource, new EmptyBetaConstraints(), buildContext);
        BetaNode n5 = new JoinNode(14, n2, mockObjectSource, new EmptyBetaConstraints(), buildContext);
        n1.addAssociation(rule1);
        n1.addAssociation(rule2);
        n1.addAssociation(rule3);
        n2.addAssociation(rule2);
        n2.addAssociation(rule3);
        n3.addAssociation(rule1);
        n4.addAssociation(rule2);
        n5.addAssociation(rule3);
        mockObjectSource.attach(buildContext);
        mockTupleSource.attach(buildContext);
        n1.attach(buildContext);
        n2.attach(buildContext);
        n3.attach(buildContext);
        n4.attach(buildContext);
        n5.attach(buildContext);
        StatefulKnowledgeSessionImpl ksession = ((StatefulKnowledgeSessionImpl) (kBase.newKieSession()));
        NodeSegmentUnlinkingTest.createSegmentMemory(n2, ksession);
        BetaMemory bm = ((BetaMemory) (ksession.getNodeMemory(n1)));
        Assert.assertNull(bm.getSegmentMemory());
        bm = ((BetaMemory) (ksession.getNodeMemory(n3)));
        Assert.assertNull(bm.getSegmentMemory());
        bm = ((BetaMemory) (ksession.getNodeMemory(n4)));
        Assert.assertNull(bm.getSegmentMemory());
        bm = ((BetaMemory) (ksession.getNodeMemory(n2)));
        Assert.assertEquals(1, bm.getNodePosMaskBit());
        Assert.assertEquals(1, bm.getSegmentMemory().getAllLinkedMaskTest());
    }

    @Test
    public void testLiaNodeInitialisation() {
        setUp(NodeSegmentUnlinkingTest.JOIN_NODE);
        // Initialise from lian
        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        InternalKnowledgeBase kBase = ((InternalKnowledgeBase) (KnowledgeBaseFactory.newKnowledgeBase(kconf)));
        StatefulKnowledgeSessionImpl ksession = ((StatefulKnowledgeSessionImpl) (kBase.newKieSession()));
        SegmentUtilities.createSegmentMemory(liaNode, ksession);
        liaNode.assertObject(((InternalFactHandle) (ksession.insert("str"))), context, ksession);
        LiaNodeMemory liaMem = ((LiaNodeMemory) (ksession.getNodeMemory(liaNode)));
        Assert.assertEquals(1, liaMem.getNodePosMaskBit());
        Assert.assertEquals(3, liaMem.getSegmentMemory().getAllLinkedMaskTest());
        BetaMemory bm1 = ((BetaMemory) (ksession.getNodeMemory(n1)));
        Assert.assertEquals(2, bm1.getNodePosMaskBit());
        Assert.assertEquals(3, bm1.getSegmentMemory().getAllLinkedMaskTest());
        // Initialise from n1
        kBase = ((InternalKnowledgeBase) (KnowledgeBaseFactory.newKnowledgeBase(kconf)));
        ksession = ((StatefulKnowledgeSessionImpl) (kBase.newKieSession()));
        n1.assertObject(((InternalFactHandle) (ksession.insert("str"))), context, ksession);
        liaMem = ((LiaNodeMemory) (ksession.getNodeMemory(liaNode)));
        Assert.assertEquals(1, liaMem.getNodePosMaskBit());
        Assert.assertEquals(3, liaMem.getSegmentMemory().getAllLinkedMaskTest());
        bm1 = ((BetaMemory) (ksession.getNodeMemory(n1)));
        Assert.assertEquals(2, bm1.getNodePosMaskBit());
        Assert.assertEquals(3, bm1.getSegmentMemory().getAllLinkedMaskTest());
    }

    @Test
    public void testLiaNodeLinking() {
        setUp(NodeSegmentUnlinkingTest.JOIN_NODE);
        // Initialise from lian
        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        InternalKnowledgeBase kBase = ((InternalKnowledgeBase) (KnowledgeBaseFactory.newKnowledgeBase(kconf)));
        StatefulKnowledgeSessionImpl ksession = ((StatefulKnowledgeSessionImpl) (kBase.newKieSession()));
        SegmentUtilities.createSegmentMemory(liaNode, ksession);
        InternalFactHandle fh1 = ((InternalFactHandle) (ksession.insert("str1")));
        n1.assertObject(fh1, context, ksession);
        LiaNodeMemory liaMem = ((LiaNodeMemory) (ksession.getNodeMemory(liaNode)));
        Assert.assertEquals(1, liaMem.getNodePosMaskBit());
        Assert.assertEquals(3, liaMem.getSegmentMemory().getAllLinkedMaskTest());
        BetaMemory bm1 = ((BetaMemory) (ksession.getNodeMemory(n1)));
        Assert.assertEquals(2, bm1.getNodePosMaskBit());
        Assert.assertEquals(3, bm1.getSegmentMemory().getAllLinkedMaskTest());
        // still unlinked
        Assert.assertFalse(liaMem.getSegmentMemory().isSegmentLinked());
        // now linked
        InternalFactHandle fh2 = ((InternalFactHandle) (ksession.insert("str2")));
        liaNode.assertObject(fh2, context, ksession);
        Assert.assertTrue(liaMem.getSegmentMemory().isSegmentLinked());
        // test unlink after one retract
        liaNode.retractLeftTuple(fh2.getFirstLeftTuple(), context, ksession);
        Assert.assertFalse(liaMem.getSegmentMemory().isSegmentLinked());
        // check counter, after multiple asserts
        InternalFactHandle fh3 = ((InternalFactHandle) (ksession.insert("str3")));
        InternalFactHandle fh4 = ((InternalFactHandle) (ksession.insert("str4")));
        liaNode.assertObject(fh3, context, ksession);
        liaNode.assertObject(fh4, context, ksession);
        Assert.assertTrue(liaMem.getSegmentMemory().isSegmentLinked());
        liaNode.retractLeftTuple(fh3.getFirstLeftTuple(), context, ksession);
        Assert.assertTrue(liaMem.getSegmentMemory().isSegmentLinked());
        liaNode.retractLeftTuple(fh4.getFirstLeftTuple(), context, ksession);
        Assert.assertFalse(liaMem.getSegmentMemory().isSegmentLinked());
    }

    @Test
    public void tesMultiNodeSegmentDifferentInitialisationPoints() {
        setUp(NodeSegmentUnlinkingTest.JOIN_NODE);
        // Initialise from n3
        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        InternalKnowledgeBase kBase = ((InternalKnowledgeBase) (KnowledgeBaseFactory.newKnowledgeBase(kconf)));
        StatefulKnowledgeSessionImpl ksession = ((StatefulKnowledgeSessionImpl) (kBase.newKieSession()));
        NodeSegmentUnlinkingTest.createSegmentMemory(n3, ksession);
        BetaMemory bm = ((BetaMemory) (ksession.getNodeMemory(n1)));
        Assert.assertNull(bm.getSegmentMemory());
        bm = ((BetaMemory) (ksession.getNodeMemory(n3)));
        Assert.assertEquals(1, bm.getNodePosMaskBit());
        Assert.assertEquals(15, bm.getSegmentMemory().getAllLinkedMaskTest());
        bm = ((BetaMemory) (ksession.getNodeMemory(n4)));
        Assert.assertEquals(2, bm.getNodePosMaskBit());
        Assert.assertEquals(15, bm.getSegmentMemory().getAllLinkedMaskTest());
        bm = ((BetaMemory) (ksession.getNodeMemory(n5)));
        Assert.assertEquals(4, bm.getNodePosMaskBit());
        Assert.assertEquals(15, bm.getSegmentMemory().getAllLinkedMaskTest());
        bm = ((BetaMemory) (ksession.getNodeMemory(n6)));
        Assert.assertEquals(8, bm.getNodePosMaskBit());
        Assert.assertEquals(15, bm.getSegmentMemory().getAllLinkedMaskTest());
        // Initialise from n4
        kBase = ((InternalKnowledgeBase) (KnowledgeBaseFactory.newKnowledgeBase(kconf)));
        ksession = ((StatefulKnowledgeSessionImpl) (kBase.newKieSession()));
        bm = NodeSegmentUnlinkingTest.createSegmentMemory(n4, ksession);
        bm = ((BetaMemory) (ksession.getNodeMemory(n1)));
        Assert.assertNull(bm.getSegmentMemory());
        bm = ((BetaMemory) (ksession.getNodeMemory(n3)));
        Assert.assertEquals(1, bm.getNodePosMaskBit());
        Assert.assertEquals(15, bm.getSegmentMemory().getAllLinkedMaskTest());
        bm = ((BetaMemory) (ksession.getNodeMemory(n4)));
        Assert.assertEquals(2, bm.getNodePosMaskBit());
        Assert.assertEquals(15, bm.getSegmentMemory().getAllLinkedMaskTest());
        bm = ((BetaMemory) (ksession.getNodeMemory(n5)));
        Assert.assertEquals(4, bm.getNodePosMaskBit());
        Assert.assertEquals(15, bm.getSegmentMemory().getAllLinkedMaskTest());
        bm = ((BetaMemory) (ksession.getNodeMemory(n6)));
        Assert.assertEquals(8, bm.getNodePosMaskBit());
        Assert.assertEquals(15, bm.getSegmentMemory().getAllLinkedMaskTest());
        // Initialise from n5
        kBase = ((InternalKnowledgeBase) (KnowledgeBaseFactory.newKnowledgeBase(kconf)));
        ksession = ((StatefulKnowledgeSessionImpl) (kBase.newKieSession()));
        NodeSegmentUnlinkingTest.createSegmentMemory(n5, ksession);
        bm = ((BetaMemory) (ksession.getNodeMemory(n1)));
        Assert.assertNull(bm.getSegmentMemory());
        bm = ((BetaMemory) (ksession.getNodeMemory(n3)));
        Assert.assertEquals(1, bm.getNodePosMaskBit());
        Assert.assertEquals(15, bm.getSegmentMemory().getAllLinkedMaskTest());
        bm = ((BetaMemory) (ksession.getNodeMemory(n4)));
        Assert.assertEquals(2, bm.getNodePosMaskBit());
        Assert.assertEquals(15, bm.getSegmentMemory().getAllLinkedMaskTest());
        bm = ((BetaMemory) (ksession.getNodeMemory(n5)));
        Assert.assertEquals(4, bm.getNodePosMaskBit());
        Assert.assertEquals(15, bm.getSegmentMemory().getAllLinkedMaskTest());
        bm = ((BetaMemory) (ksession.getNodeMemory(n6)));
        Assert.assertEquals(8, bm.getNodePosMaskBit());
        Assert.assertEquals(15, bm.getSegmentMemory().getAllLinkedMaskTest());
        // Initialise from n6
        kBase = ((InternalKnowledgeBase) (KnowledgeBaseFactory.newKnowledgeBase(kconf)));
        ksession = ((StatefulKnowledgeSessionImpl) (kBase.newKieSession()));
        NodeSegmentUnlinkingTest.createSegmentMemory(n6, ksession);
        bm = ((BetaMemory) (ksession.getNodeMemory(n1)));
        Assert.assertNull(bm.getSegmentMemory());
        bm = ((BetaMemory) (ksession.getNodeMemory(n3)));
        Assert.assertEquals(1, bm.getNodePosMaskBit());
        Assert.assertEquals(15, bm.getSegmentMemory().getAllLinkedMaskTest());
        bm = ((BetaMemory) (ksession.getNodeMemory(n4)));
        Assert.assertEquals(2, bm.getNodePosMaskBit());
        Assert.assertEquals(15, bm.getSegmentMemory().getAllLinkedMaskTest());
        bm = ((BetaMemory) (ksession.getNodeMemory(n5)));
        Assert.assertEquals(4, bm.getNodePosMaskBit());
        Assert.assertEquals(15, bm.getSegmentMemory().getAllLinkedMaskTest());
        bm = ((BetaMemory) (ksession.getNodeMemory(n6)));
        Assert.assertEquals(8, bm.getNodePosMaskBit());
        Assert.assertEquals(15, bm.getSegmentMemory().getAllLinkedMaskTest());
    }

    @Test
    public void testAllLinkedInWithJoinNodesOnly() {
        setUp(NodeSegmentUnlinkingTest.JOIN_NODE);
        Assert.assertEquals(JoinNode.class, n3.getClass());// make sure it created JoinNodes

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        InternalKnowledgeBase kBase = ((InternalKnowledgeBase) (KnowledgeBaseFactory.newKnowledgeBase(kconf)));
        StatefulKnowledgeSessionImpl ksession = ((StatefulKnowledgeSessionImpl) (kBase.newKieSession()));
        DefaultFactHandle f1 = ((DefaultFactHandle) (ksession.insert("test1")));
        n3.assertObject(f1, context, ksession);
        BetaMemory bm = ((BetaMemory) (ksession.getNodeMemory(n3)));
        Assert.assertFalse(bm.getSegmentMemory().isSegmentLinked());
        n4.assertObject(f1, context, ksession);
        Assert.assertFalse(bm.getSegmentMemory().isSegmentLinked());
        n5.assertObject(f1, context, ksession);
        Assert.assertFalse(bm.getSegmentMemory().isSegmentLinked());
        n6.assertObject(f1, context, ksession);
        Assert.assertTrue(bm.getSegmentMemory().isSegmentLinked());// only after all 4 nodes are populated, is the segment linked in

    }

    @Test
    public void testAllLinkedInWithExistsNodesOnly() {
        setUp(NodeSegmentUnlinkingTest.EXISTS_NODE);
        Assert.assertEquals(ExistsNode.class, n3.getClass());// make sure it created ExistsNodes

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        InternalKnowledgeBase kBase = ((InternalKnowledgeBase) (KnowledgeBaseFactory.newKnowledgeBase(kconf)));
        StatefulKnowledgeSessionImpl ksession = ((StatefulKnowledgeSessionImpl) (kBase.newKieSession()));
        DefaultFactHandle f1 = ((DefaultFactHandle) (ksession.insert("test1")));
        n3.assertObject(f1, context, ksession);
        BetaMemory bm = ((BetaMemory) (ksession.getNodeMemory(n3)));
        Assert.assertFalse(bm.getSegmentMemory().isSegmentLinked());
        n4.assertObject(f1, context, ksession);
        Assert.assertFalse(bm.getSegmentMemory().isSegmentLinked());
        n5.assertObject(f1, context, ksession);
        Assert.assertFalse(bm.getSegmentMemory().isSegmentLinked());
        n6.assertObject(f1, context, ksession);
        Assert.assertTrue(bm.getSegmentMemory().isSegmentLinked());// only after all 4 nodes are populated, is the segment linked in

    }

    @Test
    public void testAllLinkedInWithNotNodesOnly() {
        setUp(NodeSegmentUnlinkingTest.NOT_NODE);
        Assert.assertEquals(NotNode.class, n3.getClass());// make sure it created NotNodes

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        InternalKnowledgeBase kBase = ((InternalKnowledgeBase) (KnowledgeBaseFactory.newKnowledgeBase(kconf)));
        StatefulKnowledgeSessionImpl ksession = ((StatefulKnowledgeSessionImpl) (kBase.newKieSession()));
        BetaMemory bm = ((BetaMemory) (ksession.getNodeMemory(n3)));
        NodeSegmentUnlinkingTest.createSegmentMemory(n3, ksession);
        Assert.assertTrue(bm.getSegmentMemory().isSegmentLinked());// not nodes start off linked

        DefaultFactHandle f1 = ((DefaultFactHandle) (ksession.insert("test1")));// unlinked after first assertion

        n3.assertObject(f1, context, ksession);
        // this doesn't unlink on the assertObject, as the node's memory must be processed. So use the helper method the main network evaluator uses.
        PhreakNotNode.unlinkNotNodeOnRightInsert(((NotNode) (n3)), bm, ksession);
        Assert.assertFalse(bm.getSegmentMemory().isSegmentLinked());
        n3.retractRightTuple(f1.getFirstRightTuple(), context, ksession);
        Assert.assertTrue(bm.getSegmentMemory().isSegmentLinked());
        // assertFalse( bm.getSegmentMemory().isSigmentLinked() ); // check retraction unlinks again
    }
}

