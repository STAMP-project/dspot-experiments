package hudson.slaves;


import ChannelPinger.PING_INTERVAL_SECONDS_DEFAULT;
import ChannelPinger.PING_TIMEOUT_SECONDS_DEFAULT;
import com.google.common.testing.EqualsTester;
import hudson.remoting.Channel;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static ChannelPinger.PING_INTERVAL_SECONDS_DEFAULT;
import static ChannelPinger.PING_TIMEOUT_SECONDS_DEFAULT;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ ChannelPinger.class })
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*" })
public class ChannelPingerTest {
    @Mock
    private Channel mockChannel;

    private Map<String, String> savedSystemProperties = new HashMap<String, String>();

    @Test
    public void testDefaults() throws Exception {
        ChannelPinger channelPinger = new ChannelPinger();
        channelPinger.install(mockChannel, null);
        Mockito.verify(mockChannel).call(ArgumentMatchers.eq(new ChannelPinger.SetUpRemotePing(PING_TIMEOUT_SECONDS_DEFAULT, PING_INTERVAL_SECONDS_DEFAULT)));
        verifyStatic(ChannelPinger.class);
        ChannelPinger.setUpPingForChannel(mockChannel, null, PING_TIMEOUT_SECONDS_DEFAULT, PING_INTERVAL_SECONDS_DEFAULT, true);
    }

    @Test
    public void testFromSystemProperties() throws Exception {
        System.setProperty("hudson.slaves.ChannelPinger.pingTimeoutSeconds", "42");
        System.setProperty("hudson.slaves.ChannelPinger.pingIntervalSeconds", "73");
        ChannelPinger channelPinger = new ChannelPinger();
        channelPinger.install(mockChannel, null);
        Mockito.verify(mockChannel).call(new ChannelPinger.SetUpRemotePing(42, 73));
        verifyStatic(ChannelPinger.class);
        ChannelPinger.setUpPingForChannel(mockChannel, null, 42, 73, true);
    }

    @Test
    public void testFromOldSystemProperty() throws Exception {
        System.setProperty("hudson.slaves.ChannelPinger.pingInterval", "7");
        ChannelPinger channelPinger = new ChannelPinger();
        channelPinger.install(mockChannel, null);
        Mockito.verify(mockChannel).call(ArgumentMatchers.eq(new ChannelPinger.SetUpRemotePing(PING_TIMEOUT_SECONDS_DEFAULT, 420)));
        verifyStatic(ChannelPinger.class);
        ChannelPinger.setUpPingForChannel(mockChannel, null, PING_TIMEOUT_SECONDS_DEFAULT, 420, true);
    }

    @Test
    public void testNewSystemPropertyTrumpsOld() throws Exception {
        System.setProperty("hudson.slaves.ChannelPinger.pingIntervalSeconds", "73");
        System.setProperty("hudson.slaves.ChannelPinger.pingInterval", "7");
        ChannelPinger channelPinger = new ChannelPinger();
        channelPinger.install(mockChannel, null);
        Mockito.verify(mockChannel).call(ArgumentMatchers.eq(new ChannelPinger.SetUpRemotePing(PING_TIMEOUT_SECONDS_DEFAULT, 73)));
        verifyStatic(ChannelPinger.class);
        ChannelPinger.setUpPingForChannel(mockChannel, null, PING_TIMEOUT_SECONDS_DEFAULT, 73, true);
    }

    @Test
    public void testSetUpRemotePingEquality() {
        new EqualsTester().addEqualityGroup(new ChannelPinger.SetUpRemotePing(1, 2), new ChannelPinger.SetUpRemotePing(1, 2)).addEqualityGroup(new ChannelPinger.SetUpRemotePing(2, 3), new ChannelPinger.SetUpRemotePing(2, 3)).testEquals();
    }
}

