/**
 * ========================================================================
 */
/**
 * Copyright 2007-2011 David Yu dyuproject@gmail.com
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 * ========================================================================
 */
package io.protostuff.runtime;


import RuntimeEnv.ID_STRATEGY;
import io.protostuff.Schema;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

import static RuntimeEnv.COLLECTION_SCHEMA_ON_REPEATED_FIELDS;


/**
 * Test w/c fields should be excluded.
 *
 * @author David Yu
 */
public class FieldExclusionTest {
    @SuppressWarnings("unused")
    public static class EmptyFieldsPojo {
        transient int someInt;

        static long someLong;

        @Deprecated
        boolean g;
    }

    @SuppressWarnings("unused")
    public static class ComplexFieldsPojo {
        Collection<String[]> someCollectionValueArray;

        Collection<Collection<String>> someCollectionValueCollection;

        Collection<List<String>> someCollectionValueList;

        Collection<Set<String>> someCollectionValueSet;

        Collection<Map<String, Double>> someCollectionValueMap;

        Collection<String>[] arrayCollection;

        Collection<Object> someCollectionValueObject;

        Collection<?> someCollectionValueWildcard;

        List<String[]> someListValueArray;

        List<Collection<String>> someListValueCollection;

        List<List<String>> someListValueList;

        List<Set<String>> someListValueSet;

        List<Map<String, Double>> someListValueMap;

        List<String>[] arrayList;

        List<Object> someListValueObject;

        List<?> someListValueWildcard;

        Set<String[]> someSetValueArray;

        Set<Collection<String>> someSetValueCollection;

        Set<List<String>> someSetValueList;

        Set<Set<String>> someSetValueSet;

        Set<Map<String, Double>> someSetValueMap;

        Set<String>[] arraySet;

        Set<Object> someSetValueObject;

        Set<?> someSetValueWildcard;

        Map<String[], String> someMapKeyArray;

        Map<String, String[]> someMapValueArray;

        Map<String[], String[]> someMapBothArray;

        Map<String, String>[] arrayMap;

        Map<Object, Object> someMapBothObject;

        Map<?, ?> someMapBothWildcard;

        Map<Collection<String>, String> someMapKeyCollection;

        Map<String, Collection<String>> someMapValueCollection;

        Map<Collection<String>, Collection<String>> someMapBothCollection;

        Map<List<String>, String> someMapKeyList;

        Map<String, List<String>> someMapValueList;

        Map<List<String>, List<String>> someMapBothList;

        Map<Set<String>, String> someMapKeySet;

        Map<String, Set<String>> someMapValueSet;

        Map<Set<String>, Set<String>> someMapBothSet;

        Map<Map<String, String>, String> someMapKeyMap;

        Map<String, Map<String, String>> someMapValueMap;

        Map<Map<String, String>, Map<String, String>> someMapBothMap;

        Integer[][][][] someIntArray4D;

        long[][][] someLongArray3D;

        byte[][] someByteArray2D;
    }

    @Test
    public void testEmptyFieldsPojo() {
        RuntimeSchema<FieldExclusionTest.EmptyFieldsPojo> schema = RuntimeSchema.createFrom(FieldExclusionTest.EmptyFieldsPojo.class, ID_STRATEGY);
        Assert.assertNotNull(schema);
        Assert.assertEquals(0, schema.getFieldCount());
    }

