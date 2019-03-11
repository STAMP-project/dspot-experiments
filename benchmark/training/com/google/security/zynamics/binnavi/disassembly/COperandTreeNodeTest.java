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


import OperandDisplayStyle.UNSIGNED_HEXADECIMAL;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntDeleteException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.Database.Interfaces.SQLProvider;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.disassembly.Modules.MockModule;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import com.google.security.zynamics.zylib.disassembly.ExpressionType.EXPRESSION_LIST;
import com.google.security.zynamics.zylib.disassembly.ExpressionType.IMMEDIATE_FLOAT;
import com.google.security.zynamics.zylib.disassembly.ExpressionType.IMMEDIATE_INTEGER;
import com.google.security.zynamics.zylib.disassembly.ExpressionType.MEMDEREF;
import com.google.security.zynamics.zylib.disassembly.ExpressionType.OPERATOR;
import com.google.security.zynamics.zylib.disassembly.ExpressionType.REGISTER;
import com.google.security.zynamics.zylib.disassembly.ExpressionType.SIZE_PREFIX;
import com.google.security.zynamics.zylib.disassembly.IReference;
import com.google.security.zynamics.zylib.disassembly.ReferenceType;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class COperandTreeNodeTest {
    private final SQLProvider m_provider = new MockSqlProvider();

    private final INaviModule module = new MockModule(m_provider);

    private final INaviReplacement m_replacement = new CStringReplacement("__void__");

    private final List<IReference> m_references = new ArrayList<IReference>();

    @Test
    public void testAddListener() {
        final COperandTreeNode node = new COperandTreeNode(1, 1, "value", m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
        Assert.assertNotNull(node);
        final INaviOperandTreeNodeListener listener = new CNaviOperandTreeNodeListenerAdapter();
        node.addListener(listener);
    }

    @Test
    public void testAddReference() throws CouldntSaveDataException {
        final COperandTreeNode node = new COperandTreeNode(1, 1, "value", m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
        Assert.assertNotNull(node);
        final INaviOperandTreeNodeListener listener = new CNaviOperandTreeNodeListenerAdapter();
        node.addListener(listener);
        final IReference reference = new CReference(new CAddress(34808609L), ReferenceType.DATA_STRING);
        node.addReference(reference);
        try {
            node.addReference(null);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
        try {
            node.addReference(reference);
            Assert.fail();
        } catch (final IllegalArgumentException e) {
        }
    }

    @Test
    public void testClone() {
        final COperandTreeNode node = new COperandTreeNode(1, 1, "value", m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
        final COperandTreeNode child = new COperandTreeNode(1, 1, "child", m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
        Assert.assertNotNull(node);
        Assert.assertNotNull(child);
        COperandTreeNode.link(node, child);
        final INaviOperandTreeNodeListener listener = new CNaviOperandTreeNodeListenerAdapter();
        node.addListener(listener);
        final COperandTreeNode cloneNode = node.cloneNode();
        Assert.assertEquals(cloneNode.getReplacement().toString(), node.getReplacement().toString());
    }

    @Test
    public void testClose() {
        final COperandTreeNode node = new COperandTreeNode(1, 1, "value", m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
        Assert.assertNotNull(node);
        final INaviOperandTreeNodeListener listener = new CNaviOperandTreeNodeListenerAdapter();
        node.addListener(listener);
        node.close();
    }

    @Test
    public void testConstructor1() {
        final COperandTreeNode node = new COperandTreeNode(1, 1, "value", m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
        Assert.assertNotNull(node);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructor2() {
        new COperandTreeNode(1, 1, null, m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
    }

    @Test(expected = NullPointerException.class)
    public void testConstructor3() {
        new COperandTreeNode(1, 1, "value", m_replacement, null, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
    }

    @Test(expected = NullPointerException.class)
    public void testConstructor4() {
        new COperandTreeNode(1, 1, "value", m_replacement, m_references, null, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
    }

    @Test
    public void testDeleteReference() throws CouldntDeleteException, CouldntSaveDataException {
        final COperandTreeNode node = new COperandTreeNode(1, 1, "value", m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
        Assert.assertNotNull(node);
        final INaviOperandTreeNodeListener listener = new CNaviOperandTreeNodeListenerAdapter();
        node.addListener(listener);
        final IReference reference = new CReference(new CAddress(34808609L), ReferenceType.DATA_STRING);
        node.addReference(reference);
        Assert.assertEquals(2, node.getReferences().size());
        node.deleteReference(reference);
        Assert.assertEquals(1, node.getReferences().size());
        try {
            node.deleteReference(null);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
        try {
            node.deleteReference(reference);
            Assert.fail();
        } catch (final IllegalArgumentException e) {
        }
    }

    @Test
    public void testInitValue() {
        // int
        COperandTreeNode node = new COperandTreeNode(1, 2, "1234567", m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
        Assert.assertEquals(IMMEDIATE_INTEGER, node.getType());
        // float
        node = new COperandTreeNode(1, 3, "0.1234567", m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
        Assert.assertEquals(IMMEDIATE_FLOAT, node.getType());
        // operator
        node = new COperandTreeNode(1, 4, "{", m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
        Assert.assertEquals(EXPRESSION_LIST, node.getType());
        node = new COperandTreeNode(1, 4, "+", m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
        Assert.assertEquals(OPERATOR, node.getType());
        // register
        node = new COperandTreeNode(1, 5, "ABC", m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
        Assert.assertEquals(REGISTER, node.getType());
        // size prefix
        node = new COperandTreeNode(1, 6, "b1", m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
        Assert.assertEquals(SIZE_PREFIX, node.getType());
        node = new COperandTreeNode(1, 6, "b2", m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
        Assert.assertEquals(SIZE_PREFIX, node.getType());
        node = new COperandTreeNode(1, 6, "b4", m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
        Assert.assertEquals(SIZE_PREFIX, node.getType());
        node = new COperandTreeNode(1, 6, "b6", m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
        Assert.assertEquals(SIZE_PREFIX, node.getType());
        node = new COperandTreeNode(1, 6, "b8", m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
        Assert.assertEquals(SIZE_PREFIX, node.getType());
        node = new COperandTreeNode(1, 6, "b10", m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
        Assert.assertEquals(SIZE_PREFIX, node.getType());
        node = new COperandTreeNode(1, 6, "b16", m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
        Assert.assertEquals(SIZE_PREFIX, node.getType());
        node = new COperandTreeNode(1, 6, "b_var", m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
        Assert.assertEquals(SIZE_PREFIX, node.getType());
        node = new COperandTreeNode(1, 6, "dword", m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
        Assert.assertEquals(SIZE_PREFIX, node.getType());
        // mem deref
        node = new COperandTreeNode(1, 7, "burzel", m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
        Assert.assertEquals(MEMDEREF, node.getType());
        try {
            new COperandTreeNode(1, (-1), "burzel", m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
        } catch (final IllegalStateException e) {
        }
    }

    @Test
    public void testLink() {
        final COperandTreeNode root = new COperandTreeNode(1, 1, "value", m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
        final COperandTreeNode childOne = new COperandTreeNode(1, 1, "value", m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
        final COperandTreeNode childTwo = new COperandTreeNode(1, 1, "value", m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
        Assert.assertNotNull(root);
        Assert.assertNotNull(childOne);
        Assert.assertNotNull(childTwo);
        COperandTreeNode.link(root, childOne);
        Assert.assertEquals(root, childOne.getParent());
        Assert.assertEquals(childOne, root.getChildren().get(0));
        try {
            COperandTreeNode.link(null, childOne);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
        try {
            COperandTreeNode.link(root, null);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
    }

    @Test
    public void testMiscFunctions() throws CouldntSaveDataException {
        final COperandTreeNode node = new COperandTreeNode(1, 1, "value", m_replacement, m_references, m_provider, module.getTypeManager(), module.getContent().getTypeInstanceContainer());
        Assert.assertNotNull(node);
        final INaviOperandTreeNodeListener listener = new CNaviOperandTreeNodeListenerAdapter();
        node.addListener(listener);
        final IReference reference = new CReference(new CAddress(34808609L), ReferenceType.DATA_STRING);
        node.addReference(reference);
        Assert.assertNotNull(node.getDisplayStyle());
        Assert.assertEquals(1, node.getId());
        Assert.assertNull(node.getOperand());
        Assert.assertEquals("value", node.getValue());
        node.setDisplayStyle(UNSIGNED_HEXADECIMAL);
        try {
            node.setDisplayStyle(null);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
        node.setDisplayStyle(UNSIGNED_HEXADECIMAL);
        node.setId(12345678);
        node.removeListener(listener);
        node.toString();
    }
}

