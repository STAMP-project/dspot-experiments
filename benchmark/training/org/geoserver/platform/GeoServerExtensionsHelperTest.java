/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2014 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.platform;


import java.io.File;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class GeoServerExtensionsHelperTest {
    @Test
    public void helperProperty() {
        Assert.assertEquals("ABC", GeoServerExtensions.getProperty("TEST_PROPERTY"));
        GeoServerExtensionsHelper.property("TEST_PROPERTY", "abc");
        Assert.assertEquals("abc", GeoServerExtensions.getProperty("TEST_PROPERTY"));
        GeoServerExtensionsHelper.clear();
        Assert.assertEquals("ABC", GeoServerExtensions.getProperty("TEST_PROPERTY"));
    }

    @Test
    public void helperSingleton() {
        GeoServerExtensionsHelper.singleton("bean", this);
        Assert.assertSame(this, GeoServerExtensions.bean("bean"));
        Assert.assertSame(this, GeoServerExtensions.bean(GeoServerExtensionsHelperTest.class));
        GeoServerExtensionsHelper.clear();
        Assert.assertNull(GeoServerExtensions.bean("bean"));
        Assert.assertNull(GeoServerExtensions.bean(GeoServerExtensionsHelperTest.class));
    }

    class TestClass {}

    @SuppressWarnings("unchecked")
    @Test
    public void helperMultipleSingleton() {
        GeoServerExtensionsHelperTest.TestClass o1 = new GeoServerExtensionsHelperTest.TestClass();
        GeoServerExtensionsHelperTest.TestClass o2 = new GeoServerExtensionsHelperTest.TestClass();
        GeoServerExtensionsHelper.singleton("o1", o1, GeoServerExtensionsHelperTest.TestClass.class);
        GeoServerExtensionsHelper.singleton("o2", o2, GeoServerExtensionsHelperTest.TestClass.class);
        Assert.assertThat(GeoServerExtensions.extensions(GeoServerExtensionsHelperTest.TestClass.class), Matchers.containsInAnyOrder(Matchers.sameInstance(o1), Matchers.sameInstance(o2)));
    }

    @Test
    public void helperFile() {
        File webxml = new File("web.xml");// we are not touching the file so anywhere is fine

        GeoServerExtensionsHelper.file("WEB-INF/web.xml", webxml);
        Assert.assertSame(webxml, GeoServerExtensions.file("WEB-INF/web.xml"));
    }
}

