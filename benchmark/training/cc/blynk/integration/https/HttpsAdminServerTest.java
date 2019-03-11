package cc.blynk.integration.https;


import CookieSpecs.STANDARD;
import cc.blynk.integration.BaseTest;
import cc.blynk.integration.CounterBase;
import cc.blynk.integration.Holder;
import cc.blynk.integration.MyHostVerifier;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.http.ResponseUserEntity;
import cc.blynk.integration.model.tcp.BaseTestAppClient;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.servers.BaseServer;
import cc.blynk.utils.AppNameUtil;
import cc.blynk.utils.SHA256Util;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLContext;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
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
public class HttpsAdminServerTest extends BaseTest {
    private static BaseServer httpServer;

    private BaseServer httpAdminServer;

    private CloseableHttpClient httpclient;

    private String httpsAdminServerUrl;

    private String httpServerUrl;

    private User admin;

    private ClientPair clientPair;

    @Test
    public void testGetOnExistingUser() throws Exception {
        String testUser = "dima@dima.ua";
        HttpPut request = new HttpPut(((((httpsAdminServerUrl) + "/users/") + "xxx/") + testUser));
        request.setEntity(new StringEntity(new ResponseUserEntity("123").toString(), ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = httpclient.execute(request)) {
            Assert.assertEquals(404, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void testGetWrongUrl() throws Exception {
        String testUser = "dima@dima.ua";
        HttpPut request = new HttpPut(((((httpsAdminServerUrl) + "/urs213213/") + "xxx/") + testUser));
        request.setEntity(new StringEntity(new ResponseUserEntity("123").toString(), ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = httpclient.execute(request)) {
            Assert.assertEquals(404, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void adminLoginFlowSupport() throws Exception {
        HttpGet loadLoginPageRequest = new HttpGet(httpsAdminServerUrl);
        try (CloseableHttpResponse response = httpclient.execute(loadLoginPageRequest)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            String loginPage = TestUtil.consumeText(response);
            Assert.assertTrue(loginPage.contains("Use your Admin account to log in"));
        }
        login(admin.email, admin.pass);
        HttpGet loadAdminPage = new HttpGet(httpsAdminServerUrl);
        try (CloseableHttpResponse response = httpclient.execute(loadAdminPage)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            String adminPage = TestUtil.consumeText(response);
            Assert.assertTrue(adminPage.contains("Blynk Administration"));
            Assert.assertTrue(adminPage.contains("admin.js"));
        }
    }

    @Test
    public void adminLoginOnlyForSuperUser() throws Exception {
        String name = "admin@blynk.cc";
        String pass = "admin";
        User admin = new User(name, SHA256Util.makeHash(pass, name), AppNameUtil.BLYNK, "local", "127.0.0.1", false, false);
        holder.userDao.add(admin);
        HttpPost loginRequest = new HttpPost(((httpsAdminServerUrl) + "/login"));
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("email", admin.email));
        nvps.add(new BasicNameValuePair("password", admin.pass));
        loginRequest.setEntity(new UrlEncodedFormEntity(nvps));
        try (CloseableHttpResponse response = httpclient.execute(loginRequest)) {
            Assert.assertEquals(301, response.getStatusLine().getStatusCode());
            Header header = response.getFirstHeader("Location");
            Assert.assertNotNull(header);
            Assert.assertEquals("/admin", header.getValue());
            Header cookieHeader = response.getFirstHeader("set-cookie");
            Assert.assertNull(cookieHeader);
        }
    }

    @Test
    public void testGetUserFromAdminPageNoAccess() throws Exception {
        String testUser = "dmitriy@blynk.cc";
        String appName = "Blynk";
        HttpGet request = new HttpGet((((((httpsAdminServerUrl) + "/users/") + testUser) + "-") + appName));
        try (CloseableHttpResponse response = httpclient.execute(request)) {
            Assert.assertEquals(404, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void testGetUserFromAdminPageNoAccessWithFakeCookie() throws Exception {
        String testUser = "dmitriy@blynk.cc";
        String appName = "Blynk";
        HttpGet request = new HttpGet((((((httpsAdminServerUrl) + "/users/") + testUser) + "-") + appName));
        request.setHeader("Cookie", "session=123");
        try (CloseableHttpResponse response = httpclient.execute(request)) {
            Assert.assertEquals(404, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void testGetUserFromAdminPageNoAccessWithFakeCookie2() throws Exception {
        login(admin.email, admin.pass);
        SSLContext sslcontext = TestUtil.initUnsecuredSSLContext();
        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new MyHostVerifier());
        CloseableHttpClient httpclient2 = HttpClients.custom().setSSLSocketFactory(sslsf).setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(STANDARD).build()).build();
        String testUser = "dmitriy@blynk.cc";
        String appName = "Blynk";
        HttpGet request = new HttpGet((((((httpsAdminServerUrl) + "/users/") + testUser) + "-") + appName));
        request.setHeader("Cookie", "session=123");
        try (CloseableHttpResponse response = httpclient2.execute(request)) {
            Assert.assertEquals(404, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void testGetUserFromAdminPage() throws Exception {
        login(admin.email, admin.pass);
        String testUser = "dmitriy@blynk.cc";
        String appName = "Blynk";
        HttpGet request = new HttpGet((((((httpsAdminServerUrl) + "/users/") + testUser) + "-") + appName));
        try (CloseableHttpResponse response = httpclient.execute(request)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            String jsonProfile = TestUtil.consumeText(response);
            Assert.assertNotNull(jsonProfile);
            User user = JsonParser.readAny(jsonProfile, User.class);
            Assert.assertNotNull(user);
            Assert.assertEquals(testUser, user.email);
            Assert.assertNotNull(user.profile.dashBoards);
            Assert.assertEquals(5, user.profile.dashBoards.length);
        }
    }

    @Test
    public void testChangeUsernameChangesPassToo() throws Exception {
        login(admin.email, admin.pass);
        User user;
        HttpGet getUserRequest = new HttpGet(((httpsAdminServerUrl) + "/users/admin@blynk.cc-Blynk"));
        try (CloseableHttpResponse response = httpclient.execute(getUserRequest)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            String userProfile = TestUtil.consumeText(response);
            Assert.assertNotNull(userProfile);
            user = JsonParser.parseUserFromString(userProfile);
            Assert.assertEquals(admin.email, user.email);
        }
        user.email = "123@blynk.cc";
        // we are no allowed to change username without cahnged password
        HttpPut changeUserNameRequestWrong = new HttpPut(((httpsAdminServerUrl) + "/users/admin@blynk.cc-Blynk"));
        changeUserNameRequestWrong.setEntity(new StringEntity(user.toString(), ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = httpclient.execute(changeUserNameRequestWrong)) {
            Assert.assertEquals(400, response.getStatusLine().getStatusCode());
        }
        user.pass = "123";
        HttpPut changeUserNameRequestCorrect = new HttpPut(((httpsAdminServerUrl) + "/users/admin@blynk.cc-Blynk"));
        changeUserNameRequestCorrect.setEntity(new StringEntity(user.toString(), ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = httpclient.execute(changeUserNameRequestCorrect)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        }
        HttpGet getNonExistingUserRequest = new HttpGet(((httpsAdminServerUrl) + "/users/admin@blynk.cc-Blynk"));
        try (CloseableHttpResponse response = httpclient.execute(getNonExistingUserRequest)) {
            Assert.assertEquals(404, response.getStatusLine().getStatusCode());
        }
        HttpGet getUserRequest2 = new HttpGet(((httpsAdminServerUrl) + "/users/123@blynk.cc-Blynk"));
        try (CloseableHttpResponse response = httpclient.execute(getUserRequest2)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            String userProfile = TestUtil.consumeText(response);
            Assert.assertNotNull(userProfile);
            user = JsonParser.parseUserFromString(userProfile);
            Assert.assertEquals("123@blynk.cc", user.email);
            Assert.assertEquals(SHA256Util.makeHash("123", user.email), user.pass);
        }
    }

    @Test
    public void testUpdateUser() throws Exception {
        login(admin.email, admin.pass);
        clientPair.appClient.deactivate(1);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        User user;
        HttpGet getUserRequest = new HttpGet(((((httpsAdminServerUrl) + "/users/") + (CounterBase.getUserName())) + "-Blynk"));
        try (CloseableHttpResponse response = httpclient.execute(getUserRequest)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            String userProfile = TestUtil.consumeText(response);
            Assert.assertNotNull(userProfile);
            user = JsonParser.parseUserFromString(userProfile);
            Assert.assertEquals(CounterBase.getUserName(), user.email);
        }
        user.energy = 12333;
        HttpPut changeUserNameRequestCorrect = new HttpPut(((((httpsAdminServerUrl) + "/users/") + (CounterBase.getUserName())) + "-Blynk"));
        changeUserNameRequestCorrect.setEntity(new StringEntity(user.toString(), ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = httpclient.execute(changeUserNameRequestCorrect)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        }
        getUserRequest = new HttpGet(((((httpsAdminServerUrl) + "/users/") + (CounterBase.getUserName())) + "-Blynk"));
        try (CloseableHttpResponse response = httpclient.execute(getUserRequest)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            String userProfile = TestUtil.consumeText(response);
            Assert.assertNotNull(userProfile);
            user = JsonParser.parseUserFromString(userProfile);
            Assert.assertEquals(CounterBase.getUserName(), user.email);
            Assert.assertEquals(12333, user.energy);
        }
        TestAppClient appClient = new TestAppClient(BaseTest.properties);
        start();
        appClient.login(CounterBase.getUserName(), "1", "iOS", "1.10.2");
        appClient.verifyResult(TestUtil.ok(1));
        appClient.activate(1);
        appClient.verifyResult(TestUtil.ok(2));
        clientPair.hardwareClient.send("hardware vw 1 112");
        Mockito.verify(appClient.responseMock, Mockito.after(500).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, HARDWARE, TestUtil.b("1 vw 1 112"))));
        appClient.reset();
        appClient.send("getDevices 1");
        Device[] devices = appClient.parseDevices();
        Assert.assertNotNull(devices);
        Assert.assertEquals(1, devices.length);
        TestHardClient hardClient2 = new TestHardClient("localhost", BaseTest.tcpHardPort);
        start();
        hardClient2.login(devices[0].token);
        hardClient2.verifyResult(TestUtil.ok(1));
        hardClient2.send("hardware vw 1 112");
        Mockito.verify(appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, HARDWARE, TestUtil.b("1-0 vw 1 112"))));
    }

    @Test
    public void testGetAdminPage() throws Exception {
        HttpGet request = new HttpGet(httpsAdminServerUrl);
        try (CloseableHttpResponse response = httpclient.execute(request)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void testGetFavIconHttps() throws Exception {
        HttpGet request = new HttpGet(((httpsAdminServerUrl.replace("/admin", "")) + "/favicon.ico"));
        try (CloseableHttpResponse response = httpclient.execute(request)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void getStaticFile() throws Exception {
        HttpGet request = new HttpGet(httpsAdminServerUrl.replace("admin", "static/admin.html"));
        try (CloseableHttpResponse response = httpclient.execute(request)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void getStaticFilePathOperationVulnerability() throws Exception {
        HttpGet request = new HttpGet(httpsAdminServerUrl.replace("admin", "static/../../../../../../../../etc/passwd"));
        try (CloseableHttpResponse response = httpclient.execute(request)) {
            Assert.assertEquals(404, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void getStaticFilePathOperationVulnerability2() throws Exception {
        HttpGet request = new HttpGet(httpsAdminServerUrl.replace("admin", "/static/./..././..././..././..././..././..././..././..././.../etc/passwd"));
        try (CloseableHttpResponse response = httpclient.execute(request)) {
            Assert.assertEquals(404, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void getStaticFilePathOperationVulnerability3() throws Exception {
        HttpGet request = new HttpGet(httpsAdminServerUrl.replace("admin", "static//...//...//...//...//...//...//...//...//.../etc/passwd"));
        try (CloseableHttpResponse response = httpclient.execute(request)) {
            Assert.assertEquals(404, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void testGetFavIconHttp() throws Exception {
        HttpGet request = new HttpGet(((httpServerUrl) + "favicon.ico"));
        try (CloseableHttpResponse response = httpclient.execute(request)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void testAssignNewTokenForNonExistingToken() throws Exception {
        login(admin.email, admin.pass);
        HttpGet request = new HttpGet(((httpsAdminServerUrl) + "/users/token/assign?old=123&new=123"));
        try (CloseableHttpResponse response = httpclient.execute(request)) {
            Assert.assertEquals(400, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void testAssignNewToken() throws Exception {
        login(admin.email, admin.pass);
        HttpGet request = new HttpGet(((httpsAdminServerUrl) + "/users/token/assign?old=4ae3851817194e2596cf1b7103603ef8&new=123"));
        try (CloseableHttpResponse response = httpclient.execute(request)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        }
        HttpPut put = new HttpPut(((httpServerUrl) + "123/update/v10"));
        put.setEntity(new StringEntity("[\"100\"]", ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = httpclient.execute(put)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        }
        HttpGet get = new HttpGet(((httpServerUrl) + "123/get/v10"));
        try (CloseableHttpResponse response = httpclient.execute(get)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = TestUtil.consumeJsonPinValues(response);
            Assert.assertEquals(1, values.size());
            Assert.assertEquals("100", values.get(0));
        }
        request = new HttpGet(((httpsAdminServerUrl) + "/users/token/assign?old=4ae3851817194e2596cf1b7103603ef8&new=124"));
        try (CloseableHttpResponse response = httpclient.execute(request)) {
            Assert.assertEquals(400, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void testForceAssignNewToken() throws Exception {
        login(admin.email, admin.pass);
        HttpGet request = new HttpGet(((httpsAdminServerUrl) + "/users/token/force?email=dmitriy@blynk.cc&app=Blynk&dashId=79780619&deviceId=0&new=123"));
        try (CloseableHttpResponse response = httpclient.execute(request)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        }
        HttpPut put = new HttpPut(((httpServerUrl) + "123/update/v10"));
        put.setEntity(new StringEntity("[\"100\"]", ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = httpclient.execute(put)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        }
        HttpGet get = new HttpGet(((httpServerUrl) + "123/get/v10"));
        try (CloseableHttpResponse response = httpclient.execute(get)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = TestUtil.consumeJsonPinValues(response);
            Assert.assertEquals(1, values.size());
            Assert.assertEquals("100", values.get(0));
        }
    }
}

