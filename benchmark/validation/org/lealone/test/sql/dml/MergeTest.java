/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lealone.test.sql.dml;


import java.sql.PreparedStatement;
import org.junit.Assert;
import org.junit.Test;
import org.lealone.test.sql.SqlTestBase;


public class MergeTest extends SqlTestBase {
    @Test
    public void run() throws Exception {
        // stmt.executeUpdate("DROP TABLE IF EXISTS MergeTest");
        // ??id?primary key????MERGE???KEY????????primary key
        // stmt.executeUpdate("CREATE TABLE IF NOT EXISTS MergeTest(id int not null primary key, name varchar(500) not
        // null)");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS MergeTest(id int, name varchar(500) as '123')");
        // stmt.executeUpdate("DROP TABLE IF EXISTS tmpSelectTest");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS tmpSelectTest(id int, name varchar(500))");
        stmt.executeUpdate("DELETE FROM MergeTest");
        stmt.executeUpdate("DELETE FROM tmpSelectTest");
        sql = "INSERT INTO tmpSelectTest VALUES(DEFAULT, DEFAULT),(10, 'a'),(20, 'b')";
        // sql = "INSERT INTO tmpSelectTest VALUES(DEFAULT, 'c'),(10, 'a'),(20, 'b')";
        Assert.assertEquals(3, stmt.executeUpdate(sql));
        // ??????????????
        sql = "MERGE INTO MergeTest KEY(id) (SELECT * FROM tmpSelectTest)";
        Assert.assertEquals(3, stmt.executeUpdate(sql));
        sql = "MERGE INTO MergeTest KEY(id) VALUES()";// ??????????????????H2????bug

        try {
            stmt.executeUpdate(sql);
            Assert.fail(sql);
        } catch (Exception e) {
            // Syntax error in SQL statement "UPDATE PUBLIC.MERGETEST SET WHERE[*] ID=?"; expected "identifier";
            // SQL statement:UPDATE PUBLIC.MERGETEST SET WHERE ID=? [42001-172]
            // System.out.println(e.getMessage());
            Assert.assertTrue(e.getMessage().contains("Syntax error"));
        }
        // ???????????
        // 30 null
        // 10 a
        // 20 b
        sql = "MERGE INTO MergeTest KEY(id) VALUES(30, DEFAULT),(10, 'a'),(20, 'b')";
        Assert.assertEquals(3, stmt.executeUpdate(sql));
        try {
            sql = "MERGE INTO MergeTest KEY(id) VALUES(DEFAULT, DEFAULT),(10, 'a'),(20, 'b')";
            stmt.executeUpdate(sql);
            Assert.fail(sql);
        } catch (Exception e) {
            // org.lealone.message.JdbcSQLException: Column "ID" contains null values;
            // System.out.println(e.getMessage());
            Assert.assertTrue(e.getMessage().contains("contains null values"));
        }
        // ?????????:org.lealone.message.JdbcSQLException: Column count does not match;
        sql = "MERGE INTO MergeTest(name) KEY(id) (SELECT * FROM tmpSelectTest)";
        try {
            stmt.executeUpdate(sql);
            Assert.fail(sql);
        } catch (Exception e) {
            // System.out.println(e.getMessage());
            Assert.assertTrue(e.getMessage().contains("Column count does not match"));
        }
        // key???????VALUES?
        sql = "MERGE INTO MergeTest(name) KEY(id) VALUES('abc')";
        try {
            stmt.executeUpdate(sql);
            Assert.fail(sql);
        } catch (Exception e) {
            // Column "ID" contains null values;
            // System.out.println(e.getMessage());
            Assert.assertTrue(e.getMessage().contains("contains null values"));
        }
        sql = "MERGE INTO MergeTest(name, id) KEY(id) VALUES('abc', 10)";
        stmt.executeUpdate(sql);
        PreparedStatement ps;
        ps = conn.prepareStatement("MERGE INTO MergeTest(id, name) KEY(id) VALUES(?, ?)");
        ps.setInt(1, 30);
        ps.setString(2, "c");
        ps.executeUpdate();
        ps.close();
        sql = "EXPLAIN MERGE INTO MergeTest(id, name) KEY(id) SELECT * FROM tmpSelectTest";
        printResultSet();
        sql = "select id,name from MergeTest";
        printResultSet();
    }
}

