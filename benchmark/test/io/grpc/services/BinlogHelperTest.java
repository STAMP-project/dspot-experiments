/**
 * Copyright 2017 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc.services;


import Attributes.EMPTY;
import CallOptions.DEFAULT;
import EventType.EVENT_TYPE_CLIENT_HEADER;
import EventType.EVENT_TYPE_CLIENT_MESSAGE;
import EventType.EVENT_TYPE_SERVER_HEADER;
import EventType.EVENT_TYPE_SERVER_MESSAGE;
import EventType.EVENT_TYPE_SERVER_TRAILER;
import Grpc.TRANSPORT_ATTR_REMOTE_ADDR;
import Logger.LOGGER_CLIENT;
import Logger.LOGGER_SERVER;
import Metadata.ASCII_STRING_MARSHALLER;
import Metadata.BINARY_BYTE_MARSHALLER;
import Metadata.Key;
import MethodType.UNKNOWN;
import ServerCall.Listener;
import Status.INTERNAL;
import Type.TYPE_IPV4;
import Type.TYPE_IPV6;
import Type.TYPE_UNIX;
import Type.TYPE_UNKNOWN;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.SettableFuture;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.Duration;
import com.google.protobuf.StringValue;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Durations;
import io.grpc.Attributes;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.Context;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.binarylog.v1.Address;
import io.grpc.binarylog.v1.ClientHeader;
import io.grpc.binarylog.v1.GrpcLogEntry;
import io.grpc.binarylog.v1.Message;
import io.grpc.binarylog.v1.MetadataEntry;
import io.grpc.com.google.rpc.Status;
import io.grpc.protobuf.StatusProto;
import io.grpc.services.BinlogHelper.MaybeTruncated;
import io.grpc.services.BinlogHelper.SinkWriter;
import io.grpc.services.BinlogHelper.SinkWriterImpl;
import io.grpc.services.BinlogHelper.TimeProvider;
import io.netty.channel.unix.DomainSocketAddress;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;


/**
 * Tests for {@link BinlogHelper}.
 */
@RunWith(JUnit4.class)
public final class BinlogHelperTest {
    private static final Charset US_ASCII = Charset.forName("US-ASCII");

    private static final BinlogHelper HEADER_FULL = new BinlogHelperTest.Builder().header(Integer.MAX_VALUE).build();

    private static final BinlogHelper HEADER_256 = new BinlogHelperTest.Builder().header(256).build();

    private static final BinlogHelper MSG_FULL = new BinlogHelperTest.Builder().msg(Integer.MAX_VALUE).build();

    private static final BinlogHelper MSG_256 = new BinlogHelperTest.Builder().msg(256).build();

    private static final BinlogHelper BOTH_256 = new BinlogHelperTest.Builder().header(256).msg(256).build();

    private static final BinlogHelper BOTH_FULL = new BinlogHelperTest.Builder().header(Integer.MAX_VALUE).msg(Integer.MAX_VALUE).build();

    private static final String DATA_A = "aaaaaaaaa";

    private static final String DATA_B = "bbbbbbbbb";

    private static final String DATA_C = "ccccccccc";

    private static final Metadata.Key<String> KEY_A = Key.of("a", ASCII_STRING_MARSHALLER);

    private static final Metadata.Key<String> KEY_B = Key.of("b", ASCII_STRING_MARSHALLER);

    private static final Metadata.Key<String> KEY_C = Key.of("c", ASCII_STRING_MARSHALLER);

    private static final MetadataEntry ENTRY_A = MetadataEntry.newBuilder().setKey(io.grpc.services.KEY_A.name()).setValue(ByteString.copyFrom(BinlogHelperTest.DATA_A.getBytes(BinlogHelperTest.US_ASCII))).build();

    private static final MetadataEntry ENTRY_B = MetadataEntry.newBuilder().setKey(io.grpc.services.KEY_B.name()).setValue(ByteString.copyFrom(BinlogHelperTest.DATA_B.getBytes(BinlogHelperTest.US_ASCII))).build();

    private static final MetadataEntry ENTRY_C = MetadataEntry.newBuilder().setKey(io.grpc.services.KEY_C.name()).setValue(ByteString.copyFrom(BinlogHelperTest.DATA_C.getBytes(BinlogHelperTest.US_ASCII))).build();

    private static final long CALL_ID = 1230066625199609624L;

    private static final int HEADER_LIMIT = 10;

    private static final int MESSAGE_LIMIT = Integer.MAX_VALUE;

    private final Metadata nonEmptyMetadata = new Metadata();

    private final BinaryLogSink sink = Mockito.mock(BinaryLogSink.class);

    private final Timestamp timestamp = Timestamp.newBuilder().setSeconds(9876).setNanos(54321).build();

    private final TimeProvider timeProvider = new TimeProvider() {
        @Override
        public long currentTimeNanos() {
            return (TimeUnit.SECONDS.toNanos(9876)) + 54321;
        }
    };

    private final SinkWriter sinkWriterImpl = new SinkWriterImpl(sink, timeProvider, BinlogHelperTest.HEADER_LIMIT, BinlogHelperTest.MESSAGE_LIMIT);

    private final SinkWriter mockSinkWriter = Mockito.mock(SinkWriter.class);

    private final byte[] message = new byte[100];

    private SocketAddress peer;

