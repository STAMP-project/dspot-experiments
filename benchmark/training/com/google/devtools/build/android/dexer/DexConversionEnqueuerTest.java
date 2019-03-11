/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.android.dexer;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.devtools.build.android.dexer.Dexing.DexingKey;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link DexConversionEnqueuer}.
 */
@RunWith(JUnit4.class)
public class DexConversionEnqueuerTest {
    private static final long FILE_TIME = 12345678987654321L;

    @Mock
    private ZipFile zip;

    private DexConversionEnqueuer stuffer;

    private final Cache<DexingKey, byte[]> cache = CacheBuilder.newBuilder().build();

    @Test
    public void testEmptyZip() throws Exception {
        mockEntries();
        stuffer.call();
    }

    @Test
    public void testDirectory_copyEmptyBuffer() throws Exception {
        ZipEntry entry = newZipEntry("dir/", 0);
        assertThat(entry.isDirectory()).isTrue();// test sanity

        mockEntries(entry);
        stuffer.call();
        Future<ZipEntryContent> f = stuffer.getFiles().remove();
        assertThat(f.isDone()).isTrue();
        assertThat(f.get().getEntry()).isEqualTo(entry);
        assertThat(f.get().getContent()).isEmpty();
        assertThat(entry.getCompressedSize()).isEqualTo(0);
    }

    @Test
    public void testFile_copyContent() throws Exception {
        byte[] content = "Hello".getBytes(StandardCharsets.UTF_8);
        ZipEntry entry = newZipEntry("file", content.length);
        mockEntries(entry);
        Mockito.when(zip.getInputStream(entry)).thenReturn(new ByteArrayInputStream(content));
        stuffer.call();
        Future<ZipEntryContent> f = stuffer.getFiles().remove();
        assertThat(f.isDone()).isTrue();
        assertThat(f.get().getEntry()).isEqualTo(entry);
        assertThat(f.get().getContent()).isEqualTo(content);
        assertThat(cache.size()).isEqualTo(0);// don't cache resource files

        assertThat(entry.getCompressedSize()).isEqualTo((-1));// we don't know how the file will compress

    }

    @Test
    public void testClass_convertToDex() throws Exception {
        testConvertClassToDex();
    }

    @Test
    public void testClass_cachedResult() throws Exception {
        byte[] dexcode = testConvertClassToDex();
        makeStuffer();
        String filename = (getClass().getName().replace('.', '/')) + ".class";
        mockClassFile(filename);
        stuffer.call();
        Future<ZipEntryContent> f = stuffer.getFiles().remove();
        assertThat(f.isDone()).isTrue();
        assertThat(f.get().getEntry().getName()).isEqualTo((filename + ".dex"));
        assertThat(f.get().getEntry().getTime()).isEqualTo(DexConversionEnqueuerTest.FILE_TIME);
        assertThat(f.get().getContent()).isSameAs(dexcode);
    }

    @Test
    public void testException_stillEnqueueEndOfStreamMarker() throws Exception {
        Mockito.when(zip.entries()).thenThrow(new IllegalStateException("test"));
        try {
            stuffer.call();
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException expected) {
        }
        // assertEndOfStreamMarker() makes sure the end-of-stream marker is there
    }
}

