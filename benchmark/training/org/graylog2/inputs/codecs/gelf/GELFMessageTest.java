/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.inputs.codecs.gelf;


import GELFMessage.Type.CHUNKED;
import GELFMessage.Type.GZIP;
import GELFMessage.Type.UNCOMPRESSED;
import GELFMessage.Type.ZLIB;
import org.graylog2.inputs.TestHelper;
import org.junit.Assert;
import org.junit.Test;


public class GELFMessageTest {
    private static final String GELF_JSON = "{\"version\": \"1.1\", \"message\":\"foobar\",\"host\":\"example.com\",\"_lol_utf8\":\"\u00fc\"}";

    @Test
    public void testGetGELFTypeDetectsZLIBCompressedMessage() throws Exception {
        byte[] fakeData = new byte[20];
        fakeData[0] = ((byte) (120));
        fakeData[1] = ((byte) (156));
        GELFMessage msg = new GELFMessage(fakeData);
        Assert.assertEquals(ZLIB, msg.getGELFType());
    }

    @Test
    public void testGetGELFTypeDetectsGZIPCompressedMessage() throws Exception {
        byte[] fakeData = new byte[20];
        fakeData[0] = ((byte) (31));
        fakeData[1] = ((byte) (139));
        GELFMessage msg = new GELFMessage(fakeData);
        Assert.assertEquals(GZIP, msg.getGELFType());
    }

    @Test
    public void testGetGELFTypeDetectsChunkedMessage() throws Exception {
        byte[] fakeData = new byte[20];
        fakeData[0] = ((byte) (30));
        fakeData[1] = ((byte) (15));
        GELFMessage msg = new GELFMessage(fakeData);
        Assert.assertEquals(CHUNKED, msg.getGELFType());
    }

    @Test
    public void testGetGELFTypeDetectsUncompressedMessage() throws Exception {
        byte[] fakeData = new byte[20];
        fakeData[0] = ((byte) ('{'));
        fakeData[1] = ((byte) ('\n'));
        GELFMessage msg = new GELFMessage(fakeData);
        Assert.assertEquals(UNCOMPRESSED, msg.getGELFType());
    }

    @Test
    public void testGetJSONFromZLIBCompressedMessage() throws Exception {
        for (int level = -1; level <= 9; level++) {
            final GELFMessage msg = new GELFMessage(TestHelper.zlibCompress(GELFMessageTest.GELF_JSON, level));
            Assert.assertEquals(GELFMessageTest.GELF_JSON, msg.getJSON(1024));
        }
    }

    @Test
    public void testGetJSONFromGZIPCompressedMessage() throws Exception {
        GELFMessage msg = new GELFMessage(TestHelper.gzipCompress(GELFMessageTest.GELF_JSON));
        Assert.assertEquals(GELFMessageTest.GELF_JSON, msg.getJSON(1024));
    }

    @Test
    public void testGetJSONFromUncompressedMessage() throws Exception {
        byte[] text = GELFMessageTest.GELF_JSON.getBytes("UTF-8");
        GELFMessage msg = new GELFMessage(text);
        Assert.assertEquals(GELFMessageTest.GELF_JSON, msg.getJSON(1024));
    }

    @Test
    public void testGelfMessageChunkCreation() throws Exception {
        String id = "foobar01";
        int seqNum = 1;
        int seqCnt = 5;
        byte[] data = TestHelper.gzipCompress(GELFMessageTest.GELF_JSON);
        GELFMessage msg = new GELFMessage(TestHelper.buildGELFMessageChunk(id, seqNum, seqCnt, data));
        GELFMessageChunk chunk = new GELFMessageChunk(msg, null);
        Assert.assertEquals(TestHelper.toHex(id), chunk.getId());
        Assert.assertEquals(seqNum, chunk.getSequenceNumber());
        Assert.assertEquals(seqCnt, chunk.getSequenceCount());
        Assert.assertArrayEquals(data, chunk.getData());
    }
}

