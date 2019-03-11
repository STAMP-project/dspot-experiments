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
package org.apache.beam.runners.fnexecution.artifact;


import BeamFileSystemArtifactStagingService.MANIFEST;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import org.apache.beam.model.jobmanagement.v1.ArtifactApi.ArtifactMetadata;
import org.apache.beam.model.jobmanagement.v1.ArtifactRetrievalServiceGrpc.ArtifactRetrievalServiceBlockingStub;
import org.apache.beam.model.jobmanagement.v1.ArtifactRetrievalServiceGrpc.ArtifactRetrievalServiceStub;
import org.apache.beam.model.jobmanagement.v1.ArtifactStagingServiceGrpc.ArtifactStagingServiceBlockingStub;
import org.apache.beam.model.jobmanagement.v1.ArtifactStagingServiceGrpc.ArtifactStagingServiceStub;
import org.apache.beam.runners.fnexecution.GrpcFnServer;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Strings;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Maps;
import org.apache.beam.vendor.guava.v20_0.com.google.common.hash.Hashing;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link BeamFileSystemArtifactStagingService} and {@link BeamFileSystemArtifactRetrievalService}.
 */
@RunWith(JUnit4.class)
public class BeamFileSystemArtifactServicesTest {
    private static final int DATA_1KB = 1 << 10;

    private GrpcFnServer<BeamFileSystemArtifactStagingService> stagingServer;

    private BeamFileSystemArtifactStagingService stagingService;

    private GrpcFnServer<BeamFileSystemArtifactRetrievalService> retrievalServer;

    private BeamFileSystemArtifactRetrievalService retrievalService;

    private ArtifactStagingServiceStub stagingStub;

    private ArtifactStagingServiceBlockingStub stagingBlockingStub;

    private ArtifactRetrievalServiceStub retrievalStub;

    private ArtifactRetrievalServiceBlockingStub retrievalBlockingStub;

    private Path stagingDir;

    private Path originalDir;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void generateStagingSessionTokenTest() throws Exception {
        String basePath = stagingDir.toAbsolutePath().toString();
        String stagingToken = BeamFileSystemArtifactStagingService.generateStagingSessionToken("abc123", basePath);
        Assert.assertEquals((("{\"sessionId\":\"abc123\",\"basePath\":\"" + basePath) + "\"}"), stagingToken);
    }

    @Test
    public void putArtifactsSingleSmallFileTest() throws Exception {
        String fileName = "file1";
        String stagingSession = "123";
        String stagingSessionToken = BeamFileSystemArtifactStagingService.generateStagingSessionToken(stagingSession, stagingDir.toUri().getPath());
        Path srcFilePath = Paths.get(originalDir.toString(), fileName).toAbsolutePath();
        Files.write(srcFilePath, "some_test".getBytes(StandardCharsets.UTF_8));
        putArtifact(stagingSessionToken, srcFilePath.toString(), fileName);
        String stagingToken = commitManifest(stagingSessionToken, Collections.singletonList(ArtifactMetadata.newBuilder().setName(fileName).build()));
        Assert.assertEquals(Paths.get(stagingDir.toAbsolutePath().toString(), stagingSession, MANIFEST), Paths.get(stagingToken));
        assertFiles(Collections.singleton(fileName), stagingToken);
        checkCleanup(stagingSessionToken, stagingSession);
    }

