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


import FlushModeType.AUTO;
import LockModeType.PESSIMISTIC_READ;
import QGroup.group;
import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.Target;
import com.querydsl.core.Tuple;
import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.jpa.domain.Cat;
import com.querydsl.jpa.testutil.JPATestRunner;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import static QCat.cat;
import static QChild.child;
import static QParent.parent;


/**
 *
 *
 * @author tiwe
 */
@RunWith(JPATestRunner.class)
public class JPABase extends AbstractJPATest implements JPATest {
    private static final QCat cat = cat;

    @Rule
    @ClassRule
    public static TestRule targetRule = new TargetRule();

    @Rule
    @ClassRule
    public static TestRule jpaProviderRule = new JPAProviderRule();

    private EntityManager entityManager;

    @Test
    @NoEclipseLink
    @NoOpenJPA
    @NoHibernate
    public void connection_access() {
        Assert.assertNotNull(query().from(JPABase.cat).select(JPABase.cat).createQuery().unwrap(Connection.class));
    }

    @Test
    public void delete2() {
        Assert.assertEquals(0, delete(group).execute());
    }

    @Test
    @NoBatooJPA
    public void delete_where() {
        delete(JPABase.cat).where(JPABase.cat.name.eq("XXX")).execute();
    }

    @Test
    @ExcludeIn(Target.MYSQL)
    public void delete_where_any() {
        delete(JPABase.cat).where(JPABase.cat.kittens.any().name.eq("XXX")).execute();
    }

    @Test
    @NoBatooJPA
    @ExcludeIn(Target.MYSQL)
    public void delete_where_subQuery_exists() {
        QCat parent = JPABase.cat;
        QCat child = new QCat("kitten");
        delete(child).where(child.id.eq((-100)), JPAExpressions.selectOne().from(parent).where(parent.id.eq((-200)), child.in(parent.kittens)).exists()).execute();
    }

    @Test
    @NoBatooJPA
    public void delete_where_subQuery2() {
        QChild child = child;
        QParent parent = parent;
        JPQLQuery<?> subQuery = JPAExpressions.selectFrom(parent).where(parent.id.eq(2), child.parent.eq(parent));
        // child.in(parent.children));
        delete(child).where(child.id.eq(1), subQuery.exists()).execute();
    }

    @Test
    public void finder() {
        Map<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("name", "Bob123");
        List<Cat> cats = CustomFinder.findCustom(entityManager, Cat.class, conditions, "name");
        Assert.assertEquals(1, cats.size());
        Assert.assertEquals("Bob123", cats.get(0).getName());
    }

    @Test
    public void flushMode() {
        Assert.assertFalse(query().from(JPABase.cat).setFlushMode(AUTO).select(JPABase.cat).fetch().isEmpty());
    }

    @Test
    @NoEclipseLink
    @NoOpenJPA
    public void hint() {
        Query query = query().from(JPABase.cat).setHint("org.hibernate.cacheable", true).select(JPABase.cat).createQuery();
        Assert.assertNotNull(query);
        Assert.assertTrue(query.getHints().containsKey("org.hibernate.cacheable"));
        Assert.assertFalse(query.getResultList().isEmpty());
    }

    @Test
    public void hint2() {
        Assert.assertFalse(query().from(JPABase.cat).setHint("org.hibernate.cacheable", true).select(JPABase.cat).fetch().isEmpty());
    }

    @Test
    @ExcludeIn(Target.DERBY)
    public void iterate() {
        CloseableIterator<Cat> cats = query().from(JPABase.cat).select(JPABase.cat).iterate();
        while (cats.hasNext()) {
            Cat cat = cats.next();
            Assert.assertNotNull(cat);
        } 
        cats.close();
    }

    @Test
    public void limit1_uniqueResult() {
        Assert.assertNotNull(query().from(JPABase.cat).limit(1).select(JPABase.cat).fetchOne());
    }

    @Test
    public void lockMode() {
        Query query = query().from(JPABase.cat).setLockMode(PESSIMISTIC_READ).select(JPABase.cat).createQuery();
        Assert.assertTrue(query.getLockMode().equals(PESSIMISTIC_READ));
        Assert.assertFalse(query.getResultList().isEmpty());
    }

    @Test
    public void lockMode2() {
        Assert.assertFalse(query().from(JPABase.cat).setLockMode(PESSIMISTIC_READ).select(JPABase.cat).fetch().isEmpty());
    }

    @Test
    public void queryExposure() {
        // save(new Cat(20));
        List<Cat> results = query().from(JPABase.cat).select(JPABase.cat).createQuery().getResultList();
        Assert.assertNotNull(results);
        Assert.assertFalse(results.isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    @NoEclipseLink
    @NoBatooJPA
    public void createQuery() {
        List<Tuple> rows = query().from(JPABase.cat).select(JPABase.cat.id, JPABase.cat.name).createQuery().getResultList();
        for (Tuple row : rows) {
            Assert.assertEquals(2, row.size());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @NoEclipseLink
    @NoBatooJPA
    public void createQuery2() {
        List<Tuple> rows = query().from(JPABase.cat).select(new com.querydsl.core.types.Expression<?>[]{ JPABase.cat.id, JPABase.cat.name }).createQuery().getResultList();
        for (Tuple row : rows) {
            Assert.assertEquals(2, row.size());
        }
    }

    @Test
    public void createQuery3() {
        List<String> rows = query().from(JPABase.cat).select(JPABase.cat.name).createQuery().getResultList();
        for (String row : rows) {
            Assert.assertTrue((row instanceof String));
        }
    }
}

