package io.socket.client;


import Emitter.Listener;
import IO.Options;
import Manager.EVENT_RECONNECT;
import Manager.EVENT_RECONNECT_ATTEMPT;
import Socket.EVENT_CONNECT;
import Socket.EVENT_CONNECT_ERROR;
import Socket.EVENT_DISCONNECT;
import Socket.EVENT_RECONNECTING;
import Socket.EVENT_RECONNECT_FAILED;
import io.socket.emitter.Emitter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.hamcrest.CoreMatchers;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ConnectionTest extends Connection {
    private Socket socket;

    @Test(timeout = Connection.TIMEOUT)
    public void connectToLocalhost() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
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

    @Test(timeout = Connection.TIMEOUT)
    public void startTwoConnectionsWithSamePath() throws InterruptedException, URISyntaxException {
        Socket s1 = client("/");
        Socket s2 = client("/");
        Assert.assertThat(s1.io(), CoreMatchers.not(CoreMatchers.equalTo(s2.io())));
        s1.close();
        s2.close();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void startTwoConnectionsWithSamePathAndDifferentQuerystrings() throws InterruptedException, URISyntaxException {
        Socket s1 = client("/?woot");
        Socket s2 = client("/");
        Assert.assertThat(s1.io(), CoreMatchers.not(CoreMatchers.equalTo(s2.io())));
        s1.close();
        s2.close();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void workWithAcks() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        socket = client();
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                socket.emit("callAck");
                socket.on("ack", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Ack fn = ((Ack) (args[0]));
                        JSONObject data = new JSONObject();
                        try {
                            data.put("test", true);
                        } catch (JSONException e) {
                            throw new AssertionError(e);
                        }
                        fn.call(5, data);
                    }
                });
                socket.on("ackBack", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        JSONObject data = ((JSONObject) (args[1]));
                        try {
                            if ((((Integer) (args[0])) == 5) && (data.getBoolean("test"))) {
                                values.offer("done");
                            }
                        } catch (JSONException e) {
                            throw new AssertionError(e);
                        }
                    }
                });
            }
        });
        socket.connect();
        values.take();
        socket.close();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void receiveDateWithAck() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        socket = client();
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                try {
                    socket.emit("getAckDate", new JSONObject("{test: true}"), new Ack() {
                        @Override
                        public void call(Object... args) {
                            values.offer(args[0]);
                        }
                    });
                } catch (JSONException e) {
                    throw new AssertionError(e);
                }
            }
        });
        socket.connect();
        Assert.assertThat(values.take(), CoreMatchers.instanceOf(String.class));
        socket.close();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void sendBinaryAck() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        final byte[] buf = "huehue".getBytes(Charset.forName("UTF-8"));
        socket = client();
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                socket.emit("callAckBinary");
                socket.on("ack", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Ack fn = ((Ack) (args[0]));
                        fn.call(buf);
                    }
                });
                socket.on("ackBack", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        byte[] data = ((byte[]) (args[0]));
                        values.offer(data);
                    }
                });
            }
        });
        socket.connect();
        Assert.assertArrayEquals(buf, ((byte[]) (values.take())));
        socket.close();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void receiveBinaryDataWithAck() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        final byte[] buf = "huehue".getBytes(Charset.forName("UTF-8"));
        socket = client();
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                socket.emit("getAckBinary", "", new Ack() {
                    @Override
                    public void call(Object... args) {
                        values.offer(args[0]);
                    }
                });
            }
        });
        socket.connect();
        Assert.assertArrayEquals(buf, ((byte[]) (values.take())));
        socket.close();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void workWithFalse() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        socket = client();
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                socket.emit("echo", false);
                socket.on("echoBack", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        values.offer(args[0]);
                    }
                });
            }
        });
        socket.connect();
        Assert.assertThat(((Boolean) (values.take())), CoreMatchers.is(false));
        socket.close();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void receiveUTF8MultibyteCharacters() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        final String[] correct = new String[]{ "???", "? ? ? ? ? ?", "? ? ? ? ?", "utf8 ? string", "utf8 ? string" };
        socket = client();
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                socket.on("echoBack", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        values.offer(args[0]);
                    }
                });
                for (String data : correct) {
                    socket.emit("echo", data);
                }
            }
        });
        socket.connect();
        for (String expected : correct) {
            Assert.assertThat(((String) (values.take())), CoreMatchers.is(expected));
        }
        socket.close();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void connectToNamespaceAfterConnectionEstablished() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        final Manager manager = new Manager(new URI(uri()));
        socket = manager.socket("/");
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                final Socket foo = manager.socket("/foo");
                foo.on(EVENT_CONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        foo.close();
                        socket.close();
                        manager.close();
                        values.offer("done");
                    }
                });
                foo.open();
            }
        });
        socket.open();
        values.take();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void connectToNamespaceAfterConnectionGetsClosed() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        final Manager manager = new Manager(new URI(uri()));
        socket = manager.socket("/");
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                socket.close();
            }
        }).on(EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                final Socket foo = manager.socket("/foo");
                foo.on(EVENT_CONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        foo.close();
                        manager.close();
                        values.offer("done");
                    }
                });
                foo.open();
            }
        });
        socket.open();
        values.take();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void reconnectByDefault() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        socket = client();
        socket.io().on(EVENT_RECONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                socket.close();
                values.offer("done");
            }
        });
        socket.open();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                socket.io().engine.close();
            }
        }, 500);
        values.take();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void reconnectManually() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        socket = client();
        socket.once(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.disconnect();
            }
        }).once(EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.once(EVENT_CONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        socket.disconnect();
                        values.offer("done");
                    }
                });
                socket.connect();
            }
        });
        socket.connect();
        values.take();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void reconnectAutomaticallyAfterReconnectingManually() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        socket = client();
        socket.once(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.disconnect();
            }
        }).once(EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.on(Socket.EVENT_RECONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        socket.disconnect();
                        values.offer("done");
                    }
                });
                socket.connect();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        socket.io().engine.close();
                    }
                }, 500);
            }
        });
        socket.connect();
        values.take();
    }

    @Test(timeout = 14000)
    public void attemptReconnectsAfterAFailedReconnect() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        IO.Options opts = createOptions();
        opts.reconnection = true;
        opts.timeout = 0;
        opts.reconnectionAttempts = 2;
        opts.reconnectionDelay = 10;
        final Manager manager = new Manager(new URI(uri()), opts);
        socket = manager.socket("/timeout");
        socket.once(EVENT_RECONNECT_FAILED, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final int[] reconnects = new int[]{ 0 };
                Emitter.Listener reconnectCb = new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        (reconnects[0])++;
                    }
                };
                manager.on(EVENT_RECONNECT_ATTEMPT, reconnectCb);
                manager.on(Manager.EVENT_RECONNECT_FAILED, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        values.offer(reconnects[0]);
                    }
                });
                socket.connect();
            }
        });
        socket.connect();
        Assert.assertThat(((Integer) (values.take())), CoreMatchers.is(2));
        socket.close();
        manager.close();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void reconnectDelayShouldIncreaseEveryTime() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        IO.Options opts = createOptions();
        opts.reconnection = true;
        opts.timeout = 0;
        opts.reconnectionAttempts = 3;
        opts.reconnectionDelay = 100;
        opts.randomizationFactor = 0.2;
        final Manager manager = new Manager(new URI(uri()), opts);
        socket = manager.socket("/timeout");
        final int[] reconnects = new int[]{ 0 };
        final boolean[] increasingDelay = new boolean[]{ true };
        final long[] startTime = new long[]{ 0 };
        final long[] prevDelay = new long[]{ 0 };
        socket.on(EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                startTime[0] = new Date().getTime();
            }
        });
        socket.on(Socket.EVENT_RECONNECT_ATTEMPT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                (reconnects[0])++;
                long currentTime = new Date().getTime();
                long delay = currentTime - (startTime[0]);
                if (delay <= (prevDelay[0])) {
                    increasingDelay[0] = false;
                }
                prevDelay[0] = delay;
            }
        });
        socket.on(EVENT_RECONNECT_FAILED, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                values.offer(true);
            }
        });
        socket.connect();
        values.take();
        Assert.assertThat(reconnects[0], CoreMatchers.is(3));
        Assert.assertThat(increasingDelay[0], CoreMatchers.is(true));
        socket.close();
        manager.close();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void reconnectEventFireInSocket() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        socket = client();
        socket.on(Socket.EVENT_RECONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                values.offer("done");
            }
        });
        socket.open();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                socket.io().engine.close();
            }
        }, 500);
        values.take();
        socket.close();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void notReconnectWhenForceClosed() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        IO.Options opts = createOptions();
        opts.timeout = 0;
        opts.reconnectionDelay = 10;
        socket = IO.socket(((uri()) + "/invalid"), opts);
        socket.on(EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.on(Socket.EVENT_RECONNECT_ATTEMPT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        values.offer(false);
                    }
                });
                socket.disconnect();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        values.offer(true);
                    }
                }, 500);
            }
        });
        socket.connect();
        Assert.assertThat(((Boolean) (values.take())), CoreMatchers.is(true));
    }

    @Test(timeout = Connection.TIMEOUT)
    public void stopReconnectingWhenForceClosed() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        IO.Options opts = createOptions();
        opts.timeout = 0;
        opts.reconnectionDelay = 10;
        socket = IO.socket(((uri()) + "/invalid"), opts);
        socket.once(Socket.EVENT_RECONNECT_ATTEMPT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.on(Socket.EVENT_RECONNECT_ATTEMPT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        values.offer(false);
                    }
                });
                socket.disconnect();
                // set a timer to let reconnection possibly fire
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        values.offer(true);
                    }
                }, 500);
            }
        });
        socket.connect();
        Assert.assertThat(((Boolean) (values.take())), CoreMatchers.is(true));
    }

    @Test(timeout = Connection.TIMEOUT)
    public void reconnectAfterStoppingReconnection() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        IO.Options opts = createOptions();
        opts.forceNew = true;
        opts.timeout = 0;
        opts.reconnectionDelay = 10;
        socket = client("/invalid", opts);
        socket.once(Socket.EVENT_RECONNECT_ATTEMPT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.once(Socket.EVENT_RECONNECT_ATTEMPT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        values.offer("done");
                    }
                });
                socket.disconnect();
                socket.connect();
            }
        });
        socket.connect();
        values.take();
        socket.disconnect();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void stopReconnectingOnASocketAndKeepToReconnectOnAnother() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        final Manager manager = new Manager(new URI(uri()));
        final Socket socket1 = manager.socket("/");
        final Socket socket2 = manager.socket("/asd");
        manager.on(EVENT_RECONNECT_ATTEMPT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket1.on(EVENT_CONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        values.offer(false);
                    }
                });
                socket2.on(EVENT_CONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                socket2.disconnect();
                                manager.close();
                                values.offer(true);
                            }
                        }, 500);
                    }
                });
                socket1.disconnect();
            }
        });
        socket1.connect();
        socket2.connect();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                manager.engine.close();
            }
        }, 1000);
        Assert.assertThat(((Boolean) (values.take())), CoreMatchers.is(true));
    }

    @Test(timeout = Connection.TIMEOUT)
    public void connectWhileDisconnectingAnotherSocket() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        final Manager manager = new Manager(new URI(uri()));
        final Socket socket1 = manager.socket("/foo");
        socket1.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final Socket socket2 = manager.socket("/asd");
                socket2.on(EVENT_CONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        values.offer("done");
                        socket2.disconnect();
                    }
                });
                socket2.open();
                socket1.disconnect();
            }
        });
        socket1.open();
        values.take();
        manager.close();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void tryToReconnectTwiceAndFailWithIncorrectAddress() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        IO.Options opts = new IO.Options();
        opts.reconnection = true;
        opts.reconnectionAttempts = 2;
        opts.reconnectionDelay = 10;
        final Manager manager = new Manager(new URI("http://localhost:3940"), opts);
        socket = manager.socket("/asd");
        final int[] reconnects = new int[]{ 0 };
        Emitter.Listener cb = new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                (reconnects[0])++;
            }
        };
        manager.on(EVENT_RECONNECT_ATTEMPT, cb);
        manager.on(Manager.EVENT_RECONNECT_FAILED, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                values.offer(reconnects[0]);
            }
        });
        socket.open();
        Assert.assertThat(((Integer) (values.take())), CoreMatchers.is(2));
        socket.close();
        manager.close();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void tryToReconnectTwiceAndFailWithImmediateTimeout() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        IO.Options opts = new IO.Options();
        opts.reconnection = true;
        opts.timeout = 0;
        opts.reconnectionAttempts = 2;
        opts.reconnectionDelay = 10;
        final Manager manager = new Manager(new URI(uri()), opts);
        final int[] reconnects = new int[]{ 0 };
        Emitter.Listener reconnectCb = new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                (reconnects[0])++;
            }
        };
        manager.on(EVENT_RECONNECT_ATTEMPT, reconnectCb);
        manager.on(Manager.EVENT_RECONNECT_FAILED, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                socket.close();
                manager.close();
                values.offer(reconnects[0]);
            }
        });
        socket = manager.socket("/timeout");
        socket.open();
        Assert.assertThat(((Integer) (values.take())), CoreMatchers.is(2));
    }

    @Test(timeout = Connection.TIMEOUT)
    public void notTryToReconnectWithIncorrectPortWhenReconnectionDisabled() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        IO.Options opts = new IO.Options();
        opts.reconnection = false;
        final Manager manager = new Manager(new URI("http://localhost:9823"), opts);
        Emitter.Listener cb = new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                socket.close();
                throw new RuntimeException();
            }
        };
        manager.on(EVENT_RECONNECT_ATTEMPT, cb);
        manager.on(Manager.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        socket.close();
                        manager.close();
                        values.offer("done");
                    }
                }, 1000);
            }
        });
        socket = manager.socket("/invalid");
        socket.open();
        values.take();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void fireReconnectEventsOnSocket() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        Manager.Options opts = new Manager.Options();
        opts.reconnection = true;
        opts.timeout = 0;
        opts.reconnectionAttempts = 2;
        opts.reconnectionDelay = 10;
        final Manager manager = new Manager(new URI(uri()), opts);
        socket = manager.socket("/timeout_socket");
        final int[] reconnects = new int[]{ 0 };
        Emitter.Listener reconnectCb = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                (reconnects[0])++;
                values.offer(args[0]);
            }
        };
        socket.on(Socket.EVENT_RECONNECT_ATTEMPT, reconnectCb);
        socket.on(EVENT_RECONNECT_FAILED, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                socket.close();
                manager.close();
                values.offer(reconnects[0]);
            }
        });
        socket.open();
        Assert.assertThat(((Integer) (values.take())), CoreMatchers.is(reconnects[0]));
        Assert.assertThat(((Integer) (values.take())), CoreMatchers.is(2));
    }

    @Test(timeout = Connection.TIMEOUT)
    public void fireReconnectingWithAttemptsNumberWhenReconnectingTwice() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        Manager.Options opts = new Manager.Options();
        opts.reconnection = true;
        opts.timeout = 0;
        opts.reconnectionAttempts = 2;
        opts.reconnectionDelay = 10;
        final Manager manager = new Manager(new URI(uri()), opts);
        socket = manager.socket("/timeout_socket");
        final int[] reconnects = new int[]{ 0 };
        Emitter.Listener reconnectCb = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                (reconnects[0])++;
                values.offer(args[0]);
            }
        };
        socket.on(EVENT_RECONNECTING, reconnectCb);
        socket.on(EVENT_RECONNECT_FAILED, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                socket.close();
                manager.close();
                values.offer(reconnects[0]);
            }
        });
        socket.open();
        Assert.assertThat(((Integer) (values.take())), CoreMatchers.is(reconnects[0]));
        Assert.assertThat(((Integer) (values.take())), CoreMatchers.is(2));
    }

    @Test(timeout = Connection.TIMEOUT)
    public void emitDateAsString() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        socket = client();
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                socket.emit("echo", new Date());
                socket.on("echoBack", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        socket.close();
                        values.offer(args[0]);
                    }
                });
            }
        });
        socket.connect();
        Assert.assertThat(values.take(), CoreMatchers.instanceOf(String.class));
    }

    @Test(timeout = Connection.TIMEOUT)
    public void emitDateInObject() throws InterruptedException, URISyntaxException, JSONException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        socket = client();
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                JSONObject data = new JSONObject();
                try {
                    data.put("date", new Date());
                } catch (JSONException e) {
                    throw new AssertionError(e);
                }
                socket.emit("echo", data);
                socket.on("echoBack", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        values.offer(args[0]);
                    }
                });
            }
        });
        socket.connect();
        Object data = values.take();
        Assert.assertThat(data, CoreMatchers.instanceOf(JSONObject.class));
        Assert.assertThat(get("date"), CoreMatchers.instanceOf(String.class));
        socket.close();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void sendAndGetBinaryData() throws InterruptedException, URISyntaxException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        final byte[] buf = "asdfasdf".getBytes(Charset.forName("UTF-8"));
        socket = client();
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.emit("echo", buf);
                socket.on("echoBack", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        values.offer(args[0]);
                    }
                });
            }
        });
        socket.open();
        Assert.assertThat(((byte[]) (values.take())), CoreMatchers.is(buf));
        socket.close();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void sendBinaryDataMixedWithJson() throws InterruptedException, URISyntaxException, JSONException {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        final byte[] buf = "howdy".getBytes(Charset.forName("UTF-8"));
        socket = client();
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = new JSONObject();
                try {
                    data.put("hello", "lol");
                    data.put("message", buf);
                    data.put("goodbye", "gotcha");
                } catch (JSONException e) {
                    throw new AssertionError(e);
                }
                socket.emit("echo", data);
                socket.on("echoBack", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        values.offer(args[0]);
                    }
                });
            }
        });
        socket.open();
        JSONObject a = ((JSONObject) (values.take()));
        Assert.assertThat(a.getString("hello"), CoreMatchers.is("lol"));
        Assert.assertThat(((byte[]) (a.get("message"))), CoreMatchers.is(buf));
        Assert.assertThat(a.getString("goodbye"), CoreMatchers.is("gotcha"));
        socket.close();
    }

    @Test(timeout = Connection.TIMEOUT)
    public void sendEventsWithByteArraysInTheCorrectOrder() throws Exception {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<Object>();
        final byte[] buf = "abuff1".getBytes(Charset.forName("UTF-8"));
        socket = client();
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.emit("echo", buf);
                socket.emit("echo", "please arrive second");
                socket.on("echoBack", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        values.offer(args[0]);
                    }
                });
            }
        });
        socket.open();
        Assert.assertThat(((byte[]) (values.take())), CoreMatchers.is(buf));
        Assert.assertThat(((String) (values.take())), CoreMatchers.is("please arrive second"));
        socket.close();
    }
}

