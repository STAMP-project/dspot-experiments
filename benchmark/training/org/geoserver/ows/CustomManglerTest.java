/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.ows;


import java.util.Collections;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geoserver.test.SystemTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(SystemTest.class)
public class CustomManglerTest extends GeoServerSystemTestSupport {
    private static final String BASEURL = "http://localhost:8080/geoserver";

    @Test
    public void testBasic() {
        String url = buildURL(CustomManglerTest.BASEURL, "test", null, URLType.SERVICE);
        Assert.assertEquals("http://localhost:8080/geoserver/test?here=iam", url);
    }

    @Test
    public void testKVP() {
        String url = buildURL(CustomManglerTest.BASEURL, "test", Collections.singletonMap("param", "value()"), URLType.SERVICE);
        Assert.assertEquals("http://localhost:8080/geoserver/test?param=value%28%29&here=iam", url);
    }
}

