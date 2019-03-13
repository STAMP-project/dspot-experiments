/**
 * Copyright 2012-2019 the original author or authors.
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


import Key.CREATE_IF_NOT_FOUND;
import java.lang.reflect.Method;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.Metamodel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.sample.User;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;


/**
 * Unit tests for {@link JpaQueryLookupStrategy}.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Jens Schauder
 */
@RunWith(MockitoJUnitRunner.class)
public class JpaQueryLookupStrategyUnitTests {
    private static final QueryMethodEvaluationContextProvider EVALUATION_CONTEXT_PROVIDER = QueryMethodEvaluationContextProvider.DEFAULT;

    @Mock
    EntityManager em;

    @Mock
    EntityManagerFactory emf;

    @Mock
    QueryExtractor extractor;

    @Mock
    NamedQueries namedQueries;

    @Mock
    Metamodel metamodel;

    @Mock
    ProjectionFactory projectionFactory;

    // DATAJPA-226
    @Test
    public void invalidAnnotatedQueryCausesException() throws Exception {
        QueryLookupStrategy strategy = JpaQueryLookupStrategy.create(em, CREATE_IF_NOT_FOUND, extractor, JpaQueryLookupStrategyUnitTests.EVALUATION_CONTEXT_PROVIDER);
        Method method = JpaQueryLookupStrategyUnitTests.UserRepository.class.getMethod("findByFoo", String.class);
        RepositoryMetadata metadata = new DefaultRepositoryMetadata(JpaQueryLookupStrategyUnitTests.UserRepository.class);
        Throwable reference = new RuntimeException();
        Mockito.when(em.createQuery(ArgumentMatchers.anyString())).thenThrow(reference);
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> strategy.resolveQuery(method, metadata, projectionFactory, namedQueries)).withCause(reference);
    }

    // DATAJPA-554
    @Test
    public void sholdThrowMorePreciseExceptionIfTryingToUsePaginationInNativeQueries() throws Exception {
        QueryLookupStrategy strategy = JpaQueryLookupStrategy.create(em, CREATE_IF_NOT_FOUND, extractor, JpaQueryLookupStrategyUnitTests.EVALUATION_CONTEXT_PROVIDER);
        Method method = JpaQueryLookupStrategyUnitTests.UserRepository.class.getMethod("findByInvalidNativeQuery", String.class, Sort.class);
        RepositoryMetadata metadata = new DefaultRepositoryMetadata(JpaQueryLookupStrategyUnitTests.UserRepository.class);
        assertThatExceptionOfType(InvalidJpaQueryMethodException.class).isThrownBy(() -> strategy.resolveQuery(method, metadata, projectionFactory, namedQueries)).withMessageContaining("Cannot use native queries with dynamic sorting in method").withMessageContaining(method.toString());
    }

    interface UserRepository extends Repository<User, Long> {
        @Query("something absurd")
        User findByFoo(String foo);

        @Query(value = "select u.* from User u", nativeQuery = true)
        List<User> findByInvalidNativeQuery(String param, Sort sort);
    }
}

