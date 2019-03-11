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
package org.apache.hadoop.hive.metastore.tools.metatool;


import java.io.OutputStream;
import junit.framework.TestCase;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;


/**
 * Integration tests for the HiveMetaTool program.
 */
public class TestHiveMetaTool extends TestCase {
    private static final String DB_NAME = "TestHiveMetaToolDB";

    private static final String TABLE_NAME = "simpleTbl";

    private static final String LOCATION = "hdfs://nn.example.com/";

    private static final String NEW_LOCATION = "hdfs://nn-ha-uri/";

    private static final String PATH = "warehouse/hive/ab.avsc";

    private static final String AVRO_URI = (TestHiveMetaTool.LOCATION) + (TestHiveMetaTool.PATH);

    private static final String NEW_AVRO_URI = (TestHiveMetaTool.NEW_LOCATION) + (TestHiveMetaTool.PATH);

    private HiveMetaStoreClient client;

    private OutputStream os;

    public void testListFSRoot() throws Exception {
        HiveMetaTool.main(new String[]{ "-listFSRoot" });
        String out = os.toString();
        TestCase.assertTrue(((out + " doesn't contain ") + (client.getDatabase(TestHiveMetaTool.DB_NAME).getLocationUri())), out.contains(client.getDatabase(TestHiveMetaTool.DB_NAME).getLocationUri()));
    }

    public void testExecuteJDOQL() throws Exception {
        HiveMetaTool.main(new String[]{ "-executeJDOQL", "select locationUri from org.apache.hadoop.hive.metastore.model.MDatabase" });
        String out = os.toString();
        TestCase.assertTrue(((out + " doesn't contain ") + (client.getDatabase(TestHiveMetaTool.DB_NAME).getLocationUri())), out.contains(client.getDatabase(TestHiveMetaTool.DB_NAME).getLocationUri()));
    }

    public void testUpdateFSRootLocation() throws Exception {
        checkAvroSchemaURLProps(TestHiveMetaTool.AVRO_URI);
        HiveMetaTool.main(new String[]{ "-updateLocation", TestHiveMetaTool.NEW_LOCATION, TestHiveMetaTool.LOCATION, "-tablePropKey", "avro.schema.url" });
        checkAvroSchemaURLProps(TestHiveMetaTool.NEW_AVRO_URI);
        HiveMetaTool.main(new String[]{ "-updateLocation", TestHiveMetaTool.LOCATION, TestHiveMetaTool.NEW_LOCATION, "-tablePropKey", "avro.schema.url" });
        checkAvroSchemaURLProps(TestHiveMetaTool.AVRO_URI);
    }
}

