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


import java.lang.reflect.Method;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.metamodel.Metamodel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryCreationException;


/**
 * Unit tests for {@link NamedQuery}.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 */
@RunWith(MockitoJUnitRunner.class)
public class NamedQueryUnitTests {
    @Mock
    RepositoryMetadata metadata;

    @Mock
    QueryExtractor extractor;

    @Mock
    EntityManager em;

    @Mock
    EntityManagerFactory emf;

    @Mock
    Metamodel metamodel;

    ProjectionFactory projectionFactory = new SpelAwareProxyProjectionFactory();

    Method method;

    @Test(expected = QueryCreationException.class)
    public void rejectsPersistenceProviderIfIncapableOfExtractingQueriesAndPagebleBeingUsed() {
        Mockito.when(extractor.canExtractQuery()).thenReturn(false);
        JpaQueryMethod queryMethod = new JpaQueryMethod(method, metadata, projectionFactory, extractor);
        Mockito.when(em.createNamedQuery(queryMethod.getNamedCountQueryName())).thenThrow(new IllegalArgumentException());
        NamedQuery.lookupFrom(queryMethod, em);
    }

    // DATAJPA-142
    @Test
    @SuppressWarnings("unchecked")
    public void doesNotRejectPersistenceProviderIfNamedCountQueryIsAvailable() {
        Mockito.when(extractor.canExtractQuery()).thenReturn(false);
        JpaQueryMethod queryMethod = new JpaQueryMethod(method, metadata, projectionFactory, extractor);
        TypedQuery<Long> countQuery = Mockito.mock(TypedQuery.class);
        Mockito.when(em.createNamedQuery(ArgumentMatchers.eq(queryMethod.getNamedCountQueryName()), ArgumentMatchers.eq(Long.class))).thenReturn(countQuery);
        NamedQuery query = ((NamedQuery) (NamedQuery.lookupFrom(queryMethod, em)));
        query.doCreateCountQuery(new Object[1]);
        Mockito.verify(em, Mockito.times(1)).createNamedQuery(queryMethod.getNamedCountQueryName(), Long.class);
        Mockito.verify(em, Mockito.never()).createQuery(ArgumentMatchers.any(String.class), ArgumentMatchers.eq(Long.class));
    }

    interface SampleRepository {
        Page<String> foo(Pageable pageable);
    }
}

