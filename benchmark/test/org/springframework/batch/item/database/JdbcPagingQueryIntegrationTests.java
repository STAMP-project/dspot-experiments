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
package org.springframework.batch.item.database;


import Order.ASCENDING;
import Order.DESCENDING;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.database.support.AbstractSqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;


/**
 *
 *
 * @author Dave Syer
 * @author Michael Minella
 * @since 2.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "JdbcPagingItemReaderCommonTests-context.xml")
public class JdbcPagingQueryIntegrationTests {
    private static Log logger = LogFactory.getLog(JdbcPagingQueryIntegrationTests.class);

    @Autowired
    private DataSource dataSource;

    private int maxId;

    private JdbcTemplate jdbcTemplate;

    private int itemCount = 9;

    private int pageSize = 2;

    @Test
    public void testQueryFromStart() throws Exception {
        PagingQueryProvider queryProvider = getPagingQueryProvider();
        int total = JdbcTestUtils.countRowsInTable(jdbcTemplate, "T_FOOS");
        Assert.assertTrue((total > (pageSize)));
        int pages = total / (pageSize);
        int count = 0;
        List<Map<String, Object>> list = jdbcTemplate.queryForList(queryProvider.generateFirstPageQuery(pageSize));
        JdbcPagingQueryIntegrationTests.logger.debug(("First page result: " + list));
        Assert.assertEquals(pageSize, list.size());
        count += pageSize;
        Map<String, Object> oldValues = null;
        while (count < (pages * (pageSize))) {
            Map<String, Object> startAfterValues = getStartAfterValues(queryProvider, list);
            Assert.assertNotSame(oldValues, startAfterValues);
            list = jdbcTemplate.queryForList(queryProvider.generateRemainingPagesQuery(pageSize), getParameterList(null, startAfterValues).toArray());
            Assert.assertEquals(pageSize, list.size());
            count += pageSize;
            oldValues = startAfterValues;
        } 
        if (count < total) {
            Map<String, Object> startAfterValues = getStartAfterValues(queryProvider, list);
            list = jdbcTemplate.queryForList(queryProvider.generateRemainingPagesQuery(pageSize), getParameterList(null, startAfterValues).toArray());
            Assert.assertEquals((total - (pages * (pageSize))), list.size());
            count += list.size();
        }
        Assert.assertEquals(total, count);
    }

    @Test
    public void testQueryFromStartWithGroupBy() throws Exception {
        AbstractSqlPagingQueryProvider queryProvider = ((AbstractSqlPagingQueryProvider) (getPagingQueryProvider()));
        Map<String, Order> sortKeys = new LinkedHashMap<>();
        sortKeys.put("NAME", ASCENDING);
        sortKeys.put("CODE", DESCENDING);
        queryProvider.setSortKeys(sortKeys);
        queryProvider.setSelectClause("select NAME, CODE, sum(VALUE)");
        queryProvider.setGroupClause("NAME, CODE");
        int count = 0;
        int total = 5;
        List<Map<String, Object>> list = jdbcTemplate.queryForList(queryProvider.generateFirstPageQuery(pageSize));
        JdbcPagingQueryIntegrationTests.logger.debug(("First page result: " + list));
        Assert.assertEquals(pageSize, list.size());
        count += pageSize;
        Map<String, Object> oldValues = null;
        while (count < total) {
            Map<String, Object> startAfterValues = getStartAfterValues(queryProvider, list);
            Assert.assertNotSame(oldValues, startAfterValues);
            list = jdbcTemplate.queryForList(queryProvider.generateRemainingPagesQuery(pageSize), getParameterList(null, startAfterValues).toArray());
            count += list.size();
            if ((list.size()) < (pageSize)) {
                Assert.assertEquals(1, list.size());
            } else {
                Assert.assertEquals(pageSize, list.size());
            }
            oldValues = startAfterValues;
        } 
        Assert.assertEquals(total, count);
    }
}

