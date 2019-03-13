/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hive.jdbc;


import java.sql.Connection;
import java.util.List;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.junit.Test;


public class TestJdbcGenericUDTFGetSplits {
    protected static MiniHS2 miniHS2 = null;

    protected static String dataFileDir;

    static Path kvDataFilePath;

    protected static String tableName = "testtab1";

    protected static HiveConf conf = null;

    protected Connection hs2Conn = null;

    @Test(timeout = 200000)
    public void testGenericUDTFOrderBySplitCount1() throws Exception {
        String query = (("select get_splits(" + "'select value from ") + (TestJdbcGenericUDTFGetSplits.tableName)) + "', 5)";
        runQuery(query, getConfigs(), 10);
        query = (("select get_splits(" + "'select value from ") + (TestJdbcGenericUDTFGetSplits.tableName)) + " order by under_col', 5)";
        runQuery(query, getConfigs(), 1);
        query = (("select get_splits(" + "'select value from ") + (TestJdbcGenericUDTFGetSplits.tableName)) + " order by under_col limit 0', 5)";
        runQuery(query, getConfigs(), 0);
        query = (("select get_splits(" + "'select `value` from (select value from ") + (TestJdbcGenericUDTFGetSplits.tableName)) + " where value is not null order by value) as t', 5)";
        runQuery(query, getConfigs(), 1);
        List<String> setCmds = getConfigs();
        setCmds.add("set hive.llap.external.splits.order.by.force.single.split=false");
        query = (("select get_splits(" + "'select `value` from (select value from ") + (TestJdbcGenericUDTFGetSplits.tableName)) + " where value is not null order by value) as t', 5)";
        runQuery(query, setCmds, 10);
    }
}

