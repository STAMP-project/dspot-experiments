/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.decorators;


import junit.framework.TestCase;
import org.geoserver.catalog.WMSLayerInfo;


public class DecoratingWMSLayerInfoTest extends TestCase {
    public void testPrefixName() {
        // build up the mock
        WMSLayerInfo li = createNiceMock(WMSLayerInfo.class);
        expect(li.prefixedName()).andReturn("PREFIX");
        replay(li);
        DecoratingWMSLayerInfo ro = new DecoratingWMSLayerInfo(li);
        TestCase.assertEquals("PREFIX", ro.prefixedName());
    }
}

