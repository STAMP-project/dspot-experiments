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
package org.eclipse.che.ide.ext.git.client.commit;


import ErrorCodes.INIT_COMMIT_WAS_NOT_PERFORMED;
import java.util.Collections;
import org.eclipse.che.api.git.shared.GitUser;
import org.eclipse.che.api.git.shared.Revision;
import org.eclipse.che.api.git.shared.Status;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.ide.api.auth.OAuthServiceClient;
import org.eclipse.che.ide.commons.exception.ServerException;
import org.eclipse.che.ide.ext.git.client.BaseTest;
import org.eclipse.che.ide.ext.git.client.DateTimeFormatter;
import org.eclipse.che.ide.ext.git.client.compare.AlteredFiles;
import org.eclipse.che.ide.ext.git.client.compare.selectablechangespanel.SelectableChangesPanelPresenter;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmCallback;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmDialog;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Testing {@link CommitPresenter} functionality.
 *
 * @author Andrey Plotnikov
 * @author Vlad Zhukovskyi
 * @author Igor Vinokur
 */
public class CommitPresenterTest extends BaseTest {
    private static final String COMMIT_TEXT = "commit text";

    @Mock
    private CommitView view;

    @Mock
    private DateTimeFormatter dateTimeFormatter;

    @Mock
    private SelectableChangesPanelPresenter selectableChangesPanelPresenter;

    @Mock
    private OAuthServiceClient oAuthServiceClient;

    private CommitPresenter presenter;

    @Test
    public void shouldShowMessageWhenNothingToCommit() throws Exception {
        ConfirmDialog dialog = Mockito.mock(ConfirmDialog.class);
        Mockito.when(dialogFactory.createConfirmDialog(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(ConfirmCallback.class), ArgumentMatchers.eq(null))).thenReturn(dialog);
        presenter.showDialog(project);
        Mockito.verify(stringPromise).then(stringCaptor.capture());
        stringCaptor.getValue().apply("");
        Mockito.verify(logPromise).then(logCaptor.capture());
        logCaptor.getValue().apply(null);
        Mockito.verify(dialog).show();
    }

    @Test
    public void shouldShowDialog() throws Exception {
        final String diff = "M\tfile";
        final AlteredFiles alteredFiles = new AlteredFiles(project, diff);
        ConfirmDialog dialog = Mockito.mock(ConfirmDialog.class);
        Mockito.when(dialogFactory.createConfirmDialog(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(ConfirmCallback.class), ArgumentMatchers.eq(null))).thenReturn(dialog);
        presenter.showDialog(project);
        Mockito.verify(stringPromise).then(stringCaptor.capture());
        stringCaptor.getValue().apply("M\tfile");
        Mockito.verify(logPromise).then(logCaptor.capture());
        logCaptor.getValue().apply(null);
        Mockito.verify(view).setEnableAmendCheckBox(true);
        Mockito.verify(view).setEnablePushAfterCommitCheckBox(true);
        Mockito.verify(selectableChangesPanelPresenter).show(ArgumentMatchers.eq(alteredFiles));
        Mockito.verify(view).focusInMessageField();
        Mockito.verify(view).setEnableCommitButton(ArgumentMatchers.eq(BaseTest.DISABLE_BUTTON));
        Mockito.verify(view).getCommitMessage();
        Mockito.verify(view).showDialog();
    }

    @Test
    public void shouldShowUntrackedFilesOnInitialCommit() throws Exception {
        final String diff = "A\tfile";
        final AlteredFiles alteredFiles = new AlteredFiles(project, diff);
        PromiseError error = Mockito.mock(PromiseError.class);
        ServerException exception = Mockito.mock(ServerException.class);
        Mockito.when(exception.getErrorCode()).thenReturn(INIT_COMMIT_WAS_NOT_PERFORMED);
        Mockito.when(error.getCause()).thenReturn(exception);
        Status status = Mockito.mock(Status.class);
        Mockito.when(status.getUntracked()).thenReturn(Collections.singletonList("file"));
        presenter.showDialog(project);
        Mockito.verify(stringPromise).then(stringCaptor.capture());
        stringCaptor.getValue().apply(null);
        Mockito.verify(logPromise).catchError(promiseErrorCaptor.capture());
        promiseErrorCaptor.getValue().apply(error);
        Mockito.verify(statusPromise).then(statusPromiseCaptor.capture());
        statusPromiseCaptor.getValue().apply(status);
        Mockito.verify(view).setEnableAmendCheckBox(false);
        Mockito.verify(view).setEnablePushAfterCommitCheckBox(false);
        Mockito.verify(selectableChangesPanelPresenter).show(ArgumentMatchers.eq(alteredFiles));
        Mockito.verify(view).focusInMessageField();
        Mockito.verify(view).setEnableCommitButton(ArgumentMatchers.eq(BaseTest.DISABLE_BUTTON));
        Mockito.verify(view).getCommitMessage();
        Mockito.verify(view).showDialog();
    }

    @Test
    public void shouldEnableCommitButton() throws Exception {
        Mockito.when(view.getCommitMessage()).thenReturn("foo");
        presenter.showDialog(project);
        Mockito.verify(stringPromise).then(stringCaptor.capture());
        stringCaptor.getValue().apply("M\tfile");
        Mockito.verify(logPromise).then(logCaptor.capture());
        logCaptor.getValue().apply(null);
        Mockito.verify(view).setEnableCommitButton(ArgumentMatchers.eq(BaseTest.ENABLE_BUTTON));
    }

    @Test
    public void shouldCloseWhenCancelButtonClicked() throws Exception {
        presenter.onCancelClicked();
        Mockito.verify(view).close();
    }

