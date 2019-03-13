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
package org.apache.zookeeper.client;


import ZKClientConfig.ENABLE_CLIENT_SASL_DEFAULT;
import ZKClientConfig.ENABLE_CLIENT_SASL_KEY;
import ZKConfig.JUTE_MAXBUFFER;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig.ConfigException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static ZKClientConfig.CLIENT_MAX_PACKET_LENGTH_DEFAULT;


public class ZKClientConfigTest {
    private static final File testData = new File(System.getProperty("test.data.dir", "src/test/resources/data"));

    @Rule
    public Timeout timeout = new Timeout(10, TimeUnit.SECONDS);

    @Test
    public void testDefaultConfiguration() {
        Map<String, String> properties = new HashMap<>();
        properties.put(ZKClientConfig.ZK_SASL_CLIENT_USERNAME, "zookeeper1");
        properties.put(ZKClientConfig.LOGIN_CONTEXT_NAME_KEY, "Client1");
        properties.put(ZKClientConfig.ENABLE_CLIENT_SASL_KEY, "true");
        properties.put(ZKClientConfig.ZOOKEEPER_SERVER_REALM, "zookeeper/hadoop.hadoop.com");
        properties.put(ZKClientConfig.DISABLE_AUTO_WATCH_RESET, "true");
        properties.put(ZKClientConfig.ZOOKEEPER_CLIENT_CNXN_SOCKET, "ClientCnxnSocketNetty");
        properties.put(ZKClientConfig.SECURE_CLIENT, "true");
        for (Map.Entry<String, String> e : properties.entrySet()) {
            System.setProperty(e.getKey(), e.getValue());
        }
        /**
         * ZKClientConfig should get initialized with system properties
         */
        ZKClientConfig conf = new ZKClientConfig();
        for (Map.Entry<String, String> e : properties.entrySet()) {
            Assert.assertEquals(e.getValue(), conf.getProperty(e.getKey()));
        }
        /**
         * clear properties
         */
        for (Map.Entry<String, String> e : properties.entrySet()) {
            System.clearProperty(e.getKey());
        }
        conf = new ZKClientConfig();
        /**
         * test that all the properties are null
         */
        for (Map.Entry<String, String> e : properties.entrySet()) {
            String result = conf.getProperty(e.getKey());
            Assert.assertNull(result);
        }
    }

    @Test
    public void testSystemPropertyValue() {
        String clientName = "zookeeper1";
        System.setProperty(ZKClientConfig.ZK_SASL_CLIENT_USERNAME, clientName);
        ZKClientConfig conf = new ZKClientConfig();
        Assert.assertEquals(conf.getProperty(ZKClientConfig.ZK_SASL_CLIENT_USERNAME), clientName);
        String newClientName = "zookeeper2";
        conf.setProperty(ZKClientConfig.ZK_SASL_CLIENT_USERNAME, newClientName);
        Assert.assertEquals(conf.getProperty(ZKClientConfig.ZK_SASL_CLIENT_USERNAME), newClientName);
    }

    @Test
    public void testReadConfigurationFile() throws IOException, ConfigException {
        File file = File.createTempFile("clientConfig", ".conf", ZKClientConfigTest.testData);
        file.deleteOnExit();
        Properties clientConfProp = new Properties();
        clientConfProp.setProperty(ZKClientConfig.ENABLE_CLIENT_SASL_KEY, "true");
        clientConfProp.setProperty(ZKClientConfig.ZK_SASL_CLIENT_USERNAME, "ZK");
        clientConfProp.setProperty(ZKClientConfig.LOGIN_CONTEXT_NAME_KEY, "MyClient");
        clientConfProp.setProperty(ZKClientConfig.ZOOKEEPER_SERVER_REALM, "HADOOP.COM");
        clientConfProp.setProperty("dummyProperty", "dummyValue");
        OutputStream io = new FileOutputStream(file);
        try {
            clientConfProp.store(io, "Client Configurations");
        } finally {
            io.close();
        }
        ZKClientConfig conf = new ZKClientConfig();
        conf.addConfiguration(file.getAbsolutePath());
        Assert.assertEquals(conf.getProperty(ZKClientConfig.ENABLE_CLIENT_SASL_KEY), "true");
        Assert.assertEquals(conf.getProperty(ZKClientConfig.ZK_SASL_CLIENT_USERNAME), "ZK");
        Assert.assertEquals(conf.getProperty(ZKClientConfig.LOGIN_CONTEXT_NAME_KEY), "MyClient");
        Assert.assertEquals(conf.getProperty(ZKClientConfig.ZOOKEEPER_SERVER_REALM), "HADOOP.COM");
        Assert.assertEquals(conf.getProperty("dummyProperty"), "dummyValue");
        // try to delete it now as we have done with the created file, why to
        // wait for deleteOnExit() deletion
        file.delete();
    }

    @Test
    public void testSetConfiguration() {
        ZKClientConfig conf = new ZKClientConfig();
        String defaultValue = conf.getProperty(ENABLE_CLIENT_SASL_KEY, ENABLE_CLIENT_SASL_DEFAULT);
        if (defaultValue.equals("true")) {
            conf.setProperty(ZKClientConfig.ENABLE_CLIENT_SASL_KEY, "false");
        } else {
            conf.setProperty(ZKClientConfig.ENABLE_CLIENT_SASL_KEY, "true");
        }
        Assert.assertTrue(((conf.getProperty(ZKClientConfig.ENABLE_CLIENT_SASL_KEY)) != defaultValue));
    }

    @Test
    public void testIntegerRetrievalFromProperty() {
        ZKClientConfig conf = new ZKClientConfig();
        String prop = "UnSetProperty" + (System.currentTimeMillis());
        int defaultValue = 100;
        // property is not set we should get the default value
        int result = conf.getInt(prop, defaultValue);
        Assert.assertEquals(defaultValue, result);
        // property is set but can not be parsed to int, we should get the
        // NumberFormatException
        conf.setProperty(JUTE_MAXBUFFER, "InvlaidIntValue123");
        try {
            result = conf.getInt(JUTE_MAXBUFFER, defaultValue);
            Assert.fail("NumberFormatException is expected");
        } catch (NumberFormatException exception) {
            // do nothing
        }
        Assert.assertEquals(defaultValue, result);
        // property is set to an valid int, we should get the set value
        int value = CLIENT_MAX_PACKET_LENGTH_DEFAULT;
        conf.setProperty(JUTE_MAXBUFFER, Integer.toString(value));
        result = conf.getInt(JUTE_MAXBUFFER, defaultValue);
        Assert.assertEquals(value, result);
        // property is set but with white spaces
        value = 12345;
        conf.setProperty(JUTE_MAXBUFFER, ((" " + (Integer.toString(value))) + " "));
        result = conf.getInt(JUTE_MAXBUFFER, defaultValue);
        Assert.assertEquals(value, result);
    }
}

