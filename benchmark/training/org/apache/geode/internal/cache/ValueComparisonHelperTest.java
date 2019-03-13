/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache;


import Token.DESTROYED;
import Token.INVALID;
import Token.LOCAL_INVALID;
import Token.NOT_AVAILABLE;
import Token.REMOVED_PHASE1;
import Token.REMOVED_PHASE2;
import Token.TOMBSTONE;
import java.io.Serializable;
import java.util.Arrays;
import org.apache.geode.internal.offheap.StoredObject;
import org.apache.geode.pdx.PdxInstance;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ValueComparisonHelperTest {
    private static final String STRING = "ABCD";

    private static final byte[] BYTE_ARRAY = new byte[]{ 65, 66, 67, 68 };

    private static final String STRING2 = "EFGH";

    private static final int[] INT_ARRAY = new int[]{ 65, 66, 67, 68 };

    private static final int[] INT_ARRAY2 = new int[]{ 65, 66, 67, 68 };

    private static final int[] INT_ARRAY3 = new int[]{ 65, 66, 67, 69 };

    private static final boolean[] BOOLEAN_ARRAY = new boolean[]{ true, false, false, true };

    private static final boolean[] BOOLEAN_ARRAY2 = new boolean[]{ true, false, false, true };

    private static final boolean[] BOOLEAN_ARRAY3 = new boolean[]{ true, false, true, true };

    private static final short[] SHORT_ARRAY = new short[]{ 65, 66, 67, 68 };

    private static final short[] SHORT_ARRAY2 = new short[]{ 65, 66, 67, 68 };

    private static final short[] SHORT_ARRAY3 = new short[]{ 65, 66, 67, 69 };

    private static final float[] FLOAT_ARRAY = new float[]{ 65.1F, 66.0F, 67.3F, 68.9F };

    private static final float[] FLOAT_ARRAY2 = new float[]{ 65.1F, 66.0F, 67.3F, 68.9F };

    private static final float[] FLOAT_ARRAY3 = new float[]{ 65.1F, 66.0F, 67.3F, 69.9F };

    private static final String[] STRING_ARRAY = new String[]{ "ABCD", "EFGH" };

    private static final String[] STRING_ARRAY2 = new String[]{ ValueComparisonHelperTest.STRING, ValueComparisonHelperTest.STRING2 };

    private static final String[] STRING_ARRAY3 = new String[]{ ValueComparisonHelperTest.STRING, ValueComparisonHelperTest.STRING2, "" };

    @Test
    public void basicEqualsCanCompareTwoNullObjects() {
        assertThat(ValueComparisonHelper.basicEquals(null, null)).isTrue();
    }

    @Test
    public void basicEqualsCanCompareWhenOnlyFirstObjectIsNull() {
        assertThat(ValueComparisonHelper.basicEquals(null, new Object())).isFalse();
    }

    @Test
    public void basicEqualsCanCompareWhenOnlySecondObjectIsNull() {
        assertThat(ValueComparisonHelper.basicEquals(new Object(), null)).isFalse();
    }

    @Test
    public void basicEqualsReturnsTrueWhenCompareTwoEqualByteArrays() {
        assertThat(ValueComparisonHelper.basicEquals(ValueComparisonHelperTest.STRING.getBytes(), ValueComparisonHelperTest.BYTE_ARRAY)).isTrue();
    }

    @Test
    public void basicEqualsReturnsFalseWhenCompareTwoNotEqualByteArrays() {
        assertThat(ValueComparisonHelper.basicEquals(ValueComparisonHelperTest.STRING.getBytes(), ValueComparisonHelperTest.STRING2.getBytes())).isFalse();
    }

    @Test
    public void basicEqualsReturnsFalseWhenOnlyFirstObjectIsAByteArray() {
        assertThat(ValueComparisonHelper.basicEquals(ValueComparisonHelperTest.BYTE_ARRAY, ValueComparisonHelperTest.INT_ARRAY)).isFalse();
    }

    @Test
    public void basicEqualsReturnsFalseWhenOnlySecondObjectIsAByteArray() {
        assertThat(ValueComparisonHelper.basicEquals(ValueComparisonHelperTest.INT_ARRAY, ValueComparisonHelperTest.BYTE_ARRAY)).isFalse();
    }

    @Test
    public void basicEqualsReturnsTrueWhenCompareTwoEqualIntArrays() {
        assertThat(ValueComparisonHelper.basicEquals(ValueComparisonHelperTest.INT_ARRAY, ValueComparisonHelperTest.INT_ARRAY2)).isTrue();
    }

    @Test
    public void basicEqualsReturnsFalseWhenCompareTwoNotEqualIntArrays() {
        assertThat(ValueComparisonHelper.basicEquals(ValueComparisonHelperTest.INT_ARRAY, ValueComparisonHelperTest.INT_ARRAY3)).isFalse();
    }

    @Test
    public void basicEqualsReturnsTrueWhenCompareTwoEqualLongArrays() {
        assertThat(ValueComparisonHelper.basicEquals(Arrays.stream(ValueComparisonHelperTest.INT_ARRAY).asLongStream().toArray(), Arrays.stream(ValueComparisonHelperTest.INT_ARRAY2).asLongStream().toArray())).isTrue();
    }

    @Test
    public void basicEqualsReturnsFalseWhenCompareTwoNotEqualLongArrays() {
        assertThat(ValueComparisonHelper.basicEquals(Arrays.stream(ValueComparisonHelperTest.INT_ARRAY3).asLongStream().toArray(), Arrays.stream(ValueComparisonHelperTest.INT_ARRAY2).asLongStream().toArray())).isFalse();
    }

    @Test
    public void basicEqualsReturnsTrueWhenCompareTwoEqualBooleanArrays() {
        assertThat(ValueComparisonHelper.basicEquals(ValueComparisonHelperTest.BOOLEAN_ARRAY, ValueComparisonHelperTest.BOOLEAN_ARRAY2)).isTrue();
    }

    @Test
    public void basicEqualsReturnsFalseWhenCompareTwoNotEqualBooleanArrays() {
        assertThat(ValueComparisonHelper.basicEquals(ValueComparisonHelperTest.BOOLEAN_ARRAY3, ValueComparisonHelperTest.BOOLEAN_ARRAY)).isFalse();
    }

    @Test
    public void basicEqualsReturnsFalseWhenOnlyFirstObjectIsABooleanArray() {
        assertThat(ValueComparisonHelper.basicEquals(ValueComparisonHelperTest.BOOLEAN_ARRAY, ValueComparisonHelperTest.SHORT_ARRAY)).isFalse();
    }

    @Test
    public void basicEqualsReturnsFalseWhenOnlySecondObjectIsABooleanArray() {
        assertThat(ValueComparisonHelper.basicEquals(ValueComparisonHelperTest.SHORT_ARRAY, ValueComparisonHelperTest.BOOLEAN_ARRAY)).isFalse();
    }

    @Test
    public void basicEqualsReturnsTrueWhenCompareTwoEqualShortArrays() {
        assertThat(ValueComparisonHelper.basicEquals(ValueComparisonHelperTest.SHORT_ARRAY, ValueComparisonHelperTest.SHORT_ARRAY2)).isTrue();
    }

    @Test
    public void basicEqualsReturnsFalseWhenCompareTwoNotEqualShortArrays() {
        assertThat(ValueComparisonHelper.basicEquals(ValueComparisonHelperTest.SHORT_ARRAY2, ValueComparisonHelperTest.SHORT_ARRAY3)).isFalse();
    }

    @Test
    public void basicEqualsReturnsTrueWhenCompareTwoEqualDoubleArrays() {
        assertThat(ValueComparisonHelper.basicEquals(Arrays.stream(ValueComparisonHelperTest.INT_ARRAY).asDoubleStream().toArray(), Arrays.stream(ValueComparisonHelperTest.INT_ARRAY2).asDoubleStream().toArray())).isTrue();
    }

    @Test
    public void basicEqualsReturnsFalseWhenCompareTwoNotEqualDoubleArrays() {
        assertThat(ValueComparisonHelper.basicEquals(Arrays.stream(ValueComparisonHelperTest.INT_ARRAY3).asDoubleStream().toArray(), Arrays.stream(ValueComparisonHelperTest.INT_ARRAY).asDoubleStream().toArray())).isFalse();
    }

    @Test
    public void basicEqualsReturnsTrueWhenCompareTwoEqualCharArrays() throws Exception {
        assertThat(ValueComparisonHelper.basicEquals(ValueComparisonHelperTest.STRING.toCharArray(), new String(ValueComparisonHelperTest.BYTE_ARRAY, "UTF-8").toCharArray())).isTrue();
    }

    @Test
    public void basicEqualsReturnsFalseWhenCompareTwoNotEqualCharArrays() throws Exception {
        assertThat(ValueComparisonHelper.basicEquals(ValueComparisonHelperTest.STRING2.toCharArray(), new String(ValueComparisonHelperTest.BYTE_ARRAY, "UTF-8").toCharArray())).isFalse();
    }

    @Test
    public void basicEqualsReturnsFalseWhenOnlyFirstObjectIsACharArray() {
        assertThat(ValueComparisonHelper.basicEquals(ValueComparisonHelperTest.STRING.toCharArray(), Arrays.stream(ValueComparisonHelperTest.INT_ARRAY).asDoubleStream().toArray())).isFalse();
    }

    @Test
    public void basicEqualsReturnsFalseWhenOnlySecondObjectIsACharArray() {
        assertThat(ValueComparisonHelper.basicEquals(Arrays.stream(ValueComparisonHelperTest.INT_ARRAY).asDoubleStream().toArray(), ValueComparisonHelperTest.STRING.toCharArray())).isFalse();
    }

    @Test
    public void basicEqualsReturnsTrueWhenCompareTwoEqualFloatArrays() throws Exception {
        assertThat(ValueComparisonHelper.basicEquals(ValueComparisonHelperTest.FLOAT_ARRAY, ValueComparisonHelperTest.FLOAT_ARRAY2)).isTrue();
    }

    @Test
    public void basicEqualsReturnsFalseWhenCompareTwoNotEqualFloatArrays() throws Exception {
        assertThat(ValueComparisonHelper.basicEquals(ValueComparisonHelperTest.FLOAT_ARRAY2, ValueComparisonHelperTest.FLOAT_ARRAY3)).isFalse();
    }

    @Test
    public void basicEqualsReturnsFalseWhenOnlySecondObjectIsAFloatArray() throws Exception {
        assertThat(ValueComparisonHelper.basicEquals(ValueComparisonHelperTest.STRING, ValueComparisonHelperTest.FLOAT_ARRAY3)).isFalse();
    }

    @Test
    public void basicEqualsReturnsTrueWhenCompareTwoEqualStringArrays() throws Exception {
        assertThat(ValueComparisonHelper.basicEquals(ValueComparisonHelperTest.STRING_ARRAY, ValueComparisonHelperTest.STRING_ARRAY2)).isTrue();
    }

    @Test
    public void basicEqualsReturnsFalseWhenCompareTwoNotEqualStringArrays() throws Exception {
        assertThat(ValueComparisonHelper.basicEquals(ValueComparisonHelperTest.STRING_ARRAY, ValueComparisonHelperTest.STRING_ARRAY3)).isFalse();
    }

    @Test
    public void basicEqualsReturnsFalseWhenOnlyFirstObjectIsAStringArray() {
        assertThat(ValueComparisonHelper.basicEquals(ValueComparisonHelperTest.STRING_ARRAY, Arrays.stream(ValueComparisonHelperTest.INT_ARRAY).asLongStream().toArray())).isFalse();
    }

    @Test
    public void basicEqualsReturnsFalseWhenOnlySecondObjectIsAStringArray() {
        assertThat(ValueComparisonHelper.basicEquals(Arrays.stream(ValueComparisonHelperTest.INT_ARRAY).asDoubleStream().toArray(), ValueComparisonHelperTest.STRING_ARRAY2)).isFalse();
    }

    @Test
    public void checkEqualsReturnsFalseWhenComparingNotSerializedCacheDeserializableWithPdxInstance() {
        PdxInstance pdxInstance1 = Mockito.mock(PdxInstance.class);
        CachedDeserializable object = Mockito.mock(CachedDeserializable.class);
        Mockito.when(object.isSerialized()).thenReturn(false);
        assertThat(ValueComparisonHelper.checkEquals(object, pdxInstance1, false, Mockito.mock(InternalCache.class))).isFalse();
    }

    @Test
    public void checkEqualsReturnsFalseWhenComparingPdxInstanceWithNonPdxCacheDeserializable() {
        int[] value = new int[]{ 0, 1, 2, 3 };
        Object object = new VMCachedDeserializable(EntryEventImpl.serialize(value));
        PdxInstance pdxInstance1 = Mockito.mock(PdxInstance.class);
        assertThat(ValueComparisonHelper.checkEquals(pdxInstance1, object, false, Mockito.mock(InternalCache.class))).isFalse();
    }

    @Test
    public void checkEqualsReturnsTrueWhenComparingPdxInstanceWithItsCacheDeserializable() {
        PdxInstance pdxInstance1 = Mockito.mock(PdxInstance.class);
        Object object = new VMCachedDeserializable(pdxInstance1, 1);
        assertThat(ValueComparisonHelper.checkEquals(pdxInstance1, object, false, Mockito.mock(InternalCache.class))).isTrue();
    }

    @Test
    public void checkEqualsReturnsFalseWhenComparingTwoNonEqualStoredObject() {
        StoredObject object1 = Mockito.mock(StoredObject.class);
        StoredObject object2 = Mockito.mock(StoredObject.class);
        Mockito.when(object1.checkDataEquals(object2)).thenReturn(false);
        assertThat(ValueComparisonHelper.checkEquals(object1, object2, false, Mockito.mock(InternalCache.class))).isFalse();
    }

    @Test
    public void checkEqualsReturnsTrueWhenComparingTwoEqualStoredObject() {
        StoredObject object1 = Mockito.mock(StoredObject.class);
        StoredObject object2 = Mockito.mock(StoredObject.class);
        Mockito.when(object1.checkDataEquals(object2)).thenReturn(true);
        assertThat(ValueComparisonHelper.checkEquals(object1, object2, false, Mockito.mock(InternalCache.class))).isTrue();
    }

    @Test
    public void checkEqualsReturnsFalseWhenComparingStoredObjectWithByteArrayAsCacheDeserializable() {
        StoredObject storedObject = Mockito.mock(StoredObject.class);
        Mockito.when(storedObject.isSerialized()).thenReturn(false);
        CachedDeserializable cachedDeserializable = Mockito.mock(CachedDeserializable.class);
        Mockito.when(cachedDeserializable.isSerialized()).thenReturn(true);
        assertThat(ValueComparisonHelper.checkEquals(storedObject, cachedDeserializable, false, Mockito.mock(InternalCache.class))).isFalse();
    }

    @Test
    public void checkEqualsReturnsFalseWhenComparingNonSerializedStoredObjectWithCacheDeserializable() {
        StoredObject storedObject = Mockito.mock(StoredObject.class);
        Mockito.when(storedObject.isSerialized()).thenReturn(false);
        CachedDeserializable cachedDeserializable = Mockito.mock(CachedDeserializable.class);
        Mockito.when(cachedDeserializable.isSerialized()).thenReturn(true);
        assertThat(ValueComparisonHelper.checkEquals(storedObject, cachedDeserializable, false, Mockito.mock(InternalCache.class))).isFalse();
    }

    @Test
    public void checkEqualsCanCompareStoredObjectWithCacheDeserializable() {
        StoredObject storedObject = Mockito.mock(StoredObject.class);
        Mockito.when(storedObject.isSerialized()).thenReturn(true);
        CachedDeserializable cachedDeserializable = Mockito.mock(CachedDeserializable.class);
        byte[] serializedObj = new byte[1];
        Mockito.when(cachedDeserializable.getSerializedValue()).thenReturn(serializedObj);
        ValueComparisonHelper.checkEquals(storedObject, cachedDeserializable, false, Mockito.mock(InternalCache.class));
        Mockito.verify(storedObject).checkDataEquals(serializedObj);
    }

    @Test
    public void checkEqualsReturnsFalseWhenComparingSerializedStoredObjectWithByteArray() {
        StoredObject storedObject = Mockito.mock(StoredObject.class);
        Mockito.when(storedObject.isSerialized()).thenReturn(true);
        byte[] object = new byte[1];
        assertThat(ValueComparisonHelper.checkEquals(storedObject, object, false, Mockito.mock(InternalCache.class))).isFalse();
    }

    @Test
    public void checkEqualsCanCompareNonSerializedStoredObjectWithByteArray() {
        StoredObject storedObject = Mockito.mock(StoredObject.class);
        Mockito.when(storedObject.isSerialized()).thenReturn(false);
        byte[] object = new byte[1];
        ValueComparisonHelper.checkEquals(storedObject, object, false, Mockito.mock(InternalCache.class));
        Mockito.verify(storedObject).checkDataEquals(object);
    }

    @Test
    public void checkEqualsReturnsFalseWhenComparingNonSerializedStoredObjectWithAnObject() {
        StoredObject storedObject = Mockito.mock(StoredObject.class);
        // storeObject is a byte[]
        Mockito.when(storedObject.isSerialized()).thenReturn(false);
        Object object = new Object();
        assertThat(ValueComparisonHelper.checkEquals(storedObject, object, false, Mockito.mock(InternalCache.class))).isFalse();
        Mockito.verify(storedObject, Mockito.never()).checkDataEquals(ArgumentMatchers.any(byte[].class));
    }

    @Test
    public void checkEqualsReturnsFalseWhenComparingSerializedStoredObjectWithANullObject() {
        StoredObject storedObject = Mockito.mock(StoredObject.class);
        Mockito.when(storedObject.isSerialized()).thenReturn(true);
        assertThat(ValueComparisonHelper.checkEquals(storedObject, null, false, Mockito.mock(InternalCache.class))).isFalse();
        Mockito.verify(storedObject, Mockito.never()).checkDataEquals(ArgumentMatchers.any(byte[].class));
    }

    @Test
    public void checkEqualsReturnsFalseWhenComparingSerializedStoredObjectWithNotAvailableToken() {
        StoredObject storedObject = Mockito.mock(StoredObject.class);
        Mockito.when(storedObject.isSerialized()).thenReturn(true);
        assertThat(ValueComparisonHelper.checkEquals(storedObject, NOT_AVAILABLE, false, Mockito.mock(InternalCache.class))).isFalse();
        Mockito.verify(storedObject, Mockito.never()).checkDataEquals(ArgumentMatchers.any(byte[].class));
    }

    @Test
    public void checkEqualsReturnsFalseWhenComparingSerializedStoredObjectWithInvalidToken() {
        StoredObject storedObject = Mockito.mock(StoredObject.class);
        Mockito.when(storedObject.isSerialized()).thenReturn(true);
        assertThat(ValueComparisonHelper.checkEquals(storedObject, INVALID, false, Mockito.mock(InternalCache.class))).isFalse();
        assertThat(ValueComparisonHelper.checkEquals(storedObject, LOCAL_INVALID, false, Mockito.mock(InternalCache.class))).isFalse();
        Mockito.verify(storedObject, Mockito.never()).checkDataEquals(ArgumentMatchers.any(byte[].class));
    }

    @Test
    public void checkEqualsReturnsFalseWhenComparingSerializedStoredObjectWithRemovedToken() {
        StoredObject storedObject = Mockito.mock(StoredObject.class);
        Mockito.when(storedObject.isSerialized()).thenReturn(true);
        assertThat(ValueComparisonHelper.checkEquals(storedObject, DESTROYED, false, Mockito.mock(InternalCache.class))).isFalse();
        assertThat(ValueComparisonHelper.checkEquals(storedObject, REMOVED_PHASE1, false, Mockito.mock(InternalCache.class))).isFalse();
        assertThat(ValueComparisonHelper.checkEquals(storedObject, REMOVED_PHASE2, false, Mockito.mock(InternalCache.class))).isFalse();
        assertThat(ValueComparisonHelper.checkEquals(storedObject, TOMBSTONE, false, Mockito.mock(InternalCache.class))).isFalse();
        Mockito.verify(storedObject, Mockito.never()).checkDataEquals(ArgumentMatchers.any(byte[].class));
    }

    @Test
    public void checkEqualsCanCompareSerializedStoredObjectWithAnObject() {
        StoredObject storedObject = Mockito.mock(StoredObject.class);
        Mockito.when(storedObject.isSerialized()).thenReturn(true);
        Object object = Mockito.mock(Serializable.class);
        ValueComparisonHelper.checkEquals(object, storedObject, false, Mockito.mock(InternalCache.class));
        Mockito.verify(storedObject).checkDataEquals(ArgumentMatchers.any(byte[].class));
    }

    @Test
    public void checkEqualsReturnsTrueWhenComparingTwoEqualNotSerializedCacheDeserializable() {
        CachedDeserializable cachedDeserializable = Mockito.mock(CachedDeserializable.class);
        Mockito.when(cachedDeserializable.isSerialized()).thenReturn(false);
        CachedDeserializable object = Mockito.mock(CachedDeserializable.class);
        Mockito.when(object.isSerialized()).thenReturn(false);
        Mockito.when(cachedDeserializable.getDeserializedForReading()).thenReturn(ValueComparisonHelperTest.BYTE_ARRAY);
        Mockito.when(object.getDeserializedForReading()).thenReturn(ValueComparisonHelperTest.STRING.getBytes());
        assertThat(ValueComparisonHelper.checkEquals(cachedDeserializable, object, false, Mockito.mock(InternalCache.class))).isTrue();
    }

    @Test
    public void checkEqualsReturnsTrueWhenComparingNotSerializedDeserializableWithAByteArray() {
        CachedDeserializable cachedDeserializable = Mockito.mock(CachedDeserializable.class);
        Mockito.when(cachedDeserializable.isSerialized()).thenReturn(false);
        byte[] object = ValueComparisonHelperTest.STRING.getBytes();
        Mockito.when(cachedDeserializable.getDeserializedForReading()).thenReturn(ValueComparisonHelperTest.BYTE_ARRAY);
        assertThat(ValueComparisonHelper.checkEquals(object, cachedDeserializable, false, Mockito.mock(InternalCache.class))).isTrue();
    }

    @Test
    public void checkEqualsReturnsFalseWhenComparingNotSerializedCacheDeserializableWithAnObject() {
        CachedDeserializable cachedDeserializable = Mockito.mock(CachedDeserializable.class);
        Mockito.when(cachedDeserializable.isSerialized()).thenReturn(false);
        Object object = new Object();
        assertThat(ValueComparisonHelper.checkEquals(object, cachedDeserializable, false, Mockito.mock(InternalCache.class))).isFalse();
    }

    @Test
    public void checkEqualsCanCompareNotSerializedCacheDeserializableWithSerializedOne() {
        CachedDeserializable cachedDeserializable = Mockito.mock(CachedDeserializable.class);
        Mockito.when(cachedDeserializable.isSerialized()).thenReturn(false);
        CachedDeserializable object = Mockito.mock(CachedDeserializable.class);
        Mockito.when(object.isSerialized()).thenReturn(true);
        assertThat(ValueComparisonHelper.checkEquals(cachedDeserializable, object, false, Mockito.mock(InternalCache.class))).isFalse();
    }

    @Test
    public void checkEqualsCanCompareCacheDeserializableIfIsCompressedOffheap() {
        CachedDeserializable cachedDeserializable = Mockito.mock(CachedDeserializable.class);
        Mockito.when(cachedDeserializable.isSerialized()).thenReturn(true);
        Mockito.when(cachedDeserializable.getValue()).thenReturn(ValueComparisonHelperTest.BYTE_ARRAY);
        CachedDeserializable object = Mockito.mock(CachedDeserializable.class);
        Mockito.when(object.getSerializedValue()).thenReturn(ValueComparisonHelperTest.STRING.getBytes());
        assertThat(ValueComparisonHelper.checkEquals(cachedDeserializable, object, true, Mockito.mock(InternalCache.class))).isTrue();
    }

    @Test
    public void checkEqualsCanCompareCacheDeserializableWithAnObjectIfIsCompressedOffheap() {
        CachedDeserializable cachedDeserializable = Mockito.mock(CachedDeserializable.class);
        Mockito.when(cachedDeserializable.isSerialized()).thenReturn(true);
        Mockito.when(cachedDeserializable.getValue()).thenReturn(EntryEventImpl.serialize(ValueComparisonHelperTest.STRING));
        assertThat(ValueComparisonHelper.checkEquals(cachedDeserializable, ValueComparisonHelperTest.STRING, true, Mockito.mock(InternalCache.class))).isTrue();
    }

    @Test
    public void checkEqualsCanCompareCacheDeserializable() {
        CachedDeserializable cachedDeserializable = Mockito.mock(CachedDeserializable.class);
        Mockito.when(cachedDeserializable.isSerialized()).thenReturn(true);
        Mockito.when(cachedDeserializable.getValue()).thenReturn(ValueComparisonHelperTest.BYTE_ARRAY);
        Mockito.when(cachedDeserializable.getDeserializedForReading()).thenReturn(ValueComparisonHelperTest.STRING);
        CachedDeserializable object = Mockito.mock(CachedDeserializable.class);
        Mockito.when(object.getDeserializedForReading()).thenReturn(ValueComparisonHelperTest.STRING);
        assertThat(ValueComparisonHelper.checkEquals(cachedDeserializable, object, false, Mockito.mock(InternalCache.class))).isTrue();
        Mockito.verify(cachedDeserializable).getDeserializedForReading();
        Mockito.verify(object).getDeserializedForReading();
    }

    @Test
    public void checkEqualsReturnsFalseWhenComparingCacheDeserializableWithANullObject() {
        CachedDeserializable cachedDeserializable = Mockito.mock(CachedDeserializable.class);
        Mockito.when(cachedDeserializable.isSerialized()).thenReturn(true);
        Mockito.when(cachedDeserializable.getValue()).thenReturn(ValueComparisonHelperTest.BYTE_ARRAY);
        assertThat(ValueComparisonHelper.checkEquals(cachedDeserializable, null, false, Mockito.mock(InternalCache.class))).isFalse();
        assertThat(ValueComparisonHelper.checkEquals(cachedDeserializable, null, true, Mockito.mock(InternalCache.class))).isFalse();
    }

    @Test
    public void checkEqualsReturnsFalseWhenComparingCacheDeserializableWithNotAvailableToken() {
        CachedDeserializable cachedDeserializable = Mockito.mock(CachedDeserializable.class);
        Mockito.when(cachedDeserializable.isSerialized()).thenReturn(true);
        Mockito.when(cachedDeserializable.getValue()).thenReturn(ValueComparisonHelperTest.BYTE_ARRAY);
        assertThat(ValueComparisonHelper.checkEquals(cachedDeserializable, NOT_AVAILABLE, false, Mockito.mock(InternalCache.class))).isFalse();
        assertThat(ValueComparisonHelper.checkEquals(cachedDeserializable, NOT_AVAILABLE, true, Mockito.mock(InternalCache.class))).isFalse();
    }

    @Test
    public void checkEqualsReturnsFalseWhenComparingCacheDeserializableWithInvalidToken() {
        CachedDeserializable cachedDeserializable = Mockito.mock(CachedDeserializable.class);
        Mockito.when(cachedDeserializable.isSerialized()).thenReturn(true);
        Mockito.when(cachedDeserializable.getValue()).thenReturn(ValueComparisonHelperTest.BYTE_ARRAY);
        assertThat(ValueComparisonHelper.checkEquals(cachedDeserializable, INVALID, false, Mockito.mock(InternalCache.class))).isFalse();
        assertThat(ValueComparisonHelper.checkEquals(cachedDeserializable, LOCAL_INVALID, false, Mockito.mock(InternalCache.class))).isFalse();
        assertThat(ValueComparisonHelper.checkEquals(cachedDeserializable, INVALID, true, Mockito.mock(InternalCache.class))).isFalse();
        assertThat(ValueComparisonHelper.checkEquals(cachedDeserializable, LOCAL_INVALID, true, Mockito.mock(InternalCache.class))).isFalse();
    }

    @Test
    public void checkEqualsReturnsFalseWhenComparingCacheDeserializableWithRemovedToken() {
        CachedDeserializable cachedDeserializable = Mockito.mock(CachedDeserializable.class);
        Mockito.when(cachedDeserializable.isSerialized()).thenReturn(true);
        Mockito.when(cachedDeserializable.getValue()).thenReturn(ValueComparisonHelperTest.BYTE_ARRAY);
        assertThat(ValueComparisonHelper.checkEquals(cachedDeserializable, DESTROYED, false, Mockito.mock(InternalCache.class))).isFalse();
        assertThat(ValueComparisonHelper.checkEquals(cachedDeserializable, REMOVED_PHASE1, false, Mockito.mock(InternalCache.class))).isFalse();
        assertThat(ValueComparisonHelper.checkEquals(cachedDeserializable, REMOVED_PHASE2, false, Mockito.mock(InternalCache.class))).isFalse();
        assertThat(ValueComparisonHelper.checkEquals(cachedDeserializable, TOMBSTONE, false, Mockito.mock(InternalCache.class))).isFalse();
    }

    @Test
    public void checkEqualsCanCompareCacheDeserializableWithAnObject() {
        CachedDeserializable cachedDeserializable = Mockito.mock(CachedDeserializable.class);
        Mockito.when(cachedDeserializable.isSerialized()).thenReturn(true);
        Mockito.when(cachedDeserializable.getValue()).thenReturn(ValueComparisonHelperTest.BYTE_ARRAY);
        Mockito.when(cachedDeserializable.getDeserializedForReading()).thenReturn(ValueComparisonHelperTest.STRING);
        assertThat(ValueComparisonHelper.checkEquals(cachedDeserializable, ValueComparisonHelperTest.STRING, false, Mockito.mock(InternalCache.class))).isTrue();
    }

    @Test
    public void checkEqualsCanCompareObjectAsValueCacheDeserializableWithCacheDeserializable() {
        CachedDeserializable cachedDeserializable = Mockito.mock(CachedDeserializable.class);
        Mockito.when(cachedDeserializable.isSerialized()).thenReturn(true);
        Mockito.when(cachedDeserializable.getValue()).thenReturn(ValueComparisonHelperTest.STRING);
        CachedDeserializable object = Mockito.mock(CachedDeserializable.class);
        Mockito.when(object.getDeserializedForReading()).thenReturn(ValueComparisonHelperTest.STRING);
        assertThat(ValueComparisonHelper.checkEquals(cachedDeserializable, object, false, Mockito.mock(InternalCache.class))).isTrue();
    }

    @Test
    public void checkEqualsCanCompareObjectAsValueCacheDeserializableWithAnObject() {
        CachedDeserializable cachedDeserializable = Mockito.mock(CachedDeserializable.class);
        Mockito.when(cachedDeserializable.isSerialized()).thenReturn(true);
        Mockito.when(cachedDeserializable.getValue()).thenReturn(ValueComparisonHelperTest.STRING);
        assertThat(ValueComparisonHelper.checkEquals(cachedDeserializable, ValueComparisonHelperTest.STRING, false, Mockito.mock(InternalCache.class))).isTrue();
    }

    @Test
    public void checkEqualsCanCompareTwoObjects() {
        assertThat(ValueComparisonHelper.checkEquals(ValueComparisonHelperTest.BOOLEAN_ARRAY, ValueComparisonHelperTest.BOOLEAN_ARRAY2, false, Mockito.mock(InternalCache.class))).isTrue();
    }
}

