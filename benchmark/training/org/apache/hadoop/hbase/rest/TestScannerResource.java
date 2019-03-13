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
package org.apache.hadoop.hbase.rest;


import Constants.MIMETYPE_BINARY;
import Constants.MIMETYPE_PROTOBUF;
import Constants.MIMETYPE_XML;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.rest.client.Client;
import org.apache.hadoop.hbase.rest.client.Response;
import org.apache.hadoop.hbase.rest.model.CellSetModel;
import org.apache.hadoop.hbase.rest.model.ScannerModel;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RestTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.http.Header;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ RestTests.class, MediumTests.class })
public class TestScannerResource {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestScannerResource.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestScannerResource.class);

    private static final TableName TABLE = TableName.valueOf("TestScannerResource");

    private static final TableName TABLE_TO_BE_DISABLED = TableName.valueOf("ScannerResourceDisable");

    private static final String NONEXISTENT_TABLE = "ThisTableDoesNotExist";

    private static final String CFA = "a";

    private static final String CFB = "b";

    private static final String COLUMN_1 = (TestScannerResource.CFA) + ":1";

    private static final String COLUMN_2 = (TestScannerResource.CFB) + ":2";

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final HBaseRESTTestingUtility REST_TEST_UTIL = new HBaseRESTTestingUtility();

    private static Client client;

    private static JAXBContext context;

    private static Marshaller marshaller;

    private static Unmarshaller unmarshaller;

    private static int expectedRows1;

    private static int expectedRows2;

    private static Configuration conf;

    @Test
    public void testSimpleScannerXML() throws IOException, JAXBException {
        final int BATCH_SIZE = 5;
        // new scanner
        ScannerModel model = new ScannerModel();
        model.setBatch(BATCH_SIZE);
        model.addColumn(Bytes.toBytes(TestScannerResource.COLUMN_1));
        StringWriter writer = new StringWriter();
        TestScannerResource.marshaller.marshal(model, writer);
        byte[] body = Bytes.toBytes(writer.toString());
        // test put operation is forbidden in read-only mode
        TestScannerResource.conf.set("hbase.rest.readonly", "true");
        Response response = TestScannerResource.client.put((("/" + (TestScannerResource.TABLE)) + "/scanner"), MIMETYPE_XML, body);
        Assert.assertEquals(403, response.getCode());
        String scannerURI = response.getLocation();
        Assert.assertNull(scannerURI);
        // recall previous put operation with read-only off
        TestScannerResource.conf.set("hbase.rest.readonly", "false");
        response = TestScannerResource.client.put((("/" + (TestScannerResource.TABLE)) + "/scanner"), MIMETYPE_XML, body);
        Assert.assertEquals(201, response.getCode());
        scannerURI = response.getLocation();
        Assert.assertNotNull(scannerURI);
        // get a cell set
        response = TestScannerResource.client.get(scannerURI, MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_XML, response.getHeader("content-type"));
        CellSetModel cellSet = ((CellSetModel) (TestScannerResource.unmarshaller.unmarshal(new ByteArrayInputStream(response.getBody()))));
        // confirm batch size conformance
        Assert.assertEquals(BATCH_SIZE, TestScannerResource.countCellSet(cellSet));
        // test delete scanner operation is forbidden in read-only mode
        TestScannerResource.conf.set("hbase.rest.readonly", "true");
        response = TestScannerResource.client.delete(scannerURI);
        Assert.assertEquals(403, response.getCode());
        // recall previous delete scanner operation with read-only off
        TestScannerResource.conf.set("hbase.rest.readonly", "false");
        response = TestScannerResource.client.delete(scannerURI);
        Assert.assertEquals(200, response.getCode());
    }

    @Test
    public void testSimpleScannerPB() throws IOException {
        final int BATCH_SIZE = 10;
        // new scanner
        ScannerModel model = new ScannerModel();
        model.setBatch(BATCH_SIZE);
        model.addColumn(Bytes.toBytes(TestScannerResource.COLUMN_1));
        // test put operation is forbidden in read-only mode
        TestScannerResource.conf.set("hbase.rest.readonly", "true");
        Response response = TestScannerResource.client.put((("/" + (TestScannerResource.TABLE)) + "/scanner"), MIMETYPE_PROTOBUF, model.createProtobufOutput());
        Assert.assertEquals(403, response.getCode());
        String scannerURI = response.getLocation();
        Assert.assertNull(scannerURI);
        // recall previous put operation with read-only off
        TestScannerResource.conf.set("hbase.rest.readonly", "false");
        response = TestScannerResource.client.put((("/" + (TestScannerResource.TABLE)) + "/scanner"), MIMETYPE_PROTOBUF, model.createProtobufOutput());
        Assert.assertEquals(201, response.getCode());
        scannerURI = response.getLocation();
        Assert.assertNotNull(scannerURI);
        // get a cell set
        response = TestScannerResource.client.get(scannerURI, MIMETYPE_PROTOBUF);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_PROTOBUF, response.getHeader("content-type"));
        CellSetModel cellSet = new CellSetModel();
        cellSet.getObjectFromMessage(response.getBody());
        // confirm batch size conformance
        Assert.assertEquals(BATCH_SIZE, TestScannerResource.countCellSet(cellSet));
        // test delete scanner operation is forbidden in read-only mode
        TestScannerResource.conf.set("hbase.rest.readonly", "true");
        response = TestScannerResource.client.delete(scannerURI);
        Assert.assertEquals(403, response.getCode());
        // recall previous delete scanner operation with read-only off
        TestScannerResource.conf.set("hbase.rest.readonly", "false");
        response = TestScannerResource.client.delete(scannerURI);
        Assert.assertEquals(200, response.getCode());
    }

    @Test
    public void testSimpleScannerBinary() throws IOException {
        // new scanner
        ScannerModel model = new ScannerModel();
        model.setBatch(1);
        model.addColumn(Bytes.toBytes(TestScannerResource.COLUMN_1));
        // test put operation is forbidden in read-only mode
        TestScannerResource.conf.set("hbase.rest.readonly", "true");
        Response response = TestScannerResource.client.put((("/" + (TestScannerResource.TABLE)) + "/scanner"), MIMETYPE_PROTOBUF, model.createProtobufOutput());
        Assert.assertEquals(403, response.getCode());
        String scannerURI = response.getLocation();
        Assert.assertNull(scannerURI);
        // recall previous put operation with read-only off
        TestScannerResource.conf.set("hbase.rest.readonly", "false");
        response = TestScannerResource.client.put((("/" + (TestScannerResource.TABLE)) + "/scanner"), MIMETYPE_PROTOBUF, model.createProtobufOutput());
        Assert.assertEquals(201, response.getCode());
        scannerURI = response.getLocation();
        Assert.assertNotNull(scannerURI);
        // get a cell
        response = TestScannerResource.client.get(scannerURI, MIMETYPE_BINARY);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_BINARY, response.getHeader("content-type"));
        // verify that data was returned
        Assert.assertTrue(((response.getBody().length) > 0));
        // verify that the expected X-headers are present
        boolean foundRowHeader = false;
        boolean foundColumnHeader = false;
        boolean foundTimestampHeader = false;
        for (Header header : response.getHeaders()) {
            if (header.getName().equals("X-Row")) {
                foundRowHeader = true;
            } else
                if (header.getName().equals("X-Column")) {
                    foundColumnHeader = true;
                } else
                    if (header.getName().equals("X-Timestamp")) {
                        foundTimestampHeader = true;
                    }


        }
        Assert.assertTrue(foundRowHeader);
        Assert.assertTrue(foundColumnHeader);
        Assert.assertTrue(foundTimestampHeader);
        // test delete scanner operation is forbidden in read-only mode
        TestScannerResource.conf.set("hbase.rest.readonly", "true");
        response = TestScannerResource.client.delete(scannerURI);
        Assert.assertEquals(403, response.getCode());
        // recall previous delete scanner operation with read-only off
        TestScannerResource.conf.set("hbase.rest.readonly", "false");
        response = TestScannerResource.client.delete(scannerURI);
        Assert.assertEquals(200, response.getCode());
    }

    @Test
    public void testFullTableScan() throws IOException {
        ScannerModel model = new ScannerModel();
        model.addColumn(Bytes.toBytes(TestScannerResource.COLUMN_1));
        Assert.assertEquals(TestScannerResource.expectedRows1, TestScannerResource.fullTableScan(model));
        model = new ScannerModel();
        model.addColumn(Bytes.toBytes(TestScannerResource.COLUMN_2));
        Assert.assertEquals(TestScannerResource.expectedRows2, TestScannerResource.fullTableScan(model));
    }

    @Test
    public void testTableDoesNotExist() throws IOException, JAXBException {
        ScannerModel model = new ScannerModel();
        StringWriter writer = new StringWriter();
        TestScannerResource.marshaller.marshal(model, writer);
        byte[] body = Bytes.toBytes(writer.toString());
        Response response = TestScannerResource.client.put((("/" + (TestScannerResource.NONEXISTENT_TABLE)) + "/scanner"), MIMETYPE_XML, body);
        String scannerURI = response.getLocation();
        Assert.assertNotNull(scannerURI);
        response = TestScannerResource.client.get(scannerURI, MIMETYPE_XML);
        Assert.assertEquals(404, response.getCode());
    }

    // performs table scan during which the underlying table is disabled
    // assert that we get 410 (Gone)
    @Test
    public void testTableScanWithTableDisable() throws IOException {
        ScannerModel model = new ScannerModel();
        model.addColumn(Bytes.toBytes(TestScannerResource.COLUMN_1));
        model.setCaching(1);
        Response response = TestScannerResource.client.put((("/" + (TestScannerResource.TABLE_TO_BE_DISABLED)) + "/scanner"), MIMETYPE_PROTOBUF, model.createProtobufOutput());
        Assert.assertEquals(201, response.getCode());
        String scannerURI = response.getLocation();
        Assert.assertNotNull(scannerURI);
        TestScannerResource.TEST_UTIL.getAdmin().disableTable(TestScannerResource.TABLE_TO_BE_DISABLED);
        response = TestScannerResource.client.get(scannerURI, MIMETYPE_PROTOBUF);
        Assert.assertTrue(("got " + (response.getCode())), ((response.getCode()) == 410));
    }
}

