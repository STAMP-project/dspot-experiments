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
package org.eclipse.che.ide.ui.dialogs.confirm;


import org.eclipse.che.ide.ui.dialogs.BaseTest;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Testing {@link ConfirmDialogPresenter} functionality.
 *
 * @author Artem Zatsarynnyi
 */
public class ConfirmDialogPresenterTest extends BaseTest {
    @Mock
    private ConfirmDialogView view;

    private ConfirmDialogPresenter presenter;

    @Test
    public void shouldCallCallbackOnCanceled() throws Exception {
        presenter.cancelled();
        Mockito.verify(view).closeDialog();
        Mockito.verify(cancelCallback).cancelled();
    }

    @Test
    public void shouldNotCallCallbackOnCanceled() throws Exception {
        presenter = new ConfirmDialogPresenter(view, BaseTest.TITLE, BaseTest.MESSAGE, confirmCallback, null);
        presenter.cancelled();
        Mockito.verify(view).closeDialog();
        Mockito.verify(cancelCallback, Mockito.never()).cancelled();
    }

    @Test
    public void shouldCallCallbackOnAccepted() throws Exception {
        presenter.accepted();
        Mockito.verify(view).closeDialog();
        Mockito.verify(confirmCallback).accepted();
    }

    @Test
    public void shouldNotCallCallbackOnAccepted() throws Exception {
        presenter = new ConfirmDialogPresenter(view, BaseTest.TITLE, BaseTest.MESSAGE, null, cancelCallback);
        presenter.accepted();
        Mockito.verify(view).closeDialog();
        Mockito.verify(confirmCallback, Mockito.never()).accepted();
    }

    @Test
    public void shouldShowView() throws Exception {
        presenter.show();
        Mockito.verify(view).showDialog();
    }

    @Test
    public void onEnterClickedWhenAcceptButtonInFocusTest() throws Exception {
        Mockito.when(view.isOkButtonInFocus()).thenReturn(true);
        presenter.onEnterClicked();
        Mockito.verify(view).closeDialog();
        Mockito.verify(confirmCallback).accepted();
        Mockito.verify(cancelCallback, Mockito.never()).cancelled();
    }

    @Test
    public void onEnterClickedWhenCancelButtonInFocusTest() throws Exception {
        Mockito.when(view.isCancelButtonInFocus()).thenReturn(true);
        presenter.onEnterClicked();
        Mockito.verify(view).closeDialog();
        Mockito.verify(confirmCallback, Mockito.never()).accepted();
        Mockito.verify(cancelCallback).cancelled();
    }
}

