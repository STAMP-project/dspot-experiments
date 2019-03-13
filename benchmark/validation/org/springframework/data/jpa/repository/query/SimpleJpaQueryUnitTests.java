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


import JpaQueryFactory.INSTANCE;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.metamodel.Metamodel;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.sample.User;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.sample.UserRepository;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.expression.spel.standard.SpelExpressionParser;


/**
 * Unit test for {@link SimpleJpaQuery}.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Jens Schauder
 * @author Tom Hombergs
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class SimpleJpaQueryUnitTests {
    static final String USER_QUERY = "select u from User u";

    static final SpelExpressionParser PARSER = new SpelExpressionParser();

    private static final QueryMethodEvaluationContextProvider EVALUATION_CONTEXT_PROVIDER = QueryMethodEvaluationContextProvider.DEFAULT;

    JpaQueryMethod method;

    @Mock
    EntityManager em;

    @Mock
    EntityManagerFactory emf;

    @Mock
    QueryExtractor extractor;

    @Mock
    Query query;

    @Mock
    TypedQuery<Long> typedQuery;

    @Mock
    RepositoryMetadata metadata;

    @Mock
    ParameterBinder binder;

    @Mock
    Metamodel metamodel;

    ProjectionFactory factory = new SpelAwareProxyProjectionFactory();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void prefersDeclaredCountQueryOverCreatingOne() throws Exception {
        method = new JpaQueryMethod(SimpleJpaQueryUnitTests.class.getMethod("prefersDeclaredCountQueryOverCreatingOne"), metadata, factory, extractor);
        Mockito.when(em.createQuery("foo", Long.class)).thenReturn(typedQuery);
        SimpleJpaQuery jpaQuery = new SimpleJpaQuery(method, em, "select u from User u", SimpleJpaQueryUnitTests.EVALUATION_CONTEXT_PROVIDER, SimpleJpaQueryUnitTests.PARSER);
        assertThat(jpaQuery.createCountQuery(new Object[]{  })).isEqualTo(((javax.persistence.Query) (typedQuery)));
    }

    // DATAJPA-77
    @Test
    public void doesNotApplyPaginationToCountQuery() throws Exception {
        Mockito.when(em.createQuery(Mockito.anyString())).thenReturn(query);
        Method method = UserRepository.class.getMethod("findAllPaged", Pageable.class);
        JpaQueryMethod queryMethod = new JpaQueryMethod(method, metadata, factory, extractor);
        AbstractJpaQuery jpaQuery = new SimpleJpaQuery(queryMethod, em, "select u from User u", SimpleJpaQueryUnitTests.EVALUATION_CONTEXT_PROVIDER, SimpleJpaQueryUnitTests.PARSER);
        jpaQuery.createCountQuery(new Object[]{ PageRequest.of(1, 10) });
        Mockito.verify(query, Mockito.times(0)).setFirstResult(ArgumentMatchers.anyInt());
        Mockito.verify(query, Mockito.times(0)).setMaxResults(ArgumentMatchers.anyInt());
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void discoversNativeQuery() throws Exception {
        Method method = SimpleJpaQueryUnitTests.SampleRepository.class.getMethod("findNativeByLastname", String.class);
        JpaQueryMethod queryMethod = new JpaQueryMethod(method, metadata, factory, extractor);
        AbstractJpaQuery jpaQuery = INSTANCE.fromQueryAnnotation(queryMethod, em, SimpleJpaQueryUnitTests.EVALUATION_CONTEXT_PROVIDER);
        assertThat((jpaQuery instanceof NativeJpaQuery)).isTrue();
        Mockito.when(em.createNativeQuery(ArgumentMatchers.anyString(), ArgumentMatchers.eq(User.class))).thenReturn(query);
        Mockito.when(metadata.getReturnedDomainClass(method)).thenReturn(((Class) (User.class)));
        jpaQuery.createQuery(new Object[]{ "Matthews" });
        Mockito.verify(em).createNativeQuery("SELECT u FROM User u WHERE u.lastname = ?1", User.class);
    }

    // DATAJPA-554
    @Test(expected = InvalidJpaQueryMethodException.class)
    public void rejectsNativeQueryWithDynamicSort() throws Exception {
        Method method = SimpleJpaQueryUnitTests.SampleRepository.class.getMethod("findNativeByLastname", String.class, Sort.class);
        createJpaQuery(method);
    }

    // DATAJPA-352
    @Test
    @SuppressWarnings("unchecked")
    public void doesNotValidateCountQueryIfNotPagingMethod() throws Exception {
        Method method = SimpleJpaQueryUnitTests.SampleRepository.class.getMethod("findByAnnotatedQuery");
        Mockito.when(em.createQuery(Mockito.contains("count"))).thenThrow(IllegalArgumentException.class);
        createJpaQuery(method);
    }

    // DATAJPA-352
    @Test
    @SuppressWarnings("unchecked")
    public void validatesAndRejectsCountQueryIfPagingMethod() throws Exception {
        Method method = SimpleJpaQueryUnitTests.SampleRepository.class.getMethod("pageByAnnotatedQuery", Pageable.class);
        Mockito.when(em.createQuery(Mockito.contains("count"))).thenThrow(IllegalArgumentException.class);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Count");
        exception.expectMessage(method.getName());
        createJpaQuery(method);
    }

    @Test
    public void createsASimpleJpaQueryFromAnnotation() throws Exception {
        RepositoryQuery query = createJpaQuery(SimpleJpaQueryUnitTests.SampleRepository.class.getMethod("findByAnnotatedQuery"));
        assertThat((query instanceof SimpleJpaQuery)).isTrue();
    }

    @Test
    public void createsANativeJpaQueryFromAnnotation() throws Exception {
        RepositoryQuery query = createJpaQuery(SimpleJpaQueryUnitTests.SampleRepository.class.getMethod("findNativeByLastname", String.class));
        assertThat((query instanceof NativeJpaQuery)).isTrue();
    }

    // DATAJPA-757
    @Test
    public void createsNativeCountQuery() throws Exception {
        Mockito.when(em.createNativeQuery(ArgumentMatchers.anyString())).thenReturn(query);
        AbstractJpaQuery jpaQuery = createJpaQuery(UserRepository.class.getMethod("findUsersInNativeQueryWithPagination", Pageable.class));
        jpaQuery.doCreateCountQuery(new Object[]{ PageRequest.of(0, 10) });
        Mockito.verify(em).createNativeQuery(ArgumentMatchers.anyString());
    }

    // DATAJPA-885
    @Test
    public void projectsWithManuallyDeclaredQuery() throws Exception {
        AbstractJpaQuery jpaQuery = createJpaQuery(SimpleJpaQueryUnitTests.SampleRepository.class.getMethod("projectWithExplicitQuery"));
        jpaQuery.createQuery(new Object[0]);
        Mockito.verify(em, Mockito.times(0)).createQuery(ArgumentMatchers.anyString(), ArgumentMatchers.eq(Tuple.class));
        // Two times, first one is from the query validation
        Mockito.verify(em, Mockito.times(2)).createQuery(ArgumentMatchers.anyString());
    }

    // DATAJPA-1307
    @Test
    public void jdbcStyleParametersOnlyAllowedInNativeQueries() throws Exception {
        // just verifying that it doesn't throw an exception
        createJpaQuery(SimpleJpaQueryUnitTests.SampleRepository.class.getMethod("legalUseOfJdbcStyleParameters", String.class));
        Method illegalMethod = SimpleJpaQueryUnitTests.SampleRepository.class.getMethod("illegalUseOfJdbcStyleParameters", String.class);
        // 
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> createJpaQuery(illegalMethod));
    }

    // DATAJPA-1163
    @Test
    public void resolvesExpressionInCountQuery() throws Exception {
        Mockito.when(em.createQuery(Mockito.anyString())).thenReturn(query);
        Method method = SimpleJpaQueryUnitTests.SampleRepository.class.getMethod("findAllWithExpressionInCountQuery", Pageable.class);
        JpaQueryMethod queryMethod = new JpaQueryMethod(method, metadata, factory, extractor);
        AbstractJpaQuery jpaQuery = new SimpleJpaQuery(queryMethod, em, "select u from User u", SimpleJpaQueryUnitTests.EVALUATION_CONTEXT_PROVIDER, SimpleJpaQueryUnitTests.PARSER);
        jpaQuery.createCountQuery(new Object[]{ PageRequest.of(1, 10) });
        Mockito.verify(em).createQuery(ArgumentMatchers.eq("select u from User u"));
        Mockito.verify(em).createQuery(ArgumentMatchers.eq("select count(u.id) from User u"), ArgumentMatchers.eq(Long.class));
    }

    interface SampleRepository {
        @Query(value = "SELECT u FROM User u WHERE u.lastname = ?1", nativeQuery = true)
        List<User> findNativeByLastname(String lastname);

        @Query(value = "SELECT u FROM User u WHERE u.lastname = ?1", nativeQuery = true)
        List<User> findNativeByLastname(String lastname, Sort sort);

        @Query(value = "SELECT u FROM User u WHERE u.lastname = ?1", nativeQuery = true)
        List<User> findNativeByLastname(String lastname, Pageable pageable);

        @Query(value = "SELECT u FROM User u WHERE u.lastname = ?", nativeQuery = true)
        List<User> legalUseOfJdbcStyleParameters(String lastname);

        @Query("SELECT u FROM User u WHERE u.lastname = ?")
        List<User> illegalUseOfJdbcStyleParameters(String lastname);

        @Query(SimpleJpaQueryUnitTests.USER_QUERY)
        List<User> findByAnnotatedQuery();

        @Query(SimpleJpaQueryUnitTests.USER_QUERY)
        Page<User> pageByAnnotatedQuery(Pageable pageable);

        @Query("select u from User u")
        Collection<SimpleJpaQueryUnitTests.UserProjection> projectWithExplicitQuery();

        @Query(value = "select u from #{#entityName} u", countQuery = "select count(u.id) from #{#entityName} u")
        List<User> findAllWithExpressionInCountQuery(Pageable pageable);
    }

    interface UserProjection {}
}

