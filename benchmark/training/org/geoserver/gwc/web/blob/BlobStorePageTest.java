/**
 * (c) 2015 - 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.web.blob;


import java.util.List;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.gwc.GWC;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geowebcache.config.BlobStoreInfo;
import org.geowebcache.config.ConfigurationException;
import org.geowebcache.config.FileBlobStoreInfo;
import org.geowebcache.layer.TileLayer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for the BlobStorePage
 *
 * @author Niels Charlier
 */
public class BlobStorePageTest extends GeoServerWicketTestSupport {
    @Test
    public void testPage() {
        BlobStorePage page = new BlobStorePage();
        tester.startPage(page);
        tester.assertRenderedPage(BlobStorePage.class);
        tester.assertComponent("selector", Form.class);
        tester.assertComponent("selector:typeOfBlobStore", DropDownChoice.class);
        tester.assertComponent("blobConfigContainer", MarkupContainer.class);
        tester.assertInvisible("blobConfigContainer:blobStoreForm");
        DropDownChoice<?> typeOfBlobStore = ((DropDownChoice<?>) (tester.getComponentFromLastRenderedPage("selector:typeOfBlobStore")));
        Assert.assertEquals(1, typeOfBlobStore.getChoices().size());
        Assert.assertEquals("File BlobStore", typeOfBlobStore.getChoices().get(0).toString());
        executeAjaxEventBehavior("selector:typeOfBlobStore", "change", "0");
        tester.assertVisible("blobConfigContainer:blobStoreForm");
        tester.assertComponent("blobConfigContainer:blobStoreForm:blobSpecificPanel", FileBlobStorePanel.class);
    }

    @Test
    public void testNew() {
        BlobStorePage page = new BlobStorePage();
        tester.startPage(page);
        executeAjaxEventBehavior("selector:typeOfBlobStore", "change", "0");
        FormTester formTester = tester.newFormTester("blobConfigContainer:blobStoreForm");
        formTester.setValue("name", "myblobstore");
        formTester.setValue("enabled", false);
        formTester.setValue("blobSpecificPanel:baseDirectory:border:border_body:paramValue", "/mydir");
        tester.executeAjaxEvent("blobConfigContainer:blobStoreForm:save", "click");
        tester.assertNoErrorMessage();
        List<BlobStoreInfo> blobStores = GWC.get().getBlobStores();
        BlobStoreInfo config = blobStores.get(0);
        Assert.assertTrue((config instanceof FileBlobStoreInfo));
        Assert.assertEquals("myblobstore", config.getName());
        Assert.assertEquals("/mydir", getBaseDirectory());
        Assert.assertEquals(4096, getFileSystemBlockSize());
    }

    @Test
    public void testModify() throws ConfigurationException {
        FileBlobStoreInfo fconfig = new FileBlobStoreInfo("myblobstore");
        fconfig.setFileSystemBlockSize(1024);
        fconfig.setBaseDirectory("/mydir");
        GWC.get().addBlobStore(fconfig);
        TileLayer layer = GWC.get().getTileLayerByName("cite:Lakes");
        layer.setBlobStoreId("myblobstore");
        GWC.get().save(layer);
        BlobStorePage page = new BlobStorePage(fconfig);
        tester.startPage(page);
        tester.assertVisible("blobConfigContainer:blobStoreForm");
        tester.assertComponent("blobConfigContainer:blobStoreForm:blobSpecificPanel", FileBlobStorePanel.class);
        FormTester formTester = tester.newFormTester("blobConfigContainer:blobStoreForm");
        formTester.setValue("name", "yourblobstore");
        formTester.setValue("blobSpecificPanel:baseDirectory:border:border_body:paramValue", "/yourdir");
        formTester.submit();
        tester.executeAjaxEvent("blobConfigContainer:blobStoreForm:save", "click");
        tester.assertNoErrorMessage();
        BlobStoreInfo config = GWC.get().getBlobStores().get(0);
        Assert.assertTrue((config instanceof FileBlobStoreInfo));
        Assert.assertEquals("yourblobstore", config.getName());
        Assert.assertEquals("/yourdir", getBaseDirectory());
        // test updated id!
        layer = GWC.get().getTileLayerByName("cite:Lakes");
        Assert.assertEquals("yourblobstore", layer.getBlobStoreId());
    }

    @Test
    public void testNewDuplicate() throws ConfigurationException {
        // create blobstore
        FileBlobStoreInfo fconfig = new FileBlobStoreInfo("myblobstore");
        fconfig.setFileSystemBlockSize(1024);
        fconfig.setBaseDirectory("/mydir");
        GWC.get().addBlobStore(fconfig);
        TileLayer layer = GWC.get().getTileLayerByName("cite:Lakes");
        layer.setBlobStoreId("myblobstore");
        GWC.get().save(layer);
        BlobStorePage page = new BlobStorePage();
        tester.startPage(page);
        executeAjaxEventBehavior("selector:typeOfBlobStore", "change", "0");
        FormTester formTester = tester.newFormTester("blobConfigContainer:blobStoreForm");
        formTester.setValue("name", "myblobstore");
        formTester.setValue("enabled", false);
        formTester.setValue("blobSpecificPanel:baseDirectory:border:border_body:paramValue", "/mydir");
        tester.executeAjaxEvent("blobConfigContainer:blobStoreForm:save", "click");
        tester.assertErrorMessages("This identifier is already in use, please choose a unique one.");
        List<BlobStoreInfo> blobStores = GWC.get().getBlobStores();
        BlobStoreInfo config = blobStores.get(0);
        Assert.assertTrue((config instanceof FileBlobStoreInfo));
        Assert.assertEquals("myblobstore", config.getName());
        Assert.assertEquals("/mydir", getBaseDirectory());
        Assert.assertEquals(1024, getFileSystemBlockSize());
    }

    @Test
    public void testModifyWithoutRename() throws ConfigurationException {
        FileBlobStoreInfo fconfig = new FileBlobStoreInfo("myblobstore");
        fconfig.setFileSystemBlockSize(1024);
        fconfig.setBaseDirectory("/mydir");
        GWC.get().addBlobStore(fconfig);
        TileLayer layer = GWC.get().getTileLayerByName("cite:Lakes");
        layer.setBlobStoreId("myblobstore");
        GWC.get().save(layer);
        BlobStorePage page = new BlobStorePage(fconfig);
        tester.startPage(page);
        tester.assertVisible("blobConfigContainer:blobStoreForm");
        tester.assertComponent("blobConfigContainer:blobStoreForm:blobSpecificPanel", FileBlobStorePanel.class);
        FormTester formTester = tester.newFormTester("blobConfigContainer:blobStoreForm");
        formTester.setValue("name", "myblobstore");
        formTester.setValue("blobSpecificPanel:baseDirectory:border:border_body:paramValue", "/yourdir");
        formTester.submit();
        tester.executeAjaxEvent("blobConfigContainer:blobStoreForm:save", "click");
        tester.assertNoErrorMessage();
        BlobStoreInfo config = GWC.get().getBlobStores().get(0);
        Assert.assertTrue((config instanceof FileBlobStoreInfo));
        Assert.assertEquals("myblobstore", config.getName());
        Assert.assertEquals("/yourdir", getBaseDirectory());
        // test updated id!
        layer = GWC.get().getTileLayerByName("cite:Lakes");
        Assert.assertEquals("myblobstore", layer.getBlobStoreId());
    }
}

