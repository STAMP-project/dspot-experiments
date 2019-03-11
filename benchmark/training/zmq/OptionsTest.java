package zmq;


import Mechanisms.NULL;
import Mechanisms.PLAIN;
import SocketType.PUB;
import ZMQ.ZMQ_AFFINITY;
import ZMQ.ZMQ_BACKLOG;
import ZMQ.ZMQ_CONFLATE;
import ZMQ.ZMQ_CURVE_PUBLICKEY;
import ZMQ.ZMQ_CURVE_SECRETKEY;
import ZMQ.ZMQ_CURVE_SERVER;
import ZMQ.ZMQ_CURVE_SERVERKEY;
import ZMQ.ZMQ_GSSAPI_PLAINTEXT;
import ZMQ.ZMQ_GSSAPI_PRINCIPAL;
import ZMQ.ZMQ_GSSAPI_SERVICE_PRINCIPAL;
import ZMQ.ZMQ_HANDSHAKE_IVL;
import ZMQ.ZMQ_HEARTBEAT_IVL;
import ZMQ.ZMQ_HEARTBEAT_TIMEOUT;
import ZMQ.ZMQ_HEARTBEAT_TTL;
import ZMQ.ZMQ_IDENTITY;
import ZMQ.ZMQ_IMMEDIATE;
import ZMQ.ZMQ_IPV6;
import ZMQ.ZMQ_LAST_ENDPOINT;
import ZMQ.ZMQ_LINGER;
import ZMQ.ZMQ_MAXMSGSIZE;
import ZMQ.ZMQ_MECHANISM;
import ZMQ.ZMQ_MSG_ALLOCATOR;
import ZMQ.ZMQ_MULTICAST_HOPS;
import ZMQ.ZMQ_PLAIN_PASSWORD;
import ZMQ.ZMQ_PLAIN_SERVER;
import ZMQ.ZMQ_PLAIN_USERNAME;
import ZMQ.ZMQ_RATE;
import ZMQ.ZMQ_RCVBUF;
import ZMQ.ZMQ_RCVHWM;
import ZMQ.ZMQ_RCVTIMEO;
import ZMQ.ZMQ_RECONNECT_IVL;
import ZMQ.ZMQ_RECONNECT_IVL_MAX;
import ZMQ.ZMQ_RECOVERY_IVL;
import ZMQ.ZMQ_SELECTOR_PROVIDERCHOOSER;
import ZMQ.ZMQ_SNDBUF;
import ZMQ.ZMQ_SNDHWM;
import ZMQ.ZMQ_SNDTIMEO;
import ZMQ.ZMQ_SOCKS_PROXY;
import ZMQ.ZMQ_TCP_KEEPALIVE;
import ZMQ.ZMQ_TOS;
import ZMQ.ZMQ_TYPE;
import ZMQ.ZMQ_ZAP_DOMAIN;
import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.zeromq.SelectorProviderTest;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;
import zmq.io.net.SelectorProviderChooser;
import zmq.msg.MsgAllocatorDirect;
import zmq.msg.MsgAllocatorThreshold;


public class OptionsTest {
    private Options options;

    @Test
    public void testDefaultValues() {
        Assert.assertThat(options.affinity, CoreMatchers.is(0L));
        Assert.assertThat(options.allocator, CoreMatchers.notNullValue());
        Assert.assertThat(options.allocator, CoreMatchers.is(CoreMatchers.instanceOf(MsgAllocatorThreshold.class)));
        Assert.assertThat(options.backlog, CoreMatchers.is(100));
        Assert.assertThat(options.conflate, CoreMatchers.is(false));
    }

    @Test
    public void testAffinity() {
        options.setSocketOpt(ZMQ_AFFINITY, 1000L);
        Assert.assertThat(options.getSocketOpt(ZMQ_AFFINITY), CoreMatchers.is(((Object) (1000L))));
    }

