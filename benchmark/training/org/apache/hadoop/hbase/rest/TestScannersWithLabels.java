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
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RestTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ RestTests.class, MediumTests.class })
public class TestScannersWithLabels {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestScannersWithLabels.class);

    private static final TableName TABLE = TableName.valueOf("TestScannersWithLabels");

    private static final String CFA = "a";

    private static final String CFB = "b";

    private static final String COLUMN_1 = (TestScannersWithLabels.CFA) + ":1";

    private static final String COLUMN_2 = (TestScannersWithLabels.CFB) + ":2";

    private static final String TOPSECRET = "topsecret";

    private static final String PUBLIC = "public";

    private static final String PRIVATE = "private";

    private static final String CONFIDENTIAL = "confidential";

    private static final String SECRET = "secret";

    private static User SUPERUSER;

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final HBaseRESTTestingUtility REST_TEST_UTIL = new HBaseRESTTestingUtility();

    private static Client client;

    private static JAXBContext context;

    private static Marshaller marshaller;

    private static Unmarshaller unmarshaller;

    private static Configuration conf;

    @Test
    public void testSimpleScannerXMLWithLabelsThatReceivesNoData() throws IOException, JAXBException {
        final int BATCH_SIZE = 5;
        // new scanner
        ScannerModel model = new ScannerModel();
        model.setBatch(BATCH_SIZE);
        model.addColumn(Bytes.toBytes(TestScannersWithLabels.COLUMN_1));
        model.addLabel(TestScannersWithLabels.PUBLIC);
        StringWriter writer = new StringWriter();
        TestScannersWithLabels.marshaller.marshal(model, writer);
        byte[] body = Bytes.toBytes(writer.toString());
        // recall previous put operation with read-only off
        TestScannersWithLabels.conf.set("hbase.rest.readonly", "false");
        Response response = TestScannersWithLabels.client.put((("/" + (TestScannersWithLabels.TABLE)) + "/scanner"), MIMETYPE_XML, body);
        Assert.assertEquals(201, response.getCode());
        String scannerURI = response.getLocation();
        Assert.assertNotNull(scannerURI);
        // get a cell set
        response = TestScannersWithLabels.client.get(scannerURI, MIMETYPE_XML);
        // Respond with 204 as there are no cells to be retrieved
        Assert.assertEquals(204, response.getCode());
        // With no content in the payload, the 'Content-Type' header is not echo back
    }

    @Test
    public void testSimpleScannerXMLWithLabelsThatReceivesData() throws IOException, JAXBException {
        // new scanner
        ScannerModel model = new ScannerModel();
        model.setBatch(5);
        model.addColumn(Bytes.toBytes(TestScannersWithLabels.COLUMN_1));
        model.addLabel(TestScannersWithLabels.SECRET);
        StringWriter writer = new StringWriter();
        TestScannersWithLabels.marshaller.marshal(model, writer);
        byte[] body = Bytes.toBytes(writer.toString());
        // recall previous put operation with read-only off
        TestScannersWithLabels.conf.set("hbase.rest.readonly", "false");
        Response response = TestScannersWithLabels.client.put((("/" + (TestScannersWithLabels.TABLE)) + "/scanner"), MIMETYPE_XML, body);
        Assert.assertEquals(201, response.getCode());
        String scannerURI = response.getLocation();
        Assert.assertNotNull(scannerURI);
        // get a cell set
        response = TestScannersWithLabels.client.get(scannerURI, MIMETYPE_XML);
        // Respond with 204 as there are no cells to be retrieved
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_XML, response.getHeader("content-type"));
        CellSetModel cellSet = ((CellSetModel) (TestScannersWithLabels.unmarshaller.unmarshal(new ByteArrayInputStream(response.getBody()))));
        Assert.assertEquals(5, TestScannersWithLabels.countCellSet(cellSet));
    }
}

