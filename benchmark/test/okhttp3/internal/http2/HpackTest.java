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


import ByteString.EMPTY;
import Hpack.Reader;
import Hpack.Writer;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import okhttp3.TestUtil;
import okio.Buffer;
import okio.ByteString;
import org.junit.Assert;
import org.junit.Test;


public final class HpackTest {
    private final Buffer bytesIn = new Buffer();

    private Reader hpackReader;

    private Buffer bytesOut = new Buffer();

    private Writer hpackWriter;

    /**
     * Variable-length quantity special cases strings which are longer than 127 bytes.  Values such as
     * cookies can be 4KiB, and should be possible to send.
     *
     * <p> http://tools.ietf.org/html/draft-ietf-httpbis-header-compression-12#section-5.2
     */
    @Test
    public void largeHeaderValue() throws IOException {
        char[] value = new char[4096];
        Arrays.fill(value, '!');
        List<Header> headerBlock = TestUtil.headerEntries("cookie", new String(value));
        hpackWriter.writeHeaders(headerBlock);
        bytesIn.writeAll(bytesOut);
        hpackReader.readHeaders();
        Assert.assertEquals(0, hpackReader.headerCount);
        Assert.assertEquals(headerBlock, hpackReader.getAndResetHeaderList());
    }

    /**
     * HPACK has a max header table size, which can be smaller than the max header message. Ensure the
     * larger header content is not lost.
     */
    @Test
    public void tooLargeToHPackIsStillEmitted() throws IOException {
        bytesIn.writeByte(33);// Dynamic table size update (size = 1).

        bytesIn.writeByte(0);// Literal indexed

        bytesIn.writeByte(10);// Literal name (len = 10)

        bytesIn.writeUtf8("custom-key");
        bytesIn.writeByte(13);// Literal value (len = 13)

        bytesIn.writeUtf8("custom-header");
        hpackReader.readHeaders();
        Assert.assertEquals(0, hpackReader.headerCount);
        Assert.assertEquals(TestUtil.headerEntries("custom-key", "custom-header"), hpackReader.getAndResetHeaderList());
    }

    /**
     * Oldest entries are evicted to support newer ones.
     */
    @Test
    public void writerEviction() throws IOException {
        List<Header> headerBlock = TestUtil.headerEntries("custom-foo", "custom-header", "custom-bar", "custom-header", "custom-baz", "custom-header");
        bytesIn.writeByte(64);// Literal indexed

        bytesIn.writeByte(10);// Literal name (len = 10)

        bytesIn.writeUtf8("custom-foo");
        bytesIn.writeByte(13);// Literal value (len = 13)

        bytesIn.writeUtf8("custom-header");
        bytesIn.writeByte(64);// Literal indexed

        bytesIn.writeByte(10);// Literal name (len = 10)

        bytesIn.writeUtf8("custom-bar");
        bytesIn.writeByte(13);// Literal value (len = 13)

        bytesIn.writeUtf8("custom-header");
        bytesIn.writeByte(64);// Literal indexed

        bytesIn.writeByte(10);// Literal name (len = 10)

        bytesIn.writeUtf8("custom-baz");
        bytesIn.writeByte(13);// Literal value (len = 13)

        bytesIn.writeUtf8("custom-header");
        // Set to only support 110 bytes (enough for 2 headers).
        // Use a new Writer because we don't support change the dynamic table
        // size after Writer constructed.
        Hpack.Writer writer = new Hpack.Writer(110, false, bytesOut);
        writer.writeHeaders(headerBlock);
        Assert.assertEquals(bytesIn, bytesOut);
        Assert.assertEquals(2, writer.headerCount);
        int tableLength = writer.dynamicTable.length;
        Header entry = writer.dynamicTable[(tableLength - 1)];
        checkEntry(entry, "custom-bar", "custom-header", 55);
        entry = writer.dynamicTable[(tableLength - 2)];
        checkEntry(entry, "custom-baz", "custom-header", 55);
    }

