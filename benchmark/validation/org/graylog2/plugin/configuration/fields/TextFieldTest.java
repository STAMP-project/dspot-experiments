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
import TextField.Attribute;
import TextField.FIELD_TYPE;
import org.junit.Assert;
import org.junit.Test;


public class TextFieldTest {
    @Test
    public void testGetFieldType() throws Exception {
        TextField f = new TextField("test", "Name", "default", "description");
        Assert.assertEquals(FIELD_TYPE, f.getFieldType());
    }

    @Test
    public void testGetName() throws Exception {
        TextField f = new TextField("test", "Name", "default", "description");
        Assert.assertEquals("test", f.getName());
    }

    @Test
    public void testGetHumanName() throws Exception {
        TextField f = new TextField("test", "Name", "default", "description");
        Assert.assertEquals("Name", f.getHumanName());
    }

    @Test
    public void testGetDescription() throws Exception {
        TextField f = new TextField("test", "Name", "default", "description");
        Assert.assertEquals("description", f.getDescription());
    }

    @Test
    public void testGetDefaultValue() throws Exception {
        TextField f = new TextField("test", "Name", "default", "description");
        Assert.assertEquals("default", f.getDefaultValue());
    }

    @Test
    public void testIsOptional() throws Exception {
        TextField f = new TextField("test", "Name", "default", "description", Optional.OPTIONAL);
        Assert.assertEquals(OPTIONAL, f.isOptional());
        TextField f2 = new TextField("test", "Name", "default", "description", Optional.NOT_OPTIONAL);
        Assert.assertEquals(NOT_OPTIONAL, f2.isOptional());
    }

    @Test
    public void testGetAttributes() throws Exception {
        TextField f = new TextField("test", "Name", "default", "description");
        Assert.assertEquals(0, f.getAttributes().size());
        TextField f1 = new TextField("test", "Name", "default", "description", Attribute.IS_PASSWORD);
        Assert.assertEquals(1, f1.getAttributes().size());
        Assert.assertTrue(f1.getAttributes().contains("is_password"));
    }
}

