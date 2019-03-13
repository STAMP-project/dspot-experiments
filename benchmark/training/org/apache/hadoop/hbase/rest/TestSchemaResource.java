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
import Constants.MIMETYPE_XML;
import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.rest.client.Client;
import org.apache.hadoop.hbase.rest.client.Response;
import org.apache.hadoop.hbase.rest.model.TableSchemaModel;
import org.apache.hadoop.hbase.rest.model.TestTableSchemaModel;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ RestTests.class, MediumTests.class })
@RunWith(Parameterized.class)
public class TestSchemaResource {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSchemaResource.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSchemaResource.class);

    private static String TABLE1 = "TestSchemaResource1";

    private static String TABLE2 = "TestSchemaResource2";

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final HBaseRESTTestingUtility REST_TEST_UTIL = new HBaseRESTTestingUtility();

    private static Client client;

    private static JAXBContext context;

    private static Configuration conf;

    private static TestTableSchemaModel testTableSchemaModel;

    private static Header extraHdr = null;

    private static boolean csrfEnabled = true;

    public TestSchemaResource(Boolean csrf) {
        TestSchemaResource.csrfEnabled = csrf;
    }

    @Test
    public void testTableCreateAndDeleteXML() throws IOException, JAXBException {
        String schemaPath = ("/" + (TestSchemaResource.TABLE1)) + "/schema";
        TableSchemaModel model;
        Response response;
        Admin admin = TestSchemaResource.TEST_UTIL.getAdmin();
        Assert.assertFalse((("Table " + (TestSchemaResource.TABLE1)) + " should not exist"), admin.tableExists(TableName.valueOf(TestSchemaResource.TABLE1)));
        // create the table
        model = TestSchemaResource.testTableSchemaModel.buildTestModel(TestSchemaResource.TABLE1);
        TestSchemaResource.testTableSchemaModel.checkModel(model, TestSchemaResource.TABLE1);
        if (TestSchemaResource.csrfEnabled) {
            // test put operation is forbidden without custom header
            response = TestSchemaResource.client.put(schemaPath, MIMETYPE_XML, TestSchemaResource.toXML(model));
            Assert.assertEquals(400, response.getCode());
        }
        response = TestSchemaResource.client.put(schemaPath, MIMETYPE_XML, TestSchemaResource.toXML(model), TestSchemaResource.extraHdr);
        Assert.assertEquals(("put failed with csrf " + (TestSchemaResource.csrfEnabled ? "enabled" : "disabled")), 201, response.getCode());
        // recall the same put operation but in read-only mode
        TestSchemaResource.conf.set("hbase.rest.readonly", "true");
        response = TestSchemaResource.client.put(schemaPath, MIMETYPE_XML, TestSchemaResource.toXML(model), TestSchemaResource.extraHdr);
        Assert.assertEquals(403, response.getCode());
        // retrieve the schema and validate it
        response = TestSchemaResource.client.get(schemaPath, MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_XML, response.getHeader("content-type"));
        model = TestSchemaResource.fromXML(response.getBody());
        TestSchemaResource.testTableSchemaModel.checkModel(model, TestSchemaResource.TABLE1);
        // with json retrieve the schema and validate it
        response = TestSchemaResource.client.get(schemaPath, MIMETYPE_JSON);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_JSON, response.getHeader("content-type"));
        model = TestSchemaResource.testTableSchemaModel.fromJSON(Bytes.toString(response.getBody()));
        TestSchemaResource.testTableSchemaModel.checkModel(model, TestSchemaResource.TABLE1);
        if (TestSchemaResource.csrfEnabled) {
            // test delete schema operation is forbidden without custom header
            response = TestSchemaResource.client.delete(schemaPath);
            Assert.assertEquals(400, response.getCode());
        }
        // test delete schema operation is forbidden in read-only mode
        response = TestSchemaResource.client.delete(schemaPath, TestSchemaResource.extraHdr);
        Assert.assertEquals(403, response.getCode());
        // return read-only setting back to default
        TestSchemaResource.conf.set("hbase.rest.readonly", "false");
        // delete the table and make sure HBase concurs
        response = TestSchemaResource.client.delete(schemaPath, TestSchemaResource.extraHdr);
        Assert.assertEquals(200, response.getCode());
        Assert.assertFalse(admin.tableExists(TableName.valueOf(TestSchemaResource.TABLE1)));
    }

    @Test
    public void testTableCreateAndDeletePB() throws IOException, JAXBException {
        String schemaPath = ("/" + (TestSchemaResource.TABLE2)) + "/schema";
        TableSchemaModel model;
        Response response;
        Admin admin = TestSchemaResource.TEST_UTIL.getAdmin();
        Assert.assertFalse(admin.tableExists(TableName.valueOf(TestSchemaResource.TABLE2)));
        // create the table
        model = TestSchemaResource.testTableSchemaModel.buildTestModel(TestSchemaResource.TABLE2);
        TestSchemaResource.testTableSchemaModel.checkModel(model, TestSchemaResource.TABLE2);
        if (TestSchemaResource.csrfEnabled) {
            // test put operation is forbidden without custom header
            response = TestSchemaResource.client.put(schemaPath, MIMETYPE_PROTOBUF, model.createProtobufOutput());
            Assert.assertEquals(400, response.getCode());
        }
        response = TestSchemaResource.client.put(schemaPath, MIMETYPE_PROTOBUF, model.createProtobufOutput(), TestSchemaResource.extraHdr);
        Assert.assertEquals(("put failed with csrf " + (TestSchemaResource.csrfEnabled ? "enabled" : "disabled")), 201, response.getCode());
        // recall the same put operation but in read-only mode
        TestSchemaResource.conf.set("hbase.rest.readonly", "true");
        response = TestSchemaResource.client.put(schemaPath, MIMETYPE_PROTOBUF, model.createProtobufOutput(), TestSchemaResource.extraHdr);
        Assert.assertNotNull(TestSchemaResource.extraHdr);
        Assert.assertEquals(403, response.getCode());
        // retrieve the schema and validate it
        response = TestSchemaResource.client.get(schemaPath, MIMETYPE_PROTOBUF);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_PROTOBUF, response.getHeader("content-type"));
        model = new TableSchemaModel();
        model.getObjectFromMessage(response.getBody());
        TestSchemaResource.testTableSchemaModel.checkModel(model, TestSchemaResource.TABLE2);
        // retrieve the schema and validate it with alternate pbuf type
        response = TestSchemaResource.client.get(schemaPath, MIMETYPE_PROTOBUF_IETF);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_PROTOBUF_IETF, response.getHeader("content-type"));
        model = new TableSchemaModel();
        model.getObjectFromMessage(response.getBody());
        TestSchemaResource.testTableSchemaModel.checkModel(model, TestSchemaResource.TABLE2);
        if (TestSchemaResource.csrfEnabled) {
            // test delete schema operation is forbidden without custom header
            response = TestSchemaResource.client.delete(schemaPath);
            Assert.assertEquals(400, response.getCode());
        }
        // test delete schema operation is forbidden in read-only mode
        response = TestSchemaResource.client.delete(schemaPath, TestSchemaResource.extraHdr);
        Assert.assertEquals(403, response.getCode());
        // return read-only setting back to default
        TestSchemaResource.conf.set("hbase.rest.readonly", "false");
        // delete the table and make sure HBase concurs
        response = TestSchemaResource.client.delete(schemaPath, TestSchemaResource.extraHdr);
        Assert.assertEquals(200, response.getCode());
        Assert.assertFalse(admin.tableExists(TableName.valueOf(TestSchemaResource.TABLE2)));
    }
}

