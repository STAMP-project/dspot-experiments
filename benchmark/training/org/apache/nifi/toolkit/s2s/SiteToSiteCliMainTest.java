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
package org.apache.nifi.toolkit.s2s;


import SiteToSiteCliMain.BATCH_COUNT_OPTION;
import SiteToSiteCliMain.BATCH_DURATION_OPTION;
import SiteToSiteCliMain.BATCH_SIZE_OPTION;
import SiteToSiteCliMain.COMPRESSION_OPTION;
import SiteToSiteCliMain.DIRECTION_OPTION;
import SiteToSiteCliMain.PEER_PERSISTENCE_FILE_OPTION;
import SiteToSiteCliMain.PENALIZATION_OPTION;
import SiteToSiteCliMain.PORT_IDENTIFIER_OPTION;
import SiteToSiteCliMain.PORT_NAME_OPTION;
import SiteToSiteCliMain.TIMEOUT_OPTION;
import SiteToSiteCliMain.URL_OPTION;
import java.io.File;
import java.util.concurrent.TimeUnit;
import org.apache.commons.cli.ParseException;
import org.apache.nifi.remote.TransferDirection;
import org.apache.nifi.remote.client.KeystoreType;
import org.apache.nifi.remote.protocol.SiteToSiteTransportProtocol;
import org.apache.nifi.remote.protocol.http.HttpProxy;
import org.junit.Test;

import static SiteToSiteCliMain.KEYSTORE_OPTION;
import static SiteToSiteCliMain.KEY_STORE_PASSWORD_OPTION;
import static SiteToSiteCliMain.KEY_STORE_TYPE_OPTION;
import static SiteToSiteCliMain.PROXY_HOST_OPTION;
import static SiteToSiteCliMain.PROXY_PASSWORD_OPTION;
import static SiteToSiteCliMain.PROXY_PORT_OPTION;
import static SiteToSiteCliMain.PROXY_USERNAME_OPTION;
import static SiteToSiteCliMain.TRUST_STORE_OPTION;
import static SiteToSiteCliMain.TRUST_STORE_PASSWORD_OPTION;
import static SiteToSiteCliMain.TRUST_STORE_TYPE_OPTION;


public class SiteToSiteCliMainTest {
    private String expectedUrl;

    private TransferDirection expectedTransferDirection;

    private SiteToSiteTransportProtocol expectedSiteToSiteTransportProtocol;

    private String expectedPortName;

    private String expectedPortIdentifier;

    private long expectedTimeoutNs;

    private long expectedPenalizationNs;

    private String expectedKeystoreFilename;

    private String expectedKeystorePass;

    private KeystoreType expectedKeystoreType;

    private String expectedTruststoreFilename;

    private String expectedTruststorePass;

    private KeystoreType expectedTruststoreType;

    private boolean expectedCompression;

    private File expectedPeerPersistenceFile;

    private int expectedBatchCount;

    private long expectedBatchDuration;

    private long expectedBatchSize;

    private HttpProxy expectedHttpProxy;

    @Test
    public void testParseNoArgs() throws ParseException {
        parseAndCheckExpected(new String[0]);
    }

    @Test
    public void testParseUrl() throws ParseException {
        expectedUrl = "http://fake.url:8080/nifi";
        parseAndCheckExpected("u", URL_OPTION, expectedUrl);
    }

    @Test
    public void testParsePortName() throws ParseException {
        expectedPortName = "testPortName";
        parseAndCheckExpected("n", PORT_NAME_OPTION, expectedPortName);
    }

    @Test
    public void testParsePortIdentifier() throws ParseException {
        expectedPortIdentifier = "testPortId";
        parseAndCheckExpected("i", PORT_IDENTIFIER_OPTION, expectedPortIdentifier);
    }

    @Test
    public void testParseTimeout() throws ParseException {
        expectedTimeoutNs = TimeUnit.DAYS.toNanos(3);
        parseAndCheckExpected(null, TIMEOUT_OPTION, "3 days");
    }

    @Test
    public void testParsePenalization() throws ParseException {
        expectedPenalizationNs = TimeUnit.HOURS.toNanos(4);
        parseAndCheckExpected(null, PENALIZATION_OPTION, "4 hours");
    }

    @Test
    public void testParseKeystore() throws ParseException {
        expectedKeystoreFilename = "keystore.pkcs12";
        expectedKeystorePass = "badPassword";
        expectedKeystoreType = KeystoreType.PKCS12;
        parseAndCheckExpected(new String[]{ "--" + (KEYSTORE_OPTION), expectedKeystoreFilename, "--" + (KEY_STORE_PASSWORD_OPTION), expectedKeystorePass, "--" + (KEY_STORE_TYPE_OPTION), expectedKeystoreType.toString() });
    }

    @Test
    public void testParseTruststore() throws ParseException {
        expectedTruststoreFilename = "truststore.pkcs12";
        expectedTruststorePass = "badPassword";
        expectedTruststoreType = KeystoreType.PKCS12;
        parseAndCheckExpected(new String[]{ "--" + (TRUST_STORE_OPTION), expectedTruststoreFilename, "--" + (TRUST_STORE_PASSWORD_OPTION), expectedTruststorePass, "--" + (TRUST_STORE_TYPE_OPTION), expectedTruststoreType.toString() });
    }

    @Test
    public void testParseCompression() throws ParseException {
        expectedCompression = true;
        parseAndCheckExpected("c", COMPRESSION_OPTION, null);
    }

    @Test
    public void testParsePeerPersistenceFile() throws ParseException {
        String pathname = "test";
        expectedPeerPersistenceFile = new File(pathname);
        parseAndCheckExpected(null, PEER_PERSISTENCE_FILE_OPTION, pathname);
    }

    @Test
    public void testParseBatchCount() throws ParseException {
        expectedBatchCount = 55;
        parseAndCheckExpected(null, BATCH_COUNT_OPTION, Integer.toString(expectedBatchCount));
    }

    @Test
    public void testParseBatchDuration() throws ParseException {
        expectedBatchDuration = TimeUnit.MINUTES.toNanos(5);
        parseAndCheckExpected(null, BATCH_DURATION_OPTION, "5 min");
    }

    @Test
    public void testParseBatchSize() throws ParseException {
        expectedBatchSize = 1026;
        parseAndCheckExpected(null, BATCH_SIZE_OPTION, Long.toString(expectedBatchSize));
    }

    @Test
    public void testParseProxy() throws ParseException {
        String expectedHost = "testHost";
        int expectedPort = 292;
        String expectedUser = "testUser";
        String expectedPassword = "badPassword";
        expectedHttpProxy = new HttpProxy(expectedHost, expectedPort, expectedUser, expectedPassword);
        parseAndCheckExpected(new String[]{ "--" + (PROXY_HOST_OPTION), expectedHost, "--" + (PROXY_PORT_OPTION), Integer.toString(expectedPort), "--" + (PROXY_USERNAME_OPTION), expectedUser, "--" + (PROXY_PASSWORD_OPTION), expectedPassword });
    }

    @Test
    public void testParseTransferDirection() throws ParseException {
        expectedTransferDirection = TransferDirection.RECEIVE;
        parseAndCheckExpected("d", DIRECTION_OPTION, expectedTransferDirection.toString());
    }
}

