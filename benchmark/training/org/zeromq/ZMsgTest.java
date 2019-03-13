package org.zeromq;


import SocketType.PAIR;
import SocketType.PULL;
import SocketType.PUSH;
import ZError.ETERM;
import ZMQ.Context;
import ZMQ.NOBLOCK;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.zeromq.ZMQ.Socket;


/**
 * Created by hartmann on 3/21/14.
 */
public class ZMsgTest {
    @Test
    public void testRecvFrame() throws Exception {
        ZMQ.Context ctx = ZMQ.context(0);
        ZMQ.Socket socket = ctx.socket(PULL);
        ZFrame frame = ZFrame.recvFrame(socket, NOBLOCK);
        Assert.assertThat(frame, CoreMatchers.nullValue());
        socket.close();
        ctx.close();
    }

    @Test
    public void testRecvMsg() throws Exception {
        ZMQ.Context ctx = ZMQ.context(0);
        ZMQ.Socket socket = ctx.socket(PULL);
        ZMsg.recvMsg(socket, NOBLOCK, ( msg) -> assertThat(msg, nullValue()));
        socket.close();
        ctx.close();
    }

    @Test
    public void testRecvNullByteMsg() throws Exception {
        ZMQ.Context ctx = ZMQ.context(0);
        ZMQ.Socket sender = ctx.socket(PUSH);
        ZMQ.Socket receiver = ctx.socket(PULL);
        receiver.bind(("inproc://" + (this.hashCode())));
        sender.connect(("inproc://" + (this.hashCode())));
        sender.send(new byte[0]);
        ZMsg msg = ZMsg.recvMsg(receiver, NOBLOCK);
        Assert.assertThat(msg, CoreMatchers.notNullValue());
        sender.close();
        receiver.close();
        ctx.close();
    }

    @Test
    public void testContentSize() {
        ZMsg msg = new ZMsg();
        msg.add(new byte[0]);
        Assert.assertThat(msg.contentSize(), CoreMatchers.is(0L));
        msg.add(new byte[1]);
        Assert.assertThat(msg.contentSize(), CoreMatchers.is(1L));
    }

    @Test
    public void testEquals() {
        ZMsg msg = new ZMsg().addString("123");
        ZMsg other = new ZMsg();
        Assert.assertThat(msg.equals(msg), CoreMatchers.is(true));
        Assert.assertThat(msg.equals(null), CoreMatchers.is(false));
        Assert.assertThat(msg.equals(""), CoreMatchers.is(false));
        Assert.assertThat(msg.equals(other), CoreMatchers.is(false));
        other.add("345");
        Assert.assertThat(msg.equals(other), CoreMatchers.is(false));
        other.popString();
        other.add("123");
        Assert.assertThat(msg.equals(other), CoreMatchers.is(true));
        Assert.assertThat(msg.duplicate(), CoreMatchers.is(msg));
        msg.destroy();
    }

    @Test
    public void testHashcode() {
        ZMsg msg = new ZMsg();
        Assert.assertThat(msg.hashCode(), CoreMatchers.is(0));
        msg.add("123");
        ZMsg other = ZMsg.newStringMsg("123");
        Assert.assertThat(msg.hashCode(), CoreMatchers.is(other.hashCode()));
        other = new ZMsg().append("2");
        Assert.assertThat(msg.hashCode(), CoreMatchers.is(CoreMatchers.not(CoreMatchers.equalTo(other.hashCode()))));
    }

    @Test
    public void testDump() {
        ZMsg msg = new ZMsg().append(new byte[0]).append(new byte[]{ ((byte) (170)) });
        msg.dump();
        StringBuilder out = new StringBuilder();
        msg.dump(out);
        System.out.println(msg.toString());
        Assert.assertThat(out.toString(), CoreMatchers.endsWith("[000] \n[001] AA\n"));
    }

    @Test
    public void testWrapUnwrap() {
        ZMsg msg = new ZMsg().wrap(new ZFrame("456"));
        Assert.assertThat(msg.size(), CoreMatchers.is(2));
        ZFrame frame = msg.unwrap();
        Assert.assertThat(frame.toString(), CoreMatchers.is("456"));
        Assert.assertThat(msg.size(), CoreMatchers.is(0));
    }

