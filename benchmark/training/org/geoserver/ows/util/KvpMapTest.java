/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.ows.util;


import org.junit.Assert;
import org.junit.Test;


public class KvpMapTest {
    @Test
    public void testCaseInsensitive() {
        KvpMap map = new KvpMap();
        map.put("foo", "bar");
        Assert.assertEquals("bar", map.get("FOO"));
        Assert.assertEquals("bar", map.get("foo"));
        Assert.assertEquals("bar", map.getOrDefault("foo", null));
        Assert.assertEquals("bar", map.getOrDefault("FOO", null));
    }
}

