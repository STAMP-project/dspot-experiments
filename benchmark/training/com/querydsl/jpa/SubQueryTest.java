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


import com.querydsl.core.domain.QCat;
import com.querydsl.jpa.domain.QEmployee;
import com.querydsl.jpa.domain.QUser;
import org.junit.Assert;
import org.junit.Test;


// @Test
// public void orderBy() {
// JPQLQuery<Void> query = query().from(cat1).where(cat1.alive);
// SubQueryExpression<Double> subquery = sub().from(cat).where(cat.mate.id.eq(cat1.id)).select(cat.floatProperty.avg());
// query.orderBy(subquery.subtract(-1.0f).asc());
// 
// assertEquals("select cat1 from Cat cat1 where cat1.alive order by (select avg(cat.floatProperty) from Cat cat where cat.mate.id = cat1.id) - ?1 asc",
// query.toString().replace("\n", " "));
// }
public class SubQueryTest extends AbstractQueryTest {
    @Test
    public void single_source() {
        JPQLQuery<?> query = selectFrom(Constants.cat);
        Assert.assertEquals("select cat\nfrom Cat cat", query.toString());
    }

    @Test
    public void multiple_sources() {
        JPQLQuery<?> query = select(Constants.cat).from(Constants.cat, Constants.fatcat);
        Assert.assertEquals("select cat\nfrom Cat cat, Cat fatcat", query.toString());
    }

    @Test
    public void in() {
        Constants.cat.in(selectFrom(Constants.cat));
    }

    @Test
    public void innerJoin() {
        Assert.assertEquals("select cat\nfrom Cat cat\n  inner join cat.mate", selectFrom(Constants.cat).innerJoin(Constants.cat.mate).toString());
    }

    @Test
    public void innerJoin2() {
        QEmployee employee = QEmployee.employee;
        QUser user = QUser.user;
        Assert.assertEquals("select employee\nfrom Employee employee\n  inner join employee.user as user", selectFrom(employee).innerJoin(employee.user, user).toString());
    }

    @Test
    public void leftJoin() {
        Assert.assertEquals("select cat\nfrom Cat cat\n  left join cat.mate", selectFrom(Constants.cat).leftJoin(Constants.cat.mate).toString());
    }

    @Test
    public void join() {
        Assert.assertEquals("select cat\nfrom Cat cat\n  inner join cat.mate", selectFrom(Constants.cat).join(Constants.cat.mate).toString());
    }

    @Test
    public void uniqueProjection() {
        AbstractQueryTest.assertToString("(select cat from Cat cat)", selectFrom(Constants.cat));
    }

    @Test
    public void listProjection() {
        AbstractQueryTest.assertToString("(select cat from Cat cat)", selectFrom(Constants.cat));
    }

    @Test
    public void listContains() {
        AbstractQueryTest.assertToString("cat in (select cat from Cat cat)", Constants.cat.in(selectFrom(Constants.cat)));
    }

    @Test
    public void exists() {
        AbstractQueryTest.assertToString("exists (select 1 from Cat cat)", selectOne().from(Constants.cat).exists());
    }

    @Test
    public void exists_where() {
        AbstractQueryTest.assertToString("exists (select 1 from Cat cat where cat.weight < ?1)", selectFrom(Constants.cat).where(Constants.cat.weight.lt(1)).exists());
    }

    @Test
    public void exists_via_unique() {
        AbstractQueryTest.assertToString("exists (select 1 from Cat cat where cat.weight < ?1)", selectOne().from(Constants.cat).where(Constants.cat.weight.lt(1)).exists());
    }

    @Test
    public void notExists() {
        AbstractQueryTest.assertToString("not exists (select 1 from Cat cat)", selectOne().from(Constants.cat).notExists());
    }

    @Test
    public void notExists_where() {
        AbstractQueryTest.assertToString("not exists (select 1 from Cat cat where cat.weight < ?1)", selectOne().from(Constants.cat).where(Constants.cat.weight.lt(1)).notExists());
    }

    @Test
    public void notExists_via_unique() {
        AbstractQueryTest.assertToString("not exists (select 1 from Cat cat where cat.weight < ?1)", selectOne().from(Constants.cat).where(Constants.cat.weight.lt(1)).notExists());
    }

    @Test
    public void count() {
        AbstractQueryTest.assertToString("(select count(cat) from Cat cat)", select(Constants.cat.count()).from(Constants.cat));
    }

    @Test
    public void count_via_list() {
        AbstractQueryTest.assertToString("(select count(cat) from Cat cat)", select(Constants.cat.count()).from(Constants.cat));
    }

    @Test
    public void count_name() {
        AbstractQueryTest.assertToString("(select count(cat.name) from Cat cat)", select(Constants.cat.name.count()).from(Constants.cat));
    }

    @Test
    public void count_multiple_sources() {
        QCat other = new QCat("other");
        AbstractQueryTest.assertToString("(select count(cat) from Cat cat, Cat other)", select(Constants.cat.count()).from(Constants.cat, other));
    }

    @Test
    public void count_multiple_sources_via_list() {
        QCat other = new QCat("other");
        AbstractQueryTest.assertToString("(select count(cat) from Cat cat, Cat other)", select(Constants.cat.count()).from(Constants.cat, other));
    }

    @Test
    public void indexed_access() {
        AbstractQueryTest.assertMatches(("\\(select count\\(cat\\) from Cat cat   " + ("left join cat.kittens as cat_kittens_\\w+ " + "with index\\(cat_kittens_\\w+\\) = \\?1 where cat_kittens_\\w+.name = \\?2\\)")), select(Constants.cat.count()).from(Constants.cat).where(Constants.cat.kittens.get(0).name.eq("Kate")));
    }

    @Test
    public void indexed_access_without_constant() {
        AbstractQueryTest.assertMatches(("\\(select count\\(cat\\) from Cat cat   " + ("left join cat.kittens as cat_kittens_\\w+ " + "with index\\(cat_kittens_\\w+\\) = cat.id where cat_kittens_\\w+.name = \\?1\\)")), select(Constants.cat.count()).from(Constants.cat).where(Constants.cat.kittens.get(Constants.cat.id).name.eq("Kate")));
    }

    @Test
    public void indexOf() {
        AbstractQueryTest.assertToString("(select count(cat) from Cat cat where locate(?1,cat.name)-1 = ?2)", select(Constants.cat.count()).from(Constants.cat).where(Constants.cat.name.indexOf("a").eq(1)));
    }
}

