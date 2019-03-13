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


import ExpressionType.ImmediateInteger;
import ExpressionType.Register;
import com.google.common.collect.Lists;
import com.google.security.zynamics.binnavi.API.reil.InternalTranslationException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntLoadDataException;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockDatabase;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.Tagging.TagType;
import com.google.security.zynamics.binnavi.debug.debugger.DebuggerTemplate;
import com.google.security.zynamics.binnavi.disassembly.CFunction;
import com.google.security.zynamics.binnavi.disassembly.MockView;
import com.google.security.zynamics.binnavi.disassembly.Modules.CModule;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import com.google.security.zynamics.zylib.disassembly.FunctionType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class ModuleTest {
    private Module m_module;

    @Test
    public void testConstructor() {
        final MockSqlProvider provider = new MockSqlProvider();
        final Date creationDate = new Date();
        final Date modificationDate = new Date();
        final CModule internalModule = new CModule(123, "Name", "Comment", creationDate, modificationDate, "12345678123456781234567812345678", "1234567812345678123456781234567812345678", 55, 66, new CAddress(1365), new CAddress(1638), new DebuggerTemplate(1, "Mock Debugger", "localhaus", 88, provider), null, Integer.MAX_VALUE, false, provider);
        final TagManager nodeTagManager = new TagManager(new com.google.security.zynamics.binnavi.Tagging.CTagManager(new com.google.security.zynamics.zylib.types.trees.Tree<com.google.security.zynamics.binnavi.Tagging.CTag>(new com.google.security.zynamics.zylib.types.trees.TreeNode<com.google.security.zynamics.binnavi.Tagging.CTag>(new com.google.security.zynamics.binnavi.Tagging.CTag(0, "", "", TagType.NODE_TAG, provider))), TagType.NODE_TAG, provider));
        final TagManager viewTagManager = new TagManager(new com.google.security.zynamics.binnavi.Tagging.CTagManager(new com.google.security.zynamics.zylib.types.trees.Tree<com.google.security.zynamics.binnavi.Tagging.CTag>(new com.google.security.zynamics.zylib.types.trees.TreeNode<com.google.security.zynamics.binnavi.Tagging.CTag>(new com.google.security.zynamics.binnavi.Tagging.CTag(0, "", "", TagType.VIEW_TAG, provider))), TagType.VIEW_TAG, provider));
        final Database db = new Database(new MockDatabase());
        final Module module = new Module(db, internalModule, nodeTagManager, viewTagManager);
        Assert.assertEquals("Name", module.getName());
        Assert.assertEquals("Comment", module.getDescription());
        Assert.assertNotSame(creationDate, module.getCreationDate());
        Assert.assertNotSame(modificationDate, module.getModificationDate());
        Assert.assertTrue(creationDate.equals(module.getCreationDate()));
        Assert.assertTrue(modificationDate.equals(module.getModificationDate()));
        Assert.assertEquals(db, module.getDatabase());
        Assert.assertNotNull(module.getDebugger());
        Assert.assertEquals(1365, module.getFilebase().toLong());
        Assert.assertEquals(1638, module.getImagebase().toLong());
        Assert.assertEquals("12345678123456781234567812345678", module.getMD5());
        Assert.assertEquals("1234567812345678123456781234567812345678", module.getSHA1());
        Assert.assertEquals("Module 'Name'", module.toString());
    }

    @Test
    public void testCreateInstruction() throws InternalTranslationException {
        final Address address = new Address(256);
        final String mnemonic = "mov";
        final byte[] data = new byte[]{ 1, 2, 3, 4 };
        final List<Operand> operands = new ArrayList<Operand>();
        final OperandExpression ex2 = OperandExpression.create(m_module, "eax", Register);
        final OperandExpression ex4 = OperandExpression.create(m_module, "123", ImmediateInteger);
        operands.add(Operand.create(m_module, ex2));
        operands.add(Operand.create(m_module, ex4));
        Assert.assertEquals("dword", operands.get(0).getRootNode().getValue());
        Assert.assertEquals(1, operands.get(0).getRootNode().getChildren().size());
        Assert.assertEquals("eax", operands.get(0).getRootNode().getChildren().get(0).getValue());
        Assert.assertEquals("dword", operands.get(1).getRootNode().getValue());
        Assert.assertEquals(1, operands.get(1).getRootNode().getChildren().size());
        Assert.assertEquals("123", operands.get(1).getRootNode().getChildren().get(0).getValue());
        final Instruction instruction = Instruction.create(m_module, address, mnemonic, operands, data, "x86-32");
        Assert.assertEquals("dword", instruction.getOperands().get(0).getRootNode().getValue());
        Assert.assertEquals(1, instruction.getOperands().get(0).getRootNode().getChildren().size());
        Assert.assertEquals("eax", instruction.getOperands().get(0).getRootNode().getChildren().get(0).getValue());
        Assert.assertEquals("dword", instruction.getOperands().get(1).getRootNode().getValue());
        Assert.assertEquals(1, instruction.getOperands().get(1).getRootNode().getChildren().size());
        Assert.assertEquals("123", instruction.getOperands().get(1).getRootNode().getChildren().get(0).getValue());
        instruction.getReilCode();
    }

    @Test
    public void testCreateView() throws CouldntDeleteException, CouldntSaveDataException, InternalTranslationException, CouldntLoadDataException {
        final List<Instruction> instructions = Lists.newArrayList(createInstruction(m_module, 8192, "mov", "edi", "edi"), createInstruction(m_module, 8193, "push", "ebp"), createInstruction(m_module, 8194, "mov", "ebp", "esp"), createInstruction(m_module, 8195, "mov", "eax", "123"), createInstruction(m_module, 8196, "add", "eax", "ecx"));
        for (final Instruction instruction : instructions) {
            instruction.getReilCode();
        }
        m_module.load();
        Assert.assertNotNull(m_module.getFunction(m_module.getViews().get(1)));
        final View view = m_module.createView("Empty View", "");
        final Function function = m_module.getFunctions().get(0);
        view.createCodeNode(function, instructions);
        view.save();
        final MockModuleListener listener = new MockModuleListener();
        m_module.addListener(listener);
        Assert.assertEquals(3, m_module.getViews().size());
        m_module.deleteView(view);
        Assert.assertEquals("deletedView;", listener.events);
        Assert.assertEquals(2, m_module.getViews().size());
        Assert.assertFalse(m_module.getViews().contains(view));
        m_module.close();
    }

    @Test
    public void testSetDebuggerTemplate() throws CouldntSaveDataException {
        final MockModuleListener listener = new MockModuleListener();
        m_module.addListener(listener);
        final DebuggerTemplate template = m_module.getDatabase().getDebuggerTemplateManager().createDebuggerTemplate("New Debugger", "localhaus", 88);
        m_module.setDebuggerTemplate(template);
        Assert.assertEquals(template, m_module.getDebuggerTemplate());
        Assert.assertEquals("changedDebuggerTemplate;changedDebugger;changedModificationDate;", listener.events);
        m_module.removeListener(listener);
    }

    @Test
    public void testSetDescription() throws CouldntSaveDataException {
        final MockModuleListener listener = new MockModuleListener();
        m_module.addListener(listener);
        m_module.setDescription("New Description");
        Assert.assertEquals("New Description", m_module.getDescription());
        Assert.assertEquals("changedDescription;changedModificationDate;", listener.events);
        m_module.removeListener(listener);
    }

    @Test
    public void testSetFilebase() throws CouldntSaveDataException {
        final MockModuleListener listener = new MockModuleListener();
        m_module.addListener(listener);
        m_module.setFilebase(new Address(256));
        Assert.assertEquals(256, m_module.getFilebase().toLong());
        Assert.assertEquals("changedFilebase;changedModificationDate;", listener.events);
        m_module.removeListener(listener);
    }

    @Test
    public void testSetImagebase() throws CouldntSaveDataException {
        final MockModuleListener listener = new MockModuleListener();
        m_module.addListener(listener);
        m_module.setImagebase(new Address(256));
        Assert.assertEquals(256, m_module.getImagebase().toLong());
        Assert.assertEquals("changedImagebase;changedModificationDate;", listener.events);
        m_module.removeListener(listener);
    }

    @Test
    public void testSetName() throws CouldntSaveDataException {
        final MockModuleListener listener = new MockModuleListener();
        m_module.addListener(listener);
        m_module.setName("New Name");
        Assert.assertEquals("New Name", m_module.getName());
        Assert.assertEquals("changedName;changedModificationDate;", listener.events);
        m_module.removeListener(listener);
    }

    @Test
    public void testUnloaded() {
        m_module.close();
        try {
            m_module.getCallgraph();
            Assert.fail();
        } catch (final IllegalStateException exception) {
        }
        try {
            final MockSqlProvider provider = new MockSqlProvider();
            final CModule internalModule = new CModule(1, "", "", new Date(), new Date(), "00000000000000000000000000000000", "0000000000000000000000000000000000000000", 0, 0, new CAddress(0), new CAddress(0), null, null, Integer.MAX_VALUE, false, provider);
            final CFunction parentFunction = new CFunction(internalModule, new MockView(), new CAddress(291), "Mock Function", "Mock Function", "Mock Description", 0, 0, 0, 0, FunctionType.NORMAL, "", 0, null, null, null, provider);
            m_module.getFunction(parentFunction);
            Assert.fail();
        } catch (final IllegalStateException exception) {
        }
        try {
            m_module.getFunctions();
            Assert.fail();
        } catch (final IllegalStateException exception) {
        }
        try {
            m_module.getViews();
            Assert.fail();
        } catch (final IllegalStateException exception) {
        }
        try {
            m_module.getTraces();
            Assert.fail();
        } catch (final IllegalStateException exception) {
        }
    }
}

