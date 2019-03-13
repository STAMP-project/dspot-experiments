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
package org.apache.nifi.controller.queue.clustered;


import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.nifi.controller.repository.ContentNotFoundException;
import org.apache.nifi.controller.repository.ContentRepository;
import org.apache.nifi.controller.repository.FlowFileRecord;
import org.apache.nifi.controller.repository.claim.ContentClaim;
import org.apache.nifi.controller.repository.claim.ResourceClaim;
import org.apache.nifi.controller.repository.claim.ResourceClaimManager;
import org.apache.nifi.controller.repository.claim.StandardResourceClaimManager;
import org.apache.nifi.stream.io.StreamUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestContentRepositoryFlowFileAccess {
    @Test
    public void testInputStreamFromContentRepo() throws IOException {
        final ContentRepository contentRepo = Mockito.mock(ContentRepository.class);
        final ResourceClaimManager claimManager = new StandardResourceClaimManager();
        final ResourceClaim resourceClaim = new org.apache.nifi.controller.repository.claim.StandardResourceClaim(claimManager, "container", "section", "id", false);
        final ContentClaim contentClaim = new org.apache.nifi.controller.repository.claim.StandardContentClaim(resourceClaim, 5L);
        final FlowFileRecord flowFile = Mockito.mock(FlowFileRecord.class);
        Mockito.when(flowFile.getContentClaim()).thenReturn(contentClaim);
        Mockito.when(flowFile.getSize()).thenReturn(5L);
        final InputStream inputStream = new ByteArrayInputStream("hello".getBytes());
        Mockito.when(contentRepo.read(contentClaim)).thenReturn(inputStream);
        final ContentRepositoryFlowFileAccess flowAccess = new ContentRepositoryFlowFileAccess(contentRepo);
        final InputStream repoStream = flowAccess.read(flowFile);
        Mockito.verify(contentRepo, Mockito.times(1)).read(contentClaim);
        final byte[] buffer = new byte[5];
        StreamUtils.fillBuffer(repoStream, buffer);
        Assert.assertEquals((-1), repoStream.read());
        Assert.assertArrayEquals("hello".getBytes(), buffer);
    }

    @Test
    public void testContentNotFoundPropagated() throws IOException {
        final ContentRepository contentRepo = Mockito.mock(ContentRepository.class);
        final ResourceClaimManager claimManager = new StandardResourceClaimManager();
        final ResourceClaim resourceClaim = new org.apache.nifi.controller.repository.claim.StandardResourceClaim(claimManager, "container", "section", "id", false);
        final ContentClaim contentClaim = new org.apache.nifi.controller.repository.claim.StandardContentClaim(resourceClaim, 5L);
        final FlowFileRecord flowFile = Mockito.mock(FlowFileRecord.class);
        Mockito.when(flowFile.getContentClaim()).thenReturn(contentClaim);
        final ContentNotFoundException cnfe = new ContentNotFoundException(contentClaim);
        Mockito.when(contentRepo.read(contentClaim)).thenThrow(cnfe);
        final ContentRepositoryFlowFileAccess flowAccess = new ContentRepositoryFlowFileAccess(contentRepo);
        try {
            flowAccess.read(flowFile);
            Assert.fail("Expected ContentNotFoundException but it did not happen");
        } catch (final ContentNotFoundException thrown) {
            // expected
            thrown.getFlowFile().orElseThrow(() -> new AssertionError("Expected FlowFile to be provided"));
        }
    }

    @Test
    public void testEOFExceptionIfNotEnoughData() throws IOException {
        final ContentRepository contentRepo = Mockito.mock(ContentRepository.class);
        final ResourceClaimManager claimManager = new StandardResourceClaimManager();
        final ResourceClaim resourceClaim = new org.apache.nifi.controller.repository.claim.StandardResourceClaim(claimManager, "container", "section", "id", false);
        final ContentClaim contentClaim = new org.apache.nifi.controller.repository.claim.StandardContentClaim(resourceClaim, 5L);
        final FlowFileRecord flowFile = Mockito.mock(FlowFileRecord.class);
        Mockito.when(flowFile.getContentClaim()).thenReturn(contentClaim);
        Mockito.when(flowFile.getSize()).thenReturn(100L);
        final InputStream inputStream = new ByteArrayInputStream("hello".getBytes());
        Mockito.when(contentRepo.read(contentClaim)).thenReturn(inputStream);
        final ContentRepositoryFlowFileAccess flowAccess = new ContentRepositoryFlowFileAccess(contentRepo);
        final InputStream repoStream = flowAccess.read(flowFile);
        Mockito.verify(contentRepo, Mockito.times(1)).read(contentClaim);
        final byte[] buffer = new byte[5];
        StreamUtils.fillBuffer(repoStream, buffer);
        try {
            repoStream.read();
            Assert.fail("Expected EOFException because not enough bytes were in the InputStream for the FlowFile");
        } catch (final EOFException eof) {
            // expected
        }
    }
}

