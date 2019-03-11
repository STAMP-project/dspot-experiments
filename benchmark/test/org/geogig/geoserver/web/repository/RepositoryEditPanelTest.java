/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geogig.geoserver.web.repository;


import DropDownModel.DIRECTORY_CONFIG;
import DropDownModel.PG_CONFIG;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.util.tester.FormTester;
import org.geogig.geoserver.web.RepositoriesPage;
import org.geoserver.web.data.store.panel.TextParamPanel;
import org.junit.Test;


/**
 *
 */
public class RepositoryEditPanelTest extends CommonPanelTest {
    private static final String FORM_PREFIX = "panel:repoForm:";

    private static final String SETTINGS_PREFIX = (RepositoryEditPanelTest.FORM_PREFIX) + "repo:repositoryConfig:settingsContainer:";

    private static final String SAVE_LINK = (RepositoryEditPanelTest.FORM_PREFIX) + "save";

    private static final String FEEDBACK = (RepositoryEditPanelTest.FORM_PREFIX) + "feedback";

    @Test
    public void testPGAddWithEmptyFields() throws IOException {
        navigateToStartPage();
        // select PG config from the dropdown
        select(PG_CONFIG);
        // verify the PG config components are visible
        verifyPostgreSQLBackendComponents();
        // click the Save button
        tester.executeAjaxEvent(RepositoryEditPanelTest.SAVE_LINK, "click");
        tester.assertRenderedPage(getStartPageClass());
        // get the feedback panel
        FeedbackPanel c = ((FeedbackPanel) (tester.getComponentFromLastRenderedPage(RepositoryEditPanelTest.FEEDBACK)));
        List<FeedbackMessage> list = c.getFeedbackMessagesModel().getObject();
        // by default, 3 required fields will be emtpy: repo name, database and password
        List<String> expectedMsgs = Lists.newArrayList("Field 'Repository Name' is required.", "Field 'Database Name' is required.", "Field 'Password' is required.");
        assertFeedbackMessages(list, expectedMsgs);
    }

    @Test
    public void testDirectoryAddWithEmptyFields() throws IOException {
        navigateToStartPage();
        // select Directory from the dropdown
        select(DIRECTORY_CONFIG);
        // verify Directory config components are visible
        verifyDirectoryBackendComponents();
        // click the Save button
        tester.executeAjaxEvent(RepositoryEditPanelTest.SAVE_LINK, "click");
        tester.assertRenderedPage(getStartPageClass());
        // get the feedback panel
        FeedbackPanel c = ((FeedbackPanel) (tester.getComponentFromLastRenderedPage(RepositoryEditPanelTest.FEEDBACK)));
        List<FeedbackMessage> list = c.getFeedbackMessagesModel().getObject();
        // by default, repo parent directory and repo name will be empty
        List<String> expectedMsgs = Lists.newArrayList("Field 'Repository Name' is required.", "Field 'Parent directory' is required.");
        assertFeedbackMessages(list, expectedMsgs);
    }

    @Test
    public void testAddNewRocksDBRepo() throws IOException {
        navigateToStartPage();
        // select Directory from the dropdown
        select(DIRECTORY_CONFIG);
        // verify Directory config components are visible
        verifyDirectoryBackendComponents();
        // get the form
        FormTester formTester = tester.newFormTester(getFrom());
        // now set a name
        TextParamPanel repoNamePanel = ((TextParamPanel) (tester.getComponentFromLastRenderedPage(((RepositoryEditPanelTest.SETTINGS_PREFIX) + "repositoryNamePanel"))));
        formTester.setValue(repoNamePanel.getFormComponent(), "temp_repo");
        // and a directory
        TextField parentDirectory = ((TextField) (tester.getComponentFromLastRenderedPage(((RepositoryEditPanelTest.SETTINGS_PREFIX) + "parentDirectory:wrapper:wrapper_body:value"))));
        formTester.setValue(parentDirectory, temp.getRoot().getCanonicalPath());
        // click the Save button
        tester.executeAjaxEvent(RepositoryEditPanelTest.SAVE_LINK, "click");
        // get the page. It should be a RepositoriesPage if the SAVE was successful
        tester.assertRenderedPage(RepositoriesPage.class);
    }
}