    @Test
    public void readerEviction() throws IOException {
        List<Header> headerBlock = TestUtil.headerEntries("custom-foo", "custom-header", "custom-bar", "custom-header", "custom-baz", "custom-header");
        // Set to only support 110 bytes (enough for 2 headers).
        bytesIn.writeByte(63);// Dynamic table size update (size = 110).

        bytesIn.writeByte(79);
        bytesIn.writeByte(64);// Literal indexed

        bytesIn.writeByte(10);// Literal name (len = 10)

        bytesIn.writeUtf8("custom-foo");
        bytesIn.writeByte(13);// Literal value (len = 13)

        bytesIn.writeUtf8("custom-header");
        bytesIn.writeByte(64);// Literal indexed

        bytesIn.writeByte(10);// Literal name (len = 10)

        bytesIn.writeUtf8("custom-bar");
        bytesIn.writeByte(13);// Literal value (len = 13)

        bytesIn.writeUtf8("custom-header");
        bytesIn.writeByte(64);// Literal indexed

        bytesIn.writeByte(10);// Literal name (len = 10)

        bytesIn.writeUtf8("custom-baz");
        bytesIn.writeByte(13);// Literal value (len = 13)

        bytesIn.writeUtf8("custom-header");
        hpackReader.readHeaders();
        Assert.assertEquals(2, hpackReader.headerCount);
        Header entry1 = hpackReader.dynamicTable[((readerHeaderTableLength()) - 1)];
        checkEntry(entry1, "custom-bar", "custom-header", 55);
        Header entry2 = hpackReader.dynamicTable[((readerHeaderTableLength()) - 2)];
        checkEntry(entry2, "custom-baz", "custom-header", 55);
        // Once a header field is decoded and added to the reconstructed header
        // list, it cannot be removed from it. Hence, foo is here.
        Assert.assertEquals(headerBlock, hpackReader.getAndResetHeaderList());
        // Simulate receiving a small dynamic table size update, that implies eviction.
        bytesIn.writeByte(63);// Dynamic table size update (size = 55).

        bytesIn.writeByte(24);
        hpackReader.readHeaders();
        Assert.assertEquals(1, hpackReader.headerCount);
    }

    /**
     * Header table backing array is initially 8 long, let's ensure it grows.
     */
    @Test
    public void dynamicallyGrowsBeyond64Entries() throws IOException {
        // Lots of headers need more room!
        hpackReader = new Hpack.Reader(16384, 4096, bytesIn);
        bytesIn.writeByte(63);// Dynamic table size update (size = 16384).

        bytesIn.writeByte(225);
        bytesIn.writeByte(127);
        for (int i = 0; i < 256; i++) {
            bytesIn.writeByte(64);// Literal indexed

            bytesIn.writeByte(10);// Literal name (len = 10)

            bytesIn.writeUtf8("custom-foo");
            bytesIn.writeByte(13);// Literal value (len = 13)

            bytesIn.writeUtf8("custom-header");
        }
        hpackReader.readHeaders();
        Assert.assertEquals(256, hpackReader.headerCount);
    }

    @Test
    public void huffmanDecodingSupported() throws IOException {
        bytesIn.writeByte(68);// == Literal indexed ==

        // Indexed name (idx = 4) -> :path
        bytesIn.writeByte(140);// Literal value Huffman encoded 12 bytes

        // decodes to www.example.com which is length 15
        bytesIn.write(ByteString.decodeHex("f1e3c2e5f23a6ba0ab90f4ff"));
        hpackReader.readHeaders();
        Assert.assertEquals(1, hpackReader.headerCount);
        Assert.assertEquals(52, hpackReader.dynamicTableByteCount);
        Header entry = hpackReader.dynamicTable[((readerHeaderTableLength()) - 1)];
        checkEntry(entry, ":path", "www.example.com", 52);
    }

    /**
     * http://tools.ietf.org/html/draft-ietf-httpbis-header-compression-12#appendix-C.2.1
     */
    @Test
    public void readLiteralHeaderFieldWithIndexing() throws IOException {
        bytesIn.writeByte(64);// Literal indexed

        bytesIn.writeByte(10);// Literal name (len = 10)

        bytesIn.writeUtf8("custom-key");
        bytesIn.writeByte(13);// Literal value (len = 13)

        bytesIn.writeUtf8("custom-header");
        hpackReader.readHeaders();
        Assert.assertEquals(1, hpackReader.headerCount);
        Assert.assertEquals(55, hpackReader.dynamicTableByteCount);
        Header entry = hpackReader.dynamicTable[((readerHeaderTableLength()) - 1)];
        checkEntry(entry, "custom-key", "custom-header", 55);
        Assert.assertEquals(TestUtil.headerEntries("custom-key", "custom-header"), hpackReader.getAndResetHeaderList());
    }

