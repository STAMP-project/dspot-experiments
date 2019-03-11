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


import Settings.KEYS.ANALYZER_ARTIFACTORY_URL;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.data.nexus.MavenArtifact;
import org.owasp.dependencycheck.dependency.Dependency;
import org.owasp.dependencycheck.utils.Settings;


public class ArtifactorySearchTest extends BaseTest {
    private ArtifactorySearch searcher;

    private static String httpsProxyHostOrig;

    private static String httpsPortOrig;

    @Test
    public void shouldFailWhenHostUnknown() throws IOException {
        // Given
        Dependency dependency = new Dependency();
        dependency.setSha1sum("c5b4c491aecb72e7c32a78da0b5c6b9cda8dee0f");
        dependency.setSha256sum("512b4bf6927f4864acc419b8c5109c23361c30ed1f5798170248d33040de068e");
        dependency.setMd5sum("2d1dd0fc21ee96bccfab4353d5379649");
        final Settings settings = getSettings();
        settings.setString(ANALYZER_ARTIFACTORY_URL, "https://artifactory.techno.ingenico.com.non-existing/artifactory");
        final ArtifactorySearch artifactorySearch = new ArtifactorySearch(settings);
        // When
        try {
            artifactorySearch.search(dependency);
            Assert.fail();
        } catch (UnknownHostException exception) {
            // Then
            Assert.assertEquals("artifactory.techno.ingenico.com.non-existing", exception.getMessage());
        } catch (SocketTimeoutException exception) {
            // Then
            Assert.assertEquals("connect timed out", exception.getMessage());
        }
    }

    @Test
    public void shouldProcessCorrectlyArtifactoryAnswerWithoutSha256() throws IOException {
        // Given
        Dependency dependency = new Dependency();
        dependency.setSha1sum("2e66da15851f9f5b5079228f856c2f090ba98c38");
        dependency.setMd5sum("3dbee72667f107b4f76f2d5aa33c5687");
        final HttpURLConnection urlConnection = Mockito.mock(HttpURLConnection.class);
        final byte[] payload = ("{\n" + (((((((((((((((((((((((("  \"results\" : [ {\n" + "    \"repo\" : \"jcenter-cache\",\n") + "    \"path\" : \"/com/google/code/gson/gson/2.1/gson-2.1.jar\",\n") + "    \"created\" : \"2017-06-14T16:15:37.936+02:00\",\n") + "    \"createdBy\" : \"anonymous\",\n") + "    \"lastModified\" : \"2012-12-12T22:20:22.000+01:00\",\n") + "    \"modifiedBy\" : \"anonymous\",\n") + "    \"lastUpdated\" : \"2017-06-14T16:15:37.939+02:00\",\n") + "    \"properties\" : {\n") + "      \"artifactory.internal.etag\" : [ \"2e66da15851f9f5b5079228f856c2f090ba98c38\" ]\n") + "    },\n") + "    \"downloadUri\" : \"https://artifactory.techno.ingenico.com/artifactory/jcenter-cache/com/google/code/gson/gson/2.1/gson-2.1.jar\",\n") + "    \"remoteUrl\" : \"http://jcenter.bintray.com/com/google/code/gson/gson/2.1/gson-2.1.jar\",\n") + "    \"mimeType\" : \"application/java-archive\",\n") + "    \"size\" : \"180110\",\n") + "    \"checksums\" : {\n") + "      \"sha1\" : \"2e66da15851f9f5b5079228f856c2f090ba98c38\",\n") + "      \"md5\" : \"3dbee72667f107b4f76f2d5aa33c5687\"\n") + "    },\n") + "    \"originalChecksums\" : {\n") + "      \"sha1\" : \"2e66da15851f9f5b5079228f856c2f090ba98c38\"\n") + "    },\n") + "    \"uri\" : \"https://artifactory.techno.ingenico.com/artifactory/api/storage/jcenter-cache/com/google/code/gson/gson/2.1/gson-2.1.jar\"\n") + "  } ]\n") + "}")).getBytes(StandardCharsets.UTF_8);
        Mockito.when(urlConnection.getInputStream()).thenReturn(new ByteArrayInputStream(payload));
        // When
        final List<MavenArtifact> mavenArtifacts = searcher.processResponse(dependency, urlConnection);
        // Then
        Assert.assertEquals(1, mavenArtifacts.size());
        final MavenArtifact artifact = mavenArtifacts.get(0);
        Assert.assertEquals("com.google.code.gson", artifact.getGroupId());
        Assert.assertEquals("gson", artifact.getArtifactId());
        Assert.assertEquals("2.1", artifact.getVersion());
        Assert.assertEquals("https://artifactory.techno.ingenico.com/artifactory/jcenter-cache/com/google/code/gson/gson/2.1/gson-2.1.jar", artifact.getArtifactUrl());
        Assert.assertEquals("https://artifactory.techno.ingenico.com/artifactory/jcenter-cache/com/google/code/gson/gson/2.1/gson-2.1.pom", artifact.getPomUrl());
    }

