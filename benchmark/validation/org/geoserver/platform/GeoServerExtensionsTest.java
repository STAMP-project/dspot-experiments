/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.platform;


import GeoServerExtensions.context;
import GeoServerExtensions.extensionsCache;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.geotools.util.logging.Logging;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;


/**
 * Unit test suite for {@link GeoServerExtensions}
 *
 * @author Gabriel Roldan (TOPP)
 * @version $Id$
 */
public class GeoServerExtensionsTest {
    @Test
    public void testSetApplicationContext() {
        ApplicationContext appContext1 = createMock(ApplicationContext.class);
        ApplicationContext appContext2 = createMock(ApplicationContext.class);
        GeoServerExtensions gse = new GeoServerExtensions();
        gse.setApplicationContext(appContext1);
        extensionsCache.put(GeoServerExtensionsTest.class, new String[]{ "fake" });
        Assert.assertSame(appContext1, context);
        gse.setApplicationContext(appContext2);
        Assert.assertSame(appContext2, context);
        Assert.assertEquals(0, extensionsCache.size());
    }

    @Test
    public void testExtensions() {
        ApplicationContext appContext = createMock(ApplicationContext.class);
        GeoServerExtensions gse = new GeoServerExtensions();
        gse.setApplicationContext(appContext);
        Assert.assertEquals(0, extensionsCache.size());
        expect(appContext.getBeanNamesForType(ExtensionFilter.class)).andReturn(new String[0]);
        expect(appContext.getBeanNamesForType(GeoServerExtensionsTest.class)).andReturn(new String[]{ "testKey", "fakeKey" });
        expect(appContext.getBeanNamesForType(ExtensionProvider.class)).andReturn(new String[0]);
        expect(appContext.getBean("testKey")).andReturn(this);
        // note I'm testing null is a valid value. If that's not the case, it
        // should be reflected in the code, but I'm writing the test after the
        // code so that's what it does
        expect(appContext.isSingleton(((String) (anyObject())))).andReturn(true).anyTimes();
        expect(appContext.getBean("fakeKey")).andReturn(null);
        replay(appContext);
        List<GeoServerExtensionsTest> extensions = GeoServerExtensions.extensions(GeoServerExtensionsTest.class);
        Assert.assertNotNull(extensions);
        Assert.assertEquals(2, extensions.size());
        Assert.assertTrue(extensions.contains(this));
        Assert.assertTrue(extensions.contains(null));
        Assert.assertEquals(3, extensionsCache.size());
        Assert.assertTrue(extensionsCache.containsKey(GeoServerExtensionsTest.class));
        Assert.assertNotNull(extensionsCache.get(GeoServerExtensionsTest.class));
        Assert.assertEquals(2, extensionsCache.get(GeoServerExtensionsTest.class).length);
        verify(appContext);
    }

    /**
     * If a context is explicitly provided that is not the one set through setApplicationContext(),
     * the extensions() method shall look into it and bypass the cache
     */
    @Test
    public void testExtensionsApplicationContext() {
        ApplicationContext appContext = createMock(ApplicationContext.class);
        ApplicationContext customAppContext = createMock(ApplicationContext.class);
        GeoServerExtensions gse = new GeoServerExtensions();
        gse.setApplicationContext(appContext);
        // setApplicationContext cleared the static cache
        Assert.assertEquals(0, extensionsCache.size());
        // set the expectation over the app context used as argument
        expect(customAppContext.getBeanNamesForType(ExtensionFilter.class)).andReturn(new String[0]);
        expect(customAppContext.getBeanNamesForType(GeoServerExtensionsTest.class)).andReturn(new String[]{ "itDoesntMatterForThePurpose" });
        expect(customAppContext.getBeanNamesForType(ExtensionProvider.class)).andReturn(new String[0]);
        expect(customAppContext.getBeanNamesForType(ExtensionFilter.class)).andReturn(new String[0]);
        expect(customAppContext.getBean("itDoesntMatterForThePurpose")).andReturn(this);
        expect(appContext.isSingleton(((String) (anyObject())))).andReturn(true).anyTimes();
        expect(customAppContext.isSingleton(((String) (anyObject())))).andReturn(true).anyTimes();
        replay(customAppContext);
        replay(appContext);
        List<GeoServerExtensionsTest> extensions = GeoServerExtensions.extensions(GeoServerExtensionsTest.class, customAppContext);
        Assert.assertNotNull(extensions);
        Assert.assertEquals(1, extensions.size());
        Assert.assertSame(this, extensions.get(0));
        // cache should be untouched after this since our own context were used
        Assert.assertEquals(0, extensionsCache.size());
        verify(appContext);
        verify(customAppContext);
    }

