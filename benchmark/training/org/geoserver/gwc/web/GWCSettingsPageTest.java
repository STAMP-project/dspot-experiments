/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.web;


import CacheConfiguration.EvictionPolicy.EXPIRE_AFTER_ACCESS;
import CacheConfiguration.EvictionPolicy.EXPIRE_AFTER_WRITE;
import CacheConfiguration.EvictionPolicy.NULL;
import PublishedType.GROUP;
import PublishedType.RASTER;
import PublishedType.VECTOR;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.List;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.Result;
import org.geoserver.gwc.ConfigurableLockProvider;
import org.geoserver.gwc.GWC;
import org.geoserver.gwc.config.GWCConfig;
import org.geoserver.web.GeoServerHomePage;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geowebcache.locks.MemoryLockProvider;
import org.geowebcache.locks.NIOLockProvider;
import org.geowebcache.storage.blobstore.memory.guava.GuavaCacheProvider;
import org.junit.Assert;
import org.junit.Test;


public class GWCSettingsPageTest extends GeoServerWicketTestSupport {
    @Test
    public void testPageLoad() {
        GWCSettingsPage page = new GWCSettingsPage();
        tester.startPage(page);
        tester.assertRenderedPage(GWCSettingsPage.class);
        // print(page, true, true);
    }

    @Test
    public void testEditDirectWMSIntegration() {
        GWC gwc = GWC.get();
        boolean directWMSIntegrationEnabled = gwc.getConfig().isDirectWMSIntegrationEnabled();
        testEditCheckboxOption("form:gwcServicesPanel:enableWMSIntegration", "gwcServicesPanel:enableWMSIntegration", directWMSIntegrationEnabled);
        Assert.assertEquals((!directWMSIntegrationEnabled), gwc.getConfig().isDirectWMSIntegrationEnabled());
    }

    @Test
    public void testEditEnableWMSC() {
        GWC gwc = GWC.get();
        boolean enabled = gwc.getConfig().isWMSCEnabled();
        testEditCheckboxOption("form:gwcServicesPanel:enableWMSC", "gwcServicesPanel:enableWMSC", enabled);
        Assert.assertEquals((!enabled), gwc.getConfig().isWMSCEnabled());
    }

    @Test
    public void testEnableCacheLayersByDefault() throws Exception {
        GWC gwc = GWC.get();
        GWCConfig config = gwc.getConfig();
        config.setCacheLayersByDefault(false);
        gwc.saveConfig(config);
        Assert.assertFalse(gwc.getConfig().isCacheLayersByDefault());
        testEditCheckboxOption("form:cachingOptionsPanel:cacheLayersByDefault", "cachingOptionsPanel:cacheLayersByDefault", false);
        Assert.assertTrue(gwc.getConfig().isCacheLayersByDefault());
    }

    @Test
    public void testDisableCacheLayersByDefault() throws Exception {
        GWC gwc = GWC.get();
        GWCConfig config = gwc.getConfig();
        config.setCacheLayersByDefault(true);
        gwc.saveConfig(config);
        Assert.assertTrue(gwc.getConfig().isCacheLayersByDefault());
        testEditCheckboxOption("form:cachingOptionsPanel:cacheLayersByDefault", "cachingOptionsPanel:cacheLayersByDefault", true);
        Assert.assertFalse(gwc.getConfig().isCacheLayersByDefault());
    }

    @Test
    public void testEnableAutoCacheStyles() throws Exception {
        GWC gwc = GWC.get();
        GWCConfig config = gwc.getConfig();
        config.setCacheLayersByDefault(true);
        config.setCacheNonDefaultStyles(false);
        gwc.saveConfig(config);
        Assert.assertFalse(gwc.getConfig().isCacheNonDefaultStyles());
        testEditCheckboxOption("form:cachingOptionsPanel:container:configs:cacheNonDefaultStyles", "cachingOptionsPanel:container:configs:cacheNonDefaultStyles", false);
        Assert.assertTrue(gwc.getConfig().isCacheNonDefaultStyles());
    }

