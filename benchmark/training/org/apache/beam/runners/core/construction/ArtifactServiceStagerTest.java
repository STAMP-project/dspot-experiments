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
package org.apache.beam.runners.core.construction;


import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.beam.model.jobmanagement.v1.ArtifactApi.ArtifactMetadata;
import org.apache.beam.runners.core.construction.ArtifactServiceStager.StagedFile;
import org.apache.beam.vendor.grpc.v1p13p1.io.grpc.Server;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.apache.beam.vendor.guava.v20_0.com.google.common.hash.Hashing;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ArtifactServiceStager}.
 */
@RunWith(JUnit4.class)
public class ArtifactServiceStagerTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private Server server;

    private InMemoryArtifactStagerService service;

    private ArtifactServiceStager stager;

    @Test
    public void testStage() throws Exception {
        String stagingSessionToken = "token";
        File file = temp.newFile();
        byte[] content = "foo-bar-baz".getBytes(StandardCharsets.UTF_8);
        String contentSha256 = Hashing.sha256().newHasher().putBytes(content).hash().toString();
        try (FileChannel contentChannel = new FileOutputStream(file).getChannel()) {
            contentChannel.write(ByteBuffer.wrap(content));
        }
        stager.stage(stagingSessionToken, Collections.singleton(StagedFile.of(file, file.getName())));
        Assert.assertThat(service.getStagedArtifacts().entrySet(), Matchers.hasSize(1));
        byte[] stagedContent = Iterables.getOnlyElement(service.getStagedArtifacts().values());
        Assert.assertThat(stagedContent, Matchers.equalTo(content));
        ArtifactMetadata staged = service.getManifest().getArtifact(0);
        Assert.assertThat(staged.getName(), Matchers.equalTo(file.getName()));
        String manifestSha256 = staged.getSha256();
        Assert.assertThat(contentSha256, Matchers.equalTo(manifestSha256));
        Assert.assertThat(service.getManifest().getArtifactCount(), Matchers.equalTo(1));
        Assert.assertThat(staged, Matchers.equalTo(Iterables.getOnlyElement(service.getStagedArtifacts().keySet())));
    }

    @Test
    public void testStagingMultipleFiles() throws Exception {
        String stagingSessionToken = "token";
        File file = temp.newFile();
        byte[] content = "foo-bar-baz".getBytes(StandardCharsets.UTF_8);
        try (FileChannel contentChannel = new FileOutputStream(file).getChannel()) {
            contentChannel.write(ByteBuffer.wrap(content));
        }
        File otherFile = temp.newFile();
        byte[] otherContent = "spam-ham-eggs".getBytes(StandardCharsets.UTF_8);
        try (FileChannel contentChannel = new FileOutputStream(otherFile).getChannel()) {
            contentChannel.write(ByteBuffer.wrap(otherContent));
        }
        File thirdFile = temp.newFile();
        byte[] thirdContent = "up, down, charm, top, bottom, strange".getBytes(StandardCharsets.UTF_8);
        try (FileChannel contentChannel = new FileOutputStream(thirdFile).getChannel()) {
            contentChannel.write(ByteBuffer.wrap(thirdContent));
        }
        stager.stage(stagingSessionToken, ImmutableList.of(StagedFile.of(file, file.getName()), StagedFile.of(otherFile, otherFile.getName()), StagedFile.of(thirdFile, thirdFile.getName())));
        Assert.assertThat(service.getManifest().getArtifactCount(), Matchers.equalTo(3));
        Assert.assertThat(service.getStagedArtifacts().entrySet(), Matchers.hasSize(3));
        Set<File> stagedFiles = new HashSet<>();
        for (byte[] staged : service.getStagedArtifacts().values()) {
            if (Arrays.equals(staged, content)) {
                stagedFiles.add(file);
            } else
                if (Arrays.equals(staged, otherContent)) {
                    stagedFiles.add(otherFile);
                } else
                    if (Arrays.equals(staged, thirdContent)) {
                        stagedFiles.add(thirdFile);
                    }


        }
        Assert.assertThat("All of the files contents should be staged", stagedFiles, Matchers.hasSize(3));
    }
}

