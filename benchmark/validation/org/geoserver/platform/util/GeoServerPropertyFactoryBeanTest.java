/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.platform.util;


import org.geoserver.util.PropertyRule;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class GeoServerPropertyFactoryBeanTest {
    public static final String PROPERTY_NAME = "FOO";

    @Rule
    public PropertyRule foo = PropertyRule.system(GeoServerPropertyFactoryBeanTest.PROPERTY_NAME);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    GeoServerPropertyFactoryBean<String> factory;

    @Test
    public void testGetBean() throws Exception {
        factory.setDefaultValue("Default");
        foo.setValue("testValue1");
        Assert.assertThat(factory.createInstance(), Matchers.equalTo("Bean: testValue1"));
    }

    @Test
    public void testGetDefault() throws Exception {
        factory.setDefaultValue("Default");
        Assert.assertThat(factory.createInstance(), Matchers.equalTo("Bean: Default"));
    }

    @Test
    public void testGetUnsetDefault() throws Exception {
        exception.expect(IllegalStateException.class);
        factory.createInstance();
    }

    @Test
    public void testGetBadDefault() throws Exception {
        exception.expect(IllegalStateException.class);
        factory.setDefaultValue("UNKNOWN");
        factory.createInstance();
    }

    @Test
    public void testFallBackToDefault() throws Exception {
        factory.setDefaultValue("Default");
        foo.setValue("UNKNOWN");
        Assert.assertThat(factory.createInstance(), Matchers.equalTo("Bean: Default"));
    }
}

