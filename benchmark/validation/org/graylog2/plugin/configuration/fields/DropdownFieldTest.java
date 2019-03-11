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
package org.graylog2.plugin.configuration.fields;


import ConfigurationField.Optional;
import ConfigurationField.Optional.NOT_OPTIONAL;
import ConfigurationField.Optional.OPTIONAL;
import DropdownField.FIELD_TYPE;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class DropdownFieldTest {
    @Test
    public void testGetFieldType() throws Exception {
        DropdownField f = new DropdownField("test", "Name", "fooval", new HashMap<String, String>(), Optional.NOT_OPTIONAL);
        Assert.assertEquals(FIELD_TYPE, f.getFieldType());
    }

    @Test
    public void testGetName() throws Exception {
        DropdownField f = new DropdownField("test", "Name", "fooval", new HashMap<String, String>(), Optional.NOT_OPTIONAL);
        Assert.assertEquals("test", f.getName());
    }

    @Test
    public void testGetHumanName() throws Exception {
        DropdownField f = new DropdownField("test", "Name", "fooval", new HashMap<String, String>(), Optional.NOT_OPTIONAL);
        Assert.assertEquals("Name", f.getHumanName());
    }

    @Test
    public void testGetDescription() throws Exception {
        DropdownField f = new DropdownField("test", "Name", "fooval", new HashMap<String, String>(), "description", Optional.NOT_OPTIONAL);
        Assert.assertEquals("description", f.getDescription());
    }

    @Test
    public void testGetDefaultValue() throws Exception {
        DropdownField f = new DropdownField("test", "Name", "fooval", new HashMap<String, String>(), Optional.NOT_OPTIONAL);
        Assert.assertEquals("fooval", f.getDefaultValue());
    }

    @Test
    public void testIsOptional() throws Exception {
        DropdownField f = new DropdownField("test", "Name", "fooval", new HashMap<String, String>(), Optional.NOT_OPTIONAL);
        Assert.assertEquals(NOT_OPTIONAL, f.isOptional());
        DropdownField f2 = new DropdownField("test", "Name", "fooval", new HashMap<String, String>(), Optional.OPTIONAL);
        Assert.assertEquals(OPTIONAL, f2.isOptional());
    }

    @Test
    public void testGetValues() throws Exception {
        Map<String, String> values = Maps.newHashMap();
        values.put("foo", "bar");
        values.put("zomg", "baz");
        DropdownField f = new DropdownField("test", "Name", "fooval", values, Optional.NOT_OPTIONAL);
        Assert.assertEquals(values, f.getAdditionalInformation().get("values"));
    }
}

