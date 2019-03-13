/**
 * Copyright (c) 2017, 2019 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.media.jsonp.server;


import io.helidon.common.http.DataChunk;
import io.helidon.common.reactive.ReactiveStreamsAdapter;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;


/**
 * The JsonContentReaderTest.
 */
public class JsonContentReaderTest {
    @Test
    public void simpleJsonObject() throws Exception {
        Flux<DataChunk> flux = Flux.just("{ \"p\" : \"val\" }").map(( s) -> DataChunk.create(s.getBytes()));
        CompletionStage<? extends JsonObject> stage = JsonSupport.create().reader().applyAndCast(ReactiveStreamsAdapter.publisherToFlow(flux), JsonObject.class);
        JsonObject jsonObject = stage.toCompletableFuture().get(10, TimeUnit.SECONDS);
        MatcherAssert.assertThat(jsonObject.getJsonString("p").getString(), Is.is("val"));
    }

    @Test
    public void incompatibleTypes() throws Exception {
        Flux<DataChunk> flux = Flux.just("{ \"p\" : \"val\" }").map(( s) -> DataChunk.create(s.getBytes()));
        CompletionStage<? extends JsonArray> stage = JsonSupport.create().reader().applyAndCast(ReactiveStreamsAdapter.publisherToFlow(flux), JsonArray.class);
        try {
            JsonArray array = stage.thenApply(( o) -> {
                Assertions.fail("Shouldn't occur because of a class cast exception!");
                return o;
            }).toCompletableFuture().get(10, TimeUnit.SECONDS);
            Assertions.fail(("Should have failed because an expected array is actually an object: " + array));
        } catch (ExecutionException e) {
            MatcherAssert.assertThat(e.getCause(), IsInstanceOf.instanceOf(ClassCastException.class));
        }
    }

    @Test
    public void simpleJsonArray() throws Exception {
        Flux<DataChunk> flux = Flux.just("[ \"val\" ]").map(( s) -> DataChunk.create(s.getBytes()));
        CompletionStage<? extends JsonArray> stage = JsonSupport.create().reader().applyAndCast(ReactiveStreamsAdapter.publisherToFlow(flux), JsonArray.class);
        JsonArray array = stage.toCompletableFuture().get(10, TimeUnit.SECONDS);
        MatcherAssert.assertThat(array.getString(0), Is.is("val"));
    }

    @Test
    public void invalidJson() throws Exception {
        Flux<DataChunk> flux = Flux.just("{ \"p\" : \"val\" ").map(( s) -> DataChunk.create(s.getBytes()));
        CompletionStage<? extends JsonObject> stage = JsonSupport.create().reader().applyAndCast(ReactiveStreamsAdapter.publisherToFlow(flux), JsonObject.class);
        try {
            stage.toCompletableFuture().get(10, TimeUnit.SECONDS);
            Assertions.fail("Should have thrown an exception");
        } catch (ExecutionException e) {
            MatcherAssert.assertThat(stage.toCompletableFuture().isCompletedExceptionally(), CoreMatchers.is(true));
            MatcherAssert.assertThat(e.getCause(), IsInstanceOf.instanceOf(JsonException.class));
        }
    }

    @Test
    public void defaultJsonSupportAsSingleton() {
        MatcherAssert.assertThat(JsonSupport.create(), CoreMatchers.sameInstance(JsonSupport.create()));
    }
}

