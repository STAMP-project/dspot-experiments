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


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.StringUtil;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class EmbeddedMapInterceptorTest extends HazelcastTestSupport {
    private static final String[] CITIES = new String[]{ "NEW YORK", "ISTANBUL", "TOKYO", "LONDON", "PARIS", "CAIRO", "HONG KONG" };

    private final String mapName = "testMapInterceptor";

    private final Map<HazelcastInstance, EmbeddedMapInterceptorTest.SimpleInterceptor> interceptorMap = new HashMap<HazelcastInstance, EmbeddedMapInterceptorTest.SimpleInterceptor>();

    private TestHazelcastInstanceFactory factory;

    private HazelcastInstance hz1;

    private HazelcastInstance hz2;

    private IMap<Object, Object> map1;

    private IMap<Object, Object> map2;

    private EmbeddedMapInterceptorTest.SimpleInterceptor interceptor1;

    private EmbeddedMapInterceptorTest.SimpleInterceptor interceptor2;

    private String key;

    private String value;

    private String expectedValueAfterPut;

    private String expectedValueAfterGet;

    /**
     * Test for issue #3931 (https://github.com/hazelcast/hazelcast/issues/3931)
     */
    @Test
    public void testChainingOfSameInterceptor() {
        EmbeddedMapInterceptorTest.putAll(map1, EmbeddedMapInterceptorTest.CITIES);
        EmbeddedMapInterceptorTest.assertGet(map1, "-foo", EmbeddedMapInterceptorTest.CITIES);
        EmbeddedMapInterceptorTest.assertGet(map2, "-foo", EmbeddedMapInterceptorTest.CITIES);
    }

    /**
     * Test for issue #3932 (https://github.com/hazelcast/hazelcast/issues/3932)
     */
    @Test
    public void testStoppingNodeLeavesInterceptor() {
        EmbeddedMapInterceptorTest.putAll(map1, EmbeddedMapInterceptorTest.CITIES);
        // terminate one node
        hz2.shutdown();
        EmbeddedMapInterceptorTest.assertGet(map1, "-foo", EmbeddedMapInterceptorTest.CITIES);
        // adding the node back in
        hz2 = startNodeWithInterceptor(factory);
        map2 = hz2.getMap(mapName);
        EmbeddedMapInterceptorTest.assertGet(map1, "-foo", EmbeddedMapInterceptorTest.CITIES);
        EmbeddedMapInterceptorTest.assertGet(map2, "-foo", EmbeddedMapInterceptorTest.CITIES);
    }

    @Test
    public void testGetInterceptedValue() {
        // no value is set yet
        Assert.assertNull("Expected no value", map1.get(key));
        Assert.assertNull("Expected no value", interceptor1.getValue);
        Assert.assertNull("Expected no value after get", interceptor1.afterGetValue);
        EmbeddedMapInterceptorTest.assertNoInteractionWith(interceptor2);
        interceptor1.reset();
        interceptor2.reset();
        map1.put(key, value);
        interceptor1.reset();
        interceptor2.reset();
        // multiple get calls
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals("Expected the intercepted value", expectedValueAfterGet, map1.get(key));
            HazelcastTestSupport.assertTrueEventually(new AssertTask() {
                @Override
                public void run() {
                    Assert.assertEquals("Expected the uppercase value", expectedValueAfterPut, interceptor1.getValue);
                    Assert.assertEquals("Expected the intercepted value after get", expectedValueAfterGet, interceptor1.afterGetValue);
                    EmbeddedMapInterceptorTest.assertNoInteractionWith(interceptor2);
                }
            });
            interceptor1.reset();
            interceptor2.reset();
        }
    }

    @Test
    public void testPutInterceptedValuePropagatesToBackupCorrectly() {
        map1.put(key, value);
        testInterceptedValuePropagatesToBackupCorrectly();
    }

    @Test
    public void testPutIfAbsentInterceptedValuePropagatesToBackupCorrectly() {
        map1.putIfAbsent(key, value);
        testInterceptedValuePropagatesToBackupCorrectly();
    }

    @Test
    public void testPutTransientInterceptedValuePropagatesToBackupCorrectly() {
        map1.putTransient(key, value, 1, TimeUnit.MINUTES);
        testInterceptedValuePropagatesToBackupCorrectly();
    }

    @Test
    public void testTryPutInterceptedValuePropagatesToBackupCorrectly() {
        map1.tryPut(key, value, 5, TimeUnit.SECONDS);
        testInterceptedValuePropagatesToBackupCorrectly();
    }

    @Test
    public void testSetInterceptedValuePropagatesToBackupCorrectly() {
        map1.set(key, value);
        testInterceptedValuePropagatesToBackupCorrectly();
    }

    @Test
    public void testReplaceInterceptedValuePropagatesToBackupCorrectly() {
        final String oldValue = "oldValue";
        final String oldValueAfterPut = oldValue.toUpperCase(StringUtil.LOCALE_INTERNAL);
        final String oldValueAfterGet = oldValueAfterPut + "-foo";
        map1.put(key, oldValue);
        Assert.assertEquals("Expected the intercepted old value", oldValueAfterGet, map1.get(key));
        interceptor1.reset();
        interceptor2.reset();
        map1.replace(key, value);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals("Expected uppercase old value", oldValueAfterPut, interceptor1.putOldValue);
                Assert.assertEquals("Expected unmodified new value", value, interceptor1.putNewValue);
                Assert.assertEquals("Expected new uppercase value after put", expectedValueAfterPut, interceptor1.afterPutValue);
                EmbeddedMapInterceptorTest.assertNoInteractionWith(interceptor2);
            }
        });
        Assert.assertEquals("Expected the intercepted value", expectedValueAfterGet, map1.get(key));
        hz1.getLifecycleService().shutdown();
        Assert.assertEquals("Expected the intercepted value after backup promotion", expectedValueAfterGet, map2.get(key));
    }

    @Test
    public void testReplaceIfSameInterceptedValuePropagatesToBackupCorrectly() {
        final String oldValue = "oldValue";
        final String oldValueAfterPut = oldValue.toUpperCase(StringUtil.LOCALE_INTERNAL);
        final String oldValueAfterGet = oldValueAfterPut + "-foo";
        map1.put(key, oldValue);
        Assert.assertEquals("Expected the intercepted old value", oldValueAfterGet, map1.get(key));
        interceptor1.reset();
        interceptor2.reset();
        map1.replace(key, oldValueAfterPut, value);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals("Expected uppercase old value", oldValueAfterPut, interceptor1.putOldValue);
                Assert.assertEquals("Expected unmodified new value", value, interceptor1.putNewValue);
                Assert.assertEquals("Expected new uppercase value after put", expectedValueAfterPut, interceptor1.afterPutValue);
                EmbeddedMapInterceptorTest.assertNoInteractionWith(interceptor2);
            }
        });
        Assert.assertEquals("Expected the intercepted value", expectedValueAfterGet, map1.get(key));
        hz1.getLifecycleService().shutdown();
        Assert.assertEquals("Expected the intercepted value after backup promotion", expectedValueAfterGet, map2.get(key));
    }

    @Test
    public void testRemoveInterceptedValuePropagatesToBackupCorrectly() {
        map1.put(key, value);
        Assert.assertEquals("Expected the intercepted value", expectedValueAfterGet, map1.get(key));
        interceptor1.reset();
        interceptor2.reset();
        map1.remove(key);
        Assert.assertEquals("Expected the uppercase removed value", expectedValueAfterPut, interceptor1.removedValue);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals("Expected the uppercase value after remove", expectedValueAfterPut, interceptor1.afterRemoveValue);
            }
        });
        EmbeddedMapInterceptorTest.assertNoInteractionWith(interceptor2);
        Assert.assertNull("Expected the value to be removed", map1.get(key));
        hz1.getLifecycleService().shutdown();
        Assert.assertNull("Expected the value to be removed after backup promotion", map2.get(key));
    }

    static class SimpleInterceptor implements MapInterceptor , Serializable {
        volatile Object getValue;

        volatile Object afterGetValue;

        volatile Object putOldValue;

        volatile Object putNewValue;

        volatile Object afterPutValue;

        volatile Object removedValue;

        volatile Object afterRemoveValue;

        @Override
        public Object interceptGet(Object value) {
            getValue = value;
            if (value == null) {
                return null;
            }
            return value + "-foo";
        }

        @Override
        public void afterGet(Object value) {
            afterGetValue = value;
        }

        @Override
        public Object interceptPut(Object oldValue, Object newValue) {
            putOldValue = oldValue;
            putNewValue = newValue;
            return newValue.toString().toUpperCase(StringUtil.LOCALE_INTERNAL);
        }

        @Override
        public void afterPut(Object value) {
            afterPutValue = value;
        }

        @Override
        public Object interceptRemove(Object removedValue) {
            this.removedValue = removedValue;
            return removedValue;
        }

        @Override
        public void afterRemove(Object oldValue) {
            afterRemoveValue = oldValue;
        }

        @Override
        public int hashCode() {
            return 123456;
        }

        void reset() {
            getValue = null;
            afterGetValue = null;
            putOldValue = null;
            putNewValue = null;
            afterPutValue = null;
            removedValue = null;
            afterRemoveValue = null;
        }
    }
}