    @Test
    public void testSaveLoad() {
        ZMsg msg = new ZMsg().append("123");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        ZMsg.save(msg, out);
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(stream.toByteArray()));
        ZMsg loaded = ZMsg.load(in);
        Assert.assertThat(msg, CoreMatchers.is(loaded));
    }

    @Test
    public void testAppend() {
        ZMsg msg = new ZMsg().append(((ZMsg) (null))).append(ZMsg.newStringMsg("123"));
        Assert.assertThat(msg.popString(), CoreMatchers.is("123"));
        msg.append(ZMsg.newStringMsg("123")).append(msg);
        Assert.assertThat(msg.size(), CoreMatchers.is(2));
        Assert.assertThat(msg.contentSize(), CoreMatchers.is(6L));
    }

    @Test
    public void testPop() {
        ZMsg msg = new ZMsg();
        Assert.assertThat(msg.popString(), CoreMatchers.nullValue());
    }

    @Test
    public void testRemoves() {
        ZMsg msg = ZMsg.newStringMsg("1", "2", "3", "4");
        Assert.assertThat(msg.remove(), CoreMatchers.is(new ZFrame("1")));
        Assert.assertThat(msg.removeLast(), CoreMatchers.is(new ZFrame("4")));
        Assert.assertThat(msg.removeFirst(), CoreMatchers.is(new ZFrame("2")));
        msg.add(new ZFrame("5"));
        msg.add(new ZFrame("6"));
        msg.add(new ZFrame("7"));
        Assert.assertThat(msg.size(), CoreMatchers.is(4));
        boolean removed = msg.removeFirstOccurrence(new ZFrame("5"));
        Assert.assertThat(removed, CoreMatchers.is(true));
        removed = msg.removeFirstOccurrence(new ZFrame("5"));
        Assert.assertThat(removed, CoreMatchers.is(false));
        removed = msg.removeFirstOccurrence(new ZFrame("7"));
        Assert.assertThat(removed, CoreMatchers.is(true));
        Assert.assertThat(msg.size(), CoreMatchers.is(2));
        msg.add(new ZFrame("6"));
        msg.add(new ZFrame("4"));
        msg.add(new ZFrame("6"));
        removed = msg.removeLastOccurrence(new ZFrame("6"));
        Assert.assertThat(removed, CoreMatchers.is(true));
        Assert.assertThat(msg.size(), CoreMatchers.is(4));
    }

    @Test
    public void testMessageEquals() {
        ZMsg msg = new ZMsg();
        ZFrame hello = new ZFrame("Hello");
        ZFrame world = new ZFrame("World");
        msg.add(hello);
        msg.add(world);
        Assert.assertThat(msg, CoreMatchers.is(msg.duplicate()));
        ZMsg reverseMsg = new ZMsg();
        msg.add(hello);
        msg.addFirst(world);
        Assert.assertThat(msg.equals(reverseMsg), CoreMatchers.is(false));
    }

    @Test
    public void testSingleFrameMessage() {
        ZContext ctx = new ZContext();
        Socket output = ctx.createSocket(PAIR);
        output.bind("inproc://zmsg.test");
        Socket input = ctx.createSocket(PAIR);
        input.connect("inproc://zmsg.test");
        // Test send and receive of a single ZMsg
        ZMsg msg = new ZMsg();
        ZFrame frame = new ZFrame("Hello");
        msg.addFirst(frame);
        Assert.assertThat(msg.size(), CoreMatchers.is(1));
        Assert.assertThat(msg.contentSize(), CoreMatchers.is(5L));
        msg.send(output);
        ZMsg msg2 = ZMsg.recvMsg(input);
        Assert.assertThat(msg2, CoreMatchers.notNullValue());
        Assert.assertThat(msg2.size(), CoreMatchers.is(1));
        Assert.assertThat(msg2.contentSize(), CoreMatchers.is(5L));
        msg.destroy();
        msg2.destroy();
        ctx.close();
    }

    @Test
    public void testMultiPart() {
        ZContext ctx = new ZContext();
        Socket output = ctx.createSocket(PAIR);
        output.bind("inproc://zmsg.test2");
        Socket input = ctx.createSocket(PAIR);
        input.connect("inproc://zmsg.test2");
        ZMsg msg = new ZMsg();
        for (int i = 0; i < 10; i++) {
            msg.addString(("Frame" + i));
        }
        ZMsg copy = msg.duplicate();
        copy.send(output);
        msg.send(output);
        copy = ZMsg.recvMsg(input);
        Assert.assertThat(copy, CoreMatchers.notNullValue());
        Assert.assertThat(copy.size(), CoreMatchers.is(10));
        Assert.assertThat(copy.contentSize(), CoreMatchers.is(60L));
        copy.destroy();
        msg = ZMsg.recvMsg(input);
        Assert.assertThat(msg, CoreMatchers.notNullValue());
        Assert.assertThat(msg.size(), CoreMatchers.is(10));
        int count = 0;
        for (ZFrame f : msg) {
            Assert.assertThat(f.streq(("Frame" + (count++))), CoreMatchers.is(true));
        }
        Assert.assertThat(msg.contentSize(), CoreMatchers.is(60L));
        msg.destroy();
        ctx.close();
    }

    @Test
    public void testMessageFrameManipulation() {
        ZMsg msg = new ZMsg();
        for (int i = 0; i < 10; i++) {
            msg.addString(("Frame" + i));
        }
        // Remove all frames apart from the first and last one
        for (int i = 0; i < 8; i++) {
            Iterator<ZFrame> iter = msg.iterator();
            iter.next();// Skip first frame

            ZFrame f = iter.next();
            msg.remove(f);
            f.destroy();
        }
        Assert.assertThat(msg.size(), CoreMatchers.is(2));
        Assert.assertThat(msg.contentSize(), CoreMatchers.is(12L));
        Assert.assertThat(msg.getFirst().streq("Frame0"), CoreMatchers.is(true));
        Assert.assertThat(msg.getLast().streq("Frame9"), CoreMatchers.is(true));
        ZFrame f = new ZFrame("Address");
        msg.push(f);
        Assert.assertThat(msg.size(), CoreMatchers.is(3));
        Assert.assertThat(msg.getFirst().streq("Address"), CoreMatchers.is(true));
        msg.addString("Body");
        Assert.assertThat(msg.size(), CoreMatchers.is(4));
        ZFrame f0 = msg.pop();
        Assert.assertThat(f0.streq("Address"), CoreMatchers.is(true));
        msg.destroy();
        msg = new ZMsg();
        f = new ZFrame("Address");
        msg.wrap(f);
        Assert.assertThat(msg.size(), CoreMatchers.is(2));
        msg.addString("Body");
        Assert.assertThat(msg.size(), CoreMatchers.is(3));
        f = msg.unwrap();
        f.destroy();
        Assert.assertThat(msg.size(), CoreMatchers.is(1));
        msg.destroy();
    }

    @Test
    public void testEmptyMessage() {
        ZMsg msg = new ZMsg();
        Assert.assertThat(msg.size(), CoreMatchers.is(0));
        Assert.assertThat(msg.getFirst(), CoreMatchers.nullValue());
        Assert.assertThat(msg.getLast(), CoreMatchers.nullValue());
        Assert.assertThat(msg.isEmpty(), CoreMatchers.is(true));
        Assert.assertThat(msg.pop(), CoreMatchers.nullValue());
        Assert.assertThat(msg.removeFirst(), CoreMatchers.nullValue());
        Assert.assertThat(msg.removeFirstOccurrence(null), CoreMatchers.is(false));
        Assert.assertThat(msg.removeLast(), CoreMatchers.nullValue());
        msg.destroy();
    }

    @Test
    public void testLoadSave() {
        ZMsg msg = new ZMsg();
        for (int i = 0; i < 10; i++) {
            msg.addString(("Frame" + i));
        }
        try {
            // Save msg to a file
            File f = new File("zmsg.test");
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(f));
            Assert.assertThat(ZMsg.save(msg, dos), CoreMatchers.is(true));
            dos.close();
            // Read msg out of the file
            DataInputStream dis = new DataInputStream(new FileInputStream(f));
            ZMsg msg2 = ZMsg.load(dis);
            dis.close();
            f.delete();
            Assert.assertThat(msg2.size(), CoreMatchers.is(10));
            Assert.assertThat(msg2.contentSize(), CoreMatchers.is(60L));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Assert.fail();
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testNewStringMessage() {
        // A single string => frame
        ZMsg msg = ZMsg.newStringMsg("Foo");
        Assert.assertThat(msg.size(), CoreMatchers.is(1));
        Assert.assertThat(msg.getFirst().streq("Foo"), CoreMatchers.is(true));
        // Multiple strings => frames
        ZMsg msg2 = ZMsg.newStringMsg("Foo", "Bar", "Baz");
        Assert.assertThat(msg2.size(), CoreMatchers.is(3));
        Assert.assertThat(msg2.getFirst().streq("Foo"), CoreMatchers.is(true));
        Assert.assertThat(msg2.getLast().streq("Baz"), CoreMatchers.is(true));
        // Empty message (Not very useful)
        ZMsg msg3 = ZMsg.newStringMsg();
        Assert.assertThat(msg3.isEmpty(), CoreMatchers.is(true));
    }

    @Test
    public void testClosedContext() {
        ZContext ctx = new ZContext();
        Socket output = ctx.createSocket(PAIR);
        output.bind("inproc://zmsg.test");
        Socket input = ctx.createSocket(PAIR);
        input.connect("inproc://zmsg.test");
        ZMsg msg = ZMsg.newStringMsg("Foo", "Bar");
        msg.send(output);
        ZMsg msg2 = ZMsg.recvMsg(input);
        Assert.assertThat(msg2.popString(), CoreMatchers.is("Foo"));
        Assert.assertThat(msg2.popString(), CoreMatchers.is("Bar"));
        msg2.destroy();
        msg.send(output);
        msg.destroy();
        ctx.close();
        try {
            ZMsg.recvMsg(input);
            Assert.fail();
        } catch (ZMQException e) {
            Assert.assertThat(e.getErrorCode(), CoreMatchers.is(ETERM));
        }
    }
}

