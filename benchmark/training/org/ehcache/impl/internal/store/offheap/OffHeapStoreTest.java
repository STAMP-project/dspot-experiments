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


import java.util.Collections;
import org.ehcache.config.ResourceType;
import org.ehcache.impl.internal.util.UnmatchedResourceType;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static org.ehcache.config.ResourceType.Core.DISK;
import static org.ehcache.config.ResourceType.Core.HEAP;
import static org.ehcache.config.ResourceType.Core.OFFHEAP;


public class OffHeapStoreTest extends AbstractOffHeapStoreTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testRankAuthority() throws Exception {
        OffHeapStore.Provider provider = new OffHeapStore.Provider();
        Assert.assertThat(provider.rankAuthority(OFFHEAP, Collections.EMPTY_LIST), Matchers.is(1));
        Assert.assertThat(provider.rankAuthority(new UnmatchedResourceType(), Collections.EMPTY_LIST), Matchers.is(0));
    }

    @Test
    public void testRank() throws Exception {
        OffHeapStore.Provider provider = new OffHeapStore.Provider();
        assertRank(provider, 1, OFFHEAP);
        assertRank(provider, 0, DISK);
        assertRank(provider, 0, HEAP);
        assertRank(provider, 0, DISK, OFFHEAP);
        assertRank(provider, 0, DISK, HEAP);
        assertRank(provider, 0, OFFHEAP, HEAP);
        assertRank(provider, 0, DISK, OFFHEAP, HEAP);
        assertRank(provider, 0, new UnmatchedResourceType());
        assertRank(provider, 0, OFFHEAP, new UnmatchedResourceType());
    }
}

