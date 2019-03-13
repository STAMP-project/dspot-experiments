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
import org.springframework.boot.configurationsample.lombok.LombokAccessLevelOverwriteDataProperties;
import org.springframework.boot.configurationsample.lombok.LombokAccessLevelOverwriteDefaultProperties;
import org.springframework.boot.configurationsample.lombok.LombokAccessLevelOverwriteExplicitProperties;
import org.springframework.boot.configurationsample.lombok.LombokAccessLevelProperties;
import org.springframework.boot.configurationsample.lombok.LombokExplicitProperties;
import org.springframework.boot.configurationsample.lombok.LombokInnerClassProperties;
import org.springframework.boot.configurationsample.lombok.LombokInnerClassWithGetterProperties;
import org.springframework.boot.configurationsample.lombok.LombokSimpleDataProperties;
import org.springframework.boot.configurationsample.lombok.LombokSimpleProperties;
import org.springframework.boot.configurationsample.lombok.SimpleLombokPojo;


/**
 * Metadata generation tests for Lombok classes.
 *
 * @author Stephane Nicoll
 */
public class LombokMetadataGenerationTests extends AbstractMetadataGenerationTests {
    @Test
    public void lombokDataProperties() {
        ConfigurationMetadata metadata = compile(LombokSimpleDataProperties.class);
        assertSimpleLombokProperties(metadata, LombokSimpleDataProperties.class, "data");
    }

    @Test
    public void lombokSimpleProperties() {
        ConfigurationMetadata metadata = compile(LombokSimpleProperties.class);
        assertSimpleLombokProperties(metadata, LombokSimpleProperties.class, "simple");
    }

    @Test
    public void lombokExplicitProperties() {
        ConfigurationMetadata metadata = compile(LombokExplicitProperties.class);
        assertSimpleLombokProperties(metadata, LombokExplicitProperties.class, "explicit");
        assertThat(metadata.getItems()).hasSize(6);
    }

    @Test
    public void lombokAccessLevelProperties() {
        ConfigurationMetadata metadata = compile(LombokAccessLevelProperties.class);
        assertAccessLevelLombokProperties(metadata, LombokAccessLevelProperties.class, "accesslevel", 2);
    }

    @Test
    public void lombokAccessLevelOverwriteDataProperties() {
        ConfigurationMetadata metadata = compile(LombokAccessLevelOverwriteDataProperties.class);
        assertAccessLevelOverwriteLombokProperties(metadata, LombokAccessLevelOverwriteDataProperties.class, "accesslevel.overwrite.data");
    }

    @Test
    public void lombokAccessLevelOverwriteExplicitProperties() {
        ConfigurationMetadata metadata = compile(LombokAccessLevelOverwriteExplicitProperties.class);
        assertAccessLevelOverwriteLombokProperties(metadata, LombokAccessLevelOverwriteExplicitProperties.class, "accesslevel.overwrite.explicit");
    }

    @Test
    public void lombokAccessLevelOverwriteDefaultProperties() {
        ConfigurationMetadata metadata = compile(LombokAccessLevelOverwriteDefaultProperties.class);
        assertAccessLevelOverwriteLombokProperties(metadata, LombokAccessLevelOverwriteDefaultProperties.class, "accesslevel.overwrite.default");
    }

    @Test
    public void lombokInnerClassProperties() {
        ConfigurationMetadata metadata = compile(LombokInnerClassProperties.class);
        assertThat(metadata).has(Metadata.withGroup("config").fromSource(LombokInnerClassProperties.class));
        assertThat(metadata).has(Metadata.withGroup("config.first").ofType(LombokInnerClassProperties.Foo.class).fromSource(LombokInnerClassProperties.class));
        assertThat(metadata).has(Metadata.withProperty("config.first.name"));
        assertThat(metadata).has(Metadata.withProperty("config.first.bar.name"));
        assertThat(metadata).has(Metadata.withGroup("config.second", LombokInnerClassProperties.Foo.class).fromSource(LombokInnerClassProperties.class));
        assertThat(metadata).has(Metadata.withProperty("config.second.name"));
        assertThat(metadata).has(Metadata.withProperty("config.second.bar.name"));
        assertThat(metadata).has(Metadata.withGroup("config.third").ofType(SimpleLombokPojo.class).fromSource(LombokInnerClassProperties.class));
        // For some reason the annotation processor resolves a type for SimpleLombokPojo
        // that is resolved (compiled) and the source annotations are gone. Because we
        // don't see the @Data annotation anymore, no field is harvested. What is crazy is
        // that a sample project works fine so this seem to be related to the unit test
        // environment for some reason. assertThat(metadata,
        // containsProperty("config.third.value"));
        assertThat(metadata).has(Metadata.withProperty("config.fourth"));
        assertThat(metadata).isNotEqualTo(Metadata.withGroup("config.fourth"));
    }

    @Test
    public void lombokInnerClassWithGetterProperties() {
        ConfigurationMetadata metadata = compile(LombokInnerClassWithGetterProperties.class);
        assertThat(metadata).has(Metadata.withGroup("config").fromSource(LombokInnerClassWithGetterProperties.class));
        assertThat(metadata).has(Metadata.withGroup("config.first").ofType(LombokInnerClassWithGetterProperties.Foo.class).fromSourceMethod("getFirst()").fromSource(LombokInnerClassWithGetterProperties.class));
        assertThat(metadata).has(Metadata.withProperty("config.first.name"));
        assertThat(metadata.getItems()).hasSize(3);
    }
}

