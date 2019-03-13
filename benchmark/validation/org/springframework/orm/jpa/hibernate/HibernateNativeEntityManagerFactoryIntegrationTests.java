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
package org.springframework.orm.jpa.hibernate;


import FlushMode.AUTO;
import FlushMode.MANUAL;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.AbstractContainerEntityManagerFactoryIntegrationTests;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryIntegrationTests;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.orm.jpa.domain.Person;


/**
 * Hibernate-specific JPA tests with native SessionFactory setup and getCurrentSession interaction.
 *
 * @author Juergen Hoeller
 * @since 5.1
 */
public class HibernateNativeEntityManagerFactoryIntegrationTests extends AbstractContainerEntityManagerFactoryIntegrationTests {
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testEntityManagerFactoryImplementsEntityManagerFactoryInfo() {
        Assert.assertFalse("Must not have introduced config interface", ((entityManagerFactory) instanceof EntityManagerFactoryInfo));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEntityListener() {
        String firstName = "Tony";
        insertPerson(firstName);
        List<Person> people = sharedEntityManager.createQuery("select p from Person as p").getResultList();
        Assert.assertEquals(1, people.size());
        Assert.assertEquals(firstName, people.get(0).getFirstName());
        Assert.assertSame(applicationContext, people.get(0).postLoaded);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testCurrentSession() {
        String firstName = "Tony";
        insertPerson(firstName);
        Query q = sessionFactory.getCurrentSession().createQuery("select p from Person as p");
        List<Person> people = q.getResultList();
        Assert.assertEquals(1, people.size());
        Assert.assertEquals(firstName, people.get(0).getFirstName());
        Assert.assertSame(applicationContext, people.get(0).postLoaded);
    }

    // SPR-16956
    @Test
    public void testReadOnly() {
        Assert.assertSame(AUTO, sessionFactory.getCurrentSession().getHibernateFlushMode());
        Assert.assertFalse(sessionFactory.getCurrentSession().isDefaultReadOnly());
        endTransaction();
        this.transactionDefinition.setReadOnly(true);
        startNewTransaction();
        Assert.assertSame(MANUAL, sessionFactory.getCurrentSession().getHibernateFlushMode());
        Assert.assertTrue(sessionFactory.getCurrentSession().isDefaultReadOnly());
    }
}

