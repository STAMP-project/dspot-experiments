/**
 * Copyright 2017-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.mapstore.utils;


import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.store.StoreException;


public class MapWrapperTest {
    @Test
    public void shouldDelegateAllCallsToMap() throws StoreException {
        // Given
        final String key = "key1";
        final String value = "value1";
        final String value2 = "value2";
        final int size = 10;
        final boolean isEmpty = false;
        final boolean containsKey = true;
        final boolean containsValue = true;
        final Map<String, String> map = Mockito.mock(Map.class);
        final Map<String, String> map2 = Mockito.mock(Map.class);
        final Set<String> keySet = Mockito.mock(Set.class);
        final Collection<String> values = Mockito.mock(Collection.class);
        final Set<Map.Entry<String, String>> entrySet = Mockito.mock(Set.class);
        BDDMockito.given(map.put(key, value)).willReturn(value2);
        BDDMockito.given(map.get(key)).willReturn(value);
        BDDMockito.given(map.size()).willReturn(size);
        BDDMockito.given(map.isEmpty()).willReturn(isEmpty);
        BDDMockito.given(map.containsKey(key)).willReturn(containsKey);
        BDDMockito.given(map.containsValue(value)).willReturn(containsValue);
        BDDMockito.given(map.remove(key)).willReturn(value);
        BDDMockito.given(map.keySet()).willReturn(keySet);
        BDDMockito.given(map.values()).willReturn(values);
        BDDMockito.given(map.entrySet()).willReturn(entrySet);
        final MapWrapper<String, String> wrapper = new MapWrapper(map);
        // When / Then - put
        final String putResult = wrapper.put(key, value);
        Mockito.verify(map).put(key, value);
        Assert.assertEquals(value2, putResult);
        // When / Then - get
        final String getResult = wrapper.get(key);
        Mockito.verify(map).get(key);
        Assert.assertEquals(value, getResult);
        // When / Then - size
        final int sizeResult = wrapper.size();
        Mockito.verify(map).size();
        Assert.assertEquals(size, sizeResult);
        // When / Then - isEmpty
        final boolean isEmptyResult = wrapper.isEmpty();
        Mockito.verify(map).size();
        Assert.assertEquals(isEmpty, isEmptyResult);
        // When / Then - containsKey
        final boolean containsKeyResult = wrapper.containsKey(key);
        Mockito.verify(map).containsKey(key);
        Assert.assertEquals(containsKey, containsKeyResult);
        // When / Then - containsValue
        final boolean containsValueResult = wrapper.containsValue(value);
        Mockito.verify(map).containsValue(value);
        Assert.assertEquals(containsValue, containsValueResult);
        // When / Then - remove
        final String removeResult = wrapper.remove(key);
        Mockito.verify(map).remove(key);
        Assert.assertEquals(value, removeResult);
        // When / Then - putAll
        wrapper.putAll(map2);
        Mockito.verify(map).putAll(map2);
        // When / Then - clear
        wrapper.clear();
        Mockito.verify(map).clear();
        // When / Then - keySet
        final Set<String> keySetResult = wrapper.keySet();
        Mockito.verify(map).keySet();
        Assert.assertSame(keySet, keySetResult);
        // When / Then - values
        final Collection<String> valuesResult = wrapper.values();
        Mockito.verify(map).values();
        Assert.assertSame(values, valuesResult);
        // When / Then - entrySet
        final Set<Map.Entry<String, String>> entrySetResult = wrapper.entrySet();
        Mockito.verify(map).entrySet();
        Assert.assertSame(entrySet, entrySetResult);
        // When / Then - getMap
        final Map<String, String> mapResult = wrapper.getMap();
        Assert.assertSame(map, mapResult);
    }
}

