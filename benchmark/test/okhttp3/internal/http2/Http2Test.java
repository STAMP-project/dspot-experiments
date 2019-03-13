/**
 * Copyright (C) 2013 Square, Inc.
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


import ErrorCode.PROTOCOL_ERROR;
import ErrorCode.PROTOCOL_ERROR.httpCode;
import Http2.FLAG_ACK;
import Http2.FLAG_END_PUSH_PROMISE;
import Http2.FLAG_NONE;
import Http2.INITIAL_MAX_FRAME_SIZE;
import Http2.TYPE_CONTINUATION;
import Http2.TYPE_DATA;
import Http2.TYPE_GOAWAY;
import Http2.TYPE_HEADERS;
import Http2.TYPE_PING;
import Http2.TYPE_PUSH_PROMISE;
import Http2.TYPE_RST_STREAM;
import Http2.TYPE_SETTINGS;
import Http2.TYPE_WINDOW_UPDATE;
import Settings.MAX_FRAME_SIZE;
import Util.EMPTY_BYTE_ARRAY;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import okhttp3.TestUtil;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import org.junit.Assert;
import org.junit.Test;

import static ErrorCode.PROTOCOL_ERROR;
import static Header.TARGET_AUTHORITY;
import static Header.TARGET_METHOD;
import static Header.TARGET_PATH;
import static Header.TARGET_SCHEME;
import static Http2.INITIAL_MAX_FRAME_SIZE;


public final class Http2Test {
    final Buffer frame = new Buffer();

    final Http2Reader reader = new Http2Reader(frame, false);

    final int expectedStreamId = 15;

    @Test
    public void unknownFrameTypeSkipped() throws IOException {
        Http2Test.writeMedium(frame, 4);// has a 4-byte field

        frame.writeByte(99);// type 99

        frame.writeByte(FLAG_NONE);
        frame.writeInt(expectedStreamId);
        frame.writeInt(111111111);// custom data

        reader.nextFrame(false, new BaseTestHandler());// Should not callback.

    }

    @Test
    public void onlyOneLiteralHeadersFrame() throws IOException {
        final List<Header> sentHeaders = TestUtil.headerEntries("name", "value");
        Buffer headerBytes = literalHeaders(sentHeaders);
        Http2Test.writeMedium(frame, ((int) (headerBytes.size())));
        frame.writeByte(TYPE_HEADERS);
        frame.writeByte(((Http2.FLAG_END_HEADERS) | (Http2.FLAG_END_STREAM)));
        frame.writeInt(((expectedStreamId) & 2147483647));
        frame.writeAll(headerBytes);
        Assert.assertEquals(frame, sendHeaderFrames(true, sentHeaders));// Check writer sends the same bytes.

        reader.nextFrame(false, new BaseTestHandler() {
            @Override
            public void headers(boolean inFinished, int streamId, int associatedStreamId, List<Header> headerBlock) {
                Assert.assertTrue(inFinished);
                Assert.assertEquals(expectedStreamId, streamId);
                Assert.assertEquals((-1), associatedStreamId);
                Assert.assertEquals(sentHeaders, headerBlock);
            }
        });
    }

    @Test
    public void headersWithPriority() throws IOException {
        final List<Header> sentHeaders = TestUtil.headerEntries("name", "value");
        Buffer headerBytes = literalHeaders(sentHeaders);
        Http2Test.writeMedium(frame, ((int) ((headerBytes.size()) + 5)));
        frame.writeByte(TYPE_HEADERS);
        frame.writeByte(((Http2.FLAG_END_HEADERS) | (Http2.FLAG_PRIORITY)));
        frame.writeInt(((expectedStreamId) & 2147483647));
        frame.writeInt(0);// Independent stream.

        frame.writeByte(255);// Heaviest weight, zero-indexed.

        frame.writeAll(headerBytes);
        reader.nextFrame(false, new BaseTestHandler() {
            @Override
            public void priority(int streamId, int streamDependency, int weight, boolean exclusive) {
                Assert.assertEquals(0, streamDependency);
                Assert.assertEquals(256, weight);
                Assert.assertFalse(exclusive);
            }

            @Override
            public void headers(boolean inFinished, int streamId, int associatedStreamId, List<Header> nameValueBlock) {
                Assert.assertFalse(inFinished);
                Assert.assertEquals(expectedStreamId, streamId);
                Assert.assertEquals((-1), associatedStreamId);
                Assert.assertEquals(sentHeaders, nameValueBlock);
            }
        });
    }

    /**
     * Headers are compressed, then framed.
     */
    @Test
    public void headersFrameThenContinuation() throws IOException {
        final List<Header> sentHeaders = Http2Test.largeHeaders();
        Buffer headerBlock = literalHeaders(sentHeaders);
        // Write the first headers frame.
        Http2Test.writeMedium(frame, INITIAL_MAX_FRAME_SIZE);
        frame.writeByte(TYPE_HEADERS);
        frame.writeByte(FLAG_NONE);
        frame.writeInt(((expectedStreamId) & 2147483647));
        frame.write(headerBlock, INITIAL_MAX_FRAME_SIZE);
        // Write the continuation frame, specifying no more frames are expected.
        Http2Test.writeMedium(frame, ((int) (headerBlock.size())));
        frame.writeByte(TYPE_CONTINUATION);
        frame.writeByte(Http2.FLAG_END_HEADERS);
        frame.writeInt(((expectedStreamId) & 2147483647));
        frame.writeAll(headerBlock);
        Assert.assertEquals(frame, sendHeaderFrames(false, sentHeaders));// Check writer sends the same bytes.

        // Reading the above frames should result in a concatenated headerBlock.
        reader.nextFrame(false, new BaseTestHandler() {
            @Override
            public void headers(boolean inFinished, int streamId, int associatedStreamId, List<Header> headerBlock) {
                Assert.assertFalse(inFinished);
                Assert.assertEquals(expectedStreamId, streamId);
                Assert.assertEquals((-1), associatedStreamId);
                Assert.assertEquals(sentHeaders, headerBlock);
            }
        });
    }

    @Test
    public void pushPromise() throws IOException {
        final int expectedPromisedStreamId = 11;
        final List<Header> pushPromise = Arrays.asList(new Header(TARGET_METHOD, "GET"), new Header(TARGET_SCHEME, "https"), new Header(TARGET_AUTHORITY, "squareup.com"), new Header(TARGET_PATH, "/"));
        // Write the push promise frame, specifying the associated stream ID.
        Buffer headerBytes = literalHeaders(pushPromise);
        Http2Test.writeMedium(frame, ((int) ((headerBytes.size()) + 4)));
        frame.writeByte(TYPE_PUSH_PROMISE);
        frame.writeByte(FLAG_END_PUSH_PROMISE);
        frame.writeInt(((expectedStreamId) & 2147483647));
        frame.writeInt((expectedPromisedStreamId & 2147483647));
        frame.writeAll(headerBytes);
        Assert.assertEquals(frame, sendPushPromiseFrames(expectedPromisedStreamId, pushPromise));
        reader.nextFrame(false, new BaseTestHandler() {
            @Override
            public void pushPromise(int streamId, int promisedStreamId, List<Header> headerBlock) {
                Assert.assertEquals(expectedStreamId, streamId);
                Assert.assertEquals(expectedPromisedStreamId, promisedStreamId);
                Assert.assertEquals(pushPromise, headerBlock);
            }
        });
    }

    /**
     * Headers are compressed, then framed.
     */
    @Test
    public void pushPromiseThenContinuation() throws IOException {
        final int expectedPromisedStreamId = 11;
        final List<Header> pushPromise = Http2Test.largeHeaders();
        // Decoding the first header will cross frame boundaries.
        Buffer headerBlock = literalHeaders(pushPromise);
        // Write the first headers frame.
        Http2Test.writeMedium(frame, INITIAL_MAX_FRAME_SIZE);
        frame.writeByte(TYPE_PUSH_PROMISE);
        frame.writeByte(FLAG_NONE);
        frame.writeInt(((expectedStreamId) & 2147483647));
        frame.writeInt((expectedPromisedStreamId & 2147483647));
        frame.write(headerBlock, ((INITIAL_MAX_FRAME_SIZE) - 4));
        // Write the continuation frame, specifying no more frames are expected.
        Http2Test.writeMedium(frame, ((int) (headerBlock.size())));
        frame.writeByte(TYPE_CONTINUATION);
        frame.writeByte(Http2.FLAG_END_HEADERS);
        frame.writeInt(((expectedStreamId) & 2147483647));
        frame.writeAll(headerBlock);
        Assert.assertEquals(frame, sendPushPromiseFrames(expectedPromisedStreamId, pushPromise));
        // Reading the above frames should result in a concatenated headerBlock.
        reader.nextFrame(false, new BaseTestHandler() {
            @Override
            public void pushPromise(int streamId, int promisedStreamId, List<Header> headerBlock) {
                Assert.assertEquals(expectedStreamId, streamId);
                Assert.assertEquals(expectedPromisedStreamId, promisedStreamId);
                Assert.assertEquals(pushPromise, headerBlock);
            }
        });
    }

    @Test
    public void readRstStreamFrame() throws IOException {
        Http2Test.writeMedium(frame, 4);
        frame.writeByte(TYPE_RST_STREAM);
        frame.writeByte(FLAG_NONE);
        frame.writeInt(((expectedStreamId) & 2147483647));
        frame.writeInt(httpCode);
        reader.nextFrame(false, new BaseTestHandler() {
            @Override
            public void rstStream(int streamId, ErrorCode errorCode) {
                Assert.assertEquals(expectedStreamId, streamId);
                Assert.assertEquals(PROTOCOL_ERROR, errorCode);
            }
        });
    }

    @Test
    public void readSettingsFrame() throws IOException {
        final int reducedTableSizeBytes = 16;
        Http2Test.writeMedium(frame, 12);// 2 settings * 6 bytes (2 for the code and 4 for the value).

        frame.writeByte(TYPE_SETTINGS);
        frame.writeByte(FLAG_NONE);
        frame.writeInt(0);// Settings are always on the connection stream 0.

        frame.writeShort(1);// SETTINGS_HEADER_TABLE_SIZE

        frame.writeInt(reducedTableSizeBytes);
        frame.writeShort(2);// SETTINGS_ENABLE_PUSH

        frame.writeInt(0);
        reader.nextFrame(false, new BaseTestHandler() {
            @Override
            public void settings(boolean clearPrevious, Settings settings) {
                Assert.assertFalse(clearPrevious);// No clearPrevious in HTTP/2.

                Assert.assertEquals(reducedTableSizeBytes, settings.getHeaderTableSize());
                Assert.assertFalse(settings.getEnablePush(true));
            }
        });
    }

    @Test
    public void readSettingsFrameInvalidPushValue() throws IOException {
        Http2Test.writeMedium(frame, 6);// 2 for the code and 4 for the value

        frame.writeByte(TYPE_SETTINGS);
        frame.writeByte(FLAG_NONE);
        frame.writeInt(0);// Settings are always on the connection stream 0.

        frame.writeShort(2);
        frame.writeInt(2);
        try {
            reader.nextFrame(false, new BaseTestHandler());
            Assert.fail();
        } catch (IOException e) {
            Assert.assertEquals("PROTOCOL_ERROR SETTINGS_ENABLE_PUSH != 0 or 1", e.getMessage());
        }
    }

    @Test
    public void readSettingsFrameUnknownSettingId() throws IOException {
        Http2Test.writeMedium(frame, 6);// 2 for the code and 4 for the value

        frame.writeByte(TYPE_SETTINGS);
        frame.writeByte(FLAG_NONE);
        frame.writeInt(0);// Settings are always on the connection stream 0.

        frame.writeShort(7);// old number for SETTINGS_INITIAL_WINDOW_SIZE

        frame.writeInt(1);
        final AtomicInteger settingValue = new AtomicInteger();
        reader.nextFrame(false, new BaseTestHandler() {
            @Override
            public void settings(boolean clearPrevious, Settings settings) {
                settingValue.set(settings.get(7));
            }
        });
        Assert.assertEquals(settingValue.intValue(), 1);
    }

    @Test
    public void readSettingsFrameExperimentalId() throws IOException {
        Http2Test.writeMedium(frame, 6);// 2 for the code and 4 for the value

        frame.writeByte(TYPE_SETTINGS);
        frame.writeByte(FLAG_NONE);
        frame.writeInt(0);// Settings are always on the connection stream 0.

        frame.write(ByteString.decodeHex("f000"));// Id reserved for experimental use.

        frame.writeInt(1);
        reader.nextFrame(false, new BaseTestHandler() {
            @Override
            public void settings(boolean clearPrevious, Settings settings) {
                // no-op
            }
        });
    }

    @Test
    public void readSettingsFrameNegativeWindowSize() throws IOException {
        Http2Test.writeMedium(frame, 6);// 2 for the code and 4 for the value

        frame.writeByte(TYPE_SETTINGS);
        frame.writeByte(FLAG_NONE);
        frame.writeInt(0);// Settings are always on the connection stream 0.

        frame.writeShort(4);// SETTINGS_INITIAL_WINDOW_SIZE

        frame.writeInt(Integer.MIN_VALUE);
        try {
            reader.nextFrame(false, new BaseTestHandler());
            Assert.fail();
        } catch (IOException e) {
            Assert.assertEquals("PROTOCOL_ERROR SETTINGS_INITIAL_WINDOW_SIZE > 2^31 - 1", e.getMessage());
        }
    }

    @Test
    public void readSettingsFrameNegativeFrameLength() throws IOException {
        Http2Test.writeMedium(frame, 6);// 2 for the code and 4 for the value

        frame.writeByte(TYPE_SETTINGS);
        frame.writeByte(FLAG_NONE);
        frame.writeInt(0);// Settings are always on the connection stream 0.

        frame.writeShort(5);// SETTINGS_MAX_FRAME_SIZE

        frame.writeInt(Integer.MIN_VALUE);
        try {
            reader.nextFrame(false, new BaseTestHandler());
            Assert.fail();
        } catch (IOException e) {
            Assert.assertEquals("PROTOCOL_ERROR SETTINGS_MAX_FRAME_SIZE: -2147483648", e.getMessage());
        }
    }

    @Test
    public void readSettingsFrameTooShortFrameLength() throws IOException {
        Http2Test.writeMedium(frame, 6);// 2 for the code and 4 for the value

        frame.writeByte(TYPE_SETTINGS);
        frame.writeByte(FLAG_NONE);
        frame.writeInt(0);// Settings are always on the connection stream 0.

        frame.writeShort(5);// SETTINGS_MAX_FRAME_SIZE

        frame.writeInt((((int) (Math.pow(2, 14))) - 1));
        try {
            reader.nextFrame(false, new BaseTestHandler());
            Assert.fail();
        } catch (IOException e) {
            Assert.assertEquals("PROTOCOL_ERROR SETTINGS_MAX_FRAME_SIZE: 16383", e.getMessage());
        }
    }

    @Test
    public void readSettingsFrameTooLongFrameLength() throws IOException {
        Http2Test.writeMedium(frame, 6);// 2 for the code and 4 for the value

        frame.writeByte(TYPE_SETTINGS);
        frame.writeByte(FLAG_NONE);
        frame.writeInt(0);// Settings are always on the connection stream 0.

        frame.writeShort(5);// SETTINGS_MAX_FRAME_SIZE

        frame.writeInt(((int) (Math.pow(2, 24))));
        try {
            reader.nextFrame(false, new BaseTestHandler());
            Assert.fail();
        } catch (IOException e) {
            Assert.assertEquals("PROTOCOL_ERROR SETTINGS_MAX_FRAME_SIZE: 16777216", e.getMessage());
        }
    }

    @Test
    public void pingRoundTrip() throws IOException {
        final int expectedPayload1 = 7;
        final int expectedPayload2 = 8;
        Http2Test.writeMedium(frame, 8);// length

        frame.writeByte(TYPE_PING);
        frame.writeByte(FLAG_ACK);
        frame.writeInt(0);// connection-level

        frame.writeInt(expectedPayload1);
        frame.writeInt(expectedPayload2);
        // Check writer sends the same bytes.
        Assert.assertEquals(frame, sendPingFrame(true, expectedPayload1, expectedPayload2));
        reader.nextFrame(false, new BaseTestHandler() {
            @Override
            public void ping(boolean ack, int payload1, int payload2) {
                Assert.assertTrue(ack);
                Assert.assertEquals(expectedPayload1, payload1);
                Assert.assertEquals(expectedPayload2, payload2);
            }
        });
    }

    @Test
    public void maxLengthDataFrame() throws IOException {
        final byte[] expectedData = new byte[INITIAL_MAX_FRAME_SIZE];
        Arrays.fill(expectedData, ((byte) (2)));
        Http2Test.writeMedium(frame, expectedData.length);
        frame.writeByte(TYPE_DATA);
        frame.writeByte(FLAG_NONE);
        frame.writeInt(((expectedStreamId) & 2147483647));
        frame.write(expectedData);
        // Check writer sends the same bytes.
        Assert.assertEquals(frame, sendDataFrame(new Buffer().write(expectedData)));
        reader.nextFrame(false, new BaseTestHandler() {
            @Override
            public void data(boolean inFinished, int streamId, BufferedSource source, int length) throws IOException {
                Assert.assertFalse(inFinished);
                Assert.assertEquals(expectedStreamId, streamId);
                Assert.assertEquals(INITIAL_MAX_FRAME_SIZE, length);
                ByteString data = source.readByteString(length);
                for (byte b : data.toByteArray()) {
                    Assert.assertEquals(2, b);
                }
            }
        });
    }

    @Test
    public void dataFrameNotAssociateWithStream() throws IOException {
        byte[] payload = new byte[]{ 1, 2 };
        Http2Test.writeMedium(frame, payload.length);
        frame.writeByte(TYPE_DATA);
        frame.writeByte(FLAG_NONE);
        frame.writeInt(0);
        frame.write(payload);
        try {
            reader.nextFrame(false, new BaseTestHandler());
            Assert.fail();
        } catch (IOException e) {
            Assert.assertEquals("PROTOCOL_ERROR: TYPE_DATA streamId == 0", e.getMessage());
        }
    }

    /**
     * We do not send SETTINGS_COMPRESS_DATA = 1, nor want to. Let's make sure we error.
     */
    @Test
    public void compressedDataFrameWhenSettingDisabled() throws IOException {
        byte[] expectedData = new byte[INITIAL_MAX_FRAME_SIZE];
        Arrays.fill(expectedData, ((byte) (2)));
        Buffer zipped = Http2Test.gzip(expectedData);
        int zippedSize = ((int) (zipped.size()));
        Http2Test.writeMedium(frame, zippedSize);
        frame.writeByte(TYPE_DATA);
        frame.writeByte(Http2.FLAG_COMPRESSED);
        frame.writeInt(((expectedStreamId) & 2147483647));
        zipped.readAll(frame);
        try {
            reader.nextFrame(false, new BaseTestHandler());
            Assert.fail();
        } catch (IOException e) {
            Assert.assertEquals("PROTOCOL_ERROR: FLAG_COMPRESSED without SETTINGS_COMPRESS_DATA", e.getMessage());
        }
    }

    @Test
    public void readPaddedDataFrame() throws IOException {
        int dataLength = 1123;
        byte[] expectedData = new byte[dataLength];
        Arrays.fill(expectedData, ((byte) (2)));
        int paddingLength = 254;
        byte[] padding = new byte[paddingLength];
        Arrays.fill(padding, ((byte) (0)));
        Http2Test.writeMedium(frame, ((dataLength + paddingLength) + 1));
        frame.writeByte(TYPE_DATA);
        frame.writeByte(Http2.FLAG_PADDED);
        frame.writeInt(((expectedStreamId) & 2147483647));
        frame.writeByte(paddingLength);
        frame.write(expectedData);
        frame.write(padding);
        reader.nextFrame(false, assertData());
        Assert.assertTrue(frame.exhausted());// Padding was skipped.

    }

    @Test
    public void readPaddedDataFrameZeroPadding() throws IOException {
        int dataLength = 1123;
        byte[] expectedData = new byte[dataLength];
        Arrays.fill(expectedData, ((byte) (2)));
        Http2Test.writeMedium(frame, (dataLength + 1));
        frame.writeByte(TYPE_DATA);
        frame.writeByte(Http2.FLAG_PADDED);
        frame.writeInt(((expectedStreamId) & 2147483647));
        frame.writeByte(0);
        frame.write(expectedData);
        reader.nextFrame(false, assertData());
    }

    @Test
    public void readPaddedHeadersFrame() throws IOException {
        int paddingLength = 254;
        byte[] padding = new byte[paddingLength];
        Arrays.fill(padding, ((byte) (0)));
        Buffer headerBlock = literalHeaders(TestUtil.headerEntries("foo", "barrr", "baz", "qux"));
        Http2Test.writeMedium(frame, ((((int) (headerBlock.size())) + paddingLength) + 1));
        frame.writeByte(TYPE_HEADERS);
        frame.writeByte(((Http2.FLAG_END_HEADERS) | (Http2.FLAG_PADDED)));
        frame.writeInt(((expectedStreamId) & 2147483647));
        frame.writeByte(paddingLength);
        frame.writeAll(headerBlock);
        frame.write(padding);
        reader.nextFrame(false, assertHeaderBlock());
        Assert.assertTrue(frame.exhausted());// Padding was skipped.

    }

    @Test
    public void readPaddedHeadersFrameZeroPadding() throws IOException {
        Buffer headerBlock = literalHeaders(TestUtil.headerEntries("foo", "barrr", "baz", "qux"));
        Http2Test.writeMedium(frame, (((int) (headerBlock.size())) + 1));
        frame.writeByte(TYPE_HEADERS);
        frame.writeByte(((Http2.FLAG_END_HEADERS) | (Http2.FLAG_PADDED)));
        frame.writeInt(((expectedStreamId) & 2147483647));
        frame.writeByte(0);
        frame.writeAll(headerBlock);
        reader.nextFrame(false, assertHeaderBlock());
    }

    /**
     * Headers are compressed, then framed.
     */
    @Test
    public void readPaddedHeadersFrameThenContinuation() throws IOException {
        int paddingLength = 254;
        byte[] padding = new byte[paddingLength];
        Arrays.fill(padding, ((byte) (0)));
        // Decoding the first header will cross frame boundaries.
        Buffer headerBlock = literalHeaders(TestUtil.headerEntries("foo", "barrr", "baz", "qux"));
        // Write the first headers frame.
        Http2Test.writeMedium(frame, ((((int) ((headerBlock.size()) / 2)) + paddingLength) + 1));
        frame.writeByte(TYPE_HEADERS);
        frame.writeByte(Http2.FLAG_PADDED);
        frame.writeInt(((expectedStreamId) & 2147483647));
        frame.writeByte(paddingLength);
        frame.write(headerBlock, ((headerBlock.size()) / 2));
        frame.write(padding);
        // Write the continuation frame, specifying no more frames are expected.
        Http2Test.writeMedium(frame, ((int) (headerBlock.size())));
        frame.writeByte(TYPE_CONTINUATION);
        frame.writeByte(Http2.FLAG_END_HEADERS);
        frame.writeInt(((expectedStreamId) & 2147483647));
        frame.writeAll(headerBlock);
        reader.nextFrame(false, assertHeaderBlock());
        Assert.assertTrue(frame.exhausted());
    }

    @Test
    public void tooLargeDataFrame() throws IOException {
        try {
            sendDataFrame(new Buffer().write(new byte[16777216]));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("FRAME_SIZE_ERROR length > 16384: 16777216", e.getMessage());
        }
    }

    @Test
    public void windowUpdateRoundTrip() throws IOException {
        final long expectedWindowSizeIncrement = 2147483647;
        Http2Test.writeMedium(frame, 4);// length

        frame.writeByte(TYPE_WINDOW_UPDATE);
        frame.writeByte(FLAG_NONE);
        frame.writeInt(expectedStreamId);
        frame.writeInt(((int) (expectedWindowSizeIncrement)));
        // Check writer sends the same bytes.
        Assert.assertEquals(frame, windowUpdate(expectedWindowSizeIncrement));
        reader.nextFrame(false, new BaseTestHandler() {
            @Override
            public void windowUpdate(int streamId, long windowSizeIncrement) {
                Assert.assertEquals(expectedStreamId, streamId);
                Assert.assertEquals(expectedWindowSizeIncrement, windowSizeIncrement);
            }
        });
    }

    @Test
    public void badWindowSizeIncrement() throws IOException {
        try {
            windowUpdate(0);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("windowSizeIncrement == 0 || windowSizeIncrement > 0x7fffffffL: 0", e.getMessage());
        }
        try {
            windowUpdate(2147483648L);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("windowSizeIncrement == 0 || windowSizeIncrement > 0x7fffffffL: 2147483648", e.getMessage());
        }
    }

    @Test
    public void goAwayWithoutDebugDataRoundTrip() throws IOException {
        final ErrorCode expectedError = PROTOCOL_ERROR;
        Http2Test.writeMedium(frame, 8);// Without debug data there's only 2 32-bit fields.

        frame.writeByte(TYPE_GOAWAY);
        frame.writeByte(FLAG_NONE);
        frame.writeInt(0);// connection-scope

        frame.writeInt(expectedStreamId);// last good stream.

        frame.writeInt(expectedError.httpCode);
        // Check writer sends the same bytes.
        Assert.assertEquals(frame, sendGoAway(expectedStreamId, expectedError, EMPTY_BYTE_ARRAY));
        reader.nextFrame(false, new BaseTestHandler() {
            @Override
            public void goAway(int lastGoodStreamId, ErrorCode errorCode, ByteString debugData) {
                Assert.assertEquals(expectedStreamId, lastGoodStreamId);
                Assert.assertEquals(expectedError, errorCode);
                Assert.assertEquals(0, debugData.size());
            }
        });
    }

    @Test
    public void goAwayWithDebugDataRoundTrip() throws IOException {
        final ErrorCode expectedError = PROTOCOL_ERROR;
        final ByteString expectedData = ByteString.encodeUtf8("abcdefgh");
        // Compose the expected GOAWAY frame without debug data.
        Http2Test.writeMedium(frame, (8 + (expectedData.size())));
        frame.writeByte(TYPE_GOAWAY);
        frame.writeByte(FLAG_NONE);
        frame.writeInt(0);// connection-scope

        frame.writeInt(0);// never read any stream!

        frame.writeInt(expectedError.httpCode);
        frame.write(expectedData.toByteArray());
        // Check writer sends the same bytes.
        Assert.assertEquals(frame, sendGoAway(0, expectedError, expectedData.toByteArray()));
        reader.nextFrame(false, new BaseTestHandler() {
            @Override
            public void goAway(int lastGoodStreamId, ErrorCode errorCode, ByteString debugData) {
                Assert.assertEquals(0, lastGoodStreamId);
                Assert.assertEquals(expectedError, errorCode);
                Assert.assertEquals(expectedData, debugData);
            }
        });
    }

    @Test
    public void frameSizeError() throws IOException {
        Http2Writer writer = new Http2Writer(new Buffer(), true);
        try {
            writer.frameHeader(0, 16777216, TYPE_DATA, Http2.FLAG_NONE);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // TODO: real max is based on settings between 16384 and 16777215
            Assert.assertEquals("FRAME_SIZE_ERROR length > 16384: 16777216", e.getMessage());
        }
    }

    @Test
    public void ackSettingsAppliesMaxFrameSize() throws IOException {
        int newMaxFrameSize = 16777215;
        Http2Writer writer = new Http2Writer(new Buffer(), true);
        writer.applyAndAckSettings(new Settings().set(MAX_FRAME_SIZE, newMaxFrameSize));
        Assert.assertEquals(newMaxFrameSize, writer.maxDataLength());
        writer.frameHeader(0, newMaxFrameSize, TYPE_DATA, Http2.FLAG_NONE);
    }

    @Test
    public void streamIdHasReservedBit() throws IOException {
        Http2Writer writer = new Http2Writer(new Buffer(), true);
        try {
            int streamId = 3;
            streamId |= 1L << 31;// set reserved bit

            writer.frameHeader(streamId, INITIAL_MAX_FRAME_SIZE, TYPE_DATA, Http2.FLAG_NONE);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("reserved bit set: -2147483645", e.getMessage());
        }
    }
}

