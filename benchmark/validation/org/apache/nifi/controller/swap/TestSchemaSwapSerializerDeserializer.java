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
package org.apache.nifi.controller.swap;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.nifi.controller.queue.FlowFileQueue;
import org.apache.nifi.controller.repository.FlowFileRecord;
import org.apache.nifi.controller.repository.SwapContents;
import org.apache.nifi.controller.repository.SwapSummary;
import org.apache.nifi.controller.repository.claim.ContentClaim;
import org.apache.nifi.controller.repository.claim.ResourceClaim;
import org.apache.nifi.controller.repository.claim.ResourceClaimManager;
import org.apache.nifi.controller.repository.claim.StandardResourceClaimManager;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestSchemaSwapSerializerDeserializer {
    @Test
    public void testRoundTripSerializeDeserializeSummary() throws IOException {
        final ResourceClaimManager resourceClaimManager = new StandardResourceClaimManager();
        final ResourceClaim firstResourceClaim = resourceClaimManager.newResourceClaim("container", "section", "id", true, false);
        resourceClaimManager.incrementClaimantCount(firstResourceClaim);
        final List<FlowFileRecord> toSwap = new ArrayList<>(10000);
        final Map<String, String> attrs = new HashMap<>();
        long size = 0L;
        final ContentClaim firstClaim = MockFlowFile.createContentClaim("id", resourceClaimManager);
        for (int i = 0; i < 10000; i++) {
            attrs.put("i", String.valueOf(i));
            final FlowFileRecord ff = (i < 2) ? new MockFlowFile(attrs, i, firstClaim) : new MockFlowFile(attrs, i, resourceClaimManager);
            toSwap.add(ff);
            size += i;
        }
        final FlowFileQueue flowFileQueue = Mockito.mock(FlowFileQueue.class);
        Mockito.when(flowFileQueue.getIdentifier()).thenReturn("87bb99fe-412c-49f6-a441-d1b0af4e20b4");
        final String swapLocation = "target/testRoundTrip.swap";
        final File swapFile = new File(swapLocation);
        Files.deleteIfExists(swapFile.toPath());
        final SwapSerializer serializer = new SchemaSwapSerializer();
        try (final FileOutputStream fos = new FileOutputStream(swapFile)) {
            serializer.serializeFlowFiles(toSwap, flowFileQueue, swapLocation, fos);
        }
        final SwapDeserializer deserializer = new SchemaSwapDeserializer();
        final SwapSummary swapSummary;
        try (final FileInputStream fis = new FileInputStream(swapFile);final DataInputStream dis = new DataInputStream(fis)) {
            swapSummary = deserializer.getSwapSummary(dis, swapLocation, resourceClaimManager);
        }
        Assert.assertEquals(10000, swapSummary.getQueueSize().getObjectCount());
        Assert.assertEquals(size, swapSummary.getQueueSize().getByteCount());
        Assert.assertEquals(9999, swapSummary.getMaxFlowFileId().intValue());
        final List<ResourceClaim> resourceClaims = swapSummary.getResourceClaims();
        Assert.assertEquals(10000, resourceClaims.size());
        Assert.assertFalse(resourceClaims.stream().anyMatch(( claim) -> claim == null));
        Assert.assertEquals(2, resourceClaims.stream().filter(( claim) -> claim.getId().equals("id")).collect(Collectors.counting()).intValue());
        final Set<ResourceClaim> uniqueClaims = new java.util.HashSet(resourceClaims);
        Assert.assertEquals(9999, uniqueClaims.size());
    }

    @Test
    public void testRoundTripSerializeDeserializeFullSwapFile() throws IOException, InterruptedException {
        final ResourceClaimManager resourceClaimManager = new StandardResourceClaimManager();
        final List<FlowFileRecord> toSwap = new ArrayList<>(10000);
        final Map<String, String> attrs = new HashMap<>();
        long size = 0L;
        for (int i = 0; i < 10000; i++) {
            attrs.put("i", String.valueOf(i));
            final FlowFileRecord ff = new MockFlowFile(attrs, i, resourceClaimManager);
            toSwap.add(ff);
            size += i;
        }
        final FlowFileQueue flowFileQueue = Mockito.mock(FlowFileQueue.class);
        Mockito.when(flowFileQueue.getIdentifier()).thenReturn("87bb99fe-412c-49f6-a441-d1b0af4e20b4");
        final String swapLocation = "target/testRoundTrip.swap";
        final File swapFile = new File(swapLocation);
        Files.deleteIfExists(swapFile.toPath());
        final SwapSerializer serializer = new SchemaSwapSerializer();
        try (final OutputStream fos = new FileOutputStream(swapFile);final OutputStream out = new BufferedOutputStream(fos)) {
            serializer.serializeFlowFiles(toSwap, flowFileQueue, swapLocation, out);
        }
        final SwapContents contents;
        final SwapDeserializer deserializer = new SchemaSwapDeserializer();
        try (final FileInputStream fis = new FileInputStream(swapFile);final InputStream bufferedIn = new BufferedInputStream(fis);final DataInputStream dis = new DataInputStream(bufferedIn)) {
            contents = deserializer.deserializeFlowFiles(dis, swapLocation, flowFileQueue, resourceClaimManager);
        }
        final SwapSummary swapSummary = contents.getSummary();
        Assert.assertEquals(10000, swapSummary.getQueueSize().getObjectCount());
        Assert.assertEquals(size, swapSummary.getQueueSize().getByteCount());
        Assert.assertEquals(9999, swapSummary.getMaxFlowFileId().intValue());
        Assert.assertEquals(10000, contents.getFlowFiles().size());
        int counter = 0;
        for (final FlowFileRecord flowFile : contents.getFlowFiles()) {
            final int i = counter++;
            Assert.assertEquals(String.valueOf(i), flowFile.getAttribute("i"));
            Assert.assertEquals(i, flowFile.getSize());
        }
    }
}

