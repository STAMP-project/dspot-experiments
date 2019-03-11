package cc.blynk.server.notifications.push;


import cc.blynk.server.notifications.push.enums.Priority;
import cc.blynk.server.notifications.push.ios.IOSGCMMessage;
import cc.blynk.utils.properties.GCMProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.epoll.Epoll;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 26.06.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class GCMWrapperTest {
    private static final AsyncHttpClient client = new org.asynchttpclient.DefaultAsyncHttpClient(new DefaultAsyncHttpClientConfig.Builder().setUserAgent(null).setKeepAlive(true).setUseNativeTransport(Epoll.isAvailable()).build());

    @Mock
    private GCMProperties props;

    @Test
    public void testValidAndroidJson() throws JsonProcessingException {
        Assert.assertEquals("{\"to\":\"to\",\"priority\":\"normal\",\"data\":{\"message\":\"yo!!!\",\"dashId\":1}}", toJson());
    }

    @Test
    public void testValidIOSJson() throws JsonProcessingException {
        IOSGCMMessage iosgcmMessage = new IOSGCMMessage("to", Priority.normal, "yo!!!", 1);
        iosgcmMessage.setTitle("Blynk Notification");
        Assert.assertEquals("{\"to\":\"to\",\"priority\":\"normal\",\"notification\":{\"body\":\"yo!!!\",\"dashId\":1,\"sound\":\"default\",\"title\":\"Blynk Notification\"}}", iosgcmMessage.toJson());
    }
}

