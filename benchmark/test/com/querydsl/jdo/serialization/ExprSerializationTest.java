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
package com.querydsl.jdo.serialization;


import com.querydsl.jdo.test.domain.Product;
import com.querydsl.jdo.test.domain.QBook;
import com.querydsl.jdo.test.domain.QProduct;
import com.querydsl.jdo.test.domain.QStore;
import org.junit.Assert;
import org.junit.Test;


public class ExprSerializationTest {
    private QBook book = QBook.book;

    private QProduct product = QProduct.product;

    private QStore store = QStore.store;

    @Test
    public void instanceOf() {
        Assert.assertEquals("product instanceof com.querydsl.jdo.test.domain.Book", serialize(product.instanceOf(com.querydsl.jdo.test.domain.Book.class)));
    }

    @Test
    public void eq() {
        Assert.assertEquals("this.name == product.name", serialize(book.name.eq(product.name)));
        Assert.assertEquals("this == product", serialize(book.eq(product)));
    }

    @Test
    public void aggregation() {
        Assert.assertEquals("sum(product.price)", serialize(product.price.sum()));
        Assert.assertEquals("min(product.price)", serialize(product.price.min()));
        Assert.assertEquals("max(product.price)", serialize(product.price.max()));
        Assert.assertEquals("avg(product.price)", serialize(product.price.avg()));
        Assert.assertEquals("count(product.price)", serialize(product.price.count()));
    }

    @Test
    public void booleanTests() {
        // boolean
        Assert.assertEquals("product.name == a1 && product.price <= a2", serialize(product.name.eq("Sony Discman").and(product.price.loe(300.0))));
        Assert.assertEquals("product.name == a1 || product.price <= a2", serialize(product.name.eq("Sony Discman").or(product.price.loe(300.0))));
        Assert.assertEquals("!(product.name == a1)", serialize(product.name.eq("Sony MP3 player").not()));
    }

    @Test
    public void collectionTests() {
        Product product = new Product();
        // collection
        Assert.assertEquals("store.products.contains(a1)", serialize(store.products.contains(product)));
        // assertEquals("store.products.get(0) == a1",
        // serialize(store.products.get(0).eq(product)));
        Assert.assertEquals("store.products.isEmpty()", serialize(store.products.isEmpty()));
        Assert.assertEquals("!store.products.isEmpty()", serialize(store.products.isNotEmpty()));
        Assert.assertEquals("store.products.size() == a1", serialize(store.products.size().eq(1)));
    }

    @Test
    public void mapTests() {
        Assert.assertEquals("store.productsByName.containsKey(a1)", serialize(store.productsByName.containsKey("")));
        Assert.assertEquals("store.productsByName.containsValue(a1)", serialize(store.productsByName.containsValue(new Product())));
        Assert.assertEquals("store.productsByName.isEmpty()", serialize(store.productsByName.isEmpty()));
        Assert.assertEquals("!store.productsByName.isEmpty()", serialize(store.productsByName.isNotEmpty()));
    }

    @Test
    public void numericTests() {
        // numeric
        Assert.assertEquals("product.price == a1", serialize(product.price.eq(200.0)));
        Assert.assertEquals("product.price != a1", serialize(product.price.ne(100.0)));
        Assert.assertEquals("product.price > a1", serialize(product.price.gt(100.0)));
        Assert.assertEquals("product.price < a1", serialize(product.price.lt(300.0)));
        Assert.assertEquals("product.price >= a1", serialize(product.price.goe(100.0)));
        Assert.assertEquals("product.price <= a1", serialize(product.price.loe(300.0)));
        // TODO +
        // TODO -
        // TODO *
        // TODO /
        // TODO %
        // TODO Math.abs
        // TODO Math.sqrt
    }

    @Test
    public void stringTests() {
        // string
        Assert.assertEquals("product.name.startsWith(a1)", serialize(product.name.startsWith("Sony Discman")));
        Assert.assertEquals("product.name.endsWith(a1)", serialize(product.name.endsWith("Discman")));
        Assert.assertEquals("product.name.toLowerCase() == a1", serialize(product.name.lower().eq("sony discman")));
        Assert.assertEquals("product.name.toUpperCase() == a1", serialize(product.name.upper().eq("SONY DISCMAN")));
        Assert.assertEquals("product.name.indexOf(a1) == a2", serialize(product.name.indexOf("S").eq(0)));
        // TODO indexOf
        // TODO matches
        Assert.assertEquals("product.name.substring(a1,a2) == a3", serialize(product.name.substring(0, 4).eq("Sony")));
        Assert.assertEquals("product.name.substring(a1) == a2", serialize(product.name.substring(5).eq("Discman")));
        Assert.assertEquals("product.name == \"\"", serialize(product.name.isEmpty()));
        Assert.assertEquals("!(product.name == \"\")", serialize(product.name.isNotEmpty()));
    }
}

