/**
 * -\-\-
 * Spotify Apollo Extra
 * --
 * Copyright (C) 2013 - 2015 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.apollo.concurrent;


import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class UtilTest {
    SettableFuture<String> future;

    CompletableFuture<Integer> stage;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldConvertToStage() throws Exception {
        CompletionStage<String> converted = Util.asStage(future);
        future.set("i'm done!");
        Assert.assertThat(converted.toCompletableFuture().get(), CoreMatchers.equalTo("i'm done!"));
    }

    @Test
    public void shouldPropagateExceptionsToStage() throws Exception {
        CompletionStage<String> converted = Util.asStage(future).exceptionally(Throwable::getMessage);
        NullPointerException exception = new NullPointerException("expected exception");
        future.setException(exception);
        Assert.assertThat(converted.toCompletableFuture().get(), CoreMatchers.equalTo("expected exception"));
    }

    @Test
    public void shouldConvertToFuture() throws Exception {
        ListenableFuture<Integer> converted = Util.asFuture(stage);
        stage.complete(9834);
        Assert.assertThat(converted.get(), CoreMatchers.equalTo(9834));
    }

    @Test
    public void shouldHandleNullsFromStage() throws Exception {
        ListenableFuture<Integer> converted = Util.asFuture(stage);
        stage.complete(null);
        Assert.assertThat(converted.get(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldPropagateExceptionsToFuture() throws Throwable {
        ListenableFuture<Integer> converted = Util.asFuture(stage);
        NullPointerException expected = new NullPointerException("expected");
        stage.completeExceptionally(expected);
        thrown.expect(CoreMatchers.is(expected));
        try {
            converted.get();
        } catch (ExecutionException ee) {
            throw ee.getCause();
        }
        Assert.fail("should throw");
    }
}

