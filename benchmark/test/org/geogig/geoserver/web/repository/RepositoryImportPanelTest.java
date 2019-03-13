/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geogig.geoserver.web.repository;


import DropDownModel.DIRECTORY_CONFIG;
import DropDownModel.PG_CONFIG;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.util.tester.FormTester;
import org.geogig.geoserver.web.RepositoriesPage;
import org.junit.Test;


/**
 *
 */
public class RepositoryImportPanelTest extends CommonPanelTest {
    private static final String FORM_PREFIX = "panel:repoForm:";

    private static final String SETTINGS_PREFIX = (RepositoryImportPanelTest.FORM_PREFIX) + "repo:settingsContainer:";

    private static final String IMPORT_LINK = (RepositoryImportPanelTest.FORM_PREFIX) + "import";

    private static final String FEEDBACK = (RepositoryImportPanelTest.FORM_PREFIX) + "feedback";

    @Test
    public void testPGImportMissingFields() throws IOException {
        navigateToStartPage();
        // select PG config from the dropdown
        select(PG_CONFIG);
        // verify the PG config components are visible
        verifyPostgreSQLBackendComponents();
        // click the Import button
        tester.executeAjaxEvent(RepositoryImportPanelTest.IMPORT_LINK, "click");
        tester.assertRenderedPage(getStartPageClass());
        // get the feedback panel
        FeedbackPanel c = ((FeedbackPanel) (tester.getComponentFromLastRenderedPage(RepositoryImportPanelTest.FEEDBACK)));
        List<FeedbackMessage> list = c.getFeedbackMessagesModel().getObject();
        // by default, 3 required fields will be emtpy: repo name, database and password
        List<String> expectedMsgs = Lists.newArrayList("Field 'Repository Name' is required.", "Field 'Database Name' is required.", "Field 'Password' is required.");
        assertFeedbackMessages(list, expectedMsgs);
    }

    @Test
    public void testDirectoryImportMissingFields() throws IOException {
        navigateToStartPage();
        // select Directory from the dropdown
        select(DIRECTORY_CONFIG);
        // verify Directory config components are visible
        verifyDirectoryBackendComponents();
        // click the Import button
        tester.executeAjaxEvent(RepositoryImportPanelTest.IMPORT_LINK, "click");
        tester.assertRenderedPage(getStartPageClass());
        // get the feedback panel
        FeedbackPanel c = ((FeedbackPanel) (tester.getComponentFromLastRenderedPage(RepositoryImportPanelTest.FEEDBACK)));
        List<FeedbackMessage> list = c.getFeedbackMessagesModel().getObject();
        // by default, repo directory will be empty
        List<String> expectedMsgs = Lists.newArrayList("Field 'Repository directory' is required.");
        assertFeedbackMessages(list, expectedMsgs);
    }

    @Test
    public void testImportExistingRocksDBRepo() throws IOException {
        navigateToStartPage();
        final File repoDir = setupExistingRocksDBRepo();
        // select Directory from the dropdown
        select(DIRECTORY_CONFIG);
        // verify Directory config components are visible
        verifyDirectoryBackendComponents();
        // get the form
        FormTester formTester = tester.newFormTester(getFrom());
        // and a directory
        TextField parentDirectory = ((TextField) (tester.getComponentFromLastRenderedPage(((RepositoryImportPanelTest.SETTINGS_PREFIX) + "repoDirectoryPanel:wrapper:wrapper_body:repoDirectory"))));
        formTester.setValue(parentDirectory, repoDir.getCanonicalPath());
        // click the Save button
        tester.executeAjaxEvent(RepositoryImportPanelTest.IMPORT_LINK, "click");
        // get the page. It should be a RepositoriesPage if the SAVE was successful
        tester.assertRenderedPage(RepositoriesPage.class);
    }
}

