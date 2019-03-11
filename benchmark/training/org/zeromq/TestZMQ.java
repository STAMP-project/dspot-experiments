package org.zeromq;


import Mechanism.CURVE;
import Mechanism.NULL;
import Mechanism.PLAIN;
import SocketType.DEALER;
import SocketType.PAIR;
import SocketType.PULL;
import SocketType.PUSH;
import SocketType.REP;
import SocketType.REQ;
import SocketType.ROUTER;
import SocketType.STREAM;
import SocketType.SUB;
import SocketType.XPUB;
import ZMQ.CHARSET;
import ZMQ.Error.EADDRINUSE;
import java.io.IOException;
import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMQ.Socket.Mechanism;
import zmq.msg.MsgAllocator;
import zmq.msg.MsgAllocatorDirect;


public class TestZMQ {
    private Context ctx;

    @Test
    public void testErrno() {
        Socket socket = ctx.socket(DEALER);
        Assert.assertThat(socket.errno(), CoreMatchers.is(0));
        socket.close();
    }

    @Test(expected = ZMQException.class)
    public void testBindSameAddress() throws IOException {
        int port = Utils.findOpenPort();
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket1 = context.socket(REQ);
        ZMQ.Socket socket2 = context.socket(REQ);
        socket1.bind(("tcp://*:" + port));
        try {
            socket2.bind(("tcp://*:" + port));
            Assert.fail("Exception not thrown");
        } catch (ZMQException e) {
            Assert.assertEquals(e.getErrorCode(), EADDRINUSE.getCode());
            throw e;
        } finally {
            socket1.close();
            socket2.close();
            context.term();
        }
    }

    @Test(expected = ZMQException.class)
    public void testBindInprocSameAddress() {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket1 = context.socket(REQ);
        ZMQ.Socket socket2 = context.socket(REQ);
        socket1.bind("inproc://address.already.in.use");
        socket2.bind("inproc://address.already.in.use");
        Assert.assertThat(socket2.errno(), CoreMatchers.is(ZError.EADDRINUSE));
        socket1.close();
        socket2.close();
        context.term();
    }

    @Test
    public void testSocketUnbind() {
        Context context = ZMQ.context(1);
        Socket push = context.socket(PUSH);
        Socket pull = context.socket(PULL);
        boolean rc = pull.setReceiveTimeOut(50);
        Assert.assertThat(rc, CoreMatchers.is(true));
        // rc = push.setImmediate(false);
        // assertThat(rc, is(true));
        // rc = pull.setImmediate(false);
        // assertThat(rc, is(true));
        int port = push.bindToRandomPort("tcp://127.0.0.1");
        rc = pull.connect(("tcp://127.0.0.1:" + port));
        Assert.assertThat(rc, CoreMatchers.is(true));
        System.out.println(("Connecting socket to unbind on port " + port));
        byte[] data = "ABC".getBytes();
        rc = push.send(data);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Assert.assertArrayEquals(data, pull.recv());
        rc = pull.unbind(("tcp://127.0.0.1:" + port));
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = push.send(data);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Assert.assertNull(pull.recv());
        push.close();
        pull.close();
        context.term();
    }

    @Test
    public void testSocketSendRecvArray() {
        Context context = ZMQ.context(1);
        Socket push = context.socket(PUSH);
        Socket pull = context.socket(PULL);
        boolean rc = pull.setReceiveTimeOut(50);
        Assert.assertThat(rc, CoreMatchers.is(true));
        int port = push.bindToRandomPort("tcp://127.0.0.1");
        rc = pull.connect(("tcp://127.0.0.1:" + port));
        Assert.assertThat(rc, CoreMatchers.is(true));
        byte[] data = "ABC".getBytes(CHARSET);
        rc = push.sendMore("DEF");
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = push.send(data, 0, ((data.length) - 1), 0);
        Assert.assertThat(rc, CoreMatchers.is(true));
        byte[] recvd = pull.recv();
        Assert.assertThat(recvd, CoreMatchers.is("DEF".getBytes(CHARSET)));
        byte[] datb = new byte[2];
        int received = pull.recv(datb, 0, datb.length, 0);
        Assert.assertThat(received, CoreMatchers.is(2));
        Assert.assertThat(datb, CoreMatchers.is("AB".getBytes(CHARSET)));
        push.close();
        pull.close();
        context.term();
    }

