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
package org.ehcache.clustered.client.internal.store;


import org.ehcache.clustered.ChainUtils;
import org.ehcache.clustered.Matchers;
import org.ehcache.clustered.common.internal.store.Chain;
import org.ehcache.clustered.common.internal.util.ChainBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class ChainBuilderTest {
    @Test
    public void testChainBuilder() {
        Chain chain = new ChainBuilder().add(ChainUtils.createPayload(1L)).add(ChainUtils.createPayload(3L)).add(ChainUtils.createPayload(4L)).add(ChainUtils.createPayload(2L)).build();
        Assert.assertThat(chain, Matchers.hasPayloads(1L, 3L, 4L, 2L));
    }
}