    @Test
    public void testExtensionFilterByName() {
        ApplicationContext appContext = createNiceMock(ApplicationContext.class);
        // setApplicationContext cleared the static cache
        // set the expectation over the app context used as argument
        NameExclusionFilter filter = new NameExclusionFilter();
        filter.setBeanId("testId");
        expect(appContext.getBeanNamesForType(ExtensionFilter.class)).andReturn(new String[]{ "filter" }).anyTimes();
        expect(appContext.getBean("filter")).andReturn(filter).anyTimes();
        expect(appContext.getBeanNamesForType(GeoServerExtensionsTest.class)).andReturn(new String[]{ "testId" }).anyTimes();
        expect(appContext.getBean("testId")).andReturn(this).anyTimes();
        replay(appContext);
        // build extensions
        GeoServerExtensions gse = new GeoServerExtensions();
        gse.setApplicationContext(appContext);
        // check we get nothing
        List<GeoServerExtensionsTest> extensions = GeoServerExtensions.extensions(GeoServerExtensionsTest.class);
        Assert.assertEquals(0, extensions.size());
        // change the bean id and we should get one result instead
        filter.setBeanId("holabaloo");
        extensions = GeoServerExtensions.extensions(GeoServerExtensionsTest.class);
        Assert.assertEquals(1, extensions.size());
        Assert.assertSame(this, extensions.get(0));
    }

    @Test
    public void testExtensionFilterByClass() {
        ApplicationContext appContext = createNiceMock(ApplicationContext.class);
        // setApplicationContext cleared the static cache
        // set the expectation over the app context used as argument
        ClassExclusionFilter filter = new ClassExclusionFilter();
        filter.setBeanClass(GeoServerExtensionsTest.class);
        expect(appContext.getBeanNamesForType(ExtensionFilter.class)).andReturn(new String[]{ "filter" }).anyTimes();
        expect(appContext.getBean("filter")).andReturn(filter).anyTimes();
        expect(appContext.getBeanNamesForType(GeoServerExtensionsTest.class)).andReturn(new String[]{ "testId" }).anyTimes();
        expect(appContext.getBean("testId")).andReturn(this).anyTimes();
        replay(appContext);
        // build extensions
        GeoServerExtensions gse = new GeoServerExtensions();
        gse.setApplicationContext(appContext);
        // check we get nothing
        List<GeoServerExtensionsTest> extensions = GeoServerExtensions.extensions(GeoServerExtensionsTest.class);
        Assert.assertEquals(0, extensions.size());
        // change the bean id and we should get one result instead
        filter.setBeanClass(Integer.class);
        extensions = GeoServerExtensions.extensions(GeoServerExtensionsTest.class);
        Assert.assertEquals(1, extensions.size());
        Assert.assertSame(this, extensions.get(0));
    }

    @Test
    public void testBeanString() {
        ApplicationContext appContext = createMock(ApplicationContext.class);
        GeoServerExtensions gse = new GeoServerExtensions();
        gse.setApplicationContext(null);
        Logger LOGGER = Logging.getLogger("org.geoserver.platform");
        Level level = LOGGER.getLevel();
        try {
            LOGGER.setLevel(Level.SEVERE);
            Assert.assertNull(GeoServerExtensions.bean("beanName"));
        } finally {
            LOGGER.setLevel(level);
        }
        gse.setApplicationContext(appContext);
        expect(appContext.isSingleton(((String) (anyObject())))).andReturn(true).anyTimes();
        expect(appContext.getBean("beanName")).andReturn(null);// call #1

        expect(appContext.getBean("beanName")).andReturn(this);// call #2

        replay(appContext);
        Assert.assertNull(GeoServerExtensions.bean("beanName"));// call #1

        Assert.assertSame(this, GeoServerExtensions.bean("beanName"));// call #2

        verify(appContext);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testExtensionProvider() {
        ApplicationContext appContext = createMock(ApplicationContext.class);
        GeoServerExtensions gse = new GeoServerExtensions();
        gse.setApplicationContext(appContext);
        expect(appContext.getBeanNamesForType(ExtensionFilter.class)).andReturn(new String[0]);
        expect(appContext.getBeanNamesForType(GeoServerExtensionsTest.class)).andReturn(new String[0]);
        expect(appContext.getBeanNamesForType(ExtensionProvider.class)).andReturn(new String[]{ "testKey2" });
        ExtensionProvider xp = createMock(ExtensionProvider.class);
        expect(xp.getExtensionPoint()).andReturn(GeoServerExtensionsTest.class);
        expect(xp.getExtensions(GeoServerExtensionsTest.class)).andReturn(Arrays.asList(this));
        expect(appContext.getBean("testKey2")).andReturn(xp);
        expect(appContext.isSingleton(((String) (anyObject())))).andReturn(true).anyTimes();
        replay(xp);
        replay(appContext);
        Assert.assertEquals(1, GeoServerExtensions.extensions(GeoServerExtensionsTest.class).size());
        verify(xp);
        verify(appContext);
    }

    @Test
    public void testSystemProperty() {
        // check for a property we did set up in the setUp
        Assert.assertEquals("ABC", GeoServerExtensions.getProperty("TEST_PROPERTY", ((ApplicationContext) (null))));
        Assert.assertEquals("ABC", GeoServerExtensions.getProperty("TEST_PROPERTY", ((ServletContext) (null))));
    }

    @Test
    public void testWebProperty() {
        ServletContext servletContext = createMock(ServletContext.class);
        expect(servletContext.getInitParameter("TEST_PROPERTY")).andReturn("DEF").anyTimes();
        expect(servletContext.getInitParameter("WEB_PROPERTY")).andReturn("WWW").anyTimes();
        replay(servletContext);
        Assert.assertEquals("ABC", GeoServerExtensions.getProperty("TEST_PROPERTY", servletContext));
        Assert.assertEquals("WWW", GeoServerExtensions.getProperty("WEB_PROPERTY", servletContext));
    }
}

