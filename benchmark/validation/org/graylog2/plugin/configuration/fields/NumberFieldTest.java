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
import NumberField.Attribute;
import NumberField.FIELD_TYPE;
import org.junit.Assert;
import org.junit.Test;


public class NumberFieldTest {
    @Test
    public void testGetFieldType() throws Exception {
        NumberField f = new NumberField("test", "Name", 0, "foo", Optional.NOT_OPTIONAL);
        Assert.assertEquals(FIELD_TYPE, f.getFieldType());
    }

    @Test
    public void testGetName() throws Exception {
        NumberField f = new NumberField("test", "Name", 0, "foo", Optional.NOT_OPTIONAL);
        Assert.assertEquals("test", f.getName());
    }

    @Test
    public void testGetHumanName() throws Exception {
        NumberField f = new NumberField("test", "Name", 0, "foo", Optional.NOT_OPTIONAL);
        Assert.assertEquals("Name", f.getHumanName());
    }

    @Test
    public void testGetDescription() throws Exception {
        NumberField f = new NumberField("test", "Name", 0, "foo", Optional.NOT_OPTIONAL);
        Assert.assertEquals("foo", f.getDescription());
    }

    @Test
    public void testGetDefaultValue() throws Exception {
        NumberField f = new NumberField("test", "Name", 9001, "foo", Optional.NOT_OPTIONAL);
        Assert.assertEquals(9001, f.getDefaultValue());
    }

    @Test
    public void testIsOptional() throws Exception {
        NumberField f = new NumberField("test", "Name", 0, "foo", Optional.NOT_OPTIONAL);
        Assert.assertEquals(NOT_OPTIONAL, f.isOptional());
        NumberField f2 = new NumberField("test", "Name", 0, "foo", Optional.OPTIONAL);
        Assert.assertEquals(OPTIONAL, f2.isOptional());
    }

    @Test
    public void testGetAttributes() throws Exception {
        NumberField f = new NumberField("test", "Name", 0, "foo", Optional.NOT_OPTIONAL);
        Assert.assertEquals(0, f.getAttributes().size());
        NumberField f1 = new NumberField("test", "Name", 0, "foo", Attribute.IS_PORT_NUMBER);
        Assert.assertEquals(1, f1.getAttributes().size());
        Assert.assertTrue(f1.getAttributes().contains("is_port_number"));
        NumberField f2 = new NumberField("test", "Name", 0, "foo", Attribute.IS_PORT_NUMBER, Attribute.ONLY_POSITIVE);
        Assert.assertEquals(2, f2.getAttributes().size());
        Assert.assertTrue(f2.getAttributes().contains("is_port_number"));
        Assert.assertTrue(f2.getAttributes().contains("only_positive"));
    }
}

