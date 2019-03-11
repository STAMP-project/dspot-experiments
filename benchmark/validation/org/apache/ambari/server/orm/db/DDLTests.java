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
package org.apache.ambari.server.orm.db;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test to check the sanity and consistence of DDL scripts for different SQL dialects.
 * (e.g. no unnamed constraints, the same tables with the same columns and constraints must exist)
 */
public class DDLTests {
    private static final Logger LOG = LoggerFactory.getLogger(DDLTestUtils.class);

    private static final int EXPECTED_ALTER_TABLE_COUNT = 1;

    @Test
    public void testVerifyDerby() throws Exception {
        verifyDDL("Derby");
    }

    @Test
    public void testVerifyPostgres() throws Exception {
        verifyDDL("Postgres");
    }

    @Test
    public void testVerifyMySQL() throws Exception {
        verifyDDL("MySQL");
    }

    @Test
    public void testVerifyOracle() throws Exception {
        verifyDDL("Oracle");
    }

    @Test
    public void testVerifySqlAnywhere() throws Exception {
        verifyDDL("SQLAnywhere");
    }

    @Test
    public void testVerifyMsSqlServer() throws Exception {
        verifyDDL("SQLServer");
    }

    @Test
    public void testCompareDerby() throws Exception {
        DDLTests.compareAgainstPostgres("Derby");
    }

    @Test
    public void testCompareOracle() throws Exception {
        DDLTests.compareAgainstPostgres("Oracle");
    }

    @Test
    public void testCompareMySQL() throws Exception {
        DDLTests.compareAgainstPostgres("MySQL");
    }

    @Test
    public void testCompareSQLAnywhere() throws Exception {
        DDLTests.compareAgainstPostgres("SQLAnywhere");
    }

    @Test
    public void testCompareSQLServer() throws Exception {
        DDLTests.compareAgainstPostgres("SQLServer");
    }
}

