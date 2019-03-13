/**
 * Copyright (C) 2007 - 2012 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 *  GPLv3 + Classpath exception
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.geoserver.wcs2_0;


import java.util.List;
import org.geoserver.wcs2_0.util.NCNameResourceCodec;
import org.geotools.util.MapEntry;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class CoverageIdConverterTest {
    /**
     * Test of encode method, of class CoverageIdConverter.
     */
    @Test
    public void testEncode() {
        String result = NCNameResourceCodec.encode("ws", "name");
        Assert.assertEquals("ws__name", result);
    }

    @Test
    public void testDecode01() {
        String qualifiedName = "ws__name";
        List<MapEntry<String, String>> decode = NCNameResourceCodec.decode(qualifiedName);
        Assert.assertEquals(1, decode.size());
        Assert.assertEquals("ws", decode.get(0).getKey());
        Assert.assertEquals("name", decode.get(0).getValue());
    }

    @Test
    public void testDecode02() {
        String qualifiedName = "s1__s2__s3";
        List<MapEntry<String, String>> decode = NCNameResourceCodec.decode(qualifiedName);
        Assert.assertEquals(2, decode.size());
        Assert.assertEquals("s1__s2", decode.get(0).getKey());
        Assert.assertEquals("s3", decode.get(0).getValue());
        Assert.assertEquals("s1", decode.get(1).getKey());
        Assert.assertEquals("s2__s3", decode.get(1).getValue());
    }

    @Test
    public void testDecode03() {
        String qualifiedName = "s1___s2";
        List<MapEntry<String, String>> decode = NCNameResourceCodec.decode(qualifiedName);
        Assert.assertEquals(2, decode.size());
        Assert.assertEquals("s1_", decode.get(0).getKey());
        Assert.assertEquals("s2", decode.get(0).getValue());
        Assert.assertEquals("s1", decode.get(1).getKey());
        Assert.assertEquals("_s2", decode.get(1).getValue());
    }

    // (expected=IllegalArgumentException.class)
    @Test
    public void testDecodeBad() {
        String qualifiedName = "bad_qualified_name";
        List<MapEntry<String, String>> decode = NCNameResourceCodec.decode(qualifiedName);
        Assert.assertEquals(1, decode.size());
        Assert.assertNull(decode.get(0).getKey());
        Assert.assertEquals("bad_qualified_name", decode.get(0).getValue());
    }
}

