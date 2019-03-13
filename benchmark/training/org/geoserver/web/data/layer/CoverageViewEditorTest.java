/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.data.layer;


import CoverageView.CoverageBand;
import MockData.TASMANIA_BM;
import javax.xml.namespace.QName;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.catalog.CoverageView;
import org.geoserver.data.test.MockData;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class CoverageViewEditorTest extends GeoServerWicketTestSupport {
    private static QName TIME_RANGES = new QName(MockData.DEFAULT_URI, "timeranges", MockData.DEFAULT_PREFIX);

    @Test
    public void testSingleBandsIndexIsNotVisible() throws Exception {
        // perform the login as administrator
        login();
        // opening the new coverage view page
        CoverageViewNewPage newPage = new CoverageViewNewPage(MockData.DEFAULT_PREFIX, "timeranges", null, null);
        GeoServerWicketTestSupport.tester.startPage(newPage);
        GeoServerWicketTestSupport.tester.assertComponent("form:coverages:outputBandsChoice", ListMultipleChoice.class);
        // let's see if we have the correct components instantiated
        GeoServerWicketTestSupport.tester.assertComponent("form", Form.class);
        GeoServerWicketTestSupport.tester.assertComponent("form:name", TextField.class);
        GeoServerWicketTestSupport.tester.assertComponent("form:coverages", CoverageViewEditor.class);
        GeoServerWicketTestSupport.tester.assertComponent("form:coverages:coveragesChoice", ListMultipleChoice.class);
        GeoServerWicketTestSupport.tester.assertComponent("form:coverages:outputBandsChoice", ListMultipleChoice.class);
        GeoServerWicketTestSupport.tester.assertComponent("form:coverages:addBand", Button.class);
        // check the available bands names without any selected band
        CoverageViewEditor coverageViewEditor = ((CoverageViewEditor) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:coverages")));
        coverageViewEditor.setModelObject(null);
        ListMultipleChoice availableBands = ((ListMultipleChoice) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:coverages:coveragesChoice")));
        ListMultipleChoice selectedBands = ((ListMultipleChoice) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:coverages:outputBandsChoice")));
        // select the first band
        FormTester formTester = GeoServerWicketTestSupport.tester.newFormTester("form");
        formTester.selectMultiple("coverages:coveragesChoice", new int[]{ 0 });
        GeoServerWicketTestSupport.tester.executeAjaxEvent("form:coverages:addBand", "click");
        // check that the coverage name contains the band index
        Assert.assertThat(availableBands.getChoices().size(), Is.is(1));
        Assert.assertThat(availableBands.getChoices().get(0), Is.is("time_domainsRanges"));
        Assert.assertThat(selectedBands.getChoices().size(), Is.is(1));
        CoverageView.CoverageBand selectedBand = ((CoverageView.CoverageBand) (selectedBands.getChoices().get(0)));
        Assert.assertThat(selectedBand.getDefinition(), Is.is("time_domainsRanges"));
        // set a name and submit
        formTester.setValue("name", "bands_index_coverage_test");
        formTester.submit("save");
    }

    @Test
    public void testMultiBandsIndexIsVisible() throws Exception {
        // perform the login as administrator
        login();
        // opening the new coverage view page
        CoverageViewNewPage newPage = new CoverageViewNewPage(TASMANIA_BM.getPrefix(), TASMANIA_BM.getLocalPart(), null, null);
        GeoServerWicketTestSupport.tester.startPage(newPage);
        GeoServerWicketTestSupport.tester.assertComponent("form:coverages:outputBandsChoice", ListMultipleChoice.class);
        // check the available bands names without any selected band
        CoverageViewEditor coverageViewEditor = ((CoverageViewEditor) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:coverages")));
        coverageViewEditor.setModelObject(null);
        ListMultipleChoice availableBands = ((ListMultipleChoice) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:coverages:coveragesChoice")));
        ListMultipleChoice selectedBands = ((ListMultipleChoice) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:coverages:outputBandsChoice")));
        // select the first band
        FormTester formTester = GeoServerWicketTestSupport.tester.newFormTester("form");
        formTester.selectMultiple("coverages:coveragesChoice", new int[]{ 0 });
        GeoServerWicketTestSupport.tester.executeAjaxEvent("form:coverages:addBand", "click");
        // check that the coverage name contains the band index
        Assert.assertThat(availableBands.getChoices().size(), Is.is(3));
        Assert.assertThat(availableBands.getChoices().get(0), Is.is("tazbm@0"));
        Assert.assertThat(selectedBands.getChoices().size(), Is.is(1));
        CoverageView.CoverageBand selectedBand = ((CoverageView.CoverageBand) (selectedBands.getChoices().get(0)));
        Assert.assertThat(selectedBand.getDefinition(), Is.is("tazbm@0"));
        // set a name and submit
        formTester.setValue("name", "bands_index_coverage_test");
        formTester.submit("save");
    }
}

