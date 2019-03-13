/**
 * Copyright 2006-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.item.database.support;


import Order.ASCENDING;
import Order.DESCENDING;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.database.Order;


/**
 *
 *
 * @author Thomas Risberg
 * @author Michael Minella
 */
public abstract class AbstractSqlPagingQueryProviderTests {
    protected AbstractSqlPagingQueryProvider pagingQueryProvider;

    protected int pageSize;

    @Test
    public void testQueryContainsSortKey() {
        String s = pagingQueryProvider.generateFirstPageQuery(pageSize).toLowerCase();
        Assert.assertTrue(("Wrong query: " + s), s.contains("id asc"));
    }

    @Test
    public void testQueryContainsSortKeyDesc() {
        pagingQueryProvider.getSortKeys().put("id", DESCENDING);
        String s = pagingQueryProvider.generateFirstPageQuery(pageSize).toLowerCase();
        Assert.assertTrue(("Wrong query: " + s), s.contains("id desc"));
    }

    @Test
    public void testGenerateFirstPageQueryWithMultipleSortKeys() {
        Map<String, Order> sortKeys = new LinkedHashMap<>();
        sortKeys.put("name", ASCENDING);
        sortKeys.put("id", DESCENDING);
        pagingQueryProvider.setSortKeys(sortKeys);
        String s = pagingQueryProvider.generateFirstPageQuery(pageSize);
        Assert.assertEquals(getFirstPageSqlWithMultipleSortKeys(), s);
    }

    @Test
    public void testGenerateRemainingPagesQueryWithMultipleSortKeys() {
        Map<String, Order> sortKeys = new LinkedHashMap<>();
        sortKeys.put("name", ASCENDING);
        sortKeys.put("id", DESCENDING);
        pagingQueryProvider.setSortKeys(sortKeys);
        String s = pagingQueryProvider.generateRemainingPagesQuery(pageSize);
        Assert.assertEquals(getRemainingSqlWithMultipleSortKeys(), s);
    }

    @Test
    public void testGenerateJumpToItemQueryWithMultipleSortKeys() {
        Map<String, Order> sortKeys = new LinkedHashMap<>();
        sortKeys.put("name", ASCENDING);
        sortKeys.put("id", DESCENDING);
        pagingQueryProvider.setSortKeys(sortKeys);
        String s = pagingQueryProvider.generateJumpToItemQuery(145, pageSize);
        Assert.assertEquals(getJumpToItemQueryWithMultipleSortKeys(), s);
    }

    @Test
    public void testGenerateJumpToItemQueryForFirstPageWithMultipleSortKeys() {
        Map<String, Order> sortKeys = new LinkedHashMap<>();
        sortKeys.put("name", ASCENDING);
        sortKeys.put("id", DESCENDING);
        pagingQueryProvider.setSortKeys(sortKeys);
        String s = pagingQueryProvider.generateJumpToItemQuery(45, pageSize);
        Assert.assertEquals(getJumpToItemQueryForFirstPageWithMultipleSortKeys(), s);
    }
}

