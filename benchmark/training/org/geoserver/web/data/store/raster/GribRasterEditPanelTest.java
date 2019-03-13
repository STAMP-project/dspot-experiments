/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.data.store.raster;


import javax.xml.namespace.QName;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.data.test.MockData;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geotools.coverage.io.grib.GRIBFormat;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class GribRasterEditPanelTest extends GeoServerWicketTestSupport {
    protected static QName SAMPLE_GRIB = new QName(MockData.SF_URI, "sampleGrib", MockData.SF_PREFIX);

    @Test
    public void testGribCreate() throws Exception {
        Page page = tester.startPage(new org.geoserver.web.data.store.CoverageStoreNewPage(new GRIBFormat().getName()));
        tester.assertNoErrorMessage();
        print(page, true, true);
        Component editor = tester.getComponentFromLastRenderedPage("rasterStoreForm:parametersPanel");
        Assert.assertThat(editor, CoreMatchers.instanceOf(GribRasterEditPanel.class));
    }

    @Test
    public void testGribEdit() throws Exception {
        CoverageStoreInfo store = getCatalog().getCoverageStoreByName(GribRasterEditPanelTest.SAMPLE_GRIB.getPrefix(), GribRasterEditPanelTest.SAMPLE_GRIB.getLocalPart());
        Assert.assertNotNull(store);
        Page page = tester.startPage(new org.geoserver.web.data.store.CoverageStoreEditPage(store));
        tester.assertNoErrorMessage();
        print(page, true, true);
        Component editor = tester.getComponentFromLastRenderedPage("rasterStoreForm:parametersPanel");
        Assert.assertThat(editor, CoreMatchers.instanceOf(GribRasterEditPanel.class));
    }
}

