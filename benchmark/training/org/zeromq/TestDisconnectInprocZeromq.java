package org.zeromq;


import SocketType.SUB;
import SocketType.XPUB;
import ZMQ.CHARSET;
import ZMQ.Poller;
import ZMQ.Poller.POLLIN;
import ZMQ.SNDMORE;
import ZMQ.Socket;
import org.junit.Assert;
import org.junit.Test;


public class TestDisconnectInprocZeromq {
    @Test
    public void testDisconnectInproc() throws Exception {
        int publicationsReceived = 0;
        boolean isSubscribed = false;
        ZContext ctx = new ZContext();
        ZMQ.Socket pubSocket = ctx.createSocket(XPUB);
        ZMQ.Socket subSocket = ctx.createSocket(SUB);
        subSocket.subscribe("foo".getBytes());
        pubSocket.bind("inproc://someInProcDescriptor");
        int iteration = 0;
        ZMQ.Poller poller = ctx.createPoller(2);
        poller.register(subSocket, POLLIN);// read publications

        poller.register(pubSocket, POLLIN);// read subscriptions

        while (true) {
            poller.poll(500);
            if (poller.pollin(1)) {
                while (true) {
                    byte[] buffer = pubSocket.recv(0);
                    int msgSize = buffer.length;
                    if ((buffer[0]) == 0) {
                        Assert.assertTrue(isSubscribed);
                        System.out.printf("unsubscribing from \'%s\'\n", new String(buffer, 1, (msgSize - 1)));
                        isSubscribed = false;
                    } else {
                        assert !isSubscribed;
                        System.out.printf("subscribing on \'%s\'\n", new String(buffer, 1, (msgSize - 1)));
                        isSubscribed = true;
                    }
                    if (!(pubSocket.hasReceiveMore())) {
                        break;// Last message part

                    }
                } 
            }
            if (poller.pollin(0)) {
                while (true) {
                    byte[] buffer = subSocket.recv(0);
                    int msgSize = buffer.length;
                    System.out.printf("received on subscriber \'%s\'\n", new String(buffer, 0, msgSize));
                    if (!(subSocket.hasReceiveMore())) {
                        publicationsReceived++;
                        break;// Last message part

                    }
                } 
            }
            if (iteration == 1) {
                subSocket.connect("inproc://someInProcDescriptor");
            }
            if (iteration == 4) {
                subSocket.disconnect("inproc://someInProcDescriptor");
            }
            if (iteration == 10) {
                break;
            }
            pubSocket.send("foo".getBytes(CHARSET), SNDMORE);
            pubSocket.send("this is foo!".getBytes(CHARSET), 0);
            iteration++;
        } 
        Assert.assertEquals(3, publicationsReceived);
        Assert.assertTrue((!isSubscribed));
        ctx.close();
    }
}

