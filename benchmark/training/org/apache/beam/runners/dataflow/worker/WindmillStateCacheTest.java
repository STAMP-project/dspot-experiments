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
package org.apache.beam.runners.dataflow.worker;


import WindmillStateCache.ForKey;
import java.io.IOException;
import java.util.Objects;
import org.apache.beam.runners.core.StateNamespaces;
import org.apache.beam.runners.core.StateTag;
import org.apache.beam.sdk.state.State;
import org.apache.beam.sdk.state.StateSpec;
import org.apache.beam.vendor.grpc.v1p13p1.com.google.protobuf.ByteString;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link WindmillStateCache}.
 */
@RunWith(JUnit4.class)
public class WindmillStateCacheTest {
    private static final String COMPUTATION = "computation";

    private static final ByteString KEY = ByteString.copyFromUtf8("key");

    private static final String STATE_FAMILY = "family";

    private static class TestStateTag implements StateTag<WindmillStateCacheTest.TestState> {
        final String id;

        TestStateTag(String id) {
            this.id = id;
        }

        @Override
        public void appendTo(Appendable appendable) throws IOException {
            appendable.append(id);
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public WindmillStateCacheTest.TestState bind(StateBinder binder) {
            throw new UnsupportedOperationException();
        }

        @Override
        public StateSpec<WindmillStateCacheTest.TestState> getSpec() {
            return null;
        }

        @Override
        public String toString() {
            return ("Tag(" + (id)) + ")";
        }

        @Override
        public boolean equals(Object other) {
            return (other instanceof WindmillStateCacheTest.TestStateTag) && (Objects.equals(((WindmillStateCacheTest.TestStateTag) (other)).id, id));
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    private static class TestState implements State {
        String value = null;

        TestState(String value) {
            this.value = value;
        }

        @Override
        public void clear() {
            this.value = null;
        }

        @Override
        public boolean equals(Object other) {
            return (other instanceof WindmillStateCacheTest.TestState) && (Objects.equals(((WindmillStateCacheTest.TestState) (other)).value, value));
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return ("State(" + (value)) + ")";
        }
    }

    WindmillStateCache cache;

    ForKey keyCache;

    @Test
    public void testBasic() throws Exception {
        Assert.assertNull(keyCache.get(StateNamespaces.global(), new WindmillStateCacheTest.TestStateTag("tag1")));
        Assert.assertNull(keyCache.get(WindmillStateCacheTest.windowNamespace(0), new WindmillStateCacheTest.TestStateTag("tag2")));
        Assert.assertNull(keyCache.get(WindmillStateCacheTest.triggerNamespace(0, 0), new WindmillStateCacheTest.TestStateTag("tag3")));
        Assert.assertNull(keyCache.get(WindmillStateCacheTest.triggerNamespace(0, 0), new WindmillStateCacheTest.TestStateTag("tag2")));
        keyCache.put(StateNamespaces.global(), new WindmillStateCacheTest.TestStateTag("tag1"), new WindmillStateCacheTest.TestState("g1"), 2);
        Assert.assertEquals(121, cache.getWeight());
        keyCache.put(WindmillStateCacheTest.windowNamespace(0), new WindmillStateCacheTest.TestStateTag("tag2"), new WindmillStateCacheTest.TestState("w2"), 2);
        Assert.assertEquals(242, cache.getWeight());
        keyCache.put(WindmillStateCacheTest.triggerNamespace(0, 0), new WindmillStateCacheTest.TestStateTag("tag3"), new WindmillStateCacheTest.TestState("t3"), 2);
        Assert.assertEquals(260, cache.getWeight());
        keyCache.put(WindmillStateCacheTest.triggerNamespace(0, 0), new WindmillStateCacheTest.TestStateTag("tag2"), new WindmillStateCacheTest.TestState("t2"), 2);
        Assert.assertEquals(278, cache.getWeight());
        Assert.assertEquals(new WindmillStateCacheTest.TestState("g1"), keyCache.get(StateNamespaces.global(), new WindmillStateCacheTest.TestStateTag("tag1")));
        Assert.assertEquals(new WindmillStateCacheTest.TestState("w2"), keyCache.get(WindmillStateCacheTest.windowNamespace(0), new WindmillStateCacheTest.TestStateTag("tag2")));
        Assert.assertEquals(new WindmillStateCacheTest.TestState("t3"), keyCache.get(WindmillStateCacheTest.triggerNamespace(0, 0), new WindmillStateCacheTest.TestStateTag("tag3")));
        Assert.assertEquals(new WindmillStateCacheTest.TestState("t2"), keyCache.get(WindmillStateCacheTest.triggerNamespace(0, 0), new WindmillStateCacheTest.TestStateTag("tag2")));
    }

    /**
     * Verifies that values are cached in the appropriate namespaces.
     */
    @Test
    public void testInvalidation() throws Exception {
        Assert.assertNull(keyCache.get(StateNamespaces.global(), new WindmillStateCacheTest.TestStateTag("tag1")));
        keyCache.put(StateNamespaces.global(), new WindmillStateCacheTest.TestStateTag("tag1"), new WindmillStateCacheTest.TestState("g1"), 2);
        Assert.assertEquals(121, cache.getWeight());
        Assert.assertEquals(new WindmillStateCacheTest.TestState("g1"), keyCache.get(StateNamespaces.global(), new WindmillStateCacheTest.TestStateTag("tag1")));
        keyCache = cache.forComputation(WindmillStateCacheTest.COMPUTATION).forKey(WindmillStateCacheTest.KEY, WindmillStateCacheTest.STATE_FAMILY, 1L);
        Assert.assertEquals(121, cache.getWeight());
        Assert.assertNull(keyCache.get(StateNamespaces.global(), new WindmillStateCacheTest.TestStateTag("tag1")));
        Assert.assertEquals(0, cache.getWeight());
    }

    /**
     * Verifies that the cache is invalidated when the cache token changes.
     */
    @Test
    public void testEviction() throws Exception {
        keyCache.put(WindmillStateCacheTest.windowNamespace(0), new WindmillStateCacheTest.TestStateTag("tag2"), new WindmillStateCacheTest.TestState("w2"), 2);
        Assert.assertEquals(121, cache.getWeight());
        keyCache.put(WindmillStateCacheTest.triggerNamespace(0, 0), new WindmillStateCacheTest.TestStateTag("tag3"), new WindmillStateCacheTest.TestState("t3"), 2000000000);
        Assert.assertEquals(0, cache.getWeight());
        // Eviction is atomic across the whole window.
        Assert.assertNull(keyCache.get(WindmillStateCacheTest.windowNamespace(0), new WindmillStateCacheTest.TestStateTag("tag2")));
        Assert.assertNull(keyCache.get(WindmillStateCacheTest.triggerNamespace(0, 0), new WindmillStateCacheTest.TestStateTag("tag3")));
    }

    /**
     * Verifies that caches are kept independently per-key.
     */
    @Test
    public void testMultipleKeys() throws Exception {
        WindmillStateCache.ForKey keyCache1 = cache.forComputation("comp1").forKey(ByteString.copyFromUtf8("key1"), WindmillStateCacheTest.STATE_FAMILY, 0L);
        WindmillStateCache.ForKey keyCache2 = cache.forComputation("comp1").forKey(ByteString.copyFromUtf8("key2"), WindmillStateCacheTest.STATE_FAMILY, 0L);
        WindmillStateCache.ForKey keyCache3 = cache.forComputation("comp2").forKey(ByteString.copyFromUtf8("key1"), WindmillStateCacheTest.STATE_FAMILY, 0L);
        keyCache1.put(StateNamespaces.global(), new WindmillStateCacheTest.TestStateTag("tag1"), new WindmillStateCacheTest.TestState("g1"), 2);
        Assert.assertEquals(new WindmillStateCacheTest.TestState("g1"), keyCache1.get(StateNamespaces.global(), new WindmillStateCacheTest.TestStateTag("tag1")));
        Assert.assertNull(keyCache2.get(StateNamespaces.global(), new WindmillStateCacheTest.TestStateTag("tag1")));
        Assert.assertNull(keyCache3.get(StateNamespaces.global(), new WindmillStateCacheTest.TestStateTag("tag1")));
    }

    /**
     * Verifies explicit invalidation does indeed invalidate the correct entries.
     */
    @Test
    public void testExplicitInvalidation() throws Exception {
        WindmillStateCache.ForKey keyCache1 = cache.forComputation("comp1").forKey(ByteString.copyFromUtf8("key1"), WindmillStateCacheTest.STATE_FAMILY, 0L);
        WindmillStateCache.ForKey keyCache2 = cache.forComputation("comp1").forKey(ByteString.copyFromUtf8("key2"), WindmillStateCacheTest.STATE_FAMILY, 0L);
        WindmillStateCache.ForKey keyCache3 = cache.forComputation("comp2").forKey(ByteString.copyFromUtf8("key1"), WindmillStateCacheTest.STATE_FAMILY, 0L);
        keyCache1.put(StateNamespaces.global(), new WindmillStateCacheTest.TestStateTag("tag1"), new WindmillStateCacheTest.TestState("g1"), 1);
        keyCache2.put(StateNamespaces.global(), new WindmillStateCacheTest.TestStateTag("tag2"), new WindmillStateCacheTest.TestState("g2"), 2);
        keyCache3.put(StateNamespaces.global(), new WindmillStateCacheTest.TestStateTag("tag3"), new WindmillStateCacheTest.TestState("g3"), 3);
        Assert.assertEquals(new WindmillStateCacheTest.TestState("g1"), keyCache1.get(StateNamespaces.global(), new WindmillStateCacheTest.TestStateTag("tag1")));
        Assert.assertEquals(new WindmillStateCacheTest.TestState("g2"), keyCache2.get(StateNamespaces.global(), new WindmillStateCacheTest.TestStateTag("tag2")));
        Assert.assertEquals(new WindmillStateCacheTest.TestState("g3"), keyCache3.get(StateNamespaces.global(), new WindmillStateCacheTest.TestStateTag("tag3")));
        cache.forComputation("comp1").invalidate(ByteString.copyFromUtf8("key1"));
        Assert.assertNull(keyCache1.get(StateNamespaces.global(), new WindmillStateCacheTest.TestStateTag("tag1")));
        Assert.assertEquals(new WindmillStateCacheTest.TestState("g2"), keyCache2.get(StateNamespaces.global(), new WindmillStateCacheTest.TestStateTag("tag2")));
        Assert.assertEquals(new WindmillStateCacheTest.TestState("g3"), keyCache3.get(StateNamespaces.global(), new WindmillStateCacheTest.TestStateTag("tag3")));
    }
}