    @Test
    public void testSocketSendRecvPicture() {
        Context context = ZMQ.context(1);
        Socket push = context.socket(PUSH);
        Socket pull = context.socket(PULL);
        boolean rc = pull.setReceiveTimeOut(50);
        Assert.assertThat(rc, CoreMatchers.is(true));
        int port = push.bindToRandomPort("tcp://127.0.0.1");
        rc = pull.connect(("tcp://127.0.0.1:" + port));
        Assert.assertThat(rc, CoreMatchers.is(true));
        String picture = "1248sScfm";
        ZMsg msg = new ZMsg();
        msg.add("Hello");
        msg.add("World");
        rc = push.sendBinaryPicture(picture, 255, 65535, 429496729L, Long.MAX_VALUE, "Hello World", "Hello cruel World!", "ABC".getBytes(CHARSET), new ZFrame("My frame"), msg);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Object[] objects = pull.recvBinaryPicture(picture);
        Assert.assertThat(objects[0], CoreMatchers.is(CoreMatchers.equalTo(((byte) (255)))));
        Assert.assertThat(objects[1], CoreMatchers.is(CoreMatchers.equalTo(65535)));
        Assert.assertThat(objects[2], CoreMatchers.is(CoreMatchers.equalTo(429496729L)));
        Assert.assertThat(objects[3], CoreMatchers.is(CoreMatchers.equalTo(Long.MAX_VALUE)));
        Assert.assertThat(objects[4], CoreMatchers.is(CoreMatchers.equalTo("Hello World")));
        Assert.assertThat(objects[5], CoreMatchers.is(CoreMatchers.equalTo("Hello cruel World!")));
        Assert.assertThat(objects[6], CoreMatchers.is(CoreMatchers.equalTo("ABC".getBytes(zmq.ZMQ.CHARSET))));
        Assert.assertThat(objects[7], CoreMatchers.is(CoreMatchers.equalTo(new ZFrame("My frame"))));
        ZMsg expectedMsg = new ZMsg();
        expectedMsg.add("Hello");
        expectedMsg.add("World");
        Assert.assertThat(objects[8], CoreMatchers.is(CoreMatchers.equalTo(expectedMsg)));
        push.close();
        pull.close();
        context.term();
    }

    @Test
    public void testContextBlocky() {
        Socket router = ctx.socket(ROUTER);
        long rc = router.getLinger();
        Assert.assertThat(rc, CoreMatchers.is((-1L)));
        router.close();
        ctx.setBlocky(false);
        router = ctx.socket(ROUTER);
        rc = router.getLinger();
        Assert.assertThat(rc, CoreMatchers.is(0L));
        router.close();
    }

    @Test(timeout = 1000)
    public void testSocketDoubleClose() {
        Socket socket = ctx.socket(PUSH);
        socket.close();
        socket.close();
    }

    @Test
    public void testSubscribe() {
        ZMQ.Socket socket = ctx.socket(SUB);
        boolean rc = socket.subscribe("abc");
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = socket.unsubscribe("abc");
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = socket.unsubscribe("abc".getBytes(CHARSET));
        Assert.assertThat(rc, CoreMatchers.is(true));
        socket.close();
    }

