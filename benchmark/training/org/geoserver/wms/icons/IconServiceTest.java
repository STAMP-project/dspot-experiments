/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.icons;


import Filter.INCLUDE;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.StyleInfo;
import org.geotools.styling.Style;
import org.junit.Test;


public class IconServiceTest extends IconTestSupport {
    @Test
    public void testHandle() throws Exception {
        Style style = style(featureTypeStyle(rule(INCLUDE, grayCircle())));
        StyleInfo s = createNiceMock(StyleInfo.class);
        expect(s.getStyle()).andReturn(style);
        Catalog cat = createNiceMock(Catalog.class);
        expect(cat.getStyleByName("foo")).andReturn(s);
        replay(s, cat);
        dispatch("/icon/foo", "0.0.0=", cat);
    }

    @Test
    public void testAcceptedNames() throws Exception {
        // test a name with non word characters
        Style style = style(featureTypeStyle(rule(INCLUDE, grayCircle())));
        StyleInfo s = createNiceMock(StyleInfo.class);
        expect(s.getStyle()).andReturn(style);
        Catalog cat = createNiceMock(Catalog.class);
        expect(cat.getStyleByName("foo-bar")).andReturn(s);
        replay(s, cat);
        dispatch("/icon/foo-bar", "0.0.0=", cat);
    }
}

