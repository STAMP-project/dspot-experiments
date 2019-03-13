/**
 * Copyright 2018 ThoughtWorks, Inc.
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
package com.thoughtworks.go.remote.work;


import com.thoughtworks.go.remote.work.artifact.PluggableArtifactMetadata;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class PluggableArtifactMetadataTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void shouldAddMetadataWhenMetadataAbsentForPlugin() {
        final PluggableArtifactMetadata pluggableArtifactMetadata = new PluggableArtifactMetadata();
        Assert.assertTrue(pluggableArtifactMetadata.getMetadataPerPlugin().isEmpty());
        pluggableArtifactMetadata.addMetadata("docker", "installer", Collections.singletonMap("image", "alpine"));
        Assert.assertThat(pluggableArtifactMetadata.getMetadataPerPlugin(), Matchers.hasEntry("docker", Collections.singletonMap("installer", Collections.singletonMap("image", "alpine"))));
    }

    @Test
    public void shouldAddMetadataWhenMetadataOfOtherArtifactIsAlreadyPresetForAPlugin() {
        final PluggableArtifactMetadata pluggableArtifactMetadata = new PluggableArtifactMetadata();
        Assert.assertTrue(pluggableArtifactMetadata.getMetadataPerPlugin().isEmpty());
        pluggableArtifactMetadata.addMetadata("docker", "centos", Collections.singletonMap("image", "centos"));
        pluggableArtifactMetadata.addMetadata("docker", "alpine", Collections.singletonMap("image", "alpine"));
        final Map<String, Map> docker = pluggableArtifactMetadata.getMetadataPerPlugin().get("docker");
        Assert.assertNotNull(docker);
        Assert.assertThat(docker, Matchers.hasEntry("centos", Collections.singletonMap("image", "centos")));
        Assert.assertThat(docker, Matchers.hasEntry("alpine", Collections.singletonMap("image", "alpine")));
    }

    @Test
    public void shouldWriteMetadataFile() throws IOException {
        final PluggableArtifactMetadata pluggableArtifactMetadata = new PluggableArtifactMetadata();
        pluggableArtifactMetadata.addMetadata("cd.go.docker-registry", "centos", Collections.singletonMap("image", "centos"));
        pluggableArtifactMetadata.addMetadata("cd.go.docker-registry", "alpine", Collections.singletonMap("image", "alpine"));
        final File workingDirectory = tempFolder.newFolder("pluggable-metadata-folder");
        pluggableArtifactMetadata.write(workingDirectory);
        final File jsonFile = new File(workingDirectory.listFiles()[0], "cd.go.docker-registry.json");
        final String fileContent = FileUtils.readFileToString(jsonFile, StandardCharsets.UTF_8);
        Assert.assertThat(fileContent, Matchers.is("{\"alpine\":{\"image\":\"alpine\"},\"centos\":{\"image\":\"centos\"}}"));
    }
}

