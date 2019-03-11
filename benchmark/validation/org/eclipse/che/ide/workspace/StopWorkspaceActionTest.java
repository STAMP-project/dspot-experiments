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
package org.eclipse.che.ide.workspace;


import com.google.gwtmockito.GwtMockitoTestRunner;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.workspace.model.RuntimeImpl;
import org.eclipse.che.ide.api.workspace.model.WorkspaceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class StopWorkspaceActionTest {
    @Mock
    private CoreLocalizationConstant locale;

    @Mock
    private AppContext appContext;

    @Mock
    private CurrentWorkspaceManager workspaceManager;

    @Mock
    private WorkspaceImpl workspace;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ActionEvent actionEvent;

    @Mock
    private Promise<Void> voidPromise;

    @Captor
    private ArgumentCaptor<Operation<Void>> operationCaptor;

    @InjectMocks
    private StopWorkspaceAction action;

    @Test
    public void titleAndDescriptionShouldBeSet() {
        Mockito.verify(locale).stopWsTitle();
        Mockito.verify(locale).stopWsDescription();
    }

    @Test
    public void actionShouldBeUpdated() {
        Mockito.when(workspace.getRuntime()).thenReturn(Mockito.mock(RuntimeImpl.class));
        Mockito.when(appContext.getWorkspace()).thenReturn(workspace);
        action.updateInPerspective(actionEvent);
        Mockito.verify(actionEvent, Mockito.times(2)).getPresentation();
    }

    @Test
    public void actionShouldBePerformed() throws Exception {
        Mockito.when(appContext.getWorkspace()).thenReturn(workspace);
        Mockito.when(workspace.getId()).thenReturn("id");
        action.actionPerformed(actionEvent);
        Mockito.verify(workspaceManager).stopWorkspace();
    }
}

