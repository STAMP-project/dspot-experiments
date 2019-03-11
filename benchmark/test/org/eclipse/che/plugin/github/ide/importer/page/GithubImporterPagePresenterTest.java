/**
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.plugin.github.ide.importer.page;


import MutableProjectConfig.MutableSourceStorage;
import Wizard.UpdateDelegate;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.che.api.project.shared.dto.ProjectImporterDescriptor;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentUser;
import org.eclipse.che.ide.api.auth.OAuthServiceClient;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.oauth.OAuth2Authenticator;
import org.eclipse.che.ide.api.oauth.OAuth2AuthenticatorRegistry;
import org.eclipse.che.ide.api.project.MutableProjectConfig;
import org.eclipse.che.ide.commons.exception.UnauthorizedException;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.plugin.github.ide.GitHubLocalizationConstant;
import org.eclipse.che.plugin.github.ide.GitHubServiceClient;
import org.eclipse.che.plugin.github.ide.load.ProjectData;
import org.eclipse.che.plugin.github.shared.GitHubUser;
import org.eclipse.che.plugin.ssh.key.client.SshKeyManager;
import org.eclipse.che.security.oauth.OAuthStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Testing {@link GithubImporterPagePresenter} functionality.
 *
 * @author Roman Nikitenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class GithubImporterPagePresenterTest {
    @Captor
    private ArgumentCaptor<AsyncCallback<OAuthStatus>> asyncCallbackCaptor;

    @Mock
    private UpdateDelegate updateDelegate;

    @Mock
    private DtoFactory dtoFactory;

    @Mock
    private GithubImporterPageView view;

    @Mock
    private GitHubServiceClient gitHubClientService;

    @Mock
    private GitHubLocalizationConstant locale;

    @Mock
    private MutableProjectConfig dataObject;

    @Mock
    private MutableSourceStorage source;

    @Mock
    private GitHubUser gitHubUser;

    @Mock
    private PromiseError promiseError;

    @Mock
    private OAuth2Authenticator gitHubAuthenticator;

    @Mock
    private OAuth2AuthenticatorRegistry gitHubAuthenticatorRegistry;

    @Mock
    private AppContext appContext;

    @Mock
    private OAuthServiceClient oAuthServiceClient;

    @Mock
    private NotificationManager notificationManager;

    @Mock
    private DialogFactory dialogFactory;

    @Mock
    private SshKeyManager sshKeyManager;

    private GithubImporterPagePresenter presenter;

    @Test
    public void delegateShouldBeSet() throws Exception {
        Mockito.verify(view).setDelegate(ArgumentMatchers.any(GithubImporterPagePresenter.class));
    }

    @Test
    public void testGo() throws Exception {
        String importerDescription = "description";
        AcceptsOneWidget container = Mockito.mock(AcceptsOneWidget.class);
        ProjectImporterDescriptor projectImporter = Mockito.mock(ProjectImporterDescriptor.class);
        // when(wizardContext.getData(ImportProjectWizard.PROJECT_IMPORTER)).thenReturn(projectImporter);
        Mockito.when(projectImporter.getDescription()).thenReturn(importerDescription);
        presenter.go(container);
        Mockito.verify(view).setInputsEnableState(ArgumentMatchers.eq(true));
        Mockito.verify(container).setWidget(ArgumentMatchers.eq(view));
        Mockito.verify(view).focusInUrlInput();
    }

    @Test
    public void onLoadRepoClickedWhenGetUserReposIsSuccessful() throws Exception {
        Mockito.when(view.getAccountName()).thenReturn("AccountName");
        presenter.onLoadRepoClicked();
        Mockito.verify(gitHubClientService).getRepositoriesList(ArgumentMatchers.anyString());
        Mockito.verify(gitHubClientService).getUserInfo(ArgumentMatchers.anyString());
        Mockito.verify(gitHubClientService).getOrganizations(ArgumentMatchers.anyString());
        Mockito.verify(view).setLoaderVisibility(ArgumentMatchers.eq(true));
        Mockito.verify(view).setInputsEnableState(ArgumentMatchers.eq(false));
        Mockito.verify(view).setLoaderVisibility(ArgumentMatchers.eq(false));
        Mockito.verify(view).setInputsEnableState(ArgumentMatchers.eq(true));
        Mockito.verify(view).setAccountNames(ArgumentMatchers.<Set>any());
        Mockito.verify(view, Mockito.times(2)).showGithubPanel();
        Mockito.verify(view).setRepositories(ArgumentMatchers.any());
        Mockito.verify(view).reset();
    }

    @Test
    public void onLoadRepoClickedWhenGetUserReposIsFailed() throws Exception {
        presenter.onLoadRepoClicked();
        Mockito.verify(gitHubClientService).getRepositoriesList(ArgumentMatchers.anyString());
        Mockito.verify(gitHubClientService).getUserInfo(ArgumentMatchers.anyString());
        Mockito.verify(gitHubClientService).getOrganizations(ArgumentMatchers.anyString());
        Mockito.verify(view).setLoaderVisibility(ArgumentMatchers.eq(true));
        Mockito.verify(view).setInputsEnableState(ArgumentMatchers.eq(false));
        Mockito.verify(view).setLoaderVisibility(ArgumentMatchers.eq(false));
        Mockito.verify(view).setInputsEnableState(ArgumentMatchers.eq(true));
        Mockito.verify(view, Mockito.never()).setAccountNames(ArgumentMatchers.any());
        Mockito.verify(view, Mockito.never()).showGithubPanel();
        Mockito.verify(view, Mockito.never()).setRepositories(ArgumentMatchers.any());
    }

    @Test
    public void onRepositorySelectedTest() {
        ProjectData projectData = new ProjectData("name", "description", "type", Collections.emptyList(), "repoUrl", "readOnlyUrl", "httpTransportUrl", false);
        presenter.onRepositorySelected(projectData);
        Mockito.verify(dataObject).setName(ArgumentMatchers.eq("name"));
        Mockito.verify(dataObject).setDescription(ArgumentMatchers.eq("description"));
        Mockito.verify(source).setLocation(ArgumentMatchers.eq("repoUrl"));
        Mockito.verify(view).setProjectName(ArgumentMatchers.anyString());
        Mockito.verify(view).setProjectDescription(ArgumentMatchers.anyString());
        Mockito.verify(view).setProjectUrl(ArgumentMatchers.anyString());
        Mockito.verify(updateDelegate).updateControls();
    }

    @Test
    public void projectUrlStartWithWhiteSpaceEnteredTest() {
        String incorrectUrl = " https://github.com/codenvy/ide.git";
        Mockito.when(view.getProjectName()).thenReturn("");
        presenter.onProjectUrlChanged(incorrectUrl);
        Mockito.verify(view).markURLInvalid();
        Mockito.verify(view).setURLErrorMessage(ArgumentMatchers.eq(locale.importProjectMessageStartWithWhiteSpace()));
        Mockito.verify(source).setLocation(ArgumentMatchers.eq(incorrectUrl));
        Mockito.verify(view).setProjectName(ArgumentMatchers.anyString());
        Mockito.verify(updateDelegate).updateControls();
    }

    @Test
    public void testUrlMatchScpLikeSyntax() {
        // test for url with an alternative scp-like syntax: [user@]host.xz:path/to/repo.git/
        String correctUrl = "host.xz:path/to/repo.git";
        Mockito.when(view.getProjectName()).thenReturn("");
        presenter.onProjectUrlChanged(correctUrl);
        verifyInvocationsForCorrectUrl(correctUrl);
    }

    @Test
    public void testUrlWithoutUsername() {
        String correctUrl = "git@hostname.com:projectName.git";
        Mockito.when(view.getProjectName()).thenReturn("");
        presenter.onProjectUrlChanged(correctUrl);
        verifyInvocationsForCorrectUrl(correctUrl);
    }

    @Test
    public void testSshUriWithHostBetweenDoubleSlashAndSlash() {
        // Check for type uri which start with ssh:// and has host between // and /
        String correctUrl = "ssh://host.com/some/path";
        Mockito.when(view.getProjectName()).thenReturn("");
        presenter.onProjectUrlChanged(correctUrl);
        verifyInvocationsForCorrectUrl(correctUrl);
    }

    @Test
    public void testSshUriWithHostBetweenDoubleSlashAndColon() {
        // Check for type uri with host between // and :
        String correctUrl = "ssh://host.com:port/some/path";
        Mockito.when(view.getProjectName()).thenReturn("");
        presenter.onProjectUrlChanged(correctUrl);
        verifyInvocationsForCorrectUrl(correctUrl);
    }

    @Test
    public void testGitUriWithHostBetweenDoubleSlashAndSlash() {
        // Check for type uri which start with git:// and has host between // and /
        String correctUrl = "git://host.com/user/repo";
        Mockito.when(view.getProjectName()).thenReturn("");
        presenter.onProjectUrlChanged(correctUrl);
        verifyInvocationsForCorrectUrl(correctUrl);
    }

    @Test
    public void testSshUriWithHostBetweenAtAndColon() {
        // Check for type uri with host between @ and :
        String correctUrl = "user@host.com:login/repo";
        Mockito.when(view.getProjectName()).thenReturn("");
        presenter.onProjectUrlChanged(correctUrl);
        verifyInvocationsForCorrectUrl(correctUrl);
    }

    @Test
    public void testSshUriWithHostBetweenAtAndSlash() {
        // Check for type uri with host between @ and /
        String correctUrl = "ssh://user@host.com/some/path";
        Mockito.when(view.getProjectName()).thenReturn("");
        presenter.onProjectUrlChanged(correctUrl);
        verifyInvocationsForCorrectUrl(correctUrl);
    }

    @Test
    public void projectUrlWithIncorrectProtocolEnteredTest() {
        String correctUrl = "htps://github.com/codenvy/ide.git";
        Mockito.when(view.getProjectName()).thenReturn("");
        presenter.onProjectUrlChanged(correctUrl);
        Mockito.verify(view).markURLInvalid();
        Mockito.verify(view).setURLErrorMessage(ArgumentMatchers.eq(locale.importProjectMessageProtocolIncorrect()));
        Mockito.verify(source).setLocation(ArgumentMatchers.eq(correctUrl));
        Mockito.verify(view).setProjectName(ArgumentMatchers.anyString());
        Mockito.verify(updateDelegate).updateControls();
    }

    @Test
    public void correctProjectNameEnteredTest() {
        String correctName = "angularjs";
        Mockito.when(view.getProjectName()).thenReturn(correctName);
        presenter.onProjectNameChanged(correctName);
        Mockito.verify(dataObject).setName(ArgumentMatchers.eq(correctName));
        Mockito.verify(view).markNameValid();
        Mockito.verify(view, Mockito.never()).markNameInvalid();
        Mockito.verify(updateDelegate).updateControls();
    }

    @Test
    public void correctProjectNameWithPointEnteredTest() {
        String correctName = "Test.project..ForCodenvy";
        Mockito.when(view.getProjectName()).thenReturn(correctName);
        presenter.onProjectNameChanged(correctName);
        Mockito.verify(dataObject).setName(ArgumentMatchers.eq(correctName));
        Mockito.verify(view).markNameValid();
        Mockito.verify(view, Mockito.never()).markNameInvalid();
        Mockito.verify(updateDelegate).updateControls();
    }

    @Test
    public void emptyProjectNameEnteredTest() {
        String emptyName = "";
        Mockito.when(view.getProjectName()).thenReturn(emptyName);
        presenter.onProjectNameChanged(emptyName);
        Mockito.verify(dataObject).setName(ArgumentMatchers.eq(emptyName));
        Mockito.verify(updateDelegate).updateControls();
    }

    @Test
    public void incorrectProjectNameEnteredTest() {
        String incorrectName = "angularjs+";
        Mockito.when(view.getProjectName()).thenReturn(incorrectName);
        presenter.onProjectNameChanged(incorrectName);
        Mockito.verify(dataObject).setName(ArgumentMatchers.eq(incorrectName));
        Mockito.verify(view).markNameInvalid();
        Mockito.verify(updateDelegate).updateControls();
    }

    @Test
    public void projectDescriptionChangedTest() {
        String description = "description";
        presenter.onProjectDescriptionChanged(description);
        Mockito.verify(dataObject).setDescription(ArgumentMatchers.eq(description));
    }

    @Test
    public void keepDirectorySelectedTest() {
        Map<String, String> parameters = new HashMap<>();
        Mockito.when(source.getParameters()).thenReturn(parameters);
        Mockito.when(view.getDirectoryName()).thenReturn("directory");
        presenter.onKeepDirectorySelected(true);
        Assert.assertEquals("directory", parameters.get("keepDir"));
        Mockito.verify(dataObject).setType("blank");
        Mockito.verify(view).highlightDirectoryNameField(ArgumentMatchers.eq(false));
        Mockito.verify(view).focusDirectoryNameField();
    }

    @Test
    public void keepDirectoryUnSelectedTest() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("keepDir", "directory");
        Mockito.when(source.getParameters()).thenReturn(parameters);
        presenter.onKeepDirectorySelected(false);
        Assert.assertTrue(parameters.isEmpty());
        Mockito.verify(dataObject).setType(ArgumentMatchers.eq(null));
        Mockito.verify(view).highlightDirectoryNameField(ArgumentMatchers.eq(false));
    }

    @Test
    public void recursiveCloneSelectedTest() {
        Map<String, String> parameters = new HashMap<>();
        Mockito.when(source.getParameters()).thenReturn(parameters);
        Mockito.when(view.getDirectoryName()).thenReturn("recursive");
        presenter.onRecursiveSelected(true);
        Assert.assertTrue(parameters.containsKey("recursive"));
    }

    @Test
    public void recursiveCloneUnSelectedTest() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("recursive", null);
        Mockito.when(source.getParameters()).thenReturn(parameters);
        presenter.onRecursiveSelected(false);
        Assert.assertTrue(parameters.isEmpty());
    }

    @Test
    public void keepDirectoryNameChangedAndKeepDirectorySelectedTest() {
        Map<String, String> parameters = new HashMap<>();
        Mockito.when(source.getParameters()).thenReturn(parameters);
        Mockito.when(view.getDirectoryName()).thenReturn("directory");
        Mockito.when(view.keepDirectory()).thenReturn(true);
        presenter.onKeepDirectoryNameChanged("directory");
        Assert.assertEquals("directory", parameters.get("keepDir"));
        Mockito.verify(dataObject, Mockito.never()).setPath(ArgumentMatchers.any());
        Mockito.verify(dataObject).setType(ArgumentMatchers.eq("blank"));
        Mockito.verify(view).highlightDirectoryNameField(ArgumentMatchers.eq(false));
    }

    @Test
    public void keepDirectoryNameChangedAndKeepDirectoryUnSelectedTest() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("keepDir", "directory");
        Mockito.when(source.getParameters()).thenReturn(parameters);
        Mockito.when(view.keepDirectory()).thenReturn(false);
        presenter.onKeepDirectoryNameChanged("directory");
        Assert.assertTrue(parameters.isEmpty());
        Mockito.verify(dataObject, Mockito.never()).setPath(ArgumentMatchers.any());
        Mockito.verify(dataObject).setType(ArgumentMatchers.eq(null));
        Mockito.verify(view).highlightDirectoryNameField(ArgumentMatchers.eq(false));
    }

    @Test
    public void branchSelectedTest() {
        Map<String, String> parameters = new HashMap<>();
        Mockito.when(source.getParameters()).thenReturn(parameters);
        Mockito.when(view.getBranchName()).thenReturn("someBranch");
        presenter.onBranchCheckBoxSelected(true);
        Mockito.verify(view).enableBranchNameField(true);
        Mockito.verify(view).focusBranchNameField();
        Assert.assertEquals("someBranch", parameters.get("branch"));
    }

    @Test
    public void branchNotSelectedTest() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("branch", "someBranch");
        Mockito.when(source.getParameters()).thenReturn(parameters);
        presenter.onBranchCheckBoxSelected(false);
        Mockito.verify(view).enableBranchNameField(false);
        Assert.assertTrue(parameters.isEmpty());
    }

    @Test
    public void onLoadRepoClickedWhenAuthorizeIsFailed() throws Exception {
        String userId = "userId";
        CurrentUser user = Mockito.mock(CurrentUser.class);
        Mockito.when(appContext.getCurrentUser()).thenReturn(user);
        Mockito.when(user.getId()).thenReturn(userId);
        final Throwable exception = Mockito.mock(UnauthorizedException.class);
        Mockito.doReturn(exception).when(promiseError).getCause();
        presenter.onLoadRepoClicked();
        Mockito.verify(gitHubClientService).getRepositoriesList(ArgumentMatchers.anyString());
        Mockito.verify(gitHubClientService).getUserInfo(ArgumentMatchers.anyString());
        Mockito.verify(gitHubClientService).getOrganizations(ArgumentMatchers.anyString());
        Mockito.verify(gitHubAuthenticator).authenticate(ArgumentMatchers.anyString(), asyncCallbackCaptor.capture());
        AsyncCallback<OAuthStatus> asyncCallback = asyncCallbackCaptor.getValue();
        asyncCallback.onFailure(exception);
        Mockito.verify(view, Mockito.times(2)).setLoaderVisibility(ArgumentMatchers.eq(true));
        Mockito.verify(view, Mockito.times(2)).setInputsEnableState(ArgumentMatchers.eq(false));
        Mockito.verify(view, Mockito.times(2)).setInputsEnableState(ArgumentMatchers.eq(true));
        Mockito.verify(view, Mockito.never()).setAccountNames(ArgumentMatchers.any());
        Mockito.verify(view, Mockito.never()).showGithubPanel();
        Mockito.verify(view, Mockito.never()).setRepositories(ArgumentMatchers.any());
    }

    @Test
    public void onLoadRepoClickedWhenAuthorizeIsSuccessful() throws Exception {
        final Throwable exception = Mockito.mock(UnauthorizedException.class);
        String userId = "userId";
        CurrentUser user = Mockito.mock(CurrentUser.class);
        Mockito.doReturn(exception).when(promiseError).getCause();
        Mockito.when(appContext.getCurrentUser()).thenReturn(user);
        Mockito.when(user.getId()).thenReturn(userId);
        presenter.onLoadRepoClicked();
        Mockito.verify(gitHubClientService).getRepositoriesList(ArgumentMatchers.anyString());
        Mockito.verify(gitHubClientService).getUserInfo(ArgumentMatchers.anyString());
        Mockito.verify(gitHubClientService).getOrganizations(ArgumentMatchers.anyString());
        Mockito.verify(gitHubAuthenticator).authenticate(ArgumentMatchers.anyString(), asyncCallbackCaptor.capture());
        AsyncCallback<OAuthStatus> asyncCallback = asyncCallbackCaptor.getValue();
        asyncCallback.onSuccess(null);
        Mockito.verify(view, Mockito.times(3)).setLoaderVisibility(ArgumentMatchers.eq(true));
        Mockito.verify(view, Mockito.times(3)).setInputsEnableState(ArgumentMatchers.eq(false));
        Mockito.verify(view, Mockito.times(3)).setInputsEnableState(ArgumentMatchers.eq(true));
    }
}

