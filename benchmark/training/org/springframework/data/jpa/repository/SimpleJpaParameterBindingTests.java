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


import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.jpa.domain.sample.User;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 *
 * @author Oliver Gierke
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(// , "classpath:eclipselink.xml"
// , "classpath:openjpa.xml"
{ "classpath:application-context.xml" }// , "classpath:eclipselink.xml"
// , "classpath:openjpa.xml"
)
@Transactional
public class SimpleJpaParameterBindingTests {
    @PersistenceContext
    EntityManager em;

    @Test
    @SuppressWarnings("rawtypes")
    public void bindCollection() {
        User user = new User("Dave", "Matthews", "foo@bar.de");
        em.persist(user);
        em.flush();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<User> criteria = builder.createQuery(User.class);
        Root<User> root = criteria.from(User.class);
        ParameterExpression<Collection> parameter = builder.parameter(Collection.class);
        criteria.where(root.get("firstname").in(parameter));
        TypedQuery<User> query = em.createQuery(criteria);
        query.setParameter(parameter, Arrays.asList("Dave"));
        List<User> result = query.getResultList();
        Assert.assertThat(result.isEmpty(), CoreMatchers.is(false));
        Assert.assertThat(result.get(0), CoreMatchers.is(user));
    }
}

