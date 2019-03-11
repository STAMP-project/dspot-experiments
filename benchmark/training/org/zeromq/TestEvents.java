package org.zeromq;


import SocketType.PAIR;
import SocketType.REP;
import SocketType.REQ;
import ZMQ.EVENT_ACCEPTED;
import ZMQ.EVENT_BIND_FAILED;
import ZMQ.EVENT_CLOSED;
import ZMQ.EVENT_CONNECTED;
import ZMQ.EVENT_CONNECT_DELAYED;
import ZMQ.EVENT_CONNECT_RETRIED;
import ZMQ.EVENT_DISCONNECTED;
import ZMQ.EVENT_LISTENING;
import ZMQ.EVENT_MONITOR_STOPPED;
import ZMQ.Event;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;


public class TestEvents {
    @Test
    public void testEventConnected() {
        Context context = ZMQ.context(1);
        ZMQ.Event event;
        Socket helper = context.socket(REQ);
        int port = helper.bindToRandomPort("tcp://127.0.0.1");
        Socket socket = context.socket(REP);
        Socket monitor = context.socket(PAIR);
        monitor.setReceiveTimeOut(100);
        Assert.assertTrue(socket.monitor("inproc://monitor.socket", EVENT_CONNECTED));
        monitor.connect("inproc://monitor.socket");
        socket.connect(("tcp://127.0.0.1:" + port));
        event = Event.recv(monitor);
        Assert.assertNotNull("No event was received", event);
        Assert.assertEquals(EVENT_CONNECTED, event.getEvent());
        helper.close();
        socket.close();
        monitor.close();
        context.term();
    }

    @Test
    public void testEventConnectDelayed() throws IOException {
        Context context = ZMQ.context(1);
        ZMQ.Event event;
        Socket socket = context.socket(REP);
        Socket monitor = context.socket(PAIR);
        monitor.setReceiveTimeOut(100);
        Assert.assertTrue(socket.monitor("inproc://monitor.socket", EVENT_CONNECT_DELAYED));
        monitor.connect("inproc://monitor.socket");
        int randomPort = Utils.findOpenPort();
        socket.connect(("tcp://127.0.0.1:" + randomPort));
        event = Event.recv(monitor);
        Assert.assertNotNull("No event was received", event);
        Assert.assertEquals(EVENT_CONNECT_DELAYED, event.getEvent());
        socket.close();
        monitor.close();
        context.term();
    }

    @Test
    public void testEventConnectRetried() throws IOException, InterruptedException {
        Context context = ZMQ.context(1);
        ZMQ.Event event;
        Socket socket = context.socket(REP);
        Socket monitor = context.socket(PAIR);
        monitor.setReceiveTimeOut(100);
        Assert.assertTrue(socket.monitor("inproc://monitor.socket", EVENT_CONNECT_RETRIED));
        monitor.connect("inproc://monitor.socket");
        int randomPort = Utils.findOpenPort();
        socket.connect(("tcp://127.0.0.1:" + randomPort));
        Thread.sleep(1000L);// on windows, this is required, otherwise test fails

        event = Event.recv(monitor);
        Assert.assertNotNull("No event was received", event);
        Assert.assertEquals(EVENT_CONNECT_RETRIED, event.getEvent());
        socket.close();
        monitor.close();
        context.term();
    }

    @Test
    public void testEventListening() {
        Context context = ZMQ.context(1);
        ZMQ.Event event;
        Socket socket = context.socket(REP);
        Socket monitor = context.socket(PAIR);
        monitor.setReceiveTimeOut(100);
        Assert.assertTrue(socket.monitor("inproc://monitor.socket", EVENT_LISTENING));
        monitor.connect("inproc://monitor.socket");
        socket.bindToRandomPort("tcp://127.0.0.1");
        event = Event.recv(monitor);
        Assert.assertNotNull("No event was received", event);
        Assert.assertEquals(EVENT_LISTENING, event.getEvent());
        socket.close();
        monitor.close();
        context.term();
    }

