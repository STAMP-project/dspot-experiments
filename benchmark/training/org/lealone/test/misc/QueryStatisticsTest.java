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
package org.lealone.test.misc;


import TraceSystem.INFO;
import org.junit.Test;
import org.lealone.test.sql.SqlTestBase;


public class QueryStatisticsTest extends SqlTestBase {
    public QueryStatisticsTest() {
        super("QueryStatisticsTestDB");
        addConnectionParameter("TRACE_LEVEL_SYSTEM_OUT", INFO);
    }

    @Test
    public void run() throws Exception {
        stmt.executeUpdate("set QUERY_STATISTICS 1");
        stmt.executeUpdate("set QUERY_STATISTICS_MAX_ENTRIES 200");
        insert();
        select();
    }
}

