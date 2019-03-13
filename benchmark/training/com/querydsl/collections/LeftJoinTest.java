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
package com.querydsl.collections;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class LeftJoinTest extends AbstractQueryTest {
    private QCat cat;

    private QCat kitten;

    private List<Cat> cats;

    @Test
    public void list() {
        List<Cat> rv = CollQueryFactory.from(cat, cats).leftJoin(cat.kittens, kitten).where(kitten.isNotNull(), cat.name.eq(kitten.name)).orderBy(cat.name.asc()).fetch();
        Assert.assertEquals(1, rv.size());
        Assert.assertEquals("Bob", rv.get(0).getName());
    }

    @Test
    public void alias_() {
        Cat cc = alias(Cat.class, "cat1");
        Cat ck = alias(Cat.class, "cat2");
        List<Cat> rv = CollQueryFactory.from($(cc), cats).leftJoin($(cc.getKittens()), $(ck)).where($(ck).isNotNull(), $(cc.getName()).eq($(ck.getName()))).fetch();
        Assert.assertFalse(rv.isEmpty());
    }

    @Test
    public void map() {
        List<Cat> rv = CollQueryFactory.from(cat, cats).leftJoin(cat.kittensByName, kitten).where(cat.name.eq(kitten.name)).orderBy(cat.name.asc()).fetch();
        Assert.assertEquals("Bob", rv.get(0).getName());
        Assert.assertEquals("Kate", rv.get(1).getName());
    }
}

