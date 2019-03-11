/**
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.spanner.it;


import com.google.cloud.spanner.Database;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.IntegrationTest;
import com.google.cloud.spanner.IntegrationTestEnv;
import com.google.cloud.spanner.Options;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Statement;
import com.google.common.hash.HashFunction;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static java.util.Arrays.asList;


/**
 * Integration test reading large amounts of data. The size of data ensures that multiple chunks are
 * returned by the server.
 */
@Category(IntegrationTest.class)
@RunWith(JUnit4.class)
public class ITLargeReadTest {
    private static int numRows;

    private static final int WRITE_BATCH_SIZE = 1 << 20;

    private static final String TABLE_NAME = "TestTable";

    @ClassRule
    public static IntegrationTestEnv env = new IntegrationTestEnv();

    private static Database db;

    private static HashFunction hasher;

    private static DatabaseClient client;

    @Test
    public void read() {
        try (ResultSet resultSet = ITLargeReadTest.client.singleUse().read(ITLargeReadTest.TABLE_NAME, com.google.cloud.spanner.KeySet.all(), asList("Key", "Data", "Fingerprint", "Size"))) {
            validate(resultSet);
        }
    }

    @Test
    public void readWithSmallPrefetchChunks() {
        try (ResultSet resultSet = ITLargeReadTest.client.singleUse().read(ITLargeReadTest.TABLE_NAME, com.google.cloud.spanner.KeySet.all(), asList("Key", "Data", "Fingerprint", "Size"), Options.prefetchChunks(1))) {
            validate(resultSet);
        }
    }

    @Test
    public void query() {
        try (ResultSet resultSet = ITLargeReadTest.client.singleUse().executeQuery(Statement.of((("SELECT Key, Data, Fingerprint, Size FROM " + (ITLargeReadTest.TABLE_NAME)) + " ORDER BY Key")))) {
            validate(resultSet);
        }
    }

    @Test
    public void queryWithSmallPrefetchChunks() {
        try (ResultSet resultSet = ITLargeReadTest.client.singleUse().executeQuery(Statement.of((("SELECT Key, Data, Fingerprint, Size FROM " + (ITLargeReadTest.TABLE_NAME)) + " ORDER BY Key")), Options.prefetchChunks(1))) {
            validate(resultSet);
        }
    }
}

