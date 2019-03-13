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


import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jdo.test.domain.QProduct;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class OrderingTest extends AbstractJDOTest {
    private QProduct product = QProduct.product;

    @Test
    public void order_asc() {
        List<String> namesAsc = query().from(product).orderBy(product.name.asc(), product.description.desc()).select(product.name).fetch();
        Assert.assertEquals(30, namesAsc.size());
        String prev = null;
        for (String name : namesAsc) {
            if (prev != null) {
                Assert.assertTrue(((prev.compareTo(name)) < 0));
            }
            prev = name;
        }
    }

    @Test
    public void order_desc() {
        List<String> namesDesc = query().from(product).orderBy(product.name.desc()).select(product.name).fetch();
        Assert.assertEquals(30, namesDesc.size());
        String prev = null;
        for (String name : namesDesc) {
            if (prev != null) {
                Assert.assertTrue(((prev.compareTo(name)) > 0));
            }
            prev = name;
        }
    }

    @Test
    public void tabularResults() {
        List<Tuple> rows = query().from(product).orderBy(product.name.asc()).select(product.name, product.description).fetch();
        Assert.assertEquals(30, rows.size());
        for (Tuple row : rows) {
            Assert.assertEquals(row.get(0, String.class).substring(1), row.get(1, String.class).substring(1));
        }
    }

    @Test
    public void limit_order_asc() {
        Assert.assertEquals(Arrays.asList("A0", "A1"), query().from(product).orderBy(product.name.asc()).limit(2).select(product.name).fetch());
    }

    @Test
    public void limit_order_desc() {
        Assert.assertEquals(Arrays.asList("C9", "C8"), query().from(product).orderBy(product.name.desc()).limit(2).select(product.name).fetch());
    }

    @Test
    public void queryResults() {
        QueryResults<String> results = query().from(product).orderBy(product.name.asc()).limit(2).select(product.name).fetchResults();
        Assert.assertEquals(Arrays.asList("A0", "A1"), results.getResults());
        Assert.assertEquals(30, results.getTotal());
    }
}

