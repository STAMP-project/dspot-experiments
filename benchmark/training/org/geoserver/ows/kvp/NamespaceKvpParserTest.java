/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.ows.kvp;


import java.util.List;
import javax.xml.XMLConstants;
import org.geoserver.platform.ServiceException;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.helpers.NamespaceSupport;


public class NamespaceKvpParserTest {
    private NamespaceKvpParser parser;

    @Test
    public void testEmpty() throws Exception {
        NamespaceSupport ctx = parser.parse("");
        Assert.assertNotNull(ctx);
        List<String> prefixes = getPrefixes(ctx);
        Assert.assertTrue(prefixes.contains("xml"));// this one is always present

        Assert.assertEquals(1, prefixes.size());
    }

    @Test
    public void testFormatError() throws Exception {
        try {
            parser.parse("xmlns[bad=format]");
            Assert.fail("Expected IAE");
        } catch (ServiceException e) {
            assertProperServiceException(e);
        }
        try {
            parser.parse("xmlns(bad=http://format]");
            Assert.fail("Expected IAE");
        } catch (ServiceException e) {
            assertProperServiceException(e);
        }
        try {
            parser.parse("bad=http://format");
            Assert.fail("Expected IAE");
        } catch (ServiceException e) {
            assertProperServiceException(e);
        }
    }

    @Test
    public void testSingle() throws Exception {
        NamespaceSupport ctx = parser.parse("xmlns(foo=http://bar)");
        Assert.assertEquals("http://bar", ctx.getURI("foo"));
    }

    @Test
    public void testMultiple() throws Exception {
        NamespaceSupport ctx = parser.parse("xmlns(foo=http://bar), xmlns(ex=http://example.com),xmlns(gs=http://geoserver.org)");
        Assert.assertEquals("http://bar", ctx.getURI("foo"));
        Assert.assertEquals("http://example.com", ctx.getURI("ex"));
        Assert.assertEquals("http://geoserver.org", ctx.getURI("gs"));
    }

    @Test
    public void testDefaultNamespace() throws Exception {
        NamespaceSupport ctx = parser.parse("xmlns(http://default.namespace.com)");
        Assert.assertEquals("http://default.namespace.com", ctx.getURI(XMLConstants.DEFAULT_NS_PREFIX));
    }

    @Test
    public void testWfs20Syntax() throws Exception {
        NamespaceKvpParser parser = new NamespaceKvpParser("namespaces", true);
        NamespaceSupport ctx = parser.parse("xmlns(http://bar), xmlns(ex,http://example.com),xmlns(gs,http://geoserver.org)");
        Assert.assertEquals("http://bar", ctx.getURI(""));
        Assert.assertEquals("http://example.com", ctx.getURI("ex"));
        Assert.assertEquals("http://geoserver.org", ctx.getURI("gs"));
    }
}

