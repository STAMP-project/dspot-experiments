/**
 * (c) 2019 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.importer.web;


import FormTestPage.FORM;
import java.io.Serializable;
import java.util.Map;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.importer.Database;
import org.geoserver.web.ComponentBuilder;
import org.geoserver.web.FormTestPage;
import org.geoserver.web.GeoServerWicketUnitTestSupport;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class AbstractDBPanelTest extends GeoServerWicketUnitTestSupport {
    @Test
    public void testSubmitParameters() {
        FormTestPage testPage = new FormTestPage(((ComponentBuilder) (( id) -> new PostGISPanel(id))));
        tester.startPage(testPage);
        // set the username
        FormTester formTester = this.tester.newFormTester(FORM);
        formTester.setValue("panel:form:paramPanelContainer:paramPanels:01:username", "testUser");
        formTester.submit();
        PostGISPanel postgisPanel = ((PostGISPanel) (testPage.get("form:panel")));
        Database database = ((Database) (postgisPanel.createImportSource()));
        Map<String, Serializable> parameters = database.getParameters();
        MatcherAssert.assertThat(parameters, Matchers.hasEntry("user", "testUser"));
    }
}

