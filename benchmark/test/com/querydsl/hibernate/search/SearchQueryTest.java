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
package com.querydsl.hibernate.search;


import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class SearchQueryTest extends AbstractQueryTest {
    private final QUser user = new QUser("user");

    @Test
    public void exists() {
        Assert.assertTrue(((query().where(user.emailAddress.eq("bob@example.com")).fetchCount()) > 0));
        Assert.assertFalse(((query().where(user.emailAddress.eq("bobby@example.com")).fetchCount()) > 0));
    }

    @Test
    public void notExists() {
        Assert.assertFalse(((query().where(user.emailAddress.eq("bob@example.com")).fetchCount()) == 0));
        Assert.assertTrue(((query().where(user.emailAddress.eq("bobby@example.com")).fetchCount()) == 0));
    }

    @Test
    public void count() {
        BooleanExpression filter = user.emailAddress.eq("bob@example.com");
        Assert.assertEquals(1, query().where(filter).fetchCount());
    }

    @Test
    public void uniqueResult() {
        BooleanExpression filter = user.emailAddress.eq("bob@example.com");
        User u = query().where(filter).fetchOne();
        Assert.assertNotNull(u);
        Assert.assertEquals("bob@example.com", u.getEmailAddress());
    }

    @Test
    public void list() {
        BooleanExpression filter = user.emailAddress.eq("bob@example.com");
        List<User> list = query().where(filter).fetch();
        Assert.assertEquals(1, list.size());
        User u = query().where(filter).fetchOne();
        Assert.assertEquals(u, list.get(0));
    }

    @Test(expected = NonUniqueResultException.class)
    public void unique_result_throws_exception_on_multiple_results() {
        query().where(user.middleName.eq("X")).fetchOne();
    }

    @Test
    public void singleResult() {
        Assert.assertNotNull(query().where(user.middleName.eq("X")).fetchFirst());
    }

    @Test
    public void ordering() {
        BooleanExpression filter = user.middleName.eq("X");
        // asc
        List<String> asc = getFirstNames(query().where(filter).orderBy(user.firstName.asc()).fetch());
        Assert.assertEquals(Arrays.asList("Anton", "Barbara", "John", "Robert"), asc);
        // desc
        List<String> desc = getFirstNames(query().where(filter).orderBy(user.firstName.desc()).fetch());
        Assert.assertEquals(Arrays.asList("Robert", "John", "Barbara", "Anton"), desc);
    }

    @Test
    public void paging() {
        BooleanExpression filter = user.middleName.eq("X");
        OrderSpecifier<?> order = user.firstName.asc();
        // limit
        List<String> limit = getFirstNames(query().where(filter).orderBy(order).limit(2).fetch());
        Assert.assertEquals(Arrays.asList("Anton", "Barbara"), limit);
        // offset
        List<String> offset = getFirstNames(query().where(filter).orderBy(order).offset(1).fetch());
        Assert.assertEquals(Arrays.asList("Barbara", "John", "Robert"), offset);
        // limit + offset
        List<String> limitAndOffset = getFirstNames(query().where(filter).orderBy(order).limit(2).offset(1).fetch());
        Assert.assertEquals(Arrays.asList("Barbara", "John"), limitAndOffset);
    }

    @Test
    public void listResults() {
        BooleanExpression filter = user.middleName.eq("X");
        QueryResults<User> users = query().where(filter).orderBy(user.firstName.asc()).limit(2).fetchResults();
        List<String> asc = getFirstNames(users.getResults());
        Assert.assertEquals(Arrays.asList("Anton", "Barbara"), asc);
        Assert.assertEquals(4, users.getTotal());
    }

    @Test
    public void no_where() {
        Assert.assertEquals(5, query().fetch().size());
    }
}

