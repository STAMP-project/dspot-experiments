/**
 * Copyright 2014 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.util.io;


import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Random;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests {@link StreamDemultiplexer}.
 */
@RunWith(JUnit4.class)
public class StreamDemultiplexerTest {
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    private ByteArrayOutputStream err = new ByteArrayOutputStream();

    private ByteArrayOutputStream ctl = new ByteArrayOutputStream();

    @Test
    public void testHelloWorldOnStandardOut() throws Exception {
        byte[] multiplexed = StreamDemultiplexerTest.chunk(1, "Hello, world.");
        try (final StreamDemultiplexer demux = new StreamDemultiplexer(((byte) (1)), out)) {
            demux.write(multiplexed);
        }
        assertThat(out.toString("ISO-8859-1")).isEqualTo("Hello, world.");
    }

    @Test
    public void testOutErrCtl() throws Exception {
        byte[] multiplexed = StreamDemultiplexerTest.concat(StreamDemultiplexerTest.chunk(1, "out"), StreamDemultiplexerTest.chunk(2, "err"), StreamDemultiplexerTest.chunk(3, "ctl"));
        try (final StreamDemultiplexer demux = new StreamDemultiplexer(((byte) (1)), out, err, ctl)) {
            demux.write(multiplexed);
        }
        assertThat(toAnsi(out)).isEqualTo("out");
        assertThat(toAnsi(err)).isEqualTo("err");
        assertThat(toAnsi(ctl)).isEqualTo("ctl");
    }

    @Test
    public void testWithoutLineBreaks() throws Exception {
        byte[] multiplexed = StreamDemultiplexerTest.concat(StreamDemultiplexerTest.chunk(1, "just "), StreamDemultiplexerTest.chunk(1, "one "), StreamDemultiplexerTest.chunk(1, "line"));
        try (final StreamDemultiplexer demux = new StreamDemultiplexer(((byte) (1)), out)) {
            demux.write(multiplexed);
        }
        assertThat(out.toString("ISO-8859-1")).isEqualTo("just one line");
    }

    @Test
    public void testMultiplexAndBackWithHelloWorld() throws Exception {
        StreamDemultiplexer demux = new StreamDemultiplexer(((byte) (1)), out);
        StreamMultiplexer mux = new StreamMultiplexer(demux);
        OutputStream out = mux.createStdout();
        out.write(inAnsi("Hello, world."));
        out.flush();
        assertThat(toAnsi(this.out)).isEqualTo("Hello, world.");
    }

    @Test
    public void testMultiplexDemultiplexBinaryStress() throws Exception {
        StreamDemultiplexer demux = new StreamDemultiplexer(((byte) (1)), out, err, ctl);
        StreamMultiplexer mux = new StreamMultiplexer(demux);
        OutputStream[] muxOuts = new OutputStream[]{ mux.createStdout(), mux.createStderr(), mux.createControl() };
        ByteArrayOutputStream[] expectedOuts = new ByteArrayOutputStream[]{ new ByteArrayOutputStream(), new ByteArrayOutputStream(), new ByteArrayOutputStream() };
        Random random = new Random(-559038737);
        for (int round = 0; round < 100; round++) {
            byte[] buffer = new byte[random.nextInt(100)];
            random.nextBytes(buffer);
            int streamId = random.nextInt(3);
            expectedOuts[streamId].write(buffer);
            expectedOuts[streamId].flush();
            muxOuts[streamId].write(buffer);
            muxOuts[streamId].flush();
        }
        assertThat(out.toByteArray()).isEqualTo(expectedOuts[0].toByteArray());
        assertThat(err.toByteArray()).isEqualTo(expectedOuts[1].toByteArray());
        assertThat(ctl.toByteArray()).isEqualTo(expectedOuts[2].toByteArray());
    }
}

