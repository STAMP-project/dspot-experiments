/**
 * Copyright Terracotta, Inc.
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
package org.ehcache.impl.internal.store.offheap;


import java.util.function.BiFunction;
import java.util.function.Function;
import org.ehcache.config.EvictionAdvisor;
import org.ehcache.impl.internal.store.offheap.factories.EhcacheSegmentFactory;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static org.ehcache.impl.internal.store.offheap.factories.EhcacheSegmentFactory.EhcacheSegment.ADVISED_AGAINST_EVICTION;


/**
 * AbstractEhcacheOffHeapBackingMapTest
 */
public abstract class AbstractEhcacheOffHeapBackingMapTest {
    @Test
    public void testComputeFunctionCalledWhenNoMapping() throws Exception {
        EhcacheOffHeapBackingMap<String, String> segment = createTestSegment();
        try {
            String value = segment.compute("key", ( s, s2) -> "value", false);
            Assert.assertThat(value, CoreMatchers.is("value"));
            Assert.assertThat(segment.get("key"), CoreMatchers.is(value));
        } finally {
            destroySegment(segment);
        }
    }

    @Test
    public void testComputeFunctionReturnsSameNoPin() throws Exception {
        EhcacheOffHeapBackingMap<String, String> segment = createTestSegment();
        try {
            segment.put("key", "value");
            String value = segment.compute("key", ( s, s2) -> s2, false);
            Assert.assertThat(value, CoreMatchers.is("value"));
            Assert.assertThat(isPinned("key", segment), CoreMatchers.is(false));
        } finally {
            destroySegment(segment);
        }
    }

    @Test
    public void testComputeFunctionReturnsSamePins() throws Exception {
        EhcacheOffHeapBackingMap<String, String> segment = createTestSegment();
        try {
            segment.put("key", "value");
            String value = segment.compute("key", ( s, s2) -> s2, true);
            Assert.assertThat(value, CoreMatchers.is("value"));
            Assert.assertThat(isPinned("key", segment), CoreMatchers.is(true));
        } finally {
            destroySegment(segment);
        }
    }

    @Test
    public void testComputeFunctionReturnsSamePreservesPinWhenNoPin() throws Exception {
        EhcacheOffHeapBackingMap<String, String> segment = createTestSegment();
        try {
            putPinned("key", "value", segment);
            String value = segment.compute("key", ( s, s2) -> s2, false);
            Assert.assertThat(value, CoreMatchers.is("value"));
            Assert.assertThat(isPinned("key", segment), CoreMatchers.is(true));
        } finally {
            destroySegment(segment);
        }
    }

    @Test
    public void testComputeFunctionReturnsDifferentNoPin() throws Exception {
        EhcacheOffHeapBackingMap<String, String> segment = createTestSegment();
        try {
            segment.put("key", "value");
            String value = segment.compute("key", ( s, s2) -> "otherValue", false);
            Assert.assertThat(value, CoreMatchers.is("otherValue"));
            Assert.assertThat(isPinned("key", segment), CoreMatchers.is(false));
        } finally {
            destroySegment(segment);
        }
    }

    @Test
    public void testComputeFunctionReturnsDifferentPins() throws Exception {
        EhcacheOffHeapBackingMap<String, String> segment = createTestSegment();
        try {
            segment.put("key", "value");
            String value = segment.compute("key", ( s, s2) -> "otherValue", true);
            Assert.assertThat(value, CoreMatchers.is("otherValue"));
            Assert.assertThat(isPinned("key", segment), CoreMatchers.is(true));
        } finally {
            destroySegment(segment);
        }
    }

    @Test
    public void testComputeFunctionReturnsDifferentClearsPin() throws Exception {
        EhcacheOffHeapBackingMap<String, String> segment = createTestSegment();
        try {
            putPinned("key", "value", segment);
            String value = segment.compute("key", ( s, s2) -> "otherValue", false);
            Assert.assertThat(value, CoreMatchers.is("otherValue"));
            Assert.assertThat(isPinned("key", segment), CoreMatchers.is(false));
        } finally {
            destroySegment(segment);
        }
    }

    @Test
    public void testComputeFunctionReturnsNullRemoves() throws Exception {
        EhcacheOffHeapBackingMap<String, String> segment = createTestSegment();
        try {
            putPinned("key", "value", segment);
            String value = segment.compute("key", ( s, s2) -> null, false);
            Assert.assertThat(value, CoreMatchers.nullValue());
            Assert.assertThat(segment.containsKey("key"), CoreMatchers.is(false));
        } finally {
            destroySegment(segment);
        }
    }

    @Test
    public void testComputeIfPresentNotCalledOnNotContainedKey() throws Exception {
        EhcacheOffHeapBackingMap<String, String> segment = createTestSegment();
        try {
            try {
                segment.computeIfPresent("key", ( s, s2) -> {
                    throw new UnsupportedOperationException("Should not have been called!");
                });
            } catch (UnsupportedOperationException e) {
                Assert.fail("Mapping function should not have been called.");
            }
        } finally {
            destroySegment(segment);
        }
    }

