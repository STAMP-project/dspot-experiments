/**
 * Copyright 2011-2019 the original author or authors.
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


import LockModeType.READ;
import QRole.role.name;
import java.util.Collections;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Metamodel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.jpa.domain.sample.Role;
import org.springframework.data.jpa.repository.sample.RoleRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;


/**
 * Integration test for lock support.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 */
@RunWith(MockitoJUnitRunner.class)
public class CrudMethodMetadataUnitTests {
    @Mock
    EntityManager em;

    @Mock
    EntityManagerFactory emf;

    @Mock
    CriteriaBuilder builder;

    @Mock
    CriteriaQuery<Role> criteriaQuery;

    @Mock
    JpaEntityInformation<Role, Integer> information;

    @Mock
    TypedQuery<Role> typedQuery;

    @Mock
    Query query;

    @Mock
    Metamodel metamodel;

    RoleRepository repository;

    // DATAJPA-73, DATAJPA-173
    @Test
    public void usesLockInformationAnnotatedAtRedeclaredMethod() {
        Mockito.when(em.getCriteriaBuilder()).thenReturn(builder);
        Mockito.when(builder.createQuery(Role.class)).thenReturn(criteriaQuery);
        Mockito.when(em.createQuery(criteriaQuery)).thenReturn(typedQuery);
        Mockito.when(typedQuery.setLockMode(ArgumentMatchers.any(LockModeType.class))).thenReturn(typedQuery);
        repository.findAll();
        Mockito.verify(typedQuery).setLockMode(READ);
        Mockito.verify(typedQuery).setHint("foo", "bar");
    }

    // DATAJPA-359, DATAJPA-173
    @Test
    public void usesMetadataAnnotatedAtRedeclaredFindOne() {
        repository.findById(1);
        Map<String, Object> expectedLinks = Collections.singletonMap("foo", ((Object) ("bar")));
        LockModeType expectedLockModeType = LockModeType.READ;
        Mockito.verify(em).find(Role.class, 1, expectedLockModeType, expectedLinks);
    }

    // DATAJPA-574
    @Test
    public void appliesLockModeAndQueryHintsToQuerydslQuery() {
        Mockito.when(em.getDelegate()).thenReturn(Mockito.mock(EntityManager.class));
        Mockito.when(em.createQuery(ArgumentMatchers.anyString())).thenReturn(query);
        repository.findOne(name.eq("role"));
        Mockito.verify(query).setLockMode(READ);
        Mockito.verify(query).setHint("foo", "bar");
    }
}

