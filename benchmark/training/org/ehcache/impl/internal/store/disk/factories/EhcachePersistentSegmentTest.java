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
package org.ehcache.impl.internal.store.disk.factories;


import java.io.IOException;
import org.ehcache.config.EvictionAdvisor;
import org.ehcache.impl.internal.store.offheap.factories.EhcacheSegmentFactory;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import static org.ehcache.impl.internal.store.offheap.factories.EhcacheSegmentFactory.EhcacheSegment.ADVISED_AGAINST_EVICTION;


public class EhcachePersistentSegmentTest {
    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testPutAdvisedAgainstEvictionComputesMetadata() throws IOException {
        EhcachePersistentSegmentFactory.EhcachePersistentSegment<String, String> segment = createTestSegmentWithAdvisor(( key, value) -> {
            return "please-do-not-evict-me".equals(key);
        });
        try {
            segment.put("please-do-not-evict-me", "value");
            Assert.assertThat(segment.getMetadata("please-do-not-evict-me", ADVISED_AGAINST_EVICTION), CoreMatchers.is(ADVISED_AGAINST_EVICTION));
        } finally {
            segment.destroy();
        }
    }

    @Test
    public void testPutPinnedAdvisedAgainstEvictionComputesMetadata() throws IOException {
        EhcachePersistentSegmentFactory.EhcachePersistentSegment<String, String> segment = createTestSegmentWithAdvisor(( key, value) -> {
            return "please-do-not-evict-me".equals(key);
        });
        try {
            segment.putPinned("please-do-not-evict-me", "value");
            Assert.assertThat(segment.getMetadata("please-do-not-evict-me", ADVISED_AGAINST_EVICTION), CoreMatchers.is(ADVISED_AGAINST_EVICTION));
        } finally {
            segment.destroy();
        }
    }

    @Test
    public void testAdviceAgainstEvictionPreventsEviction() throws IOException {
        EhcachePersistentSegmentFactory.EhcachePersistentSegment<String, String> segment = createTestSegmentWithAdvisorAndListener();
        try {
            Assert.assertThat(segment.evictable(1), CoreMatchers.is(true));
            Assert.assertThat(segment.evictable(((ADVISED_AGAINST_EVICTION) | 1)), CoreMatchers.is(false));
        } finally {
            segment.destroy();
        }
    }

    @Test
    public void testEvictionFiresEvent() throws IOException {
        @SuppressWarnings("unchecked")
        EhcacheSegmentFactory.EhcacheSegment.EvictionListener<String, String> evictionListener = Mockito.mock(EhcacheSegmentFactory.EhcacheSegment.EvictionListener.class);
        EhcachePersistentSegmentFactory.EhcachePersistentSegment<String, String> segment = createTestSegmentWithListener(evictionListener);
        try {
            segment.put("key", "value");
            segment.evict(segment.getEvictionIndex(), false);
            Mockito.verify(evictionListener).onEviction("key", "value");
        } finally {
            segment.destroy();
        }
    }
}

