/**
 * Copyright 2006-2015 the original author or authors.
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


import Order.DESCENDING;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.database.Order;
import org.springframework.util.StringUtils;


/**
 *
 *
 * @author Thomas Risberg
 * @author Dave Syer
 * @author Michael Minella
 * @since 2.0
 */
public class SqlPagingQueryUtilsTests {
    private Map<String, Order> sortKeys;

    @Test
    public void testGenerateLimitSqlQuery() {
        AbstractSqlPagingQueryProvider qp = new SqlPagingQueryUtilsTests.TestSqlPagingQueryProvider("FOO", "BAR", sortKeys);
        Assert.assertEquals("SELECT FOO FROM BAR ORDER BY ID ASC LIMIT 100", SqlPagingQueryUtils.generateLimitSqlQuery(qp, false, "LIMIT 100"));
        Assert.assertEquals("SELECT FOO FROM BAR WHERE ((ID > ?)) ORDER BY ID ASC LIMIT 100", SqlPagingQueryUtils.generateLimitSqlQuery(qp, true, "LIMIT 100"));
        qp.setWhereClause("BAZ IS NOT NULL");
        Assert.assertEquals("SELECT FOO FROM BAR WHERE BAZ IS NOT NULL ORDER BY ID ASC LIMIT 100", SqlPagingQueryUtils.generateLimitSqlQuery(qp, false, "LIMIT 100"));
        Assert.assertEquals("SELECT FOO FROM BAR WHERE (BAZ IS NOT NULL) AND ((ID > ?)) ORDER BY ID ASC LIMIT 100", SqlPagingQueryUtils.generateLimitSqlQuery(qp, true, "LIMIT 100"));
    }

    @Test
    public void testGenerateTopSqlQuery() {
        AbstractSqlPagingQueryProvider qp = new SqlPagingQueryUtilsTests.TestSqlPagingQueryProvider("FOO", "BAR", sortKeys);
        Assert.assertEquals("SELECT TOP 100 FOO FROM BAR ORDER BY ID ASC", SqlPagingQueryUtils.generateTopSqlQuery(qp, false, "TOP 100"));
        Assert.assertEquals("SELECT TOP 100 FOO FROM BAR WHERE ((ID > ?)) ORDER BY ID ASC", SqlPagingQueryUtils.generateTopSqlQuery(qp, true, "TOP 100"));
        qp.setWhereClause("BAZ IS NOT NULL");
        Assert.assertEquals("SELECT TOP 100 FOO FROM BAR WHERE BAZ IS NOT NULL ORDER BY ID ASC", SqlPagingQueryUtils.generateTopSqlQuery(qp, false, "TOP 100"));
        Assert.assertEquals("SELECT TOP 100 FOO FROM BAR WHERE (BAZ IS NOT NULL) AND ((ID > ?)) ORDER BY ID ASC", SqlPagingQueryUtils.generateTopSqlQuery(qp, true, "TOP 100"));
    }

    @Test
    public void testGenerateRowNumSqlQuery() {
        AbstractSqlPagingQueryProvider qp = new SqlPagingQueryUtilsTests.TestSqlPagingQueryProvider("FOO", "BAR", sortKeys);
        Assert.assertEquals("SELECT * FROM (SELECT FOO FROM BAR ORDER BY ID ASC) WHERE ROWNUMBER <= 100", SqlPagingQueryUtils.generateRowNumSqlQuery(qp, false, "ROWNUMBER <= 100"));
        Assert.assertEquals("SELECT * FROM (SELECT FOO FROM BAR ORDER BY ID ASC) WHERE ROWNUMBER <= 100 AND ((ID > ?))", SqlPagingQueryUtils.generateRowNumSqlQuery(qp, true, "ROWNUMBER <= 100"));
        qp.setWhereClause("BAZ IS NOT NULL");
        Assert.assertEquals("SELECT * FROM (SELECT FOO FROM BAR WHERE BAZ IS NOT NULL ORDER BY ID ASC) WHERE ROWNUMBER <= 100", SqlPagingQueryUtils.generateRowNumSqlQuery(qp, false, "ROWNUMBER <= 100"));
        Assert.assertEquals("SELECT * FROM (SELECT FOO FROM BAR WHERE BAZ IS NOT NULL ORDER BY ID ASC) WHERE ROWNUMBER <= 100 AND ((ID > ?))", SqlPagingQueryUtils.generateRowNumSqlQuery(qp, true, "ROWNUMBER <= 100"));
    }

