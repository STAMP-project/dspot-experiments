/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.util;


import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.platform.resource.Resource;
import org.geoserver.util.ZipTestUtil;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class IOUtilsTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder(new File("target"));

    @Test
    public void testInflateBadEntryName() throws IOException {
        File destDir = temp.newFolder("d1").toPath().toFile();
        destDir.mkdirs();
        Resource directory = new GeoServerResourceLoader(destDir).get("");
        File file = ZipTestUtil.initZipSlipFile(temp.newFile("d1.zip"));
        try {
            IOUtils.inflate(new ZipFile(file), directory, null, null, null, null, false, false);
            Assert.fail("Expected decompression to fail");
        } catch (IOException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.startsWith("Entry is outside of the target directory"));
        }
    }
}

