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
package org.apache.drill.exec.sql;


import org.apache.drill.test.ClusterTest;
import org.junit.Test;


public class TestSchemaCaseInsensitivity extends ClusterTest {
    @Test
    public void testUseCommand() throws Exception {
        queryBuilder().sql("use Information_Schema").run();
        queryBuilder().sql("use Sys").run();
        queryBuilder().sql("use Dfs").run();
        queryBuilder().sql("use Dfs.Tmp").run();
    }

    @Test
    public void testDescribeSchema() throws Exception {
        checkRecordCount(1, "describe schema SyS");
        checkRecordCount(1, "describe schema Information_Schema");
        ClusterTest.client.testBuilder().sqlQuery("describe schema DfS.tMp").unOrdered().sqlBaselineQuery("describe schema dfs.tmp").go();
    }

    @Test
    public void testDescribeTable() throws Exception {
        checkRecordCount(4, "describe Information_Schema.`Tables`");
        checkRecordCount(1, "describe Information_Schema.`Tables` Table_Catalog");
        checkRecordCount(1, "describe Information_Schema.`Tables` '%Catalog'");
        checkRecordCount(6, "describe SyS.Version");
    }

    @Test
    public void testShowSchemas() throws Exception {
        checkRecordCount(1, "show schemas like '%Y%'");
        checkRecordCount(1, "show schemas like 'Info%'");
        checkRecordCount(1, "show schemas like 'D%Tmp'");
    }

    @Test
    public void testShowTables() throws Exception {
        checkRecordCount(1, "show tables in Information_Schema like 'SC%'");
        checkRecordCount(1, "show tables in Sys like '%ION'");
    }

    @Test
    public void testSelectStatement() throws Exception {
        checkRecordCount(1, "select * from Information_Schema.Schemata where Schema_Name = 'dfs.tmp'");
        checkRecordCount(1, "select * from Sys.Version");
    }
}

