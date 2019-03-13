/**
 * This file is part of dependency-check-core.
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
 *
 * Copyright (c) 2018 Nicolas Henneaux. All Rights Reserved.
 */
package org.owasp.dependencycheck.data.artifactory;


import Settings.KEYS.ANALYZER_ARTIFACTORY_API_TOKEN;
import Settings.KEYS.ANALYZER_ARTIFACTORY_API_USERNAME;
import Settings.KEYS.ANALYZER_ARTIFACTORY_BEARER_TOKEN;
import Settings.KEYS.ANALYZER_ARTIFACTORY_URL;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.owasp.dependencycheck.data.nexus.MavenArtifact;
import org.owasp.dependencycheck.dependency.Dependency;
import org.owasp.dependencycheck.utils.Settings;


@Ignore
public class ArtifactorySearchIT {
    @Test
    public void testWithRealInstanceUsingBearerToken() throws IOException {
        // Given
        Dependency dependency = new Dependency();
        dependency.setSha1sum("c5b4c491aecb72e7c32a78da0b5c6b9cda8dee0f");
        dependency.setSha256sum("512b4bf6927f4864acc419b8c5109c23361c30ed1f5798170248d33040de068e");
        dependency.setMd5sum("2d1dd0fc21ee96bccfab4353d5379649");
        final Settings settings = new Settings();
        settings.setString(ANALYZER_ARTIFACTORY_URL, "https://artifactory.techno.ingenico.com/artifactory");
        // See org.owasp.dependencycheck.utils.Settings.KEYS.ANALYZER_ARTIFACTORY_BEARER_TOKEN .for how to generate a bearer token using the API
        settings.setString(ANALYZER_ARTIFACTORY_BEARER_TOKEN, "yourBearerToken");
        final ArtifactorySearch artifactorySearch = new ArtifactorySearch(settings);
        // When
        final List<MavenArtifact> mavenArtifacts = artifactorySearch.search(dependency);
        // Then
        Assert.assertEquals(1, mavenArtifacts.size());
        final MavenArtifact artifact = mavenArtifacts.get(0);
        Assert.assertEquals("com.google.code.gson", artifact.getGroupId());
        Assert.assertEquals("gson", artifact.getArtifactId());
        Assert.assertEquals("2.8.5", artifact.getVersion());
        Assert.assertEquals("https://artifactory.techno.ingenico.com/artifactory/repo1-cache/com/google/code/gson/gson/2.8.5/gson-2.8.5-sources.jar", artifact.getArtifactUrl());
        Assert.assertEquals("https://artifactory.techno.ingenico.com/artifactory/repo1-cache/com/google/code/gson/gson/2.8.5/gson-2.8.5.pom", artifact.getPomUrl());
    }

    @Test
    public void testWithRealInstanceAnonymous() throws IOException {
        // Given
        Dependency dependency = new Dependency();
        dependency.setSha1sum("c5b4c491aecb72e7c32a78da0b5c6b9cda8dee0f");
        final Settings settings = new Settings();
        settings.setString(ANALYZER_ARTIFACTORY_URL, "https://artifactory.techno.ingenico.com/artifactory");
        final ArtifactorySearch artifactorySearch = new ArtifactorySearch(settings);
        // When
        try {
            artifactorySearch.search(dependency);
            Assert.fail("No Match found, should throw an exception!");
        } catch (FileNotFoundException e) {
            // Then
            Assert.assertEquals("Artifact Dependency{ fileName='null', actualFilePath='null', filePath='null', packagePath='null'} not found in Artifactory", e.getMessage());
        }
    }

    @Test
    public void testWithRealInstanceWithUserToken() throws IOException {
        // Given
        Dependency dependency = new Dependency();
        dependency.setSha1sum("0695b63d702f505b9b916e02272e3b6381bade7f");
        dependency.setMd5sum("cde1963375c651f769d40c8023ab5876");
        final Settings settings = new Settings();
        settings.setString(ANALYZER_ARTIFACTORY_URL, "https://artifactory.techno.ingenico.com/artifactory");
        settings.setString(ANALYZER_ARTIFACTORY_API_USERNAME, "yourUserName");
        settings.setString(ANALYZER_ARTIFACTORY_API_TOKEN, "yourSecretApiToken");
        final ArtifactorySearch artifactorySearch = new ArtifactorySearch(settings);
        // When
        final List<MavenArtifact> mavenArtifacts = artifactorySearch.search(dependency);
        // Then
        Assert.assertEquals(1, mavenArtifacts.size());
        final MavenArtifact artifact = mavenArtifacts.get(0);
        Assert.assertEquals("com.google.code.gson", artifact.getGroupId());
        Assert.assertEquals("gson", artifact.getArtifactId());
        Assert.assertEquals("2.4", artifact.getVersion());
        Assert.assertEquals("https://artifactory.techno.ingenico.com/artifactory/gradle-libs-cache/com/google/code/gson/gson/2.4/gson-2.4.jar", artifact.getArtifactUrl());
        Assert.assertEquals("https://artifactory.techno.ingenico.com/artifactory/gradle-libs-cache/com/google/code/gson/gson/2.4/gson-2.4.pom", artifact.getPomUrl());
    }
}

