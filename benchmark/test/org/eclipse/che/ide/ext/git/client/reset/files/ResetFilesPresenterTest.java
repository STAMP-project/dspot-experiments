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
package org.eclipse.che.ide.ext.git.client.reset.files;


import ResetRequest.ResetType;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.che.api.git.shared.IndexFile;
import org.eclipse.che.api.git.shared.Status;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.ide.ext.git.client.BaseTest;
import org.eclipse.che.ide.resource.Path;
import org.eclipse.che.ide.ui.dialogs.message.MessageDialog;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Testing {@link ResetFilesPresenter} functionality.
 *
 * @author Andrey Plotnikov
 * @author Vlad Zhukovskyi
 */
public class ResetFilesPresenterTest extends BaseTest {
    @Mock
    private ResetFilesView view;

    @Mock
    private IndexFile indexFile;

    private ResetFilesPresenter presenter;

    @Test
    public void testShowDialogWhenStatusRequestIsSuccessful() throws Exception {
        final Status status = Mockito.mock(Status.class);
        List<String> changes = new ArrayList<String>();
        changes.add("Change");
        Mockito.when(status.getAdded()).thenReturn(changes);
        Mockito.when(status.getChanged()).thenReturn(changes);
        Mockito.when(status.getRemoved()).thenReturn(changes);
        presenter.showDialog(project);
        Mockito.verify(statusPromise).then(statusPromiseCaptor.capture());
        statusPromiseCaptor.getValue().apply(status);
        Mockito.verify(view).setIndexedFiles(ArgumentMatchers.anyObject());
        Mockito.verify(view).showDialog();
    }

    @Test
    public void testShowDialogWhenStatusRequestIsSuccessfulButIndexIsEmpty() throws Exception {
        MessageDialog messageDialog = Mockito.mock(MessageDialog.class);
        Mockito.when(constant.messagesWarningTitle()).thenReturn("Warning");
        Mockito.when(constant.indexIsEmpty()).thenReturn("Index is Empty");
        Mockito.when(dialogFactory.createMessageDialog(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyObject())).thenReturn(messageDialog);
        final Status status = Mockito.mock(Status.class);
        List<String> changes = new ArrayList<>();
        Mockito.when(status.getAdded()).thenReturn(changes);
        Mockito.when(status.getChanged()).thenReturn(changes);
        Mockito.when(status.getRemoved()).thenReturn(changes);
        presenter.showDialog(project);
        Mockito.verify(statusPromise).then(statusPromiseCaptor.capture());
        statusPromiseCaptor.getValue().apply(status);
        Mockito.verify(dialogFactory).createMessageDialog(ArgumentMatchers.eq("Warning"), ArgumentMatchers.eq("Index is Empty"), ArgumentMatchers.anyObject());
        Mockito.verify(view, Mockito.never()).setIndexedFiles(ArgumentMatchers.anyObject());
        Mockito.verify(view, Mockito.never()).showDialog();
    }

    @Test
    public void testShowDialogWhenStatusRequestIsFailed() throws Exception {
        presenter.showDialog(project);
        Mockito.verify(statusPromise).catchError(promiseErrorCaptor.capture());
        promiseErrorCaptor.getValue().apply(promiseError);
        Mockito.verify(console).printError(ArgumentMatchers.anyString());
        Mockito.verify(notificationManager).notify(ArgumentMatchers.anyString());
    }

