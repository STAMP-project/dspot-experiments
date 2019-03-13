package cc.blynk.integration.http;


import HttpHeaderNames.AUTHORIZATION;
import HttpMultipartMode.BROWSER_COMPATIBLE;
import cc.blynk.integration.BaseTest;
import cc.blynk.integration.CounterBase;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.BaseTestHardwareClient;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.server.core.dao.ota.OTAManager;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.servers.BaseServer;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class OTATest extends BaseTest {
    private BaseServer httpServer;

    private BaseServer httpsServer;

    private CloseableHttpClient httpclient;

    private String httpsAdminServerUrl;

    private ClientPair clientPair;

    private byte[] auth;

    @Test
    public void testInitiateOTA() throws Exception {
        HttpGet request = new HttpGet((((httpsAdminServerUrl) + "/ota/start?token=") + (clientPair.token)));
        request.setHeader(AUTHORIZATION.toString(), ("Basic " + (Base64.getEncoder().encodeToString(auth))));
        try (CloseableHttpResponse response = httpclient.execute(request)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            Assert.assertEquals("", TestUtil.consumeText(response));
        }
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(7777, BLYNK_INTERNAL, TestUtil.b("ota http://127.0.0.1:18080/static/ota/firmware_ota.bin"))));
    }

    @Test
    public void testInitiateOTAWithFileName() throws Exception {
        HttpGet request = new HttpGet(((((httpsAdminServerUrl) + "/ota/start?fileName=test.bin") + "&token=") + (clientPair.token)));
        request.setHeader(AUTHORIZATION.toString(), ("Basic " + (Base64.getEncoder().encodeToString(auth))));
        try (CloseableHttpResponse response = httpclient.execute(request)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            Assert.assertEquals("", TestUtil.consumeText(response));
        }
        String expectedResult = "http://127.0.0.1:18080/static/ota/test.bin";
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(7777, BLYNK_INTERNAL, TestUtil.b(("ota " + expectedResult)))));
        request = new HttpGet(((((httpsAdminServerUrl) + "/ota/start?fileName=test.bin") + "&token=") + (clientPair.token)));
        request.setHeader(AUTHORIZATION.toString(), ("Basic " + (Base64.getEncoder().encodeToString(auth))));
        try (CloseableHttpResponse response = httpclient.execute(request)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            Assert.assertEquals("", TestUtil.consumeText(response));
        }
    }

    @Test
    public void testImprovedUploadMethod() throws Exception {
        HttpPost post = new HttpPost((((httpsAdminServerUrl) + "/ota/start?token=") + (clientPair.token)));
        post.setHeader(AUTHORIZATION.toString(), ("Basic " + (Base64.getEncoder().encodeToString(auth))));
        String fileName = "test.bin";
        InputStream binFile = OTATest.class.getResourceAsStream(("/static/ota/" + fileName));
        ContentBody fileBody = new org.apache.http.entity.mime.content.InputStreamBody(binFile, ContentType.APPLICATION_OCTET_STREAM, fileName);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(BROWSER_COMPATIBLE);
        builder.addPart("upfile", fileBody);
        HttpEntity entity = builder.build();
        post.setEntity(entity);
        String path;
        try (CloseableHttpResponse response = httpclient.execute(post)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            path = TestUtil.consumeText(response);
            Assert.assertNotNull(path);
            Assert.assertTrue(path.startsWith("/static"));
            Assert.assertTrue(path.endsWith("bin"));
        }
        String responseUrl = "http://127.0.0.1:18080" + path;
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.internal(7777, ("ota " + responseUrl))));
        HttpGet index = new HttpGet((("http://localhost:" + (BaseTest.properties.getHttpPort())) + path));
        try (CloseableHttpResponse response = httpclient.execute(index)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            Assert.assertEquals("application/octet-stream", response.getHeaders("Content-Type")[0].getValue());
        }
    }

    @Test
    public void testOTAFailedWhenNoDevice() throws Exception {
        stop();
        HttpPost post = new HttpPost((((httpsAdminServerUrl) + "/ota/start?token=") + (clientPair.token)));
        post.setHeader(AUTHORIZATION.toString(), ("Basic " + (Base64.getEncoder().encodeToString(auth))));
        String fileName = "test.bin";
        InputStream binFile = OTATest.class.getResourceAsStream(("/static/ota/" + fileName));
        ContentBody fileBody = new org.apache.http.entity.mime.content.InputStreamBody(binFile, ContentType.APPLICATION_OCTET_STREAM, fileName);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(BROWSER_COMPATIBLE);
        builder.addPart("upfile", fileBody);
        HttpEntity entity = builder.build();
        post.setEntity(entity);
        try (CloseableHttpResponse response = httpclient.execute(post)) {
            Assert.assertEquals(400, response.getStatusLine().getStatusCode());
            String error = TestUtil.consumeText(response);
            Assert.assertNotNull(error);
            Assert.assertEquals("No device in session.", error);
        }
    }

    @Test
    public void testOTAWrongToken() throws Exception {
        HttpPost post = new HttpPost((((httpsAdminServerUrl) + "/ota/start?token=") + 123));
        post.setHeader(AUTHORIZATION.toString(), ("Basic " + (Base64.getEncoder().encodeToString(auth))));
        String fileName = "test.bin";
        InputStream binFile = OTATest.class.getResourceAsStream(("/static/ota/" + fileName));
        ContentBody fileBody = new org.apache.http.entity.mime.content.InputStreamBody(binFile, ContentType.APPLICATION_OCTET_STREAM, fileName);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(BROWSER_COMPATIBLE);
        builder.addPart("upfile", fileBody);
        HttpEntity entity = builder.build();
        post.setEntity(entity);
        try (CloseableHttpResponse response = httpclient.execute(post)) {
            Assert.assertEquals(400, response.getStatusLine().getStatusCode());
            String error = TestUtil.consumeText(response);
            Assert.assertNotNull(error);
            Assert.assertEquals("Invalid token.", error);
        }
    }

    @Test
    public void testAuthorizationFailed() throws Exception {
        HttpPost post = new HttpPost((((httpsAdminServerUrl) + "/ota/start?token=") + 123));
        post.setHeader(AUTHORIZATION.toString(), ("Basic " + (Base64.getEncoder().encodeToString("123:123".getBytes()))));
        String fileName = "test.bin";
        InputStream binFile = OTATest.class.getResourceAsStream(("/static/ota/" + fileName));
        ContentBody fileBody = new org.apache.http.entity.mime.content.InputStreamBody(binFile, ContentType.APPLICATION_OCTET_STREAM, fileName);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(BROWSER_COMPATIBLE);
        builder.addPart("upfile", fileBody);
        HttpEntity entity = builder.build();
        post.setEntity(entity);
        try (CloseableHttpResponse response = httpclient.execute(post)) {
            Assert.assertEquals(403, response.getStatusLine().getStatusCode());
            String error = TestUtil.consumeText(response);
            Assert.assertNotNull(error);
            Assert.assertEquals("Authentication failed.", error);
        }
    }

    @Test
    public void testImprovedUploadMethodAndCheckOTAStatusForDeviceThatNeverWasOnline() throws Exception {
        HttpPost post = new HttpPost((((httpsAdminServerUrl) + "/ota/start?token=") + (clientPair.token)));
        post.setHeader(AUTHORIZATION.toString(), ("Basic " + (Base64.getEncoder().encodeToString(auth))));
        String fileName = "test.bin";
        InputStream binFile = OTATest.class.getResourceAsStream(("/static/ota/" + fileName));
        ContentBody fileBody = new org.apache.http.entity.mime.content.InputStreamBody(binFile, ContentType.APPLICATION_OCTET_STREAM, fileName);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(BROWSER_COMPATIBLE);
        builder.addPart("upfile", fileBody);
        HttpEntity entity = builder.build();
        post.setEntity(entity);
        String path;
        try (CloseableHttpResponse response = httpclient.execute(post)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            path = TestUtil.consumeText(response);
            Assert.assertNotNull(path);
            Assert.assertTrue(path.startsWith("/static"));
            Assert.assertTrue(path.endsWith("bin"));
        }
        String responseUrl = "http://127.0.0.1:18080" + path;
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.internal(7777, ("ota " + responseUrl))));
        clientPair.appClient.getDevice(1, 0);
        Device device = clientPair.appClient.parseDevice(1);
        Assert.assertNotNull(device);
        Assert.assertEquals("admin@blynk.cc", device.deviceOtaInfo.otaInitiatedBy);
        Assert.assertEquals(System.currentTimeMillis(), device.deviceOtaInfo.otaInitiatedAt, 5000);
        Assert.assertEquals(System.currentTimeMillis(), device.deviceOtaInfo.otaUpdateAt, 5000);
        clientPair.hardwareClient.send(("internal " + (TestUtil.b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"))));
        Assert.assertEquals("admin@blynk.cc", device.deviceOtaInfo.otaInitiatedBy);
        Assert.assertEquals(System.currentTimeMillis(), device.deviceOtaInfo.otaInitiatedAt, 5000);
        Assert.assertEquals(System.currentTimeMillis(), device.deviceOtaInfo.otaUpdateAt, 5000);
    }

    @Test
    public void testImprovedUploadMethodAndCheckOTAStatusForDeviceThatWasOnline() throws Exception {
        clientPair.hardwareClient.send(("internal " + (TestUtil.b("ver 0.3.1 fm 0.3.3 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"))));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
        HttpPost post = new HttpPost((((httpsAdminServerUrl) + "/ota/start?token=") + (clientPair.token)));
        post.setHeader(AUTHORIZATION.toString(), ("Basic " + (Base64.getEncoder().encodeToString(auth))));
        String fileName = "test.bin";
        InputStream binFile = OTATest.class.getResourceAsStream(("/static/ota/" + fileName));
        ContentBody fileBody = new org.apache.http.entity.mime.content.InputStreamBody(binFile, ContentType.APPLICATION_OCTET_STREAM, fileName);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(BROWSER_COMPATIBLE);
        builder.addPart("upfile", fileBody);
        HttpEntity entity = builder.build();
        post.setEntity(entity);
        String path;
        try (CloseableHttpResponse response = httpclient.execute(post)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            path = TestUtil.consumeText(response);
            Assert.assertNotNull(path);
            Assert.assertTrue(path.startsWith("/static"));
            Assert.assertTrue(path.endsWith("bin"));
        }
        String responseUrl = "http://127.0.0.1:18080" + path;
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.internal(7777, ("ota " + responseUrl))));
        clientPair.appClient.getDevice(1, 0);
        Device device = clientPair.appClient.parseDevice(1);
        Assert.assertNotNull(device);
        Assert.assertEquals("0.3.1", device.hardwareInfo.blynkVersion);
        Assert.assertEquals(10, device.hardwareInfo.heartbeatInterval);
        Assert.assertEquals("111", device.hardwareInfo.build);
        Assert.assertEquals("admin@blynk.cc", device.deviceOtaInfo.otaInitiatedBy);
        Assert.assertEquals(System.currentTimeMillis(), device.deviceOtaInfo.otaInitiatedAt, 5000);
        Assert.assertEquals(System.currentTimeMillis(), device.deviceOtaInfo.otaUpdateAt, 5000);
        clientPair.hardwareClient.send(("internal " + (TestUtil.b("ver 0.3.1 fm 0.3.3 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 112"))));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.getDevice(1, 0);
        device = clientPair.appClient.parseDevice(2);
        Assert.assertNotNull(device);
        Assert.assertEquals("0.3.1", device.hardwareInfo.blynkVersion);
        Assert.assertEquals(10, device.hardwareInfo.heartbeatInterval);
        Assert.assertEquals("112", device.hardwareInfo.build);
        Assert.assertEquals("admin@blynk.cc", device.deviceOtaInfo.otaInitiatedBy);
        Assert.assertEquals(System.currentTimeMillis(), device.deviceOtaInfo.otaInitiatedAt, 5000);
        Assert.assertEquals(System.currentTimeMillis(), device.deviceOtaInfo.otaUpdateAt, 5000);
    }

    @Test
    public void takeBuildDateFromBinaryFile() {
        String fileName = "test.bin";
        Path path = new File(("src/test/resources/static/ota/" + fileName)).toPath();
        Assert.assertEquals("Aug 14 2017 20:31:49", OTAManager.getBuildPatternFromString(path));
    }

    @Test
    public void basicOTAForAllDevices() throws Exception {
        HttpPost post = new HttpPost(((httpsAdminServerUrl) + "/ota/start"));
        post.setHeader(AUTHORIZATION.toString(), ("Basic " + (Base64.getEncoder().encodeToString(auth))));
        String fileName = "test.bin";
        InputStream binFile = OTATest.class.getResourceAsStream(("/static/ota/" + fileName));
        ContentBody fileBody = new org.apache.http.entity.mime.content.InputStreamBody(binFile, ContentType.APPLICATION_OCTET_STREAM, fileName);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(BROWSER_COMPATIBLE);
        builder.addPart("upfile", fileBody);
        HttpEntity entity = builder.build();
        post.setEntity(entity);
        String path;
        try (CloseableHttpResponse response = httpclient.execute(post)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            path = TestUtil.consumeText(response);
            Assert.assertNotNull(path);
            Assert.assertTrue(path.startsWith("/static"));
            Assert.assertTrue(path.endsWith("bin"));
        }
        String responseUrl = "http://127.0.0.1:18080" + path;
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.after(500).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.internal(7777, ("ota " + responseUrl))));
        HttpGet index = new HttpGet((("http://localhost:" + (BaseTest.properties.getHttpPort())) + path));
        try (CloseableHttpResponse response = httpclient.execute(index)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            Assert.assertEquals("application/octet-stream", response.getHeaders("Content-Type")[0].getValue());
        }
    }

    @Test
    public void testConnectedDeviceGotOTACommand() throws Exception {
        HttpPost post = new HttpPost(((httpsAdminServerUrl) + "/ota/start"));
        post.setHeader(AUTHORIZATION.toString(), ("Basic " + (Base64.getEncoder().encodeToString(auth))));
        String fileName = "test.bin";
        InputStream binFile = OTATest.class.getResourceAsStream(("/static/ota/" + fileName));
        ContentBody fileBody = new org.apache.http.entity.mime.content.InputStreamBody(binFile, ContentType.APPLICATION_OCTET_STREAM, fileName);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(BROWSER_COMPATIBLE);
        builder.addPart("upfile", fileBody);
        HttpEntity entity = builder.build();
        post.setEntity(entity);
        String path;
        try (CloseableHttpResponse response = httpclient.execute(post)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            path = TestUtil.consumeText(response);
        }
        String responseUrl = "http://127.0.0.1:18080" + path;
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.after(500).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.internal(7777, ("ota " + responseUrl))));
        clientPair.hardwareClient.send(("internal " + (TestUtil.b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 123"))));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.internal(7777, ("ota " + responseUrl))));
        clientPair.hardwareClient.reset();
        clientPair.appClient.getDevice(1, 0);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.deviceOtaInfo);
        Assert.assertEquals("admin@blynk.cc", device.deviceOtaInfo.otaInitiatedBy);
        Assert.assertEquals(System.currentTimeMillis(), device.deviceOtaInfo.otaInitiatedAt, 5000);
        Assert.assertEquals(System.currentTimeMillis(), device.deviceOtaInfo.otaInitiatedAt, 5000);
        Assert.assertNotEquals(device.deviceOtaInfo.otaInitiatedAt, device.deviceOtaInfo.otaUpdateAt);
        Assert.assertEquals("123", device.hardwareInfo.build);
        clientPair.hardwareClient.send((("internal " + (TestUtil.b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build "))) + "Aug 14 2017 20:31:49"));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.after(500).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.internal(7777, ("ota " + responseUrl))));
    }

    @Test
    public void testStopOTA() throws Exception {
        HttpPost post = new HttpPost(((httpsAdminServerUrl) + "/ota/start"));
        post.setHeader(AUTHORIZATION.toString(), ("Basic " + (Base64.getEncoder().encodeToString(auth))));
        String fileName = "test.bin";
        InputStream binFile = OTATest.class.getResourceAsStream(("/static/ota/" + fileName));
        ContentBody fileBody = new org.apache.http.entity.mime.content.InputStreamBody(binFile, ContentType.APPLICATION_OCTET_STREAM, fileName);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(BROWSER_COMPATIBLE);
        builder.addPart("upfile", fileBody);
        HttpEntity entity = builder.build();
        post.setEntity(entity);
        String path;
        try (CloseableHttpResponse response = httpclient.execute(post)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            path = TestUtil.consumeText(response);
            Assert.assertNotNull(path);
            Assert.assertTrue(path.startsWith("/static"));
            Assert.assertTrue(path.endsWith("bin"));
        }
        String responseUrl = "http://127.0.0.1:18080" + path;
        HttpGet stopOta = new HttpGet(((httpsAdminServerUrl) + "/ota/stop"));
        stopOta.setHeader(AUTHORIZATION.toString(), ("Basic " + (Base64.getEncoder().encodeToString(auth))));
        try (CloseableHttpResponse response = httpclient.execute(stopOta)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        }
        clientPair.hardwareClient.send(("internal " + (TestUtil.b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"))));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.internal(7777, ("ota " + responseUrl))));
    }

    @Test
    public void basicOTAForNonExistingSingleUser() throws Exception {
        HttpPost post = new HttpPost(((httpsAdminServerUrl) + "/ota/start?user=dimaxxx@mail.ua"));
        post.setHeader(AUTHORIZATION.toString(), ("Basic " + (Base64.getEncoder().encodeToString(auth))));
        String fileName = "test.bin";
        InputStream binFile = OTATest.class.getResourceAsStream(("/static/ota/" + fileName));
        ContentBody fileBody = new org.apache.http.entity.mime.content.InputStreamBody(binFile, ContentType.APPLICATION_OCTET_STREAM, fileName);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(BROWSER_COMPATIBLE);
        builder.addPart("upfile", fileBody);
        HttpEntity entity = builder.build();
        post.setEntity(entity);
        try (CloseableHttpResponse response = httpclient.execute(post)) {
            Assert.assertEquals(400, response.getStatusLine().getStatusCode());
            String er = TestUtil.consumeText(response);
            Assert.assertNotNull(er);
            Assert.assertEquals("Requested user not found.", er);
        }
    }

    @Test
    public void basicOTAForSingleUser() throws Exception {
        HttpPost post = new HttpPost((((httpsAdminServerUrl) + "/ota/start?user=") + (CounterBase.getUserName())));
        post.setHeader(AUTHORIZATION.toString(), ("Basic " + (Base64.getEncoder().encodeToString(auth))));
        String fileName = "test.bin";
        InputStream binFile = OTATest.class.getResourceAsStream(("/static/ota/" + fileName));
        ContentBody fileBody = new org.apache.http.entity.mime.content.InputStreamBody(binFile, ContentType.APPLICATION_OCTET_STREAM, fileName);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(BROWSER_COMPATIBLE);
        builder.addPart("upfile", fileBody);
        HttpEntity entity = builder.build();
        post.setEntity(entity);
        String path;
        try (CloseableHttpResponse response = httpclient.execute(post)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            path = TestUtil.consumeText(response);
            Assert.assertNotNull(path);
            Assert.assertTrue(path.startsWith("/static"));
            Assert.assertTrue(path.endsWith("bin"));
        }
        String responseUrl = "http://127.0.0.1:18080" + path;
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.after(500).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.internal(7777, ("ota " + responseUrl))));
        TestHardClient newHardwareClient = new TestHardClient("localhost", BaseTest.properties.getHttpPort());
        start();
        newHardwareClient.login(clientPair.token);
        Mockito.verify(newHardwareClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        newHardwareClient.reset();
        newHardwareClient.send(("internal " + (TestUtil.b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"))));
        Mockito.verify(newHardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        Mockito.verify(newHardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.internal(7777, ("ota " + responseUrl))));
    }

    @Test
    public void basicOTAForSingleUserAndNonExistingProject() throws Exception {
        HttpPost post = new HttpPost(((((httpsAdminServerUrl) + "/ota/start?user=") + (CounterBase.getUserName())) + "&project=123"));
        post.setHeader(AUTHORIZATION.toString(), ("Basic " + (Base64.getEncoder().encodeToString(auth))));
        String fileName = "test.bin";
        InputStream binFile = OTATest.class.getResourceAsStream(("/static/ota/" + fileName));
        ContentBody fileBody = new org.apache.http.entity.mime.content.InputStreamBody(binFile, ContentType.APPLICATION_OCTET_STREAM, fileName);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(BROWSER_COMPATIBLE);
        builder.addPart("upfile", fileBody);
        HttpEntity entity = builder.build();
        post.setEntity(entity);
        String path;
        try (CloseableHttpResponse response = httpclient.execute(post)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            path = TestUtil.consumeText(response);
            Assert.assertNotNull(path);
            Assert.assertTrue(path.startsWith("/static"));
            Assert.assertTrue(path.endsWith("bin"));
        }
        String responseUrl = "http://127.0.0.1:18080" + path;
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.after(500).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.internal(7777, ("ota " + responseUrl))));
        TestHardClient newHardwareClient = new TestHardClient("localhost", BaseTest.properties.getHttpPort());
        start();
        newHardwareClient.login(clientPair.token);
        Mockito.verify(newHardwareClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        newHardwareClient.reset();
        newHardwareClient.send(("internal " + (TestUtil.b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"))));
        Mockito.verify(newHardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.internal(7777, ("ota " + responseUrl))));
    }

    @Test
    public void basicOTAForSingleUserAndExistingProject() throws Exception {
        HttpPost post = new HttpPost(((((httpsAdminServerUrl) + "/ota/start?user=") + (CounterBase.getUserName())) + "&project=My%20Dashboard"));
        post.setHeader(AUTHORIZATION.toString(), ("Basic " + (Base64.getEncoder().encodeToString(auth))));
        String fileName = "test.bin";
        InputStream binFile = OTATest.class.getResourceAsStream(("/static/ota/" + fileName));
        ContentBody fileBody = new org.apache.http.entity.mime.content.InputStreamBody(binFile, ContentType.APPLICATION_OCTET_STREAM, fileName);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(BROWSER_COMPATIBLE);
        builder.addPart("upfile", fileBody);
        HttpEntity entity = builder.build();
        post.setEntity(entity);
        String path;
        try (CloseableHttpResponse response = httpclient.execute(post)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            path = TestUtil.consumeText(response);
            Assert.assertNotNull(path);
            Assert.assertTrue(path.startsWith("/static"));
            Assert.assertTrue(path.endsWith("bin"));
        }
        String responseUrl = "http://127.0.0.1:18080" + path;
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.after(500).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.internal(7777, ("ota " + responseUrl))));
        TestHardClient newHardwareClient = new TestHardClient("localhost", BaseTest.properties.getHttpPort());
        start();
        newHardwareClient.login(clientPair.token);
        Mockito.verify(newHardwareClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        newHardwareClient.reset();
        newHardwareClient.send(("internal " + (TestUtil.b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"))));
        Mockito.verify(newHardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        Mockito.verify(newHardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.internal(7777, ("ota " + responseUrl))));
    }
}

