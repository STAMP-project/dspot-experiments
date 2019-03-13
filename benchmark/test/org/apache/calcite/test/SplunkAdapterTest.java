/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.test;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import org.apache.calcite.util.TestUtil;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test of the Calcite adapter for Splunk.
 */
public class SplunkAdapterTest {
    public static final String SPLUNK_URL = "https://localhost:8089";

    public static final String SPLUNK_USER = "admin";

    public static final String SPLUNK_PASSWORD = "changeme";

    /**
     * Tests the vanity driver.
     */
    @Test
    public void testVanityDriver() throws SQLException {
        loadDriverClass();
        if (!(enabled())) {
            return;
        }
        Properties info = new Properties();
        info.setProperty("url", SplunkAdapterTest.SPLUNK_URL);
        info.put("user", SplunkAdapterTest.SPLUNK_USER);
        info.put("password", SplunkAdapterTest.SPLUNK_PASSWORD);
        Connection connection = DriverManager.getConnection("jdbc:splunk:", info);
        connection.close();
    }

    /**
     * Tests the vanity driver with properties in the URL.
     */
    @Test
    public void testVanityDriverArgsInUrl() throws SQLException {
        loadDriverClass();
        if (!(enabled())) {
            return;
        }
        Connection connection = DriverManager.getConnection(((((((((("jdbc:splunk:" + "url='") + (SplunkAdapterTest.SPLUNK_URL)) + "'") + ";user='") + (SplunkAdapterTest.SPLUNK_USER)) + "'") + ";password='") + (SplunkAdapterTest.SPLUNK_PASSWORD)) + "'"));
        connection.close();
    }

    static final String[] SQL_STRINGS = new String[]{ "select \"source\", \"sourcetype\"\n" + "from \"splunk\".\"splunk\"", "select \"sourcetype\"\n" + "from \"splunk\".\"splunk\"", "select distinct \"sourcetype\"\n" + "from \"splunk\".\"splunk\"", "select count(\"sourcetype\")\n" + "from \"splunk\".\"splunk\"", // gives wrong answer, not error. currently returns same as count.
    "select count(distinct \"sourcetype\")\n" + "from \"splunk\".\"splunk\"", "select \"sourcetype\", count(\"source\")\n" + ("from \"splunk\".\"splunk\"\n" + "group by \"sourcetype\""), "select \"sourcetype\", count(\"source\") as c\n" + (("from \"splunk\".\"splunk\"\n" + "group by \"sourcetype\"\n") + "order by c desc\n"), // group + order
    "select s.\"product_id\", count(\"source\") as c\n" + ((("from \"splunk\".\"splunk\" as s\n" + "where s.\"sourcetype\" = \'access_combined_wcookie\'\n") + "group by s.\"product_id\"\n") + "order by c desc\n"), // non-advertised field
    "select s.\"sourcetype\", s.\"action\" from \"splunk\".\"splunk\" as s", "select s.\"source\", s.\"product_id\", s.\"product_name\", s.\"method\"\n" + ("from \"splunk\".\"splunk\" as s\n" + "where s.\"sourcetype\" = \'access_combined_wcookie\'\n"), "select p.\"product_name\", s.\"action\"\n" + (("from \"splunk\".\"splunk\" as s\n" + "  join \"mysql\".\"products\" as p\n") + "on s.\"product_id\" = p.\"product_id\""), "select s.\"source\", s.\"product_id\", p.\"product_name\", p.\"price\"\n" + ((("from \"splunk\".\"splunk\" as s\n" + "    join \"mysql\".\"products\" as p\n") + "    on s.\"product_id\" = p.\"product_id\"\n") + "where s.\"sourcetype\" = \'access_combined_wcookie\'\n") };

    static final String[] ERROR_SQL_STRINGS = new String[]{ // gives error in SplunkPushDownRule
    "select count(*) from \"splunk\".\"splunk\"", // gives no rows; suspect off-by-one because no base fields are
    // referenced
    "select s.\"product_id\", s.\"product_name\", s.\"method\"\n" + ("from \"splunk\".\"splunk\" as s\n" + "where s.\"sourcetype\" = \'access_combined_wcookie\'\n"), // horrible error if you access a field that doesn't exist
    "select s.\"sourcetype\", s.\"access\"\n" + "from \"splunk\".\"splunk\" as s\n" };

    // Fields:
    // sourcetype=access_*
    // action (purchase | update)
    // method (GET | POST)
    /**
     * Reads from a table.
     */
    @Test
    public void testSelect() throws SQLException {
        final String sql = "select \"source\", \"sourcetype\"\n" + "from \"splunk\".\"splunk\"";
        checkSql(sql, ( resultSet) -> {
            try {
                if (!(((resultSet.next()) && (resultSet.next())) && (resultSet.next()))) {
                    throw new AssertionError("expected at least 3 rows");
                }
                return null;
            } catch (SQLException e) {
                throw TestUtil.rethrow(e);
            }
        });
    }

    @Test
    public void testSelectDistinct() throws SQLException {
        checkSql(("select distinct \"sourcetype\"\n" + "from \"splunk\".\"splunk\""), SplunkAdapterTest.expect("sourcetype=access_combined_wcookie", "sourcetype=vendor_sales", "sourcetype=secure"));
    }

    /**
     * "status" is not a built-in column but we know it has some values in the
     * test data.
     */
    @Test
    public void testSelectNonBuiltInColumn() throws SQLException {
        checkSql(("select \"status\"\n" + "from \"splunk\".\"splunk\""), ( a0) -> {
            final Set<String> actual = new HashSet<>();
            try {
                while (a0.next()) {
                    actual.add(a0.getString(1));
                } 
                Assert.assertThat(actual.contains("404"), CoreMatchers.is(true));
                return null;
            } catch (SQLException e) {
                throw TestUtil.rethrow(e);
            }
        });
    }

    @Test
    public void testGroupBy() throws SQLException {
        checkSql(("select s.\"host\", count(\"source\") as c\n" + (("from \"splunk\".\"splunk\" as s\n" + "group by s.\"host\"\n") + "order by c desc\n")), SplunkAdapterTest.expect("host=vendor_sales; C=30244", "host=www1; C=24221", "host=www3; C=22975", "host=www2; C=22595", "host=mailsv; C=9829"));
    }
}

/**
 * End SplunkAdapterTest.java
 */
