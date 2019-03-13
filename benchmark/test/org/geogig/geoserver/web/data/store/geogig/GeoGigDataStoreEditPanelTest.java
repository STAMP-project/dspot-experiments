/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geogig.geoserver.web.data.store.geogig;


import GeoGigDataStoreFactory.DISPLAY_NAME;
import GeoGigDataStoreFactory.REPOSITORY.key;
import com.google.common.base.Suppliers;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.apache.wicket.Page;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.util.tester.FormTester;
import org.geogig.geoserver.config.GeoServerGeoGigRepositoryResolver;
import org.geogig.geoserver.config.RepositoryInfo;
import org.geogig.geoserver.config.RepositoryManager;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.web.GeoServerApplication;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.data.store.StoreEditPanel;
import org.geoserver.web.data.store.StoreExtensionPoints;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.geogig.model.ObjectId;
import org.locationtech.geogig.model.Ref;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class GeoGigDataStoreEditPanelTest extends GeoServerWicketTestSupport {
    private static final String BASE = "dataStoreForm:parametersPanel";

    private Page page;

    private DataStoreInfo storeInfo;

    private Form<DataStoreInfo> editForm;

    private RepositoryManager mockManager;

    @Test
    public void testExtensionPoint() {
        storeInfo = getCatalog().getFactory().createDataStore();
        storeInfo.setType(DISPLAY_NAME);
        editForm = new Form("formid");
        editForm.setModel(new org.apache.wicket.model.Model(storeInfo));
        GeoServerApplication app = getGeoServerApplication();
        StoreEditPanel storeEditPanel = StoreExtensionPoints.getStoreEditPanel("id", editForm, storeInfo, app);
        Assert.assertNotNull(storeEditPanel);
        Assert.assertTrue((storeEditPanel instanceof GeoGigDataStoreEditPanel));
    }

    @Test
    public void testStartupForNew() {
        startPanelForNewStore();
        assertCommonComponents();
    }

    @Test
    public void testStartupForEdit() {
        startPanelToEditStore();
        assertCommonComponents();
        tester.assertModelValue(((GeoGigDataStoreEditPanelTest.BASE) + ":branch:branchDropDown"), "alpha");
    }

    @Test
    public void testRefreshBranchListWithBadConnectionParams() throws Exception {
        startPanelForNewStore();
        editForm.getModelObject().getConnectionParameters().put(key, GeoServerGeoGigRepositoryResolver.getURI("dummy_repo_2"));
        final FormTester formTester = tester.newFormTester("dataStoreForm");
        BranchSelectionPanel branchPanel = getComponent(((GeoGigDataStoreEditPanelTest.BASE) + ":branch"), BranchSelectionPanel.class);
        RepositoryInfo repoInfo = Mockito.mock(RepositoryInfo.class);
        Mockito.when(repoInfo.getId()).thenReturn(UUID.randomUUID().toString());
        Mockito.when(mockManager.getByRepoName("dummy_repo_2")).thenReturn(repoInfo);
        Mockito.when(mockManager.listBranches(ArgumentMatchers.anyString())).thenThrow(new IOException("Could not connect"));
        branchPanel.setRepositoryManager(Suppliers.ofInstance(mockManager));
        String submitLink = (GeoGigDataStoreEditPanelTest.BASE) + ":branch:refresh";
        tester.executeAjaxEvent(submitLink, "click");
        FeedbackMessage feedbackMessage = formTester.getForm().getFeedbackMessages().first();
        Assert.assertNotNull(feedbackMessage);
        Serializable message = feedbackMessage.getMessage();
        Assert.assertNotNull(message);
        String expectedMessage = "Could not list branches: Could not connect";
        Assert.assertEquals(expectedMessage, message.toString());
    }

    @Test
    public void testRefreshBranchList() throws Exception {
        startPanelForNewStore();
        editForm.getModelObject().getConnectionParameters().put(key, GeoServerGeoGigRepositoryResolver.getURI("dummy_repo_3"));
        final FormTester formTester = tester.newFormTester("dataStoreForm");
        BranchSelectionPanel branchPanel = getComponent(((GeoGigDataStoreEditPanelTest.BASE) + ":branch"), BranchSelectionPanel.class);
        Assert.assertNotNull(branchPanel);
        ObjectId dummyId = hashString("dummy");
        final List<Ref> branches = Arrays.asList(new Ref("master", dummyId), new Ref("alpha", dummyId), new Ref("sandbox", dummyId));
        RepositoryInfo repoInfo = Mockito.mock(RepositoryInfo.class);
        Mockito.when(repoInfo.getId()).thenReturn(UUID.randomUUID().toString());
        branchPanel.setRepositoryManager(Suppliers.ofInstance(mockManager));
        Mockito.when(mockManager.getByRepoName("dummy_repo_3")).thenReturn(repoInfo);
        Mockito.when(mockManager.listBranches(ArgumentMatchers.anyString())).thenReturn(branches);
        String dropDownPath = (GeoGigDataStoreEditPanelTest.BASE) + ":branch:branchDropDown";
        final DropDownChoice choice = getComponent(dropDownPath, DropDownChoice.class);
        Assert.assertTrue(choice.getChoices().isEmpty());
        String submitLink = (GeoGigDataStoreEditPanelTest.BASE) + ":branch:refresh";
        tester.executeAjaxEvent(submitLink, "click");
        FeedbackMessage feedbackMessage = formTester.getForm().getFeedbackMessages().first();
        Assert.assertNull(feedbackMessage);
        Assert.assertEquals(Arrays.asList("master", "alpha", "sandbox"), choice.getChoices());
    }
}

