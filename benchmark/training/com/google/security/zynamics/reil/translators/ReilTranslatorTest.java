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
package com.google.security.zynamics.reil.translators;


import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.security.zynamics.reil.ReilFunction;
import com.google.security.zynamics.reil.ReilGraph;
import com.google.security.zynamics.reil.algorithms.mono2.common.MonoReilSolverResult;
import com.google.security.zynamics.reil.algorithms.mono2.common.enums.AnalysisDirection;
import com.google.security.zynamics.reil.algorithms.mono2.registertracking.RegisterSetLatticeElement;
import com.google.security.zynamics.reil.algorithms.mono2.registertracking.RegisterTracker;
import com.google.security.zynamics.zylib.disassembly.MockBlockContainer;
import com.google.security.zynamics.zylib.disassembly.MockCodeContainer;
import com.google.security.zynamics.zylib.disassembly.MockCodeEdge;
import com.google.security.zynamics.zylib.disassembly.MockInstruction;
import com.google.security.zynamics.zylib.disassembly.MockOperandTree;
import com.google.security.zynamics.zylib.gui.zygraph.edges.EdgeType;
import java.util.ArrayList;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ReilTranslatorTest {
    private static final ReilTranslator<MockInstruction> m_translator = new ReilTranslator<MockInstruction>();

    @Test
    public void testInlinedFunctionGeneration() throws InternalTranslationException {
        final MockBlockContainer container = new MockBlockContainer();
        final MockCodeContainer block1 = new MockCodeContainer();
        block1.m_instructions.add(createMov(4096, "eax", "1"));
        final MockCodeContainer block2 = new MockCodeContainer();
        block2.m_instructions.add(createMov(4608, "ebx", "eax"));
        final MockCodeContainer block3 = new MockCodeContainer();
        block3.m_instructions.add(createMov(4097, "ecx", "ebx"));
        container.m_blocks.add(block1);
        container.m_blocks.add(block2);
        container.m_blocks.add(block3);
        container.m_edges.add(new MockCodeEdge<MockCodeContainer>(block1, block2, EdgeType.ENTER_INLINED_FUNCTION));
        container.m_edges.add(new MockCodeEdge<MockCodeContainer>(block2, block3, EdgeType.LEAVE_INLINED_FUNCTION));
        final ReilFunction function = ReilTranslatorTest.m_translator.translate(new StandardEnvironment(), container);
        System.out.println(function.getGraph().getNodes());
        System.out.println(function.getGraph().getEdges());
        Assert.assertEquals(3, function.getGraph().getNodes().size());
        Assert.assertEquals(2, function.getGraph().getEdges().size());
        final MonoReilSolverResult<RegisterSetLatticeElement> result = RegisterTracker.track(function, Iterables.getFirst(block1.getInstructions(), null), "eax", new com.google.security.zynamics.reil.algorithms.mono2.registertracking.RegisterTrackingOptions(true, new HashSet<String>(), true, AnalysisDirection.DOWN));
        System.out.println(result);
    }

    @Test
    public void testInlinedFunctionGeneration2() throws InternalTranslationException {
        final MockBlockContainer container = new MockBlockContainer();
        final MockCodeContainer block1 = new MockCodeContainer();
        block1.m_instructions.add(createPush(4096, "eax"));
        block1.m_instructions.add(createMov(4097, "edx", "3"));
        block1.m_instructions.add(createCall(4098));
        final MockCodeContainer block2 = new MockCodeContainer();
        block2.m_instructions.add(createPush(9472, "ebx"));
        final MockCodeContainer block3 = new MockCodeContainer();
        block3.m_instructions.add(createPush(4099, "ecx"));
        container.m_blocks.add(block1);
        container.m_blocks.add(block2);
        container.m_blocks.add(block3);
        final MockCodeEdge<MockCodeContainer> edge1 = new MockCodeEdge<MockCodeContainer>(block1, block2, EdgeType.ENTER_INLINED_FUNCTION);
        final MockCodeEdge<MockCodeContainer> edge2 = new MockCodeEdge<MockCodeContainer>(block2, block3, EdgeType.LEAVE_INLINED_FUNCTION);
        block1.m_outgoingEdges.add(edge1);
        container.m_edges.add(edge1);
        container.m_edges.add(edge2);
        final ReilFunction function = ReilTranslatorTest.m_translator.translate(new StandardEnvironment(), container);
        System.out.println(function.getGraph().getEdges());
        Assert.assertEquals(3, function.getGraph().getNodes().size());
        Assert.assertEquals(2, function.getGraph().getEdges().size());
        final MonoReilSolverResult<RegisterSetLatticeElement> result = RegisterTracker.track(function, Iterables.get(block1.getInstructions(), 0), "esp", new com.google.security.zynamics.reil.algorithms.mono2.registertracking.RegisterTrackingOptions(true, new HashSet<String>(), true, AnalysisDirection.DOWN));
        System.out.println(result);
    }

    @Test
    public void testRepStosStos() throws InternalTranslationException {
        final MockCodeContainer container = new MockCodeContainer();
        container.m_instructions.add(new MockInstruction(256, "rep stosb", Lists.newArrayList(new MockOperandTree(), new MockOperandTree())));
        container.m_instructions.add(new MockInstruction(512, "stosb", new ArrayList<MockOperandTree>()));
        final ReilGraph g = ReilTranslatorTest.m_translator.translate(new StandardEnvironment(), container);
        System.out.println(g);
        Assert.assertEquals(9, g.nodeCount());
        Assert.assertEquals(11, g.edgeCount());
    }

    @Test
    public void testSimple() throws InternalTranslationException {
        final ReilGraph g = ReilTranslatorTest.m_translator.translate(new StandardEnvironment(), new MockInstruction("nop", new ArrayList<MockOperandTree>()));
        Assert.assertEquals(1, g.nodeCount());
        Assert.assertEquals(0, g.edgeCount());
    }

    @Test
    public void testStos() throws InternalTranslationException {
        final ReilGraph g = ReilTranslatorTest.m_translator.translate(new StandardEnvironment(), new MockInstruction("stosb", new ArrayList<MockOperandTree>()));
        System.out.println(g.getNodes().get(0).getInstructions());
        System.out.println(g.getNodes().get(1).getInstructions());
        System.out.println(g.getNodes().get(2).getInstructions());
        System.out.println(g.getNodes().get(3).getInstructions());
        System.out.println(g.getEdges());
        Assert.assertEquals(4, g.nodeCount());
        Assert.assertEquals(4, g.edgeCount());
    }
}