    @Test
    public void testComplexFieldsPojo() {
        Schema<FieldExclusionTest.ComplexFieldsPojo> schema = RuntimeSchema.getSchema(FieldExclusionTest.ComplexFieldsPojo.class);
        RuntimeSchema<FieldExclusionTest.ComplexFieldsPojo> mappedSchema = ((RuntimeSchema<FieldExclusionTest.ComplexFieldsPojo>) (schema));
        Assert.assertTrue(((mappedSchema.getFieldCount()) == 45));
        Class<?> expectedCollectionClass = (COLLECTION_SCHEMA_ON_REPEATED_FIELDS) ? RuntimeCollectionField.class : RuntimeObjectField.class;
        FieldExclusionTest.assertAssignable(mappedSchema.getFieldByName("someCollectionValueArray"), expectedCollectionClass);
        FieldExclusionTest.assertAssignable(mappedSchema.getFieldByName("someCollectionValueCollection"), expectedCollectionClass);
        FieldExclusionTest.assertAssignable(mappedSchema.getFieldByName("someCollectionValueList"), expectedCollectionClass);
        FieldExclusionTest.assertAssignable(mappedSchema.getFieldByName("someCollectionValueSet"), expectedCollectionClass);
        FieldExclusionTest.assertAssignable(mappedSchema.getFieldByName("someCollectionValueMap"), expectedCollectionClass);
        FieldExclusionTest.assertAssignable(mappedSchema.getFieldByName("someCollectionValueObject"), expectedCollectionClass);
        FieldExclusionTest.assertAssignable(mappedSchema.getFieldByName("someCollectionValueWildcard"), expectedCollectionClass);
        FieldExclusionTest.assertAssignable(mappedSchema.getFieldByName("someListValueArray"), expectedCollectionClass);
        FieldExclusionTest.assertAssignable(mappedSchema.getFieldByName("someListValueCollection"), expectedCollectionClass);
        FieldExclusionTest.assertAssignable(mappedSchema.getFieldByName("someListValueList"), expectedCollectionClass);
        FieldExclusionTest.assertAssignable(mappedSchema.getFieldByName("someListValueSet"), expectedCollectionClass);
        FieldExclusionTest.assertAssignable(mappedSchema.getFieldByName("someListValueMap"), expectedCollectionClass);
        FieldExclusionTest.assertAssignable(mappedSchema.getFieldByName("someListValueObject"), expectedCollectionClass);
        FieldExclusionTest.assertAssignable(mappedSchema.getFieldByName("someListValueWildcard"), expectedCollectionClass);
        FieldExclusionTest.assertAssignable(mappedSchema.getFieldByName("someSetValueArray"), expectedCollectionClass);
        FieldExclusionTest.assertAssignable(mappedSchema.getFieldByName("someSetValueCollection"), expectedCollectionClass);
        FieldExclusionTest.assertAssignable(mappedSchema.getFieldByName("someSetValueList"), expectedCollectionClass);
        FieldExclusionTest.assertAssignable(mappedSchema.getFieldByName("someSetValueSet"), expectedCollectionClass);
        FieldExclusionTest.assertAssignable(mappedSchema.getFieldByName("someSetValueMap"), expectedCollectionClass);
        FieldExclusionTest.assertAssignable(mappedSchema.getFieldByName("someSetValueObject"), expectedCollectionClass);
        FieldExclusionTest.assertAssignable(mappedSchema.getFieldByName("someSetValueWildcard"), expectedCollectionClass);
        Assert.assertTrue(((mappedSchema.getFieldByName("someMapKeyArray")) instanceof RuntimeMapField<?, ?, ?>));
        Assert.assertTrue(((mappedSchema.getFieldByName("someMapValueArray")) instanceof RuntimeMapField<?, ?, ?>));
        Assert.assertTrue(((mappedSchema.getFieldByName("someMapBothArray")) instanceof RuntimeMapField<?, ?, ?>));
        Assert.assertTrue(((mappedSchema.getFieldByName("someMapBothObject")) instanceof RuntimeMapField<?, ?, ?>));
        Assert.assertTrue(((mappedSchema.getFieldByName("someMapBothWildcard")) instanceof RuntimeMapField<?, ?, ?>));
        Assert.assertTrue(((mappedSchema.getFieldByName("someMapKeyCollection")) instanceof RuntimeMapField<?, ?, ?>));
        Assert.assertTrue(((mappedSchema.getFieldByName("someMapValueCollection")) instanceof RuntimeMapField<?, ?, ?>));
        Assert.assertTrue(((mappedSchema.getFieldByName("someMapBothCollection")) instanceof RuntimeMapField<?, ?, ?>));
        Assert.assertTrue(((mappedSchema.getFieldByName("someMapKeyList")) instanceof RuntimeMapField<?, ?, ?>));
        Assert.assertTrue(((mappedSchema.getFieldByName("someMapValueList")) instanceof RuntimeMapField<?, ?, ?>));
        Assert.assertTrue(((mappedSchema.getFieldByName("someMapBothList")) instanceof RuntimeMapField<?, ?, ?>));
        Assert.assertTrue(((mappedSchema.getFieldByName("someMapKeySet")) instanceof RuntimeMapField<?, ?, ?>));
        Assert.assertTrue(((mappedSchema.getFieldByName("someMapValueSet")) instanceof RuntimeMapField<?, ?, ?>));
        Assert.assertTrue(((mappedSchema.getFieldByName("someMapBothSet")) instanceof RuntimeMapField<?, ?, ?>));
        Assert.assertTrue(((mappedSchema.getFieldByName("someMapKeyMap")) instanceof RuntimeMapField<?, ?, ?>));
        Assert.assertTrue(((mappedSchema.getFieldByName("someMapValueMap")) instanceof RuntimeMapField<?, ?, ?>));
        Assert.assertTrue(((mappedSchema.getFieldByName("someMapBothMap")) instanceof RuntimeMapField<?, ?, ?>));
        Assert.assertTrue(((mappedSchema.getFieldByName("someIntArray4D")) instanceof RuntimeObjectField<?>));
        Assert.assertTrue(((mappedSchema.getFieldByName("someLongArray3D")) instanceof RuntimeObjectField<?>));
        Assert.assertTrue(((mappedSchema.getFieldByName("someByteArray2D")) instanceof RuntimeObjectField<?>));
    }
}