    @Test
    public void putArtifactsMultipleFilesTest() throws Exception {
        String stagingSession = "123";
        Map<String, Integer> files = /* 100 kb */
        /* 10 kb */
        /* 1 kb */
        /* 1.5 kb */
        /* 1 kb */
        /* 500b */
        ImmutableMap.<String, Integer>builder().put("file5cb", ((BeamFileSystemArtifactServicesTest.DATA_1KB) / 2)).put("file1kb", BeamFileSystemArtifactServicesTest.DATA_1KB).put("file15cb", (((BeamFileSystemArtifactServicesTest.DATA_1KB) * 3) / 2)).put("nested/file1kb", BeamFileSystemArtifactServicesTest.DATA_1KB).put("file10kb", (10 * (BeamFileSystemArtifactServicesTest.DATA_1KB))).put("file100kb", (100 * (BeamFileSystemArtifactServicesTest.DATA_1KB))).build();
        Map<String, String> hashes = Maps.newHashMap();
        final String text = "abcdefghinklmop\n";
        files.forEach(( fileName, size) -> {
            Path filePath = Paths.get(originalDir.toString(), fileName).toAbsolutePath();
            try {
                Files.createDirectories(filePath.getParent());
                byte[] contents = Strings.repeat(text, Double.valueOf(Math.ceil(((size * 1.0) / (text.length())))).intValue()).getBytes(StandardCharsets.UTF_8);
                Files.write(filePath, contents);
                hashes.put(fileName, Hashing.sha256().hashBytes(contents).toString());
            } catch (IOException ignored) {
            }
        });
        String stagingSessionToken = BeamFileSystemArtifactStagingService.generateStagingSessionToken(stagingSession, stagingDir.toUri().getPath());
        List<ArtifactMetadata> metadata = new ArrayList<>();
        for (String fileName : files.keySet()) {
            putArtifact(stagingSessionToken, Paths.get(originalDir.toString(), fileName).toAbsolutePath().toString(), fileName);
            metadata.add(ArtifactMetadata.newBuilder().setName(fileName).setSha256(hashes.get(fileName)).build());
        }
        String retrievalToken = commitManifest(stagingSessionToken, metadata);
        Assert.assertEquals(Paths.get(stagingDir.toAbsolutePath().toString(), stagingSession, "MANIFEST").toString(), retrievalToken);
        assertFiles(files.keySet(), retrievalToken);
        checkCleanup(stagingSessionToken, stagingSession);
    }

