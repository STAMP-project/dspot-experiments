/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.jpa;


import LockMode.PESSIMISTIC_WRITE;
import QGroup.group;
import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.domain.Cat;
import com.querydsl.jpa.domain.QCat;
import com.querydsl.jpa.testutil.HibernateTestRunner;
import java.io.IOException;
import java.util.List;
import org.hibernate.FlushMode.AUTO;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author tiwe
 */
@RunWith(HibernateTestRunner.class)
public class HibernateBase extends AbstractJPATest implements HibernateTest {
    private static final QCat cat = QCat.cat;

    @Rule
    @ClassRule
    public static TestRule jpaProviderRule = new JPAProviderRule();

    @Rule
    @ClassRule
    public static TestRule targetRule = new TargetRule();

    private Session session;

    @Test
    public void query_exposure() {
        // save(new Cat());
        List<Cat> results = query().from(HibernateBase.cat).select(HibernateBase.cat).createQuery().list();
        Assert.assertNotNull(results);
        Assert.assertFalse(results.isEmpty());
    }

    @Test
    public void delete() {
        Assert.assertEquals(0, delete(group).execute());
    }

    @Test
    public void with_comment() {
        query().from(HibernateBase.cat).setComment("my comment").select(HibernateBase.cat).fetch();
    }

    @Test
    public void lockMode() {
        query().from(HibernateBase.cat).setLockMode(HibernateBase.cat, PESSIMISTIC_WRITE).select(HibernateBase.cat).fetch();
    }

    @Test
    public void flushMode() {
        query().from(HibernateBase.cat).setFlushMode(AUTO).select(HibernateBase.cat).fetch();
    }

    @Test
    public void scroll() throws IOException {
        CloseableIterator<Cat> cats = new ScrollableResultsIterator<Cat>(query().from(HibernateBase.cat).select(HibernateBase.cat).createQuery().scroll());
        Assert.assertTrue(cats.hasNext());
        while (cats.hasNext()) {
            Assert.assertNotNull(cats.next());
        } 
        cats.close();
    }

    @Test
    public void scrollTuple() throws IOException {
        CloseableIterator<Tuple> rows = new ScrollableResultsIterator<Tuple>(query().from(HibernateBase.cat).select(HibernateBase.cat.name, HibernateBase.cat.birthdate).createQuery().scroll());
        Assert.assertTrue(rows.hasNext());
        while (rows.hasNext()) {
            Tuple row = rows.next();
            Assert.assertEquals(2, row.size());
        } 
        rows.close();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createQuery() {
        List<Tuple> rows = query().from(HibernateBase.cat).select(HibernateBase.cat.id, HibernateBase.cat.name).createQuery().list();
        for (Tuple row : rows) {
            Assert.assertEquals(2, row.size());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createQuery2() {
        List<Tuple> rows = query().from(HibernateBase.cat).select(new com.querydsl.core.types.Expression[]{ HibernateBase.cat.id, HibernateBase.cat.name }).createQuery().list();
        for (Tuple row : rows) {
            Assert.assertEquals(2, row.size());
        }
    }

    @Test
    public void createQuery3() {
        List<String> rows = query().from(HibernateBase.cat).select(HibernateBase.cat.name).createQuery().list();
        for (String row : rows) {
            Assert.assertTrue((row instanceof String));
        }
    }
}