    @Test
    public void testDisableAutoCacheStyles() throws Exception {
        GWC gwc = GWC.get();
        GWCConfig config = gwc.getConfig();
        config.setCacheLayersByDefault(true);
        config.setCacheNonDefaultStyles(true);
        gwc.saveConfig(config);
        Assert.assertTrue(gwc.getConfig().isCacheNonDefaultStyles());
        testEditCheckboxOption("form:cachingOptionsPanel:container:configs:cacheNonDefaultStyles", "cachingOptionsPanel:container:configs:cacheNonDefaultStyles", true);
        Assert.assertFalse(gwc.getConfig().isCacheNonDefaultStyles());
    }

    @Test
    public void testSetDefaultCacheFormats() throws Exception {
        GWC gwc = GWC.get();
        GWCConfig config = gwc.getConfig();
        config.setCacheLayersByDefault(true);
        config.getDefaultVectorCacheFormats().add("foo/bar");
        gwc.saveConfig(config);
        GWCSettingsPage page = new GWCSettingsPage();
        tester.startPage(page);
        // print(page, true, true);
        tester.assertRenderedPage(GWCSettingsPage.class);
        final List<String> vectorFormats = new java.util.ArrayList(GWC.getAdvertisedCachedFormats(VECTOR));
        vectorFormats.add("foo/bar");
        final List<String> rasterFormats = new java.util.ArrayList(GWC.getAdvertisedCachedFormats(RASTER));
        final List<String> groupFormats = new java.util.ArrayList(GWC.getAdvertisedCachedFormats(GROUP));
        tester.assertListView("form:cachingOptionsPanel:container:configs:vectorFormatsGroup:vectorFromats", vectorFormats);
        tester.assertListView("form:cachingOptionsPanel:container:configs:rasterFormatsGroup:rasterFromats", rasterFormats);
        tester.assertListView("form:cachingOptionsPanel:container:configs:otherFormatsGroup:otherFromats", groupFormats);
        FormTester form = tester.newFormTester("form");
        final boolean replace = true;// tell selectMultiple to first set all options to false

        form.selectMultiple("cachingOptionsPanel:container:configs:vectorFormatsGroup", new int[]{ 1, 3 }, replace);
        form.selectMultiple("cachingOptionsPanel:container:configs:rasterFormatsGroup", new int[]{ 1, 3 }, replace);
        form.selectMultiple("cachingOptionsPanel:container:configs:otherFormatsGroup", new int[]{ 1, 3 }, replace);
        // print(page, true, true);
        form.submit("submit");
        tester.assertRenderedPage(GeoServerHomePage.class);
        Assert.assertEquals(Sets.newHashSet(vectorFormats.get(1), vectorFormats.get(3)), gwc.getConfig().getDefaultVectorCacheFormats());
        Assert.assertEquals(Sets.newHashSet(rasterFormats.get(1), rasterFormats.get(3)), gwc.getConfig().getDefaultCoverageCacheFormats());
        Assert.assertEquals(Sets.newHashSet(groupFormats.get(1), groupFormats.get(3)), gwc.getConfig().getDefaultOtherCacheFormats());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEditLockProvider() {
        GWC gwc = GWC.get();
        ConfigurableLockProvider lockProvider = ((ConfigurableLockProvider) (gwc.getLockProvider()));
        Assert.assertTrue(((lockProvider.getDelegate()) instanceof MemoryLockProvider));
        GWCSettingsPage page = new GWCSettingsPage();
        tester.startPage(page);
        tester.assertRenderedPage(GWCSettingsPage.class);
        // determine in a future proof way which item contains nioLock
        DropDownChoice<String> lockDropDown = ((DropDownChoice<String>) (tester.getComponentFromLastRenderedPage("form:cachingOptionsPanel:container:configs:lockProvider")));
        List<String> choices = ((List<String>) (lockDropDown.getChoices()));
        int nioLockIndex = -1;
        for (int i = 0; i < (choices.size()); i++) {
            if ("nioLock".equals(choices.get(i))) {
                nioLockIndex = i;
                break;
            }
        }
        Assert.assertTrue((nioLockIndex >= 0));
        FormTester form = tester.newFormTester("form");
        form.select("cachingOptionsPanel:container:configs:lockProvider", nioLockIndex);
        form.submit("submit");
        tester.assertNoErrorMessage();
        // check the lock provider has been changed
        lockProvider = ((ConfigurableLockProvider) (gwc.getLockProvider()));
        Assert.assertTrue(((lockProvider.getDelegate()) instanceof NIOLockProvider));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testNewDefaultGridSet() throws IOException {
        GWC gwc = GWC.get();
        GWCConfig config = gwc.getConfig();
        config.setCacheLayersByDefault(true);
        gwc.saveConfig(config);
        // Creation of a new page to test
        GWCSettingsPage page = new GWCSettingsPage();
        // Start the page
        tester.startPage(page);
        // Ensure the page is correctly rendered
        tester.assertRenderedPage(GWCSettingsPage.class);
        // Ensure the component cachedGridsets belongs to the DefaultGridsetsEditor class
        tester.assertComponent("form:cachingOptionsPanel:container:configs:cachedGridsets", DefaultGridsetsEditor.class);
        // Get the available GridSets
        DropDownChoice<String> availableItems = ((DropDownChoice<String>) (tester.getComponentFromLastRenderedPage("form:cachingOptionsPanel:container:configs:cachedGridsets:availableGridsets")));
        // Ensure the component is present
        Assert.assertNotNull(availableItems);
        // Get the first item
        String item = availableItems.getChoices().get(0);
        // Ensure the item is not null
        Assert.assertNotNull(item);
        // Ensure the item is GlobalCRS84Pixel
        Assert.assertTrue(item.equalsIgnoreCase("GlobalCRS84Pixel"));
        // Selection of the form tests
        FormTester form = tester.newFormTester("form", false);
        form.select("cachingOptionsPanel:container:configs:cachedGridsets:availableGridsets", 0);
        tester.executeAjaxEvent("form:cachingOptionsPanel:container:configs:cachedGridsets:addGridset", "click");
        // Check that the page is correctly rendered
        tester.assertRenderedPage(GWCSettingsPage.class);
        // Save the changes
        form = tester.newFormTester("form", false);
        form.select("cachingOptionsPanel:container:configs:cachedGridsets:availableGridsets", 0);
        form.submit("submit");
        // Check no exception has been thrown
        tester.assertNoErrorMessage();
        // Restart the page
        tester.startPage(page);
        // Ensure the page is correctly rendered
        tester.assertRenderedPage(GWCSettingsPage.class);
        // Get the list of available elements
        availableItems = ((DropDownChoice<String>) (tester.getComponentFromLastRenderedPage("form:cachingOptionsPanel:container:configs:cachedGridsets:availableGridsets")));
        // Ensure that the one used above is no more present
        Assert.assertFalse(availableItems.getChoices().contains(item));
    }

    @Test
    public void testEnableDisableInnerCaching() throws Exception {
        GWC gwc = GWC.get();
        GWCConfig config = gwc.getConfig();
        // Creation of a new page to test
        GWCSettingsPage page = new GWCSettingsPage();
        // Start the page
        tester.startPage(page);
        // Ensure the page is correctly rendered
        tester.assertRenderedPage(GWCSettingsPage.class);
        // Ensure the component blobstores belongs to the BlobStorePanel class
        tester.assertComponent("form:cachingOptionsPanel:container:configs:blobstores", InMemoryBlobStorePanel.class);
        // Selection of the form tests
        FormTester form = tester.newFormTester("form");
        form.setValue("cachingOptionsPanel:container:configs:blobstores:innerCachingEnabled", true);
        // Check that the page is correctly rendered
        tester.assertRenderedPage(GWCSettingsPage.class);
        // Save the changes
        form.submit("submit");
        // Check no exception has been thrown
        tester.assertNoErrorMessage();
        // Check the GWCConfig
        config = gwc.getConfig();
        Assert.assertTrue(config.isInnerCachingEnabled());
        // Start the page
        tester.startPage(new GWCSettingsPage());
        // Ensure the page is correctly rendered
        tester.assertRenderedPage(GWCSettingsPage.class);
        // Check if the Cache Provider is GuavaCacheProvider
        tester.assertComponent("form:cachingOptionsPanel:container:configs:blobstores:container:caches", DropDownChoice.class);
        @SuppressWarnings("unchecked")
        DropDownChoice<String> choice = ((DropDownChoice<String>) (tester.getComponentFromLastRenderedPage("form:cachingOptionsPanel:container:configs:blobstores:container:caches")));
        Assert.assertTrue(choice.getChoices().get(0).equalsIgnoreCase(GuavaCacheProvider.class.toString()));
        // Ensure that the other fields are enabled
        Component comp1 = tester.getComponentFromLastRenderedPage("form:cachingOptionsPanel:container:configs:blobstores:container:cacheConfContainer:hardMemoryLimit");
        Component comp2 = tester.getComponentFromLastRenderedPage("form:cachingOptionsPanel:container:configs:blobstores:container:cacheConfContainer:concurrencyLevel");
        Assert.assertTrue(comp1.isEnabled());
        Assert.assertTrue(comp2.isEnabled());
        // Selection of the form tests
        form = tester.newFormTester("form");
        form.setValue("cachingOptionsPanel:container:configs:blobstores:container:persistenceEnabled", true);
        form.setValue("cachingOptionsPanel:container:configs:blobstores:container:cacheConfContainer:hardMemoryLimit", (1 + ""));
        form.setValue("cachingOptionsPanel:container:configs:blobstores:container:cacheConfContainer:concurrencyLevel", (1 + ""));
        // Check that the page is correctly rendered
        tester.assertRenderedPage(GWCSettingsPage.class);
        // Save the changes
        form.submit("submit");
        // Check no exception has been thrown
        tester.assertNoErrorMessage();
        // Check the GWCConfig
        config = gwc.getConfig();
        Assert.assertTrue(config.isPersistenceEnabled());
        Assert.assertEquals(config.getCacheConfigurations().get(GuavaCacheProvider.class.toString()).getConcurrencyLevel(), 1);
        Assert.assertEquals(config.getCacheConfigurations().get(GuavaCacheProvider.class.toString()).getHardMemoryLimit(), 1);
        // Start the page
        tester.startPage(new GWCSettingsPage());
        // Ensure the page is correctly rendered
        tester.assertRenderedPage(GWCSettingsPage.class);
        // Selection of the form tests
        form = tester.newFormTester("form");
        form.setValue("cachingOptionsPanel:container:configs:blobstores:innerCachingEnabled", false);
        // Save the changes
        form.submit("submit");
        // Start the page
        tester.startPage(new GWCSettingsPage());
        // Ensure the page is correctly rendered
        tester.assertRenderedPage(GWCSettingsPage.class);
        Result res = tester.isVisible("form:cachingOptionsPanel:container:configs:blobstores:container:persistenceEnabled");
        Assert.assertTrue(res.wasFailed());
        // Check the GWCConfig
        config = gwc.getConfig();
        Assert.assertFalse(config.isInnerCachingEnabled());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEvictionPolicy() {
        // creating a start the gwc configuration page
        GWCSettingsPage page = new GWCSettingsPage();
        tester.startPage(page);
        // enabling the cache
        FormTester form = tester.newFormTester("form");
        form.setValue("cachingOptionsPanel:container:configs:blobstores:innerCachingEnabled", true);
        form.submit("submit");
        tester.startPage(new GWCSettingsPage());
        // check that the cache provider is guava
        tester.assertComponent("form:cachingOptionsPanel:container:configs:blobstores:container:caches", DropDownChoice.class);
        DropDownChoice<String> choice = ((DropDownChoice<String>) (tester.getComponentFromLastRenderedPage("form:cachingOptionsPanel:container:configs:blobstores:container:caches")));
        Assert.assertTrue(choice.getChoices().get(0).equalsIgnoreCase(GuavaCacheProvider.class.toString()));
        // check that only guava supported eviction policies are available
        DropDownChoice<String> evictionPoliciesDropDown = ((DropDownChoice<String>) (tester.getComponentFromLastRenderedPage("form:cachingOptionsPanel:container:configs:blobstores:container:cacheConfContainer:policy")));
        List evictionPolicies = evictionPoliciesDropDown.getChoices();
        Assert.assertTrue(((evictionPolicies.size()) == 3));
        Assert.assertTrue(evictionPolicies.contains(NULL));
        Assert.assertTrue(evictionPolicies.contains(EXPIRE_AFTER_ACCESS));
        Assert.assertTrue(evictionPolicies.contains(EXPIRE_AFTER_WRITE));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMemoryCachePanelOpen() throws IOException {
        // enable in memory caching
        GWC gwc = GWC.get();
        GWCConfig config = gwc.getConfig();
        config.setInnerCachingEnabled(true);
        gwc.saveConfig(config);
        // used to blow because an unused label element was added in the code but not in HTML
        GWCSettingsPage page = new GWCSettingsPage();
        tester.startPage(page);
        tester.assertVisible("form:cachingOptionsPanel:container:configs:blobstores:container");
    }
}

