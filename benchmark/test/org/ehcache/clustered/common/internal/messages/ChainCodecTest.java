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


import java.util.Iterator;
import org.ehcache.clustered.ChainUtils;
import org.ehcache.clustered.common.internal.store.Chain;
import org.ehcache.clustered.common.internal.store.Element;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static org.ehcache.clustered.Matchers.hasPayloads;
import static org.ehcache.clustered.Matchers.sameSequenceAs;


public class ChainCodecTest {
    @Test
    public void testChainWithSingleElement() {
        Chain chain = ChainUtils.chainOf(ChainUtils.createPayload(1L));
        Assert.assertThat(chain.isEmpty(), Matchers.is(false));
        Iterator<Element> chainIterator = chain.iterator();
        Assert.assertThat(ChainUtils.readPayload(chainIterator.next().getPayload()), Matchers.is(1L));
        Assert.assertThat(chainIterator.hasNext(), Matchers.is(false));
        Chain decoded = ChainCodec.decode(ChainCodec.encode(chain));
        Assert.assertThat(decoded.isEmpty(), Matchers.is(false));
        chainIterator = decoded.iterator();
        Assert.assertThat(ChainUtils.readPayload(chainIterator.next().getPayload()), Matchers.is(1L));
        Assert.assertThat(chainIterator.hasNext(), Matchers.is(false));
    }

    @Test
    public void testChainWithSingleSequencedElement() {
        Chain chain = ChainUtils.sequencedChainOf(ChainUtils.createPayload(1L));
        Assert.assertThat(chain.isEmpty(), Matchers.is(false));
        Iterator<Element> chainIterator = chain.iterator();
        Assert.assertThat(ChainUtils.readPayload(chainIterator.next().getPayload()), Matchers.is(1L));
        Assert.assertThat(chainIterator.hasNext(), Matchers.is(false));
        Chain decoded = ChainCodec.decode(ChainCodec.encode(chain));
        Assert.assertThat(decoded.isEmpty(), Matchers.is(false));
        chainIterator = decoded.iterator();
        Assert.assertThat(ChainUtils.readPayload(chainIterator.next().getPayload()), Matchers.is(1L));
        Assert.assertThat(chainIterator.hasNext(), Matchers.is(false));
        Assert.assertThat(decoded, sameSequenceAs(chain));
    }

    @Test
    public void testChainWithMultipleElements() {
        Chain chain = ChainUtils.chainOf(ChainUtils.createPayload(1L), ChainUtils.createPayload(2L), ChainUtils.createPayload(3L));
        Assert.assertThat(chain.isEmpty(), Matchers.is(false));
        Assert.assertThat(chain, hasPayloads(1L, 2L, 3L));
        Chain decoded = ChainCodec.decode(ChainCodec.encode(chain));
        Assert.assertThat(decoded.isEmpty(), Matchers.is(false));
        Assert.assertThat(decoded, hasPayloads(1L, 2L, 3L));
    }

    @Test
    public void testChainWithMultipleSequencedElements() {
        Chain chain = ChainUtils.sequencedChainOf(ChainUtils.createPayload(1L), ChainUtils.createPayload(2L), ChainUtils.createPayload(3L));
        Assert.assertThat(chain.isEmpty(), Matchers.is(false));
        Assert.assertThat(chain, hasPayloads(1L, 2L, 3L));
        Chain decoded = ChainCodec.decode(ChainCodec.encode(chain));
        Assert.assertThat(decoded.isEmpty(), Matchers.is(false));
        Assert.assertThat(decoded, hasPayloads(1L, 2L, 3L));
        Assert.assertThat(decoded, sameSequenceAs(chain));
    }

    @Test
    public void testEmptyChain() {
        Chain decoded = ChainCodec.decode(ChainCodec.encode(ChainUtils.chainOf()));
        Assert.assertThat(decoded.isEmpty(), Matchers.is(true));
    }
}

