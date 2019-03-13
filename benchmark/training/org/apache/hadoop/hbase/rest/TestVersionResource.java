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
import MediaType.APPLICATION_JSON_TYPE;
import RESTServlet.VERSION_STRING;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.rest.client.Client;
import org.apache.hadoop.hbase.rest.client.Response;
import org.apache.hadoop.hbase.rest.model.StorageClusterVersionModel;
import org.apache.hadoop.hbase.rest.model.VersionModel;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RestTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ RestTests.class, MediumTests.class })
public class TestVersionResource {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestVersionResource.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestVersionResource.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final HBaseRESTTestingUtility REST_TEST_UTIL = new HBaseRESTTestingUtility();

    private static Client client;

    private static JAXBContext context;

    @Test
    public void testGetStargateVersionText() throws IOException {
        Response response = TestVersionResource.client.get("/version", MIMETYPE_TEXT);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_TEXT, response.getHeader("content-type"));
        String body = Bytes.toString(response.getBody());
        Assert.assertTrue(((body.length()) > 0));
        Assert.assertTrue(body.contains(VERSION_STRING));
        Assert.assertTrue(body.contains(System.getProperty("java.vm.vendor")));
        Assert.assertTrue(body.contains(System.getProperty("java.version")));
        Assert.assertTrue(body.contains(System.getProperty("java.vm.version")));
        Assert.assertTrue(body.contains(System.getProperty("os.name")));
        Assert.assertTrue(body.contains(System.getProperty("os.version")));
        Assert.assertTrue(body.contains(System.getProperty("os.arch")));
        // TODO: fix when we actually get a jersey version
        // assertTrue(body.contains(ServletContainer.class.getPackage().getImplementationVersion()));
    }

    @Test
    public void testGetStargateVersionXML() throws IOException, JAXBException {
        Response response = TestVersionResource.client.get("/version", MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_XML, response.getHeader("content-type"));
        VersionModel model = ((VersionModel) (TestVersionResource.context.createUnmarshaller().unmarshal(new ByteArrayInputStream(response.getBody()))));
        TestVersionResource.validate(model);
        TestVersionResource.LOG.info("success retrieving Stargate version as XML");
    }

    @Test
    public void testGetStargateVersionJSON() throws IOException {
        Response response = TestVersionResource.client.get("/version", MIMETYPE_JSON);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_JSON, response.getHeader("content-type"));
        ObjectMapper mapper = new JacksonJaxbJsonProvider().locateMapper(VersionModel.class, APPLICATION_JSON_TYPE);
        VersionModel model = mapper.readValue(response.getBody(), VersionModel.class);
        TestVersionResource.validate(model);
        TestVersionResource.LOG.info("success retrieving Stargate version as JSON");
    }

    @Test
    public void testGetStargateVersionPB() throws IOException {
        Response response = TestVersionResource.client.get("/version", MIMETYPE_PROTOBUF);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_PROTOBUF, response.getHeader("content-type"));
        VersionModel model = new VersionModel();
        model.getObjectFromMessage(response.getBody());
        TestVersionResource.validate(model);
        response = TestVersionResource.client.get("/version", MIMETYPE_PROTOBUF_IETF);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_PROTOBUF_IETF, response.getHeader("content-type"));
        model = new VersionModel();
        model.getObjectFromMessage(response.getBody());
        TestVersionResource.validate(model);
    }

    @Test
    public void testGetStorageClusterVersionText() throws IOException {
        Response response = TestVersionResource.client.get("/version/cluster", MIMETYPE_TEXT);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_TEXT, response.getHeader("content-type"));
    }

    @Test
    public void testGetStorageClusterVersionXML() throws IOException, JAXBException {
        Response response = TestVersionResource.client.get("/version/cluster", MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_XML, response.getHeader("content-type"));
        StorageClusterVersionModel clusterVersionModel = ((StorageClusterVersionModel) (TestVersionResource.context.createUnmarshaller().unmarshal(new ByteArrayInputStream(response.getBody()))));
        Assert.assertNotNull(clusterVersionModel);
        Assert.assertNotNull(clusterVersionModel.getVersion());
        TestVersionResource.LOG.info("success retrieving storage cluster version as XML");
    }

    @Test
    public void testGetStorageClusterVersionJSON() throws IOException {
        Response response = TestVersionResource.client.get("/version/cluster", MIMETYPE_JSON);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_JSON, response.getHeader("content-type"));
        ObjectMapper mapper = new JacksonJaxbJsonProvider().locateMapper(StorageClusterVersionModel.class, APPLICATION_JSON_TYPE);
        StorageClusterVersionModel clusterVersionModel = mapper.readValue(response.getBody(), StorageClusterVersionModel.class);
        Assert.assertNotNull(clusterVersionModel);
        Assert.assertNotNull(clusterVersionModel.getVersion());
        TestVersionResource.LOG.info("success retrieving storage cluster version as JSON");
    }
}

