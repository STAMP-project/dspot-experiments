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
package org.eclipse.che.ide.ext.git.client.status;


import StatusNotification.Status;
import org.eclipse.che.ide.ext.git.client.BaseTest;
import org.eclipse.che.ide.ext.git.client.outputconsole.GitOutputConsoleFactory;
import org.eclipse.che.ide.processes.panel.ProcessesPanelPresenter;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Testing {@link StatusCommandPresenter} functionality.
 *
 * @author Andrey Plotnikov
 * @author Vlad Zhukovskyi
 */
public class StatusCommandPresenterTest extends BaseTest {
    @InjectMocks
    private StatusCommandPresenter presenter;

    @Mock
    private GitOutputConsoleFactory gitOutputConsoleFactory;

    @Mock
    private ProcessesPanelPresenter processesPanelPresenter;

    @Test
    public void testShowStatusWhenStatusTextRequestIsSuccessful() throws Exception {
        Mockito.when(gitOutputConsoleFactory.create(ArgumentMatchers.anyString())).thenReturn(console);
        presenter.showStatus(project);
        Mockito.verify(stringPromise).then(stringCaptor.capture());
        stringCaptor.getValue().apply("");
        Mockito.verify(console, Mockito.times(2)).print(ArgumentMatchers.anyString());
        Mockito.verify(processesPanelPresenter).addCommandOutput(ArgumentMatchers.anyObject());
    }

    @Test
    public void testShowStatusWhenStatusTextRequestIsFailed() throws Exception {
        presenter.showStatus(project);
        Mockito.verify(stringPromise).catchError(promiseErrorCaptor.capture());
        promiseErrorCaptor.getValue().apply(promiseError);
        Mockito.verify(notificationManager).notify(ArgumentMatchers.anyString(), ArgumentMatchers.any(Status.class), ArgumentMatchers.anyObject());
        Mockito.verify(constant).statusFailed();
    }
}

