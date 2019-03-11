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
package org.apache.beam.runners.direct.portable.artifact;


import ArtifactRetrievalServiceGrpc.ArtifactRetrievalServiceStub;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.beam.model.jobmanagement.v1.ArtifactApi.ArtifactChunk;
import org.apache.beam.model.jobmanagement.v1.ArtifactApi.ArtifactMetadata;
import org.apache.beam.model.jobmanagement.v1.ArtifactApi.GetArtifactRequest;
import org.apache.beam.model.jobmanagement.v1.ArtifactApi.GetManifestRequest;
import org.apache.beam.model.jobmanagement.v1.ArtifactApi.GetManifestResponse;
import org.apache.beam.model.jobmanagement.v1.ArtifactApi.Manifest;
import org.apache.beam.runners.fnexecution.GrpcFnServer;
import org.apache.beam.runners.fnexecution.InProcessServerFactory;
import org.apache.beam.runners.fnexecution.ServerFactory;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link LocalFileSystemArtifactRetrievalService}.
 */
@RunWith(JUnit4.class)
public class LocalFileSystemArtifactRetrievalServiceTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private File root;

    private ServerFactory serverFactory = InProcessServerFactory.create();

    private GrpcFnServer<LocalFileSystemArtifactStagerService> stagerServer;

    private GrpcFnServer<LocalFileSystemArtifactRetrievalService> retrievalServer;

    private ArtifactRetrievalServiceStub retrievalStub;

    @Test
    public void retrieveManifest() throws Exception {
        Map<String, byte[]> artifacts = new HashMap<>();
        artifacts.put("foo", "bar, baz, quux".getBytes(StandardCharsets.UTF_8));
        artifacts.put("spam", new byte[]{ 127, -22, 5 });
        stageAndCreateRetrievalService(artifacts);
        final AtomicReference<Manifest> returned = new AtomicReference<>();
        final CountDownLatch completed = new CountDownLatch(1);
        retrievalStub.getManifest(GetManifestRequest.getDefaultInstance(), new org.apache.beam.vendor.grpc.v1p13p1.io.grpc.stub.StreamObserver<GetManifestResponse>() {
            @Override
            public void onNext(GetManifestResponse value) {
                returned.set(value.getManifest());
            }

            @Override
            public void onError(Throwable t) {
                completed.countDown();
            }

            @Override
            public void onCompleted() {
                completed.countDown();
            }
        });
        completed.await();
        Assert.assertThat(returned.get(), Matchers.not(Matchers.nullValue()));
        List<String> manifestArtifacts = new ArrayList<>();
        for (ArtifactMetadata artifactMetadata : returned.get().getArtifactList()) {
            manifestArtifacts.add(artifactMetadata.getName());
        }
        Assert.assertThat(manifestArtifacts, Matchers.containsInAnyOrder("foo", "spam"));
    }

    @Test
    public void retrieveArtifact() throws Exception {
        Map<String, byte[]> artifacts = new HashMap<>();
        byte[] fooContents = "bar, baz, quux".getBytes(StandardCharsets.UTF_8);
        artifacts.put("foo", fooContents);
        byte[] spamContents = new byte[]{ 127, -22, 5 };
        artifacts.put("spam", spamContents);
        stageAndCreateRetrievalService(artifacts);
        final CountDownLatch completed = new CountDownLatch(2);
        ByteArrayOutputStream returnedFooBytes = new ByteArrayOutputStream();
        retrievalStub.getArtifact(GetArtifactRequest.newBuilder().setName("foo").build(), new LocalFileSystemArtifactRetrievalServiceTest.MultimapChunkAppender(returnedFooBytes, completed));
        ByteArrayOutputStream returnedSpamBytes = new ByteArrayOutputStream();
        retrievalStub.getArtifact(GetArtifactRequest.newBuilder().setName("spam").build(), new LocalFileSystemArtifactRetrievalServiceTest.MultimapChunkAppender(returnedSpamBytes, completed));
        completed.await();
        Assert.assertArrayEquals(fooContents, returnedFooBytes.toByteArray());
        Assert.assertArrayEquals(spamContents, returnedSpamBytes.toByteArray());
    }

    @Test
    public void retrieveArtifactNotPresent() throws Exception {
        stageAndCreateRetrievalService(Collections.singletonMap("foo", "bar, baz, quux".getBytes(StandardCharsets.UTF_8)));
        final CountDownLatch completed = new CountDownLatch(1);
        final AtomicReference<Throwable> thrown = new AtomicReference<>();
        retrievalStub.getArtifact(GetArtifactRequest.newBuilder().setName("spam").build(), new org.apache.beam.vendor.grpc.v1p13p1.io.grpc.stub.StreamObserver<ArtifactChunk>() {
            @Override
            public void onNext(ArtifactChunk value) {
                Assert.fail((("Should never receive an " + (ArtifactChunk.class.getSimpleName())) + " for a nonexistent artifact"));
            }

            @Override
            public void onError(Throwable t) {
                thrown.set(t);
                completed.countDown();
            }

            @Override
            public void onCompleted() {
                completed.countDown();
            }
        });
        completed.await();
        Assert.assertThat(thrown.get(), Matchers.not(Matchers.nullValue()));
        Assert.assertThat(thrown.get().getMessage(), Matchers.containsString("No such artifact"));
        Assert.assertThat(thrown.get().getMessage(), Matchers.containsString("spam"));
    }

    private static class MultimapChunkAppender implements org.apache.beam.vendor.grpc.v1p13p1.io.grpc.stub.StreamObserver<ArtifactChunk> {
        private final ByteArrayOutputStream target;

        private final CountDownLatch completed;

        private MultimapChunkAppender(ByteArrayOutputStream target, CountDownLatch completed) {
            this.target = target;
            this.completed = completed;
        }

        @Override
        public void onNext(ArtifactChunk value) {
            try {
                target.write(value.getData().toByteArray());
            } catch (IOException e) {
                // This should never happen
                throw new AssertionError(e);
            }
        }

        @Override
        public void onError(Throwable t) {
            completed.countDown();
        }

        @Override
        public void onCompleted() {
            completed.countDown();
        }
    }
}

