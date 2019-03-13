/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.as2;


import AS2Charset.US_ASCII;
import AS2EncryptionAlgorithm.AES128_CBC;
import AS2Header.AS2_FROM;
import AS2Header.AS2_TO;
import AS2Header.AS2_VERSION;
import AS2Header.CONTENT_LENGTH;
import AS2Header.CONTENT_TYPE;
import AS2Header.DATE;
import AS2Header.FROM;
import AS2Header.MESSAGE_ID;
import AS2Header.SUBJECT;
import AS2Header.TARGET_HOST;
import AS2Header.USER_AGENT;
import AS2MediaType.APPLICATION_EDIFACT;
import AS2MediaType.APPLICATION_PKCS7_SIGNATURE;
import AS2MediaType.MULTIPART_SIGNED;
import AS2MessageStructure.ENCRYPTED;
import AS2MessageStructure.PLAIN;
import AS2MessageStructure.SIGNED;
import AS2MimeType.APPLICATION_PKCS7_MIME;
import AS2SignatureAlgorithm.SHA256WITHRSA;
import java.security.KeyPair;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.as2.api.AS2ClientConnection;
import org.apache.camel.component.as2.api.AS2ClientManager;
import org.apache.camel.component.as2.api.AS2SignedDataGenerator;
import org.apache.camel.component.as2.api.entity.ApplicationEDIFACTEntity;
import org.apache.camel.component.as2.api.entity.ApplicationPkcs7MimeEnvelopedDataEntity;
import org.apache.camel.component.as2.api.entity.ApplicationPkcs7SignatureEntity;
import org.apache.camel.component.as2.api.entity.MimeEntity;
import org.apache.camel.component.as2.api.entity.MultipartSignedEntity;
import org.apache.camel.component.as2.internal.AS2ApiCollection;
import org.apache.camel.component.as2.internal.AS2ServerManagerApiMethod;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.bouncycastle.util.io.Streams;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for {@link org.apache.camel.component.as2.api.AS2ServerManager} APIs.
 */
public class AS2ServerManagerIntegrationTest extends AbstractAS2TestSupport {
    private static final String PROCESSOR_EXCEPTION_MSG = "Processor Exception";

    private static final String EXPECTED_EXCEPTION_MSG = "Failed to process AS2 message: " + (AS2ServerManagerIntegrationTest.PROCESSOR_EXCEPTION_MSG);

    private static final Logger LOG = LoggerFactory.getLogger(AS2ServerManagerIntegrationTest.class);

    private static final String PATH_PREFIX = AS2ApiCollection.getCollection().getApiName(AS2ServerManagerApiMethod.class).getName();

    private static final String METHOD = "POST";

    private static final String TARGET_HOST = "localhost";

    private static final int TARGET_PORT = 8888;

    private static final String AS2_VERSION = "1.1";

    private static final String USER_AGENT = "Camel AS2 Endpoint";

    private static final String REQUEST_URI = "/";

    private static final String AS2_NAME = "878051556";

    private static final String SUBJECT = "Test Case";

    private static final String FROM = "mrAS@example.org";

    private static final String CLIENT_FQDN = "example.org";

    private static final String DISPOSITION_NOTIFICATION_TO = "mrAS@example.org";

    private static final String[] SIGNED_RECEIPT_MIC_ALGORITHMS = new String[]{ "sha1", "md5" };

    private static final String EDI_MESSAGE = "UNB+UNOA:1+005435656:1+006415160:1+060515:1434+00000000000778\'\n" + (((((((((((((((((((((((("UNH+00000000000117+INVOIC:D:97B:UN\'\n" + "BGM+380+342459+9\'\n") + "DTM+3:20060515:102\'\n") + "RFF+ON:521052\'\n") + "NAD+BY+792820524::16++CUMMINS MID-RANGE ENGINE PLANT\'\n") + "NAD+SE+005435656::16++GENERAL WIDGET COMPANY\'\n") + "CUX+1:USD\'\n") + "LIN+1++157870:IN\'\n") + "IMD+F++:::WIDGET\'\n") + "QTY+47:1020:EA\'\n") + "ALI+US\'\n") + "MOA+203:1202.58\'\n") + "PRI+INV:1.179\'\n") + "LIN+2++157871:IN\'\n") + "IMD+F++:::DIFFERENT WIDGET\'\n") + "QTY+47:20:EA\'\n") + "ALI+JP\'\n") + "MOA+203:410\'\n") + "PRI+INV:20.5\'\n") + "UNS+S\'\n") + "MOA+39:2137.58\'\n") + "ALC+C+ABG\'\n") + "MOA+8:525\'\n") + "UNT+23+00000000000117\'\n") + "UNZ+1+00000000000778'");

