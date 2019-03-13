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
package org.springframework.boot.autoconfigure.data.ldap;


import org.junit.Test;
import org.springframework.boot.autoconfigure.TestAutoConfigurationPackage;
import org.springframework.boot.autoconfigure.data.alt.ldap.PersonLdapRepository;
import org.springframework.boot.autoconfigure.data.empty.EmptyDataPackage;
import org.springframework.boot.autoconfigure.data.ldap.person.Person;
import org.springframework.boot.autoconfigure.data.ldap.person.PersonRepository;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.ldap.repository.config.EnableLdapRepositories;


/**
 * Tests for {@link LdapRepositoriesAutoConfiguration}
 *
 * @author Edd? Mel?ndez
 */
public class LdapRepositoriesAutoConfigurationTests {
    private AnnotationConfigApplicationContext context;

    @Test
    public void testDefaultRepositoryConfiguration() {
        load(LdapRepositoriesAutoConfigurationTests.TestConfiguration.class);
        assertThat(this.context.getBean(PersonRepository.class)).isNotNull();
    }

    @Test
    public void testNoRepositoryConfiguration() {
        load(LdapRepositoriesAutoConfigurationTests.EmptyConfiguration.class);
        assertThat(this.context.getBeanNamesForType(PersonRepository.class)).isEmpty();
    }

    @Test
    public void doesNotTriggerDefaultRepositoryDetectionIfCustomized() {
        load(LdapRepositoriesAutoConfigurationTests.CustomizedConfiguration.class);
        assertThat(this.context.getBean(PersonLdapRepository.class)).isNotNull();
    }

    @Configuration
    @TestAutoConfigurationPackage(Person.class)
    protected static class TestConfiguration {}

    @Configuration
    @TestAutoConfigurationPackage(EmptyDataPackage.class)
    protected static class EmptyConfiguration {}

    @Configuration
    @TestAutoConfigurationPackage(LdapRepositoriesAutoConfigurationTests.class)
    @EnableLdapRepositories(basePackageClasses = PersonLdapRepository.class)
    protected static class CustomizedConfiguration {}
}

