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


import com.querydsl.jdo.test.domain.QProduct;
import com.querydsl.jdo.test.domain.QStore;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.jdo.Query;
import org.junit.Assert;
import org.junit.Test;


public class FetchPlanTest extends AbstractJDOTest {
    private JDOQuery<?> query;

    @SuppressWarnings("unchecked")
    @Test
    public void listProducts() throws Exception {
        QProduct product = QProduct.product;
        query = query();
        query.from(product).where(product.name.startsWith("A")).addFetchGroup("myfetchgroup1").addFetchGroup("myfetchgroup2").setMaxFetchDepth(2).select(product).fetch();
        // query.close();
        Field queriesField = AbstractJDOQuery.class.getDeclaredField("queries");
        queriesField.setAccessible(true);
        List<Query> queries = ((List<Query>) (queriesField.get(query)));
        Query jdoQuery = queries.get(0);
        Assert.assertEquals(new HashSet<String>(Arrays.asList("myfetchgroup1", "myfetchgroup2")), jdoQuery.getFetchPlan().getGroups());
        Assert.assertEquals(2, jdoQuery.getFetchPlan().getMaxFetchDepth());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void listStores() throws Exception {
        QStore store = QStore.store;
        query = query();
        query.from(store).addFetchGroup("products").select(store).fetch();
        Field queriesField = AbstractJDOQuery.class.getDeclaredField("queries");
        queriesField.setAccessible(true);
        List<Query> queries = ((List<Query>) (queriesField.get(query)));
        Query jdoQuery = queries.get(0);
        Assert.assertEquals(new HashSet<String>(Arrays.asList("products")), jdoQuery.getFetchPlan().getGroups());
        Assert.assertEquals(1, jdoQuery.getFetchPlan().getMaxFetchDepth());
    }
}

