/**
 * (c) 2015 - 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.test.web;


import java.util.Arrays;
import org.junit.Test;


public class Gml311LinksTest extends AbstractMapPreviewPageTest {
    public Gml311LinksTest() {
        super(Arrays.asList("http://localhost:80/context/gsml/ows?service=WFS&amp;version=1.1.0&amp;request=GetFeature&amp;typeName=gsml%3AMappedFeature&amp;outputFormat=gml3&amp;maxFeatures=50", "http://localhost:80/context/gsml/ows?service=WFS&amp;version=1.1.0&amp;request=GetFeature&amp;typeName=gsml%3AGeologicUnit&amp;outputFormat=gml3&amp;maxFeatures=50", "http://localhost:80/context/ex/ows?service=WFS&amp;version=1.1.0&amp;request=GetFeature&amp;typeName=ex%3AFirstParentFeature&amp;outputFormat=gml3&amp;maxFeatures=50", "http://localhost:80/context/ex/ows?service=WFS&amp;version=1.1.0&amp;request=GetFeature&amp;typeName=ex%3ASecondParentFeature&amp;outputFormat=gml3&amp;maxFeatures=50", "http://localhost:80/context/ex/ows?service=WFS&amp;version=1.1.0&amp;request=GetFeature&amp;typeName=ex%3AParentFeature&amp;outputFormat=gml3&amp;maxFeatures=50", "http://localhost:80/context/om/ows?service=WFS&amp;version=1.1.0&amp;request=GetFeature&amp;typeName=om%3AObservation&amp;outputFormat=gml3&amp;maxFeatures=50"));
    }

    @Test
    public void testGml311Links() {
        super.testAppSchemaGmlLinks();
    }
}

