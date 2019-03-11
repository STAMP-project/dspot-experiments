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
public class SyslogPriLevelConverterTest {
    @Test
    public void testConvert() throws Exception {
        Converter hc = new SyslogPriLevelConverter(new HashMap<String, Object>());
        Assert.assertNull(hc.convert(null));
        Assert.assertEquals("", hc.convert(""));
        Assert.assertEquals("lol no number", hc.convert("lol no number"));
        Assert.assertEquals(6, hc.convert("14"));// info

        Assert.assertEquals(4, hc.convert("12"));// warning

        Assert.assertEquals(7, hc.convert("7"));// debug

        Assert.assertEquals(7, hc.convert("87"));// debug

        Assert.assertEquals(5, hc.convert("5"));// notice

    }
}

