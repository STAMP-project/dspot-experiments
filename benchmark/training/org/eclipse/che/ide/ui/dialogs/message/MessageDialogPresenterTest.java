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
package org.eclipse.che.ide.ui.dialogs.message;


import org.eclipse.che.ide.ui.dialogs.BaseTest;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmCallback;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Testing {@link MessageDialogPresenter} functionality.
 *
 * @author Artem Zatsarynnyi
 */
public class MessageDialogPresenterTest extends BaseTest {
    @Mock
    private MessageDialogView view;

    @Mock
    private ConfirmCallback confirmCallback;

    private MessageDialogPresenter presenter;

    @Test
    public void shouldCallCallbackOnAccepted() throws Exception {
        presenter.accepted();
        Mockito.verify(view).closeDialog();
        Mockito.verify(confirmCallback).accepted();
    }

    @Test
    public void shouldNotCallCallbackOnAccepted() throws Exception {
        presenter = new MessageDialogPresenter(view, BaseTest.TITLE, BaseTest.MESSAGE, null);
        presenter.accepted();
        Mockito.verify(view).closeDialog();
        Mockito.verify(confirmCallback, Mockito.never()).accepted();
    }

    @Test
    public void shouldShowView() throws Exception {
        presenter.show();
        Mockito.verify(view).showDialog();
    }
}

