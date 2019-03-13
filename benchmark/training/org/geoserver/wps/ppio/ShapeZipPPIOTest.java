/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.ppio;


import java.io.IOException;
import java.io.InputStream;
import org.geoserver.util.ZipTestUtil;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ShapeZipPPIOTest {
    @Test
    public void testDecodeBadEntryName() throws Exception {
        try (InputStream input = ZipTestUtil.getZipSlipInput()) {
            new ShapeZipPPIO(null).decode(input);
            Assert.fail("Expected decompression to fail");
        } catch (IOException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.startsWith("Entry is outside of the target directory"));
        }
    }
}

