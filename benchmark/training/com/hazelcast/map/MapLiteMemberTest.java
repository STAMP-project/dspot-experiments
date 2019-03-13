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
package com.hazelcast.map;


import com.hazelcast.config.Config;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapLiteMemberTest extends HazelcastTestSupport {
    private Config liteConfig = new Config().setLiteMember(true);

    private TestHazelcastInstanceFactory factory;

    private IMap<Integer, Object> map;

    @Test
    public void testMapPutOnLiteMember() {
        Assert.assertNull(map.put(1, 2));
    }

    @Test
    public void testMapGetOnLiteMember() {
        map.put(1, 2);
        Assert.assertEquals(2, map.get(1));
    }

    @Test
    public void testMapSizeOnLiteMember() {
        map.put(1, 2);
        Assert.assertEquals(1, map.size());
    }

    @Test
    public void testMapLocalKeysOnLiteMember() {
        map.put(1, 1);
        final Set resultSet = map.localKeySet();
        Assert.assertNotNull(resultSet);
        Assert.assertEquals(0, resultSet.size());
    }

    @Test
    public void testMapEntryListenerOnLiteMember() {
        final MapLiteMemberTest.DummyEntryListener listener = new MapLiteMemberTest.DummyEntryListener();
        map.addEntryListener(listener, true);
        map.put(1, 2);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(1, listener.key);
                Assert.assertEquals(2, listener.value);
            }
        });
    }

    @Test
    public void testMapInterceptorOnLiteMember() {
        map.addInterceptor(new MapLiteMemberTest.DummyMapInterceptor());
        map.put(1, "new");
        Assert.assertEquals("intercepted", map.get(1));
    }

    @Test
    public void testMapEntryProcessorOnLiteMember() {
        map.put(1, 2);
        final Map resultMap = this.map.executeOnEntries(new MapLiteMemberTest.DummyEntryProcessor());
        Assert.assertEquals("dummy", map.get(1));
        Assert.assertEquals(1, resultMap.size());
        Assert.assertEquals("done", resultMap.get(1));
    }

    @Test
    public void testMapValuesQuery() {
        MapLiteMemberTest.testMapValuesQuery(map);
    }

    @Test
    public void testMapKeysQuery() {
        MapLiteMemberTest.testMapKeysQuery(map);
    }

    private static class DummyEntryListener implements EntryAddedListener<Object, Object> {
        private volatile Object key;

        private volatile Object value;

        @Override
        public void entryAdded(EntryEvent<Object, Object> event) {
            key = event.getKey();
            value = event.getValue();
        }
    }

    private static class DummyMapInterceptor implements MapInterceptor {
        @Override
        public Object interceptGet(Object value) {
            return null;
        }

        @Override
        public void afterGet(Object value) {
        }

        @Override
        public Object interceptPut(Object oldValue, Object newValue) {
            if (newValue.equals("new")) {
                return "intercepted";
            } else {
                throw new RuntimeException("no put");
            }
        }

        @Override
        public void afterPut(Object value) {
        }

        @Override
        public Object interceptRemove(Object removedValue) {
            return null;
        }

        @Override
        public void afterRemove(Object value) {
        }
    }

    private static class DummyEntryProcessor implements EntryBackupProcessor<Object, Object> , EntryProcessor<Object, Object> {
        @Override
        public Object process(Map.Entry<Object, Object> entry) {
            entry.setValue("dummy");
            return "done";
        }

        @Override
        public void processBackup(Map.Entry<Object, Object> entry) {
            process(entry);
        }

        @Override
        public EntryBackupProcessor<Object, Object> getBackupProcessor() {
            return this;
        }
    }
}