    @Test
    public void testEventBindFailed() {
        Context context = ZMQ.context(1);
        ZMQ.Event event;
        Socket helper = context.socket(REP);
        int port = helper.bindToRandomPort("tcp://127.0.0.1");
        Socket socket = context.socket(REP);
        Socket monitor = context.socket(PAIR);
        monitor.setReceiveTimeOut(100);
        Assert.assertTrue(socket.monitor("inproc://monitor.socket", EVENT_BIND_FAILED));
        monitor.connect("inproc://monitor.socket");
        try {
            socket.bind(("tcp://127.0.0.1:" + port));
        } catch (ZMQException ex) {
        }
        event = Event.recv(monitor);
        Assert.assertNotNull("No event was received", event);
        Assert.assertEquals(EVENT_BIND_FAILED, event.getEvent());
        helper.close();
        socket.close();
        monitor.close();
        context.term();
    }

    @Test
    public void testEventAccepted() {
        Context context = ZMQ.context(1);
        ZMQ.Event event;
        Socket socket = context.socket(REP);
        Socket monitor = context.socket(PAIR);
        Socket helper = context.socket(REQ);
        monitor.setReceiveTimeOut(100);
        Assert.assertTrue(socket.monitor("inproc://monitor.socket", EVENT_ACCEPTED));
        monitor.connect("inproc://monitor.socket");
        int port = socket.bindToRandomPort("tcp://127.0.0.1");
        helper.connect(("tcp://127.0.0.1:" + port));
        event = Event.recv(monitor);
        Assert.assertNotNull("No event was received", event);
        Assert.assertEquals(EVENT_ACCEPTED, event.getEvent());
        helper.close();
        socket.close();
        monitor.close();
        context.term();
    }

    @Test
    public void testEventClosed() {
        Context context = ZMQ.context(1);
        Socket monitor = context.socket(PAIR);
        try {
            ZMQ.Event event;
            Socket socket = context.socket(REP);
            monitor.setReceiveTimeOut(100);
            socket.bindToRandomPort("tcp://127.0.0.1");
            Assert.assertTrue(socket.monitor("inproc://monitor.socket", EVENT_CLOSED));
            monitor.connect("inproc://monitor.socket");
            socket.close();
            event = Event.recv(monitor);
            Assert.assertNotNull("No event was received", event);
            Assert.assertEquals(EVENT_CLOSED, event.getEvent());
        } finally {
            monitor.close();
            context.term();
        }
    }

    @Test
    public void testEventDisconnected() {
        Context context = ZMQ.context(1);
        ZMQ.Event event;
        Socket socket = context.socket(REP);
        Socket monitor = context.socket(PAIR);
        Socket helper = context.socket(REQ);
        monitor.setReceiveTimeOut(100);
        int port = socket.bindToRandomPort("tcp://127.0.0.1");
        helper.connect(("tcp://127.0.0.1:" + port));
        Assert.assertTrue(socket.monitor("inproc://monitor.socket", EVENT_DISCONNECTED));
        monitor.connect("inproc://monitor.socket");
        zmq.ZMQ.sleep(1);
        helper.close();
        event = Event.recv(monitor);
        Assert.assertNotNull("No event was received", event);
        Assert.assertEquals(EVENT_DISCONNECTED, event.getEvent());
        socket.close();
        monitor.close();
        context.term();
    }

    @Test
    public void testEventMonitorStopped() {
        Context context = ZMQ.context(1);
        ZMQ.Event event;
        Socket socket = context.socket(REP);
        Socket monitor = context.socket(PAIR);
        monitor.setReceiveTimeOut(100);
        Assert.assertTrue(socket.monitor("inproc://monitor.socket", EVENT_MONITOR_STOPPED));
        monitor.connect("inproc://monitor.socket");
        socket.monitor(null, 0);
        event = Event.recv(monitor);
        Assert.assertNotNull("No event was received", event);
        Assert.assertEquals(EVENT_MONITOR_STOPPED, event.getEvent());
        socket.close();
        monitor.close();
        context.term();
    }
}

