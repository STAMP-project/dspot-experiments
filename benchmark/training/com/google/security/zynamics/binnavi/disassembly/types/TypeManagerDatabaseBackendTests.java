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
package com.google.security.zynamics.binnavi.disassembly.types;


import BaseTypeCategory.ATOMIC;
import BaseTypeCategory.STRUCT;
import com.google.common.base.Optional;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntDeleteException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntLoadDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.disassembly.MockOperandTreeNode;
import com.google.security.zynamics.binnavi.disassembly.Modules.MockModule;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import java.util.ArrayList;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for the TypeManagerDatabaseBackend class. These tests check whether backend methods
 * end up calling the corresponding sql provider methods.
 */
@RunWith(JUnit4.class)
public class TypeManagerDatabaseBackendTests {
    private final MockSqlProvider mockProvider = new MockSqlProvider();

    private final MockModule mockModule = new MockModule();

    private final TypeManagerDatabaseBackend backend = new TypeManagerDatabaseBackend(mockProvider, mockModule);

    @Test
    public void testConstructMember() throws CouldntLoadDataException, CouldntSaveDataException {
        final BaseType intType = backend.createType("test_int", 32, true, null, ATOMIC);
        final BaseType structType = backend.createType("test_struct", 500, false, null, STRUCT);
        final String memberName = "test_member";
        final Optional<Integer> offset = Optional.of(0);
        final Optional<Integer> numberElements = Optional.<Integer>absent();
        final TypeMember member = backend.createStructureMember(structType, intType, memberName, offset.get());
        final RawTypeMember rawMember = mockProvider.loadTypeMember(mockModule, member.getId());
        Assert.assertEquals(member.getId(), rawMember.getId());
        Assert.assertEquals(intType.getId(), rawMember.getBaseTypeId());
        Assert.assertEquals(numberElements, rawMember.getNumberOfElements());
        Assert.assertEquals(Optional.<Integer>absent(), rawMember.getArgumentIndex());
        Assert.assertEquals(memberName, rawMember.getName());
        Assert.assertEquals(offset, rawMember.getOffset());
        Assert.assertEquals(((Integer) (structType.getId())), rawMember.getParentId());
    }

    @Test
    public void testConstructType() throws CouldntLoadDataException, CouldntSaveDataException {
        final String typeName = "test_type";
        final int bitSize = 100;
        final boolean isSigned = true;
        final BaseType baseType = backend.createType(typeName, bitSize, isSigned, null, ATOMIC);
        final RawBaseType rawBaseType = mockProvider.loadType(mockModule, baseType.getId());
        Assert.assertEquals(baseType.getId(), rawBaseType.getId());
        Assert.assertEquals(bitSize, rawBaseType.getSize());
        Assert.assertEquals(isSigned, rawBaseType.isSigned());
        Assert.assertEquals(typeName, rawBaseType.getName());
        Assert.assertNull(rawBaseType.getPointerId());
    }

    @Test
    public void testCreateTypeSubstitution() throws CouldntSaveDataException {
        final CAddress address = new CAddress(4096);
        final int position = 0;
        final int offset = 32;
        final MockOperandTreeNode node = new MockOperandTreeNode();
        final BaseType baseType = backend.createType("test_type", 32, true, null, ATOMIC);
        final TypeSubstitution substitution = backend.createTypeSubstitution(node, baseType, new ArrayList<Integer>(), position, offset, address);
        Assert.assertEquals(node, substitution.getOperandTreeNode());
        Assert.assertEquals(baseType, substitution.getBaseType());
        Assert.assertEquals(position, substitution.getPosition());
        Assert.assertEquals(offset, substitution.getOffset());
        Assert.assertEquals(address, substitution.getAddress());
    }

    @Test
    public void testDeleteMember() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException {
        final BaseType intType = backend.createType("test_int", 32, true, null, ATOMIC);
        final BaseType structType = backend.createType("test_struct", 500, false, null, STRUCT);
        final TypeMember member = backend.createStructureMember(structType, intType, "test_member", 1);
        Assert.assertNotNull(mockProvider.loadTypeMember(mockModule, member.getId()));
        backend.deleteMember(member);
        Assert.assertNull(mockProvider.loadTypeMember(mockModule, member.getId()));
    }

