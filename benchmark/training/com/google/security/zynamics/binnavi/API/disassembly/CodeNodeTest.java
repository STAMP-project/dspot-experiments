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


import com.google.security.zynamics.binnavi.API.reil.InternalTranslationException;
import com.google.security.zynamics.binnavi.Common.CommonTestObjects;
import com.google.security.zynamics.binnavi.disassembly.MockInstruction;
import java.awt.Color;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class CodeNodeTest {
    private CodeNode m_node;

    @Test
    public void testAddInstruction() {
        final MockCodeNodeListener listener = new MockCodeNodeListener();
        m_node.addListener(listener);
        final Instruction instruction = new Instruction(new MockInstruction());
        final Instruction clonedInstruction = m_node.addInstruction(instruction);
        m_node.setInstructionColor(instruction, 10000, Color.RED);
        Assert.assertEquals("addedInstruction;", listener.events);
        m_node.removeInstruction(clonedInstruction);
        try {
            m_node.setInstructionColor(clonedInstruction, 10000, Color.RED);
            Assert.fail();
        } catch (final IllegalArgumentException e) {
        }
        Assert.assertEquals("addedInstruction;removedInstruction;", listener.events);
        m_node.removeListener(listener);
    }

    @Test
    public void testConstructor() throws InternalTranslationException {
        Assert.assertEquals(291, m_node.getAddress().toLong());
        Assert.assertNotNull(m_node.getLocalComments());
        Assert.assertEquals(1, m_node.getLocalComments().size());
        Assert.assertEquals(new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(null, CommonTestObjects.TEST_USER_1, null, "Global Comment"), m_node.getLocalComments().get(0));
        Assert.assertEquals(1, m_node.getInstructions().size());
        Assert.assertEquals("123  nop \n", m_node.toString());
        Assert.assertNotNull(m_node.getReilCode());
    }
}

