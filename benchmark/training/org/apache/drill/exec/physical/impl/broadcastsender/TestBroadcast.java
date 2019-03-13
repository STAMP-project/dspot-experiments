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
package org.apache.drill.exec.physical.impl.broadcastsender;


import org.apache.drill.categories.OperatorTest;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(OperatorTest.class)
public class TestBroadcast extends BaseTestQuery {
    public static final String BROAD_CAST_QUERY = "select * from " + ((("dfs.`broadcast/sales` s " + "INNER JOIN ") + "dfs.`broadcast/customer` c ") + "ON s.id = c.id");

    @Test
    public void plansWithBroadcast() throws Exception {
        // TODO: actually verify that this plan has a broadcast exchange in it once plan tools are enabled.
        setup();
        BaseTestQuery.test("explain plan for %s", TestBroadcast.BROAD_CAST_QUERY);
    }

    @Test
    public void broadcastExecuteWorks() throws Exception {
        setup();
        BaseTestQuery.test(TestBroadcast.BROAD_CAST_QUERY);
    }
}