    @Test
    public void testAllocator() {
        options.setSocketOpt(ZMQ_MSG_ALLOCATOR, new MsgAllocatorDirect());
        Assert.assertThat(options.getSocketOpt(ZMQ_MSG_ALLOCATOR), CoreMatchers.is(((Object) (options.allocator))));
    }

    @Test
    public void testBacklog() {
        options.setSocketOpt(ZMQ_BACKLOG, 2000);
        Assert.assertThat(options.getSocketOpt(ZMQ_BACKLOG), CoreMatchers.is(((Object) (2000))));
    }

    @Test
    public void testConflate() {
        options.setSocketOpt(ZMQ_CONFLATE, true);
        Assert.assertThat(options.getSocketOpt(ZMQ_CONFLATE), CoreMatchers.is(((Object) (true))));
    }

    @Test
    public void testRate() {
        options.setSocketOpt(ZMQ_RATE, 10);
        Assert.assertThat(options.getSocketOpt(ZMQ_RATE), CoreMatchers.is(((Object) (10))));
    }

    @Test
    public void testRecoveryIvl() {
        options.setSocketOpt(ZMQ_RECOVERY_IVL, 11);
        Assert.assertThat(options.getSocketOpt(ZMQ_RECOVERY_IVL), CoreMatchers.is(((Object) (11))));
    }

    @Test
    public void testMulticastHops() {
        options.setSocketOpt(ZMQ_MULTICAST_HOPS, 12);
        Assert.assertThat(options.getSocketOpt(ZMQ_MULTICAST_HOPS), CoreMatchers.is(((Object) (12))));
    }

    @Test
    public void testPlainUsername() {
        options.setSocketOpt(ZMQ_CURVE_SERVER, true);
        String username = "username";
        options.setSocketOpt(ZMQ_PLAIN_USERNAME, username);
        Assert.assertThat(options.getSocketOpt(ZMQ_PLAIN_USERNAME), CoreMatchers.is(((Object) (username))));
        Assert.assertThat(options.getSocketOpt(ZMQ_PLAIN_SERVER), CoreMatchers.is(((Object) (false))));
        Assert.assertThat(options.getSocketOpt(ZMQ_MECHANISM), CoreMatchers.is(((Object) (PLAIN))));
    }

    @Test
    public void testPlainPassword() {
        options.setSocketOpt(ZMQ_CURVE_SERVER, true);
        String password = "password";
        options.setSocketOpt(ZMQ_PLAIN_PASSWORD, password);
        Assert.assertThat(options.getSocketOpt(ZMQ_PLAIN_PASSWORD), CoreMatchers.is(((Object) (password))));
        Assert.assertThat(options.getSocketOpt(ZMQ_PLAIN_SERVER), CoreMatchers.is(((Object) (false))));
        Assert.assertThat(options.getSocketOpt(ZMQ_MECHANISM), CoreMatchers.is(((Object) (PLAIN))));
    }

    @Test
    public void testPlainUsernameNull() {
        options.setSocketOpt(ZMQ_CURVE_SERVER, true);
        options.setSocketOpt(ZMQ_PLAIN_USERNAME, null);
        Assert.assertThat(options.getSocketOpt(ZMQ_PLAIN_USERNAME), CoreMatchers.nullValue());
        Assert.assertThat(options.getSocketOpt(ZMQ_PLAIN_SERVER), CoreMatchers.is(((Object) (false))));
        Assert.assertThat(options.getSocketOpt(ZMQ_MECHANISM), CoreMatchers.is(((Object) (NULL))));
    }

    @Test
    public void testPlainPasswordNull() {
        options.setSocketOpt(ZMQ_CURVE_SERVER, true);
        options.setSocketOpt(ZMQ_PLAIN_PASSWORD, null);
        Assert.assertThat(options.getSocketOpt(ZMQ_PLAIN_PASSWORD), CoreMatchers.nullValue());
        Assert.assertThat(options.getSocketOpt(ZMQ_PLAIN_SERVER), CoreMatchers.is(((Object) (false))));
        Assert.assertThat(options.getSocketOpt(ZMQ_MECHANISM), CoreMatchers.is(((Object) (NULL))));
    }

