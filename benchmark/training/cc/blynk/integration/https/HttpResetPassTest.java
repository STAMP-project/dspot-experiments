package cc.blynk.integration.https;


import cc.blynk.integration.BaseTest;
import cc.blynk.integration.Holder;
import cc.blynk.integration.ServerProperties;
import cc.blynk.integration.TestUtil;
import cc.blynk.server.servers.BaseServer;
import cc.blynk.utils.AppNameUtil;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
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
public class HttpResetPassTest extends BaseTest {
    private static BaseServer httpServer;

    private CloseableHttpClient httpclient;

    private String httpServerUrl;

    @Test
    public void submitResetPasswordRequest() throws Exception {
        String email = "dmitriy@blynk.cc";
        HttpPost resetPassRequest = new HttpPost(((httpServerUrl) + "/resetPassword"));
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("email", email));
        nvps.add(new BasicNameValuePair("appName", AppNameUtil.BLYNK));
        resetPassRequest.setEntity(new UrlEncodedFormEntity(nvps));
        try (CloseableHttpResponse response = httpclient.execute(resetPassRequest)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            String data = TestUtil.consumeText(response);
            Assert.assertNotNull(data);
            Assert.assertEquals("Email was sent.", data);
        }
        String productName = HttpResetPassTest.properties.productName;
        Mockito.verify(holder.mailWrapper).sendHtml(ArgumentMatchers.eq(email), ArgumentMatchers.eq((("Password reset request for the " + productName) + " app.")), ArgumentMatchers.contains("/landing?token="));
        Mockito.verify(holder.mailWrapper).sendHtml(ArgumentMatchers.eq(email), ArgumentMatchers.eq((("Password reset request for the " + productName) + " app.")), ArgumentMatchers.contains((("You recently made a request to reset your password for the " + productName) + " app. To complete the process, click the link below.")));
        Mockito.verify(holder.mailWrapper).sendHtml(ArgumentMatchers.eq(email), ArgumentMatchers.eq((("Password reset request for the " + productName) + " app.")), ArgumentMatchers.contains((("If you did not request a password reset from " + productName) + ", please ignore this message.")));
    }
}

