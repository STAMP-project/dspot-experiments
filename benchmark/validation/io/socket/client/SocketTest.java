package io.socket.client;


import IO.Options;
import Socket.EVENT_CONNECT;
import Socket.EVENT_CONNECT_ERROR;
import Socket.EVENT_DISCONNECT;
import Socket.EVENT_PING;
import Socket.EVENT_PONG;
import Socket.EVENT_RECONNECT;
import Socket.EVENT_RECONNECT_ATTEMPT;
import io.socket.emitter.Emitter;
import io.socket.util.Optional;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class SocketTest extends Connection {
    private Socket socket;

    @Test(timeout = Connection.TIMEOUT)
    public void shouldHaveAnAccessibleSocketIdEqualToServerSideSocketId() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Optional> values = new LinkedBlockingQueue<Optional>();
        socket = client();
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                values.offer(Optional.ofNullable(socket.id()));
            }
        });
        socket.connect();
        @SuppressWarnings("unchecked")
        Optional<String> id = values.take();
        Assert.assertThat(id.isPresent(), CoreMatchers.is(true));
        Assert.assertThat(id.get(), CoreMatchers.is(socket.io().engine.id()));
        socket.disconnect();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void shouldHaveAnAccessibleSocketIdEqualToServerSideSocketIdOnCustomNamespace() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Optional> values = new LinkedBlockingQueue<Optional>();
        socket = client("/foo");
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                values.offer(Optional.ofNullable(socket.id()));
            }
        });
        socket.connect();
        @SuppressWarnings("unchecked")
        Optional<String> id = values.take();
        Assert.assertThat(id.isPresent(), CoreMatchers.is(true));
        Assert.assertThat(id.get(), CoreMatchers.is(("/foo#" + (socket.io().engine.id()))));
        socket.disconnect();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void clearsSocketIdUponDisconnection() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Optional> values = new LinkedBlockingQueue<Optional>();
        socket = client();
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                socket.on(EVENT_DISCONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        values.offer(Optional.ofNullable(socket.id()));
                    }
                });
                socket.disconnect();
            }
        });
        socket.connect();
        @SuppressWarnings("unchecked")
        Optional<String> id = values.take();
        Assert.assertThat(id.isPresent(), CoreMatchers.is(false));
    }

    @Test(timeout = Connection.TIMEOUT)
    public void doesNotFireConnectErrorIfWeForceDisconnectInOpeningState() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Optional> values = new LinkedBlockingQueue<Optional>();
        IO.Options opts = new IO.Options();
        opts.timeout = 100;
        socket = client(opts);
        socket.on(EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                values.offer(Optional.of(new Error("Unexpected")));
            }
        });
        socket.connect();
        socket.disconnect();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                values.offer(Optional.empty());
            }
        }, 300);
        @SuppressWarnings("unchecked")
        Optional<Error> err = values.take();
        if (err.isPresent())
            throw err.get();

    }

    @Test(timeout = Connection.TIMEOUT)
    public void pingAndPongWithLatency() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        socket = client();
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                final boolean[] pinged = new boolean[]{ false };
                socket.once(EVENT_PING, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        pinged[0] = true;
                    }
                });
                socket.once(EVENT_PONG, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        long ms = ((long) (args[0]));
                        values.offer(pinged[0]);
                        values.offer(ms);
                    }
                });
            }
        });
        socket.connect();
        @SuppressWarnings("unchecked")
        boolean pinged = ((boolean) (values.take()));
        Assert.assertThat(pinged, CoreMatchers.is(true));
        @SuppressWarnings("unchecked")
        long ms = ((long) (values.take()));
        Assert.assertThat(ms, Matchers.greaterThan(((long) (0))));
        socket.disconnect();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void shouldChangeSocketIdUponReconnection() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Optional> values = new LinkedBlockingQueue<Optional>();
        socket = client();
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                values.offer(Optional.ofNullable(socket.id()));
                socket.on(EVENT_RECONNECT_ATTEMPT, new Emitter.Listener() {
                    @Override
                    public void call(Object... objects) {
                        values.offer(Optional.ofNullable(socket.id()));
                    }
                });
                socket.on(EVENT_RECONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... objects) {
                        values.offer(Optional.ofNullable(socket.id()));
                    }
                });
                socket.io().engine.close();
            }
        });
        socket.connect();
        @SuppressWarnings("unchecked")
        Optional<String> id1 = values.take();
        @SuppressWarnings("unchecked")
        Optional<String> id2 = values.take();
        Assert.assertThat(id2.isPresent(), CoreMatchers.is(false));
        @SuppressWarnings("unchecked")
        Optional<String> id3 = values.take();
        Assert.assertThat(id3.get(), CoreMatchers.is(CoreMatchers.not(id1.get())));
        socket.disconnect();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void shouldAcceptAQueryStringOnDefaultNamespace() throws InterruptedException, URISyntaxException, JSONException {
        final BlockingQueue<Optional> values = new LinkedBlockingQueue<Optional>();
        socket = client("/?c=d");
        socket.emit("getHandshake", new Ack() {
            @Override
            public void call(Object... args) {
                JSONObject handshake = ((JSONObject) (args[0]));
                values.offer(Optional.ofNullable(handshake));
            }
        });
        socket.connect();
        @SuppressWarnings("unchecked")
        Optional<JSONObject> handshake = values.take();
        Assert.assertThat(handshake.get().getJSONObject("query").getString("c"), CoreMatchers.is("d"));
        socket.disconnect();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void shouldAcceptAQueryString() throws InterruptedException, URISyntaxException, JSONException {
        final BlockingQueue<Optional> values = new LinkedBlockingQueue<Optional>();
        socket = client("/abc?b=c&d=e");
        socket.on("handshake", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject handshake = ((JSONObject) (args[0]));
                values.offer(Optional.ofNullable(handshake));
            }
        });
        socket.connect();
        @SuppressWarnings("unchecked")
        Optional<JSONObject> handshake = values.take();
        JSONObject query = handshake.get().getJSONObject("query");
        Assert.assertThat(query.getString("b"), CoreMatchers.is("c"));
        Assert.assertThat(query.getString("d"), CoreMatchers.is("e"));
        socket.disconnect();
    }
}

