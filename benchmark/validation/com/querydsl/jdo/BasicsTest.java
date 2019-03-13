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
package com.querydsl.jdo;


import com.google.common.collect.ImmutableList;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.jdo.test.domain.Book;
import com.querydsl.jdo.test.domain.Product;
import com.querydsl.jdo.test.domain.QBook;
import com.querydsl.jdo.test.domain.QProduct;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class BasicsTest extends AbstractJDOTest {
    private static final JDOQLTemplates templates = new JDOQLTemplates();

    private final QBook book = QBook.book;

    private final QProduct product = QProduct.product;

    private final QProduct product2 = new QProduct("product2");

    @Test
    public void serialization() throws IOException {
        JDOQuery<?> query = query();
        Assert.assertEquals("FROM com.querydsl.jdo.test.domain.Product", query.from(product).toString());
        Assert.assertEquals(("FROM com.querydsl.jdo.test.domain.Product" + "\nVARIABLES com.querydsl.jdo.test.domain.Product product2"), query.from(product2).toString());
        query.where(product.ne(product2)).select(product, product2).fetch();
        query.close();
    }

    @Test
    public void subQuerySerialization() throws IOException {
        JDOQuery<?> query = query();
        Assert.assertEquals("FROM com.querydsl.jdo.test.domain.Product", query.from(product).toString());
        Assert.assertEquals(("FROM com.querydsl.jdo.test.domain.Product" + "\nVARIABLES com.querydsl.jdo.test.domain.Product product2"), query.from(product2).toString());
    }

    @Test
    public void delete() {
        long count = query().from(product).fetchCount();
        Assert.assertEquals(0, delete(product).where(product.name.eq("XXX")).execute());
        Assert.assertEquals(count, delete(product).execute());
    }

    @Test
    public void alias() {
        Assert.assertEquals(2, query().from(product).select(product.name.as(product.name)).fetch().size());
    }

    @Test
    public void countTests() {
        Assert.assertEquals("count", 2, query().from(product).fetchCount());
    }

    @Test
    public void list_distinct() {
        // XXX List implementation of JDO provider has weird equals implementation
        Assert.assertEquals(ImmutableList.copyOf(query().from(product).orderBy(product.name.asc()).select(product.name).fetch()), ImmutableList.copyOf(query().from(product).orderBy(product.name.asc()).distinct().select(product.name).fetch()));
    }

    @Test
    public void list_distinct_two_sources() {
        try {
            // XXX List implementation of JDO provider has weird equals implementation
            Assert.assertEquals(ImmutableList.copyOf(query().from(product, product2).select(product, product2).fetch()), ImmutableList.copyOf(query().from(product, product2).distinct().select(product, product2).fetch()));
        } catch (AssertionError e) {
            Assume.assumeNoException("Unreliable test, but keep around", e);
        }
    }

    @Test
    public void single_result() {
        query().from(product).select(product).fetchFirst();
    }

    @Test
    public void single_result_with_array() {
        query().from(product).select(new Expression<?>[]{ product }).fetchFirst();
    }

    @Test
    public void factoryExpression_in_groupBy() {
        Expression<Product> productBean = Projections.bean(Product.class, product.name, product.description);
        Assert.assertFalse(query().from(product).groupBy(productBean).select(productBean).fetch().isEmpty());
    }

    @Test(expected = NonUniqueResultException.class)
    public void unique_result_throws_exception_on_multiple_results() {
        query().from(product).select(product).fetchOne();
    }

    @Test
    public void simpleTest() throws IOException {
        JDOQuery<?> query = new JDOQuery<Void>(pm, BasicsTest.templates, false);
        Assert.assertEquals("Sony Discman", query.from(product).where(product.name.eq("Sony Discman")).select(product.name).fetchOne());
        query.close();
    }

    @Test
    public void projectionTests() {
        Assert.assertEquals("Sony Discman", query().from(product).where(product.name.eq("Sony Discman")).select(product.name).fetchOne());
    }

    @Test
    public void basicTests() {
        Assert.assertEquals("list", 2, query().from(product).select(product).fetch().size());
        Assert.assertEquals("list", 2, query().from(product).select(product.name, product.description).fetch().size());
        Assert.assertEquals("list", 1, query().from(book).select(book).fetch().size());
        Assert.assertEquals("eq", 1, query(product, product.name.eq("Sony Discman")).size());
        Assert.assertEquals("instanceof ", 1, query(product, instanceOf(Book.class)).size());
    }

    @Test
    public void empty_booleanBuilder() {
        Assert.assertEquals("empty boolean builder", 2, query(product, new BooleanBuilder()).size());
    }

    @Test
    public void and() {
        Assert.assertEquals("and", 1, query(product, product.name.eq("Sony Discman").and(product.price.loe(300.0))).size());
    }

    @Test
    public void or() {
        Assert.assertEquals("or", 2, query(product, product.name.eq("Sony Discman").or(product.price.loe(300.0))).size());
    }

    @Test
    public void not() {
        Assert.assertEquals("not", 2, query(product, product.name.eq("Sony MP3 player").not()).size());
    }

    @Test
    public void numericTests() {
        // numeric
        // TODO +
        // TODO -
        // TODO *
        // TODO /
        // TODO %
        // TODO Math.abs
        // TODO Math.sqrt
    }

    @Test
    public void eq() {
        Assert.assertEquals("eq", 1, query(product, product.price.eq(200.0)).size());
        Assert.assertEquals("eq", 0, query(product, product.price.eq(100.0)).size());
    }

    @Test
    public void ne() {
        Assert.assertEquals("ne", 2, query(product, product.price.ne(100.0)).size());
    }

    @Test
    public void in_empty() {
        Assert.assertEquals(0, query(product, product.name.in(ImmutableList.<String>of())).size());
    }

    @Test
    public void not_in_empty() {
        int count = query(product, product.name.isNotNull()).size();
        Assert.assertEquals(count, query(product, product.name.notIn(ImmutableList.<String>of())).size());
    }

    @Test
    public void lt() {
        Assert.assertEquals("lt", 2, query(product, product.price.lt(300.0)).size());
    }

    @Test
    public void gt() {
        Assert.assertEquals("gt", 1, query(product, product.price.gt(100.0)).size());
    }

    @Test
    public void goe() {
        Assert.assertEquals("goe", 1, query(product, product.price.goe(100.0)).size());
    }

    @Test
    public void loe() {
        Assert.assertEquals("loe", 2, query(product, product.price.loe(300.0)).size());
    }

    @Test
    public void starts_with() {
        Assert.assertEquals("startsWith", 1, query(product, product.name.startsWith("Sony Discman")).size());
    }

    @Test
    public void matches() {
        Assert.assertEquals("matches", 1, query(product, product.name.matches("Sony.*")).size());
        Assert.assertSame(query(product, product.name.matches("Sony.*")).size(), query(product, product.name.likeIgnoreCase("sony%")).size());
    }

    @Test
    public void like() {
        Assert.assertEquals("matches", 1, query(product, product.name.like("Sony%")).size());
    }

    @Test
    public void ends_with() {
        Assert.assertEquals("endsWith", 1, query(product, product.name.endsWith("Discman")).size());
    }

    @Test
    public void to_lowerCase() {
        Assert.assertEquals("toLowerCase", 1, query(product, product.name.lower().eq("sony discman")).size());
    }

    @Test
    public void to_upperCase() {
        Assert.assertEquals("toUpperCase", 1, query(product, product.name.upper().eq("SONY DISCMAN")).size());
    }

    @Test
    public void index_of() {
        Assert.assertEquals("indexOf", 1, query(product, product.name.indexOf("S").eq(0)).size());
    }

    @Test
    public void substring1() {
        Assert.assertEquals("substring", 1, query(product, product.name.substring(5).eq("Discman")).size());
    }

    @Test
    public void substring2() {
        Assert.assertEquals("substring", 1, query(product, product.name.substring(0, 4).eq("Sony")).size());
    }
}

