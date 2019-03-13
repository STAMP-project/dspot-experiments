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
package org.apache.hive.jdbc;


import ConfVars.HIVE_METASTORE_SSL_KEYSTORE_PATH;
import ConfVars.HIVE_SERVER2_SSL_KEYSTORE_PATH.varname;
import SSLTestUtils.SSL_CONN_PARAMS;
import java.io.File;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.hadoop.hive.jdbc.SSLTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestSSL {
    private static final Logger LOG = LoggerFactory.getLogger(TestSSL.class);

    private static final String LOCALHOST_KEY_STORE_NAME = "keystore.jks";

    private static final String EXAMPLEDOTCOM_KEY_STORE_NAME = "keystore_exampledotcom.jks";

    private static final String TRUST_STORE_NAME = "truststore.jks";

    private static final String KEY_STORE_TRUST_STORE_PASSWORD = "HiveJdbc";

    private static final String JAVA_TRUST_STORE_PROP = "javax.net.ssl.trustStore";

    private static final String JAVA_TRUST_STORE_PASS_PROP = "javax.net.ssl.trustStorePassword";

    private MiniHS2 miniHS2 = null;

    private static HiveConf conf = new HiveConf();

    private Connection hs2Conn = null;

    private String dataFileDir = SSLTestUtils.getDataFileDir();

    private Map<String, String> confOverlay;

    /**
     * *
     * Test SSL client with non-SSL server fails
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testInvalidConfig() throws Exception {
        SSLTestUtils.clearSslConfOverlay(confOverlay);
        // Test in binary mode
        SSLTestUtils.setBinaryConfOverlay(confOverlay);
        miniHS2.start(confOverlay);
        DriverManager.setLoginTimeout(4);
        try {
            hs2Conn = DriverManager.getConnection(miniHS2.getJdbcURL("default", SSL_CONN_PARAMS), System.getProperty("user.name"), "bar");
            Assert.fail("SSL connection should fail with NON-SSL server");
        } catch (SQLException e) {
            // expected error
            Assert.assertEquals("08S01", e.getSQLState().trim());
        }
        System.setProperty(TestSSL.JAVA_TRUST_STORE_PROP, (((dataFileDir) + (File.separator)) + (TestSSL.TRUST_STORE_NAME)));
        System.setProperty(TestSSL.JAVA_TRUST_STORE_PASS_PROP, TestSSL.KEY_STORE_TRUST_STORE_PASSWORD);
        try {
            hs2Conn = DriverManager.getConnection(((miniHS2.getJdbcURL()) + ";ssl=true"), System.getProperty("user.name"), "bar");
            Assert.fail("SSL connection should fail with NON-SSL server");
        } catch (SQLException e) {
            // expected error
            Assert.assertEquals("08S01", e.getSQLState().trim());
        }
        miniHS2.stop();
        // Test in http mode with ssl properties specified in url
        System.clearProperty(TestSSL.JAVA_TRUST_STORE_PROP);
        System.clearProperty(TestSSL.JAVA_TRUST_STORE_PASS_PROP);
        SSLTestUtils.setHttpConfOverlay(confOverlay);
        miniHS2.start(confOverlay);
        try {
            hs2Conn = DriverManager.getConnection(miniHS2.getJdbcURL("default", SSL_CONN_PARAMS), System.getProperty("user.name"), "bar");
            Assert.fail("SSL connection should fail with NON-SSL server");
        } catch (SQLException e) {
            // expected error
            Assert.assertEquals("08S01", e.getSQLState().trim());
        }
    }

    /**
     * *
     * Test non-SSL client with SSL server fails
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConnectionMismatch() throws Exception {
        SSLTestUtils.setSslConfOverlay(confOverlay);
        // Test in binary mode
        SSLTestUtils.setBinaryConfOverlay(confOverlay);
        miniHS2.start(confOverlay);
        // Start HS2 with SSL
        try {
            hs2Conn = DriverManager.getConnection(miniHS2.getJdbcURL(), System.getProperty("user.name"), "bar");
            Assert.fail("NON SSL connection should fail with SSL server");
        } catch (SQLException e) {
            // expected error
            Assert.assertEquals("08S01", e.getSQLState().trim());
        }
        try {
            hs2Conn = DriverManager.getConnection(((miniHS2.getJdbcURL()) + ";ssl=false"), System.getProperty("user.name"), "bar");
            Assert.fail("NON SSL connection should fail with SSL server");
        } catch (SQLException e) {
            // expected error
            Assert.assertEquals("08S01", e.getSQLState().trim());
        }
        miniHS2.stop();
        // Test in http mode
        SSLTestUtils.setHttpConfOverlay(confOverlay);
        miniHS2.start(confOverlay);
        try {
            hs2Conn = DriverManager.getConnection(miniHS2.getJdbcURL("default", ";ssl=false"), System.getProperty("user.name"), "bar");
            Assert.fail("NON SSL connection should fail with SSL server");
        } catch (SQLException e) {
            // expected error
            Assert.assertEquals("08S01", e.getSQLState().trim());
        }
    }

    /**
     * *
     * Test SSL client connection to SSL server
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSSLConnectionWithURL() throws Exception {
        SSLTestUtils.setSslConfOverlay(confOverlay);
        // Test in binary mode
        SSLTestUtils.setBinaryConfOverlay(confOverlay);
        // Start HS2 with SSL
        miniHS2.start(confOverlay);
        // make SSL connection
        hs2Conn = DriverManager.getConnection(miniHS2.getJdbcURL("default", SSL_CONN_PARAMS), System.getProperty("user.name"), "bar");
        hs2Conn.close();
        miniHS2.stop();
        // Test in http mode
        SSLTestUtils.setHttpConfOverlay(confOverlay);
        miniHS2.start(confOverlay);
        // make SSL connection
        hs2Conn = DriverManager.getConnection(miniHS2.getJdbcURL("default", SSL_CONN_PARAMS), System.getProperty("user.name"), "bar");
        hs2Conn.close();
    }

    /**
     * *
     * Test SSL client connection to SSL server
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSSLConnectionWithProperty() throws Exception {
        SSLTestUtils.setSslConfOverlay(confOverlay);
        // Test in binary mode
        SSLTestUtils.setBinaryConfOverlay(confOverlay);
        // Start HS2 with SSL
        miniHS2.start(confOverlay);
        System.setProperty(TestSSL.JAVA_TRUST_STORE_PROP, (((dataFileDir) + (File.separator)) + (TestSSL.TRUST_STORE_NAME)));
        System.setProperty(TestSSL.JAVA_TRUST_STORE_PASS_PROP, TestSSL.KEY_STORE_TRUST_STORE_PASSWORD);
        // make SSL connection
        hs2Conn = DriverManager.getConnection(((miniHS2.getJdbcURL()) + ";ssl=true"), System.getProperty("user.name"), "bar");
        hs2Conn.close();
        miniHS2.stop();
        // Test in http mode
        SSLTestUtils.setHttpConfOverlay(confOverlay);
        miniHS2.start(confOverlay);
        // make SSL connection
        hs2Conn = DriverManager.getConnection(miniHS2.getJdbcURL("default", SSL_CONN_PARAMS), System.getProperty("user.name"), "bar");
        hs2Conn.close();
    }

    /**
     * Start HS2 in SSL mode, open a SSL connection and fetch data
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSSLFetch() throws Exception {
        SSLTestUtils.setSslConfOverlay(confOverlay);
        // Test in binary mode
        SSLTestUtils.setBinaryConfOverlay(confOverlay);
        // Start HS2 with SSL
        miniHS2.start(confOverlay);
        String tableName = "sslTab";
        Path dataFilePath = new Path(dataFileDir, "kv1.txt");
        // make SSL connection
        hs2Conn = DriverManager.getConnection(miniHS2.getJdbcURL("default", SSL_CONN_PARAMS), System.getProperty("user.name"), "bar");
        // Set up test data
        SSLTestUtils.setupTestTableWithData(tableName, dataFilePath, hs2Conn);
        Statement stmt = hs2Conn.createStatement();
        ResultSet res = stmt.executeQuery(("SELECT * FROM " + tableName));
        int rowCount = 0;
        while (res.next()) {
            ++rowCount;
            Assert.assertEquals(("val_" + (res.getInt(1))), res.getString(2));
        } 
        // read result over SSL
        Assert.assertEquals(500, rowCount);
        hs2Conn.close();
    }

    /**
     * *
     * Test a new connection when server sends a certificate with wrong CN
     * (sends a certificate for www.example.com instead of localhost)
     * Opening a new connection with this wrong certificate should fail
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConnectionWrongCertCN() throws Exception {
        // This call sets the default ssl params including the correct keystore in the server config
        SSLTestUtils.setSslConfOverlay(confOverlay);
        // Replace default keystore with keystore for www.example.com
        confOverlay.put(varname, (((dataFileDir) + (File.separator)) + (TestSSL.EXAMPLEDOTCOM_KEY_STORE_NAME)));
        // Binary (TCP) mode
        SSLTestUtils.setBinaryConfOverlay(confOverlay);
        miniHS2.start(confOverlay);
        try {
            hs2Conn = DriverManager.getConnection(miniHS2.getJdbcURL("default", SSL_CONN_PARAMS), System.getProperty("user.name"), "bar");
            Assert.fail(("SSL connection, with the server providing wrong certifcate (with CN www.example.com, " + "instead of localhost), should fail"));
        } catch (SQLException e) {
            // Expected error: should throw java.security.cert.CertificateException
            Assert.assertEquals("08S01", e.getSQLState().trim());
            Assert.assertTrue(e.toString().contains("java.security.cert.CertificateException"));
        }
        miniHS2.stop();
        // Http mode
        SSLTestUtils.setHttpConfOverlay(confOverlay);
        miniHS2.start(confOverlay);
        try {
            hs2Conn = DriverManager.getConnection(miniHS2.getJdbcURL("default", SSL_CONN_PARAMS), System.getProperty("user.name"), "bar");
            Assert.fail(("SSL connection, with the server providing wrong certifcate (with CN www.example.com, " + "instead of localhost), should fail"));
        } catch (SQLException e) {
            // Expected error: should throw javax.net.ssl.SSLPeerUnverifiedException
            Assert.assertEquals("08S01", e.getSQLState().trim());
            Assert.assertTrue(e.toString().contains("javax.net.ssl.SSLPeerUnverifiedException"));
        }
        // Revert to default keystore path
        confOverlay.put(varname, (((dataFileDir) + (File.separator)) + (TestSSL.LOCALHOST_KEY_STORE_NAME)));
    }

    /**
     * Test HMS server with SSL
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMetastoreWithSSL() throws Exception {
        SSLTestUtils.setMetastoreSslConf(TestSSL.conf);
        SSLTestUtils.setSslConfOverlay(confOverlay);
        // Test in http mode
        SSLTestUtils.setHttpConfOverlay(confOverlay);
        miniHS2 = new MiniHS2.Builder().withRemoteMetastore().withConf(TestSSL.conf).cleanupLocalDirOnStartup(false).build();
        miniHS2.start(confOverlay);
        String tableName = "sslTab";
        Path dataFilePath = new Path(dataFileDir, "kv1.txt");
        // make SSL connection
        hs2Conn = DriverManager.getConnection(miniHS2.getJdbcURL("default", SSL_CONN_PARAMS), System.getProperty("user.name"), "bar");
        // Set up test data
        SSLTestUtils.setupTestTableWithData(tableName, dataFilePath, hs2Conn);
        Statement stmt = hs2Conn.createStatement();
        ResultSet res = stmt.executeQuery(("SELECT * FROM " + tableName));
        int rowCount = 0;
        while (res.next()) {
            ++rowCount;
            Assert.assertEquals(("val_" + (res.getInt(1))), res.getString(2));
        } 
        // read result over SSL
        Assert.assertEquals(500, rowCount);
        hs2Conn.close();
    }

    /**
     * Verify the HS2 can't connect to HMS if the certificate doesn't match
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMetastoreConnectionWrongCertCN() throws Exception {
        SSLTestUtils.setMetastoreSslConf(TestSSL.conf);
        TestSSL.conf.setVar(HIVE_METASTORE_SSL_KEYSTORE_PATH, (((dataFileDir) + (File.separator)) + (TestSSL.EXAMPLEDOTCOM_KEY_STORE_NAME)));
        miniHS2 = new MiniHS2.Builder().withRemoteMetastore().withConf(TestSSL.conf).cleanupLocalDirOnStartup(false).build();
        try {
            miniHS2.start(confOverlay);
        } catch (ConnectException e) {
            Assert.assertTrue(e.toString().contains("Connection refused"));
        }
        miniHS2.stop();
    }
}

