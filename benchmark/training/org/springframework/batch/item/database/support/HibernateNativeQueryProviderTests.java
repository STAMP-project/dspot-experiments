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


import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.query.NativeQuery;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.batch.item.database.orm.HibernateNativeQueryProvider;


/**
 *
 *
 * @author Anatoly Polinsky
 * @author Dave Syer
 * @author Will Schipp
 */
public class HibernateNativeQueryProviderTests {
    protected HibernateNativeQueryProvider<HibernateNativeQueryProviderTests.Foo> hibernateQueryProvider;

    public HibernateNativeQueryProviderTests() {
        hibernateQueryProvider = new HibernateNativeQueryProvider();
        hibernateQueryProvider.setEntityClass(HibernateNativeQueryProviderTests.Foo.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateQueryWithStatelessSession() {
        String sqlQuery = "select * from T_FOOS";
        hibernateQueryProvider.setSqlQuery(sqlQuery);
        StatelessSession session = Mockito.mock(StatelessSession.class);
        NativeQuery<HibernateNativeQueryProviderTests.Foo> query = Mockito.mock(NativeQuery.class);
        Mockito.when(session.createNativeQuery(sqlQuery)).thenReturn(query);
        Mockito.when(query.addEntity(HibernateNativeQueryProviderTests.Foo.class)).thenReturn(query);
        hibernateQueryProvider.setStatelessSession(session);
        Assert.assertNotNull(hibernateQueryProvider.createQuery());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldCreateQueryWithStatefulSession() {
        String sqlQuery = "select * from T_FOOS";
        hibernateQueryProvider.setSqlQuery(sqlQuery);
        Session session = Mockito.mock(Session.class);
        NativeQuery<HibernateNativeQueryProviderTests.Foo> query = Mockito.mock(NativeQuery.class);
        Mockito.when(session.createNativeQuery(sqlQuery)).thenReturn(query);
        Mockito.when(query.addEntity(HibernateNativeQueryProviderTests.Foo.class)).thenReturn(query);
        hibernateQueryProvider.setSession(session);
        Assert.assertNotNull(hibernateQueryProvider.createQuery());
    }

    private static class Foo {}
}

