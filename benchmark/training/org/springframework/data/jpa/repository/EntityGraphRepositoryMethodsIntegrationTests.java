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


import QUser.user.firstname;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUtil;
import org.assertj.core.api.SoftAssertions;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.sample.Role;
import org.springframework.data.jpa.domain.sample.User;
import org.springframework.data.jpa.repository.sample.RepositoryMethodsWithEntityGraphConfigRepository;
import org.springframework.data.jpa.support.EntityManagerTestUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


/**
 * Integration tests for RepositoryMethodsWithEntityGraphConfigJpaRepository.
 *
 * @author Thomas Darimont
 * @author Oliver Gierke
 * @author Jocelyn Ntakpe
 * @author Christoph Strobl
 * @author Jens Schauder
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:config/namespace-autoconfig-context.xml")
@Transactional
public class EntityGraphRepositoryMethodsIntegrationTests {
    @Autowired
    EntityManager em;

    @Autowired
    RepositoryMethodsWithEntityGraphConfigRepository repository;

    User tom;

    User ollie;

    User christoph;

    Role role;

    PersistenceUtil util = Persistence.getPersistenceUtil();

    // DATAJPA-612
    @Test
    public void shouldRespectConfiguredJpaEntityGraph() {
        Assume.assumeTrue(EntityManagerTestUtils.currentEntityManagerIsAJpa21EntityManager(em));
        em.flush();
        em.clear();
        List<User> result = repository.findAll();
        assertThat(result.size()).isEqualTo(3);
        assertThat(util.isLoaded(result.get(0), "roles")).isTrue();
        assertThat(result.get(0)).isEqualTo(tom);
    }

    // DATAJPA-689
    @Test
    public void shouldRespectConfiguredJpaEntityGraphInFindOne() {
        Assume.assumeTrue(EntityManagerTestUtils.currentEntityManagerIsAJpa21EntityManager(em));
        em.flush();
        em.clear();
        User user = repository.findById(tom.getId()).get();
        assertThat(user).isNotNull();
        // 
        // 
        assertThat(util.isLoaded(user, "colleagues")).describedAs("colleages should be fetched with 'user.detail' fetchgraph").isTrue();
    }

    // DATAJPA-696
    @Test
    public void shouldRespectInferFetchGraphFromMethodName() {
        Assume.assumeTrue(EntityManagerTestUtils.currentEntityManagerIsAJpa21EntityManager(em));
        em.flush();
        em.clear();
        User user = repository.getOneWithDefinedEntityGraphById(tom.getId());
        assertThat(user).isNotNull();
        // 
        // 
        assertThat(util.isLoaded(user, "colleagues")).describedAs("colleages should be fetched with 'user.detail' fetchgraph").isTrue();
    }

    // DATAJPA-696
    @Test
    public void shouldRespectDynamicFetchGraphForGetOneWithAttributeNamesById() {
        Assume.assumeTrue(EntityManagerTestUtils.currentEntityManagerIsAJpa21EntityManager(em));
        em.flush();
        em.clear();
        User user = repository.getOneWithAttributeNamesById(tom.getId());
        assertThat(user).isNotNull();
        // 
        // 
        assertThat(util.isLoaded(user, "colleagues")).describedAs("colleages should be fetched with 'user.detail' fetchgraph").isTrue();
        assertThat(util.isLoaded(user, "colleagues")).isTrue();
        SoftAssertions softly = new SoftAssertions();
        for (User colleague : user.getColleagues()) {
            softly.assertThat(util.isLoaded(colleague, "roles")).isTrue();
        }
        softly.assertAll();
    }

    // DATAJPA-790, DATAJPA-1087
    @Test
    public void shouldRespectConfiguredJpaEntityGraphWithPaginationAndQueryDslPredicates() {
        Assume.assumeTrue(EntityManagerTestUtils.currentEntityManagerIsAJpa21EntityManager(em));
        em.flush();
        em.clear();
        Page<User> page = repository.findAll(firstname.isNotNull(), PageRequest.of(0, 100));
        List<User> result = page.getContent();
        assertThat(result.size()).isEqualTo(3);
        assertThat(util.isLoaded(result.get(0), "roles")).isTrue();
        assertThat(result.get(0)).isEqualTo(tom);
    }

    // DATAJPA-1207
    @Test
    public void shouldRespectConfiguredJpaEntityGraphWithPaginationAndSpecification() {
        Assume.assumeTrue(EntityManagerTestUtils.currentEntityManagerIsAJpa21EntityManager(em));
        em.flush();
        em.clear();
        Page<User> page = // 
        // 
        // 
        repository.findAll(((Specification<User>) (this::firstNameIsNotNull)), PageRequest.of(0, 100));
        List<User> result = page.getContent();
        assertThat(result.size()).isEqualTo(3);
        assertThat(util.isLoaded(result.get(0), "roles")).isTrue();
        assertThat(result.get(0)).isEqualTo(tom);
    }

    // DATAJPA-1041
    @Test
    public void shouldRespectNamedEntitySubGraph() {
        Assume.assumeTrue(EntityManagerTestUtils.currentEntityManagerIsAJpa21EntityManager(em));
        em.flush();
        em.clear();
        User user = repository.findOneWithMultipleSubGraphsUsingNamedEntityGraphById(tom.getId());
        assertThat(user).isNotNull();
        SoftAssertions softly = new SoftAssertions();
        // 
        // 
        softly.assertThat(util.isLoaded(user, "colleagues")).describedAs("colleagues on root should have been fetched by named 'User.colleagues' subgraph  declaration").isTrue();
        for (User colleague : user.getColleagues()) {
            softly.assertThat(util.isLoaded(colleague, "colleagues")).isTrue();
            softly.assertThat(util.isLoaded(colleague, "roles")).isTrue();
        }
        softly.assertAll();
    }

    // DATAJPA-1041
    @Test
    public void shouldRespectMultipleSubGraphForSameAttributeWithDynamicFetchGraph() {
        Assume.assumeTrue(EntityManagerTestUtils.currentEntityManagerIsAJpa21EntityManager(em));
        em.flush();
        em.clear();
        User user = repository.findOneWithMultipleSubGraphsById(tom.getId());
        assertThat(user).isNotNull();
        SoftAssertions softly = new SoftAssertions();
        // 
        // 
        softly.assertThat(util.isLoaded(user, "colleagues")).describedAs("colleagues on root should have been fetched by dynamic subgraph declaration").isTrue();
        for (User colleague : user.getColleagues()) {
            softly.assertThat(util.isLoaded(colleague, "colleagues")).isTrue();
            softly.assertThat(util.isLoaded(colleague, "roles")).isTrue();
        }
        softly.assertAll();
    }

    // DATAJPA-1041, DATAJPA-1075
    @Test
    public void shouldCreateDynamicGraphWithMultipleLevelsOfSubgraphs() {
        Assume.assumeTrue(EntityManagerTestUtils.currentEntityManagerIsAJpa21EntityManager(em));
        em.flush();
        em.clear();
        User user = repository.findOneWithDeepGraphById(tom.getId());
        assertThat(user).isNotNull();
        SoftAssertions softly = new SoftAssertions();
        // 
        // 
        softly.assertThat(Persistence.getPersistenceUtil().isLoaded(user, "colleagues")).describedAs("Colleagues on root should have been fetched by dynamic subgraph declaration").isTrue();
        for (User colleague : user.getColleagues()) {
            softly.assertThat(Persistence.getPersistenceUtil().isLoaded(colleague, "colleagues")).isTrue();
            softly.assertThat(Persistence.getPersistenceUtil().isLoaded(colleague, "roles")).isTrue();
            for (User colleagueOfColleague : colleague.getColleagues()) {
                softly.assertThat(Persistence.getPersistenceUtil().isLoaded(colleagueOfColleague, "roles")).isTrue();
                softly.assertThat(Persistence.getPersistenceUtil().isLoaded(colleagueOfColleague, "colleagues")).isFalse();
            }
        }
        softly.assertAll();
    }
}

