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
package com.google.security.zynamics.binnavi.API.disassembly;


import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntLoadDataException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class ViewGraphHelpersTest {
    private CodeNode m_codeNode;

    private FunctionNode m_functionNode;

    private TextNode m_textNode;

    private ViewGraph m_graph;

    private View m_view;

    @Test
    public void testGetCodeNode() {
        Assert.assertEquals(m_codeNode, ViewGraphHelpers.getCodeNode(m_graph, new Address(291)));
        Assert.assertNull(ViewGraphHelpers.getCodeNode(m_graph, new Address(292)));
        Assert.assertEquals(m_codeNode, ViewGraphHelpers.getCodeNode(m_graph, 291));
        Assert.assertNull(ViewGraphHelpers.getCodeNode(m_graph, 292));
    }

    @Test
    public void testGetCodeNodes() {
        final List<CodeNode> codeNodes = ViewGraphHelpers.getCodeNodes(m_graph);
        Assert.assertEquals(1, codeNodes.size());
        Assert.assertEquals(m_codeNode, codeNodes.get(0));
    }

    @Test
    public void testGetFunctionNode() {
        Assert.assertEquals(m_functionNode, ViewGraphHelpers.getFunctionNode(m_graph, "Mock Function"));
        Assert.assertNull(ViewGraphHelpers.getFunctionNode(m_graph, "Sock Function"));
    }

    @Test
    public void testGetFunctionNodes() {
        final List<FunctionNode> functionNodes = ViewGraphHelpers.getFunctionNodes(m_graph);
        Assert.assertEquals(1, functionNodes.size());
        Assert.assertEquals(m_functionNode, functionNodes.get(0));
    }

    @Test
    public void testGetInstruction() {
        Assert.assertEquals(m_codeNode.getInstructions().get(0), ViewGraphHelpers.getInstruction(m_graph, new Address(291)));
        Assert.assertNull(ViewGraphHelpers.getInstruction(m_graph, new Address(297)));
        Assert.assertEquals(m_codeNode.getInstructions().get(0), ViewGraphHelpers.getInstruction(m_graph, 291));
        Assert.assertNull(ViewGraphHelpers.getInstruction(m_graph, 297));
    }

    @Test
    public void testInline() throws PartialLoadException, CouldntLoadDataException {
        m_view.load();
        final InliningResult result = ViewGraphHelpers.inlineFunctionCall(m_view, m_codeNode, m_codeNode.getInstructions().get(1), m_functionNode.getFunction());
        Assert.assertNotNull(result.getFirstNode());
        Assert.assertNotNull(result.getSecondNode());
        Assert.assertEquals(2, result.getFirstNode().getInstructions().size());
        Assert.assertEquals(1, result.getSecondNode().getInstructions().size());
    }
}

