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


import org.junit.Assert;
import org.junit.Test;


public class JoinFlagsTest extends AbstractQueryTest {
    @Test
    public void fetchAll() {
        QueryHelper query1 = query().from(Constants.cat).fetchAll().where(Constants.cat.name.isNotNull());
        Assert.assertEquals("select cat\nfrom Cat cat fetch all properties\nwhere cat.name is not null", query1.toString());
    }

    @Test
    public void fetchAll2() {
        QueryHelper query2 = query().from(Constants.cat).fetchAll().from(Constants.cat1).fetchAll();
        Assert.assertEquals("select cat\nfrom Cat cat fetch all properties, Cat cat1 fetch all properties", query2.toString());
    }

    @Test
    public void fetch() {
        QueryHelper query = query().from(Constants.cat).innerJoin(Constants.cat.mate, Constants.cat1).fetchJoin();
        Assert.assertEquals("select cat\nfrom Cat cat\n  inner join fetch cat.mate as cat1", query.toString());
    }

    @Test
    public void rightJoin() {
        QueryHelper query = query().from(Constants.cat).rightJoin(Constants.cat.mate, Constants.cat1);
        Assert.assertEquals("select cat\nfrom Cat cat\n  right join cat.mate as cat1", query.toString());
    }
}