    @Test
    public void testComputeIfPresentReturnsSameValue() throws Exception {
        EhcacheOffHeapBackingMap<String, String> segment = createTestSegment();
        try {
            segment.put("key", "value");
            String value = segment.computeIfPresent("key", ( s, s2) -> s2);
            Assert.assertThat(segment.get("key"), CoreMatchers.is(value));
        } finally {
            destroySegment(segment);
        }
    }

    @Test
    public void testComputeIfPresentReturnsDifferentValue() throws Exception {
        EhcacheOffHeapBackingMap<String, String> segment = createTestSegment();
        try {
            segment.put("key", "value");
            String value = segment.computeIfPresent("key", ( s, s2) -> "newValue");
            Assert.assertThat(segment.get("key"), CoreMatchers.is(value));
        } finally {
            destroySegment(segment);
        }
    }

    @Test
    public void testComputeIfPresentReturnsNullRemovesMapping() throws Exception {
        EhcacheOffHeapBackingMap<String, String> segment = createTestSegment();
        try {
            segment.put("key", "value");
            String value = segment.computeIfPresent("key", ( s, s2) -> null);
            Assert.assertThat(segment.containsKey("key"), CoreMatchers.is(false));
        } finally {
            destroySegment(segment);
        }
    }

    @Test
    public void testComputeIfPinnedNoOpUnpinned() throws Exception {
        EhcacheOffHeapBackingMap<String, String> segment = createTestSegment();
        try {
            segment.put("key", "value");
            boolean result = segment.computeIfPinned("key", ( s, s2) -> {
                Assert.fail("Method should not be invoked");
                return null;
            }, ( s) -> {
                Assert.fail("Method should not be invoked");
                return false;
            });
            Assert.assertThat(isPinned("key", segment), CoreMatchers.is(false));
            Assert.assertThat(result, CoreMatchers.is(false));
        } finally {
            destroySegment(segment);
        }
    }

    @Test
    public void testComputeIfPinnedClearsMappingOnNullReturnWithPinningFalse() throws Exception {
        EhcacheOffHeapBackingMap<String, String> segment = createTestSegment();
        final String value = "value";
        try {
            putPinned("key", value, segment);
            boolean result = segment.computeIfPinned("key", ( s, s2) -> {
                Assert.assertThat(s2, CoreMatchers.is(value));
                return null;
            }, ( s) -> {
                Assert.assertThat(s, CoreMatchers.is(value));
                return false;
            });
            Assert.assertThat(segment.containsKey("key"), CoreMatchers.is(false));
            Assert.assertThat(result, CoreMatchers.is(true));
        } finally {
            destroySegment(segment);
        }
    }

    @Test
    public void testComputeIfPinnedClearsMappingOnNullReturnWithPinningTrue() throws Exception {
        EhcacheOffHeapBackingMap<String, String> segment = createTestSegment();
        final String value = "value";
        try {
            putPinned("key", value, segment);
            boolean result = segment.computeIfPinned("key", ( s, s2) -> {
                Assert.assertThat(s2, CoreMatchers.is(value));
                return null;
            }, ( s) -> {
                Assert.assertThat(s, CoreMatchers.is(value));
                return true;
            });
            Assert.assertThat(segment.containsKey("key"), CoreMatchers.is(false));
            Assert.assertThat(result, CoreMatchers.is(true));
        } finally {
            destroySegment(segment);
        }
    }

    @Test
    public void testComputeIfPinnedClearsPinWithoutChangingValue() throws Exception {
        EhcacheOffHeapBackingMap<String, String> segment = createTestSegment();
        final String value = "value";
        try {
            putPinned("key", value, segment);
            boolean result = segment.computeIfPinned("key", ( s, s2) -> {
                Assert.assertThat(s2, CoreMatchers.is(value));
                return s2;
            }, ( s) -> {
                Assert.assertThat(s, CoreMatchers.is(value));
                return true;
            });
            Assert.assertThat(isPinned("key", segment), CoreMatchers.is(false));
            Assert.assertThat(result, CoreMatchers.is(true));
        } finally {
            destroySegment(segment);
        }
    }

    @Test
    public void testComputeIfPinnedPreservesPinWithoutChangingValue() throws Exception {
        EhcacheOffHeapBackingMap<String, String> segment = createTestSegment();
        final String value = "value";
        try {
            putPinned("key", value, segment);
            boolean result = segment.computeIfPinned("key", ( s, s2) -> {
                Assert.assertThat(s2, CoreMatchers.is(value));
                return s2;
            }, ( s) -> {
                Assert.assertThat(s, CoreMatchers.is(value));
                return false;
            });
            Assert.assertThat(isPinned("key", segment), CoreMatchers.is(true));
            Assert.assertThat(result, CoreMatchers.is(false));
        } finally {
            destroySegment(segment);
        }
    }

