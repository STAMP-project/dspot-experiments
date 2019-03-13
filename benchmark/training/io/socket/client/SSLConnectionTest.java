package io.socket.client;


import IO.Options;
import Socket.EVENT_CONNECT;
import io.socket.emitter.Emitter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import okhttp3.OkHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class SSLConnectionTest extends Connection {
    private static OkHttpClient sOkHttpClient;

    private Socket socket;

    static {
        try {
            SSLConnectionTest.prepareOkHttpClient();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test(timeout = Connection.TIMEOUT)
    public void connect() throws Exception {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        IO.Options opts = createOptions();
        opts.callFactory = SSLConnectionTest.sOkHttpClient;
        opts.webSocketFactory = SSLConnectionTest.sOkHttpClient;
        socket = client(opts);
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                socket.emit("echo");
                socket.on("echoBack", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        values.offer("done");
                    }
                });
            }
        });
        socket.connect();
        values.take();
        socket.close();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void defaultSSLContext() throws Exception {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        IO.setDefaultOkHttpWebSocketFactory(SSLConnectionTest.sOkHttpClient);
        IO.setDefaultOkHttpCallFactory(SSLConnectionTest.sOkHttpClient);
        socket = client();
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                socket.emit("echo");
                socket.on("echoBack", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        values.offer("done");
                    }
                });
            }
        });
        socket.connect();
        values.take();
        socket.close();
    }
}

