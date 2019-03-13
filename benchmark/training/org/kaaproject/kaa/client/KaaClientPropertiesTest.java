/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.client;


import KaaClientProperties.KAA_CLIENT_PROPERTIES_FILE;
import ServerType.BOOTSTRAP;
import TransportProtocolIdConstants.TCP_TRANSPORT_ID;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.client.channel.TransportConnectionInfo;
import org.kaaproject.kaa.client.channel.TransportProtocolId;
import org.kaaproject.kaa.client.util.CommonsBase64;

import static KaaClientProperties.FILE_SEPARATOR;


public class KaaClientPropertiesTest {
    @Test
    public void testGetBootstrapServers() throws Exception {
        System.setProperty(KAA_CLIENT_PROPERTIES_FILE, "client-test.properties");
        KaaClientProperties properties = new KaaClientProperties();
        properties.setBase64(CommonsBase64.getInstance());
        Map<TransportProtocolId, List<TransportConnectionInfo>> bootstraps = properties.getBootstrapServers();
        Assert.assertEquals(1, bootstraps.size());
        Assert.assertNotNull(bootstraps.get(TCP_TRANSPORT_ID));
        Assert.assertEquals(1, bootstraps.get(TCP_TRANSPORT_ID).size());
        TransportConnectionInfo serverInfo = bootstraps.get(TCP_TRANSPORT_ID).get(0);
        Assert.assertEquals(BOOTSTRAP, serverInfo.getServerType());
        Assert.assertEquals(1, serverInfo.getAccessPointId());
        Assert.assertEquals(TCP_TRANSPORT_ID, serverInfo.getTransportId());
    }

    @Test
    public void testGetSdkToken() throws IOException {
        System.setProperty(KAA_CLIENT_PROPERTIES_FILE, "client-test.properties");
        KaaClientProperties properties = new KaaClientProperties();
        Assert.assertEquals("O7D+oECY1jhs6qIK8LA0zdaykmQ=", properties.getSdkToken());
    }

    @Test
    public void testGetPollDelay() throws IOException {
        System.setProperty(KAA_CLIENT_PROPERTIES_FILE, "client-test.properties");
        KaaClientProperties properties = new KaaClientProperties();
        Assert.assertEquals(0, properties.getPollDelay().intValue());
    }

    @Test
    public void testGetPollPeriod() throws IOException {
        System.setProperty(KAA_CLIENT_PROPERTIES_FILE, "client-test.properties");
        KaaClientProperties properties = new KaaClientProperties();
        Assert.assertEquals(10, properties.getPollPeriod().intValue());
    }

    @Test
    public void testGetPollUnit() throws IOException {
        System.setProperty(KAA_CLIENT_PROPERTIES_FILE, "client-test.properties");
        KaaClientProperties properties = new KaaClientProperties();
        Assert.assertEquals(TimeUnit.SECONDS, properties.getPollUnit());
    }

    @Test
    public void testGetDefaultConfigData() throws IOException {
        System.setProperty(KAA_CLIENT_PROPERTIES_FILE, "client-test.properties");
        KaaClientProperties properties = new KaaClientProperties();
        Assert.assertEquals(null, properties.getDefaultConfigData());
    }

    @Test
    public void testGetDefaultConfigSchema() throws IOException {
        System.setProperty(KAA_CLIENT_PROPERTIES_FILE, "client-test.properties");
        KaaClientProperties properties = new KaaClientProperties();
        Assert.assertEquals(null, properties.getDefaultConfigSchema());
    }

    @Test
    public void testGetWorkingDirectory() throws IOException {
        System.setProperty(KAA_CLIENT_PROPERTIES_FILE, "client-test.properties");
        KaaClientProperties properties = new KaaClientProperties();
        Assert.assertEquals(("." + (FILE_SEPARATOR)), properties.getWorkingDirectory());
    }