    @Test
    public void testDeleteType() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException {
        final BaseType baseType = backend.createType("test_int", 32, true, null, ATOMIC);
        Assert.assertNotNull(mockProvider.loadType(mockModule, baseType.getId()));
        backend.deleteType(baseType);
        Assert.assertNull(mockProvider.loadType(mockModule, baseType.getId()));
    }

    @Test
    public void testDeleteTypeSubstitution() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException {
        final MockOperandTreeNode node = new MockOperandTreeNode();
        final BaseType baseType = backend.createType("test_type", 32, true, null, ATOMIC);
        final CAddress address = new CAddress(4096);
        final int position = 0;
        final TypeSubstitution substitution = backend.createTypeSubstitution(node, baseType, new ArrayList<Integer>(), position, 32, address);
        RawTypeSubstitution rawSubstitution = mockProvider.loadTypeSubstitution(mockModule, address.toBigInteger(), position, node.getId());
        Assert.assertNotNull(rawSubstitution);
        backend.deleteTypeSubstitution(substitution);
        rawSubstitution = mockProvider.loadTypeSubstitution(mockModule, address.toBigInteger(), position, node.getId());
        Assert.assertNull(rawSubstitution);
    }

    @Test(expected = NullPointerException.class)
    public void testInvalidConstruction0() {
        new TypeManagerDatabaseBackend(null, new MockModule());
    }

    @Test(expected = NullPointerException.class)
    public void testInvalidConstruction1() {
        new TypeManagerDatabaseBackend(new MockSqlProvider(), null);
    }

    @Test(expected = NullPointerException.class)
    public void testInvalidConstruction2() {
        new TypeManagerDatabaseBackend(null, null);
    }

    @Test
    public void testUpdateMember() throws CouldntLoadDataException, CouldntSaveDataException {
        final BaseType intType = backend.createType("test_int", 32, true, null, ATOMIC);
        final BaseType uintType = backend.createType("test_uint", 32, false, null, ATOMIC);
        final BaseType structType = backend.createType("test_struct", 500, false, null, STRUCT);
        final TypeMember member = backend.createStructureMember(structType, intType, "test_member", 0);
        final String newName = "new_name";
        final Optional<Integer> newOffset = Optional.<Integer>of(10);
        backend.updateStructureMember(member, uintType, newName, newOffset.get());
        final RawTypeMember updatedMember = mockProvider.loadTypeMember(mockModule, member.getId());
        Assert.assertEquals(newName, updatedMember.getName());
        Assert.assertEquals(uintType.getId(), updatedMember.getBaseTypeId());
        Assert.assertEquals(newOffset, updatedMember.getOffset());
    }

    @Test
    public void testUpdateType() throws CouldntLoadDataException, CouldntSaveDataException {
        final String newName = "narf_type";
        final int newSize = 64;
        final boolean newIsSigned = false;
        final BaseType intType = backend.createType("test_int", 32, true, null, ATOMIC);
        backend.updateType(intType, newName, newSize, newIsSigned);
        final RawBaseType updatedType = mockProvider.loadType(mockModule, intType.getId());
        Assert.assertEquals(newName, updatedType.getName());
        Assert.assertEquals(newSize, updatedType.getSize());
        Assert.assertEquals(newIsSigned, updatedType.isSigned());
    }

    @Test
    public void testUpdateTypeSubstitution() throws CouldntLoadDataException, CouldntSaveDataException {
        final MockOperandTreeNode node = new MockOperandTreeNode();
        final BaseType baseType = backend.createType("test_type", 32, true, null, ATOMIC);
        final BaseType newType = backend.createType("new_test_type", 32, true, null, ATOMIC);
        final Integer newOffset = 64;
        final TypeSubstitution substitution = backend.createTypeSubstitution(node, baseType, new ArrayList<Integer>(), 0, 32, new CAddress(4096));
        backend.updateSubstitution(substitution, newType, new ArrayList<Integer>(), newOffset);
        final RawTypeSubstitution updatedSubstitution = mockProvider.loadTypeSubstitution(mockModule, substitution.getAddress().toBigInteger(), substitution.getPosition(), substitution.getExpressionId());
        Assert.assertEquals(newOffset, updatedSubstitution.getOffset());
        Assert.assertEquals(newType.getId(), updatedSubstitution.getBaseTypeId());
    }
}

