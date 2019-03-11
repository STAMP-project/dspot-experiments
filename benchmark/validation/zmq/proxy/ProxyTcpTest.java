package zmq.proxy;


import Msg.MORE;
import Step.Result;
import ZMQ.CHARSET;
import ZMQ.ZMQ_DEALER;
import ZMQ.ZMQ_DECODER;
import ZMQ.ZMQ_ENCODER;
import ZMQ.ZMQ_ROUTER;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import zmq.Ctx;
import zmq.Helper;
import zmq.Msg;
import zmq.SocketBase;
import zmq.ZMQ;
import zmq.io.coder.Decoder;
import zmq.io.coder.EncoderBase;
import zmq.msg.MsgAllocatorThreshold;
import zmq.util.Errno;
import zmq.util.Utils;


@Ignore
public class ProxyTcpTest {
    static class Client extends Thread {
        private int port;

        public Client(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            System.out.println("Start client thread");
            try {
                Socket s = new Socket("127.0.0.1", port);
                Helper.send(s, "helo");
                Helper.send(s, "1234567890abcdefghizklmnopqrstuvwxyz");
                Helper.send(s, "end");
                Helper.send(s, "end");
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Stop client thread");
        }
    }

    static class Dealer extends Thread {
        private final SocketBase s;

        private final String name;

        private final int port;

        public Dealer(Ctx ctx, String name, int port) {
            this.s = ZMQ.socket(ctx, ZMQ_DEALER);
            this.name = name;
            this.port = port;
        }

        @Override
        public void run() {
            System.out.println(("Start dealer " + (name)));
            ZMQ.connect(s, ("tcp://127.0.0.1:" + (port)));
            int i = 0;
            while (true) {
                Msg msg = s.recv(0);
                if (msg == null) {
                    throw new RuntimeException("hello");
                }
                System.out.println(((("DEALER " + (name)) + " received ") + msg));
                String data = new String(msg.data(), 0, msg.size(), ZMQ.CHARSET);
                Msg response = null;
                if ((i % 3) == 2) {
                    response = new Msg(((msg.size()) + 3));
                    response.put("OK ".getBytes(CHARSET)).put(msg.data());
                } else {
                    response = new Msg(msg.data());
                }
                s.send(response, ((i % 3) == 2 ? 0 : ZMQ.ZMQ_SNDMORE));
                i++;
                if (data.equals("end")) {
                    break;
                }
            } 
            s.close();
            System.out.println(("Stop dealer " + (name)));
        }
    }

    public static class ProxyDecoder extends Decoder {
        private final Step readHeader = new Step() {
            @Override
            public Result apply() {
                return readHeader();
            }
        };

        private final Step readBody = new Step() {
            @Override
            public Result apply() {
                return readBody();
            }
        };

        byte[] header = new byte[4];

        Msg msg;

        int size = -1;

        boolean identitySent = false;

        Msg bottom;

        public ProxyDecoder(int bufsize, long maxmsgsize) {
            super(new Errno(), bufsize, maxmsgsize, new MsgAllocatorThreshold());
            nextStep(header, 4, readHeader);
            bottom = new Msg();
            bottom.setFlags(MORE);
        }

        private Result readHeader() {
            size = Integer.parseInt(new String(header, ZMQ.CHARSET));
            System.out.println(("Received " + (size)));
            msg = new Msg(size);
            nextStep(msg, readBody);
            return Result.MORE_DATA;
        }

        private Result readBody() {
            System.out.println(("Received body " + (new String(msg.data(), ZMQ.CHARSET))));
            if (!(identitySent)) {
                Msg identity = new Msg();
                // push(identity);
                identitySent = true;
            }
            // push(bottom);
            // push(msg);
            nextStep(header, 4, readHeader);
            return Result.DECODED;
        }
    }

    public static class ProxyEncoder extends EncoderBase {
        private final Runnable writeHeader = new Runnable() {
            @Override
            public void run() {
                writeHeader();
            }
        };

        private final Runnable writeBody = new Runnable() {
            @Override
            public void run() {
                writeBody();
            }
        };

        ByteBuffer header = ByteBuffer.allocate(4);

        Msg msg;

        int size = -1;

        boolean messageReady;

        boolean identityReceived;

        public ProxyEncoder(int bufsize, long unused) {
            super(new Errno(), bufsize);
            nextStep(((Msg) (null)), writeHeader, true);
            messageReady = false;
            identityReceived = false;
        }

        private void writeBody() {
            System.out.println(("write body " + (msg)));
            nextStep(msg, writeHeader, (!(msg.hasMore())));
        }

        private void writeHeader() {
            msg = inProgress;
            if ((msg) == null) {
                return;
            }
            if (!(identityReceived)) {
                identityReceived = true;
                nextStep(header, ((msg.size()) < 255 ? 2 : 10), writeBody, true);
                return;
            } else
                if (!(messageReady)) {
                    messageReady = true;
                    msg = inProgress;
                    if ((msg) == null) {
                        return;
                    }
                }

            messageReady = false;
            System.out.println(("write header " + (msg.size())));
            header.clear();
            header.put(String.format("%04d", msg.size()).getBytes(CHARSET));
            header.flip();
            nextStep(header, header.limit(), writeBody, false);
            return;
        }
    }

    static class Proxy extends Thread {
        private Ctx ctx;

        private int routerPort;

        private int dealerPort;

        Proxy(Ctx ctx, int routerPort, int dealerPort) {
            this.ctx = ctx;
            this.routerPort = routerPort;
            this.dealerPort = dealerPort;
        }

        @Override
        public void run() {
            boolean rc;
            SocketBase routerBind = ZMQ.socket(ctx, ZMQ_ROUTER);
            Assert.assertThat(routerBind, CoreMatchers.notNullValue());
            routerBind.setSocketOpt(ZMQ_DECODER, ProxyTcpTest.ProxyDecoder.class);
            routerBind.setSocketOpt(ZMQ_ENCODER, ProxyTcpTest.ProxyEncoder.class);
            rc = ZMQ.bind(routerBind, ("tcp://127.0.0.1:" + (routerPort)));
            Assert.assertThat(rc, CoreMatchers.is(true));
            SocketBase dealerBind = ZMQ.socket(ctx, ZMQ_DEALER);
            Assert.assertThat(dealerBind, CoreMatchers.notNullValue());
            rc = ZMQ.bind(dealerBind, ("tcp://127.0.0.1:" + (dealerPort)));
            Assert.assertThat(rc, CoreMatchers.is(true));
            ZMQ.proxy(routerBind, dealerBind, null);
            ZMQ.close(routerBind);
            ZMQ.close(dealerBind);
        }
    }

    @Test
    public void testProxyTcp() throws Exception {
        int routerPort = Utils.findOpenPort();
        int dealerPort = Utils.findOpenPort();
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        ProxyTcpTest.Proxy mt = new ProxyTcpTest.Proxy(ctx, routerPort, dealerPort);
        mt.start();
        new ProxyTcpTest.Dealer(ctx, "A", dealerPort).start();
        // new Dealer(ctx, "B", dealerPort).start();
        ZMQ.sleep(1);
        Thread client = new ProxyTcpTest.Client(routerPort);
        client.start();
        client.join();
        ZMQ.term(ctx);
    }
}

