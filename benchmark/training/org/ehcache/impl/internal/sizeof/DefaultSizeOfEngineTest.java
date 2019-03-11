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
package org.ehcache.impl.internal.sizeof;


import org.ehcache.core.spi.store.heap.LimitExceededException;
import org.ehcache.core.spi.store.heap.SizeOfEngine;
import org.ehcache.impl.copy.IdentityCopier;
import org.ehcache.impl.internal.store.heap.holders.CopiedOnHeapValueHolder;
import org.ehcache.spi.copy.Copier;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Abhilash
 */
public class DefaultSizeOfEngineTest {
    @Test
    public void testMaxObjectGraphSizeExceededException() {
        SizeOfEngine sizeOfEngine = new DefaultSizeOfEngine(3, Long.MAX_VALUE);
        try {
            Copier<DefaultSizeOfEngineTest.MaxDepthGreaterThanThree> valueCopier = IdentityCopier.identityCopier();
            sizeOfEngine.sizeof(new DefaultSizeOfEngineTest.MaxDepthGreaterThanThree(), new CopiedOnHeapValueHolder<>(new DefaultSizeOfEngineTest.MaxDepthGreaterThanThree(), 0L, true, valueCopier));
            Assert.fail();
        } catch (Exception limitExceededException) {
            MatcherAssert.assertThat(limitExceededException, Matchers.instanceOf(LimitExceededException.class));
        }
    }

    @Test
    public void testMaxObjectSizeExceededException() {
        SizeOfEngine sizeOfEngine = new DefaultSizeOfEngine(Long.MAX_VALUE, 1000);
        try {
            String overSized = new String(new byte[1000]);
            Copier<String> valueCopier = IdentityCopier.identityCopier();
            sizeOfEngine.sizeof(overSized, new CopiedOnHeapValueHolder<>("test", 0L, true, valueCopier));
            Assert.fail();
        } catch (Exception limitExceededException) {
            MatcherAssert.assertThat(limitExceededException, Matchers.instanceOf(LimitExceededException.class));
            MatcherAssert.assertThat(limitExceededException.getMessage(), Matchers.containsString("Max Object Size reached for the object"));
        }
    }

    private static class MaxDepthGreaterThanThree {
        private Object second = new Object();

        private Object third = new Object();

        private Object fourth = new Object();
    }
}

