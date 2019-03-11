/**
 * (c) 2015 - 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.web.blob;


import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.gwc.GWC;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geowebcache.config.BlobStoreInfo;
import org.geowebcache.config.ConfigurationException;
import org.geowebcache.layer.TileLayer;
import org.geowebcache.s3.S3BlobStoreInfo;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for the BlobStorePage with S3 BlobStore Panel
 *
 * @author Niels Charlier
 */
public class S3BlobStorePageTest extends GeoServerWicketTestSupport {
    @Test
    public void testPage() {
        BlobStorePage page = new BlobStorePage();
        tester.startPage(page);
        tester.assertRenderedPage(BlobStorePage.class);
        tester.assertComponent("selector", Form.class);
        tester.assertComponent("selector:typeOfBlobStore", DropDownChoice.class);
        tester.assertComponent("blobConfigContainer", MarkupContainer.class);
        tester.assertInvisible("blobConfigContainer:blobStoreForm");
        DropDownChoice typeOfBlobStore = ((DropDownChoice) (tester.getComponentFromLastRenderedPage("selector:typeOfBlobStore")));
        Assert.assertEquals(2, typeOfBlobStore.getChoices().size());
        Assert.assertEquals("File BlobStore", typeOfBlobStore.getChoices().get(0).toString());
        Assert.assertEquals("S3 BlobStore", typeOfBlobStore.getChoices().get(1).toString());
        executeAjaxEventBehavior("selector:typeOfBlobStore", "change", "0");
        tester.assertVisible("blobConfigContainer:blobStoreForm");
        tester.assertComponent("blobConfigContainer:blobStoreForm:blobSpecificPanel", FileBlobStorePanel.class);
        executeAjaxEventBehavior("selector:typeOfBlobStore", "change", "1");
        tester.assertComponent("blobConfigContainer:blobStoreForm:blobSpecificPanel", S3BlobStorePanel.class);
    }

    @Test
    public void testNew() throws ConfigurationException {
        BlobStorePage page = new BlobStorePage();
        tester.startPage(page);
        executeAjaxEventBehavior("selector:typeOfBlobStore", "change", "1");
        FormTester formTester = tester.newFormTester("blobConfigContainer:blobStoreForm");
        formTester.setValue("name", "myblobstore");
        formTester.setValue("enabled", false);
        formTester.setValue("blobSpecificPanel:bucket", "mybucket");
        formTester.setValue("blobSpecificPanel:awsAccessKey", "myaccesskey");
        formTester.setValue("blobSpecificPanel:awsSecretKey", "mysecretkey");
        tester.executeAjaxEvent("blobConfigContainer:blobStoreForm:save", "click");
        List<BlobStoreInfo> blobStores = GWC.get().getBlobStores();
        BlobStoreInfo config = blobStores.get(0);
        Assert.assertTrue((config instanceof S3BlobStoreInfo));
        Assert.assertEquals("myblobstore", config.getName());
        Assert.assertEquals("mybucket", getBucket());
        Assert.assertEquals("myaccesskey", getAwsAccessKey());
        Assert.assertEquals("mysecretkey", getAwsSecretKey());
        Assert.assertEquals(50, getMaxConnections().intValue());
        GWC.get().removeBlobStores(Collections.singleton("myblobstore"));
    }

    @Test
    public void testModify() throws Exception {
        S3BlobStoreInfo sconfig = new S3BlobStoreInfo();
        Field id = BlobStoreInfo.class.getDeclaredField("name");
        id.setAccessible(true);
        id.set(sconfig, "myblobstore");
        sconfig.setMaxConnections(50);
        sconfig.setBucket("mybucket");
        sconfig.setAwsAccessKey("myaccesskey");
        sconfig.setAwsSecretKey("mysecretkey");
        GWC.get().addBlobStore(sconfig);
        TileLayer layer = GWC.get().getTileLayerByName("cite:Lakes");
        layer.setBlobStoreId("myblobstore");
        GWC.get().save(layer);
        BlobStorePage page = new BlobStorePage(sconfig);
        tester.startPage(page);
        tester.assertVisible("blobConfigContainer:blobStoreForm");
        tester.assertComponent("blobConfigContainer:blobStoreForm:blobSpecificPanel", S3BlobStorePanel.class);
        FormTester formTester = tester.newFormTester("blobConfigContainer:blobStoreForm");
        formTester.setValue("name", "yourblobstore");
        formTester.setValue("blobSpecificPanel:bucket", "yourbucket");
        formTester.submit();
        tester.executeAjaxEvent("blobConfigContainer:blobStoreForm:save", "click");
        BlobStoreInfo config = GWC.get().getBlobStores().get(0);
        Assert.assertTrue((config instanceof S3BlobStoreInfo));
        Assert.assertEquals("yourblobstore", config.getId());
        Assert.assertEquals("yourbucket", getBucket());
        // test updated id!
        layer = GWC.get().getTileLayerByName("cite:Lakes");
        Assert.assertEquals("yourblobstore", layer.getBlobStoreId());
        GWC.get().removeBlobStores(Collections.singleton("yourblobstore"));
    }
}

