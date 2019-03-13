/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2014 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.web;


import RESTUploadECQLPathMapper.EXPRESSION_KEY;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.config.GeoServer;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.admin.GlobalSettingsPage;
import org.geoserver.web.data.workspace.WorkspaceEditPage;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for evaluating the ECQL TextField for the RESTUploadPathMapper.
 */
public class RESTECQLPanelTest extends GeoServerWicketTestSupport {
    /**
     * Workspace info object called "cite"
     */
    private WorkspaceInfo citeWorkspace;

    @Test
    public void testExpressionSettingOnWorkSpace() {
        // Opening the selected page
        tester.startPage(new WorkspaceEditPage(citeWorkspace));
        tester.assertRenderedPage(WorkspaceEditPage.class);
        tester.assertNoErrorMessage();
        // Setting of the "settings" parameter to true
        FormTester form = tester.newFormTester("form");
        form.setValue("settings:enabled", true);
        form.submit();
        // Check if no error has been found
        tester.assertNoErrorMessage();
        // Get GeoServer object for retrieving the settings associated to the workspace
        GeoServer gs = getGeoServer();
        Assert.assertNotNull(gs.getSettings(citeWorkspace));
        // Reload the page
        tester.startPage(new WorkspaceEditPage(citeWorkspace));
        tester.assertRenderedPage(WorkspaceEditPage.class);
        // CQL expression
        String expression = "stringTemplate(path, \'(\\w{4})_(\\w{7})_(\\d{3})_(\\d{4})(\\d{2})(\\d{2})T(\\d{7})_(\\d{2})\\.(\\w{4})\', " + "'/${1}/${4}/${5}/${6}/${0}')";
        // Set the root directory
        FormTester form2 = tester.newFormTester("form");
        form2.setValue("settings:settingsContainer:otherSettings:extensions:0:content:ecqlexp", expression);
        form2.submit();
        // Check if no error has been found
        tester.assertNoErrorMessage();
        // Control if the defined root has been correctly set
        Assert.assertEquals(gs.getSettings(citeWorkspace).getMetadata().get(EXPRESSION_KEY, String.class), expression);
    }

    @Test
    public void testExpressionSettingOnGlobal() {
        // Opening the selected page
        tester.startPage(new GlobalSettingsPage());
        tester.assertRenderedPage(GlobalSettingsPage.class);
        tester.assertNoErrorMessage();
        // Get GeoServer object for searching the global settings
        GeoServer gs = getGeoServer();
        // CQL expression
        String expression = "stringTemplate(path, \'(\\w{4})_(\\w{7})_(\\d{3})_(\\d{4})(\\d{2})(\\d{2})T(\\d{7})_(\\d{2})\\.(\\w{4})\', " + "'/${1}/${4}/${5}/${6}/${0}')";
        // Set the root directory
        FormTester form = tester.newFormTester("form");
        form.setValue("extensions:0:content:ecqlexp", expression);
        form.submit("submit");
        // Check if no error has been found
        tester.assertNoErrorMessage();
        // Control if the defined root has been correctly set
        Assert.assertEquals(gs.getGlobal().getSettings().getMetadata().get(EXPRESSION_KEY, String.class), expression);
    }
}

