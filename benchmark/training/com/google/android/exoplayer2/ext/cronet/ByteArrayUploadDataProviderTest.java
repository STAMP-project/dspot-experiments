/**
 * Copyright (C) 2016 The Android Open Source Project
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
package com.google.android.exoplayer2.ext.cronet;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.chromium.net.UploadDataSink;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests for {@link ByteArrayUploadDataProvider}.
 */
@RunWith(RobolectricTestRunner.class)
public final class ByteArrayUploadDataProviderTest {
    private static final byte[] TEST_DATA = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

    @Mock
    private UploadDataSink mockUploadDataSink;

    private ByteBuffer byteBuffer;

    private ByteArrayUploadDataProvider byteArrayUploadDataProvider;

    @Test
    public void testGetLength() {
        assertThat(byteArrayUploadDataProvider.getLength()).isEqualTo(ByteArrayUploadDataProviderTest.TEST_DATA.length);
    }

    @Test
    public void testReadFullBuffer() throws IOException {
        byteArrayUploadDataProvider.read(mockUploadDataSink, byteBuffer);
        assertThat(byteBuffer.array()).isEqualTo(ByteArrayUploadDataProviderTest.TEST_DATA);
    }

    @Test
    public void testReadPartialBuffer() throws IOException {
        byte[] firstHalf = Arrays.copyOf(ByteArrayUploadDataProviderTest.TEST_DATA, ((ByteArrayUploadDataProviderTest.TEST_DATA.length) / 2));
        byte[] secondHalf = Arrays.copyOfRange(ByteArrayUploadDataProviderTest.TEST_DATA, ((ByteArrayUploadDataProviderTest.TEST_DATA.length) / 2), ByteArrayUploadDataProviderTest.TEST_DATA.length);
        byteBuffer = ByteBuffer.allocate(((ByteArrayUploadDataProviderTest.TEST_DATA.length) / 2));
        // Read half of the data.
        byteArrayUploadDataProvider.read(mockUploadDataSink, byteBuffer);
        assertThat(byteBuffer.array()).isEqualTo(firstHalf);
        // Read the second half of the data.
        byteBuffer.rewind();
        byteArrayUploadDataProvider.read(mockUploadDataSink, byteBuffer);
        assertThat(byteBuffer.array()).isEqualTo(secondHalf);
        Mockito.verify(mockUploadDataSink, Mockito.times(2)).onReadSucceeded(false);
    }

    @Test
    public void testRewind() throws IOException {
        // Read all the data.
        byteArrayUploadDataProvider.read(mockUploadDataSink, byteBuffer);
        assertThat(byteBuffer.array()).isEqualTo(ByteArrayUploadDataProviderTest.TEST_DATA);
        // Rewind and make sure it can be read again.
        byteBuffer.clear();
        byteArrayUploadDataProvider.rewind(mockUploadDataSink);
        byteArrayUploadDataProvider.read(mockUploadDataSink, byteBuffer);
        assertThat(byteBuffer.array()).isEqualTo(ByteArrayUploadDataProviderTest.TEST_DATA);
        Mockito.verify(mockUploadDataSink).onRewindSucceeded();
    }
}

