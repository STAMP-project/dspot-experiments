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


import java.io.InputStream;
import org.geoserver.wps.WPSTestSupport;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.type.AttributeDescriptor;


/**
 *
 *
 * @author ian
 */
public class WFSPPIOTest extends WPSTestSupport {
    private InputStream is;

    /**
     * Test method for {@link org.geoserver.wps.ppio.WFSPPIO#decode(java.io.InputStream)}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDecodeInputStream() throws Exception {
        SimpleFeatureCollection rawTarget = ((SimpleFeatureCollection) (new WFSPPIO.WFS11().decode(is)));
        for (AttributeDescriptor ad : rawTarget.getSchema().getAttributeDescriptors()) {
            final String name = ad.getLocalName();
            if ("metaDataProperty".equalsIgnoreCase(name)) {
                Assert.fail("this should be deleted");
            }
        }
    }
}

