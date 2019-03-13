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
package org.graylog2.logmessage;


import java.util.HashMap;
import java.util.Map;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.Tools;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;


public class MessageTest {
    @Test
    public void testIdGetsSet() {
        Message lm = new Message("foo", "bar", Tools.nowUTC());
        Assert.assertNotNull(lm.getId());
        Assert.assertFalse(lm.getId().isEmpty());
    }

    @Test
    public void testIsCompleteSucceeds() {
        Message lm = new Message("foo", "bar", Tools.nowUTC());
        Assert.assertTrue(lm.isComplete());
    }

    @Test
    public void testIsCompleteFails() {
        Message lm = new Message("foo", null, Tools.nowUTC());
        Assert.assertTrue(lm.isComplete());
        lm = new Message("foo", "", Tools.nowUTC());
        Assert.assertTrue(lm.isComplete());
        lm = new Message(null, "bar", Tools.nowUTC());
        Assert.assertFalse(lm.isComplete());
        lm = new Message("", "bar", Tools.nowUTC());
        Assert.assertFalse(lm.isComplete());
        lm = new Message("", "", Tools.nowUTC());
        Assert.assertFalse(lm.isComplete());
        lm = new Message(null, null, Tools.nowUTC());
        Assert.assertFalse(lm.isComplete());
    }

    @Test
    public void testAddField() {
        Message lm = new Message("foo", "bar", Tools.nowUTC());
        lm.addField("ohai", "thar");
        Assert.assertEquals("thar", lm.getField("ohai"));
    }

    @Test
    public void testAddFieldsWithMap() {
        Message lm = new Message("foo", "bar", Tools.nowUTC());
        lm.addField("ohai", "hai");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("lol", "wut");
        map.put("aha", "pipes");
        lm.addFields(map);
        Assert.assertEquals(7, lm.getFieldCount());
        Assert.assertEquals("wut", lm.getField("lol"));
        Assert.assertEquals("pipes", lm.getField("aha"));
        Assert.assertEquals("hai", lm.getField("ohai"));
    }

    @Test
    public void testRemoveField() {
        Message lm = new Message("foo", "bar", Tools.nowUTC());
        lm.addField("something", "foo");
        lm.addField("something_else", "bar");
        lm.removeField("something_else");
        Assert.assertEquals(5, lm.getFieldCount());
        Assert.assertEquals("foo", lm.getField("something"));
    }

    @Test
    public void testRemoveFieldWithNonExistentKey() {
        Message lm = new Message("foo", "bar", Tools.nowUTC());
        lm.addField("something", "foo");
        lm.addField("something_else", "bar");
        lm.removeField("LOLIDONTEXIST");
        Assert.assertEquals(6, lm.getFieldCount());
    }

    @Test
    public void testRemoveFieldDoesNotDeleteReservedFields() {
        DateTime time = Tools.nowUTC();
        Message lm = new Message("foo", "bar", time);
        lm.removeField("source");
        lm.removeField("timestamp");
        lm.removeField("_id");
        Assert.assertTrue(lm.isComplete());
        Assert.assertEquals("foo", lm.getField("message"));
        Assert.assertEquals("bar", lm.getField("source"));
        Assert.assertEquals(time, lm.getField("timestamp"));
        Assert.assertEquals(4, lm.getFieldCount());
    }

    @Test
    public void testToString() {
        Message lm = new Message("foo", "bar", Tools.nowUTC());
        lm.toString();
        // Fine if it does not crash.
    }
}

