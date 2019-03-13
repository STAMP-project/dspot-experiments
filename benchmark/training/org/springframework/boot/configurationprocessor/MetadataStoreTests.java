/**
 * Copyright 2012-2017 the original author or authors.
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


import ConfigurationMetadataAnnotationProcessor.ADDITIONAL_METADATA_LOCATIONS_OPTION;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import javax.annotation.processing.ProcessingEnvironment;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 * Tests for {@link MetadataStore}.
 *
 * @author Andy Wilkinson
 */
public class MetadataStoreTests {
    @Rule
    public final TemporaryFolder temp = new TemporaryFolder();

    private final ProcessingEnvironment environment = Mockito.mock(ProcessingEnvironment.class);

    private final MetadataStore metadataStore = new MetadataStore(this.environment);

    @Test
    public void additionalMetadataIsLocatedInMavenBuild() throws IOException {
        File app = this.temp.newFolder("app");
        File classesLocation = new File(app, "target/classes");
        File metaInf = new File(classesLocation, "META-INF");
        metaInf.mkdirs();
        File additionalMetadata = new File(metaInf, "additional-spring-configuration-metadata.json");
        additionalMetadata.createNewFile();
        assertThat(this.metadataStore.locateAdditionalMetadataFile(new File(classesLocation, "META-INF/additional-spring-configuration-metadata.json"))).isEqualTo(additionalMetadata);
    }

    @Test
    public void additionalMetadataIsLocatedInGradle3Build() throws IOException {
        File app = this.temp.newFolder("app");
        File classesLocation = new File(app, "build/classes/main");
        File resourcesLocation = new File(app, "build/resources/main");
        File metaInf = new File(resourcesLocation, "META-INF");
        metaInf.mkdirs();
        File additionalMetadata = new File(metaInf, "additional-spring-configuration-metadata.json");
        additionalMetadata.createNewFile();
        assertThat(this.metadataStore.locateAdditionalMetadataFile(new File(classesLocation, "META-INF/additional-spring-configuration-metadata.json"))).isEqualTo(additionalMetadata);
    }

    @Test
    public void additionalMetadataIsLocatedInGradle4Build() throws IOException {
        File app = this.temp.newFolder("app");
        File classesLocation = new File(app, "build/classes/java/main");
        File resourcesLocation = new File(app, "build/resources/main");
        File metaInf = new File(resourcesLocation, "META-INF");
        metaInf.mkdirs();
        File additionalMetadata = new File(metaInf, "additional-spring-configuration-metadata.json");
        additionalMetadata.createNewFile();
        assertThat(this.metadataStore.locateAdditionalMetadataFile(new File(classesLocation, "META-INF/additional-spring-configuration-metadata.json"))).isEqualTo(additionalMetadata);
    }

    @Test
    public void additionalMetadataIsLocatedUsingLocationsOption() throws IOException {
        File app = this.temp.newFolder("app");
        File location = new File(app, "src/main/resources");
        File metaInf = new File(location, "META-INF");
        metaInf.mkdirs();
        File additionalMetadata = new File(metaInf, "additional-spring-configuration-metadata.json");
        additionalMetadata.createNewFile();
        BDDMockito.given(this.environment.getOptions()).willReturn(Collections.singletonMap(ADDITIONAL_METADATA_LOCATIONS_OPTION, location.getAbsolutePath()));
        assertThat(this.metadataStore.locateAdditionalMetadataFile(new File(app, "foo"))).isEqualTo(additionalMetadata);
    }
}