    @Test
    public void shouldProcessCorrectlyArtifactoryAnswerWithMultipleMatches() throws IOException {
        // Given
        Dependency dependency = new Dependency();
        dependency.setSha1sum("94a9ce681a42d0352b3ad22659f67835e560d107");
        dependency.setMd5sum("03dcfdd88502505cc5a805a128bfdd8d");
        final HttpURLConnection urlConnection = Mockito.mock(HttpURLConnection.class);
        final byte[] payload = multipleMatchesPayload();
        Mockito.when(urlConnection.getInputStream()).thenReturn(new ByteArrayInputStream(payload));
        // When
        final List<MavenArtifact> mavenArtifacts = searcher.processResponse(dependency, urlConnection);
        // Then
        Assert.assertEquals(2, mavenArtifacts.size());
        final MavenArtifact artifact1 = mavenArtifacts.get(0);
        Assert.assertEquals("axis", artifact1.getGroupId());
        Assert.assertEquals("axis", artifact1.getArtifactId());
        Assert.assertEquals("1.4", artifact1.getVersion());
        Assert.assertEquals("https://artifactory.techno.ingenico.com/artifactory/gradle-libs-cache/axis/axis/1.4/axis-1.4.jar", artifact1.getArtifactUrl());
        Assert.assertEquals("https://artifactory.techno.ingenico.com/artifactory/gradle-libs-cache/axis/axis/1.4/axis-1.4.pom", artifact1.getPomUrl());
        final MavenArtifact artifact2 = mavenArtifacts.get(1);
        Assert.assertEquals("org.apache.axis", artifact2.getGroupId());
        Assert.assertEquals("axis", artifact2.getArtifactId());
        Assert.assertEquals("1.4", artifact2.getVersion());
        Assert.assertEquals("https://artifactory.techno.ingenico.com/artifactory/gradle-libs-cache/org/apache/axis/axis/1.4/axis-1.4.jar", artifact2.getArtifactUrl());
        Assert.assertEquals("https://artifactory.techno.ingenico.com/artifactory/gradle-libs-cache/org/apache/axis/axis/1.4/axis-1.4.pom", artifact2.getPomUrl());
    }

    @Test
    public void shouldHandleNoMatches() throws IOException {
        // Given
        Dependency dependency = new Dependency();
        dependency.setSha1sum("94a9ce681a42d0352b3ad22659f67835e560d108");
        final HttpURLConnection urlConnection = Mockito.mock(HttpURLConnection.class);
        final byte[] payload = ("{\n" + "  \"results\" : [ ]}").getBytes(StandardCharsets.UTF_8);
        Mockito.when(urlConnection.getInputStream()).thenReturn(new ByteArrayInputStream(payload));
        // When
        try {
            searcher.processResponse(dependency, urlConnection);
            Assert.fail("No Match found, should throw an exception!");
        } catch (FileNotFoundException e) {
            // Then
            Assert.assertEquals("Artifact Dependency{ fileName='null', actualFilePath='null', filePath='null', packagePath='null'} not found in Artifactory", e.getMessage());
        }
    }

