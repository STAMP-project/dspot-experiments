/**
 * Copyright 2015 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.remote;


import build.bazel.remote.execution.v2.Digest;
import com.google.devtools.build.lib.remote.Chunker.Chunk;
import com.google.devtools.build.lib.remote.util.DigestUtil;
import com.google.devtools.build.lib.vfs.DigestHashFunction;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests for {@link Chunker}.
 */
@RunWith(JUnit4.class)
public class ChunkerTest {
    private final DigestUtil digestUtil = new DigestUtil(DigestHashFunction.SHA256);

    @Test
    public void chunkingShouldWork() throws IOException {
        Random rand = new Random();
        byte[] expectedData = new byte[21];
        rand.nextBytes(expectedData);
        Digest expectedDigest = digestUtil.compute(expectedData);
        Chunker chunker = Chunker.builder(digestUtil).setInput(expectedData).setChunkSize(10).build();
        ByteArrayOutputStream actualData = new ByteArrayOutputStream();
        assertThat(chunker.hasNext()).isTrue();
        Chunk next = chunker.next();
        assertThat(next.getOffset()).isEqualTo(0);
        assertThat(next.getData()).hasSize(10);
        assertThat(next.getDigest()).isEqualTo(expectedDigest);
        next.getData().writeTo(actualData);
        assertThat(chunker.hasNext()).isTrue();
        next = chunker.next();
        assertThat(next.getOffset()).isEqualTo(10);
        assertThat(next.getData()).hasSize(10);
        assertThat(next.getDigest()).isEqualTo(expectedDigest);
        next.getData().writeTo(actualData);
        assertThat(chunker.hasNext()).isTrue();
        next = chunker.next();
        assertThat(next.getOffset()).isEqualTo(20);
        assertThat(next.getData()).hasSize(1);
        assertThat(next.getDigest()).isEqualTo(expectedDigest);
        next.getData().writeTo(actualData);
        assertThat(chunker.hasNext()).isFalse();
        assertThat(actualData.toByteArray()).isEqualTo(expectedData);
    }

    @Test
    public void nextShouldThrowIfNoMoreData() throws IOException {
        byte[] data = new byte[10];
        Chunker chunker = Chunker.builder(digestUtil).setInput(data).setChunkSize(10).build();
        assertThat(chunker.hasNext()).isTrue();
        assertThat(chunker.next()).isNotNull();
        assertThat(chunker.hasNext()).isFalse();
        try {
            chunker.next();
            TestCase.fail("Should have thrown an exception");
        } catch (NoSuchElementException expected) {
            // Intentionally left empty.
        }
    }

    @Test
    public void emptyData() throws Exception {
        byte[] data = new byte[0];
        Chunker chunker = Chunker.builder(digestUtil).setInput(data).build();
        assertThat(chunker.hasNext()).isTrue();
        Chunk next = chunker.next();
        assertThat(next).isNotNull();
        assertThat(next.getData()).isEmpty();
        assertThat(next.getOffset()).isEqualTo(0);
        assertThat(chunker.hasNext()).isFalse();
        try {
            chunker.next();
            TestCase.fail("Should have thrown an exception");
        } catch (NoSuchElementException expected) {
            // Intentionally left empty.
        }
    }

    @Test
    public void reset() throws Exception {
        byte[] data = new byte[]{ 1, 2, 3 };
        Chunker chunker = Chunker.builder(digestUtil).setInput(data).setChunkSize(1).build();
        assertNextEquals(chunker, ((byte) (1)));
        assertNextEquals(chunker, ((byte) (2)));
        chunker.reset();
        assertNextEquals(chunker, ((byte) (1)));
        assertNextEquals(chunker, ((byte) (2)));
        assertNextEquals(chunker, ((byte) (3)));
        chunker.reset();
        assertNextEquals(chunker, ((byte) (1)));
    }

    @Test
    public void resourcesShouldBeReleased() throws IOException {
        // Test that after having consumed all data or after reset() is called (whatever happens first)
        // the underlying InputStream should be closed.
        byte[] data = new byte[]{ 1, 2 };
        final AtomicReference<InputStream> in = new AtomicReference<>();
        Supplier<InputStream> supplier = () -> {
            in.set(Mockito.spy(new ByteArrayInputStream(data)));
            return in.get();
        };
        Digest digest = digestUtil.compute(data);
        Chunker chunker = new Chunker(supplier, digest, 1, digestUtil);
        assertThat(in.get()).isNull();
        assertNextEquals(chunker, ((byte) (1)));
        Mockito.verify(in.get(), Mockito.never()).close();
        assertNextEquals(chunker, ((byte) (2)));
        Mockito.verify(in.get()).close();
        chunker.reset();
        chunker.next();
        chunker.reset();
        Mockito.verify(in.get()).close();
    }
}