    /**
     * http://tools.ietf.org/html/draft-ietf-httpbis-header-compression-12#appendix-C.2.2
     */
    @Test
    public void literalHeaderFieldWithoutIndexingIndexedName() throws IOException {
        List<Header> headerBlock = TestUtil.headerEntries(":path", "/sample/path");
        bytesIn.writeByte(4);// == Literal not indexed ==

        // Indexed name (idx = 4) -> :path
        bytesIn.writeByte(12);// Literal value (len = 12)

        bytesIn.writeUtf8("/sample/path");
        hpackWriter.writeHeaders(headerBlock);
        Assert.assertEquals(bytesIn, bytesOut);
        hpackReader.readHeaders();
        Assert.assertEquals(0, hpackReader.headerCount);
        Assert.assertEquals(headerBlock, hpackReader.getAndResetHeaderList());
    }

    @Test
    public void literalHeaderFieldWithoutIndexingNewName() throws IOException {
        List<Header> headerBlock = TestUtil.headerEntries("custom-key", "custom-header");
        bytesIn.writeByte(0);// Not indexed

        bytesIn.writeByte(10);// Literal name (len = 10)

        bytesIn.writeUtf8("custom-key");
        bytesIn.writeByte(13);// Literal value (len = 13)

        bytesIn.writeUtf8("custom-header");
        hpackReader.readHeaders();
        Assert.assertEquals(0, hpackReader.headerCount);
        Assert.assertEquals(headerBlock, hpackReader.getAndResetHeaderList());
    }

    @Test
    public void literalHeaderFieldNeverIndexedIndexedName() throws IOException {
        bytesIn.writeByte(20);// == Literal never indexed ==

        // Indexed name (idx = 4) -> :path
        bytesIn.writeByte(12);// Literal value (len = 12)

        bytesIn.writeUtf8("/sample/path");
        hpackReader.readHeaders();
        Assert.assertEquals(0, hpackReader.headerCount);
        Assert.assertEquals(TestUtil.headerEntries(":path", "/sample/path"), hpackReader.getAndResetHeaderList());
    }

    @Test
    public void literalHeaderFieldNeverIndexedNewName() throws IOException {
        List<Header> headerBlock = TestUtil.headerEntries("custom-key", "custom-header");
        bytesIn.writeByte(16);// Never indexed

        bytesIn.writeByte(10);// Literal name (len = 10)

        bytesIn.writeUtf8("custom-key");
        bytesIn.writeByte(13);// Literal value (len = 13)

        bytesIn.writeUtf8("custom-header");
        hpackReader.readHeaders();
        Assert.assertEquals(0, hpackReader.headerCount);
        Assert.assertEquals(headerBlock, hpackReader.getAndResetHeaderList());
    }

    @Test
    public void literalHeaderFieldWithIncrementalIndexingIndexedName() throws IOException {
        List<Header> headerBlock = TestUtil.headerEntries(":path", "/sample/path");
        bytesIn.writeByte(68);// Indexed name (idx = 4) -> :path

        bytesIn.writeByte(12);// Literal value (len = 12)

        bytesIn.writeUtf8("/sample/path");
        hpackReader.readHeaders();
        Assert.assertEquals(1, hpackReader.headerCount);
        Assert.assertEquals(headerBlock, hpackReader.getAndResetHeaderList());
    }

    @Test
    public void literalHeaderFieldWithIncrementalIndexingNewName() throws IOException {
        List<Header> headerBlock = TestUtil.headerEntries("custom-key", "custom-header");
        bytesIn.writeByte(64);// Never indexed

        bytesIn.writeByte(10);// Literal name (len = 10)

        bytesIn.writeUtf8("custom-key");
        bytesIn.writeByte(13);// Literal value (len = 13)

        bytesIn.writeUtf8("custom-header");
        hpackWriter.writeHeaders(headerBlock);
        Assert.assertEquals(bytesIn, bytesOut);
        Assert.assertEquals(1, hpackWriter.headerCount);
        Header entry = hpackWriter.dynamicTable[((hpackWriter.dynamicTable.length) - 1)];
        checkEntry(entry, "custom-key", "custom-header", 55);
        hpackReader.readHeaders();
        Assert.assertEquals(1, hpackReader.headerCount);
        Assert.assertEquals(headerBlock, hpackReader.getAndResetHeaderList());
    }

