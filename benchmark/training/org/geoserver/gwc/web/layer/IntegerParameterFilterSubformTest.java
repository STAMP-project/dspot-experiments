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
import org.geowebcache.filter.parameters.IntegerParameterFilter;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class IntegerParameterFilterSubformTest extends GeoServerWicketTestSupport {
    private IModel<IntegerParameterFilter> model;

    private IntegerParameterFilter pf;

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
        AbstractTextComponent<List<Integer>> values = ((AbstractTextComponent<List<Integer>>) (tester.getComponentFromLastRenderedPage("form:panel:values")));
        Assert.assertThat(defaultValue.getValue(), Matchers.equalTo(""));
        Assert.assertThat(values.getValue(), Matchers.equalTo(""));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testLoadSpecifiedValues() {
        pf.setDefaultValue("testDefault");
        pf.setValues(Arrays.asList(1, 2));
        startPage();
        AbstractTextComponent<String> defaultValue = ((AbstractTextComponent<String>) (tester.getComponentFromLastRenderedPage("form:panel:defaultValue")));
        AbstractTextComponent<List<Integer>> values = ((AbstractTextComponent<List<Integer>>) (tester.getComponentFromLastRenderedPage("form:panel:values")));
        Assert.assertThat(defaultValue.getValue(), Matchers.equalTo("testDefault"));
        Assert.assertThat(values.getValue(), Matchers.equalTo("1\r\n2"));
    }

    @Test
    public void testChange() {
        startPage();
        FormTester formTester = tester.newFormTester("form");
        formTester.setValue("panel:defaultValue", "testDefault");
        formTester.setValue("panel:values", "1\r\n2");
        formTester.submit();
        Assert.assertThat(pf.getDefaultValue(), Matchers.equalTo("testDefault"));
        Assert.assertThat(pf.getValues(), Matchers.contains(1, 2));
    }
}

