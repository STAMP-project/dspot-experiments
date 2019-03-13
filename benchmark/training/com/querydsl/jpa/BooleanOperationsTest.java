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


import com.querydsl.core.BooleanBuilder;
import org.junit.Assert;
import org.junit.Test;


public class BooleanOperationsTest extends AbstractQueryTest {
    @Test
    public void booleanOperations_or() {
        AbstractQueryTest.assertToString("cust is null or cat is null", Constants.cust.isNull().or(Constants.cat.isNull()));
    }

    @Test
    public void booleanOperations_and() {
        AbstractQueryTest.assertToString("cust is null and cat is null", Constants.cust.isNull().and(Constants.cat.isNull()));
    }

    @Test
    public void booleanOperations_not() {
        AbstractQueryTest.assertToString("not cust is null", Constants.cust.isNull().not());
    }

    @Test
    public void booleanOperations2_and() {
        Constants.cat.name.eq(Constants.cust.name.firstName).and(Constants.cat.bodyWeight.eq(Constants.kitten.bodyWeight));
    }

    @Test
    public void booleanOperations2_or() {
        Constants.cat.name.eq(Constants.cust.name.firstName).or(Constants.cat.bodyWeight.eq(Constants.kitten.bodyWeight));
    }

    @Test
    public void logicalOperations_or() {
        AbstractQueryTest.assertToString("cat = kitten or kitten = cat", Constants.cat.eq(Constants.kitten).or(Constants.kitten.eq(Constants.cat)));
    }

    @Test
    public void logicalOperations_and() {
        AbstractQueryTest.assertToString("cat = kitten and kitten = cat", Constants.cat.eq(Constants.kitten).and(Constants.kitten.eq(Constants.cat)));
    }

    @Test
    public void logicalOperations_and2() {
        AbstractQueryTest.assertToString("cat is null and (kitten is null or kitten.bodyWeight > ?1)", Constants.cat.isNull().and(Constants.kitten.isNull().or(Constants.kitten.bodyWeight.gt(10))));
    }

    @Test
    public void booleanBuilder1() {
        BooleanBuilder bb1 = new BooleanBuilder();
        bb1.and(Constants.cat.eq(Constants.cat));
        BooleanBuilder bb2 = new BooleanBuilder();
        bb2.or(Constants.cat.eq(Constants.cat));
        bb2.or(Constants.cat.eq(Constants.cat));
        AbstractQueryTest.assertToString("cat = cat and (cat = cat or cat = cat)", bb1.and(bb2));
    }

    @Test
    public void booleanBuilder2() {
        BooleanBuilder bb1 = new BooleanBuilder();
        bb1.and(Constants.cat.eq(Constants.cat));
        BooleanBuilder bb2 = new BooleanBuilder();
        bb2.or(Constants.cat.eq(Constants.cat));
        bb2.or(Constants.cat.eq(Constants.cat));
        AbstractQueryTest.assertToString("cat = cat and (cat = cat or cat = cat)", bb1.and(bb2.getValue()));
    }

    @Test
    public void booleanBuilder_with_null_in_where() {
        Assert.assertEquals("select cat\nfrom Cat cat", JPAExpressions.selectFrom(Constants.cat).where(new BooleanBuilder()).toString());
    }

    @Test
    public void booleanBuilder_with_null_in_having() {
        Assert.assertEquals("select cat\nfrom Cat cat\ngroup by cat.name", JPAExpressions.selectFrom(Constants.cat).groupBy(Constants.cat.name).having(new BooleanBuilder()).toString());
    }
}

