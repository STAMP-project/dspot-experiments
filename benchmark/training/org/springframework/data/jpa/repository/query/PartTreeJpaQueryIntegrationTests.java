/**
 * Copyright 2011-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License
 * import org.springframework.aop.framework.Advised;
 * ");
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


import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.sample.User;
import org.springframework.data.jpa.provider.HibernateUtils;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Integration tests for {@link PartTreeJpaQuery}.
 *
 * @author Oliver Gierke
 * @author Mark Paluch
 * @author Michael Cramer
 * @author Jens Schauder
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:infrastructure.xml")
public class PartTreeJpaQueryIntegrationTests {
    private static String PROPERTY = "h.target." + (PartTreeJpaQueryIntegrationTests.getQueryProperty());

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @PersistenceContext
    EntityManager entityManager;

    PersistenceProvider provider;

    // DATADOC-90
    @Test
    public void test() throws Exception {
        JpaQueryMethod queryMethod = getQueryMethod("findByFirstname", String.class, Pageable.class);
        PartTreeJpaQuery jpaQuery = new PartTreeJpaQuery(queryMethod, entityManager, provider);
        jpaQuery.createQuery(new Object[]{ "Matthews", PageRequest.of(0, 1) });
        jpaQuery.createQuery(new Object[]{ "Matthews", PageRequest.of(0, 1) });
    }

    @Test
    public void cannotIgnoreCaseIfNotString() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Unable to ignore case of java.lang.Integer types, the property 'id' must reference a String");
        testIgnoreCase("findByIdIgnoringCase", 3);
    }

    @Test
    public void cannotIgnoreCaseIfNotStringUnlessIgnoringAll() throws Exception {
        testIgnoreCase("findByIdAllIgnoringCase", 3);
    }

    // DATAJPA-121
    @Test
    public void recreatesQueryIfNullValueIsGiven() throws Exception {
        JpaQueryMethod queryMethod = getQueryMethod("findByFirstname", String.class, Pageable.class);
        PartTreeJpaQuery jpaQuery = new PartTreeJpaQuery(queryMethod, entityManager, provider);
        Query query = jpaQuery.createQuery(new Object[]{ "Matthews", PageRequest.of(0, 1) });
        assertThat(HibernateUtils.getHibernateQuery(PartTreeJpaQueryIntegrationTests.getValue(query, PartTreeJpaQueryIntegrationTests.PROPERTY))).endsWith("firstname=:param0");
        query = jpaQuery.createQuery(new Object[]{ null, PageRequest.of(0, 1) });
        assertThat(HibernateUtils.getHibernateQuery(PartTreeJpaQueryIntegrationTests.getValue(query, PartTreeJpaQueryIntegrationTests.PROPERTY))).endsWith("firstname is null");
    }

    // DATAJPA-920
    @Test
    public void shouldLimitExistsProjectionQueries() throws Exception {
        JpaQueryMethod queryMethod = getQueryMethod("existsByFirstname", String.class);
        PartTreeJpaQuery jpaQuery = new PartTreeJpaQuery(queryMethod, entityManager, provider);
        Query query = jpaQuery.createQuery(new Object[]{ "Matthews" });
        assertThat(query.getMaxResults()).isEqualTo(1);
    }

    // DATAJPA-920
    @Test
    public void shouldSelectAliasedIdForExistsProjectionQueries() throws Exception {
        JpaQueryMethod queryMethod = getQueryMethod("existsByFirstname", String.class);
        PartTreeJpaQuery jpaQuery = new PartTreeJpaQuery(queryMethod, entityManager, provider);
        Query query = jpaQuery.createQuery(new Object[]{ "Matthews" });
        assertThat(HibernateUtils.getHibernateQuery(PartTreeJpaQueryIntegrationTests.getValue(query, PartTreeJpaQueryIntegrationTests.PROPERTY))).contains(".id from User as");
    }

    // DATAJPA-1074
    @Test
    public void isEmptyCollection() throws Exception {
        JpaQueryMethod queryMethod = getQueryMethod("findByRolesIsEmpty");
        PartTreeJpaQuery jpaQuery = new PartTreeJpaQuery(queryMethod, entityManager, provider);
        Query query = jpaQuery.createQuery(new Object[]{  });
        assertThat(HibernateUtils.getHibernateQuery(PartTreeJpaQueryIntegrationTests.getValue(query, PartTreeJpaQueryIntegrationTests.PROPERTY))).endsWith("roles is empty");
    }

    // DATAJPA-1074
    @Test
    public void isNotEmptyCollection() throws Exception {
        JpaQueryMethod queryMethod = getQueryMethod("findByRolesIsNotEmpty");
        PartTreeJpaQuery jpaQuery = new PartTreeJpaQuery(queryMethod, entityManager, provider);
        Query query = jpaQuery.createQuery(new Object[]{  });
        assertThat(HibernateUtils.getHibernateQuery(PartTreeJpaQueryIntegrationTests.getValue(query, PartTreeJpaQueryIntegrationTests.PROPERTY))).endsWith("roles is not empty");
    }

    // DATAJPA-1074
    @Test(expected = IllegalArgumentException.class)
    public void rejectsIsEmptyOnNonCollectionProperty() throws Exception {
        JpaQueryMethod method = getQueryMethod("findByFirstnameIsEmpty");
        AbstractJpaQuery jpaQuery = new PartTreeJpaQuery(method, entityManager, provider);
        jpaQuery.createQuery(new Object[]{ "Oliver" });
    }

    // DATAJPA-863
    @Test
    public void errorsDueToMismatchOfParametersContainNameOfMethodAndInterface() throws Exception {
        JpaQueryMethod method = getQueryMethod("findByFirstname");
        // the property we are looking for
        // the method being analyzed
        // 
        // 
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new PartTreeJpaQuery(method, entityManager, provider)).withMessageContaining("findByFirstname").withMessageContaining(" firstname ").withMessageContaining("UserRepository");// the repository

    }

    // DATAJPA-863
    @Test
    public void errorsDueToMissingPropertyContainNameOfMethodAndInterface() throws Exception {
        JpaQueryMethod method = getQueryMethod("findByNoSuchProperty", String.class);
        // the property we are looking for
        // the method being analyzed
        // 
        // 
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new PartTreeJpaQuery(method, entityManager, provider)).withMessageContaining("findByNoSuchProperty").withMessageContaining(" noSuchProperty ").withMessageContaining("UserRepository");// the repository

    }

    @SuppressWarnings("unused")
    interface UserRepository extends Repository<User, Long> {
        Page<User> findByFirstname(String firstname, Pageable pageable);

        User findByIdIgnoringCase(Integer id);

        User findByIdAllIgnoringCase(Integer id);

        boolean existsByFirstname(String firstname);

        List<User> findByCreatedAtAfter(@Temporal(TemporalType.TIMESTAMP)
        @Param("refDate")
        Date refDate);

        List<User> findByRolesIsEmpty();

        List<User> findByRolesIsNotEmpty();

        List<User> findByFirstnameIsEmpty();

        // Wrong number of parameters
        User findByFirstname();

        // Wrong property name
        User findByNoSuchProperty(String x);
    }
}

