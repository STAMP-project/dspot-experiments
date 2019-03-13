/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.util;


import DefaultCacheProvider.BEAN_NAME_PROPERTY;
import java.util.logging.Level;
import org.easymock.classextension.EasyMock;
import org.geoserver.platform.GeoServerExtensionsHelper;
import org.geotools.util.logging.Logging;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class DefaultCacheProviderTest {
    @Rule
    public GeoServerExtensionsHelper.ExtensionsHelperRule extensions = new GeoServerExtensionsHelper.ExtensionsHelperRule();

    @Rule
    public LoggerRule logging = new LoggerRule(Logging.getLogger(DefaultCacheProvider.class), Level.WARNING);

    @Test
    public void testDefault() {
        CacheProvider provider = DefaultCacheProvider.findProvider();
        Assert.assertThat(provider, Matchers.instanceOf(DefaultCacheProvider.class));
    }

    @Test
    public void testFindInContext() {
        CacheProvider testCacheProvider1 = addMockProvider("testCacheProvider1");
        EasyMock.replay(testCacheProvider1);
        CacheProvider provider = DefaultCacheProvider.findProvider();
        Assert.assertThat(provider, Matchers.sameInstance(testCacheProvider1));
        EasyMock.verify(testCacheProvider1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFindTwoInContext() {
        CacheProvider testCacheProvider1 = addMockProvider("testCacheProvider1");
        CacheProvider testCacheProvider2 = addMockProvider("testCacheProvider2");
        EasyMock.replay(testCacheProvider1, testCacheProvider2);
        CacheProvider provider = DefaultCacheProvider.findProvider();
        Assert.assertThat(provider, Matchers.anyOf(Matchers.sameInstance(testCacheProvider1), Matchers.sameInstance(testCacheProvider2)));
        String providerName = "testCacheProvider2";
        if (provider == testCacheProvider1) {
            providerName = "testCacheProvider1";
        }
        logging.assertLogged(Matchers.allOf(Matchers.hasProperty("level", Matchers.is(Level.WARNING)), Matchers.hasProperty("parameters", // Name of the provider being used
        // Available providers
        // The system property to override
        Matchers.arrayContainingInAnyOrder(Matchers.equalTo(providerName), Matchers.anyOf(Matchers.equalTo("testCacheProvider1, testCacheProvider2"), Matchers.equalTo("testCacheProvider2, testCacheProvider1")), Matchers.equalTo("GEOSERVER_DEFAULT_CACHE_PROVIDER")))));
        EasyMock.verify(testCacheProvider1, testCacheProvider2);
    }

    @Test
    public void testResolveWithProperty() {
        CacheProvider testCacheProvider1 = addMockProvider("testCacheProvider1");
        CacheProvider testCacheProvider2 = addMockProvider("testCacheProvider2");
        // Test that the bean specified in the property is used
        extensions.property(BEAN_NAME_PROPERTY, "testCacheProvider1");
        EasyMock.replay(testCacheProvider1, testCacheProvider2);
        CacheProvider provider = DefaultCacheProvider.findProvider();
        Assert.assertThat(provider, Matchers.sameInstance(testCacheProvider1));
        EasyMock.verify(testCacheProvider1, testCacheProvider2);
        // Retry with the property changed to ensure we weren't jsut lucky before
        EasyMock.reset(testCacheProvider1, testCacheProvider2);
        extensions.property(BEAN_NAME_PROPERTY, "testCacheProvider2");
        EasyMock.replay(testCacheProvider1, testCacheProvider2);
        provider = DefaultCacheProvider.findProvider();
        Assert.assertThat(provider, Matchers.sameInstance(testCacheProvider2));
        EasyMock.verify(testCacheProvider1, testCacheProvider2);
    }

    @Test
    public void testPropertyPriority() {
        CacheProvider testCacheProvider3 = addMockProvider("testCacheProvider3");
        CacheProvider testCacheProvider2 = addMockProvider("testCacheProvider2");
        // Test that the bean specified in the property is used
        extensions.property(BEAN_NAME_PROPERTY, "testCacheProvider1,testCacheProvider2,testCacheProvider3");
        EasyMock.replay(testCacheProvider3, testCacheProvider2);
        CacheProvider provider = DefaultCacheProvider.findProvider();
        Assert.assertThat(provider, Matchers.sameInstance(testCacheProvider2));
        EasyMock.verify(testCacheProvider3, testCacheProvider2);
    }
}

