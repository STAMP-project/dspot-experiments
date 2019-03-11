/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate;


import BlobHeadRequestHandler.Actions.PUT_BLOB_HEAD_CHUNK;
import EmptyTransportResponseHandler.INSTANCE_SAME;
import TransportResponse.Empty;
import io.crate.blob.BlobTransferTarget;
import io.crate.blob.DigestBlob;
import io.crate.blob.transfer.HeadChunkFileTooSmallException;
import io.crate.blob.transfer.PutHeadChunkRunnable;
import io.crate.test.integration.CrateUnitTest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.util.concurrent.EsExecutors;
import org.elasticsearch.transport.TransportFuture;
import org.elasticsearch.transport.TransportRequest;
import org.elasticsearch.transport.TransportRequestOptions;
import org.elasticsearch.transport.TransportResponse;
import org.elasticsearch.transport.TransportService;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class BlobHeadRequestHandlerTests extends CrateUnitTest {
    @Test
    public void testPutHeadChunkRunnableFileGrowth() throws Exception {
        File file = File.createTempFile("test", "");
        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(new byte[]{ 101 });
            UUID transferId = UUID.randomUUID();
            BlobTransferTarget blobTransferTarget = Mockito.mock(BlobTransferTarget.class);
            TransportService transportService = Mockito.mock(TransportService.class);
            DiscoveryNode discoveryNode = Mockito.mock(DiscoveryNode.class);
            DigestBlob digestBlob = Mockito.mock(DigestBlob.class);
            Mockito.when(digestBlob.file()).thenReturn(file);
            ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(EsExecutors.daemonThreadFactory("blob-head"));
            try {
                scheduledExecutor.schedule(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            outputStream.write(new byte[]{ 102, 103, 104, 105 });
                        } catch (IOException ex) {
                            // pass
                        }
                    }
                }, 800, TimeUnit.MILLISECONDS);
                PutHeadChunkRunnable runnable = new PutHeadChunkRunnable(digestBlob, 5, transportService, blobTransferTarget, discoveryNode, transferId);
                @SuppressWarnings("unchecked")
                TransportFuture<TransportResponse.Empty> result = Mockito.mock(TransportFuture.class);
                Mockito.when(transportService.submitRequest(ArgumentMatchers.eq(discoveryNode), ArgumentMatchers.eq(PUT_BLOB_HEAD_CHUNK), ArgumentMatchers.any(TransportRequest.class), ArgumentMatchers.any(TransportRequestOptions.class), ArgumentMatchers.eq(INSTANCE_SAME))).thenReturn(result);
                runnable.run();
                Mockito.verify(blobTransferTarget).putHeadChunkTransferFinished(transferId);
                Mockito.verify(transportService, Mockito.times(2)).submitRequest(ArgumentMatchers.eq(discoveryNode), ArgumentMatchers.eq(PUT_BLOB_HEAD_CHUNK), ArgumentMatchers.any(TransportRequest.class), ArgumentMatchers.any(TransportRequestOptions.class), ArgumentMatchers.eq(INSTANCE_SAME));
            } finally {
                scheduledExecutor.awaitTermination(1, TimeUnit.SECONDS);
                scheduledExecutor.shutdownNow();
            }
        }
    }

    @Test
    public void testPutHeadChunkRunnableFileDoesntGrow() throws Exception {
        // this test is rather slow, tune wait time in PutHeadChunkRunnable?
        expectedException.expect(HeadChunkFileTooSmallException.class);
        File file = File.createTempFile("test", "");
        File notExisting = new File("./does/not/exist");
        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(new byte[]{ 101 });
        }
        UUID transferId = UUID.randomUUID();
        BlobTransferTarget transferTarget = Mockito.mock(BlobTransferTarget.class);
        TransportService transportService = Mockito.mock(TransportService.class);
        DiscoveryNode discoveryNode = Mockito.mock(DiscoveryNode.class);
        DigestBlob digestBlob = Mockito.mock(DigestBlob.class);
        Mockito.when(digestBlob.file()).thenReturn(notExisting);
        Mockito.when(digestBlob.getContainerFile()).thenReturn(file);
        PutHeadChunkRunnable runnable = new PutHeadChunkRunnable(digestBlob, 5, transportService, transferTarget, discoveryNode, transferId);
        @SuppressWarnings("unchecked")
        TransportFuture<TransportResponse.Empty> result = Mockito.mock(TransportFuture.class);
        Mockito.when(transportService.submitRequest(ArgumentMatchers.eq(discoveryNode), ArgumentMatchers.eq(PUT_BLOB_HEAD_CHUNK), ArgumentMatchers.any(TransportRequest.class), ArgumentMatchers.any(TransportRequestOptions.class), ArgumentMatchers.eq(INSTANCE_SAME))).thenReturn(result);
        runnable.run();
        Mockito.verify(digestBlob).getContainerFile();
    }
}

