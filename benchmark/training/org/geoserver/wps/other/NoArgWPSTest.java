/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.other;


import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.geoserver.wps.WPSTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class NoArgWPSTest extends WPSTestSupport {
    /**
     * This test runs a no-argument WPS Process and checks the result.
     */
    @Test
    public void NoArgumentProcessTest() throws Exception {
        String xml = "<wps:Execute service='WPS' version='1.0.0' xmlns:wps='http://www.opengis.net/wps/1.0.0' " + ((((((((("xmlns:ows='http://www.opengis.net/ows/1.1'>" + "<ows:Identifier>gs:NoArgWPS</ows:Identifier>") + "<wps:DataInputs>") + "</wps:DataInputs>") + "<wps:ResponseForm>") + "<wps:RawDataOutput mimeType=\"text/xml\">") + "<ows:Identifier>result</ows:Identifier>") + "</wps:RawDataOutput>") + "</wps:ResponseForm>") + "</wps:Execute>");
        // Document d = postAsDOM(root(), xml); // allows you to debug exception
        InputStream is = post(root(), xml);
        String s = IOUtils.toString(is);
        Assert.assertEquals(s, "Completed!");
    }
}

