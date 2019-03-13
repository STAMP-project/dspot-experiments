/**
 * (c) 2014-2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.admin;


import RenderedRegistryMode.MODE_NAME;
import com.sun.media.jai.mlib.MlibWarpRIF;
import com.sun.media.jai.opimage.WarpRIF;
import it.geosolutions.jaiext.JAIExt;
import java.util.Collection;
import java.util.Set;
import org.apache.wicket.extensions.markup.html.form.palette.Palette;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.config.JAIEXTInfo;
import org.geoserver.config.JAIInfo;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geotools.image.ImageWorker;
import org.geotools.image.util.ImageUtilities;
import org.junit.Assert;
import org.junit.Test;


public class JAIPageTest extends GeoServerWicketTestSupport {
    @Test
    public void testValues() {
        JAIInfo info = ((JAIInfo) (getGeoServerApplication().getGeoServer().getGlobal().getJAI()));
        login();
        GeoServerWicketTestSupport.tester.startPage(JAIPage.class);
        GeoServerWicketTestSupport.tester.assertComponent("form:tileThreads", TextField.class);
        GeoServerWicketTestSupport.tester.assertModelValue("form:tileThreads", info.getTileThreads());
    }

    @Test
    public void testNativeWarp() {
        if (!(ImageUtilities.isMediaLibAvailable())) {
            // If medialib acceleration is not available, the test is not needed
            Assert.assertTrue(true);
            return;
        }
        GeoServer geoServer = getGeoServerApplication().getGeoServer();
        GeoServerInfo global = geoServer.getGlobal();
        JAIInfo info = ((JAIInfo) (global.getJAI()));
        // Ensure that by default Warp acceleration is set to false
        Assert.assertFalse(info.isAllowNativeWarp());
        // Register Warp as JAI operation
        JAIExt.registerJAIDescriptor("Warp");
        JAIEXTInfo jeinfo = info.getJAIEXTInfo();
        Set<String> jeOps = jeinfo.getJAIEXTOperations();
        jeOps.remove("Warp");
        jeinfo.setJAIEXTOperations(jeOps);
        jeinfo.getJAIOperations().add("Warp");
        info.setJAIEXTInfo(jeinfo);
        global.setJAI(info);
        geoServer.save(global);
        login();
        // Ensure the page is rendered
        GeoServerWicketTestSupport.tester.startPage(JAIPage.class);
        GeoServerWicketTestSupport.tester.assertRenderedPage(JAIPage.class);
        // Ensure the JAI Page is present if enabled
        Palette p;
        Collection jaiext;
        Object factory;
        boolean isJAIExtEnabled = ImageWorker.isJaiExtEnabled();
        if (isJAIExtEnabled) {
            GeoServerWicketTestSupport.tester.assertComponent("form:jaiext", JAIEXTPanel.class);
            GeoServerWicketTestSupport.tester.assertComponent("form:jaiext:jaiextOps", Palette.class);
            p = ((Palette) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:jaiext:jaiextOps")));
            jaiext = p.getChoices();
            Assert.assertNotNull(jaiext);
            // JAI choices
            Assert.assertTrue((!(jaiext.contains("Warp"))));
        } else {
            GeoServerWicketTestSupport.tester.assertInvisible("form:jaiext");
        }
        // Set Native Warp enabled
        FormTester form = GeoServerWicketTestSupport.tester.newFormTester("form");
        form.setValue("allowNativeWarp", true);
        form.submit("submit");
        // Ensure no exception has been thrown
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        info = ((JAIInfo) (global.getJAI()));
        // Check that Warp is enabled
        if (isJAIExtEnabled) {
            Assert.assertTrue(info.isAllowNativeWarp());
            // Ensure the factory is correctly registered
            factory = info.getJAI().getOperationRegistry().getFactory(MODE_NAME, "Warp");
            Assert.assertTrue((factory instanceof MlibWarpRIF));
        } else {
            Assert.assertFalse(info.isAllowNativeWarp());
            factory = info.getJAI().getOperationRegistry().getFactory(MODE_NAME, "Warp");
            Assert.assertTrue((factory instanceof WarpRIF));
        }
        // Unset Native Warp enabled
        // Render the page again
        GeoServerWicketTestSupport.tester.startPage(JAIPage.class);
        GeoServerWicketTestSupport.tester.assertRenderedPage(JAIPage.class);
        if (isJAIExtEnabled) {
            GeoServerWicketTestSupport.tester.assertComponent("form:jaiext", JAIEXTPanel.class);
            GeoServerWicketTestSupport.tester.assertComponent("form:jaiext:jaiextOps", Palette.class);
            p = ((Palette) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:jaiext:jaiextOps")));
            jaiext = p.getChoices();
            Assert.assertNotNull(jaiext);
            // JAI choices
            Assert.assertTrue((!(jaiext.contains("Warp"))));
        }
        form = GeoServerWicketTestSupport.tester.newFormTester("form");
        form.setValue("allowNativeWarp", false);
        form.submit("submit");
        // Ensure no exception has been thrown
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        info = ((JAIInfo) (global.getJAI()));
        // Check that Warp is disabled
        Assert.assertFalse(info.isAllowNativeWarp());
        // Ensure the factory is correctly registered
        factory = info.getJAI().getOperationRegistry().getFactory(MODE_NAME, "Warp");
        Assert.assertTrue((factory instanceof WarpRIF));
    }
}

