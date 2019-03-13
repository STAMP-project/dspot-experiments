/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.wicket;


import java.io.File;
import org.geoserver.web.StringValidatable;
import org.junit.Assert;
import org.junit.Test;


public class FileExistsValidatorTest {
    private static File root;

    private static FileExistsValidator validator;

    @Test
    public void testAbsoluteRaw() throws Exception {
        File tazbm = new File(FileExistsValidatorTest.root, "wcs/BlueMarble.tiff");
        StringValidatable validatable = new StringValidatable(tazbm.getAbsolutePath());
        FileExistsValidatorTest.validator.validate(validatable);
        Assert.assertTrue(validatable.isValid());
    }

    @Test
    public void testAbsoluteURI() throws Exception {
        File tazbm = new File(FileExistsValidatorTest.root, "wcs/BlueMarble.tiff");
        StringValidatable validatable = new StringValidatable(tazbm.toURI().toString());
        FileExistsValidatorTest.validator.validate(validatable);
        Assert.assertTrue(validatable.isValid());
    }

    @Test
    public void testRelative() throws Exception {
        StringValidatable validatable = new StringValidatable("file:wcs/BlueMarble.tiff");
        FileExistsValidatorTest.validator.validate(validatable);
        Assert.assertTrue(validatable.isValid());
    }
}

