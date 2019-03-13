package org.zeromq;


import SocketType.DEALER;
import SocketType.ROUTER;
import ZProxy.Plug;
import ZProxy.Proxy.SimpleProxy;
import java.io.IOException;
import org.junit.Test;
import org.zeromq.ZMQ.Socket;


public class TestZProxy {
    private final class ProxyProvider extends ZProxy.Proxy.SimpleProxy {
        private int frontPort;

        private int backPort;

        private int capturePort;

        public ProxyProvider() throws IOException {
        }

        @Override
        public Socket create(ZContext ctx, ZProxy.Plug place, Object... extraArgs) {
            Socket socket = null;
            if (place == (Plug.FRONT)) {
                socket = ctx.createSocket(ROUTER);
            }
            if (place == (Plug.BACK)) {
                socket = ctx.createSocket(DEALER);
            }
            return socket;
        }

        @Override
        public boolean configure(Socket socket, ZProxy.Plug place, Object... extrArgs) throws IOException {
            if (place == (Plug.FRONT)) {
                frontPort = socket.bindToRandomPort("tcp://127.0.0.1");
            }
            if (place == (Plug.BACK)) {
                backPort = socket.bindToRandomPort("tcp://127.0.0.1");
            }
            if ((place == (Plug.CAPTURE)) && (socket != null)) {
                capturePort = socket.bindToRandomPort("tcp://127.0.0.1");
            }
            return true;
        }

        @Override
        public boolean restart(ZMsg cfg, Socket socket, ZProxy.Plug place, Object... extraArgs) throws IOException {
            // System.out.println("HOT restart msg : " + cfg);
            if (place == (Plug.FRONT)) {
                socket.unbind(("tcp://127.0.0.1:" + (frontPort)));
                waitSomeTime();
                frontPort = socket.bindToRandomPort("tcp://127.0.0.1");
            }
            if (place == (Plug.BACK)) {
                socket.unbind(("tcp://127.0.0.1:" + (backPort)));
                waitSomeTime();
                backPort = socket.bindToRandomPort("tcp://127.0.0.1");
            }
            if ((place == (Plug.CAPTURE)) && (socket != null)) {
                socket.unbind(("tcp://127.0.0.1:" + (capturePort)));
                waitSomeTime();
                capturePort = socket.bindToRandomPort("tcp://127.0.0.1");
            }
            String msg = cfg.popString();
            return "COLD".equals(msg);
        }

        @Override
        public boolean configure(Socket pipe, ZMsg cfg, Socket frontend, Socket backend, Socket capture, Object... args) {
            assert cfg.popString().equals("TEST-CONFIG");
            ZMsg msg = new ZMsg();
            msg.add("TODO");
            msg.send(pipe);
            return true;
        }

        @Override
        public boolean custom(Socket pipe, String cmd, Socket frontend, Socket backend, Socket capture, Object... args) {
            // TODO test custom commands
            return super.custom(pipe, cmd, frontend, backend, capture, args);
        }
    }

    @Test
    public void testAllOptionsAsync() throws IOException {
        System.out.println("All options async");
        ZContext ctx = nullContext();
        testAllOptionsAsync(ctx, null);
        wait4NullContext(ctx);
    }

    @Test
    public void testAllOptionsAsyncNew() throws IOException {
        System.out.println("All options async new");
        ZContext ctx = newContext();
        testAllOptionsAsync(ctx, null);
        wait4NewContext(ctx);
    }

    @Test
    public void testAllOptionsSync() throws IOException {
        System.out.println("All options sync");
        ZContext ctx = nullContext();
        testAllOptionsSync(ctx, null);
        wait4NullContext(ctx);
    }

    @Test
    public void testAllOptionsSyncNew() throws IOException {
        System.out.println("All options sync new");
        ZContext ctx = newContext();
        testAllOptionsSync(ctx, null);
        wait4NewContext(ctx);
    }

    @Test
    public void testAllOptionsSyncNewHot() throws IOException {
        System.out.println("All options sync new hot");
        ZContext ctx = newContext();
        ZMsg hot = new ZMsg();
        hot.add("HOT");
        testAllOptionsSync(ctx, hot);
        wait4NewContext(ctx);
    }

    @Test
    public void testAllOptionsSyncNewCold() throws IOException {
        System.out.println("All options sync new hot with restart");
        ZContext ctx = newContext();
        ZMsg hot = new ZMsg();
        hot.add("COLD");
        testAllOptionsSync(ctx, hot);
        wait4NewContext(ctx);
    }

    @Test
    public void testStateSync() throws IOException {
        System.out.println("State sync");
        ZContext ctx = newContext();
        testStateSync(ctx);
        wait4NewContext(ctx);
    }

    @Test
    public void testStateSyncPause() throws IOException {
        System.out.println("State sync pause");
        ZContext ctx = newContext();
        testStateSyncPause(ctx);
        wait4NewContext(ctx);
    }

    @Test
    public void testStateASync() throws IOException {
        System.out.println("State async");
        ZContext ctx = newContext();
        testStateASync(ctx);
        wait4NewContext(ctx);
    }
}

