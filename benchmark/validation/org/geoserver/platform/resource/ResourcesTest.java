/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.platform.resource;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class ResourcesTest extends FileWrapperResourceTheoryTest {
    @Test
    public void resourcesTest() throws IOException {
        Resource source = getResource();
        Resource directory = getDirectory();
        Resources.copy(source.file(), directory);
        Resource target = directory.get(source.name());
        Assert.assertTrue(Resources.exists(target));
        Assert.assertEquals(target.name(), source.name());
    }
}

