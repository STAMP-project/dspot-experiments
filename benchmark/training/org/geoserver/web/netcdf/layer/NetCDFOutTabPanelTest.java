/**
 * (c) 2015 - 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.netcdf.layer;


import NetCDFSettingsContainer.NETCDFOUT_KEY;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.web.ComponentBuilder;
import org.geoserver.web.FormTestPage;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.netcdf.DataPacking;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test class for the {@link NetCDFOutTabPanel}.
 */
public class NetCDFOutTabPanelTest extends GeoServerWicketTestSupport {
    /**
     * LayerInfo model
     */
    private Model<LayerInfo> layerModel;

    /**
     * CoverageInfo model
     */
    private Model<CoverageInfo> resourceModel;

    @Test
    public void testComponent() {
        login();
        // Opening the selected page
        tester.startPage(new FormTestPage(new ComponentBuilder() {
            private static final long serialVersionUID = -6705646666953650890L;

            public Component buildComponent(final String id) {
                return new NetCDFOutTabPanel(id, layerModel, resourceModel);
            }
        }));
        tester.assertComponent("form:panel", NetCDFOutTabPanel.class);
        // Checking Components and their default values
        tester.assertComponent("form:panel:netcdfeditor", NetCDFOutSettingsEditor.class);
        // getting component
        NetCDFOutSettingsEditor editor = ((NetCDFOutSettingsEditor) (tester.getComponentFromLastRenderedPage("form:panel:netcdfeditor")));
        // Getting Model Object
        NetCDFLayerSettingsContainer container = editor.getModelObject();
        // Ensure the model is not null
        Assert.assertNotNull(container);
        // Ensure the container is equal to the one inside the MetadataMap
        NetCDFLayerSettingsContainer actualContainer = resourceModel.getObject().getMetadata().get(NETCDFOUT_KEY, NetCDFLayerSettingsContainer.class);
        Assert.assertEquals(container, actualContainer);
        // Ensure the Shuffle Component value is correct
        tester.assertComponent("form:panel:netcdfeditor:container:shuffle", CheckBox.class);
        CheckBox shuffle = ((CheckBox) (tester.getComponentFromLastRenderedPage("form:panel:netcdfeditor:container:shuffle")));
        Assert.assertEquals(shuffle.getModelObject(), container.isShuffle());
        // Ensure the Copy Variable Attributes component value is correct
        tester.assertComponent("form:panel:netcdfeditor:container:copyAttributes", CheckBox.class);
        CheckBox copyAttributes = ((CheckBox) (tester.getComponentFromLastRenderedPage("form:panel:netcdfeditor:container:copyAttributes")));
        Assert.assertEquals(copyAttributes.getModelObject(), container.isCopyAttributes());
        // Ensure the Copy Global Attributes component value is correct
        tester.assertComponent("form:panel:netcdfeditor:container:copyGlobalAttributes", CheckBox.class);
        CheckBox copyGlobalAttributes = ((CheckBox) (tester.getComponentFromLastRenderedPage("form:panel:netcdfeditor:container:copyGlobalAttributes")));
        Assert.assertEquals(copyGlobalAttributes.getModelObject(), container.isCopyGlobalAttributes());
        // Ensure the Compression Component value is correct
        tester.assertComponent("form:panel:netcdfeditor:container:compressionLevel", TextField.class);
        TextField<Integer> compressionLevel = ((TextField<Integer>) (tester.getComponentFromLastRenderedPage("form:panel:netcdfeditor:container:compressionLevel")));
        Assert.assertEquals(compressionLevel.getModelObject().intValue(), container.getCompressionLevel());
        // Ensure the DataPacking Component value is correct
        tester.assertComponent("form:panel:netcdfeditor:container:dataPacking", DropDownChoice.class);
        DropDownChoice<DataPacking> dataPacking = ((DropDownChoice<DataPacking>) (tester.getComponentFromLastRenderedPage("form:panel:netcdfeditor:container:dataPacking")));
        Assert.assertEquals(dataPacking.getModelObject(), container.getDataPacking());
        FormTester formTester;
        formTester = tester.newFormTester("form");
        formTester.setValue("panel:netcdfeditor:container:standardName", "test-name");
        formTester.setValue("panel:netcdfeditor:container:uom", "test-uom");
        formTester.submit();
        tester.assertNoErrorMessage();
        Assert.assertEquals("test-name", actualContainer.getLayerName());
        Assert.assertEquals("test-uom", actualContainer.getLayerUOM());
        // add a global attribute
        formTester = tester.newFormTester("form");
        formTester.setValue("panel:netcdfeditor:container:newGlobalAttributeKey", "test-global-attribute");
        formTester.setValue("panel:netcdfeditor:container:newGlobalAttributeValue", "Test Global Attribute");
        tester.executeAjaxEvent("form:panel:netcdfeditor:container:addGlobalAttribute", "click");
        formTester.setValue("panel:netcdfeditor:container:compressionLevel", "0");
        formTester.submit();
        tester.assertNoErrorMessage();
        Assert.assertEquals(1, actualContainer.getGlobalAttributes().size());
        Assert.assertEquals("test-global-attribute", actualContainer.getGlobalAttributes().get(0).getKey());
        Assert.assertEquals("Test Global Attribute", actualContainer.getGlobalAttributes().get(0).getValue());
        // add a variable attribute
        formTester = tester.newFormTester("form");
        formTester.setValue("panel:netcdfeditor:container:newVariableAttributeKey", "test-variable-attribute");
        formTester.setValue("panel:netcdfeditor:container:newVariableAttributeValue", "Test Variable Attribute");
        tester.executeAjaxEvent("form:panel:netcdfeditor:container:addVariableAttribute", "click");
        formTester.setValue("panel:netcdfeditor:container:compressionLevel", "0");
        formTester.submit();
        tester.assertNoErrorMessage();
        Assert.assertEquals(1, actualContainer.getVariableAttributes().size());
        Assert.assertEquals("test-variable-attribute", actualContainer.getVariableAttributes().get(0).getKey());
        Assert.assertEquals("Test Variable Attribute", actualContainer.getVariableAttributes().get(0).getValue());
        // add an extra variable
        formTester = tester.newFormTester("form");
        formTester.setValue("panel:netcdfeditor:container:newExtraVariableSource", "reftime");
        formTester.setValue("panel:netcdfeditor:container:newExtraVariableOutput", "forecast_reference_time");
        formTester.setValue("panel:netcdfeditor:container:newExtraVariableDimensions", "time");
        tester.executeAjaxEvent("form:panel:netcdfeditor:container:addExtraVariable", "click");
        formTester.setValue("panel:netcdfeditor:container:compressionLevel", "0");
        formTester.submit();
        tester.assertNoErrorMessage();
        Assert.assertEquals(1, actualContainer.getExtraVariables().size());
        Assert.assertEquals("reftime", actualContainer.getExtraVariables().get(0).getSource());
        Assert.assertEquals("forecast_reference_time", actualContainer.getExtraVariables().get(0).getOutput());
        Assert.assertEquals("time", actualContainer.getExtraVariables().get(0).getDimensions());
    }
}

