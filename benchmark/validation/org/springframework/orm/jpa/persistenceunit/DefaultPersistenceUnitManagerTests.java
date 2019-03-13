/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.orm.jpa.persistenceunit;


import org.junit.Test;
import org.springframework.context.index.CandidateComponentsTestClassLoader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.jpa.domain.Person;


/**
 * Tests for {@link DefaultPersistenceUnitManager}.
 *
 * @author Stephane Nicoll
 */
public class DefaultPersistenceUnitManagerTests {
    private final DefaultPersistenceUnitManager manager = new DefaultPersistenceUnitManager();

    @Test
    public void defaultDomainWithScan() {
        this.manager.setPackagesToScan("org.springframework.orm.jpa.domain");
        this.manager.setResourceLoader(new org.springframework.core.io.DefaultResourceLoader(CandidateComponentsTestClassLoader.disableIndex(getClass().getClassLoader())));
        testDefaultDomain();
    }

    @Test
    public void defaultDomainWithIndex() {
        this.manager.setPackagesToScan("org.springframework.orm.jpa.domain");
        this.manager.setResourceLoader(new org.springframework.core.io.DefaultResourceLoader(CandidateComponentsTestClassLoader.index(getClass().getClassLoader(), new ClassPathResource("spring.components", Person.class))));
        testDefaultDomain();
    }
}

