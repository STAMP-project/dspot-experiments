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


public class CollectionTest {
    private final QCat cat = new QCat("cat");

    private final QCat other = new QCat("other");

    private List<Cat> cats;

    @Test
    public void join() {
        Assert.assertEquals("4", CollQueryFactory.from(cat, cats).innerJoin(cat.kittens, other).where(other.name.eq("4")).select(cat.name).fetchFirst());
    }

    @Test
    public void join_from_two_sources() {
        QCat catKittens = new QCat("cat_kittens");
        QCat otherKittens = new QCat("other_kittens");
        Assert.assertEquals(30, CollQueryFactory.from(cat, cats).from(other, cats).innerJoin(cat.kittens, catKittens).innerJoin(other.kittens, otherKittens).where(catKittens.eq(otherKittens)).fetchCount());
    }

    @Test
    public void any_uniqueResult() {
        Assert.assertEquals("4", CollQueryFactory.from(cat, cats).where(cat.kittens.any().name.eq("4")).select(cat.name).fetchOne());
    }

    @Test
    public void any_count() {
        Assert.assertEquals(4, CollQueryFactory.from(cat, cats).where(cat.kittens.any().name.isNotNull()).fetchCount());
    }

    @Test
    public void any_two_levels() {
        Assert.assertEquals(4, CollQueryFactory.from(cat, cats).where(cat.kittens.any().kittens.any().isNotNull()).fetchCount());
    }

    @Test
    public void any_two_levels2() {
        Assert.assertEquals(4, CollQueryFactory.from(cat, cats).where(cat.kittens.any().name.isNotNull(), cat.kittens.any().kittens.any().isNotNull()).fetchCount());
    }

    @Test
    public void any_from_two_sources() {
        Assert.assertEquals(16, CollQueryFactory.from(cat, cats).from(other, cats).where(cat.kittens.any().name.eq(other.kittens.any().name)).fetchCount());
    }

    @Test
    public void list_size() {
        Assert.assertEquals(4, CollQueryFactory.from(cat, cats).where(cat.kittens.size().gt(0)).fetchCount());
        Assert.assertEquals(2, CollQueryFactory.from(cat, cats).where(cat.kittens.size().gt(2)).fetchCount());
    }

    @Test
    public void list_is_empty() {
        Assert.assertEquals(0, CollQueryFactory.from(cat, cats).where(cat.kittens.isEmpty()).fetchCount());
    }
}

