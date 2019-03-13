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
package org.eclipse.che.ide.ext.git.client.remote.add;


import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.ext.git.client.BaseTest;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Testing {@link AddRemoteRepositoryPresenter} functionality.
 *
 * @author Andrey Plotnikov
 */
public class AddRemoteRepositoryPresenterTest extends BaseTest {
    @Mock
    private AddRemoteRepositoryView view;

    @Mock
    private AsyncCallback<Void> callback;

    private AddRemoteRepositoryPresenter presenter;

    @Test
    public void testShowDialog() throws Exception {
        presenter.showDialog(callback);
        Mockito.verify(view).setUrl(ArgumentMatchers.eq(BaseTest.EMPTY_TEXT));
        Mockito.verify(view).setName(ArgumentMatchers.eq(BaseTest.EMPTY_TEXT));
        Mockito.verify(view).setEnableOkButton(ArgumentMatchers.eq(BaseTest.DISABLE_BUTTON));
        Mockito.verify(view).showDialog();
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();
        Mockito.verify(view).close();
    }

    @Test
    public void testOnValueChangedEnableButton() throws Exception {
        presenter.onValueChanged();
        Mockito.verify(view).setEnableOkButton(ArgumentMatchers.eq(BaseTest.ENABLE_BUTTON));
    }

    @Test
    public void testOnValueChangedDisableButton() throws Exception {
        Mockito.when(view.getName()).thenReturn(BaseTest.EMPTY_TEXT);
        Mockito.when(view.getUrl()).thenReturn(BaseTest.EMPTY_TEXT);
        presenter.onValueChanged();
        Mockito.verify(view).setEnableOkButton(ArgumentMatchers.eq(BaseTest.DISABLE_BUTTON));
    }

    @Test
    public void testUrlWithLeadingAndTrailingSpaces() throws Exception {
        Mockito.when(appContext.getRootProject()).thenReturn(Mockito.mock(Project.class));
        Mockito.when(voidPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(voidPromise);
        Mockito.when(voidPromise.catchError(ArgumentMatchers.any(Operation.class))).thenReturn(voidPromise);
        Mockito.when(service.remoteAdd(ArgumentMatchers.anyObject(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(voidPromise);
        Mockito.when(view.getUrl()).thenReturn(((" " + (BaseTest.REMOTE_URI)) + " "));
        presenter.onOkClicked();
        Mockito.verify(service).remoteAdd(ArgumentMatchers.anyObject(), ArgumentMatchers.anyString(), ArgumentMatchers.eq(BaseTest.REMOTE_URI));
    }
}

