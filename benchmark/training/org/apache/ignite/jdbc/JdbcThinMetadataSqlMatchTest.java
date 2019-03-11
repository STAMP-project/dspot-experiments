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
package org.apache.ignite.jdbc;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Verify we are able to escape "_" character in the metadata request.
 */
public class JdbcThinMetadataSqlMatchTest extends GridCommonAbstractTest {
    /**
     * Connection.
     */
    private static Connection conn;

    /**
     * Test for escaping the "_" character in the table metadata request
     */
    @Test
    public void testTablesMatch() throws SQLException {
        assertEqualsCollections(Arrays.asList("MY0FAV0TABLE", "MY_FAV_TABLE"), getTableNames("MY_FAV_TABLE"));
        assertEqualsCollections(Collections.singletonList("MY_FAV_TABLE"), getTableNames("MY\\_FAV\\_TABLE"));
        assertEqualsCollections(Collections.emptyList(), getTableNames("\\%"));
        assertEqualsCollections(Arrays.asList("MY0FAV0TABLE", "MY_FAV_TABLE", "OTHER_TABLE"), getTableNames("%"));
        assertEqualsCollections(Collections.emptyList(), getTableNames(""));
        assertEqualsCollections(Arrays.asList("MY0FAV0TABLE", "MY_FAV_TABLE", "OTHER_TABLE"), getTableNames(null));
    }
}