    @Test
    public void theSameHeaderAfterOneIncrementalIndexed() throws IOException {
        List<Header> headerBlock = TestUtil.headerEntries("custom-key", "custom-header", "custom-key", "custom-header");
        bytesIn.writeByte(64);// Never indexed

        bytesIn.writeByte(10);// Literal name (len = 10)

        bytesIn.writeUtf8("custom-key");
        bytesIn.writeByte(13);// Literal value (len = 13)

        bytesIn.writeUtf8("custom-header");
        bytesIn.writeByte(190);// Indexed name and value (idx = 63)

        hpackWriter.writeHeaders(headerBlock);
        Assert.assertEquals(bytesIn, bytesOut);
        Assert.assertEquals(1, hpackWriter.headerCount);
        Header entry = hpackWriter.dynamicTable[((hpackWriter.dynamicTable.length) - 1)];
        checkEntry(entry, "custom-key", "custom-header", 55);
        hpackReader.readHeaders();
        Assert.assertEquals(1, hpackReader.headerCount);
        Assert.assertEquals(headerBlock, hpackReader.getAndResetHeaderList());
    }

    @Test
    public void staticHeaderIsNotCopiedIntoTheIndexedTable() throws IOException {
        bytesIn.writeByte(130);// == Indexed - Add ==

        // idx = 2 -> :method: GET
        hpackReader.readHeaders();
        Assert.assertEquals(0, hpackReader.headerCount);
        Assert.assertEquals(0, hpackReader.dynamicTableByteCount);
        Assert.assertNull(hpackReader.dynamicTable[((readerHeaderTableLength()) - 1)]);
        Assert.assertEquals(TestUtil.headerEntries(":method", "GET"), hpackReader.getAndResetHeaderList());
    }

    // Example taken from twitter/hpack DecoderTest.testUnusedIndex
    @Test
    public void readIndexedHeaderFieldIndex0() throws IOException {
        bytesIn.writeByte(128);// == Indexed - Add idx = 0

        try {
            hpackReader.readHeaders();
            Assert.fail();
        } catch (IOException e) {
            Assert.assertEquals("index == 0", e.getMessage());
        }
    }

    // Example taken from twitter/hpack DecoderTest.testIllegalIndex
    @Test
    public void readIndexedHeaderFieldTooLargeIndex() throws IOException {
        bytesIn.writeShort(65280);// == Indexed - Add idx = 127

        try {
            hpackReader.readHeaders();
            Assert.fail();
        } catch (IOException e) {
            Assert.assertEquals("Header index too large 127", e.getMessage());
        }
    }

    // Example taken from twitter/hpack DecoderTest.testInsidiousIndex
    @Test
    public void readIndexedHeaderFieldInsidiousIndex() throws IOException {
        bytesIn.writeByte(255);// == Indexed - Add ==

        bytesIn.write(ByteString.decodeHex("8080808008"));// idx = -2147483521

        try {
            hpackReader.readHeaders();
            Assert.fail();
        } catch (IOException e) {
            Assert.assertEquals("Header index too large -2147483521", e.getMessage());
        }
    }

    // Example taken from twitter/hpack DecoderTest.testHeaderTableSizeUpdate
    @Test
    public void minMaxHeaderTableSize() throws IOException {
        bytesIn.writeByte(32);
        hpackReader.readHeaders();
        Assert.assertEquals(0, hpackReader.maxDynamicTableByteCount());
        bytesIn.writeByte(63);// encode size 4096

        bytesIn.writeByte(225);
        bytesIn.writeByte(31);
        hpackReader.readHeaders();
        Assert.assertEquals(4096, hpackReader.maxDynamicTableByteCount());
    }

    // Example taken from twitter/hpack DecoderTest.testIllegalHeaderTableSizeUpdate
    @Test
    public void cannotSetTableSizeLargerThanSettingsValue() throws IOException {
        bytesIn.writeByte(63);// encode size 4097

        bytesIn.writeByte(226);
        bytesIn.writeByte(31);
        try {
            hpackReader.readHeaders();
            Assert.fail();
        } catch (IOException e) {
            Assert.assertEquals("Invalid dynamic table size update 4097", e.getMessage());
        }
    }

