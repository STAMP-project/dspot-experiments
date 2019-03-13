package com.alibaba.otter.canal.server;


import PacketType.CLIENTACK;
import PacketType.CLIENTAUTHENTICATION;
import PacketType.CLIENTROLLBACK;
import PacketType.GET;
import PacketType.SUBSCRIPTION;
import PacketType.UNSUBSCRIPTION;
import com.alibaba.otter.canal.protocol.CanalPacket.Ack;
import com.alibaba.otter.canal.protocol.CanalPacket.ClientAck;
import com.alibaba.otter.canal.protocol.CanalPacket.ClientAuth;
import com.alibaba.otter.canal.protocol.CanalPacket.ClientRollback;
import com.alibaba.otter.canal.protocol.CanalPacket.Get;
import com.alibaba.otter.canal.protocol.CanalPacket.Handshake;
import com.alibaba.otter.canal.protocol.CanalPacket.Messages;
import com.alibaba.otter.canal.protocol.CanalPacket.Packet;
import com.alibaba.otter.canal.protocol.CanalPacket.PacketType;
import com.alibaba.otter.canal.protocol.CanalPacket.Sub;
import com.alibaba.otter.canal.protocol.CanalPacket.Unsub;
import com.alibaba.otter.canal.server.netty.CanalServerWithNetty;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import org.junit.Test;


public class CanalServerWithNettyTest {
    protected static final String cluster1 = "127.0.0.1:2188";

    protected static final String DESTINATION = "ljhtest1";

    protected static final String DETECTING_SQL = "insert into retl.xdual values(1,now()) on duplicate key update x=now()";

    protected static final String MYSQL_ADDRESS = "127.0.0.1";

    protected static final String USERNAME = "retl";

    protected static final String PASSWORD = "retl";

    protected static final String FILTER = "retl\\..*,erosa.canaltable1s,erosa.canaltable1t";

    private final ByteBuffer header = ByteBuffer.allocate(4);

    private CanalServerWithNetty nettyServer;

    @Test
    public void testAuth() {
        try {
            SocketChannel channel = SocketChannel.open();
            channel.connect(new InetSocketAddress("127.0.0.1", 1088));
            Packet p = Packet.parseFrom(readNextPacket(channel));
            if ((p.getVersion()) != 1) {
                throw new Exception("unsupported version at this client.");
            }
            if ((p.getType()) != (PacketType.HANDSHAKE)) {
                throw new Exception("expect handshake but found other type.");
            }
            // 
            Handshake handshake = Handshake.parseFrom(p.getBody());
            System.out.println(handshake.getSupportedCompressions());
            // 
            ClientAuth ca = ClientAuth.newBuilder().setUsername("").setNetReadTimeout(10000).setNetWriteTimeout(10000).build();
            writeWithHeader(channel, Packet.newBuilder().setType(CLIENTAUTHENTICATION).setBody(ca.toByteString()).build().toByteArray());
            // 
            p = Packet.parseFrom(readNextPacket(channel));
            if ((p.getType()) != (PacketType.ACK)) {
                throw new Exception("unexpected packet type when ack is expected");
            }
            Ack ack = Ack.parseFrom(p.getBody());
            if ((ack.getErrorCode()) > 0) {
                throw new Exception(("something goes wrong when doing authentication: " + (ack.getErrorMessage())));
            }
            writeWithHeader(channel, Packet.newBuilder().setType(SUBSCRIPTION).setBody(Sub.newBuilder().setDestination(CanalServerWithNettyTest.DESTINATION).setClientId("1").build().toByteString()).build().toByteArray());
            // 
            p = Packet.parseFrom(readNextPacket(channel));
            ack = Ack.parseFrom(p.getBody());
            if ((ack.getErrorCode()) > 0) {
                throw new Exception(("failed to subscribe with reason: " + (ack.getErrorMessage())));
            }
            for (int i = 0; i < 10; i++) {
                writeWithHeader(channel, Packet.newBuilder().setType(GET).setBody(Get.newBuilder().setDestination(CanalServerWithNettyTest.DESTINATION).setClientId("1").setFetchSize(10).build().toByteString()).build().toByteArray());
                p = Packet.parseFrom(readNextPacket(channel));
                long batchId = -1L;
                switch (p.getType()) {
                    case MESSAGES :
                        {
                            Messages messages = Messages.parseFrom(p.getBody());
                            batchId = messages.getBatchId();
                            break;
                        }
                    case ACK :
                        {
                            ack = Ack.parseFrom(p.getBody());
                            if ((ack.getErrorCode()) > 0) {
                                throw new Exception(("failed to subscribe with reason: " + (ack.getErrorMessage())));
                            }
                            break;
                        }
                    default :
                        {
                            throw new Exception(("unexpected packet type: " + (p.getType())));
                        }
                }
                System.out.println(("!!!!!!!!!!!!!!!!! " + batchId));
                Thread.sleep(1000L);
                writeWithHeader(channel, Packet.newBuilder().setType(CLIENTACK).setBody(ClientAck.newBuilder().setDestination(CanalServerWithNettyTest.DESTINATION).setClientId("1").setBatchId(batchId).build().toByteString()).build().toByteArray());
            }
            writeWithHeader(channel, Packet.newBuilder().setType(CLIENTROLLBACK).setBody(ClientRollback.newBuilder().setDestination(CanalServerWithNettyTest.DESTINATION).setClientId("1").build().toByteString()).build().toByteArray());
            writeWithHeader(channel, Packet.newBuilder().setType(UNSUBSCRIPTION).setBody(Unsub.newBuilder().setDestination(CanalServerWithNettyTest.DESTINATION).setClientId("1").build().toByteString()).build().toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

