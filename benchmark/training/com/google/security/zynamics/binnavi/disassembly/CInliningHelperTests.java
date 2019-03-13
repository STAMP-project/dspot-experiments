/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.security.zynamics.binnavi.disassembly;


import EdgeType.ENTER_INLINED_FUNCTION;
import EdgeType.JUMP_CONDITIONAL_FALSE;
import EdgeType.JUMP_CONDITIONAL_TRUE;
import EdgeType.JUMP_UNCONDITIONAL;
import EdgeType.LEAVE_INLINED_FUNCTION;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.disassembly.Modules.CModule;
import com.google.security.zynamics.binnavi.disassembly.views.INaviView;
import com.google.security.zynamics.zylib.disassembly.ReferenceType;
import java.math.BigInteger;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class CInliningHelperTests {
    private CFunction m_function;

    private INaviView m_view;

    private CModule m_module;

    private MockSqlProvider m_sql;

    @Test
    public void testCodeNodeEnd() {
        // In this test we split a code node at its last instruction
        final CInstruction instruction1 = MockCreator.createInstructionWithOperand(new BigInteger("1234"), m_module, m_sql);
        final CInstruction instruction2 = MockCreator.createInstructionWithOperand(new BigInteger("1235"), m_module, m_sql);
        final CInstruction instruction3 = MockCreator.createInstructionWithOperand(new BigInteger("1236"), m_module, m_sql);
        final INaviCodeNode codeNode1 = m_view.getContent().createCodeNode(m_function, Lists.newArrayList(instruction1, instruction2, instruction3));
        final INaviCodeNode codeNode2 = m_view.getContent().createCodeNode(m_function, Lists.newArrayList(instruction1, instruction2, instruction3));
        final INaviCodeNode codeNode3 = m_view.getContent().createCodeNode(m_function, Lists.newArrayList(instruction1, instruction2, instruction3));
        m_view.getContent().createEdge(codeNode1, codeNode2, JUMP_CONDITIONAL_TRUE);
        m_view.getContent().createEdge(codeNode2, codeNode3, JUMP_CONDITIONAL_FALSE);
        CInliningHelper.inlineCodeNode(m_view, codeNode2, Iterables.get(codeNode2.getInstructions(), 2), m_function);
        // The block is not split + 5 nodes from the inlined function
        Assert.assertEquals((3 + 5), m_view.getNodeCount());
        final List<INaviViewNode> blocks = m_view.getGraph().getNodes();
        final INaviCodeNode startNode = ((INaviCodeNode) (findStartNode(blocks)));
        Assert.assertEquals(3, Iterables.size(startNode.getInstructions()));
        Assert.assertEquals(instruction1, Iterables.get(startNode.getInstructions(), 0));
        Assert.assertEquals(instruction2, Iterables.get(startNode.getInstructions(), 1));
        Assert.assertEquals(instruction3, Iterables.get(startNode.getInstructions(), 2));
        Assert.assertEquals(1, startNode.getIncomingEdges().size());
        Assert.assertEquals(JUMP_CONDITIONAL_TRUE, startNode.getIncomingEdges().get(0).getType());
        Assert.assertEquals(1, startNode.getOutgoingEdges().size());
        Assert.assertEquals(ENTER_INLINED_FUNCTION, startNode.getOutgoingEdges().get(0).getType());
        final INaviViewNode firstInlinedNode = startNode.getOutgoingEdges().get(0).getTarget();
        Assert.assertEquals(m_function.getAddress(), Iterables.getFirst(getInstructions(), null).getAddress());
        Assert.assertEquals(1, firstInlinedNode.getIncomingEdges().size());
        Assert.assertEquals(1, firstInlinedNode.getOutgoingEdges().size());
        final INaviViewNode middleInlinedNode = firstInlinedNode.getOutgoingEdges().get(0).getTarget();
        Assert.assertEquals(2, middleInlinedNode.getIncomingEdges().size());
        Assert.assertEquals(2, middleInlinedNode.getOutgoingEdges().size());
        final INaviViewNode firstReturnNode = middleInlinedNode.getOutgoingEdges().get(0).getTarget();
        final INaviViewNode secondReturnNode = middleInlinedNode.getOutgoingEdges().get(1).getTarget();
        Assert.assertEquals(1, firstReturnNode.getIncomingEdges().size());
        Assert.assertEquals(1, firstReturnNode.getOutgoingEdges().size());
        Assert.assertEquals(LEAVE_INLINED_FUNCTION, firstReturnNode.getOutgoingEdges().get(0).getType());
        Assert.assertEquals(1, secondReturnNode.getIncomingEdges().size());
        Assert.assertEquals(1, secondReturnNode.getOutgoingEdges().size());
        Assert.assertEquals(LEAVE_INLINED_FUNCTION, secondReturnNode.getOutgoingEdges().get(0).getType());
    }

    @Test
    public void testCodeNodeMiddle() {
        // In this test we split a code node right in the middle of the node
        final CInstruction instruction1 = MockCreator.createInstructionWithOperand(new BigInteger("1234"), m_module, m_sql);
        final CInstruction instruction2 = MockCreator.createInstructionWithOperand(new BigInteger("1235"), m_module, m_sql);
        final CInstruction instruction3 = MockCreator.createInstructionWithOperand(new BigInteger("1236"), m_module, m_sql);
        final INaviCodeNode codeNode1 = m_view.getContent().createCodeNode(m_function, Lists.newArrayList(instruction1, instruction2, instruction3));
        final INaviCodeNode codeNode2 = m_view.getContent().createCodeNode(m_function, Lists.newArrayList(instruction1, instruction2, instruction3));
        final INaviCodeNode codeNode3 = m_view.getContent().createCodeNode(m_function, Lists.newArrayList(instruction1, instruction2, instruction3));
        m_view.getContent().createEdge(codeNode1, codeNode2, JUMP_CONDITIONAL_TRUE);
        m_view.getContent().createEdge(codeNode2, codeNode3, JUMP_CONDITIONAL_FALSE);
        CInliningHelper.inlineCodeNode(m_view, codeNode2, Iterables.get(codeNode2.getInstructions(), 1), m_function);
        // The block is split + 5 nodes from the inlined function
        Assert.assertEquals((((2 + 1) + 5) + 1), m_view.getNodeCount());
        Assert.assertEquals(((2 + 5) + 2), m_view.getEdgeCount());
        final List<INaviViewNode> blocks = m_view.getGraph().getNodes();
        final INaviCodeNode startNode = ((INaviCodeNode) (findStartNode(blocks)));
        Assert.assertEquals(2, Iterables.size(startNode.getInstructions()));
        Assert.assertEquals(instruction1, Iterables.get(startNode.getInstructions(), 0));
        Assert.assertEquals(instruction2, Iterables.get(startNode.getInstructions(), 1));
        Assert.assertEquals(1, startNode.getIncomingEdges().size());
        Assert.assertEquals(JUMP_CONDITIONAL_TRUE, startNode.getIncomingEdges().get(0).getType());
        Assert.assertEquals(1, startNode.getOutgoingEdges().size());
        Assert.assertEquals(ENTER_INLINED_FUNCTION, startNode.getOutgoingEdges().get(0).getType());
        final INaviViewNode firstInlinedNode = startNode.getOutgoingEdges().get(0).getTarget();
        Assert.assertEquals(m_function.getAddress(), Iterables.get(getInstructions(), 0).getAddress());
        Assert.assertEquals(1, firstInlinedNode.getIncomingEdges().size());
        Assert.assertEquals(1, firstInlinedNode.getOutgoingEdges().size());
        final INaviViewNode middleInlinedNode = firstInlinedNode.getOutgoingEdges().get(0).getTarget();
        Assert.assertEquals(2, middleInlinedNode.getIncomingEdges().size());
        Assert.assertEquals(2, middleInlinedNode.getOutgoingEdges().size());
        final INaviViewNode firstReturnNode = middleInlinedNode.getOutgoingEdges().get(0).getTarget();
        final INaviViewNode secondReturnNode = middleInlinedNode.getOutgoingEdges().get(1).getTarget();
        Assert.assertEquals(1, firstReturnNode.getIncomingEdges().size());
        Assert.assertEquals(1, firstReturnNode.getOutgoingEdges().size());
        Assert.assertEquals(LEAVE_INLINED_FUNCTION, firstReturnNode.getOutgoingEdges().get(0).getType());
        Assert.assertEquals(1, secondReturnNode.getIncomingEdges().size());
        Assert.assertEquals(1, secondReturnNode.getOutgoingEdges().size());
        Assert.assertEquals(LEAVE_INLINED_FUNCTION, secondReturnNode.getOutgoingEdges().get(0).getType());
    }

    @Test
    public void testCodeNodeStart() {
        // In this test we split a code node at its very first instruction
        final CInstruction instruction1 = MockCreator.createInstructionWithOperand(new BigInteger("1234"), m_module, m_sql);
        final CInstruction instruction2 = MockCreator.createInstructionWithOperand(new BigInteger("1235"), m_module, m_sql);
        final CInstruction instruction3 = MockCreator.createInstructionWithOperand(new BigInteger("1236"), m_module, m_sql);
        final INaviCodeNode codeNode1 = m_view.getContent().createCodeNode(m_function, Lists.newArrayList(instruction1, instruction2, instruction3));
        final INaviCodeNode codeNode2 = m_view.getContent().createCodeNode(m_function, Lists.newArrayList(instruction1, instruction2, instruction3));
        final INaviCodeNode codeNode3 = m_view.getContent().createCodeNode(m_function, Lists.newArrayList(instruction1, instruction2, instruction3));
        m_view.getContent().createEdge(codeNode1, codeNode2, JUMP_CONDITIONAL_TRUE);
        m_view.getContent().createEdge(codeNode2, codeNode3, JUMP_CONDITIONAL_FALSE);
        CInliningHelper.inlineCodeNode(m_view, codeNode2, Iterables.get(codeNode2.getInstructions(), 0), m_function);
        // The block is split + 5 nodes from the inlined function
        Assert.assertEquals((((2 + 1) + 5) + 1), m_view.getNodeCount());
        final List<INaviViewNode> blocks = m_view.getGraph().getNodes();
        final INaviCodeNode startNode = ((INaviCodeNode) (findStartNode(blocks)));
        Assert.assertEquals(1, Iterables.size(startNode.getInstructions()));
        Assert.assertEquals(instruction1, Iterables.get(startNode.getInstructions(), 0));
        Assert.assertEquals(1, startNode.getIncomingEdges().size());
        Assert.assertEquals(JUMP_CONDITIONAL_TRUE, startNode.getIncomingEdges().get(0).getType());
        Assert.assertEquals(1, startNode.getOutgoingEdges().size());
        Assert.assertEquals(ENTER_INLINED_FUNCTION, startNode.getOutgoingEdges().get(0).getType());
        final INaviViewNode firstInlinedNode = startNode.getOutgoingEdges().get(0).getTarget();
        Assert.assertEquals(m_function.getAddress(), Iterables.get(getInstructions(), 0).getAddress());
        Assert.assertEquals(1, firstInlinedNode.getIncomingEdges().size());
        Assert.assertEquals(1, firstInlinedNode.getOutgoingEdges().size());
        final INaviViewNode middleInlinedNode = firstInlinedNode.getOutgoingEdges().get(0).getTarget();
        Assert.assertEquals(2, middleInlinedNode.getIncomingEdges().size());
        Assert.assertEquals(2, middleInlinedNode.getOutgoingEdges().size());
        final INaviViewNode firstReturnNode = middleInlinedNode.getOutgoingEdges().get(0).getTarget();
        final INaviViewNode secondReturnNode = middleInlinedNode.getOutgoingEdges().get(1).getTarget();
        Assert.assertEquals(1, firstReturnNode.getIncomingEdges().size());
        Assert.assertEquals(1, firstReturnNode.getOutgoingEdges().size());
        Assert.assertEquals(LEAVE_INLINED_FUNCTION, firstReturnNode.getOutgoingEdges().get(0).getType());
        Assert.assertEquals(1, secondReturnNode.getIncomingEdges().size());
        Assert.assertEquals(1, secondReturnNode.getOutgoingEdges().size());
        Assert.assertEquals(LEAVE_INLINED_FUNCTION, secondReturnNode.getOutgoingEdges().get(0).getType());
    }

    @Test
    public void testFunctionNode() throws CouldntSaveDataException {
        final CFunction function1 = MockCreator.createFunction(m_module, m_sql);
        @SuppressWarnings("unused")
        final CFunction function2 = MockCreator.createFunction(m_module, m_sql);
        final CFunction function3 = MockCreator.createFunction(m_module, m_sql);
        final CFunctionNode functionNode1 = m_view.getContent().createFunctionNode(function1);
        final CFunctionNode functionNode2 = m_view.getContent().createFunctionNode(m_function);
        final CFunctionNode functionNode3 = m_view.getContent().createFunctionNode(function3);
        m_view.getContent().createEdge(functionNode1, functionNode2, JUMP_UNCONDITIONAL);
        m_view.getContent().createEdge(functionNode2, functionNode3, JUMP_UNCONDITIONAL);
        Iterables.get(getInstructions(), 0).getOperands().get(0).getRootNode().addReference(new CReference(function1.getAddress(), ReferenceType.UNCONDITIONAL));
        CInliningHelper.inlineFunctionNode(m_view, functionNode2);
        Assert.assertEquals((2 + 5), m_view.getNodeCount());
    }

    @Test
    public void testRecursiveNode() {
        // In this test we split a code node that has a jump back to its start
        final CInstruction instruction11 = MockCreator.createInstructionWithOperand(new BigInteger("1234"), m_module, m_sql);
        final CInstruction instruction12 = MockCreator.createInstructionWithOperand(new BigInteger("1235"), m_module, m_sql);
        final CInstruction instruction13 = MockCreator.createInstructionWithOperand(new BigInteger("1236"), m_module, m_sql);
        final CInstruction instruction21 = MockCreator.createInstructionWithOperand(new BigInteger("1237"), m_module, m_sql);
        final CInstruction instruction22 = MockCreator.createInstructionWithOperand(new BigInteger("1238"), m_module, m_sql);
        final CInstruction instruction23 = MockCreator.createInstructionWithOperand(new BigInteger("1239"), m_module, m_sql);
        final CInstruction instruction31 = MockCreator.createInstructionWithOperand(new BigInteger("1240"), m_module, m_sql);
        final CInstruction instruction32 = MockCreator.createInstructionWithOperand(new BigInteger("1241"), m_module, m_sql);
        final CInstruction instruction33 = MockCreator.createInstructionWithOperand(new BigInteger("1242"), m_module, m_sql);
        final INaviCodeNode codeNode1 = m_view.getContent().createCodeNode(m_function, Lists.newArrayList(instruction11, instruction12, instruction13));
        final INaviCodeNode codeNode2 = m_view.getContent().createCodeNode(m_function, Lists.newArrayList(instruction21, instruction22, instruction23));
        final INaviCodeNode codeNode3 = m_view.getContent().createCodeNode(m_function, Lists.newArrayList(instruction31, instruction32, instruction33));
        m_view.getContent().createEdge(codeNode1, codeNode2, JUMP_CONDITIONAL_TRUE);
        m_view.getContent().createEdge(codeNode2, codeNode2, JUMP_CONDITIONAL_FALSE);
        m_view.getContent().createEdge(codeNode2, codeNode3, JUMP_UNCONDITIONAL);
        Assert.assertEquals(3, m_view.getEdgeCount());
        CInliningHelper.inlineCodeNode(m_view, codeNode2, Iterables.get(codeNode2.getInstructions(), 1), m_function);
        // The block is split + 5 nodes from the inlined function
        Assert.assertEquals((((2 + 1) + 5) + 1), m_view.getNodeCount());
        Assert.assertEquals(((3 + 4) + 3), m_view.getEdgeCount());
        final List<INaviViewNode> blocks = m_view.getGraph().getNodes();
        final INaviCodeNode startNode = ((INaviCodeNode) (findStartNode(blocks)));
        final INaviViewNode returnInlinedNode = findNodeWithLeaveInlinedFunction(blocks);
        // The first code node remains untouched by the inlining
        Assert.assertEquals(0, codeNode1.getIncomingEdges().size());
        Assert.assertEquals(1, codeNode1.getOutgoingEdges().size());
        Assert.assertEquals(startNode, codeNode1.getOutgoingEdges().get(0).getTarget());
        // The start node is split at the second instruction
        Assert.assertEquals(2, Iterables.size(startNode.getInstructions()));
        Assert.assertEquals(instruction21, Iterables.get(startNode.getInstructions(), 0));
        Assert.assertEquals(instruction22, Iterables.get(startNode.getInstructions(), 1));
        Assert.assertEquals(2, startNode.getIncomingEdges().size());
        Assert.assertEquals(JUMP_CONDITIONAL_TRUE, startNode.getIncomingEdges().get(0).getType());
        Assert.assertEquals(JUMP_CONDITIONAL_FALSE, startNode.getIncomingEdges().get(1).getType());
        Assert.assertEquals(codeNode1, startNode.getIncomingEdges().get(0).getSource());
        Assert.assertEquals(returnInlinedNode, startNode.getIncomingEdges().get(1).getSource());
        Assert.assertEquals(1, startNode.getOutgoingEdges().size());
        Assert.assertEquals(ENTER_INLINED_FUNCTION, startNode.getOutgoingEdges().get(0).getType());
        // Check the inlined nodes
        final INaviViewNode firstInlinedNode = startNode.getOutgoingEdges().get(0).getTarget();
        Assert.assertEquals(m_function.getAddress(), Iterables.get(getInstructions(), 0).getAddress());
        Assert.assertEquals(1, firstInlinedNode.getIncomingEdges().size());
        Assert.assertEquals(1, firstInlinedNode.getOutgoingEdges().size());
        final INaviViewNode middleInlinedNode = firstInlinedNode.getOutgoingEdges().get(0).getTarget();
        Assert.assertEquals(2, middleInlinedNode.getIncomingEdges().size());
        Assert.assertEquals(2, middleInlinedNode.getOutgoingEdges().size());
        final INaviViewNode firstReturnNode = middleInlinedNode.getOutgoingEdges().get(0).getTarget();
        final INaviViewNode secondReturnNode = middleInlinedNode.getOutgoingEdges().get(1).getTarget();
        Assert.assertEquals(1, firstReturnNode.getIncomingEdges().size());
        Assert.assertEquals(1, firstReturnNode.getOutgoingEdges().size());
        Assert.assertEquals(LEAVE_INLINED_FUNCTION, firstReturnNode.getOutgoingEdges().get(0).getType());
        Assert.assertEquals(returnInlinedNode, firstReturnNode.getOutgoingEdges().get(0).getTarget());
        Assert.assertEquals(1, secondReturnNode.getIncomingEdges().size());
        Assert.assertEquals(1, secondReturnNode.getOutgoingEdges().size());
        Assert.assertEquals(LEAVE_INLINED_FUNCTION, secondReturnNode.getOutgoingEdges().get(0).getType());
        // Check the lower half of the split node
        Assert.assertEquals(2, returnInlinedNode.getIncomingEdges().size());
        Assert.assertEquals(firstReturnNode, returnInlinedNode.getIncomingEdges().get(0).getSource());
        Assert.assertEquals(secondReturnNode, returnInlinedNode.getIncomingEdges().get(1).getSource());
        Assert.assertEquals(2, returnInlinedNode.getOutgoingEdges().size());
        Assert.assertEquals(startNode, returnInlinedNode.getOutgoingEdges().get(0).getTarget());
        Assert.assertEquals(codeNode3, returnInlinedNode.getOutgoingEdges().get(1).getTarget());
    }
}

