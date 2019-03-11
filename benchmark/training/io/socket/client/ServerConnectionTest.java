package io.socket.client;


import IO.Options;
import Manager.EVENT_TRANSPORT;
import Socket.EVENT_CONNECT;
import Socket.EVENT_DISCONNECT;
import Socket.EVENT_MESSAGE;
import Transport.EVENT_REQUEST_HEADERS;
import Transport.EVENT_RESPONSE_HEADERS;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Socket.EVENT_CLOSE;
import io.socket.engineio.client.Transport;
import io.socket.engineio.client.transports.Polling;
import io.socket.engineio.client.transports.WebSocket;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.hamcrest.CoreMatchers;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ServerConnectionTest extends Connection {
    private Socket socket;

    private Socket socket2;

    @Test(timeout = Connection.TIMEOUT)
    public void openAndClose() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        socket = client();
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                values.offer(args);
                socket.disconnect();
            }
        }).on(EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                values.offer(args);
            }
        });
        socket.connect();
        Assert.assertThat(((Object[]) (values.take())).length, CoreMatchers.is(0));
        Object[] args = ((Object[]) (values.take()));
        Assert.assertThat(args.length, CoreMatchers.is(1));
        Assert.assertThat(args[0], CoreMatchers.is(CoreMatchers.instanceOf(String.class)));
    }

    @Test(timeout = Connection.TIMEOUT)
    public void message() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        socket = client();
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                socket.send("foo", "bar");
            }
        }).on(EVENT_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                values.offer(args);
            }
        });
        socket.connect();
        Assert.assertThat(((Object[]) (values.take())), CoreMatchers.is(new Object[]{ "hello client" }));
        Assert.assertThat(((Object[]) (values.take())), CoreMatchers.is(new Object[]{ "foo", "bar" }));
        socket.disconnect();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void event() throws Exception {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        final JSONObject obj = new JSONObject();
        obj.put("foo", 1);
        socket = client();
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                socket.emit("echo", obj, null, "bar");
            }
        }).on("echoBack", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                values.offer(args);
            }
        });
        socket.connect();
        Object[] args = ((Object[]) (values.take()));
        Assert.assertThat(args.length, CoreMatchers.is(3));
        Assert.assertThat(args[0].toString(), CoreMatchers.is(obj.toString()));
        Assert.assertThat(args[1], CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(((String) (args[2])), CoreMatchers.is("bar"));
        socket.disconnect();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void ack() throws Exception {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        final JSONObject obj = new JSONObject();
        obj.put("foo", 1);
        socket = client();
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                socket.emit("ack", new Object[]{ obj, "bar" }, new Ack() {
                    @Override
                    public void call(Object... args) {
                        values.offer(args);
                    }
                });
            }
        });
        socket.connect();
        Object[] args = ((Object[]) (values.take()));
        Assert.assertThat(args.length, CoreMatchers.is(2));
        Assert.assertThat(args[0].toString(), CoreMatchers.is(obj.toString()));
        Assert.assertThat(((String) (args[1])), CoreMatchers.is("bar"));
        socket.disconnect();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void ackWithoutArgs() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        socket = client();
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                socket.emit("ack", null, new Ack() {
                    @Override
                    public void call(Object... args) {
                        values.offer(args.length);
                    }
                });
            }
        });
        socket.connect();
        Assert.assertThat(((Integer) (values.take())), CoreMatchers.is(0));
        socket.disconnect();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void ackWithoutArgsFromClient() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        socket = client();
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                socket.on("ack", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        values.offer(args);
                        Ack ack = ((Ack) (args[0]));
                        ack.call();
                    }
                }).on("ackBack", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        values.offer(args);
                        socket.disconnect();
                    }
                });
                socket.emit("callAck");
            }
        });
        socket.connect();
        Object[] args = ((Object[]) (values.take()));
        Assert.assertThat(args.length, CoreMatchers.is(1));
        Assert.assertThat(args[0], CoreMatchers.is(CoreMatchers.instanceOf(Ack.class)));
        args = ((Object[]) (values.take()));
        Assert.assertThat(args.length, CoreMatchers.is(0));
        socket.disconnect();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void closeEngineConnection() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        socket = client();
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.io().engine.on(EVENT_CLOSE, new Emitter.Listener() {
                    @Override
                    public void call(Object... objects) {
                        values.offer("done");
                    }
                });
                socket.disconnect();
            }
        });
        socket.connect();
        values.take();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void broadcast() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        socket = client();
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                try {
                    socket2 = client();
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                socket2.on(EVENT_CONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... objects) {
                        socket2.emit("broadcast", "hi");
                    }
                });
                socket2.connect();
            }
        }).on("broadcastBack", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                values.offer(args);
            }
        });
        socket.connect();
        Object[] args = ((Object[]) (values.take()));
        Assert.assertThat(args.length, CoreMatchers.is(1));
        Assert.assertThat(((String) (args[0])), CoreMatchers.is("hi"));
        socket.disconnect();
        socket2.disconnect();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void room() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        socket = client();
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                socket.emit("room", "hi");
            }
        }).on("roomBack", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                values.offer(args);
            }
        });
        socket.connect();
        Object[] args = ((Object[]) (values.take()));
        Assert.assertThat(args.length, CoreMatchers.is(1));
        Assert.assertThat(((String) (args[0])), CoreMatchers.is("hi"));
        socket.disconnect();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void pollingHeaders() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        IO.Options opts = createOptions();
        opts.transports = new String[]{ Polling.NAME };
        socket = client(opts);
        socket.io().on(EVENT_TRANSPORT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Transport transport = ((Transport) (args[0]));
                transport.on(EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        @SuppressWarnings("unchecked")
                        Map<String, List<String>> headers = ((Map<String, List<String>>) (args[0]));
                        headers.put("X-SocketIO", Arrays.asList("hi"));
                    }
                }).on(EVENT_RESPONSE_HEADERS, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        @SuppressWarnings("unchecked")
                        Map<String, List<String>> headers = ((Map<String, List<String>>) (args[0]));
                        List<String> value = headers.get("X-SocketIO");
                        values.offer((value != null ? value.get(0) : ""));
                    }
                });
            }
        });
        socket.open();
        Assert.assertThat(((String) (values.take())), CoreMatchers.is("hi"));
        socket.close();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void websocketHandshakeHeaders() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        IO.Options opts = createOptions();
        opts.transports = new String[]{ WebSocket.NAME };
        socket = client(opts);
        socket.io().on(EVENT_TRANSPORT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Transport transport = ((Transport) (args[0]));
                transport.on(EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        @SuppressWarnings("unchecked")
                        Map<String, List<String>> headers = ((Map<String, List<String>>) (args[0]));
                        headers.put("X-SocketIO", Arrays.asList("hi"));
                    }
                }).on(EVENT_RESPONSE_HEADERS, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        @SuppressWarnings("unchecked")
                        Map<String, List<String>> headers = ((Map<String, List<String>>) (args[0]));
                        List<String> value = headers.get("X-SocketIO");
                        values.offer((value != null ? value.get(0) : ""));
                    }
                });
            }
        });
        socket.open();
        Assert.assertThat(((String) (values.take())), CoreMatchers.is("hi"));
        socket.close();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void disconnectFromServer() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        socket = client();
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.emit("requestDisconnect");
            }
        }).on(EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                values.offer("disconnected");
            }
        });
        socket.connect();
        Assert.assertThat(((String) (values.take())), CoreMatchers.is("disconnected"));
    }
}

