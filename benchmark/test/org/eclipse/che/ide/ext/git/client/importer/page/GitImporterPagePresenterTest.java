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
package org.eclipse.che.ide.ext.git.client.importer.page;


import MutableProjectConfig.MutableSourceStorage;
import Wizard.UpdateDelegate;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.che.ide.api.project.MutableProjectConfig;
import org.eclipse.che.ide.ext.git.client.GitLocalizationConstant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Testing {@link GitImporterPagePresenter} functionality.
 *
 * @author Roman Nikitenko
 */
@RunWith(MockitoJUnitRunner.class)
public class GitImporterPagePresenterTest {
    @Mock
    private UpdateDelegate updateDelegate;

    @Mock
    private GitImporterPageView view;

    @Mock
    private GitLocalizationConstant locale;

    @Mock
    private MutableProjectConfig dataObject;

    @Mock
    private MutableSourceStorage source;

    @Mock
    private Map<String, String> parameters;

    @InjectMocks
    private GitImporterPagePresenter presenter;

    @Test
    public void testGo() {
        AcceptsOneWidget container = Mockito.mock(AcceptsOneWidget.class);
        presenter.go(container);
        Mockito.verify(container).setWidget(ArgumentMatchers.eq(view));
        Mockito.verify(view).setProjectName(ArgumentMatchers.anyString());
        Mockito.verify(view).setProjectDescription(ArgumentMatchers.anyString());
        Mockito.verify(view).setProjectUrl(ArgumentMatchers.anyString());
        Mockito.verify(view).setInputsEnableState(ArgumentMatchers.eq(true));
        Mockito.verify(view).focusInUrlInput();
    }

    @Test
    public void projectUrlStartWithWhiteSpaceEnteredTest() {
        String incorrectUrl = " https://github.com/codenvy/ide.git";
        String name = "ide";
        Mockito.when(view.getProjectName()).thenReturn("");
        presenter.projectUrlChanged(incorrectUrl);
        Mockito.verify(view).markURLInvalid();
        Mockito.verify(view).setURLErrorMessage(ArgumentMatchers.eq(locale.importProjectMessageStartWithWhiteSpace()));
        Mockito.verify(dataObject).setName(ArgumentMatchers.eq(name));
        Mockito.verify(view).setProjectName(name);
        Mockito.verify(updateDelegate).updateControls();
    }

    @Test
    public void testUrlMatchScpLikeSyntax() {
        // test for url with an alternative scp-like syntax: [user@]host.xz:path/to/repo.git/
        String correctUrl = "host.xz:path/to/repo.git";
        Mockito.when(view.getProjectName()).thenReturn("");
        presenter.projectUrlChanged(correctUrl);
        verifyInvocationsForCorrectUrl(correctUrl);
    }

    @Test
    public void testUrlWithoutUsername() {
        String correctUrl = "git@hostname.com:projectName.git";
        Mockito.when(view.getProjectName()).thenReturn("");
        presenter.projectUrlChanged(correctUrl);
        verifyInvocationsForCorrectUrl(correctUrl);
    }

    @Test
    public void testSshUriWithHostBetweenDoubleSlashAndSlash() {
        // Check for type uri which start with ssh:// and has host between // and /
        String correctUrl = "ssh://host.com/some/path";
        Mockito.when(view.getProjectName()).thenReturn("");
        presenter.projectUrlChanged(correctUrl);
        verifyInvocationsForCorrectUrl(correctUrl);
    }

    @Test
    public void testSshUriWithHostBetweenDoubleSlashAndColon() {
        // Check for type uri with host between // and :
        String correctUrl = "ssh://host.com:port/some/path";
        Mockito.when(view.getProjectName()).thenReturn("");
        presenter.projectUrlChanged(correctUrl);
        verifyInvocationsForCorrectUrl(correctUrl);
    }

    @Test
    public void testGitUriWithHostBetweenDoubleSlashAndSlash() {
        // Check for type uri which start with git:// and has host between // and /
        String correctUrl = "git://host.com/user/repo";
        Mockito.when(view.getProjectName()).thenReturn("");
        presenter.projectUrlChanged(correctUrl);
        verifyInvocationsForCorrectUrl(correctUrl);
    }

    @Test
    public void testSshUriWithHostBetweenAtAndColon() {
        // Check for type uri with host between @ and :
        String correctUrl = "user@host.com:login/repo";
        Mockito.when(view.getProjectName()).thenReturn("");
        presenter.projectUrlChanged(correctUrl);
        verifyInvocationsForCorrectUrl(correctUrl);
    }

    @Test
    public void testSshUriWithHostBetweenAtAndSlash() {
        // Check for type uri with host between @ and /
        String correctUrl = "ssh://user@host.com/some/path";
        Mockito.when(view.getProjectName()).thenReturn("");
        presenter.projectUrlChanged(correctUrl);
        verifyInvocationsForCorrectUrl(correctUrl);
    }

    @Test
    public void projectUrlWithIncorrectProtocolEnteredTest() {
        String incorrectUrl = "htps://github.com/codenvy/ide.git";
        Mockito.when(view.getProjectName()).thenReturn("");
        presenter.projectUrlChanged(incorrectUrl);
        Mockito.verify(view).markURLInvalid();
        Mockito.verify(view).setURLErrorMessage(ArgumentMatchers.eq(locale.importProjectMessageProtocolIncorrect()));
        Mockito.verify(source).setLocation(ArgumentMatchers.eq(incorrectUrl));
        Mockito.verify(view).setProjectName(ArgumentMatchers.anyString());
        Mockito.verify(updateDelegate).updateControls();
    }

