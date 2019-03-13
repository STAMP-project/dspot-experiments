/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.metadata.store.zookeeper;


import com.google.gson.Gson;
import org.apache.curator.test.TestingServer;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.metadata.definition.model.FullServiceDefinition;
import org.apache.dubbo.metadata.identifier.MetadataIdentifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * 2018/10/9
 */
public class ZookeeperMetadataReportTest {
    private TestingServer zkServer;

    private ZookeeperMetadataReport zookeeperMetadataReport;

    private URL registryUrl;

    private ZookeeperMetadataReportFactory zookeeperMetadataReportFactory;

    @Test
    public void testStoreProvider() throws ClassNotFoundException, InterruptedException {
        String interfaceName = "org.apache.dubbo.metadata.store.zookeeper.ZookeeperMetadataReport4TstService";
        String version = "1.0.0.zk.md";
        String group = null;
        String application = "vic.zk.md";
        MetadataIdentifier providerMetadataIdentifier = storePrivider(zookeeperMetadataReport, interfaceName, version, group, application);
        String fileContent = zookeeperMetadataReport.zkClient.getContent(zookeeperMetadataReport.getNodePath(providerMetadataIdentifier));
        fileContent = waitSeconds(fileContent, 3500, zookeeperMetadataReport.getNodePath(providerMetadataIdentifier));
        Assertions.assertNotNull(fileContent);
        deletePath(providerMetadataIdentifier, zookeeperMetadataReport);
        fileContent = zookeeperMetadataReport.zkClient.getContent(zookeeperMetadataReport.getNodePath(providerMetadataIdentifier));
        fileContent = waitSeconds(fileContent, 1000, zookeeperMetadataReport.getNodePath(providerMetadataIdentifier));
        Assertions.assertNull(fileContent);
        providerMetadataIdentifier = storePrivider(zookeeperMetadataReport, interfaceName, version, group, application);
        fileContent = zookeeperMetadataReport.zkClient.getContent(zookeeperMetadataReport.getNodePath(providerMetadataIdentifier));
        fileContent = waitSeconds(fileContent, 3500, zookeeperMetadataReport.getNodePath(providerMetadataIdentifier));
        Assertions.assertNotNull(fileContent);
        Gson gson = new Gson();
        FullServiceDefinition fullServiceDefinition = gson.fromJson(fileContent, FullServiceDefinition.class);
        Assertions.assertEquals(fullServiceDefinition.getParameters().get("paramTest"), "zkTest");
    }

    @Test
    public void testConsumer() throws ClassNotFoundException, InterruptedException {
        String interfaceName = "org.apache.dubbo.metadata.store.zookeeper.ZookeeperMetadataReport4TstService";
        String version = "1.0.0.zk.md";
        String group = null;
        String application = "vic.zk.md";
        MetadataIdentifier consumerMetadataIdentifier = storeConsumer(zookeeperMetadataReport, interfaceName, version, group, application);
        String fileContent = zookeeperMetadataReport.zkClient.getContent(zookeeperMetadataReport.getNodePath(consumerMetadataIdentifier));
        fileContent = waitSeconds(fileContent, 3500, zookeeperMetadataReport.getNodePath(consumerMetadataIdentifier));
        Assertions.assertNotNull(fileContent);
        deletePath(consumerMetadataIdentifier, zookeeperMetadataReport);
        fileContent = zookeeperMetadataReport.zkClient.getContent(zookeeperMetadataReport.getNodePath(consumerMetadataIdentifier));
        fileContent = waitSeconds(fileContent, 1000, zookeeperMetadataReport.getNodePath(consumerMetadataIdentifier));
        Assertions.assertNull(fileContent);
        consumerMetadataIdentifier = storeConsumer(zookeeperMetadataReport, interfaceName, version, group, application);
        fileContent = zookeeperMetadataReport.zkClient.getContent(zookeeperMetadataReport.getNodePath(consumerMetadataIdentifier));
        fileContent = waitSeconds(fileContent, 3000, zookeeperMetadataReport.getNodePath(consumerMetadataIdentifier));
        Assertions.assertNotNull(fileContent);
        Assertions.assertEquals(fileContent, "{\"paramConsumerTest\":\"zkCm\"}");
    }
}

