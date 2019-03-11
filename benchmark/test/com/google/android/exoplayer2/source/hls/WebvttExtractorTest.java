/**
 * Copyright (C) 2018 The Android Open Source Project
 *
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
 */
package com.google.android.exoplayer2.source.hls;


import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests for {@link WebvttExtractor}.
 */
@RunWith(RobolectricTestRunner.class)
public class WebvttExtractorTest {
    @Test
    public void sniff_sniffsWebvttHeaderWithTrailingSpace() throws IOException, InterruptedException {
        byte[] data = new byte[]{ 'W', 'E', 'B', 'V', 'T', 'T', ' ', '\t' };
        assertThat(WebvttExtractorTest.sniffData(data)).isTrue();
    }

    @Test
    public void sniff_discardsByteOrderMark() throws IOException, InterruptedException {
        byte[] data = new byte[]{ ((byte) (239)), ((byte) (187)), ((byte) (191)), 'W', 'E', 'B', 'V', 'T', 'T', '\n', ' ' };
        assertThat(WebvttExtractorTest.sniffData(data)).isTrue();
    }

    @Test
    public void sniff_failsForIncorrectBom() throws IOException, InterruptedException {
        byte[] data = new byte[]{ ((byte) (239)), ((byte) (187)), ((byte) (187)), 'W', 'E', 'B', 'V', 'T', 'T', '\n' };
        assertThat(WebvttExtractorTest.sniffData(data)).isFalse();
    }

    @Test
    public void sniff_failsForIncompleteHeader() throws IOException, InterruptedException {
        byte[] data = new byte[]{ 'W', 'E', 'B', 'V', 'T', '\n' };
        assertThat(WebvttExtractorTest.sniffData(data)).isFalse();
    }

    @Test
    public void sniff_failsForIncorrectHeader() throws IOException, InterruptedException {
        byte[] data = new byte[]{ ((byte) (239)), ((byte) (187)), ((byte) (191)), 'W', 'e', 'B', 'V', 'T', 'T', '\n' };
        assertThat(WebvttExtractorTest.sniffData(data)).isFalse();
    }
}