    @Test
    public void testCurvePublicKey() {
        byte[] key = new byte[32];
        Arrays.fill(key, ((byte) (11)));
        options.setSocketOpt(ZMQ_CURVE_PUBLICKEY, key);
        Assert.assertThat(options.getSocketOpt(ZMQ_CURVE_PUBLICKEY), CoreMatchers.is(((Object) (key))));
    }

    @Test
    public void testCurveSecretKey() {
        byte[] key = new byte[32];
        Arrays.fill(key, ((byte) (12)));
        options.setSocketOpt(ZMQ_CURVE_SECRETKEY, key);
        Assert.assertThat(options.getSocketOpt(ZMQ_CURVE_SECRETKEY), CoreMatchers.is(((Object) (key))));
    }

    @Test
    public void testCurveServerKey() {
        byte[] key = new byte[32];
        Arrays.fill(key, ((byte) (14)));
        options.setSocketOpt(ZMQ_CURVE_SERVERKEY, key);
        Assert.assertThat(options.getSocketOpt(ZMQ_CURVE_SERVERKEY), CoreMatchers.is(((Object) (key))));
    }

    @Test
    public void testGssPlaintext() {
        options.setSocketOpt(ZMQ_GSSAPI_PLAINTEXT, true);
        Assert.assertThat(options.getSocketOpt(ZMQ_GSSAPI_PLAINTEXT), CoreMatchers.is(((Object) (true))));
    }

    @Test
    public void testHeartbeatInterval() {
        options.setSocketOpt(ZMQ_HEARTBEAT_IVL, 1000);
        Assert.assertThat(options.getSocketOpt(ZMQ_HEARTBEAT_IVL), CoreMatchers.is(((Object) (1000))));
    }

    @Test
    public void testHeartbeatTimeout() {
        options.setSocketOpt(ZMQ_HEARTBEAT_TIMEOUT, 1001);
        Assert.assertThat(options.getSocketOpt(ZMQ_HEARTBEAT_TIMEOUT), CoreMatchers.is(((Object) (1001))));
    }

    @Test
    public void testHeartbeatTtlRounded() {
        options.setSocketOpt(ZMQ_HEARTBEAT_TTL, 2020);
        Assert.assertThat(options.getSocketOpt(ZMQ_HEARTBEAT_TTL), CoreMatchers.is(((Object) (2000))));
    }

    @Test
    public void testHeartbeatTtlMin() {
        options.setSocketOpt(ZMQ_HEARTBEAT_TTL, (-99));
        Assert.assertThat(options.getSocketOpt(ZMQ_HEARTBEAT_TTL), CoreMatchers.is(((Object) (0))));
    }

    @Test
    public void testHeartbeatTtlRoundedMin() {
        options.setSocketOpt(ZMQ_HEARTBEAT_TTL, 99);
        Assert.assertThat(options.getSocketOpt(ZMQ_HEARTBEAT_TTL), CoreMatchers.is(((Object) (0))));
    }

