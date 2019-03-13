/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler;


import LeftInputAdapterNode.LiaNodeMemory;
import ResourceType.DRL;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.NodeMemories;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.Rete;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.SegmentMemory;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;


public class MemoryLeakTest {
    @Test
    public void testStagedTupleLeak() throws Exception {
        // BZ-1056599
        String str = "rule R1 when\n" + ((((((((((((((("    $i : Integer()\n" + "then\n") + "    insertLogical( $i.toString() );\n") + "end\n") + "\n") + "rule R2 when\n") + "    $i : Integer()\n") + "then\n") + "    delete( $i );\n") + "end\n") + "\n") + "rule R3 when\n") + "    $l : Long()\n") + "    $s : String( this == $l.toString() )\n") + "then\n") + "end\n");
        KieBase kbase = new KieHelper().addContent(str, DRL).build();
        KieSession ksession = kbase.newKieSession();
        for (int i = 0; i < 10; i++) {
            ksession.insert(i);
            ksession.fireAllRules();
        }
        Rete rete = getRete();
        JoinNode joinNode = null;
        for (ObjectTypeNode otn : rete.getObjectTypeNodes()) {
            if ((String.class) == (otn.getObjectType().getValueType().getClassType())) {
                joinNode = ((JoinNode) (otn.getObjectSinkPropagator().getSinks()[0]));
                break;
            }
        }
        Assert.assertNotNull(joinNode);
        InternalWorkingMemory wm = ((InternalWorkingMemory) (ksession));
        BetaMemory memory = ((BetaMemory) (wm.getNodeMemory(joinNode)));
        TupleSets<RightTuple> stagedRightTuples = memory.getStagedRightTuples();
        Assert.assertNull(stagedRightTuples.getDeleteFirst());
        Assert.assertNull(stagedRightTuples.getInsertFirst());
    }

    @Test
    public void testStagedLeftTupleLeak() throws Exception {
        // BZ-1058874
        String str = "rule R1 when\n" + ((("    String( this == \"this\" )\n" + "    String( this == \"that\" )\n") + "then\n") + "end\n");
        KieBase kbase = new KieHelper().addContent(str, DRL).build();
        KieSession ksession = kbase.newKieSession();
        ksession.fireAllRules();
        for (int i = 0; i < 10; i++) {
            FactHandle fh = ksession.insert("this");
            ksession.fireAllRules();
            ksession.delete(fh);
            ksession.fireAllRules();
        }
        Rete rete = getRete();
        LeftInputAdapterNode liaNode = null;
        for (ObjectTypeNode otn : rete.getObjectTypeNodes()) {
            if ((String.class) == (otn.getObjectType().getValueType().getClassType())) {
                AlphaNode alphaNode = ((AlphaNode) (otn.getObjectSinkPropagator().getSinks()[0]));
                liaNode = ((LeftInputAdapterNode) (alphaNode.getObjectSinkPropagator().getSinks()[0]));
                break;
            }
        }
        Assert.assertNotNull(liaNode);
        InternalWorkingMemory wm = ((InternalWorkingMemory) (ksession));
        LeftInputAdapterNode.LiaNodeMemory memory = ((LeftInputAdapterNode.LiaNodeMemory) (wm.getNodeMemory(liaNode)));
        TupleSets<LeftTuple> stagedLeftTuples = memory.getSegmentMemory().getStagedLeftTuples();
        Assert.assertNull(stagedLeftTuples.getDeleteFirst());
        Assert.assertNull(stagedLeftTuples.getInsertFirst());
    }

    @Test
    public void testBetaMemoryLeakOnFactDelete() {
        // DROOLS-913
        String drl = "rule R1 when\n" + (((((((((("    $a : Integer(this == 1)\n" + "    $b : String()\n") + "    $c : Integer(this == 2)\n") + "then \n") + "end\n") + "rule R2 when\n") + "    $a : Integer(this == 1)\n") + "    $b : String()\n") + "    $c : Integer(this == 3)\n") + "then \n") + "end\n");
        KieSession ksession = new KieHelper().addContent(drl, DRL).build().newKieSession();
        FactHandle fh1 = ksession.insert(1);
        FactHandle fh2 = ksession.insert(3);
        FactHandle fh3 = ksession.insert("test");
        ksession.fireAllRules();
        ksession.delete(fh1);
        ksession.delete(fh2);
        ksession.delete(fh3);
        ksession.fireAllRules();
        NodeMemories nodeMemories = getNodeMemories();
        for (int i = 0; i < (nodeMemories.length()); i++) {
            Memory memory = nodeMemories.peekNodeMemory(i);
            if ((memory != null) && ((memory.getSegmentMemory()) != null)) {
                SegmentMemory segmentMemory = memory.getSegmentMemory();
                System.out.println(memory);
                LeftTuple deleteFirst = memory.getSegmentMemory().getStagedLeftTuples().getDeleteFirst();
                if ((segmentMemory.getRootNode()) instanceof JoinNode) {
                    BetaMemory bm = ((BetaMemory) (segmentMemory.getNodeMemories().getFirst()));
                    Assert.assertEquals(0, bm.getLeftTupleMemory().size());
                }
                System.out.println(deleteFirst);
                Assert.assertNull(deleteFirst);
            }
        }
    }
}

