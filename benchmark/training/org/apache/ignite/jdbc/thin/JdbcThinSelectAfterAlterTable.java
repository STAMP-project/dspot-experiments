/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.jdbc.thin;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.apache.ignite.configuration.ClientConnectorConfiguration;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Base class for complex SQL tests based on JDBC driver.
 */
public class JdbcThinSelectAfterAlterTable extends GridCommonAbstractTest {
    /**
     * Client connection port.
     */
    private int cliPort = ClientConnectorConfiguration.DFLT_PORT;

    /**
     * JDBC connection.
     */
    private Connection conn;

    /**
     * JDBC statement.
     */
    private Statement stmt;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSelectAfterAlterTableSingleNode() throws Exception {
        stmt.executeUpdate("alter table person add age int");
        checkNewColumn(stmt);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSelectAfterAlterTableMultiNode() throws Exception {
        try (Connection conn2 = DriverManager.getConnection(("jdbc:ignite:thin://127.0.0.1:" + ((ClientConnectorConfiguration.DFLT_PORT) + 1)))) {
            try (Statement stmt2 = conn2.createStatement()) {
                stmt2.executeUpdate("alter table person add age int");
            }
        }
        checkNewColumn(stmt);
    }
}