    @Test
    public void testGenerateRowNumSqlQueryWithNesting() {
        AbstractSqlPagingQueryProvider qp = new SqlPagingQueryUtilsTests.TestSqlPagingQueryProvider("FOO", "BAR", sortKeys);
        Assert.assertEquals("SELECT FOO FROM (SELECT FOO, ROWNUM as TMP_ROW_NUM FROM (SELECT FOO FROM BAR ORDER BY ID ASC)) WHERE ROWNUMBER <= 100", SqlPagingQueryUtils.generateRowNumSqlQueryWithNesting(qp, "FOO", false, "ROWNUMBER <= 100"));
    }

    @Test
    public void testGenerateTopSqlQueryDescending() {
        sortKeys.put("ID", DESCENDING);
        AbstractSqlPagingQueryProvider qp = new SqlPagingQueryUtilsTests.TestSqlPagingQueryProvider("FOO", "BAR", sortKeys);
        Assert.assertEquals("SELECT TOP 100 FOO FROM BAR ORDER BY ID DESC", SqlPagingQueryUtils.generateTopSqlQuery(qp, false, "TOP 100"));
        Assert.assertEquals("SELECT TOP 100 FOO FROM BAR WHERE ((ID < ?)) ORDER BY ID DESC", SqlPagingQueryUtils.generateTopSqlQuery(qp, true, "TOP 100"));
        qp.setWhereClause("BAZ IS NOT NULL");
        Assert.assertEquals("SELECT TOP 100 FOO FROM BAR WHERE BAZ IS NOT NULL ORDER BY ID DESC", SqlPagingQueryUtils.generateTopSqlQuery(qp, false, "TOP 100"));
        Assert.assertEquals("SELECT TOP 100 FOO FROM BAR WHERE (BAZ IS NOT NULL) AND ((ID < ?)) ORDER BY ID DESC", SqlPagingQueryUtils.generateTopSqlQuery(qp, true, "TOP 100"));
    }

    @Test
    public void testGenerateRowNumSqlQueryDescending() {
        sortKeys.put("ID", DESCENDING);
        AbstractSqlPagingQueryProvider qp = new SqlPagingQueryUtilsTests.TestSqlPagingQueryProvider("FOO", "BAR", sortKeys);
        Assert.assertEquals("SELECT * FROM (SELECT FOO FROM BAR ORDER BY ID DESC) WHERE ROWNUMBER <= 100", SqlPagingQueryUtils.generateRowNumSqlQuery(qp, false, "ROWNUMBER <= 100"));
        Assert.assertEquals("SELECT * FROM (SELECT FOO FROM BAR ORDER BY ID DESC) WHERE ROWNUMBER <= 100 AND ((ID < ?))", SqlPagingQueryUtils.generateRowNumSqlQuery(qp, true, "ROWNUMBER <= 100"));
        qp.setWhereClause("BAZ IS NOT NULL");
        Assert.assertEquals("SELECT * FROM (SELECT FOO FROM BAR WHERE BAZ IS NOT NULL ORDER BY ID DESC) WHERE ROWNUMBER <= 100", SqlPagingQueryUtils.generateRowNumSqlQuery(qp, false, "ROWNUMBER <= 100"));
        Assert.assertEquals("SELECT * FROM (SELECT FOO FROM BAR WHERE BAZ IS NOT NULL ORDER BY ID DESC) WHERE ROWNUMBER <= 100 AND ((ID < ?))", SqlPagingQueryUtils.generateRowNumSqlQuery(qp, true, "ROWNUMBER <= 100"));
    }

