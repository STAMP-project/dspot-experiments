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
import Constants.MIMETYPE_XML;
import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.rest.client.Client;
import org.apache.hadoop.hbase.rest.client.Response;
import org.apache.hadoop.hbase.rest.model.NamespacesModel;
import org.apache.hadoop.hbase.rest.model.TestNamespacesModel;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RestTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ RestTests.class, MediumTests.class })
public class TestNamespacesResource {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestNamespacesResource.class);

    private static String NAMESPACE1 = "TestNamespacesInstanceResource1";

    private static String NAMESPACE2 = "TestNamespacesInstanceResource2";

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final HBaseRESTTestingUtility REST_TEST_UTIL = new HBaseRESTTestingUtility();

    private static Client client;

    private static JAXBContext context;

    private static Configuration conf;

    private static TestNamespacesModel testNamespacesModel;

    @Test
    public void testNamespaceListXMLandJSON() throws IOException, JAXBException {
        String namespacePath = "/namespaces/";
        NamespacesModel model;
        Response response;
        // Check that namespace does not yet exist via non-REST call.
        Admin admin = TestNamespacesResource.TEST_UTIL.getAdmin();
        Assert.assertFalse(doesNamespaceExist(admin, TestNamespacesResource.NAMESPACE1));
        model = TestNamespacesResource.testNamespacesModel.buildTestModel();
        TestNamespacesResource.testNamespacesModel.checkModel(model);
        // Check that REST GET finds only default namespaces via XML and JSON responses.
        response = TestNamespacesResource.client.get(namespacePath, MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        model = TestNamespacesResource.fromXML(response.getBody());
        TestNamespacesResource.testNamespacesModel.checkModel(model, "hbase", "default");
        response = TestNamespacesResource.client.get(namespacePath, MIMETYPE_JSON);
        Assert.assertEquals(200, response.getCode());
        model = TestNamespacesResource.testNamespacesModel.fromJSON(Bytes.toString(response.getBody()));
        TestNamespacesResource.testNamespacesModel.checkModel(model, "hbase", "default");
        // Create namespace and check that REST GET finds one additional namespace.
        createNamespaceViaAdmin(admin, TestNamespacesResource.NAMESPACE1);
        response = TestNamespacesResource.client.get(namespacePath, MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        model = TestNamespacesResource.fromXML(response.getBody());
        TestNamespacesResource.testNamespacesModel.checkModel(model, TestNamespacesResource.NAMESPACE1, "hbase", "default");
        response = TestNamespacesResource.client.get(namespacePath, MIMETYPE_JSON);
        Assert.assertEquals(200, response.getCode());
        model = TestNamespacesResource.testNamespacesModel.fromJSON(Bytes.toString(response.getBody()));
        TestNamespacesResource.testNamespacesModel.checkModel(model, TestNamespacesResource.NAMESPACE1, "hbase", "default");
        // Create another namespace and check that REST GET finds one additional namespace.
        createNamespaceViaAdmin(admin, TestNamespacesResource.NAMESPACE2);
        response = TestNamespacesResource.client.get(namespacePath, MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        model = TestNamespacesResource.fromXML(response.getBody());
        TestNamespacesResource.testNamespacesModel.checkModel(model, TestNamespacesResource.NAMESPACE1, TestNamespacesResource.NAMESPACE2, "hbase", "default");
        response = TestNamespacesResource.client.get(namespacePath, MIMETYPE_JSON);
        Assert.assertEquals(200, response.getCode());
        model = TestNamespacesResource.testNamespacesModel.fromJSON(Bytes.toString(response.getBody()));
        TestNamespacesResource.testNamespacesModel.checkModel(model, TestNamespacesResource.NAMESPACE1, TestNamespacesResource.NAMESPACE2, "hbase", "default");
        // Delete namespace and check that REST still finds correct namespaces.
        admin.deleteNamespace(TestNamespacesResource.NAMESPACE1);
        response = TestNamespacesResource.client.get(namespacePath, MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        model = TestNamespacesResource.fromXML(response.getBody());
        TestNamespacesResource.testNamespacesModel.checkModel(model, TestNamespacesResource.NAMESPACE2, "hbase", "default");
        response = TestNamespacesResource.client.get(namespacePath, MIMETYPE_JSON);
        Assert.assertEquals(200, response.getCode());
        model = TestNamespacesResource.testNamespacesModel.fromJSON(Bytes.toString(response.getBody()));
        TestNamespacesResource.testNamespacesModel.checkModel(model, TestNamespacesResource.NAMESPACE2, "hbase", "default");
        admin.deleteNamespace(TestNamespacesResource.NAMESPACE2);
    }

    @Test
    public void testNamespaceListPBandDefault() throws IOException, JAXBException {
        String schemaPath = "/namespaces/";
        NamespacesModel model;
        Response response;
        // Check that namespace does not yet exist via non-REST call.
        Admin admin = TestNamespacesResource.TEST_UTIL.getAdmin();
        Assert.assertFalse(doesNamespaceExist(admin, TestNamespacesResource.NAMESPACE1));
        model = TestNamespacesResource.testNamespacesModel.buildTestModel();
        TestNamespacesResource.testNamespacesModel.checkModel(model);
        // Check that REST GET finds only default namespaces via PB and default Accept header.
        response = TestNamespacesResource.client.get(schemaPath, MIMETYPE_PROTOBUF);
        Assert.assertEquals(200, response.getCode());
        model.getObjectFromMessage(response.getBody());
        TestNamespacesResource.testNamespacesModel.checkModel(model, "hbase", "default");
        response = TestNamespacesResource.client.get(schemaPath);
        Assert.assertEquals(200, response.getCode());
        // Create namespace and check that REST GET finds one additional namespace.
        createNamespaceViaAdmin(admin, TestNamespacesResource.NAMESPACE1);
        response = TestNamespacesResource.client.get(schemaPath, MIMETYPE_PROTOBUF);
        Assert.assertEquals(200, response.getCode());
        model.getObjectFromMessage(response.getBody());
        TestNamespacesResource.testNamespacesModel.checkModel(model, TestNamespacesResource.NAMESPACE1, "hbase", "default");
        response = TestNamespacesResource.client.get(schemaPath);
        Assert.assertEquals(200, response.getCode());
        // Create another namespace and check that REST GET finds one additional namespace.
        createNamespaceViaAdmin(admin, TestNamespacesResource.NAMESPACE2);
        response = TestNamespacesResource.client.get(schemaPath, MIMETYPE_PROTOBUF);
        Assert.assertEquals(200, response.getCode());
        model.getObjectFromMessage(response.getBody());
        TestNamespacesResource.testNamespacesModel.checkModel(model, TestNamespacesResource.NAMESPACE1, TestNamespacesResource.NAMESPACE2, "hbase", "default");
        response = TestNamespacesResource.client.get(schemaPath);
        Assert.assertEquals(200, response.getCode());
        // Delete namespace and check that REST GET still finds correct namespaces.
        admin.deleteNamespace(TestNamespacesResource.NAMESPACE1);
        response = TestNamespacesResource.client.get(schemaPath, MIMETYPE_PROTOBUF);
        Assert.assertEquals(200, response.getCode());
        model.getObjectFromMessage(response.getBody());
        TestNamespacesResource.testNamespacesModel.checkModel(model, TestNamespacesResource.NAMESPACE2, "hbase", "default");
        response = TestNamespacesResource.client.get(schemaPath);
        Assert.assertEquals(200, response.getCode());
        admin.deleteNamespace(TestNamespacesResource.NAMESPACE2);
    }
}

