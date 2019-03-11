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
package org.apache.drill.exec.store.jdbc;


import ExecConstants.SLICE_TARGET;
import StoragePluginTestUtils.DFS_PLUGIN_NAME;
import java.math.BigDecimal;
import org.apache.drill.categories.JdbcStorageTest;
import org.apache.drill.exec.expr.fn.impl.DateUtility;
import org.apache.drill.test.ClusterTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * JDBC storage plugin tests against H2.
 */
@Category(JdbcStorageTest.class)
public class TestJdbcPluginWithH2IT extends ClusterTest {
    private static final String TABLE_PATH = "jdbcmulti/";

    private static final String TABLE_NAME = String.format("%s.`%s`", DFS_PLUGIN_NAME, TestJdbcPluginWithH2IT.TABLE_PATH);

    @Test
    public void testCrossSourceMultiFragmentJoin() throws Exception {
        try {
            client.alterSession(SLICE_TARGET, 1);
            run(("select x.person_id, y.salary from h2.drill_h2_test.person x " + "join %s y on x.person_id = y.person_id "), TestJdbcPluginWithH2IT.TABLE_NAME);
        } finally {
            client.resetSession(SLICE_TARGET);
        }
    }

    @Test
    public void validateResult() throws Exception {
        // Skip date, time, and timestamp types since h2 mangles these due to improper timezone support.
        testBuilder().sqlQuery(("select person_id, first_name, last_name, address, city, state, zip, json, bigint_field, smallint_field, " + ("numeric_field, boolean_field, double_field, float_field, real_field, time_field, timestamp_field, " + "date_field, clob_field from h2.`drill_h2_test`.person"))).ordered().baselineColumns("person_id", "first_name", "last_name", "address", "city", "state", "zip", "json", "bigint_field", "smallint_field", "numeric_field", "boolean_field", "double_field", "float_field", "real_field", "time_field", "timestamp_field", "date_field", "clob_field").baselineValues(1, "first_name_1", "last_name_1", "1401 John F Kennedy Blvd", "Philadelphia", "PA", 19107, "{ a : 5, b : 6 }", 123456L, 1, new BigDecimal("10.01"), false, 1.0, 1.1, 111.0, DateUtility.parseLocalTime("13:00:01.0"), DateUtility.parseLocalDateTime("2012-02-29 13:00:01.0"), DateUtility.parseLocalDate("2012-02-29"), "some clob data 1").baselineValues(2, "first_name_2", "last_name_2", "One Ferry Building", "San Francisco", "CA", 94111, "{ foo : \"abc\" }", 95949L, 2, new BigDecimal("20.02"), true, 2.0, 2.1, 222.0, DateUtility.parseLocalTime("23:59:59.0"), DateUtility.parseLocalDateTime("1999-09-09 23:59:59.0"), DateUtility.parseLocalDate("1999-09-09"), "some more clob data").baselineValues(3, "first_name_3", "last_name_3", "176 Bowery", "New York", "NY", 10012, "{ z : [ 1, 2, 3 ] }", 45456L, 3, new BigDecimal("30.04"), true, 3.0, 3.1, 333.0, DateUtility.parseLocalTime("11:34:21.0"), DateUtility.parseLocalDateTime("2011-10-30 11:34:21.0"), DateUtility.parseLocalDate("2011-10-30"), "clobber").baselineValues(4, null, null, "2 15th St NW", "Washington", "DC", 20007, "{ z : { a : 1, b : 2, c : 3 } }", (-67L), 4, new BigDecimal("40.04"), false, 4.0, 4.1, 444.0, DateUtility.parseLocalTime("16:00:01.0"), DateUtility.parseLocalDateTime("2015-06-01 16:00:01.0"), DateUtility.parseLocalDate("2015-06-01"), "xxx").baselineValues(5, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null).go();
    }

    @Test
    public void pushdownJoin() throws Exception {
        run("use h2");
        String query = "select x.person_id from (select person_id from h2.drill_h2_test.person) x " + "join (select person_id from h2.drill_h2_test.person) y on x.person_id = y.person_id ";
        String plan = queryBuilder().sql(query).explainText();
        Assert.assertThat("Query plan shouldn't contain Join operator", plan, IsNot.not(CoreMatchers.containsString("Join")));
    }