    @Test
    public void testOnResetClickedWhenNothingToReset() throws Exception {
        MessageDialog messageDialog = Mockito.mock(MessageDialog.class);
        final Status status = Mockito.mock(Status.class);
        IndexFile indexFile = Mockito.mock(IndexFile.class);
        Mockito.when(dtoFactory.createDto(IndexFile.class)).thenReturn(indexFile);
        Mockito.when(constant.messagesWarningTitle()).thenReturn("Warning");
        Mockito.when(constant.indexIsEmpty()).thenReturn("Index is Empty");
        Mockito.when(dialogFactory.createMessageDialog(constant.messagesWarningTitle(), constant.indexIsEmpty(), null)).thenReturn(messageDialog);
        Mockito.when(indexFile.isIndexed()).thenReturn(true);
        Mockito.when(indexFile.withIndexed(ArgumentMatchers.anyBoolean())).thenReturn(indexFile);
        Mockito.when(indexFile.withPath(ArgumentMatchers.anyString())).thenReturn(indexFile);
        List<String> changes = new ArrayList<String>();
        changes.add("Change");
        Mockito.when(status.getAdded()).thenReturn(changes);
        Mockito.when(status.getChanged()).thenReturn(changes);
        Mockito.when(status.getRemoved()).thenReturn(changes);
        presenter.showDialog(project);
        Mockito.verify(statusPromise).then(statusPromiseCaptor.capture());
        statusPromiseCaptor.getValue().apply(status);
        presenter.onResetClicked();
        Mockito.verify(view).close();
        Mockito.verify(console).print(ArgumentMatchers.anyString());
        Mockito.verify(constant, Mockito.times(2)).nothingToReset();
    }

    @Test
    public void testOnResetClickedWhenResetRequestIsSuccessful() throws Exception {
        final Status status = Mockito.mock(Status.class);
        List<String> changes = new ArrayList<String>();
        changes.add("Change");
        Mockito.when(status.getAdded()).thenReturn(changes);
        Mockito.when(status.getChanged()).thenReturn(changes);
        Mockito.when(status.getRemoved()).thenReturn(changes);
        Mockito.when(service.reset(ArgumentMatchers.any(Path.class), ArgumentMatchers.anyString(), ArgumentMatchers.any(ResetType.class), ArgumentMatchers.anyObject())).thenReturn(voidPromise);
        Mockito.when(voidPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(voidPromise);
        Mockito.when(voidPromise.catchError(ArgumentMatchers.any(Operation.class))).thenReturn(voidPromise);
        presenter.showDialog(project);
        Mockito.verify(statusPromise).then(statusPromiseCaptor.capture());
        statusPromiseCaptor.getValue().apply(status);
        presenter.onResetClicked();
        Mockito.verify(voidPromise).then(voidPromiseCaptor.capture());
        voidPromiseCaptor.getValue().apply(null);
        Mockito.verify(view).close();
        Mockito.verify(notificationManager).notify(ArgumentMatchers.anyString());
        Mockito.verify(console).print(ArgumentMatchers.anyString());
        Mockito.verify(constant, Mockito.times(2)).resetFilesSuccessfully();
    }

    @Test
    public void testOnResetClickedWhenResetRequestIsFailed() throws Exception {
        final Status status = Mockito.mock(Status.class);
        List<String> changes = new ArrayList<String>();
        changes.add("Change");
        Mockito.when(status.getAdded()).thenReturn(changes);
        Mockito.when(status.getChanged()).thenReturn(changes);
        Mockito.when(status.getRemoved()).thenReturn(changes);
        Mockito.when(service.reset(ArgumentMatchers.any(Path.class), ArgumentMatchers.anyString(), ArgumentMatchers.any(ResetType.class), ArgumentMatchers.anyObject())).thenReturn(voidPromise);
        Mockito.when(voidPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(voidPromise);
        Mockito.when(voidPromise.catchError(ArgumentMatchers.any(Operation.class))).thenReturn(voidPromise);
        presenter.showDialog(project);
        Mockito.verify(statusPromise).then(statusPromiseCaptor.capture());
        statusPromiseCaptor.getValue().apply(status);
        presenter.onResetClicked();
        Mockito.verify(voidPromise).catchError(promiseErrorCaptor.capture());
        promiseErrorCaptor.getValue().apply(promiseError);
        Mockito.verify(console).printError(ArgumentMatchers.anyString());
        Mockito.verify(notificationManager).notify(ArgumentMatchers.anyString());
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();
        Mockito.verify(view).close();
    }
}

