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


import com.querydsl.jpa.domain.Cat;
import com.querydsl.jpa.domain.QCat;
import com.querydsl.jpa.hibernate.HibernateQuery;
import com.querydsl.jpa.testutil.HibernateTestRunner;
import java.util.Arrays;
import java.util.List;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(HibernateTestRunner.class)
public class IntegrationBase extends ParsingTest implements HibernateTest {
    private Session session;

    @Override
    @Test
    public void groupBy() throws Exception {
        // NOTE : commented out, because HQLSDB doesn't support these queries
    }

    @Override
    @Test
    public void groupBy_2() throws Exception {
        // NOTE : commented out, because HQLSDB doesn't support these queries
    }

    @Override
    @Test
    public void orderBy() throws Exception {
        // NOTE : commented out, because HQLSDB doesn't support these queries
    }

    @Override
    @Test
    public void docoExamples910() throws Exception {
        // NOTE : commented out, because HQLSDB doesn't support these queries
    }

    @Test
    public void scroll() {
        session.save(new Cat("Bob", 10));
        session.save(new Cat("Steve", 11));
        QCat cat = QCat.cat;
        HibernateQuery<?> query = new HibernateQuery<Void>(session);
        ScrollableResults results = query.from(cat).select(cat).scroll(ScrollMode.SCROLL_INSENSITIVE);
        while (results.next()) {
            Assert.assertNotNull(results.get(0));
        } 
        results.close();
    }

    @Test
    public void update() {
        session.save(new Cat("Bob", 10));
        session.save(new Cat("Steve", 11));
        QCat cat = QCat.cat;
        long amount = update(cat).where(cat.name.eq("Bob")).set(cat.name, "Bobby").set(cat.alive, false).execute();
        Assert.assertEquals(1, amount);
        Assert.assertEquals(0L, query().from(cat).where(cat.name.eq("Bob")).fetchCount());
    }

    @Test
    public void update_with_null() {
        session.save(new Cat("Bob", 10));
        session.save(new Cat("Steve", 11));
        QCat cat = QCat.cat;
        long amount = update(cat).where(cat.name.eq("Bob")).set(cat.name, ((String) (null))).set(cat.alive, false).execute();
        Assert.assertEquals(1, amount);
    }

    @Test
    public void delete() {
        session.save(new Cat("Bob", 10));
        session.save(new Cat("Steve", 11));
        QCat cat = QCat.cat;
        long amount = delete(cat).where(cat.name.eq("Bob")).execute();
        Assert.assertEquals(1, amount);
    }

    @Test
    public void collection() throws Exception {
        List<Cat> cats = Arrays.asList(new Cat("Bob", 10), new Cat("Steve", 11));
        for (Cat cat : cats) {
            session.save(cat);
        }
        query().from(Constants.cat).innerJoin(Constants.cat.kittens, Constants.kitten).where(Constants.kitten.in(cats)).parse();
    }
}

