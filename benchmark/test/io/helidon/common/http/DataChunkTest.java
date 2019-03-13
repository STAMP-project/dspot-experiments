/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.common.http;


import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Unit test for {@link DataChunk}.
 */
class DataChunkTest {
    @Test
    public void testSimpleWrapping() {
        byte[] bytes = "urzatron".getBytes(StandardCharsets.UTF_8);
        DataChunk chunk = DataChunk.create(bytes);
        MatcherAssert.assertThat(chunk.bytes(), CoreMatchers.is(bytes));
        MatcherAssert.assertThat(chunk.flush(), CoreMatchers.is(false));
        MatcherAssert.assertThat(chunk.id(), CoreMatchers.not(0L));
        MatcherAssert.assertThat(chunk.isReleased(), CoreMatchers.is(false));
        MatcherAssert.assertThat(chunk.data().array(), CoreMatchers.is(bytes));
    }

    @Test
    public void testReleasing() {
        byte[] bytes = "urzatron".getBytes(StandardCharsets.UTF_8);
        AtomicBoolean ab = new AtomicBoolean(false);
        DataChunk chunk = DataChunk.create(true, ByteBuffer.wrap(bytes), () -> ab.set(true));
        MatcherAssert.assertThat(chunk.bytes(), CoreMatchers.is(bytes));
        MatcherAssert.assertThat(chunk.flush(), CoreMatchers.is(true));
        MatcherAssert.assertThat(chunk.id(), CoreMatchers.not(0L));
        MatcherAssert.assertThat(chunk.data().array(), CoreMatchers.is(bytes));
        MatcherAssert.assertThat(chunk.isReleased(), CoreMatchers.is(false));
        MatcherAssert.assertThat(ab.get(), CoreMatchers.is(false));
        chunk.release();
        MatcherAssert.assertThat(chunk.isReleased(), CoreMatchers.is(true));
        MatcherAssert.assertThat(ab.get(), CoreMatchers.is(true));
    }

    @Test
    public void testReleasingNoRunnable() {
        byte[] bytes = "urzatron".getBytes(StandardCharsets.UTF_8);
        DataChunk chunk = DataChunk.create(true, ByteBuffer.wrap(bytes));
        MatcherAssert.assertThat(chunk.bytes(), CoreMatchers.is(bytes));
        MatcherAssert.assertThat(chunk.flush(), CoreMatchers.is(true));
        MatcherAssert.assertThat(chunk.id(), CoreMatchers.not(0L));
        MatcherAssert.assertThat(chunk.data().array(), CoreMatchers.is(bytes));
        MatcherAssert.assertThat(chunk.isReleased(), CoreMatchers.is(false));
        chunk.release();
        MatcherAssert.assertThat(chunk.isReleased(), CoreMatchers.is(true));
    }
}

