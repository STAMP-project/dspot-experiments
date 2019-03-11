/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.config.util;


import org.easymock.Capture;
import org.geoserver.config.util.SecureXStream.ForbiddenClassExceptionEx;
import org.geoserver.util.PropertyRule;
import org.hamcrest.Matchers;
import org.hamcrest.core.AllOf;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;


public class SecureXStreamTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public PropertyRule whitelistProperty = PropertyRule.system("GEOSERVER_XSTREAM_WHITELIST");

    @Test
    public void testPropertyCanAllow() throws Exception {
        // Check that additional whitelist entries can be added via a system property.
        whitelistProperty.setValue("org.easymock.**");
        SecureXStream xs = new SecureXStream();
        // Check that a class in the package deserializes
        Object o = xs.fromXML((("<" + (Capture.class.getCanonicalName())) + " />"));
        Assert.assertThat(o, Matchers.instanceOf(Capture.class));
        // Check that a class from elsewhere still causes an exception
        exception.expect(ForbiddenClassExceptionEx.class);
        xs.fromXML((("<" + (AllOf.class.getCanonicalName())) + " />"));
    }

    @Test
    public void testPropertyCanAllowMultiple() throws Exception {
        // Check that additional whitelist entries can be added via a system property.
        whitelistProperty.setValue("org.easymock.**; org.junit.**");
        SecureXStream xs = new SecureXStream();
        // Check that a class in the first package deserializes
        Object o1 = xs.fromXML((("<" + (Capture.class.getCanonicalName())) + " />"));
        Assert.assertThat(o1, Matchers.instanceOf(Capture.class));
        // Check that a class in the second package deserializes
        Object o2 = xs.fromXML((("<" + (TestName.class.getCanonicalName())) + " />"));
        Assert.assertThat(o2, Matchers.instanceOf(TestName.class));
        // Check that a class from elsewhere still causes an exception
        exception.expect(ForbiddenClassExceptionEx.class);
        xs.fromXML((("<" + (AllOf.class.getCanonicalName())) + " />"));
    }

    @Test
    public void testErrorMessage() throws Exception {
        SecureXStream xs = new SecureXStream();
        try {
            xs.fromXML((("<" + (Capture.class.getCanonicalName())) + " />"));
        } catch (ForbiddenClassExceptionEx e) {
            Assert.assertEquals(("Unauthorized class found, see logs for more details on how to handle it: " + "org.easymock.Capture"), e.getMessage());
        }
    }
}

