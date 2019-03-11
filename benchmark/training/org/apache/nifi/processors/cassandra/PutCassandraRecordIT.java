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
package org.apache.nifi.processors.cassandra;


import PutCassandraRecord.REL_SUCCESS;
import RecordFieldType.INT;
import RecordFieldType.STRING;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.apache.nifi.serialization.record.MockRecordParser;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


public class PutCassandraRecordIT {
    private static TestRunner testRunner;

    private static MockRecordParser recordReader;

    private static Cluster cluster;

    private static Session session;

    private static final String KEYSPACE = "sample_keyspace";

    private static final String TABLE = "sample_table";

    private static final String HOST = "localhost";

    private static final int PORT = 9042;

    @Test
    public void testSimplePut() {
        PutCassandraRecordIT.recordReader.addSchemaField("id", INT);
        PutCassandraRecordIT.recordReader.addSchemaField("name", STRING);
        PutCassandraRecordIT.recordReader.addSchemaField("age", INT);
        PutCassandraRecordIT.recordReader.addRecord(1, "Ram", 42);
        PutCassandraRecordIT.recordReader.addRecord(2, "Jeane", 47);
        PutCassandraRecordIT.recordReader.addRecord(3, "Ilamaran", 27);
        PutCassandraRecordIT.recordReader.addRecord(4, "Jian", 14);
        PutCassandraRecordIT.recordReader.addRecord(5, "Sakura", 24);
        PutCassandraRecordIT.testRunner.enqueue("");
        PutCassandraRecordIT.testRunner.run();
        PutCassandraRecordIT.testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Assert.assertEquals(5, getRecordsCount());
    }
}

