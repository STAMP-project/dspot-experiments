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
package org.drools.compiler.integrationtests;


import ResourceType.DRL;
import RightInputAdapterNode.RiaNodeMemory;
import java.util.ArrayList;
import java.util.List;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.phreak.RuleExecutor;
import org.drools.core.phreak.SegmentUtilities;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.EvalConditionNode;
import org.drools.core.reteoo.ExistsNode;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.core.reteoo.NotNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.RightInputAdapterNode;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.SegmentMemory;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;


public class LinkingTest {
    public static class A {
        private int value;

        public A() {
        }

        public A(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static class B {
        private int value;

        public B() {
        }

        public B(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static class C {
        private int value;

        public C() {
        }

        public C(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static class D {
        private int value;

        public D() {
        }

        public D(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static class E {
        private int value;

        public E() {
        }

        public E(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static class F {
        private int value;

        public F() {
        }

        public F(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static class G {
        private int value;

        public G() {
        }

        public G(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    @Test
    public void testSubNetworkSharing() throws Exception {
        // Checks the network is correctly formed, with sharing
        String str = "";
        str += "package org.kie \n";
        str += ("import " + (LinkingTest.A.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.B.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.C.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.D.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.E.class.getCanonicalName())) + "\n";
        str += "global java.util.List list \n";
        str += "rule rule1 when \n";
        str += "   A() \n";
        str += "   B() \n";
        str += "   C() \n";
        str += "   D() \n";
        str += "then \n";
        str += "end \n";
        str += "rule rule2 when \n";
        str += "   A() \n";
        str += "   exists( B() and C() ) \n";
        str += "   D() \n";
        str += "then \n";
        str += "end \n";
        str += "rule rule3 when \n";
        str += "   A() \n";
        str += "   exists( B() and C() and D() ) \n";
        str += "   E() \n";
        str += "then \n";
        str += "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        ObjectTypeNode node = LinkingTest.getObjectTypeNode(kbase, LinkingTest.A.class);
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl) (kbase.newKieSession()));
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (node.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(3, liaNode.getSinkPropagator().size());
        ExistsNode existsNode2 = ((ExistsNode) (liaNode.getSinkPropagator().getSinks()[1]));
        ExistsNode existsNode3 = ((ExistsNode) (liaNode.getSinkPropagator().getSinks()[2]));
        JoinNode joinNodeB = ((JoinNode) (liaNode.getSinkPropagator().getSinks()[0]));
        Assert.assertSame(joinNodeB.getRightInput(), LinkingTest.getObjectTypeNode(kbase, LinkingTest.B.class));
        JoinNode joinNodeC = ((JoinNode) (joinNodeB.getSinkPropagator().getSinks()[0]));
        Assert.assertSame(joinNodeC.getRightInput(), LinkingTest.getObjectTypeNode(kbase, LinkingTest.C.class));
        Assert.assertEquals(2, joinNodeC.getSinkPropagator().size());
        JoinNode joinNodeD = ((JoinNode) (joinNodeC.getSinkPropagator().getSinks()[0]));
        Assert.assertSame(joinNodeD.getRightInput(), LinkingTest.getObjectTypeNode(kbase, LinkingTest.D.class));
        Assert.assertEquals(2, joinNodeD.getSinkPropagator().size());
        Assert.assertSame(existsNode2, getObjectSinkPropagator().getSinks()[0]);
        Assert.assertSame(existsNode3, getObjectSinkPropagator().getSinks()[0]);
    }

    @Test
    public void testSubNetworkSharingMemories() throws Exception {
        // checks the memory sharing works, and linking, uses the already checked network from testSubNetworkSharing
        String str = "";
        str += "package org.kie \n";
        str += ("import " + (LinkingTest.A.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.B.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.C.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.D.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.E.class.getCanonicalName())) + "\n";
        str += "global java.util.List list \n";
        str += "rule rule1 when \n";
        str += "   A() \n";
        str += "   B() \n";
        str += "   C() \n";
        str += "   D() \n";
        str += "then \n";
        str += "end \n";
        str += "rule rule2 when \n";
        str += "   A() \n";
        str += "   exists( B() and C() ) \n";
        str += "   D() \n";
        str += "then \n";
        str += "end \n";
        str += "rule rule3 when \n";
        str += "   A() \n";
        str += "   exists( B() and C() and D() ) \n";
        str += "   E() \n";
        str += "then \n";
        str += "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        ObjectTypeNode node = LinkingTest.getObjectTypeNode(kbase, LinkingTest.A.class);
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl) (kbase.newKieSession()));
        LeftInputAdapterNode liaNodeA = ((LeftInputAdapterNode) (node.getObjectSinkPropagator().getSinks()[0]));
        ExistsNode existsNode2 = ((ExistsNode) (liaNodeA.getSinkPropagator().getSinks()[1]));
        ExistsNode existsNode3 = ((ExistsNode) (liaNodeA.getSinkPropagator().getSinks()[2]));
        JoinNode joinNodeB = ((JoinNode) (liaNodeA.getSinkPropagator().getSinks()[0]));
        JoinNode joinNodeC = ((JoinNode) (joinNodeB.getSinkPropagator().getSinks()[0]));
        JoinNode joinNodeD1 = ((JoinNode) (joinNodeC.getSinkPropagator().getSinks()[0]));
        JoinNode joinNodeD2 = ((JoinNode) (existsNode2.getSinkPropagator().getSinks()[0]));
        JoinNode joinNodeE = ((JoinNode) (existsNode3.getSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtn1 = ((RuleTerminalNode) (joinNodeD1.getSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtn2 = ((RuleTerminalNode) (joinNodeD2.getSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtn3 = ((RuleTerminalNode) (joinNodeE.getSinkPropagator().getSinks()[0]));
        FactHandle fha = wm.insert(new LinkingTest.A());
        wm.insert(new LinkingTest.B());
        wm.insert(new LinkingTest.C());
        wm.insert(new LinkingTest.D());
        wm.flushPropagations();
        BetaMemory bm = null;
        LiaNodeMemory liam = ((LiaNodeMemory) (wm.getNodeMemory(liaNodeA)));
        BetaMemory bm1 = ((BetaMemory) (wm.getNodeMemory(joinNodeB)));
        BetaMemory bm2 = ((BetaMemory) (wm.getNodeMemory(joinNodeC)));
        BetaMemory bm3 = ((BetaMemory) (wm.getNodeMemory(joinNodeD1)));
        Assert.assertEquals(1, liam.getNodePosMaskBit());
        Assert.assertEquals(1, bm1.getNodePosMaskBit());
        Assert.assertEquals(2, bm2.getNodePosMaskBit());
        Assert.assertEquals(1, bm3.getNodePosMaskBit());
        Assert.assertNotSame(liam.getSegmentMemory(), bm1.getSegmentMemory());
        Assert.assertSame(bm1.getSegmentMemory(), bm2.getSegmentMemory());
        Assert.assertNotSame(bm2.getSegmentMemory(), bm3.getSegmentMemory());
        BetaMemory bm4 = ((BetaMemory) (wm.getNodeMemory(existsNode2)));
        BetaMemory bm5 = ((BetaMemory) (wm.getNodeMemory(joinNodeD2)));
        Assert.assertEquals(1, bm4.getNodePosMaskBit());
        Assert.assertEquals(2, bm5.getNodePosMaskBit());
        Assert.assertSame(bm4.getSegmentMemory(), bm5.getSegmentMemory());
        PathMemory rs1 = ((PathMemory) (wm.getNodeMemory(rtn1)));
        PathMemory rs2 = ((PathMemory) (wm.getNodeMemory(rtn2)));
        PathMemory rs3 = ((PathMemory) (wm.getNodeMemory(rtn3)));
        Assert.assertTrue(rs1.isRuleLinked());
        Assert.assertTrue(rs2.isRuleLinked());
        Assert.assertFalse(rs3.isRuleLinked());// no E yet

        wm.insert(new LinkingTest.E());
        wm.flushPropagations();
        BetaMemory bm6 = ((BetaMemory) (wm.getNodeMemory(existsNode3)));
        BetaMemory bm7 = ((BetaMemory) (wm.getNodeMemory(joinNodeE)));
        Assert.assertEquals(1, bm6.getNodePosMaskBit());
        Assert.assertEquals(2, bm7.getNodePosMaskBit());
        Assert.assertSame(bm6.getSegmentMemory(), bm7.getSegmentMemory());
        Assert.assertTrue(rs1.isRuleLinked());
        Assert.assertTrue(rs2.isRuleLinked());
        Assert.assertTrue(rs3.isRuleLinked());
        wm.retract(fha);
        wm.fireAllRules();// need to have rules evalulated, for unlinking to happen

        Assert.assertFalse(rs1.isRuleLinked());
        Assert.assertFalse(rs2.isRuleLinked());
        Assert.assertFalse(rs3.isRuleLinked());
    }

    @Test
    public void testSubNetworkRiaLinking() throws Exception {
        String str = "";
        str += "package org.kie \n";
        str += ("import " + (LinkingTest.A.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.B.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.C.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.D.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.E.class.getCanonicalName())) + "\n";
        str += "global java.util.List list \n";
        str += "rule rule1 when \n";
        str += "   A() \n";
        str += "   B() \n";
        str += "   exists( C() and D() ) \n";
        str += "   E() \n";
        str += "then \n";
        str += "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        ObjectTypeNode node = LinkingTest.getObjectTypeNode(kbase, LinkingTest.A.class);
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl) (kbase.newKieSession()));
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (node.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(1, liaNode.getSinkPropagator().size());
        JoinNode bNode = ((JoinNode) (liaNode.getSinkPropagator().getSinks()[0]));
        Assert.assertEquals(2, bNode.getSinkPropagator().size());
        ExistsNode exists1n = ((ExistsNode) (bNode.getSinkPropagator().getSinks()[1]));
        JoinNode cNode = ((JoinNode) (bNode.getSinkPropagator().getSinks()[0]));
        JoinNode dNode = ((JoinNode) (cNode.getSinkPropagator().getSinks()[0]));
        Assert.assertEquals(1, dNode.getSinkPropagator().size());
        RightInputAdapterNode riaNode1 = ((RightInputAdapterNode) (dNode.getSinkPropagator().getSinks()[0]));
        JoinNode eNode = ((JoinNode) (exists1n.getSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtn = ((RuleTerminalNode) (eNode.getSinkPropagator().getSinks()[0]));
        SegmentUtilities.createSegmentMemory(exists1n, wm);
        BetaMemory existsBm = ((BetaMemory) (wm.getNodeMemory(exists1n)));
        Assert.assertEquals(0, existsBm.getSegmentMemory().getLinkedNodeMask());
        FactHandle fhc = wm.insert(new LinkingTest.C());
        FactHandle fhd = wm.insert(new LinkingTest.D());
        wm.flushPropagations();
        Assert.assertEquals(1, existsBm.getSegmentMemory().getLinkedNodeMask());// exists is start of new segment

        wm.retract(fhd);
        wm.flushPropagations();
        Assert.assertEquals(0, existsBm.getSegmentMemory().getLinkedNodeMask());
        PathMemory rs = ((PathMemory) (wm.getNodeMemory(rtn)));
        Assert.assertFalse(rs.isRuleLinked());
        wm.insert(new LinkingTest.A());
        wm.flushPropagations();
        Assert.assertFalse(rs.isRuleLinked());
        wm.insert(new LinkingTest.B());
        wm.flushPropagations();
        Assert.assertFalse(rs.isRuleLinked());
        wm.insert(new LinkingTest.E());
        wm.flushPropagations();
        Assert.assertFalse(rs.isRuleLinked());
        wm.insert(new LinkingTest.D());
        wm.flushPropagations();
        Assert.assertTrue(rs.isRuleLinked());
        wm.retract(fhc);
        wm.flushPropagations();
        Assert.assertFalse(rs.isRuleLinked());
        wm.insert(new LinkingTest.C());
        wm.flushPropagations();
        Assert.assertTrue(rs.isRuleLinked());
    }

    @Test
    public void testNonReactiveSubNetworkInShareMasks() throws Exception {
        String str = "";
        str += "package org.kie \n";
        str += ("import " + (LinkingTest.A.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.B.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.C.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.D.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.E.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.F.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.G.class.getCanonicalName())) + "\n";
        str += "global java.util.List list \n";
        str += "rule rule1 when \n";
        str += "   A() \n";
        str += "   exists( B() and C() ) \n";
        str += "   exists( eval(1==1) ) \n";
        str += "   D() \n";
        str += "then \n";
        str += "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        ObjectTypeNode node = LinkingTest.getObjectTypeNode(kbase, LinkingTest.A.class);
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl) (kbase.newKieSession()));
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (node.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(2, liaNode.getSinkPropagator().size());
        JoinNode bNode = ((JoinNode) (liaNode.getSinkPropagator().getSinks()[0]));
        JoinNode cNode = ((JoinNode) (bNode.getSinkPropagator().getSinks()[0]));
        ExistsNode exists1n = ((ExistsNode) (liaNode.getSinkPropagator().getSinks()[1]));
        EvalConditionNode evalNode = ((EvalConditionNode) (exists1n.getSinkPropagator().getSinks()[0]));
        ExistsNode exists2n = ((ExistsNode) (exists1n.getSinkPropagator().getSinks()[1]));
        JoinNode dNode = ((JoinNode) (exists2n.getSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtn = ((RuleTerminalNode) (dNode.getSinkPropagator().getSinks()[0]));
        wm.insert(new LinkingTest.A());
        PathMemory pmem = ((PathMemory) (wm.getNodeMemory(rtn)));
        Assert.assertEquals(3, pmem.getSegmentMemories().length);
        Assert.assertEquals(7, pmem.getAllLinkedMaskTest());// D is in the exists segment

        BetaMemory bm = ((BetaMemory) (wm.getNodeMemory(dNode)));
        Assert.assertNull(bm.getSegmentMemory());// check lazy initialization

        wm.insert(new LinkingTest.D());
        wm.flushPropagations();
        Assert.assertEquals(2, bm.getSegmentMemory().getAllLinkedMaskTest());// only D can be linked in

    }

    @Test
    public void testNonReactiveSubNetworkOwnSegmentMasks() throws Exception {
        String str = "";
        str += "package org.kie \n";
        str += ("import " + (LinkingTest.A.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.B.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.C.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.D.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.E.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.F.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.G.class.getCanonicalName())) + "\n";
        str += "global java.util.List list \n";
        str += "rule rule1 when \n";
        str += "   A() \n";
        str += "   exists( B() and C() ) \n";
        str += "   exists( eval(1==1) ) \n";
        str += "   D() \n";
        str += "then \n";
        str += "end \n";
        str += "rule rule2 when \n";
        str += "   A() \n";
        str += "   exists( B() and C() ) \n";
        str += "   exists( eval(1==1) ) \n";
        str += "   E() \n";
        str += "then \n";
        str += "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        ObjectTypeNode node = LinkingTest.getObjectTypeNode(kbase, LinkingTest.A.class);
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl) (kbase.newKieSession()));
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (node.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(2, liaNode.getSinkPropagator().size());
        JoinNode bNode = ((JoinNode) (liaNode.getSinkPropagator().getSinks()[0]));
        JoinNode cNode = ((JoinNode) (bNode.getSinkPropagator().getSinks()[0]));
        ExistsNode exists1n = ((ExistsNode) (liaNode.getSinkPropagator().getSinks()[1]));
        EvalConditionNode evalNode = ((EvalConditionNode) (exists1n.getSinkPropagator().getSinks()[0]));
        ExistsNode exists2n = ((ExistsNode) (exists1n.getSinkPropagator().getSinks()[1]));
        JoinNode dNode = ((JoinNode) (exists2n.getSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtn = ((RuleTerminalNode) (dNode.getSinkPropagator().getSinks()[0]));
        wm.insert(new LinkingTest.A());
        PathMemory pmem = ((PathMemory) (wm.getNodeMemory(rtn)));
        Assert.assertEquals(4, pmem.getSegmentMemories().length);
        Assert.assertEquals(11, pmem.getAllLinkedMaskTest());// the exists eval segment does not need to be linked in

        RightInputAdapterNode.RiaNodeMemory riaMem = ((RightInputAdapterNode.RiaNodeMemory) (wm.getNodeMemory(((MemoryFactory) (exists1n.getRightInput())))));
        Assert.assertEquals(2, riaMem.getRiaPathMemory().getAllLinkedMaskTest());// second segment must be linked in

        wm.insert(new LinkingTest.B());
        wm.insert(new LinkingTest.C());
        Assert.assertEquals(2, riaMem.getRiaPathMemory().getSegmentMemories().length);
        riaMem = ((RightInputAdapterNode.RiaNodeMemory) (wm.getNodeMemory(((MemoryFactory) (exists2n.getRightInput())))));
        Assert.assertEquals(0, riaMem.getRiaPathMemory().getAllLinkedMaskTest());// no segments to be linked in

    }

    @Test
    public void testNestedSubNetwork() throws Exception {
        String str = "";
        str += "package org.kie \n";
        str += ("import " + (LinkingTest.A.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.B.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.C.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.D.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.E.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.F.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.G.class.getCanonicalName())) + "\n";
        str += "global java.util.List list \n";
        str += "rule rule1 when \n";
        str += "   A() \n";
        str += "   B() \n";
        str += "   exists( C() and D() and exists( E() and F() ) ) \n";
        str += "   G() \n";
        str += "then \n";
        str += "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        ObjectTypeNode node = LinkingTest.getObjectTypeNode(kbase, LinkingTest.A.class);
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl) (kbase.newKieSession()));
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (node.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(1, liaNode.getSinkPropagator().size());
        JoinNode bNode = ((JoinNode) (liaNode.getSinkPropagator().getSinks()[0]));
        Assert.assertEquals(2, bNode.getSinkPropagator().size());
        ExistsNode exists1n = ((ExistsNode) (bNode.getSinkPropagator().getSinks()[1]));
        JoinNode cNode = ((JoinNode) (bNode.getSinkPropagator().getSinks()[0]));
        JoinNode dNode = ((JoinNode) (cNode.getSinkPropagator().getSinks()[0]));
        Assert.assertEquals(2, dNode.getSinkPropagator().size());
        ExistsNode exists2n = ((ExistsNode) (dNode.getSinkPropagator().getSinks()[1]));
        JoinNode eNode = ((JoinNode) (dNode.getSinkPropagator().getSinks()[0]));
        JoinNode fNode = ((JoinNode) (eNode.getSinkPropagator().getSinks()[0]));
        RightInputAdapterNode riaNode2 = ((RightInputAdapterNode) (fNode.getSinkPropagator().getSinks()[0]));
        Assert.assertEquals(exists2n, riaNode2.getObjectSinkPropagator().getSinks()[0]);
        RightInputAdapterNode riaNode1 = ((RightInputAdapterNode) (exists2n.getSinkPropagator().getSinks()[0]));
        Assert.assertEquals(exists1n, riaNode1.getObjectSinkPropagator().getSinks()[0]);
        JoinNode gNode = ((JoinNode) (exists1n.getSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtn = ((RuleTerminalNode) (gNode.getSinkPropagator().getSinks()[0]));
        wm.insert(new LinkingTest.A());
        wm.insert(new LinkingTest.B());
        wm.insert(new LinkingTest.C());
        wm.insert(new LinkingTest.D());
        wm.insert(new LinkingTest.F());
        wm.insert(new LinkingTest.G());
        PathMemory rs = ((PathMemory) (wm.getNodeMemory(rtn)));
        Assert.assertFalse(rs.isRuleLinked());
        FactHandle fhE1 = wm.insert(new LinkingTest.E());
        FactHandle fhE2 = wm.insert(new LinkingTest.E());
        wm.flushPropagations();
        Assert.assertTrue(rs.isRuleLinked());
        wm.retract(fhE1);
        wm.flushPropagations();
        Assert.assertTrue(rs.isRuleLinked());
        wm.retract(fhE2);
        wm.flushPropagations();
        Assert.assertFalse(rs.isRuleLinked());
    }

    @Test
    public void testNestedSubNetworkMasks() throws Exception {
        String str = "";
        str += "package org.kie \n";
        str += ("import " + (LinkingTest.A.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.B.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.C.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.D.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.E.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.F.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.G.class.getCanonicalName())) + "\n";
        str += "global java.util.List list \n";
        str += "rule rule1 when \n";
        str += "   A() \n";
        str += "   B() \n";
        str += "   exists( C() and D() and exists( E() and F() ) ) \n";
        str += "   G() \n";
        str += "then \n";
        str += "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        ObjectTypeNode node = LinkingTest.getObjectTypeNode(kbase, LinkingTest.A.class);
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl) (kbase.newKieSession()));
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (node.getObjectSinkPropagator().getSinks()[0]));
        JoinNode bNode = ((JoinNode) (liaNode.getSinkPropagator().getSinks()[0]));
        ExistsNode exists1n = ((ExistsNode) (bNode.getSinkPropagator().getSinks()[1]));
        JoinNode cNode = ((JoinNode) (bNode.getSinkPropagator().getSinks()[0]));
        JoinNode dNode = ((JoinNode) (cNode.getSinkPropagator().getSinks()[0]));
        ExistsNode exists2n = ((ExistsNode) (dNode.getSinkPropagator().getSinks()[1]));
        JoinNode eNode = ((JoinNode) (dNode.getSinkPropagator().getSinks()[0]));
        JoinNode fNode = ((JoinNode) (eNode.getSinkPropagator().getSinks()[0]));
        RightInputAdapterNode riaNode2 = ((RightInputAdapterNode) (fNode.getSinkPropagator().getSinks()[0]));
        RightInputAdapterNode riaNode1 = ((RightInputAdapterNode) (exists2n.getSinkPropagator().getSinks()[0]));
        JoinNode gNode = ((JoinNode) (exists1n.getSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtn = ((RuleTerminalNode) (gNode.getSinkPropagator().getSinks()[0]));
        wm.insert(new LinkingTest.A());
        wm.insert(new LinkingTest.B());
        wm.insert(new LinkingTest.C());
        wm.insert(new LinkingTest.D());
        wm.insert(new LinkingTest.G());
        wm.flushPropagations();
        LiaNodeMemory liaMem = ((LiaNodeMemory) (wm.getNodeMemory(liaNode)));
        BetaMemory bMem = ((BetaMemory) (wm.getNodeMemory(bNode)));
        BetaMemory exists1Mem = ((BetaMemory) (wm.getNodeMemory(exists1n)));
        BetaMemory cMem = ((BetaMemory) (wm.getNodeMemory(cNode)));
        BetaMemory dMem = ((BetaMemory) (wm.getNodeMemory(dNode)));
        BetaMemory exists2Mem = ((BetaMemory) (wm.getNodeMemory(exists2n)));
        BetaMemory eMem = ((BetaMemory) (wm.getNodeMemory(eNode)));
        BetaMemory fMem = ((BetaMemory) (wm.getNodeMemory(fNode)));
        BetaMemory gMem = ((BetaMemory) (wm.getNodeMemory(gNode)));
        RightInputAdapterNode.RiaNodeMemory riaMem1 = ((RightInputAdapterNode.RiaNodeMemory) (wm.getNodeMemory(riaNode1)));
        RightInputAdapterNode.RiaNodeMemory riaMem2 = ((RightInputAdapterNode.RiaNodeMemory) (wm.getNodeMemory(riaNode2)));
        PathMemory rs = ((PathMemory) (wm.getNodeMemory(rtn)));
        Assert.assertFalse(rs.isRuleLinked());// E and F are not inserted yet, so rule is unlinked

        // ---
        // assert a and b in same segment
        Assert.assertSame(liaMem.getSegmentMemory(), bMem.getSegmentMemory());
        // exists1 and b not in same segment
        Assert.assertNotSame(bMem.getSegmentMemory(), exists1Mem.getSegmentMemory());
        // exists1 and b are in same segment
        Assert.assertSame(exists1Mem.getSegmentMemory(), gMem.getSegmentMemory());
        // check segment masks
        Assert.assertEquals(2, rs.getSegmentMemories().length);
        Assert.assertEquals(3, rs.getAllLinkedMaskTest());
        Assert.assertEquals(1, rs.getLinkedSegmentMask());
        Assert.assertEquals(3, liaMem.getSegmentMemory().getAllLinkedMaskTest());
        Assert.assertEquals(1, liaMem.getNodePosMaskBit());
        Assert.assertEquals(2, bMem.getNodePosMaskBit());
        Assert.assertEquals(3, exists1Mem.getSegmentMemory().getAllLinkedMaskTest());
        Assert.assertEquals(1, exists1Mem.getNodePosMaskBit());
        Assert.assertEquals(2, gMem.getNodePosMaskBit());
        // assert c, d are in the same segment, and that this is the only segment in ria1 memory
        Assert.assertSame(dMem.getSegmentMemory(), cMem.getSegmentMemory());
        // assert d and exists are not in the same segment
        Assert.assertNotSame(exists2Mem.getSegmentMemory(), dMem.getSegmentMemory());
        Assert.assertEquals(3, riaMem1.getRiaPathMemory().getSegmentMemories().length);
        Assert.assertEquals(null, riaMem1.getRiaPathMemory().getSegmentMemories()[0]);// only needs to know about segments in the subnetwork

        Assert.assertEquals(dMem.getSegmentMemory(), riaMem1.getRiaPathMemory().getSegmentMemories()[1]);
        Assert.assertEquals(1, dMem.getSegmentMemory().getPathMemories().size());
        Assert.assertSame(riaMem1.getRiaPathMemory(), cMem.getSegmentMemory().getPathMemories().get(0));
        Assert.assertEquals(3, cMem.getSegmentMemory().getAllLinkedMaskTest());
        Assert.assertEquals(3, cMem.getSegmentMemory().getLinkedNodeMask());// E and F is not yet inserted, so bit is not set

        Assert.assertEquals(1, cMem.getNodePosMaskBit());
        Assert.assertEquals(2, dMem.getNodePosMaskBit());
        Assert.assertEquals(0, exists2Mem.getNodePosMaskBit());
        FactHandle fhE1 = wm.insert(new LinkingTest.E());// insert to lazy initialize exists2Mem segment

        FactHandle fhF1 = wm.insert(new LinkingTest.F());
        wm.flushPropagations();
        Assert.assertEquals(1, exists2Mem.getNodePosMaskBit());
        Assert.assertEquals(6, riaMem1.getRiaPathMemory().getAllLinkedMaskTest());// only cares that the segment for c, E and exists1 are set, ignores the outer first segment

        Assert.assertEquals(6, riaMem1.getRiaPathMemory().getLinkedSegmentMask());// E and F are inerted, so 6

        wm.delete(fhE1);
        wm.delete(fhF1);
        wm.flushPropagations();
        Assert.assertEquals(2, riaMem1.getRiaPathMemory().getLinkedSegmentMask());// E deleted

        // assert e, f are in the same segment, and that this is the only segment in ria2 memory
        Assert.assertNotNull(null, eMem.getSegmentMemory());// subnetworks are recursively created, so segment already exists

        Assert.assertSame(fMem.getSegmentMemory(), eMem.getSegmentMemory());
        Assert.assertEquals(3, riaMem2.getRiaPathMemory().getSegmentMemories().length);
        Assert.assertEquals(null, riaMem2.getRiaPathMemory().getSegmentMemories()[0]);// only needs to know about segments in the subnetwork

        Assert.assertEquals(null, riaMem2.getRiaPathMemory().getSegmentMemories()[1]);// only needs to know about segments in the subnetwork

        Assert.assertEquals(fMem.getSegmentMemory(), riaMem2.getRiaPathMemory().getSegmentMemories()[2]);
        Assert.assertSame(riaMem2.getRiaPathMemory(), eMem.getSegmentMemory().getPathMemories().get(0));
        Assert.assertEquals(3, eMem.getSegmentMemory().getAllLinkedMaskTest());
        Assert.assertEquals(0, eMem.getSegmentMemory().getLinkedNodeMask());
        Assert.assertEquals(4, riaMem2.getRiaPathMemory().getAllLinkedMaskTest());// only cares that the segment for e and f set, ignores the outer two segment

        Assert.assertEquals(0, riaMem2.getRiaPathMemory().getLinkedSegmentMask());// E and F is not yet inserted, so bit is not set

        fhE1 = wm.insert(new LinkingTest.E());
        wm.insert(new LinkingTest.F());
        wm.flushPropagations();
        Assert.assertTrue(rs.isRuleLinked());// E and F are now inserted yet, so rule is linked

        Assert.assertEquals(3, rs.getAllLinkedMaskTest());
        Assert.assertEquals(3, rs.getLinkedSegmentMask());
        // retest bits
        Assert.assertEquals(3, cMem.getSegmentMemory().getAllLinkedMaskTest());
        Assert.assertEquals(3, cMem.getSegmentMemory().getLinkedNodeMask());
        Assert.assertEquals(6, riaMem1.getRiaPathMemory().getAllLinkedMaskTest());
        Assert.assertEquals(6, riaMem1.getRiaPathMemory().getLinkedSegmentMask());
        Assert.assertEquals(3, eMem.getSegmentMemory().getAllLinkedMaskTest());
        Assert.assertEquals(3, eMem.getSegmentMemory().getLinkedNodeMask());
        Assert.assertEquals(4, riaMem2.getRiaPathMemory().getAllLinkedMaskTest());
        Assert.assertEquals(4, riaMem2.getRiaPathMemory().getLinkedSegmentMask());
        wm.delete(fhE1);
        wm.flushPropagations();
        // retest bits
        Assert.assertFalse(rs.isRuleLinked());
        Assert.assertEquals(3, cMem.getSegmentMemory().getLinkedNodeMask());
        Assert.assertEquals(2, riaMem1.getRiaPathMemory().getLinkedSegmentMask());
        Assert.assertEquals(2, eMem.getSegmentMemory().getLinkedNodeMask());
        Assert.assertEquals(0, riaMem2.getRiaPathMemory().getLinkedSegmentMask());
    }

    @Test
    public void testJoinNodes() throws Exception {
        String str = "";
        str += "package org.kie \n";
        str += ("import " + (LinkingTest.A.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.B.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.C.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.D.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.E.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.F.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.G.class.getCanonicalName())) + "\n";
        str += "global java.util.List list \n";
        str += "rule rule1 when \n";
        str += "   $a : A() \n";
        str += "   $b : B() \n";
        str += "   $c : C() \n";
        str += "then \n";
        str += "  list.add( $a.getValue() + \":\"+ $b.getValue() + \":\" + $c.getValue() ); \n";
        str += "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        ObjectTypeNode aotn = LinkingTest.getObjectTypeNode(kbase, LinkingTest.A.class);
        ObjectTypeNode botn = LinkingTest.getObjectTypeNode(kbase, LinkingTest.B.class);
        ObjectTypeNode cotn = LinkingTest.getObjectTypeNode(kbase, LinkingTest.C.class);
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        List list = new ArrayList();
        wm.setGlobal("list", list);
        for (int i = 0; i < 3; i++) {
            wm.insert(new LinkingTest.A(i));
        }
        for (int i = 0; i < 3; i++) {
            wm.insert(new LinkingTest.B(i));
        }
        for (int i = 0; i < 29; i++) {
            wm.insert(new LinkingTest.C(i));
        }
        wm.flushPropagations();
        LeftInputAdapterNode aNode = ((LeftInputAdapterNode) (aotn.getObjectSinkPropagator().getSinks()[0]));
        JoinNode bNode = ((JoinNode) (aNode.getSinkPropagator().getSinks()[0]));
        JoinNode cNode = ((JoinNode) (bNode.getSinkPropagator().getSinks()[0]));
        LiaNodeMemory amem = ((LiaNodeMemory) (wm.getNodeMemory(aNode)));
        BetaMemory bmem = ((BetaMemory) (wm.getNodeMemory(bNode)));
        BetaMemory cmem = ((BetaMemory) (wm.getNodeMemory(cNode)));
        // amem.getSegmentMemory().getStagedLeftTuples().insertSize() == 3
        Assert.assertNotNull(amem.getSegmentMemory().getStagedLeftTuples().getInsertFirst());
        Assert.assertNotNull(amem.getSegmentMemory().getStagedLeftTuples().getInsertFirst().getStagedNext());
        Assert.assertNotNull(amem.getSegmentMemory().getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext());
        Assert.assertNull(amem.getSegmentMemory().getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext().getStagedNext());
        // assertEquals( 3, bmem.getStagedRightTuples().insertSize() );
        Assert.assertNotNull(bmem.getStagedRightTuples().getInsertFirst());
        Assert.assertNotNull(bmem.getStagedRightTuples().getInsertFirst().getStagedNext());
        Assert.assertNotNull(bmem.getStagedRightTuples().getInsertFirst().getStagedNext().getStagedNext());
        Assert.assertNull(bmem.getStagedRightTuples().getInsertFirst().getStagedNext().getStagedNext().getStagedNext());
        wm.fireAllRules();
        Assert.assertNull(amem.getSegmentMemory().getStagedLeftTuples().getInsertFirst());
        Assert.assertNull(bmem.getStagedRightTuples().getInsertFirst());
        Assert.assertNull(cmem.getStagedRightTuples().getInsertFirst());
        Assert.assertEquals(261, list.size());
        Assert.assertTrue(list.contains("2:2:14"));
        Assert.assertTrue(list.contains("1:0:6"));
        Assert.assertTrue(list.contains("0:1:1"));
        Assert.assertTrue(list.contains("2:2:14"));
        Assert.assertTrue(list.contains("0:0:25"));
    }

    @Test
    public void testExistsNodes1() throws Exception {
        String str = "";
        str += "package org.kie \n";
        str += ("import " + (LinkingTest.A.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.B.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.C.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.D.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.E.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.F.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.G.class.getCanonicalName())) + "\n";
        str += "global java.util.List list \n";
        str += "rule rule1 when \n";
        str += "   exists A() \n";
        str += "then \n";
        str += "  list.add( \'x\' ); \n";
        str += "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        KieSession wm = kbase.newKieSession();
        List list = new ArrayList();
        wm.setGlobal("list", list);
        wm.fireAllRules();
        Assert.assertEquals(0, list.size());
        wm = ((StatefulKnowledgeSessionImpl) (kbase.newKieSession()));
        list = new ArrayList();
        wm.setGlobal("list", list);
        FactHandle fh = wm.insert(new LinkingTest.A(1));
        wm.fireAllRules();
        Assert.assertEquals(1, list.size());
        wm.retract(fh);
        wm.fireAllRules();
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void testExistsNodes2() throws Exception {
        String str = "";
        str += "package org.kie \n";
        str += ("import " + (LinkingTest.A.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.B.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.C.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.D.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.E.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.F.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.G.class.getCanonicalName())) + "\n";
        str += "global java.util.List list \n";
        str += "rule rule1 when \n";
        str += "   $a : A() \n";
        str += "   exists B() \n";
        str += "   $c : C() \n";
        str += "then \n";
        str += "  list.add( \'x\' ); \n";
        str += "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        KieSession wm = kbase.newKieSession();
        List list = new ArrayList();
        wm.setGlobal("list", list);
        for (int i = 0; i < 3; i++) {
            wm.insert(new LinkingTest.A(i));
        }
        for (int i = 0; i < 3; i++) {
            wm.insert(new LinkingTest.C(i));
        }
        wm.fireAllRules();
        Assert.assertEquals(0, list.size());
        wm = ((StatefulKnowledgeSessionImpl) (kbase.newKieSession()));
        list = new ArrayList();
        wm.setGlobal("list", list);
        for (int i = 0; i < 3; i++) {
            wm.insert(new LinkingTest.A(i));
        }
        FactHandle fh = wm.insert(new LinkingTest.B(1));
        for (int i = 0; i < 3; i++) {
            wm.insert(new LinkingTest.C(i));
        }
        wm.fireAllRules();
        Assert.assertEquals(9, list.size());
        wm.retract(fh);
        wm.fireAllRules();
        Assert.assertEquals(9, list.size());
    }

    @Test
    public void testNotNodeUnlinksWithNoConstriants() {
        String str = "";
        str += "package org.kie \n";
        str += ("import " + (LinkingTest.A.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.B.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.C.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.D.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.E.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.F.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.G.class.getCanonicalName())) + "\n";
        str += "global java.util.List list \n";
        str += "rule rule1 when \n";
        str += "   $a : A() \n";
        str += "   not B() \n";
        str += "   $c : C() \n";
        str += "then \n";
        str += "  list.add( \'x\' ); \n";
        str += "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        ObjectTypeNode aotn = LinkingTest.getObjectTypeNode(kbase, LinkingTest.A.class);
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl) (kbase.newKieSession()));
        List list = new ArrayList();
        wm.setGlobal("list", list);
        LeftInputAdapterNode aNode = ((LeftInputAdapterNode) (aotn.getObjectSinkPropagator().getSinks()[0]));
        NotNode bNode = ((NotNode) (aNode.getSinkPropagator().getSinks()[0]));
        JoinNode cNode = ((JoinNode) (bNode.getSinkPropagator().getSinks()[0]));
        SegmentUtilities.createSegmentMemory(cNode, wm);
        LiaNodeMemory amem = ((LiaNodeMemory) (wm.getNodeMemory(aNode)));
        // Only NotNode is linked in
        Assert.assertEquals(2, amem.getSegmentMemory().getLinkedNodeMask());
        FactHandle fha = wm.insert(new LinkingTest.A());
        FactHandle fhb = wm.insert(new LinkingTest.B());
        FactHandle fhc = wm.insert(new LinkingTest.C());
        wm.fireAllRules();
        Assert.assertEquals(0, list.size());
        // NotNode unlinks, which is allowed because it has no variable constraints
        Assert.assertEquals(5, amem.getSegmentMemory().getLinkedNodeMask());
        // NotNode links back in again, which is allowed because it has no variable constraints
        wm.retract(fhb);
        wm.flushPropagations();
        Assert.assertEquals(7, amem.getSegmentMemory().getLinkedNodeMask());
        wm.fireAllRules();
        Assert.assertEquals(1, list.size());
        // Now try with lots of facthandles on NotNode
        list.clear();
        List<FactHandle> handles = new ArrayList<FactHandle>();
        for (int i = 0; i < 5; i++) {
            handles.add(wm.insert(new LinkingTest.B()));
        }
        wm.fireAllRules();
        Assert.assertEquals(0, list.size());
        Assert.assertEquals(5, amem.getSegmentMemory().getLinkedNodeMask());
        for (FactHandle fh : handles) {
            wm.retract(fh);
        }
        wm.flushPropagations();
        Assert.assertEquals(7, amem.getSegmentMemory().getLinkedNodeMask());
        wm.fireAllRules();
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void testNotNodeDoesNotUnlinksWithConstriants() {
        String str = "";
        str += "package org.kie \n";
        str += ("import " + (LinkingTest.A.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.B.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.C.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.D.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.E.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.F.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.G.class.getCanonicalName())) + "\n";
        str += "global java.util.List list \n";
        str += "rule rule1 when \n";
        str += "   $a : A() \n";
        str += "   not B( value == $a.value ) \n";
        str += "   $c : C() \n";
        str += "then \n";
        str += "  list.add( \'x\' ); \n";
        str += "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        ObjectTypeNode aotn = LinkingTest.getObjectTypeNode(kbase, LinkingTest.A.class);
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl) (kbase.newKieSession()));
        List list = new ArrayList();
        wm.setGlobal("list", list);
        LeftInputAdapterNode aNode = ((LeftInputAdapterNode) (aotn.getObjectSinkPropagator().getSinks()[0]));
        NotNode bNode = ((NotNode) (aNode.getSinkPropagator().getSinks()[0]));
        JoinNode cNode = ((JoinNode) (bNode.getSinkPropagator().getSinks()[0]));
        SegmentUtilities.createSegmentMemory(cNode, wm);
        LiaNodeMemory amem = ((LiaNodeMemory) (wm.getNodeMemory(aNode)));
        // Only NotNode is linked in
        Assert.assertEquals(2, amem.getSegmentMemory().getLinkedNodeMask());
        FactHandle fha = wm.insert(new LinkingTest.A());
        FactHandle fhb = wm.insert(new LinkingTest.B(1));
        FactHandle fhc = wm.insert(new LinkingTest.C());
        wm.flushPropagations();
        // All nodes are linked in
        Assert.assertEquals(7, amem.getSegmentMemory().getLinkedNodeMask());
        // NotNode does not unlink, due to variable constraint
        wm.retract(fhb);
        wm.flushPropagations();
        Assert.assertEquals(7, amem.getSegmentMemory().getLinkedNodeMask());
    }

    @Test
    public void testNotNodes1() throws Exception {
        String str = "";
        str += "package org.kie \n";
        str += ("import " + (LinkingTest.A.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.B.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.C.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.D.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.E.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.F.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.G.class.getCanonicalName())) + "\n";
        str += "global java.util.List list \n";
        str += "rule rule1 when \n";
        str += "   not A() \n";
        str += "then \n";
        str += "  list.add( \'x\' ); \n";
        str += "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl) (kbase.newKieSession()));
        List list = new ArrayList();
        wm.setGlobal("list", list);
        wm.fireAllRules();
        Assert.assertEquals(1, list.size());
        wm = ((StatefulKnowledgeSessionImpl) (kbase.newKieSession()));
        list = new ArrayList();
        wm.setGlobal("list", list);
        FactHandle fh = wm.insert(new LinkingTest.A(1));
        wm.fireAllRules();
        Assert.assertEquals(0, list.size());
        wm.retract(fh);
        wm.fireAllRules();
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void testNotNodes2() throws Exception {
        String str = "";
        str += "package org.kie \n";
        str += ("import " + (LinkingTest.A.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.B.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.C.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.D.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.E.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.F.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.G.class.getCanonicalName())) + "\n";
        str += "global java.util.List list \n";
        str += "rule rule1 when \n";
        str += "   $a : A() \n";
        str += "   not B() \n";
        str += "   $c : C() \n";
        str += "then \n";
        str += "  list.add( \'x\' ); \n";
        str += "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        ObjectTypeNode aotn = LinkingTest.getObjectTypeNode(kbase, LinkingTest.A.class);
        ObjectTypeNode botn = LinkingTest.getObjectTypeNode(kbase, LinkingTest.A.class);
        ObjectTypeNode cotn = LinkingTest.getObjectTypeNode(kbase, LinkingTest.A.class);
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl) (kbase.newKieSession()));
        List list = new ArrayList();
        wm.setGlobal("list", list);
        for (int i = 0; i < 3; i++) {
            wm.insert(new LinkingTest.A(i));
        }
        for (int i = 0; i < 3; i++) {
            wm.insert(new LinkingTest.C(i));
        }
        wm.fireAllRules();
        Assert.assertEquals(9, list.size());
        wm = ((StatefulKnowledgeSessionImpl) (kbase.newKieSession()));
        list = new ArrayList();
        wm.setGlobal("list", list);
        for (int i = 0; i < 3; i++) {
            wm.insert(new LinkingTest.A(i));
        }
        FactHandle fh = wm.insert(new LinkingTest.B(1));
        for (int i = 0; i < 3; i++) {
            wm.insert(new LinkingTest.C(i));
        }
        wm.fireAllRules();
        Assert.assertEquals(0, list.size());
        wm.retract(fh);
        wm.fireAllRules();
        Assert.assertEquals(9, list.size());
    }

    @Test
    public void testNotNodeMasksWithConstraints() throws Exception {
        String str = "";
        str += "package org.kie \n";
        str += ("import " + (LinkingTest.A.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.B.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.C.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.D.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.E.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.F.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.G.class.getCanonicalName())) + "\n";
        str += "global java.util.List list \n";
        str += "rule rule1 when \n";
        str += "   $a : A() \n";
        str += "   not( B( value == $a.value ) ) \n";
        str += "   C() \n";
        str += "then \n";
        str += "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        ObjectTypeNode node = LinkingTest.getObjectTypeNode(kbase, LinkingTest.A.class);
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl) (kbase.newKieSession()));
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (node.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(1, liaNode.getSinkPropagator().size());
        wm.insert(new LinkingTest.A());
        wm.flushPropagations();
        NotNode notNode = ((NotNode) (liaNode.getSinkPropagator().getSinks()[0]));
        JoinNode cNode = ((JoinNode) (notNode.getSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtn = ((RuleTerminalNode) (cNode.getSinkPropagator().getSinks()[0]));
        PathMemory pmem = ((PathMemory) (wm.getNodeMemory(rtn)));
        Assert.assertEquals(1, pmem.getSegmentMemories().length);
        Assert.assertEquals(1, pmem.getAllLinkedMaskTest());
        SegmentMemory sm = pmem.getSegmentMemories()[0];
        Assert.assertEquals(5, sm.getAllLinkedMaskTest());
        Assert.assertEquals(3, sm.getLinkedNodeMask());
        Assert.assertFalse(sm.isSegmentLinked());
        Assert.assertFalse(pmem.isRuleLinked());
        wm.insert(new LinkingTest.C());
        wm.flushPropagations();
        Assert.assertEquals(7, sm.getLinkedNodeMask());// only 5 is needed to link, the 'not' turns on but it has no unfleunce either way

        Assert.assertTrue(sm.isSegmentLinked());
        Assert.assertTrue(pmem.isRuleLinked());
    }

    @Test
    public void testNotNodeMasksWithoutConstraints() throws Exception {
        String str = "";
        str += "package org.kie \n";
        str += ("import " + (LinkingTest.A.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.B.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.C.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.D.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.E.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.F.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.G.class.getCanonicalName())) + "\n";
        str += "global java.util.List list \n";
        str += "rule rule1 when \n";
        str += "   A() \n";
        str += "   not( B( ) ) \n";
        str += "   C() \n";
        str += "then \n";
        str += "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        ObjectTypeNode node = LinkingTest.getObjectTypeNode(kbase, LinkingTest.A.class);
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl) (kbase.newKieSession()));
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (node.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(1, liaNode.getSinkPropagator().size());
        wm.insert(new LinkingTest.A());
        wm.flushPropagations();
        NotNode notNode = ((NotNode) (liaNode.getSinkPropagator().getSinks()[0]));
        JoinNode cNode = ((JoinNode) (notNode.getSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtn = ((RuleTerminalNode) (cNode.getSinkPropagator().getSinks()[0]));
        PathMemory pmem = ((PathMemory) (wm.getNodeMemory(rtn)));
        Assert.assertEquals(1, pmem.getSegmentMemories().length);
        Assert.assertEquals(1, pmem.getAllLinkedMaskTest());
        SegmentMemory sm = pmem.getSegmentMemories()[0];
        Assert.assertEquals(7, sm.getAllLinkedMaskTest());
        Assert.assertEquals(3, sm.getLinkedNodeMask());
        Assert.assertFalse(sm.isSegmentLinked());
        Assert.assertFalse(pmem.isRuleLinked());
        wm.insert(new LinkingTest.C());
        wm.flushPropagations();
        Assert.assertEquals(7, sm.getLinkedNodeMask());
        Assert.assertTrue(sm.isSegmentLinked());
        Assert.assertTrue(pmem.isRuleLinked());
    }

    @Test
    public void testForallNodes() throws Exception {
        String str = "";
        str += "package org.kie \n";
        str += ("import " + (LinkingTest.A.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.B.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.C.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.D.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.E.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.F.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.G.class.getCanonicalName())) + "\n";
        str += "global java.util.List list \n";
        str += "rule rule1 when \n";
        str += "   $a : A() \n";
        str += "   forall( B() )\n";
        str += "   $c : C() \n";
        str += "then \n";
        str += "  list.add( \'x\' ); \n";
        str += "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        ObjectTypeNode aotn = LinkingTest.getObjectTypeNode(kbase, LinkingTest.A.class);
        ObjectTypeNode botn = LinkingTest.getObjectTypeNode(kbase, LinkingTest.A.class);
        ObjectTypeNode cotn = LinkingTest.getObjectTypeNode(kbase, LinkingTest.A.class);
        KieSession wm = kbase.newKieSession();
        List list = new ArrayList();
        // wm.setGlobal( "list", list );
        // 
        // for ( int i = 0; i < 3; i++ ) {
        // wm.insert(  new A(i) );
        // }
        // 
        // wm.insert(  new B(2) );
        // 
        // for ( int i = 0; i < 3; i++ ) {
        // wm.insert(  new C(i) );
        // }
        // 
        // wm.fireAllRules();
        // assertEquals( 0, list.size() );
        wm = ((StatefulKnowledgeSessionImpl) (kbase.newKieSession()));
        list = new ArrayList();
        wm.setGlobal("list", list);
        for (int i = 0; i < 2; i++) {
            wm.insert(new LinkingTest.A(i));
        }
        for (int i = 0; i < 27; i++) {
            wm.insert(new LinkingTest.B(1));
        }
        for (int i = 0; i < 2; i++) {
            wm.insert(new LinkingTest.C(i));
        }
        wm.fireAllRules();
        Assert.assertEquals(4, list.size());
        // wm.retract( fh );
        // wm.fireAllRules();
        // assertEquals( 9, list.size() );
    }

    @Test
    public void testAccumulateNodes1() throws Exception {
        String str = "";
        str += "package org.kie \n";
        str += ("import " + (LinkingTest.A.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.B.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.C.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.D.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.E.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.F.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.G.class.getCanonicalName())) + "\n";
        str += "global java.util.List list \n";
        str += "rule rule1 when \n";
        str += "   accumulate( $a : A(); $l : collectList( $a ) ) \n";
        str += "then \n";
        str += "  list.add( $l.size() ); \n";
        str += "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        KieSession wm = kbase.newKieSession();
        List list = new ArrayList();
        wm.setGlobal("list", list);
        wm.fireAllRules();
        Assert.assertEquals(1, list.size());
        wm = kbase.newKieSession();
        list = new ArrayList();
        wm.setGlobal("list", list);
        FactHandle fh1 = wm.insert(new LinkingTest.A(1));
        FactHandle fh2 = wm.insert(new LinkingTest.A(2));
        FactHandle fh3 = wm.insert(new LinkingTest.A(3));
        FactHandle fh4 = wm.insert(new LinkingTest.A(4));
        wm.fireAllRules();
        Assert.assertEquals(4, list.get(0));
    }

    @Test
    public void testAccumulateNodes2() throws Exception {
        String str = "";
        str += "package org.kie \n";
        str += ("import " + (LinkingTest.A.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.B.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.C.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.D.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.E.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.F.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.G.class.getCanonicalName())) + "\n";
        str += "global java.util.List list \n";
        str += "rule rule1 when \n";
        str += "   A() \n";
        str += "   accumulate( $a : B(); $l : collectList( $a ) ) \n";
        str += "   C() \n";
        str += "then \n";
        str += "  list.add( $l.size() ); \n";
        str += "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        KieSession wm = kbase.newKieSession();
        List list = new ArrayList();
        wm.setGlobal("list", list);
        wm.fireAllRules();
        Assert.assertEquals(0, list.size());
        wm = ((StatefulKnowledgeSessionImpl) (kbase.newKieSession()));
        list = new ArrayList();
        wm.setGlobal("list", list);
        FactHandle fh1 = wm.insert(new LinkingTest.B(1));
        FactHandle fh2 = wm.insert(new LinkingTest.B(2));
        FactHandle fh3 = wm.insert(new LinkingTest.B(3));
        FactHandle fh4 = wm.insert(new LinkingTest.B(4));
        FactHandle fha = wm.insert(new LinkingTest.A(1));
        FactHandle fhc = wm.insert(new LinkingTest.C(1));
        wm.fireAllRules();
        Assert.assertEquals(4, list.get(0));
    }

    @Test
    public void testSubnetwork() throws Exception {
        String str = "";
        str += "package org.kie \n";
        str += ("import " + (LinkingTest.A.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.B.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.C.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.D.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.E.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.F.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.G.class.getCanonicalName())) + "\n";
        str += "global java.util.List list \n";
        str += "rule rule1 when \n";
        str += "   $a : A() \n";
        str += "   exists ( B() and C() ) \n";
        str += "   $e : D() \n";
        str += "then \n";
        str += "  list.add( \'x\' ); \n";
        str += "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        ObjectTypeNode aotn = LinkingTest.getObjectTypeNode(kbase, LinkingTest.A.class);
        ObjectTypeNode botn = LinkingTest.getObjectTypeNode(kbase, LinkingTest.A.class);
        ObjectTypeNode cotn = LinkingTest.getObjectTypeNode(kbase, LinkingTest.A.class);
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl) (kbase.newKieSession()));
        List list = new ArrayList();
        wm.setGlobal("list", list);
        wm.insert(new LinkingTest.A());
        wm.insert(new LinkingTest.B());
        for (int i = 0; i < 28; i++) {
            wm.insert(new LinkingTest.C());
        }
        wm.insert(new LinkingTest.D());
        wm.flushPropagations();
        InternalAgenda agenda = ((InternalAgenda) (wm.getAgenda()));
        InternalAgendaGroup group = ((InternalAgendaGroup) (agenda.getNextFocus()));
        AgendaItem item = ((AgendaItem) (group.remove()));
        RuleExecutor ruleExecutor = getRuleExecutor();
        int count = ruleExecutor.evaluateNetworkAndFire(wm, null, 0, (-1));
        // assertEquals(3, count );
        wm.fireAllRules();
        Assert.assertEquals(1, list.size());
        wm.fireAllRules();
        Assert.assertEquals(1, list.size());// check it doesn't double fire

    }

    @Test
    public void testNestedSubnetwork() throws Exception {
        String str = "";
        str += "package org.kie \n";
        str += ("import " + (LinkingTest.A.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.B.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.C.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.D.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.E.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.F.class.getCanonicalName())) + "\n";
        str += ("import " + (LinkingTest.G.class.getCanonicalName())) + "\n";
        str += "global java.util.List list \n";
        str += "rule rule1 when \n";
        str += "   $a : A() \n";
        str += "   exists ( B() and exists( C() and D() ) and E() ) \n";
        str += "   $f : F() \n";
        str += "then \n";
        str += "  list.add( \'x\' ); \n";
        str += "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        ObjectTypeNode aotn = LinkingTest.getObjectTypeNode(kbase, LinkingTest.A.class);
        ObjectTypeNode botn = LinkingTest.getObjectTypeNode(kbase, LinkingTest.A.class);
        ObjectTypeNode cotn = LinkingTest.getObjectTypeNode(kbase, LinkingTest.A.class);
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl) (kbase.newKieSession()));
        List list = new ArrayList();
        wm.setGlobal("list", list);
        wm.insert(new LinkingTest.A());
        wm.insert(new LinkingTest.B());
        for (int i = 0; i < 28; i++) {
            wm.insert(new LinkingTest.C());
        }
        for (int i = 0; i < 29; i++) {
            wm.insert(new LinkingTest.D());
        }
        wm.insert(new LinkingTest.E());
        wm.insert(new LinkingTest.F());
        // DefaultAgenda agenda = ( DefaultAgenda ) wm.getAgenda();
        // InternalAgendaGroup group = (InternalAgendaGroup) agenda.getNextFocus();
        // AgendaItem item = (AgendaItem) group.remove();
        // int count = ((RuleAgendaItem)item).evaluateNetworkAndFire( wm );
        // //assertEquals(7, count ); // proves we correctly track nested sub network staged propagations
        // 
        // agenda.addActivation( item, true );
        // agenda = ( DefaultAgenda ) wm.getAgenda();
        // group = (InternalAgendaGroup) agenda.getNextFocus();
        // item = (AgendaItem) group.remove();
        // 
        // agenda.fireActivation( item );
        // assertEquals( 1, list.size() );
        // 
        // agenda = ( DefaultAgenda ) wm.getAgenda();
        // group = (InternalAgendaGroup) agenda.getNextFocus();
        // item = (AgendaItem) group.remove();
        // count = ((RuleAgendaItem)item).evaluateNetworkAndFire( wm );
        // //assertEquals(0, count );
        wm.fireAllRules();
        Assert.assertEquals(1, list.size());
        wm.fireAllRules();
        Assert.assertEquals(1, list.size());
    }
}

