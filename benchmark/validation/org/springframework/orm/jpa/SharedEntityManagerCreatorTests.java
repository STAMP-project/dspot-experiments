/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.orm.jpa;


import ParameterMode.IN;
import ParameterMode.INOUT;
import ParameterMode.OUT;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TransactionRequiredException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Unit tests for {@link SharedEntityManagerCreator}.
 *
 * @author Oliver Gierke
 * @author Juergen Hoeller
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class SharedEntityManagerCreatorTests {
    @Test
    public void proxyingWorksIfInfoReturnsNullEntityManagerInterface() {
        EntityManagerFactory emf = Mockito.mock(EntityManagerFactory.class, Mockito.withSettings().extraInterfaces(EntityManagerFactoryInfo.class));
        // EntityManagerFactoryInfo.getEntityManagerInterface returns null
        Assert.assertThat(SharedEntityManagerCreator.createSharedEntityManager(emf), CoreMatchers.is(CoreMatchers.notNullValue()));
    }

    @Test(expected = TransactionRequiredException.class)
    public void transactionRequiredExceptionOnJoinTransaction() {
        EntityManagerFactory emf = Mockito.mock(EntityManagerFactory.class);
        EntityManager em = SharedEntityManagerCreator.createSharedEntityManager(emf);
        em.joinTransaction();
    }

    @Test(expected = TransactionRequiredException.class)
    public void transactionRequiredExceptionOnFlush() {
        EntityManagerFactory emf = Mockito.mock(EntityManagerFactory.class);
        EntityManager em = SharedEntityManagerCreator.createSharedEntityManager(emf);
        em.flush();
    }

    @Test(expected = TransactionRequiredException.class)
    public void transactionRequiredExceptionOnPersist() {
        EntityManagerFactory emf = Mockito.mock(EntityManagerFactory.class);
        EntityManager em = SharedEntityManagerCreator.createSharedEntityManager(emf);
        em.persist(new Object());
    }

    @Test(expected = TransactionRequiredException.class)
    public void transactionRequiredExceptionOnMerge() {
        EntityManagerFactory emf = Mockito.mock(EntityManagerFactory.class);
        EntityManager em = SharedEntityManagerCreator.createSharedEntityManager(emf);
        em.merge(new Object());
    }

    @Test(expected = TransactionRequiredException.class)
    public void transactionRequiredExceptionOnRemove() {
        EntityManagerFactory emf = Mockito.mock(EntityManagerFactory.class);
        EntityManager em = SharedEntityManagerCreator.createSharedEntityManager(emf);
        em.remove(new Object());
    }

    @Test(expected = TransactionRequiredException.class)
    public void transactionRequiredExceptionOnRefresh() {
        EntityManagerFactory emf = Mockito.mock(EntityManagerFactory.class);
        EntityManager em = SharedEntityManagerCreator.createSharedEntityManager(emf);
        em.refresh(new Object());
    }

    @Test
    public void deferredQueryWithUpdate() {
        EntityManagerFactory emf = Mockito.mock(EntityManagerFactory.class);
        EntityManager targetEm = Mockito.mock(EntityManager.class);
        Query query = Mockito.mock(Query.class);
        BDDMockito.given(emf.createEntityManager()).willReturn(targetEm);
        BDDMockito.given(targetEm.createQuery("x")).willReturn(query);
        BDDMockito.given(targetEm.isOpen()).willReturn(true);
        EntityManager em = SharedEntityManagerCreator.createSharedEntityManager(emf);
        em.createQuery("x").executeUpdate();
        Mockito.verify(query).executeUpdate();
        Mockito.verify(targetEm).close();
    }

    @Test
    public void deferredQueryWithSingleResult() {
        EntityManagerFactory emf = Mockito.mock(EntityManagerFactory.class);
        EntityManager targetEm = Mockito.mock(EntityManager.class);
        Query query = Mockito.mock(Query.class);
        BDDMockito.given(emf.createEntityManager()).willReturn(targetEm);
        BDDMockito.given(targetEm.createQuery("x")).willReturn(query);
        BDDMockito.given(targetEm.isOpen()).willReturn(true);
        EntityManager em = SharedEntityManagerCreator.createSharedEntityManager(emf);
        em.createQuery("x").getSingleResult();
        Mockito.verify(query).getSingleResult();
        Mockito.verify(targetEm).close();
    }

    @Test
    public void deferredQueryWithResultList() {
        EntityManagerFactory emf = Mockito.mock(EntityManagerFactory.class);
        EntityManager targetEm = Mockito.mock(EntityManager.class);
        Query query = Mockito.mock(Query.class);
        BDDMockito.given(emf.createEntityManager()).willReturn(targetEm);
        BDDMockito.given(targetEm.createQuery("x")).willReturn(query);
        BDDMockito.given(targetEm.isOpen()).willReturn(true);
        EntityManager em = SharedEntityManagerCreator.createSharedEntityManager(emf);
        em.createQuery("x").getResultList();
        Mockito.verify(query).getResultList();
        Mockito.verify(targetEm).close();
    }

    @Test
    public void deferredQueryWithResultStream() {
        EntityManagerFactory emf = Mockito.mock(EntityManagerFactory.class);
        EntityManager targetEm = Mockito.mock(EntityManager.class);
        Query query = Mockito.mock(Query.class);
        BDDMockito.given(emf.createEntityManager()).willReturn(targetEm);
        BDDMockito.given(targetEm.createQuery("x")).willReturn(query);
        BDDMockito.given(targetEm.isOpen()).willReturn(true);
        EntityManager em = SharedEntityManagerCreator.createSharedEntityManager(emf);
        em.createQuery("x").getResultStream();
        Mockito.verify(query).getResultStream();
        Mockito.verify(targetEm).close();
    }

    @Test
    public void deferredStoredProcedureQueryWithIndexedParameters() {
        EntityManagerFactory emf = Mockito.mock(EntityManagerFactory.class);
        EntityManager targetEm = Mockito.mock(EntityManager.class);
        StoredProcedureQuery query = Mockito.mock(StoredProcedureQuery.class);
        BDDMockito.given(emf.createEntityManager()).willReturn(targetEm);
        BDDMockito.given(targetEm.createStoredProcedureQuery("x")).willReturn(query);
        BDDMockito.willReturn("y").given(query).getOutputParameterValue(0);
        BDDMockito.willReturn("z").given(query).getOutputParameterValue(2);
        BDDMockito.given(targetEm.isOpen()).willReturn(true);
        EntityManager em = SharedEntityManagerCreator.createSharedEntityManager(emf);
        StoredProcedureQuery spq = em.createStoredProcedureQuery("x");
        spq.registerStoredProcedureParameter(0, String.class, OUT);
        spq.registerStoredProcedureParameter(1, Number.class, IN);
        spq.registerStoredProcedureParameter(2, Object.class, INOUT);
        spq.execute();
        Assert.assertEquals("y", spq.getOutputParameterValue(0));
        try {
            spq.getOutputParameterValue(1);
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        Assert.assertEquals("z", spq.getOutputParameterValue(2));
        Mockito.verify(query).registerStoredProcedureParameter(0, String.class, OUT);
        Mockito.verify(query).registerStoredProcedureParameter(1, Number.class, IN);
        Mockito.verify(query).registerStoredProcedureParameter(2, Object.class, INOUT);
        Mockito.verify(query).execute();
        Mockito.verify(targetEm).close();
        Mockito.verifyNoMoreInteractions(query);
        Mockito.verifyNoMoreInteractions(targetEm);
    }

    @Test
    public void deferredStoredProcedureQueryWithNamedParameters() {
        EntityManagerFactory emf = Mockito.mock(EntityManagerFactory.class);
        EntityManager targetEm = Mockito.mock(EntityManager.class);
        StoredProcedureQuery query = Mockito.mock(StoredProcedureQuery.class);
        BDDMockito.given(emf.createEntityManager()).willReturn(targetEm);
        BDDMockito.given(targetEm.createStoredProcedureQuery("x")).willReturn(query);
        BDDMockito.willReturn("y").given(query).getOutputParameterValue("a");
        BDDMockito.willReturn("z").given(query).getOutputParameterValue("c");
        BDDMockito.given(targetEm.isOpen()).willReturn(true);
        EntityManager em = SharedEntityManagerCreator.createSharedEntityManager(emf);
        StoredProcedureQuery spq = em.createStoredProcedureQuery("x");
        spq.registerStoredProcedureParameter("a", String.class, OUT);
        spq.registerStoredProcedureParameter("b", Number.class, IN);
        spq.registerStoredProcedureParameter("c", Object.class, INOUT);
        spq.execute();
        Assert.assertEquals("y", spq.getOutputParameterValue("a"));
        try {
            spq.getOutputParameterValue("b");
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        Assert.assertEquals("z", spq.getOutputParameterValue("c"));
        Mockito.verify(query).registerStoredProcedureParameter("a", String.class, OUT);
        Mockito.verify(query).registerStoredProcedureParameter("b", Number.class, IN);
        Mockito.verify(query).registerStoredProcedureParameter("c", Object.class, INOUT);
        Mockito.verify(query).execute();
        Mockito.verify(targetEm).close();
        Mockito.verifyNoMoreInteractions(query);
        Mockito.verifyNoMoreInteractions(targetEm);
    }
}

