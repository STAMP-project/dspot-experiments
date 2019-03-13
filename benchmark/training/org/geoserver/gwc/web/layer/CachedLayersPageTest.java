/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.web.layer;


import GeoServerExtensionsHelper.ExtensionsHelperRule;
import java.lang.reflect.Method;
import java.util.List;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.geoserver.gwc.GWC;
import org.geoserver.ows.URLMangler;
import org.geoserver.platform.GeoServerExtensionsHelper;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.data.layergroup.LayerGroupEditPage;
import org.geowebcache.layer.TileLayer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class CachedLayersPageTest extends GeoServerWicketTestSupport {
    private static final Method getReplaceModelMethod;

    /* Yes, we do need to use reflection here as the stupid wicket model is private and has no setter which
    makes it hard to test!
    See https://cwiki.apache.org/confluence/display/WICKET/Testing+Pages
     */
    static {
        try {
            getReplaceModelMethod = AttributeModifier.class.getDeclaredMethod("getReplaceModel");
            CachedLayersPageTest.getReplaceModelMethod.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Rule
    public ExtensionsHelperRule extensions = new GeoServerExtensionsHelper.ExtensionsHelperRule();

    protected static final String NATURE_GROUP = "nature";

    @Test
    public void testPageLoad() {
        CachedLayersPage page = new CachedLayersPage();
        tester.startPage(page);
        tester.assertRenderedPage(CachedLayersPage.class);
        // print(page, true, true);
    }

    @Test
    public void testLayerGroupLink() {
        GWC gwc = GWC.get();
        TileLayer tileLayer = gwc.getTileLayerByName(CachedLayersPageTest.NATURE_GROUP);
        Assert.assertNotNull(tileLayer);
        tester.startComponentInPage(new ConfigureCachedLayerAjaxLink("test", new TileLayerDetachableModel(tileLayer.getName()), null));
        // tester.debugComponentTrees();
        tester.executeAjaxEvent("test:link", "click");
        tester.assertNoErrorMessage();
        tester.assertRenderedPage(LayerGroupEditPage.class);
    }

    @Test
    public void testNoMangleSeedLink() {
        // Don't add a mangler
        CachedLayersPage page = new CachedLayersPage();
        tester.startPage(page);
        tester.assertModelValue("table:listContainer:items:1:itemProperties:7:component:seedLink", "http://localhost:80/context/gwc/rest/seed/cgf:Polygons");
    }

    @Test
    public void testMangleSeedLink() {
        // Mimic a Proxy URL mangler
        URLMangler testMangler = ( base, path, map, type) -> {
            base.setLength(0);
            base.append("http://rewrite/");
        };
        extensions.singleton("testMangler", testMangler, URLMangler.class);
        CachedLayersPage page = new CachedLayersPage();
        tester.startPage(page);
        tester.assertModelValue("table:listContainer:items:1:itemProperties:7:component:seedLink", "http://rewrite/gwc/rest/seed/cgf:Polygons");
    }

    @Test
    public void testNoManglePreviewLink() {
        // Don't add a mangler
        CachedLayersPage page = new CachedLayersPage();
        tester.startPage(page);
        // print(page, true, true);
        Component component = tester.getComponentFromLastRenderedPage("table:listContainer:items:1:itemProperties:6:component:menu");
        List<AttributeModifier> attr = component.getBehaviors(AttributeModifier.class);
        try {
            IModel<?> model = ((IModel<?>) (CachedLayersPageTest.getReplaceModelMethod.invoke(attr.get(0))));
            Assert.assertTrue("Unmangled names fail", model.getObject().toString().contains("http://localhost:80/context/gwc/demo/cgf"));
            return;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testManglePreviewLink() {
        // Mimic a Proxy URL mangler
        URLMangler testMangler = ( base, path, map, type) -> {
            base.setLength(0);
            base.append("http://rewrite/");
        };
        extensions.singleton("testMangler", testMangler, URLMangler.class);
        CachedLayersPage page = new CachedLayersPage();
        tester.startPage(page);
        Component component = tester.getComponentFromLastRenderedPage("table:listContainer:items:1:itemProperties:6:component:menu");
        List<AttributeModifier> attr = component.getBehaviors(AttributeModifier.class);
        try {
            IModel<?> model = ((IModel<?>) (CachedLayersPageTest.getReplaceModelMethod.invoke(attr.get(0))));
            Assert.assertTrue("Mangled names fail", model.getObject().toString().contains("http://rewrite/gwc/demo/cgf"));
            return;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

