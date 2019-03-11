/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.importer.web;


import ImportTaskTable.AdvancedOptionPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.geoserver.importer.ImportContext;
import org.geoserver.importer.ImportData;
import org.geoserver.importer.ImportTask;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.wicket.GeoServerDataProvider;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 *
 *
 * @author Kevin Smith, Boundless
 */
public class ImportTaskTableTest extends GeoServerWicketTestSupport {
    private ImportData data;

    private ImportContext context;

    private GeoServerDataProvider<ImportTask> provider;

    private FeedbackPanel feedback;

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void testTwoCRSSetByFindThenApply() {
        tester.assertComponent("taskTable", ImportTaskTable.class);
        // Click the Find CRS button for the first layer to import
        tester.clickLink("taskTable:listContainer:items:1:itemProperties:2:component:form:crs:find", true);
        // Select the first CRS
        tester.clickLink("taskTable:listContainer:items:1:itemProperties:2:component:form:crs:popup:content:table:listContainer:items:1:itemProperties:0:component:link", true);
        // Click the Find CRS button for the second layer to import
        tester.clickLink("taskTable:listContainer:items:2:itemProperties:2:component:form:crs:find", true);
        // Select the first CRS
        tester.clickLink("taskTable:listContainer:items:2:itemProperties:2:component:form:crs:popup:content:table:listContainer:items:2:itemProperties:0:component:link", true);
        // The EPSG codes should be set
        tester.assertModelValue("taskTable:listContainer:items:1:itemProperties:2:component:form:crs:srs", "EPSG:2000");
        tester.assertModelValue("taskTable:listContainer:items:2:itemProperties:2:component:form:crs:srs", "EPSG:2001");
        // Check that the WKT links set
        tester.assertModelValue("taskTable:listContainer:items:1:itemProperties:2:component:form:crs:wkt:wktLabel", "EPSG:Anguilla 1957 / British West Indies Grid");
        tester.assertModelValue("taskTable:listContainer:items:2:itemProperties:2:component:form:crs:wkt:wktLabel", "EPSG:Antigua 1943 / British West Indies Grid");
        // Apply the first
        tester.clickLink("taskTable:listContainer:items:1:itemProperties:2:component:form:apply", true);
        // The first entry should be replaced with an "Advanced" link, the numbering continues from
        // those used before so the second item is 3
        tester.assertComponent("taskTable:listContainer:items:3:itemProperties:2:component", AdvancedOptionPanel.class);
        // The second (4) should still be set
        tester.assertModelValue("taskTable:listContainer:items:4:itemProperties:2:component:form:crs:srs", "EPSG:2001");
    }

    @Test
    public void testTwoCRSSetManuallyThenApply() {
        tester.assertComponent("taskTable", ImportTaskTable.class);
        // "Type" in the EPSG codes
        fill("taskTable:listContainer:items:1:itemProperties:2:component:form", "crs:srs", "EPSG:3857");
        fill("taskTable:listContainer:items:2:itemProperties:2:component:form", "crs:srs", "EPSG:4326");
        // Check that the WKT links set
        tester.assertModelValue("taskTable:listContainer:items:1:itemProperties:2:component:form:crs:wkt:wktLabel", "EPSG:WGS 84 / Pseudo-Mercator");
        tester.assertModelValue("taskTable:listContainer:items:2:itemProperties:2:component:form:crs:wkt:wktLabel", "EPSG:WGS 84");
        // Apply the first
        tester.clickLink("taskTable:listContainer:items:1:itemProperties:2:component:form:apply", true);
        // The first entry should be replaced with an "Advanced" link, the numbering continues from
        // those used before so the second item is 3
        tester.assertComponent("taskTable:listContainer:items:3:itemProperties:2:component", AdvancedOptionPanel.class);
        // The second (4) should still be set
        tester.assertModelValue("taskTable:listContainer:items:4:itemProperties:2:component:form:crs:srs", "EPSG:4326");
    }
}

