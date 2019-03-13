/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.cli.command.init;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;
import org.junit.Test;

import static ProjectGenerationRequest.DEFAULT_SERVICE_URL;


/**
 * Tests for {@link ProjectGenerationRequest}.
 *
 * @author Stephane Nicoll
 * @author Edd? Mel?ndez
 */
public class ProjectGenerationRequestTests {
    public static final Map<String, String> EMPTY_TAGS = Collections.emptyMap();

    private final ProjectGenerationRequest request = new ProjectGenerationRequest();

    @Test
    public void defaultSettings() {
        assertThat(this.request.generateUrl(ProjectGenerationRequestTests.createDefaultMetadata())).isEqualTo(ProjectGenerationRequestTests.createDefaultUrl("?type=test-type"));
    }

    @Test
    public void customServer() throws URISyntaxException {
        String customServerUrl = "http://foo:8080/initializr";
        this.request.setServiceUrl(customServerUrl);
        this.request.getDependencies().add("security");
        assertThat(this.request.generateUrl(ProjectGenerationRequestTests.createDefaultMetadata())).isEqualTo(new URI((customServerUrl + "/starter.zip?dependencies=security&type=test-type")));
    }

    @Test
    public void customBootVersion() {
        this.request.setBootVersion("1.2.0.RELEASE");
        assertThat(this.request.generateUrl(ProjectGenerationRequestTests.createDefaultMetadata())).isEqualTo(ProjectGenerationRequestTests.createDefaultUrl("?type=test-type&bootVersion=1.2.0.RELEASE"));
    }

    @Test
    public void singleDependency() {
        this.request.getDependencies().add("web");
        assertThat(this.request.generateUrl(ProjectGenerationRequestTests.createDefaultMetadata())).isEqualTo(ProjectGenerationRequestTests.createDefaultUrl("?dependencies=web&type=test-type"));
    }

    @Test
    public void multipleDependencies() {
        this.request.getDependencies().add("web");
        this.request.getDependencies().add("data-jpa");
        assertThat(this.request.generateUrl(ProjectGenerationRequestTests.createDefaultMetadata())).isEqualTo(ProjectGenerationRequestTests.createDefaultUrl("?dependencies=web%2Cdata-jpa&type=test-type"));
    }

    @Test
    public void customJavaVersion() {
        this.request.setJavaVersion("1.8");
        assertThat(this.request.generateUrl(ProjectGenerationRequestTests.createDefaultMetadata())).isEqualTo(ProjectGenerationRequestTests.createDefaultUrl("?type=test-type&javaVersion=1.8"));
    }

    @Test
    public void customPackageName() {
        this.request.setPackageName("demo.foo");
        assertThat(this.request.generateUrl(ProjectGenerationRequestTests.createDefaultMetadata())).isEqualTo(ProjectGenerationRequestTests.createDefaultUrl("?packageName=demo.foo&type=test-type"));
    }

    @Test
    public void customType() throws URISyntaxException {
        ProjectType projectType = new ProjectType("custom", "Custom Type", "/foo", true, ProjectGenerationRequestTests.EMPTY_TAGS);
        InitializrServiceMetadata metadata = new InitializrServiceMetadata(projectType);
        this.request.setType("custom");
        this.request.getDependencies().add("data-rest");
        assertThat(this.request.generateUrl(metadata)).isEqualTo(new URI(((DEFAULT_SERVICE_URL) + "/foo?dependencies=data-rest&type=custom")));
    }

    @Test
    public void customPackaging() {
        this.request.setPackaging("war");
        assertThat(this.request.generateUrl(ProjectGenerationRequestTests.createDefaultMetadata())).isEqualTo(ProjectGenerationRequestTests.createDefaultUrl("?type=test-type&packaging=war"));
    }

    @Test
    public void customLanguage() {
        this.request.setLanguage("groovy");
        assertThat(this.request.generateUrl(ProjectGenerationRequestTests.createDefaultMetadata())).isEqualTo(ProjectGenerationRequestTests.createDefaultUrl("?type=test-type&language=groovy"));
    }

