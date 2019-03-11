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
public class NumericConverterTest {
    @Test
    public void testConvert() throws Exception {
        Converter hc = new NumericConverter(new HashMap<String, Object>());
        Assert.assertNull(hc.convert(null));
        Assert.assertEquals("", hc.convert(""));
        Assert.assertEquals("lol no number", hc.convert("lol no number"));
        Assert.assertEquals(9001, hc.convert("9001"));
        Assert.assertEquals(2147483648L, hc.convert("2147483648"));
        Assert.assertEquals(10.4, hc.convert("10.4"));
        Assert.assertEquals(Integer.class, hc.convert("4").getClass());
    }
}

