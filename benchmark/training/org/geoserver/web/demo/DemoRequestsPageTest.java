/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.demo;


import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.platform.resource.Files;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.SerializationUtils;


/**
 *
 *
 * @author Gabriel Roldan
 * @unknown $Id$
 */
public class DemoRequestsPageTest extends GeoServerWicketTestSupport {
    private File demoDir;

    /**
     * Kind of smoke test to make sure the page structure was correctly set up once loaded
     */
    @Test
    public void testStructure() {
        // print(tester.getLastRenderedPage(), true, true);
        Assert.assertTrue(((tester.getLastRenderedPage()) instanceof DemoRequestsPage));
        tester.assertComponent("demoRequestsForm", Form.class);
        tester.assertComponent("demoRequestsForm:demoRequestsList", DropDownChoice.class);
        tester.assertComponent("demoRequestsForm:url", TextField.class);
        tester.assertComponent("demoRequestsForm:body:editorContainer:editorParent:editor", TextArea.class);
        tester.assertComponent("demoRequestsForm:username", TextField.class);
        tester.assertComponent("demoRequestsForm:password", PasswordTextField.class);
        tester.assertComponent("demoRequestsForm:submit", AjaxSubmitLink.class);
        tester.assertComponent("responseWindow", ModalWindow.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDemoListLoaded() {
        // print(tester.getLastRenderedPage(), true, true);
        /* Expected choices are the file names in the demo requests dir
        (/src/test/resources/test-data/demo-requests in this case)
         */
        final List<String> expectedList = Arrays.asList(new String[]{ "WFS_getFeature-1.1.xml", "WMS_describeLayer.url" });
        DropDownChoice dropDown = ((DropDownChoice) (tester.getComponentFromLastRenderedPage("demoRequestsForm:demoRequestsList")));
        List choices = dropDown.getChoices();
        Assert.assertEquals(expectedList, choices);
    }

    @Test
    public void testUrlLinkUnmodified() {
        // print(tester.getLastRenderedPage(), true, true);
        final FormTester requestFormTester = tester.newFormTester("demoRequestsForm");
        final String requestName = "WMS_describeLayer.url";
        requestFormTester.select("demoRequestsList", 1);
        /* There's an AjaxFormSubmitBehavior attached to onchange so force it */
        tester.executeAjaxEvent("demoRequestsForm:demoRequestsList", "change");
        tester.assertModelValue("demoRequestsForm:demoRequestsList", requestName);
        final boolean isAjax = true;
        tester.clickLink("demoRequestsForm:submit", isAjax);
        tester.assertVisible("responseWindow");
        IModel model = tester.getLastRenderedPage().getDefaultModel();
        Assert.assertTrue(((model.getObject()) instanceof DemoRequest));
        DemoRequest req = ((DemoRequest) (model.getObject()));
        Assert.assertEquals(Files.asResource(demoDir).path(), req.getDemoDir());
        String requestFileName = req.getRequestFileName();
        String requestUrl = req.getRequestUrl();
        String requestBody = req.getRequestBody();
        Assert.assertEquals(requestName, requestFileName);
        Assert.assertNotNull(requestUrl);
        Assert.assertNull(requestBody);
    }

    @Test
    public void testUrlLinkSelected() {
        // print(tester.getLastRenderedPage(), true, true);
        final FormTester requestFormTester = tester.newFormTester("demoRequestsForm");
        final String requestName = "WMS_describeLayer.url";
        requestFormTester.select("demoRequestsList", 1);
        /* There's an AjaxFormSubmitBehavior attached to onchange so force it */
        tester.executeAjaxEvent("demoRequestsForm:demoRequestsList", "change");
        tester.assertModelValue("demoRequestsForm:demoRequestsList", requestName);
        final boolean isAjax = true;
        tester.clickLink("demoRequestsForm:submit", isAjax);
        tester.assertVisible("responseWindow");
        IModel model = tester.getLastRenderedPage().getDefaultModel();
        Assert.assertTrue(((model.getObject()) instanceof DemoRequest));
        DemoRequest req = ((DemoRequest) (model.getObject()));
        Assert.assertEquals(Files.asResource(demoDir).path(), req.getDemoDir());
        String requestFileName = req.getRequestFileName();
        String requestUrl = req.getRequestUrl();
        String requestBody = req.getRequestBody();
        Assert.assertEquals(requestName, requestFileName);
        Assert.assertNotNull(requestUrl);
        Assert.assertNull(requestBody);
    }

    @Test
    public void testUrlLinkModified() {
        // print(tester.getLastRenderedPage(), true, true);
        final FormTester requestFormTester = tester.newFormTester("demoRequestsForm");
        final String requestName = "WMS_describeLayer.url";
        requestFormTester.select("demoRequestsList", 1);
        /* There's an AjaxFormSubmitBehavior attached to onchange so force it */
        tester.executeAjaxEvent("demoRequestsForm:demoRequestsList", "change");
        tester.assertModelValue("demoRequestsForm:demoRequestsList", requestName);
        final String modifiedUrl = "http://modified/url";
        TextField url = ((TextField) (tester.getComponentFromLastRenderedPage("demoRequestsForm:url")));
        url.setModelValue(new String[]{ modifiedUrl });
        Assert.assertEquals(modifiedUrl, url.getValue());
        final boolean isAjax = true;
        tester.clickLink("demoRequestsForm:submit", isAjax);
        tester.assertVisible("responseWindow");
        IModel model = tester.getLastRenderedPage().getDefaultModel();
        Assert.assertTrue(((model.getObject()) instanceof DemoRequest));
        DemoRequest req = ((DemoRequest) (model.getObject()));
        String requestUrl = req.getRequestUrl();
        Assert.assertEquals(modifiedUrl, requestUrl);
    }

    @Test
    public void testProxyBaseUrl() {
        // setup the proxy base url
        GeoServerInfo global = getGeoServer().getGlobal();
        String proxyBaseUrl = "http://www.geoserver.org/test_gs";
        global.getSettings().setProxyBaseUrl(proxyBaseUrl);
        try {
            getGeoServer().save(global);
            final FormTester requestFormTester = tester.newFormTester("demoRequestsForm");
            final String requestName = "WMS_describeLayer.url";
            requestFormTester.select("demoRequestsList", 1);
            /* There's an AjaxFormSubmitBehavior attached to onchange so force it */
            tester.executeAjaxEvent("demoRequestsForm:demoRequestsList", "change");
            tester.assertModelValue("demoRequestsForm:demoRequestsList", requestName);
            final boolean isAjax = true;
            tester.clickLink("demoRequestsForm:submit", isAjax);
            tester.assertVisible("responseWindow");
            IModel model = tester.getLastRenderedPage().getDefaultModel();
            Assert.assertTrue(((model.getObject()) instanceof DemoRequest));
            DemoRequest req = ((DemoRequest) (model.getObject()));
            Assert.assertEquals(Files.asResource(demoDir).path(), req.getDemoDir());
            String requestFileName = req.getRequestFileName();
            String requestUrl = req.getRequestUrl();
            Assert.assertEquals(requestName, requestFileName);
            Assert.assertTrue(requestUrl.startsWith((proxyBaseUrl + "/wms")));
        } finally {
            global.getSettings().setProxyBaseUrl(null);
            getGeoServer().save(global);
        }
    }

    @Test
    public void testAuthentication() {
        final FormTester requestFormTester = tester.newFormTester("demoRequestsForm");
        final String requestName = "WMS_describeLayer.url";
        requestFormTester.select("demoRequestsList", 1);
        /* There's an AjaxFormSubmitBehavior attached to onchange so force it */
        tester.executeAjaxEvent("demoRequestsForm:demoRequestsList", "change");
        tester.assertModelValue("demoRequestsForm:demoRequestsList", requestName);
        String username = "admin";
        String password = "geoserver";
        requestFormTester.setValue("username", username);
        requestFormTester.setValue("password", password);
        final boolean isAjax = true;
        tester.clickLink("demoRequestsForm:submit", isAjax);
        tester.assertVisible("responseWindow");
        IModel model = tester.getLastRenderedPage().getDefaultModel();
        Assert.assertTrue(((model.getObject()) instanceof DemoRequest));
        Assert.assertEquals(username, tester.getLastRequest().getPostParameters().getParameterValue("username").toString());
        Assert.assertEquals(password, tester.getLastRequest().getPostParameters().getParameterValue("password").toString());
    }

    @Test
    public void testSerializable() {
        DemoRequestsPage page = new DemoRequestsPage();
        DemoRequestsPage page2 = ((DemoRequestsPage) (SerializationUtils.deserialize(SerializationUtils.serialize(page))));
        Assert.assertEquals(page.demoDir, page2.demoDir);
    }
}

