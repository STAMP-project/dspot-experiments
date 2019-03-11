/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.domain;


import DefaultGoPublisher.PUBLISH_ERR;
import com.thoughtworks.go.work.DefaultGoPublisher;
import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;

import static ArtifactPlanType.unit;


public class TestArtifactPlanTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private DefaultGoPublisher mockArtifactPublisher;

    private File rootPath;

    @Test
    public void shouldNotThrowExceptionIfFolderNotFound() throws Exception {
        final MergedTestArtifactPlan compositeTestArtifact = new MergedTestArtifactPlan(new ArtifactPlan(unit, "some_random_path_that_does_not_exist", "testoutput"));
        compositeTestArtifact.publishBuiltInArtifacts(mockArtifactPublisher, rootPath);
        Mockito.verify(mockArtifactPublisher).taggedConsumeLineWithPrefix(PUBLISH_ERR, "The Directory target/test/some_random_path_that_does_not_exist specified as a test artifact was not found. Please check your configuration");
    }

    @Test
    public void shouldNotThrowExceptionIfUserSpecifiesNonFolderFileThatExistsAsSrc() throws Exception {
        temporaryFolder.newFolder("tempFolder");
        File nonFolderFileThatExists = temporaryFolder.newFile("tempFolder/nonFolderFileThatExists");
        final ArtifactPlan compositeTestArtifact = new ArtifactPlan(new ArtifactPlan(unit, nonFolderFileThatExists.getPath(), "testoutput"));
        compositeTestArtifact.publishBuiltInArtifacts(mockArtifactPublisher, rootPath);
        Mockito.doNothing().when(mockArtifactPublisher).upload(ArgumentMatchers.any(File.class), ArgumentMatchers.any(String.class));
    }

    @Test
    public void shouldSupportGlobPatternsInSourcePath() {
        ArtifactPlan artifactPlan = new ArtifactPlan(unit, "**/*/a.log", "logs");
        MergedTestArtifactPlan testArtifactPlan = new MergedTestArtifactPlan(artifactPlan);
        File first = new File("target/test/report/a.log");
        File second = new File("target/test/test/a/b/a.log");
        first.mkdirs();
        second.mkdirs();
        testArtifactPlan.publishBuiltInArtifacts(mockArtifactPublisher, rootPath);
        Mockito.verify(mockArtifactPublisher).upload(first, "logs/report");
        Mockito.verify(mockArtifactPublisher).upload(second, "logs/test/a/b");
        Mockito.verify(mockArtifactPublisher, new Times(2)).upload(ArgumentMatchers.any(File.class), ArgumentMatchers.eq("testoutput"));
    }
}

