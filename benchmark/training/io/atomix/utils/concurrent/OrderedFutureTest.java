/**
 * Copyright 2017-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.utils.concurrent;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;


/**
 * Ordered completable future test.
 */
public class OrderedFutureTest {
    /**
     * Tests ordered completion of future callbacks.
     */
    @Test
    public void testOrderedCompletion() throws Throwable {
        CompletableFuture<String> future = new OrderedFuture();
        AtomicInteger order = new AtomicInteger();
        future.whenComplete(( r, e) -> Assert.assertEquals(1, order.incrementAndGet()));
        future.whenComplete(( r, e) -> Assert.assertEquals(2, order.incrementAndGet()));
        future.handle(( r, e) -> {
            Assert.assertEquals(3, order.incrementAndGet());
            Assert.assertEquals("foo", r);
            return "bar";
        });
        future.thenRun(() -> Assert.assertEquals(3, order.incrementAndGet()));
        future.thenAccept(( r) -> {
            Assert.assertEquals(5, order.incrementAndGet());
            Assert.assertEquals("foo", r);
        });
        future.thenApply(( r) -> {
            Assert.assertEquals(6, order.incrementAndGet());
            Assert.assertEquals("foo", r);
            return "bar";
        });
        future.whenComplete(( r, e) -> {
            Assert.assertEquals(7, order.incrementAndGet());
            Assert.assertEquals("foo", r);
        });
        future.complete("foo");
    }
}

