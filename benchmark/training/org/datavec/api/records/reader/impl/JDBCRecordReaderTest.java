/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.datavec.api.records.reader.impl;


import JDBCRecordReader.JDBC_DRIVER_CLASS_NAME;
import JDBCRecordReader.JDBC_RESULTSET_TYPE;
import JDBCRecordReader.JDBC_URL;
import java.net.URI;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.datavec.api.conf.Configuration;
import org.datavec.api.records.Record;
import org.datavec.api.records.listener.RecordListener;
import org.datavec.api.records.listener.impl.LogRecordListener;
import org.datavec.api.records.metadata.RecordMetaData;
import org.datavec.api.records.metadata.RecordMetaDataJdbc;
import org.datavec.api.records.metadata.RecordMetaDataLine;
import org.datavec.api.records.reader.impl.jdbc.JDBCRecordReader;
import org.datavec.api.writable.BooleanWritable;
import org.datavec.api.writable.DoubleWritable;
import org.datavec.api.writable.FloatWritable;
import org.datavec.api.writable.IntWritable;
import org.datavec.api.writable.LongWritable;
import org.datavec.api.writable.Text;
import org.datavec.api.writable.Writable;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class JDBCRecordReaderTest {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    Connection conn;

    EmbeddedDataSource dataSource;

    private final String dbName = "datavecTests";

    private final String driverClassName = "org.apache.derby.jdbc.EmbeddedDriver";

    @Test
    public void testSimpleIter() throws Exception {
        try (JDBCRecordReader reader = getInitializedReader("SELECT * FROM Coffee")) {
            List<List<Writable>> records = new ArrayList<>();
            while (reader.hasNext()) {
                List<Writable> values = reader.next();
                records.add(values);
            } 
            Assert.assertFalse(records.isEmpty());
            List<Writable> first = records.get(0);
            Assert.assertEquals(new Text("Bolivian Dark"), first.get(0));
            Assert.assertEquals(new Text("14-001"), first.get(1));
            Assert.assertEquals(new DoubleWritable(8.95), first.get(2));
        }
    }

    @Test
    public void testSimpleWithListener() throws Exception {
        try (JDBCRecordReader reader = getInitializedReader("SELECT * FROM Coffee")) {
            RecordListener recordListener = new LogRecordListener();
            reader.setListeners(recordListener);
            reader.next();
            Assert.assertTrue(recordListener.invoked());
        }
    }

    @Test
    public void testReset() throws Exception {
        try (JDBCRecordReader reader = getInitializedReader("SELECT * FROM Coffee")) {
            List<List<Writable>> records = new ArrayList<>();
            records.add(reader.next());
            reader.reset();
            records.add(reader.next());
            Assert.assertEquals(2, records.size());
            Assert.assertEquals(new Text("Bolivian Dark"), records.get(0).get(0));
            Assert.assertEquals(new Text("Bolivian Dark"), records.get(1).get(0));
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testLackingDataSourceShouldFail() throws Exception {
        try (JDBCRecordReader reader = new JDBCRecordReader("SELECT * FROM Coffee")) {
            reader.initialize(null);
        }
    }

    @Test
    public void testConfigurationDataSourceInitialization() throws Exception {
        try (JDBCRecordReader reader = new JDBCRecordReader("SELECT * FROM Coffee")) {
            Configuration conf = new Configuration();
            conf.set(JDBC_URL, (("jdbc:derby:" + (dbName)) + ";create=true"));
            conf.set(JDBC_DRIVER_CLASS_NAME, driverClassName);
            reader.initialize(conf, null);
            Assert.assertTrue(reader.hasNext());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitConfigurationMissingParametersShouldFail() throws Exception {
        try (JDBCRecordReader reader = new JDBCRecordReader("SELECT * FROM Coffee")) {
            Configuration conf = new Configuration();
            conf.set(JDBC_URL, "should fail anyway");
            reader.initialize(conf, null);
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRecordDataInputStreamShouldFail() throws Exception {
        try (JDBCRecordReader reader = getInitializedReader("SELECT * FROM Coffee")) {
            reader.record(null, null);
        }
    }

    @Test
    public void testLoadFromMetaData() throws Exception {
        try (JDBCRecordReader reader = getInitializedReader("SELECT * FROM Coffee")) {
            RecordMetaDataJdbc rmd = new RecordMetaDataJdbc(new URI(conn.getMetaData().getURL()), "SELECT * FROM Coffee WHERE ProdNum = ?", Collections.singletonList("14-001"), reader.getClass());
            Record res = reader.loadFromMetaData(rmd);
            Assert.assertNotNull(res);
            Assert.assertEquals(new Text("Bolivian Dark"), res.getRecord().get(0));
            Assert.assertEquals(new Text("14-001"), res.getRecord().get(1));
            Assert.assertEquals(new DoubleWritable(8.95), res.getRecord().get(2));
        }
    }

    @Test
    public void testNextRecord() throws Exception {
        try (JDBCRecordReader reader = getInitializedReader("SELECT * FROM Coffee")) {
            Record r = reader.nextRecord();
            List<Writable> fields = r.getRecord();
            RecordMetaData meta = r.getMetaData();
            Assert.assertNotNull(r);
            Assert.assertNotNull(fields);
            Assert.assertNotNull(meta);
            Assert.assertEquals(new Text("Bolivian Dark"), fields.get(0));
            Assert.assertEquals(new Text("14-001"), fields.get(1));
            Assert.assertEquals(new DoubleWritable(8.95), fields.get(2));
            Assert.assertEquals(RecordMetaDataJdbc.class, meta.getClass());
        }
    }

    @Test
    public void testNextRecordAndRecover() throws Exception {
        try (JDBCRecordReader reader = getInitializedReader("SELECT * FROM Coffee")) {
            Record r = reader.nextRecord();
            List<Writable> fields = r.getRecord();
            RecordMetaData meta = r.getMetaData();
            Record recovered = reader.loadFromMetaData(meta);
            List<Writable> fieldsRecovered = recovered.getRecord();
            Assert.assertEquals(fields.size(), fieldsRecovered.size());
            for (int i = 0; i < (fields.size()); i++) {
                Assert.assertEquals(fields.get(i), fieldsRecovered.get(i));
            }
        }
    }

    // Resetting the record reader when initialized as forward only should fail
    @Test(expected = RuntimeException.class)
    public void testResetForwardOnlyShouldFail() throws Exception {
        try (JDBCRecordReader reader = new JDBCRecordReader("SELECT * FROM Coffee", dataSource)) {
            Configuration conf = new Configuration();
            conf.setInt(JDBC_RESULTSET_TYPE, ResultSet.TYPE_FORWARD_ONLY);
            reader.initialize(conf, null);
            reader.next();
            reader.reset();
        }
    }

    @Test
    public void testReadAllTypes() throws Exception {
        TestDb.buildAllTypesTable(conn);
        try (JDBCRecordReader reader = new JDBCRecordReader("SELECT * FROM AllTypes", dataSource)) {
            reader.initialize(null);
            List<Writable> item = reader.next();
            Assert.assertEquals(item.size(), 15);
            Assert.assertEquals(BooleanWritable.class, item.get(0).getClass());// boolean to boolean

            Assert.assertEquals(Text.class, item.get(1).getClass());// date to text

            Assert.assertEquals(Text.class, item.get(2).getClass());// time to text

            Assert.assertEquals(Text.class, item.get(3).getClass());// timestamp to text

            Assert.assertEquals(Text.class, item.get(4).getClass());// char to text

            Assert.assertEquals(Text.class, item.get(5).getClass());// long varchar to text

            Assert.assertEquals(Text.class, item.get(6).getClass());// varchar to text

            Assert.assertEquals(DoubleWritable.class, item.get(7).getClass());// float to double (derby's float is an alias of double by default)

            Assert.assertEquals(FloatWritable.class, item.get(8).getClass());// real to float

            Assert.assertEquals(DoubleWritable.class, item.get(9).getClass());// decimal to double

            Assert.assertEquals(DoubleWritable.class, item.get(10).getClass());// numeric to double

            Assert.assertEquals(DoubleWritable.class, item.get(11).getClass());// double to double

            Assert.assertEquals(IntWritable.class, item.get(12).getClass());// integer to integer

            Assert.assertEquals(IntWritable.class, item.get(13).getClass());// small int to integer

            Assert.assertEquals(LongWritable.class, item.get(14).getClass());// bigint to long

        }
    }

    @Test(expected = RuntimeException.class)
    public void testNextNoMoreShouldFail() throws Exception {
        try (JDBCRecordReader reader = getInitializedReader("SELECT * FROM Coffee")) {
            while (reader.hasNext()) {
                reader.next();
            } 
            reader.next();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidMetadataShouldFail() throws Exception {
        try (JDBCRecordReader reader = getInitializedReader("SELECT * FROM Coffee")) {
            RecordMetaDataLine md = new RecordMetaDataLine(1, new URI("file://test"), JDBCRecordReader.class);
            reader.loadFromMetaData(md);
        }
    }
}

