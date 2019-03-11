/**
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.webserver;


import io.helidon.common.http.Reader;
import io.helidon.common.reactive.ReactiveStreamsAdapter;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;


/**
 * The ServerRequestReaderTest.
 */
public class ServerRequestReaderTest {
    static class A {}

    static class B extends ServerRequestReaderTest.A {}

    static class C extends ServerRequestReaderTest.B {}

    @Test
    public void test1() throws Exception {
        Reader<ServerRequestReaderTest.B> reader = ( publisher, clazz) -> ReactiveStreamsAdapter.publisherFromFlow(publisher).collectList().toFuture().thenApply(( byteBuffers) -> new io.helidon.webserver.B());
        CompletionStage<? extends ServerRequestReaderTest.B> apply = reader.apply(ReactiveStreamsAdapter.publisherToFlow(Flux.empty()));
        CompletionStage<? extends ServerRequestReaderTest.B> a = reader.apply(ReactiveStreamsAdapter.publisherToFlow(Flux.empty()), ServerRequestReaderTest.A.class);
        CompletionStage<? extends ServerRequestReaderTest.B> b = reader.apply(ReactiveStreamsAdapter.publisherToFlow(Flux.empty()), ServerRequestReaderTest.B.class);
        // this should not be possible to compile:
        // CompletionStage<? extends B> apply2 = reader.apply(ReactiveStreamsAdapter.publisherToFlow(Flux.empty()), C.class);
        // which is why we have the cast method
        CompletionStage<? extends ServerRequestReaderTest.C> c = reader.applyAndCast(ReactiveStreamsAdapter.publisherToFlow(Flux.empty()), ServerRequestReaderTest.C.class);
        MatcherAssert.assertThat(apply.toCompletableFuture().get(10, TimeUnit.SECONDS), IsInstanceOf.instanceOf(ServerRequestReaderTest.B.class));
        MatcherAssert.assertThat(a.toCompletableFuture().get(10, TimeUnit.SECONDS), IsInstanceOf.instanceOf(ServerRequestReaderTest.A.class));
        MatcherAssert.assertThat(b.toCompletableFuture().get(10, TimeUnit.SECONDS), IsInstanceOf.instanceOf(ServerRequestReaderTest.B.class));
        try {
            ServerRequestReaderTest.B bOrC = c.toCompletableFuture().get(10, TimeUnit.SECONDS);
            Assertions.fail(("Should have thrown an exception.. " + bOrC));
            // if there was no explicit cast, only this would fail: Assert.assertThat(actual, IsInstanceOf.instanceOf(C.class));
        } catch (ExecutionException e) {
            MatcherAssert.assertThat(e.getCause(), IsInstanceOf.instanceOf(ClassCastException.class));
        }
    }
}

