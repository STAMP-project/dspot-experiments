/**
 * Copyright 2006-2008 the original author or authors.
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
package org.springframework.batch.item.database.support;


import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.database.orm.HibernateNativeQueryProvider;
import org.springframework.batch.item.sample.Foo;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 *
 * @author Anatoly Polinsky
 * @author Dave Syer
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "../data-source-context.xml")
public class HibernateNativeQueryProviderIntegrationTests {
    protected DataSource dataSource;

    protected HibernateNativeQueryProvider<Foo> hibernateQueryProvider;

    private SessionFactory sessionFactory;

    public HibernateNativeQueryProviderIntegrationTests() {
        hibernateQueryProvider = new HibernateNativeQueryProvider();
        hibernateQueryProvider.setEntityClass(Foo.class);
    }

    @Test
    @Transactional
    public void shouldRetrieveAndMapAllFoos() throws Exception {
        String nativeQuery = "select * from T_FOOS";
        hibernateQueryProvider.setSqlQuery(nativeQuery);
        hibernateQueryProvider.afterPropertiesSet();
        hibernateQueryProvider.setSession(sessionFactory.openSession());
        Query<Foo> query = hibernateQueryProvider.createQuery();
        List<Foo> expectedFoos = new ArrayList<>();
        expectedFoos.add(new Foo(1, "bar1", 1));
        expectedFoos.add(new Foo(2, "bar2", 2));
        expectedFoos.add(new Foo(3, "bar3", 3));
        expectedFoos.add(new Foo(4, "bar4", 4));
        expectedFoos.add(new Foo(5, "bar5", 5));
        @SuppressWarnings("unchecked")
        List<Foo> actualFoos = query.list();
        Assert.assertEquals(actualFoos, expectedFoos);
    }
}

