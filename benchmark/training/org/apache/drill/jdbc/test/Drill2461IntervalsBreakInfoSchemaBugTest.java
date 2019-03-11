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
import java.sql.Statement;
import org.apache.drill.categories.JdbcTest;
import org.apache.drill.jdbc.JdbcTestBase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(JdbcTest.class)
public class Drill2461IntervalsBreakInfoSchemaBugTest extends JdbcTestBase {
    private static final String VIEW_NAME = (Drill2461IntervalsBreakInfoSchemaBugTest.class.getSimpleName()) + "_View";

    private static Connection connection;

    @Test
    public void testIntervalInViewDoesntCrashInfoSchema() throws Exception {
        final Statement stmt = Drill2461IntervalsBreakInfoSchemaBugTest.connection.createStatement();
        ResultSet util;
        // Create a view using an INTERVAL type:
        util = stmt.executeQuery("USE dfs.tmp");
        Assert.assertTrue(util.next());
        Assert.assertTrue(("Error setting schema to dfs.tmp: " + (util.getString(2))), util.getBoolean(1));
        util = stmt.executeQuery(((((("CREATE OR REPLACE VIEW " + (Drill2461IntervalsBreakInfoSchemaBugTest.VIEW_NAME)) + " AS ") + "\n  SELECT CAST( NULL AS INTERVAL HOUR(4) TO MINUTE ) AS optINTERVAL_HM ") + "\n  FROM INFORMATION_SCHEMA.CATALOGS ") + "\n  LIMIT 1 "));
        Assert.assertTrue(util.next());
        Assert.assertTrue(((("Error creating temporary test-columns view " + (Drill2461IntervalsBreakInfoSchemaBugTest.VIEW_NAME)) + ": ") + (util.getString(2))), util.getBoolean(1));
        // Test whether query INFORMATION_SCHEMA.COLUMNS works (doesn't crash):
        util = stmt.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS");
        Assert.assertTrue(util.next());
        // Clean up the test view:
        util = Drill2461IntervalsBreakInfoSchemaBugTest.connection.createStatement().executeQuery(("DROP VIEW " + (Drill2461IntervalsBreakInfoSchemaBugTest.VIEW_NAME)));
        Assert.assertTrue(util.next());
        Assert.assertTrue(((("Error dropping temporary test-columns view " + (Drill2461IntervalsBreakInfoSchemaBugTest.VIEW_NAME)) + ": ") + (util.getString(2))), util.getBoolean(1));
    }
}

