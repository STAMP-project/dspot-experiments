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
import Constants.MIMETYPE_PROTOBUF;
import Constants.MIMETYPE_XML;
import NamespaceDescriptor.Builder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.rest.client.Client;
import org.apache.hadoop.hbase.rest.client.Response;
import org.apache.hadoop.hbase.rest.model.NamespacesInstanceModel;
import org.apache.hadoop.hbase.rest.model.TableListModel;
import org.apache.hadoop.hbase.rest.model.TestNamespacesInstanceModel;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RestTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ RestTests.class, MediumTests.class })
public class TestNamespacesInstanceResource {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestNamespacesInstanceResource.class);

    private static String NAMESPACE1 = "TestNamespacesInstanceResource1";

    private static Map<String, String> NAMESPACE1_PROPS = new HashMap<>();

    private static String NAMESPACE2 = "TestNamespacesInstanceResource2";

    private static Map<String, String> NAMESPACE2_PROPS = new HashMap<>();

    private static String NAMESPACE3 = "TestNamespacesInstanceResource3";

    private static Map<String, String> NAMESPACE3_PROPS = new HashMap<>();

    private static String NAMESPACE4 = "TestNamespacesInstanceResource4";

    private static Map<String, String> NAMESPACE4_PROPS = new HashMap<>();

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final HBaseRESTTestingUtility REST_TEST_UTIL = new HBaseRESTTestingUtility();

    private static Client client;

    private static JAXBContext context;

    private static Configuration conf;

    private static TestNamespacesInstanceModel testNamespacesInstanceModel;

    protected static ObjectMapper jsonMapper;

    @Test
    public void testCannotDeleteDefaultAndHbaseNamespaces() throws IOException {
        String defaultPath = "/namespaces/default";
        String hbasePath = "/namespaces/hbase";
        Response response;
        // Check that doesn't exist via non-REST call.
        Admin admin = TestNamespacesInstanceResource.TEST_UTIL.getAdmin();
        Assert.assertNotNull(findNamespace(admin, "default"));
        Assert.assertNotNull(findNamespace(admin, "hbase"));
        // Try (but fail) to delete namespaces via REST.
        response = TestNamespacesInstanceResource.client.delete(defaultPath);
        Assert.assertEquals(503, response.getCode());
        response = TestNamespacesInstanceResource.client.delete(hbasePath);
        Assert.assertEquals(503, response.getCode());
        Assert.assertNotNull(findNamespace(admin, "default"));
        Assert.assertNotNull(findNamespace(admin, "hbase"));
    }

    @Test
    public void testGetNamespaceTablesAndCannotDeleteNamespace() throws IOException, JAXBException {
        Admin admin = TestNamespacesInstanceResource.TEST_UTIL.getAdmin();
        String nsName = "TestNamespacesInstanceResource5";
        Response response;
        // Create namespace via admin.
        NamespaceDescriptor.Builder nsBuilder = NamespaceDescriptor.create(nsName);
        NamespaceDescriptor nsd = nsBuilder.build();
        nsd.setConfiguration("key1", "value1");
        admin.createNamespace(nsd);
        // Create two tables via admin.
        HColumnDescriptor colDesc = new HColumnDescriptor("cf1");
        TableName tn1 = TableName.valueOf((nsName + ":table1"));
        HTableDescriptor table = new HTableDescriptor(tn1);
        table.addFamily(colDesc);
        admin.createTable(table);
        TableName tn2 = TableName.valueOf((nsName + ":table2"));
        table = new HTableDescriptor(tn2);
        table.addFamily(colDesc);
        admin.createTable(table);
        Map<String, String> nsProperties = new HashMap<>();
        nsProperties.put("key1", "value1");
        List<String> nsTables = Arrays.asList("table1", "table2");
        // Check get namespace properties as XML, JSON and Protobuf.
        String namespacePath = "/namespaces/" + nsName;
        response = TestNamespacesInstanceResource.client.get(namespacePath);
        Assert.assertEquals(200, response.getCode());
        response = TestNamespacesInstanceResource.client.get(namespacePath, MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        NamespacesInstanceModel model = TestNamespacesInstanceResource.fromXML(response.getBody());
        checkNamespaceProperties(model.getProperties(), nsProperties);
        response = TestNamespacesInstanceResource.client.get(namespacePath, MIMETYPE_JSON);
        Assert.assertEquals(200, response.getCode());
        model = TestNamespacesInstanceResource.jsonMapper.readValue(response.getBody(), NamespacesInstanceModel.class);
        checkNamespaceProperties(model.getProperties(), nsProperties);
        response = TestNamespacesInstanceResource.client.get(namespacePath, MIMETYPE_PROTOBUF);
        Assert.assertEquals(200, response.getCode());
        model.getObjectFromMessage(response.getBody());
        checkNamespaceProperties(model.getProperties(), nsProperties);
        // Check get namespace tables as XML, JSON and Protobuf.
        namespacePath = ("/namespaces/" + nsName) + "/tables";
        response = TestNamespacesInstanceResource.client.get(namespacePath);
        Assert.assertEquals(200, response.getCode());
        response = TestNamespacesInstanceResource.client.get(namespacePath, MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        TableListModel tablemodel = TestNamespacesInstanceResource.fromXML(response.getBody());
        checkNamespaceTables(tablemodel.getTables(), nsTables);
        response = TestNamespacesInstanceResource.client.get(namespacePath, MIMETYPE_JSON);
        Assert.assertEquals(200, response.getCode());
        tablemodel = TestNamespacesInstanceResource.jsonMapper.readValue(response.getBody(), TableListModel.class);
        checkNamespaceTables(tablemodel.getTables(), nsTables);
        response = TestNamespacesInstanceResource.client.get(namespacePath, MIMETYPE_PROTOBUF);
        Assert.assertEquals(200, response.getCode());
        tablemodel.setTables(new ArrayList());
        tablemodel.getObjectFromMessage(response.getBody());
        checkNamespaceTables(tablemodel.getTables(), nsTables);
        // Check cannot delete namespace via REST because it contains tables.
        response = TestNamespacesInstanceResource.client.delete(namespacePath);
        namespacePath = "/namespaces/" + nsName;
        Assert.assertEquals(503, response.getCode());
    }

    @Test
    public void testNamespaceCreateAndDeleteXMLAndJSON() throws IOException, JAXBException {
        String namespacePath1 = "/namespaces/" + (TestNamespacesInstanceResource.NAMESPACE1);
        String namespacePath2 = "/namespaces/" + (TestNamespacesInstanceResource.NAMESPACE2);
        NamespacesInstanceModel model1;
        NamespacesInstanceModel model2;
        Response response;
        // Check that namespaces don't exist via non-REST call.
        Admin admin = TestNamespacesInstanceResource.TEST_UTIL.getAdmin();
        Assert.assertNull(findNamespace(admin, TestNamespacesInstanceResource.NAMESPACE1));
        Assert.assertNull(findNamespace(admin, TestNamespacesInstanceResource.NAMESPACE2));
        model1 = TestNamespacesInstanceResource.testNamespacesInstanceModel.buildTestModel(TestNamespacesInstanceResource.NAMESPACE1, TestNamespacesInstanceResource.NAMESPACE1_PROPS);
        TestNamespacesInstanceResource.testNamespacesInstanceModel.checkModel(model1, TestNamespacesInstanceResource.NAMESPACE1, TestNamespacesInstanceResource.NAMESPACE1_PROPS);
        model2 = TestNamespacesInstanceResource.testNamespacesInstanceModel.buildTestModel(TestNamespacesInstanceResource.NAMESPACE2, TestNamespacesInstanceResource.NAMESPACE2_PROPS);
        TestNamespacesInstanceResource.testNamespacesInstanceModel.checkModel(model2, TestNamespacesInstanceResource.NAMESPACE2, TestNamespacesInstanceResource.NAMESPACE2_PROPS);
        // Test cannot PUT (alter) non-existent namespace.
        response = TestNamespacesInstanceResource.client.put(namespacePath1, MIMETYPE_XML, TestNamespacesInstanceResource.toXML(model1));
        Assert.assertEquals(403, response.getCode());
        String jsonString = TestNamespacesInstanceResource.jsonMapper.writeValueAsString(model2);
        response = TestNamespacesInstanceResource.client.put(namespacePath2, MIMETYPE_JSON, Bytes.toBytes(jsonString));
        Assert.assertEquals(403, response.getCode());
        // Test cannot create tables when in read only mode.
        TestNamespacesInstanceResource.conf.set("hbase.rest.readonly", "true");
        response = TestNamespacesInstanceResource.client.post(namespacePath1, MIMETYPE_XML, TestNamespacesInstanceResource.toXML(model1));
        Assert.assertEquals(403, response.getCode());
        jsonString = TestNamespacesInstanceResource.jsonMapper.writeValueAsString(model2);
        response = TestNamespacesInstanceResource.client.post(namespacePath2, MIMETYPE_JSON, Bytes.toBytes(jsonString));
        Assert.assertEquals(403, response.getCode());
        NamespaceDescriptor nd1 = findNamespace(admin, TestNamespacesInstanceResource.NAMESPACE1);
        NamespaceDescriptor nd2 = findNamespace(admin, TestNamespacesInstanceResource.NAMESPACE2);
        Assert.assertNull(nd1);
        Assert.assertNull(nd2);
        TestNamespacesInstanceResource.conf.set("hbase.rest.readonly", "false");
        // Create namespace via XML and JSON.
        response = TestNamespacesInstanceResource.client.post(namespacePath1, MIMETYPE_XML, TestNamespacesInstanceResource.toXML(model1));
        Assert.assertEquals(201, response.getCode());
        jsonString = TestNamespacesInstanceResource.jsonMapper.writeValueAsString(model2);
        response = TestNamespacesInstanceResource.client.post(namespacePath2, MIMETYPE_JSON, Bytes.toBytes(jsonString));
        Assert.assertEquals(201, response.getCode());
        // Check that created namespaces correctly.
        nd1 = findNamespace(admin, TestNamespacesInstanceResource.NAMESPACE1);
        nd2 = findNamespace(admin, TestNamespacesInstanceResource.NAMESPACE2);
        Assert.assertNotNull(nd1);
        Assert.assertNotNull(nd2);
        checkNamespaceProperties(nd1, TestNamespacesInstanceResource.NAMESPACE1_PROPS);
        checkNamespaceProperties(nd1, TestNamespacesInstanceResource.NAMESPACE1_PROPS);
        // Test cannot delete tables when in read only mode.
        TestNamespacesInstanceResource.conf.set("hbase.rest.readonly", "true");
        response = TestNamespacesInstanceResource.client.delete(namespacePath1);
        Assert.assertEquals(403, response.getCode());
        response = TestNamespacesInstanceResource.client.delete(namespacePath2);
        Assert.assertEquals(403, response.getCode());
        nd1 = findNamespace(admin, TestNamespacesInstanceResource.NAMESPACE1);
        nd2 = findNamespace(admin, TestNamespacesInstanceResource.NAMESPACE2);
        Assert.assertNotNull(nd1);
        Assert.assertNotNull(nd2);
        TestNamespacesInstanceResource.conf.set("hbase.rest.readonly", "false");
        // Delete namespaces via XML and JSON.
        response = TestNamespacesInstanceResource.client.delete(namespacePath1);
        Assert.assertEquals(200, response.getCode());
        response = TestNamespacesInstanceResource.client.delete(namespacePath2);
        Assert.assertEquals(200, response.getCode());
        nd1 = findNamespace(admin, TestNamespacesInstanceResource.NAMESPACE1);
        nd2 = findNamespace(admin, TestNamespacesInstanceResource.NAMESPACE2);
        Assert.assertNull(nd1);
        Assert.assertNull(nd2);
    }

    @Test
    public void testNamespaceCreateAndDeletePBAndNoBody() throws IOException, JAXBException {
        String namespacePath3 = "/namespaces/" + (TestNamespacesInstanceResource.NAMESPACE3);
        String namespacePath4 = "/namespaces/" + (TestNamespacesInstanceResource.NAMESPACE4);
        NamespacesInstanceModel model3;
        NamespacesInstanceModel model4;
        Response response;
        // Check that namespaces don't exist via non-REST call.
        Admin admin = TestNamespacesInstanceResource.TEST_UTIL.getAdmin();
        Assert.assertNull(findNamespace(admin, TestNamespacesInstanceResource.NAMESPACE3));
        Assert.assertNull(findNamespace(admin, TestNamespacesInstanceResource.NAMESPACE4));
        model3 = TestNamespacesInstanceResource.testNamespacesInstanceModel.buildTestModel(TestNamespacesInstanceResource.NAMESPACE3, TestNamespacesInstanceResource.NAMESPACE3_PROPS);
        TestNamespacesInstanceResource.testNamespacesInstanceModel.checkModel(model3, TestNamespacesInstanceResource.NAMESPACE3, TestNamespacesInstanceResource.NAMESPACE3_PROPS);
        model4 = TestNamespacesInstanceResource.testNamespacesInstanceModel.buildTestModel(TestNamespacesInstanceResource.NAMESPACE4, TestNamespacesInstanceResource.NAMESPACE4_PROPS);
        TestNamespacesInstanceResource.testNamespacesInstanceModel.checkModel(model4, TestNamespacesInstanceResource.NAMESPACE4, TestNamespacesInstanceResource.NAMESPACE4_PROPS);
        // Test cannot PUT (alter) non-existent namespace.
        response = TestNamespacesInstanceResource.client.put(namespacePath3, MIMETYPE_BINARY, new byte[]{  });
        Assert.assertEquals(403, response.getCode());
        response = TestNamespacesInstanceResource.client.put(namespacePath4, MIMETYPE_PROTOBUF, model4.createProtobufOutput());
        Assert.assertEquals(403, response.getCode());
        // Test cannot create tables when in read only mode.
        TestNamespacesInstanceResource.conf.set("hbase.rest.readonly", "true");
        response = TestNamespacesInstanceResource.client.post(namespacePath3, MIMETYPE_BINARY, new byte[]{  });
        Assert.assertEquals(403, response.getCode());
        response = TestNamespacesInstanceResource.client.put(namespacePath4, MIMETYPE_PROTOBUF, model4.createProtobufOutput());
        Assert.assertEquals(403, response.getCode());
        NamespaceDescriptor nd3 = findNamespace(admin, TestNamespacesInstanceResource.NAMESPACE3);
        NamespaceDescriptor nd4 = findNamespace(admin, TestNamespacesInstanceResource.NAMESPACE4);
        Assert.assertNull(nd3);
        Assert.assertNull(nd4);
        TestNamespacesInstanceResource.conf.set("hbase.rest.readonly", "false");
        // Create namespace via no body and protobuf.
        response = TestNamespacesInstanceResource.client.post(namespacePath3, MIMETYPE_BINARY, new byte[]{  });
        Assert.assertEquals(201, response.getCode());
        response = TestNamespacesInstanceResource.client.post(namespacePath4, MIMETYPE_PROTOBUF, model4.createProtobufOutput());
        Assert.assertEquals(201, response.getCode());
        // Check that created namespaces correctly.
        nd3 = findNamespace(admin, TestNamespacesInstanceResource.NAMESPACE3);
        nd4 = findNamespace(admin, TestNamespacesInstanceResource.NAMESPACE4);
        Assert.assertNotNull(nd3);
        Assert.assertNotNull(nd4);
        checkNamespaceProperties(nd3, new HashMap());
        checkNamespaceProperties(nd4, TestNamespacesInstanceResource.NAMESPACE4_PROPS);
        // Check cannot post tables that already exist.
        response = TestNamespacesInstanceResource.client.post(namespacePath3, MIMETYPE_BINARY, new byte[]{  });
        Assert.assertEquals(403, response.getCode());
        response = TestNamespacesInstanceResource.client.post(namespacePath4, MIMETYPE_PROTOBUF, model4.createProtobufOutput());
        Assert.assertEquals(403, response.getCode());
        // Check cannot post tables when in read only mode.
        TestNamespacesInstanceResource.conf.set("hbase.rest.readonly", "true");
        response = TestNamespacesInstanceResource.client.delete(namespacePath3);
        Assert.assertEquals(403, response.getCode());
        response = TestNamespacesInstanceResource.client.delete(namespacePath4);
        Assert.assertEquals(403, response.getCode());
        nd3 = findNamespace(admin, TestNamespacesInstanceResource.NAMESPACE3);
        nd4 = findNamespace(admin, TestNamespacesInstanceResource.NAMESPACE4);
        Assert.assertNotNull(nd3);
        Assert.assertNotNull(nd4);
        TestNamespacesInstanceResource.conf.set("hbase.rest.readonly", "false");
        // Delete namespaces via XML and JSON.
        response = TestNamespacesInstanceResource.client.delete(namespacePath3);
        Assert.assertEquals(200, response.getCode());
        response = TestNamespacesInstanceResource.client.delete(namespacePath4);
        Assert.assertEquals(200, response.getCode());
        nd3 = findNamespace(admin, TestNamespacesInstanceResource.NAMESPACE3);
        nd4 = findNamespace(admin, TestNamespacesInstanceResource.NAMESPACE4);
        Assert.assertNull(nd3);
        Assert.assertNull(nd4);
    }
}

