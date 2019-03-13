/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.internal.serialization.impl;


import FieldType.BOOLEAN;
import FieldType.BOOLEAN_ARRAY;
import com.hazelcast.nio.Bits;
import com.hazelcast.nio.BufferObjectDataInput;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.FieldDefinition;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class PortableUtilsTest extends HazelcastTestSupport {
    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void testConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(PortableUtils.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateAndGetArrayQuantifierFromCurrentToken_malformed() {
        PortableUtils.validateAndGetArrayQuantifierFromCurrentToken("legs[", "person.legs[0]");
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateAndGetArrayQuantifierFromCurrentToken_negative() {
        Assert.assertEquals(0, PortableUtils.validateAndGetArrayQuantifierFromCurrentToken("legs[-1]", "person.legs[-1]"));
    }

    @Test
    public void validateAndGetArrayQuantifierFromCurrentToken_correct() {
        Assert.assertEquals(0, PortableUtils.validateAndGetArrayQuantifierFromCurrentToken("legs[0]", "person.legs[0]"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateAndGetArrayQuantifierFromCurrentToken_withException() {
        PortableUtils.validateAndGetArrayQuantifierFromCurrentToken("legs", null);
    }

    @Test
    public void getPortableArrayCellPosition() throws Exception {
        // GIVEN
        BufferObjectDataInput in = Mockito.mock(BufferObjectDataInput.class);
        int offset = 10;
        int cellIndex = 3;
        // WHEN
        PortableUtils.getPortableArrayCellPosition(in, offset, cellIndex);
        // THEN
        Mockito.verify(in, Mockito.times(1)).readInt((offset + (cellIndex * (Bits.INT_SIZE_IN_BYTES))));
    }

    @Test
    public void isCurrentPathTokenWithoutQuantifier() {
        Assert.assertTrue(PortableUtils.isCurrentPathTokenWithoutQuantifier("wheels"));
        Assert.assertFalse(PortableUtils.isCurrentPathTokenWithoutQuantifier("wheels[1]"));
        Assert.assertFalse(PortableUtils.isCurrentPathTokenWithoutQuantifier("wheels[any]"));
    }

    @Test
    public void isCurrentPathTokenWithAnyQuantifier() {
        Assert.assertFalse(PortableUtils.isCurrentPathTokenWithAnyQuantifier("wheels"));
        Assert.assertFalse(PortableUtils.isCurrentPathTokenWithAnyQuantifier("wheels[1]"));
        Assert.assertTrue(PortableUtils.isCurrentPathTokenWithAnyQuantifier("wheels[any]"));
    }

    @Test
    public void unknownFieldException() {
        // GIVEN
        BufferObjectDataInput in = Mockito.mock(BufferObjectDataInput.class);
        ClassDefinition cd = Mockito.mock(ClassDefinition.class);
        PortableNavigatorContext ctx = new PortableNavigatorContext(in, cd, null);
        // WHEN
        HazelcastSerializationException ex = PortableUtils.createUnknownFieldException(ctx, "person.brain");
        // THEN
        Assert.assertNotNull(ex);
    }

    @Test
    public void wrongUseOfAnyOperationException() {
        // GIVEN
        BufferObjectDataInput in = Mockito.mock(BufferObjectDataInput.class);
        ClassDefinition cd = Mockito.mock(ClassDefinition.class);
        PortableNavigatorContext ctx = new PortableNavigatorContext(in, cd, null);
        // WHEN
        IllegalArgumentException ex = PortableUtils.createWrongUseOfAnyOperationException(ctx, "person.brain");
        // THEN
        Assert.assertNotNull(ex);
    }

    @Test
    public void validateArrayType_notArray() {
        // GIVEN
        ClassDefinition cd = Mockito.mock(ClassDefinition.class);
        FieldDefinition fd = Mockito.mock(FieldDefinition.class);
        // WHEN
        Mockito.when(fd.getType()).thenReturn(BOOLEAN);
        // THEN - ex thrown
        expected.expect(IllegalArgumentException.class);
        PortableUtils.validateArrayType(cd, fd, "person.brain");
    }

    @Test
    public void validateArrayType_array() {
        // GIVEN
        ClassDefinition cd = Mockito.mock(ClassDefinition.class);
        FieldDefinition fd = Mockito.mock(FieldDefinition.class);
        // WHEN
        Mockito.when(fd.getType()).thenReturn(BOOLEAN_ARRAY);
        // THEN - nothing thrown
        PortableUtils.validateArrayType(cd, fd, "person.brain");
    }

    @Test
    public void validateFactoryAndClass_compatible() {
        // GIVEN
        int factoryId = 1;
        int classId = 2;
        FieldDefinition fd = Mockito.mock(FieldDefinition.class);
        // WHEN ids are compatible
        Mockito.when(fd.getFactoryId()).thenReturn(factoryId);
        Mockito.when(fd.getClassId()).thenReturn(classId);
        // THEN - nothing thrown
        PortableUtils.validateFactoryAndClass(fd, factoryId, classId, "person.brain");
    }

    @Test
    public void validateFactoryAndClass_incompatibleFactoryId() {
        // GIVEN
        int factoryId = 1;
        int classId = 2;
        FieldDefinition fd = Mockito.mock(FieldDefinition.class);
        // WHEN ids are compatible
        Mockito.when(fd.getFactoryId()).thenReturn((factoryId + 1));
        Mockito.when(fd.getClassId()).thenReturn(classId);
        // THEN - ex thrown
        expected.expect(IllegalArgumentException.class);
        PortableUtils.validateFactoryAndClass(fd, factoryId, classId, "person.brain");
    }

    @Test
    public void validateFactoryAndClass_incompatibleClassId() {
        // GIVEN
        int factoryId = 1;
        int classId = 2;
        FieldDefinition fd = Mockito.mock(FieldDefinition.class);
        // WHEN ids are compatible
        Mockito.when(fd.getFactoryId()).thenReturn(factoryId);
        Mockito.when(fd.getClassId()).thenReturn((classId + 1));
        // THEN - ex thrown
        expected.expect(IllegalArgumentException.class);
        PortableUtils.validateFactoryAndClass(fd, factoryId, classId, "person.brain");
    }
}

