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
package org.graylog2.inputs.converters;


import com.google.common.collect.Maps;
import java.util.Map;
import org.graylog2.ConfigurationException;
import org.junit.Assert;
import org.junit.Test;


public class CsvConverterTest {
    @Test
    public void testConfigHandling() throws ConfigurationException {
        Map<String, Object> configMap = Maps.newHashMap();
        assertConfigException(configMap);
        configMap.put("column_header", null);
        assertConfigException(configMap);
        configMap.put("column_header", "");
        assertConfigException(configMap);
        // the rest of the fields have defaults
        configMap.put("column_header", "field1");
        CsvConverter csvConverter = new CsvConverter(configMap);
        Assert.assertNotNull(csvConverter);
    }

    @Test
    public void testSuccessfulConversion() throws ConfigurationException {
        Map<String, Object> configMap = Maps.newHashMap();
        configMap.put("column_header", "f1,f2");
        CsvConverter csvConverter = new CsvConverter(configMap);
        @SuppressWarnings("unchecked")
        Map<String, String> result = ((Map<String, String>) (csvConverter.convert("\"content1\",\"cont\\\\ent3\"")));
        Assert.assertEquals("content1", result.get("f1"));
        Assert.assertEquals("cont\\ent3", result.get("f2"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEdgeCases() throws ConfigurationException {
        Map<String, Object> configMap = Maps.newHashMap();
        configMap.put("column_header", "f1,f2");
        CsvConverter csvConverter = new CsvConverter(configMap);
        String resultString = ((String) (csvConverter.convert("")));
        Assert.assertEquals("", resultString);
        // too few fields
        Map<String, String> result = ((Map<String, String>) (csvConverter.convert("field1")));
        Assert.assertNull("Too few fields in data doesn't work", result);
        // too many fields
        result = ((Map<String, String>) (csvConverter.convert("field1,field2,field3")));
        Assert.assertNull("Too many fields in data doesn't work", result);
        // unclosed quote level
        result = ((Map<String, String>) (csvConverter.convert("field1,field2,\"field3")));
        Assert.assertNull("Unbalanced quoting does not work", result);
    }
}

