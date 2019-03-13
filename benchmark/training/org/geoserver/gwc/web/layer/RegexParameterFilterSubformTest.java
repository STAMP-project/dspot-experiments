/**
 * (c) 2015 - 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.web.layer;


import Case.UPPER;
import java.util.Locale;
import org.apache.wicket.markup.html.form.AbstractSingleSelectChoice;
import org.apache.wicket.markup.html.form.AbstractTextComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geowebcache.filter.parameters.CaseNormalizer.Case;
import org.geowebcache.filter.parameters.RegexParameterFilter;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class RegexParameterFilterSubformTest extends GeoServerWicketTestSupport {
    private IModel<RegexParameterFilter> model;

    private RegexParameterFilter pf;

    @Test
    public void testPageLoad() {
        startPage();
        tester.assertComponent("form:panel:defaultValue", AbstractTextComponent.class);
        tester.assertComponent("form:panel:regex", AbstractTextComponent.class);
        tester.assertComponent("form:panel:normalize", CaseNormalizerSubform.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testLoadDefaultValues() {
        startPage();
        AbstractTextComponent<String> defaultValue = ((AbstractTextComponent<String>) (tester.getComponentFromLastRenderedPage("form:panel:defaultValue")));
        AbstractTextComponent<String> regex = ((AbstractTextComponent<String>) (tester.getComponentFromLastRenderedPage("form:panel:regex")));
        AbstractSingleSelectChoice<Case> kase = ((AbstractSingleSelectChoice<Case>) (tester.getComponentFromLastRenderedPage("form:panel:normalize:case")));
        AbstractSingleSelectChoice<Locale> locale = ((AbstractSingleSelectChoice<Locale>) (tester.getComponentFromLastRenderedPage("form:panel:normalize:locale")));
        Assert.assertThat(defaultValue.getValue(), Matchers.equalTo(""));
        Assert.assertThat(regex.getValue(), Matchers.equalTo(""));
        Assert.assertThat(kase.getValue(), Matchers.equalTo("NONE"));
        Assert.assertThat(locale.getValue(), Matchers.equalTo(""));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testLoadSpecifiedValues() {
        pf.setDefaultValue("testDefault");
        pf.setRegex("testRegex");
        pf.setNormalize(new org.geowebcache.filter.parameters.CaseNormalizer(Case.UPPER, Locale.CANADA));
        startPage();
        AbstractTextComponent<String> defaultValue = ((AbstractTextComponent<String>) (tester.getComponentFromLastRenderedPage("form:panel:defaultValue")));
        AbstractTextComponent<String> regex = ((AbstractTextComponent<String>) (tester.getComponentFromLastRenderedPage("form:panel:regex")));
        AbstractSingleSelectChoice<Case> kase = ((AbstractSingleSelectChoice<Case>) (tester.getComponentFromLastRenderedPage("form:panel:normalize:case")));
        AbstractSingleSelectChoice<Locale> locale = ((AbstractSingleSelectChoice<Locale>) (tester.getComponentFromLastRenderedPage("form:panel:normalize:locale")));
        Assert.assertThat(defaultValue.getValue(), Matchers.equalTo("testDefault"));
        Assert.assertThat(regex.getValue(), Matchers.equalTo("testRegex"));
        Assert.assertThat(kase.getValue(), Matchers.equalTo("UPPER"));
        Assert.assertThat(locale.getValue(), Matchers.equalTo("en_CA"));
    }

    @Test
    public void testChange() {
        startPage();
        FormTester formTester = tester.newFormTester("form");
        formTester.setValue("panel:defaultValue", "testDefault");
        formTester.setValue("panel:regex", "testRegex");
        formTester.setValue("panel:normalize:case", "UPPER");
        formTester.setValue("panel:normalize:locale", "en_CA");
        formTester.submit();
        Assert.assertThat(pf.getDefaultValue(), Matchers.equalTo("testDefault"));
        Assert.assertThat(pf.getRegex(), Matchers.equalTo("testRegex"));
        Assert.assertThat(pf.getNormalize(), Matchers.both(Matchers.hasProperty("case", Matchers.is(UPPER))).and(Matchers.hasProperty("locale", Matchers.is(Locale.CANADA))));
    }
}

