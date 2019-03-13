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
package org.eclipse.che.ide.ext.git.client.revert.commit;


import java.util.Collections;
import java.util.List;
import org.eclipse.che.api.git.shared.LogResponse;
import org.eclipse.che.api.git.shared.RevertResult;
import org.eclipse.che.api.git.shared.Revision;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.ide.ext.git.client.BaseTest;
import org.eclipse.che.ide.ext.git.client.revert.RevertCommitPresenter;
import org.eclipse.che.ide.ext.git.client.revert.RevertCommitView;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


public class RevertCommitPresenterTest extends BaseTest {
    @Mock
    private RevertCommitView view;

    @InjectMocks
    private RevertCommitPresenter presenter;

    @Mock
    private Promise<RevertResult> revertPromise;

    @Captor
    private ArgumentCaptor<Operation<RevertResult>> revertCaptor;

    @Test
    public void shouldGetCommitsAndShowDialog() throws Exception {
        LogResponse response = Mockito.mock(LogResponse.class);
        List<Revision> revisions = Collections.singletonList(Mockito.mock(Revision.class));
        Mockito.when(response.getCommits()).thenReturn(revisions);
        presenter.show(project);
        Mockito.verify(logPromise).then(logCaptor.capture());
        logCaptor.getValue().apply(response);
        Mockito.verify(view).setRevisions(revisions);
        Mockito.verify(view).showDialog();
    }

    @Test
    public void shouldShowNotificationOnGetCommitsError() throws Exception {
        Mockito.when(constant.logFailed()).thenReturn("error");
        presenter.show(project);
        Mockito.verify(logPromise).catchError(promiseErrorCaptor.capture());
        promiseErrorCaptor.getValue().apply(Mockito.mock(PromiseError.class));
        Mockito.verify(notificationManager).notify(ArgumentMatchers.eq("error"), ArgumentMatchers.eq(FAIL), ArgumentMatchers.eq(FLOAT_MODE));
    }

    @Test
    public void shouldNotifyOnSuccessfulRevert() throws Exception {
        RevertResult revertResult = Mockito.mock(RevertResult.class);
        Mockito.when(revertResult.getNewHead()).thenReturn("1234");
        Mockito.when(revertResult.getRevertedCommits()).thenReturn(Collections.emptyList());
        Mockito.when(revertResult.getConflicts()).thenReturn(Collections.emptyMap());
        Revision selectedRevision = Mockito.mock(Revision.class);
        Mockito.when(selectedRevision.getId()).thenReturn("1234");
        presenter.show(project);
        presenter.onRevisionSelected(selectedRevision);
        presenter.onRevertClicked();
        Mockito.verify(revertPromise).then(revertCaptor.capture());
        revertCaptor.getValue().apply(revertResult);
        Mockito.verify(notificationManager).notify(constant.revertCommitSuccessfully());
    }

    @Test
    public void shouldNotifyOnFailedRevert() throws Exception {
        RevertResult revertResult = Mockito.mock(RevertResult.class);
        Mockito.when(revertResult.getNewHead()).thenReturn("1234");
        Mockito.when(revertResult.getRevertedCommits()).thenReturn(Collections.emptyList());
        Mockito.when(revertResult.getConflicts()).thenReturn(Collections.emptyMap());
        Revision selectedRevision = Mockito.mock(Revision.class);
        Mockito.when(selectedRevision.getId()).thenReturn("1234");
        presenter.show(project);
        presenter.onRevisionSelected(selectedRevision);
        presenter.onRevertClicked();
        Mockito.verify(revertPromise).catchError(promiseErrorCaptor.capture());
        promiseErrorCaptor.getValue().apply(Mockito.mock(PromiseError.class));
        Mockito.verify(notificationManager).notify(ArgumentMatchers.eq(constant.revertCommitFailed()), ArgumentMatchers.eq(FAIL), ArgumentMatchers.eq(FLOAT_MODE));
    }
}

