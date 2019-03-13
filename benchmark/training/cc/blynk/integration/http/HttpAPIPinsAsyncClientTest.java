package cc.blynk.integration.http;


import PinType.VIRTUAL;
import cc.blynk.integration.Holder;
import cc.blynk.integration.SingleServerInstancePerTest;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.BaseTestAppClient;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.server.core.model.Profile;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.controls.Button;
import cc.blynk.utils.FileUtils;
import io.netty.handler.codec.http.HttpHeaderNames;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Future;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
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
public class HttpAPIPinsAsyncClientTest extends SingleServerInstancePerTest {
    private static AsyncHttpClient httpclient;

    private static String httpsServerUrl;

    // ----------------------------GET METHODS SECTION
    @Test
    public void testGetWithFakeToken() throws Exception {
        Future<Response> f = HttpAPIPinsAsyncClientTest.httpclient.prepareGet(((HttpAPIPinsAsyncClientTest.httpsServerUrl) + "dsadasddasdasdasdasdasdas/get/d8")).execute();
        Response response = f.get();
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals("Invalid token.", response.getResponseBody());
        Assert.assertEquals("*", response.getHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    public void testGetWithWrongPathToken() throws Exception {
        Future<Response> f = HttpAPIPinsAsyncClientTest.httpclient.prepareGet(((HttpAPIPinsAsyncClientTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/w/d8")).execute();
        Assert.assertEquals(404, f.get().getStatusCode());
    }

    @Test
    public void testGetWithWrongPin() throws Exception {
        Future<Response> f = HttpAPIPinsAsyncClientTest.httpclient.prepareGet(((HttpAPIPinsAsyncClientTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/get/x8")).execute();
        Response response = f.get();
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals("Wrong pin format.", response.getResponseBody());
        Assert.assertEquals("*", response.getHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    public void testGetWithNonExistingPin() throws Exception {
        Future<Response> f = HttpAPIPinsAsyncClientTest.httpclient.prepareGet(((HttpAPIPinsAsyncClientTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/get/v11")).execute();
        Response response = f.get();
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals("Requested pin doesn't exist in the app.", response.getResponseBody());
        Assert.assertEquals("*", response.getHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    public void testPutViaGetRequestSingleValue() throws Exception {
        Future<Response> f = HttpAPIPinsAsyncClientTest.httpclient.prepareGet(((HttpAPIPinsAsyncClientTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/update/v11?value=10")).execute();
        Response response = f.get();
        Assert.assertEquals(200, response.getStatusCode());
        Assert.assertEquals("*", response.getHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN));
        f = HttpAPIPinsAsyncClientTest.httpclient.prepareGet(((HttpAPIPinsAsyncClientTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/get/v11")).execute();
        response = f.get();
        Assert.assertEquals(200, response.getStatusCode());
        List<String> values = TestUtil.consumeJsonPinValues(response.getResponseBody());
        Assert.assertEquals(1, values.size());
        Assert.assertEquals("10", values.get(0));
        Assert.assertEquals("*", response.getHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    public void testPutAndGetTerminalValue() throws Exception {
        Future<Response> f = HttpAPIPinsAsyncClientTest.httpclient.prepareGet(((HttpAPIPinsAsyncClientTest.httpsServerUrl) + "7b0a3a61322e41a5b50589cf52d775d1/get/v17")).execute();
        Response response = f.get();
        Assert.assertEquals(200, response.getStatusCode());
        List<String> values = TestUtil.consumeJsonPinValues(response.getResponseBody());
        Assert.assertEquals(0, values.size());
        f = HttpAPIPinsAsyncClientTest.httpclient.prepareGet(((HttpAPIPinsAsyncClientTest.httpsServerUrl) + "7b0a3a61322e41a5b50589cf52d775d1/update/v17?value=10")).execute();
        response = f.get();
        Assert.assertEquals(200, response.getStatusCode());
        f = HttpAPIPinsAsyncClientTest.httpclient.prepareGet(((HttpAPIPinsAsyncClientTest.httpsServerUrl) + "7b0a3a61322e41a5b50589cf52d775d1/update/v17?value=11")).execute();
        response = f.get();
        Assert.assertEquals(200, response.getStatusCode());
        f = HttpAPIPinsAsyncClientTest.httpclient.prepareGet(((HttpAPIPinsAsyncClientTest.httpsServerUrl) + "7b0a3a61322e41a5b50589cf52d775d1/get/v17")).execute();
        response = f.get();
        Assert.assertEquals(200, response.getStatusCode());
        values = TestUtil.consumeJsonPinValues(response.getResponseBody());
        Assert.assertEquals(2, values.size());
        Assert.assertEquals("10", values.get(0));
        Assert.assertEquals("11", values.get(1));
    }

    @Test
    public void testPutViaGetRequestMultipleValue() throws Exception {
        Future<Response> f = HttpAPIPinsAsyncClientTest.httpclient.prepareGet(((HttpAPIPinsAsyncClientTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/update/v11?value=10&value=11")).execute();
        Response response = f.get();
        Assert.assertEquals(200, response.getStatusCode());
        Assert.assertEquals("*", response.getHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN));
        f = HttpAPIPinsAsyncClientTest.httpclient.prepareGet(((HttpAPIPinsAsyncClientTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/get/v11")).execute();
        response = f.get();
        Assert.assertEquals(200, response.getStatusCode());
        List<String> values = TestUtil.consumeJsonPinValues(response.getResponseBody());
        Assert.assertEquals(2, values.size());
        Assert.assertEquals("10", values.get(0));
        Assert.assertEquals("11", values.get(1));
        Assert.assertEquals("*", response.getHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    public void testPutGetNonExistingPin() throws Exception {
        Future<Response> f = HttpAPIPinsAsyncClientTest.httpclient.preparePut(((HttpAPIPinsAsyncClientTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/update/v10")).setHeader("Content-Type", "application/json").setBody("[\"100\"]").execute();
        Response response = f.get();
        Assert.assertEquals(200, response.getStatusCode());
        Assert.assertEquals("*", response.getHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN));
        f = HttpAPIPinsAsyncClientTest.httpclient.prepareGet(((HttpAPIPinsAsyncClientTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/get/v10")).execute();
        response = f.get();
        Assert.assertEquals(200, response.getStatusCode());
        List<String> values = TestUtil.consumeJsonPinValues(response.getResponseBody());
        Assert.assertEquals(1, values.size());
        Assert.assertEquals("100", values.get(0));
        Assert.assertEquals("*", response.getHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    public void testMultiPutGetNonExistingPin() throws Exception {
        Future<Response> f = HttpAPIPinsAsyncClientTest.httpclient.preparePut(((HttpAPIPinsAsyncClientTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/update/v10")).setHeader("Content-Type", "application/json").setBody("[\"100\", \"101\", \"102\"]").execute();
        Response response = f.get();
        Assert.assertEquals(200, response.getStatusCode());
        Assert.assertEquals("*", response.getHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN));
        f = HttpAPIPinsAsyncClientTest.httpclient.prepareGet(((HttpAPIPinsAsyncClientTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/get/v10")).execute();
        response = f.get();
        Assert.assertEquals(200, response.getStatusCode());
        List<String> values = TestUtil.consumeJsonPinValues(response.getResponseBody());
        Assert.assertEquals(3, values.size());
        Assert.assertEquals("100", values.get(0));
        Assert.assertEquals("101", values.get(1));
        Assert.assertEquals("102", values.get(2));
        Assert.assertEquals("*", response.getHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    public void testGetPinData() throws Exception {
        Future<Response> f = HttpAPIPinsAsyncClientTest.httpclient.prepareGet(((HttpAPIPinsAsyncClientTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/update/v111?value=10")).execute();
        Response response = f.get();
        Assert.assertEquals(200, response.getStatusCode());
        Assert.assertEquals("*", response.getHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN));
        f = HttpAPIPinsAsyncClientTest.httpclient.prepareGet(((HttpAPIPinsAsyncClientTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/data/v111")).execute();
        response = f.get();
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals("No data.", response.getResponseBody());
        Assert.assertEquals("*", response.getHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN));
        f = HttpAPIPinsAsyncClientTest.httpclient.prepareGet(((HttpAPIPinsAsyncClientTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/data/z111")).execute();
        response = f.get();
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals("Wrong pin format.", response.getResponseBody());
        Assert.assertEquals("*", response.getHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    public void testGetCSVDataRedirect() throws Exception {
        Path reportingPath = Paths.get(HttpAPIPinsAsyncClientTest.holder.reportingDiskDao.dataFolder, "dmitriy@blynk.cc");
        Files.createDirectories(reportingPath);
        FileUtils.write(Paths.get(reportingPath.toString(), "history_125564119-0_v10_minute.bin"), 1, 2);
        Future<Response> f = HttpAPIPinsAsyncClientTest.httpclient.prepareGet(((HttpAPIPinsAsyncClientTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/data/v10")).execute();
        Response response = f.get();
        Assert.assertEquals(301, response.getStatusCode());
        String redirectLocation = response.getHeader(HttpHeaderNames.LOCATION);
        Assert.assertNotNull(redirectLocation);
        Assert.assertTrue(redirectLocation.contains("/dmitriy@blynk.cc_125564119_0_v10_"));
        Assert.assertEquals("*", response.getHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertEquals("0", response.getHeader(HttpHeaderNames.CONTENT_LENGTH));
        f = HttpAPIPinsAsyncClientTest.httpclient.prepareGet(((HttpAPIPinsAsyncClientTest.httpsServerUrl) + (redirectLocation.replaceFirst("/", "")))).execute();
        response = f.get();
        Assert.assertEquals(200, response.getStatusCode());
        Assert.assertEquals("application/x-gzip", response.getHeader(HttpHeaderNames.CONTENT_TYPE));
        Assert.assertEquals("*", response.getHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    public void testChangeLabelPropertyViaGet() throws Exception {
        Future<Response> f = HttpAPIPinsAsyncClientTest.httpclient.prepareGet((((HttpAPIPinsAsyncClientTest.httpsServerUrl) + (clientPair.token)) + "/update/v4?label=My-New-Label")).execute();
        Response response = f.get();
        Assert.assertEquals(200, response.getStatusCode());
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.setProperty(111, "1-0 4 label My-New-Label")));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(1);
        Widget widget = profile.dashBoards[0].findWidgetByPin(0, ((short) (4)), VIRTUAL);
        Assert.assertNotNull(widget);
        Assert.assertEquals("My-New-Label", widget.label);
    }

    @Test
    public void testChangeColorPropertyViaGet() throws Exception {
        Future<Response> f = HttpAPIPinsAsyncClientTest.httpclient.prepareGet((((HttpAPIPinsAsyncClientTest.httpsServerUrl) + (clientPair.token)) + "/update/v4?color=%23000000")).execute();
        Response response = f.get();
        Assert.assertEquals(200, response.getStatusCode());
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.setProperty(111, "1-0 4 color #000000")));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(1);
        Widget widget = profile.dashBoards[0].findWidgetByPin(0, ((short) (4)), VIRTUAL);
        Assert.assertNotNull(widget);
        Assert.assertEquals(255, widget.color);
    }

    @Test
    public void testChangeOnLabelPropertyViaGet() throws Exception {
        clientPair.appClient.reset();
        clientPair.appClient.updateWidget(1, "{\"id\":1, \"width\":1, \"height\":1,  \"x\":1, \"y\":1, \"label\":\"Some Text\", \"type\":\"BUTTON\",         \"pinType\":\"VIRTUAL\", \"pin\":2, \"value\":\"1\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        Future<Response> f = HttpAPIPinsAsyncClientTest.httpclient.prepareGet((((HttpAPIPinsAsyncClientTest.httpsServerUrl) + (clientPair.token)) + "/update/v2?onLabel=newOnButtonLabel")).execute();
        Response response = f.get();
        Assert.assertEquals(200, response.getStatusCode());
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.setProperty(111, "1-0 2 onLabel newOnButtonLabel")));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(1);
        Button button = ((Button) (profile.dashBoards[0].findWidgetByPin(0, ((short) (2)), VIRTUAL)));
        Assert.assertNotNull(button);
        Assert.assertEquals("newOnButtonLabel", button.onLabel);
    }

    @Test
    public void testChangeOffLabelPropertyViaGet() throws Exception {
        clientPair.appClient.reset();
        clientPair.appClient.updateWidget(1, "{\"id\":1, \"width\":1, \"height\":1, \"x\":1, \"y\":1, \"label\":\"Some Text\", \"type\":\"BUTTON\",         \"pinType\":\"VIRTUAL\", \"pin\":1, \"value\":\"1\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        Future<Response> f = HttpAPIPinsAsyncClientTest.httpclient.prepareGet((((HttpAPIPinsAsyncClientTest.httpsServerUrl) + (clientPair.token)) + "/update/v1?offLabel=newOffButtonLabel")).execute();
        Response response = f.get();
        Assert.assertEquals(200, response.getStatusCode());
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.setProperty(111, "1-0 1 offLabel newOffButtonLabel")));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(1);
        Button button = ((Button) (profile.dashBoards[0].findWidgetByPin(0, ((short) (1)), VIRTUAL)));
        Assert.assertNotNull(button);
        Assert.assertEquals("newOffButtonLabel", button.offLabel);
    }
}