    @Test
    public void putArtifactsMultipleFilesConcurrentlyTest() throws Exception {
        String stagingSession = "123";
        Map<String, Integer> files = /* 100 kb */
        /* 10 kb */
        /* 1 kb */
        /* 1.5 kb */
        /* 1 kb */
        /* 500b */
        ImmutableMap.<String, Integer>builder().put("file5cb", ((BeamFileSystemArtifactServicesTest.DATA_1KB) / 2)).put("file1kb", BeamFileSystemArtifactServicesTest.DATA_1KB).put("file15cb", (((BeamFileSystemArtifactServicesTest.DATA_1KB) * 3) / 2)).put("nested/file1kb", BeamFileSystemArtifactServicesTest.DATA_1KB).put("file10kb", (10 * (BeamFileSystemArtifactServicesTest.DATA_1KB))).put("file100kb", (100 * (BeamFileSystemArtifactServicesTest.DATA_1KB))).build();
        final String text = "abcdefghinklmop\n";
        files.forEach(( fileName, size) -> {
            Path filePath = Paths.get(originalDir.toString(), fileName).toAbsolutePath();
            try {
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, Strings.repeat(text, Double.valueOf(Math.ceil(((size * 1.0) / (text.length())))).intValue()).getBytes(StandardCharsets.UTF_8));
            } catch (IOException ignored) {
            }
        });
        String stagingSessionToken = BeamFileSystemArtifactStagingService.generateStagingSessionToken(stagingSession, stagingDir.toUri().getPath());
        List<ArtifactMetadata> metadata = Collections.synchronizedList(new ArrayList<>());
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        try {
            for (String fileName : files.keySet()) {
                executorService.execute(() -> {
                    try {
                        putArtifact(stagingSessionToken, Paths.get(originalDir.toString(), fileName).toAbsolutePath().toString(), fileName);
                    } catch (Exception e) {
                        Assert.fail(e.getMessage());
                    }
                    metadata.add(ArtifactMetadata.newBuilder().setName(fileName).build());
                });
            }
        } finally {
            executorService.shutdown();
            executorService.awaitTermination(2, TimeUnit.SECONDS);
        }
        String retrievalToken = commitManifest(stagingSessionToken, metadata);
        Assert.assertEquals(Paths.get(stagingDir.toAbsolutePath().toString(), stagingSession, "MANIFEST").toString(), retrievalToken);
        assertFiles(files.keySet(), retrievalToken);
        checkCleanup(stagingSessionToken, stagingSession);
    }

    @Test
    public void putArtifactsMultipleFilesConcurrentSessionsTest() throws Exception {
        String stagingSession1 = "123";
        String stagingSession2 = "abc";
        Map<String, Integer> files1 = /* 1.5 kb */
        /* 1 kb */
        /* 500b */
        ImmutableMap.<String, Integer>builder().put("file5cb", ((BeamFileSystemArtifactServicesTest.DATA_1KB) / 2)).put("file1kb", BeamFileSystemArtifactServicesTest.DATA_1KB).put("file15cb", (((BeamFileSystemArtifactServicesTest.DATA_1KB) * 3) / 2)).build();
        Map<String, Integer> files2 = /* 100 kb */
        /* 10 kb */
        /* 1 kb */
        ImmutableMap.<String, Integer>builder().put("nested/file1kb", BeamFileSystemArtifactServicesTest.DATA_1KB).put("file10kb", (10 * (BeamFileSystemArtifactServicesTest.DATA_1KB))).put("file100kb", (100 * (BeamFileSystemArtifactServicesTest.DATA_1KB))).build();
        final String text = "abcdefghinklmop\n";
        ImmutableMap.<String, Integer>builder().putAll(files1).putAll(files2).build().forEach(( fileName, size) -> {
            Path filePath = Paths.get(originalDir.toString(), fileName).toAbsolutePath();
            try {
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, Strings.repeat(text, Double.valueOf(Math.ceil(((size * 1.0) / (text.length())))).intValue()).getBytes(StandardCharsets.UTF_8));
            } catch ( ignored) {
            }
        });
        String stagingSessionToken1 = BeamFileSystemArtifactStagingService.generateStagingSessionToken(stagingSession1, stagingDir.toUri().getPath());
        String stagingSessionToken2 = BeamFileSystemArtifactStagingService.generateStagingSessionToken(stagingSession2, stagingDir.toUri().getPath());
        List<ArtifactMetadata> metadata1 = Collections.synchronizedList(new ArrayList<>());
        List<ArtifactMetadata> metadata2 = Collections.synchronizedList(new ArrayList<>());
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        try {
            Iterator<String> iterator1 = files1.keySet().iterator();
            Iterator<String> iterator2 = files2.keySet().iterator();
            while ((iterator1.hasNext()) && (iterator2.hasNext())) {
                String fileName1 = iterator1.next();
                String fileName2 = iterator2.next();
                executorService.execute(() -> {
                    try {
                        putArtifact(stagingSessionToken1, Paths.get(originalDir.toString(), fileName1).toAbsolutePath().toString(), fileName1);
                        putArtifact(stagingSessionToken2, Paths.get(originalDir.toString(), fileName2).toAbsolutePath().toString(), fileName2);
                    } catch (Exception e) {
                        Assert.fail(e.getMessage());
                    }
                    metadata1.add(ArtifactMetadata.newBuilder().setName(fileName1).build());
                    metadata2.add(ArtifactMetadata.newBuilder().setName(fileName2).build());
                });
            } 
        } finally {
            executorService.shutdown();
            executorService.awaitTermination(2, TimeUnit.SECONDS);
        }
        String retrievalToken1 = commitManifest(stagingSessionToken1, metadata1);
        String retrievalToken2 = commitManifest(stagingSessionToken2, metadata2);
        Assert.assertEquals(Paths.get(stagingDir.toAbsolutePath().toString(), stagingSession1, "MANIFEST").toString(), retrievalToken1);
        Assert.assertEquals(Paths.get(stagingDir.toAbsolutePath().toString(), stagingSession2, "MANIFEST").toString(), retrievalToken2);
        assertFiles(files1.keySet(), retrievalToken1);
        assertFiles(files2.keySet(), retrievalToken2);
        checkCleanup(stagingSessionToken1, stagingSession1);
        checkCleanup(stagingSessionToken2, stagingSession2);
    }
}

