/**
 * Copyright 2014-2019 the original author or authors.
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
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.sample.UserRepository;
import org.springframework.data.jpa.support.EntityManagerTestUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


/**
 * Integration tests for JPA 2.1 stored procedure support.
 *
 * @author Thomas Darimont
 * @author Oliver Gierke
 * @since 1.6
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:application-context.xml")
@Transactional
public class UserRepositoryStoredProcedureTests {
    @Autowired
    UserRepository repository;

    @PersistenceContext
    EntityManager em;

    // DATAJPA-455
    @Test
    public void callProcedureWithInAndOutParameters() {
        Assume.assumeTrue(EntityManagerTestUtils.currentEntityManagerIsAJpa21EntityManager(em));
        Assert.assertThat(repository.plus1inout(1), CoreMatchers.is(2));
    }

    // DATAJPA-455
    @Test
    public void callProcedureExplicitNameWithInAndOutParameters() {
        Assume.assumeTrue(EntityManagerTestUtils.currentEntityManagerIsAJpa21EntityManager(em));
        Assert.assertThat(repository.explicitlyNamedPlus1inout(1), CoreMatchers.is(2));
    }

    // DATAJPA-455
    @Test
    public void entityAnnotatedCustomNamedProcedurePlus1IO() {
        Assume.assumeTrue(EntityManagerTestUtils.currentEntityManagerIsAJpa21EntityManager(em));
        Assert.assertThat(repository.entityAnnotatedCustomNamedProcedurePlus1IO(1), CoreMatchers.is(2));
    }
}