    @Test
    public void testGenerateLimitJumpToQuery() {
        AbstractSqlPagingQueryProvider qp = new SqlPagingQueryUtilsTests.TestSqlPagingQueryProvider("FOO", "BAR", sortKeys);
        Assert.assertEquals("SELECT ID FROM BAR ORDER BY ID ASC LIMIT 100, 1", SqlPagingQueryUtils.generateLimitJumpToQuery(qp, "LIMIT 100, 1"));
        qp.setWhereClause("BAZ IS NOT NULL");
        Assert.assertEquals("SELECT ID FROM BAR WHERE BAZ IS NOT NULL ORDER BY ID ASC LIMIT 100, 1", SqlPagingQueryUtils.generateLimitJumpToQuery(qp, "LIMIT 100, 1"));
    }

    @Test
    public void testGenerateTopJumpToQuery() {
        AbstractSqlPagingQueryProvider qp = new SqlPagingQueryUtilsTests.TestSqlPagingQueryProvider("FOO", "BAR", sortKeys);
        Assert.assertEquals("SELECT TOP 100, 1 ID FROM BAR ORDER BY ID ASC", SqlPagingQueryUtils.generateTopJumpToQuery(qp, "TOP 100, 1"));
        qp.setWhereClause("BAZ IS NOT NULL");
        Assert.assertEquals("SELECT TOP 100, 1 ID FROM BAR WHERE BAZ IS NOT NULL ORDER BY ID ASC", SqlPagingQueryUtils.generateTopJumpToQuery(qp, "TOP 100, 1"));
    }

    @Test
    public void testGenerateTopJumpQueryDescending() {
        sortKeys.put("ID", DESCENDING);
        AbstractSqlPagingQueryProvider qp = new SqlPagingQueryUtilsTests.TestSqlPagingQueryProvider("FOO", "BAR", sortKeys);
        String query = SqlPagingQueryUtils.generateTopJumpToQuery(qp, "TOP 100, 1");
        Assert.assertTrue(("Wrong query: " + query), query.contains("ID DESC"));
        Assert.assertEquals(("Wrong query: " + query), 0, StringUtils.countOccurrencesOf(query, "ASC"));
        Assert.assertEquals(("Wrong query: " + query), 1, StringUtils.countOccurrencesOf(query, "DESC"));
        qp.setWhereClause("BAZ IS NOT NULL");
        Assert.assertTrue(("Wrong query: " + query), query.contains("ID DESC"));
    }

    @Test
    public void testGenerateLimitJumpQueryDescending() {
        sortKeys.put("ID", DESCENDING);
        AbstractSqlPagingQueryProvider qp = new SqlPagingQueryUtilsTests.TestSqlPagingQueryProvider("FOO", "BAR", sortKeys);
        String query = SqlPagingQueryUtils.generateLimitJumpToQuery(qp, "LIMIT 100, 1");
        Assert.assertTrue(("Wrong query: " + query), query.contains("ID DESC"));
        Assert.assertEquals(("Wrong query: " + query), 0, StringUtils.countOccurrencesOf(query, "ASC"));
        Assert.assertEquals(("Wrong query: " + query), 1, StringUtils.countOccurrencesOf(query, "DESC"));
        qp.setWhereClause("BAZ IS NOT NULL");
        Assert.assertTrue(("Wrong query: " + query), query.contains("ID DESC"));
    }

    private static class TestSqlPagingQueryProvider extends AbstractSqlPagingQueryProvider {
        public TestSqlPagingQueryProvider(String select, String from, Map<String, Order> sortKeys) {
            setSelectClause(select);
            setFromClause(from);
            setSortKeys(sortKeys);
        }

        @Override
        public String generateFirstPageQuery(int pageSize) {
            return null;
        }

        @Override
        public String generateRemainingPagesQuery(int pageSize) {
            return null;
        }

        @Override
        public String generateJumpToItemQuery(int itemIndex, int pageSize) {
            return null;
        }
    }
}

