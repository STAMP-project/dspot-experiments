/**
 * Copyright 2018 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.common;


import HttpData.EMPTY_DATA;
import java.io.InputStream;
import java.io.Reader;
import java.nio.CharBuffer;
import org.junit.Test;

import static java.nio.charset.StandardCharsets.UTF_8;


public class HttpDataTest {
    @Test
    public void toInputStream() throws Exception {
        assertThat(EMPTY_DATA.toInputStream().read()).isEqualTo((-1));
        final InputStream in1 = HttpData.of(new byte[]{ 1, 2, 3, 4 }).toInputStream();
        assertThat(in1.read()).isOne();
        assertThat(in1.read()).isEqualTo(2);
        assertThat(in1.read()).isEqualTo(3);
        assertThat(in1.read()).isEqualTo(4);
        assertThat(in1.read()).isEqualTo((-1));
        final InputStream in2 = HttpData.of(new byte[]{ 1, 2, 3, 4 }, 1, 2).toInputStream();
        assertThat(in2.read()).isEqualTo(2);
        assertThat(in2.read()).isEqualTo(3);
        assertThat(in2.read()).isEqualTo((-1));
    }

    @Test
    public void toReader() throws Exception {
        final Reader in = HttpData.ofUtf8("?A").toReader(UTF_8);
        assertThat(in.read()).isEqualTo(((int) ('\uac00')));
        assertThat(in.read()).isEqualTo(((int) ('A')));
        assertThat(in.read()).isEqualTo((-1));
    }

    @Test
    public void toReaderUtf8() throws Exception {
        final Reader in = HttpData.ofUtf8("?B").toReaderUtf8();
        assertThat(in.read()).isEqualTo(((int) ('\u3042')));
        assertThat(in.read()).isEqualTo(((int) ('B')));
        assertThat(in.read()).isEqualTo((-1));
    }

    @Test
    public void toReaderAscii() throws Exception {
        final Reader in = HttpData.ofUtf8("?C").toReaderAscii();
        // '?' will be decoded into 3 bytes of unknown characters
        assertThat(in.read()).isEqualTo(65533);
        assertThat(in.read()).isEqualTo(65533);
        assertThat(in.read()).isEqualTo(65533);
        assertThat(in.read()).isEqualTo(((int) ('C')));
        assertThat(in.read()).isEqualTo((-1));
    }

    @Test
    public void fromUtf8CharSequence() throws Exception {
        assertThat(HttpData.ofUtf8(((CharSequence) ("\uac00A"))).toStringUtf8()).isEqualTo("?A");
        assertThat(HttpData.ofUtf8(CharBuffer.wrap("?B")).toStringUtf8()).isEqualTo("?B");
    }

    @Test
    public void fromAsciiCharSequence() throws Exception {
        assertThat(HttpData.ofAscii(((CharSequence) ("\uac00A"))).toStringUtf8()).isEqualTo("?A");
        assertThat(HttpData.ofAscii(CharBuffer.wrap("?B")).toStringUtf8()).isEqualTo("?B");
    }
}

