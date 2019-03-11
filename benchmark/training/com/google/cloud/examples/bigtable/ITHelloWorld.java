/**
 * Copyright 2019 Google LLC.  All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.examples.bigtable;


import com.google.cloud.bigtable.admin.v2.BigtableTableAdminClient;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.models.Row;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Integration tests for {@link HelloWorld}
 */
public class ITHelloWorld {
    private static final String PROJECT_PROPERTY_NAME = "bigtable.project";

    private static final String INSTANCE_PROPERTY_NAME = "bigtable.instance";

    private static final String TABLE_PREFIX = "table";

    private static String tableId;

    private static BigtableDataClient dataClient;

    private static BigtableTableAdminClient adminClient;

    private static String projectId;

    private static String instanceId;

    private HelloWorld helloWorld;

    @Test
    public void testCreateAndDeleteTable() throws IOException {
        // Creates a table.
        String testTable = generateTableId();
        HelloWorld testHelloWorld = new HelloWorld(ITHelloWorld.projectId, ITHelloWorld.instanceId, testTable);
        testHelloWorld.createTable();
        Assert.assertTrue(ITHelloWorld.adminClient.exists(testTable));
        // Deletes a table.
        testHelloWorld.deleteTable();
        Assert.assertTrue((!(ITHelloWorld.adminClient.exists(testTable))));
    }

    @Test
    public void testWriteToTable() {
        // Writes to a table.
        helloWorld.writeToTable();
        Row row = ITHelloWorld.dataClient.readRow(ITHelloWorld.tableId, "rowKey0");
        Assert.assertNotNull(row);
    }

    // TODO: add test for helloWorld.readSingleRow()
    // TODO: add test for helloWorld.readTable()
    @Test
    public void testRunDoesNotFail() throws Exception {
        helloWorld.run();
    }
}

