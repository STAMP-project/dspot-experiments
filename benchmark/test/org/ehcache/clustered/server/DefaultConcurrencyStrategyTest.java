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
package org.ehcache.clustered.server;


import ServerStoreOpMessage.GetMessage;
import java.util.HashSet;
import java.util.Set;
import org.ehcache.clustered.common.internal.messages.ConcurrentEntityMessage;
import org.ehcache.clustered.common.internal.messages.EhcacheEntityMessage;
import org.ehcache.clustered.common.internal.messages.ServerStoreOpMessage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.Mockito;
import org.terracotta.entity.ConcurrencyStrategy;


/**
 *
 *
 * @author Ludovic Orban
 */
public class DefaultConcurrencyStrategyTest {
    private static final KeySegmentMapper DEFAULT_MAPPER = new KeySegmentMapper(16);

    @Test
    public void testConcurrencyKey() throws Exception {
        final int concurrency = 107;
        ConcurrencyStrategy<EhcacheEntityMessage> strategy = ConcurrencyStrategies.clusterTierConcurrency(DefaultConcurrencyStrategyTest.DEFAULT_MAPPER);
        MatcherAssert.assertThat(strategy.concurrencyKey(new DefaultConcurrencyStrategyTest.NonConcurrentTestEntityMessage()), Is.is(ConcurrencyStrategies.DEFAULT_KEY));
        for (int i = -1024; i < 1024; i++) {
            MatcherAssert.assertThat(strategy.concurrencyKey(new DefaultConcurrencyStrategyTest.ConcurrentTestEntityMessage(i)), DefaultConcurrencyStrategyTest.withinRange(ConcurrencyStrategies.DEFAULT_KEY, concurrency));
        }
    }

    @Test
    public void testConcurrencyKeyForServerStoreGetOperation() throws Exception {
        ConcurrencyStrategy<EhcacheEntityMessage> strategy = ConcurrencyStrategies.clusterTierConcurrency(DefaultConcurrencyStrategyTest.DEFAULT_MAPPER);
        ServerStoreOpMessage.GetMessage getMessage = Mockito.mock(GetMessage.class);
        MatcherAssert.assertThat(strategy.concurrencyKey(getMessage), Is.is(UNIVERSAL_KEY));
    }

    @Test
    public void testKeysForSynchronization() throws Exception {
        final int concurrency = 111;
        ConcurrencyStrategy<EhcacheEntityMessage> strategy = ConcurrencyStrategies.clusterTierConcurrency(DefaultConcurrencyStrategyTest.DEFAULT_MAPPER);
        Set<Integer> visitedConcurrencyKeys = new HashSet<>();
        for (int i = -1024; i < 1024; i++) {
            int concurrencyKey = strategy.concurrencyKey(new DefaultConcurrencyStrategyTest.ConcurrentTestEntityMessage(i));
            MatcherAssert.assertThat(concurrencyKey, DefaultConcurrencyStrategyTest.withinRange(ConcurrencyStrategies.DEFAULT_KEY, concurrency));
            visitedConcurrencyKeys.add(concurrencyKey);
        }
        Set<Integer> keysForSynchronization = strategy.getKeysForSynchronization();
        MatcherAssert.assertThat(keysForSynchronization.contains(ConcurrencyStrategies.DEFAULT_KEY), Is.is(true));
        MatcherAssert.assertThat(keysForSynchronization.containsAll(visitedConcurrencyKeys), Is.is(true));
    }

    private static class NonConcurrentTestEntityMessage extends EhcacheEntityMessage {}

    private static class ConcurrentTestEntityMessage extends EhcacheEntityMessage implements ConcurrentEntityMessage {
        private final int key;

        public ConcurrentTestEntityMessage(int key) {
            this.key = key;
        }

        @Override
        public long concurrencyKey() {
            return key;
        }
    }
}