    @Test
    public void configBinLog_global() throws Exception {
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.BOTH_FULL, makeLog("*", "p.s/m"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.BOTH_FULL, makeLog("*{h;m}", "p.s/m"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.HEADER_FULL, makeLog("*{h}", "p.s/m"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.MSG_FULL, makeLog("*{m}", "p.s/m"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.HEADER_256, makeLog("*{h:256}", "p.s/m"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.MSG_256, makeLog("*{m:256}", "p.s/m"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.BOTH_256, makeLog("*{h:256;m:256}", "p.s/m"));
        BinlogHelperTest.assertSameLimits(new BinlogHelperTest.Builder().header(Integer.MAX_VALUE).msg(256).build(), makeLog("*{h;m:256}", "p.s/m"));
        BinlogHelperTest.assertSameLimits(new BinlogHelperTest.Builder().header(256).msg(Integer.MAX_VALUE).build(), makeLog("*{h:256;m}", "p.s/m"));
    }

    @Test
    public void configBinLog_method() throws Exception {
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.BOTH_FULL, makeLog("p.s/m", "p.s/m"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.BOTH_FULL, makeLog("p.s/m{h;m}", "p.s/m"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.HEADER_FULL, makeLog("p.s/m{h}", "p.s/m"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.MSG_FULL, makeLog("p.s/m{m}", "p.s/m"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.HEADER_256, makeLog("p.s/m{h:256}", "p.s/m"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.MSG_256, makeLog("p.s/m{m:256}", "p.s/m"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.BOTH_256, makeLog("p.s/m{h:256;m:256}", "p.s/m"));
        BinlogHelperTest.assertSameLimits(new BinlogHelperTest.Builder().header(Integer.MAX_VALUE).msg(256).build(), makeLog("p.s/m{h;m:256}", "p.s/m"));
        BinlogHelperTest.assertSameLimits(new BinlogHelperTest.Builder().header(256).msg(Integer.MAX_VALUE).build(), makeLog("p.s/m{h:256;m}", "p.s/m"));
    }

    @Test
    public void configBinLog_method_absent() throws Exception {
        Assert.assertNull(makeLog("p.s/m", "p.s/absent"));
    }

    @Test
    public void configBinLog_service() throws Exception {
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.BOTH_FULL, makeLog("p.s/*", "p.s/m"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.BOTH_FULL, makeLog("p.s/*{h;m}", "p.s/m"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.HEADER_FULL, makeLog("p.s/*{h}", "p.s/m"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.MSG_FULL, makeLog("p.s/*{m}", "p.s/m"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.HEADER_256, makeLog("p.s/*{h:256}", "p.s/m"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.MSG_256, makeLog("p.s/*{m:256}", "p.s/m"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.BOTH_256, makeLog("p.s/*{h:256;m:256}", "p.s/m"));
        BinlogHelperTest.assertSameLimits(new BinlogHelperTest.Builder().header(Integer.MAX_VALUE).msg(256).build(), makeLog("p.s/*{h;m:256}", "p.s/m"));
        BinlogHelperTest.assertSameLimits(new BinlogHelperTest.Builder().header(256).msg(Integer.MAX_VALUE).build(), makeLog("p.s/*{h:256;m}", "p.s/m"));
    }

    @Test
    public void configBinLog_service_absent() throws Exception {
        Assert.assertNull(makeLog("p.s/*", "p.other/m"));
    }

    @Test
    public void createLogFromOptionString() throws Exception {
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.BOTH_FULL, makeLog(null));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.HEADER_FULL, makeLog("{h}"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.MSG_FULL, makeLog("{m}"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.HEADER_256, makeLog("{h:256}"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.MSG_256, makeLog("{m:256}"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.BOTH_256, makeLog("{h:256;m:256}"));
        BinlogHelperTest.assertSameLimits(new BinlogHelperTest.Builder().header(Integer.MAX_VALUE).msg(256).build(), makeLog("{h;m:256}"));
        BinlogHelperTest.assertSameLimits(new BinlogHelperTest.Builder().header(256).msg(Integer.MAX_VALUE).build(), makeLog("{h:256;m}"));
    }

    @Test
    public void badFactoryConfigStrDetected() throws Exception {
        try {
            new io.grpc.services.BinlogHelper.FactoryImpl(sink, "obviouslybad{");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessageThat().startsWith("Illegal log config pattern");
        }
    }

    @Test
    public void createLogFromOptionString_malformed() throws Exception {
        assertIllegalPatternDetected("bad");
        assertIllegalPatternDetected("{bad}");
        assertIllegalPatternDetected("{x;y}");
        assertIllegalPatternDetected("{h:abc}");
        assertIllegalPatternDetected("{2}");
        assertIllegalPatternDetected("{2;2}");
        // The grammar specifies that if both h and m are present, h comes before m
        assertIllegalPatternDetected("{m:123;h:123}");
        // NumberFormatException
        assertIllegalPatternDetected("{h:99999999999999}");
    }

    @Test
    public void configBinLog_multiConfig_withGlobal() throws Exception {
        String configStr = "*{h}," + (("package.both256/*{h:256;m:256}," + "package.service1/both128{h:128;m:128},") + "package.service2/method_messageOnly{m}");
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.HEADER_FULL, makeLog(configStr, "otherpackage.service/method"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.BOTH_256, makeLog(configStr, "package.both256/method1"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.BOTH_256, makeLog(configStr, "package.both256/method2"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.BOTH_256, makeLog(configStr, "package.both256/method3"));
        BinlogHelperTest.assertSameLimits(new BinlogHelperTest.Builder().header(128).msg(128).build(), makeLog(configStr, "package.service1/both128"));
        // the global config is in effect
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.HEADER_FULL, makeLog(configStr, "package.service1/absent"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.MSG_FULL, makeLog(configStr, "package.service2/method_messageOnly"));
        // the global config is in effect
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.HEADER_FULL, makeLog(configStr, "package.service2/absent"));
    }

    @Test
    public void configBinLog_multiConfig_noGlobal() throws Exception {
        String configStr = "package.both256/*{h:256;m:256}," + ("package.service1/both128{h:128;m:128}," + "package.service2/method_messageOnly{m}");
        Assert.assertNull(makeLog(configStr, "otherpackage.service/method"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.BOTH_256, makeLog(configStr, "package.both256/method1"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.BOTH_256, makeLog(configStr, "package.both256/method2"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.BOTH_256, makeLog(configStr, "package.both256/method3"));
        BinlogHelperTest.assertSameLimits(new BinlogHelperTest.Builder().header(128).msg(128).build(), makeLog(configStr, "package.service1/both128"));
        // no global config in effect
        Assert.assertNull(makeLog(configStr, "package.service1/absent"));
        BinlogHelperTest.assertSameLimits(BinlogHelperTest.MSG_FULL, makeLog(configStr, "package.service2/method_messageOnly"));
        // no global config in effect
        Assert.assertNull(makeLog(configStr, "package.service2/absent"));
    }

    @Test
    public void configBinLog_blacklist() {
        Assert.assertNull(makeLog("*,-p.s/blacklisted", "p.s/blacklisted"));
        Assert.assertNull(makeLog("-p.s/blacklisted,*", "p.s/blacklisted"));
        Assert.assertNotNull(makeLog("-p.s/method,*", "p.s/allowed"));
        Assert.assertNull(makeLog("p.s/*,-p.s/blacklisted", "p.s/blacklisted"));
        Assert.assertNull(makeLog("-p.s/blacklisted,p.s/*", "p.s/blacklisted"));
        Assert.assertNotNull(makeLog("-p.s/blacklisted,p.s/*", "p.s/allowed"));
    }

    @Test
    public void configBinLog_duplicates_global() throws Exception {
        assertDuplicatelPatternDetected("*{h},*{h:256}");
    }

    @Test
    public void configBinLog_duplicates_service() throws Exception {
        assertDuplicatelPatternDetected("p.s/*,p.s/*{h}");
    }

    @Test
    public void configBinLog_duplicates_method() throws Exception {
        assertDuplicatelPatternDetected("p.s/*,p.s/*{h:1;m:2}");
        assertDuplicatelPatternDetected("p.s/m,-p.s/m");
        assertDuplicatelPatternDetected("-p.s/m,p.s/m");
        assertDuplicatelPatternDetected("-p.s/m,-p.s/m");
    }

    @Test
    public void socketToProto_ipv4() throws Exception {
        InetAddress address = InetAddress.getByName("127.0.0.1");
        int port = 12345;
        InetSocketAddress socketAddress = new InetSocketAddress(address, port);
        Assert.assertEquals(Address.newBuilder().setType(TYPE_IPV4).setAddress("127.0.0.1").setIpPort(12345).build(), BinlogHelper.socketToProto(socketAddress));
    }

    @Test
    public void socketToProto_ipv6() throws Exception {
        // this is a ipv6 link local address
        InetAddress address = InetAddress.getByName("2001:db8:0:0:0:0:2:1");
        int port = 12345;
        InetSocketAddress socketAddress = new InetSocketAddress(address, port);
        Assert.assertEquals(// RFC 5952 section 4: ipv6 canonical form required
        Address.newBuilder().setType(TYPE_IPV6).setAddress("2001:db8::2:1").setIpPort(12345).build(), BinlogHelper.socketToProto(socketAddress));
    }

    @Test
    public void socketToProto_unix() throws Exception {
        String path = "/some/path";
        DomainSocketAddress socketAddress = new DomainSocketAddress(path);
        Assert.assertEquals(Address.newBuilder().setType(TYPE_UNIX).setAddress("/some/path").build(), BinlogHelper.socketToProto(socketAddress));
    }

    @Test
    public void socketToProto_unknown() throws Exception {
        SocketAddress unknownSocket = new SocketAddress() {
            @Override
            public String toString() {
                return "some-socket-address";
            }
        };
        Assert.assertEquals(Address.newBuilder().setType(TYPE_UNKNOWN).setAddress("some-socket-address").build(), BinlogHelper.socketToProto(unknownSocket));
    }

    @Test
    public void metadataToProto_empty() throws Exception {
        Assert.assertEquals(GrpcLogEntry.newBuilder().setType(EVENT_TYPE_CLIENT_HEADER).setClientHeader(ClientHeader.newBuilder().setMetadata(io.grpc.binarylog.v1.Metadata.getDefaultInstance())).build(), BinlogHelperTest.metadataToProtoTestHelper(EVENT_TYPE_CLIENT_HEADER, new Metadata(), Integer.MAX_VALUE));
    }

    @Test
    public void metadataToProto() throws Exception {
        Assert.assertEquals(GrpcLogEntry.newBuilder().setType(EVENT_TYPE_CLIENT_HEADER).setClientHeader(ClientHeader.newBuilder().setMetadata(io.grpc.binarylog.v1.Metadata.newBuilder().addEntry(BinlogHelperTest.ENTRY_A).addEntry(BinlogHelperTest.ENTRY_B).addEntry(BinlogHelperTest.ENTRY_C).build())).build(), BinlogHelperTest.metadataToProtoTestHelper(EVENT_TYPE_CLIENT_HEADER, nonEmptyMetadata, Integer.MAX_VALUE));
    }

    @Test
    public void metadataToProto_setsTruncated() throws Exception {
        Assert.assertTrue(BinlogHelper.createMetadataProto(nonEmptyMetadata, 0).truncated);
    }

    @Test
    public void metadataToProto_truncated() throws Exception {
        // 0 byte limit not enough for any metadata
        Assert.assertEquals(io.grpc.binarylog.v1.Metadata.getDefaultInstance(), BinlogHelper.createMetadataProto(nonEmptyMetadata, 0).proto.build());
        // not enough bytes for first key value
        Assert.assertEquals(io.grpc.binarylog.v1.Metadata.getDefaultInstance(), BinlogHelper.createMetadataProto(nonEmptyMetadata, 9).proto.build());
        // enough for first key value
        Assert.assertEquals(io.grpc.binarylog.v1.Metadata.newBuilder().addEntry(BinlogHelperTest.ENTRY_A).build(), BinlogHelper.createMetadataProto(nonEmptyMetadata, 10).proto.build());
        // Test edge cases for >= 2 key values
        Assert.assertEquals(io.grpc.binarylog.v1.Metadata.newBuilder().addEntry(BinlogHelperTest.ENTRY_A).build(), BinlogHelper.createMetadataProto(nonEmptyMetadata, 19).proto.build());
        Assert.assertEquals(io.grpc.binarylog.v1.Metadata.newBuilder().addEntry(BinlogHelperTest.ENTRY_A).addEntry(BinlogHelperTest.ENTRY_B).build(), BinlogHelper.createMetadataProto(nonEmptyMetadata, 20).proto.build());
        Assert.assertEquals(io.grpc.binarylog.v1.Metadata.newBuilder().addEntry(BinlogHelperTest.ENTRY_A).addEntry(BinlogHelperTest.ENTRY_B).build(), BinlogHelper.createMetadataProto(nonEmptyMetadata, 29).proto.build());
        // not truncated: enough for all keys
        Assert.assertEquals(io.grpc.binarylog.v1.Metadata.newBuilder().addEntry(BinlogHelperTest.ENTRY_A).addEntry(BinlogHelperTest.ENTRY_B).addEntry(BinlogHelperTest.ENTRY_C).build(), BinlogHelper.createMetadataProto(nonEmptyMetadata, 30).proto.build());
    }

    @Test
    public void messageToProto() throws Exception {
        byte[] bytes = "this is a long message: AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA".getBytes(BinlogHelperTest.US_ASCII);
        Assert.assertEquals(GrpcLogEntry.newBuilder().setMessage(Message.newBuilder().setData(ByteString.copyFrom(bytes)).setLength(bytes.length).build()).build(), BinlogHelperTest.messageToProtoTestHelper(bytes, Integer.MAX_VALUE));
    }

    @Test
    public void messageToProto_truncated() throws Exception {
        byte[] bytes = "this is a long message: AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA".getBytes(BinlogHelperTest.US_ASCII);
        Assert.assertEquals(GrpcLogEntry.newBuilder().setMessage(Message.newBuilder().setLength(bytes.length).build()).setPayloadTruncated(true).build(), BinlogHelperTest.messageToProtoTestHelper(bytes, 0));
        int limit = 10;
        String truncatedMessage = "this is a ";
        Assert.assertEquals(GrpcLogEntry.newBuilder().setMessage(Message.newBuilder().setData(ByteString.copyFrom(truncatedMessage.getBytes(BinlogHelperTest.US_ASCII))).setLength(bytes.length).build()).setPayloadTruncated(true).build(), BinlogHelperTest.messageToProtoTestHelper(bytes, limit));
    }

    @Test
    public void logClientHeader() throws Exception {
        long seq = 1;
        String authority = "authority";
        String methodName = "service/method";
        Duration timeout = Durations.fromMillis(1234);
        InetAddress address = InetAddress.getByName("127.0.0.1");
        int port = 12345;
        InetSocketAddress peerAddress = new InetSocketAddress(address, port);
        long callId = 1000;
        GrpcLogEntry.Builder builder = BinlogHelperTest.metadataToProtoTestHelper(EVENT_TYPE_CLIENT_HEADER, nonEmptyMetadata, 10).toBuilder().setTimestamp(timestamp).setSequenceIdWithinCall(seq).setLogger(LOGGER_CLIENT).setCallId(callId);
        builder.getClientHeaderBuilder().setMethodName(("/" + methodName)).setAuthority(authority).setTimeout(timeout);
        GrpcLogEntry base = builder.build();
        {
            /* peerAddress= */
            sinkWriterImpl.logClientHeader(seq, methodName, authority, timeout, nonEmptyMetadata, LOGGER_CLIENT, callId, null);
            Mockito.verify(sink).write(base);
        }
        // logger is server
        {
            sinkWriterImpl.logClientHeader(seq, methodName, authority, timeout, nonEmptyMetadata, LOGGER_SERVER, callId, peerAddress);
            Mockito.verify(sink).write(base.toBuilder().setPeer(BinlogHelper.socketToProto(peerAddress)).setLogger(LOGGER_SERVER).build());
        }
        // authority is null
        {
            /* authority= */
            /* peerAddress= */
            sinkWriterImpl.logClientHeader(seq, methodName, null, timeout, nonEmptyMetadata, LOGGER_CLIENT, callId, null);
            Mockito.verify(sink).write(base.toBuilder().setClientHeader(builder.getClientHeader().toBuilder().clearAuthority().build()).build());
        }
        // timeout is null
        {
            /* timeout= */
            /* peerAddress= */
            sinkWriterImpl.logClientHeader(seq, methodName, authority, null, nonEmptyMetadata, LOGGER_CLIENT, callId, null);
            Mockito.verify(sink).write(base.toBuilder().setClientHeader(builder.getClientHeader().toBuilder().clearTimeout().build()).build());
        }
        // peerAddress is non null (error for client side)
        try {
            sinkWriterImpl.logClientHeader(seq, methodName, authority, timeout, nonEmptyMetadata, LOGGER_CLIENT, callId, peerAddress);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            // noop
        }
    }

    @Test
    public void logServerHeader() throws Exception {
        long seq = 1;
        InetAddress address = InetAddress.getByName("127.0.0.1");
        int port = 12345;
        InetSocketAddress peerAddress = new InetSocketAddress(address, port);
        long callId = 1000;
        GrpcLogEntry.Builder builder = BinlogHelperTest.metadataToProtoTestHelper(EVENT_TYPE_SERVER_HEADER, nonEmptyMetadata, 10).toBuilder().setTimestamp(timestamp).setSequenceIdWithinCall(seq).setLogger(LOGGER_CLIENT).setCallId(callId).setPeer(BinlogHelper.socketToProto(peerAddress));
        {
            sinkWriterImpl.logServerHeader(seq, nonEmptyMetadata, LOGGER_CLIENT, callId, peerAddress);
            Mockito.verify(sink).write(builder.build());
        }
        // logger is server
        // null peerAddress is required for server side
        {
            /* peerAddress= */
            sinkWriterImpl.logServerHeader(seq, nonEmptyMetadata, LOGGER_SERVER, callId, null);
            Mockito.verify(sink).write(builder.setLogger(LOGGER_SERVER).clearPeer().build());
        }
        // logger is server
        // non null peerAddress is an error
        try {
            sinkWriterImpl.logServerHeader(seq, nonEmptyMetadata, LOGGER_SERVER, callId, peerAddress);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            // noop
        }
    }

    @Test
    public void logTrailer() throws Exception {
        long seq = 1;
        InetAddress address = InetAddress.getByName("127.0.0.1");
        int port = 12345;
        InetSocketAddress peerAddress = new InetSocketAddress(address, port);
        long callId = 1000;
        Status statusDescription = INTERNAL.withDescription("my description");
        GrpcLogEntry.Builder builder = BinlogHelperTest.metadataToProtoTestHelper(EVENT_TYPE_SERVER_TRAILER, nonEmptyMetadata, 10).toBuilder().setTimestamp(timestamp).setSequenceIdWithinCall(seq).setLogger(LOGGER_CLIENT).setCallId(callId).setPeer(BinlogHelper.socketToProto(peerAddress));
        builder.getTrailerBuilder().setStatusCode(INTERNAL.getCode().value()).setStatusMessage("my description");
        GrpcLogEntry base = builder.build();
        {
            sinkWriterImpl.logTrailer(seq, statusDescription, nonEmptyMetadata, LOGGER_CLIENT, callId, peerAddress);
            Mockito.verify(sink).write(base);
        }
        // logger is server
        {
            /* peerAddress= */
            sinkWriterImpl.logTrailer(seq, statusDescription, nonEmptyMetadata, LOGGER_SERVER, callId, null);
            Mockito.verify(sink).write(base.toBuilder().clearPeer().setLogger(LOGGER_SERVER).build());
        }
        // peerAddress is null
        {
            /* peerAddress= */
            sinkWriterImpl.logTrailer(seq, statusDescription, nonEmptyMetadata, LOGGER_CLIENT, callId, null);
            Mockito.verify(sink).write(base.toBuilder().clearPeer().build());
        }
        // status code is present but description is null
        {
            // strip the description
            sinkWriterImpl.logTrailer(seq, statusDescription.getCode().toStatus(), nonEmptyMetadata, LOGGER_CLIENT, callId, peerAddress);
            Mockito.verify(sink).write(base.toBuilder().setTrailer(base.getTrailer().toBuilder().clearStatusMessage()).build());
        }
        // status proto always logged if present (com.google.rpc.Status),
        {
            int zeroHeaderBytes = 0;
            SinkWriterImpl truncatingWriter = new SinkWriterImpl(sink, timeProvider, zeroHeaderBytes, BinlogHelperTest.MESSAGE_LIMIT);
            com.google.rpc.Status statusProto = com.google.rpc.Status.newBuilder().addDetails(Any.pack(StringValue.newBuilder().setValue("arbitrarypayload").build())).setCode(INTERNAL.getCode().value()).setMessage("status detail string").build();
            StatusException statusException = StatusProto.toStatusException(statusProto, nonEmptyMetadata);
            truncatingWriter.logTrailer(seq, statusException.getStatus(), statusException.getTrailers(), LOGGER_CLIENT, callId, peerAddress);
            Mockito.verify(sink).write(base.toBuilder().setTrailer(builder.getTrailerBuilder().setStatusMessage("status detail string").setStatusDetails(ByteString.copyFrom(statusProto.toByteArray())).setMetadata(io.grpc.binarylog.v1.Metadata.getDefaultInstance())).build());
        }
    }

    @Test
    public void alwaysLoggedMetadata_grpcTraceBin() throws Exception {
        Metadata.Key<byte[]> key = Key.of("grpc-trace-bin", BINARY_BYTE_MARSHALLER);
        Metadata metadata = new Metadata();
        metadata.put(key, new byte[1]);
        int zeroHeaderBytes = 0;
        MaybeTruncated<io.grpc.binarylog.v1.Metadata.Builder> pair = BinlogHelper.createMetadataProto(metadata, zeroHeaderBytes);
        Assert.assertEquals(key.name(), Iterables.getOnlyElement(pair.proto.getEntryBuilderList()).getKey());
        Assert.assertFalse(pair.truncated);
    }

    @Test
    public void neverLoggedMetadata_grpcStatusDetilsBin() throws Exception {
        Metadata.Key<byte[]> key = Key.of("grpc-status-details-bin", BINARY_BYTE_MARSHALLER);
        Metadata metadata = new Metadata();
        metadata.put(key, new byte[1]);
        int unlimitedHeaderBytes = Integer.MAX_VALUE;
        MaybeTruncated<io.grpc.binarylog.v1.Metadata.Builder> pair = BinlogHelper.createMetadataProto(metadata, unlimitedHeaderBytes);
        assertThat(pair.proto.getEntryBuilderList()).isEmpty();
        Assert.assertFalse(pair.truncated);
    }

    @Test
    public void logRpcMessage() throws Exception {
        long seq = 1;
        long callId = 1000;
        GrpcLogEntry base = BinlogHelperTest.messageToProtoTestHelper(message, BinlogHelperTest.MESSAGE_LIMIT).toBuilder().setTimestamp(timestamp).setType(EVENT_TYPE_CLIENT_MESSAGE).setLogger(LOGGER_CLIENT).setSequenceIdWithinCall(1).setCallId(callId).build();
        {
            sinkWriterImpl.logRpcMessage(seq, EVENT_TYPE_CLIENT_MESSAGE, BinaryLogProvider.BYTEARRAY_MARSHALLER, message, LOGGER_CLIENT, callId);
            Mockito.verify(sink).write(base);
        }
        // server messsage
        {
            sinkWriterImpl.logRpcMessage(seq, EVENT_TYPE_SERVER_MESSAGE, BinaryLogProvider.BYTEARRAY_MARSHALLER, message, LOGGER_CLIENT, callId);
            Mockito.verify(sink).write(base.toBuilder().setType(EVENT_TYPE_SERVER_MESSAGE).build());
        }
        // logger is server
        {
            sinkWriterImpl.logRpcMessage(seq, EVENT_TYPE_CLIENT_MESSAGE, BinaryLogProvider.BYTEARRAY_MARSHALLER, message, LOGGER_SERVER, callId);
            Mockito.verify(sink).write(base.toBuilder().setLogger(LOGGER_SERVER).build());
        }
    }

    @Test
    public void getPeerSocketTest() {
        Assert.assertNull(BinlogHelper.getPeerSocket(EMPTY));
        Assert.assertSame(peer, BinlogHelper.getPeerSocket(Attributes.newBuilder().set(TRANSPORT_ATTR_REMOTE_ADDR, peer).build()));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void serverDeadlineLogged() {
        final AtomicReference<ServerCall> interceptedCall = new AtomicReference<>();
        final ServerCall.Listener mockListener = Mockito.mock(Listener.class);
        final MethodDescriptor<byte[], byte[]> method = MethodDescriptor.<byte[], byte[]>newBuilder().setType(UNKNOWN).setFullMethodName("service/method").setRequestMarshaller(BinaryLogProvider.BYTEARRAY_MARSHALLER).setResponseMarshaller(BinaryLogProvider.BYTEARRAY_MARSHALLER).build();
        // We expect the contents of the "grpc-timeout" header to be installed the context
        Context.current().withDeadlineAfter(1, TimeUnit.SECONDS, Executors.newSingleThreadScheduledExecutor()).run(new Runnable() {
            @Override
            public void run() {
                ServerCall.Listener<byte[]> unused = getServerInterceptor(BinlogHelperTest.CALL_ID).interceptCall(new io.grpc.internal.NoopServerCall<byte[], byte[]>() {
                    @Override
                    public MethodDescriptor<byte[], byte[]> getMethodDescriptor() {
                        return method;
                    }
                }, new Metadata(), new io.grpc.ServerCallHandler<byte[], byte[]>() {
                    @Override
                    public ServerCall.Listener<byte[]> startCall(ServerCall<byte[], byte[]> call, Metadata headers) {
                        interceptedCall.set(call);
                        return mockListener;
                    }
                });
            }
        });
        ArgumentCaptor<Duration> timeoutCaptor = ArgumentCaptor.forClass(Duration.class);
        /* seq= */
        Mockito.verify(mockSinkWriter).logClientHeader(eq(1L), ArgumentMatchers.eq("service/method"), isNull(String.class), timeoutCaptor.capture(), any(Metadata.class), ArgumentMatchers.eq(LOGGER_SERVER), eq(BinlogHelperTest.CALL_ID), ArgumentMatchers.isNull(SocketAddress.class));
        Mockito.verifyNoMoreInteractions(mockSinkWriter);
        Duration timeout = timeoutCaptor.getValue();
        assertThat(((TimeUnit.SECONDS.toNanos(1)) - (Durations.toNanos(timeout)))).isAtMost(TimeUnit.MILLISECONDS.toNanos(250));
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void clientDeadlineLogged_deadlineSetViaCallOption() {
        MethodDescriptor<byte[], byte[]> method = MethodDescriptor.<byte[], byte[]>newBuilder().setType(UNKNOWN).setFullMethodName("service/method").setRequestMarshaller(BinaryLogProvider.BYTEARRAY_MARSHALLER).setResponseMarshaller(BinaryLogProvider.BYTEARRAY_MARSHALLER).build();
        ClientCall.Listener<byte[]> mockListener = Mockito.mock(ClientCall.Listener.class);
        ClientCall<byte[], byte[]> call = getClientInterceptor(BinlogHelperTest.CALL_ID).interceptCall(method, DEFAULT.withDeadlineAfter(1, TimeUnit.SECONDS), new Channel() {
            @Override
            public <RequestT, ResponseT> ClientCall<RequestT, ResponseT> newCall(MethodDescriptor<RequestT, ResponseT> methodDescriptor, CallOptions callOptions) {
                return new io.grpc.internal.NoopClientCall();
            }

            @Override
            public String authority() {
                return null;
            }
        });
        call.start(mockListener, new Metadata());
        ArgumentCaptor<Duration> callOptTimeoutCaptor = ArgumentCaptor.forClass(Duration.class);
        Mockito.verify(mockSinkWriter).logClientHeader(anyLong(), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class), callOptTimeoutCaptor.capture(), any(Metadata.class), ArgumentMatchers.any(io.grpc.binarylog.v1.GrpcLogEntry.Logger.class), anyLong(), ArgumentMatchers.any(SocketAddress.class));
        Duration timeout = callOptTimeoutCaptor.getValue();
        assertThat(((TimeUnit.SECONDS.toNanos(1)) - (Durations.toNanos(timeout)))).isAtMost(TimeUnit.MILLISECONDS.toNanos(250));
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void clientDeadlineLogged_deadlineSetViaContext() throws Exception {
        // important: deadline is read from the ctx where call was created
        final SettableFuture<ClientCall<byte[], byte[]>> callFuture = SettableFuture.create();
        Context.current().withDeadline(io.grpc.Deadline.after(1, TimeUnit.SECONDS), Executors.newSingleThreadScheduledExecutor()).run(new Runnable() {
            @Override
            public void run() {
                MethodDescriptor<byte[], byte[]> method = MethodDescriptor.<byte[], byte[]>newBuilder().setType(UNKNOWN).setFullMethodName("service/method").setRequestMarshaller(BinaryLogProvider.BYTEARRAY_MARSHALLER).setResponseMarshaller(BinaryLogProvider.BYTEARRAY_MARSHALLER).build();
                callFuture.set(getClientInterceptor(BinlogHelperTest.CALL_ID).interceptCall(method, DEFAULT.withDeadlineAfter(1, TimeUnit.SECONDS), new Channel() {
                    @Override
                    public <RequestT, ResponseT> ClientCall<RequestT, ResponseT> newCall(MethodDescriptor<RequestT, ResponseT> methodDescriptor, CallOptions callOptions) {
                        return new io.grpc.internal.NoopClientCall();
                    }

                    @Override
                    public String authority() {
                        return null;
                    }
                }));
            }
        });
        ClientCall.Listener<byte[]> mockListener = Mockito.mock(ClientCall.Listener.class);
        callFuture.get().start(mockListener, new Metadata());
        ArgumentCaptor<Duration> callOptTimeoutCaptor = ArgumentCaptor.forClass(Duration.class);
        Mockito.verify(mockSinkWriter).logClientHeader(anyLong(), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class), callOptTimeoutCaptor.capture(), any(Metadata.class), ArgumentMatchers.any(io.grpc.binarylog.v1.GrpcLogEntry.Logger.class), anyLong(), ArgumentMatchers.any(SocketAddress.class));
        Duration timeout = callOptTimeoutCaptor.getValue();
        assertThat(((TimeUnit.SECONDS.toNanos(1)) - (Durations.toNanos(timeout)))).isAtMost(TimeUnit.MILLISECONDS.toNanos(250));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void clientInterceptor() throws Exception {
        final AtomicReference<ClientCall.Listener> interceptedListener = new AtomicReference<>();
        // capture these manually because ClientCall can not be mocked
        final AtomicReference<Metadata> actualClientInitial = new AtomicReference<>();
        final AtomicReference<Object> actualRequest = new AtomicReference<>();
        final SettableFuture<Void> halfCloseCalled = SettableFuture.create();
        final SettableFuture<Void> cancelCalled = SettableFuture.create();
        Channel channel = new Channel() {
            @Override
            public <RequestT, ResponseT> ClientCall<RequestT, ResponseT> newCall(MethodDescriptor<RequestT, ResponseT> methodDescriptor, CallOptions callOptions) {
                return new io.grpc.internal.NoopClientCall<RequestT, ResponseT>() {
                    @Override
                    public void start(Listener<ResponseT> responseListener, Metadata headers) {
                        interceptedListener.set(responseListener);
                        actualClientInitial.set(headers);
                    }

                    @Override
                    public void sendMessage(RequestT message) {
                        actualRequest.set(message);
                    }

                    @Override
                    public void cancel(String message, Throwable cause) {
                        cancelCalled.set(null);
                    }

                    @Override
                    public void halfClose() {
                        halfCloseCalled.set(null);
                    }

                    @Override
                    public Attributes getAttributes() {
                        return Attributes.newBuilder().set(TRANSPORT_ATTR_REMOTE_ADDR, peer).build();
                    }
                };
            }

            @Override
            public String authority() {
                return "the-authority";
            }
        };
        ClientCall.Listener<byte[]> mockListener = Mockito.mock(ClientCall.Listener.class);
        MethodDescriptor<byte[], byte[]> method = MethodDescriptor.<byte[], byte[]>newBuilder().setType(UNKNOWN).setFullMethodName("service/method").setRequestMarshaller(BinaryLogProvider.BYTEARRAY_MARSHALLER).setResponseMarshaller(BinaryLogProvider.BYTEARRAY_MARSHALLER).build();
        ClientCall<byte[], byte[]> interceptedCall = getClientInterceptor(BinlogHelperTest.CALL_ID).interceptCall(method, DEFAULT, channel);
        // send client header
        {
            Metadata clientInitial = new Metadata();
            interceptedCall.start(mockListener, clientInitial);
            /* seq= */
            Mockito.verify(mockSinkWriter).logClientHeader(eq(1L), ArgumentMatchers.eq("service/method"), ArgumentMatchers.eq("the-authority"), ArgumentMatchers.isNull(Duration.class), same(clientInitial), ArgumentMatchers.eq(LOGGER_CLIENT), eq(BinlogHelperTest.CALL_ID), ArgumentMatchers.isNull(SocketAddress.class));
            Mockito.verifyNoMoreInteractions(mockSinkWriter);
            Assert.assertSame(clientInitial, actualClientInitial.get());
        }
        // receive server header
        {
            Metadata serverInitial = new Metadata();
            interceptedListener.get().onHeaders(serverInitial);
            /* seq= */
            Mockito.verify(mockSinkWriter).logServerHeader(eq(2L), same(serverInitial), ArgumentMatchers.eq(LOGGER_CLIENT), eq(BinlogHelperTest.CALL_ID), ArgumentMatchers.same(peer));
            Mockito.verifyNoMoreInteractions(mockSinkWriter);
            Mockito.verify(mockListener).onHeaders(same(serverInitial));
        }
        // send client msg
        {
            byte[] request = "this is a request".getBytes(BinlogHelperTest.US_ASCII);
            interceptedCall.sendMessage(request);
            /* seq= */
            Mockito.verify(mockSinkWriter).logRpcMessage(eq(3L), ArgumentMatchers.eq(EVENT_TYPE_CLIENT_MESSAGE), same(BinaryLogProvider.BYTEARRAY_MARSHALLER), ArgumentMatchers.same(request), ArgumentMatchers.eq(LOGGER_CLIENT), eq(BinlogHelperTest.CALL_ID));
            Mockito.verifyNoMoreInteractions(mockSinkWriter);
            Assert.assertSame(request, actualRequest.get());
        }
        // client half close
        {
            interceptedCall.halfClose();
            /* seq= */
            Mockito.verify(mockSinkWriter).logHalfClose(eq(4L), ArgumentMatchers.eq(LOGGER_CLIENT), eq(BinlogHelperTest.CALL_ID));
            halfCloseCalled.get(1, TimeUnit.SECONDS);
            Mockito.verifyNoMoreInteractions(mockSinkWriter);
        }
        // receive server msg
        {
            byte[] response = "this is a response".getBytes(BinlogHelperTest.US_ASCII);
            interceptedListener.get().onMessage(response);
            /* seq= */
            Mockito.verify(mockSinkWriter).logRpcMessage(eq(5L), ArgumentMatchers.eq(EVENT_TYPE_SERVER_MESSAGE), same(BinaryLogProvider.BYTEARRAY_MARSHALLER), ArgumentMatchers.same(response), ArgumentMatchers.eq(LOGGER_CLIENT), eq(BinlogHelperTest.CALL_ID));
            Mockito.verifyNoMoreInteractions(mockSinkWriter);
            Mockito.verify(mockListener).onMessage(ArgumentMatchers.same(response));
        }
        // receive trailer
        {
            Status status = INTERNAL.withDescription("some description");
            Metadata trailers = new Metadata();
            interceptedListener.get().onClose(status, trailers);
            /* seq= */
            Mockito.verify(mockSinkWriter).logTrailer(eq(6L), same(status), same(trailers), ArgumentMatchers.eq(LOGGER_CLIENT), eq(BinlogHelperTest.CALL_ID), ArgumentMatchers.isNull(SocketAddress.class));
            Mockito.verifyNoMoreInteractions(mockSinkWriter);
            Mockito.verify(mockListener).onClose(same(status), same(trailers));
        }
        // cancel
        {
            interceptedCall.cancel(null, null);
            /* seq= */
            Mockito.verify(mockSinkWriter).logCancel(eq(7L), ArgumentMatchers.eq(LOGGER_CLIENT), eq(BinlogHelperTest.CALL_ID));
            cancelCalled.get(1, TimeUnit.SECONDS);
        }
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void clientInterceptor_trailersOnlyResponseLogsPeerAddress() throws Exception {
        final AtomicReference<ClientCall.Listener> interceptedListener = new AtomicReference<>();
        // capture these manually because ClientCall can not be mocked
        final AtomicReference<Metadata> actualClientInitial = new AtomicReference<>();
        final AtomicReference<Object> actualRequest = new AtomicReference<>();
        Channel channel = new Channel() {
            @Override
            public <RequestT, ResponseT> ClientCall<RequestT, ResponseT> newCall(MethodDescriptor<RequestT, ResponseT> methodDescriptor, CallOptions callOptions) {
                return new io.grpc.internal.NoopClientCall<RequestT, ResponseT>() {
                    @Override
                    public void start(Listener<ResponseT> responseListener, Metadata headers) {
                        interceptedListener.set(responseListener);
                        actualClientInitial.set(headers);
                    }

                    @Override
                    public void sendMessage(RequestT message) {
                        actualRequest.set(message);
                    }

                    @Override
                    public Attributes getAttributes() {
                        return Attributes.newBuilder().set(TRANSPORT_ATTR_REMOTE_ADDR, peer).build();
                    }
                };
            }

            @Override
            public String authority() {
                return "the-authority";
            }
        };
        ClientCall.Listener<byte[]> mockListener = Mockito.mock(ClientCall.Listener.class);
        MethodDescriptor<byte[], byte[]> method = MethodDescriptor.<byte[], byte[]>newBuilder().setType(UNKNOWN).setFullMethodName("service/method").setRequestMarshaller(BinaryLogProvider.BYTEARRAY_MARSHALLER).setResponseMarshaller(BinaryLogProvider.BYTEARRAY_MARSHALLER).build();
        ClientCall<byte[], byte[]> interceptedCall = getClientInterceptor(BinlogHelperTest.CALL_ID).interceptCall(method, DEFAULT.withDeadlineAfter(1, TimeUnit.SECONDS), channel);
        Metadata clientInitial = new Metadata();
        interceptedCall.start(mockListener, clientInitial);
        /* seq= */
        Mockito.verify(mockSinkWriter).logClientHeader(eq(1L), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(Duration.class), any(Metadata.class), ArgumentMatchers.eq(LOGGER_CLIENT), eq(BinlogHelperTest.CALL_ID), ArgumentMatchers.isNull(SocketAddress.class));
        Mockito.verifyNoMoreInteractions(mockSinkWriter);
        // trailer only response
        {
            Status status = INTERNAL.withDescription("some description");
            Metadata trailers = new Metadata();
            interceptedListener.get().onClose(status, trailers);
            /* seq= */
            Mockito.verify(mockSinkWriter).logTrailer(eq(2L), same(status), same(trailers), ArgumentMatchers.eq(LOGGER_CLIENT), eq(BinlogHelperTest.CALL_ID), ArgumentMatchers.same(peer));
            Mockito.verifyNoMoreInteractions(mockSinkWriter);
            Mockito.verify(mockListener).onClose(same(status), same(trailers));
        }
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void serverInterceptor() throws Exception {
        final AtomicReference<ServerCall> interceptedCall = new AtomicReference<>();
        ServerCall.Listener<byte[]> capturedListener;
        final ServerCall.Listener mockListener = Mockito.mock(Listener.class);
        // capture these manually because ServerCall can not be mocked
        final AtomicReference<Metadata> actualServerInitial = new AtomicReference<>();
        final AtomicReference<byte[]> actualResponse = new AtomicReference<>();
        final AtomicReference<Status> actualStatus = new AtomicReference<>();
        final AtomicReference<Metadata> actualTrailers = new AtomicReference<>();
        // begin call and receive client header
        {
            Metadata clientInitial = new Metadata();
            final MethodDescriptor<byte[], byte[]> method = MethodDescriptor.<byte[], byte[]>newBuilder().setType(UNKNOWN).setFullMethodName("service/method").setRequestMarshaller(BinaryLogProvider.BYTEARRAY_MARSHALLER).setResponseMarshaller(BinaryLogProvider.BYTEARRAY_MARSHALLER).build();
            capturedListener = getServerInterceptor(BinlogHelperTest.CALL_ID).interceptCall(new io.grpc.internal.NoopServerCall<byte[], byte[]>() {
                @Override
                public void sendHeaders(Metadata headers) {
                    actualServerInitial.set(headers);
                }

                @Override
                public void sendMessage(byte[] message) {
                    actualResponse.set(message);
                }

                @Override
                public void close(Status status, Metadata trailers) {
                    actualStatus.set(status);
                    actualTrailers.set(trailers);
                }

                @Override
                public MethodDescriptor<byte[], byte[]> getMethodDescriptor() {
                    return method;
                }

                @Override
                public Attributes getAttributes() {
                    return Attributes.newBuilder().set(TRANSPORT_ATTR_REMOTE_ADDR, peer).build();
                }

                @Override
                public String getAuthority() {
                    return "the-authority";
                }
            }, clientInitial, new io.grpc.ServerCallHandler<byte[], byte[]>() {
                @Override
                public ServerCall.Listener<byte[]> startCall(ServerCall<byte[], byte[]> call, Metadata headers) {
                    interceptedCall.set(call);
                    return mockListener;
                }
            });
            /* seq= */
            Mockito.verify(mockSinkWriter).logClientHeader(eq(1L), ArgumentMatchers.eq("service/method"), ArgumentMatchers.eq("the-authority"), ArgumentMatchers.isNull(Duration.class), same(clientInitial), ArgumentMatchers.eq(LOGGER_SERVER), eq(BinlogHelperTest.CALL_ID), ArgumentMatchers.same(peer));
            Mockito.verifyNoMoreInteractions(mockSinkWriter);
        }
        // send server header
        {
            Metadata serverInital = new Metadata();
            interceptedCall.get().sendHeaders(serverInital);
            /* seq= */
            Mockito.verify(mockSinkWriter).logServerHeader(eq(2L), same(serverInital), ArgumentMatchers.eq(LOGGER_SERVER), eq(BinlogHelperTest.CALL_ID), ArgumentMatchers.isNull(SocketAddress.class));
            Mockito.verifyNoMoreInteractions(mockSinkWriter);
            Assert.assertSame(serverInital, actualServerInitial.get());
        }
        // receive client msg
        {
            byte[] request = "this is a request".getBytes(BinlogHelperTest.US_ASCII);
            capturedListener.onMessage(request);
            /* seq= */
            Mockito.verify(mockSinkWriter).logRpcMessage(eq(3L), ArgumentMatchers.eq(EVENT_TYPE_CLIENT_MESSAGE), same(BinaryLogProvider.BYTEARRAY_MARSHALLER), ArgumentMatchers.same(request), ArgumentMatchers.eq(LOGGER_SERVER), eq(BinlogHelperTest.CALL_ID));
            Mockito.verifyNoMoreInteractions(mockSinkWriter);
            Mockito.verify(mockListener).onMessage(ArgumentMatchers.same(request));
        }
        // client half close
        {
            capturedListener.onHalfClose();
            Mockito.verify(mockSinkWriter).logHalfClose(eq(4L), ArgumentMatchers.eq(LOGGER_SERVER), eq(BinlogHelperTest.CALL_ID));
            Mockito.verifyNoMoreInteractions(mockSinkWriter);
            Mockito.verify(mockListener).onHalfClose();
        }
        // send server msg
        {
            byte[] response = "this is a response".getBytes(BinlogHelperTest.US_ASCII);
            interceptedCall.get().sendMessage(response);
            /* seq= */
            Mockito.verify(mockSinkWriter).logRpcMessage(eq(5L), ArgumentMatchers.eq(EVENT_TYPE_SERVER_MESSAGE), same(BinaryLogProvider.BYTEARRAY_MARSHALLER), ArgumentMatchers.same(response), ArgumentMatchers.eq(LOGGER_SERVER), eq(BinlogHelperTest.CALL_ID));
            Mockito.verifyNoMoreInteractions(mockSinkWriter);
            Assert.assertSame(response, actualResponse.get());
        }
        // send trailer
        {
            Status status = INTERNAL.withDescription("some description");
            Metadata trailers = new Metadata();
            interceptedCall.get().close(status, trailers);
            /* seq= */
            Mockito.verify(mockSinkWriter).logTrailer(eq(6L), same(status), same(trailers), ArgumentMatchers.eq(LOGGER_SERVER), eq(BinlogHelperTest.CALL_ID), ArgumentMatchers.isNull(SocketAddress.class));
            Mockito.verifyNoMoreInteractions(mockSinkWriter);
            Assert.assertSame(status, actualStatus.get());
            Assert.assertSame(trailers, actualTrailers.get());
        }
        // cancel
        {
            capturedListener.onCancel();
            /* seq= */
            Mockito.verify(mockSinkWriter).logCancel(eq(7L), ArgumentMatchers.eq(LOGGER_SERVER), eq(BinlogHelperTest.CALL_ID));
            Mockito.verify(mockListener).onCancel();
        }
    }

    /**
     * A builder class to make unit test code more readable.
     */
    private static final class Builder {
        int maxHeaderBytes = 0;

        int maxMessageBytes = 0;

        BinlogHelperTest.Builder header(int bytes) {
            maxHeaderBytes = bytes;
            return this;
        }

        BinlogHelperTest.Builder msg(int bytes) {
            maxMessageBytes = bytes;
            return this;
        }

        BinlogHelper build() {
            return new BinlogHelper(new SinkWriterImpl(Mockito.mock(BinaryLogSink.class), null, maxHeaderBytes, maxMessageBytes));
        }
    }
}

