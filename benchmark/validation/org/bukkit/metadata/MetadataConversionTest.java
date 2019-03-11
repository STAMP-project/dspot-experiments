/**
 * Copyright (C) 2011  Ryan Michela
 */
/**
 *
 */
/**
 * This program is free software: you can redistribute it and/or modify
 */
/**
 * it under the terms of the GNU General Public License as published by
 */
/**
 * the Free Software Foundation, either version 3 of the License, or
 */
/**
 * (at your option) any later version.
 */
/**
 *
 */
/**
 * This program is distributed in the hope that it will be useful,
 */
/**
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 */
/**
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 */
/**
 * GNU General Public License for more details.
 */
/**
 *
 */
/**
 * You should have received a copy of the GNU General Public License
 */
/**
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bukkit.metadata;


import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.TestPlugin;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class MetadataConversionTest {
    private Plugin plugin = new TestPlugin("x");

    private FixedMetadataValue subject;

    @Test
    public void testFromInt() {
        setSubject(10);
        Assert.assertEquals(10, subject.asInt());
        Assert.assertEquals(10, subject.asFloat(), 1.0E-6);
        Assert.assertEquals(10, subject.asDouble(), 1.0E-6);
        Assert.assertEquals(10, subject.asLong());
        Assert.assertEquals(10, subject.asShort());
        Assert.assertEquals(10, subject.asByte());
        Assert.assertEquals(true, subject.asBoolean());
        Assert.assertEquals("10", subject.asString());
    }

    @Test
    public void testFromFloat() {
        setSubject(10.5);
        Assert.assertEquals(10, subject.asInt());
        Assert.assertEquals(10.5, subject.asFloat(), 1.0E-6);
        Assert.assertEquals(10.5, subject.asDouble(), 1.0E-6);
        Assert.assertEquals(10, subject.asLong());
        Assert.assertEquals(10, subject.asShort());
        Assert.assertEquals(10, subject.asByte());
        Assert.assertEquals(true, subject.asBoolean());
        Assert.assertEquals("10.5", subject.asString());
    }

    @Test
    public void testFromNumericString() {
        setSubject("10");
        Assert.assertEquals(10, subject.asInt());
        Assert.assertEquals(10, subject.asFloat(), 1.0E-6);
        Assert.assertEquals(10, subject.asDouble(), 1.0E-6);
        Assert.assertEquals(10, subject.asLong());
        Assert.assertEquals(10, subject.asShort());
        Assert.assertEquals(10, subject.asByte());
        Assert.assertEquals(false, subject.asBoolean());
        Assert.assertEquals("10", subject.asString());
    }

    @Test
    public void testFromNonNumericString() {
        setSubject("true");
        Assert.assertEquals(0, subject.asInt());
        Assert.assertEquals(0, subject.asFloat(), 1.0E-6);
        Assert.assertEquals(0, subject.asDouble(), 1.0E-6);
        Assert.assertEquals(0, subject.asLong());
        Assert.assertEquals(0, subject.asShort());
        Assert.assertEquals(0, subject.asByte());
        Assert.assertEquals(true, subject.asBoolean());
        Assert.assertEquals("true", subject.asString());
    }

    @Test
    public void testFromNull() {
        setSubject(null);
        Assert.assertEquals(0, subject.asInt());
        Assert.assertEquals(0, subject.asFloat(), 1.0E-6);
        Assert.assertEquals(0, subject.asDouble(), 1.0E-6);
        Assert.assertEquals(0, subject.asLong());
        Assert.assertEquals(0, subject.asShort());
        Assert.assertEquals(0, subject.asByte());
        Assert.assertEquals(false, subject.asBoolean());
        Assert.assertEquals("", subject.asString());
    }
}

