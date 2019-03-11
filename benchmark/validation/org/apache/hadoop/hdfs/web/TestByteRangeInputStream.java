/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.web;


import ByteRangeInputStream.StreamStatus.SEEK;
import com.google.common.net.HttpHeaders;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.hadoop.hdfs.web.ByteRangeInputStream.InputStreamAndFileLength;
import org.apache.hadoop.test.Whitebox;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestByteRangeInputStream {
    private class ByteRangeInputStreamImpl extends ByteRangeInputStream {
        public ByteRangeInputStreamImpl(URLOpener o, URLOpener r) throws IOException {
            super(o, r);
        }

        @Override
        protected URL getResolvedUrl(HttpURLConnection connection) throws IOException {
            return new URL("http://resolvedurl/");
        }
    }

    @Test
    public void testByteRange() throws IOException {
        ByteRangeInputStream.URLOpener oMock = getMockURLOpener(new URL("http://test"));
        ByteRangeInputStream.URLOpener rMock = getMockURLOpener(null);
        ByteRangeInputStream bris = new TestByteRangeInputStream.ByteRangeInputStreamImpl(oMock, rMock);
        bris.seek(0);
        Assert.assertEquals("getPos wrong", 0, bris.getPos());
        bris.read();
        Assert.assertEquals("Initial call made incorrectly (offset check)", 0, bris.startPos);
        Assert.assertEquals("getPos should return 1 after reading one byte", 1, bris.getPos());
        Mockito.verify(oMock, Mockito.times(1)).connect(0, false);
        bris.read();
        Assert.assertEquals("getPos should return 2 after reading two bytes", 2, bris.getPos());
        // No additional connections should have been made (no seek)
        Mockito.verify(oMock, Mockito.times(1)).connect(0, false);
        rMock.setURL(new URL("http://resolvedurl/"));
        bris.seek(100);
        bris.read();
        Assert.assertEquals("Seek to 100 bytes made incorrectly (offset Check)", 100, bris.startPos);
        Assert.assertEquals("getPos should return 101 after reading one byte", 101, bris.getPos());
        Mockito.verify(rMock, Mockito.times(1)).connect(100, true);
        bris.seek(101);
        bris.read();
        // Seek to 101 should not result in another request
        Mockito.verify(rMock, Mockito.times(1)).connect(100, true);
        Mockito.verify(rMock, Mockito.times(0)).connect(101, true);
        bris.seek(2500);
        bris.read();
        Assert.assertEquals("Seek to 2500 bytes made incorrectly (offset Check)", 2500, bris.startPos);
        Mockito.doReturn(getMockConnection(null)).when(rMock).connect(ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean());
        bris.seek(500);
        try {
            bris.read();
            Assert.fail("Exception should be thrown when content-length is not given");
        } catch (IOException e) {
            Assert.assertTrue(("Incorrect response message: " + (e.getMessage())), e.getMessage().startsWith(((HttpHeaders.CONTENT_LENGTH) + " is missing: ")));
        }
        bris.close();
    }

    @Test
    public void testPropagatedClose() throws IOException {
        ByteRangeInputStream bris = Mockito.mock(ByteRangeInputStream.class, Mockito.CALLS_REAL_METHODS);
        InputStreamAndFileLength mockStream = new InputStreamAndFileLength(1L, Mockito.mock(InputStream.class));
        Mockito.doReturn(mockStream).when(bris).openInputStream(Mockito.anyLong());
        Whitebox.setInternalState(bris, "status", SEEK);
        int brisOpens = 0;
        int brisCloses = 0;
        int isCloses = 0;
        // first open, shouldn't close underlying stream
        bris.getInputStream();
        Mockito.verify(bris, Mockito.times((++brisOpens))).openInputStream(Mockito.anyLong());
        Mockito.verify(bris, Mockito.times(brisCloses)).close();
        Mockito.verify(mockStream.in, Mockito.times(isCloses)).close();
        // stream is open, shouldn't close underlying stream
        bris.getInputStream();
        Mockito.verify(bris, Mockito.times(brisOpens)).openInputStream(Mockito.anyLong());
        Mockito.verify(bris, Mockito.times(brisCloses)).close();
        Mockito.verify(mockStream.in, Mockito.times(isCloses)).close();
        // seek forces a reopen, should close underlying stream
        bris.seek(1);
        bris.getInputStream();
        Mockito.verify(bris, Mockito.times((++brisOpens))).openInputStream(Mockito.anyLong());
        Mockito.verify(bris, Mockito.times(brisCloses)).close();
        Mockito.verify(mockStream.in, Mockito.times((++isCloses))).close();
        // verify that the underlying stream isn't closed after a seek
        // ie. the state was correctly updated
        bris.getInputStream();
        Mockito.verify(bris, Mockito.times(brisOpens)).openInputStream(Mockito.anyLong());
        Mockito.verify(bris, Mockito.times(brisCloses)).close();
        Mockito.verify(mockStream.in, Mockito.times(isCloses)).close();
        // seeking to same location should be a no-op
        bris.seek(1);
        bris.getInputStream();
        Mockito.verify(bris, Mockito.times(brisOpens)).openInputStream(Mockito.anyLong());
        Mockito.verify(bris, Mockito.times(brisCloses)).close();
        Mockito.verify(mockStream.in, Mockito.times(isCloses)).close();
        // close should of course close
        bris.close();
        Mockito.verify(bris, Mockito.times((++brisCloses))).close();
        Mockito.verify(mockStream.in, Mockito.times((++isCloses))).close();
        // it's already closed, underlying stream should not close
        bris.close();
        Mockito.verify(bris, Mockito.times((++brisCloses))).close();
        Mockito.verify(mockStream.in, Mockito.times(isCloses)).close();
        // it's closed, don't reopen it
        boolean errored = false;
        try {
            bris.getInputStream();
        } catch (IOException e) {
            errored = true;
            Assert.assertEquals("Stream closed", e.getMessage());
        } finally {
            Assert.assertTrue("Read a closed steam", errored);
        }
        Mockito.verify(bris, Mockito.times(brisOpens)).openInputStream(Mockito.anyLong());
        Mockito.verify(bris, Mockito.times(brisCloses)).close();
        Mockito.verify(mockStream.in, Mockito.times(isCloses)).close();
    }

    @Test
    public void testAvailable() throws IOException {
        ByteRangeInputStream bris = Mockito.mock(ByteRangeInputStream.class, Mockito.CALLS_REAL_METHODS);
        InputStreamAndFileLength mockStream = new InputStreamAndFileLength(65535L, Mockito.mock(InputStream.class));
        Mockito.doReturn(mockStream).when(bris).openInputStream(Mockito.anyLong());
        Whitebox.setInternalState(bris, "status", SEEK);
        Assert.assertEquals("Before read or seek, available should be same as filelength", 65535, bris.available());
        Mockito.verify(bris, Mockito.times(1)).openInputStream(Mockito.anyLong());
        bris.seek(10);
        Assert.assertEquals("Seek 10 bytes, available should return filelength - 10", 65525, bris.available());
        // no more bytes available
        bris.seek(65535);
        Assert.assertEquals("Seek till end of file, available should return 0 bytes", 0, bris.available());
        // test reads, seek back to 0 and start reading
        bris.seek(0);
        bris.read();
        Assert.assertEquals("Read 1 byte, available must return  filelength - 1", 65534, bris.available());
        bris.read();
        Assert.assertEquals("Read another 1 byte, available must return  filelength - 2", 65533, bris.available());
        // seek and read
        bris.seek(100);
        bris.read();
        Assert.assertEquals("Seek to offset 100 and read 1 byte, available should return filelength - 101", 65434, bris.available());
        bris.close();
    }

    @Test
    public void testAvailableLengthNotKnown() throws IOException {
        ByteRangeInputStream bris = Mockito.mock(ByteRangeInputStream.class, Mockito.CALLS_REAL_METHODS);
        // Length is null for chunked transfer-encoding
        InputStreamAndFileLength mockStream = new InputStreamAndFileLength(null, Mockito.mock(InputStream.class));
        Mockito.doReturn(mockStream).when(bris).openInputStream(Mockito.anyLong());
        Whitebox.setInternalState(bris, "status", SEEK);
        Assert.assertEquals(Integer.MAX_VALUE, bris.available());
    }

    @Test
    public void testAvailableStreamClosed() throws IOException {
        ByteRangeInputStream bris = Mockito.mock(ByteRangeInputStream.class, Mockito.CALLS_REAL_METHODS);
        InputStreamAndFileLength mockStream = new InputStreamAndFileLength(null, Mockito.mock(InputStream.class));
        Mockito.doReturn(mockStream).when(bris).openInputStream(Mockito.anyLong());
        Whitebox.setInternalState(bris, "status", SEEK);
        bris.close();
        try {
            bris.available();
            Assert.fail("Exception should be thrown when stream is closed");
        } catch (IOException e) {
            Assert.assertTrue("Exception when stream is closed", e.getMessage().equals("Stream closed"));
        }
    }
}

