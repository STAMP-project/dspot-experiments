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


import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.JpaQueryExecution.ModifyingExecution;
import org.springframework.data.jpa.repository.query.JpaQueryExecution.PagedExecution;
import org.springframework.data.repository.query.DefaultParameters;
import org.springframework.data.repository.query.Parameters;


/**
 * Unit test for {@link JpaQueryExecution}.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Mark Paluch
 * @author Nicolas Cirigliano
 * @author Jens Schauder
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class JpaQueryExecutionUnitTests {
    @Mock
    EntityManager em;

    @Mock
    AbstractStringBasedJpaQuery jpaQuery;

    @Mock
    Query query;

    @Mock
    JpaQueryMethod method;

    @Mock
    TypedQuery<Long> countQuery;

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullQuery() {
        execute(null, new Object[]{  });
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullBinder() throws Exception {
        new JpaQueryExecutionUnitTests.StubQueryExecution().execute(jpaQuery, null);
    }

    @Test
    public void transformsNoResultExceptionToNull() {
        Assert.assertThat(new JpaQueryExecution() {
            @Override
            protected Object doExecute(AbstractJpaQuery query, Object[] values) {
                return null;
            }
        }.execute(jpaQuery, new Object[]{  }), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    // DATAJPA-806
    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void modifyingExecutionFlushesEntityManagerIfSet() {
        Mockito.when(method.getReturnType()).thenReturn(((Class) (void.class)));
        Mockito.when(method.getFlushAutomatically()).thenReturn(true);
        ModifyingExecution execution = new ModifyingExecution(method, em);
        execution.execute(jpaQuery, new Object[]{  });
        Mockito.verify(em, Mockito.times(1)).flush();
        Mockito.verify(em, Mockito.times(0)).clear();
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void modifyingExecutionClearsEntityManagerIfSet() {
        Mockito.when(method.getReturnType()).thenReturn(((Class) (void.class)));
        Mockito.when(method.getClearAutomatically()).thenReturn(true);
        ModifyingExecution execution = new ModifyingExecution(method, em);
        execution.execute(jpaQuery, new Object[]{  });
        Mockito.verify(em, Mockito.times(0)).flush();
        Mockito.verify(em, Mockito.times(1)).clear();
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void allowsMethodReturnTypesForModifyingQuery() throws Exception {
        Mockito.when(method.getReturnType()).thenReturn(((Class) (void.class)), ((Class) (int.class)), ((Class) (Integer.class)));
        new ModifyingExecution(method, em);
        new ModifyingExecution(method, em);
        new ModifyingExecution(method, em);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test(expected = IllegalArgumentException.class)
    public void modifyingExecutionRejectsNonIntegerOrVoidReturnType() throws Exception {
        Mockito.when(method.getReturnType()).thenReturn(((Class) (Long.class)));
        new ModifyingExecution(method, em);
    }

    // DATAJPA-124, DATAJPA-912
    @Test
    public void pagedExecutionRetrievesObjectsForPageableOutOfRange() throws Exception {
        Parameters<?, ?> parameters = new DefaultParameters(getClass().getMethod("sampleMethod", Pageable.class));
        Mockito.when(jpaQuery.createCountQuery(Mockito.any(Object[].class))).thenReturn(countQuery);
        Mockito.when(jpaQuery.createQuery(Mockito.any(Object[].class))).thenReturn(query);
        Mockito.when(countQuery.getResultList()).thenReturn(Arrays.asList(20L));
        PagedExecution execution = new PagedExecution(parameters);
        execution.doExecute(jpaQuery, new Object[]{ PageRequest.of(2, 10) });
        Mockito.verify(query).getResultList();
        Mockito.verify(countQuery).getResultList();
    }

    // DATAJPA-477, DATAJPA-912
    @Test
    public void pagedExecutionShouldNotGenerateCountQueryIfQueryReportedNoResults() throws Exception {
        Parameters<?, ?> parameters = new DefaultParameters(getClass().getMethod("sampleMethod", Pageable.class));
        Mockito.when(jpaQuery.createQuery(Mockito.any(Object[].class))).thenReturn(query);
        Mockito.when(query.getResultList()).thenReturn(Arrays.asList(0L));
        PagedExecution execution = new PagedExecution(parameters);
        execution.doExecute(jpaQuery, new Object[]{ PageRequest.of(0, 10) });
        Mockito.verify(countQuery, Mockito.times(0)).getResultList();
        Mockito.verify(jpaQuery, Mockito.times(0)).createCountQuery(((Object[]) (ArgumentMatchers.any())));
    }

    // DATAJPA-912
    @Test
    public void pagedExecutionShouldUseCountFromResultIfOffsetIsZeroAndResultsWithinPageSize() throws Exception {
        Parameters<?, ?> parameters = new DefaultParameters(getClass().getMethod("sampleMethod", Pageable.class));
        Mockito.when(jpaQuery.createQuery(Mockito.any(Object[].class))).thenReturn(query);
        Mockito.when(query.getResultList()).thenReturn(Arrays.asList(new Object(), new Object(), new Object(), new Object()));
        PagedExecution execution = new PagedExecution(parameters);
        execution.doExecute(jpaQuery, new Object[]{ PageRequest.of(0, 10) });
        Mockito.verify(jpaQuery, Mockito.times(0)).createCountQuery(((Object[]) (ArgumentMatchers.any())));
    }

    // DATAJPA-912
    @Test
    public void pagedExecutionShouldUseCountFromResultWithOffsetAndResultsWithinPageSize() throws Exception {
        Parameters<?, ?> parameters = new DefaultParameters(getClass().getMethod("sampleMethod", Pageable.class));
        Mockito.when(jpaQuery.createQuery(Mockito.any(Object[].class))).thenReturn(query);
        Mockito.when(query.getResultList()).thenReturn(Arrays.asList(new Object(), new Object(), new Object(), new Object()));
        PagedExecution execution = new PagedExecution(parameters);
        execution.doExecute(jpaQuery, new Object[]{ PageRequest.of(5, 10) });
        Mockito.verify(jpaQuery, Mockito.times(0)).createCountQuery(((Object[]) (ArgumentMatchers.any())));
    }

    // DATAJPA-912
    @Test
    public void pagedExecutionShouldUseRequestCountFromResultWithOffsetAndResultsHitLowerPageSizeBounds() throws Exception {
        Parameters<?, ?> parameters = new DefaultParameters(getClass().getMethod("sampleMethod", Pageable.class));
        Mockito.when(jpaQuery.createQuery(Mockito.any(Object[].class))).thenReturn(query);
        Mockito.when(query.getResultList()).thenReturn(Collections.emptyList());
        Mockito.when(jpaQuery.createCountQuery(Mockito.any(Object[].class))).thenReturn(query);
        Mockito.when(countQuery.getResultList()).thenReturn(Arrays.asList(20L));
        PagedExecution execution = new PagedExecution(parameters);
        execution.doExecute(jpaQuery, new Object[]{ PageRequest.of(4, 4) });
        Mockito.verify(jpaQuery).createCountQuery(((Object[]) (ArgumentMatchers.any())));
    }

    // DATAJPA-912
    @Test
    public void pagedExecutionShouldUseRequestCountFromResultWithOffsetAndResultsHitUpperPageSizeBounds() throws Exception {
        Parameters<?, ?> parameters = new DefaultParameters(getClass().getMethod("sampleMethod", Pageable.class));
        Mockito.when(jpaQuery.createQuery(Mockito.any(Object[].class))).thenReturn(query);
        Mockito.when(query.getResultList()).thenReturn(Arrays.asList(new Object(), new Object(), new Object(), new Object()));
        Mockito.when(jpaQuery.createCountQuery(Mockito.any(Object[].class))).thenReturn(query);
        Mockito.when(countQuery.getResultList()).thenReturn(Arrays.asList(20L));
        PagedExecution execution = new PagedExecution(parameters);
        execution.doExecute(jpaQuery, new Object[]{ PageRequest.of(4, 4) });
        Mockito.verify(jpaQuery).createCountQuery(((Object[]) (ArgumentMatchers.any())));
    }

    // DATAJPA-951
    @Test
    public void doesNotPreemtivelyWrapResultIntoOptional() throws Exception {
        Mockito.doReturn(method).when(jpaQuery).getQueryMethod();
        Mockito.doReturn(Optional.class).when(method).getReturnType();
        JpaQueryExecutionUnitTests.StubQueryExecution execution = new JpaQueryExecutionUnitTests.StubQueryExecution() {
            @Override
            protected Object doExecute(AbstractJpaQuery query, Object[] values) {
                return "result";
            }
        };
        Object result = execution.execute(jpaQuery, new Object[0]);
        Assert.assertThat(result, CoreMatchers.is(CoreMatchers.instanceOf(String.class)));
    }

    static class StubQueryExecution extends JpaQueryExecution {
        @Override
        protected Object doExecute(AbstractJpaQuery query, Object[] values) {
            return null;
        }
    }
}

