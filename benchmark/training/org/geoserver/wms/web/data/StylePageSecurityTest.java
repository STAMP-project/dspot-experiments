/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.web.data;


import MockData.CITE_PREFIX;
import MockData.SF_PREFIX;
import StyleEditPage.NAME;
import StyleEditPage.WORKSPACE;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class StylePageSecurityTest extends GeoServerWicketTestSupport {
    StyleInfo buildingsStyle;

    @Test
    public void testNewCite() throws IOException, URISyntaxException {
        loginAsCite();
        tester.startPage(StyleNewPage.class);
        tester.assertRenderedPage(StyleNewPage.class);
        tester.assertNoErrorMessage();
        tester.assertComponent("styleForm:context:panel:name", TextField.class);
        tester.assertComponent("styleForm:context:panel:format", DropDownChoice.class);
        tester.assertComponent("styleForm:context:panel:workspace", DropDownChoice.class);
        // format only editable for new styles
        DropDownChoice format = ((DropDownChoice) (tester.getComponentFromLastRenderedPage("styleForm:context:panel:format")));
        TestCase.assertTrue(format.isEnabled());
        // workspace only editable upon creation or if admin
        DropDownChoice workspace = ((DropDownChoice) (tester.getComponentFromLastRenderedPage("styleForm:context:panel:workspace")));
        TestCase.assertTrue(workspace.isEnabled());
        Assert.assertFalse(workspace.isNullValid());
        Assert.assertNotNull(workspace.getModelObject());
    }

    @Test
    public void testNewCiteCiteWs() throws IOException, URISyntaxException {
        loginAsCite();
        tester.startPage(StyleNewPage.class);
        FormTester form = tester.newFormTester("styleForm");
        File styleFile = new File(new java.io.File(getClass().getResource("default_point.sld").toURI()));
        String sld = IOUtils.toString(new FileReader(styleFile)).replaceAll("\r\n", "\n").replaceAll("\r", "\n");
        form.setValue("styleEditor:editorContainer:editorParent:editor", sld);
        form.setValue("context:panel:name", "cite_style");
        form.setValue("context:panel:workspace", getCatalog().getWorkspaceByName(CITE_PREFIX).getId());
        form.submit();
        tester.assertRenderedPage(StyleNewPage.class);
        tester.executeAjaxEvent("submit", "click");
        tester.assertRenderedPage(StylePage.class);
        StyleInfo style = getCatalog().getStyleByName("cite_style");
        Assert.assertNotNull(style);
        Assert.assertNull(style.getLegend());
    }

    @Test
    public void testNewCiteSfWs() throws IOException, URISyntaxException {
        loginAsCite();
        tester.startPage(StyleNewPage.class);
        // Setting a workspace we don't have permissions for should fail
        FormTester form = tester.newFormTester("styleForm");
        File styleFile = new File(new java.io.File(getClass().getResource("default_point.sld").toURI()));
        String sld = IOUtils.toString(new FileReader(styleFile)).replaceAll("\r\n", "\n").replaceAll("\r", "\n");
        form.setValue("styleEditor:editorContainer:editorParent:editor", sld);
        form.setValue("context:panel:name", "sf_style");
        form.setValue("context:panel:workspace", getCatalog().getWorkspaceByName(SF_PREFIX).getId());
        form.submit();
        tester.assertRenderedPage(StyleNewPage.class);
        tester.executeAjaxEvent("submit", "click");
        tester.assertErrorMessages("Field 'Workspace' is required.");
        StyleInfo style = getCatalog().getStyleByName("sf_style");
        Assert.assertNull(style);
    }

    @Test
    public void testNewAdmin() {
        login();
        tester.startPage(StyleNewPage.class);
        tester.assertRenderedPage(StyleNewPage.class);
        tester.assertNoErrorMessage();
        tester.assertComponent("styleForm:context:panel:name", TextField.class);
        tester.assertComponent("styleForm:context:panel:format", DropDownChoice.class);
        tester.assertComponent("styleForm:context:panel:workspace", DropDownChoice.class);
        // format only editable for new styles
        DropDownChoice format = ((DropDownChoice) (tester.getComponentFromLastRenderedPage("styleForm:context:panel:format")));
        TestCase.assertTrue(format.isEnabled());
        // workspace only editable upon creation or if admin
        DropDownChoice workspace = ((DropDownChoice) (tester.getComponentFromLastRenderedPage("styleForm:context:panel:workspace")));
        TestCase.assertTrue(workspace.isEnabled());
        TestCase.assertTrue(workspace.isNullValid());
        Assert.assertNull(workspace.getModelObject());
    }

    @Test
    public void testEditCite() {
        loginAsCite();
        PageParameters pp = new PageParameters();
        pp.set(NAME, buildingsStyle.getName());
        pp.set(WORKSPACE, Optional.ofNullable(buildingsStyle.getWorkspace()).map(( ws) -> ws.getName()).orElse(""));
        StyleEditPage page = tester.startPage(StyleEditPage.class, pp);
        tester.startPage(page);
        tester.assertRenderedPage(StyleEditPage.class);
        tester.assertNoErrorMessage();
        tester.assertComponent("styleForm:context:panel:name", TextField.class);
        tester.assertComponent("styleForm:context:panel:format", DropDownChoice.class);
        tester.assertComponent("styleForm:context:panel:workspace", DropDownChoice.class);
        // format only editable for new styles
        DropDownChoice format = ((DropDownChoice) (tester.getComponentFromLastRenderedPage("styleForm:context:panel:format")));
        Assert.assertFalse(format.isEnabled());
        // workspace only editable upon creation or if admin
        DropDownChoice workspace = ((DropDownChoice) (tester.getComponentFromLastRenderedPage("styleForm:context:panel:workspace")));
        Assert.assertFalse(workspace.isEnabled());
        Assert.assertFalse(workspace.isNullValid());
        Assert.assertNotNull(workspace.getModelObject());
    }

    @Test
    public void testEditAdmin() {
        login();
        StyleEditPage page = new StyleEditPage(buildingsStyle);
        tester.startPage(page);
        tester.assertRenderedPage(StyleEditPage.class);
        tester.assertNoErrorMessage();
        tester.assertComponent("styleForm:context:panel:name", TextField.class);
        tester.assertComponent("styleForm:context:panel:format", DropDownChoice.class);
        tester.assertComponent("styleForm:context:panel:workspace", DropDownChoice.class);
        // format only editable for new styles
        DropDownChoice format = ((DropDownChoice) (tester.getComponentFromLastRenderedPage("styleForm:context:panel:format")));
        Assert.assertFalse(format.isEnabled());
        // workspace only editable upon creation or if admin
        DropDownChoice workspace = ((DropDownChoice) (tester.getComponentFromLastRenderedPage("styleForm:context:panel:workspace")));
        TestCase.assertTrue(workspace.isEnabled());
        TestCase.assertTrue(workspace.isNullValid());
        Assert.assertNotNull(workspace.getModelObject());
    }
}

