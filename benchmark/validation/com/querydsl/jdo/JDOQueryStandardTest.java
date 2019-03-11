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


import com.querydsl.core.types.ParamNotSetException;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Param;
import com.querydsl.jdo.test.domain.Product;
import com.querydsl.jdo.test.domain.QBook;
import com.querydsl.jdo.test.domain.QProduct;
import com.querydsl.jdo.test.domain.QStore;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static Module.JDO;
import static Target.H2;


public class JDOQueryStandardTest extends AbstractJDOTest {
    public static class Projection {
        public Projection(String str) {
        }
    }

    private static final Date publicationDate;

    private static final java.sql.Date date;

    private static final Time time;

    static {
        Calendar cal = Calendar.getInstance();
        cal.set(2000, 1, 2, 3, 4);
        cal.set(Calendar.MILLISECOND, 0);
        publicationDate = cal.getTime();
        date = new java.sql.Date(cal.getTimeInMillis());
        time = new Time(cal.getTimeInMillis());
    }

    private static String productName = "ABCD";

    private static String otherName = "ABC0";

    private final QueryExecution standardTest = new QueryExecution(JDO, H2) {
        @Override
        protected Fetchable createQuery() {
            return query().from(store, product, otherProduct).select(store, product, otherProduct);
        }

        @Override
        protected Fetchable createQuery(Predicate filter) {
            return query().from(store, product, otherProduct).where(filter).select(store, product, otherProduct);
        }
    };

    private final QProduct product = QProduct.product;

    private final QProduct otherProduct = new QProduct("otherProduct");

    private final QStore store = QStore.store;

    private final QStore otherStore = new QStore("otherStore");

    @Test
    public void standardTest() {
        Product p = query().from(product).where(product.name.eq(JDOQueryStandardTest.productName)).limit(1).select(product).fetchOne();
        Product p2 = query().from(product).where(product.name.startsWith(JDOQueryStandardTest.otherName)).limit(1).select(product).fetchOne();
        standardTest.noProjections();
        standardTest.noCounts();
        standardTest.runBooleanTests(product.name.isNull(), otherProduct.price.lt(10.0));
        standardTest.runCollectionTests(store.products, otherStore.products, p, p2);
        standardTest.runDateTests(product.dateField, otherProduct.dateField, JDOQueryStandardTest.date);
        standardTest.runDateTimeTests(product.publicationDate, otherProduct.publicationDate, JDOQueryStandardTest.publicationDate);
        // NO list support in JDOQL
        // testData.listTests(store.products, otherStore.products, p);
        standardTest.runMapTests(store.productsByName, otherStore.productsByName, JDOQueryStandardTest.productName, p, "X", p2);
        standardTest.runNumericCasts(product.price, otherProduct.price, 200.0);
        standardTest.runNumericTests(product.amount, otherProduct.amount, 2);
        standardTest.runStringTests(product.name, otherProduct.name, JDOQueryStandardTest.productName);
        standardTest.runTimeTests(product.timeField, otherProduct.timeField, JDOQueryStandardTest.time);
        standardTest.report();
    }

    @Test
    public void tupleProjection() {
        List<Tuple> tuples = query().from(product).select(product.name, product.price).fetch();
        Assert.assertFalse(tuples.isEmpty());
        for (Tuple tuple : tuples) {
            Assert.assertNotNull(tuple);
            Assert.assertNotNull(tuple.get(product.name));
            Assert.assertNotNull(tuple.get(product.price));
            Assert.assertNotNull(tuple.get(0, String.class));
            Assert.assertNotNull(tuple.get(1, Double.class));
        }
    }

    @Test
    public void params() {
        Param<String> name = new Param<String>(String.class, "name");
        Assert.assertEquals("ABC0", query().from(product).where(product.name.eq(name)).set(name, "ABC0").select(product.name).fetchFirst());
    }

    @Test
    public void params_anon() {
        Param<String> name = new Param<String>(String.class);
        Assert.assertEquals("ABC0", query().from(product).where(product.name.eq(name)).set(name, "ABC0").select(product.name).fetchFirst());
    }

    @Test(expected = ParamNotSetException.class)
    public void params_not_set() {
        Param<String> name = new Param<String>(String.class, "name");
        Assert.assertEquals("ABC0", query().from(product).where(product.name.eq(name)).select(product.name).fetchFirst());
    }

    @Test
    public void detatchCollection() {
        new JDOQuery<com.querydsl.jdo.test.domain.Book>(pm, true).select(QBook.book).from(QBook.book).fetch();
    }
}

