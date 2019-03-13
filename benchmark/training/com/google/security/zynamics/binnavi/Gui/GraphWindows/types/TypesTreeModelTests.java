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
package com.google.security.zynamics.binnavi.Gui.GraphWindows.types;


import com.google.common.collect.Multimap;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntDeleteException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.disassembly.types.BaseType;
import com.google.security.zynamics.binnavi.disassembly.types.TestTypeSystem;
import com.google.security.zynamics.binnavi.disassembly.types.TypeManager;
import javax.swing.tree.DefaultMutableTreeNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests the propagation of changes in the {@link TypeManager type manager} to the
 * {@link TypesTreeModel types tree model}.
 *
 * We test the structure of the types tree model which should like this:
 *
 * <pre>
 *
 * struct SimpleStruct {
 * int ss_int_member;
 * unsigned int ss_uint_member;
 * unsigned int ss_array_member[10];
 * }
 *
 * struct NestedStruct {
 * int ns_int_member;
 * SimpleStruct ns_simple_struct_member {
 * int ss_int_member;
 * unsigned int ss_uint_member;
 * unsigned int ss_array_member[10];
 * }
 * }
 *
 * struct DoubleNestedStruct {
 * NestedStruct dns_nested_struct_member {
 * int ns_int_member;
 * SimpleStruct ns_simple_struct_member {
 * int ss_int_member;
 * unsigned int ss_uint_member;
 * unsigned int ss_array_member[10];
 * }
 * }
 * int dns_int_member;
 * unsigned int* dns_pointer_member;
 * }
 *
 * union SimpleUnion {
 * int su_int_member;
 * unsigned int su_uint_member;
 * unsigned int su_array_member[10];
 * }
 *
 * union ComplexUnion {
 * int cu_int_member;
 * SimpleStruct cu_nested_struct_member {
 * int ss_int_member;
 * unsigned int ss_uint_member;
 * unsigned int ss_array_member[10];
 * }
 * DoubleNestedStruct cu_double_nested_struct_member {
 * NestedStruct dns_nested_struct_member {
 * int ns_int_member;
 * SimpleStruct ns_simple_struct_member {
 * int ss_int_member;
 * unsigned int ss_uint_member;
 * unsigned int ss_array_member[10];
 * }
 * }
 * int dns_int_member;
 * unsigned int* dns_pointer_member;
 * }
 * }
 * </pre>
 */
@RunWith(JUnit4.class)
public class TypesTreeModelTests {
    private TypeManager typeManager;

    private TestTypeSystem typeSystem;

    // There is one invisible root node.
    private static final int LEVEL_0_NODES = 1;

    // There are 9 base type nodes.
    private static final int LEVEL_1_NODES = 9;

    // There are 14 structure members (without additional nesting).
    private static final int LEVEL_2_NODES = 14;

    // There are 5 third level nested nodes: SimpleStruct members and NestedStruct members within
    // DoubleNestedStruct and ComplexUnion, respectively.
    private static final int LEVEL_3_NODES = 11;

    // There are 5 fourth level nested nodes: the SimpleStruct members within NestedStruct within
    // DoubleNestedStruct and the members in ComplexUnion.DoubleNestedStruct.NestedStruct.
    private static final int LEVEL_4_NODES = 5;