    // Example taken from twitter/hpack DecoderTest.testInsidiousMaxHeaderSize
    @Test
    public void readHeaderTableStateChangeInsidiousMaxHeaderByteCount() throws IOException {
        bytesIn.writeByte(63);
        bytesIn.write(ByteString.decodeHex("e1ffffff07"));// count = -2147483648

        try {
            hpackReader.readHeaders();
            Assert.fail();
        } catch (IOException e) {
            Assert.assertEquals("Invalid dynamic table size update -2147483648", e.getMessage());
        }
    }

    /**
     * http://tools.ietf.org/html/draft-ietf-httpbis-header-compression-12#appendix-C.2.4
     */
    @Test
    public void readIndexedHeaderFieldFromStaticTableWithoutBuffering() throws IOException {
        bytesIn.writeByte(32);// Dynamic table size update (size = 0).

        bytesIn.writeByte(130);// == Indexed - Add ==

        // idx = 2 -> :method: GET
        hpackReader.readHeaders();
        // Not buffered in header table.
        Assert.assertEquals(0, hpackReader.headerCount);
        Assert.assertEquals(TestUtil.headerEntries(":method", "GET"), hpackReader.getAndResetHeaderList());
    }

    @Test
    public void readLiteralHeaderWithIncrementalIndexingStaticName() throws IOException {
        bytesIn.writeByte(125);// == Literal indexed ==

        // Indexed name (idx = 60) -> "www-authenticate"
        bytesIn.writeByte(5);// Literal value (len = 5)

        bytesIn.writeUtf8("Basic");
        hpackReader.readHeaders();
        Assert.assertEquals(Arrays.asList(new Header("www-authenticate", "Basic")), hpackReader.getAndResetHeaderList());
    }

    @Test
    public void readLiteralHeaderWithIncrementalIndexingDynamicName() throws IOException {
        bytesIn.writeByte(64);
        bytesIn.writeByte(10);// Literal name (len = 10)

        bytesIn.writeUtf8("custom-foo");
        bytesIn.writeByte(5);// Literal value (len = 5)

        bytesIn.writeUtf8("Basic");
        bytesIn.writeByte(126);
        bytesIn.writeByte(6);// Literal value (len = 6)

        bytesIn.writeUtf8("Basic2");
        hpackReader.readHeaders();
        Assert.assertEquals(Arrays.asList(new Header("custom-foo", "Basic"), new Header("custom-foo", "Basic2")), hpackReader.getAndResetHeaderList());
    }

    /**
     * http://tools.ietf.org/html/draft-ietf-httpbis-header-compression-12#appendix-C.2
     */
    @Test
    public void readRequestExamplesWithoutHuffman() throws IOException {
        firstRequestWithoutHuffman();
        hpackReader.readHeaders();
        checkReadFirstRequestWithoutHuffman();
        secondRequestWithoutHuffman();
        hpackReader.readHeaders();
        checkReadSecondRequestWithoutHuffman();
        thirdRequestWithoutHuffman();
        hpackReader.readHeaders();
        checkReadThirdRequestWithoutHuffman();
    }

    @Test
    public void readFailingRequestExample() throws IOException {
        bytesIn.writeByte(130);// == Indexed - Add ==

        // idx = 2 -> :method: GET
        bytesIn.writeByte(134);// == Indexed - Add ==

        // idx = 7 -> :scheme: http
        bytesIn.writeByte(132);// == Indexed - Add ==

        bytesIn.writeByte(127);// == Bad index! ==

        // Indexed name (idx = 4) -> :authority
        bytesIn.writeByte(15);// Literal value (len = 15)

        bytesIn.writeUtf8("www.example.com");
        try {
            hpackReader.readHeaders();
            Assert.fail();
        } catch (IOException e) {
            Assert.assertEquals("Header index too large 78", e.getMessage());
        }
    }

    /**
     * http://tools.ietf.org/html/draft-ietf-httpbis-header-compression-12#appendix-C.4
     */
    @Test
    public void readRequestExamplesWithHuffman() throws IOException {
        firstRequestWithHuffman();
        hpackReader.readHeaders();
        checkReadFirstRequestWithHuffman();
        secondRequestWithHuffman();
        hpackReader.readHeaders();
        checkReadSecondRequestWithHuffman();
        thirdRequestWithHuffman();
        hpackReader.readHeaders();
        checkReadThirdRequestWithHuffman();
    }

