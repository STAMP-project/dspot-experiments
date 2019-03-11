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
package org.apache.nifi.controller.repository.claim;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.nifi.controller.repository.FileSystemRepository;
import org.apache.nifi.stream.io.StreamUtils;
import org.apache.nifi.util.NiFiProperties;
import org.junit.Assert;
import org.junit.Test;


public class TestContentClaimWriteCache {
    private FileSystemRepository repository = null;

    private StandardResourceClaimManager claimManager = null;

    private final File rootFile = new File("target/testContentClaimWriteCache");

    private NiFiProperties nifiProperties;

    @Test
    public void testFlushWriteCorrectData() throws IOException {
        final ContentClaimWriteCache cache = new ContentClaimWriteCache(repository, 4);
        final ContentClaim claim1 = cache.getContentClaim();
        Assert.assertNotNull(claim1);
        final OutputStream out = cache.write(claim1);
        Assert.assertNotNull(out);
        out.write("hello".getBytes());
        out.write("good-bye".getBytes());
        cache.flush();
        Assert.assertEquals(13L, claim1.getLength());
        final InputStream in = repository.read(claim1);
        final byte[] buff = new byte[((int) (claim1.getLength()))];
        StreamUtils.fillBuffer(in, buff);
        Assert.assertArrayEquals("hellogood-bye".getBytes(), buff);
        final ContentClaim claim2 = cache.getContentClaim();
        final OutputStream out2 = cache.write(claim2);
        Assert.assertNotNull(out2);
        out2.write("good-day".getBytes());
        out2.write("hello".getBytes());
        cache.flush();
        Assert.assertEquals(13L, claim2.getLength());
        final InputStream in2 = repository.read(claim2);
        final byte[] buff2 = new byte[((int) (claim2.getLength()))];
        StreamUtils.fillBuffer(in2, buff2);
        Assert.assertArrayEquals("good-dayhello".getBytes(), buff2);
    }
}

