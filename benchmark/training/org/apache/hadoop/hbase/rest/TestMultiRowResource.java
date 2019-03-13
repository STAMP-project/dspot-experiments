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
import Constants.MIMETYPE_JSON;
import Constants.MIMETYPE_XML;
import MediaType.APPLICATION_JSON_TYPE;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import java.io.IOException;
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
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RestTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.http.Header;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@Category({ RestTests.class, MediumTests.class })
@RunWith(Parameterized.class)
public class TestMultiRowResource {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMultiRowResource.class);

    private static final TableName TABLE = TableName.valueOf("TestRowResource");

    private static final String CFA = "a";

    private static final String CFB = "b";

    private static final String COLUMN_1 = (TestMultiRowResource.CFA) + ":1";

    private static final String COLUMN_2 = (TestMultiRowResource.CFB) + ":2";

    private static final String ROW_1 = "testrow5";

    private static final String VALUE_1 = "testvalue5";

    private static final String ROW_2 = "testrow6";

    private static final String VALUE_2 = "testvalue6";

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final HBaseRESTTestingUtility REST_TEST_UTIL = new HBaseRESTTestingUtility();

    private static Client client;

    private static JAXBContext context;

    private static Marshaller marshaller;

    private static Unmarshaller unmarshaller;

    private static Configuration conf;

    private static Header extraHdr = null;

    private static boolean csrfEnabled = true;

    public TestMultiRowResource(Boolean csrf) {
        TestMultiRowResource.csrfEnabled = csrf;
    }

    @Test
    public void testMultiCellGetJSON() throws IOException, JAXBException {
        String row_5_url = (((("/" + (TestMultiRowResource.TABLE)) + "/") + (TestMultiRowResource.ROW_1)) + "/") + (TestMultiRowResource.COLUMN_1);
        String row_6_url = (((("/" + (TestMultiRowResource.TABLE)) + "/") + (TestMultiRowResource.ROW_2)) + "/") + (TestMultiRowResource.COLUMN_2);
        StringBuilder path = new StringBuilder();
        path.append("/");
        path.append(TestMultiRowResource.TABLE);
        path.append("/multiget/?row=");
        path.append(TestMultiRowResource.ROW_1);
        path.append("&row=");
        path.append(TestMultiRowResource.ROW_2);
        if (TestMultiRowResource.csrfEnabled) {
            Response response = TestMultiRowResource.client.post(row_5_url, MIMETYPE_BINARY, Bytes.toBytes(TestMultiRowResource.VALUE_1));
            Assert.assertEquals(400, response.getCode());
        }
        TestMultiRowResource.client.post(row_5_url, MIMETYPE_BINARY, Bytes.toBytes(TestMultiRowResource.VALUE_1), TestMultiRowResource.extraHdr);
        TestMultiRowResource.client.post(row_6_url, MIMETYPE_BINARY, Bytes.toBytes(TestMultiRowResource.VALUE_2), TestMultiRowResource.extraHdr);
        Response response = TestMultiRowResource.client.get(path.toString(), MIMETYPE_JSON);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_JSON, response.getHeader("content-type"));
        TestMultiRowResource.client.delete(row_5_url, TestMultiRowResource.extraHdr);
        TestMultiRowResource.client.delete(row_6_url, TestMultiRowResource.extraHdr);
    }

    @Test
    public void testMultiCellGetXML() throws IOException, JAXBException {
        String row_5_url = (((("/" + (TestMultiRowResource.TABLE)) + "/") + (TestMultiRowResource.ROW_1)) + "/") + (TestMultiRowResource.COLUMN_1);
        String row_6_url = (((("/" + (TestMultiRowResource.TABLE)) + "/") + (TestMultiRowResource.ROW_2)) + "/") + (TestMultiRowResource.COLUMN_2);
        StringBuilder path = new StringBuilder();
        path.append("/");
        path.append(TestMultiRowResource.TABLE);
        path.append("/multiget/?row=");
        path.append(TestMultiRowResource.ROW_1);
        path.append("&row=");
        path.append(TestMultiRowResource.ROW_2);
        TestMultiRowResource.client.post(row_5_url, MIMETYPE_BINARY, Bytes.toBytes(TestMultiRowResource.VALUE_1), TestMultiRowResource.extraHdr);
        TestMultiRowResource.client.post(row_6_url, MIMETYPE_BINARY, Bytes.toBytes(TestMultiRowResource.VALUE_2), TestMultiRowResource.extraHdr);
        Response response = TestMultiRowResource.client.get(path.toString(), MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_XML, response.getHeader("content-type"));
        TestMultiRowResource.client.delete(row_5_url, TestMultiRowResource.extraHdr);
        TestMultiRowResource.client.delete(row_6_url, TestMultiRowResource.extraHdr);
    }

    @Test
    public void testMultiCellGetWithColsJSON() throws IOException, JAXBException {
        String row_5_url = (((("/" + (TestMultiRowResource.TABLE)) + "/") + (TestMultiRowResource.ROW_1)) + "/") + (TestMultiRowResource.COLUMN_1);
        String row_6_url = (((("/" + (TestMultiRowResource.TABLE)) + "/") + (TestMultiRowResource.ROW_2)) + "/") + (TestMultiRowResource.COLUMN_2);
        StringBuilder path = new StringBuilder();
        path.append("/");
        path.append(TestMultiRowResource.TABLE);
        path.append("/multiget");
        path.append(((("/" + (TestMultiRowResource.COLUMN_1)) + ",") + (TestMultiRowResource.CFB)));
        path.append("?row=");
        path.append(TestMultiRowResource.ROW_1);
        path.append("&row=");
        path.append(TestMultiRowResource.ROW_2);
        TestMultiRowResource.client.post(row_5_url, MIMETYPE_BINARY, Bytes.toBytes(TestMultiRowResource.VALUE_1), TestMultiRowResource.extraHdr);
        TestMultiRowResource.client.post(row_6_url, MIMETYPE_BINARY, Bytes.toBytes(TestMultiRowResource.VALUE_2), TestMultiRowResource.extraHdr);
        Response response = TestMultiRowResource.client.get(path.toString(), MIMETYPE_JSON);
        Assert.assertEquals(200, response.getCode());
        ObjectMapper mapper = new JacksonJaxbJsonProvider().locateMapper(CellSetModel.class, APPLICATION_JSON_TYPE);
        CellSetModel cellSet = mapper.readValue(response.getBody(), CellSetModel.class);
        Assert.assertEquals(2, cellSet.getRows().size());
        Assert.assertEquals(TestMultiRowResource.ROW_1, Bytes.toString(cellSet.getRows().get(0).getKey()));
        Assert.assertEquals(TestMultiRowResource.VALUE_1, Bytes.toString(cellSet.getRows().get(0).getCells().get(0).getValue()));
        Assert.assertEquals(TestMultiRowResource.ROW_2, Bytes.toString(cellSet.getRows().get(1).getKey()));
        Assert.assertEquals(TestMultiRowResource.VALUE_2, Bytes.toString(cellSet.getRows().get(1).getCells().get(0).getValue()));
        TestMultiRowResource.client.delete(row_5_url, TestMultiRowResource.extraHdr);
        TestMultiRowResource.client.delete(row_6_url, TestMultiRowResource.extraHdr);
    }

    @Test
    public void testMultiCellGetJSONNotFound() throws IOException, JAXBException {
        String row_5_url = (((("/" + (TestMultiRowResource.TABLE)) + "/") + (TestMultiRowResource.ROW_1)) + "/") + (TestMultiRowResource.COLUMN_1);
        StringBuilder path = new StringBuilder();
        path.append("/");
        path.append(TestMultiRowResource.TABLE);
        path.append("/multiget/?row=");
        path.append(TestMultiRowResource.ROW_1);
        path.append("&row=");
        path.append(TestMultiRowResource.ROW_2);
        TestMultiRowResource.client.post(row_5_url, MIMETYPE_BINARY, Bytes.toBytes(TestMultiRowResource.VALUE_1), TestMultiRowResource.extraHdr);
        Response response = TestMultiRowResource.client.get(path.toString(), MIMETYPE_JSON);
        Assert.assertEquals(200, response.getCode());
        ObjectMapper mapper = new JacksonJaxbJsonProvider().locateMapper(CellSetModel.class, APPLICATION_JSON_TYPE);
        CellSetModel cellSet = ((CellSetModel) (mapper.readValue(response.getBody(), CellSetModel.class)));
        Assert.assertEquals(1, cellSet.getRows().size());
        Assert.assertEquals(TestMultiRowResource.ROW_1, Bytes.toString(cellSet.getRows().get(0).getKey()));
        Assert.assertEquals(TestMultiRowResource.VALUE_1, Bytes.toString(cellSet.getRows().get(0).getCells().get(0).getValue()));
        TestMultiRowResource.client.delete(row_5_url, TestMultiRowResource.extraHdr);
    }

    @Test
    public void testMultiCellGetWithColsInQueryPathJSON() throws IOException, JAXBException {
        String row_5_url = (((("/" + (TestMultiRowResource.TABLE)) + "/") + (TestMultiRowResource.ROW_1)) + "/") + (TestMultiRowResource.COLUMN_1);
        String row_6_url = (((("/" + (TestMultiRowResource.TABLE)) + "/") + (TestMultiRowResource.ROW_2)) + "/") + (TestMultiRowResource.COLUMN_2);
        StringBuilder path = new StringBuilder();
        path.append("/");
        path.append(TestMultiRowResource.TABLE);
        path.append("/multiget/?row=");
        path.append(TestMultiRowResource.ROW_1);
        path.append("/");
        path.append(TestMultiRowResource.COLUMN_1);
        path.append("&row=");
        path.append(TestMultiRowResource.ROW_2);
        path.append("/");
        path.append(TestMultiRowResource.COLUMN_1);
        TestMultiRowResource.client.post(row_5_url, MIMETYPE_BINARY, Bytes.toBytes(TestMultiRowResource.VALUE_1), TestMultiRowResource.extraHdr);
        TestMultiRowResource.client.post(row_6_url, MIMETYPE_BINARY, Bytes.toBytes(TestMultiRowResource.VALUE_2), TestMultiRowResource.extraHdr);
        Response response = TestMultiRowResource.client.get(path.toString(), MIMETYPE_JSON);
        Assert.assertEquals(200, response.getCode());
        ObjectMapper mapper = new JacksonJaxbJsonProvider().locateMapper(CellSetModel.class, APPLICATION_JSON_TYPE);
        CellSetModel cellSet = mapper.readValue(response.getBody(), CellSetModel.class);
        Assert.assertEquals(1, cellSet.getRows().size());
        Assert.assertEquals(TestMultiRowResource.ROW_1, Bytes.toString(cellSet.getRows().get(0).getKey()));
        Assert.assertEquals(TestMultiRowResource.VALUE_1, Bytes.toString(cellSet.getRows().get(0).getCells().get(0).getValue()));
        TestMultiRowResource.client.delete(row_5_url, TestMultiRowResource.extraHdr);
        TestMultiRowResource.client.delete(row_6_url, TestMultiRowResource.extraHdr);
    }
}