    @Test
    public void readSingleByteInt() throws IOException {
        Assert.assertEquals(10, newReader(byteStream()).readInt(10, 31));
        Assert.assertEquals(10, newReader(byteStream()).readInt((224 | 10), 31));
    }

    @Test
    public void readMultibyteInt() throws IOException {
        Assert.assertEquals(1337, newReader(byteStream(154, 10)).readInt(31, 31));
    }

    @Test
    public void writeSingleByteInt() throws IOException {
        hpackWriter.writeInt(10, 31, 0);
        assertBytes(10);
        hpackWriter.writeInt(10, 31, 224);
        assertBytes((224 | 10));
    }

    @Test
    public void writeMultibyteInt() throws IOException {
        hpackWriter.writeInt(1337, 31, 0);
        assertBytes(31, 154, 10);
        hpackWriter.writeInt(1337, 31, 224);
        assertBytes((224 | 31), 154, 10);
    }

    @Test
    public void max31BitValue() throws IOException {
        hpackWriter.writeInt(2147483647, 31, 0);
        assertBytes(31, 224, 255, 255, 255, 7);
        Assert.assertEquals(2147483647, newReader(byteStream(224, 255, 255, 255, 7)).readInt(31, 31));
    }

    @Test
    public void prefixMask() throws IOException {
        hpackWriter.writeInt(31, 31, 0);
        assertBytes(31, 0);
        Assert.assertEquals(31, newReader(byteStream(0)).readInt(31, 31));
    }

    @Test
    public void prefixMaskMinusOne() throws IOException {
        hpackWriter.writeInt(30, 31, 0);
        assertBytes(30);
        Assert.assertEquals(31, newReader(byteStream(0)).readInt(31, 31));
    }

    @Test
    public void zero() throws IOException {
        hpackWriter.writeInt(0, 31, 0);
        assertBytes(0);
        Assert.assertEquals(0, newReader(byteStream()).readInt(0, 31));
    }

    @Test
    public void lowercaseHeaderNameBeforeEmit() throws IOException {
        hpackWriter.writeHeaders(Arrays.asList(new Header("FoO", "BaR")));
        assertBytes(64, 3, 'f', 'o', 'o', 3, 'B', 'a', 'R');
    }

    @Test
    public void mixedCaseHeaderNameIsMalformed() throws IOException {
        try {
            newReader(byteStream(0, 3, 'F', 'o', 'o', 3, 'B', 'a', 'R')).readHeaders();
            Assert.fail();
        } catch (IOException e) {
            Assert.assertEquals("PROTOCOL_ERROR response malformed: mixed case name: Foo", e.getMessage());
        }
    }

    @Test
    public void emptyHeaderName() throws IOException {
        hpackWriter.writeByteString(ByteString.encodeUtf8(""));
        assertBytes(0);
        Assert.assertEquals(EMPTY, newReader(byteStream(0)).readByteString());
    }

    @Test
    public void emitsDynamicTableSizeUpdate() throws IOException {
        hpackWriter.setHeaderTableSizeSetting(2048);
        hpackWriter.writeHeaders(Arrays.asList(new Header("foo", "bar")));
        // Dynamic table size update (size = 2048).
        assertBytes(63, 225, 15, 64, 3, 'f', 'o', 'o', 3, 'b', 'a', 'r');
        hpackWriter.setHeaderTableSizeSetting(8192);
        hpackWriter.writeHeaders(Arrays.asList(new Header("bar", "foo")));
        // Dynamic table size update (size = 8192).
        assertBytes(63, 225, 63, 64, 3, 'b', 'a', 'r', 3, 'f', 'o', 'o');
        // No more dynamic table updates should be emitted.
        hpackWriter.writeHeaders(Arrays.asList(new Header("far", "boo")));
        assertBytes(64, 3, 'f', 'a', 'r', 3, 'b', 'o', 'o');
    }

    @Test
    public void noDynamicTableSizeUpdateWhenSizeIsEqual() throws IOException {
        int currentSize = hpackWriter.headerTableSizeSetting;
        hpackWriter.setHeaderTableSizeSetting(currentSize);
        hpackWriter.writeHeaders(Arrays.asList(new Header("foo", "bar")));
        assertBytes(64, 3, 'f', 'o', 'o', 3, 'b', 'a', 'r');
    }

