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


import com.google.security.zynamics.binnavi.Database.MockClasses.MockDatabase;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.Tagging.MockTagManager;
import com.google.security.zynamics.binnavi.Tagging.TagType;
import com.google.security.zynamics.binnavi.disassembly.COperandTree;
import com.google.security.zynamics.binnavi.disassembly.COperandTreeNode;
import com.google.security.zynamics.binnavi.disassembly.Modules.MockModule;
import com.google.security.zynamics.zylib.disassembly.IOperandTree;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class OperandTest {
    @Test
    public void testConstructor() {
        final MockModule mockModule = new MockModule();
        final COperandTreeNode rootNode = new COperandTreeNode(1, IOperandTree.NODE_TYPE_SIZE_PREFIX_ID, "b4", null, new ArrayList<com.google.security.zynamics.zylib.disassembly.IReference>(), new MockSqlProvider(), mockModule.getTypeManager(), mockModule.getContent().getTypeInstanceContainer());
        final COperandTreeNode childNode = new COperandTreeNode(1, IOperandTree.NODE_TYPE_REGISTER_ID, "eax", null, new ArrayList<com.google.security.zynamics.zylib.disassembly.IReference>(), new MockSqlProvider(), mockModule.getTypeManager(), mockModule.getContent().getTypeInstanceContainer());
        COperandTreeNode.link(rootNode, childNode);
        final COperandTree tree = new COperandTree(rootNode, new MockSqlProvider(), mockModule.getTypeManager(), mockModule.getContent().getTypeInstanceContainer());
        final Operand operand = new Operand(tree);
        Assert.assertEquals("dword", operand.getRootNode().getValue());
        Assert.assertEquals(1, operand.getRootNode().getChildren().size());
        Assert.assertEquals("eax", operand.getRootNode().getChildren().get(0).getValue());
        Assert.assertEquals("eax", operand.toString());
    }

    @Test
    public void testCreate() {
        final Database database = new Database(new MockDatabase());
        final MockModule mockModule = new MockModule();
        final TagManager nodeTagManager = new TagManager(new MockTagManager(TagType.NODE_TAG));
        final TagManager viewTagManager = new TagManager(new MockTagManager(TagType.VIEW_TAG));
        final Module module = new Module(database, mockModule, nodeTagManager, viewTagManager);
        final COperandTreeNode rootNode = new COperandTreeNode(1, IOperandTree.NODE_TYPE_SIZE_PREFIX_ID, "b4", null, new ArrayList<com.google.security.zynamics.zylib.disassembly.IReference>(), new MockSqlProvider(), mockModule.getTypeManager(), mockModule.getContent().getTypeInstanceContainer());
        final COperandTreeNode childNode = new COperandTreeNode(1, IOperandTree.NODE_TYPE_REGISTER_ID, "eax", null, new ArrayList<com.google.security.zynamics.zylib.disassembly.IReference>(), new MockSqlProvider(), mockModule.getTypeManager(), mockModule.getContent().getTypeInstanceContainer());
        COperandTreeNode.link(rootNode, childNode);
        final OperandExpression root = new OperandExpression(rootNode);
        final Operand operand = Operand.create(module, root);
        Assert.assertEquals("dword", operand.getRootNode().getValue());
        Assert.assertEquals(1, operand.getRootNode().getChildren().size());
        Assert.assertEquals("eax", operand.getRootNode().getChildren().get(0).getValue());
        Assert.assertEquals("eax", operand.toString());
    }
}

