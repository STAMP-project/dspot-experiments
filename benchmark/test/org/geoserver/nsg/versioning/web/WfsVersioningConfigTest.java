/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.nsg.versioning.web;


import WFSInfo.Version.V_20;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.wfs.GMLInfo;
import org.geoserver.wfs.WFSInfo;
import org.geoserver.wfs.web.WFSAdminPage;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class WfsVersioningConfigTest extends GeoServerWicketTestSupport {
    @Test
    public void testChangesToValues() throws Exception {
        String testValue1 = "100";
        String testValue2 = "0";
        login();
        tester.startPage(WFSAdminPage.class);
        FormTester ft = tester.newFormTester("form");
        ft.setValue("maxNumberOfFeaturesForPreview", ((String) (testValue1)));
        ft.submit("submit");
        WFSInfo wfs = getGeoServer().getService(WFSInfo.class);
        Assert.assertEquals("testValue1 = 100", 100, ((int) (wfs.getMaxNumberOfFeaturesForPreview())));
        tester.startPage(WFSAdminPage.class);
        ft = tester.newFormTester("form");
        ft.setValue("maxNumberOfFeaturesForPreview", ((String) (testValue2)));
        ft.submit("submit");
        wfs = getGeoServer().getService(WFSInfo.class);
        Assert.assertEquals("testValue2 = 0", 0, ((int) (wfs.getMaxNumberOfFeaturesForPreview())));
    }

    @Test
    public void testGML32ForceMimeType() throws Exception {
        // make sure GML MIME type overriding is disabled
        WFSInfo info = getGeoServer().getService(WFSInfo.class);
        GMLInfo gmlInfo = info.getGML().get(V_20);
        gmlInfo.setMimeTypeToForce(null);
        getGeoServer().save(info);
        // login with administrator privileges
        login();
        // start WFS service administration page
        tester.startPage(new WFSAdminPage());
        // check that GML MIME type overriding is disabled
        tester.assertComponent("form:gml32:forceGmlMimeType", CheckBox.class);
        CheckBox checkbox = ((CheckBox) (tester.getComponentFromLastRenderedPage("form:gml32:forceGmlMimeType")));
        Assert.assertThat(checkbox.getModelObject(), CoreMatchers.is(false));
        // MIME type drop down choice should be invisible
        tester.assertInvisible("form:gml32:mimeTypeToForce");
        // activate MIME type overriding by clicking in the checkbox
        FormTester formTester = tester.newFormTester("form");
        formTester.setValue("gml32:forceGmlMimeType", true);
        tester.executeAjaxEvent("form:gml32:forceGmlMimeType", "click");
        formTester = tester.newFormTester("form");
        formTester.submit("submit");
        // GML MIME typing overriding should be activated now
        tester.startPage(new WFSAdminPage());
        Assert.assertThat(checkbox.getModelObject(), CoreMatchers.is(true));
        tester.assertVisible("form:gml32:mimeTypeToForce");
        // WFS global service configuration should have been updated too
        info = getGeoServer().getService(WFSInfo.class);
        gmlInfo = info.getGML().get(V_20);
        Assert.assertThat(gmlInfo.getMimeTypeToForce().isPresent(), CoreMatchers.is(true));
        // select text / xml as MIME type to force
        formTester = tester.newFormTester("form");
        formTester.select("gml32:mimeTypeToForce", 2);
        tester.executeAjaxEvent("form:gml32:mimeTypeToForce", "change");
        formTester = tester.newFormTester("form");
        formTester.submit("submit");
        // WFS global service configuration should be forcing text / xml
        info = getGeoServer().getService(WFSInfo.class);
        gmlInfo = info.getGML().get(V_20);
        Assert.assertThat(gmlInfo.getMimeTypeToForce().isPresent(), CoreMatchers.is(true));
        Assert.assertThat(gmlInfo.getMimeTypeToForce().get(), CoreMatchers.is("text/xml"));
        // deactivate GML MIME type overriding by clicking in the checkbox
        tester.startPage(new WFSAdminPage());
        formTester = tester.newFormTester("form");
        formTester.setValue("gml32:forceGmlMimeType", false);
        tester.executeAjaxEvent("form:gml32:forceGmlMimeType", "click");
        formTester = tester.newFormTester("form");
        formTester.submit("submit");
        // GML MIME type overriding should be deactivated now
        tester.startPage(new WFSAdminPage());
        Assert.assertThat(checkbox.getModelObject(), CoreMatchers.is(true));
        tester.assertInvisible("form:gml32:mimeTypeToForce");
        // WFS global service configuration should have been updated too
        info = getGeoServer().getService(WFSInfo.class);
        gmlInfo = info.getGML().get(V_20);
        Assert.assertThat(gmlInfo.getMimeTypeToForce().isPresent(), CoreMatchers.is(false));
    }
}