    @Test
    public void testHeartbeatTtlMax() {
        options.setSocketOpt(ZMQ_HEARTBEAT_TTL, 655399);
        Assert.assertThat(options.getSocketOpt(ZMQ_HEARTBEAT_TTL), CoreMatchers.is(((Object) (655300))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHeartbeatTtlOverflow() {
        options.setSocketOpt(ZMQ_HEARTBEAT_TTL, 655400);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHeartbeatTtlUnderflow() {
        options.setSocketOpt(ZMQ_HEARTBEAT_TTL, (-100));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHeartbeatIvlUnderflow() {
        options.setSocketOpt(ZMQ_HEARTBEAT_IVL, (-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHeartbeatTimeoutUnderflow() {
        options.setSocketOpt(ZMQ_HEARTBEAT_TIMEOUT, (-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHandshakeIvlUnderflow() {
        options.setSocketOpt(ZMQ_HANDSHAKE_IVL, (-1));
    }

    @Test
    public void testSelectorObject() {
        try (ZContext ctx = new ZContext();Socket socket = ctx.createSocket(PUB)) {
            SelectorProviderChooser chooser = new SelectorProviderTest.DefaultSelectorProviderChooser();
            socket.setSelectorChooser(chooser);
            Assert.assertEquals(chooser, socket.getSelectorProviderChooser());
        }
    }

    @Test
    public void testSelectorClass() {
        Options opt = new Options();
        Class<SelectorProviderTest.DefaultSelectorProviderChooser> chooser = SelectorProviderTest.DefaultSelectorProviderChooser.class;
        opt.setSocketOpt(ZMQ_SELECTOR_PROVIDERCHOOSER, chooser);
        Assert.assertTrue(((opt.getSocketOpt(ZMQ_SELECTOR_PROVIDERCHOOSER)) instanceof SelectorProviderChooser));
    }

    @Test
    public void testSelectorClassName() {
        Options opt = new Options();
        Class<SelectorProviderTest.DefaultSelectorProviderChooser> chooser = SelectorProviderTest.DefaultSelectorProviderChooser.class;
        opt.setSocketOpt(ZMQ_SELECTOR_PROVIDERCHOOSER, chooser.getName());
        Assert.assertTrue(((opt.getSocketOpt(ZMQ_SELECTOR_PROVIDERCHOOSER)) instanceof SelectorProviderChooser));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSelectorClassNameFailed() {
        Options opt = new Options();
        opt.setSocketOpt(ZMQ_SELECTOR_PROVIDERCHOOSER, String.class.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSelectorFailed() {
        Options opt = new Options();
        Assert.assertFalse(opt.setSocketOpt(ZMQ_SELECTOR_PROVIDERCHOOSER, ""));
    }

    @Test
    public void testDefaultValue() {
        // assertThat(options.getSocketOpt(ZMQ.ZMQ_DECODER), is((Object)options.decoder));
        // assertThat(options.getSocketOpt(ZMQ.ZMQ_ENCODER), is((Object)options.encoder));
        Assert.assertThat(options.getSocketOpt(ZMQ_GSSAPI_PRINCIPAL), CoreMatchers.is(((Object) (options.gssPrincipal))));
        Assert.assertThat(options.getSocketOpt(ZMQ_GSSAPI_SERVICE_PRINCIPAL), CoreMatchers.is(((Object) (options.gssServicePrincipal))));
        Assert.assertThat(options.getSocketOpt(ZMQ_HANDSHAKE_IVL), CoreMatchers.is(((Object) (options.handshakeIvl))));
        Assert.assertThat(options.getSocketOpt(ZMQ_IDENTITY), CoreMatchers.is(((Object) (options.identity))));
        Assert.assertThat(options.getSocketOpt(ZMQ_IMMEDIATE), CoreMatchers.is(((Object) (options.immediate))));
        // assertThat(options.getSocketOpt(ZMQ.ZMQ_TCP_ACCEPT_FILTER), is((Object)options.ipcAcceptFilters));
        Assert.assertThat(options.getSocketOpt(ZMQ_IPV6), CoreMatchers.is(((Object) (options.ipv6))));
        Assert.assertThat(options.getSocketOpt(ZMQ_LAST_ENDPOINT), CoreMatchers.is(((Object) (options.lastEndpoint))));
        Assert.assertThat(options.getSocketOpt(ZMQ_LINGER), CoreMatchers.is(((Object) (options.linger))));
        Assert.assertThat(options.getSocketOpt(ZMQ_MAXMSGSIZE), CoreMatchers.is(((Object) (options.maxMsgSize))));
        Assert.assertThat(options.getSocketOpt(ZMQ_MECHANISM), CoreMatchers.is(((Object) (options.mechanism))));
        Assert.assertThat(options.getSocketOpt(ZMQ_MULTICAST_HOPS), CoreMatchers.is(((Object) (options.multicastHops))));
        Assert.assertThat(options.getSocketOpt(ZMQ_PLAIN_PASSWORD), CoreMatchers.is(((Object) (options.plainPassword))));
        Assert.assertThat(options.getSocketOpt(ZMQ_PLAIN_USERNAME), CoreMatchers.is(((Object) (options.plainUsername))));
        Assert.assertThat(options.getSocketOpt(ZMQ_RATE), CoreMatchers.is(((Object) (options.rate))));
        Assert.assertThat(options.getSocketOpt(ZMQ_RCVBUF), CoreMatchers.is(((Object) (options.rcvbuf))));
        Assert.assertThat(options.getSocketOpt(ZMQ_RECONNECT_IVL), CoreMatchers.is(((Object) (options.reconnectIvl))));
        Assert.assertThat(options.getSocketOpt(ZMQ_RECONNECT_IVL_MAX), CoreMatchers.is(((Object) (options.reconnectIvlMax))));
        Assert.assertThat(options.getSocketOpt(ZMQ_RECOVERY_IVL), CoreMatchers.is(((Object) (options.recoveryIvl))));
        Assert.assertThat(options.getSocketOpt(ZMQ_RCVHWM), CoreMatchers.is(((Object) (options.recvHwm))));
        Assert.assertThat(options.getSocketOpt(ZMQ_RCVTIMEO), CoreMatchers.is(((Object) (options.recvTimeout))));
        Assert.assertThat(options.getSocketOpt(ZMQ_SNDHWM), CoreMatchers.is(((Object) (options.sendHwm))));
        Assert.assertThat(options.getSocketOpt(ZMQ_SNDTIMEO), CoreMatchers.is(((Object) (options.sendTimeout))));
        Assert.assertThat(options.getSocketOpt(ZMQ_SNDBUF), CoreMatchers.is(((Object) (options.sndbuf))));
        Assert.assertThat(options.getSocketOpt(ZMQ_SOCKS_PROXY), CoreMatchers.is(((Object) (options.socksProxyAddress))));
        // assertThat(options.getSocketOpt(ZMQ.ZMQ_TCP_ACCEPT_FILTER), is((Object)options.tcpAcceptFilters));
        Assert.assertThat(options.getSocketOpt(ZMQ_TCP_KEEPALIVE), CoreMatchers.is(((Object) (options.tcpKeepAlive))));
        // assertThat(options.getSocketOpt(ZMQ.ZMQ_TCP_KEEPALIVE_CNT), is((Object)options.tcpKeepAliveCnt));
        // assertThat(options.getSocketOpt(ZMQ.ZMQ_TCP_KEEPALIVE_IDLE), is((Object)options.tcpKeepAliveIdle));
        // assertThat(options.getSocketOpt(ZMQ.ZMQ_TCP_KEEPALIVE_INTVL), is((Object)options.tcpKeepAliveIntvl));
        Assert.assertThat(options.getSocketOpt(ZMQ_TOS), CoreMatchers.is(((Object) (options.tos))));
        Assert.assertThat(options.getSocketOpt(ZMQ_TYPE), CoreMatchers.is(((Object) (options.type))));
        Assert.assertThat(options.getSocketOpt(ZMQ_ZAP_DOMAIN), CoreMatchers.is(((Object) (options.zapDomain))));
        Assert.assertThat(options.getSocketOpt(ZMQ_HANDSHAKE_IVL), CoreMatchers.is(((Object) (options.handshakeIvl))));
        Assert.assertThat(options.getSocketOpt(ZMQ_HEARTBEAT_IVL), CoreMatchers.is(((Object) (options.heartbeatInterval))));
        Assert.assertThat(options.getSocketOpt(ZMQ_HEARTBEAT_TIMEOUT), CoreMatchers.is(((Object) (options.heartbeatTimeout))));
        Assert.assertThat(options.getSocketOpt(ZMQ_HEARTBEAT_TTL), CoreMatchers.is(((Object) (options.heartbeatTtl))));
        Assert.assertThat(options.getSocketOpt(ZMQ_SELECTOR_PROVIDERCHOOSER), CoreMatchers.nullValue());
    }
}