    @Test
    public void testSocketAffinity() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        socket.setAffinity(42);
        long rc = socket.getAffinity();
        Assert.assertThat(rc, CoreMatchers.is(42L));
        socket.close();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSocketBacklog() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean set = socket.setBacklog(42L);
        Assert.assertThat(set, CoreMatchers.is(true));
        int rc = socket.getBacklog();
        Assert.assertThat(rc, CoreMatchers.is(42));
        socket.close();
    }

    @Test
    public void testSocketConflate() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean set = socket.setConflate(true);
        Assert.assertThat(set, CoreMatchers.is(true));
        boolean rc = socket.getConflate();
        Assert.assertThat(rc, CoreMatchers.is(true));
        set = socket.setConflate(false);
        Assert.assertThat(set, CoreMatchers.is(true));
        rc = socket.isConflate();
        Assert.assertThat(rc, CoreMatchers.is(false));
        socket.close();
    }

    @Test
    public void testSocketConnectRid() {
        final Socket socket = ctx.socket(ROUTER);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean set = socket.setConnectRid("rid");
        Assert.assertThat(set, CoreMatchers.is(true));
        set = socket.setConnectRid("rid".getBytes(CHARSET));
        Assert.assertThat(set, CoreMatchers.is(true));
        socket.close();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSocketCurveAsServer() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.setCurveServer(true);
        Assert.assertThat(rc, CoreMatchers.is(true));
        boolean server = socket.getCurveServer();
        Assert.assertThat(server, CoreMatchers.is(true));
        server = socket.getAsServerCurve();
        Assert.assertThat(server, CoreMatchers.is(true));
        server = socket.isAsServerCurve();
        Assert.assertThat(server, CoreMatchers.is(true));
        Mechanism mechanism = socket.getMechanism();
        Assert.assertThat(mechanism, CoreMatchers.is(CURVE));
        socket.close();
    }

    @Test
    public void testSocketCurveSecret() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        byte[] key = new byte[32];
        Arrays.fill(key, ((byte) (2)));
        boolean rc = socket.setCurveSecretKey(key);
        Assert.assertThat(rc, CoreMatchers.is(true));
        byte[] curve = socket.getCurveSecretKey();
        Assert.assertThat(curve, CoreMatchers.is(key));
        boolean server = socket.getCurveServer();
        Assert.assertThat(server, CoreMatchers.is(false));
        Mechanism mechanism = socket.getMechanism();
        Assert.assertThat(mechanism, CoreMatchers.is(CURVE));
        socket.close();
    }

    @Test
    public void testSocketCurvePublic() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        Mechanism mechanism = socket.getMechanism();
        Assert.assertThat(mechanism, CoreMatchers.is(NULL));
        byte[] key = new byte[32];
        Arrays.fill(key, ((byte) (2)));
        boolean rc = socket.setCurvePublicKey(key);
        Assert.assertThat(rc, CoreMatchers.is(true));
        byte[] curve = socket.getCurvePublicKey();
        Assert.assertThat(curve, CoreMatchers.is(key));
        boolean server = socket.getCurveServer();
        Assert.assertThat(server, CoreMatchers.is(false));
        mechanism = socket.getMechanism();
        Assert.assertThat(mechanism, CoreMatchers.is(CURVE));
        socket.close();
    }

    @Test
    public void testSocketCurveServer() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        byte[] key = new byte[32];
        Arrays.fill(key, ((byte) (2)));
        boolean rc = socket.setCurveServerKey(key);
        Assert.assertThat(rc, CoreMatchers.is(true));
        byte[] curve = socket.getCurveServerKey();
        Assert.assertThat(curve, CoreMatchers.is(key));
        boolean server = socket.getCurveServer();
        Assert.assertThat(server, CoreMatchers.is(false));
        Mechanism mechanism = socket.getMechanism();
        Assert.assertThat(mechanism, CoreMatchers.is(CURVE));
        socket.close();
    }

    @Test
    public void testSocketHandshake() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean set = socket.setHandshakeIvl(42);
        Assert.assertThat(set, CoreMatchers.is(true));
        int rc = socket.getHandshakeIvl();
        Assert.assertThat(rc, CoreMatchers.is(42));
        socket.close();
    }

    @Test
    public void testSocketHeartbeatIvl() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean set = socket.setHeartbeatIvl(42);
        Assert.assertThat(set, CoreMatchers.is(true));
        int rc = socket.getHeartbeatIvl();
        Assert.assertThat(rc, CoreMatchers.is(42));
        socket.close();
    }

    @Test
    public void testSocketHeartbeatTtl() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean set = socket.setHeartbeatTtl(420);
        Assert.assertThat(set, CoreMatchers.is(true));
        int rc = socket.getHeartbeatTtl();
        Assert.assertThat(rc, CoreMatchers.is(400));
        socket.close();
    }

    @Test
    public void testSocketHeartbeatTimeout() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean set = socket.setHeartbeatTimeout(42);
        Assert.assertThat(set, CoreMatchers.is(true));
        int rc = socket.getHeartbeatTimeout();
        Assert.assertThat(rc, CoreMatchers.is(42));
        socket.close();
    }

    @Test
    public void testSocketHeartbeatContext() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        byte[] context = new byte[3];
        context[0] = 4;
        context[1] = 2;
        context[2] = 1;
        boolean set = socket.setHeartbeatContext(context);
        Assert.assertThat(set, CoreMatchers.is(true));
        byte[] hctx = socket.getHeartbeatContext();
        Assert.assertThat(hctx, CoreMatchers.is(context));
        socket.close();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSocketHWM() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean set = socket.setHWM(42);
        Assert.assertThat(set, CoreMatchers.is(true));
        int rc = socket.getRcvHWM();
        Assert.assertThat(rc, CoreMatchers.is(42));
        rc = socket.getSndHWM();
        Assert.assertThat(rc, CoreMatchers.is(42));
        set = socket.setHWM(43L);
        Assert.assertThat(set, CoreMatchers.is(true));
        rc = socket.getRcvHWM();
        Assert.assertThat(rc, CoreMatchers.is(43));
        rc = socket.getSndHWM();
        Assert.assertThat(rc, CoreMatchers.is(43));
        rc = socket.getHWM();
        Assert.assertThat(rc, CoreMatchers.is((-1)));
        socket.close();
    }

    @Test
    public void testSocketIdentity() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        byte[] identity = new byte[42];
        Arrays.fill(identity, ((byte) (66)));
        boolean set = socket.setIdentity(identity);
        Assert.assertThat(set, CoreMatchers.is(true));
        byte[] rc = socket.getIdentity();
        Assert.assertThat(rc, CoreMatchers.is(identity));
        socket.close();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSocketImmediate() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean set = socket.setImmediate(false);
        Assert.assertThat(set, CoreMatchers.is(true));
        boolean rc = socket.getImmediate();
        Assert.assertThat(rc, CoreMatchers.is(false));
        rc = socket.getDelayAttachOnConnect();
        Assert.assertThat(rc, CoreMatchers.is(true));
        set = socket.setImmediate(true);
        Assert.assertThat(set, CoreMatchers.is(true));
        rc = socket.getImmediate();
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = socket.getDelayAttachOnConnect();
        Assert.assertThat(rc, CoreMatchers.is(false));
        set = socket.setDelayAttachOnConnect(false);
        Assert.assertThat(set, CoreMatchers.is(true));
        rc = socket.getImmediate();
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = socket.getDelayAttachOnConnect();
        Assert.assertThat(rc, CoreMatchers.is(false));
        set = socket.setDelayAttachOnConnect(true);
        Assert.assertThat(set, CoreMatchers.is(true));
        rc = socket.getImmediate();
        Assert.assertThat(rc, CoreMatchers.is(false));
        rc = socket.getDelayAttachOnConnect();
        Assert.assertThat(rc, CoreMatchers.is(true));
        socket.close();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSocketIPv6() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean set = socket.setIPv6(true);
        Assert.assertThat(set, CoreMatchers.is(true));
        boolean rc = socket.getIPv6();
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = socket.getIPv4Only();
        Assert.assertThat(rc, CoreMatchers.is(false));
        set = socket.setIPv6(false);
        Assert.assertThat(set, CoreMatchers.is(true));
        rc = socket.getIPv6();
        Assert.assertThat(rc, CoreMatchers.is(false));
        rc = socket.getIPv4Only();
        Assert.assertThat(rc, CoreMatchers.is(true));
        set = socket.setIPv4Only(false);
        Assert.assertThat(set, CoreMatchers.is(true));
        rc = socket.getIPv6();
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = socket.getIPv4Only();
        Assert.assertThat(rc, CoreMatchers.is(false));
        set = socket.setIPv4Only(true);
        Assert.assertThat(set, CoreMatchers.is(true));
        rc = socket.getIPv6();
        Assert.assertThat(rc, CoreMatchers.is(false));
        rc = socket.getIPv4Only();
        Assert.assertThat(rc, CoreMatchers.is(true));
        socket.close();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSocketLinger() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean set = socket.setLinger(42);
        Assert.assertThat(set, CoreMatchers.is(true));
        int rc = socket.getLinger();
        Assert.assertThat(rc, CoreMatchers.is(42));
        set = socket.setLinger(42L);
        Assert.assertThat(set, CoreMatchers.is(true));
        rc = socket.getLinger();
        Assert.assertThat(rc, CoreMatchers.is(42));
        socket.close();
    }

    @Test
    public void testSocketMaxMsgSize() {
        final Socket socket = ctx.socket(STREAM);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean set = socket.setMaxMsgSize(42);
        Assert.assertThat(set, CoreMatchers.is(true));
        long rc = socket.getMaxMsgSize();
        Assert.assertThat(rc, CoreMatchers.is(42L));
        socket.close();
    }

    @Test
    public void testSocketMsgAllocationThreshold() {
        final Socket socket = ctx.socket(STREAM);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean set = socket.setMsgAllocationHeapThreshold(42);
        Assert.assertThat(set, CoreMatchers.is(true));
        int rc = socket.getMsgAllocationHeapThreshold();
        Assert.assertThat(rc, CoreMatchers.is(42));
        socket.close();
    }

    @Test
    public void testSocketMsgAllocator() {
        final Socket socket = ctx.socket(STREAM);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        MsgAllocator allocator = new MsgAllocatorDirect();
        boolean set = socket.setMsgAllocator(allocator);
        Assert.assertThat(set, CoreMatchers.is(true));
        // TODO
        socket.close();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSocketMulticastHops() {
        final Socket socket = ctx.socket(STREAM);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        try {
            socket.setMulticastHops(42);
        } finally {
            socket.close();
        }
    }

    @Test
    public void testSocketGetMulticastHops() {
        final Socket socket = ctx.socket(STREAM);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        long rc = socket.getMulticastHops();
        Assert.assertThat(rc, CoreMatchers.is(1L));
        socket.close();
    }

    @SuppressWarnings("deprecation")
    @Test(expected = UnsupportedOperationException.class)
    public void testSocketMulticastLoop() {
        final Socket socket = ctx.socket(STREAM);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        try {
            socket.setMulticastLoop(true);
        } finally {
            socket.close();
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSocketHasMulticastLoop() {
        final Socket socket = ctx.socket(STREAM);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.hasMulticastLoop();
        Assert.assertThat(rc, CoreMatchers.is(false));
        socket.close();
    }

    @Test
    public void testSocketPlainPassword() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.setPlainPassword("password");
        Assert.assertThat(rc, CoreMatchers.is(true));
        String password = socket.getPlainPassword();
        Assert.assertThat(password, CoreMatchers.is("password"));
        boolean server = socket.getPlainServer();
        Assert.assertThat(server, CoreMatchers.is(false));
        Mechanism mechanism = socket.getMechanism();
        Assert.assertThat(mechanism, CoreMatchers.is(PLAIN));
        socket.close();
    }

    @Test
    public void testSocketPlainUsername() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.setPlainUsername("username");
        Assert.assertThat(rc, CoreMatchers.is(true));
        String username = socket.getPlainUsername();
        Assert.assertThat(username, CoreMatchers.is("username"));
        boolean server = socket.getPlainServer();
        Assert.assertThat(server, CoreMatchers.is(false));
        Mechanism mechanism = socket.getMechanism();
        Assert.assertThat(mechanism, CoreMatchers.is(PLAIN));
        socket.close();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSocketPlainServer() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.setPlainServer(true);
        Assert.assertThat(rc, CoreMatchers.is(true));
        boolean server = socket.getPlainServer();
        Assert.assertThat(server, CoreMatchers.is(true));
        server = socket.isAsServerPlain();
        Assert.assertThat(server, CoreMatchers.is(true));
        server = socket.getAsServerPlain();
        Assert.assertThat(server, CoreMatchers.is(true));
        Mechanism mechanism = socket.getMechanism();
        Assert.assertThat(mechanism, CoreMatchers.is(PLAIN));
        socket.close();
    }

    @Test
    public void testSocketProbeRouter() {
        final Socket socket = ctx.socket(ROUTER);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.setProbeRouter(true);
        Assert.assertThat(rc, CoreMatchers.is(true));
        socket.close();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSocketRate() {
        final Socket socket = ctx.socket(ROUTER);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        try {
            socket.setRate(42);
        } finally {
            socket.close();
        }
    }

    @Test
    public void testSocketGetRate() {
        final Socket socket = ctx.socket(ROUTER);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        long rate = socket.getRate();
        Assert.assertThat(rate, CoreMatchers.is(100L));
        socket.close();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSocketRcvHwm() {
        final Socket socket = ctx.socket(DEALER);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.setRcvHWM(42L);
        Assert.assertThat(rc, CoreMatchers.is(true));
        int hwm = socket.getRcvHWM();
        Assert.assertThat(hwm, CoreMatchers.is(42));
        hwm = socket.getSndHWM();
        Assert.assertThat(hwm, CoreMatchers.is(CoreMatchers.not(42)));
        socket.close();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSocketSndHwm() {
        final Socket socket = ctx.socket(DEALER);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.setSndHWM(42L);
        Assert.assertThat(rc, CoreMatchers.is(true));
        int hwm = socket.getSndHWM();
        Assert.assertThat(hwm, CoreMatchers.is(42));
        hwm = socket.getRcvHWM();
        Assert.assertThat(hwm, CoreMatchers.is(CoreMatchers.not(42)));
        socket.close();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSocketReceiveBufferSize() {
        final Socket socket = ctx.socket(DEALER);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.setReceiveBufferSize(42L);
        Assert.assertThat(rc, CoreMatchers.is(true));
        int size = socket.getReceiveBufferSize();
        Assert.assertThat(size, CoreMatchers.is(42));
        size = socket.getSendBufferSize();
        Assert.assertThat(size, CoreMatchers.is(CoreMatchers.not(42)));
        socket.close();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSocketSendBufferSize() {
        final Socket socket = ctx.socket(DEALER);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.setSendBufferSize(42L);
        Assert.assertThat(rc, CoreMatchers.is(true));
        int size = socket.getSendBufferSize();
        Assert.assertThat(size, CoreMatchers.is(42));
        size = socket.getReceiveBufferSize();
        Assert.assertThat(size, CoreMatchers.is(CoreMatchers.not(42)));
        socket.close();
    }

    @Test
    public void testSocketReceiveTimeOut() {
        final Socket socket = ctx.socket(PAIR);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.setReceiveTimeOut(42);
        Assert.assertThat(rc, CoreMatchers.is(true));
        int size = socket.getReceiveTimeOut();
        Assert.assertThat(size, CoreMatchers.is(42));
        size = socket.getSendTimeOut();
        Assert.assertThat(size, CoreMatchers.is(CoreMatchers.not(42)));
        socket.close();
    }

    @Test
    public void testSocketSendTimeOut() {
        final Socket socket = ctx.socket(PAIR);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.setSendTimeOut(42);
        Assert.assertThat(rc, CoreMatchers.is(true));
        int size = socket.getSendTimeOut();
        Assert.assertThat(size, CoreMatchers.is(42));
        size = socket.getReceiveTimeOut();
        Assert.assertThat(size, CoreMatchers.is(CoreMatchers.not(42)));
        socket.close();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSocketReconnectIVL() {
        final Socket socket = ctx.socket(REP);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.setReconnectIVL(42L);
        Assert.assertThat(rc, CoreMatchers.is(true));
        int reconnect = socket.getReconnectIVL();
        Assert.assertThat(reconnect, CoreMatchers.is(42));
        socket.close();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSocketReconnectIVLMax() {
        final Socket socket = ctx.socket(REP);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.setReconnectIVLMax(42L);
        Assert.assertThat(rc, CoreMatchers.is(true));
        int reconnect = socket.getReconnectIVLMax();
        Assert.assertThat(reconnect, CoreMatchers.is(42));
        socket.close();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSocketRecoveryInterval() {
        final Socket socket = ctx.socket(REP);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        try {
            socket.setRecoveryInterval(42L);
        } finally {
            socket.close();
        }
    }

    @Test
    public void testSocketgetRecoveryInterval() {
        final Socket socket = ctx.socket(REP);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        long reconnect = socket.getRecoveryInterval();
        Assert.assertThat(reconnect, CoreMatchers.is(10000L));
        socket.close();
    }

    @Test
    public void testSocketReqCorrelate() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.setReqCorrelate(true);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = socket.setReqCorrelate(false);
        Assert.assertThat(rc, CoreMatchers.is(true));
        socket.close();
    }

    @SuppressWarnings("deprecation")
    @Test(expected = UnsupportedOperationException.class)
    public void testSocketGetReqCorrelate() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        try {
            socket.getReqCorrelate();
        } finally {
            socket.close();
        }
    }

    @Test
    public void testSocketReqRelaxed() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.setReqRelaxed(true);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = socket.setReqRelaxed(false);
        Assert.assertThat(rc, CoreMatchers.is(true));
        socket.close();
    }

    @SuppressWarnings("deprecation")
    @Test(expected = UnsupportedOperationException.class)
    public void testSocketGetReqRelaxed() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        try {
            socket.getReqRelaxed();
        } finally {
            socket.close();
        }
    }

    @Test
    public void testSocketRouterHandover() {
        final Socket socket = ctx.socket(ROUTER);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.setRouterHandover(true);
        Assert.assertThat(rc, CoreMatchers.is(true));
        socket.close();
    }

    @Test
    public void testSocketRouterMandatory() {
        final Socket socket = ctx.socket(ROUTER);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.setRouterMandatory(true);
        Assert.assertThat(rc, CoreMatchers.is(true));
        socket.close();
    }

    @Test
    public void testSocketRouterRaw() {
        final Socket socket = ctx.socket(ROUTER);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.setRouterRaw(true);
        Assert.assertThat(rc, CoreMatchers.is(true));
        socket.close();
    }

    @Test
    public void testSocketSocksProxy() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.setSocksProxy("abc");
        Assert.assertThat(rc, CoreMatchers.is(true));
        String proxy = socket.getSocksProxy();
        Assert.assertThat(proxy, CoreMatchers.is("abc"));
        rc = socket.setSocksProxy("def".getBytes(CHARSET));
        Assert.assertThat(rc, CoreMatchers.is(true));
        proxy = socket.getSocksProxy();
        Assert.assertThat(proxy, CoreMatchers.is("def"));
        socket.close();
    }

    @SuppressWarnings("deprecation")
    @Test(expected = UnsupportedOperationException.class)
    public void testSocketSwap() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        try {
            socket.setSwap(42L);
        } finally {
            socket.close();
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSocketGetSwap() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        long rc = socket.getSwap();
        Assert.assertThat(rc, CoreMatchers.is((-1L)));
        socket.close();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSocketTCPKeepAlive() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.setTCPKeepAlive(1L);
        Assert.assertThat(rc, CoreMatchers.is(true));
        int tcp = socket.getTCPKeepAlive();
        Assert.assertThat(tcp, CoreMatchers.is(1));
        long tcpl = socket.getTCPKeepAliveSetting();
        Assert.assertThat(tcpl, CoreMatchers.is(1L));
        socket.close();
    }

    @Test
    public void testSocketTCPKeepAliveCount() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.setTCPKeepAliveCount(42);
        Assert.assertThat(rc, CoreMatchers.is(false));
        long tcp = socket.getTCPKeepAliveCount();
        Assert.assertThat(tcp, CoreMatchers.is(0L));
        socket.close();
    }

    @Test
    public void testSocketTCPKeepAliveInterval() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.setTCPKeepAliveInterval(42);
        Assert.assertThat(rc, CoreMatchers.is(false));
        long tcp = socket.getTCPKeepAliveInterval();
        Assert.assertThat(tcp, CoreMatchers.is(0L));
        socket.close();
    }

    @Test
    public void testSocketTCPKeepAliveIdle() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.setTCPKeepAliveIdle(42);
        Assert.assertThat(rc, CoreMatchers.is(false));
        long tcp = socket.getTCPKeepAliveIdle();
        Assert.assertThat(tcp, CoreMatchers.is(0L));
        socket.close();
    }

    @Test
    public void testSocketTos() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.setTos(42);
        Assert.assertThat(rc, CoreMatchers.is(true));
        int tos = socket.getTos();
        Assert.assertThat(tos, CoreMatchers.is(42));
        socket.close();
    }

    @Test
    public void testSocketType() {
        final Socket socket = ctx.socket(REQ);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        SocketType rc = socket.getSocketType();
        Assert.assertThat(rc, CoreMatchers.is(REQ));
        socket.close();
    }

    @Test
    public void testSocketXpubNoDrop() {
        final Socket socket = ctx.socket(XPUB);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.setXpubNoDrop(true);
        Assert.assertThat(rc, CoreMatchers.is(true));
        socket.close();
    }

    @Test
    public void testSocketXpubVerbose() {
        final Socket socket = ctx.socket(XPUB);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.setXpubVerbose(true);
        Assert.assertThat(rc, CoreMatchers.is(true));
        socket.close();
    }

    @Test
    public void testSocketZAPDomain() {
        final Socket socket = ctx.socket(XPUB);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = socket.setZAPDomain("abc");
        Assert.assertThat(rc, CoreMatchers.is(true));
        String domain = socket.getZAPDomain();
        Assert.assertThat(domain, CoreMatchers.is("abc"));
        domain = socket.getZapDomain();
        Assert.assertThat(domain, CoreMatchers.is("abc"));
        rc = socket.setZapDomain("def");
        Assert.assertThat(rc, CoreMatchers.is(true));
        domain = socket.getZapDomain();
        Assert.assertThat(domain, CoreMatchers.is("def"));
        domain = socket.getZAPDomain();
        Assert.assertThat(domain, CoreMatchers.is("def"));
        rc = socket.setZapDomain("ghi".getBytes(CHARSET));
        Assert.assertThat(rc, CoreMatchers.is(true));
        domain = socket.getZapDomain();
        Assert.assertThat(domain, CoreMatchers.is("ghi"));
        domain = socket.getZAPDomain();
        Assert.assertThat(domain, CoreMatchers.is("ghi"));
        rc = socket.setZAPDomain("jkl".getBytes(CHARSET));
        Assert.assertThat(rc, CoreMatchers.is(true));
        domain = socket.getZapDomain();
        Assert.assertThat(domain, CoreMatchers.is("jkl"));
        domain = socket.getZAPDomain();
        Assert.assertThat(domain, CoreMatchers.is("jkl"));
        socket.close();
    }
}