    @Test
    public void testAtomicTypeAdded() throws CouldntSaveDataException {
        final DefaultMutableTreeNode root = createModel();
        final BaseType newType = typeManager.createAtomicType("new_type", 32, true);
        Assert.assertTrue(TypesTreeModelTests.hasBaseTypeChildNode(root, newType));
        verifySorted(root);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorInvalid0() {
        TypesTreeModel.createDefaultModel(null);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorInvalid1() {
        TypesTreeModel.createSingleTypeModel(null, typeManager.getTypes().get(0));
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorInvalid2() {
        TypesTreeModel.createSingleTypeModel(typeManager, null);
    }

    @Test
    public void testDefaultConstructor() {
        final DefaultMutableTreeNode root = createModel();
        final Multimap<Integer, DefaultMutableTreeNode> nodesByLevel = TypesTreeModelTests.getNodesByLevel(root);
        Assert.assertEquals(TypesTreeModelTests.LEVEL_0_NODES, nodesByLevel.get(0).size());
        Assert.assertEquals(TypesTreeModelTests.LEVEL_1_NODES, nodesByLevel.get(1).size());
        Assert.assertEquals(TypesTreeModelTests.LEVEL_2_NODES, nodesByLevel.get(2).size());
        Assert.assertEquals(TypesTreeModelTests.LEVEL_3_NODES, nodesByLevel.get(3).size());
        Assert.assertEquals(TypesTreeModelTests.LEVEL_4_NODES, nodesByLevel.get(4).size());
        verifySorted(root);
    }

    @Test
    public void testMemberAdded() throws CouldntSaveDataException {
        final DefaultMutableTreeNode root = createModel();
        typeManager.appendMember(typeSystem.simpleStruct, typeSystem.intType, "new_member");
        final Multimap<Integer, DefaultMutableTreeNode> nodesByLevel = TypesTreeModelTests.getNodesByLevel(root);
        Assert.assertEquals(TypesTreeModelTests.LEVEL_0_NODES, nodesByLevel.get(0).size());
        Assert.assertEquals(TypesTreeModelTests.LEVEL_1_NODES, nodesByLevel.get(1).size());
        Assert.assertEquals(((TypesTreeModelTests.LEVEL_2_NODES) + 1), nodesByLevel.get(2).size());
        Assert.assertEquals(((TypesTreeModelTests.LEVEL_3_NODES) + 2), nodesByLevel.get(3).size());
        Assert.assertEquals(((TypesTreeModelTests.LEVEL_4_NODES) + 1), nodesByLevel.get(4).size());
        verifySorted(root);
    }

    @Test
    public void testMemberDeleted0() throws CouldntDeleteException, CouldntSaveDataException {
        final DefaultMutableTreeNode root = createModel();
        typeManager.deleteMember(typeSystem.ssIntMember);
        final Multimap<Integer, DefaultMutableTreeNode> nodesByLevel = TypesTreeModelTests.getNodesByLevel(root);
        Assert.assertEquals(TypesTreeModelTests.LEVEL_0_NODES, nodesByLevel.get(0).size());
        Assert.assertEquals(TypesTreeModelTests.LEVEL_1_NODES, nodesByLevel.get(1).size());
        Assert.assertEquals(((TypesTreeModelTests.LEVEL_2_NODES) - 1), nodesByLevel.get(2).size());
        Assert.assertEquals(((TypesTreeModelTests.LEVEL_3_NODES) - 2), nodesByLevel.get(3).size());
        Assert.assertEquals(((TypesTreeModelTests.LEVEL_4_NODES) - 1), nodesByLevel.get(4).size());
        verifySorted(root);
    }

    @Test
    public void testMemberDeleted1() throws CouldntDeleteException, CouldntSaveDataException {
        final DefaultMutableTreeNode root = createModel();
        typeManager.deleteMember(typeSystem.nsSimpleStructMember);
        final Multimap<Integer, DefaultMutableTreeNode> nodesByLevel = TypesTreeModelTests.getNodesByLevel(root);
        Assert.assertEquals(TypesTreeModelTests.LEVEL_0_NODES, nodesByLevel.get(0).size());
        Assert.assertEquals(TypesTreeModelTests.LEVEL_1_NODES, nodesByLevel.get(1).size());
        Assert.assertEquals(((TypesTreeModelTests.LEVEL_2_NODES) - 1), nodesByLevel.get(2).size());
        Assert.assertEquals(((TypesTreeModelTests.LEVEL_3_NODES) - 4), nodesByLevel.get(3).size());
        Assert.assertEquals(((TypesTreeModelTests.LEVEL_4_NODES) - 4), nodesByLevel.get(4).size());
        verifySorted(root);
    }

    @Test
    public void testMembersMoved() {
        // TODO(jannewger): implement as soon as the moveMembers() method in the TypeManager has been
        // re-implemented.
    }

    @Test
    public void testMemberUpdated() throws CouldntSaveDataException {
        final DefaultMutableTreeNode root = createModel();
        typeManager.updateStructureMember(typeSystem.nsIntMember, typeSystem.simpleStruct, "ns_updated_member", typeSystem.nsIntMember.getBitOffset().get());
        final Multimap<Integer, DefaultMutableTreeNode> nodesByLevel = TypesTreeModelTests.getNodesByLevel(root);
        Assert.assertEquals(TypesTreeModelTests.LEVEL_0_NODES, nodesByLevel.get(0).size());
        Assert.assertEquals(TypesTreeModelTests.LEVEL_1_NODES, nodesByLevel.get(1).size());
        Assert.assertEquals(TypesTreeModelTests.LEVEL_2_NODES, nodesByLevel.get(2).size());
        Assert.assertEquals(((TypesTreeModelTests.LEVEL_3_NODES) + 3), nodesByLevel.get(3).size());
        Assert.assertEquals(((TypesTreeModelTests.LEVEL_4_NODES) + 3), nodesByLevel.get(4).size());
        verifySorted(root);
    }

    @Test
    public void testTypeDeleted() throws CouldntDeleteException {
        final DefaultMutableTreeNode root = createModel();
        typeManager.deleteType(typeSystem.intType);
        final Multimap<Integer, DefaultMutableTreeNode> nodesByLevel = TypesTreeModelTests.getNodesByLevel(root);
        Assert.assertEquals(TypesTreeModelTests.LEVEL_0_NODES, nodesByLevel.get(0).size());
        Assert.assertEquals(((TypesTreeModelTests.LEVEL_1_NODES) - 1), nodesByLevel.get(1).size());
        Assert.assertEquals(((TypesTreeModelTests.LEVEL_2_NODES) - 5), nodesByLevel.get(2).size());
        Assert.assertEquals(((TypesTreeModelTests.LEVEL_3_NODES) - 4), nodesByLevel.get(3).size());
        Assert.assertEquals(((TypesTreeModelTests.LEVEL_4_NODES) - 2), nodesByLevel.get(4).size());
        verifySorted(root);
    }

    @Test
    public void testTypeUpdated() throws CouldntSaveDataException {
        final DefaultMutableTreeNode root = createModel();
        typeManager.updateType(typeSystem.simpleStruct, "AAAAA", typeSystem.simpleStruct.getBitSize(), false);
        final Multimap<Integer, DefaultMutableTreeNode> nodesByLevel = TypesTreeModelTests.getNodesByLevel(root);
        Assert.assertEquals(TypesTreeModelTests.LEVEL_0_NODES, nodesByLevel.get(0).size());
        Assert.assertEquals(TypesTreeModelTests.LEVEL_1_NODES, nodesByLevel.get(1).size());
        Assert.assertEquals(TypesTreeModelTests.LEVEL_2_NODES, nodesByLevel.get(2).size());
        Assert.assertEquals(TypesTreeModelTests.LEVEL_3_NODES, nodesByLevel.get(3).size());
        Assert.assertEquals(TypesTreeModelTests.LEVEL_4_NODES, nodesByLevel.get(4).size());
        verifySorted(root);
    }
}

