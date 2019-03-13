package org.zeromq;


import SocketType.PULL;
import SocketType.PUSH;
import ZMQ.CHARSET;
import ZMQ.Context;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.CharacterCodingException;
import org.junit.Assert;
import org.junit.Test;
import org.zeromq.ZMQ.Socket;

import static ZMQ.CHARSET;


public class ByteBuffersTest {
    static class Client extends Thread {
        private int port = -1;

        public Client(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            System.out.println("Start client thread ");
            ZMQ.Context context = ZMQ.context(1);
            Socket pullConnect = context.socket(PULL);
            pullConnect.connect(("tcp://127.0.0.1:" + (port)));
            pullConnect.recv(0);
            pullConnect.close();
            context.close();
            System.out.println("Stop client thread ");
        }
    }

    @Test
    public void testByteBufferSend() throws IOException, InterruptedException {
        int port = Utils.findOpenPort();
        ZMQ.Context context = ZMQ.context(1);
        ByteBuffer bb = ByteBuffer.allocate(4).order(ByteOrder.nativeOrder());
        ZMQ.Socket push = null;
        ZMQ.Socket pull = null;
        try {
            push = context.socket(PUSH);
            pull = context.socket(PULL);
            pull.bind(("tcp://*:" + port));
            push.connect(("tcp://localhost:" + port));
            bb.put("PING".getBytes(CHARSET));
            bb.flip();
            Thread.sleep(1000);
            push.sendByteBuffer(bb, 0);
            String actual = new String(pull.recv(), CHARSET);
            Assert.assertEquals("PING", actual);
        } finally {
            try {
                push.close();
            } catch (Exception ignore) {
            }
            try {
                pull.close();
            } catch (Exception ignore) {
            }
            try {
                context.term();
            } catch (Exception ignore) {
            }
        }
    }

    @Test
    public void testByteBufferRecv() throws IOException, InterruptedException, CharacterCodingException {
        int port = Utils.findOpenPort();
        ZMQ.Context context = ZMQ.context(1);
        ByteBuffer bb = ByteBuffer.allocate(6).order(ByteOrder.nativeOrder());
        ZMQ.Socket push = null;
        ZMQ.Socket pull = null;
        try {
            push = context.socket(PUSH);
            pull = context.socket(PULL);
            pull.bind(("tcp://*:" + port));
            push.connect(("tcp://localhost:" + port));
            Thread.sleep(1000);
            push.send("PING".getBytes(CHARSET), 0);
            pull.recvByteBuffer(bb, 0);
            bb.flip();
            byte[] b = new byte[bb.remaining()];
            bb.duplicate().get(b);
            Assert.assertEquals("PING", new String(b, CHARSET));
        } finally {
            try {
                push.close();
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
            try {
                pull.close();
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
            try {
                context.term();
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }
    }

    @Test
    public void testByteBufferLarge() throws IOException, InterruptedException, CharacterCodingException {
        int port = Utils.findOpenPort();
        ZMQ.Context context = ZMQ.context(1);
        int[] array = new int[2048 * 2000];
        for (int i = 0; i < (array.length); ++i) {
            array[i] = i;
        }
        ByteBuffer bSend = ByteBuffer.allocate((((Integer.SIZE) / 8) * (array.length))).order(ByteOrder.nativeOrder());
        bSend.asIntBuffer().put(array);
        ByteBuffer bRec = ByteBuffer.allocate(bSend.capacity()).order(ByteOrder.nativeOrder());
        int size = (bSend.capacity()) / (1024 * 1024);
        System.out.println((("Test sending large message (~" + size) + "Mb)"));
        ZMQ.Socket push = null;
        ZMQ.Socket pull = null;
        try {
            push = context.socket(PUSH);
            pull = context.socket(PULL);
            pull.bind(("tcp://*:" + port));
            push.connect(("tcp://localhost:" + port));
            Thread.sleep(1000);
            long start = System.currentTimeMillis();
            push.sendByteBuffer(bSend, 0);
            pull.recvByteBuffer(bRec, 0);
            long end = System.currentTimeMillis();
            System.out.println((((("Received ~" + size) + "Mb msg in ") + (end - start)) + " millisec."));
            bRec.flip();
            Assert.assertEquals(bSend, bRec);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        } finally {
            try {
                push.close();
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
            try {
                pull.close();
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
            try {
                context.term();
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }
    }

    @Test
    public void testByteBufferLargeDirect() throws IOException, InterruptedException, CharacterCodingException {
        int port = Utils.findOpenPort();
        ZMQ.Context context = ZMQ.context(1);
        int[] array = new int[2048 * 2000];
        for (int i = 0; i < (array.length); ++i) {
            array[i] = i;
        }
        ByteBuffer bSend = ByteBuffer.allocateDirect((((Integer.SIZE) / 8) * (array.length))).order(ByteOrder.nativeOrder());
        bSend.asIntBuffer().put(array);
        ByteBuffer bRec = ByteBuffer.allocateDirect(bSend.capacity()).order(ByteOrder.nativeOrder());
        int[] recArray = new int[array.length];
        int size = (bSend.capacity()) / (1024 * 1024);
        System.out.println((("Test sending direct large message (~" + size) + "Mb)"));
        ZMQ.Socket push = null;
        ZMQ.Socket pull = null;
        try {
            push = context.socket(PUSH);
            pull = context.socket(PULL);
            pull.bind(("tcp://*:" + port));
            push.connect(("tcp://localhost:" + port));
            Thread.sleep(1000);
            long start = System.currentTimeMillis();
            push.sendByteBuffer(bSend, 0);
            pull.recvByteBuffer(bRec, 0);
            long end = System.currentTimeMillis();
            System.out.println((((("Received ~" + size) + "Mb msg in ") + (end - start)) + " millisec."));
            bRec.flip();
            bRec.asIntBuffer().get(recArray);
            Assert.assertArrayEquals(array, recArray);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        } finally {
            try {
                push.close();
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
            try {
                pull.close();
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
            try {
                context.term();
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }
    }
}

