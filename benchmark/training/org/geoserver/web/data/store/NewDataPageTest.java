/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.data.store;


import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.ResourceModel;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.impl.CatalogImpl;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class NewDataPageTest extends GeoServerWicketTestSupport {
    @Test
    public void testLoad() {
        GeoServerWicketTestSupport.tester.assertRenderedPage(NewDataPage.class);
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        GeoServerWicketTestSupport.tester.assertComponent("storeForm:vectorResources", ListView.class);
        GeoServerWicketTestSupport.tester.assertComponent("storeForm:rasterResources", ListView.class);
    }

    /**
     * Need to use a static class so it has no back pointer to NewDataPageTest which is not
     * serializable
     *
     * @author groldan
     */
    private static class NewDataPageWithFakeCatalog extends NewDataPage {
        @Override
        protected Catalog getCatalog() {
            return new CatalogImpl();
        }
    }

    @Test
    public void testLoadWithNoWorkspaces() {
        GeoServerWicketTestSupport.tester.startPage(new NewDataPageTest.NewDataPageWithFakeCatalog());
        GeoServerWicketTestSupport.tester.assertRenderedPage(NewDataPageTest.NewDataPageWithFakeCatalog.class);
        String expectedErrMsg = ((String) (new ResourceModel("NewDataPage.noWorkspacesErrorMessage").getObject()));
        Assert.assertNotNull(expectedErrMsg);
        GeoServerWicketTestSupport.tester.assertErrorMessages(new String[]{ expectedErrMsg });
    }

    @Test
    public void testClickLink() {
        Label label = ((Label) (findComponentByContent(GeoServerWicketTestSupport.tester.getLastRenderedPage(), "Properties", Label.class)));
        // getPath() will start with 0: which indicates the page
        GeoServerWicketTestSupport.tester.clickLink(label.getParent().getPath().substring(2));
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        GeoServerWicketTestSupport.tester.assertRenderedPage(DataAccessNewPage.class);
        // print(tester.getLastRenderedPage(), true, true);
        GeoServerWicketTestSupport.tester.assertModelValue("dataStoreForm:storeType", "Properties");
    }
}

