/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.configurationprocessor;


import org.junit.Test;
import org.springframework.boot.configurationprocessor.metadata.ConfigurationMetadata;
import org.springframework.boot.configurationprocessor.metadata.Metadata;
import org.springframework.boot.configurationsample.endpoint.CamelCaseEndpoint;
import org.springframework.boot.configurationsample.endpoint.CustomPropertiesEndpoint;
import org.springframework.boot.configurationsample.endpoint.DisabledEndpoint;
import org.springframework.boot.configurationsample.endpoint.EnabledEndpoint;
import org.springframework.boot.configurationsample.endpoint.SimpleEndpoint;
import org.springframework.boot.configurationsample.endpoint.SpecificEndpoint;
import org.springframework.boot.configurationsample.endpoint.incremental.IncrementalEndpoint;


/**
 * Metadata generation tests for Actuator endpoints.
 *
 * @author Stephane Nicoll
 */
public class EndpointMetadataGenerationTests extends AbstractMetadataGenerationTests {
    @Test
    public void simpleEndpoint() {
        ConfigurationMetadata metadata = compile(SimpleEndpoint.class);
        assertThat(metadata).has(Metadata.withGroup("management.endpoint.simple").fromSource(SimpleEndpoint.class));
        assertThat(metadata).has(enabledFlag("simple", true));
        assertThat(metadata).has(cacheTtl("simple"));
        assertThat(metadata.getItems()).hasSize(3);
    }

    @Test
    public void disableEndpoint() {
        ConfigurationMetadata metadata = compile(DisabledEndpoint.class);
        assertThat(metadata).has(Metadata.withGroup("management.endpoint.disabled").fromSource(DisabledEndpoint.class));
        assertThat(metadata).has(enabledFlag("disabled", false));
        assertThat(metadata.getItems()).hasSize(2);
    }

    @Test
    public void enabledEndpoint() {
        ConfigurationMetadata metadata = compile(EnabledEndpoint.class);
        assertThat(metadata).has(Metadata.withGroup("management.endpoint.enabled").fromSource(EnabledEndpoint.class));
        assertThat(metadata).has(enabledFlag("enabled", true));
        assertThat(metadata.getItems()).hasSize(2);
    }

    @Test
    public void customPropertiesEndpoint() {
        ConfigurationMetadata metadata = compile(CustomPropertiesEndpoint.class);
        assertThat(metadata).has(Metadata.withGroup("management.endpoint.customprops").fromSource(CustomPropertiesEndpoint.class));
        assertThat(metadata).has(Metadata.withProperty("management.endpoint.customprops.name").ofType(String.class).withDefaultValue("test"));
        assertThat(metadata).has(enabledFlag("customprops", true));
        assertThat(metadata).has(cacheTtl("customprops"));
        assertThat(metadata.getItems()).hasSize(4);
    }

    @Test
    public void specificEndpoint() {
        ConfigurationMetadata metadata = compile(SpecificEndpoint.class);
        assertThat(metadata).has(Metadata.withGroup("management.endpoint.specific").fromSource(SpecificEndpoint.class));
        assertThat(metadata).has(enabledFlag("specific", true));
        assertThat(metadata).has(cacheTtl("specific"));
        assertThat(metadata.getItems()).hasSize(3);
    }

    @Test
    public void camelCaseEndpoint() {
        ConfigurationMetadata metadata = compile(CamelCaseEndpoint.class);
        assertThat(metadata).has(Metadata.withGroup("management.endpoint.pascal-case").fromSource(CamelCaseEndpoint.class));
        assertThat(metadata).has(enabledFlag("PascalCase", "pascal-case", true));
        assertThat(metadata.getItems()).hasSize(2);
    }

    @Test
    public void incrementalEndpointBuildChangeGeneralEnabledFlag() throws Exception {
        TestProject project = new TestProject(this.temporaryFolder, IncrementalEndpoint.class);
        ConfigurationMetadata metadata = project.fullBuild();
        assertThat(metadata).has(Metadata.withGroup("management.endpoint.incremental").fromSource(IncrementalEndpoint.class));
        assertThat(metadata).has(enabledFlag("incremental", true));
        assertThat(metadata).has(cacheTtl("incremental"));
        assertThat(metadata.getItems()).hasSize(3);
        project.replaceText(IncrementalEndpoint.class, "id = \"incremental\"", "id = \"incremental\", enableByDefault = false");
        metadata = project.incrementalBuild(IncrementalEndpoint.class);
        assertThat(metadata).has(Metadata.withGroup("management.endpoint.incremental").fromSource(IncrementalEndpoint.class));
        assertThat(metadata).has(enabledFlag("incremental", false));
        assertThat(metadata).has(cacheTtl("incremental"));
        assertThat(metadata.getItems()).hasSize(3);
    }

    @Test
    public void incrementalEndpointBuildChangeCacheFlag() throws Exception {
        TestProject project = new TestProject(this.temporaryFolder, IncrementalEndpoint.class);
        ConfigurationMetadata metadata = project.fullBuild();
        assertThat(metadata).has(Metadata.withGroup("management.endpoint.incremental").fromSource(IncrementalEndpoint.class));
        assertThat(metadata).has(enabledFlag("incremental", true));
        assertThat(metadata).has(cacheTtl("incremental"));
        assertThat(metadata.getItems()).hasSize(3);
        project.replaceText(IncrementalEndpoint.class, "@Nullable String param", "String param");
        metadata = project.incrementalBuild(IncrementalEndpoint.class);
        assertThat(metadata).has(Metadata.withGroup("management.endpoint.incremental").fromSource(IncrementalEndpoint.class));
        assertThat(metadata).has(enabledFlag("incremental", true));
        assertThat(metadata.getItems()).hasSize(2);
    }

    @Test
    public void incrementalEndpointBuildEnableSpecificEndpoint() throws Exception {
        TestProject project = new TestProject(this.temporaryFolder, SpecificEndpoint.class);
        ConfigurationMetadata metadata = project.fullBuild();
        assertThat(metadata).has(Metadata.withGroup("management.endpoint.specific").fromSource(SpecificEndpoint.class));
        assertThat(metadata).has(enabledFlag("specific", true));
        assertThat(metadata).has(cacheTtl("specific"));
        assertThat(metadata.getItems()).hasSize(3);
        project.replaceText(SpecificEndpoint.class, "enableByDefault = true", "enableByDefault = false");
        metadata = project.incrementalBuild(SpecificEndpoint.class);
        assertThat(metadata).has(Metadata.withGroup("management.endpoint.specific").fromSource(SpecificEndpoint.class));
        assertThat(metadata).has(enabledFlag("specific", false));
        assertThat(metadata).has(cacheTtl("specific"));
        assertThat(metadata.getItems()).hasSize(3);
    }
}

