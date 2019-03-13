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
import org.geotools.coverage.io.netcdf.NetCDFFormat;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class NetCDFRasterEditPanelTest extends GeoServerWicketTestSupport {
    protected static QName SAMPLE_NETCDF = new QName(MockData.SF_URI, "sampleNetCDF", MockData.SF_PREFIX);

    @Test
    public void testNetCDFCreate() throws Exception {
        Page page = tester.startPage(new org.geoserver.web.data.store.CoverageStoreNewPage(new NetCDFFormat().getName()));
        tester.assertNoErrorMessage();
        print(page, true, true);
        Component editor = tester.getComponentFromLastRenderedPage("rasterStoreForm:parametersPanel");
        Assert.assertThat(editor, CoreMatchers.instanceOf(NetCDFRasterEditPanel.class));
    }

    @Test
    public void testNetCDFEdit() throws Exception {
        CoverageStoreInfo store = getCatalog().getCoverageStoreByName(NetCDFRasterEditPanelTest.SAMPLE_NETCDF.getPrefix(), NetCDFRasterEditPanelTest.SAMPLE_NETCDF.getLocalPart());
        Assert.assertNotNull(store);
        Page page = tester.startPage(new org.geoserver.web.data.store.CoverageStoreEditPage(store));
        tester.assertNoErrorMessage();
        print(page, true, true);
        Component editor = tester.getComponentFromLastRenderedPage("rasterStoreForm:parametersPanel");
        Assert.assertThat(editor, CoreMatchers.instanceOf(NetCDFRasterEditPanel.class));
    }
}

