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
package org.eclipse.che.ide.ext.git.client.branch;


import BranchListMode.LIST_LOCAL;
import java.util.Collections;
import java.util.List;
import org.eclipse.che.api.git.shared.Branch;
import org.eclipse.che.api.git.shared.CheckoutRequest;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.workspace.shared.dto.ProjectConfigDto;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.git.client.BaseTest;
import org.eclipse.che.ide.ext.git.client.patcher.WindowPatcher;
import org.eclipse.che.ide.resource.Path;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmCallback;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmDialog;
import org.eclipse.che.ide.ui.dialogs.input.InputCallback;
import org.eclipse.che.ide.ui.dialogs.input.InputDialog;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Testing {@link BranchPresenter} functionality.
 *
 * @author Andrey Plotnikov
 * @author Vlad Zhukovskyi
 */
public class BranchPresenterTest extends BaseTest {
    @Captor
    private ArgumentCaptor<InputCallback> inputCallbackCaptor;

    @Captor
    private ArgumentCaptor<ConfirmCallback> confirmCallbackCaptor;

    private static final String BRANCH_NAME = "branchName";

    private static final String REMOTE_BRANCH_NAME = "origin/branchName";

    private static final boolean IS_REMOTE = true;

    private static final boolean IS_ACTIVE = true;

    @Mock
    private BranchView view;

    @Mock
    private Branch selectedBranch;

    @Mock
    private DialogFactory dialogFactory;

    @Mock
    private DtoFactory dtoFactory;

    @Mock
    private CheckoutRequest checkoutRequest;

    private BranchPresenter presenter;

