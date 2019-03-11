/**
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.storage.contrib.nio;


import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link CloudStorageFileSystem}.
 */
@RunWith(JUnit4.class)
public class CloudStorageReadTest {
    private static final String ALONE = "To be, or not to be, that is the question\u2014\n" + (((((("Whether \'tis Nobler in the mind to suffer\n" + "The Slings and Arrows of outrageous Fortune,\n") + "Or to take Arms against a Sea of troubles,\n") + "And by opposing, end them? To die, to sleep\u2014\n") + "No more; and by a sleep, to say we end\n") + "The Heart-ache, and the thousand Natural shocks\n") + "That Flesh is heir to? \'Tis a consummation\n");

    // Large enough value that we write more than one "chunk", for interesting behavior.
    private static final int repeat = 10000;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testInputStreamReads() throws IOException, InterruptedException {
        // fill in the file
        byte[] bytes = CloudStorageReadTest.ALONE.getBytes(StandardCharsets.UTF_8);
        try (FileSystem fs = CloudStorageFileSystem.forBucket("bucket")) {
            Path p = fillFile(fs, bytes, CloudStorageReadTest.repeat);
            try (InputStream is = Files.newInputStream(p)) {
                byte[] buf = new byte[bytes.length];
                for (int i = 0; i < (CloudStorageReadTest.repeat); i++) {
                    Arrays.fill(buf, ((byte) (0)));
                    for (int off = 0; off < (bytes.length);) {
                        int delta = is.read(buf, off, ((bytes.length) - off));
                        if (delta < 0) {
                            // EOF
                            break;
                        }
                        off += delta;
                    }
                    assertWithMessage(("Wrong bytes from input stream at repeat " + i)).that(new String(buf, StandardCharsets.UTF_8)).isEqualTo(CloudStorageReadTest.ALONE);
                }
                // reading past the end
                int eof = is.read(buf, 0, 1);
                assertWithMessage("EOF should return -1").that(eof).isEqualTo((-1));
            } finally {
                // clean up
                Files.delete(p);
            }
        }
    }

    @Test
    public void testChannelReads() throws IOException, InterruptedException {
        // fill in the file
        byte[] bytes = CloudStorageReadTest.ALONE.getBytes(StandardCharsets.UTF_8);
        try (FileSystem fs = CloudStorageFileSystem.forBucket("bucket")) {
            Path p = fillFile(fs, bytes, CloudStorageReadTest.repeat);
            try (SeekableByteChannel chan = Files.newByteChannel(p, StandardOpenOption.READ)) {
                ByteBuffer buf = ByteBuffer.allocate(bytes.length);
                for (int i = 0; i < (CloudStorageReadTest.repeat); i++) {
                    buf.clear();
                    for (int off = 0; off < (bytes.length);) {
                        int read = chan.read(buf);
                        if (read < 0) {
                            // EOF
                            break;
                        }
                        off += read;
                    }
                    assertWithMessage(("Wrong bytes from channel at repeat " + i)).that(new String(buf.array(), StandardCharsets.UTF_8)).isEqualTo(CloudStorageReadTest.ALONE);
                }
                // reading past the end
                buf.clear();
                int eof = chan.read(buf);
                assertWithMessage("EOF should return -1").that(eof).isEqualTo((-1));
            } finally {
                // clean up
                Files.delete(p);
            }
        }
    }
}

