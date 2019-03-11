/**
 * Copyright 2016-2019 the original author or authors.
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


import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.data.jpa.domain.sample.Role;
import org.springframework.data.jpa.domain.sample.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Integration tests for {@link AbstractStringBasedJpaQuery}.
 *
 * @author Oliver Gierke
 * @unknown Henrik Freischlader Trio - Nobody Else To Blame (Openness)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:infrastructure.xml")
public class AbstractStringBasedJpaQueryIntegrationTests {
    @PersistenceContext
    EntityManager em;

    // DATAJPA-885
    @Test
    public void createsNormalQueryForJpaManagedReturnTypes() throws Exception {
        EntityManager mock = Mockito.mock(EntityManager.class);
        Mockito.when(mock.getDelegate()).thenReturn(mock);
        Mockito.when(mock.getEntityManagerFactory()).thenReturn(em.getEntityManagerFactory());
        Mockito.when(mock.getMetamodel()).thenReturn(em.getMetamodel());
        JpaQueryMethod method = getMethod("findRolesByEmailAddress", String.class);
        AbstractStringBasedJpaQuery jpaQuery = new SimpleJpaQuery(method, mock, QueryMethodEvaluationContextProvider.DEFAULT, new SpelExpressionParser());
        jpaQuery.createJpaQuery(method.getAnnotatedQuery(), method.getResultProcessor().getReturnedType());
        Mockito.verify(mock, Mockito.times(1)).createQuery(ArgumentMatchers.anyString());
        Mockito.verify(mock, Mockito.times(0)).createQuery(ArgumentMatchers.anyString(), ArgumentMatchers.eq(Tuple.class));
    }

    interface SampleRepository extends Repository<User, Integer> {
        @Query("select u.roles from User u where u.emailAddress = ?1")
        Set<Role> findRolesByEmailAddress(String emailAddress);
    }
}

