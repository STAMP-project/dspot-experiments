/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.filters;


import org.junit.Assert;
import org.junit.Test;


/**
 * Wrap a String up as a ServletInputStream so we can read it multiple times.
 *
 * @author David Winslow <dwinslow@openplans.org>
 */
public class BufferedRequestStreamTest {
    BufferedRequestStream myBRS;

    String myTestString;

    @Test
    public void testReadLine() throws Exception {
        byte[] b = new byte[1024];
        int off = 0;
        int len = 1024;
        int amountRead = myBRS.readLine(b, off, len);
        String s = new String(b, 0, amountRead);
        Assert.assertEquals(s, myTestString);
    }
}

