/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.password;


import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import org.apache.commons.io.IOUtils;
import org.geoserver.security.GeoServerSecurityTestSupport;
import org.geoserver.test.SystemTest;
import org.geotools.util.URLs;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(SystemTest.class)
public class URLMasterPasswordProviderTest extends GeoServerSecurityTestSupport {
    @Test
    public void testEncryption() throws Exception {
        File tmp = File.createTempFile("passwd", "tmp", new File("target"));
        tmp = tmp.getCanonicalFile();
        URLMasterPasswordProviderConfig config = new URLMasterPasswordProviderConfig();
        config.setName("test");
        config.setReadOnly(false);
        config.setLoginEnabled(true);
        config.setClassName(URLMasterPasswordProvider.class.getCanonicalName());
        config.setURL(URLs.fileToUrl(tmp));
        config.setEncrypting(true);
        URLMasterPasswordProvider mpp = new URLMasterPasswordProvider();
        mpp.setSecurityManager(getSecurityManager());
        mpp.initializeFromConfig(config);
        mpp.setName(config.getName());
        mpp.doSetMasterPassword("geoserver".toCharArray());
        String encoded = IOUtils.toString(new FileInputStream(tmp));
        Assert.assertFalse("geoserver".equals(encoded));
        char[] passwd = mpp.doGetMasterPassword();
        Assert.assertTrue(Arrays.equals("geoserver".toCharArray(), passwd));
    }
}

