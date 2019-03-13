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
package org.springframework.boot.autoconfigure;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link ImportAutoConfiguration}.
 *
 * @author Phillip Webb
 */
public class ImportAutoConfigurationTests {
    @Test
    public void multipleAnnotationsShouldMergeCorrectly() {
        assertThat(getImportedConfigBeans(ImportAutoConfigurationTests.Config.class)).containsExactly("ConfigA", "ConfigB", "ConfigC", "ConfigD");
        assertThat(getImportedConfigBeans(ImportAutoConfigurationTests.AnotherConfig.class)).containsExactly("ConfigA", "ConfigB", "ConfigC", "ConfigD");
    }

    @Test
    public void classesAsAnAlias() {
        assertThat(getImportedConfigBeans(ImportAutoConfigurationTests.AnotherConfigUsingClasses.class)).containsExactly("ConfigA", "ConfigB", "ConfigC", "ConfigD");
    }

    @Test
    public void excluding() {
        assertThat(getImportedConfigBeans(ImportAutoConfigurationTests.ExcludingConfig.class)).containsExactly("ConfigA", "ConfigB", "ConfigD");
    }

    @Test
    public void excludeAppliedGlobally() {
        assertThat(getImportedConfigBeans(ImportAutoConfigurationTests.ExcludeDConfig.class, ImportAutoConfigurationTests.ImportADConfig.class)).containsExactly("ConfigA");
    }

    @Test
    public void excludeWithRedundancy() {
        assertThat(getImportedConfigBeans(ImportAutoConfigurationTests.ExcludeADConfig.class, ImportAutoConfigurationTests.ExcludeDConfig.class, ImportAutoConfigurationTests.ImportADConfig.class)).isEmpty();
    }

    @ImportAutoConfiguration({ ImportAutoConfigurationTests.ConfigD.class, ImportAutoConfigurationTests.ConfigB.class })
    @ImportAutoConfigurationTests.MetaImportAutoConfiguration
    static class Config {}

    @ImportAutoConfigurationTests.MetaImportAutoConfiguration
    @ImportAutoConfiguration({ ImportAutoConfigurationTests.ConfigB.class, ImportAutoConfigurationTests.ConfigD.class })
    static class AnotherConfig {}

    @ImportAutoConfigurationTests.MetaImportAutoConfiguration
    @ImportAutoConfiguration(classes = { ImportAutoConfigurationTests.ConfigB.class, ImportAutoConfigurationTests.ConfigD.class })
    static class AnotherConfigUsingClasses {}

    @ImportAutoConfiguration(classes = { ImportAutoConfigurationTests.ConfigD.class, ImportAutoConfigurationTests.ConfigB.class }, exclude = ImportAutoConfigurationTests.ConfigC.class)
    @ImportAutoConfigurationTests.MetaImportAutoConfiguration
    static class ExcludingConfig {}

    @ImportAutoConfiguration(classes = { ImportAutoConfigurationTests.ConfigA.class, ImportAutoConfigurationTests.ConfigD.class })
    static class ImportADConfig {}

    @ImportAutoConfiguration(exclude = { ImportAutoConfigurationTests.ConfigA.class, ImportAutoConfigurationTests.ConfigD.class })
    static class ExcludeADConfig {}

    @ImportAutoConfiguration(exclude = ImportAutoConfigurationTests.ConfigD.class)
    static class ExcludeDConfig {}

    @Retention(RetentionPolicy.RUNTIME)
    @ImportAutoConfiguration({ ImportAutoConfigurationTests.ConfigC.class, ImportAutoConfigurationTests.ConfigA.class })
    @interface MetaImportAutoConfiguration {}

    @Configuration
    static class ConfigA {}

    @Configuration
    @AutoConfigureAfter(ImportAutoConfigurationTests.ConfigA.class)
    static class ConfigB {}

    @Configuration
    @AutoConfigureAfter(ImportAutoConfigurationTests.ConfigB.class)
    static class ConfigC {}

    @Configuration
    @AutoConfigureAfter(ImportAutoConfigurationTests.ConfigC.class)
    static class ConfigD {}
}

