/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.data.jpa.repository.cdi;


import javax.enterprise.inject.spi.Bean;
import javax.persistence.EntityManager;
import org.junit.Test;


/**
 * Unit tests for {@link JpaRepositoryExtension}.
 *
 * @author Oliver Gierke
 */
public class JpaRepositoryExtensionUnitTests {
    Bean<EntityManager> em;

    Bean<EntityManager> alternativeEm;

    @Test
    public void registersEntityManager() {
        JpaRepositoryExtension extension = new JpaRepositoryExtension();
        extension.processBean(JpaRepositoryExtensionUnitTests.createEntityManagerBeanMock(em));
        JpaRepositoryExtensionUnitTests.assertEntityManagerRegistered(extension, em);
    }

    // DATAJPA-388
    @Test
    public void alternativeEntityManagerOverridesDefault() {
        JpaRepositoryExtension extension = new JpaRepositoryExtension();
        extension.processBean(JpaRepositoryExtensionUnitTests.createEntityManagerBeanMock(em));
        extension.processBean(JpaRepositoryExtensionUnitTests.createEntityManagerBeanMock(alternativeEm));
        JpaRepositoryExtensionUnitTests.assertEntityManagerRegistered(extension, alternativeEm);
    }

    // DATAJPA-388
    @Test
    public void alternativeEntityManagerDoesNotGetOverridden() {
        JpaRepositoryExtension extension = new JpaRepositoryExtension();
        extension.processBean(JpaRepositoryExtensionUnitTests.createEntityManagerBeanMock(alternativeEm));
        extension.processBean(JpaRepositoryExtensionUnitTests.createEntityManagerBeanMock(em));
        JpaRepositoryExtensionUnitTests.assertEntityManagerRegistered(extension, alternativeEm);
    }
}

