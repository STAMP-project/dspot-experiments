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
package org.graylog2.plugin;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class MessageSummaryTest {
    public static final ImmutableList<String> STREAM_IDS = ImmutableList.of("stream1", "stream2");

    public static final String INDEX_NAME = "graylog2_3";

    private Message message;

    private MessageSummary messageSummary;

    @Test
    public void testGetIndex() throws Exception {
        Assert.assertEquals(MessageSummaryTest.INDEX_NAME, messageSummary.getIndex());
    }

    @Test
    public void testGetId() throws Exception {
        Assert.assertEquals(message.getId(), messageSummary.getId());
    }

    @Test
    public void testGetSource() throws Exception {
        Assert.assertEquals(message.getSource(), messageSummary.getSource());
    }

    @Test
    public void testGetMessage() throws Exception {
        Assert.assertEquals(message.getMessage(), messageSummary.getMessage());
    }

    @Test
    public void testGetTimestamp() throws Exception {
        Assert.assertEquals(message.getTimestamp(), messageSummary.getTimestamp());
    }

    @Test
    public void testGetStreamIds() throws Exception {
        assertThat(messageSummary.getStreamIds()).containsAll(MessageSummaryTest.STREAM_IDS);
    }

    @Test
    public void testGetFields() throws Exception {
        Assert.assertEquals(new HashMap<String, Object>(), messageSummary.getFields());
        message.addField("foo", "bar");
        Assert.assertEquals(ImmutableMap.of("foo", "bar"), messageSummary.getFields());
    }

    @Test
    public void testJSONSerialization() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final MapType valueType = mapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class);
        final Map<String, Object> map = mapper.readValue(mapper.writeValueAsBytes(messageSummary), valueType);
        Assert.assertEquals(Sets.newHashSet("id", "timestamp", "message", "index", "source", "streamIds", "fields"), map.keySet());
    }

    @Test
    public void testHasField() throws Exception {
        Assert.assertFalse(messageSummary.hasField("foo"));
        message.addField("foo", "bar");
        Assert.assertTrue(messageSummary.hasField("foo"));
    }

    @Test
    public void testGetField() throws Exception {
        Assert.assertNull(messageSummary.getField("foo"));
        message.addField("foo", "bar");
        Assert.assertEquals("bar", messageSummary.getField("foo"));
    }
}

