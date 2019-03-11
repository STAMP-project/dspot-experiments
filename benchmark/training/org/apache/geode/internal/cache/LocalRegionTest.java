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


import Token.INVALID;
import org.apache.geode.CancelCriterion;
import org.apache.geode.cache.EntryNotFoundException;
import org.apache.geode.cache.Operation;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.client.internal.ServerRegionProxy;
import org.junit.Test;
import org.mockito.Mockito;


public class LocalRegionTest {
    private LocalRegion region;

    private EntryEventImpl event;

    private ServerRegionProxy serverRegionProxy;

    private Operation operation;

    private CancelCriterion cancelCriterion;

    private RegionAttributes regionAttributes;

    private final Object key = new Object();

    private final String value = "value";

    @Test
    public void serverPutWillCheckPutIfAbsentResult() {
        Object result = new Object();
        operation = Operation.PUT_IF_ABSENT;
        Mockito.when(event.getOperation()).thenReturn(operation);
        Mockito.when(event.isCreate()).thenReturn(true);
        Mockito.when(serverRegionProxy.put(key, value, null, event, operation, true, null, null, true)).thenReturn(result);
        Mockito.doCallRealMethod().when(region).serverPut(event, true, null);
        region.serverPut(event, true, null);
        Mockito.verify(region).checkPutIfAbsentResult(event, value, result);
    }

    @Test
    public void checkPutIfAbsentResultSucceedsIfResultIsNull() {
        Object result = null;
        Mockito.doCallRealMethod().when(region).checkPutIfAbsentResult(event, value, result);
        region.checkPutIfAbsentResult(event, value, result);
        Mockito.verify(event, Mockito.never()).hasRetried();
    }

    @Test(expected = EntryNotFoundException.class)
    public void checkPutIfAbsentResultThrowsIfResultNotNullAndEventHasNotRetried() {
        Object result = new Object();
        Mockito.when(event.hasRetried()).thenReturn(false);
        Mockito.doCallRealMethod().when(region).checkPutIfAbsentResult(event, value, result);
        region.checkPutIfAbsentResult(event, value, result);
    }

    @Test(expected = EntryNotFoundException.class)
    public void checkPutIfAbsentResultThrowsIfEventHasRetriedButResultNotHaveSameValue() {
        Object result = new Object();
        Mockito.when(event.hasRetried()).thenReturn(true);
        Mockito.when(region.putIfAbsentResultHasSameValue(true, value, result)).thenReturn(false);
        Mockito.doCallRealMethod().when(region).checkPutIfAbsentResult(event, value, result);
        region.checkPutIfAbsentResult(event, value, result);
    }

    @Test
    public void checkPutIfAbsentResultSucceedsIfEventHasRetriedAndResultHasSameValue() {
        Object result = new Object();
        Mockito.when(event.hasRetried()).thenReturn(true);
        Mockito.when(region.putIfAbsentResultHasSameValue(true, value, result)).thenReturn(true);
        Mockito.doCallRealMethod().when(region).checkPutIfAbsentResult(event, value, result);
        region.checkPutIfAbsentResult(event, value, result);
        Mockito.verify(event).hasRetried();
        Mockito.verify(region).putIfAbsentResultHasSameValue(true, value, result);
    }

    @Test
    public void putIfAbsentResultHasSameValueReturnTrueIfResultIsInvalidTokenAndValueToBePutIsNull() {
        Mockito.when(region.putIfAbsentResultHasSameValue(true, null, INVALID)).thenCallRealMethod();
        assertThat(region.putIfAbsentResultHasSameValue(true, null, INVALID)).isTrue();
    }

    @Test
    public void putIfAbsentResultHasSameValueReturnFalseIfResultIsInvalidTokenAndValueToBePutIsNotNull() {
        Mockito.when(region.putIfAbsentResultHasSameValue(true, value, INVALID)).thenCallRealMethod();
        assertThat(region.putIfAbsentResultHasSameValue(true, value, INVALID)).isFalse();
    }

    @Test
    public void putIfAbsentResultHasSameValueReturnTrueIfResultHasSameValue() {
        Object result = "value";
        Mockito.when(region.putIfAbsentResultHasSameValue(true, value, result)).thenCallRealMethod();
        assertThat(region.putIfAbsentResultHasSameValue(true, value, result)).isTrue();
        Mockito.verify(region, Mockito.never()).getAttributes();
    }

    @Test
    public void putIfAbsentResultHasSameValueReturnFalseIfResultDoesNotHaveSameValue() {
        Object result = "differentValue";
        Mockito.when(region.putIfAbsentResultHasSameValue(true, value, result)).thenCallRealMethod();
        assertThat(region.putIfAbsentResultHasSameValue(true, value, result)).isFalse();
        Mockito.verify(region, Mockito.never()).getAttributes();
    }