    @Test
    public void pushdownJoinAndFilterPushDown() throws Exception {
        String query = "select * from \n" + (((("h2.drill_h2_test.person e\n" + "INNER JOIN \n") + "h2.drill_h2_test.person s\n") + "ON e.FIRST_NAME = s.FIRST_NAME\n") + "WHERE e.LAST_NAME > 'hello'");
        String plan = queryBuilder().sql(query).explainText();
        Assert.assertThat("Query plan shouldn't contain Join operator", plan, IsNot.not(CoreMatchers.containsString("Join")));
        Assert.assertThat("Query plan shouldn't contain Filter operator", plan, IsNot.not(CoreMatchers.containsString("Filter")));
    }

    @Test
    public void pushdownAggregation() throws Exception {
        String query = "select count(*) from h2.drill_h2_test.person";
        String plan = queryBuilder().sql(query).explainText();
        Assert.assertThat("Query plan shouldn't contain Aggregate operator", plan, IsNot.not(CoreMatchers.containsString("Aggregate")));
    }

    @Test
    public void pushdownDoubleJoinAndFilter() throws Exception {
        String query = "select * from \n" + ((((((("h2.drill_h2_test.person e\n" + "INNER JOIN \n") + "h2.drill_h2_test.person s\n") + "ON e.person_ID = s.person_ID\n") + "INNER JOIN \n") + "h2.drill_h2_test.person ed\n") + "ON e.person_ID = ed.person_ID\n") + "WHERE s.first_name > 'abc' and ed.first_name > 'efg'");
        String plan = queryBuilder().sql(query).explainText();
        Assert.assertThat("Query plan shouldn't contain Join operator", plan, IsNot.not(CoreMatchers.containsString("Join")));
        Assert.assertThat("Query plan shouldn't contain Filter operator", plan, IsNot.not(CoreMatchers.containsString("Filter")));
    }

    @Test
    public void showTablesDefaultSchema() throws Exception {
        run("use h2.drill_h2_test");
        Assert.assertEquals(1, queryBuilder().sql("show tables like 'PERSON'").run().recordCount());
        // check table names case insensitivity
        Assert.assertEquals(1, queryBuilder().sql("show tables like 'person'").run().recordCount());
    }

    @Test
    public void describe() throws Exception {
        run("use h2.drill_h2_test");
        Assert.assertEquals(19, queryBuilder().sql("describe PERSON").run().recordCount());
        // check table names case insensitivity
        Assert.assertEquals(19, queryBuilder().sql("describe person").run().recordCount());
    }

    @Test
    public void ensureDrillFunctionsAreNotPushedDown() throws Exception {
        // This should verify that we're not trying to push CONVERT_FROM into the JDBC storage plugin. If were pushing
        // this function down, the SQL query would fail.
        run("select CONVERT_FROM(JSON, 'JSON') from h2.drill_h2_test.person where person_ID = 4");
    }

    @Test
    public void pushdownFilter() throws Exception {
        String query = "select * from h2.drill_h2_test.person where person_ID = 1";
        String plan = queryBuilder().sql(query).explainText();
        Assert.assertThat("Query plan shouldn't contain Filter operator", plan, IsNot.not(CoreMatchers.containsString("Filter")));
    }

    @Test
    public void testCaseInsensitiveTableNames() throws Exception {
        Assert.assertEquals(5, queryBuilder().sql("select * from h2.drill_h2_test.PeRsOn").run().recordCount());
        Assert.assertEquals(5, queryBuilder().sql("select * from h2.drill_h2_test.PERSON").run().recordCount());
        Assert.assertEquals(5, queryBuilder().sql("select * from h2.drill_h2_test.person").run().recordCount());
    }

    @Test
    public void testJdbcStoragePluginSerDe() throws Exception {
        String query = "select * from h2.drill_h2_test.PeRsOn";
        String plan = queryBuilder().sql(query).explainJson();
        Assert.assertEquals(5, queryBuilder().physical(plan).run().recordCount());
    }
}

