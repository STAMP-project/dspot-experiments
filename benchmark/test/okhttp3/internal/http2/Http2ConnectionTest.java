/**
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package okhttp3.internal.http2;


import ErrorCode.CANCEL;
import ErrorCode.PROTOCOL_ERROR;
import ErrorCode.REFUSED_STREAM;
import Http2.TYPE_DATA;
import Http2.TYPE_GOAWAY;
import Http2.TYPE_HEADERS;
import Http2.TYPE_PING;
import Http2.TYPE_RST_STREAM;
import Http2.TYPE_SETTINGS;
import Http2.TYPE_WINDOW_UPDATE;
import Util.EMPTY_BYTE_ARRAY;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import okhttp3.Headers;
import okhttp3.TestUtil;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import okio.Source;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;

import static Header.RESPONSE_STATUS;
import static Header.TARGET_AUTHORITY;
import static Header.TARGET_METHOD;
import static Header.TARGET_PATH;
import static Header.TARGET_SCHEME;
import static Http2.INITIAL_MAX_FRAME_SIZE;


public final class Http2ConnectionTest {
    private final MockHttp2Peer peer = new MockHttp2Peer();

    @Rule
    public final TestRule timeout = new Timeout(5000);

    @Test
    public void serverPingsClientHttp2() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.sendFrame().ping(false, 2, 3);
        peer.acceptFrame();// PING

        peer.play();
        // play it back
        connect(peer);
        // verify the peer received what was expected
        MockHttp2Peer.InFrame ping = peer.takeFrame();
        Assert.assertEquals(TYPE_PING, ping.type);
        Assert.assertEquals(0, ping.streamId);
        Assert.assertEquals(2, ping.payload1);
        Assert.assertEquals(3, ping.payload2);
        Assert.assertTrue(ping.ack);
    }

    @Test
    public void peerHttp2ServerLowersInitialWindowSize() throws Exception {
        Settings initial = new Settings();
        initial.set(Settings.INITIAL_WINDOW_SIZE, 1684);
        Settings shouldntImpactConnection = new Settings();
        shouldntImpactConnection.set(Settings.INITIAL_WINDOW_SIZE, 3368);
        peer.sendFrame().settings(initial);
        peer.acceptFrame();// ACK

        peer.sendFrame().settings(shouldntImpactConnection);
        peer.acceptFrame();// ACK 2

        peer.acceptFrame();// HEADERS

        peer.play();
        Http2Connection connection = connect(peer);
        // Verify the peer received the second ACK.
        MockHttp2Peer.InFrame ackFrame = peer.takeFrame();
        Assert.assertEquals(TYPE_SETTINGS, ackFrame.type);
        Assert.assertEquals(0, ackFrame.streamId);
        Assert.assertTrue(ackFrame.ack);
        // This stream was created *after* the connection settings were adjusted.
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("a", "android"), false);
        Assert.assertEquals(3368, connection.peerSettings.getInitialWindowSize());
        // New Stream is has the most recent initial window size.
        Assert.assertEquals(3368, stream.bytesLeftInWriteWindow);
    }

    @Test
    public void peerHttp2ServerZerosCompressionTable() throws Exception {
        boolean client = false;// Peer is server, so we are client.

        Settings settings = new Settings();
        settings.set(Settings.HEADER_TABLE_SIZE, 0);
        Http2Connection connection = connectWithSettings(client, settings);
        // Verify the peer's settings were read and applied.
        Assert.assertEquals(0, connection.peerSettings.getHeaderTableSize());
        Http2Writer writer = connection.writer;
        Assert.assertEquals(0, writer.hpackWriter.dynamicTableByteCount);
        Assert.assertEquals(0, writer.hpackWriter.headerTableSizeSetting);
    }

    @Test
    public void peerHttp2ClientDisablesPush() throws Exception {
        boolean client = false;// Peer is client, so we are server.

        Settings settings = new Settings();
        settings.set(Settings.ENABLE_PUSH, 0);// The peer client disables push.

        Http2Connection connection = connectWithSettings(client, settings);
        // verify the peer's settings were read and applied.
        Assert.assertFalse(connection.peerSettings.getEnablePush(true));
    }

    @Test
    public void peerIncreasesMaxFrameSize() throws Exception {
        int newMaxFrameSize = 16385;
        Settings settings = new Settings();
        settings.set(Settings.MAX_FRAME_SIZE, newMaxFrameSize);
        Http2Connection connection = connectWithSettings(true, settings);
        // verify the peer's settings were read and applied.
        Assert.assertEquals(newMaxFrameSize, connection.peerSettings.getMaxFrameSize((-1)));
        Assert.assertEquals(newMaxFrameSize, connection.writer.maxDataLength());
    }

    /**
     * Webservers may set the initial window size to zero, which is a special case because it means
     * that we have to flush headers immediately before any request body can be sent.
     * https://github.com/square/okhttp/issues/2543
     */
    @Test
    public void peerSetsZeroFlowControl() throws Exception {
        peer.setClient(true);
        // Write the mocking script.
        peer.sendFrame().settings(new Settings().set(Settings.INITIAL_WINDOW_SIZE, 0));
        peer.acceptFrame();// ACK

        peer.sendFrame().windowUpdate(0, 10);// Increase the connection window size.

        peer.acceptFrame();// PING

        peer.sendFrame().ping(true, 1, 0);
        peer.acceptFrame();// HEADERS STREAM 3

        peer.sendFrame().windowUpdate(3, 5);
        peer.acceptFrame();// DATA STREAM 3 "abcde"

        peer.sendFrame().windowUpdate(3, 5);
        peer.acceptFrame();// DATA STREAM 3 "fghi"

        peer.play();
        // Play it back.
        Http2Connection connection = connect(peer);
        connection.writePingAndAwaitPong();// Ensure the SETTINGS have been received.

        Http2Stream stream = connection.newStream(TestUtil.headerEntries("a", "android"), true);
        BufferedSink sink = Okio.buffer(stream.getSink());
        sink.writeUtf8("abcdefghi");
        sink.flush();
        // Verify the peer received what was expected.
        peer.takeFrame();// PING

        MockHttp2Peer.InFrame headers = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, headers.type);
        MockHttp2Peer.InFrame data1 = peer.takeFrame();
        Assert.assertEquals(TYPE_DATA, data1.type);
        Assert.assertEquals(3, data1.streamId);
        Assert.assertArrayEquals("abcde".getBytes(StandardCharsets.UTF_8), data1.data);
        MockHttp2Peer.InFrame data2 = peer.takeFrame();
        Assert.assertEquals(TYPE_DATA, data2.type);
        Assert.assertEquals(3, data2.streamId);
        Assert.assertArrayEquals("fghi".getBytes(StandardCharsets.UTF_8), data2.data);
    }

    /**
     * Confirm that we account for discarded data frames. It's possible that data frames are in-flight
     * just prior to us canceling a stream.
     */
    @Test
    public void discardedDataFramesAreCounted() throws Exception {
        // Write the mocking script.
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM 3

        peer.sendFrame().headers(false, 3, TestUtil.headerEntries("a", "apple"));
        peer.sendFrame().data(false, 3, data(1024), 1024);
        peer.acceptFrame();// RST_STREAM

        peer.sendFrame().data(true, 3, data(1024), 1024);
        peer.acceptFrame();// RST_STREAM

        peer.play();
        Http2Connection connection = connect(peer);
        Http2Stream stream1 = connection.newStream(TestUtil.headerEntries("b", "bark"), false);
        Source source = stream1.getSource();
        Buffer buffer = new Buffer();
        while ((buffer.size()) != 1024)
            source.read(buffer, 1024);

        stream1.close(CANCEL);
        MockHttp2Peer.InFrame frame1 = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, frame1.type);
        MockHttp2Peer.InFrame frame2 = peer.takeFrame();
        Assert.assertEquals(TYPE_RST_STREAM, frame2.type);
        MockHttp2Peer.InFrame frame3 = peer.takeFrame();
        Assert.assertEquals(TYPE_RST_STREAM, frame3.type);
        Assert.assertEquals(2048, connection.unacknowledgedBytesRead);
    }

    @Test
    public void receiveGoAwayHttp2() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM 3

        peer.acceptFrame();// SYN_STREAM 5

        peer.sendFrame().goAway(3, PROTOCOL_ERROR, Util.EMPTY_BYTE_ARRAY);
        peer.acceptFrame();// PING

        peer.sendFrame().ping(true, 1, 0);
        peer.acceptFrame();// DATA STREAM 3

        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        Http2Stream stream1 = connection.newStream(TestUtil.headerEntries("a", "android"), true);
        Http2Stream stream2 = connection.newStream(TestUtil.headerEntries("b", "banana"), true);
        connection.writePingAndAwaitPong();// Ensure the GO_AWAY that resets stream2 has been received.

        BufferedSink sink1 = Okio.buffer(stream1.getSink());
        BufferedSink sink2 = Okio.buffer(stream2.getSink());
        sink1.writeUtf8("abc");
        try {
            sink2.writeUtf8("abc");
            sink2.flush();
            Assert.fail();
        } catch (IOException expected) {
            Assert.assertEquals("stream was reset: REFUSED_STREAM", expected.getMessage());
        }
        sink1.writeUtf8("def");
        sink1.close();
        try {
            connection.newStream(TestUtil.headerEntries("c", "cola"), true);
            Assert.fail();
        } catch (ConnectionShutdownException expected) {
        }
        Assert.assertTrue(stream1.isOpen());
        Assert.assertFalse(stream2.isOpen());
        Assert.assertEquals(1, connection.openStreamCount());
        // verify the peer received what was expected
        MockHttp2Peer.InFrame synStream1 = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, synStream1.type);
        MockHttp2Peer.InFrame synStream2 = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, synStream2.type);
        MockHttp2Peer.InFrame ping = peer.takeFrame();
        Assert.assertEquals(TYPE_PING, ping.type);
        MockHttp2Peer.InFrame data1 = peer.takeFrame();
        Assert.assertEquals(TYPE_DATA, data1.type);
        Assert.assertEquals(3, data1.streamId);
        Assert.assertArrayEquals("abcdef".getBytes(StandardCharsets.UTF_8), data1.data);
    }

    @Test
    public void readSendsWindowUpdateHttp2() throws Exception {
        int windowSize = 100;
        int windowUpdateThreshold = 50;
        // Write the mocking script.
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.sendFrame().headers(false, 3, TestUtil.headerEntries("a", "android"));
        for (int i = 0; i < 3; i++) {
            // Send frames of summing to size 50, which is windowUpdateThreshold.
            peer.sendFrame().data(false, 3, data(24), 24);
            peer.sendFrame().data(false, 3, data(25), 25);
            peer.sendFrame().data(false, 3, data(1), 1);
            peer.acceptFrame();// connection WINDOW UPDATE

            peer.acceptFrame();// stream WINDOW UPDATE

        }
        peer.sendFrame().data(true, 3, data(0), 0);
        peer.play();
        // Play it back.
        Http2Connection connection = connect(peer);
        connection.okHttpSettings.set(Settings.INITIAL_WINDOW_SIZE, windowSize);
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("b", "banana"), false);
        Assert.assertEquals(0, stream.unacknowledgedBytesRead);
        Assert.assertEquals(Headers.of("a", "android"), stream.takeHeaders());
        Source in = stream.getSource();
        Buffer buffer = new Buffer();
        buffer.writeAll(in);
        Assert.assertEquals((-1), in.read(buffer, 1));
        Assert.assertEquals(150, buffer.size());
        MockHttp2Peer.InFrame synStream = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, synStream.type);
        for (int i = 0; i < 3; i++) {
            List<Integer> windowUpdateStreamIds = new ArrayList<>(2);
            for (int j = 0; j < 2; j++) {
                MockHttp2Peer.InFrame windowUpdate = peer.takeFrame();
                Assert.assertEquals(TYPE_WINDOW_UPDATE, windowUpdate.type);
                windowUpdateStreamIds.add(windowUpdate.streamId);
                Assert.assertEquals(windowUpdateThreshold, windowUpdate.windowSizeIncrement);
            }
            Assert.assertTrue(windowUpdateStreamIds.contains(0));// connection

            Assert.assertTrue(windowUpdateStreamIds.contains(3));// stream

        }
    }

    @Test
    public void serverSendsEmptyDataClientDoesntSendWindowUpdateHttp2() throws Exception {
        // Write the mocking script.
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.sendFrame().headers(false, 3, TestUtil.headerEntries("a", "android"));
        peer.sendFrame().data(true, 3, data(0), 0);
        peer.play();
        // Play it back.
        Http2Connection connection = connect(peer);
        Http2Stream client = connection.newStream(TestUtil.headerEntries("b", "banana"), false);
        Assert.assertEquals((-1), client.getSource().read(new Buffer(), 1));
        // Verify the peer received what was expected.
        MockHttp2Peer.InFrame synStream = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, synStream.type);
        Assert.assertEquals(5, peer.frameCount());
    }

    @Test
    public void clientSendsEmptyDataServerDoesntSendWindowUpdateHttp2() throws Exception {
        // Write the mocking script.
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.acceptFrame();// DATA

        peer.sendFrame().headers(false, 3, TestUtil.headerEntries("a", "android"));
        peer.play();
        // Play it back.
        Http2Connection connection = connect(peer);
        Http2Stream client = connection.newStream(TestUtil.headerEntries("b", "banana"), true);
        BufferedSink out = Okio.buffer(client.getSink());
        out.write(Util.EMPTY_BYTE_ARRAY);
        out.flush();
        out.close();
        // Verify the peer received what was expected.
        Assert.assertEquals(TYPE_HEADERS, peer.takeFrame().type);
        Assert.assertEquals(TYPE_DATA, peer.takeFrame().type);
        Assert.assertEquals(5, peer.frameCount());
    }

    @Test
    public void maxFrameSizeHonored() throws Exception {
        byte[] buff = new byte[(peer.maxOutboundDataLength()) + 1];
        Arrays.fill(buff, ((byte) ('*')));
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.sendFrame().headers(false, 3, TestUtil.headerEntries("a", "android"));
        peer.acceptFrame();// DATA

        peer.acceptFrame();// DATA

        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("b", "banana"), true);
        BufferedSink out = Okio.buffer(stream.getSink());
        out.write(buff);
        out.flush();
        out.close();
        MockHttp2Peer.InFrame synStream = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, synStream.type);
        MockHttp2Peer.InFrame data = peer.takeFrame();
        Assert.assertEquals(peer.maxOutboundDataLength(), data.data.length);
        data = peer.takeFrame();
        Assert.assertEquals(1, data.data.length);
    }

    @Test
    public void pushPromiseStream() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.sendFrame().headers(false, 3, TestUtil.headerEntries("a", "android"));
        final List<Header> expectedRequestHeaders = Arrays.asList(new Header(TARGET_METHOD, "GET"), new Header(TARGET_SCHEME, "https"), new Header(TARGET_AUTHORITY, "squareup.com"), new Header(TARGET_PATH, "/cached"));
        peer.sendFrame().pushPromise(3, 2, expectedRequestHeaders);
        final List<Header> expectedResponseHeaders = Arrays.asList(new Header(RESPONSE_STATUS, "200"));
        peer.sendFrame().headers(true, 2, expectedResponseHeaders);
        peer.sendFrame().data(true, 3, data(0), 0);
        peer.play();
        Http2ConnectionTest.RecordingPushObserver observer = new Http2ConnectionTest.RecordingPushObserver();
        // play it back
        Http2Connection connection = connect(peer, observer, Listener.REFUSE_INCOMING_STREAMS);
        Http2Stream client = connection.newStream(TestUtil.headerEntries("b", "banana"), false);
        Assert.assertEquals((-1), client.getSource().read(new Buffer(), 1));
        // verify the peer received what was expected
        Assert.assertEquals(TYPE_HEADERS, peer.takeFrame().type);
        Assert.assertEquals(expectedRequestHeaders, observer.takeEvent());
        Assert.assertEquals(expectedResponseHeaders, observer.takeEvent());
    }

    @Test
    public void doublePushPromise() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.sendFrame().pushPromise(3, 2, TestUtil.headerEntries("a", "android"));
        peer.acceptFrame();// SYN_REPLY

        peer.sendFrame().pushPromise(3, 2, TestUtil.headerEntries("b", "banana"));
        peer.acceptFrame();// RST_STREAM

        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        connection.newStream(TestUtil.headerEntries("b", "banana"), false);
        // verify the peer received what was expected
        Assert.assertEquals(TYPE_HEADERS, peer.takeFrame().type);
        Assert.assertEquals(PROTOCOL_ERROR, peer.takeFrame().errorCode);
    }

    @Test
    public void pushPromiseStreamsAutomaticallyCancel() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.sendFrame().pushPromise(3, 2, Arrays.asList(new Header(TARGET_METHOD, "GET"), new Header(TARGET_SCHEME, "https"), new Header(TARGET_AUTHORITY, "squareup.com"), new Header(TARGET_PATH, "/cached")));
        peer.sendFrame().headers(true, 2, Arrays.asList(new Header(RESPONSE_STATUS, "200")));
        peer.acceptFrame();// RST_STREAM

        peer.play();
        // play it back
        connect(peer, PushObserver.CANCEL, Listener.REFUSE_INCOMING_STREAMS);
        // verify the peer received what was expected
        MockHttp2Peer.InFrame rstStream = peer.takeFrame();
        Assert.assertEquals(TYPE_RST_STREAM, rstStream.type);
        Assert.assertEquals(2, rstStream.streamId);
        Assert.assertEquals(CANCEL, rstStream.errorCode);
    }

    /**
     * When writing a set of headers fails due to an {@code IOException}, make sure the writer is left
     * in a consistent state so the next writer also gets an {@code IOException} also instead of
     * something worse (like an {@link IllegalStateException}.
     *
     * <p>See https://github.com/square/okhttp/issues/1651
     */
    @Test
    public void socketExceptionWhileWritingHeaders() throws Exception {
        peer.acceptFrame();// SYN_STREAM.

        peer.play();
        String longString = TestUtil.repeat('a', ((INITIAL_MAX_FRAME_SIZE) + 1));
        Socket socket = peer.openSocket();
        Http2Connection connection = new Http2Connection.Builder(true).socket(socket).pushObserver(Http2ConnectionTest.IGNORE).build();
        connection.start(false);
        socket.shutdownOutput();
        try {
            connection.newStream(TestUtil.headerEntries("a", longString), false);
            Assert.fail();
        } catch (IOException expected) {
        }
        try {
            connection.newStream(TestUtil.headerEntries("b", longString), false);
            Assert.fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void clientCreatesStreamAndServerReplies() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.acceptFrame();// DATA

        peer.sendFrame().headers(false, 3, TestUtil.headerEntries("a", "android"));
        peer.sendFrame().data(true, 3, new Buffer().writeUtf8("robot"), 5);
        peer.acceptFrame();// PING

        peer.sendFrame().ping(true, 1, 0);// PING

        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("b", "banana"), true);
        BufferedSink out = Okio.buffer(stream.getSink());
        out.writeUtf8("c3po");
        out.close();
        Assert.assertEquals(Headers.of("a", "android"), stream.takeHeaders());
        assertStreamData("robot", stream.getSource());
        connection.writePingAndAwaitPong();
        Assert.assertEquals(0, connection.openStreamCount());
        // verify the peer received what was expected
        MockHttp2Peer.InFrame synStream = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, synStream.type);
        Assert.assertFalse(synStream.outFinished);
        Assert.assertEquals(3, synStream.streamId);
        Assert.assertEquals((-1), synStream.associatedStreamId);
        Assert.assertEquals(TestUtil.headerEntries("b", "banana"), synStream.headerBlock);
        MockHttp2Peer.InFrame requestData = peer.takeFrame();
        Assert.assertArrayEquals("c3po".getBytes(StandardCharsets.UTF_8), requestData.data);
    }

    @Test
    public void serverFinishesStreamWithHeaders() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.acceptFrame();// PING

        peer.sendFrame().headers(true, 3, TestUtil.headerEntries("headers", "bam"));
        peer.sendFrame().ping(true, 1, 0);// PONG

        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("a", "artichaut"), false);
        connection.writePingAndAwaitPong();
        Assert.assertEquals(Headers.of("headers", "bam"), stream.takeHeaders());
        Assert.assertEquals(Util.EMPTY_HEADERS, stream.trailers());
        Assert.assertEquals(0, connection.openStreamCount());
        // verify the peer received what was expected
        MockHttp2Peer.InFrame synStream = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, synStream.type);
        Assert.assertFalse(synStream.outFinished);
        Assert.assertEquals(3, synStream.streamId);
        Assert.assertEquals((-1), synStream.associatedStreamId);
        Assert.assertEquals(TestUtil.headerEntries("a", "artichaut"), synStream.headerBlock);
    }

    @Test
    public void serverWritesTrailersAndClientReadsTrailers() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.sendFrame().headers(false, 3, TestUtil.headerEntries("headers", "bam"));
        peer.acceptFrame();// PING

        peer.sendFrame().headers(true, 3, TestUtil.headerEntries("trailers", "boom"));
        peer.sendFrame().ping(true, 1, 0);// PONG

        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("a", "artichaut"), false);
        Assert.assertEquals(Headers.of("headers", "bam"), stream.takeHeaders());
        connection.writePingAndAwaitPong();
        Assert.assertEquals(Headers.of("trailers", "boom"), stream.trailers());
        Assert.assertEquals(0, connection.openStreamCount());
        // verify the peer received what was expected
        MockHttp2Peer.InFrame synStream = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, synStream.type);
        Assert.assertFalse(synStream.outFinished);
        Assert.assertEquals(3, synStream.streamId);
        Assert.assertEquals((-1), synStream.associatedStreamId);
        Assert.assertEquals(TestUtil.headerEntries("a", "artichaut"), synStream.headerBlock);
    }

    @Test
    public void serverWritesTrailersWithData() throws Exception {
        // We buffer some outbound data and headers and confirm that the END_STREAM flag comes with the
        // headers (and not with the data).
        // write the mocking script for the client
        peer.setClient(true);
        // Write the mocking script.
        peer.sendFrame().settings(new Settings());
        peer.sendFrame().headers(true, 3, TestUtil.headerEntries("client", "abc"));
        peer.acceptFrame();// ACK

        peer.acceptFrame();// HEADERS STREAM 3

        peer.acceptFrame();// DATA STREAM 3 "abcde"

        peer.acceptFrame();// HEADERS STREAM 3

        peer.play();
        // Play it back.
        Http2Connection connection = connect(peer);
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("a", "android"), true);
        stream.enqueueTrailers(Headers.of("foo", "bar"));
        BufferedSink sink = Okio.buffer(stream.getSink());
        sink.writeUtf8("abcdefghi");
        sink.close();
        // Verify the peer received what was expected.
        MockHttp2Peer.InFrame headers1 = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, headers1.type);
        MockHttp2Peer.InFrame data1 = peer.takeFrame();
        Assert.assertEquals(TYPE_DATA, data1.type);
        Assert.assertEquals(3, data1.streamId);
        Assert.assertArrayEquals("abcdefghi".getBytes(StandardCharsets.UTF_8), data1.data);
        Assert.assertFalse(data1.inFinished);
        MockHttp2Peer.InFrame headers2 = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, headers2.type);
        Assert.assertTrue(headers2.inFinished);
    }

    @Test
    public void clientCannotReadTrailersWithoutExhaustingStream() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.sendFrame().data(false, 3, new Buffer().writeUtf8("robot"), 5);
        peer.sendFrame().headers(true, 3, TestUtil.headerEntries("trailers", "boom"));
        peer.acceptFrame();// PING

        peer.sendFrame().ping(true, 1, 0);// PONG

        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("a", "artichaut"), true);
        connection.writePingAndAwaitPong();
        try {
            stream.trailers();
            Assert.fail();
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void clientCannotReadTrailersIfTheStreamFailed() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.sendFrame().rstStream(3, PROTOCOL_ERROR);
        peer.acceptFrame();// PING

        peer.sendFrame().ping(true, 1, 0);// PONG

        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("a", "artichaut"), true);
        connection.writePingAndAwaitPong();
        try {
            stream.trailers();
            Assert.fail();
        } catch (StreamResetException expected) {
        }
    }

    @Test
    public void serverCannotEnqueueTrailersAfterFinishingTheStream() throws Exception {
        peer.setClient(true);
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// PING

        peer.sendFrame().ping(true, 1, 0);
        peer.play();
        // Play it back.
        Http2Connection connection = connect(peer);
        connection.writePingAndAwaitPong();
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("a", "android"), true);
        // finish the stream
        stream.writeHeaders(TestUtil.headerEntries("b", "berserk"), true, false);
        try {
            stream.enqueueTrailers(Headers.of("trailers", "boom"));
            Assert.fail();
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void noTrailersFrameYieldsEmptyTrailers() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.sendFrame().headers(false, 3, TestUtil.headerEntries("headers", "bam"));
        peer.sendFrame().data(true, 3, new Buffer().writeUtf8("robot"), 5);
        peer.acceptFrame();// PING

        peer.sendFrame().ping(true, 1, 0);// PONG

        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("a", "artichaut"), false);
        BufferedSource source = Okio.buffer(stream.getSource());
        connection.writePingAndAwaitPong();
        Assert.assertEquals(Headers.of("headers", "bam"), stream.takeHeaders());
        Assert.assertEquals("robot", source.readUtf8(5));
        Assert.assertEquals(Util.EMPTY_HEADERS, stream.trailers());
        Assert.assertEquals(0, connection.openStreamCount());
        // verify the peer received what was expected
        MockHttp2Peer.InFrame synStream = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, synStream.type);
        Assert.assertFalse(synStream.outFinished);
        Assert.assertEquals(3, synStream.streamId);
        Assert.assertEquals((-1), synStream.associatedStreamId);
        Assert.assertEquals(TestUtil.headerEntries("a", "artichaut"), synStream.headerBlock);
    }

    @Test
    public void serverReadsHeadersDataHeaders() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.acceptFrame();// DATA

        peer.acceptFrame();// HEADERS

        peer.sendFrame().headers(true, 3, TestUtil.headerEntries("a", "android"));
        peer.acceptFrame();// PING

        peer.sendFrame().ping(true, 1, 0);// PING

        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("b", "banana"), true);
        BufferedSink out = Okio.buffer(stream.getSink());
        out.writeUtf8("c3po");
        out.close();
        stream.writeHeaders(TestUtil.headerEntries("e", "elephant"), false, false);
        connection.writePingAndAwaitPong();
        Assert.assertEquals(0, connection.openStreamCount());
        // verify the peer received what was expected
        MockHttp2Peer.InFrame synStream = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, synStream.type);
        Assert.assertFalse(synStream.outFinished);
        Assert.assertEquals(3, synStream.streamId);
        Assert.assertEquals((-1), synStream.associatedStreamId);
        Assert.assertEquals(TestUtil.headerEntries("b", "banana"), synStream.headerBlock);
        MockHttp2Peer.InFrame requestData = peer.takeFrame();
        Assert.assertArrayEquals("c3po".getBytes(StandardCharsets.UTF_8), requestData.data);
        MockHttp2Peer.InFrame nextFrame = peer.takeFrame();
        Assert.assertEquals(TestUtil.headerEntries("e", "elephant"), nextFrame.headerBlock);
    }

    @Test
    public void clientCreatesStreamAndServerRepliesWithFin() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.acceptFrame();// PING

        peer.sendFrame().headers(true, 3, TestUtil.headerEntries("a", "android"));
        peer.sendFrame().ping(true, 1, 0);
        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        connection.newStream(TestUtil.headerEntries("b", "banana"), false);
        Assert.assertEquals(1, connection.openStreamCount());
        connection.writePingAndAwaitPong();// Ensure that the SYN_REPLY has been received.

        Assert.assertEquals(0, connection.openStreamCount());
        // verify the peer received what was expected
        MockHttp2Peer.InFrame synStream = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, synStream.type);
        MockHttp2Peer.InFrame ping = peer.takeFrame();
        Assert.assertEquals(TYPE_PING, ping.type);
    }

    @Test
    public void serverPingsClient() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.sendFrame().ping(false, 2, 0);
        peer.acceptFrame();// PING

        peer.play();
        // play it back
        connect(peer);
        // verify the peer received what was expected
        MockHttp2Peer.InFrame ping = peer.takeFrame();
        Assert.assertEquals(0, ping.streamId);
        Assert.assertEquals(2, ping.payload1);
        Assert.assertEquals(0, ping.payload2);
        Assert.assertTrue(ping.ack);
    }

    @Test
    public void clientPingsServer() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// PING

        peer.sendFrame().ping(true, 1, 5);
        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        long pingAtNanos = System.nanoTime();
        connection.writePingAndAwaitPong();
        long elapsedNanos = (System.nanoTime()) - pingAtNanos;
        Assert.assertTrue((elapsedNanos > 0));
        Assert.assertTrue((elapsedNanos < (TimeUnit.SECONDS.toNanos(1))));
        // verify the peer received what was expected
        MockHttp2Peer.InFrame pingFrame = peer.takeFrame();
        Assert.assertEquals(TYPE_PING, pingFrame.type);
        Assert.assertEquals(0, pingFrame.streamId);
        Assert.assertEquals(1330343787, pingFrame.payload1);// OkOk

        Assert.assertEquals(-257978967, pingFrame.payload2);// donut

        Assert.assertFalse(pingFrame.ack);
    }

    @Test
    public void unexpectedPingIsNotReturned() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.sendFrame().ping(false, 2, 0);
        peer.acceptFrame();// PING

        peer.sendFrame().ping(true, 3, 0);// This ping will not be returned.

        peer.sendFrame().ping(false, 4, 0);
        peer.acceptFrame();// PING

        peer.play();
        // play it back
        connect(peer);
        // verify the peer received what was expected
        MockHttp2Peer.InFrame ping2 = peer.takeFrame();
        Assert.assertEquals(2, ping2.payload1);
        MockHttp2Peer.InFrame ping4 = peer.takeFrame();
        Assert.assertEquals(4, ping4.payload1);
    }

    @Test
    public void serverSendsSettingsToClient() throws Exception {
        // write the mocking script
        final Settings settings = new Settings();
        settings.set(Settings.MAX_CONCURRENT_STREAMS, 10);
        peer.sendFrame().settings(settings);
        peer.acceptFrame();// ACK

        peer.sendFrame().ping(false, 2, 0);
        peer.acceptFrame();// PING

        peer.play();
        // play it back
        final CountDownLatch maxConcurrentStreamsUpdated = new CountDownLatch(1);
        final AtomicInteger maxConcurrentStreams = new AtomicInteger();
        Http2Connection.Listener listener = new Http2Connection.Listener() {
            @Override
            public void onStream(Http2Stream stream) throws IOException {
                throw new AssertionError();
            }

            @Override
            public void onSettings(Http2Connection connection) {
                maxConcurrentStreams.set(connection.maxConcurrentStreams());
                maxConcurrentStreamsUpdated.countDown();
            }
        };
        Http2Connection connection = connect(peer, Http2ConnectionTest.IGNORE, listener);
        synchronized(connection) {
            Assert.assertEquals(10, connection.peerSettings.getMaxConcurrentStreams((-1)));
        }
        maxConcurrentStreamsUpdated.await();
        Assert.assertEquals(10, maxConcurrentStreams.get());
    }

    @Test
    public void multipleSettingsFramesAreMerged() throws Exception {
        // write the mocking script
        Settings settings1 = new Settings();
        settings1.set(Settings.HEADER_TABLE_SIZE, 10000);
        settings1.set(Settings.INITIAL_WINDOW_SIZE, 20000);
        settings1.set(Settings.MAX_FRAME_SIZE, 30000);
        peer.sendFrame().settings(settings1);
        peer.acceptFrame();// ACK SETTINGS

        Settings settings2 = new Settings();
        settings2.set(Settings.INITIAL_WINDOW_SIZE, 40000);
        settings2.set(Settings.MAX_FRAME_SIZE, 50000);
        settings2.set(Settings.MAX_CONCURRENT_STREAMS, 60000);
        peer.sendFrame().settings(settings2);
        peer.acceptFrame();// ACK SETTINGS

        peer.sendFrame().ping(false, 2, 0);
        peer.acceptFrame();// PING

        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        Assert.assertEquals(TYPE_SETTINGS, peer.takeFrame().type);
        Assert.assertEquals(TYPE_PING, peer.takeFrame().type);
        synchronized(connection) {
            Assert.assertEquals(10000, connection.peerSettings.getHeaderTableSize());
            Assert.assertEquals(40000, connection.peerSettings.getInitialWindowSize());
            Assert.assertEquals(50000, connection.peerSettings.getMaxFrameSize((-1)));
            Assert.assertEquals(60000, connection.peerSettings.getMaxConcurrentStreams((-1)));
        }
    }

    @Test
    public void clearSettingsBeforeMerge() throws Exception {
        // write the mocking script
        Settings settings1 = new Settings();
        settings1.set(Settings.HEADER_TABLE_SIZE, 10000);
        settings1.set(Settings.INITIAL_WINDOW_SIZE, 20000);
        settings1.set(Settings.MAX_FRAME_SIZE, 30000);
        peer.sendFrame().settings(settings1);
        peer.acceptFrame();// ACK

        peer.sendFrame().ping(false, 2, 0);
        peer.acceptFrame();
        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        // fake a settings frame with clear flag set.
        Settings settings2 = new Settings();
        settings2.set(Settings.MAX_CONCURRENT_STREAMS, 60000);
        connection.readerRunnable.settings(true, settings2);
        synchronized(connection) {
            Assert.assertEquals((-1), connection.peerSettings.getHeaderTableSize());
            Assert.assertEquals(Settings.DEFAULT_INITIAL_WINDOW_SIZE, connection.peerSettings.getInitialWindowSize());
            Assert.assertEquals((-1), connection.peerSettings.getMaxFrameSize((-1)));
            Assert.assertEquals(60000, connection.peerSettings.getMaxConcurrentStreams((-1)));
        }
    }

    @Test
    public void bogusDataFrameDoesNotDisruptConnection() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.sendFrame().data(true, 41, new Buffer().writeUtf8("bogus"), 5);
        peer.acceptFrame();// RST_STREAM

        peer.sendFrame().ping(false, 2, 0);
        peer.acceptFrame();// PING

        peer.play();
        // play it back
        connect(peer);
        // verify the peer received what was expected
        MockHttp2Peer.InFrame rstStream = peer.takeFrame();
        Assert.assertEquals(TYPE_RST_STREAM, rstStream.type);
        Assert.assertEquals(41, rstStream.streamId);
        Assert.assertEquals(PROTOCOL_ERROR, rstStream.errorCode);
        MockHttp2Peer.InFrame ping = peer.takeFrame();
        Assert.assertEquals(2, ping.payload1);
    }

    @Test
    public void bogusReplySilentlyIgnored() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.sendFrame().headers(false, 41, TestUtil.headerEntries("a", "android"));
        peer.sendFrame().ping(false, 2, 0);
        peer.acceptFrame();// PING

        peer.play();
        // play it back
        connect(peer);
        // verify the peer received what was expected
        MockHttp2Peer.InFrame ping = peer.takeFrame();
        Assert.assertEquals(2, ping.payload1);
    }

    @Test
    public void serverClosesClientOutputStream() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.sendFrame().rstStream(3, CANCEL);
        peer.acceptFrame();// PING

        peer.sendFrame().ping(true, 1, 0);
        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("a", "android"), true);
        BufferedSink out = Okio.buffer(stream.getSink());
        connection.writePingAndAwaitPong();// Ensure that the RST_CANCEL has been received.

        try {
            out.writeUtf8("square");
            out.flush();
            Assert.fail();
        } catch (IOException expected) {
            Assert.assertEquals("stream was reset: CANCEL", expected.getMessage());
        }
        try {
            out.close();
            Assert.fail();
        } catch (IOException expected) {
            // Close throws because buffered data wasn't flushed.
        }
        Assert.assertEquals(0, connection.openStreamCount());
        // verify the peer received what was expected
        MockHttp2Peer.InFrame synStream = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, synStream.type);
        Assert.assertFalse(synStream.inFinished);
        Assert.assertFalse(synStream.outFinished);
        MockHttp2Peer.InFrame ping = peer.takeFrame();
        Assert.assertEquals(TYPE_PING, ping.type);
    }

    /**
     * Test that the client sends a RST_STREAM if doing so won't disrupt the output stream.
     */
    @Test
    public void clientClosesClientInputStream() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.acceptFrame();// RST_STREAM

        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("a", "android"), false);
        Source in = stream.getSource();
        BufferedSink out = Okio.buffer(stream.getSink());
        in.close();
        try {
            in.read(new Buffer(), 1);
            Assert.fail();
        } catch (IOException expected) {
            Assert.assertEquals("stream closed", expected.getMessage());
        }
        try {
            out.writeUtf8("a");
            out.flush();
            Assert.fail();
        } catch (IOException expected) {
            Assert.assertEquals("stream finished", expected.getMessage());
        }
        Assert.assertEquals(0, connection.openStreamCount());
        // verify the peer received what was expected
        MockHttp2Peer.InFrame synStream = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, synStream.type);
        Assert.assertTrue(synStream.inFinished);
        Assert.assertFalse(synStream.outFinished);
        MockHttp2Peer.InFrame rstStream = peer.takeFrame();
        Assert.assertEquals(TYPE_RST_STREAM, rstStream.type);
        Assert.assertEquals(CANCEL, rstStream.errorCode);
    }

    /**
     * Test that the client doesn't send a RST_STREAM if doing so will disrupt the output stream.
     */
    @Test
    public void clientClosesClientInputStreamIfOutputStreamIsClosed() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.acceptFrame();// DATA

        peer.acceptFrame();// DATA with FLAG_FIN

        peer.acceptFrame();// RST_STREAM

        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("a", "android"), true);
        Source source = stream.getSource();
        BufferedSink out = Okio.buffer(stream.getSink());
        source.close();
        try {
            source.read(new Buffer(), 1);
            Assert.fail();
        } catch (IOException expected) {
            Assert.assertEquals("stream closed", expected.getMessage());
        }
        out.writeUtf8("square");
        out.flush();
        out.close();
        Assert.assertEquals(0, connection.openStreamCount());
        // verify the peer received what was expected
        MockHttp2Peer.InFrame synStream = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, synStream.type);
        Assert.assertFalse(synStream.inFinished);
        Assert.assertFalse(synStream.outFinished);
        MockHttp2Peer.InFrame data = peer.takeFrame();
        Assert.assertEquals(TYPE_DATA, data.type);
        Assert.assertArrayEquals("square".getBytes(StandardCharsets.UTF_8), data.data);
        MockHttp2Peer.InFrame fin = peer.takeFrame();
        Assert.assertEquals(TYPE_DATA, fin.type);
        Assert.assertTrue(fin.inFinished);
        Assert.assertFalse(fin.outFinished);
        MockHttp2Peer.InFrame rstStream = peer.takeFrame();
        Assert.assertEquals(TYPE_RST_STREAM, rstStream.type);
        Assert.assertEquals(CANCEL, rstStream.errorCode);
    }

    @Test
    public void serverClosesClientInputStream() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.sendFrame().headers(false, 3, TestUtil.headerEntries("b", "banana"));
        peer.sendFrame().data(true, 3, new Buffer().writeUtf8("square"), 6);
        peer.acceptFrame();// PING

        peer.sendFrame().ping(true, 1, 0);
        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("a", "android"), false);
        Source source = stream.getSource();
        assertStreamData("square", source);
        connection.writePingAndAwaitPong();// Ensure that inFinished has been received.

        Assert.assertEquals(0, connection.openStreamCount());
        // verify the peer received what was expected
        MockHttp2Peer.InFrame synStream = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, synStream.type);
        Assert.assertTrue(synStream.inFinished);
        Assert.assertFalse(synStream.outFinished);
    }

    @Test
    public void remoteDoubleSynReply() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.sendFrame().headers(false, 3, TestUtil.headerEntries("a", "android"));
        peer.acceptFrame();// PING

        peer.sendFrame().headers(false, 3, TestUtil.headerEntries("b", "banana"));
        peer.sendFrame().ping(true, 1, 0);
        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("c", "cola"), false);
        Assert.assertEquals(Headers.of("a", "android"), stream.takeHeaders());
        connection.writePingAndAwaitPong();// Ensure that the 2nd SYN REPLY has been received.

        // verify the peer received what was expected
        MockHttp2Peer.InFrame synStream = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, synStream.type);
        MockHttp2Peer.InFrame ping = peer.takeFrame();
        Assert.assertEquals(TYPE_PING, ping.type);
    }

    @Test
    public void remoteSendsDataAfterInFinished() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.sendFrame().headers(false, 3, TestUtil.headerEntries("a", "android"));
        peer.sendFrame().data(true, 3, new Buffer().writeUtf8("robot"), 5);
        peer.sendFrame().data(true, 3, new Buffer().writeUtf8("c3po"), 4);
        peer.acceptFrame();// RST_STREAM

        peer.sendFrame().ping(false, 2, 0);// Ping just to make sure the stream was fastforwarded.

        peer.acceptFrame();// PING

        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("b", "banana"), false);
        Assert.assertEquals(Headers.of("a", "android"), stream.takeHeaders());
        assertStreamData("robot", stream.getSource());
        // verify the peer received what was expected
        MockHttp2Peer.InFrame synStream = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, synStream.type);
        MockHttp2Peer.InFrame rstStream = peer.takeFrame();
        Assert.assertEquals(TYPE_RST_STREAM, rstStream.type);
        Assert.assertEquals(3, rstStream.streamId);
        MockHttp2Peer.InFrame ping = peer.takeFrame();
        Assert.assertEquals(TYPE_PING, ping.type);
        Assert.assertEquals(2, ping.payload1);
    }

    @Test
    public void clientDoesNotLimitFlowControl() throws Exception {
        int dataLength = 16384;
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.sendFrame().headers(false, 3, TestUtil.headerEntries("b", "banana"));
        peer.sendFrame().data(false, 3, new Buffer().write(new byte[dataLength]), dataLength);
        peer.sendFrame().data(false, 3, new Buffer().write(new byte[dataLength]), dataLength);
        peer.sendFrame().data(false, 3, new Buffer().write(new byte[dataLength]), dataLength);
        peer.sendFrame().data(false, 3, new Buffer().write(new byte[dataLength]), dataLength);
        peer.sendFrame().data(false, 3, new Buffer().write(new byte[1]), 1);
        peer.sendFrame().ping(false, 2, 0);// Ping just to make sure the stream was fastforwarded.

        peer.acceptFrame();// PING

        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("a", "android"), false);
        Assert.assertEquals(Headers.of("b", "banana"), stream.takeHeaders());
        // verify the peer received what was expected
        MockHttp2Peer.InFrame synStream = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, synStream.type);
        MockHttp2Peer.InFrame ping = peer.takeFrame();
        Assert.assertEquals(TYPE_PING, ping.type);
        Assert.assertEquals(2, ping.payload1);
    }

    @Test
    public void remoteSendsRefusedStreamBeforeReplyHeaders() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.sendFrame().rstStream(3, REFUSED_STREAM);
        peer.sendFrame().ping(false, 2, 0);
        peer.acceptFrame();// PING

        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("a", "android"), false);
        try {
            stream.takeHeaders();
            Assert.fail();
        } catch (IOException expected) {
            Assert.assertEquals("stream was reset: REFUSED_STREAM", expected.getMessage());
        }
        Assert.assertEquals(0, connection.openStreamCount());
        // verify the peer received what was expected
        MockHttp2Peer.InFrame synStream = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, synStream.type);
        MockHttp2Peer.InFrame ping = peer.takeFrame();
        Assert.assertEquals(TYPE_PING, ping.type);
        Assert.assertEquals(2, ping.payload1);
    }

    @Test
    public void receiveGoAway() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM 1

        peer.acceptFrame();// SYN_STREAM 3

        peer.acceptFrame();// PING.

        peer.sendFrame().goAway(3, PROTOCOL_ERROR, EMPTY_BYTE_ARRAY);
        peer.sendFrame().ping(true, 1, 0);
        peer.acceptFrame();// DATA STREAM 1

        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        Http2Stream stream1 = connection.newStream(TestUtil.headerEntries("a", "android"), true);
        Http2Stream stream2 = connection.newStream(TestUtil.headerEntries("b", "banana"), true);
        connection.writePingAndAwaitPong();// Ensure the GO_AWAY that resets stream2 has been received.

        BufferedSink sink1 = Okio.buffer(stream1.getSink());
        BufferedSink sink2 = Okio.buffer(stream2.getSink());
        sink1.writeUtf8("abc");
        try {
            sink2.writeUtf8("abc");
            sink2.flush();
            Assert.fail();
        } catch (IOException expected) {
            Assert.assertEquals("stream was reset: REFUSED_STREAM", expected.getMessage());
        }
        sink1.writeUtf8("def");
        sink1.close();
        try {
            connection.newStream(TestUtil.headerEntries("c", "cola"), false);
            Assert.fail();
        } catch (ConnectionShutdownException expected) {
        }
        Assert.assertTrue(stream1.isOpen());
        Assert.assertFalse(stream2.isOpen());
        Assert.assertEquals(1, connection.openStreamCount());
        // verify the peer received what was expected
        MockHttp2Peer.InFrame synStream1 = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, synStream1.type);
        MockHttp2Peer.InFrame synStream2 = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, synStream2.type);
        MockHttp2Peer.InFrame ping = peer.takeFrame();
        Assert.assertEquals(TYPE_PING, ping.type);
        MockHttp2Peer.InFrame data1 = peer.takeFrame();
        Assert.assertEquals(TYPE_DATA, data1.type);
        Assert.assertEquals(3, data1.streamId);
        Assert.assertArrayEquals("abcdef".getBytes(StandardCharsets.UTF_8), data1.data);
    }

    @Test
    public void sendGoAway() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM 1

        peer.acceptFrame();// GOAWAY

        peer.acceptFrame();// PING

        peer.sendFrame().headers(false, 2, TestUtil.headerEntries("b", "b"));// Should be ignored!

        peer.sendFrame().ping(true, 1, 0);
        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        connection.newStream(TestUtil.headerEntries("a", "android"), false);
        synchronized(connection) {
            if (connection.shutdown) {
                throw new ConnectionShutdownException();
            }
        }
        connection.writePing(false, 1, 2);
        connection.shutdown(PROTOCOL_ERROR);
        Assert.assertEquals(1, connection.openStreamCount());
        connection.awaitPong();// Prevent the peer from exiting prematurely.

        // verify the peer received what was expected
        MockHttp2Peer.InFrame synStream1 = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, synStream1.type);
        MockHttp2Peer.InFrame pingFrame = peer.takeFrame();
        Assert.assertEquals(TYPE_PING, pingFrame.type);
        MockHttp2Peer.InFrame goaway = peer.takeFrame();
        Assert.assertEquals(TYPE_GOAWAY, goaway.type);
        Assert.assertEquals(0, goaway.streamId);
        Assert.assertEquals(PROTOCOL_ERROR, goaway.errorCode);
    }

    @Test
    public void close() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.acceptFrame();// GOAWAY

        peer.acceptFrame();// RST_STREAM

        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("a", "android"), false);
        Assert.assertEquals(1, connection.openStreamCount());
        connection.close();
        Assert.assertEquals(0, connection.openStreamCount());
        try {
            connection.newStream(TestUtil.headerEntries("b", "banana"), false);
            Assert.fail();
        } catch (ConnectionShutdownException expected) {
        }
        BufferedSink sink = Okio.buffer(stream.getSink());
        try {
            sink.writeByte(0);
            sink.flush();
            Assert.fail();
        } catch (IOException expected) {
            Assert.assertEquals("stream finished", expected.getMessage());
        }
        try {
            stream.getSource().read(new Buffer(), 1);
            Assert.fail();
        } catch (IOException expected) {
            Assert.assertEquals("stream was reset: CANCEL", expected.getMessage());
        }
        // verify the peer received what was expected
        MockHttp2Peer.InFrame synStream = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, synStream.type);
        MockHttp2Peer.InFrame goaway = peer.takeFrame();
        Assert.assertEquals(TYPE_GOAWAY, goaway.type);
        MockHttp2Peer.InFrame rstStream = peer.takeFrame();
        Assert.assertEquals(TYPE_RST_STREAM, rstStream.type);
        Assert.assertEquals(3, rstStream.streamId);
    }

    @Test
    public void getResponseHeadersTimesOut() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.acceptFrame();// RST_STREAM

        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("b", "banana"), false);
        stream.readTimeout().timeout(500, TimeUnit.MILLISECONDS);
        long startNanos = System.nanoTime();
        try {
            stream.takeHeaders();
            Assert.fail();
        } catch (InterruptedIOException expected) {
        }
        long elapsedNanos = (System.nanoTime()) - startNanos;
        awaitWatchdogIdle();
        /* 200ms delta */
        Assert.assertEquals(500.0, TimeUnit.NANOSECONDS.toMillis(elapsedNanos), 200.0);
        Assert.assertEquals(0, connection.openStreamCount());
        // verify the peer received what was expected
        Assert.assertEquals(TYPE_HEADERS, peer.takeFrame().type);
        Assert.assertEquals(TYPE_RST_STREAM, peer.takeFrame().type);
    }

    @Test
    public void readTimesOut() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.sendFrame().headers(false, 3, TestUtil.headerEntries("a", "android"));
        peer.acceptFrame();// RST_STREAM

        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("b", "banana"), false);
        stream.readTimeout().timeout(500, TimeUnit.MILLISECONDS);
        Source source = stream.getSource();
        long startNanos = System.nanoTime();
        try {
            source.read(new Buffer(), 1);
            Assert.fail();
        } catch (InterruptedIOException expected) {
        }
        long elapsedNanos = (System.nanoTime()) - startNanos;
        awaitWatchdogIdle();
        /* 200ms delta */
        Assert.assertEquals(500.0, TimeUnit.NANOSECONDS.toMillis(elapsedNanos), 200.0);
        Assert.assertEquals(0, connection.openStreamCount());
        // verify the peer received what was expected
        Assert.assertEquals(TYPE_HEADERS, peer.takeFrame().type);
        Assert.assertEquals(TYPE_RST_STREAM, peer.takeFrame().type);
    }

    @Test
    public void writeTimesOutAwaitingStreamWindow() throws Exception {
        // Set the peer's receive window to 5 bytes!
        Settings peerSettings = new Settings().set(Settings.INITIAL_WINDOW_SIZE, 5);
        // write the mocking script
        peer.sendFrame().settings(peerSettings);
        peer.acceptFrame();// ACK SETTINGS

        peer.acceptFrame();// PING

        peer.sendFrame().ping(true, 1, 0);
        peer.acceptFrame();// SYN_STREAM

        peer.sendFrame().headers(false, 3, TestUtil.headerEntries("a", "android"));
        peer.acceptFrame();// DATA

        peer.acceptFrame();// RST_STREAM

        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        connection.writePingAndAwaitPong();// Make sure settings have been received.

        Http2Stream stream = connection.newStream(TestUtil.headerEntries("b", "banana"), true);
        Sink sink = stream.getSink();
        sink.write(new Buffer().writeUtf8("abcde"), 5);
        stream.writeTimeout().timeout(500, TimeUnit.MILLISECONDS);
        long startNanos = System.nanoTime();
        sink.write(new Buffer().writeUtf8("f"), 1);
        try {
            sink.flush();// This will time out waiting on the write window.

            Assert.fail();
        } catch (InterruptedIOException expected) {
        }
        long elapsedNanos = (System.nanoTime()) - startNanos;
        awaitWatchdogIdle();
        /* 200ms delta */
        Assert.assertEquals(500.0, TimeUnit.NANOSECONDS.toMillis(elapsedNanos), 200.0);
        Assert.assertEquals(0, connection.openStreamCount());
        // verify the peer received what was expected
        Assert.assertEquals(TYPE_PING, peer.takeFrame().type);
        Assert.assertEquals(TYPE_HEADERS, peer.takeFrame().type);
        Assert.assertEquals(TYPE_DATA, peer.takeFrame().type);
        Assert.assertEquals(TYPE_RST_STREAM, peer.takeFrame().type);
    }

    @Test
    public void writeTimesOutAwaitingConnectionWindow() throws Exception {
        // Set the peer's receive window to 5 bytes. Give the stream 5 bytes back, so only the
        // connection-level window is applicable.
        Settings peerSettings = new Settings().set(Settings.INITIAL_WINDOW_SIZE, 5);
        // write the mocking script
        peer.sendFrame().settings(peerSettings);
        peer.acceptFrame();// ACK SETTINGS

        peer.acceptFrame();// PING

        peer.sendFrame().ping(true, 1, 0);
        peer.acceptFrame();// SYN_STREAM

        peer.sendFrame().headers(false, 3, TestUtil.headerEntries("a", "android"));
        peer.acceptFrame();// PING

        peer.sendFrame().ping(true, 3, 0);
        peer.acceptFrame();// DATA

        peer.acceptFrame();// RST_STREAM

        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        connection.writePingAndAwaitPong();// Make sure settings have been acked.

        Http2Stream stream = connection.newStream(TestUtil.headerEntries("b", "banana"), true);
        connection.writePingAndAwaitPong();// Make sure the window update has been received.

        Sink sink = stream.getSink();
        stream.writeTimeout().timeout(500, TimeUnit.MILLISECONDS);
        sink.write(new Buffer().writeUtf8("abcdef"), 6);
        long startNanos = System.nanoTime();
        try {
            sink.flush();// This will time out waiting on the write window.

            Assert.fail();
        } catch (InterruptedIOException expected) {
        }
        long elapsedNanos = (System.nanoTime()) - startNanos;
        awaitWatchdogIdle();
        /* 200ms delta */
        Assert.assertEquals(500.0, TimeUnit.NANOSECONDS.toMillis(elapsedNanos), 200.0);
        Assert.assertEquals(0, connection.openStreamCount());
        // verify the peer received what was expected
        Assert.assertEquals(TYPE_PING, peer.takeFrame().type);
        Assert.assertEquals(TYPE_HEADERS, peer.takeFrame().type);
        Assert.assertEquals(TYPE_PING, peer.takeFrame().type);
        Assert.assertEquals(TYPE_DATA, peer.takeFrame().type);
        Assert.assertEquals(TYPE_RST_STREAM, peer.takeFrame().type);
    }

    @Test
    public void outgoingWritesAreBatched() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.sendFrame().headers(false, 3, TestUtil.headerEntries("a", "android"));
        peer.acceptFrame();// DATA

        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("b", "banana"), true);
        // two outgoing writes
        Sink sink = stream.getSink();
        sink.write(new Buffer().writeUtf8("abcde"), 5);
        sink.write(new Buffer().writeUtf8("fghij"), 5);
        sink.close();
        // verify the peer received one incoming frame
        Assert.assertEquals(TYPE_HEADERS, peer.takeFrame().type);
        MockHttp2Peer.InFrame data = peer.takeFrame();
        Assert.assertEquals(TYPE_DATA, data.type);
        Assert.assertArrayEquals("abcdefghij".getBytes(StandardCharsets.UTF_8), data.data);
        Assert.assertTrue(data.inFinished);
    }

    @Test
    public void headers() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.acceptFrame();// PING

        peer.sendFrame().headers(false, 3, TestUtil.headerEntries("a", "android"));
        peer.sendFrame().headers(false, 3, TestUtil.headerEntries("c", "c3po"));
        peer.sendFrame().ping(true, 1, 0);
        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("b", "banana"), true);
        connection.writePingAndAwaitPong();// Ensure that the HEADERS has been received.

        Assert.assertEquals(Headers.of("a", "android"), stream.takeHeaders());
        Assert.assertEquals(Headers.of("c", "c3po"), stream.takeHeaders());
        // verify the peer received what was expected
        MockHttp2Peer.InFrame synStream = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, synStream.type);
        MockHttp2Peer.InFrame ping = peer.takeFrame();
        Assert.assertEquals(TYPE_PING, ping.type);
    }

    @Test
    public void readMultipleSetsOfResponseHeaders() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.sendFrame().headers(false, 3, TestUtil.headerEntries("a", "android"));
        peer.acceptFrame();// PING

        peer.sendFrame().headers(true, 3, TestUtil.headerEntries("c", "cola"));
        peer.sendFrame().ping(true, 1, 0);// PONG

        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("b", "banana"), true);
        stream.getConnection().flush();
        Assert.assertEquals(Headers.of("a", "android"), stream.takeHeaders());
        connection.writePingAndAwaitPong();
        Assert.assertEquals(Headers.of("c", "cola"), stream.trailers());
        // verify the peer received what was expected
        Assert.assertEquals(TYPE_HEADERS, peer.takeFrame().type);
        Assert.assertEquals(TYPE_PING, peer.takeFrame().type);
    }

    @Test
    public void readSendsWindowUpdate() throws Exception {
        int windowSize = 100;
        int windowUpdateThreshold = 50;
        // Write the mocking script.
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.sendFrame().headers(false, 3, TestUtil.headerEntries("a", "android"));
        for (int i = 0; i < 3; i++) {
            // Send frames of summing to size 50, which is windowUpdateThreshold.
            peer.sendFrame().data(false, 3, data(24), 24);
            peer.sendFrame().data(false, 3, data(25), 25);
            peer.sendFrame().data(false, 3, data(1), 1);
            peer.acceptFrame();// connection WINDOW UPDATE

            peer.acceptFrame();// stream WINDOW UPDATE

        }
        peer.sendFrame().data(true, 3, data(0), 0);
        peer.play();
        // Play it back.
        Http2Connection connection = connect(peer);
        connection.okHttpSettings.set(Settings.INITIAL_WINDOW_SIZE, windowSize);
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("b", "banana"), false);
        Assert.assertEquals(0, stream.unacknowledgedBytesRead);
        Assert.assertEquals(Headers.of("a", "android"), stream.takeHeaders());
        Source in = stream.getSource();
        Buffer buffer = new Buffer();
        buffer.writeAll(in);
        Assert.assertEquals((-1), in.read(buffer, 1));
        Assert.assertEquals(150, buffer.size());
        MockHttp2Peer.InFrame synStream = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, synStream.type);
        for (int i = 0; i < 3; i++) {
            List<Integer> windowUpdateStreamIds = new ArrayList<>(2);
            for (int j = 0; j < 2; j++) {
                MockHttp2Peer.InFrame windowUpdate = peer.takeFrame();
                Assert.assertEquals(TYPE_WINDOW_UPDATE, windowUpdate.type);
                windowUpdateStreamIds.add(windowUpdate.streamId);
                Assert.assertEquals(windowUpdateThreshold, windowUpdate.windowSizeIncrement);
            }
            Assert.assertTrue(windowUpdateStreamIds.contains(0));// connection

            Assert.assertTrue(windowUpdateStreamIds.contains(3));// stream

        }
    }

    @Test
    public void serverSendsEmptyDataClientDoesntSendWindowUpdate() throws Exception {
        // Write the mocking script.
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.sendFrame().headers(false, 3, TestUtil.headerEntries("a", "android"));
        peer.sendFrame().data(true, 3, data(0), 0);
        peer.play();
        // Play it back.
        Http2Connection connection = connect(peer);
        Http2Stream client = connection.newStream(TestUtil.headerEntries("b", "banana"), false);
        Assert.assertEquals((-1), client.getSource().read(new Buffer(), 1));
        // Verify the peer received what was expected.
        MockHttp2Peer.InFrame synStream = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, synStream.type);
        Assert.assertEquals(5, peer.frameCount());
    }

    @Test
    public void clientSendsEmptyDataServerDoesntSendWindowUpdate() throws Exception {
        // Write the mocking script.
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.acceptFrame();// DATA

        peer.sendFrame().headers(false, 3, TestUtil.headerEntries("a", "android"));
        peer.play();
        // Play it back.
        Http2Connection connection = connect(peer);
        Http2Stream client = connection.newStream(TestUtil.headerEntries("b", "banana"), true);
        BufferedSink out = Okio.buffer(client.getSink());
        out.write(EMPTY_BYTE_ARRAY);
        out.flush();
        out.close();
        // Verify the peer received what was expected.
        Assert.assertEquals(TYPE_HEADERS, peer.takeFrame().type);
        Assert.assertEquals(TYPE_DATA, peer.takeFrame().type);
        Assert.assertEquals(5, peer.frameCount());
    }

    @Test
    public void testTruncatedDataFrame() throws Exception {
        // write the mocking script
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// ACK

        peer.acceptFrame();// SYN_STREAM

        peer.sendFrame().headers(false, 3, TestUtil.headerEntries("a", "android"));
        peer.sendFrame().data(false, 3, data(1024), 1024);
        peer.truncateLastFrame((8 + 100));
        peer.play();
        // play it back
        Http2Connection connection = connect(peer);
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("b", "banana"), false);
        Assert.assertEquals(Headers.of("a", "android"), stream.takeHeaders());
        Source in = stream.getSource();
        try {
            Okio.buffer(in).readByteString(101);
            Assert.fail();
        } catch (IOException expected) {
            Assert.assertEquals("stream was reset: PROTOCOL_ERROR", expected.getMessage());
        }
    }

    @Test
    public void blockedStreamDoesntStarveNewStream() throws Exception {
        int framesThatFillWindow = Http2ConnectionTest.roundUp(Settings.DEFAULT_INITIAL_WINDOW_SIZE, peer.maxOutboundDataLength());
        // Write the mocking script. This accepts more data frames than necessary!
        peer.sendFrame().settings(new Settings());
        peer.acceptFrame();// SETTINGS ACK

        peer.acceptFrame();// SYN_STREAM on stream 1

        for (int i = 0; i < framesThatFillWindow; i++) {
            peer.acceptFrame();// DATA on stream 1

        }
        peer.acceptFrame();// SYN_STREAM on stream 2

        peer.acceptFrame();// DATA on stream 2

        peer.play();
        // Play it back.
        Http2Connection connection = connect(peer);
        Http2Stream stream1 = connection.newStream(TestUtil.headerEntries("a", "apple"), true);
        BufferedSink out1 = Okio.buffer(stream1.getSink());
        out1.write(new byte[Settings.DEFAULT_INITIAL_WINDOW_SIZE]);
        out1.flush();
        // Check that we've filled the window for both the stream and also the connection.
        Assert.assertEquals(0, connection.bytesLeftInWriteWindow);
        Assert.assertEquals(0, connection.getStream(3).bytesLeftInWriteWindow);
        // receiving a window update on the connection will unblock new streams.
        connection.readerRunnable.windowUpdate(0, 3);
        Assert.assertEquals(3, connection.bytesLeftInWriteWindow);
        Assert.assertEquals(0, connection.getStream(3).bytesLeftInWriteWindow);
        // Another stream should be able to send data even though 1 is blocked.
        Http2Stream stream2 = connection.newStream(TestUtil.headerEntries("b", "banana"), true);
        BufferedSink out2 = Okio.buffer(stream2.getSink());
        out2.writeUtf8("foo");
        out2.flush();
        Assert.assertEquals(0, connection.bytesLeftInWriteWindow);
        Assert.assertEquals(0, connection.getStream(3).bytesLeftInWriteWindow);
        Assert.assertEquals(((Settings.DEFAULT_INITIAL_WINDOW_SIZE) - 3), connection.getStream(5).bytesLeftInWriteWindow);
    }

    @Test
    public void remoteOmitsInitialSettings() throws Exception {
        // Write the mocking script. Note no SETTINGS frame is sent or acknowledged.
        peer.acceptFrame();// SYN_STREAM

        peer.sendFrame().headers(false, 3, TestUtil.headerEntries("a", "android"));
        peer.acceptFrame();// GOAWAY

        peer.play();
        Http2Connection connection = new Http2Connection.Builder(true).socket(peer.openSocket()).build();
        connection.start(false);
        Http2Stream stream = connection.newStream(TestUtil.headerEntries("b", "banana"), false);
        try {
            stream.takeHeaders();
            Assert.fail();
        } catch (IOException expected) {
            Assert.assertEquals("stream was reset: PROTOCOL_ERROR", expected.getMessage());
        }
        // verify the peer received what was expected
        MockHttp2Peer.InFrame synStream = peer.takeFrame();
        Assert.assertEquals(TYPE_HEADERS, synStream.type);
        MockHttp2Peer.InFrame goaway = peer.takeFrame();
        Assert.assertEquals(TYPE_GOAWAY, goaway.type);
        Assert.assertEquals(PROTOCOL_ERROR, goaway.errorCode);
    }

    static final PushObserver IGNORE = new PushObserver() {
        @Override
        public boolean onRequest(int streamId, List<Header> requestHeaders) {
            return false;
        }

        @Override
        public boolean onHeaders(int streamId, List<Header> responseHeaders, boolean last) {
            return false;
        }

        @Override
        public boolean onData(int streamId, BufferedSource source, int byteCount, boolean last) throws IOException {
            source.skip(byteCount);
            return false;
        }

        @Override
        public void onReset(int streamId, ErrorCode errorCode) {
        }
    };

    private static class RecordingPushObserver implements PushObserver {
        final List<Object> events = new ArrayList<>();

        public synchronized Object takeEvent() throws Exception {
            while (events.isEmpty()) {
                wait();
            } 
            return events.remove(0);
        }

        @Override
        public synchronized boolean onRequest(int streamId, List<Header> requestHeaders) {
            Assert.assertEquals(2, streamId);
            events.add(requestHeaders);
            notifyAll();
            return false;
        }

        @Override
        public synchronized boolean onHeaders(int streamId, List<Header> responseHeaders, boolean last) {
            Assert.assertEquals(2, streamId);
            Assert.assertTrue(last);
            events.add(responseHeaders);
            notifyAll();
            return false;
        }

        @Override
        public synchronized boolean onData(int streamId, BufferedSource source, int byteCount, boolean last) {
            events.add(new AssertionError("onData"));
            notifyAll();
            return false;
        }

        @Override
        public synchronized void onReset(int streamId, ErrorCode errorCode) {
            events.add(new AssertionError("onReset"));
            notifyAll();
        }
    }
}

