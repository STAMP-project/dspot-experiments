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


import FieldType.BYTE;
import FieldType.PORTABLE;
import com.hazelcast.nio.serialization.ClassDefinitionBuilder;
import com.hazelcast.nio.serialization.FieldDefinition;
import com.hazelcast.nio.serialization.FieldType;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClassAndFieldDefinitionTest {
    int portableVersion = 1;

    private ClassDefinitionImpl classDefinition;

    private static String[] fieldNames = new String[]{ "f1", "f2", "f3" };

    @Test
    public void testClassDef_getter_setter() throws Exception {
        ClassDefinitionImpl cd = ((ClassDefinitionImpl) (new ClassDefinitionBuilder(1, 2, portableVersion).build()));
        cd.setVersionIfNotSet(3);
        cd.setVersionIfNotSet(5);
        Assert.assertEquals(1, cd.getFactoryId());
        Assert.assertEquals(2, cd.getClassId());
        Assert.assertEquals(portableVersion, cd.getVersion());
        Assert.assertEquals(3, classDefinition.getFieldCount());
    }

    @Test
    public void testClassDef_getField_properIndex() throws Exception {
        for (int i = 0; i < (classDefinition.getFieldCount()); i++) {
            FieldDefinition field = classDefinition.getField(i);
            Assert.assertNotNull(field);
        }
    }

    @Test
    public void testClassDef_hasField() throws Exception {
        for (int i = 0; i < (classDefinition.getFieldCount()); i++) {
            String fieldName = ClassAndFieldDefinitionTest.fieldNames[i];
            boolean hasField = classDefinition.hasField(fieldName);
            Assert.assertTrue(hasField);
        }
    }

    @Test
    public void testClassDef_getFieldType() throws Exception {
        for (String fieldName : ClassAndFieldDefinitionTest.fieldNames) {
            FieldType fieldType = classDefinition.getFieldType(fieldName);
            Assert.assertNotNull(fieldType);
        }
    }

    @Test
    public void testClassDef_getFieldClassId() throws Exception {
        for (String fieldName : ClassAndFieldDefinitionTest.fieldNames) {
            int classId = classDefinition.getFieldClassId(fieldName);
            Assert.assertEquals(0, classId);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClassDef_getFieldClassId_invalidField() throws Exception {
        classDefinition.getFieldClassId("The Invalid Field");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClassDef_getFieldType_invalidField() throws Exception {
        classDefinition.getFieldType("The Invalid Field");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testClassDef_getField_negativeIndex() throws Exception {
        classDefinition.getField((-1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testClassDef_getField_HigherThenSizeIndex() throws Exception {
        classDefinition.getField(classDefinition.getFieldCount());
    }

    @Test
    public void testClassDef_equal_hashCode() throws Exception {
        ClassDefinitionImpl cdEmpty1 = ((ClassDefinitionImpl) (new ClassDefinitionBuilder(1, 2, 3).build()));
        ClassDefinitionImpl cdEmpty2 = ((ClassDefinitionImpl) (new ClassDefinitionBuilder(1, 2, 3).build()));
        ClassDefinitionImpl cd1 = ((ClassDefinitionImpl) (new ClassDefinitionBuilder(1, 2, 5).build()));
        ClassDefinitionImpl cd2 = ((ClassDefinitionImpl) (new ClassDefinitionBuilder(2, 2, 3).build()));
        ClassDefinitionImpl cd3 = ((ClassDefinitionImpl) (new ClassDefinitionBuilder(1, 9, 3).build()));
        ClassDefinitionImpl cdWithField = ((ClassDefinitionImpl) (new ClassDefinitionBuilder(1, 2, 3).addIntField("f1").build()));
        Assert.assertEquals(cdEmpty1, cdEmpty2);
        Assert.assertNotEquals(cd1, cdEmpty1);
        Assert.assertNotEquals(cd2, cdEmpty1);
        Assert.assertNotEquals(cd3, cdEmpty1);
        Assert.assertNotEquals(cdWithField, classDefinition);
        Assert.assertNotEquals(cdEmpty1, classDefinition);
        Assert.assertNotEquals(classDefinition, null);
        Assert.assertNotEquals(classDefinition, "Another Class");
        Assert.assertNotEquals(0, cd1.hashCode());
    }

    @Test
    public void testClassDef_toString() throws Exception {
        Assert.assertNotNull(classDefinition.toString());
    }

    @Test
    public void testFieldDef_getter_setter() throws Exception {
        FieldDefinition field0 = classDefinition.getField(0);
        FieldDefinition field = classDefinition.getField("f1");
        FieldDefinitionImpl fd = new FieldDefinitionImpl(9, "name", FieldType.PORTABLE, 5, 6, 7);
        FieldDefinitionImpl fd_nullName = new FieldDefinitionImpl(10, null, FieldType.PORTABLE, 15, 16, 17);
        Assert.assertEquals(field, field0);
        Assert.assertEquals(0, field.getFactoryId());
        Assert.assertEquals(0, field.getClassId());
        Assert.assertEquals(3, field.getVersion());
        Assert.assertEquals(0, field.getIndex());
        Assert.assertEquals("f1", field.getName());
        Assert.assertEquals(BYTE, field.getType());
        Assert.assertEquals(5, fd.getFactoryId());
        Assert.assertEquals(6, fd.getClassId());
        Assert.assertEquals(7, fd.getVersion());
        Assert.assertEquals(9, fd.getIndex());
        Assert.assertEquals("name", fd.getName());
        Assert.assertEquals(PORTABLE, fd.getType());
        Assert.assertEquals(15, fd_nullName.getFactoryId());
        Assert.assertEquals(16, fd_nullName.getClassId());
        Assert.assertEquals(17, fd_nullName.getVersion());
        Assert.assertEquals(10, fd_nullName.getIndex());
        Assert.assertNull(fd_nullName.getName());
        Assert.assertEquals(PORTABLE, fd_nullName.getType());
    }

    @Test
    public void testFieldDef_equal_hashCode() throws Exception {
        FieldDefinitionImpl fd0 = new FieldDefinitionImpl(0, "name", FieldType.BOOLEAN, portableVersion);
        FieldDefinitionImpl fd0_1 = new FieldDefinitionImpl(0, "name", FieldType.INT, portableVersion);
        FieldDefinitionImpl fd1 = new FieldDefinitionImpl(1, "name", FieldType.BOOLEAN, portableVersion);
        FieldDefinitionImpl fd2 = new FieldDefinitionImpl(0, "namex", FieldType.BOOLEAN, portableVersion);
        Assert.assertNotEquals(fd0, fd0_1);
        Assert.assertNotEquals(fd0, fd1);
        Assert.assertNotEquals(fd0, fd2);
        Assert.assertNotEquals(fd0, null);
        Assert.assertNotEquals(fd0, "Another Class");
        Assert.assertNotEquals(0, fd0.hashCode());
    }

    @Test
    public void testFieldDef_toString() throws Exception {
        Assert.assertNotNull(new FieldDefinitionImpl(0, "name", FieldType.BOOLEAN, portableVersion).toString());
    }
}

