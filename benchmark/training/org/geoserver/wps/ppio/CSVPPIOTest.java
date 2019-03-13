/**
 * GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2017, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geoserver.wps.ppio;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import org.geoserver.wps.WPSTestSupport;
import org.geoserver.wps.resource.WPSResourceManager;
import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.filter.text.cql2.CQL;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.filter.Filter;


/**
 *
 *
 * @author ian
 */
public class CSVPPIOTest extends WPSTestSupport {
    private InputStream is;

    private WPSResourceManager resourceManager;

    /**
     * Test method for {@link org.geoserver.wps.ppio.WFSPPIO#decode(java.io.InputStream)}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDecodeInputStream() throws Exception {
        SimpleFeatureCollection states = ((SimpleFeatureCollection) (decode(is)));
        Assert.assertEquals("Wrong number of states", 51, states.size());
        Assert.assertEquals("Wrong number of columns", 9, states.getSchema().getAttributeCount());
        Filter filter = CQL.toFilter("State = 'Alabama'");
        SimpleFeatureCollection alabama = states.subCollection(filter);
        Assert.assertEquals("inc1995 wrong", 19683, DataUtilities.first(alabama).getAttribute("inc1995"));
    }

    @Test
    public void testEncodeOutputStream() throws Exception {
        SimpleFeatureCollection states = ((SimpleFeatureCollection) (decode(is)));
        Assert.assertEquals("Wrong number of states", 51, states.size());
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
        new CSVPPIO(resourceManager).encode(states, os);
        String csv = os.toString();
        BufferedReader r = new BufferedReader(new StringReader(csv));
        String line;
        int lines = 0;
        while ((line = r.readLine()) != null) {
            String[] attribs = line.split(",");
            if (lines == 0) {
                Assert.assertEquals(attribs[0], "State");
                Assert.assertEquals(attribs[1], "inc1980");
                Assert.assertEquals(attribs[4], "inc2000");
                Assert.assertEquals(attribs[8], "inc2012");
            }
            if (attribs[0].equalsIgnoreCase("Tennessee")) {
                // Tennessee,7711,15903,21800,25946,28455,32172,34089,37678
                Assert.assertEquals("7711", attribs[1]);
                Assert.assertEquals("37678", attribs[8]);
            }
            lines++;
        } 
        Assert.assertEquals("Wrong number of lines", 52, lines);
    }
}

