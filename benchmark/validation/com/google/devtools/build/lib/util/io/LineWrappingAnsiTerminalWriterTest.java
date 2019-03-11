/**
 * Copyright 2016 The Bazel Authors. All rights reserved.
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


import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static LoggingTerminalWriter.FAIL;
import static LoggingTerminalWriter.NEWLINE;
import static LoggingTerminalWriter.NORMAL;
import static LoggingTerminalWriter.OK;


/**
 * Tests {@link LineWrappingAnsiTerminalWriter}.
 */
@RunWith(JUnit4.class)
public class LineWrappingAnsiTerminalWriterTest {
    static final String NL = NEWLINE;

    static final String OK = OK;

    static final String FAIL = FAIL;

    static final String NORMAL = NORMAL;

    @Test
    public void testSimpleLineWrapping() throws IOException {
        LoggingTerminalWriter terminal = new LoggingTerminalWriter();
        append("abcdefghij");
        assertThat(terminal.getTranscript()).isEqualTo((((("abcd+" + (LineWrappingAnsiTerminalWriterTest.NL)) + "efgh+") + (LineWrappingAnsiTerminalWriterTest.NL)) + "ij"));
    }

    @Test
    public void testAlwaysWrap() throws IOException {
        LoggingTerminalWriter terminal = new LoggingTerminalWriter();
        append("12345").newline();
        assertThat(terminal.getTranscript()).isEqualTo(((("1234+" + (LineWrappingAnsiTerminalWriterTest.NL)) + "5") + (LineWrappingAnsiTerminalWriterTest.NL)));
    }

    @Test
    public void testWrapLate() throws IOException {
        LoggingTerminalWriter terminal = new LoggingTerminalWriter();
        append("1234");
        // Lines are only wrapped, once a character is written that cannot fit in the current line, and
        // not already once the last usable character of a line is used. Hence, in this example, we do
        // not want to see the continuation character.
        assertThat(terminal.getTranscript()).isEqualTo("1234");
    }

    @Test
    public void testNewlineTranslated() throws IOException {
        LoggingTerminalWriter terminal = new LoggingTerminalWriter();
        append("foo\nbar\n");
        assertThat(terminal.getTranscript()).isEqualTo(((("foo" + (LineWrappingAnsiTerminalWriterTest.NL)) + "bar") + (LineWrappingAnsiTerminalWriterTest.NL)));
    }

    @Test
    public void testNewlineResetsCount() throws IOException {
        LoggingTerminalWriter terminal = new LoggingTerminalWriter();
        append("ABC\nABC").newline();
        assertThat(terminal.getTranscript()).isEqualTo(((((((("123" + (LineWrappingAnsiTerminalWriterTest.NL)) + "abc") + (LineWrappingAnsiTerminalWriterTest.NL)) + "ABC") + (LineWrappingAnsiTerminalWriterTest.NL)) + "ABC") + (LineWrappingAnsiTerminalWriterTest.NL)));
    }

    @Test
    public void testEventsPassedThrough() throws IOException {
        LoggingTerminalWriter terminal = new LoggingTerminalWriter();
        append("normal");
        assertThat(terminal.getTranscript()).isEqualTo(((((((LineWrappingAnsiTerminalWriterTest.OK) + "ok") + (LineWrappingAnsiTerminalWriterTest.FAIL)) + "fail") + (LineWrappingAnsiTerminalWriterTest.NORMAL)) + "normal"));
    }
}

