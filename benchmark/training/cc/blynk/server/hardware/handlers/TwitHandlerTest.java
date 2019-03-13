package cc.blynk.server.hardware.handlers;


import Command.TWEET;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.Profile;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.widgets.notifications.Twitter;
import cc.blynk.server.core.protocol.exceptions.QuotaLimitException;
import cc.blynk.server.core.protocol.model.messages.MessageFactory;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.HardwareStateHolder;
import cc.blynk.server.hardware.handlers.hardware.logic.TwitLogic;
import cc.blynk.server.notifications.twitter.TwitterWrapper;
import cc.blynk.utils.properties.ServerProperties;
import io.netty.channel.ChannelHandlerContext;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * The Blynk Project.
 * Created by Andrew Zakordonets.
 * Created on 26.04.15.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class TwitHandlerTest {
    @Mock
    private TwitterWrapper twitterWrapper;

    @Mock
    private ChannelHandlerContext ctx;

    @Mock
    private User user;

    @Mock
    private Profile profile;

    @Mock
    private DashBoard dash;

    @Mock
    private Device device;

    private HardwareStateHolder state;

    @Test(expected = QuotaLimitException.class)
    public void testSendQuotaLimitationException() {
        StringMessage twitMessage = ((StringMessage) (MessageFactory.produce(1, TWEET, "this is a test tweet")));
        TwitLogic tweetHandler = Mockito.spy(new TwitLogic(twitterWrapper, 60));
        state.user.profile = profile;
        Twitter twitter = new Twitter();
        twitter.token = "token";
        twitter.secret = "secret_token";
        Mockito.when(state.user.profile.getDashByIdOrThrow(1)).thenReturn(dash);
        Mockito.when(dash.getTwitterWidget()).thenReturn(twitter);
        dash.isActive = true;
        tweetHandler.messageReceived(ctx, state, twitMessage);
        tweetHandler.messageReceived(ctx, state, twitMessage);
    }

    @Test
    public void testSendQuotaLimitationIsWorking() throws InterruptedException {
        StringMessage twitMessage = ((StringMessage) (MessageFactory.produce(1, TWEET, "this is a test tweet")));
        ServerProperties props = new ServerProperties(Collections.emptyMap());
        props.setProperty("notifications.frequency.user.quota.limit", "1");
        final long defaultQuotaTime = (props.getLongProperty("notifications.frequency.user.quota.limit")) * 1000;
        TwitLogic tweetHandler = Mockito.spy(new TwitLogic(twitterWrapper, 60));
        state.user.profile = profile;
        Twitter twitter = new Twitter();
        twitter.token = "token";
        twitter.secret = "secret_token";
        Mockito.when(state.user.profile.getDashByIdOrThrow(1)).thenReturn(dash);
        Mockito.when(dash.getTwitterWidget()).thenReturn(twitter);
        dash.isActive = true;
        tweetHandler.messageReceived(ctx, state, twitMessage);
        TimeUnit.MILLISECONDS.sleep(defaultQuotaTime);
        tweetHandler.messageReceived(ctx, state, twitMessage);
    }
}

