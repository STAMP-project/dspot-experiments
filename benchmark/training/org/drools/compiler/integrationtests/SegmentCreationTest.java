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


import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.ConditionalBranchNode;
import org.drools.core.reteoo.InitialFactImpl;
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
import org.kie.api.KieBase;
import org.kie.api.runtime.rule.FactHandle;


public class SegmentCreationTest {
    @Test
    public void testSingleEmptyLhs() throws Exception {
        KieBase kbase = buildKnowledgeBase(" ");
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode aotn = getObjectTypeNode(kbase, InitialFactImpl.class);
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (aotn.getObjectSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtn = ((RuleTerminalNode) (liaNode.getSinkPropagator().getSinks()[0]));
        wm.insert(new LinkingTest.A());
        wm.flushPropagations();
        // LiaNode and Rule are in same segment
        LiaNodeMemory liaMem = ((LiaNodeMemory) (wm.getNodeMemory(liaNode)));
        SegmentMemory smem = liaMem.getSegmentMemory();
        Assert.assertEquals(liaNode, smem.getRootNode());
        Assert.assertEquals(rtn, smem.getTipNode());
        Assert.assertNull(smem.getNext());
        Assert.assertNull(smem.getFirst());
    }

    @Test
    public void testSingleSharedEmptyLhs() throws Exception {
        KieBase kbase = buildKnowledgeBase(" ", " ");
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode aotn = getObjectTypeNode(kbase, InitialFactImpl.class);
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (aotn.getObjectSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtn1 = ((RuleTerminalNode) (liaNode.getSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtn2 = ((RuleTerminalNode) (liaNode.getSinkPropagator().getSinks()[1]));
        wm.insert(new LinkingTest.A());
        wm.flushPropagations();
        // LiaNode  is in it's own segment
        LiaNodeMemory liaMem = ((LiaNodeMemory) (wm.getNodeMemory(liaNode)));
        SegmentMemory smem = liaMem.getSegmentMemory();
        Assert.assertEquals(liaNode, smem.getRootNode());
        Assert.assertEquals(liaNode, smem.getTipNode());
        // each RTN is in it's own segment
        SegmentMemory rtnSmem1 = smem.getFirst();
        Assert.assertEquals(rtn1, rtnSmem1.getRootNode());
        Assert.assertEquals(rtn1, rtnSmem1.getTipNode());
        SegmentMemory rtnSmem2 = rtnSmem1.getNext();
        Assert.assertEquals(rtn2, rtnSmem2.getRootNode());
        Assert.assertEquals(rtn2, rtnSmem2.getTipNode());
    }

    @Test
    public void testSinglePattern() throws Exception {
        KieBase kbase = buildKnowledgeBase("   A() \n");
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode aotn = getObjectTypeNode(kbase, LinkingTest.A.class);
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (aotn.getObjectSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtn = ((RuleTerminalNode) (liaNode.getSinkPropagator().getSinks()[0]));
        wm.insert(new LinkingTest.A());
        wm.flushPropagations();
        // LiaNode and Rule are in same segment
        LiaNodeMemory liaMem = ((LiaNodeMemory) (wm.getNodeMemory(liaNode)));
        SegmentMemory smem = liaMem.getSegmentMemory();
        Assert.assertEquals(liaNode, smem.getRootNode());
        Assert.assertEquals(rtn, smem.getTipNode());
        Assert.assertNull(smem.getNext());
        Assert.assertNull(smem.getFirst());
    }

    @Test
    public void testSingleSharedPattern() throws Exception {
        KieBase kbase = buildKnowledgeBase("   A() \n", "   A() \n");
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode aotn = getObjectTypeNode(kbase, LinkingTest.A.class);
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (aotn.getObjectSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtn1 = ((RuleTerminalNode) (liaNode.getSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtn2 = ((RuleTerminalNode) (liaNode.getSinkPropagator().getSinks()[1]));
        wm.insert(new LinkingTest.A());
        wm.flushPropagations();
        // LiaNode  is in it's own segment
        LiaNodeMemory liaMem = ((LiaNodeMemory) (wm.getNodeMemory(liaNode)));
        SegmentMemory smem = liaMem.getSegmentMemory();
        Assert.assertEquals(liaNode, smem.getRootNode());
        Assert.assertEquals(liaNode, smem.getTipNode());
        // each RTN is in it's own segment
        SegmentMemory rtnSmem1 = smem.getFirst();
        Assert.assertEquals(rtn1, rtnSmem1.getRootNode());
        Assert.assertEquals(rtn1, rtnSmem1.getTipNode());
        SegmentMemory rtnSmem2 = rtnSmem1.getNext();
        Assert.assertEquals(rtn2, rtnSmem2.getRootNode());
        Assert.assertEquals(rtn2, rtnSmem2.getTipNode());
    }

    @Test
    public void testMultiSharedPattern() throws Exception {
        KieBase kbase = buildKnowledgeBase("   A() \n", "   A() B() \n", "   A() B() C() \n");
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode aotn = getObjectTypeNode(kbase, LinkingTest.A.class);
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (aotn.getObjectSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtn1 = ((RuleTerminalNode) (liaNode.getSinkPropagator().getSinks()[0]));
        JoinNode bNode = ((JoinNode) (liaNode.getSinkPropagator().getSinks()[1]));
        RuleTerminalNode rtn2 = ((RuleTerminalNode) (bNode.getSinkPropagator().getSinks()[0]));
        JoinNode cNode = ((JoinNode) (bNode.getSinkPropagator().getSinks()[1]));
        RuleTerminalNode rtn3 = ((RuleTerminalNode) (cNode.getSinkPropagator().getSinks()[0]));
        wm.insert(new LinkingTest.A());
        wm.insert(new LinkingTest.B());
        wm.insert(new LinkingTest.C());
        wm.flushPropagations();
        // LiaNode  is in it's own segment
        LiaNodeMemory liaMem = ((LiaNodeMemory) (wm.getNodeMemory(liaNode)));
        SegmentMemory smem = liaMem.getSegmentMemory();
        Assert.assertEquals(liaNode, smem.getRootNode());
        Assert.assertEquals(liaNode, smem.getTipNode());
        SegmentMemory rtnSmem1 = smem.getFirst();
        Assert.assertEquals(rtn1, rtnSmem1.getRootNode());
        Assert.assertEquals(rtn1, rtnSmem1.getTipNode());
        SegmentMemory bSmem = rtnSmem1.getNext();
        Assert.assertEquals(bNode, bSmem.getRootNode());
        Assert.assertEquals(bNode, bSmem.getTipNode());
        // child segment is not yet initialised, so null
        Assert.assertNull(bSmem.getFirst());
        // there is no next
        Assert.assertNull(bSmem.getNext());
        wm.fireAllRules();// child segments should now be initialised

        wm.flushPropagations();
        SegmentMemory rtnSmem2 = bSmem.getFirst();
        Assert.assertEquals(rtn2, rtnSmem2.getRootNode());
        Assert.assertEquals(rtn2, rtnSmem2.getTipNode());
        SegmentMemory cSmem = rtnSmem2.getNext();
        Assert.assertEquals(cNode, cSmem.getRootNode());
        Assert.assertEquals(rtn3, cSmem.getTipNode());// note rtn3 is in the same segment as C

    }

    @Test
    public void testSubnetworkNoSharing() throws Exception {
        KieBase kbase = buildKnowledgeBase(" A()  not ( B() and C() ) \n");
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode aotn = getObjectTypeNode(kbase, LinkingTest.A.class);
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (aotn.getObjectSinkPropagator().getSinks()[0]));
        JoinNode bNode = ((JoinNode) (liaNode.getSinkPropagator().getSinks()[0]));
        JoinNode cNode = ((JoinNode) (bNode.getSinkPropagator().getSinks()[0]));
        RightInputAdapterNode riaNode = ((RightInputAdapterNode) (cNode.getSinkPropagator().getSinks()[0]));
        NotNode notNode = ((NotNode) (liaNode.getSinkPropagator().getSinks()[1]));
        RuleTerminalNode rtn1 = ((RuleTerminalNode) (notNode.getSinkPropagator().getSinks()[0]));
        wm.insert(new LinkingTest.A());
        wm.insert(new LinkingTest.B());
        wm.insert(new LinkingTest.C());
        wm.flushPropagations();
        // LiaNode is in it's own segment
        LiaNodeMemory liaMem = ((LiaNodeMemory) (wm.getNodeMemory(liaNode)));
        SegmentMemory smem = liaMem.getSegmentMemory();
        Assert.assertEquals(liaNode, smem.getRootNode());
        Assert.assertEquals(liaNode, smem.getTipNode());
        Assert.assertNull(smem.getNext());
        smem = smem.getFirst();
        SegmentMemory bSmem = wm.getNodeMemory(bNode).getSegmentMemory();// it's nested inside of smem, so lookup from wm

        Assert.assertEquals(smem, bSmem);
        Assert.assertEquals(bNode, bSmem.getRootNode());
        Assert.assertEquals(riaNode, bSmem.getTipNode());
        BetaMemory bm = ((BetaMemory) (wm.getNodeMemory(notNode)));
        Assert.assertEquals(bm.getSegmentMemory(), smem.getNext());
        Assert.assertEquals(bSmem, bm.getRiaRuleMemory().getSegmentMemory());// check subnetwork ref was made

    }

    @Test
    public void tesSubnetworkAfterShare() throws Exception {
        KieBase kbase = buildKnowledgeBase("   A() \n", "   A()  not ( B() and C() ) \n");
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode aotn = getObjectTypeNode(kbase, LinkingTest.A.class);
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (aotn.getObjectSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtn1 = ((RuleTerminalNode) (liaNode.getSinkPropagator().getSinks()[0]));
        JoinNode bNode = ((JoinNode) (liaNode.getSinkPropagator().getSinks()[1]));
        JoinNode cNode = ((JoinNode) (bNode.getSinkPropagator().getSinks()[0]));
        RightInputAdapterNode riaNode = ((RightInputAdapterNode) (cNode.getSinkPropagator().getSinks()[0]));
        NotNode notNode = ((NotNode) (liaNode.getSinkPropagator().getSinks()[2]));
        RuleTerminalNode rtn2 = ((RuleTerminalNode) (notNode.getSinkPropagator().getSinks()[0]));
        wm.insert(new LinkingTest.A());
        wm.insert(new LinkingTest.B());
        wm.insert(new LinkingTest.C());
        wm.flushPropagations();
        // LiaNode  is in it's own segment
        LiaNodeMemory liaMem = ((LiaNodeMemory) (wm.getNodeMemory(liaNode)));
        SegmentMemory smem = liaMem.getSegmentMemory();
        Assert.assertEquals(liaNode, smem.getRootNode());
        Assert.assertEquals(liaNode, smem.getTipNode());
        SegmentMemory rtnSmem1 = smem.getFirst();
        Assert.assertEquals(rtn1, rtnSmem1.getRootNode());
        Assert.assertEquals(rtn1, rtnSmem1.getTipNode());
        SegmentMemory bSmem = rtnSmem1.getNext();
        Assert.assertEquals(bNode, bSmem.getRootNode());
        Assert.assertEquals(riaNode, bSmem.getTipNode());
        SegmentMemory notSmem = bSmem.getNext();
        Assert.assertEquals(notNode, notSmem.getRootNode());
        Assert.assertEquals(rtn2, notSmem.getTipNode());
        // child segment is not yet initialised, so null
        Assert.assertNull(bSmem.getFirst());
    }

    @Test
    public void tesShareInSubnetwork() throws Exception {
        KieBase kbase = buildKnowledgeBase("   A() \n", "   A() B() C() \n", "   A()  not ( B() and C() ) \n");
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode aotn = getObjectTypeNode(kbase, LinkingTest.A.class);
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (aotn.getObjectSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtn1 = ((RuleTerminalNode) (liaNode.getSinkPropagator().getSinks()[0]));
        JoinNode bNode = ((JoinNode) (liaNode.getSinkPropagator().getSinks()[1]));
        JoinNode cNode = ((JoinNode) (bNode.getSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtn2 = ((RuleTerminalNode) (cNode.getSinkPropagator().getSinks()[0]));
        RightInputAdapterNode riaNode = ((RightInputAdapterNode) (cNode.getSinkPropagator().getSinks()[1]));
        NotNode notNode = ((NotNode) (liaNode.getSinkPropagator().getSinks()[2]));
        RuleTerminalNode rtn3 = ((RuleTerminalNode) (notNode.getSinkPropagator().getSinks()[0]));
        wm.insert(new LinkingTest.A());
        wm.insert(new LinkingTest.B());
        wm.insert(new LinkingTest.C());
        wm.flushPropagations();
        // LiaNode  is in it's own segment
        LiaNodeMemory liaMem = ((LiaNodeMemory) (wm.getNodeMemory(liaNode)));
        SegmentMemory smem = liaMem.getSegmentMemory();
        Assert.assertEquals(liaNode, smem.getRootNode());
        Assert.assertEquals(liaNode, smem.getTipNode());
        SegmentMemory rtnSmem1 = smem.getFirst();
        Assert.assertEquals(rtn1, rtnSmem1.getRootNode());
        Assert.assertEquals(rtn1, rtnSmem1.getTipNode());
        SegmentMemory bSmem = rtnSmem1.getNext();
        Assert.assertEquals(bNode, bSmem.getRootNode());
        Assert.assertEquals(cNode, bSmem.getTipNode());
        Assert.assertNull(bSmem.getFirst());// segment is not initialized yet

        wm.fireAllRules();
        SegmentMemory rtn2Smem = bSmem.getFirst();
        Assert.assertEquals(rtn2, rtn2Smem.getRootNode());
        Assert.assertEquals(rtn2, rtn2Smem.getTipNode());
        SegmentMemory riaSmem = rtn2Smem.getNext();
        Assert.assertEquals(riaNode, riaSmem.getRootNode());
        Assert.assertEquals(riaNode, riaSmem.getTipNode());
        SegmentMemory notSmem = bSmem.getNext();
        Assert.assertEquals(notNode, notSmem.getRootNode());
        Assert.assertEquals(rtn3, notSmem.getTipNode());
    }

    @Test
    public void testBranchCESingleSegment() throws Exception {
        KieBase kbase = buildKnowledgeBase(("   $a : A() \n" + ("   if ( $a != null ) do[t1] \n" + "   B() \n")));
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode aotn = getObjectTypeNode(kbase, LinkingTest.A.class);
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (aotn.getObjectSinkPropagator().getSinks()[0]));
        ConditionalBranchNode cen1Node = ((ConditionalBranchNode) (liaNode.getSinkPropagator().getSinks()[0]));
        JoinNode bNode = ((JoinNode) (cen1Node.getSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtn1 = ((RuleTerminalNode) (bNode.getSinkPropagator().getSinks()[0]));
        FactHandle bFh = wm.insert(new LinkingTest.B());
        wm.flushPropagations();
        LiaNodeMemory liaMem = ((LiaNodeMemory) (wm.getNodeMemory(liaNode)));
        SegmentMemory smem = liaMem.getSegmentMemory();
        Assert.assertEquals(1, smem.getAllLinkedMaskTest());
        Assert.assertEquals(4, smem.getLinkedNodeMask());// B links, but it will not trigger mask

        Assert.assertFalse(smem.isSegmentLinked());
        PathMemory pmem = ((PathMemory) (wm.getNodeMemory(rtn1)));
        Assert.assertEquals(1, pmem.getAllLinkedMaskTest());
        Assert.assertEquals(0, pmem.getLinkedSegmentMask());
        Assert.assertFalse(pmem.isRuleLinked());
        wm.insert(new LinkingTest.A());
        wm.flushPropagations();
        Assert.assertEquals(5, smem.getLinkedNodeMask());// A links in segment

        Assert.assertTrue(smem.isSegmentLinked());
        Assert.assertEquals(1, pmem.getLinkedSegmentMask());
        Assert.assertTrue(pmem.isRuleLinked());
        wm.delete(bFh);// retract B does not unlink the rule

        wm.flushPropagations();
        Assert.assertEquals(1, pmem.getLinkedSegmentMask());
        Assert.assertTrue(pmem.isRuleLinked());
    }

    @Test
    public void testBranchCEMultipleSegments() throws Exception {
        KieBase kbase = // r1
        // r2
        // r3
        buildKnowledgeBase("   $a : A() \n", ("   $a : A() \n" + ("   if ( $a != null ) do[t1] \n" + "   B() \n")), ("   $a : A() \n" + (("   if ( $a != null ) do[t1] \n" + "   B() \n") + "   C() \n")));
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode aotn = getObjectTypeNode(kbase, LinkingTest.A.class);
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (aotn.getObjectSinkPropagator().getSinks()[0]));
        ConditionalBranchNode cen1Node = ((ConditionalBranchNode) (liaNode.getSinkPropagator().getSinks()[1]));
        JoinNode bNode = ((JoinNode) (cen1Node.getSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtn2 = ((RuleTerminalNode) (bNode.getSinkPropagator().getSinks()[0]));
        JoinNode cNode = ((JoinNode) (bNode.getSinkPropagator().getSinks()[1]));
        RuleTerminalNode rtn3 = ((RuleTerminalNode) (cNode.getSinkPropagator().getSinks()[0]));
        FactHandle bFh = wm.insert(new LinkingTest.B());
        FactHandle cFh = wm.insert(new LinkingTest.C());
        wm.flushPropagations();
        BetaMemory bNodeBm = ((BetaMemory) (wm.getNodeMemory(bNode)));
        SegmentMemory bNodeSmem = bNodeBm.getSegmentMemory();
        Assert.assertEquals(0, bNodeSmem.getAllLinkedMaskTest());// no beta nodes before branch CE, so never unlinks

        Assert.assertEquals(2, bNodeSmem.getLinkedNodeMask());
        PathMemory pmemr2 = ((PathMemory) (wm.getNodeMemory(rtn2)));
        Assert.assertEquals(1, pmemr2.getAllLinkedMaskTest());
        Assert.assertEquals(2, pmemr2.getLinkedSegmentMask());
        Assert.assertEquals(3, pmemr2.getSegmentMemories().length);
        Assert.assertFalse(pmemr2.isRuleLinked());
        PathMemory pmemr3 = ((PathMemory) (wm.getNodeMemory(rtn3)));
        Assert.assertEquals(1, pmemr3.getAllLinkedMaskTest());// notice only the first segment links

        Assert.assertEquals(3, pmemr3.getSegmentMemories().length);
        Assert.assertFalse(pmemr3.isRuleLinked());
        BetaMemory cNodeBm = ((BetaMemory) (wm.getNodeMemory(cNode)));
        SegmentMemory cNodeSmem = cNodeBm.getSegmentMemory();
        Assert.assertEquals(1, cNodeSmem.getAllLinkedMaskTest());
        Assert.assertEquals(1, cNodeSmem.getLinkedNodeMask());
        wm.insert(new LinkingTest.A());
        wm.flushPropagations();
        Assert.assertTrue(pmemr2.isRuleLinked());
        Assert.assertTrue(pmemr3.isRuleLinked());
        wm.delete(bFh);// retract B does not unlink the rule

        wm.delete(cFh);// retract C does not unlink the rule

        wm.flushPropagations();
        Assert.assertEquals(3, pmemr2.getLinkedSegmentMask());// b segment never unlinks, as it has no impact on path unlinking anyway

        Assert.assertTrue(pmemr2.isRuleLinked());
        Assert.assertEquals(3, pmemr3.getLinkedSegmentMask());// b segment never unlinks, as it has no impact on path unlinking anyway

        Assert.assertTrue(pmemr3.isRuleLinked());
    }
}

