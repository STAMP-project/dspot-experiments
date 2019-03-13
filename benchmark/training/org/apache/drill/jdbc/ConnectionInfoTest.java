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
package org.apache.drill.jdbc;


import Quoting.DOUBLE_QUOTE.string;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.apache.drill.categories.JdbcTest;
import org.apache.drill.categories.SlowTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;


/**
 * Test for Drill's Properties in the JDBC URL connection string
 */
@Category({ SlowTest.class, JdbcTest.class })
public class ConnectionInfoTest extends JdbcTestBase {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testQuotingIdentifiersProperty() throws Exception {
        Connection connection = JdbcTestBase.connect("jdbc:drill:zk=local;quoting_identifiers=\'\"\'");
        DatabaseMetaData dbmd = connection.getMetaData();
        Assert.assertThat(dbmd.getIdentifierQuoteString(), CoreMatchers.equalTo(string));
        JdbcTestBase.reset();
        connection = JdbcTestBase.connect("jdbc:drill:zk=local;quoting_identifiers=[");
        dbmd = connection.getMetaData();
        Assert.assertThat(dbmd.getIdentifierQuoteString(), CoreMatchers.equalTo(Quoting.BRACKET.string));
    }

    @Test
    public void testIncorrectCharacterForQuotingIdentifiers() throws Exception {
        thrown.expect(SQLException.class);
        thrown.expectMessage(CoreMatchers.containsString("Option planner.parser.quoting_identifiers must be one of: [`, \", []"));
        JdbcTestBase.connect("jdbc:drill:zk=local;quoting_identifiers=&");
    }

    @Test
    public void testSetSchemaUsingConnectionMethod() throws Exception {
        Connection connection = JdbcTestBase.connect("jdbc:drill:zk=local");
        Assert.assertNull(connection.getSchema());
        connection.setSchema("dfs.tmp");
        Assert.assertEquals("dfs.tmp", connection.getSchema());
    }

    @Test
    public void testIncorrectlySetSchema() throws Exception {
        Connection connection = JdbcTestBase.connect("jdbc:drill:zk=local");
        thrown.expect(SQLException.class);
        thrown.expectMessage("Error when setting schema");
        connection.setSchema("ABC");
    }

    @Test
    public void testSchemaInConnectionString() throws Exception {
        Connection connection = JdbcTestBase.connect("jdbc:drill:zk=local;schema=sys");
        Assert.assertEquals("sys", connection.getSchema());
    }
}