    private static AS2SignedDataGenerator gen;

    private static KeyPair issueKP;

    private static X509Certificate issueCert;

    private static KeyPair signingKP;

    private static X509Certificate signingCert;

    private static List<X509Certificate> certList;

    private static KeyPair decryptingKP;

    @Test
    public void receivePlainEDIMessageTest() throws Exception {
        AS2ClientConnection clientConnection = new AS2ClientConnection(AS2ServerManagerIntegrationTest.AS2_VERSION, AS2ServerManagerIntegrationTest.USER_AGENT, AS2ServerManagerIntegrationTest.CLIENT_FQDN, AS2ServerManagerIntegrationTest.TARGET_HOST, AS2ServerManagerIntegrationTest.TARGET_PORT);
        AS2ClientManager clientManager = new AS2ClientManager(clientConnection);
        clientManager.send(AS2ServerManagerIntegrationTest.EDI_MESSAGE, AS2ServerManagerIntegrationTest.REQUEST_URI, AS2ServerManagerIntegrationTest.SUBJECT, AS2ServerManagerIntegrationTest.FROM, AS2ServerManagerIntegrationTest.AS2_NAME, AS2ServerManagerIntegrationTest.AS2_NAME, PLAIN, ContentType.create(APPLICATION_EDIFACT, US_ASCII), null, null, null, null, null, AS2ServerManagerIntegrationTest.DISPOSITION_NOTIFICATION_TO, AS2ServerManagerIntegrationTest.SIGNED_RECEIPT_MIC_ALGORITHMS, null, null);
        MockEndpoint mockEndpoint = getMockEndpoint("mock:as2RcvMsgs");
        mockEndpoint.expectedMinimumMessageCount(1);
        mockEndpoint.setResultWaitTime(TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS));
        mockEndpoint.assertIsSatisfied();
        final List<Exchange> exchanges = mockEndpoint.getExchanges();
        assertNotNull("listen result", exchanges);
        assertFalse("listen result", exchanges.isEmpty());
        AS2ServerManagerIntegrationTest.LOG.debug(("poll result: " + exchanges));
        Exchange exchange = exchanges.get(0);
        Message message = exchange.getIn();
        assertNotNull("exchange message", message);
        BasicHttpContext context = message.getBody(BasicHttpContext.class);
        assertNotNull("context", context);
        HttpCoreContext coreContext = HttpCoreContext.adapt(context);
        HttpRequest request = coreContext.getRequest();
        assertNotNull("request", request);
        assertEquals("Unexpected method value", AS2ServerManagerIntegrationTest.METHOD, request.getRequestLine().getMethod());
        assertEquals("Unexpected request URI value", AS2ServerManagerIntegrationTest.REQUEST_URI, request.getRequestLine().getUri());
        assertEquals("Unexpected HTTP version value", HttpVersion.HTTP_1_1, request.getRequestLine().getProtocolVersion());
        assertEquals("Unexpected subject value", AS2ServerManagerIntegrationTest.SUBJECT, request.getFirstHeader(AS2Header.SUBJECT).getValue());
        assertEquals("Unexpected from value", AS2ServerManagerIntegrationTest.FROM, request.getFirstHeader(AS2Header.FROM).getValue());
        assertEquals("Unexpected AS2 version value", AS2ServerManagerIntegrationTest.AS2_VERSION, request.getFirstHeader(AS2Header.AS2_VERSION).getValue());
        assertEquals("Unexpected AS2 from value", AS2ServerManagerIntegrationTest.AS2_NAME, request.getFirstHeader(AS2_FROM).getValue());
        assertEquals("Unexpected AS2 to value", AS2ServerManagerIntegrationTest.AS2_NAME, request.getFirstHeader(AS2_TO).getValue());
        assertTrue("Unexpected message id value", request.getFirstHeader(MESSAGE_ID).getValue().endsWith(((AS2ServerManagerIntegrationTest.CLIENT_FQDN) + ">")));
        assertEquals("Unexpected target host value", (((AS2ServerManagerIntegrationTest.TARGET_HOST) + ":") + (AS2ServerManagerIntegrationTest.TARGET_PORT)), request.getFirstHeader(AS2Header.TARGET_HOST).getValue());
        assertEquals("Unexpected user agent value", AS2ServerManagerIntegrationTest.USER_AGENT, request.getFirstHeader(AS2Header.USER_AGENT).getValue());
        assertNotNull("Date value missing", request.getFirstHeader(DATE));
        assertNotNull("Content length value missing", request.getFirstHeader(CONTENT_LENGTH));
        assertTrue("Unexpected content type for message", request.getFirstHeader(CONTENT_TYPE).getValue().startsWith(APPLICATION_EDIFACT));
        assertTrue("Request does not contain entity", (request instanceof BasicHttpEntityEnclosingRequest));
        HttpEntity entity = ((BasicHttpEntityEnclosingRequest) (request)).getEntity();
        assertNotNull("Request does not contain entity", entity);
        assertTrue("Unexpected request entity type", (entity instanceof ApplicationEDIFACTEntity));
        ApplicationEDIFACTEntity ediEntity = ((ApplicationEDIFACTEntity) (entity));
        assertTrue("Unexpected content type for entity", ediEntity.getContentType().getValue().startsWith(APPLICATION_EDIFACT));
        assertTrue("Entity not set as main body of request", ediEntity.isMainBody());
        String rcvdMessage = ediEntity.getEdiMessage().replaceAll("\r", "");
        assertEquals("EDI message does not match", AS2ServerManagerIntegrationTest.EDI_MESSAGE, rcvdMessage);
    }

    @Test
    public void receiveMultipartSignedMessageTest() throws Exception {
        AS2ClientConnection clientConnection = new AS2ClientConnection(AS2ServerManagerIntegrationTest.AS2_VERSION, AS2ServerManagerIntegrationTest.USER_AGENT, AS2ServerManagerIntegrationTest.CLIENT_FQDN, AS2ServerManagerIntegrationTest.TARGET_HOST, AS2ServerManagerIntegrationTest.TARGET_PORT);
        AS2ClientManager clientManager = new AS2ClientManager(clientConnection);
        clientManager.send(AS2ServerManagerIntegrationTest.EDI_MESSAGE, AS2ServerManagerIntegrationTest.REQUEST_URI, AS2ServerManagerIntegrationTest.SUBJECT, AS2ServerManagerIntegrationTest.FROM, AS2ServerManagerIntegrationTest.AS2_NAME, AS2ServerManagerIntegrationTest.AS2_NAME, SIGNED, ContentType.create(APPLICATION_EDIFACT, US_ASCII), null, SHA256WITHRSA, AS2ServerManagerIntegrationTest.certList.toArray(new Certificate[0]), AS2ServerManagerIntegrationTest.signingKP.getPrivate(), null, AS2ServerManagerIntegrationTest.DISPOSITION_NOTIFICATION_TO, AS2ServerManagerIntegrationTest.SIGNED_RECEIPT_MIC_ALGORITHMS, null, null);
        MockEndpoint mockEndpoint = getMockEndpoint("mock:as2RcvMsgs");
        mockEndpoint.expectedMinimumMessageCount(1);
        mockEndpoint.setResultWaitTime(TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS));
        mockEndpoint.assertIsSatisfied();
        final List<Exchange> exchanges = mockEndpoint.getExchanges();
        assertNotNull("listen result", exchanges);
        assertFalse("listen result", exchanges.isEmpty());
        AS2ServerManagerIntegrationTest.LOG.debug(("poll result: " + exchanges));
        Exchange exchange = exchanges.get(0);
        Message message = exchange.getIn();
        assertNotNull("exchange message", message);
        BasicHttpContext context = message.getBody(BasicHttpContext.class);
        assertNotNull("context", context);
        HttpCoreContext coreContext = HttpCoreContext.adapt(context);
        HttpRequest request = coreContext.getRequest();
        assertNotNull("request", request);
        assertEquals("Unexpected method value", AS2ServerManagerIntegrationTest.METHOD, request.getRequestLine().getMethod());
        assertEquals("Unexpected request URI value", AS2ServerManagerIntegrationTest.REQUEST_URI, request.getRequestLine().getUri());
        assertEquals("Unexpected HTTP version value", HttpVersion.HTTP_1_1, request.getRequestLine().getProtocolVersion());
        assertEquals("Unexpected subject value", AS2ServerManagerIntegrationTest.SUBJECT, request.getFirstHeader(AS2Header.SUBJECT).getValue());
        assertEquals("Unexpected from value", AS2ServerManagerIntegrationTest.FROM, request.getFirstHeader(AS2Header.FROM).getValue());
        assertEquals("Unexpected AS2 version value", AS2ServerManagerIntegrationTest.AS2_VERSION, request.getFirstHeader(AS2Header.AS2_VERSION).getValue());
        assertEquals("Unexpected AS2 from value", AS2ServerManagerIntegrationTest.AS2_NAME, request.getFirstHeader(AS2_FROM).getValue());
        assertEquals("Unexpected AS2 to value", AS2ServerManagerIntegrationTest.AS2_NAME, request.getFirstHeader(AS2_TO).getValue());
        assertTrue("Unexpected message id value", request.getFirstHeader(MESSAGE_ID).getValue().endsWith(((AS2ServerManagerIntegrationTest.CLIENT_FQDN) + ">")));
        assertEquals("Unexpected target host value", (((AS2ServerManagerIntegrationTest.TARGET_HOST) + ":") + (AS2ServerManagerIntegrationTest.TARGET_PORT)), request.getFirstHeader(AS2Header.TARGET_HOST).getValue());
        assertEquals("Unexpected user agent value", AS2ServerManagerIntegrationTest.USER_AGENT, request.getFirstHeader(AS2Header.USER_AGENT).getValue());
        assertNotNull("Date value missing", request.getFirstHeader(DATE));
        assertNotNull("Content length value missing", request.getFirstHeader(CONTENT_LENGTH));
        assertTrue("Unexpected content type for message", request.getFirstHeader(CONTENT_TYPE).getValue().startsWith(MULTIPART_SIGNED));
        assertTrue("Request does not contain entity", (request instanceof BasicHttpEntityEnclosingRequest));
        HttpEntity entity = ((BasicHttpEntityEnclosingRequest) (request)).getEntity();
        assertNotNull("Request does not contain entity", entity);
        assertTrue("Unexpected request entity type", (entity instanceof MultipartSignedEntity));
        MultipartSignedEntity signedEntity = ((MultipartSignedEntity) (entity));
        assertTrue("Entity not set as main body of request", signedEntity.isMainBody());
        assertTrue("Request contains invalid number of mime parts", ((signedEntity.getPartCount()) == 2));
        // Validated first mime part.
        assertTrue("First mime part incorrect type ", ((signedEntity.getPart(0)) instanceof ApplicationEDIFACTEntity));
        ApplicationEDIFACTEntity ediEntity = ((ApplicationEDIFACTEntity) (signedEntity.getPart(0)));
        assertTrue("Unexpected content type for first mime part", ediEntity.getContentType().getValue().startsWith(APPLICATION_EDIFACT));
        assertFalse("First mime type set as main body of request", ediEntity.isMainBody());
        // Validate second mime part.
        assertTrue("Second mime part incorrect type ", ((signedEntity.getPart(1)) instanceof ApplicationPkcs7SignatureEntity));
        ApplicationPkcs7SignatureEntity signatureEntity = ((ApplicationPkcs7SignatureEntity) (signedEntity.getPart(1)));
        assertTrue("Unexpected content type for second mime part", signatureEntity.getContentType().getValue().startsWith(APPLICATION_PKCS7_SIGNATURE));
        assertFalse("First mime type set as main body of request", signatureEntity.isMainBody());
        // Validate Signature
        assertTrue("Signature is invalid", signedEntity.isValid());
    }

    @Test
    public void receiveEnvelopedMessageTest() throws Exception {
        AS2ClientConnection clientConnection = new AS2ClientConnection(AS2ServerManagerIntegrationTest.AS2_VERSION, AS2ServerManagerIntegrationTest.USER_AGENT, AS2ServerManagerIntegrationTest.CLIENT_FQDN, AS2ServerManagerIntegrationTest.TARGET_HOST, AS2ServerManagerIntegrationTest.TARGET_PORT);
        AS2ClientManager clientManager = new AS2ClientManager(clientConnection);
        clientManager.send(AS2ServerManagerIntegrationTest.EDI_MESSAGE, AS2ServerManagerIntegrationTest.REQUEST_URI, AS2ServerManagerIntegrationTest.SUBJECT, AS2ServerManagerIntegrationTest.FROM, AS2ServerManagerIntegrationTest.AS2_NAME, AS2ServerManagerIntegrationTest.AS2_NAME, ENCRYPTED, ContentType.create(APPLICATION_EDIFACT, US_ASCII), null, null, null, null, null, AS2ServerManagerIntegrationTest.DISPOSITION_NOTIFICATION_TO, AS2ServerManagerIntegrationTest.SIGNED_RECEIPT_MIC_ALGORITHMS, AES128_CBC, AS2ServerManagerIntegrationTest.certList.toArray(new Certificate[0]));
        MockEndpoint mockEndpoint = getMockEndpoint("mock:as2RcvMsgs");
        mockEndpoint.expectedMinimumMessageCount(1);
        mockEndpoint.setResultWaitTime(TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS));
        mockEndpoint.assertIsSatisfied();
        final List<Exchange> exchanges = mockEndpoint.getExchanges();
        assertNotNull("listen result", exchanges);
        assertFalse("listen result", exchanges.isEmpty());
        AS2ServerManagerIntegrationTest.LOG.debug(("poll result: " + exchanges));
        Exchange exchange = exchanges.get(0);
        Message message = exchange.getIn();
        assertNotNull("exchange message", message);
        BasicHttpContext context = message.getBody(BasicHttpContext.class);
        assertNotNull("context", context);
        HttpCoreContext coreContext = HttpCoreContext.adapt(context);
        HttpRequest request = coreContext.getRequest();
        assertNotNull("request", request);
        assertEquals("Unexpected method value", AS2ServerManagerIntegrationTest.METHOD, request.getRequestLine().getMethod());
        assertEquals("Unexpected request URI value", AS2ServerManagerIntegrationTest.REQUEST_URI, request.getRequestLine().getUri());
        assertEquals("Unexpected HTTP version value", HttpVersion.HTTP_1_1, request.getRequestLine().getProtocolVersion());
        assertEquals("Unexpected subject value", AS2ServerManagerIntegrationTest.SUBJECT, request.getFirstHeader(AS2Header.SUBJECT).getValue());
        assertEquals("Unexpected from value", AS2ServerManagerIntegrationTest.FROM, request.getFirstHeader(AS2Header.FROM).getValue());
        assertEquals("Unexpected AS2 version value", AS2ServerManagerIntegrationTest.AS2_VERSION, request.getFirstHeader(AS2Header.AS2_VERSION).getValue());
        assertEquals("Unexpected AS2 from value", AS2ServerManagerIntegrationTest.AS2_NAME, request.getFirstHeader(AS2_FROM).getValue());
        assertEquals("Unexpected AS2 to value", AS2ServerManagerIntegrationTest.AS2_NAME, request.getFirstHeader(AS2_TO).getValue());
        assertTrue("Unexpected message id value", request.getFirstHeader(MESSAGE_ID).getValue().endsWith(((AS2ServerManagerIntegrationTest.CLIENT_FQDN) + ">")));
        assertEquals("Unexpected target host value", (((AS2ServerManagerIntegrationTest.TARGET_HOST) + ":") + (AS2ServerManagerIntegrationTest.TARGET_PORT)), request.getFirstHeader(AS2Header.TARGET_HOST).getValue());
        assertEquals("Unexpected user agent value", AS2ServerManagerIntegrationTest.USER_AGENT, request.getFirstHeader(AS2Header.USER_AGENT).getValue());
        assertNotNull("Date value missing", request.getFirstHeader(DATE));
        assertNotNull("Content length value missing", request.getFirstHeader(CONTENT_LENGTH));
        assertTrue("Unexpected content type for message", request.getFirstHeader(CONTENT_TYPE).getValue().startsWith(APPLICATION_PKCS7_MIME));
        assertTrue("Request does not contain entity", (request instanceof BasicHttpEntityEnclosingRequest));
        HttpEntity entity = ((BasicHttpEntityEnclosingRequest) (request)).getEntity();
        assertNotNull("Request does not contain entity", entity);
        assertTrue("Unexpected request entity type", (entity instanceof ApplicationPkcs7MimeEnvelopedDataEntity));
        ApplicationPkcs7MimeEnvelopedDataEntity envelopedEntity = ((ApplicationPkcs7MimeEnvelopedDataEntity) (entity));
        assertTrue("Entity not set as main body of request", envelopedEntity.isMainBody());
        // Validated enveloped part.
        MimeEntity encryptedEntity = envelopedEntity.getEncryptedEntity(AS2ServerManagerIntegrationTest.signingKP.getPrivate());
        assertTrue("Enveloped mime part incorrect type ", (encryptedEntity instanceof ApplicationEDIFACTEntity));
        ApplicationEDIFACTEntity ediEntity = ((ApplicationEDIFACTEntity) (encryptedEntity));
        assertTrue("Unexpected content type for enveloped mime part", ediEntity.getContentType().getValue().startsWith(APPLICATION_EDIFACT));
        assertFalse("Enveloped mime type set as main body of request", ediEntity.isMainBody());
        assertEquals("Unexpected content for enveloped mime part", AS2ServerManagerIntegrationTest.EDI_MESSAGE.replaceAll("[\n\r]", ""), ediEntity.getEdiMessage().replaceAll("[\n\r]", ""));
    }

    @Test
    public void sendEditMessageToFailingProcessorTest() throws Exception {
        AS2ClientConnection clientConnection = new AS2ClientConnection(AS2ServerManagerIntegrationTest.AS2_VERSION, AS2ServerManagerIntegrationTest.USER_AGENT, AS2ServerManagerIntegrationTest.CLIENT_FQDN, AS2ServerManagerIntegrationTest.TARGET_HOST, AS2ServerManagerIntegrationTest.TARGET_PORT);
        AS2ClientManager clientManager = new AS2ClientManager(clientConnection);
        HttpCoreContext context = clientManager.send(AS2ServerManagerIntegrationTest.EDI_MESSAGE, "/process_error", AS2ServerManagerIntegrationTest.SUBJECT, AS2ServerManagerIntegrationTest.FROM, AS2ServerManagerIntegrationTest.AS2_NAME, AS2ServerManagerIntegrationTest.AS2_NAME, PLAIN, ContentType.create(APPLICATION_EDIFACT, US_ASCII), null, null, null, null, null, AS2ServerManagerIntegrationTest.DISPOSITION_NOTIFICATION_TO, AS2ServerManagerIntegrationTest.SIGNED_RECEIPT_MIC_ALGORITHMS, null, null);
        MockEndpoint mockEndpoint = getMockEndpoint("mock:as2RcvMsgs");
        mockEndpoint.expectedMinimumMessageCount(0);
        mockEndpoint.setResultWaitTime(TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS));
        mockEndpoint.assertIsSatisfied();
        HttpResponse response = context.getResponse();
        assertEquals("Unexpected status code for response", HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatusLine().getStatusCode());
        HttpEntity responseEntity = response.getEntity();
        String errorMessage = new String(Streams.readAll(responseEntity.getContent()));
        assertEquals("", AS2ServerManagerIntegrationTest.EXPECTED_EXCEPTION_MSG, errorMessage);
    }
}

