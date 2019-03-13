/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.vfny.geoserver.global.xml;


import java.io.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Test;


public class WriterHelperTest {
    private ByteArrayOutputStream bos;

    private WriterHelper helper;

    @Test
    public void testNoEscape() throws Exception {
        helper.textTag("title", "$%()");
        String result = bos.toString();
        Assert.assertEquals("<title>$%()</title>\n", result);
    }

    @Test
    public void testEscapePlain() throws Exception {
        helper.textTag("title", "Test < > & \' \"");
        String result = bos.toString();
        Assert.assertEquals("<title>Test &lt; &gt; &amp; &apos; &quot;</title>\n", result);
    }

    @Test
    public void testEscapeNewlines() throws Exception {
        helper.textTag("title", "<\n>\n");
        String result = bos.toString();
        Assert.assertEquals("<title>&lt;\n&gt;\n</title>\n", result);
    }
}

