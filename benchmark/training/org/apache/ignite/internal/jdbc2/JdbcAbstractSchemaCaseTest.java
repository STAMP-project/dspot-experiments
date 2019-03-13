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
package org.apache.ignite.internal.jdbc2;


import java.sql.SQLException;
import java.util.concurrent.Callable;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Base (for v2 and thin drivers) test for the case (in)sensitivity of schema name.
 */
public abstract class JdbcAbstractSchemaCaseTest extends GridCommonAbstractTest {
    /**
     * Grid count.
     */
    private static final int GRID_CNT = 2;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings({ "unused" })
    @Test
    public void testSchemaName() throws Exception {
        checkSchemaConnection("test0");
        checkSchemaConnection("test1");
        checkSchemaConnection("\"TestCase\"");
        checkSchemaConnection("\"TEST0\"");
        checkSchemaConnection("\"TEST1\"");
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                checkSchemaConnection("TestCase");
                return null;
            }
        }, SQLException.class, null);
    }

    /**
     * Check case (in)sensitivity of schema name that is specified in the connection url.
     */
    @Test
    public void testSchemaNameWithCreateTableIfNotExists() throws Exception {
        createTableWithImplicitSchema("test0");
        assertTablesInSchemasPresented("TEST0");// due to its normalized.

        createTableWithImplicitSchema("test1");
        assertTablesInSchemasPresented("TEST0", "TEST1");
        createTableWithImplicitSchema("\"TestCase\"");
        assertTablesInSchemasPresented("TEST0", "TEST1", "TestCase");
        createTableWithImplicitSchema("\"TEST0\"");
        assertTablesInSchemasPresented("TEST0", "TEST1", "TestCase");
        createTableWithImplicitSchema("\"TEST1\"");
        assertTablesInSchemasPresented("TEST0", "TEST1", "TestCase");
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                createTableWithImplicitSchema("TestCase");// due to normalization it is converted to "TESTCASE".

                return null;
            }
        }, SQLException.class, null);
    }
}