    @Test
    public void shouldDisableCommitButtonOnEmptyMessage() throws Exception {
        Mockito.when(view.getCommitMessage()).thenReturn(BaseTest.EMPTY_TEXT);
        presenter.onValueChanged();
        Mockito.verify(view).setEnableCommitButton(ArgumentMatchers.eq(BaseTest.DISABLE_BUTTON));
    }

    @Test
    public void shouldEnableCommitButtonOnAmendAndNoFilesChecked() throws Exception {
        Mockito.when(view.getCommitMessage()).thenReturn(CommitPresenterTest.COMMIT_TEXT);
        Mockito.when(view.isAmend()).thenReturn(true);
        presenter.onValueChanged();
        Mockito.verify(view).setEnableCommitButton(ArgumentMatchers.eq(BaseTest.ENABLE_BUTTON));
    }

    @Test
    public void shouldPrintSuccessMessageOnAddToIndexAndCommitSuccess() throws Exception {
        Revision revision = Mockito.mock(Revision.class);
        GitUser gitUser = Mockito.mock(GitUser.class);
        Mockito.when(gitUser.getName()).thenReturn("commiterName");
        Mockito.when(revision.getId()).thenReturn("commitId");
        Mockito.when(revision.getCommitter()).thenReturn(gitUser);
        Mockito.when(constant.commitMessage(ArgumentMatchers.eq("commitId"), ArgumentMatchers.anyString())).thenReturn("commitMessage");
        Mockito.when(constant.commitUser(ArgumentMatchers.anyString())).thenReturn("commitUser");
        presenter.showDialog(project);
        presenter.onCommitClicked();
        Mockito.verify(voidPromise).then(voidPromiseCaptor.capture());
        voidPromiseCaptor.getValue().apply(null);
        Mockito.verify(revisionPromise).then(revisionCaptor.capture());
        revisionCaptor.getValue().apply(revision);
        Mockito.verify(console).print("commitMessage commitUser");
        Mockito.verify(notificationManager).notify("commitMessage commitUser");
        Mockito.verify(view).close();
    }

    @Test
    public void shouldPrintFailMessageOnOnAddToIndexSuccessButCommitFailed() throws Exception {
        Mockito.when(constant.commitFailed()).thenReturn("commitFailed");
        presenter.showDialog(project);
        presenter.onCommitClicked();
        Mockito.verify(voidPromise).then(voidPromiseCaptor.capture());
        voidPromiseCaptor.getValue().apply(null);
        Mockito.verify(revisionPromise).catchError(promiseErrorCaptor.capture());
        promiseErrorCaptor.getValue().apply(promiseError);
        Mockito.verify(console).printError("error");
        Mockito.verify(notificationManager).notify(ArgumentMatchers.eq("commitFailed"), ArgumentMatchers.eq("error"), ArgumentMatchers.eq(FAIL), ArgumentMatchers.eq(FLOAT_MODE));
    }

    @Test
    public void shouldShowErrorNotificationOnAddToIndexFailed() throws Exception {
        Mockito.when(constant.addFailed()).thenReturn("addFailed");
        presenter.showDialog(project);
        presenter.onCommitClicked();
        Mockito.verify(voidPromise).catchError(promiseErrorCaptor.capture());
        promiseErrorCaptor.getValue().apply(promiseError);
        Mockito.verify(notificationManager).notify(ArgumentMatchers.eq("addFailed"), ArgumentMatchers.eq(FAIL), ArgumentMatchers.eq(FLOAT_MODE));
    }

    @Test
    public void shouldShowPushSuccessNotificationIfPushAfterCommitChecked() throws Exception {
        Mockito.when(constant.pushSuccess(ArgumentMatchers.anyString())).thenReturn("pushSuccess");
        Mockito.when(view.isPushAfterCommit()).thenReturn(true);
        Mockito.when(view.getRemoteBranch()).thenReturn("origin/master");
        presenter.showDialog(project);
        presenter.onCommitClicked();
        Mockito.verify(voidPromise).then(voidPromiseCaptor.capture());
        voidPromiseCaptor.getValue().apply(null);
        Mockito.verify(revisionPromise).then(revisionCaptor.capture());
        revisionCaptor.getValue().apply(Mockito.mock(Revision.class));
        Mockito.verify(pushPromise).then(pushPromiseCaptor.capture());
        pushPromiseCaptor.getValue().apply(null);
        Mockito.verify(notificationManager).notify(ArgumentMatchers.eq("pushSuccess"), ArgumentMatchers.eq(SUCCESS), ArgumentMatchers.eq(FLOAT_MODE));
    }

    @Test
    public void shouldShowPushFailedNotification() throws Exception {
        Mockito.when(constant.pushFail()).thenReturn("pushFail");
        Mockito.when(view.isPushAfterCommit()).thenReturn(true);
        Mockito.when(view.getRemoteBranch()).thenReturn("origin/master");
        presenter.showDialog(project);
        presenter.onCommitClicked();
        Mockito.verify(voidPromise).then(voidPromiseCaptor.capture());
        voidPromiseCaptor.getValue().apply(null);
        Mockito.verify(revisionPromise).then(revisionCaptor.capture());
        revisionCaptor.getValue().apply(Mockito.mock(Revision.class));
        Mockito.verify(pushPromise).catchError(promiseErrorCaptor.capture());
        promiseErrorCaptor.getValue().apply(promiseError);
        Mockito.verify(notificationManager).notify(ArgumentMatchers.eq("pushFail"), ArgumentMatchers.eq(FAIL), ArgumentMatchers.eq(FLOAT_MODE));
    }
}

