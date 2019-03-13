package cc.blynk.integration.tcp;


import cc.blynk.integration.SingleServerInstancePerTestWithDB;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.Profile;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.widgets.controls.Slider;
import cc.blynk.utils.StringUtils;
import java.util.concurrent.Future;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.Response;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/2/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class CloneWorkFlowTest extends SingleServerInstancePerTestWithDB {
    @Test
    public void testGetNonExistingQR() throws Exception {
        clientPair.appClient.send(("getProjectByCloneCode " + 123));
        clientPair.appClient.verifyResult(TestUtil.serverError(1));
    }

    @Test
    public void getCloneCode() throws Exception {
        clientPair.appClient.send("getCloneCode 1");
        String token = clientPair.appClient.getBody();
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
    }

    @Test
    public void getProjectByCloneCode() throws Exception {
        clientPair.hardwareClient.send("hardware vw 4 4");
        clientPair.hardwareClient.send("hardware vw 44 44");
        clientPair.appClient.send("getCloneCode 1");
        String token = clientPair.appClient.getBody(3);
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        clientPair.appClient.send(("getProjectByCloneCode " + token));
        DashBoard dashBoard = clientPair.appClient.parseDash(4);
        Assert.assertEquals("My Dashboard", dashBoard.name);
        Device device = dashBoard.devices[0];
        Assert.assertEquals(0, device.connectTime);
        Assert.assertEquals(0, device.dataReceivedAt);
        Assert.assertEquals(0, device.disconnectTime);
        Assert.assertEquals(0, device.firstConnectTime);
        Assert.assertNull(device.deviceOtaInfo);
        Assert.assertNull(device.hardwareInfo);
        Slider slider = ((Slider) (dashBoard.getWidgetById(4)));
        Assert.assertNotNull(slider);
        Assert.assertNull(slider.value);
        Assert.assertNotNull(dashBoard.pinsStorage);
        Assert.assertEquals(0, dashBoard.pinsStorage.size());
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(5);
        Assert.assertEquals(1, profile.dashBoards.length);
    }

    @Test
    public void getProjectByCloneCodeNew() throws Exception {
        clientPair.appClient.send("getCloneCode 1");
        String token = clientPair.appClient.getBody();
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        clientPair.appClient.send(((("getProjectByCloneCode " + token) + "\u0000") + "new"));
        DashBoard dashBoard = clientPair.appClient.parseDash(2);
        Assert.assertEquals("My Dashboard", dashBoard.name);
        Device device = dashBoard.devices[0];
        Assert.assertEquals((-1), dashBoard.parentId);
        Assert.assertEquals(2, dashBoard.id);
        Assert.assertEquals(0, device.connectTime);
        Assert.assertEquals(0, device.dataReceivedAt);
        Assert.assertEquals(0, device.disconnectTime);
        Assert.assertEquals(0, device.firstConnectTime);
        Assert.assertNull(device.deviceOtaInfo);
        Assert.assertNull(device.hardwareInfo);
        Assert.assertNotNull(device.token);
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(3);
        Assert.assertEquals(2, profile.dashBoards.length);
    }

    @Test
    public void getProjectByCloneCodeNewFormat() throws Exception {
        clientPair.appClient.send("getCloneCode 1");
        String token = clientPair.appClient.getBody();
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        clientPair.appClient.send(((("getProjectByCloneCode " + token) + (StringUtils.BODY_SEPARATOR_STRING)) + "new"));
        DashBoard dashBoard = clientPair.appClient.parseDash(2);
        Assert.assertEquals("My Dashboard", dashBoard.name);
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(3);
        Assert.assertEquals(2, profile.dashBoards.length);
        Assert.assertEquals(2, profile.dashBoards[1].id);
    }

    @Test
    public void getProjectByCloneCodeViaHttp() throws Exception {
        clientPair.appClient.send("getCloneCode 1");
        String token = clientPair.appClient.getBody();
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        AsyncHttpClient httpclient = new org.asynchttpclient.DefaultAsyncHttpClient(new DefaultAsyncHttpClientConfig.Builder().setUserAgent(null).setKeepAlive(true).build());
        Future<Response> f = httpclient.prepareGet((((("http://localhost:" + (SingleServerInstancePerTestWithDB.properties.getHttpPort())) + "/") + token) + "/clone")).execute();
        Response response = f.get();
        Assert.assertEquals(200, response.getStatusCode());
        String responseBody = response.getResponseBody();
        Assert.assertNotNull(responseBody);
        DashBoard dashBoard = JsonParser.parseDashboard(responseBody, 0);
        Assert.assertEquals("My Dashboard", dashBoard.name);
        httpclient.close();
    }

    @Test
    public void getProjectByNonExistingCloneCodeViaHttp() throws Exception {
        AsyncHttpClient httpclient = new org.asynchttpclient.DefaultAsyncHttpClient(new DefaultAsyncHttpClientConfig.Builder().setUserAgent(null).setKeepAlive(true).build());
        Future<Response> f = httpclient.prepareGet((((("http://localhost:" + (SingleServerInstancePerTestWithDB.properties.getHttpPort())) + "/") + 123) + "/clone")).execute();
        Response response = f.get();
        Assert.assertEquals(500, response.getStatusCode());
        String responseBody = response.getResponseBody();
        Assert.assertEquals("Requested QR not found.", responseBody);
        httpclient.close();
    }
}