    @Test
    public void shouldProcessCorrectlyArtifactoryAnswer() throws IOException {
        // Given
        Dependency dependency = new Dependency();
        dependency.setSha1sum("c5b4c491aecb72e7c32a78da0b5c6b9cda8dee0f");
        dependency.setSha256sum("512b4bf6927f4864acc419b8c5109c23361c30ed1f5798170248d33040de068e");
        dependency.setMd5sum("2d1dd0fc21ee96bccfab4353d5379649");
        final HttpURLConnection urlConnection = Mockito.mock(HttpURLConnection.class);
        final byte[] payload = payloadWithSha256().getBytes(StandardCharsets.UTF_8);
        Mockito.when(urlConnection.getInputStream()).thenReturn(new ByteArrayInputStream(payload));
        // When
        final List<MavenArtifact> mavenArtifacts = searcher.processResponse(dependency, urlConnection);
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
    public void shouldProcessCorrectlyArtifactoryAnswerMisMatchMd5() throws IOException {
        // Given
        Dependency dependency = new Dependency();
        dependency.setSha1sum("c5b4c491aecb72e7c32a78da0b5c6b9cda8dee0f");
        dependency.setSha256sum("512b4bf6927f4864acc419b8c5109c23361c30ed1f5798170248d33040de068e");
        dependency.setMd5sum("2d1dd0fc21ee96bccfab4353d5379640");
        final HttpURLConnection urlConnection = Mockito.mock(HttpURLConnection.class);
        final byte[] payload = payloadWithSha256().getBytes(StandardCharsets.UTF_8);
        Mockito.when(urlConnection.getInputStream()).thenReturn(new ByteArrayInputStream(payload));
        // When
        try {
            searcher.processResponse(dependency, urlConnection);
            Assert.fail("MD5 mismatching should throw an exception!");
        } catch (FileNotFoundException e) {
            // Then
            Assert.assertEquals("Artifact found by API is not matching the md5 of the artifact (repository hash is 2d1dd0fc21ee96bccfab4353d5379649 while actual is 2d1dd0fc21ee96bccfab4353d5379640) !", e.getMessage());
        }
    }

    @Test
    public void shouldProcessCorrectlyArtifactoryAnswerMisMatchSha1() throws IOException {
        // Given
        Dependency dependency = new Dependency();
        dependency.setSha1sum("c5b4c491aecb72e7c32a78da0b5c6b9cda8dee0e");
        dependency.setSha256sum("512b4bf6927f4864acc419b8c5109c23361c30ed1f5798170248d33040de068e");
        dependency.setMd5sum("2d1dd0fc21ee96bccfab4353d5379649");
        final HttpURLConnection urlConnection = Mockito.mock(HttpURLConnection.class);
        final byte[] payload = payloadWithSha256().getBytes(StandardCharsets.UTF_8);
        Mockito.when(urlConnection.getInputStream()).thenReturn(new ByteArrayInputStream(payload));
        // When
        try {
            searcher.processResponse(dependency, urlConnection);
            Assert.fail("SHA1 mismatching should throw an exception!");
        } catch (FileNotFoundException e) {
            // Then
            Assert.assertEquals("Artifact found by API is not matching the SHA1 of the artifact (repository hash is c5b4c491aecb72e7c32a78da0b5c6b9cda8dee0f while actual is c5b4c491aecb72e7c32a78da0b5c6b9cda8dee0e) !", e.getMessage());
        }
    }

    @Test
    public void shouldProcessCorrectlyArtifactoryAnswerMisMatchSha256() throws IOException {
        // Given
        Dependency dependency = new Dependency();
        dependency.setSha1sum("c5b4c491aecb72e7c32a78da0b5c6b9cda8dee0f");
        dependency.setSha256sum("512b4bf6927f4864acc419b8c5109c23361c30ed1f5798170248d33040de068f");
        dependency.setMd5sum("2d1dd0fc21ee96bccfab4353d5379649");
        final HttpURLConnection urlConnection = Mockito.mock(HttpURLConnection.class);
        final byte[] payload = payloadWithSha256().getBytes(StandardCharsets.UTF_8);
        Mockito.when(urlConnection.getInputStream()).thenReturn(new ByteArrayInputStream(payload));
        // When
        try {
            searcher.processResponse(dependency, urlConnection);
            Assert.fail("SHA256 mismatching should throw an exception!");
        } catch (FileNotFoundException e) {
            // Then
            Assert.assertEquals("Artifact found by API is not matching the SHA-256 of the artifact (repository hash is 512b4bf6927f4864acc419b8c5109c23361c30ed1f5798170248d33040de068e while actual is 512b4bf6927f4864acc419b8c5109c23361c30ed1f5798170248d33040de068f) !", e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionWhenPatternCannotBeParsed() throws IOException {
        // Given
        Dependency dependency = new Dependency();
        dependency.setSha1sum("c5b4c491aecb72e7c32a78da0b5c6b9cda8dee0f");
        dependency.setSha256sum("512b4bf6927f4864acc419b8c5109c23361c30ed1f5798170248d33040de068e");
        dependency.setMd5sum("2d1dd0fc21ee96bccfab4353d5379649");
        final HttpURLConnection urlConnection = Mockito.mock(HttpURLConnection.class);
        final byte[] payload = payloadWithSha256().replace("/com/google/code/gson/gson/2.8.5/gson-2.8.5-sources.jar", "/2.8.5/gson-2.8.5-sources.jar").getBytes(StandardCharsets.UTF_8);
        Mockito.when(urlConnection.getInputStream()).thenReturn(new ByteArrayInputStream(payload));
        // When
        try {
            searcher.processResponse(dependency, urlConnection);
            Assert.fail("SHA256 mismatching should throw an exception!");
        } catch (IllegalStateException e) {
            // Then
            Assert.assertEquals("Cannot extract the Maven information from the apth retrieved in Artifactory /2.8.5/gson-2.8.5-sources.jar", e.getMessage());
        }
    }
}

