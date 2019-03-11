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
package uk.gov.gchq.gaffer.mapstore.multimap;


import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.store.StoreException;


public class MapOfSetsTest {
    @Test
    public void shouldPutValueInExistingMapSet() throws StoreException {
        // Given
        final String key = "key1";
        final String value = "value1";
        final Map<String, Set<String>> map = new HashMap<>();
        final Set<String> set = Mockito.mock(Set.class);
        final MapOfSets<String, String> mapOfSets = new MapOfSets(map);
        map.put(key, set);
        BDDMockito.given(set.add(value)).willReturn(true);
        // When
        final boolean putResult = mapOfSets.put(key, value);
        // Then
        Assert.assertTrue(putResult);
        Mockito.verify(set).add(value);
    }

    @Test
    public void shouldPutValueInMapWhenNullSetAndNullSetClass() throws StoreException {
        // Given
        final String key = "key1";
        final String value = "value1";
        final Map<String, Set<String>> map = new HashMap<>();
        final MapOfSets<String, String> mapOfSets = new MapOfSets(map);
        // When
        final boolean putResult = mapOfSets.put(key, value);
        // Then
        Assert.assertTrue(putResult);
        Assert.assertEquals(Sets.newHashSet(value), mapOfSets.get(key));
    }

    @Test
    public void shouldPutValueInMapWhenNullSetAndLinkedHashSetClass() throws StoreException {
        // Given
        final String key = "key1";
        final String value = "value1";
        final Map<String, Set<String>> map = new HashMap<>();
        final MapOfSets<String, String> mapOfSets = new MapOfSets(map, LinkedHashSet.class);
        // When
        final boolean putResult = mapOfSets.put(key, value);
        // Then
        Assert.assertTrue(putResult);
        Assert.assertEquals(Sets.newLinkedHashSet(Collections.singleton(value)), map.get(key));
    }

    @Test
    public void shouldGetSetFromMap() throws StoreException {
        // Given
        final String key = "key1";
        final Set<String> set = Mockito.mock(Set.class);
        final Map<String, Set<String>> map = Mockito.mock(Map.class);
        final MapOfSets<String, String> mapOfSets = new MapOfSets(map, LinkedHashSet.class);
        BDDMockito.given(map.get(key)).willReturn(set);
        // When
        final Collection<String> result = mapOfSets.get(key);
        // Then
        Mockito.verify(map).get(key);
        Assert.assertSame(set, result);
    }

    @Test
    public void shouldClearMap() throws StoreException {
        // Given
        final String key = "key1";
        final Set<String> set = Mockito.mock(Set.class);
        final Map<String, Set<String>> map = Mockito.mock(Map.class);
        final MapOfSets<String, String> mapOfSets = new MapOfSets(map, LinkedHashSet.class);
        BDDMockito.given(map.get(key)).willReturn(set);
        // When
        mapOfSets.clear();
        // Then
        Mockito.verify(map).clear();
    }
}

