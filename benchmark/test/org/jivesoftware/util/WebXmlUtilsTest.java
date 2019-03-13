package org.jivesoftware.util;


import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.dom4j.Document;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link WebXmlUtils}
 *
 * @author Guus der Kinderen, guus.der.kinderen@gmail.com
 */
public class WebXmlUtilsTest {
    @Test
    public void testGetServletNames() throws Exception {
        // Setup fixture.
        final Document webXml = WebXmlUtils.asDocument(new File(WebXmlUtilsTest.class.getResource("/org/jivesoftware/util/test-web.xml").toURI()));
        // Execute system under test.
        final List<String> results = WebXmlUtils.getServletNames(webXml);
        // Verify result.
        Assert.assertNotNull(results);
        final Iterator iterator = results.iterator();// Names should be reported in order.

        Assert.assertEquals("PluginServlet", iterator.next());
        Assert.assertEquals("FaviconServlet", iterator.next());
        Assert.assertEquals("dwr-invoker", iterator.next());
        Assert.assertEquals("PluginIconServlet", iterator.next());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testGetFilterNames() throws Exception {
        // Setup fixture.
        final Document webXml = WebXmlUtils.asDocument(new File(WebXmlUtilsTest.class.getResource("/org/jivesoftware/util/test-web.xml").toURI()));
        // Execute system under test.
        final List<String> results = WebXmlUtils.getFilterNames(webXml);
        // Verify result.
        Assert.assertNotNull(results);
        final Iterator iterator = results.iterator();// Names should be reported in order.

        Assert.assertEquals("AuthCheck", iterator.next());
        Assert.assertEquals("PluginFilter", iterator.next());
        Assert.assertEquals("Set Character Encoding", iterator.next());
        Assert.assertEquals("LocaleFilter", iterator.next());
        Assert.assertEquals("sitemesh", iterator.next());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testGetServletClassName() throws Exception {
        // Setup fixture.
        final Document webXml = WebXmlUtils.asDocument(new File(WebXmlUtilsTest.class.getResource("/org/jivesoftware/util/test-web.xml").toURI()));
        final String servletName = "dwr-invoker";
        // Execute system under test.
        final String result = WebXmlUtils.getServletClassName(webXml, servletName);
        // Verify result.
        Assert.assertEquals("uk.ltd.getahead.dwr.DWRServlet", result);
    }

    @Test
    public void testGetServletClassNameForNonExistingServlet() throws Exception {
        // Setup fixture.
        final Document webXml = WebXmlUtils.asDocument(new File(WebXmlUtilsTest.class.getResource("/org/jivesoftware/util/test-web.xml").toURI()));
        final String servletName = "This does not exist";
        // Execute system under test.
        final String result = WebXmlUtils.getServletClassName(webXml, servletName);
        // Verify result.
        Assert.assertNull(result);
    }

    @Test
    public void testGetFilterClassName() throws Exception {
        // Setup fixture.
        final Document webXml = WebXmlUtils.asDocument(new File(WebXmlUtilsTest.class.getResource("/org/jivesoftware/util/test-web.xml").toURI()));
        final String filterName = "Set Character Encoding";
        // Execute system under test.
        final String result = WebXmlUtils.getFilterClassName(webXml, filterName);
        // Verify result.
        Assert.assertEquals("org.jivesoftware.util.SetCharacterEncodingFilter", result);
    }

    @Test
    public void testGetFilterClassNameForNonExistingFilter() throws Exception {
        // Setup fixture.
        final Document webXml = WebXmlUtils.asDocument(new File(WebXmlUtilsTest.class.getResource("/org/jivesoftware/util/test-web.xml").toURI()));
        final String filterName = "This does not exist";
        // Execute system under test.
        final String result = WebXmlUtils.getFilterClassName(webXml, filterName);
        // Verify result.
        Assert.assertNull(result);
    }

    @Test
    public void testGetServletInitParams() throws Exception {
        // Setup fixture.
        final Document webXml = WebXmlUtils.asDocument(new File(WebXmlUtilsTest.class.getResource("/org/jivesoftware/util/test-web.xml").toURI()));
        final String servletName = "FaviconServlet";
        // Execute system under test.
        final Map<String, String> result = WebXmlUtils.getServletInitParams(webXml, servletName);
        // Verify result.
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("42", result.get("answer"));
        Assert.assertEquals("fishes", result.get("thanks"));
    }

    @Test
    public void testGetServletInitParamsForServletWithoutParams() throws Exception {
        // Setup fixture.
        final Document webXml = WebXmlUtils.asDocument(new File(WebXmlUtilsTest.class.getResource("/org/jivesoftware/util/test-web.xml").toURI()));
        final String servletName = "PluginServlet";
        // Execute system under test.
        final Map<String, String> result = WebXmlUtils.getServletInitParams(webXml, servletName);
        // Verify result.
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testGetServletInitParamsForNonExistingServlet() throws Exception {
        // Setup fixture.
        final Document webXml = WebXmlUtils.asDocument(new File(WebXmlUtilsTest.class.getResource("/org/jivesoftware/util/test-web.xml").toURI()));
        final String servletName = "This does not exist";
        // Execute system under test.
        final Map<String, String> result = WebXmlUtils.getServletInitParams(webXml, servletName);
        // Verify result.
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testGetFilterInitParams() throws Exception {
        // Setup fixture.
        final Document webXml = WebXmlUtils.asDocument(new File(WebXmlUtilsTest.class.getResource("/org/jivesoftware/util/test-web.xml").toURI()));
        final String filterName = "AuthCheck";
        // Execute system under test.
        final Map<String, String> result = WebXmlUtils.getFilterInitParams(webXml, filterName);
        // Verify result.
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("login.jsp,index.jsp?logout=true,setup/index.jsp,setup/setup-*,.gif,.png,error-serverdown.jsp,loginToken.jsp", result.get("excludes"));
    }

    @Test
    public void testGetFilterInitParamsForFilterWithoutParams() throws Exception {
        // Setup fixture.
        final Document webXml = WebXmlUtils.asDocument(new File(WebXmlUtilsTest.class.getResource("/org/jivesoftware/util/test-web.xml").toURI()));
        final String filterName = "PluginFilter";
        // Execute system under test.
        final Map<String, String> result = WebXmlUtils.getFilterInitParams(webXml, filterName);
        // Verify result.
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testGetFilterInitParamsForNonExistingFilter() throws Exception {
        // Setup fixture.
        final Document webXml = WebXmlUtils.asDocument(new File(WebXmlUtilsTest.class.getResource("/org/jivesoftware/util/test-web.xml").toURI()));
        final String filterName = "This does not exist";
        // Execute system under test.
        final Map<String, String> result = WebXmlUtils.getFilterInitParams(webXml, filterName);
        // Verify result.
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testGetServletUrlPatterns() throws Exception {
        // Setup fixture.
        final Document webXml = WebXmlUtils.asDocument(new File(WebXmlUtilsTest.class.getResource("/org/jivesoftware/util/test-web.xml").toURI()));
        final String servletName = "dwr-invoker";
        // Execute system under test.
        final Set<String> results = WebXmlUtils.getServletUrlPatterns(webXml, servletName);
        // Verify result.
        Assert.assertNotNull(results);
        Assert.assertEquals(2, results.size());
        Assert.assertTrue(results.contains("/dwr/*"));
        Assert.assertTrue(results.contains("/more-dwr/*"));
    }

    @Test
    public void testGetServletUrlPatternsForNonExistingServlet() throws Exception {
        // Setup fixture.
        final Document webXml = WebXmlUtils.asDocument(new File(WebXmlUtilsTest.class.getResource("/org/jivesoftware/util/test-web.xml").toURI()));
        final String servletName = "This does not exist";
        // Execute system under test.
        final Set<String> results = WebXmlUtils.getServletUrlPatterns(webXml, servletName);
        // Verify result.
        Assert.assertNotNull(results);
        Assert.assertEquals(0, results.size());
    }

    @Test
    public void testGetFilterUrlPatterns() throws Exception {
        // Setup fixture.
        final Document webXml = WebXmlUtils.asDocument(new File(WebXmlUtilsTest.class.getResource("/org/jivesoftware/util/test-web.xml").toURI()));
        final String filterName = "LocaleFilter";
        // Execute system under test.
        final Set<String> results = WebXmlUtils.getFilterUrlPatterns(webXml, filterName);
        // Verify result.
        Assert.assertNotNull(results);
        Assert.assertEquals(2, results.size());
        Assert.assertTrue(results.contains("*.jsp"));
        Assert.assertTrue(results.contains("foo.bar"));
    }

    @Test
    public void testGetFilterUrlPatternsForNonExistingFilter() throws Exception {
        // Setup fixture.
        final Document webXml = WebXmlUtils.asDocument(new File(WebXmlUtilsTest.class.getResource("/org/jivesoftware/util/test-web.xml").toURI()));
        final String filterName = "This does not exist";
        // Execute system under test.
        final Set<String> results = WebXmlUtils.getFilterUrlPatterns(webXml, filterName);
        // Verify result.
        Assert.assertNotNull(results);
        Assert.assertEquals(0, results.size());
    }

    @Test
    public void testGetFilterUrlPatternsForFilterThatUsesServletMapping() throws Exception {
        // Setup fixture.
        final Document webXml = WebXmlUtils.asDocument(new File(WebXmlUtilsTest.class.getResource("/org/jivesoftware/util/test-web.xml").toURI()));
        final String filterName = "AuthCheck";
        // Execute system under test.
        final Set<String> results = WebXmlUtils.getFilterUrlPatterns(webXml, filterName);
        // Verify result.
        Assert.assertNotNull(results);
        Assert.assertEquals(3, results.size());
        Assert.assertTrue(results.contains("test/*.jsp"));// from url pattern

        Assert.assertTrue(results.contains("/dwr/*"));// from servlet-mapping

        Assert.assertTrue(results.contains("/more-dwr/*"));// from servlet-mapping

    }
}

