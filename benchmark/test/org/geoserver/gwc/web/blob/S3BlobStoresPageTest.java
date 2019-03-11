/**
 * (c) 2015 - 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.web.blob;


import java.util.Collections;
import java.util.List;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.CheckBox;
import org.geoserver.gwc.GWC;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.wicket.GeoServerDialog;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.geowebcache.config.BlobStoreInfo;
import org.geowebcache.layer.TileLayer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Modified Test for the BlobStoresPage, but now with a S3 BlobStore
 *
 * @author Niels Charlier
 */
public class S3BlobStoresPageTest extends GeoServerWicketTestSupport {
    private static final String ID_DUMMY1 = "zzz";

    private static final String ID_DUMMY2 = "yyy";

    @Test
    public void testPage() {
        BlobStoresPage page = new BlobStoresPage();
        tester.startPage(page);
        tester.assertRenderedPage(BlobStoresPage.class);
        tester.assertComponent("storesPanel", GeoServerTablePanel.class);
        tester.assertComponent("confirmDeleteDialog", GeoServerDialog.class);
        tester.assertComponent("headerPanel:addNew", AjaxLink.class);
        tester.assertComponent("headerPanel:removeSelected", AjaxLink.class);
    }

    @Test
    public void testBlobStores() throws Exception {
        BlobStoresPage page = new BlobStoresPage();
        BlobStoreInfo dummy1 = dummyStore1();
        GWC.get().addBlobStore(dummy1);
        List<BlobStoreInfo> blobStores = GWC.get().getBlobStores();
        tester.startPage(page);
        GeoServerTablePanel table = ((GeoServerTablePanel) (tester.getComponentFromLastRenderedPage("storesPanel")));
        Assert.assertEquals(blobStores.size(), table.getDataProvider().size());
        Assert.assertTrue(getStoresFromTable(table).contains(dummy1));
        BlobStoreInfo dummy2 = dummyStore2();
        GWC.get().addBlobStore(dummy2);
        Assert.assertEquals(((blobStores.size()) + 1), table.getDataProvider().size());
        Assert.assertTrue(getStoresFromTable(table).contains(dummy2));
        // sort descending on type, S3 should be on top
        tester.clickLink("storesPanel:listContainer:sortableLinks:1:header:link", true);
        tester.clickLink("storesPanel:listContainer:sortableLinks:1:header:link", true);
        Assert.assertEquals(dummy2, getStoresFromTable(table).get(0));
        GWC.get().removeBlobStores(Collections.singleton(S3BlobStoresPageTest.ID_DUMMY1));
        GWC.get().removeBlobStores(Collections.singleton(S3BlobStoresPageTest.ID_DUMMY2));
    }

    @Test
    public void testNew() {
        BlobStoresPage page = new BlobStoresPage();
        tester.startPage(page);
        tester.clickLink("headerPanel:addNew", true);
        tester.assertRenderedPage(BlobStorePage.class);
    }

    @Test
    public void testDelete() throws Exception {
        BlobStoresPage page = new BlobStoresPage();
        tester.startPage(page);
        GeoServerTablePanel table = ((GeoServerTablePanel) (tester.getComponentFromLastRenderedPage("storesPanel")));
        BlobStoreInfo dummy1 = dummyStore1();
        GWC.get().addBlobStore(dummy1);
        Assert.assertTrue(GWC.get().getBlobStores().contains(dummy1));
        // sort descending on id
        tester.clickLink("storesPanel:listContainer:sortableLinks:0:header:link", true);
        tester.clickLink("storesPanel:listContainer:sortableLinks:0:header:link", true);
        // select
        CheckBox selector = ((CheckBox) (tester.getComponentFromLastRenderedPage("storesPanel:listContainer:items:1:selectItemContainer:selectItem")));
        tester.getRequest().setParameter(selector.getInputName(), "true");
        tester.executeAjaxEvent(selector, "click");
        Assert.assertEquals(1, table.getSelection().size());
        Assert.assertEquals(dummy1, table.getSelection().get(0));
        // click delete
        tester.clickLink("headerPanel:removeSelected", true);
        Assert.assertFalse(GWC.get().getBlobStores().contains(dummy1));
        // with layer
        GWC.get().addBlobStore(dummy1);
        Assert.assertTrue(GWC.get().getBlobStores().contains(dummy1));
        TileLayer layer = GWC.get().getTileLayerByName("cite:Lakes");
        layer.setBlobStoreId(S3BlobStoresPageTest.ID_DUMMY1);
        Assert.assertEquals(S3BlobStoresPageTest.ID_DUMMY1, layer.getBlobStoreId());
        GWC.get().save(layer);
        // sort descending on id
        tester.clickLink("storesPanel:listContainer:sortableLinks:0:header:link", true);
        tester.clickLink("storesPanel:listContainer:sortableLinks:0:header:link", true);
        // select
        // super.print(page, false, false, true);
        selector = ((CheckBox) (tester.getComponentFromLastRenderedPage("storesPanel:listContainer:items:2:selectItemContainer:selectItem")));
        tester.getRequest().setParameter(selector.getInputName(), "true");
        tester.executeAjaxEvent(selector, "click");
        // click delete
        Assert.assertEquals(1, table.getSelection().size());
        Assert.assertEquals(dummy1, table.getSelection().get(0));
        ModalWindow w = ((ModalWindow) (tester.getComponentFromLastRenderedPage("confirmDeleteDialog:dialog")));
        Assert.assertFalse(w.isShown());
        tester.clickLink("headerPanel:removeSelected", true);
        Assert.assertTrue(w.isShown());
        // confirm
        GeoServerDialog dialog = ((GeoServerDialog) (tester.getComponentFromLastRenderedPage("confirmDeleteDialog")));
        dialog.submit(new org.apache.wicket.ajax.AjaxRequestHandler(tester.getLastRenderedPage()));
        Assert.assertFalse(GWC.get().getBlobStores().contains(dummy1));
        layer = GWC.get().getTileLayerByName("cite:Lakes");
        Assert.assertNull(layer.getBlobStoreId());
    }
}

