/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.web.data;


import AbstractStylePage.ChooseImagePanel;
import FeedbackMessage.ERROR;
import MockData.BUILDINGS;
import java.awt.Color;
import java.awt.TextArea;
import java.awt.TextField;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.io.IOUtils;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.apache.wicket.util.tester.WicketTesterHelper;
import org.geoserver.catalog.impl.StyleInfoImpl;
import org.geoserver.config.GeoServerDataDirectory;
import org.geoserver.data.test.TestData;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.platform.resource.Paths;
import org.geoserver.platform.resource.Resource;
import org.geoserver.platform.resource.Resources;
import org.geoserver.web.GeoServerApplication;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.geotools.util.URLs;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


public class StyleEditPageTest extends GeoServerWicketTestSupport {
    StyleInfo buildingsStyle;

    StyleEditPage edit;

    @Test
    public void testLoad() throws Exception {
        tester.assertRenderedPage(StyleEditPage.class);
        tester.assertNoErrorMessage();
        tester.debugComponentTrees();
        tester.assertComponent("styleForm:context:panel:name", TextField.class);
        tester.assertComponent("styleForm:styleEditor:editorContainer:editorParent:editor", TextArea.class);
        tester.assertVisible("styleForm:context:panel:upload");
        // Load the legend
        tester.executeAjaxEvent("styleForm:context:panel:legendPanel:externalGraphicContainer:showhide:show", "click");
        tester.assertComponent("styleForm:context:panel:legendPanel", ExternalGraphicPanel.class);
        tester.assertComponent("styleForm:context:panel:legendPanel:externalGraphicContainer:list:onlineResource", TextField.class);
        tester.assertComponent("styleForm:context:panel:legendPanel:externalGraphicContainer:list:width", TextField.class);
        tester.assertComponent("styleForm:context:panel:legendPanel:externalGraphicContainer:list:height", TextField.class);
        tester.assertComponent("styleForm:context:panel:legendPanel:externalGraphicContainer:list:format", TextField.class);
        tester.assertModelValue("styleForm:context:panel:name", "Buildings");
        GeoServerResourceLoader loader = GeoServerExtensions.bean(GeoServerResourceLoader.class);
        Assert.assertNotNull(loader);
        String path = Paths.path("styles", Paths.convert(buildingsStyle.getFilename()));
        Resource styleFile = loader.get(path);
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document d1 = db.parse(styleFile.in());
        // GEOS-3257, actually drag into xml and compare with xmlunit to avoid
        // line ending problems
        String xml = tester.getComponentFromLastRenderedPage("styleForm:styleEditor").getDefaultModelObjectAsString();
        xml = xml.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&quot;", "\"");
        Document d2 = db.parse(new ByteArrayInputStream(xml.getBytes()));
        assertXMLEqual(d1, d2);
    }

    @Test
    public void testInsertImage() throws Exception {
        // create some fake images
        GeoServerDataDirectory dd = GeoServerApplication.get().getBeanOfType(GeoServerDataDirectory.class);
        dd.getStyles().get("somepicture.png").out().close();
        dd.getStyles().get("otherpicture.jpg").out().close();
        dd.getStyles().get("vector.svg").out().close();
        // since we don't have code mirror available in the test environment, we are kind of limited
        // we'll make the tool bar visible to test the dialog anyway
        tester.getComponentFromLastRenderedPage("styleForm:styleEditor:editorContainer:toolbar", false).setVisible(true);
        tester.assertComponent("styleForm:styleEditor:editorContainer:toolbar:custom-buttons:1", AjaxLink.class);
        tester.clickLink("styleForm:styleEditor:editorContainer:toolbar:custom-buttons:1");
        tester.assertComponent("dialog:dialog:content:form:userPanel", ChooseImagePanel.class);
        tester.assertComponent("dialog:dialog:content:form:userPanel:image", DropDownChoice.class);
        tester.assertInvisible("dialog:dialog:content:form:userPanel:display");
        @SuppressWarnings("unchecked")
        List<? extends String> choices = ((DropDownChoice<String>) (tester.getComponentFromLastRenderedPage("dialog:dialog:content:form:userPanel:image"))).getChoices();
        Assert.assertEquals(3, choices.size());
        Assert.assertEquals("otherpicture.jpg", choices.get(0));
        Assert.assertEquals("somepicture.png", choices.get(1));
        Assert.assertEquals("vector.svg", choices.get(2));
        FormTester formTester = tester.newFormTester("dialog:dialog:content:form");
        formTester.select("userPanel:image", 1);
        tester.executeAjaxEvent("dialog:dialog:content:form:userPanel:image", "change");
        tester.assertVisible("dialog:dialog:content:form:userPanel:display");
        Assert.assertTrue(getName().equals("somepicture.png"));
        formTester.submit("submit");
        // we can at least test that the right javascript code is there
        Pattern pattern = Pattern.compile(("replaceSelection\\(\'<ExternalGraphic>\\\\n" + ((("<OnlineResource xlink:type=\"simple\" xlink:href=\"" + "(.*)\" />\\\\n") + "<Format>(.*)</Format>\\\\n") + "</ExternalGraphic>\\\\n\'\\)")));
        Matcher matcher = pattern.matcher(tester.getLastResponse().getDocument());
        Assert.assertTrue(matcher.find());
        Assert.assertEquals("somepicture.png", matcher.group(1));
        Assert.assertEquals("image/png", matcher.group(2));
        // test uploading
        tester.clickLink("styleForm:styleEditor:editorContainer:toolbar:custom-buttons:1");
        formTester = tester.newFormTester("dialog:dialog:content:form");
        File file = new File(URLs.urlToFile(getClass().getResource("GeoServer_75.png")));
        formTester.setFile("userPanel:upload", file, "image/png");
        formTester.submit("submit");
        Assert.assertTrue(Resources.exists(dd.getStyles().get("GeoServer_75.png")));
        matcher = pattern.matcher(tester.getLastResponse().getDocument());
        Assert.assertTrue(matcher.find());
        Assert.assertEquals("GeoServer_75.png", matcher.group(1));
        Assert.assertEquals("image/png", matcher.group(2));
    }

    @Test
    public void testLayerAssociationsTab() {
        LayerInfo l = getCatalog().getLayers().get(0);
        Assert.assertFalse(((l.getDefaultStyle()) == (buildingsStyle)));
        tester.executeAjaxEvent("styleForm:context:tabs-container:tabs:1:link", "click");
        tester.assertComponent("styleForm:context:panel:layer.table", GeoServerTablePanel.class);
        // Set the form value of the checkbox to true and force an ajax form update
        FormTester form = tester.newFormTester("styleForm");
        form.setValue("context:panel:layer.table:listContainer:items:1:itemProperties:2:component:default.selected", true);
        AbstractAjaxBehavior behavior = ((AbstractAjaxBehavior) (WicketTesterHelper.findBehavior(tester.getComponentFromLastRenderedPage("styleForm:context:panel:layer.table:listContainer:items:1:itemProperties:2:component:default.selected"), AjaxFormComponentUpdatingBehavior.class)));
        tester.executeBehavior(behavior);
        l = getCatalog().getLayers().get(0);
        Assert.assertEquals(buildingsStyle, l.getDefaultStyle());
    }

    @Test
    public void testLayerAssociationsMissingStyle() {
        LayerInfo l = getCatalog().getLayers().get(0);
        StyleInfo s = l.getDefaultStyle();
        l.setDefaultStyle(null);
        // Save against the facade to skip validation
        getCatalog().getFacade().save(l);
        try {
            edit = new StyleEditPage(buildingsStyle);
            tester.startPage(edit);
            tester.executeAjaxEvent("styleForm:context:tabs-container:tabs:1:link", "click");
            tester.assertComponent("styleForm:context:panel:layer.table", GeoServerTablePanel.class);
        } finally {
            l.setDefaultStyle(s);
            getCatalog().save(l);
        }
    }

    @Test
    public void testLayerAttributesUnreachableLayer() throws Exception {
        tester.executeAjaxEvent("styleForm:context:tabs-container:tabs:3:link", "click");
        tester.executeAjaxEvent("styleForm:context:panel:changeLayer:link", "click");
        tester.assertComponent("styleForm:popup:content:layer.table", GeoServerTablePanel.class);
        tester.executeAjaxEvent("styleForm:popup:content:layer.table:navigatorBottom:navigator:last", "click");
        tester.assertLabel("styleForm:popup:content:layer.table:listContainer:items:30:itemProperties:2:component:link:layer.name", "unlayer");
        tester.executeAjaxEvent("styleForm:popup:content:layer.table:listContainer:items:30:itemProperties:2:component:link", "click");
        tester.assertContains("Failed to load attribute list, internal error is:");
    }

    @Test
    public void testLayerAttributesTabWMS() {
        tester.executeAjaxEvent("styleForm:context:tabs-container:tabs:3:link", "click");
        tester.executeAjaxEvent("styleForm:context:panel:changeLayer:link", "click");
        tester.assertComponent("styleForm:popup:content:layer.table", GeoServerTablePanel.class);
        // 31 layers total, 25 layers per page; foo should not appear on page 1 or 2.
        tester.assertContainsNot("wmsstore");
        tester.executeAjaxEvent("styleForm:popup:content:layer.table:navigatorBottom:navigator:last", "click");
        tester.assertContainsNot("wmsstore");
    }

    @Test
    public void testMissingName() throws Exception {
        FormTester form = tester.newFormTester("styleForm");
        form.setValue("context:panel:name", "");
        form.submit();
        tester.assertRenderedPage(StyleEditPage.class);
        tester.assertErrorMessages(new String[]{ "Field 'Name' is required." });
    }

    @Test
    public void testChangeName() throws Exception {
        FormTester form = tester.newFormTester("styleForm");
        form.setValue("context:panel:name", "BuildingsNew");
        form.submit();
        Assert.assertNull(getCatalog().getStyleByName("Buildings"));
        Assert.assertNotNull(getCatalog().getStyleByName("BuildingsNew"));
    }

    @Test
    public void testChangeNameAlreadyExists() throws Exception {
        FormTester form = tester.newFormTester("styleForm");
        form.setValue("context:panel:name", "Default");
        tester.executeAjaxEvent("submit", "click");
        tester.assertContains("java.lang.IllegalArgumentException: Style named &#039;Default&#039; already exists");
        tester.debugComponentTrees();
    }

    @Test
    public void testValidate() throws Exception {
        String xml = "<StyledLayerDescriptor version='1.0.0' " + ((((((" xsi:schemaLocation='http://www.opengis.net/sld StyledLayerDescriptor.xsd' " + " xmlns='http://www.opengis.net/sld' ") + " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>") + "  <NamedLayer>") + "    <Name>Style</Name>") + "  </NamedLayer>") + "</StyledLayerDescriptor>");
        // tester.debugComponentTrees();
        tester.newFormTester("styleForm").setValue("styleEditor:editorContainer:editorParent:editor", xml);
        tester.executeAjaxEvent("validate", "click");
        tester.assertNoErrorMessage();
    }

    @Test
    public void testValidateEntityExpansion() throws Exception {
        String xml = IOUtils.toString(TestData.class.getResource("externalEntities.sld"), "UTF-8");
        // tester.debugComponentTrees();
        tester.newFormTester("styleForm").setValue("styleEditor:editorContainer:editorParent:editor", xml);
        tester.executeAjaxEvent("validate", "click");
        List<Serializable> messages = tester.getMessages(ERROR);
        Assert.assertEquals(1, messages.size());
        String message = messages.get(0).toString();
        Assert.assertThat(message, CoreMatchers.containsString("Entity resolution disallowed"));
        Assert.assertThat(message, CoreMatchers.containsString("/this/file/does/not/exist"));
    }

    @Test
    public void testValidateNamedLayers() throws Exception {
        String xml = "<StyledLayerDescriptor version='1.0.0' " + (((((((((" xsi:schemaLocation='http://www.opengis.net/sld StyledLayerDescriptor.xsd' " + " xmlns='http://www.opengis.net/sld' ") + " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>") + "  <NamedLayer>\n") + "    <Name>Streams</Name>\n") + // Reference the Streams layer
        "  </NamedLayer>\n") + "  <NamedLayer>\n") + "    <Name>RoadSegments</Name>\n") + // 2nd, valid layer
        "  </NamedLayer>\n") + "</StyledLayerDescriptor>");
        tester.newFormTester("styleForm").setValue("styleEditor:editorContainer:editorParent:editor", xml);
        tester.executeAjaxEvent("validate", "click");
        tester.assertNoErrorMessage();
    }

    @Test
    public void testValidateNamedLayersInvalid() throws Exception {
        String xml = "<StyledLayerDescriptor version='1.0.0' " + (((((((((" xsi:schemaLocation='http://www.opengis.net/sld StyledLayerDescriptor.xsd' " + " xmlns='http://www.opengis.net/sld' ") + " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>") + "  <NamedLayer>\n") + "    <Name>Streams</Name>\n") + // Reference the Streams layer
        "  </NamedLayer>\n") + "  <NamedLayer>\n") + "    <Name>Stream</Name>\n") + // 2nd, invalid layer
        "  </NamedLayer>\n") + "</StyledLayerDescriptor>");
        tester.newFormTester("styleForm").setValue("styleEditor:editorContainer:editorParent:editor", xml);
        tester.executeAjaxEvent("validate", "click");
        tester.assertErrorMessages(new String[]{ "No layer or layer group named 'Stream' found in the catalog" });
    }

    /**
     * Test that while editing a style, the user can create and then discard a legend without ever
     * saving it.
     */
    @Test
    public void testDiscardNewLegendInfo() {
        tester.assertRenderedPage(StyleEditPage.class);
        tester.assertNoErrorMessage();
        // Show the legend panel (The test style does not initially have a legend)
        tester.executeAjaxEvent("styleForm:context:panel:legendPanel:externalGraphicContainer:showhide:show", "click");
        // Assert that the legend panel components exist
        tester.assertComponent("styleForm:context:panel:legendPanel", ExternalGraphicPanel.class);
        tester.assertComponent("styleForm:context:panel:legendPanel:externalGraphicContainer:list:onlineResource", TextField.class);
        tester.assertComponent("styleForm:context:panel:legendPanel:externalGraphicContainer:list:width", TextField.class);
        tester.assertComponent("styleForm:context:panel:legendPanel:externalGraphicContainer:list:height", TextField.class);
        tester.assertComponent("styleForm:context:panel:legendPanel:externalGraphicContainer:list:format", TextField.class);
        // Hide the legend panel (= "Discard Legend")
        tester.executeAjaxEvent("styleForm:context:panel:legendPanel:externalGraphicContainer:showhide:hide", "click");
        tester.assertNoErrorMessage();
        // Submit the style (no legend should be saved)
        tester.executeAjaxEvent("submit", "click");
        StyleInfo style = getCatalog().getStyleByName(BUILDINGS.getLocalPart());
        Assert.assertNotNull(style);
        Assert.assertNull(style.getLegend());
    }

    /**
     * Test that while editing a style, the user can discard a previously saved legend.
     */
    @Test
    public void testDiscardExistingLegend() throws IOException, URISyntaxException {
        // Create a legend for the style
        StyleInfo style = getCatalog().getStyleByName(BUILDINGS.getLocalPart());
        LegendInfo legendInfo = getCatalog().getFactory().createLegend();
        legendInfo.setFormat("jpg");
        legendInfo.setOnlineResource("test.jpg");
        legendInfo.setHeight(100);
        legendInfo.setWidth(100);
        style.setLegend(legendInfo);
        getCatalog().save(style);
        // Reload the page
        tester.startPage(new StyleEditPage(getCatalog().getStyleByName(BUILDINGS.getLocalPart())));
        // Make sure the legend fields exist and are populated as expected
        tester.assertComponent("styleForm:context:panel:legendPanel:externalGraphicContainer:list:onlineResource", TextField.class);
        tester.assertModelValue("styleForm:context:panel:legendPanel:externalGraphicContainer:list:onlineResource", "test.jpg");
        tester.assertComponent("styleForm:context:panel:legendPanel:externalGraphicContainer:list:width", TextField.class);
        tester.assertModelValue("styleForm:context:panel:legendPanel:externalGraphicContainer:list:width", 100);
        tester.assertComponent("styleForm:context:panel:legendPanel:externalGraphicContainer:list:height", TextField.class);
        tester.assertModelValue("styleForm:context:panel:legendPanel:externalGraphicContainer:list:height", 100);
        tester.assertComponent("styleForm:context:panel:legendPanel:externalGraphicContainer:list:format", TextField.class);
        tester.assertModelValue("styleForm:context:panel:legendPanel:externalGraphicContainer:list:format", "jpg");
        // Hide the legend panel (= "Discard Legend")
        tester.executeAjaxEvent("styleForm:context:panel:legendPanel:externalGraphicContainer:showhide:hide", "click");
        // Submit the form. (The legend should be discarded)
        FormTester form = tester.newFormTester("styleForm", false);
        form.submit();
        tester.assertNoErrorMessage();
        style = getCatalog().getStyleByName(BUILDINGS.getLocalPart());
        Assert.assertNotNull(style);
        Assert.assertNull(style.getLegend());
    }

    /**
     * Test that while editing a style, the user can discard a legend, even if the legend has
     * invalid values at the time, and then continue to save the style.
     */
    @Test
    public void testDiscardLegendWithBadValues() throws IOException, URISyntaxException {
        tester.executeAjaxEvent("styleForm:context:panel:legendPanel:externalGraphicContainer:showhide:show", "click");
        // Make sure the fields we are editing actually exist
        tester.assertComponent("styleForm:context:panel:legendPanel:externalGraphicContainer:list:onlineResource", TextField.class);
        tester.assertComponent("styleForm:context:panel:legendPanel:externalGraphicContainer:list:width", TextField.class);
        tester.assertComponent("styleForm:context:panel:legendPanel:externalGraphicContainer:list:height", TextField.class);
        tester.assertComponent("styleForm:context:panel:legendPanel:externalGraphicContainer:list:format", TextField.class);
        // Set some bad values for the legend
        FormTester form = tester.newFormTester("styleForm", false);
        form.setValue("context:panel:legendPanel:externalGraphicContainer:list:onlineResource", "missing.ong");
        form.setValue("context:panel:legendPanel:externalGraphicContainer:list:width", "-100");
        form.setValue("context:panel:legendPanel:externalGraphicContainer:list:height", "");
        form.setValue("context:panel:legendPanel:externalGraphicContainer:list:format", "bad/value");
        // Hide the legend panel (= "Discard Legend")
        tester.executeAjaxEvent("styleForm:context:panel:legendPanel:externalGraphicContainer:showhide:hide", "click");
        // Refresh the state of the FormTester after the executeAjaxEvent
        form = tester.newFormTester("styleForm", false);
        // Submit the form. (The bad legend values should no longer be set).
        form.submit();
        tester.assertNoErrorMessage();
        StyleInfo style = getCatalog().getStyleByName(BUILDINGS.getLocalPart());
        Assert.assertNotNull(style);
        Assert.assertNull(style.getLegend());
    }

    @Test
    public void applyThenSubmit() throws Exception {
        tester.executeAjaxEvent("apply", "click");
        tester.executeAjaxEvent("submit", "click");
        tester.assertNoErrorMessage();
    }

    @Test
    public void testLayerPreviewTab() {
        LayerInfo l = getCatalog().getLayers().get(0);
        Assert.assertFalse(((l.getDefaultStyle()) == (buildingsStyle)));
        // used to fail with an exception here because the template file cannot be found
        tester.executeAjaxEvent("styleForm:context:tabs-container:tabs:2:link", "click");
        print(tester.getLastRenderedPage(), true, true);
        tester.assertComponent("styleForm:context:panel", OpenLayersPreviewPanel.class);
    }

    @Test
    public void testLayerPreviewTabStyleGroup() {
        LayerInfo l = getCatalog().getLayers().get(0);
        Assert.assertFalse(((l.getDefaultStyle()) == (buildingsStyle)));
        // used to fail with an exception here because the template file cannot be found
        tester.executeAjaxEvent("styleForm:context:tabs-container:tabs:2:link", "click");
        tester.assertComponent("styleForm:context:panel", OpenLayersPreviewPanel.class);
        OpenLayersPreviewPanel previewPanel = ((OpenLayersPreviewPanel) (tester.getComponentFromLastRenderedPage("styleForm:context:panel")));
        Assert.assertFalse(previewPanel.isPreviewStyleGroup);
        FormTester form = tester.newFormTester("styleForm");
        form.setValue("context:panel:previewStyleGroup", true);
        form.submit();
        Assert.assertTrue(previewPanel.isPreviewStyleGroup);
    }

    @Test
    public void testRenameDefaultStyle() {
        StyleInfo styleInfo = new StyleInfoImpl(null);
        styleInfo.setName("point");
        styleInfo.setFilename("test.sld");
        GeoServerApplication app = ((GeoServerApplication) (applicationContext.getBean("webApplication")));
        WicketTester styleTest = new WicketTester(app, false);
        StyleEditPage page = new StyleEditPage(styleInfo);
        styleTest.startPage(page);
        styleTest.assertDisabled("styleForm:context:panel:name");
    }

    @Test
    public void testChangeWsDefaultStyle() {
        StyleInfo styleInfo = new StyleInfoImpl(null);
        styleInfo.setName("point");
        styleInfo.setFilename("test.sld");
        GeoServerApplication app = ((GeoServerApplication) (applicationContext.getBean("webApplication")));
        WicketTester styleTest = new WicketTester(app, false);
        StyleEditPage page = new StyleEditPage(styleInfo);
        styleTest.startPage(page);
        styleTest.assertDisabled("styleForm:context:panel:workspace");
    }

    @Test
    public void testPreviewSLD11Legend() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + (((((((((((((((((("<StyledLayerDescriptor xmlns=\"http://www.opengis.net/sld\" version=\"1.1.0\" " + "xmlns:se=\"http://www.opengis.net/se\">\n") + "  <NamedLayer>\n") + "    <se:Name>ne_110m_admin_0_countries</se:Name>\n") + "    <UserStyle>\n") + "      <se:Name>ne_110m_admin_0_countries</se:Name>\n") + "      <se:FeatureTypeStyle>\n") + "        <se:Rule>\n") + "          <se:Name>Single symbol</se:Name>\n") + "          <se:PolygonSymbolizer>\n") + "            <se:Fill>\n") + "              <se:SvgParameter name=\"fill\">#ff0000</se:SvgParameter>\n") + "            </se:Fill>\n") + "          </se:PolygonSymbolizer>\n") + "        </se:Rule>\n") + "      </se:FeatureTypeStyle>\n") + "    </UserStyle>\n") + "  </NamedLayer>\n") + "</StyledLayerDescriptor>");
        // tester.debugComponentTrees();
        tester.newFormTester("styleForm").setValue("styleEditor:editorContainer:editorParent:editor", xml);
        tester.clickLink("styleForm:context:panel:preview", true);
        StyleAdminPanel panel = ((StyleAdminPanel) (tester.getComponentFromLastRenderedPage("styleForm:context:panel")));
        // check the SvgParameter has been interpreted and we get a red fill, not a gray one
        assertPixel(panel.legendImage, 10, 10, Color.RED);
    }
}

