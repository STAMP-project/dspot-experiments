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
package org.springframework.data.jpa.repository.query;


import LockModeType.PESSIMISTIC_WRITE;
import java.util.List;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.QueryHint;
import javax.persistence.TypedQuery;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.data.jpa.domain.sample.User;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.jpa.support.EntityManagerTestUtils;
import org.springframework.data.repository.Repository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


/**
 * Integration test for {@link AbstractJpaQuery}.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:infrastructure.xml")
public class AbstractJpaQueryTests {
    @PersistenceContext
    EntityManager em;

    Query query;

    TypedQuery<Long> countQuery;

    // DATADOC-97
    @Test
    public void addsHintsToQueryObject() throws Exception {
        JpaQueryMethod queryMethod = getMethod("findByLastname", String.class);
        AbstractJpaQuery jpaQuery = new AbstractJpaQueryTests.DummyJpaQuery(queryMethod, em);
        Query result = jpaQuery.createQuery(new Object[]{ "Matthews" });
        Mockito.verify(result).setHint("foo", "bar");
        result = jpaQuery.createCountQuery(new Object[]{ "Matthews" });
        Mockito.verify(result).setHint("foo", "bar");
    }

    // DATAJPA-54
    @Test
    public void skipsHintsForCountQueryIfConfigured() throws Exception {
        JpaQueryMethod queryMethod = getMethod("findByFirstname", String.class);
        AbstractJpaQuery jpaQuery = new AbstractJpaQueryTests.DummyJpaQuery(queryMethod, em);
        Query result = jpaQuery.createQuery(new Object[]{ "Dave" });
        Mockito.verify(result).setHint("bar", "foo");
        result = jpaQuery.createCountQuery(new Object[]{ "Dave" });
        Mockito.verify(result, Mockito.never()).setHint("bar", "foo");
    }

    // DATAJPA-73
    @Test
    public void addsLockingModeToQueryObject() throws Exception {
        Mockito.when(query.setLockMode(ArgumentMatchers.any(LockModeType.class))).thenReturn(query);
        JpaQueryMethod queryMethod = getMethod("findOneLocked", Integer.class);
        AbstractJpaQuery jpaQuery = new AbstractJpaQueryTests.DummyJpaQuery(queryMethod, em);
        Query result = jpaQuery.createQuery(new Object[]{ Integer.valueOf(1) });
        Mockito.verify(result).setLockMode(PESSIMISTIC_WRITE);
    }

    // DATAJPA-466
    @Test
    @Transactional
    public void shouldAddEntityGraphHintForFetch() throws Exception {
        Assume.assumeTrue(EntityManagerTestUtils.currentEntityManagerIsAJpa21EntityManager(em));
        JpaQueryMethod queryMethod = getMethod("findAll");
        EntityGraph<?> entityGraph = em.getEntityGraph("User.overview");
        AbstractJpaQuery jpaQuery = new AbstractJpaQueryTests.DummyJpaQuery(queryMethod, em);
        Query result = jpaQuery.createQuery(new Object[0]);
        Mockito.verify(result).setHint("javax.persistence.fetchgraph", entityGraph);
    }

    // DATAJPA-466
    @Test
    @Transactional
    public void shouldAddEntityGraphHintForLoad() throws Exception {
        Assume.assumeTrue(EntityManagerTestUtils.currentEntityManagerIsAJpa21EntityManager(em));
        JpaQueryMethod queryMethod = getMethod("getById", Integer.class);
        EntityGraph<?> entityGraph = em.getEntityGraph("User.detail");
        AbstractJpaQuery jpaQuery = new AbstractJpaQueryTests.DummyJpaQuery(queryMethod, em);
        Query result = jpaQuery.createQuery(new Object[]{ 1 });
        Mockito.verify(result).setHint("javax.persistence.loadgraph", entityGraph);
    }

    interface SampleRepository extends Repository<User, Integer> {
        @QueryHints({ @QueryHint(name = "foo", value = "bar") })
        List<User> findByLastname(String lastname);

        @QueryHints(value = { @QueryHint(name = "bar", value = "foo") }, forCounting = false)
        List<User> findByFirstname(String firstname);

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("select u from User u where u.id = ?1")
        List<User> findOneLocked(Integer primaryKey);

        // DATAJPA-466
        @org.springframework.data.jpa.repository.EntityGraph(value = "User.detail", type = EntityGraphType.LOAD)
        User getById(Integer id);

        // DATAJPA-466
        @org.springframework.data.jpa.repository.EntityGraph("User.overview")
        List<User> findAll();
    }

    class DummyJpaQuery extends AbstractJpaQuery {
        public DummyJpaQuery(JpaQueryMethod method, EntityManager em) {
            super(method, em);
        }

        @Override
        protected Query doCreateQuery(Object[] values) {
            return query;
        }

        @Override
        protected TypedQuery<Long> doCreateCountQuery(Object[] values) {
            return ((TypedQuery<Long>) (countQuery));
        }
    }
}

