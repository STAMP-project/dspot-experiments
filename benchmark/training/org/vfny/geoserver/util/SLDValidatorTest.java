/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.vfny.geoserver.util;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class SLDValidatorTest {
    @Test
    public void testValid() throws Exception {
        SLDValidator validator = new SLDValidator();
        List errors = validator.validateSLD(getClass().getResourceAsStream("valid.sld"));
        // showErrors(errors);
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void testInvalid() throws Exception {
        SLDValidator validator = new SLDValidator();
        List errors = validator.validateSLD(getClass().getResourceAsStream("invalid.sld"));
        // showErrors(errors);
        Assert.assertFalse(errors.isEmpty());
    }
}

