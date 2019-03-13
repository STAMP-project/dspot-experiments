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
package org.eclipse.che.ide.ext.git.client.history;


import ErrorCodes.INIT_COMMIT_WAS_NOT_PERFORMED;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.che.api.git.shared.LogResponse;
import org.eclipse.che.api.git.shared.Revision;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.ide.commons.exception.ServerException;
import org.eclipse.che.ide.ext.git.client.BaseTest;
import org.eclipse.che.ide.ext.git.client.compare.AlteredFiles;
import org.eclipse.che.ide.ext.git.client.compare.ComparePresenter;
import org.eclipse.che.ide.ext.git.client.compare.changeslist.ChangesListPresenter;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmCallback;
import org.eclipse.che.ide.ui.dialogs.message.MessageDialog;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


public class HistoryPresenterTest extends BaseTest {
    @Mock
    private HistoryView view;

    @Mock
    private ComparePresenter comparePresenter;

    @Mock
    private ChangesListPresenter changesListPresenter;

    @InjectMocks
    private HistoryPresenter presenter;

    @Test
    public void shouldGetCommitsAndShowDialog() throws Exception {
        LogResponse response = Mockito.mock(LogResponse.class);
        List<Revision> revisions = Collections.singletonList(Mockito.mock(Revision.class));
        Mockito.when(response.getCommits()).thenReturn(revisions);
        presenter.show();
        Mockito.verify(logPromise).then(logCaptor.capture());
        logCaptor.getValue().apply(response);
        Mockito.verify(view).setRevisions(revisions);
        Mockito.verify(view).showDialog();
    }

    @Test
    public void shouldShowDialogOnInitCommitError() throws Exception {
        PromiseError error = Mockito.mock(PromiseError.class);
        ServerException exception = Mockito.mock(ServerException.class);
        Mockito.when(exception.getErrorCode()).thenReturn(INIT_COMMIT_WAS_NOT_PERFORMED);
        Mockito.when(error.getCause()).thenReturn(exception);
        Mockito.when(constant.initCommitWasNotPerformed()).thenReturn("error message");
        MessageDialog dialog = Mockito.mock(MessageDialog.class);
        Mockito.when(dialogFactory.createMessageDialog(ArgumentMatchers.eq("title"), ArgumentMatchers.eq("error message"), ArgumentMatchers.any(ConfirmCallback.class))).thenReturn(dialog);
        presenter.show();
        Mockito.verify(logPromise).catchError(promiseErrorCaptor.capture());
        promiseErrorCaptor.getValue().apply(error);
        Mockito.verify(dialog).show();
    }

    @Test
    public void shouldShowNotificationOnGetLogError() throws Exception {
        Mockito.when(constant.logFailed()).thenReturn("error");
        presenter.show();
        Mockito.verify(logPromise).catchError(promiseErrorCaptor.capture());
        promiseErrorCaptor.getValue().apply(Mockito.mock(PromiseError.class));
        Mockito.verify(notificationManager).notify(ArgumentMatchers.eq("error"), ArgumentMatchers.eq(FAIL), ArgumentMatchers.eq(EMERGE_MODE));
    }

    @Test
    public void shouldShowCompareWhenOneFileChangedInCurrentRevision() throws Exception {
        final String diff = "M\tfile";
        final AlteredFiles alteredFiles = new AlteredFiles(project, diff);
        Revision parentRevision = Mockito.mock(Revision.class);
        Revision selectedRevision = Mockito.mock(Revision.class);
        Mockito.when(parentRevision.getId()).thenReturn("commitA");
        Mockito.when(selectedRevision.getId()).thenReturn("commitB");
        LogResponse logResponse = Mockito.mock(LogResponse.class);
        List<Revision> revisions = new ArrayList<>();
        revisions.add(selectedRevision);
        revisions.add(parentRevision);
        Mockito.when(logResponse.getCommits()).thenReturn(revisions);
        presenter.show();
        presenter.onRevisionSelected(selectedRevision);
        Mockito.verify(logPromise).then(logCaptor.capture());
        logCaptor.getValue().apply(logResponse);
        presenter.onCompareClicked();
        Mockito.verify(stringPromise).then(stringCaptor.capture());
        stringCaptor.getValue().apply(diff);
        Mockito.verify(comparePresenter).showCompareBetweenRevisions(ArgumentMatchers.eq(alteredFiles), ArgumentMatchers.anyString(), ArgumentMatchers.eq("commitA"), ArgumentMatchers.eq("commitB"));
    }

    @Test
    public void shouldShowChangedListWhenSeveralFilesChangedInSelectedRevision() throws Exception {
        final String diff = "M\tfile1\nM\tfile2";
        final AlteredFiles alteredFiles = new AlteredFiles(project, diff);
        Revision revisionA = Mockito.mock(Revision.class);
        Revision revisionB = Mockito.mock(Revision.class);
        Mockito.when(revisionA.getId()).thenReturn("commitA");
        Mockito.when(revisionB.getId()).thenReturn("commitB");
        LogResponse logResponse = Mockito.mock(LogResponse.class);
        List<Revision> revisions = new ArrayList<>();
        revisions.add(revisionA);
        revisions.add(revisionB);
        Mockito.when(logResponse.getCommits()).thenReturn(revisions);
        presenter.show();
        presenter.onRevisionSelected(revisionA);
        Mockito.verify(logPromise).then(logCaptor.capture());
        logCaptor.getValue().apply(logResponse);
        presenter.onCompareClicked();
        Mockito.verify(stringPromise).then(stringCaptor.capture());
        stringCaptor.getValue().apply(diff);
        Mockito.verify(changesListPresenter).show(ArgumentMatchers.eq(alteredFiles), ArgumentMatchers.eq("commitB"), ArgumentMatchers.eq("commitA"));
    }

    @Test
    public void shouldShowNotificationOnGetDiffError() throws Exception {
        Revision revisionA = Mockito.mock(Revision.class);
        Revision revisionB = Mockito.mock(Revision.class);
        LogResponse logResponse = Mockito.mock(LogResponse.class);
        List<Revision> revisions = new ArrayList<>();
        revisions.add(revisionA);
        revisions.add(revisionB);
        Mockito.when(logResponse.getCommits()).thenReturn(revisions);
        Mockito.when(constant.diffFailed()).thenReturn("error");
        presenter.show();
        presenter.onRevisionSelected(revisionA);
        Mockito.verify(logPromise).then(logCaptor.capture());
        logCaptor.getValue().apply(logResponse);
        presenter.onCompareClicked();
        Mockito.verify(stringPromise).catchError(promiseErrorCaptor.capture());
        promiseErrorCaptor.getValue().apply(null);
        Mockito.verify(notificationManager).notify(ArgumentMatchers.eq("error"), ArgumentMatchers.eq(FAIL), ArgumentMatchers.eq(EMERGE_MODE));
    }

    @Test
    public void shouldShowDialogIfNothingToCompare() throws Exception {
        MessageDialog dialog = Mockito.mock(MessageDialog.class);
        Mockito.when(dialogFactory.createMessageDialog(ArgumentMatchers.eq("title"), ArgumentMatchers.eq("error message"), ArgumentMatchers.any(ConfirmCallback.class))).thenReturn(dialog);
        presenter.show();
        presenter.onRevisionSelected(Mockito.mock(Revision.class));
        presenter.onCompareClicked();
        Mockito.verify(stringPromise).then(stringCaptor.capture());
        stringCaptor.getValue().apply("");
        Mockito.verify(dialog).show();
    }
}

