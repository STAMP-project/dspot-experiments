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


import ExpressionType.Register;
import com.google.common.collect.Lists;
import com.google.security.zynamics.binnavi.API.reil.InternalTranslationException;
import com.google.security.zynamics.binnavi.Common.CommonTestObjects;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntLoadDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.LoadCancelledException;
import com.google.security.zynamics.binnavi.Database.Interfaces.SQLProvider;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockDatabase;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.Interfaces.IComment;
import com.google.security.zynamics.binnavi.Tagging.CTagManager;
import com.google.security.zynamics.binnavi.Tagging.MockTagManager;
import com.google.security.zynamics.binnavi.Tagging.TagType;
import com.google.security.zynamics.binnavi.disassembly.CInstruction;
import com.google.security.zynamics.binnavi.disassembly.COperandTree;
import com.google.security.zynamics.binnavi.disassembly.COperandTreeNode;
import com.google.security.zynamics.binnavi.disassembly.Modules.CModule;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import com.google.security.zynamics.zylib.disassembly.IOperandTree;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class InstructionTest {
    @Test
    public void testCommentInitialization() throws CouldntLoadDataException, LoadCancelledException {
        final SQLProvider provider = new MockSqlProvider();
        final CModule internalModule = new CModule(123, "Name", "Comment", new Date(), new Date(), "12345678123456781234567812345678", "1234567812345678123456781234567812345678", 55, 66, new CAddress(1365), new CAddress(1638), new com.google.security.zynamics.binnavi.debug.debugger.DebuggerTemplate(1, "Mock Debugger", "localhaus", 88, provider), null, Integer.MAX_VALUE, false, provider);
        internalModule.load();
        final COperandTreeNode rootNode1 = new COperandTreeNode(1, IOperandTree.NODE_TYPE_REGISTER_ID, "eax", null, new ArrayList<com.google.security.zynamics.zylib.disassembly.IReference>(), provider, internalModule.getTypeManager(), internalModule.getContent().getTypeInstanceContainer());
        final COperandTreeNode rootNode2 = new COperandTreeNode(1, IOperandTree.NODE_TYPE_REGISTER_ID, "ebx", null, new ArrayList<com.google.security.zynamics.zylib.disassembly.IReference>(), provider, internalModule.getTypeManager(), internalModule.getContent().getTypeInstanceContainer());
        final COperandTree operand1 = new COperandTree(rootNode1, provider, internalModule.getTypeManager(), internalModule.getContent().getTypeInstanceContainer());
        final COperandTree operand2 = new COperandTree(rootNode2, provider, internalModule.getTypeManager(), internalModule.getContent().getTypeInstanceContainer());
        final List<COperandTree> operands = Lists.newArrayList(operand1, operand2);
        final CInstruction internalInstruction = new CInstruction(false, internalModule, new CAddress(291), "mov", operands, new byte[]{ 1, 2, 3 }, "x86-32", provider);
        final Instruction instruction = new Instruction(internalInstruction);
        final MockInstructionListener listener = new MockInstructionListener();
        instruction.addListener(listener);
        final ArrayList<IComment> comment = Lists.<IComment>newArrayList(new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(null, CommonTestObjects.TEST_USER_1, null, "Hannes"));
        instruction.initializeComment(comment);
        Assert.assertEquals(comment, internalInstruction.getGlobalComment());
        Assert.assertEquals(comment, instruction.getComment());
        // TODO (timkornau): check if double messages are what we want here of rather not.
        // assertEquals("InitializedComment;", listener.events);
        instruction.removeListener(listener);
    }

    @Test
    public void testConstructor() throws CouldntLoadDataException, LoadCancelledException {
        final SQLProvider provider = new MockSqlProvider();
        final CModule internalModule = new CModule(123, "Name", "Comment", new Date(), new Date(), "12345678123456781234567812345678", "1234567812345678123456781234567812345678", 55, 66, new CAddress(1365), new CAddress(1638), new com.google.security.zynamics.binnavi.debug.debugger.DebuggerTemplate(1, "Mock Debugger", "localhaus", 88, provider), null, Integer.MAX_VALUE, false, provider);
        internalModule.load();
        final COperandTreeNode rootNode1 = new COperandTreeNode(1, IOperandTree.NODE_TYPE_REGISTER_ID, "eax", null, new ArrayList<com.google.security.zynamics.zylib.disassembly.IReference>(), provider, internalModule.getTypeManager(), internalModule.getContent().getTypeInstanceContainer());
        final COperandTreeNode rootNode2 = new COperandTreeNode(1, IOperandTree.NODE_TYPE_REGISTER_ID, "ebx", null, new ArrayList<com.google.security.zynamics.zylib.disassembly.IReference>(), provider, internalModule.getTypeManager(), internalModule.getContent().getTypeInstanceContainer());
        final COperandTree operand1 = new COperandTree(rootNode1, provider, internalModule.getTypeManager(), internalModule.getContent().getTypeInstanceContainer());
        final COperandTree operand2 = new COperandTree(rootNode2, provider, internalModule.getTypeManager(), internalModule.getContent().getTypeInstanceContainer());
        final List<COperandTree> operands = Lists.newArrayList(operand1, operand2);
        final CInstruction internalInstruction = new CInstruction(false, internalModule, new CAddress(291), "mov", operands, new byte[]{ 1, 2, 3 }, "x86-32", provider);
        final Instruction instruction = new Instruction(internalInstruction);
        Assert.assertEquals(291, instruction.getAddress().toLong());
        Assert.assertEquals(null, instruction.getComment());
        Assert.assertArrayEquals(new byte[]{ 1, 2, 3 }, instruction.getData());
        Assert.assertEquals("mov", instruction.getMnemonic());
        Assert.assertEquals(2, instruction.getOperands().size());
        Assert.assertEquals("eax", instruction.getOperands().get(0).getRootNode().getChildren().get(0).getValue());
        Assert.assertEquals("ebx", instruction.getOperands().get(1).getRootNode().getChildren().get(0).getValue());
        Assert.assertEquals("123  mov eax, ebx", instruction.toString());
        Assert.assertEquals("x86-32", instruction.getArchitecture());
    }

    @Test
    public void testCreate() throws CouldntLoadDataException, LoadCancelledException {
        final SQLProvider provider = new MockSqlProvider();
        final CModule internalModule = new CModule(123, "Name", "Comment", new Date(), new Date(), "12345678123456781234567812345678", "1234567812345678123456781234567812345678", 55, 66, new CAddress(1365), new CAddress(1638), new com.google.security.zynamics.binnavi.debug.debugger.DebuggerTemplate(1, "Mock Debugger", "localhaus", 88, provider), null, Integer.MAX_VALUE, false, provider);
        internalModule.load();
        final Database database = new Database(new MockDatabase());
        final CTagManager mockTagManager = new CTagManager(new com.google.security.zynamics.zylib.types.trees.Tree<com.google.security.zynamics.binnavi.Tagging.CTag>(new com.google.security.zynamics.zylib.types.trees.TreeNode<com.google.security.zynamics.binnavi.Tagging.CTag>(new com.google.security.zynamics.binnavi.Tagging.CTag(1, "Root", "", TagType.NODE_TAG, provider))), TagType.NODE_TAG, provider);
        final TagManager nodeTagManager = new TagManager(mockTagManager);
        final TagManager viewTagManager = new TagManager(new MockTagManager(TagType.VIEW_TAG));
        final Module module = new Module(database, internalModule, nodeTagManager, viewTagManager);
        final List<Operand> operands = new ArrayList<Operand>();
        final OperandExpression ex2 = OperandExpression.create(module, "eax", Register);
        final OperandExpression ex4 = OperandExpression.create(module, "ebx", Register);
        operands.add(Operand.create(module, ex2));
        operands.add(Operand.create(module, ex4));
        final Instruction instruction = Instruction.create(module, new Address(291), "mov", operands, new byte[]{ 1, 2, 3 }, "x86-32");
        Assert.assertEquals(291, instruction.getAddress().toLong());
        Assert.assertEquals(null, instruction.getComment());
        Assert.assertArrayEquals(new byte[]{ 1, 2, 3 }, instruction.getData());
        Assert.assertEquals("mov", instruction.getMnemonic());
        Assert.assertEquals(2, instruction.getOperands().size());
        Assert.assertEquals("eax", instruction.getOperands().get(0).getRootNode().getChildren().get(0).getValue());
        Assert.assertEquals("ebx", instruction.getOperands().get(1).getRootNode().getChildren().get(0).getValue());
        Assert.assertEquals("123  mov eax, ebx", instruction.toString());
    }

    @Test
    public void testReil() throws InternalTranslationException, CouldntLoadDataException, LoadCancelledException {
        final SQLProvider provider = new MockSqlProvider();
        final CModule internalModule = new CModule(123, "Name", "Comment", new Date(), new Date(), "12345678123456781234567812345678", "1234567812345678123456781234567812345678", 55, 66, new CAddress(1365), new CAddress(1638), new com.google.security.zynamics.binnavi.debug.debugger.DebuggerTemplate(1, "Mock Debugger", "localhaus", 88, provider), null, Integer.MAX_VALUE, false, provider);
        internalModule.load();
        final COperandTreeNode rootNode1 = new COperandTreeNode(1, IOperandTree.NODE_TYPE_REGISTER_ID, "eax", null, new ArrayList<com.google.security.zynamics.zylib.disassembly.IReference>(), provider, internalModule.getTypeManager(), internalModule.getContent().getTypeInstanceContainer());
        final COperandTreeNode rootNode2 = new COperandTreeNode(1, IOperandTree.NODE_TYPE_REGISTER_ID, "ebx", null, new ArrayList<com.google.security.zynamics.zylib.disassembly.IReference>(), provider, internalModule.getTypeManager(), internalModule.getContent().getTypeInstanceContainer());
        final COperandTree operand1 = new COperandTree(rootNode1, provider, internalModule.getTypeManager(), internalModule.getContent().getTypeInstanceContainer());
        final COperandTree operand2 = new COperandTree(rootNode2, provider, internalModule.getTypeManager(), internalModule.getContent().getTypeInstanceContainer());
        final List<COperandTree> operands = Lists.newArrayList(operand1, operand2);
        final CInstruction internalInstruction = new CInstruction(false, internalModule, new CAddress(291), "mov", operands, new byte[]{ 1, 2, 3 }, "x86-32", provider);
        final Instruction instruction = new Instruction(internalInstruction);
        instruction.getReilCode();
    }
}

