/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.util.hive;


import HiveWriter.ConnectFailure;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hive.hcatalog.streaming.HiveEndPoint;
import org.apache.hive.hcatalog.streaming.InvalidTable;
import org.apache.hive.hcatalog.streaming.RecordWriter;
import org.apache.hive.hcatalog.streaming.StreamingConnection;
import org.apache.hive.hcatalog.streaming.StreamingException;
import org.apache.hive.hcatalog.streaming.TransactionBatch;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class HiveWriterTest {
    private HiveEndPoint hiveEndPoint;

    private int txnsPerBatch;

    private boolean autoCreatePartitions;

    private int callTimeout;

    private ExecutorService executorService;

    private UserGroupInformation userGroupInformation;

    private HiveConf hiveConf;

    private HiveWriter hiveWriter;

    private StreamingConnection streamingConnection;

    private RecordWriter recordWriter;

    private Callable<RecordWriter> recordWriterCallable;

    private TransactionBatch transactionBatch;

    @Test
    public void testNormal() {
        Assert.assertNotNull(hiveWriter);
    }

    @Test(expected = ConnectFailure.class)
    public void testNewConnectionInvalidTable() throws Exception {
        hiveEndPoint = Mockito.mock(HiveEndPoint.class);
        InvalidTable invalidTable = new InvalidTable("badDb", "badTable");
        Mockito.when(hiveEndPoint.newConnection(autoCreatePartitions, hiveConf, userGroupInformation)).thenThrow(invalidTable);
        try {
            initWriter();
        } catch (HiveWriter e) {
            Assert.assertEquals(invalidTable, e.getCause());
            throw e;
        }
    }

    @Test(expected = ConnectFailure.class)
    public void testRecordWriterStreamingException() throws Exception {
        recordWriterCallable = Mockito.mock(Callable.class);
        StreamingException streamingException = new StreamingException("Test Exception");
        Mockito.when(recordWriterCallable.call()).thenThrow(streamingException);
        try {
            initWriter();
        } catch (HiveWriter e) {
            Assert.assertEquals(streamingException, e.getCause());
            throw e;
        }
    }
}

