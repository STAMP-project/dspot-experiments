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
package org.apache.camel.component.as2.api;


import AS2Charset.US_ASCII;
import AS2CompressionAlgorithm.ZLIB;
import AS2DispositionType.PROCESSED;
import AS2EncryptionAlgorithm.AES128_CBC;
import AS2EncryptionAlgorithm.AES128_CCM;
import AS2EncryptionAlgorithm.AES128_GCM;
import AS2EncryptionAlgorithm.AES192_CBC;
import AS2EncryptionAlgorithm.AES192_CCM;
import AS2EncryptionAlgorithm.AES192_GCM;
import AS2EncryptionAlgorithm.AES256_CBC;
import AS2EncryptionAlgorithm.AES256_CCM;
import AS2EncryptionAlgorithm.AES256_GCM;
import AS2EncryptionAlgorithm.CAMELLIA128_CBC;
import AS2EncryptionAlgorithm.CAMELLIA192_CBC;
import AS2EncryptionAlgorithm.CAMELLIA256_CBC;
import AS2EncryptionAlgorithm.CAST5_CBC;
import AS2EncryptionAlgorithm.DES_CBC;
import AS2EncryptionAlgorithm.DES_EDE3_CBC;
import AS2EncryptionAlgorithm.GOST28147_GCFB;
import AS2EncryptionAlgorithm.IDEA_CBC;
import AS2EncryptionAlgorithm.RC2_CBC;
import AS2EncryptionAlgorithm.RC4;
import AS2EncryptionAlgorithm.SEED_CBC;
import AS2Header.AS2_FROM;
import AS2Header.AS2_TO;
import AS2Header.AS2_VERSION;
import AS2Header.CONTENT_LENGTH;
import AS2Header.CONTENT_TYPE;
import AS2Header.DATE;
import AS2Header.DISPOSITION_NOTIFICATION_OPTIONS;
import AS2Header.FROM;
import AS2Header.MESSAGE_ID;
import AS2Header.SERVER;
import AS2Header.SUBJECT;
import AS2Header.TARGET_HOST;
import AS2Header.USER_AGENT;
import AS2MediaType.APPLICATION_EDIFACT;
import AS2MediaType.APPLICATION_PKCS7_SIGNATURE;
import AS2MediaType.MULTIPART_SIGNED;
import AS2MessageStructure.ENCRYPTED_COMPRESSED;
import AS2MessageStructure.ENCRYPTED_COMPRESSED_SIGNED;
import AS2MessageStructure.PLAIN;
import AS2MessageStructure.PLAIN_COMPRESSED;
import AS2MessageStructure.SIGNED;
import AS2MessageStructure.SIGNED_COMPRESSED;
import AS2MimeType.APPLICATION_PKCS7_MIME;
import AS2MimeType.MESSAGE_DISPOSITION_NOTIFICATION;
import AS2MimeType.TEXT_PLAIN;
import AS2SignatureAlgorithm.SHA256WITHRSA;
import DispositionMode.AUTOMATIC_ACTION_MDN_SENT_AUTOMATICALLY;
import java.security.KeyPair;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.component.as2.api.entity.AS2DispositionModifier;
import org.apache.camel.component.as2.api.entity.AS2DispositionType;
import org.apache.camel.component.as2.api.entity.AS2MessageDispositionNotificationEntity;
import org.apache.camel.component.as2.api.entity.ApplicationEDIEntity;
import org.apache.camel.component.as2.api.entity.ApplicationEDIFACTEntity;
import org.apache.camel.component.as2.api.entity.ApplicationPkcs7MimeCompressedDataEntity;
import org.apache.camel.component.as2.api.entity.ApplicationPkcs7MimeEnvelopedDataEntity;
import org.apache.camel.component.as2.api.entity.ApplicationPkcs7SignatureEntity;
import org.apache.camel.component.as2.api.entity.DispositionMode;
import org.apache.camel.component.as2.api.entity.DispositionNotificationMultipartReportEntity;
import org.apache.camel.component.as2.api.entity.MimeEntity;
import org.apache.camel.component.as2.api.entity.MultipartSignedEntity;
import org.apache.camel.component.as2.api.entity.TextPlainEntity;
import org.apache.camel.component.as2.api.util.AS2Utils;
import org.apache.camel.component.as2.api.util.EntityUtils;
import org.apache.camel.component.as2.api.util.HttpMessageUtils;
import org.apache.camel.component.as2.api.util.MicUtils;
import org.apache.camel.component.as2.api.util.MicUtils.ReceivedContentMic;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpDateGenerator;
import org.bouncycastle.cms.jcajce.ZlibExpanderProvider;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AS2MessageTest {
    public static final String EDI_MESSAGE = "UNB+UNOA:1+005435656:1+006415160:1+060515:1434+00000000000778\'\n" + (((((((((((((((((((((((("UNH+00000000000117+INVOIC:D:97B:UN\'\n" + "BGM+380+342459+9\'\n") + "DTM+3:20060515:102\'\n") + "RFF+ON:521052\'\n") + "NAD+BY+792820524::16++CUMMINS MID-RANGE ENGINE PLANT\'\n") + "NAD+SE+005435656::16++GENERAL WIDGET COMPANY\'\n") + "CUX+1:USD\'\n") + "LIN+1++157870:IN\'\n") + "IMD+F++:::WIDGET\'\n") + "QTY+47:1020:EA\'\n") + "ALI+US\'\n") + "MOA+203:1202.58\'\n") + "PRI+INV:1.179\'\n") + "LIN+2++157871:IN\'\n") + "IMD+F++:::DIFFERENT WIDGET\'\n") + "QTY+47:20:EA\'\n") + "ALI+JP\'\n") + "MOA+203:410\'\n") + "PRI+INV:20.5\'\n") + "UNS+S\'\n") + "MOA+39:2137.58\'\n") + "ALC+C+ABG\'\n") + "MOA+8:525\'\n") + "UNT+23+00000000000117\'\n") + "UNZ+1+00000000000778'");

    private static final Logger LOG = LoggerFactory.getLogger(AS2MessageTest.class);

    private static final String METHOD = "POST";

    private static final String TARGET_HOST = "localhost";

    private static final int TARGET_PORT = AvailablePortFinder.getNextAvailable(8080);

    private static final String RECIPIENT_DELIVERY_ADDRESS = ("http://localhost:" + (AS2MessageTest.TARGET_PORT)) + "/handle-receipts";

    private static final String AS2_VERSION = "1.1";

    private static final String USER_AGENT = "Camel AS2 Endpoint";

    private static final String REQUEST_URI = "/";

    private static final String AS2_NAME = "878051556";

    private static final String SUBJECT = "Test Case";

    private static final String FROM = "mrAS@example.org";

    private static final String CLIENT_FQDN = "client.example.org";

    private static final String SERVER_FQDN = "server.example.org";

    private static final String REPORTING_UA = "Server Responding with MDN";

    private static final String DISPOSITION_NOTIFICATION_TO = "mrAS@example.org";

    private static final String DISPOSITION_NOTIFICATION_OPTIONS = "signed-receipt-protocol=optional,pkcs7-signature; signed-receipt-micalg=optional,sha1";

    private static final String[] SIGNED_RECEIPT_MIC_ALGORITHMS = new String[]{ "sha1", "md5" };

    private static final HttpDateGenerator DATE_GENERATOR = new HttpDateGenerator();

    private static AS2ServerConnection testServer;

    private static KeyPair issueKP;

    private static X509Certificate issueCert;

    private static KeyPair signingKP;

    private static X509Certificate signingCert;

    private static List<X509Certificate> certList;

    private AS2SignedDataGenerator gen;

    @Test
    public void plainEDIMessageTest() throws Exception {
        AS2ClientConnection clientConnection = new AS2ClientConnection(AS2MessageTest.AS2_VERSION, AS2MessageTest.USER_AGENT, AS2MessageTest.CLIENT_FQDN, AS2MessageTest.TARGET_HOST, AS2MessageTest.TARGET_PORT);
        AS2ClientManager clientManager = new AS2ClientManager(clientConnection);
        HttpCoreContext httpContext = clientManager.send(AS2MessageTest.EDI_MESSAGE, AS2MessageTest.REQUEST_URI, AS2MessageTest.SUBJECT, AS2MessageTest.FROM, AS2MessageTest.AS2_NAME, AS2MessageTest.AS2_NAME, PLAIN, ContentType.create(APPLICATION_EDIFACT, US_ASCII), null, null, null, null, null, AS2MessageTest.DISPOSITION_NOTIFICATION_TO, AS2MessageTest.SIGNED_RECEIPT_MIC_ALGORITHMS, null, null);
        HttpRequest request = httpContext.getRequest();
        Assert.assertEquals("Unexpected method value", AS2MessageTest.METHOD, request.getRequestLine().getMethod());
        Assert.assertEquals("Unexpected request URI value", AS2MessageTest.REQUEST_URI, request.getRequestLine().getUri());
        Assert.assertEquals("Unexpected HTTP version value", HttpVersion.HTTP_1_1, request.getRequestLine().getProtocolVersion());
        Assert.assertEquals("Unexpected subject value", AS2MessageTest.SUBJECT, request.getFirstHeader(AS2Header.SUBJECT).getValue());
        Assert.assertEquals("Unexpected from value", AS2MessageTest.FROM, request.getFirstHeader(AS2Header.FROM).getValue());
        Assert.assertEquals("Unexpected AS2 version value", AS2MessageTest.AS2_VERSION, request.getFirstHeader(AS2Header.AS2_VERSION).getValue());
        Assert.assertEquals("Unexpected AS2 from value", AS2MessageTest.AS2_NAME, request.getFirstHeader(AS2_FROM).getValue());
        Assert.assertEquals("Unexpected AS2 to value", AS2MessageTest.AS2_NAME, request.getFirstHeader(AS2_TO).getValue());
        Assert.assertTrue("Unexpected message id value", request.getFirstHeader(MESSAGE_ID).getValue().endsWith(((AS2MessageTest.CLIENT_FQDN) + ">")));
        Assert.assertEquals("Unexpected target host value", (((AS2MessageTest.TARGET_HOST) + ":") + (AS2MessageTest.TARGET_PORT)), request.getFirstHeader(AS2Header.TARGET_HOST).getValue());
        Assert.assertEquals("Unexpected user agent value", AS2MessageTest.USER_AGENT, request.getFirstHeader(AS2Header.USER_AGENT).getValue());
        Assert.assertNotNull("Date value missing", request.getFirstHeader(DATE));
        Assert.assertNotNull("Content length value missing", request.getFirstHeader(CONTENT_LENGTH));
        Assert.assertTrue("Unexpected content type for message", request.getFirstHeader(CONTENT_TYPE).getValue().startsWith(APPLICATION_EDIFACT));
        Assert.assertTrue("Request does not contain entity", (request instanceof BasicHttpEntityEnclosingRequest));
        HttpEntity entity = ((BasicHttpEntityEnclosingRequest) (request)).getEntity();
        Assert.assertNotNull("Request does not contain entity", entity);
        Assert.assertTrue("Unexpected request entity type", (entity instanceof ApplicationEDIFACTEntity));
        ApplicationEDIFACTEntity ediEntity = ((ApplicationEDIFACTEntity) (entity));
        Assert.assertTrue("Unexpected content type for entity", ediEntity.getContentType().getValue().startsWith(APPLICATION_EDIFACT));
        Assert.assertTrue("Entity not set as main body of request", ediEntity.isMainBody());
    }

    @Test
    public void multipartSignedMessageTest() throws Exception {
        AS2ClientConnection clientConnection = new AS2ClientConnection(AS2MessageTest.AS2_VERSION, AS2MessageTest.USER_AGENT, AS2MessageTest.CLIENT_FQDN, AS2MessageTest.TARGET_HOST, AS2MessageTest.TARGET_PORT);
        AS2ClientManager clientManager = new AS2ClientManager(clientConnection);
        HttpCoreContext httpContext = clientManager.send(AS2MessageTest.EDI_MESSAGE, AS2MessageTest.REQUEST_URI, AS2MessageTest.SUBJECT, AS2MessageTest.FROM, AS2MessageTest.AS2_NAME, AS2MessageTest.AS2_NAME, SIGNED, ContentType.create(APPLICATION_EDIFACT, US_ASCII), null, SHA256WITHRSA, AS2MessageTest.certList.toArray(new Certificate[0]), AS2MessageTest.signingKP.getPrivate(), null, AS2MessageTest.DISPOSITION_NOTIFICATION_TO, AS2MessageTest.SIGNED_RECEIPT_MIC_ALGORITHMS, null, null);
        HttpRequest request = httpContext.getRequest();
        Assert.assertEquals("Unexpected method value", AS2MessageTest.METHOD, request.getRequestLine().getMethod());
        Assert.assertEquals("Unexpected request URI value", AS2MessageTest.REQUEST_URI, request.getRequestLine().getUri());
        Assert.assertEquals("Unexpected HTTP version value", HttpVersion.HTTP_1_1, request.getRequestLine().getProtocolVersion());
        Assert.assertEquals("Unexpected subject value", AS2MessageTest.SUBJECT, request.getFirstHeader(AS2Header.SUBJECT).getValue());
        Assert.assertEquals("Unexpected from value", AS2MessageTest.FROM, request.getFirstHeader(AS2Header.FROM).getValue());
        Assert.assertEquals("Unexpected AS2 version value", AS2MessageTest.AS2_VERSION, request.getFirstHeader(AS2Header.AS2_VERSION).getValue());
        Assert.assertEquals("Unexpected AS2 from value", AS2MessageTest.AS2_NAME, request.getFirstHeader(AS2_FROM).getValue());
        Assert.assertEquals("Unexpected AS2 to value", AS2MessageTest.AS2_NAME, request.getFirstHeader(AS2_TO).getValue());
        Assert.assertTrue("Unexpected message id value", request.getFirstHeader(MESSAGE_ID).getValue().endsWith(((AS2MessageTest.CLIENT_FQDN) + ">")));
        Assert.assertEquals("Unexpected target host value", (((AS2MessageTest.TARGET_HOST) + ":") + (AS2MessageTest.TARGET_PORT)), request.getFirstHeader(AS2Header.TARGET_HOST).getValue());
        Assert.assertEquals("Unexpected user agent value", AS2MessageTest.USER_AGENT, request.getFirstHeader(AS2Header.USER_AGENT).getValue());
        Assert.assertNotNull("Date value missing", request.getFirstHeader(DATE));
        Assert.assertNotNull("Content length value missing", request.getFirstHeader(CONTENT_LENGTH));
        Assert.assertTrue("Unexpected content type for message", request.getFirstHeader(CONTENT_TYPE).getValue().startsWith(MULTIPART_SIGNED));
        Assert.assertTrue("Request does not contain entity", (request instanceof BasicHttpEntityEnclosingRequest));
        HttpEntity entity = ((BasicHttpEntityEnclosingRequest) (request)).getEntity();
        Assert.assertNotNull("Request does not contain entity", entity);
        Assert.assertTrue("Unexpected request entity type", (entity instanceof MultipartSignedEntity));
        MultipartSignedEntity signedEntity = ((MultipartSignedEntity) (entity));
        Assert.assertTrue("Entity not set as main body of request", signedEntity.isMainBody());
        Assert.assertTrue("Request contains invalid number of mime parts", ((signedEntity.getPartCount()) == 2));
        // Validated first mime part.
        Assert.assertTrue("First mime part incorrect type ", ((signedEntity.getPart(0)) instanceof ApplicationEDIFACTEntity));
        ApplicationEDIFACTEntity ediEntity = ((ApplicationEDIFACTEntity) (signedEntity.getPart(0)));
        Assert.assertTrue("Unexpected content type for first mime part", ediEntity.getContentType().getValue().startsWith(APPLICATION_EDIFACT));
        Assert.assertFalse("First mime type set as main body of request", ediEntity.isMainBody());
        // Validate second mime part.
        Assert.assertTrue("Second mime part incorrect type ", ((signedEntity.getPart(1)) instanceof ApplicationPkcs7SignatureEntity));
        ApplicationPkcs7SignatureEntity signatureEntity = ((ApplicationPkcs7SignatureEntity) (signedEntity.getPart(1)));
        Assert.assertTrue("Unexpected content type for second mime part", signatureEntity.getContentType().getValue().startsWith(APPLICATION_PKCS7_SIGNATURE));
        Assert.assertFalse("First mime type set as main body of request", signatureEntity.isMainBody());
    }

    @Test
    public void aes128CbcEnvelopedMessageTest() throws Exception {
        envelopedMessageTest(AES128_CBC);
    }

    @Test
    public void aes192CbcEnvelopedMessageTest() throws Exception {
        envelopedMessageTest(AES192_CBC);
    }

    @Test
    public void aes256CbcEnvelopedMessageTest() throws Exception {
        envelopedMessageTest(AES256_CBC);
    }

    @Test
    public void aes128CcmEnvelopedMessageTest() throws Exception {
        envelopedMessageTest(AES128_CCM);
    }

    @Test
    public void aes192CcmEnvelopedMessageTest() throws Exception {
        envelopedMessageTest(AES192_CCM);
    }

    @Test
    public void aes256CcmEnvelopedMessageTest() throws Exception {
        envelopedMessageTest(AES256_CCM);
    }

    @Test
    public void aes128GcmEnvelopedMessageTest() throws Exception {
        envelopedMessageTest(AES128_GCM);
    }

    @Test
    public void aes192GcmEnvelopedMessageTest() throws Exception {
        envelopedMessageTest(AES192_GCM);
    }

    @Test
    public void aes256GcmEnvelopedMessageTest() throws Exception {
        envelopedMessageTest(AES256_GCM);
    }

    @Test
    public void camellia128CbcEnvelopedMessageTest() throws Exception {
        envelopedMessageTest(CAMELLIA128_CBC);
    }

    @Test
    public void camellia192CbcEnvelopedMessageTest() throws Exception {
        envelopedMessageTest(CAMELLIA192_CBC);
    }

    @Test
    public void camellia256CbcEnvelopedMessageTest() throws Exception {
        envelopedMessageTest(CAMELLIA256_CBC);
    }

    @Test
    public void cast5CbcEnvelopedMessageTest() throws Exception {
        envelopedMessageTest(CAST5_CBC);
    }

    @Test
    public void desCbcEnvelopedMessageTest() throws Exception {
        envelopedMessageTest(DES_CBC);
    }

    @Test
    public void desEde3CbcEnvelopedMessageTest() throws Exception {
        envelopedMessageTest(DES_EDE3_CBC);
    }

    @Test
    public void cost28147GcfbEnvelopedMessageTest() throws Exception {
        envelopedMessageTest(GOST28147_GCFB);
    }

    @Test
    public void ideaCbcEnvelopedMessageTest() throws Exception {
        envelopedMessageTest(IDEA_CBC);
    }

    @Test
    public void rc2CbcEnvelopedMessageTest() throws Exception {
        envelopedMessageTest(RC2_CBC);
    }

    @Test
    public void rc4EnvelopedMessageTest() throws Exception {
        envelopedMessageTest(RC4);
    }

    @Test
    public void seedCbcEnvelopedMessageTest() throws Exception {
        envelopedMessageTest(SEED_CBC);
    }

    @Test
    public void aes128CbcEnvelopedAndSignedMessageTest() throws Exception {
        envelopedAndSignedMessageTest(AES128_CBC);
    }

    @Test
    public void signatureVerificationTest() throws Exception {
        AS2ClientConnection clientConnection = new AS2ClientConnection(AS2MessageTest.AS2_VERSION, AS2MessageTest.USER_AGENT, AS2MessageTest.CLIENT_FQDN, AS2MessageTest.TARGET_HOST, AS2MessageTest.TARGET_PORT);
        AS2ClientManager clientManager = new AS2ClientManager(clientConnection);
        HttpCoreContext httpContext = clientManager.send(AS2MessageTest.EDI_MESSAGE, AS2MessageTest.REQUEST_URI, AS2MessageTest.SUBJECT, AS2MessageTest.FROM, AS2MessageTest.AS2_NAME, AS2MessageTest.AS2_NAME, SIGNED, ContentType.create(APPLICATION_EDIFACT, US_ASCII), null, SHA256WITHRSA, AS2MessageTest.certList.toArray(new Certificate[0]), AS2MessageTest.signingKP.getPrivate(), null, AS2MessageTest.DISPOSITION_NOTIFICATION_TO, AS2MessageTest.SIGNED_RECEIPT_MIC_ALGORITHMS, null, null);
        HttpRequest request = httpContext.getRequest();
        Assert.assertTrue("Request does not contain entity", (request instanceof BasicHttpEntityEnclosingRequest));
        HttpEntity entity = ((BasicHttpEntityEnclosingRequest) (request)).getEntity();
        Assert.assertNotNull("Request does not contain entity", entity);
        Assert.assertTrue("Unexpected request entity type", (entity instanceof MultipartSignedEntity));
        MultipartSignedEntity multipartSignedEntity = ((MultipartSignedEntity) (entity));
        MimeEntity signedEntity = multipartSignedEntity.getSignedDataEntity();
        Assert.assertTrue("Signed entity wrong type", (signedEntity instanceof ApplicationEDIEntity));
        ApplicationEDIEntity ediMessageEntity = ((ApplicationEDIEntity) (signedEntity));
        Assert.assertNotNull("Multipart signed entity does not contain EDI message entity", ediMessageEntity);
        ApplicationPkcs7SignatureEntity signatureEntity = multipartSignedEntity.getSignatureEntity();
        Assert.assertNotNull("Multipart signed entity does not contain signature entity", signatureEntity);
        // Validate Signature
        Assert.assertTrue("Signature is invalid", multipartSignedEntity.isValid());
    }

    @Test
    public void mdnMessageTest() throws Exception {
        AS2ClientConnection clientConnection = new AS2ClientConnection(AS2MessageTest.AS2_VERSION, AS2MessageTest.USER_AGENT, AS2MessageTest.CLIENT_FQDN, AS2MessageTest.TARGET_HOST, AS2MessageTest.TARGET_PORT);
        AS2ClientManager clientManager = new AS2ClientManager(clientConnection);
        HttpCoreContext httpContext = clientManager.send(AS2MessageTest.EDI_MESSAGE, AS2MessageTest.REQUEST_URI, AS2MessageTest.SUBJECT, AS2MessageTest.FROM, AS2MessageTest.AS2_NAME, AS2MessageTest.AS2_NAME, PLAIN, ContentType.create(APPLICATION_EDIFACT, US_ASCII), null, null, null, null, null, AS2MessageTest.DISPOSITION_NOTIFICATION_TO, AS2MessageTest.SIGNED_RECEIPT_MIC_ALGORITHMS, null, null);
        HttpResponse response = httpContext.getResponse();
        Assert.assertEquals("Unexpected method value", HttpVersion.HTTP_1_1, response.getStatusLine().getProtocolVersion());
        Assert.assertEquals("Unexpected method value", HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertEquals("Unexpected method value", EnglishReasonPhraseCatalog.INSTANCE.getReason(200, null), response.getStatusLine().getReasonPhrase());
        HttpEntity responseEntity = response.getEntity();
        Assert.assertNotNull("Response entity", responseEntity);
        Assert.assertTrue("Unexpected response entity type", (responseEntity instanceof MultipartSignedEntity));
        MultipartSignedEntity responseSignedEntity = ((MultipartSignedEntity) (responseEntity));
        MimeEntity responseSignedDataEntity = responseSignedEntity.getSignedDataEntity();
        Assert.assertTrue("Signed entity wrong type", (responseSignedDataEntity instanceof DispositionNotificationMultipartReportEntity));
        DispositionNotificationMultipartReportEntity reportEntity = ((DispositionNotificationMultipartReportEntity) (responseSignedDataEntity));
        Assert.assertEquals("Unexpected number of body parts in report", 2, reportEntity.getPartCount());
        MimeEntity firstPart = reportEntity.getPart(0);
        Assert.assertEquals("Unexpected content type in first body part of report", ContentType.create(TEXT_PLAIN, US_ASCII).toString(), firstPart.getContentTypeValue());
        MimeEntity secondPart = reportEntity.getPart(1);
        Assert.assertEquals("Unexpected content type in second body part of report", ContentType.create(MESSAGE_DISPOSITION_NOTIFICATION, US_ASCII).toString(), secondPart.getContentTypeValue());
        ApplicationPkcs7SignatureEntity signatureEntity = responseSignedEntity.getSignatureEntity();
        Assert.assertNotNull("Signature Entity", signatureEntity);
        // Validate Signature
        Assert.assertTrue("Signature is invalid", responseSignedEntity.isValid());
    }

    @Test
    public void asynchronousMdnMessageTest() throws Exception {
        AS2AsynchronousMDNManager mdnManager = new AS2AsynchronousMDNManager(AS2MessageTest.AS2_VERSION, AS2MessageTest.USER_AGENT, AS2MessageTest.CLIENT_FQDN, AS2MessageTest.certList.toArray(new X509Certificate[0]), AS2MessageTest.signingKP.getPrivate());
        // Create plain edi request message to acknowledge
        ApplicationEDIEntity ediEntity = EntityUtils.createEDIEntity(AS2MessageTest.EDI_MESSAGE, ContentType.create(APPLICATION_EDIFACT, US_ASCII), null, false);
        HttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest("POST", AS2MessageTest.REQUEST_URI);
        HttpMessageUtils.setHeaderValue(request, AS2Header.SUBJECT, AS2MessageTest.SUBJECT);
        String httpdate = AS2MessageTest.DATE_GENERATOR.getCurrentDate();
        HttpMessageUtils.setHeaderValue(request, DATE, httpdate);
        HttpMessageUtils.setHeaderValue(request, AS2_TO, AS2MessageTest.AS2_NAME);
        HttpMessageUtils.setHeaderValue(request, AS2_FROM, AS2MessageTest.AS2_NAME);
        String originalMessageId = AS2Utils.createMessageId(AS2MessageTest.SERVER_FQDN);
        HttpMessageUtils.setHeaderValue(request, MESSAGE_ID, originalMessageId);
        HttpMessageUtils.setHeaderValue(request, AS2Header.DISPOSITION_NOTIFICATION_OPTIONS, AS2MessageTest.DISPOSITION_NOTIFICATION_OPTIONS);
        EntityUtils.setMessageEntity(request, ediEntity);
        // Create response for MDN creation.
        HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
        httpdate = AS2MessageTest.DATE_GENERATOR.getCurrentDate();
        response.setHeader(DATE, httpdate);
        response.setHeader(SERVER, AS2MessageTest.REPORTING_UA);
        // Create a receipt for edi message
        Map<String, String> extensionFields = new HashMap<>();
        extensionFields.put("Original-Recipient", ("rfc822;" + (AS2MessageTest.AS2_NAME)));
        AS2DispositionModifier dispositionModifier = AS2DispositionModifier.createWarning("AS2 is cool!");
        String[] failureFields = new String[]{ "failure-field-1" };
        String[] errorFields = new String[]{ "error-field-1" };
        String[] warningFields = new String[]{ "warning-field-1" };
        DispositionNotificationMultipartReportEntity mdn = new DispositionNotificationMultipartReportEntity(request, response, DispositionMode.AUTOMATIC_ACTION_MDN_SENT_AUTOMATICALLY, AS2DispositionType.PROCESSED, dispositionModifier, failureFields, errorFields, warningFields, extensionFields, null, "boundary", true, null, "Got ya message!");
        // Send MDN
        HttpCoreContext httpContext = mdnManager.send(mdn, AS2MessageTest.RECIPIENT_DELIVERY_ADDRESS);
        HttpRequest mndRequest = httpContext.getRequest();
        DispositionNotificationMultipartReportEntity reportEntity = HttpMessageUtils.getEntity(mndRequest, DispositionNotificationMultipartReportEntity.class);
        Assert.assertNotNull("Request does not contain resport", reportEntity);
        Assert.assertEquals("Report entity contains invalid number of parts", 2, reportEntity.getPartCount());
        Assert.assertTrue("Report first part is not text entity", ((reportEntity.getPart(0)) instanceof TextPlainEntity));
        Assert.assertTrue("Report second part is not MDN entity", ((reportEntity.getPart(1)) instanceof AS2MessageDispositionNotificationEntity));
        AS2MessageDispositionNotificationEntity mdnEntity = ((AS2MessageDispositionNotificationEntity) (reportEntity.getPart(1)));
        Assert.assertEquals("Unexpected value for Reporting UA", AS2MessageTest.REPORTING_UA, mdnEntity.getReportingUA());
        Assert.assertEquals("Unexpected value for Final Recipient", AS2MessageTest.AS2_NAME, mdnEntity.getFinalRecipient());
        Assert.assertEquals("Unexpected value for Original Message ID", originalMessageId, mdnEntity.getOriginalMessageId());
        Assert.assertEquals("Unexpected value for Disposition Mode", AUTOMATIC_ACTION_MDN_SENT_AUTOMATICALLY, mdnEntity.getDispositionMode());
        Assert.assertEquals("Unexpected value for Disposition Type", PROCESSED, mdnEntity.getDispositionType());
        Assert.assertEquals("Unexpected value for Disposition Modifier", dispositionModifier, mdnEntity.getDispositionModifier());
        Assert.assertArrayEquals("Unexpected value for Failure Fields", failureFields, mdnEntity.getFailureFields());
        Assert.assertArrayEquals("Unexpected value for Error Fields", errorFields, mdnEntity.getErrorFields());
        Assert.assertArrayEquals("Unexpected value for Warning Fields", warningFields, mdnEntity.getWarningFields());
        Assert.assertEquals("Unexpected value for Extension Fields", extensionFields, mdnEntity.getExtensionFields());
        ReceivedContentMic expectedMic = MicUtils.createReceivedContentMic(request, null);
        ReceivedContentMic mdnMic = mdnEntity.getReceivedContentMic();
        Assert.assertEquals("Unexpected value for Recieved Content Mic", expectedMic.getEncodedMessageDigest(), mdnMic.getEncodedMessageDigest());
        AS2MessageTest.LOG.debug(("\r\n" + (AS2Utils.printMessage(mndRequest))));
    }

    @Test
    public void compressedMessageTest() throws Exception {
        AS2ClientConnection clientConnection = new AS2ClientConnection(AS2MessageTest.AS2_VERSION, AS2MessageTest.USER_AGENT, AS2MessageTest.CLIENT_FQDN, AS2MessageTest.TARGET_HOST, AS2MessageTest.TARGET_PORT);
        AS2ClientManager clientManager = new AS2ClientManager(clientConnection);
        AS2MessageTest.LOG.info(("Key Algoritm: " + (AS2MessageTest.signingKP.getPrivate().getAlgorithm())));
        HttpCoreContext httpContext = clientManager.send(AS2MessageTest.EDI_MESSAGE, AS2MessageTest.REQUEST_URI, AS2MessageTest.SUBJECT, AS2MessageTest.FROM, AS2MessageTest.AS2_NAME, AS2MessageTest.AS2_NAME, PLAIN_COMPRESSED, ContentType.create(APPLICATION_EDIFACT, US_ASCII), null, null, null, null, ZLIB, AS2MessageTest.DISPOSITION_NOTIFICATION_TO, AS2MessageTest.SIGNED_RECEIPT_MIC_ALGORITHMS, null, null);
        HttpRequest request = httpContext.getRequest();
        Assert.assertEquals("Unexpected method value", AS2MessageTest.METHOD, request.getRequestLine().getMethod());
        Assert.assertEquals("Unexpected request URI value", AS2MessageTest.REQUEST_URI, request.getRequestLine().getUri());
        Assert.assertEquals("Unexpected HTTP version value", HttpVersion.HTTP_1_1, request.getRequestLine().getProtocolVersion());
        Assert.assertEquals("Unexpected subject value", AS2MessageTest.SUBJECT, request.getFirstHeader(AS2Header.SUBJECT).getValue());
        Assert.assertEquals("Unexpected from value", AS2MessageTest.FROM, request.getFirstHeader(AS2Header.FROM).getValue());
        Assert.assertEquals("Unexpected AS2 version value", AS2MessageTest.AS2_VERSION, request.getFirstHeader(AS2Header.AS2_VERSION).getValue());
        Assert.assertEquals("Unexpected AS2 from value", AS2MessageTest.AS2_NAME, request.getFirstHeader(AS2_FROM).getValue());
        Assert.assertEquals("Unexpected AS2 to value", AS2MessageTest.AS2_NAME, request.getFirstHeader(AS2_TO).getValue());
        Assert.assertTrue("Unexpected message id value", request.getFirstHeader(MESSAGE_ID).getValue().endsWith(((AS2MessageTest.CLIENT_FQDN) + ">")));
        Assert.assertEquals("Unexpected target host value", (((AS2MessageTest.TARGET_HOST) + ":") + (AS2MessageTest.TARGET_PORT)), request.getFirstHeader(AS2Header.TARGET_HOST).getValue());
        Assert.assertEquals("Unexpected user agent value", AS2MessageTest.USER_AGENT, request.getFirstHeader(AS2Header.USER_AGENT).getValue());
        Assert.assertNotNull("Date value missing", request.getFirstHeader(DATE));
        Assert.assertNotNull("Content length value missing", request.getFirstHeader(CONTENT_LENGTH));
        Assert.assertTrue("Unexpected content type for message", request.getFirstHeader(CONTENT_TYPE).getValue().startsWith(APPLICATION_PKCS7_MIME));
        Assert.assertTrue("Request does not contain entity", (request instanceof BasicHttpEntityEnclosingRequest));
        HttpEntity entity = ((BasicHttpEntityEnclosingRequest) (request)).getEntity();
        Assert.assertNotNull("Request does not contain entity", entity);
        Assert.assertTrue("Unexpected request entity type", (entity instanceof ApplicationPkcs7MimeCompressedDataEntity));
        ApplicationPkcs7MimeCompressedDataEntity compressedDataEntity = ((ApplicationPkcs7MimeCompressedDataEntity) (entity));
        Assert.assertTrue("Entity not set as main body of request", compressedDataEntity.isMainBody());
        // Validated compessed part.
        MimeEntity compressedEntity = compressedDataEntity.getCompressedEntity(new ZlibExpanderProvider());
        Assert.assertTrue("Enveloped mime part incorrect type ", (compressedEntity instanceof ApplicationEDIFACTEntity));
        ApplicationEDIFACTEntity ediEntity = ((ApplicationEDIFACTEntity) (compressedEntity));
        Assert.assertTrue("Unexpected content type for compressed entity", ediEntity.getContentType().getValue().startsWith(APPLICATION_EDIFACT));
        Assert.assertFalse("Compressed entity set as main body of request", ediEntity.isMainBody());
    }

    @Test
    public void compressedAndSignedMessageTest() throws Exception {
        AS2ClientConnection clientConnection = new AS2ClientConnection(AS2MessageTest.AS2_VERSION, AS2MessageTest.USER_AGENT, AS2MessageTest.CLIENT_FQDN, AS2MessageTest.TARGET_HOST, AS2MessageTest.TARGET_PORT);
        AS2ClientManager clientManager = new AS2ClientManager(clientConnection);
        AS2MessageTest.LOG.info(("Key Algoritm: " + (AS2MessageTest.signingKP.getPrivate().getAlgorithm())));
        HttpCoreContext httpContext = clientManager.send(AS2MessageTest.EDI_MESSAGE, AS2MessageTest.REQUEST_URI, AS2MessageTest.SUBJECT, AS2MessageTest.FROM, AS2MessageTest.AS2_NAME, AS2MessageTest.AS2_NAME, SIGNED_COMPRESSED, ContentType.create(APPLICATION_EDIFACT, US_ASCII), "base64", SHA256WITHRSA, AS2MessageTest.certList.toArray(new Certificate[0]), AS2MessageTest.signingKP.getPrivate(), ZLIB, AS2MessageTest.DISPOSITION_NOTIFICATION_TO, AS2MessageTest.SIGNED_RECEIPT_MIC_ALGORITHMS, null, null);
        HttpRequest request = httpContext.getRequest();
        Assert.assertEquals("Unexpected method value", AS2MessageTest.METHOD, request.getRequestLine().getMethod());
        Assert.assertEquals("Unexpected request URI value", AS2MessageTest.REQUEST_URI, request.getRequestLine().getUri());
        Assert.assertEquals("Unexpected HTTP version value", HttpVersion.HTTP_1_1, request.getRequestLine().getProtocolVersion());
        Assert.assertEquals("Unexpected subject value", AS2MessageTest.SUBJECT, request.getFirstHeader(AS2Header.SUBJECT).getValue());
        Assert.assertEquals("Unexpected from value", AS2MessageTest.FROM, request.getFirstHeader(AS2Header.FROM).getValue());
        Assert.assertEquals("Unexpected AS2 version value", AS2MessageTest.AS2_VERSION, request.getFirstHeader(AS2Header.AS2_VERSION).getValue());
        Assert.assertEquals("Unexpected AS2 from value", AS2MessageTest.AS2_NAME, request.getFirstHeader(AS2_FROM).getValue());
        Assert.assertEquals("Unexpected AS2 to value", AS2MessageTest.AS2_NAME, request.getFirstHeader(AS2_TO).getValue());
        Assert.assertTrue("Unexpected message id value", request.getFirstHeader(MESSAGE_ID).getValue().endsWith(((AS2MessageTest.CLIENT_FQDN) + ">")));
        Assert.assertEquals("Unexpected target host value", (((AS2MessageTest.TARGET_HOST) + ":") + (AS2MessageTest.TARGET_PORT)), request.getFirstHeader(AS2Header.TARGET_HOST).getValue());
        Assert.assertEquals("Unexpected user agent value", AS2MessageTest.USER_AGENT, request.getFirstHeader(AS2Header.USER_AGENT).getValue());
        Assert.assertNotNull("Date value missing", request.getFirstHeader(DATE));
        Assert.assertNotNull("Content length value missing", request.getFirstHeader(CONTENT_LENGTH));
        Assert.assertTrue("Unexpected content type for message", request.getFirstHeader(CONTENT_TYPE).getValue().startsWith(APPLICATION_PKCS7_MIME));
        Assert.assertTrue("Request does not contain entity", (request instanceof BasicHttpEntityEnclosingRequest));
        HttpEntity entity = ((BasicHttpEntityEnclosingRequest) (request)).getEntity();
        Assert.assertNotNull("Request does not contain entity", entity);
        Assert.assertTrue("Unexpected request entity type", (entity instanceof ApplicationPkcs7MimeCompressedDataEntity));
        ApplicationPkcs7MimeCompressedDataEntity compressedDataEntity = ((ApplicationPkcs7MimeCompressedDataEntity) (entity));
        Assert.assertTrue("Entity not set as main body of request", compressedDataEntity.isMainBody());
        // Validated compressed part.
        MimeEntity compressedEntity = compressedDataEntity.getCompressedEntity(new ZlibExpanderProvider());
        Assert.assertTrue("Enveloped mime part incorrect type ", (compressedEntity instanceof MultipartSignedEntity));
        MultipartSignedEntity multipartSignedEntity = ((MultipartSignedEntity) (compressedEntity));
        Assert.assertTrue("Unexpected content type for compressed entity", multipartSignedEntity.getContentType().getValue().startsWith(MULTIPART_SIGNED));
        Assert.assertFalse("Multipart signed entity set as main body of request", multipartSignedEntity.isMainBody());
        Assert.assertTrue("Multipart signed entity contains invalid number of mime parts", ((multipartSignedEntity.getPartCount()) == 2));
        // Validated first mime part.
        Assert.assertTrue("First mime part incorrect type ", ((multipartSignedEntity.getPart(0)) instanceof ApplicationEDIFACTEntity));
        ApplicationEDIFACTEntity ediEntity = ((ApplicationEDIFACTEntity) (multipartSignedEntity.getPart(0)));
        Assert.assertTrue("Unexpected content type for first mime part", ediEntity.getContentType().getValue().startsWith(APPLICATION_EDIFACT));
        Assert.assertFalse("First mime type set as main body of request", ediEntity.isMainBody());
        // Validate second mime part.
        Assert.assertTrue("Second mime part incorrect type ", ((multipartSignedEntity.getPart(1)) instanceof ApplicationPkcs7SignatureEntity));
        ApplicationPkcs7SignatureEntity signatureEntity = ((ApplicationPkcs7SignatureEntity) (multipartSignedEntity.getPart(1)));
        Assert.assertTrue("Unexpected content type for second mime part", signatureEntity.getContentType().getValue().startsWith(APPLICATION_PKCS7_SIGNATURE));
        Assert.assertFalse("First mime type set as main body of request", signatureEntity.isMainBody());
    }

    @Test
    public void envelopedAndCompressedMessageTest() throws Exception {
        AS2ClientConnection clientConnection = new AS2ClientConnection(AS2MessageTest.AS2_VERSION, AS2MessageTest.USER_AGENT, AS2MessageTest.CLIENT_FQDN, AS2MessageTest.TARGET_HOST, AS2MessageTest.TARGET_PORT);
        AS2ClientManager clientManager = new AS2ClientManager(clientConnection);
        AS2MessageTest.LOG.info(("Key Algoritm: " + (AS2MessageTest.signingKP.getPrivate().getAlgorithm())));
        HttpCoreContext httpContext = clientManager.send(AS2MessageTest.EDI_MESSAGE, AS2MessageTest.REQUEST_URI, AS2MessageTest.SUBJECT, AS2MessageTest.FROM, AS2MessageTest.AS2_NAME, AS2MessageTest.AS2_NAME, ENCRYPTED_COMPRESSED, ContentType.create(APPLICATION_EDIFACT, US_ASCII), "base64", null, null, null, ZLIB, AS2MessageTest.DISPOSITION_NOTIFICATION_TO, AS2MessageTest.SIGNED_RECEIPT_MIC_ALGORITHMS, AES128_CBC, AS2MessageTest.certList.toArray(new Certificate[0]));
        HttpRequest request = httpContext.getRequest();
        Assert.assertEquals("Unexpected method value", AS2MessageTest.METHOD, request.getRequestLine().getMethod());
        Assert.assertEquals("Unexpected request URI value", AS2MessageTest.REQUEST_URI, request.getRequestLine().getUri());
        Assert.assertEquals("Unexpected HTTP version value", HttpVersion.HTTP_1_1, request.getRequestLine().getProtocolVersion());
        Assert.assertEquals("Unexpected subject value", AS2MessageTest.SUBJECT, request.getFirstHeader(AS2Header.SUBJECT).getValue());
        Assert.assertEquals("Unexpected from value", AS2MessageTest.FROM, request.getFirstHeader(AS2Header.FROM).getValue());
        Assert.assertEquals("Unexpected AS2 version value", AS2MessageTest.AS2_VERSION, request.getFirstHeader(AS2Header.AS2_VERSION).getValue());
        Assert.assertEquals("Unexpected AS2 from value", AS2MessageTest.AS2_NAME, request.getFirstHeader(AS2_FROM).getValue());
        Assert.assertEquals("Unexpected AS2 to value", AS2MessageTest.AS2_NAME, request.getFirstHeader(AS2_TO).getValue());
        Assert.assertTrue("Unexpected message id value", request.getFirstHeader(MESSAGE_ID).getValue().endsWith(((AS2MessageTest.CLIENT_FQDN) + ">")));
        Assert.assertEquals("Unexpected target host value", (((AS2MessageTest.TARGET_HOST) + ":") + (AS2MessageTest.TARGET_PORT)), request.getFirstHeader(AS2Header.TARGET_HOST).getValue());
        Assert.assertEquals("Unexpected user agent value", AS2MessageTest.USER_AGENT, request.getFirstHeader(AS2Header.USER_AGENT).getValue());
        Assert.assertNotNull("Date value missing", request.getFirstHeader(DATE));
        Assert.assertNotNull("Content length value missing", request.getFirstHeader(CONTENT_LENGTH));
        Assert.assertTrue("Unexpected content type for message", request.getFirstHeader(CONTENT_TYPE).getValue().startsWith(APPLICATION_PKCS7_MIME));
        Assert.assertTrue("Request does not contain entity", (request instanceof BasicHttpEntityEnclosingRequest));
        HttpEntity entity = ((BasicHttpEntityEnclosingRequest) (request)).getEntity();
        Assert.assertNotNull("Request does not contain entity", entity);
        Assert.assertTrue("Unexpected request entity type", (entity instanceof ApplicationPkcs7MimeEnvelopedDataEntity));
        ApplicationPkcs7MimeEnvelopedDataEntity envelopedEntity = ((ApplicationPkcs7MimeEnvelopedDataEntity) (entity));
        Assert.assertTrue("Entity not set as main body of request", envelopedEntity.isMainBody());
        // Validated enveloped part.
        MimeEntity encryptedEntity = envelopedEntity.getEncryptedEntity(AS2MessageTest.signingKP.getPrivate());
        Assert.assertTrue("Enveloped mime part incorrect type ", (encryptedEntity instanceof ApplicationPkcs7MimeCompressedDataEntity));
        ApplicationPkcs7MimeCompressedDataEntity compressedDataEntity = ((ApplicationPkcs7MimeCompressedDataEntity) (encryptedEntity));
        Assert.assertTrue("Unexpected content type for compressed mime part", compressedDataEntity.getContentType().getValue().startsWith(APPLICATION_PKCS7_MIME));
        Assert.assertFalse("Enveloped mime type set as main body of request", compressedDataEntity.isMainBody());
        // Validated compressed part.
        MimeEntity compressedEntity = compressedDataEntity.getCompressedEntity(new ZlibExpanderProvider());
        Assert.assertTrue("Enveloped mime part incorrect type ", (compressedEntity instanceof ApplicationEDIFACTEntity));
        ApplicationEDIFACTEntity ediEntity = ((ApplicationEDIFACTEntity) (compressedEntity));
        Assert.assertTrue("Unexpected content type for compressed entity", ediEntity.getContentType().getValue().startsWith(APPLICATION_EDIFACT));
        Assert.assertFalse("Compressed entity set as main body of request", ediEntity.isMainBody());
        Assert.assertEquals("Unexpected content for enveloped mime part", AS2MessageTest.EDI_MESSAGE.replaceAll("[\n\r]", ""), ediEntity.getEdiMessage().replaceAll("[\n\r]", ""));
    }

    @Test
    public void envelopedCompressedAndSignedMessageTest() throws Exception {
        AS2ClientConnection clientConnection = new AS2ClientConnection(AS2MessageTest.AS2_VERSION, AS2MessageTest.USER_AGENT, AS2MessageTest.CLIENT_FQDN, AS2MessageTest.TARGET_HOST, AS2MessageTest.TARGET_PORT);
        AS2ClientManager clientManager = new AS2ClientManager(clientConnection);
        AS2MessageTest.LOG.info(("Key Algoritm: " + (AS2MessageTest.signingKP.getPrivate().getAlgorithm())));
        HttpCoreContext httpContext = clientManager.send(AS2MessageTest.EDI_MESSAGE, AS2MessageTest.REQUEST_URI, AS2MessageTest.SUBJECT, AS2MessageTest.FROM, AS2MessageTest.AS2_NAME, AS2MessageTest.AS2_NAME, ENCRYPTED_COMPRESSED_SIGNED, ContentType.create(APPLICATION_EDIFACT, US_ASCII), null, SHA256WITHRSA, AS2MessageTest.certList.toArray(new Certificate[0]), AS2MessageTest.signingKP.getPrivate(), ZLIB, AS2MessageTest.DISPOSITION_NOTIFICATION_TO, AS2MessageTest.SIGNED_RECEIPT_MIC_ALGORITHMS, AES128_CBC, AS2MessageTest.certList.toArray(new Certificate[0]));
        HttpRequest request = httpContext.getRequest();
        Assert.assertEquals("Unexpected method value", AS2MessageTest.METHOD, request.getRequestLine().getMethod());
        Assert.assertEquals("Unexpected request URI value", AS2MessageTest.REQUEST_URI, request.getRequestLine().getUri());
        Assert.assertEquals("Unexpected HTTP version value", HttpVersion.HTTP_1_1, request.getRequestLine().getProtocolVersion());
        Assert.assertEquals("Unexpected subject value", AS2MessageTest.SUBJECT, request.getFirstHeader(AS2Header.SUBJECT).getValue());
        Assert.assertEquals("Unexpected from value", AS2MessageTest.FROM, request.getFirstHeader(AS2Header.FROM).getValue());
        Assert.assertEquals("Unexpected AS2 version value", AS2MessageTest.AS2_VERSION, request.getFirstHeader(AS2Header.AS2_VERSION).getValue());
        Assert.assertEquals("Unexpected AS2 from value", AS2MessageTest.AS2_NAME, request.getFirstHeader(AS2_FROM).getValue());
        Assert.assertEquals("Unexpected AS2 to value", AS2MessageTest.AS2_NAME, request.getFirstHeader(AS2_TO).getValue());
        Assert.assertTrue("Unexpected message id value", request.getFirstHeader(MESSAGE_ID).getValue().endsWith(((AS2MessageTest.CLIENT_FQDN) + ">")));
        Assert.assertEquals("Unexpected target host value", (((AS2MessageTest.TARGET_HOST) + ":") + (AS2MessageTest.TARGET_PORT)), request.getFirstHeader(AS2Header.TARGET_HOST).getValue());
        Assert.assertEquals("Unexpected user agent value", AS2MessageTest.USER_AGENT, request.getFirstHeader(AS2Header.USER_AGENT).getValue());
        Assert.assertNotNull("Date value missing", request.getFirstHeader(DATE));
        Assert.assertNotNull("Content length value missing", request.getFirstHeader(CONTENT_LENGTH));
        Assert.assertTrue("Unexpected content type for message", request.getFirstHeader(CONTENT_TYPE).getValue().startsWith(APPLICATION_PKCS7_MIME));
        Assert.assertTrue("Request does not contain entity", (request instanceof BasicHttpEntityEnclosingRequest));
        HttpEntity entity = ((BasicHttpEntityEnclosingRequest) (request)).getEntity();
        Assert.assertNotNull("Request does not contain entity", entity);
        Assert.assertTrue("Unexpected request entity type", (entity instanceof ApplicationPkcs7MimeEnvelopedDataEntity));
        ApplicationPkcs7MimeEnvelopedDataEntity envelopedEntity = ((ApplicationPkcs7MimeEnvelopedDataEntity) (entity));
        Assert.assertTrue("Entity not set as main body of request", envelopedEntity.isMainBody());
        // Validated enveloped part.
        MimeEntity encryptedEntity = envelopedEntity.getEncryptedEntity(AS2MessageTest.signingKP.getPrivate());
        Assert.assertTrue("Enveloped mime part incorrect type ", (encryptedEntity instanceof ApplicationPkcs7MimeCompressedDataEntity));
        ApplicationPkcs7MimeCompressedDataEntity compressedDataEntity = ((ApplicationPkcs7MimeCompressedDataEntity) (encryptedEntity));
        Assert.assertTrue("Unexpected content type for compressed mime part", compressedDataEntity.getContentType().getValue().startsWith(APPLICATION_PKCS7_MIME));
        Assert.assertFalse("Enveloped mime type set as main body of request", compressedDataEntity.isMainBody());
        // Validated compressed part.
        MimeEntity compressedEntity = compressedDataEntity.getCompressedEntity(new ZlibExpanderProvider());
        Assert.assertTrue("Enveloped mime part incorrect type ", (compressedEntity instanceof MultipartSignedEntity));
        MultipartSignedEntity multipartSignedEntity = ((MultipartSignedEntity) (compressedEntity));
        Assert.assertTrue("Unexpected content type for compressed entity", multipartSignedEntity.getContentType().getValue().startsWith(MULTIPART_SIGNED));
        Assert.assertFalse("Multipart signed entity set as main body of request", multipartSignedEntity.isMainBody());
        Assert.assertTrue("Multipart signed entity contains invalid number of mime parts", ((multipartSignedEntity.getPartCount()) == 2));
        // Validated first mime part.
        Assert.assertTrue("First mime part incorrect type ", ((multipartSignedEntity.getPart(0)) instanceof ApplicationEDIFACTEntity));
        ApplicationEDIFACTEntity ediEntity = ((ApplicationEDIFACTEntity) (multipartSignedEntity.getPart(0)));
        Assert.assertTrue("Unexpected content type for first mime part", ediEntity.getContentType().getValue().startsWith(APPLICATION_EDIFACT));
        Assert.assertFalse("First mime type set as main body of request", ediEntity.isMainBody());
        // Validate second mime part.
        Assert.assertTrue("Second mime part incorrect type ", ((multipartSignedEntity.getPart(1)) instanceof ApplicationPkcs7SignatureEntity));
        ApplicationPkcs7SignatureEntity signatureEntity = ((ApplicationPkcs7SignatureEntity) (multipartSignedEntity.getPart(1)));
        Assert.assertTrue("Unexpected content type for second mime part", signatureEntity.getContentType().getValue().startsWith(APPLICATION_PKCS7_SIGNATURE));
        Assert.assertFalse("First mime type set as main body of request", signatureEntity.isMainBody());
    }
}

