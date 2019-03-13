/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc;


import junit.framework.TestCase;
import org.geowebcache.util.URLMangler;


public class ResponseUtilsURLManglerTest extends TestCase {
    private URLMangler urlMangler;

    public void testBuildURL() {
        String url = urlMangler.buildURL("http://foo.example.com", "/foo", "/bar");
        TestCase.assertEquals("http://foo.example.com/foo/bar", url);
    }

    public void testBuildTrailingSlashes() throws Exception {
        String url = urlMangler.buildURL("http://foo.example.com/", "/foo/", "/bar");
        TestCase.assertEquals("http://foo.example.com/foo/bar", url);
    }

    public void testBuildNoLeadingSlashes() throws Exception {
        String url = urlMangler.buildURL("http://foo.example.com/", "foo/", "bar");
        TestCase.assertEquals("http://foo.example.com/foo/bar", url);
    }
}

