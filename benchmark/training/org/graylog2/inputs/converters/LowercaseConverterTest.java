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


import java.util.HashMap;
import org.graylog2.plugin.inputs.Converter;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class LowercaseConverterTest {
    @Test
    public void testConvert() throws Exception {
        Converter c = new LowercaseConverter(new HashMap<String, Object>());
        Assert.assertNull(c.convert(null));
        Assert.assertEquals("", c.convert(""));
        Assert.assertEquals("foobar", c.convert("foobar"));
        Assert.assertEquals("foo bar", c.convert("foo BAR"));
        Assert.assertEquals("foobar", c.convert("FooBar"));
        Assert.assertEquals("foobar ", c.convert("foobar "));
        Assert.assertEquals(" foobar", c.convert(" foobar"));
        Assert.assertEquals("foobar", c.convert("FOOBAR"));
    }
}

