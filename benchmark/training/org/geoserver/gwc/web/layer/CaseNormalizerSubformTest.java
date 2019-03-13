/**
 * (c) 2015 - 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.web.layer;


import Case.NONE;
import Case.UPPER;
import java.util.Locale;
import org.apache.wicket.markup.html.form.AbstractSingleSelectChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geowebcache.filter.parameters.CaseNormalizer;
import org.geowebcache.filter.parameters.CaseNormalizer.Case;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CaseNormalizerSubformTest extends GeoServerWicketTestSupport {
    private IModel<CaseNormalizer> model;

    private CaseNormalizer cn;

    @SuppressWarnings("unchecked")
    @Test
    public void testPageLoad() {
        startPage();
        tester.assertComponent("form:panel:case", AbstractSingleSelectChoice.class);
        tester.assertComponent("form:panel:locale", AbstractSingleSelectChoice.class);
        AbstractSingleSelectChoice<Case> kase = ((AbstractSingleSelectChoice<Case>) (tester.getComponentFromLastRenderedPage("form:panel:case")));
        AbstractSingleSelectChoice<Locale> locale = ((AbstractSingleSelectChoice<Locale>) (tester.getComponentFromLastRenderedPage("form:panel:locale")));
        Assert.assertThat(kase.isNullValid(), Matchers.is(false));
        Assert.assertThat(locale.isNullValid(), Matchers.is(true));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testLoadDefaultValues() {
        startPage();
        AbstractSingleSelectChoice<Case> kase = ((AbstractSingleSelectChoice<Case>) (tester.getComponentFromLastRenderedPage("form:panel:case")));
        AbstractSingleSelectChoice<Locale> locale = ((AbstractSingleSelectChoice<Locale>) (tester.getComponentFromLastRenderedPage("form:panel:locale")));
        Assert.assertThat(kase.getValue(), Matchers.equalTo("NONE"));
        Assert.assertThat(locale.getValue(), Matchers.equalTo(""));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testLoadSpecifiedValues() {
        cn = new CaseNormalizer(Case.UPPER, Locale.CANADA);
        model = Model.of(cn);
        startPage();
        AbstractSingleSelectChoice<Case> kase = ((AbstractSingleSelectChoice<Case>) (tester.getComponentFromLastRenderedPage("form:panel:case")));
        AbstractSingleSelectChoice<Locale> locale = ((AbstractSingleSelectChoice<Locale>) (tester.getComponentFromLastRenderedPage("form:panel:locale")));
        Assert.assertThat(kase.getValue(), Matchers.equalTo("UPPER"));
        Assert.assertThat(locale.getValue(), Matchers.equalTo("en_CA"));
    }

    @Test
    public void testChangeFromDefault() {
        startPage();
        FormTester formTester = tester.newFormTester("form");
        formTester.setValue("panel:case", "UPPER");
        formTester.setValue("panel:locale", "en_CA");
        formTester.submit();
        Assert.assertThat(cn.getCase(), Matchers.is(UPPER));
        Assert.assertThat(cn.getConfiguredLocale(), Matchers.is(Locale.CANADA));
    }

    @Test
    public void testChange() {
        cn = new CaseNormalizer(Case.LOWER, Locale.TAIWAN);
        model = Model.of(cn);
        startPage();
        FormTester formTester = tester.newFormTester("form");
        formTester.setValue("panel:case", "UPPER");
        formTester.setValue("panel:locale", "en_CA");
        formTester.submit();
        Assert.assertThat(cn.getCase(), Matchers.is(UPPER));
        Assert.assertThat(cn.getConfiguredLocale(), Matchers.is(Locale.CANADA));
    }

    @Test
    public void testChangeToDefault() {
        cn = new CaseNormalizer(Case.LOWER, Locale.TAIWAN);
        model = Model.of(cn);
        startPage();
        FormTester formTester = tester.newFormTester("form");
        formTester.setValue("panel:case", "NONE");
        formTester.setValue("panel:locale", "-1");
        formTester.submit();
        Assert.assertThat(cn.getCase(), Matchers.is(NONE));
        Assert.assertThat(cn.getConfiguredLocale(), Matchers.nullValue());
        Assert.assertThat(cn.getLocale(), Matchers.instanceOf(Locale.class));
    }
}

