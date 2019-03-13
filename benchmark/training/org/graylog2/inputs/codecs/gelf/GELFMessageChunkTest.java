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


import org.graylog2.inputs.TestHelper;
import org.graylog2.plugin.Tools;
import org.junit.Assert;
import org.junit.Test;


public class GELFMessageChunkTest {
    public static final String GELF_JSON = "{\"message\":\"foo\",\"host\":\"bar\",\"_lol_utf8\":\"\u00fc\"}";

    @Test
    public void testGetArrival() throws Exception {
        final GELFMessageChunk chunk = buildChunk();
        final long l = Tools.nowUTC().getMillis();
        final long arrival = chunk.getArrival();
        Assert.assertTrue(((l - arrival) < 5000L));// delta shmelta

    }

    @Test
    public void testGetId() throws Exception {
        Assert.assertEquals(TestHelper.toHex("lolwat67"), buildChunk().getId());
    }

    @Test
    public void testGetData() throws Exception {
        Assert.assertArrayEquals(TestHelper.gzipCompress(GELFMessageChunkTest.GELF_JSON), buildChunk().getData());
    }

    @Test
    public void testGetSequenceCount() throws Exception {
        Assert.assertEquals(4, buildChunk().getSequenceCount());
    }

    @Test
    public void testGetSequenceNumber() throws Exception {
        Assert.assertEquals(3, buildChunk().getSequenceNumber());
    }

    @Test
    public void testToString() throws Exception {
        Assert.assertNotNull(buildChunk().toString());
    }
}

