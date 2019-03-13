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
package org.springframework.boot.autoconfigure.orm.jpa;


import AvailableSettings.IMPLICIT_NAMING_STRATEGY;
import AvailableSettings.PHYSICAL_NAMING_STRATEGY;
import AvailableSettings.SCANNER;
import AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link HibernateProperties}.
 *
 * @author Stephane Nicoll
 * @author Artsiom Yudovin
 */
public class HibernatePropertiesTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withUserConfiguration(HibernatePropertiesTests.TestConfiguration.class);

    @Mock
    private Supplier<String> ddlAutoSupplier;

    @Test
    public void noCustomNamingStrategy() {
        this.contextRunner.run(assertHibernateProperties(( hibernateProperties) -> {
            assertThat(hibernateProperties).doesNotContainKeys("hibernate.ejb.naming_strategy");
            assertThat(hibernateProperties).containsEntry(PHYSICAL_NAMING_STRATEGY, SpringPhysicalNamingStrategy.class.getName());
            assertThat(hibernateProperties).containsEntry(IMPLICIT_NAMING_STRATEGY, SpringImplicitNamingStrategy.class.getName());
        }));
    }

    @Test
    public void hibernate5CustomNamingStrategies() {
        this.contextRunner.withPropertyValues("spring.jpa.hibernate.naming.implicit-strategy:com.example.Implicit", "spring.jpa.hibernate.naming.physical-strategy:com.example.Physical").run(assertHibernateProperties(( hibernateProperties) -> {
            assertThat(hibernateProperties).contains(entry(IMPLICIT_NAMING_STRATEGY, "com.example.Implicit"), entry(PHYSICAL_NAMING_STRATEGY, "com.example.Physical"));
            assertThat(hibernateProperties).doesNotContainKeys("hibernate.ejb.naming_strategy");
        }));
    }

    @Test
    public void hibernate5CustomNamingStrategiesViaJpaProperties() {
        this.contextRunner.withPropertyValues("spring.jpa.properties.hibernate.implicit_naming_strategy:com.example.Implicit", "spring.jpa.properties.hibernate.physical_naming_strategy:com.example.Physical").run(assertHibernateProperties(( hibernateProperties) -> {
            // You can override them as we don't provide any default
            assertThat(hibernateProperties).contains(entry(IMPLICIT_NAMING_STRATEGY, "com.example.Implicit"), entry(PHYSICAL_NAMING_STRATEGY, "com.example.Physical"));
            assertThat(hibernateProperties).doesNotContainKeys("hibernate.ejb.naming_strategy");
        }));
    }

    @Test
    public void useNewIdGeneratorMappingsDefault() {
        this.contextRunner.run(assertHibernateProperties(( hibernateProperties) -> assertThat(hibernateProperties).containsEntry(USE_NEW_ID_GENERATOR_MAPPINGS, "true")));
    }

    @Test
    public void useNewIdGeneratorMappingsFalse() {
        this.contextRunner.withPropertyValues("spring.jpa.hibernate.use-new-id-generator-mappings:false").run(assertHibernateProperties(( hibernateProperties) -> assertThat(hibernateProperties).containsEntry(USE_NEW_ID_GENERATOR_MAPPINGS, "false")));
    }

    @Test
    public void scannerUsesDisabledScannerByDefault() {
        this.contextRunner.run(assertHibernateProperties(( hibernateProperties) -> assertThat(hibernateProperties).containsEntry(SCANNER, "org.hibernate.boot.archive.scan.internal.DisabledScanner")));
    }

    @Test
    public void scannerCanBeCustomized() {
        this.contextRunner.withPropertyValues("spring.jpa.properties.hibernate.archive.scanner:org.hibernate.boot.archive.scan.internal.StandardScanner").run(assertHibernateProperties(( hibernateProperties) -> assertThat(hibernateProperties).containsEntry(SCANNER, "org.hibernate.boot.archive.scan.internal.StandardScanner")));
    }

    @Test
    public void defaultDdlAutoIsNotInvokedIfPropertyIsSet() {
        this.contextRunner.withPropertyValues("spring.jpa.hibernate.ddl-auto=validate").run(assertDefaultDdlAutoNotInvoked("validate"));
    }

    @Test
    public void defaultDdlAutoIsNotInvokedIfHibernateSpecificPropertyIsSet() {
        this.contextRunner.withPropertyValues("spring.jpa.properties.hibernate.hbm2ddl.auto=create").run(assertDefaultDdlAutoNotInvoked("create"));
    }

    @Configuration
    @EnableConfigurationProperties({ JpaProperties.class, HibernateProperties.class })
    static class TestConfiguration {}
}

