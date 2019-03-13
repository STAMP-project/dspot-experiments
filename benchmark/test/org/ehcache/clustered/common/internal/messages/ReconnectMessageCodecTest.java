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


import java.util.HashSet;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ReconnectMessageCodecTest {
    private ReconnectMessageCodec reconnectMessageCodec;

    @Test
    public void testClusterTierReconnectCodec() {
        ClusterTierReconnectMessage reconnectMessage = new ClusterTierReconnectMessage();
        Set<Long> setToInvalidate = new HashSet<>();
        setToInvalidate.add(1L);
        setToInvalidate.add(11L);
        setToInvalidate.add(111L);
        Set<Long> locks = new HashSet<>();
        locks.add(20L);
        locks.add(200L);
        locks.add(2000L);
        reconnectMessage.addInvalidationsInProgress(setToInvalidate);
        reconnectMessage.clearInProgress();
        reconnectMessage.addLocksHeld(locks);
        ClusterTierReconnectMessage decoded = reconnectMessageCodec.decode(reconnectMessageCodec.encode(reconnectMessage));
        Assert.assertThat(decoded, Matchers.notNullValue());
        Assert.assertThat(decoded.getInvalidationsInProgress(), Matchers.containsInAnyOrder(setToInvalidate.toArray()));
        Assert.assertThat(decoded.isClearInProgress(), Matchers.is(true));
        Assert.assertThat(decoded.getLocksHeld(), Matchers.containsInAnyOrder(locks.toArray()));
    }
}

