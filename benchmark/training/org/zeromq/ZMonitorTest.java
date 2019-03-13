package org.zeromq;


import Event.ACCEPTED;
import Event.ACCEPT_FAILED;
import Event.CONNECTED;
import Event.DISCONNECTED;
import Event.HANDSHAKE_PROTOCOL;
import Event.LISTENING;
import SocketType.DEALER;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMonitor.ZEvent;


public class ZMonitorTest {
    @Test
    public void testZMonitorImpossibleWorkflows() throws IOException {
        final ZContext ctx = new ZContext();
        final Socket socket = ctx.createSocket(DEALER);
        final ZMonitor monitor = new ZMonitor(ctx, socket);
        // impossible to monitor events before being started
        ZEvent event = monitor.nextEvent();
        Assert.assertThat(event, CoreMatchers.nullValue());
        event = monitor.nextEvent((-1));
        Assert.assertThat(event, CoreMatchers.nullValue());
        monitor.start();
        // all no-ops commands once ZMonitor is started
        monitor.add().remove().verbose(false).start();
        socket.close();
        monitor.close();
        ctx.close();
    }

    @Test
    public void testZMonitor() throws IOException {
        final ZContext ctx = new ZContext();
        final Socket client = ctx.createSocket(DEALER);
        final Socket server = ctx.createSocket(DEALER);
        final ZMonitor clientMonitor = new ZMonitor(ctx, client);
        clientMonitor.verbose(true);
        clientMonitor.add(LISTENING, CONNECTED, DISCONNECTED, ACCEPT_FAILED);
        clientMonitor.remove(ACCEPT_FAILED);
        clientMonitor.start();
        final ZMonitor serverMonitor = new ZMonitor(ctx, server);
        serverMonitor.verbose(false);
        serverMonitor.add(LISTENING, ACCEPTED, HANDSHAKE_PROTOCOL);
        serverMonitor.start();
        // Check server is now listening
        int port = server.bindToRandomPort("tcp://127.0.0.1");
        ZEvent received = serverMonitor.nextEvent();
        Assert.assertThat(received.type, CoreMatchers.is(LISTENING));
        // Check server connected to client
        boolean rc = client.connect(("tcp://127.0.0.1:" + port));
        Assert.assertThat(rc, CoreMatchers.is(true));
        received = clientMonitor.nextEvent();
        Assert.assertThat(received.type, CoreMatchers.is(CONNECTED));
        // Check server accepted connection
        received = serverMonitor.nextEvent(true);
        Assert.assertThat(received.type, CoreMatchers.is(ACCEPTED));
        System.out.println(((("server @ tcp://127.0.0.1:" + port) + " received ") + (received.toString())));
        // Check server accepted connection
        received = serverMonitor.nextEvent((-1));// timeout -1 aka blocking

        Assert.assertThat(received.type, CoreMatchers.is(HANDSHAKE_PROTOCOL));
        received = serverMonitor.nextEvent(false);// with no blocking

        Assert.assertThat(received, CoreMatchers.nullValue());
        received = serverMonitor.nextEvent(10);// timeout

        Assert.assertThat(received, CoreMatchers.nullValue());
        client.close();
        clientMonitor.close();
        server.close();
        serverMonitor.close();
        ctx.close();
    }
}

