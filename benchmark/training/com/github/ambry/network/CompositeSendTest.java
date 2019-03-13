package com.github.ambry.network;


import com.github.ambry.utils.ByteBufferOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class CompositeSendTest {
    @Test
    public void testCompositeSend() throws IOException {
        byte[] buf1 = new byte[1024];
        byte[] buf2 = new byte[2048];
        byte[] buf3 = new byte[4096];
        new Random().nextBytes(buf1);
        new Random().nextBytes(buf2);
        new Random().nextBytes(buf3);
        ByteArraySend byteArraySend1 = new ByteArraySend(buf1);
        ByteArraySend byteArraySend2 = new ByteArraySend(buf2);
        ByteArraySend byteArraySend3 = new ByteArraySend(buf3);
        List<Send> listToSend = new ArrayList<Send>(3);
        listToSend.add(byteArraySend1);
        listToSend.add(byteArraySend2);
        listToSend.add(byteArraySend3);
        CompositeSend compositeSend = new CompositeSend(listToSend);
        ByteBuffer bufferToWrite = ByteBuffer.allocate(((1024 + 2048) + 4096));
        ByteBufferOutputStream bufferToWriteStream = new ByteBufferOutputStream(bufferToWrite);
        WritableByteChannel writableByteChannel = Channels.newChannel(bufferToWriteStream);
        while (!(compositeSend.isSendComplete())) {
            compositeSend.writeTo(writableByteChannel);
        } 
        bufferToWrite.flip();
        for (int i = 0; i < 1024; i++) {
            Assert.assertEquals(buf1[i], bufferToWrite.get(i));
        }
        for (int i = 0; i < 2048; i++) {
            Assert.assertEquals(buf2[i], bufferToWrite.get((1024 + i)));
        }
        for (int i = 0; i < 4096; i++) {
            Assert.assertEquals(buf3[i], bufferToWrite.get(((1024 + 2048) + i)));
        }
    }
}

