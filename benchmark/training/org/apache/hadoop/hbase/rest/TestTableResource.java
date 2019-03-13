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


import Constants.MIMETYPE_JSON;
import Constants.MIMETYPE_PROTOBUF;
import Constants.MIMETYPE_PROTOBUF_IETF;
import Constants.MIMETYPE_TEXT;
import Constants.MIMETYPE_XML;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.rest.client.Client;
import org.apache.hadoop.hbase.rest.client.Response;
import org.apache.hadoop.hbase.rest.model.TableInfoModel;
import org.apache.hadoop.hbase.rest.model.TableListModel;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RestTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ RestTests.class, MediumTests.class })
public class TestTableResource {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestTableResource.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestTableResource.class);

    private static final TableName TABLE = TableName.valueOf("TestTableResource");

    private static final String COLUMN_FAMILY = "test";

    private static final String COLUMN = (TestTableResource.COLUMN_FAMILY) + ":qualifier";

    private static final int NUM_REGIONS = 4;

    private static List<HRegionLocation> regionMap;

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final HBaseRESTTestingUtility REST_TEST_UTIL = new HBaseRESTTestingUtility();

    private static Client client;

    private static JAXBContext context;

    @Test
    public void testTableListText() throws IOException {
        Response response = TestTableResource.client.get("/", MIMETYPE_TEXT);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_TEXT, response.getHeader("content-type"));
    }

    @Test
    public void testTableListXML() throws IOException, JAXBException {
        Response response = TestTableResource.client.get("/", MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_XML, response.getHeader("content-type"));
        TableListModel model = ((TableListModel) (TestTableResource.context.createUnmarshaller().unmarshal(new ByteArrayInputStream(response.getBody()))));
        TestTableResource.checkTableList(model);
    }

    @Test
    public void testTableListJSON() throws IOException {
        Response response = TestTableResource.client.get("/", MIMETYPE_JSON);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_JSON, response.getHeader("content-type"));
    }

    @Test
    public void testTableListPB() throws IOException, JAXBException {
        Response response = TestTableResource.client.get("/", MIMETYPE_PROTOBUF);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_PROTOBUF, response.getHeader("content-type"));
        TableListModel model = new TableListModel();
        model.getObjectFromMessage(response.getBody());
        TestTableResource.checkTableList(model);
        response = TestTableResource.client.get("/", MIMETYPE_PROTOBUF_IETF);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_PROTOBUF_IETF, response.getHeader("content-type"));
        model = new TableListModel();
        model.getObjectFromMessage(response.getBody());
        TestTableResource.checkTableList(model);
    }

    @Test
    public void testTableInfoText() throws IOException {
        Response response = TestTableResource.client.get((("/" + (TestTableResource.TABLE)) + "/regions"), MIMETYPE_TEXT);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_TEXT, response.getHeader("content-type"));
    }

    @Test
    public void testTableInfoXML() throws IOException, JAXBException {
        Response response = TestTableResource.client.get((("/" + (TestTableResource.TABLE)) + "/regions"), MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_XML, response.getHeader("content-type"));
        TableInfoModel model = ((TableInfoModel) (TestTableResource.context.createUnmarshaller().unmarshal(new ByteArrayInputStream(response.getBody()))));
        checkTableInfo(model);
    }

    @Test
    public void testTableInfoJSON() throws IOException {
        Response response = TestTableResource.client.get((("/" + (TestTableResource.TABLE)) + "/regions"), MIMETYPE_JSON);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_JSON, response.getHeader("content-type"));
    }

    @Test
    public void testTableInfoPB() throws IOException, JAXBException {
        Response response = TestTableResource.client.get((("/" + (TestTableResource.TABLE)) + "/regions"), MIMETYPE_PROTOBUF);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_PROTOBUF, response.getHeader("content-type"));
        TableInfoModel model = new TableInfoModel();
        model.getObjectFromMessage(response.getBody());
        checkTableInfo(model);
        response = TestTableResource.client.get((("/" + (TestTableResource.TABLE)) + "/regions"), MIMETYPE_PROTOBUF_IETF);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_PROTOBUF_IETF, response.getHeader("content-type"));
        model = new TableInfoModel();
        model.getObjectFromMessage(response.getBody());
        checkTableInfo(model);
    }
}

