/**
 * Copyright 2016 Victor Albertos
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
package io.rx_cache2.internal.cache;


import io.rx_cache2.internal.Memory;
import io.rx_cache2.internal.Record;
import io.rx_cache2.internal.common.BaseTest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


public class ActionTest extends BaseTest {
    private Memory memory;

    private Action actionUT;

    private static final String PROVIDER_KEY = "get_mocks";

    private static final String DYNAMIC_KEY_1 = "filter_1";

    private static final String DYNAMIC_KEY_2 = "filter_2";

    private static final String DYNAMIC_KEY_GROUP_1 = "page_1";

    private static final String DYNAMIC_KEY_GROUP_2 = "page_2";

    @Test
    public void Check_Keys_Matching_Provider_Key() {
        List<String> keysMatchingProviderKey = actionUT.getKeysOnMemoryMatchingProviderKey(ActionTest.PROVIDER_KEY);
        MatcherAssert.assertThat(keysMatchingProviderKey.get(0), Is.is(ActionTest.filter1Page1));
        MatcherAssert.assertThat(keysMatchingProviderKey.get(1), Is.is(ActionTest.filter1Page2));
        MatcherAssert.assertThat(keysMatchingProviderKey.get(2), Is.is(ActionTest.filter2Page1));
        MatcherAssert.assertThat(keysMatchingProviderKey.get(3), Is.is(ActionTest.filter2Page2));
        MatcherAssert.assertThat(keysMatchingProviderKey.size(), Is.is(4));
    }

    @Test
    public void Check_Keys_Matching_Dynamic_Key() {
        List<String> keysMatchingDynamicKey1 = actionUT.getKeysOnMemoryMatchingDynamicKey(ActionTest.PROVIDER_KEY, ActionTest.DYNAMIC_KEY_1);
        MatcherAssert.assertThat(keysMatchingDynamicKey1.get(0), Is.is(ActionTest.filter1Page1));
        MatcherAssert.assertThat(keysMatchingDynamicKey1.get(1), Is.is(ActionTest.filter1Page2));
        MatcherAssert.assertThat(keysMatchingDynamicKey1.size(), Is.is(2));
        List<String> keysMatchingDynamicKey2 = actionUT.getKeysOnMemoryMatchingDynamicKey(ActionTest.PROVIDER_KEY, ActionTest.DYNAMIC_KEY_2);
        MatcherAssert.assertThat(keysMatchingDynamicKey2.get(0), Is.is(ActionTest.filter2Page1));
        MatcherAssert.assertThat(keysMatchingDynamicKey2.get(1), Is.is(ActionTest.filter2Page2));
        MatcherAssert.assertThat(keysMatchingDynamicKey2.size(), Is.is(2));
    }

    @Test
    public void Check_Keys_Matching_Dynamic_Key_Group() {
        String keyMatchingDynamicKey1DynamicKeyGroup1 = actionUT.getKeyOnMemoryMatchingDynamicKeyGroup(ActionTest.PROVIDER_KEY, ActionTest.DYNAMIC_KEY_1, ActionTest.DYNAMIC_KEY_GROUP_1);
        MatcherAssert.assertThat(keyMatchingDynamicKey1DynamicKeyGroup1, Is.is(ActionTest.filter1Page1));
        String keyMatchingDynamicKey1DynamicKeyGroup2 = actionUT.getKeyOnMemoryMatchingDynamicKeyGroup(ActionTest.PROVIDER_KEY, ActionTest.DYNAMIC_KEY_1, ActionTest.DYNAMIC_KEY_GROUP_2);
        MatcherAssert.assertThat(keyMatchingDynamicKey1DynamicKeyGroup2, Is.is(ActionTest.filter1Page2));
        String keyMatchingDynamicKey2DynamicKeyGroup1 = actionUT.getKeyOnMemoryMatchingDynamicKeyGroup(ActionTest.PROVIDER_KEY, ActionTest.DYNAMIC_KEY_2, ActionTest.DYNAMIC_KEY_GROUP_1);
        MatcherAssert.assertThat(keyMatchingDynamicKey2DynamicKeyGroup1, Is.is(ActionTest.filter2Page1));
        String keyMatchingDynamicKey2DynamicKeyGroup2 = actionUT.getKeyOnMemoryMatchingDynamicKeyGroup(ActionTest.PROVIDER_KEY, ActionTest.DYNAMIC_KEY_2, ActionTest.DYNAMIC_KEY_GROUP_2);
        MatcherAssert.assertThat(keyMatchingDynamicKey2DynamicKeyGroup2, Is.is(ActionTest.filter2Page2));
    }

    private static String filter1Page1;

    private static String filter1Page2;

    private static String filter2Page1;

    private static String filter2Page2;

    private static class MockMemory implements Memory {
        private final LinkedHashMap<String, Record> mockCache;

        public MockMemory() {
            mockCache = new LinkedHashMap();
            io.rx_cache2.internal.cache.Action action = new io.rx_cache2.internal.cache.Action(null, null) {};
            ActionTest.filter1Page1 = action.composeKey(ActionTest.PROVIDER_KEY, ActionTest.DYNAMIC_KEY_1, ActionTest.DYNAMIC_KEY_GROUP_1);
            mockCache.put(ActionTest.filter1Page1, mock(ActionTest.filter1Page1));
            ActionTest.filter1Page2 = action.composeKey(ActionTest.PROVIDER_KEY, ActionTest.DYNAMIC_KEY_1, ActionTest.DYNAMIC_KEY_GROUP_2);
            mockCache.put(ActionTest.filter1Page2, mock(ActionTest.filter1Page2));
            ActionTest.filter2Page1 = action.composeKey(ActionTest.PROVIDER_KEY, ActionTest.DYNAMIC_KEY_2, ActionTest.DYNAMIC_KEY_GROUP_1);
            mockCache.put(ActionTest.filter2Page1, mock(ActionTest.filter2Page1));
            ActionTest.filter2Page2 = action.composeKey(ActionTest.PROVIDER_KEY, ActionTest.DYNAMIC_KEY_2, ActionTest.DYNAMIC_KEY_GROUP_2);
            mockCache.put(ActionTest.filter2Page2, mock(ActionTest.filter2Page2));
        }

        @Override
        public <T> Record<T> getIfPresent(String key) {
            return null;
        }

        @Override
        public <T> void put(String key, Record<T> record) {
        }

        @Override
        public Set<String> keySet() {
            return mockCache.keySet();
        }

        @Override
        public void evict(String key) {
        }

        @Override
        public void evictAll() {
        }

        private Record<String> mock(String value) {
            return new Record(value, true, null);
        }
    }
}

