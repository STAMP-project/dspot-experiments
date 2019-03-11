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
package org.apache.hive.jdbc;


import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TestJdbcWithMiniLlap for Arrow format
 */
public class TestJdbcWithMiniLlapArrow extends BaseJdbcWithMiniLlap {
    protected static final Logger LOG = LoggerFactory.getLogger(TestJdbcWithMiniLlapArrow.class);

    private static MiniHS2 miniHS2 = null;

    private static final String tableName = "testJdbcMinihs2Tbl";

    private static String dataFileDir;

    private static final String testDbName = "testJdbcMinihs2";

    private static final String tag = "mytag";

    private static class ExceptionHolder {
        Throwable throwable;
    }

    /**
     * SleepMsUDF
     */
    public static class SleepMsUDF extends UDF {
        public Integer evaluate(int value, int ms) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                // No-op
            }
            return value;
        }
    }

    @Test
    @Override
    public void testKillQuery() throws Exception {
        testKillQueryById();
        testKillQueryByTagNegative();
        testKillQueryByTagAdmin();
        testKillQueryByTagOwner();
    }
}

