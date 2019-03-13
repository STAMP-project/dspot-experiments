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


import org.junit.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;


/**
 * Integration tests for {@link AutoConfigurationImportSelector}.
 *
 * @author Stephane Nicoll
 */
public class AutoConfigurationImportSelectorIntegrationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Test
    public void singleSelectorWithNoImports() {
        this.contextRunner.withUserConfiguration(AutoConfigurationImportSelectorIntegrationTests.NoConfig.class).run(( context) -> assertThat(getImportedConfigBeans(context)).isEmpty());
    }

    @Test
    public void singleSelector() {
        this.contextRunner.withUserConfiguration(AutoConfigurationImportSelectorIntegrationTests.SingleConfig.class).run(( context) -> assertThat(getImportedConfigBeans(context)).containsExactly("ConfigC"));
    }

    @Test
    public void multipleSelectorsShouldMergeAndSortCorrectly() {
        this.contextRunner.withUserConfiguration(AutoConfigurationImportSelectorIntegrationTests.Config.class, AutoConfigurationImportSelectorIntegrationTests.AnotherConfig.class).run(( context) -> assertThat(getImportedConfigBeans(context)).containsExactly("ConfigA", "ConfigB", "ConfigC", "ConfigD"));
    }

    @Test
    public void multipleSelectorsWithRedundantImportsShouldMergeAndSortCorrectly() {
        this.contextRunner.withUserConfiguration(AutoConfigurationImportSelectorIntegrationTests.SingleConfig.class, AutoConfigurationImportSelectorIntegrationTests.Config.class, AutoConfigurationImportSelectorIntegrationTests.AnotherConfig.class).run(( context) -> assertThat(getImportedConfigBeans(context)).containsExactly("ConfigA", "ConfigB", "ConfigC", "ConfigD"));
    }

    @ImportAutoConfiguration
    static class NoConfig {}

    @ImportAutoConfiguration(AutoConfigurationImportSelectorIntegrationTests.ConfigC.class)
    static class SingleConfig {}

    @ImportAutoConfiguration({ AutoConfigurationImportSelectorIntegrationTests.ConfigD.class, AutoConfigurationImportSelectorIntegrationTests.ConfigB.class })
    static class Config {}

    @ImportAutoConfiguration({ AutoConfigurationImportSelectorIntegrationTests.ConfigC.class, AutoConfigurationImportSelectorIntegrationTests.ConfigA.class })
    static class AnotherConfig {}

    @Configuration
    static class ConfigA {}

    @Configuration
    @AutoConfigureAfter(AutoConfigurationImportSelectorIntegrationTests.ConfigA.class)
    @AutoConfigureBefore(AutoConfigurationImportSelectorIntegrationTests.ConfigC.class)
    static class ConfigB {}

    @Configuration
    static class ConfigC {}

    @Configuration
    @AutoConfigureAfter(AutoConfigurationImportSelectorIntegrationTests.ConfigC.class)
    static class ConfigD {}
}

