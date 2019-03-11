package cc.blynk.integration.tcp;


import cc.blynk.integration.SingleServerInstancePerTest;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.BaseTestAppClient;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.server.core.model.Profile;
import io.netty.channel.ChannelFuture;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 28.04.16.
 */
@RunWith(MockitoJUnitRunner.class)
@Ignore("ignored cause requires token to work properly")
public class FacebookLoginTest extends SingleServerInstancePerTest {
    private final String facebookAuthToken = "";

    @Test
    public void testLoginWorksForNewUser() throws Exception {
        String host = "localhost";
        String email = "dima@gmail.com";
        ClientPair clientPair = TestUtil.initAppAndHardPair(host, SingleServerInstancePerTest.properties.getHttpsPort(), SingleServerInstancePerTest.properties.getHttpPort(), email, "1", "user_profile_json.txt", SingleServerInstancePerTest.properties, 10000);
        ChannelFuture channelFuture = stop();
        channelFuture.await();
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.send(((((((((("login " + email) + "\u0000") + (facebookAuthToken)) + "\u0000") + "Android") + "\u0000") + "1.10.4") + "\u0000") + "facebook"));
        Mockito.verify(appClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        String expected = TestUtil.readTestUserProfile();
        appClient.reset();
        appClient.send("loadProfileGzipped");
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        Profile profile = appClient.parseProfile(1);
        profile.dashBoards[0].updatedAt = 0;
        Assert.assertEquals(expected, profile.toString());
    }

    @Test
    public void testFacebookLoginWorksForExistingUser() throws Exception {
        initFacebookAppAndHardPair("localhost", SingleServerInstancePerTest.properties.getHttpsPort(), SingleServerInstancePerTest.properties.getHttpPort(), "dima@gmail.com", facebookAuthToken);
    }
}

