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
package org.eclipse.che.ide.projectimport.zip;


import MutableProjectConfig.MutableSourceStorage;
import Wizard.UpdateDelegate;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import java.util.Map;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.project.MutableProjectConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Testing {@link ZipImporterPagePresenter} functionality.
 *
 * @author Roman Nikitenko
 */
@RunWith(MockitoJUnitRunner.class)
public class ZipImporterPagePresenterTest {
    private static final String SKIP_FIRST_LEVEL_PARAM_NAME = "skipFirstLevel";

    private static final String CORRECT_URL = "https://host.com/some/path/angularjs.zip";

    private static final String INCORRECT_URL = " https://host.com/some/path/angularjs.zip";

    @Mock
    private ZipImporterPageView view;

    @Mock
    private CoreLocalizationConstant locale;

    @Mock
    private MutableProjectConfig dataObject;

    @Mock
    private MutableSourceStorage sourceStorageDto;

    @Mock
    private UpdateDelegate delegate;

    @Mock
    private Map<String, String> parameters;

    @InjectMocks
    private ZipImporterPagePresenter presenter;

    @Test
    public void testGo() {
        AcceptsOneWidget container = Mockito.mock(AcceptsOneWidget.class);
        Mockito.when(parameters.get(ZipImporterPagePresenterTest.SKIP_FIRST_LEVEL_PARAM_NAME)).thenReturn("true");
        presenter.go(container);
        Mockito.verify(container).setWidget(ArgumentMatchers.eq(view));
        Mockito.verify(view).setProjectName(ArgumentMatchers.nullable(String.class));
        Mockito.verify(view).setProjectDescription(ArgumentMatchers.nullable(String.class));
        Mockito.verify(view).setProjectUrl(ArgumentMatchers.nullable(String.class));
        Mockito.verify(view).setSkipFirstLevel(ArgumentMatchers.nullable(Boolean.class));
        Mockito.verify(view).setInputsEnableState(ArgumentMatchers.eq(true));
        Mockito.verify(view).focusInUrlInput();
    }

    @Test
    public void incorrectProjectUrlEnteredTest() {
        Mockito.when(view.getProjectName()).thenReturn("");
        Mockito.when(view.getProjectName()).thenReturn("angularjs");
        presenter.projectUrlChanged(ZipImporterPagePresenterTest.INCORRECT_URL);
        Mockito.verify(view).showUrlError(ArgumentMatchers.nullable(String.class));
        Mockito.verify(delegate).updateControls();
    }

    @Test
    public void projectUrlStartWithWhiteSpaceEnteredTest() {
        Mockito.when(view.getProjectName()).thenReturn("name");
        presenter.projectUrlChanged(ZipImporterPagePresenterTest.INCORRECT_URL);
        Mockito.verify(view).showUrlError(ArgumentMatchers.eq(locale.importProjectMessageStartWithWhiteSpace()));
        Mockito.verify(delegate).updateControls();
    }

    @Test
    public void correctProjectUrlEnteredTest() {
        Mockito.when(view.getProjectName()).thenReturn("", "angularjs");
        presenter.projectUrlChanged(ZipImporterPagePresenterTest.CORRECT_URL);
        Mockito.verify(view, Mockito.never()).showUrlError(ArgumentMatchers.anyString());
        Mockito.verify(view).hideNameError();
        Mockito.verify(view).setProjectName(ArgumentMatchers.anyString());
        Mockito.verify(delegate).updateControls();
    }

    @Test
    public void correctProjectNameEnteredTest() {
        String correctName = "angularjs";
        Mockito.when(view.getProjectName()).thenReturn(correctName);
        presenter.projectNameChanged(correctName);
        Mockito.verify(view).hideNameError();
        Mockito.verify(view, Mockito.never()).showNameError();
        Mockito.verify(delegate).updateControls();
    }

    @Test
    public void emptyProjectNameEnteredTest() {
        String emptyName = "";
        Mockito.when(view.getProjectName()).thenReturn(emptyName);
        presenter.projectNameChanged(emptyName);
        Mockito.verify(view).showNameError();
        Mockito.verify(delegate).updateControls();
    }

    @Test
    public void incorrectProjectNameEnteredTest() {
        String incorrectName = "angularjs+";
        Mockito.when(view.getProjectName()).thenReturn(incorrectName);
        presenter.projectNameChanged(incorrectName);
        Mockito.verify(view).showNameError();
        Mockito.verify(delegate).updateControls();
    }

    @Test
    public void skipFirstLevelSelectedTest() {
        presenter.skipFirstLevelChanged(true);
        Mockito.verify(delegate).updateControls();
    }

    @Test
    public void projectDescriptionChangedTest() {
        String description = "description";
        presenter.projectDescriptionChanged(description);
        Mockito.verify(delegate).updateControls();
    }

    @Test
    public void pageShouldNotBeReadyIfUrlIsEmpty() throws Exception {
        Mockito.when(view.getProjectName()).thenReturn("name");
        presenter.projectUrlChanged("");
        Mockito.verify(view).showUrlError(ArgumentMatchers.eq(""));
        Mockito.verify(delegate).updateControls();
    }
}