    @Test
    public void testShowBranchesWhenGetBranchesRequestIsSuccessful() throws Exception {
        final List<Branch> branches = Collections.singletonList(selectedBranch);
        Mockito.when(service.branchList(ArgumentMatchers.anyObject(), ArgumentMatchers.anyObject())).thenReturn(branchListPromise);
        Mockito.when(branchListPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(branchListPromise);
        Mockito.when(branchListPromise.catchError(ArgumentMatchers.any(Operation.class))).thenReturn(branchListPromise);
        presenter.showBranches(project);
        Mockito.verify(branchListPromise).then(branchListCaptor.capture());
        branchListCaptor.getValue().apply(branches);
        Mockito.verify(view).showDialogIfClosed();
        Mockito.verify(view).setBranches(ArgumentMatchers.eq(branches));
        Mockito.verify(console, Mockito.never()).printError(ArgumentMatchers.anyString());
        Mockito.verify(notificationManager, Mockito.never()).notify(ArgumentMatchers.anyString(), ArgumentMatchers.any(ProjectConfigDto.class));
        Mockito.verify(constant, Mockito.never()).branchesListFailed();
    }

    @Test
    public void shouldShowLocalBranchesWheBranchesFilterIsSetToLocal() throws Exception {
        // given
        final List<Branch> branches = Collections.singletonList(selectedBranch);
        Mockito.when(service.branchList(ArgumentMatchers.anyObject(), ArgumentMatchers.eq(LIST_LOCAL))).thenReturn(branchListPromise);
        Mockito.when(branchListPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(branchListPromise);
        Mockito.when(branchListPromise.catchError(ArgumentMatchers.any(Operation.class))).thenReturn(branchListPromise);
        Mockito.when(view.getFilterValue()).thenReturn("local");
        // when
        presenter.showBranches(project);
        Mockito.verify(branchListPromise).then(branchListCaptor.capture());
        branchListCaptor.getValue().apply(branches);
        // then
        Mockito.verify(view).showDialogIfClosed();
        Mockito.verify(view).setBranches(ArgumentMatchers.eq(branches));
        Mockito.verify(console, Mockito.never()).printError(ArgumentMatchers.anyString());
        Mockito.verify(notificationManager, Mockito.never()).notify(ArgumentMatchers.anyString(), ArgumentMatchers.any(ProjectConfigDto.class));
        Mockito.verify(constant, Mockito.never()).branchesListFailed();
    }

    @Test
    public void shouldShowRemoteBranchesWheBranchesFilterIsSetToRemote() throws Exception {
        // given
        final List<Branch> branches = Collections.singletonList(selectedBranch);
        Mockito.when(service.branchList(ArgumentMatchers.anyObject(), ArgumentMatchers.eq(LIST_LOCAL))).thenReturn(branchListPromise);
        Mockito.when(branchListPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(branchListPromise);
        Mockito.when(branchListPromise.catchError(ArgumentMatchers.any(Operation.class))).thenReturn(branchListPromise);
        Mockito.when(view.getFilterValue()).thenReturn("remote");
        // when
        presenter.showBranches(project);
        Mockito.verify(branchListPromise).then(branchListCaptor.capture());
        branchListCaptor.getValue().apply(branches);
        // then
        Mockito.verify(view).showDialogIfClosed();
        Mockito.verify(view).setBranches(ArgumentMatchers.eq(branches));
        Mockito.verify(console, Mockito.never()).printError(ArgumentMatchers.anyString());
        Mockito.verify(notificationManager, Mockito.never()).notify(ArgumentMatchers.anyString(), ArgumentMatchers.any(ProjectConfigDto.class));
        Mockito.verify(constant, Mockito.never()).branchesListFailed();
    }

    @Test
    public void testOnCloseClicked() throws Exception {
        presenter.onClose();
        Mockito.verify(view).closeDialogIfShowing();
    }

    @Test
    public void testOnRenameClickedWhenLocalBranchSelected() throws Exception {
        Mockito.reset(selectedBranch);
        Mockito.when(service.branchRename(ArgumentMatchers.anyObject(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(voidPromise);
        Mockito.when(voidPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(voidPromise);
        Mockito.when(voidPromise.catchError(ArgumentMatchers.any(Operation.class))).thenReturn(voidPromise);
        Mockito.when(selectedBranch.getDisplayName()).thenReturn(BranchPresenterTest.BRANCH_NAME);
        Mockito.when(selectedBranch.isRemote()).thenReturn(false);
        InputDialog inputDialog = Mockito.mock(InputDialog.class);
        Mockito.when(dialogFactory.createInputDialog(ArgumentMatchers.anyObject(), ArgumentMatchers.anyObject(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyObject(), ArgumentMatchers.anyObject())).thenReturn(inputDialog);
        selectBranch();
        presenter.onRenameClicked();
        Mockito.verify(dialogFactory).createInputDialog(ArgumentMatchers.eq(null), ArgumentMatchers.eq(null), ArgumentMatchers.eq("branchName"), ArgumentMatchers.eq(0), ArgumentMatchers.eq("branchName".length()), inputCallbackCaptor.capture(), ArgumentMatchers.eq(null));
        InputCallback inputCallback = inputCallbackCaptor.getValue();
        inputCallback.accepted(WindowPatcher.RETURNED_MESSAGE);
        Mockito.verify(selectedBranch, Mockito.times(2)).getDisplayName();
        Mockito.verify(dialogFactory, Mockito.never()).createConfirmDialog(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyObject(), ArgumentMatchers.anyObject());
        Mockito.verify(console, Mockito.never()).printError(ArgumentMatchers.anyString());
        Mockito.verify(notificationManager, Mockito.never()).notify(ArgumentMatchers.anyString());
        Mockito.verify(constant, Mockito.never()).branchRenameFailed();
    }

    @Test
    public void testOnRenameClickedWhenRemoteBranchSelectedAndUserConfirmRename() throws Exception {
        Mockito.reset(selectedBranch);
        Mockito.when(service.branchRename(ArgumentMatchers.anyObject(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(voidPromise);
        Mockito.when(voidPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(voidPromise);
        Mockito.when(voidPromise.catchError(ArgumentMatchers.any(Operation.class))).thenReturn(voidPromise);
        Mockito.when(selectedBranch.getDisplayName()).thenReturn(BranchPresenterTest.REMOTE_BRANCH_NAME);
        Mockito.when(selectedBranch.isRemote()).thenReturn(true);
        InputDialog inputDialog = Mockito.mock(InputDialog.class);
        Mockito.when(dialogFactory.createInputDialog(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyObject(), ArgumentMatchers.anyObject())).thenReturn(inputDialog);
        ConfirmDialog confirmDialog = Mockito.mock(ConfirmDialog.class);
        Mockito.when(dialogFactory.createConfirmDialog(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyObject(), ArgumentMatchers.anyObject())).thenReturn(confirmDialog);
        selectBranch();
        presenter.onRenameClicked();
        Mockito.verify(dialogFactory).createConfirmDialog(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), confirmCallbackCaptor.capture(), ArgumentMatchers.anyObject());
        ConfirmCallback confirmCallback = confirmCallbackCaptor.getValue();
        confirmCallback.accepted();
        Mockito.verify(dialogFactory).createInputDialog(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), inputCallbackCaptor.capture(), ArgumentMatchers.anyObject());
        InputCallback inputCallback = inputCallbackCaptor.getValue();
        inputCallback.accepted(WindowPatcher.RETURNED_MESSAGE);
        Mockito.verify(voidPromise).then(voidPromiseCaptor.capture());
        voidPromiseCaptor.getValue().apply(null);
        Mockito.verify(selectedBranch, Mockito.times(2)).getDisplayName();
        Mockito.verify(service, Mockito.times(2)).branchList(ArgumentMatchers.anyObject(), ArgumentMatchers.eq(LIST_ALL));
        Mockito.verify(console, Mockito.never()).printError(ArgumentMatchers.anyString());
        Mockito.verify(notificationManager, Mockito.never()).notify(ArgumentMatchers.anyString());
        Mockito.verify(constant, Mockito.never()).branchRenameFailed();
    }

    @Test
    public void testOnDeleteClickedWhenBranchDeleteRequestIsSuccessful() throws Exception {
        Mockito.when(service.branchDelete(ArgumentMatchers.any(Path.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean())).thenReturn(voidPromise);
        Mockito.when(voidPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(voidPromise);
        Mockito.when(voidPromise.catchError(ArgumentMatchers.any(Operation.class))).thenReturn(voidPromise);
        selectBranch();
        presenter.onDeleteClicked();
        Mockito.verify(voidPromise).then(voidPromiseCaptor.capture());
        voidPromiseCaptor.getValue().apply(null);
        Mockito.verify(selectedBranch).getName();
        Mockito.verify(service, Mockito.times(2)).branchList(ArgumentMatchers.anyObject(), ArgumentMatchers.eq(LIST_ALL));
        Mockito.verify(constant, Mockito.never()).branchDeleteFailed();
        Mockito.verify(console, Mockito.never()).printError(ArgumentMatchers.anyString());
        Mockito.verify(notificationManager, Mockito.never()).notify(ArgumentMatchers.anyString());
    }

    @Test
    public void testOnCheckoutClickedWhenSelectedNotRemoteBranch() throws Exception {
        Mockito.when(service.checkout(ArgumentMatchers.any(Path.class), ArgumentMatchers.any(CheckoutRequest.class))).thenReturn(stringPromise);
        Mockito.when(stringPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(stringPromise);
        Mockito.when(stringPromise.catchError(ArgumentMatchers.any(Operation.class))).thenReturn(stringPromise);
        Mockito.when(selectedBranch.isRemote()).thenReturn(false);
        Mockito.when(dtoFactory.createDto(CheckoutRequest.class)).thenReturn(checkoutRequest);
        selectBranch();
        presenter.onCheckoutClicked();
        Mockito.verify(stringPromise).then(stringCaptor.capture());
        stringCaptor.getValue().apply(null);
        Mockito.verify(checkoutRequest).setName(ArgumentMatchers.eq(BranchPresenterTest.BRANCH_NAME));
        Mockito.verifyNoMoreInteractions(checkoutRequest);
    }

    @Test
    public void testOnCheckoutClickedWhenSelectedRemoteBranch() throws Exception {
        Mockito.when(service.checkout(ArgumentMatchers.any(Path.class), ArgumentMatchers.any(CheckoutRequest.class))).thenReturn(stringPromise);
        Mockito.when(stringPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(stringPromise);
        Mockito.when(stringPromise.catchError(ArgumentMatchers.any(Operation.class))).thenReturn(stringPromise);
        Mockito.when(dtoFactory.createDto(CheckoutRequest.class)).thenReturn(checkoutRequest);
        selectBranch();
        presenter.onCheckoutClicked();
        Mockito.verify(stringPromise).then(stringCaptor.capture());
        stringCaptor.getValue().apply(null);
        Mockito.verify(checkoutRequest).setTrackBranch(ArgumentMatchers.eq(BranchPresenterTest.BRANCH_NAME));
        Mockito.verifyNoMoreInteractions(checkoutRequest);
    }

    @Test
    public void testOnCreateClickedWhenBranchCreateRequestIsSuccessful() throws Exception {
        Mockito.when(service.branchCreate(ArgumentMatchers.any(Path.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(branchPromise);
        Mockito.when(branchPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(branchPromise);
        Mockito.when(branchPromise.catchError(ArgumentMatchers.any(Operation.class))).thenReturn(branchPromise);
        InputDialog inputDialog = Mockito.mock(InputDialog.class);
        Mockito.when(dialogFactory.createInputDialog(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyObject(), ArgumentMatchers.anyObject())).thenReturn(inputDialog);
        presenter.showBranches(project);
        presenter.onCreateClicked();
        Mockito.verify(dialogFactory).createInputDialog(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), inputCallbackCaptor.capture(), ArgumentMatchers.anyObject());
        InputCallback inputCallback = inputCallbackCaptor.getValue();
        inputCallback.accepted(BranchPresenterTest.BRANCH_NAME);
        Mockito.verify(branchPromise).then(branchCaptor.capture());
        branchCaptor.getValue().apply(selectedBranch);
        Mockito.verify(constant).branchTypeNew();
        Mockito.verify(service).branchCreate(ArgumentMatchers.any(Path.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.verify(service, Mockito.times(2)).branchList(ArgumentMatchers.anyObject(), ArgumentMatchers.eq(LIST_ALL));
    }

    @Test
    public void checkoutButtonShouldBeEnabled() throws Exception {
        Mockito.when(selectedBranch.isActive()).thenReturn(false);
        presenter.onBranchSelected(selectedBranch);
        Mockito.verify(view).setEnableCheckoutButton(ArgumentMatchers.eq(BaseTest.ENABLE_BUTTON));
    }

    @Test
    public void checkoutButtonShouldBeDisabled() throws Exception {
        Mockito.when(selectedBranch.isActive()).thenReturn(true);
        presenter.onBranchSelected(selectedBranch);
        Mockito.verify(view).setEnableCheckoutButton(ArgumentMatchers.eq(BaseTest.DISABLE_BUTTON));
    }

    @Test
    public void renameButtonShouldBeEnabledWhenLocalBranchSelected() throws Exception {
        Mockito.when(selectedBranch.isRemote()).thenReturn(false);
        presenter.onBranchSelected(selectedBranch);
        Mockito.verify(view).setEnableRenameButton(ArgumentMatchers.eq(BaseTest.ENABLE_BUTTON));
    }

    @Test
    public void renameButtonShouldBeEnabledWhenRemoteBranchSelected() throws Exception {
        Mockito.when(selectedBranch.isRemote()).thenReturn(true);
        presenter.onBranchSelected(selectedBranch);
        Mockito.verify(view).setEnableRenameButton(ArgumentMatchers.eq(BaseTest.ENABLE_BUTTON));
    }

    @Test
    public void deleteButtonShouldBeEnabled() throws Exception {
        Mockito.when(selectedBranch.isActive()).thenReturn(false);
        presenter.onBranchSelected(selectedBranch);
        Mockito.verify(view).setEnableDeleteButton(ArgumentMatchers.eq(BaseTest.ENABLE_BUTTON));
    }

    @Test
    public void deleteButtonShouldBeDisabled() throws Exception {
        Mockito.when(selectedBranch.isActive()).thenReturn(true);
        presenter.onBranchSelected(selectedBranch);
        Mockito.verify(view).setEnableDeleteButton(ArgumentMatchers.eq(BaseTest.DISABLE_BUTTON));
    }

    @Test
    public void checkoutDeleteRenameButtonsShouldBeDisabled() throws Exception {
        presenter.onBranchUnselected();
        Mockito.verify(view).setEnableCheckoutButton(ArgumentMatchers.eq(BaseTest.DISABLE_BUTTON));
        Mockito.verify(view).setEnableDeleteButton(ArgumentMatchers.eq(BaseTest.DISABLE_BUTTON));
        Mockito.verify(view).setEnableRenameButton(ArgumentMatchers.eq(BaseTest.DISABLE_BUTTON));
    }
}

