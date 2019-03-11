package com.alibaba.otter.canal.server;


import Compression.NONE;
import EntryType.ROWDATA;
import Header.Builder;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.Header;
import com.alibaba.otter.canal.protocol.CanalPacket.Messages;
import com.alibaba.otter.canal.protocol.CanalPacket.Packet;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Test;


public class ProtocolTest {
    @Test
    public void testSimple() throws IOException {
        Header.Builder headerBuilder = Header.newBuilder();
        headerBuilder.setLogfileName("mysql-bin.000001");
        headerBuilder.setLogfileOffset(1024);
        headerBuilder.setExecuteTime(1024);
        Entry.Builder entryBuilder = Entry.newBuilder();
        entryBuilder.setHeader(headerBuilder.build());
        entryBuilder.setEntryType(ROWDATA);
        Entry entry = entryBuilder.build();
        Message message = new Message(3, true, Arrays.asList(entry.toByteString()));
        byte[] body = buildData(message);
        Packet packet = Packet.parseFrom(body);
        switch (packet.getType()) {
            case MESSAGES :
                {
                    if (!(packet.getCompression().equals(NONE))) {
                        throw new CanalClientException("compression is not supported in this connector");
                    }
                    Messages messages = Messages.parseFrom(packet.getBody());
                    Message result = new Message(messages.getBatchId());
                    for (ByteString byteString : messages.getMessagesList()) {
                        result.addEntry(Entry.parseFrom(byteString));
                    }
                    System.out.println(result);
                    break;
                }
            default :
                {
                    throw new CanalClientException(("unexpected packet type: " + (packet.getType())));
                }
        }
    }
}