    @Test
    public void growDynamicTableSize() throws IOException {
        hpackWriter.setHeaderTableSizeSetting(8192);
        hpackWriter.setHeaderTableSizeSetting(16384);
        hpackWriter.writeHeaders(Arrays.asList(new Header("foo", "bar")));
        // Dynamic table size update (size = 16384).
        assertBytes(63, 225, 127, 64, 3, 'f', 'o', 'o', 3, 'b', 'a', 'r');
    }

    @Test
    public void shrinkDynamicTableSize() throws IOException {
        hpackWriter.setHeaderTableSizeSetting(2048);
        hpackWriter.setHeaderTableSizeSetting(0);
        hpackWriter.writeHeaders(Arrays.asList(new Header("foo", "bar")));
        // Dynamic size update (size = 0).
        assertBytes(32, 64, 3, 'f', 'o', 'o', 3, 'b', 'a', 'r');
    }

    @Test
    public void manyDynamicTableSizeChanges() throws IOException {
        hpackWriter.setHeaderTableSizeSetting(16384);
        hpackWriter.setHeaderTableSizeSetting(8096);
        hpackWriter.setHeaderTableSizeSetting(0);
        hpackWriter.setHeaderTableSizeSetting(4096);
        hpackWriter.setHeaderTableSizeSetting(2048);
        hpackWriter.writeHeaders(Arrays.asList(new Header("foo", "bar")));
        // Dynamic size update (size = 0).
        // Dynamic size update (size = 2048).
        assertBytes(32, 63, 225, 15, 64, 3, 'f', 'o', 'o', 3, 'b', 'a', 'r');
    }

    @Test
    public void dynamicTableEvictionWhenSizeLowered() throws IOException {
        List<Header> headerBlock = TestUtil.headerEntries("custom-key1", "custom-header", "custom-key2", "custom-header");
        hpackWriter.writeHeaders(headerBlock);
        Assert.assertEquals(2, hpackWriter.headerCount);
        hpackWriter.setHeaderTableSizeSetting(56);
        Assert.assertEquals(1, hpackWriter.headerCount);
        hpackWriter.setHeaderTableSizeSetting(0);
        Assert.assertEquals(0, hpackWriter.headerCount);
    }

    @Test
    public void noEvictionOnDynamicTableSizeIncrease() throws IOException {
        List<Header> headerBlock = TestUtil.headerEntries("custom-key1", "custom-header", "custom-key2", "custom-header");
        hpackWriter.writeHeaders(headerBlock);
        Assert.assertEquals(2, hpackWriter.headerCount);
        hpackWriter.setHeaderTableSizeSetting(8192);
        Assert.assertEquals(2, hpackWriter.headerCount);
    }

    @Test
    public void dynamicTableSizeHasAnUpperBound() {
        hpackWriter.setHeaderTableSizeSetting(1048576);
        Assert.assertEquals(16384, hpackWriter.maxDynamicTableByteCount);
    }

