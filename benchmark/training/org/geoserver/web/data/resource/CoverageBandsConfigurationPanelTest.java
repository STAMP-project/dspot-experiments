package org.geoserver.web.data.resource;


import javax.xml.namespace.QName;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.model.Model;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.data.test.SystemTestData;
import org.geoserver.web.ComponentBuilder;
import org.geoserver.web.FormTestPage;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class CoverageBandsConfigurationPanelTest extends GeoServerWicketTestSupport {
    public static QName HYPER = new QName(SystemTestData.WCS_URI, "Hyper", SystemTestData.WCS_PREFIX);

    @Test
    public void testBands() throws Exception {
        CoverageInfo coverage = getCatalog().getCoverageByName(getLayerId(SystemTestData.TASMANIA_BM));
        Model<CoverageInfo> model = new Model(coverage);
        FormTestPage page = new FormTestPage(( id) -> new CoverageBandsConfigurationPanel(id, model));
        GeoServerWicketTestSupport.tester.startPage(page);
        MarkupContainer container = ((MarkupContainer) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:panel:bands:listContainer:items")));
        Assert.assertEquals(3, container.size());
    }

    @Test
    public void testHyperspectral() throws Exception {
        CoverageInfo coverage = getCatalog().getCoverageByName(getLayerId(CoverageBandsConfigurationPanelTest.HYPER));
        Model<CoverageInfo> model = new Model(coverage);
        FormTestPage page = new FormTestPage(( id) -> new CoverageBandsConfigurationPanel(id, model));
        GeoServerWicketTestSupport.tester.startPage(page);
        MarkupContainer container = ((MarkupContainer) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:panel:bands:listContainer:items")));
        // set to non pageable, we don't have support for editing a paging table right now
        Assert.assertEquals(326, container.size());
    }
}