    @Test
    public void testComputeIfPinnedReplacesValueUnpinnedWhenUnpinFunctionFalse() throws Exception {
        EhcacheOffHeapBackingMap<String, String> segment = createTestSegment();
        final String value = "value";
        final String newValue = "newValue";
        try {
            putPinned("key", value, segment);
            boolean result = segment.computeIfPinned("key", ( s, s2) -> {
                Assert.assertThat(s2, CoreMatchers.is(value));
                return newValue;
            }, ( s) -> {
                Assert.assertThat(s, CoreMatchers.is(value));
                return false;
            });
            Assert.assertThat(segment.get("key"), CoreMatchers.is(newValue));
            Assert.assertThat(isPinned("key", segment), CoreMatchers.is(false));
            Assert.assertThat(result, CoreMatchers.is(false));
        } finally {
            destroySegment(segment);
        }
    }

    @Test
    public void testComputeIfPinnedReplacesValueUnpinnedWhenUnpinFunctionTrue() throws Exception {
        EhcacheOffHeapBackingMap<String, String> segment = createTestSegment();
        final String value = "value";
        final String newValue = "newValue";
        try {
            putPinned("key", value, segment);
            boolean result = segment.computeIfPinned("key", ( s, s2) -> {
                Assert.assertThat(s2, CoreMatchers.is(value));
                return newValue;
            }, ( s) -> {
                Assert.assertThat(s, CoreMatchers.is(value));
                return true;
            });
            Assert.assertThat(segment.get("key"), CoreMatchers.is(newValue));
            Assert.assertThat(isPinned("key", segment), CoreMatchers.is(false));
            Assert.assertThat(result, CoreMatchers.is(false));
        } finally {
            destroySegment(segment);
        }
    }

    @Test
    public void testComputeIfPresentAndPinNoOpNoMapping() throws Exception {
        EhcacheOffHeapBackingMap<String, String> segment = createTestSegment();
        segment.computeIfPresentAndPin("key", ( s, s2) -> {
            Assert.fail("Function should not be invoked");
            return null;
        });
    }

    @Test
    public void testComputeIfPresentAndPinDoesPin() throws Exception {
        EhcacheOffHeapBackingMap<String, String> segment = createTestSegment();
        final String value = "value";
        segment.put("key", value);
        segment.computeIfPresentAndPin("key", ( s, s2) -> {
            Assert.assertThat(s2, CoreMatchers.is(value));
            return value;
        });
        Assert.assertThat(isPinned("key", segment), CoreMatchers.is(true));
    }

    @Test
    public void testComputeIfPresentAndPinPreservesPin() throws Exception {
        EhcacheOffHeapBackingMap<String, String> segment = createTestSegment();
        final String value = "value";
        putPinned("key", value, segment);
        segment.computeIfPresentAndPin("key", ( s, s2) -> {
            Assert.assertThat(s2, CoreMatchers.is(value));
            return value;
        });
        Assert.assertThat(isPinned("key", segment), CoreMatchers.is(true));
    }

    @Test
    public void testComputeIfPresentAndPinReplacesAndPins() throws Exception {
        EhcacheOffHeapBackingMap<String, String> segment = createTestSegment();
        final String value = "value";
        final String newValue = "newValue";
        segment.put("key", value);
        segment.computeIfPresentAndPin("key", ( s, s2) -> {
            Assert.assertThat(s2, CoreMatchers.is(value));
            return newValue;
        });
        Assert.assertThat(isPinned("key", segment), CoreMatchers.is(true));
        Assert.assertThat(segment.get("key"), CoreMatchers.is(newValue));
    }

    @Test
    public void testPutAdvicedAgainstEvictionComputesMetadata() throws Exception {
        EhcacheOffHeapBackingMap<String, String> segment = createTestSegment(( key, value) -> "please-do-not-evict-me".equals(key));
        try {
            segment.put("please-do-not-evict-me", "value");
            Assert.assertThat(getMetadata("please-do-not-evict-me", ADVISED_AGAINST_EVICTION, segment), CoreMatchers.is(ADVISED_AGAINST_EVICTION));
        } finally {
            destroySegment(segment);
        }
    }

    @Test
    public void testPutPinnedAdvicedAgainstEvictionComputesMetadata() throws Exception {
        EhcacheOffHeapBackingMap<String, String> segment = createTestSegment(( key, value) -> "please-do-not-evict-me".equals(key));
        try {
            putPinned("please-do-not-evict-me", "value", segment);
            Assert.assertThat(getMetadata("please-do-not-evict-me", ADVISED_AGAINST_EVICTION, segment), CoreMatchers.is(ADVISED_AGAINST_EVICTION));
        } finally {
            destroySegment(segment);
        }
    }
}

