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
package org.lealone.test.aose;


import org.junit.Test;
import org.lealone.test.TestBase;
import org.lealone.test.sql.SqlTestBase;


// ?CACHE_SIZE????RowStorage??????????????????page?????ColumnStorage??????
public class HrcStorageSqlTest extends SqlTestBase {
    public HrcStorageSqlTest() {
        super("HrcStorageSqlTest");
        TestBase.initTransactionEngine();
        setEmbedded(true);
        addConnectionParameter("PAGE_SIZE", (((2 * 1024) * 1024) + ""));
        // addConnectionParameter("COMPRESS", "true");
        printURL();
    }

    int rowCount = 6000;

    int columnCount = 20;

    int pageSplitSize = 1024 * 1024;

    @Test
    public void run() throws Exception {
        for (int i = 0; i < 20; i++) {
            System.out.println();
            System.out.println((("------------------loop " + (i + 1)) + " start---------------------"));
            testRowStorage();
            System.out.println();
            testColumnStorage();
            System.out.println((("------------------loop " + (i + 1)) + " end---------------------"));
        }
    }
}