    @Test
    public void customProjectInfo() {
        this.request.setGroupId("org.acme");
        this.request.setArtifactId("sample");
        this.request.setVersion("1.0.1-SNAPSHOT");
        this.request.setDescription("Spring Boot Test");
        assertThat(this.request.generateUrl(ProjectGenerationRequestTests.createDefaultMetadata())).isEqualTo(ProjectGenerationRequestTests.createDefaultUrl(("?groupId=org.acme&artifactId=sample&version=1.0.1-SNAPSHOT" + "&description=Spring+Boot+Test&type=test-type")));
    }

    @Test
    public void outputCustomizeArtifactId() {
        this.request.setOutput("my-project");
        assertThat(this.request.generateUrl(ProjectGenerationRequestTests.createDefaultMetadata())).isEqualTo(ProjectGenerationRequestTests.createDefaultUrl("?artifactId=my-project&type=test-type"));
    }

    @Test
    public void outputArchiveCustomizeArtifactId() {
        this.request.setOutput("my-project.zip");
        assertThat(this.request.generateUrl(ProjectGenerationRequestTests.createDefaultMetadata())).isEqualTo(ProjectGenerationRequestTests.createDefaultUrl("?artifactId=my-project&type=test-type"));
    }

    @Test
    public void outputArchiveWithDotsCustomizeArtifactId() {
        this.request.setOutput("my.nice.project.zip");
        assertThat(this.request.generateUrl(ProjectGenerationRequestTests.createDefaultMetadata())).isEqualTo(ProjectGenerationRequestTests.createDefaultUrl("?artifactId=my.nice.project&type=test-type"));
    }

    @Test
    public void outputDoesNotOverrideCustomArtifactId() {
        this.request.setOutput("my-project");
        this.request.setArtifactId("my-id");
        assertThat(this.request.generateUrl(ProjectGenerationRequestTests.createDefaultMetadata())).isEqualTo(ProjectGenerationRequestTests.createDefaultUrl("?artifactId=my-id&type=test-type"));
    }

    @Test
    public void buildNoMatch() throws Exception {
        InitializrServiceMetadata metadata = ProjectGenerationRequestTests.readMetadata();
        setBuildAndFormat("does-not-exist", null);
        assertThatExceptionOfType(ReportableException.class).isThrownBy(() -> this.request.generateUrl(metadata)).withMessageContaining("does-not-exist");
    }

    @Test
    public void buildMultipleMatch() throws Exception {
        InitializrServiceMetadata metadata = ProjectGenerationRequestTests.readMetadata("types-conflict");
        setBuildAndFormat("gradle", null);
        assertThatExceptionOfType(ReportableException.class).isThrownBy(() -> this.request.generateUrl(metadata)).withMessageContaining("gradle-project").withMessageContaining("gradle-project-2");
    }

    @Test
    public void buildOneMatch() throws Exception {
        InitializrServiceMetadata metadata = ProjectGenerationRequestTests.readMetadata();
        setBuildAndFormat("gradle", null);
        assertThat(this.request.generateUrl(metadata)).isEqualTo(ProjectGenerationRequestTests.createDefaultUrl("?type=gradle-project"));
    }

    @Test
    public void typeAndBuildAndFormat() throws Exception {
        InitializrServiceMetadata metadata = ProjectGenerationRequestTests.readMetadata();
        setBuildAndFormat("gradle", "project");
        this.request.setType("maven-build");
        assertThat(this.request.generateUrl(metadata)).isEqualTo(ProjectGenerationRequestTests.createUrl("/pom.xml?type=maven-build"));
    }

    @Test
    public void invalidType() {
        this.request.setType("does-not-exist");
        assertThatExceptionOfType(ReportableException.class).isThrownBy(() -> this.request.generateUrl(createDefaultMetadata()));
    }

    @Test
    public void noTypeAndNoDefault() throws Exception {
        assertThatExceptionOfType(ReportableException.class).isThrownBy(() -> this.request.generateUrl(readMetadata("types-conflict"))).withMessageContaining("no default is defined");
    }
}