    @Test
    public void huffmanEncode() throws IOException {
        hpackWriter = new Hpack.Writer(4096, true, bytesOut);
        hpackWriter.writeHeaders(TestUtil.headerEntries("foo", "bar"));
        ByteString expected = // String literal not Huffman encoded (len = 3).
        // 'foo' Huffman encoded.
        // String literal is Huffman encoded (len = 2).
        // Literal header, new name.
        new Buffer().writeByte(64).writeByte(130).writeByte(148).writeByte(231).writeByte(3).writeByte('b').writeByte('a').writeByte('r').readByteString();
        ByteString actual = bytesOut.readByteString();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void staticTableIndexedHeaders() throws IOException {
        hpackWriter.writeHeaders(TestUtil.headerEntries(":method", "GET"));
        assertBytes(130);
        Assert.assertEquals(0, hpackWriter.headerCount);
        hpackWriter.writeHeaders(TestUtil.headerEntries(":method", "POST"));
        assertBytes(131);
        Assert.assertEquals(0, hpackWriter.headerCount);
        hpackWriter.writeHeaders(TestUtil.headerEntries(":path", "/"));
        assertBytes(132);
        Assert.assertEquals(0, hpackWriter.headerCount);
        hpackWriter.writeHeaders(TestUtil.headerEntries(":path", "/index.html"));
        assertBytes(133);
        Assert.assertEquals(0, hpackWriter.headerCount);
        hpackWriter.writeHeaders(TestUtil.headerEntries(":scheme", "http"));
        assertBytes(134);
        Assert.assertEquals(0, hpackWriter.headerCount);
        hpackWriter.writeHeaders(TestUtil.headerEntries(":scheme", "https"));
        assertBytes(135);
        Assert.assertEquals(0, hpackWriter.headerCount);
    }

    @Test
    public void dynamicTableIndexedHeader() throws IOException {
        hpackWriter.writeHeaders(TestUtil.headerEntries("custom-key", "custom-header"));
        assertBytes(64, 10, 'c', 'u', 's', 't', 'o', 'm', '-', 'k', 'e', 'y', 13, 'c', 'u', 's', 't', 'o', 'm', '-', 'h', 'e', 'a', 'd', 'e', 'r');
        Assert.assertEquals(1, hpackWriter.headerCount);
        hpackWriter.writeHeaders(TestUtil.headerEntries("custom-key", "custom-header"));
        assertBytes(190);
        Assert.assertEquals(1, hpackWriter.headerCount);
    }

    @Test
    public void doNotIndexPseudoHeaders() throws IOException {
        hpackWriter.writeHeaders(TestUtil.headerEntries(":method", "PUT"));
        assertBytes(2, 3, 'P', 'U', 'T');
        Assert.assertEquals(0, hpackWriter.headerCount);
        hpackWriter.writeHeaders(TestUtil.headerEntries(":path", "/okhttp"));
        assertBytes(4, 7, '/', 'o', 'k', 'h', 't', 't', 'p');
        Assert.assertEquals(0, hpackWriter.headerCount);
    }

    @Test
    public void incrementalIndexingWithAuthorityPseudoHeader() throws IOException {
        hpackWriter.writeHeaders(TestUtil.headerEntries(":authority", "foo.com"));
        assertBytes(65, 7, 'f', 'o', 'o', '.', 'c', 'o', 'm');
        Assert.assertEquals(1, hpackWriter.headerCount);
        hpackWriter.writeHeaders(TestUtil.headerEntries(":authority", "foo.com"));
        assertBytes(190);
        Assert.assertEquals(1, hpackWriter.headerCount);
        // If the :authority header somehow changes, it should be re-added to the dynamic table.
        hpackWriter.writeHeaders(TestUtil.headerEntries(":authority", "bar.com"));
        assertBytes(65, 7, 'b', 'a', 'r', '.', 'c', 'o', 'm');
        Assert.assertEquals(2, hpackWriter.headerCount);
        hpackWriter.writeHeaders(TestUtil.headerEntries(":authority", "bar.com"));
        assertBytes(190);
        Assert.assertEquals(2, hpackWriter.headerCount);
    }

    @Test
    public void incrementalIndexingWithStaticTableIndexedName() throws IOException {
        hpackWriter.writeHeaders(TestUtil.headerEntries("accept-encoding", "gzip"));
        assertBytes(80, 4, 'g', 'z', 'i', 'p');
        Assert.assertEquals(1, hpackWriter.headerCount);
        hpackWriter.writeHeaders(TestUtil.headerEntries("accept-encoding", "gzip"));
        assertBytes(190);
        Assert.assertEquals(1, hpackWriter.headerCount);
    }

    @Test
    public void incrementalIndexingWithDynamcTableIndexedName() throws IOException {
        hpackWriter.writeHeaders(TestUtil.headerEntries("foo", "bar"));
        assertBytes(64, 3, 'f', 'o', 'o', 3, 'b', 'a', 'r');
        Assert.assertEquals(1, hpackWriter.headerCount);
        hpackWriter.writeHeaders(TestUtil.headerEntries("foo", "bar1"));
        assertBytes(126, 4, 'b', 'a', 'r', '1');
        Assert.assertEquals(2, hpackWriter.headerCount);
        hpackWriter.writeHeaders(TestUtil.headerEntries("foo", "bar1"));
        assertBytes(190);
        Assert.assertEquals(2, hpackWriter.headerCount);
    }
}