    @Test
    public void putIfAbsentResultHasSameValueChecksRegionAttributesIfNotFromClient() {
        Object result = "value";
        Mockito.when(region.putIfAbsentResultHasSameValue(false, value, result)).thenCallRealMethod();
        assertThat(region.putIfAbsentResultHasSameValue(false, value, result)).isTrue();
        Mockito.verify(region).getAttributes();
    }

    @Test
    public void putIfAbsentResultHasSameValueReturnFalseIfResultDoesNotHaveSameValueAndNotFromClient() {
        Object oldValue = "differentValue";
        Object result = new VMCachedDeserializable(EntryEventImpl.serialize(oldValue));
        Mockito.when(region.putIfAbsentResultHasSameValue(false, value, result)).thenCallRealMethod();
        assertThat(region.putIfAbsentResultHasSameValue(false, value, result)).isFalse();
        Mockito.verify(region).getAttributes();
    }

    @Test
    public void putIfAbsentResultHasSameValueReturnTrueIfResultHasSameValueAndNotFromClient() {
        Object result = new VMCachedDeserializable(EntryEventImpl.serialize(value));
        Mockito.when(region.putIfAbsentResultHasSameValue(false, value, result)).thenCallRealMethod();
        assertThat(region.putIfAbsentResultHasSameValue(false, value, result)).isTrue();
        Mockito.verify(region).getAttributes();
    }

    @Test
    public void bridgePutIfAbsentResultHasSameValueCanCheckValueForObject() {
        Object result = "value";
        byte[] valueToBePut = EntryEventImpl.serialize(value);
        Mockito.when(region.bridgePutIfAbsentResultHasSameValue(valueToBePut, true, result)).thenCallRealMethod();
        assertThat(region.bridgePutIfAbsentResultHasSameValue(valueToBePut, true, result)).isTrue();
        Mockito.verify(region).getAttributes();
    }

    @Test
    public void bridgePutIfAbsentResultHasSameValueCanCheckValueForNonObjectByteArray() {
        byte[] valueToBePut = new byte[]{ 0, 1, 2, 3 };
        Object result = new VMCachedDeserializable(EntryEventImpl.serialize(valueToBePut));
        Mockito.when(region.bridgePutIfAbsentResultHasSameValue(valueToBePut, false, result)).thenCallRealMethod();
        assertThat(region.bridgePutIfAbsentResultHasSameValue(valueToBePut, false, result)).isTrue();
        Mockito.verify(region).getAttributes();
    }

    @Test
    public void bridgePutIfAbsentResultHasSameValueCanCheckValueForIntArray() {
        int[] newValue = new int[]{ 0, 1, 2, 3 };
        byte[] valueToBePut = EntryEventImpl.serialize(newValue);
        Object result = newValue;
        Mockito.when(region.bridgePutIfAbsentResultHasSameValue(valueToBePut, true, result)).thenCallRealMethod();
        assertThat(region.bridgePutIfAbsentResultHasSameValue(valueToBePut, true, result)).isTrue();
        Mockito.verify(region).getAttributes();
    }

    @Test
    public void bridgePutIfAbsentResultHasSameValueCanCheckValueForArrayOfArray() {
        String[] array1 = new String[]{ "0", "1", "2" };
        String[] array2 = new String[]{ "3", "4", "5" };
        String[] array3 = new String[]{ "7" };
        String[][] newValue = new String[][]{ array1, array2, array3 };
        byte[] valueToBePut = EntryEventImpl.serialize(newValue);
        Object result = new VMCachedDeserializable(EntryEventImpl.serialize(newValue));
        Mockito.when(region.bridgePutIfAbsentResultHasSameValue(valueToBePut, true, result)).thenCallRealMethod();
        assertThat(region.bridgePutIfAbsentResultHasSameValue(valueToBePut, true, result)).isTrue();
        Mockito.verify(region).getAttributes();
    }

    @Test
    public void bridgePutIfAbsentResultHasSameValueCanCheckDifferentValuesForArrayOfArray() {
        String[] array1 = new String[]{ "0", "1", "2" };
        String[] array2 = new String[]{ "3", "4", "5" };
        String[] array3 = new String[]{ "7" };
        String[] array4 = new String[]{ "8" };
        String[][] newValue = new String[][]{ array1, array2, array3 };
        String[][] returnedValue = new String[][]{ array1, array2, array4 };
        byte[] valueToBePut = EntryEventImpl.serialize(newValue);
        Object result = new VMCachedDeserializable(EntryEventImpl.serialize(returnedValue));
        Mockito.when(region.bridgePutIfAbsentResultHasSameValue(valueToBePut, true, result)).thenCallRealMethod();
        assertThat(region.bridgePutIfAbsentResultHasSameValue(valueToBePut, true, result)).isFalse();
        Mockito.verify(region).getAttributes();
    }
}

