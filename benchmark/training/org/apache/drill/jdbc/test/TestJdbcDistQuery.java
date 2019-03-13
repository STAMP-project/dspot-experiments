/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.jdbc.test;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.List;
import org.apache.drill.categories.JdbcTest;
import org.apache.drill.jdbc.Driver;
import org.apache.drill.jdbc.JdbcTestBase;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.drill.test.TestTools;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(JdbcTest.class)
public class TestJdbcDistQuery extends JdbcTestBase {
    private static final Logger logger = LoggerFactory.getLogger(TestJdbcDistQuery.class);

    // Set a timeout unless we're debugging.
    @Rule
    public TestRule TIMEOUT = TestTools.getTimeoutRule(50000);

    static {
        Driver.load();
    }

    @Test
    public void testSimpleQuerySingleFile() throws Exception {
        testQuery("select R_REGIONKEY, R_NAME from dfs.`sample-data/regionsSF/`");
    }

    @Test
    public void testSimpleQueryMultiFile() throws Exception {
        testQuery("select R_REGIONKEY, R_NAME from dfs.`sample-data/regionsMF/`");
    }

    @Test
    public void testWhereOverSFile() throws Exception {
        testQuery("select R_REGIONKEY, R_NAME from dfs.`sample-data/regionsSF/` WHERE R_REGIONKEY = 1");
    }

    @Test
    public void testWhereOverMFile() throws Exception {
        testQuery("select R_REGIONKEY, R_NAME from dfs.`sample-data/regionsMF/` WHERE R_REGIONKEY = 1");
    }

    @Test
    public void testAggSingleFile() throws Exception {
        testQuery("select R_REGIONKEY from dfs.`sample-data/regionsSF/` group by R_REGIONKEY");
    }

    @Test
    public void testAggMultiFile() throws Exception {
        testQuery("select R_REGIONKEY from dfs.`sample-data/regionsMF/` group by R_REGIONKEY");
    }

    @Test
    public void testAggOrderByDiffGKeyMultiFile() throws Exception {
        testQuery(("select R_REGIONKEY, SUM(cast(R_REGIONKEY AS int)) As S " + ("from dfs.`sample-data/regionsMF/` " + "group by R_REGIONKEY ORDER BY S")));
    }

    @Test
    public void testAggOrderBySameGKeyMultiFile() throws Exception {
        testQuery(("select R_REGIONKEY, SUM(cast(R_REGIONKEY AS int)) As S " + (("from dfs.`sample-data/regionsMF/` " + "group by R_REGIONKEY ") + "ORDER BY R_REGIONKEY")));
    }

    // NPE at ExternalSortBatch.java : 151
    @Test
    public void testSortSingleFile() throws Exception {
        testQuery(("select R_REGIONKEY " + ("from dfs.`sample-data/regionsSF/` " + "order by R_REGIONKEY")));
    }

    // NPE at ExternalSortBatch.java : 151
    @Test
    public void testSortMultiFile() throws Exception {
        testQuery(("select R_REGIONKEY " + ("from dfs.`sample-data/regionsMF/` " + "order by R_REGIONKEY")));
    }

    @Test
    public void testSortMFileWhere() throws Exception {
        testQuery(("select R_REGIONKEY " + (("from dfs.`sample-data/regionsMF/` " + "WHERE R_REGIONKEY = 1 ") + "order by R_REGIONKEY")));
    }

    @Test
    public void testSelectLimit() throws Exception {
        testQuery(("select R_REGIONKEY, R_NAME " + ("from dfs.`sample-data/regionsMF/` " + "limit 2")));
    }

    @Test
    public void testSchemaForEmptyResultSet() throws Exception {
        String query = "select fullname, occupation, postal_code from cp.`customer.json` where 0 = 1";
        try (Connection c = JdbcTestBase.connect()) {
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery(query);
            ResultSetMetaData md = r.getMetaData();
            List<String> columns = Lists.newArrayList();
            for (int i = 1; i <= (md.getColumnCount()); i++) {
                columns.add(md.getColumnName(i));
            }
            String[] expected = new String[]{ "fullname", "occupation", "postal_code" };
            Assert.assertEquals(3, md.getColumnCount());
            Assert.assertArrayEquals(expected, columns.toArray());
            // TODO:  Purge nextUntilEnd(...) and calls when remaining fragment race
            // conditions are fixed (not just DRILL-2245 fixes).
            TestJdbcDistQuery.nextUntilEnd(r);
        }
    }
}

