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


import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.controller.queue.FlowFileQueue;
import org.apache.nifi.controller.repository.FlowFileRecord;
import org.apache.nifi.controller.repository.SwapContents;
import org.apache.nifi.controller.repository.claim.ResourceClaimManager;
import org.apache.nifi.controller.repository.claim.StandardResourceClaimManager;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


@SuppressWarnings("deprecation")
public class TestSimpleSwapSerializerDeserializer {
    @Test
    public void testRoundTripSerializeDeserialize() throws IOException {
        final ResourceClaimManager resourceClaimManager = new StandardResourceClaimManager();
        final List<FlowFileRecord> toSwap = new ArrayList<>(10000);
        final Map<String, String> attrs = new HashMap<>();
        for (int i = 0; i < 10000; i++) {
            attrs.put("i", String.valueOf(i));
            final FlowFileRecord ff = new MockFlowFile(attrs, i, resourceClaimManager);
            toSwap.add(ff);
        }
        final String queueId = "87bb99fe-412c-49f6-a441-d1b0af4e20b4";
        final FlowFileQueue flowFileQueue = Mockito.mock(FlowFileQueue.class);
        Mockito.when(flowFileQueue.getIdentifier()).thenReturn(queueId);
        final String swapLocation = ("target/testRoundTrip-" + queueId) + ".swap";
        final File swapFile = new File(swapLocation);
        Files.deleteIfExists(swapFile.toPath());
        try {
            final SimpleSwapSerializer serializer = new SimpleSwapSerializer();
            try (final FileOutputStream fos = new FileOutputStream(swapFile)) {
                serializer.serializeFlowFiles(toSwap, flowFileQueue, swapLocation, fos);
            }
            final SimpleSwapDeserializer deserializer = new SimpleSwapDeserializer();
            final SwapContents swappedIn;
            try (final FileInputStream fis = new FileInputStream(swapFile);final DataInputStream dis = new DataInputStream(fis)) {
                swappedIn = deserializer.deserializeFlowFiles(dis, swapLocation, flowFileQueue, resourceClaimManager);
            }
            Assert.assertEquals(toSwap.size(), swappedIn.getFlowFiles().size());
            for (int i = 0; i < (toSwap.size()); i++) {
                final FlowFileRecord pre = toSwap.get(i);
                final FlowFileRecord post = swappedIn.getFlowFiles().get(i);
                Assert.assertEquals(pre.getSize(), post.getSize());
                Assert.assertEquals(pre.getAttributes(), post.getAttributes());
                Assert.assertEquals(pre.getSize(), post.getSize());
                Assert.assertEquals(pre.getId(), post.getId());
                Assert.assertEquals(pre.getContentClaim(), post.getContentClaim());
                Assert.assertEquals(pre.getContentClaimOffset(), post.getContentClaimOffset());
                Assert.assertEquals(pre.getEntryDate(), post.getEntryDate());
                Assert.assertEquals(pre.getLastQueueDate(), post.getLastQueueDate());
                Assert.assertEquals(pre.getLineageStartDate(), post.getLineageStartDate());
                Assert.assertEquals(pre.getPenaltyExpirationMillis(), post.getPenaltyExpirationMillis());
            }
        } finally {
            Files.deleteIfExists(swapFile.toPath());
        }
    }
}

