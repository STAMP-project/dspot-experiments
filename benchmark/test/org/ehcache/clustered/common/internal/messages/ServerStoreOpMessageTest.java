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
package org.ehcache.clustered.common.internal.messages;


import org.ehcache.clustered.ChainUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.junit.Test;


public class ServerStoreOpMessageTest {
    @Test
    public void testConcurrencyKeysEqualForSameCacheAndKey() {
        ConcurrentEntityMessage m1 = new ServerStoreOpMessage.AppendMessage(1L, ChainUtils.createPayload(1L));
        ConcurrentEntityMessage m2 = new ServerStoreOpMessage.GetAndAppendMessage(1L, ChainUtils.createPayload(1L));
        ConcurrentEntityMessage m3 = new ServerStoreOpMessage.ReplaceAtHeadMessage(1L, ChainUtils.chainOf(), ChainUtils.chainOf());
        MatcherAssert.assertThat(m1.concurrencyKey(), Is.is(m2.concurrencyKey()));
        MatcherAssert.assertThat(m2.concurrencyKey(), Is.is(m3.concurrencyKey()));
    }

    @Test
    public void testConcurrencyKeysEqualForDifferentCachesSameKey() {
        ConcurrentEntityMessage m1 = new ServerStoreOpMessage.AppendMessage(1L, ChainUtils.createPayload(1L));
        ConcurrentEntityMessage m2 = new ServerStoreOpMessage.GetAndAppendMessage(1L, ChainUtils.createPayload(1L));
        MatcherAssert.assertThat(m1.concurrencyKey(), Is.is(m2.concurrencyKey()));
    }

    @Test
    public void testConcurrencyKeysNotEqualForDifferentCachesAndKeys() {
        ConcurrentEntityMessage m1 = new ServerStoreOpMessage.AppendMessage(1L, ChainUtils.createPayload(1L));
        ConcurrentEntityMessage m2 = new ServerStoreOpMessage.GetAndAppendMessage(2L, ChainUtils.createPayload(1L));
        ConcurrentEntityMessage m3 = new ServerStoreOpMessage.AppendMessage(3L, ChainUtils.createPayload(1L));
        MatcherAssert.assertThat(m1.concurrencyKey(), IsNot.not(m2.concurrencyKey()));
        MatcherAssert.assertThat(m1.concurrencyKey(), IsNot.not(m3.concurrencyKey()));
    }
}

