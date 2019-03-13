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


import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static BaseTypeCategory.ATOMIC;


/**
 * Tests all methods related to {@link BaseType base types} without going through the type manager.
 */
@RunWith(JUnit4.class)
public class BaseTypeTest {
    private TestTypeSystem typeSystem;

    private BaseType testStruct;

    private TypeMember testMember0;

    private TypeMember testMember1;

    private TypeMember testMember2;

    private TypeMember testMember3;

    private static final String NAME = "NAME";

    @Test(expected = NullPointerException.class)
    public void testAddMember_NullMember() {
        testStruct.addMember(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddMember_OtherParent() {
        testStruct.addMember(typeSystem.ssIntMember);
    }

    @Test
    public void testAddMember3() {
        testStruct.addMember(testMember0);
        Assert.assertEquals(testMember0.getBitSize(), testStruct.getBitSize());
        Assert.assertEquals(1, testStruct.getMemberCount());
        Assert.assertEquals(testMember0, testStruct.getLastMember());
    }

    @Test
    public void testAddMember4() {
        testStruct.addMember(testMember0);
        testStruct.addMember(testMember0);
        testStruct.addMember(testMember0);
        testStruct.addMember(testMember0);
        Assert.assertEquals(testMember0.getBitSize(), testStruct.getBitSize());
        Assert.assertEquals(1, testStruct.getMemberCount());
        Assert.assertEquals(testMember0, testStruct.getLastMember());
    }

    @Test
    public void testAddMember5() {
        testStruct.addMember(testMember0);
        testStruct.addMember(testMember1);
        testStruct.addMember(testMember2);
        testStruct.addMember(testMember3);
        Assert.assertEquals(((((testMember0.getBitSize()) + (testMember1.getBitSize())) + (testMember2.getBitSize())) + (testMember3.getBitSize())), testStruct.getBitSize());
        Assert.assertEquals(4, testStruct.getMemberCount());
        Assert.assertEquals(testMember3, testStruct.getLastMember());
    }

    @Test
    public void testAddMembers3() {
        addMembersAndVerifyOperation();
    }

    @Test(expected = NullPointerException.class)
    public void testAppendToPointerHierarchy1() {
        BaseType.appendToPointerHierarchy(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testAppendToPointerHierarchy2() {
        BaseType.appendToPointerHierarchy(typeSystem.uintType, null);
    }

    @Test
    public void testAppendToPointerHierarchy3() {
        Assert.assertEquals(0, typeSystem.uintType.getPointerLevel());
        Assert.assertEquals(0, typeSystem.intType.getPointerLevel());
        BaseType.appendToPointerHierarchy(typeSystem.uintType, typeSystem.intType);
        Assert.assertEquals(typeSystem.uintType, typeSystem.intType.pointsTo());
        Assert.assertEquals(typeSystem.intType, typeSystem.uintType.pointedToBy());
        Assert.assertEquals(1, typeSystem.intType.getPointerLevel());
        Assert.assertEquals(0, typeSystem.uintType.getPointerLevel());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAppendToPointerHierarchy4() {
        Assert.assertEquals(0, typeSystem.uintType.getPointerLevel());
        Assert.assertEquals(0, typeSystem.intType.getPointerLevel());
        BaseType.appendToPointerHierarchy(typeSystem.uintType, typeSystem.intType);
        Assert.assertEquals(typeSystem.uintType, typeSystem.intType.pointsTo());
        Assert.assertEquals(typeSystem.intType, typeSystem.uintType.pointedToBy());
        Assert.assertEquals(1, typeSystem.intType.getPointerLevel());
        Assert.assertEquals(0, typeSystem.uintType.getPointerLevel());
        BaseType.appendToPointerHierarchy(typeSystem.intType, typeSystem.uintType);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBaseTypeConstructor1() {
        new BaseType((-1), null, (-1), false, ATOMIC);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBaseTypeConstructor2() {
        new BaseType(0, null, (-1), false, ATOMIC);
    }

    @Test(expected = NullPointerException.class)
    public void testBaseTypeConstructor3() {
        new BaseType(1, null, (-1), false, ATOMIC);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBaseTypeConstructor4() {
        new BaseType(1, BaseTypeTest.NAME, (-1), false, ATOMIC);
    }

    @Test
    public void testBaseTypeConstructor5() {
        final BaseType baseType = new BaseType(1, BaseTypeTest.NAME, 4, false, ATOMIC);
        // explicit
        Assert.assertEquals(1, baseType.getId());
        Assert.assertEquals(BaseTypeTest.NAME, baseType.getName());
        Assert.assertEquals(4, baseType.getBitSize());
        Assert.assertEquals(1, baseType.getByteSize());
        Assert.assertEquals(false, baseType.isSigned());
        // implicit
        Assert.assertNull(baseType.pointedToBy());
        Assert.assertNull(baseType.pointsTo());
        Assert.assertFalse(baseType.isStackFrame());
    }

    @Test
    public void testDeleteMember1() {
        addMembersAndVerifyOperation();
        testStruct.deleteMember(testMember0);
        Assert.assertEquals(((((testMember0.getBaseType().getBitSize()) + (testMember1.getBaseType().getBitSize())) + (testMember2.getBaseType().getBitSize())) + (testMember3.getBaseType().getBitSize())), testStruct.getBitSize());
        Assert.assertEquals(3, testStruct.getMemberCount());
    }

    @Test(expected = NullPointerException.class)
    public void testDeleteMember2() {
        addMembersAndVerifyOperation();
        testStruct.deleteMember(null);
    }

    @Test
    public void testSetSize_Atomic() {
        typeSystem.intType.setSize(123);
        Assert.assertEquals(123, typeSystem.intType.getBitSize());
    }

    @Test
    public void testSetSize_Pointer() {
        typeSystem.uintPointerType.setSize(123);
        Assert.assertEquals(123, typeSystem.uintPointerType.getBitSize());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetSize_Array() {
        typeSystem.uintArrayType.setSize(100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetSize_Struct() {
        typeSystem.simpleStruct.setSize(123);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetSize_Union() {
        typeSystem.simpleUnion.setSize(123);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetSize_Prototype() {
        typeSystem.voidFunctionPrototype.setSize(123);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetSize_Negative() {
        typeSystem.uintType.setSize((-1));
    }

    @Test
    public void testSetSize_Zero() {
        typeSystem.uintType.setSize(0);
        Assert.assertEquals(0, typeSystem.uintType.getBitSize());
    }

    @Test
    public void testGetBitSize_Atomic() {
        Assert.assertEquals(32, typeSystem.intType.getBitSize());
    }

    @Test
    public void testGetBitSize_Pointer() {
        Assert.assertEquals(32, typeSystem.uintPointerType.getBitSize());
    }

    @Test
    public void testGetBitSize_Array() {
        Assert.assertEquals((10 * 32), typeSystem.uintArrayType.getBitSize());
    }

    @Test
    public void testGetBitSize_Struct() {
        Assert.assertEquals((((10 * 32) + 32) + 32), typeSystem.simpleStruct.getBitSize());
    }

    @Test
    public void testGetBitSize_Union() {
        Assert.assertEquals(typeSystem.uintArrayType.getBitSize(), typeSystem.simpleUnion.getBitSize());
        Assert.assertEquals(typeSystem.doubleNestedStruct.getBitSize(), typeSystem.complexUnion.getBitSize());
    }

    @Test
    public void testGetBitSize_Prototype() {
        Assert.assertEquals(0, typeSystem.voidFunctionPrototype.getBitSize());
    }

    @Test
    public void testGetByteSize() {
        Assert.assertEquals(4, typeSystem.uintType.getByteSize());
    }

    @Test
    public void testGetLastMember1() {
        Assert.assertNull(typeSystem.uintType.getLastMember());
    }

    @Test
    public void testGetLastMember2() {
        addMembersAndVerifyOperation();
        Assert.assertEquals(testMember3, testStruct.getLastMember());
    }

    @Test
    public void testGetMemberCount() {
        Assert.assertEquals(0, typeSystem.uintType.getMemberCount());
        addMembersAndVerifyOperation();
        Assert.assertEquals(4, testStruct.getMemberCount());
    }

    @Test
    public void testGetPointerLevel() {
        Assert.assertEquals(0, typeSystem.uintType.getPointerLevel());
        Assert.assertEquals(0, typeSystem.intType.getPointerLevel());
        BaseType.appendToPointerHierarchy(typeSystem.uintType, typeSystem.intType);
        Assert.assertEquals(0, typeSystem.uintType.getPointerLevel());
        Assert.assertEquals(1, typeSystem.intType.getPointerLevel());
    }

    @Test(expected = NullPointerException.class)
    public void testGetPointerTypeName1() {
        BaseType.getPointerTypeName(null, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPointerTypeName2() {
        BaseType.getPointerTypeName(typeSystem.uintType, 0);
    }

    @Test
    public void testGetPointerTypeName3() {
        final String pointerName = BaseType.getPointerTypeName(typeSystem.uintType, 10);
        Assert.assertEquals((((typeSystem.uintType.getName()) + " ") + (Strings.repeat("*", 10))), pointerName);
    }

    @Test(expected = NullPointerException.class)
    public void testGetValueType1() {
        BaseType.getValueType(null);
    }

    @Test
    public void testGetValueType2() {
        BaseType.appendToPointerHierarchy(typeSystem.uintType, typeSystem.intType);
        Assert.assertEquals(typeSystem.uintType, BaseType.getValueType(typeSystem.intType));
    }

    @Test(expected = NullPointerException.class)
    public void testGetValueTypeName1() {
        BaseType.getValueTypeName(null);
    }

    @Test
    public void testGetValueTypeName2() {
        BaseType.appendToPointerHierarchy(typeSystem.uintType, typeSystem.intType);
        Assert.assertEquals(typeSystem.uintType.getName(), BaseType.getValueTypeName(typeSystem.intType));
    }

    @Test
    public void testGetValueTypeName3() {
        Assert.assertEquals(typeSystem.intType.getName(), BaseType.getValueTypeName(typeSystem.intType));
    }

    @Test
    public void testHasMembers() {
        Assert.assertEquals(false, testStruct.hasMembers());
        addMembersAndVerifyOperation();
        Assert.assertEquals(true, testStruct.hasMembers());
    }

    @Test
    public void testIterator() {
        addMembersAndVerifyOperation();
        int offset = 0;
        for (final TypeMember member : testStruct) {
            Preconditions.checkArgument(((member.getBitOffset().get()) >= offset));
            offset = member.getBitOffset().get();
        }
    }

    @Test
    public void testMoveMemberInBetween() {
        typeSystem.simpleStruct.moveMembers(Sets.newTreeSet(typeSystem.ssIntMember), 32);
        Assert.assertEquals(Integer.valueOf(0), typeSystem.ssUintMember.getBitOffset().get());
        Assert.assertEquals(Integer.valueOf(32), typeSystem.ssIntMember.getBitOffset().get());
        Assert.assertEquals(Integer.valueOf(64), typeSystem.ssArrayMember.getBitOffset().get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoveMemberInvalid1() {
        typeSystem.simpleStruct.moveMembers(Sets.newTreeSet(typeSystem.ssIntMember), (-100));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoveMemberInvalid2() {
        typeSystem.simpleStruct.moveMembers(Sets.newTreeSet(typeSystem.ssIntMember, typeSystem.ssUintMember), 999);
    }

    @Test
    public void testMoveMembersToBeginning() {
        typeSystem.simpleStruct.moveMembers(Sets.newTreeSet(typeSystem.ssUintMember, typeSystem.ssArrayMember), (-32));
        Assert.assertEquals(Integer.valueOf(0), typeSystem.ssUintMember.getBitOffset().get());
        Assert.assertEquals(Integer.valueOf(32), typeSystem.ssArrayMember.getBitOffset().get());
        Assert.assertEquals(Integer.valueOf(352), typeSystem.ssIntMember.getBitOffset().get());
    }

    @Test
    public void testMoveMembersToBeginningReverse() {
        typeSystem.simpleStruct.moveMembers(Sets.newTreeSet(typeSystem.ssArrayMember, typeSystem.ssUintMember), (-32));
        Assert.assertEquals(Integer.valueOf(0), typeSystem.ssUintMember.getBitOffset().get());
        Assert.assertEquals(Integer.valueOf(32), typeSystem.ssArrayMember.getBitOffset().get());
        Assert.assertEquals(Integer.valueOf(352), typeSystem.ssIntMember.getBitOffset().get());
    }

    @Test
    public void testMoveMembersToEnd() {
        typeSystem.simpleStruct.moveMembers(Sets.<TypeMember>newTreeSet(typeSystem.ssIntMember, typeSystem.ssUintMember), 320);
        Assert.assertEquals(Integer.valueOf(320), typeSystem.ssIntMember.getBitOffset().get());
        Assert.assertEquals(Integer.valueOf(352), typeSystem.ssUintMember.getBitOffset().get());
        Assert.assertEquals(Integer.valueOf(0), typeSystem.ssArrayMember.getBitOffset().get());
    }

    @Test
    public void testMoveMembersToEndReverse() {
        typeSystem.simpleStruct.moveMembers(Sets.newTreeSet(typeSystem.ssUintMember, typeSystem.ssIntMember), 320);
        Assert.assertEquals(Integer.valueOf(320), typeSystem.ssIntMember.getBitOffset().get());
        Assert.assertEquals(Integer.valueOf(352), typeSystem.ssUintMember.getBitOffset().get());
        Assert.assertEquals(Integer.valueOf(0), typeSystem.ssArrayMember.getBitOffset().get());
    }

    @Test
    public void testMoveMemberToBeginning() {
        typeSystem.simpleStruct.moveMembers(Sets.newTreeSet(typeSystem.ssUintMember), (-32));
        Assert.assertEquals(Integer.valueOf(0), typeSystem.ssUintMember.getBitOffset().get());
        Assert.assertEquals(Integer.valueOf(32), typeSystem.ssIntMember.getBitOffset().get());
        Assert.assertEquals(Integer.valueOf(64), typeSystem.ssArrayMember.getBitOffset().get());
    }

    @Test
    public void testMoveMemberToEnd() {
        typeSystem.simpleStruct.moveMembers(Sets.newTreeSet(typeSystem.ssIntMember), 64);
        Assert.assertEquals(Integer.valueOf(0), typeSystem.ssUintMember.getBitOffset().get());
        Assert.assertEquals(Integer.valueOf(32), typeSystem.ssArrayMember.getBitOffset().get());
        Assert.assertEquals(Integer.valueOf(64), typeSystem.ssIntMember.getBitOffset().get());
    }

    @Test
    public void testSetIsStackFrame() {
        typeSystem.uintType.setIsStackFrame(true);
        Assert.assertEquals(true, typeSystem.uintType.isStackFrame());
        typeSystem.uintType.setIsStackFrame(false);
        Assert.assertEquals(false, typeSystem.uintType.isStackFrame());
    }

    @Test(expected = NullPointerException.class)
    public void testSetName1() {
        typeSystem.uintType.setName(null);
    }

    @Test
    public void testSetName2() {
        typeSystem.uintType.setName("NEW NAME");
        Assert.assertEquals("NEW NAME", typeSystem.uintType.getName());
    }

    @Test
    public void testSetSigned() {
        typeSystem.uintType.setSigned(true);
        Assert.assertEquals(true, typeSystem.uintType.isSigned());
        typeSystem.uintType.setSigned(false);
        Assert.assertEquals(false, typeSystem.uintType.isSigned());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSubsequentMembers_Underflow() {
        typeSystem.simpleStruct.getSubsequentMembersInclusive((-1));
    }

    @Test
    public void testGetSubsequentMembers_FirstMember() {
        ImmutableList<TypeMember> expectedMembers = ImmutableList.<TypeMember>of(typeSystem.ssIntMember, typeSystem.ssUintMember, typeSystem.ssArrayMember);
        Assert.assertEquals(expectedMembers, typeSystem.simpleStruct.getSubsequentMembersInclusive(0));
    }

    @Test
    public void testGetSubsequentMembers_SecondMember() {
        final ImmutableList<TypeMember> members = typeSystem.simpleStruct.getSubsequentMembersInclusive(typeSystem.ssUintMember.getBitOffset().get());
        final ImmutableList<TypeMember> expectedMembers = ImmutableList.<TypeMember>of(typeSystem.ssUintMember, typeSystem.ssArrayMember);
        Assert.assertEquals(expectedMembers, members);
    }

    @Test
    public void testGetSubsequentMembers_ThirdMember() {
        final ImmutableList<TypeMember> members = typeSystem.simpleStruct.getSubsequentMembersInclusive(typeSystem.ssArrayMember.getBitOffset().get());
        final ImmutableList<TypeMember> expectedMembers = ImmutableList.<TypeMember>of(typeSystem.ssArrayMember);
        Assert.assertEquals(expectedMembers, members);
    }

    @Test
    public void testGetSubsequentMembers_PastEnd() {
        final ImmutableList<TypeMember> members = typeSystem.simpleStruct.getSubsequentMembersInclusive(typeSystem.simpleStruct.getBitSize());
        Assert.assertEquals(ImmutableList.<TypeMember>of(), members);
    }

    @Test
    public void testMemberOrderingConsistency0() {
        typeSystem.ssIntMember.setOffset(Optional.<Integer>of(typeSystem.simpleStruct.getBitSize()));
        final Iterator<TypeMember> iterator = typeSystem.simpleStruct.iterator();
        Assert.assertEquals(typeSystem.ssUintMember, iterator.next());
        Assert.assertEquals(typeSystem.ssArrayMember, iterator.next());
        Assert.assertEquals(typeSystem.ssIntMember, iterator.next());
    }

    @Test
    public void testMemberOrderingConsistency1() {
        typeSystem.ssUintMember.setOffset(Optional.<Integer>of(typeSystem.simpleStruct.getBitSize()));
        final Iterator<TypeMember> iterator = typeSystem.simpleStruct.iterator();
        Assert.assertEquals(typeSystem.ssIntMember, iterator.next());
        Assert.assertEquals(typeSystem.ssArrayMember, iterator.next());
        Assert.assertEquals(typeSystem.ssUintMember, iterator.next());
    }

    @Test
    public void testMemberOrderingConsistency2() {
        typeSystem.ssArrayMember.setOffset(Optional.<Integer>of(typeSystem.simpleStruct.getBitSize()));
        final Iterator<TypeMember> iterator = typeSystem.simpleStruct.iterator();
        Assert.assertEquals(typeSystem.ssIntMember, iterator.next());
        Assert.assertEquals(typeSystem.ssUintMember, iterator.next());
        Assert.assertEquals(typeSystem.ssArrayMember, iterator.next());
    }
}

