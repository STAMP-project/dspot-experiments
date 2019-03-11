/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.web.layer;


import java.util.Arrays;
import java.util.List;
import org.apache.wicket.markup.html.form.AbstractTextComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geowebcache.filter.parameters.FloatParameterFilter;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class FloatParameterFilterSubformTest extends GeoServerWicketTestSupport {
    private IModel<FloatParameterFilter> model;

    private FloatParameterFilter pf;

    @Test
    public void testPageLoad() {
        startPage();
        tester.assertComponent("form:panel:defaultValue", AbstractTextComponent.class);
        tester.assertComponent("form:panel:values", AbstractTextComponent.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testLoadDefaultValues() {
        startPage();
        AbstractTextComponent<String> defaultValue = ((AbstractTextComponent<String>) (tester.getComponentFromLastRenderedPage("form:panel:defaultValue")));
        AbstractTextComponent<List<Float>> values = ((AbstractTextComponent<List<Float>>) (tester.getComponentFromLastRenderedPage("form:panel:values")));
        Assert.assertThat(defaultValue.getValue(), Matchers.equalTo(""));
        Assert.assertThat(values.getValue(), Matchers.equalTo(""));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testLoadSpecifiedValues() {
        pf.setDefaultValue("testDefault");
        pf.setValues(Arrays.asList(1.5F, 2.6F));
        startPage();
        AbstractTextComponent<String> defaultValue = ((AbstractTextComponent<String>) (tester.getComponentFromLastRenderedPage("form:panel:defaultValue")));
        AbstractTextComponent<List<Float>> values = ((AbstractTextComponent<List<Float>>) (tester.getComponentFromLastRenderedPage("form:panel:values")));
        Assert.assertThat(defaultValue.getValue(), Matchers.equalTo("testDefault"));
        Assert.assertThat(values.getValue(), Matchers.equalTo("1.5\r\n2.6"));
    }

    @Test
    public void testChange() {
        startPage();
        FormTester formTester = tester.newFormTester("form");
        formTester.setValue("panel:defaultValue", "testDefault");
        formTester.setValue("panel:values", "1.5\r\n2.6");
        formTester.submit();
        Assert.assertThat(pf.getDefaultValue(), Matchers.equalTo("testDefault"));
        Assert.assertThat(pf.getValues(), Matchers.contains(1.5F, 2.6F));
    }
}

