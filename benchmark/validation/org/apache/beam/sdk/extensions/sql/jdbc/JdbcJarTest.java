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
package org.apache.beam.sdk.extensions.sql.jdbc;


import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Test for JDBC via {@link java.net.URLClassLoader}.
 */
public class JdbcJarTest {
    private static final String DRIVER_URL = "jdbc:beam:";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void classLoader_simple() throws Exception {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        // SELECT 1 is a special case and does not reach the parser
        Assert.assertTrue(statement.execute("SELECT 1"));
    }

    @Test
    public void classLoader_parse() throws Exception {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        Assert.assertTrue(statement.execute("SELECT 'beam'"));
    }

    @Test
    public void classLoader_ddl() throws Exception {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        Assert.assertEquals(0, statement.executeUpdate("CREATE EXTERNAL TABLE test (id INTEGER) TYPE 'text'"));
        Assert.assertEquals(0, statement.executeUpdate("DROP TABLE test"));
    }

    @Test
    public void classLoader_readFile() throws Exception {
        File simpleTable = folder.newFile();
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        Assert.assertEquals(0, statement.executeUpdate((("CREATE EXTERNAL TABLE test (id INTEGER) TYPE 'text' LOCATION '" + (simpleTable.getAbsolutePath())) + "'")));
        Assert.assertTrue(statement.execute("SELECT * FROM test"));
        Assert.assertEquals(0, statement.executeUpdate("DROP TABLE test"));
    }
}

