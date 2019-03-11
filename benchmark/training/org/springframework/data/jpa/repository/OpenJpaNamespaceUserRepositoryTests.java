/**
 * Copyright 2008-2019 the original author or authors.
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
package org.springframework.data.jpa.repository;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


/**
 * Testcase to run {@link UserRepository} integration tests on top of OpenJPA.
 *
 * @author Oliver Gierke
 */
@ContextConfiguration("classpath:openjpa.xml")
public class OpenJpaNamespaceUserRepositoryTests extends NamespaceUserRepositoryTests {
    @PersistenceContext
    EntityManager em;

    @Test
    public void checkQueryValidationWithOpenJpa() {
        try {
            em.createQuery("something absurd");
            Assert.fail("Creating query did not validate it");
        } catch (Exception e) {
            // expected
        }
        try {
            em.createNamedQuery("not available");
            Assert.fail("Creating invalid named query did not validate it");
        } catch (Exception e) {
            // expected
        }
    }
}

