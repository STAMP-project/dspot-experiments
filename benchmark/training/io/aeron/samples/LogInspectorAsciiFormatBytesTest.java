/**
 * Copyright 2014-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.aeron.samples;


import LogInspector.AERON_LOG_DATA_FORMAT_PROP_NAME;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class LogInspectorAsciiFormatBytesTest {
    private String originalDataFormatProperty;

    private final byte buffer;

    private final char expected;

    public LogInspectorAsciiFormatBytesTest(final int buffer, final int expected) {
        this.buffer = ((byte) (buffer));
        this.expected = ((char) (expected));
    }

    @Test
    public void shouldFormatBytesToAscii() {
        System.setProperty(AERON_LOG_DATA_FORMAT_PROP_NAME, "ascii");
        final char[] formattedBytes = LogInspector.formatBytes(new UnsafeBuffer(new byte[]{ buffer }), 0, 1);
        Assert.assertEquals(expected, formattedBytes[0]);
    }
}

