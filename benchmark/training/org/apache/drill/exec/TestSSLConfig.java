/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec;


import ExecConstants.HTTP_KEYSTORE_PASSWORD;
import ExecConstants.HTTP_KEYSTORE_PATH;
import ExecConstants.HTTP_TRUSTSTORE_PASSWORD;
import ExecConstants.HTTP_TRUSTSTORE_PATH;
import ExecConstants.SSL_USE_HADOOP_CONF;
import ExecConstants.USER_SSL_ENABLED;
import SSLConfig.Mode.SERVER;
import java.text.MessageFormat;
import junit.framework.TestCase;
import org.apache.drill.categories.SecurityTest;
import org.apache.drill.common.exceptions.DrillException;
import org.apache.drill.exec.ssl.SSLConfig;
import org.apache.drill.exec.ssl.SSLConfigBuilder;
import org.apache.drill.test.ConfigBuilder;
import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(SecurityTest.class)
public class TestSSLConfig {
    @Test
    public void testMissingKeystorePath() throws Exception {
        ConfigBuilder config = new ConfigBuilder();
        config.put(HTTP_KEYSTORE_PATH, "");
        config.put(HTTP_KEYSTORE_PASSWORD, "root");
        config.put(SSL_USE_HADOOP_CONF, false);
        config.put(USER_SSL_ENABLED, true);
        try {
            SSLConfig sslv = new SSLConfigBuilder().config(config.build()).mode(SERVER).initializeSSLContext(false).validateKeyStore(true).build();
            TestCase.fail();
            // Expected
        } catch (Exception e) {
            Assert.assertTrue((e instanceof DrillException));
        }
    }

    @Test
    public void testMissingKeystorePassword() throws Exception {
        ConfigBuilder config = new ConfigBuilder();
        config.put(HTTP_KEYSTORE_PATH, "/root");
        config.put(HTTP_KEYSTORE_PASSWORD, "");
        config.put(SSL_USE_HADOOP_CONF, false);
        config.put(USER_SSL_ENABLED, true);
        try {
            SSLConfig sslv = new SSLConfigBuilder().config(config.build()).mode(SERVER).initializeSSLContext(false).validateKeyStore(true).build();
            TestCase.fail();
            // Expected
        } catch (Exception e) {
            Assert.assertTrue((e instanceof DrillException));
        }
    }

    @Test
    public void testForKeystoreConfig() throws Exception {
        ConfigBuilder config = new ConfigBuilder();
        config.put(HTTP_KEYSTORE_PATH, "/root");
        config.put(HTTP_KEYSTORE_PASSWORD, "root");
        try {
            SSLConfig sslv = new SSLConfigBuilder().config(config.build()).mode(SERVER).initializeSSLContext(false).validateKeyStore(true).build();
            Assert.assertEquals("/root", sslv.getKeyStorePath());
            Assert.assertEquals("root", sslv.getKeyStorePassword());
        } catch (Exception e) {
            TestCase.fail();
        }
    }

    @Test
    public void testForBackwardCompatability() throws Exception {
        ConfigBuilder config = new ConfigBuilder();
        config.put("javax.net.ssl.keyStore", "/root");
        config.put("javax.net.ssl.keyStorePassword", "root");
        SSLConfig sslv = new SSLConfigBuilder().config(config.build()).mode(SERVER).initializeSSLContext(false).validateKeyStore(true).build();
        Assert.assertEquals("/root", sslv.getKeyStorePath());
        Assert.assertEquals("root", sslv.getKeyStorePassword());
    }

    @Test
    public void testForTrustStore() throws Exception {
        ConfigBuilder config = new ConfigBuilder();
        config.put(HTTP_TRUSTSTORE_PATH, "/root");
        config.put(HTTP_TRUSTSTORE_PASSWORD, "root");
        config.put(SSL_USE_HADOOP_CONF, false);
        SSLConfig sslv = new SSLConfigBuilder().config(config.build()).mode(SERVER).initializeSSLContext(false).validateKeyStore(true).build();
        Assert.assertEquals(true, sslv.hasTrustStorePath());
        Assert.assertEquals(true, sslv.hasTrustStorePassword());
        Assert.assertEquals("/root", sslv.getTrustStorePath());
        Assert.assertEquals("root", sslv.getTrustStorePassword());
    }

    @Test
    public void testInvalidHadoopKeystore() throws Exception {
        Configuration hadoopConfig = new Configuration();
        String hadoopSSLFileProp = MessageFormat.format(HADOOP_SSL_CONF_TPL_KEY, SERVER.toString().toLowerCase());
        hadoopConfig.set(hadoopSSLFileProp, "ssl-server-invalid.xml");
        ConfigBuilder config = new ConfigBuilder();
        config.put(USER_SSL_ENABLED, true);
        config.put(SSL_USE_HADOOP_CONF, true);
        SSLConfig sslv;
        try {
            sslv = new SSLConfigBuilder().config(config.build()).mode(SERVER).initializeSSLContext(false).validateKeyStore(true).hadoopConfig(hadoopConfig).build();
            TestCase.fail();
        } catch (Exception e) {
            Assert.assertTrue((e instanceof DrillException));
        }
    }
}

