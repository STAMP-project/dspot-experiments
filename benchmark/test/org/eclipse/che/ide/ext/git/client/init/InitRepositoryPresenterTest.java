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
package org.eclipse.che.ide.ext.git.client.init;


import StatusNotification.Status;
import org.eclipse.che.ide.ext.git.client.BaseTest;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Testing {@link InitRepositoryPresenter} functionality.
 *
 * @author Andrey Plotnikov
 * @author Roman Nikitenko
 * @author Vlad Zhukovskyi
 */
public class InitRepositoryPresenterTest extends BaseTest {
    private InitRepositoryPresenter presenter;

    @Test
    public void testOnOkClickedInitWSRequestAndGetProjectIsSuccessful() throws Exception {
        presenter.initRepository(project);
        Mockito.verify(voidPromise).then(voidPromiseCaptor.capture());
        voidPromiseCaptor.getValue().apply(null);
        Mockito.verify(gitOutputConsoleFactory).create(ArgumentMatchers.eq(InitRepositoryPresenter.INIT_COMMAND_NAME));
        Mockito.verify(console).print(ArgumentMatchers.eq(constant.initSuccess()));
        Mockito.verify(processesPanelPresenter).addCommandOutput(ArgumentMatchers.eq(console));
        Mockito.verify(notificationManager).notify(ArgumentMatchers.anyString());
        Mockito.verify(project).synchronize();
    }

    @Test
    public void testOnOkClickedInitWSRequestIsFailed() throws Exception {
        presenter.initRepository(project);
        Mockito.verify(voidPromise).catchError(promiseErrorCaptor.capture());
        promiseErrorCaptor.getValue().apply(promiseError);
        Mockito.verify(constant).initFailed();
        Mockito.verify(gitOutputConsoleFactory).create(InitRepositoryPresenter.INIT_COMMAND_NAME);
        Mockito.verify(console).printError(ArgumentMatchers.anyObject());
        Mockito.verify(processesPanelPresenter).addCommandOutput(ArgumentMatchers.eq(console));
        Mockito.verify(notificationManager).notify(ArgumentMatchers.anyString(), ArgumentMatchers.any(Status.class), ArgumentMatchers.anyObject());
    }
}