    @Test
    public void testSetWorkingDirectory() throws IOException {
        String requestedWorkDir = "dir_";
        String fileSeparator = System.getProperty("file.separator");
        System.setProperty(KAA_CLIENT_PROPERTIES_FILE, "client-test.properties");
        KaaClientProperties properties = new KaaClientProperties();
        properties.setWorkingDirectory(requestedWorkDir);
        Assert.assertEquals((requestedWorkDir + fileSeparator), properties.getWorkingDirectory());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetBlankWorkingDirectory() throws IOException {
        String requestedWorkDir = "";
        System.setProperty(KAA_CLIENT_PROPERTIES_FILE, "client-test.properties");
        KaaClientProperties properties = new KaaClientProperties();
        properties.setWorkingDirectory(requestedWorkDir);
    }

    @Test
    public void testGetStateFileName() throws IOException {
        System.setProperty(KAA_CLIENT_PROPERTIES_FILE, "client-test.properties");
        KaaClientProperties properties = new KaaClientProperties();
        Assert.assertEquals("state.properties", properties.getStateFileName());
    }

    @Test
    public void testGetStateFileFullName() throws IOException {
        System.setProperty(KAA_CLIENT_PROPERTIES_FILE, "client-test.properties");
        KaaClientProperties properties = new KaaClientProperties();
        Assert.assertEquals(((properties.getWorkingDirectory()) + "state.properties"), properties.getStateFileFullName());
    }

    @Test
    public void testSetStateFileName() throws IOException {
        String requestedName = "test_state.properties";
        System.setProperty(KAA_CLIENT_PROPERTIES_FILE, "client-test.properties");
        KaaClientProperties properties = new KaaClientProperties();
        properties.setStateFileName(requestedName);
        Assert.assertEquals(requestedName, properties.getStateFileName());
        Assert.assertEquals(((properties.getWorkingDirectory()) + requestedName), properties.getStateFileFullName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetBlankStateFileName() throws IOException {
        String requestedName = "";
        System.setProperty(KAA_CLIENT_PROPERTIES_FILE, "client-test.properties");
        KaaClientProperties properties = new KaaClientProperties();
        properties.setStateFileName(requestedName);
    }

    @Test
    public void testGetPublicKeyFileName() throws IOException {
        System.setProperty(KAA_CLIENT_PROPERTIES_FILE, "client-test.properties");
        KaaClientProperties properties = new KaaClientProperties();
        Assert.assertEquals("key.public", properties.getPublicKeyFileName());
    }

    @Test
    public void testGetPublicKeyFileFullName() throws IOException {
        System.setProperty(KAA_CLIENT_PROPERTIES_FILE, "client-test.properties");
        KaaClientProperties properties = new KaaClientProperties();
        Assert.assertEquals(((properties.getWorkingDirectory()) + "key.public"), properties.getPublicKeyFileFullName());
    }

    @Test
    public void testSetPublicKeyFileName() throws IOException {
        String requestedName = "test_key.public";
        System.setProperty(KAA_CLIENT_PROPERTIES_FILE, "client-test.properties");
        KaaClientProperties properties = new KaaClientProperties();
        properties.setPublicKeyFileName(requestedName);
        Assert.assertEquals(requestedName, properties.getPublicKeyFileName());
        Assert.assertEquals(((properties.getWorkingDirectory()) + requestedName), properties.getPublicKeyFileFullName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetBlankPublicKeyFileName() throws IOException {
        String requestedName = "";
        System.setProperty(KAA_CLIENT_PROPERTIES_FILE, "client-test.properties");
        KaaClientProperties properties = new KaaClientProperties();
        properties.setPublicKeyFileName(requestedName);
    }

    @Test
    public void testGetPrivateKeyFileName() throws IOException {
        System.setProperty(KAA_CLIENT_PROPERTIES_FILE, "client-test.properties");
        KaaClientProperties properties = new KaaClientProperties();
        Assert.assertEquals("key.private", properties.getPrivateKeyFileName());
    }

    @Test
    public void testGetPrivateKeyFileFullName() throws IOException {
        System.setProperty(KAA_CLIENT_PROPERTIES_FILE, "client-test.properties");
        KaaClientProperties properties = new KaaClientProperties();
        Assert.assertEquals(((properties.getWorkingDirectory()) + "key.private"), properties.getPrivateKeyFileFullName());
    }

    @Test
    public void testSetPrivateKeyFileName() throws IOException {
        String requestedName = "test_key.private";
        System.setProperty(KAA_CLIENT_PROPERTIES_FILE, "client-test.properties");
        KaaClientProperties properties = new KaaClientProperties();
        properties.setPrivateKeyFileName(requestedName);
        Assert.assertEquals(requestedName, properties.getPrivateKeyFileName());
        Assert.assertEquals(((properties.getWorkingDirectory()) + requestedName), properties.getPrivateKeyFileFullName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetBlankPrivateKeyFileName() throws IOException {
        String requestedName = "";
        System.setProperty(KAA_CLIENT_PROPERTIES_FILE, "client-test.properties");
        KaaClientProperties properties = new KaaClientProperties();
        properties.setPrivateKeyFileName(requestedName);
    }
}

