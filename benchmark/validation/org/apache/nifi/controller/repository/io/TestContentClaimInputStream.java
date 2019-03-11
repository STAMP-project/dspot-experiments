/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.controller.repository.io;


import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.nifi.controller.repository.ContentRepository;
import org.apache.nifi.controller.repository.claim.ContentClaim;
import org.apache.nifi.stream.io.StreamUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestContentClaimInputStream {
    private ContentRepository repo;

    private ContentClaim contentClaim;

    private AtomicBoolean closed = new AtomicBoolean();

    @Test
    public void testStreamCreatedFromRepository() throws IOException {
        final ContentClaimInputStream in = new ContentClaimInputStream(repo, contentClaim, 0L);
        final byte[] buff = new byte[5];
        StreamUtils.fillBuffer(in, buff);
        Mockito.verify(repo, Mockito.times(1)).read(contentClaim);
        Mockito.verifyNoMoreInteractions(repo);
        final String contentRead = new String(buff);
        Assert.assertEquals("hello", contentRead);
        Assert.assertEquals(5, in.getBytesConsumed());
        Assert.assertFalse(closed.get());
        // Ensure that underlying stream is closed
        in.close();
        Assert.assertTrue(closed.get());
    }

    @Test
    public void testThatContentIsSkipped() throws IOException {
        final ContentClaimInputStream in = new ContentClaimInputStream(repo, contentClaim, 3L);
        final byte[] buff = new byte[2];
        StreamUtils.fillBuffer(in, buff);
        Mockito.verify(repo, Mockito.times(1)).read(contentClaim);
        Mockito.verifyNoMoreInteractions(repo);
        final String contentRead = new String(buff);
        Assert.assertEquals("lo", contentRead);
        Assert.assertEquals(2, in.getBytesConsumed());
        Assert.assertFalse(closed.get());
        // Ensure that underlying stream is closed
        in.close();
        Assert.assertTrue(closed.get());
    }

    @Test
    public void testRereadEntireClaim() throws IOException {
        final ContentClaimInputStream in = new ContentClaimInputStream(repo, contentClaim, 0L);
        final byte[] buff = new byte[5];
        final int invocations = 10;
        for (int i = 0; i < invocations; i++) {
            in.mark(5);
            StreamUtils.fillBuffer(in, buff, true);
            final String contentRead = new String(buff);
            Assert.assertEquals("hello", contentRead);
            Assert.assertEquals((5 * (i + 1)), in.getBytesConsumed());
            Assert.assertEquals(5, in.getCurrentOffset());
            Assert.assertEquals((-1), in.read());
            in.reset();
        }
        Mockito.verify(repo, Mockito.times((invocations + 1))).read(contentClaim);// Will call reset() 'invocations' times plus the initial read

        Mockito.verifyNoMoreInteractions(repo);
        // Ensure that underlying stream is closed
        in.close();
        Assert.assertTrue(closed.get());
    }

    @Test
    public void testMultipleResetCallsAfterMark() throws IOException {
        final ContentClaimInputStream in = new ContentClaimInputStream(repo, contentClaim, 0L);
        final byte[] buff = new byte[5];
        final int invocations = 10;
        in.mark(5);
        for (int i = 0; i < invocations; i++) {
            StreamUtils.fillBuffer(in, buff, true);
            final String contentRead = new String(buff);
            Assert.assertEquals("hello", contentRead);
            Assert.assertEquals((5 * (i + 1)), in.getBytesConsumed());
            Assert.assertEquals(5, in.getCurrentOffset());
            Assert.assertEquals((-1), in.read());
            in.reset();
        }
        Mockito.verify(repo, Mockito.times((invocations + 1))).read(contentClaim);// Will call reset() 'invocations' times plus the initial read

        Mockito.verifyNoMoreInteractions(repo);
        // Ensure that underlying stream is closed
        in.close();
        Assert.assertTrue(closed.get());
    }

    @Test
    public void testRereadWithOffset() throws IOException {
        final ContentClaimInputStream in = new ContentClaimInputStream(repo, contentClaim, 3L);
        final byte[] buff = new byte[2];
        final int invocations = 10;
        for (int i = 0; i < invocations; i++) {
            in.mark(5);
            StreamUtils.fillBuffer(in, buff, true);
            final String contentRead = new String(buff);
            Assert.assertEquals("lo", contentRead);
            Assert.assertEquals((2 * (i + 1)), in.getBytesConsumed());
            Assert.assertEquals(5, in.getCurrentOffset());
            Assert.assertEquals((-1), in.read());
            in.reset();
        }
        Mockito.verify(repo, Mockito.times((invocations + 1))).read(contentClaim);// Will call reset() 'invocations' times plus the initial read

        Mockito.verifyNoMoreInteractions(repo);
        // Ensure that underlying stream is closed
        in.close();
        Assert.assertTrue(closed.get());
    }
}

