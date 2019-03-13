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


import com.google.common.io.ByteStreams;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test for {@link StreamMultiplexer}.
 */
@RunWith(JUnit4.class)
public class StreamMultiplexerTest {
    private ByteArrayOutputStream multiplexed;

    private OutputStream out;

    private OutputStream err;

    private OutputStream ctl;

    @Test
    public void testEmptyWire() throws IOException {
        out.flush();
        err.flush();
        ctl.flush();
        assertThat(multiplexed.toByteArray()).isEmpty();
    }

    @Test
    public void testHelloWorldOnStdOut() throws Exception {
        out.write(StreamMultiplexerTest.getLatin("Hello, world."));
        out.flush();
        StreamMultiplexerTest.assertMessage(multiplexed.toByteArray(), 0, "Hello, world.");
    }

    @Test
    public void testInterleavedStdoutStderrControl() throws Exception {
        int start = 0;
        out.write(StreamMultiplexerTest.getLatin("Hello, stdout."));
        out.flush();
        StreamMultiplexerTest.assertMessage(multiplexed.toByteArray(), start, "Hello, stdout.");
        start = multiplexed.toByteArray().length;
        err.write(StreamMultiplexerTest.getLatin("Hello, stderr."));
        err.flush();
        StreamMultiplexerTest.assertMessage(multiplexed.toByteArray(), start, "Hello, stderr.");
        start = multiplexed.toByteArray().length;
        ctl.write(StreamMultiplexerTest.getLatin("Hello, control."));
        ctl.flush();
        StreamMultiplexerTest.assertMessage(multiplexed.toByteArray(), start, "Hello, control.");
        start = multiplexed.toByteArray().length;
        out.write(StreamMultiplexerTest.getLatin("... and back!"));
        out.flush();
        StreamMultiplexerTest.assertMessage(multiplexed.toByteArray(), start, "... and back!");
    }

    @Test
    public void testWillNotCommitToUnderlyingStreamUnlessFlushOrNewline() throws Exception {
        out.write(StreamMultiplexerTest.getLatin(("There are no newline characters in here, so it won't" + " get written just yet.")));
        assertThat(new byte[0]).isEqualTo(multiplexed.toByteArray());
    }

    @Test
    public void testNewlineTriggersFlush() throws Exception {
        out.write(StreamMultiplexerTest.getLatin("No newline just yet, so no flushing. "));
        assertThat(new byte[0]).isEqualTo(multiplexed.toByteArray());
        out.write(StreamMultiplexerTest.getLatin("OK, here we go:\nAnd more to come."));
        StreamMultiplexerTest.assertMessage(multiplexed.toByteArray(), 0, "No newline just yet, so no flushing. OK, here we go:\n");
        int firstMessageLength = multiplexed.toByteArray().length;
        out.write(((byte) ('\n')));
        StreamMultiplexerTest.assertMessage(multiplexed.toByteArray(), firstMessageLength, "And more to come.\n");
    }

    @Test
    public void testFlush() throws Exception {
        out.write(StreamMultiplexerTest.getLatin("Don't forget to flush!"));
        assertThat(multiplexed.toByteArray()).isEqualTo(new byte[0]);
        out.flush();// now the output will appear in multiplexed.

        StreamMultiplexerTest.assertStartsWith(multiplexed.toByteArray(), 1, 0, 0, 0);
        StreamMultiplexerTest.assertMessage(multiplexed.toByteArray(), 0, "Don't forget to flush!");
    }

    @Test
    public void testByteEncoding() throws IOException {
        OutputStream devNull = ByteStreams.nullOutputStream();
        StreamDemultiplexer demux = new StreamDemultiplexer(((byte) (1)), devNull);
        StreamMultiplexer mux = new StreamMultiplexer(demux);
        OutputStream out = mux.createStdout();
        // When we cast 266 to a byte, we get 10. So basically, we ended up
        // comparing 266 with 10 as an integer (because out.write takes an int),
        // and then later cast it to 10. This way we'd end up with a control
        // character \n in the middle of the payload which would then screw things
        // up when the real control character arrived. The fixed version of the
        // StreamMultiplexer avoids this problem by always casting to a byte before
        // carrying out any comparisons.
        out.write(266);
        out.write(10);
    }
}

