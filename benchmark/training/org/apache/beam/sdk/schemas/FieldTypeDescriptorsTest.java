/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.schemas;


import FieldType.BOOLEAN;
import FieldType.BYTE;
import FieldType.BYTES;
import FieldType.DATETIME;
import FieldType.DECIMAL;
import FieldType.DOUBLE;
import FieldType.FLOAT;
import FieldType.INT16;
import FieldType.INT32;
import FieldType.INT64;
import FieldType.STRING;
import java.util.ArrayList;
import org.apache.beam.sdk.schemas.Schema.FieldType;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * test for {@link FieldTypeDescriptors}.
 */
public class FieldTypeDescriptorsTest {
    @Test
    public void testPrimitiveTypeToJavaType() {
        Assert.assertEquals(TypeDescriptors.bytes(), FieldTypeDescriptors.javaTypeForFieldType(BYTE));
        Assert.assertEquals(TypeDescriptors.shorts(), FieldTypeDescriptors.javaTypeForFieldType(INT16));
        Assert.assertEquals(TypeDescriptors.integers(), FieldTypeDescriptors.javaTypeForFieldType(INT32));
        Assert.assertEquals(TypeDescriptors.longs(), FieldTypeDescriptors.javaTypeForFieldType(INT64));
        Assert.assertEquals(TypeDescriptors.bigdecimals(), FieldTypeDescriptors.javaTypeForFieldType(DECIMAL));
        Assert.assertEquals(TypeDescriptors.floats(), FieldTypeDescriptors.javaTypeForFieldType(FLOAT));
        Assert.assertEquals(TypeDescriptors.doubles(), FieldTypeDescriptors.javaTypeForFieldType(DOUBLE));
        Assert.assertEquals(TypeDescriptors.strings(), FieldTypeDescriptors.javaTypeForFieldType(STRING));
        Assert.assertEquals(TypeDescriptor.of(Instant.class), FieldTypeDescriptors.javaTypeForFieldType(DATETIME));
        Assert.assertEquals(TypeDescriptors.booleans(), FieldTypeDescriptors.javaTypeForFieldType(BOOLEAN));
        Assert.assertEquals(TypeDescriptor.of(byte[].class), FieldTypeDescriptors.javaTypeForFieldType(BYTES));
    }

    @Test
    public void testRowTypeToJavaType() {
        Assert.assertEquals(TypeDescriptors.lists(TypeDescriptors.rows()), FieldTypeDescriptors.javaTypeForFieldType(FieldType.array(FieldType.row(Schema.builder().build()))));
    }

    @Test
    public void testArrayTypeToJavaType() {
        Assert.assertEquals(TypeDescriptors.lists(TypeDescriptors.longs()), FieldTypeDescriptors.javaTypeForFieldType(FieldType.array(INT64)));
        Assert.assertEquals(TypeDescriptors.lists(TypeDescriptors.lists(TypeDescriptors.longs())), FieldTypeDescriptors.javaTypeForFieldType(FieldType.array(FieldType.array(INT64))));
    }

    @Test
    public void testMapTypeToJavaType() {
        Assert.assertEquals(TypeDescriptors.maps(TypeDescriptors.strings(), TypeDescriptors.longs()), FieldTypeDescriptors.javaTypeForFieldType(FieldType.map(STRING, INT64)));
        Assert.assertEquals(TypeDescriptors.maps(TypeDescriptors.strings(), TypeDescriptors.lists(TypeDescriptors.longs())), FieldTypeDescriptors.javaTypeForFieldType(FieldType.map(STRING, FieldType.array(INT64))));
    }

    @Test
    public void testPrimitiveTypeToFieldType() {
        Assert.assertEquals(BYTE, FieldTypeDescriptors.fieldTypeForJavaType(TypeDescriptors.bytes()));
        Assert.assertEquals(INT16, FieldTypeDescriptors.fieldTypeForJavaType(TypeDescriptors.shorts()));
        Assert.assertEquals(INT32, FieldTypeDescriptors.fieldTypeForJavaType(TypeDescriptors.integers()));
        Assert.assertEquals(INT64, FieldTypeDescriptors.fieldTypeForJavaType(TypeDescriptors.longs()));
        Assert.assertEquals(DECIMAL, FieldTypeDescriptors.fieldTypeForJavaType(TypeDescriptors.bigdecimals()));
        Assert.assertEquals(FLOAT, FieldTypeDescriptors.fieldTypeForJavaType(TypeDescriptors.floats()));
        Assert.assertEquals(DOUBLE, FieldTypeDescriptors.fieldTypeForJavaType(TypeDescriptors.doubles()));
        Assert.assertEquals(STRING, FieldTypeDescriptors.fieldTypeForJavaType(TypeDescriptors.strings()));
        Assert.assertEquals(DATETIME, FieldTypeDescriptors.fieldTypeForJavaType(TypeDescriptor.of(Instant.class)));
        Assert.assertEquals(BOOLEAN, FieldTypeDescriptors.fieldTypeForJavaType(TypeDescriptors.booleans()));
        Assert.assertEquals(BYTES, FieldTypeDescriptors.fieldTypeForJavaType(TypeDescriptor.of(byte[].class)));
    }

    @Rule
    public transient ExpectedException thrown = ExpectedException.none();

    @Test
    public void testRowTypeToFieldType() {
        thrown.expect(IllegalArgumentException.class);
        FieldTypeDescriptors.fieldTypeForJavaType(TypeDescriptors.rows());
    }

    @Test
    public void testArrayTypeToFieldType() {
        Assert.assertEquals(FieldType.array(STRING), FieldTypeDescriptors.fieldTypeForJavaType(TypeDescriptors.lists(TypeDescriptors.strings())));
        Assert.assertEquals(FieldType.array(FieldType.array(STRING)), FieldTypeDescriptors.fieldTypeForJavaType(TypeDescriptors.lists(TypeDescriptors.lists(TypeDescriptors.strings()))));
        Assert.assertEquals(FieldType.array(STRING), FieldTypeDescriptors.fieldTypeForJavaType(TypeDescriptor.of(new ArrayList<String>() {}.getClass())));
    }

    @Test
    public void testMapTypeToFieldType() {
        Assert.assertEquals(FieldType.map(STRING, INT64), FieldTypeDescriptors.fieldTypeForJavaType(TypeDescriptors.maps(TypeDescriptors.strings(), TypeDescriptors.longs())));
        Assert.assertEquals(FieldType.map(STRING, FieldType.array(INT64)), FieldTypeDescriptors.fieldTypeForJavaType(TypeDescriptors.maps(TypeDescriptors.strings(), TypeDescriptors.lists(TypeDescriptors.longs()))));
    }
}