    @Test
    public void correctProjectNameEnteredTest() {
        // String correctName = "angularjs";
        // when(view.getProjectName()).thenReturn(correctName);
        // 
        // presenter.projectNameChanged(correctName);
        // 
        // verify(dataObject).setName(eq(correctName));
        // 
        // verify(view).markURLValid();
        // verify(view).setURLErrorMessage(eq(null));
        // verify(view, never()).markURLInvalid();
        // verify(updateDelegate).updateControls();
    }

    @Test
    public void correctProjectNameWithPointEnteredTest() {
        String correctName = "Test.project..ForCodenvy";
        Mockito.when(view.getProjectName()).thenReturn(correctName);
        presenter.projectNameChanged(correctName);
        Mockito.verify(dataObject).setName(ArgumentMatchers.eq(correctName));
        Mockito.verify(view).markNameValid();
        Mockito.verify(view, Mockito.never()).markNameInvalid();
        Mockito.verify(updateDelegate).updateControls();
    }

    @Test
    public void emptyProjectNameEnteredTest() {
        String emptyName = "";
        Mockito.when(view.getProjectName()).thenReturn(emptyName);
        presenter.projectNameChanged(emptyName);
        Mockito.verify(dataObject).setName(ArgumentMatchers.eq(emptyName));
        Mockito.verify(updateDelegate).updateControls();
    }

    @Test
    public void incorrectProjectNameEnteredTest() {
        String incorrectName = "angularjs+";
        Mockito.when(view.getProjectName()).thenReturn(incorrectName);
        presenter.projectNameChanged(incorrectName);
        Mockito.verify(dataObject).setName(ArgumentMatchers.eq(incorrectName));
        Mockito.verify(view).markNameInvalid();
        Mockito.verify(updateDelegate).updateControls();
    }

    @Test
    public void projectDescriptionChangedTest() {
        String description = "description";
        presenter.projectDescriptionChanged(description);
        Mockito.verify(dataObject).setDescription(ArgumentMatchers.eq(description));
    }

    @Test
    public void keepDirectorySelectedTest() {
        // Map<String, String> parameters = new HashMap<>();
        // when(source.getQueryParameters()).thenReturn(parameters);
        // when(view.getDirectoryName()).thenReturn("directory");
        // 
        // presenter.keepDirectorySelected(true);
        // 
        // assertEquals("directory", parameters.get("keepDirectory"));
        // verify(dataObject).withType("blank");
        // verify(view).highlightDirectoryNameField(eq(false));
        // verify(view).focusDirectoryNameField();
    }

    @Test
    public void keepDirectoryUnSelectedTest() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("keepDir", "directory");
        Mockito.when(source.getParameters()).thenReturn(parameters);
        presenter.keepDirectorySelected(false);
        Assert.assertTrue(parameters.isEmpty());
        Mockito.verify(dataObject).setType(ArgumentMatchers.eq(null));
        Mockito.verify(view).highlightDirectoryNameField(ArgumentMatchers.eq(false));
    }

    @Test
    public void keepDirectoryNameChangedAndKeepDirectorySelectedTest() {
        // Map<String, String> parameters = new HashMap<>();
        // when(source.getQueryParameters()).thenReturn(parameters);
        // when(view.getDirectoryName()).thenReturn("directory");
        // when(view.keepDirectory()).thenReturn(true);
        // 
        // presenter.keepDirectoryNameChanged("directory");
        // 
        // assertEquals("directory", parameters.get("keepDirectory"));
        // verify(dataObject, never()).setPath(any());
        // verify(dataObject).setType(eq("blank"));
        // verify(view).highlightDirectoryNameField(eq(false));
    }

    @Test
    public void keepDirectoryNameChangedAndKeepDirectoryUnSelectedTest() {
        // Map<String, String> parameters = new HashMap<>();
        // parameters.put("keepDirectory", "directory");
        // when(source.getQueryParameters()).thenReturn(parameters);
        // when(view.keepDirectory()).thenReturn(false);
        // 
        // presenter.keepDirectoryNameChanged("directory");
        // 
        // assertTrue(parameters.isEmpty());
        // verify(dataObject, never()).setPath(any());
        // verify(dataObject).setType(eq(null));
        // verify(view).highlightDirectoryNameField(eq(false));
    }

    @Test
    public void branchSelectedTest() {
        Map<String, String> parameters = new HashMap<>();
        Mockito.when(source.getParameters()).thenReturn(parameters);
        Mockito.when(view.getBranchName()).thenReturn("someBranch");
        Mockito.when(view.isBranchName()).thenReturn(true);
        presenter.branchSelected(true);
        Mockito.verify(view).enableBranchNameField(true);
        Mockito.verify(view).focusBranchNameField();
        Assert.assertEquals("someBranch", parameters.get("branch"));
    }

    @Test
    public void branchNotSelectedTest() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("branch", "someBranch");
        Mockito.when(source.getParameters()).thenReturn(parameters);
        presenter.branchSelected(false);
        Mockito.verify(view).enableBranchNameField(false);
        Assert.assertTrue(parameters.isEmpty());
    }

    /**
     * Empty directory name field must be highlighted when Keep directory is checked.
     */
    @Test
    public void emptyDirectoryNameEnteredTest() {
        Mockito.when(view.getDirectoryName()).thenReturn("");
        presenter.keepDirectorySelected(true);
        Mockito.verify(view).highlightDirectoryNameField(true);
    }

    /**
     * Non empty directory name field must be without highlighting when Keep directory is checked.
     */
    @Test
    public void directoryNameEnteredTest() {
        Mockito.when(view.getDirectoryName()).thenReturn("test");
        presenter.keepDirectorySelected(true);
        Mockito.verify(view).highlightDirectoryNameField(false);
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
}

