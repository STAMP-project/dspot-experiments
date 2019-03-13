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
package org.drools.compiler.phreak;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.EvalConditionNode;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.SegmentMemory;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;


public class AddRuleTest {
    @Test
    public void testPopulatedSingleRuleNoSharing() throws Exception {
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        wm.insert(new A(1));
        wm.insert(new B(1));
        wm.insert(new C(1));
        wm.insert(new C(2));
        wm.insert(new D(1));
        wm.insert(new E(1));
        wm.fireAllRules();
        kbase.addPackages(buildKnowledgePackage("r1", "   A() B() C(object == 2) D() E()\n"));
        List list = new ArrayList();
        wm.setGlobal("list", list);
        ObjectTypeNode aotn = getObjectTypeNode(kbase, A.class);
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (aotn.getObjectSinkPropagator().getSinks()[0]));
        LiaNodeMemory lm = ((LiaNodeMemory) (wm.getNodeMemory(liaNode)));
        SegmentMemory sm = lm.getSegmentMemory();
        Assert.assertNotNull(sm.getStagedLeftTuples().getInsertFirst());
        wm.fireAllRules();
        Assert.assertNull(sm.getStagedLeftTuples().getInsertFirst());
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("r1", getRule().getName());
    }

    @Test
    public void testPopulatedSingleRuleNoSharingWithSubnetworkAtStart() throws Exception {
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        wm.insert(new A(1));
        wm.insert(new A(2));
        wm.insert(new D(1));
        wm.insert(new E(1));
        wm.insert(new C(2));
        wm.fireAllRules();
        kbase.addPackages(buildKnowledgePackage("r1", "   A() not( B() and C() ) D() E()\n"));
        List list = new ArrayList();
        wm.setGlobal("list", list);
        ObjectTypeNode aotn = getObjectTypeNode(kbase, A.class);
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (aotn.getObjectSinkPropagator().getSinks()[0]));
        LiaNodeMemory lm = ((LiaNodeMemory) (wm.getNodeMemory(liaNode)));
        SegmentMemory sm = lm.getSegmentMemory();
        Assert.assertNull(sm.getStagedLeftTuples().getInsertFirst());
        SegmentMemory subSm = sm.getFirst();
        SegmentMemory mainSm = subSm.getNext();
        Assert.assertNotNull(subSm.getStagedLeftTuples().getInsertFirst());
        Assert.assertNotNull(subSm.getStagedLeftTuples().getInsertFirst().getStagedNext());
        Assert.assertNull(subSm.getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext());
        Assert.assertNotNull(mainSm.getStagedLeftTuples().getInsertFirst());
        Assert.assertNotNull(mainSm.getStagedLeftTuples().getInsertFirst().getStagedNext());
        Assert.assertNull(mainSm.getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext());
        wm.fireAllRules();
        Assert.assertNull(subSm.getStagedLeftTuples().getInsertFirst());
        Assert.assertNull(mainSm.getStagedLeftTuples().getInsertFirst());
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("r1", getRule().getName());
    }

    @Test
    public void testPopulatedRuleMidwayShare() throws Exception {
        InternalKnowledgeBase kbase1 = buildKnowledgeBase("r1", "   a : A() B() C(1;) D() E()\n");
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase1.newKieSession()));
        List list = new ArrayList();
        wm.setGlobal("list", list);
        wm.insert(new A(1));
        wm.insert(new A(2));
        wm.insert(new A(3));
        wm.insert(new B(1));
        wm.insert(new C(1));
        wm.insert(new C(2));
        wm.insert(new D(1));
        wm.insert(new E(1));
        wm.fireAllRules();
        Assert.assertEquals(3, list.size());
        kbase1.addPackages(buildKnowledgePackage("r2", "   a : A() B() C(2;) D() E()\n"));
        ObjectTypeNode aotn = getObjectTypeNode(kbase1, A.class);
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (aotn.getObjectSinkPropagator().getSinks()[0]));
        JoinNode bNode = ((JoinNode) (liaNode.getSinkPropagator().getFirstLeftTupleSink()));
        JoinNode c1Node = ((JoinNode) (bNode.getSinkPropagator().getFirstLeftTupleSink()));
        JoinNode c2Node = ((JoinNode) (bNode.getSinkPropagator().getLastLeftTupleSink()));
        LiaNodeMemory lm = ((LiaNodeMemory) (wm.getNodeMemory(liaNode)));
        SegmentMemory sm = lm.getSegmentMemory();
        BetaMemory c1Mem = ((BetaMemory) (wm.getNodeMemory(c1Node)));
        TestCase.assertSame(sm.getFirst(), c1Mem.getSegmentMemory());
        Assert.assertEquals(3, c1Mem.getLeftTupleMemory().size());
        Assert.assertEquals(1, c1Mem.getRightTupleMemory().size());
        BetaMemory c2Mem = ((BetaMemory) (wm.getNodeMemory(c2Node)));
        SegmentMemory c2Smem = sm.getFirst().getNext();
        TestCase.assertSame(c2Smem, c2Mem.getSegmentMemory());
        Assert.assertEquals(0, c2Mem.getLeftTupleMemory().size());
        Assert.assertEquals(0, c2Mem.getRightTupleMemory().size());
        Assert.assertNotNull(c2Smem.getStagedLeftTuples().getInsertFirst());
        Assert.assertNotNull(c2Smem.getStagedLeftTuples().getInsertFirst().getStagedNext());
        Assert.assertNotNull(c2Smem.getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext());
        Assert.assertNull(c2Smem.getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext().getStagedNext());
        wm.fireAllRules();
        Assert.assertEquals(3, c2Mem.getLeftTupleMemory().size());
        Assert.assertEquals(1, c2Mem.getRightTupleMemory().size());
        Assert.assertNull(c2Smem.getStagedLeftTuples().getInsertFirst());
        Assert.assertEquals(6, list.size());
        Assert.assertEquals("r1", getRule().getName());
        Assert.assertEquals("r1", getRule().getName());
        Assert.assertEquals("r1", getRule().getName());
        Assert.assertEquals("r2", getRule().getName());
        Assert.assertEquals(3, ((A) (getDeclarationValue("a"))).getObject());
        Assert.assertEquals("r2", getRule().getName());
        Assert.assertEquals(2, ((A) (getDeclarationValue("a"))).getObject());
        Assert.assertEquals("r2", getRule().getName());
        Assert.assertEquals(1, ((A) (getDeclarationValue("a"))).getObject());
    }

    @Test
    public void testPopulatedRuleWithEvals() throws Exception {
        InternalKnowledgeBase kbase1 = buildKnowledgeBase("r1", "   a:A() B() eval(1==1) eval(1==1) C(1;) \n");
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase1.newKieSession()));
        List list = new ArrayList();
        wm.setGlobal("list", list);
        wm.insert(new A(1));
        wm.insert(new A(2));
        wm.insert(new A(3));
        wm.insert(new B(1));
        wm.insert(new C(1));
        wm.insert(new C(2));
        wm.fireAllRules();
        Assert.assertEquals(3, list.size());
        kbase1.addPackages(buildKnowledgePackage("r2", "   a:A() B() eval(1==1) eval(1==1) C(2;) \n"));
        ObjectTypeNode aotn = getObjectTypeNode(kbase1, A.class);
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (aotn.getObjectSinkPropagator().getSinks()[0]));
        JoinNode bNode = ((JoinNode) (liaNode.getSinkPropagator().getFirstLeftTupleSink()));
        EvalConditionNode e1 = ((EvalConditionNode) (bNode.getSinkPropagator().getFirstLeftTupleSink()));
        EvalConditionNode e2 = ((EvalConditionNode) (e1.getSinkPropagator().getFirstLeftTupleSink()));
        JoinNode c1Node = ((JoinNode) (e2.getSinkPropagator().getFirstLeftTupleSink()));
        JoinNode c2Node = ((JoinNode) (e2.getSinkPropagator().getLastLeftTupleSink()));
        LiaNodeMemory lm = ((LiaNodeMemory) (wm.getNodeMemory(liaNode)));
        SegmentMemory sm = lm.getSegmentMemory();
        BetaMemory c1Mem = ((BetaMemory) (wm.getNodeMemory(c1Node)));
        TestCase.assertSame(sm.getFirst(), c1Mem.getSegmentMemory());
        Assert.assertEquals(3, c1Mem.getLeftTupleMemory().size());
        Assert.assertEquals(1, c1Mem.getRightTupleMemory().size());
        BetaMemory c2Mem = ((BetaMemory) (wm.getNodeMemory(c2Node)));
        SegmentMemory c2Smem = sm.getFirst().getNext();
        TestCase.assertSame(c2Smem, c2Mem.getSegmentMemory());
        Assert.assertEquals(0, c2Mem.getLeftTupleMemory().size());
        Assert.assertEquals(0, c2Mem.getRightTupleMemory().size());
        Assert.assertNotNull(c2Smem.getStagedLeftTuples().getInsertFirst());
        Assert.assertNotNull(c2Smem.getStagedLeftTuples().getInsertFirst().getStagedNext());
        Assert.assertNotNull(c2Smem.getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext());
        Assert.assertNull(c2Smem.getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext().getStagedNext());
        wm.fireAllRules();
        Assert.assertEquals(3, c2Mem.getLeftTupleMemory().size());
        Assert.assertEquals(1, c2Mem.getRightTupleMemory().size());
        Assert.assertNull(c2Smem.getStagedLeftTuples().getInsertFirst());
        Assert.assertEquals(6, list.size());
        Assert.assertEquals("r1", getRule().getName());
        Assert.assertEquals("r1", getRule().getName());
        Assert.assertEquals("r1", getRule().getName());
        Assert.assertEquals("r2", getRule().getName());
        Assert.assertEquals(3, ((A) (getDeclarationValue("a"))).getObject());
        Assert.assertEquals("r2", getRule().getName());
        Assert.assertEquals(2, ((A) (getDeclarationValue("a"))).getObject());
        Assert.assertEquals("r2", getRule().getName());
        Assert.assertEquals(1, ((A) (getDeclarationValue("a"))).getObject());
    }

    @Test
    public void testPopulatedSharedLiaNode() throws Exception {
        InternalKnowledgeBase kbase1 = buildKnowledgeBase("r1", "   A() B(1;) C() D() E()\n");
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase1.newKieSession()));
        List list = new ArrayList();
        wm.setGlobal("list", list);
        wm.insert(new A(1));
        wm.insert(new A(2));
        wm.insert(new A(3));
        wm.insert(new B(1));
        wm.insert(new B(2));
        wm.insert(new C(1));
        wm.insert(new D(1));
        wm.insert(new E(1));
        wm.fireAllRules();
        Assert.assertEquals(3, list.size());
        kbase1.addPackages(buildKnowledgePackage("r2", "   a : A() B(2;) C() D() E()\n"));
        ObjectTypeNode aotn = getObjectTypeNode(kbase1, A.class);
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (aotn.getObjectSinkPropagator().getSinks()[0]));
        JoinNode bNode1 = ((JoinNode) (liaNode.getSinkPropagator().getFirstLeftTupleSink()));
        JoinNode bNode2 = ((JoinNode) (liaNode.getSinkPropagator().getLastLeftTupleSink()));
        BetaMemory bm = ((BetaMemory) (wm.getNodeMemory(bNode2)));
        SegmentMemory sm = bm.getSegmentMemory();
        Assert.assertNotNull(sm.getStagedLeftTuples().getInsertFirst());
        Assert.assertNotNull(sm.getStagedLeftTuples().getInsertFirst().getStagedNext());
        Assert.assertNotNull(sm.getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext());
        Assert.assertNull(sm.getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext().getStagedNext());
        wm.fireAllRules();
        Assert.assertNull(sm.getStagedLeftTuples().getInsertFirst());
        Assert.assertEquals(6, list.size());
        Assert.assertEquals("r1", getRule().getName());
        Assert.assertEquals("r1", getRule().getName());
        Assert.assertEquals("r1", getRule().getName());
        Assert.assertEquals("r2", getRule().getName());
        Assert.assertEquals("r2", getRule().getName());
        Assert.assertEquals("r2", getRule().getName());
        List results = new ArrayList();
        results.add(((A) (getDeclarationValue("a"))).getObject());
        results.add(((A) (getDeclarationValue("a"))).getObject());
        results.add(((A) (getDeclarationValue("a"))).getObject());
        assertTrue(results.containsAll(Arrays.asList(1, 2, 3)));
    }

    @Test
    public void testPopulatedSharedToRtn() throws Exception {
        InternalKnowledgeBase kbase1 = buildKnowledgeBase("r1", "   A() B() C() D() E()\n");
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase1.newKieSession()));
        List list = new ArrayList();
        wm.setGlobal("list", list);
        wm.insert(new A(1));
        wm.insert(new A(2));
        wm.insert(new B(1));
        wm.insert(new C(1));
        wm.insert(new D(1));
        wm.insert(new E(1));
        wm.fireAllRules();
        Assert.assertEquals(2, list.size());
        kbase1.addPackages(buildKnowledgePackage("r2", "   A() B() C() D() E()\n"));
        ObjectTypeNode eotn = getObjectTypeNode(kbase1, E.class);
        JoinNode eNode = ((JoinNode) (eotn.getObjectSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtn = ((RuleTerminalNode) (eNode.getSinkPropagator().getLastLeftTupleSink()));
        PathMemory pm = ((PathMemory) (wm.getNodeMemory(rtn)));
        SegmentMemory sm = pm.getSegmentMemory();
        Assert.assertNotNull(sm.getStagedLeftTuples().getInsertFirst());
        Assert.assertNotNull(sm.getStagedLeftTuples().getInsertFirst().getStagedNext());
        Assert.assertNull(sm.getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext());
        wm.fireAllRules();
        Assert.assertNull(sm.getStagedLeftTuples().getInsertFirst());
        Assert.assertEquals(4, list.size());
        Assert.assertEquals("r1", getRule().getName());
        Assert.assertEquals("r1", getRule().getName());
        Assert.assertEquals("r2", getRule().getName());
        Assert.assertEquals("r2", getRule().getName());
    }

    @Test
    public void testPopulatedMultipleShares() throws Exception {
        InternalKnowledgeBase kbase1 = buildKnowledgeBase("r1", "   A(1;)  A(2;) B(1;) B(2;) C(1;) D() E()\n");
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase1.newKieSession()));
        List list = new ArrayList();
        wm.setGlobal("list", list);
        wm.insert(new A(1));
        wm.insert(new A(2));
        wm.insert(new A(2));
        wm.insert(new A(3));
        wm.insert(new B(1));
        wm.insert(new B(2));
        wm.insert(new C(1));
        wm.insert(new C(2));
        wm.insert(new D(1));
        wm.insert(new E(1));
        wm.fireAllRules();
        Assert.assertEquals(2, list.size());
        kbase1.addPackages(buildKnowledgePackage("r2", "   A(1;)  A(2;) B(1;) B(2;) C(2;) D() E()\n"));
        kbase1.addPackages(buildKnowledgePackage("r3", "   A(1;)  A(3;) B(1;) B(2;) C(2;) D() E()\n"));
        wm.fireAllRules();
        System.out.println(list);
        Assert.assertEquals(5, list.size());
        Assert.assertEquals("r1", getRule().getName());
        Assert.assertEquals("r1", getRule().getName());
        Assert.assertEquals("r3", getRule().getName());// only one A3

        Assert.assertEquals("r2", getRule().getName());
        Assert.assertEquals("r2", getRule().getName());
    }

    @Test
    public void testSplitTwoBeforeCreatedSegment() throws Exception {
        InternalKnowledgeBase kbase1 = buildKnowledgeBase("r1", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) D(1;) D(2;) E(1;) E(2;)\n");
        kbase1.addPackages(buildKnowledgePackage("r2", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) D(1;) D(2;) E(1;) E(2;)\n"));
        kbase1.addPackages(buildKnowledgePackage("r3", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) D(1;) D(2;)\n"));
        kbase1.addPackages(buildKnowledgePackage("r4", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) \n"));
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase1.newKieSession()));
        List list = new ArrayList();
        wm.setGlobal("list", list);
        wm.insert(new E(1));
        wm.insert(new E(2));
        wm.flushPropagations();
        RuleTerminalNode rtn1 = getRtn("org.kie.r1", kbase1);
        RuleTerminalNode rtn2 = getRtn("org.kie.r2", kbase1);
        RuleTerminalNode rtn3 = getRtn("org.kie.r3", kbase1);
        RuleTerminalNode rtn4 = getRtn("org.kie.r4", kbase1);
        PathMemory pm1 = ((PathMemory) (wm.getNodeMemory(rtn1)));
        SegmentMemory[] smems = pm1.getSegmentMemories();
        Assert.assertEquals(4, smems.length);
        Assert.assertNull(smems[0]);
        Assert.assertNull(smems[1]);
        Assert.assertNull(smems[3]);
        SegmentMemory sm = smems[2];
        Assert.assertEquals(2, sm.getPos());
        Assert.assertEquals(4, sm.getSegmentPosMaskBit());
        Assert.assertEquals(4, pm1.getLinkedSegmentMask());
        kbase1.addPackages(buildKnowledgePackage("r5", "   A(1;)  A(2;) B(1;) B(2;) \n"));
        smems = pm1.getSegmentMemories();
        Assert.assertEquals(5, smems.length);
        Assert.assertNull(smems[0]);
        Assert.assertNull(smems[1]);
        Assert.assertNull(smems[2]);
        sm = smems[3];
        Assert.assertEquals(3, sm.getPos());
        Assert.assertEquals(8, sm.getSegmentPosMaskBit());
        Assert.assertEquals(8, pm1.getLinkedSegmentMask());
        RuleTerminalNode rtn5 = getRtn("org.kie.r5", kbase1);
        PathMemory pm5 = ((PathMemory) (wm.getNodeMemory(rtn5)));
        smems = pm5.getSegmentMemories();
        Assert.assertEquals(2, smems.length);
        Assert.assertNull(smems[0]);
        Assert.assertNull(smems[1]);
    }

    @Test
    public void testSplitOneBeforeCreatedSegment() throws Exception {
        InternalKnowledgeBase kbase1 = buildKnowledgeBase("r1", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) D(1;) D(2;) E(1;) E(2;)\n");
        kbase1.addPackages(buildKnowledgePackage("r2", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) D(1;) D(2;) E(1;) E(2;)\n"));
        kbase1.addPackages(buildKnowledgePackage("r3", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) D(1;) D(2;)\n"));
        kbase1.addPackages(buildKnowledgePackage("r4", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) \n"));
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase1.newKieSession()));
        List list = new ArrayList();
        wm.setGlobal("list", list);
        wm.insert(new D(1));
        wm.insert(new D(2));
        wm.flushPropagations();
        RuleTerminalNode rtn1 = getRtn("org.kie.r1", kbase1);
        RuleTerminalNode rtn2 = getRtn("org.kie.r2", kbase1);
        RuleTerminalNode rtn3 = getRtn("org.kie.r3", kbase1);
        RuleTerminalNode rtn4 = getRtn("org.kie.r4", kbase1);
        PathMemory pm1 = ((PathMemory) (wm.getNodeMemory(rtn1)));
        SegmentMemory[] smems = pm1.getSegmentMemories();
        Assert.assertEquals(4, smems.length);
        Assert.assertNull(smems[0]);
        Assert.assertNull(smems[2]);
        Assert.assertNull(smems[3]);
        SegmentMemory sm = smems[1];
        Assert.assertEquals(1, sm.getPos());
        Assert.assertEquals(2, sm.getSegmentPosMaskBit());
        Assert.assertEquals(2, pm1.getLinkedSegmentMask());
        PathMemory pm3 = ((PathMemory) (wm.getNodeMemory(rtn3)));
        SegmentMemory[] smemsP3 = pm3.getSegmentMemories();
        Assert.assertEquals(3, smemsP3.length);
        Assert.assertNull(smemsP3[0]);
        Assert.assertNull(smemsP3[2]);
        sm = smems[1];
        Assert.assertEquals(1, sm.getPos());
        Assert.assertEquals(2, sm.getSegmentPosMaskBit());
        Assert.assertEquals(2, pm1.getLinkedSegmentMask());
        kbase1.addPackages(buildKnowledgePackage("r5", "   A(1;)  A(2;) B(1;) B(2;) \n"));
        smems = pm1.getSegmentMemories();
        Assert.assertEquals(5, smems.length);
        Assert.assertNull(smems[0]);
        Assert.assertNull(smems[1]);
        Assert.assertNull(smems[3]);
        Assert.assertNull(smems[4]);
        sm = smems[2];
        Assert.assertEquals(2, sm.getPos());
        Assert.assertEquals(4, sm.getSegmentPosMaskBit());
        Assert.assertEquals(4, pm1.getLinkedSegmentMask());
        smems = pm3.getSegmentMemories();
        Assert.assertEquals(4, smems.length);
        Assert.assertNull(smems[0]);
        Assert.assertNull(smems[1]);
        Assert.assertNull(smems[3]);
        sm = smems[2];
        Assert.assertEquals(2, sm.getPos());
        Assert.assertEquals(4, sm.getSegmentPosMaskBit());
        Assert.assertEquals(4, pm1.getLinkedSegmentMask());
        RuleTerminalNode rtn5 = getRtn("org.kie.r5", kbase1);
        PathMemory pm5 = ((PathMemory) (wm.getNodeMemory(rtn5)));
        smems = pm5.getSegmentMemories();
        Assert.assertEquals(2, smems.length);
        Assert.assertNull(smems[0]);
        Assert.assertNull(smems[1]);
    }

    @Test
    public void testSplitOnCreatedSegment() throws Exception {
        // this test splits D1 and D2 on the later add rule
        InternalKnowledgeBase kbase1 = buildKnowledgeBase("r1", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) D(1;) D(2;) E(1;) E(2;)\n");
        kbase1.addPackages(buildKnowledgePackage("r2", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) D(1;) D(2;) E(1;) E(2;)\n"));
        kbase1.addPackages(buildKnowledgePackage("r3", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) D(1;) D(2;)\n"));
        kbase1.addPackages(buildKnowledgePackage("r4", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) \n"));
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase1.newKieSession()));
        List list = new ArrayList();
        wm.setGlobal("list", list);
        wm.insert(new D(1));
        wm.insert(new D(2));
        wm.insert(new D(3));
        wm.flushPropagations();
        RuleTerminalNode rtn1 = getRtn("org.kie.r1", kbase1);
        PathMemory pm1 = ((PathMemory) (wm.getNodeMemory(rtn1)));
        Assert.assertEquals(2, pm1.getLinkedSegmentMask());
        SegmentMemory[] smems = pm1.getSegmentMemories();
        Assert.assertEquals(4, smems.length);
        Assert.assertNull(smems[0]);
        Assert.assertNull(smems[2]);
        Assert.assertNull(smems[3]);
        SegmentMemory sm = smems[1];
        Assert.assertEquals(1, sm.getPos());
        Assert.assertEquals(2, sm.getSegmentPosMaskBit());
        kbase1.addPackages(buildKnowledgePackage("r5", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) D(1;) D(3;)\n"));
        wm.fireAllRules();
        Assert.assertEquals(6, pm1.getLinkedSegmentMask());
        smems = pm1.getSegmentMemories();
        Assert.assertEquals(5, smems.length);
        Assert.assertNull(smems[0]);
        Assert.assertNull(smems[3]);
        Assert.assertNull(smems[4]);
        sm = smems[1];
        Assert.assertEquals(1, sm.getPos());
        Assert.assertEquals(2, sm.getSegmentPosMaskBit());
        sm = smems[2];
        Assert.assertEquals(2, sm.getPos());
        Assert.assertEquals(4, sm.getSegmentPosMaskBit());
        RuleTerminalNode rtn5 = getRtn("org.kie.r5", kbase1);
        PathMemory pm5 = ((PathMemory) (wm.getNodeMemory(rtn5)));
        Assert.assertEquals(6, pm5.getLinkedSegmentMask());
        smems = pm5.getSegmentMemories();
        Assert.assertEquals(3, smems.length);
        Assert.assertNull(smems[0]);
        sm = smems[1];
        Assert.assertEquals(1, sm.getPos());
        Assert.assertEquals(2, sm.getSegmentPosMaskBit());
        sm = smems[2];
        Assert.assertEquals(2, sm.getPos());
        Assert.assertEquals(4, sm.getSegmentPosMaskBit());
    }
}

