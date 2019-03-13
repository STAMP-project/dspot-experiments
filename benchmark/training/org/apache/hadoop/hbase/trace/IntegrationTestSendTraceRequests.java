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
package org.apache.hadoop.hbase.trace;


import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hbase.IntegrationTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.testclassification.IntegrationTests;
import org.apache.hadoop.hbase.util.AbstractHBaseTool;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(IntegrationTests.class)
public class IntegrationTestSendTraceRequests extends AbstractHBaseTool {
    public static final String TABLE_ARG = "t";

    public static final String CF_ARG = "f";

    public static final String TABLE_NAME_DEFAULT = "SendTracesTable";

    public static final String COLUMN_FAMILY_DEFAULT = "D";

    private TableName tableName = TableName.valueOf(IntegrationTestSendTraceRequests.TABLE_NAME_DEFAULT);

    private byte[] familyName = Bytes.toBytes(IntegrationTestSendTraceRequests.COLUMN_FAMILY_DEFAULT);

    private IntegrationTestingUtility util;

    private Random random = new Random();

    private Admin admin;

    private SpanReceiverHost receiverHost;

    @Test
    public void internalDoWork() throws Exception {
        util = createUtil();
        admin = getAdmin();
        setupReceiver();
        deleteTable();
        createTable();
        LinkedBlockingQueue<Long> rks = insertData();
        ExecutorService service = Executors.newFixedThreadPool(20);
        doScans(service, rks);
        doGets(service, rks);
        service.shutdown();
        service.awaitTermination(100, TimeUnit.SECONDS);
        Thread.sleep(90000);
        receiverHost.closeReceivers();
        util.restoreCluster();
        util = null;
    }
}

