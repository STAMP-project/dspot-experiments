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


import Constants.MIMETYPE_PROTOBUF;
import Constants.MIMETYPE_PROTOBUF_IETF;
import Constants.MIMETYPE_XML;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.rest.client.Client;
import org.apache.hadoop.hbase.rest.client.Response;
import org.apache.hadoop.hbase.rest.model.StorageClusterStatusModel;
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
public class TestStatusResource {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestStatusResource.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestStatusResource.class);

    private static final byte[] META_REGION_NAME = Bytes.toBytes(((TableName.META_TABLE_NAME) + ",,1"));

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final HBaseRESTTestingUtility REST_TEST_UTIL = new HBaseRESTTestingUtility();

    private static Client client;

    private static JAXBContext context;

    private static Configuration conf;

    @Test
    public void testGetClusterStatusXML() throws IOException, JAXBException {
        Response response = TestStatusResource.client.get("/status/cluster", MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_XML, response.getHeader("content-type"));
        StorageClusterStatusModel model = ((StorageClusterStatusModel) (TestStatusResource.context.createUnmarshaller().unmarshal(new ByteArrayInputStream(response.getBody()))));
        TestStatusResource.validate(model);
    }

    @Test
    public void testGetClusterStatusPB() throws IOException {
        Response response = TestStatusResource.client.get("/status/cluster", MIMETYPE_PROTOBUF);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_PROTOBUF, response.getHeader("content-type"));
        StorageClusterStatusModel model = new StorageClusterStatusModel();
        model.getObjectFromMessage(response.getBody());
        TestStatusResource.validate(model);
        response = TestStatusResource.client.get("/status/cluster", MIMETYPE_PROTOBUF_IETF);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_PROTOBUF_IETF, response.getHeader("content-type"));
        model = new StorageClusterStatusModel();
        model.getObjectFromMessage(response.getBody());
        TestStatusResource.validate(model);
    }
}

