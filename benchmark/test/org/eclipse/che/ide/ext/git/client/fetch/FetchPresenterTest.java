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
package org.eclipse.che.ide.ext.git.client.fetch;


import java.util.ArrayList;
import java.util.List;
import org.eclipse.che.api.git.shared.Branch;
import org.eclipse.che.api.git.shared.Remote;
import org.eclipse.che.ide.api.auth.OAuthServiceClient;
import org.eclipse.che.ide.ext.git.client.BaseTest;
import org.eclipse.che.ide.ext.git.client.BranchSearcher;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Testing {@link FetchPresenter} functionality.
 *
 * @author Andrey Plotnikov
 * @author Vlad Zhukovskyi
 */
public class FetchPresenterTest extends BaseTest {
    public static final boolean NO_REMOVE_DELETE_REFS = false;

    public static final boolean FETCH_ALL_BRANCHES = true;

    @Mock
    private FetchView view;

    @Mock
    private Branch branch;

    @Mock
    private BranchSearcher branchSearcher;

    @Mock
    private OAuthServiceClient oAuthServiceClient;

    private FetchPresenter presenter;

    @Test
    public void testShowDialogWhenBranchListRequestIsSuccessful() throws Exception {
        final List<Remote> remotes = new ArrayList<>();
        remotes.add(Mockito.mock(Remote.class));
        final List<Branch> branches = new ArrayList<>();
        branches.add(branch);
        presenter.showDialog(project);
        Mockito.verify(remoteListPromise).then(remoteListCaptor.capture());
        remoteListCaptor.getValue().apply(remotes);
        Mockito.verify(branchListPromise).then(branchListCaptor.capture());
        branchListCaptor.getValue().apply(branches);
        Mockito.verify(branchListPromise, Mockito.times(2)).then(branchListCaptor.capture());
        branchListCaptor.getValue().apply(branches);
        Mockito.verify(view).setEnableFetchButton(ArgumentMatchers.eq(BaseTest.ENABLE_BUTTON));
        Mockito.verify(view).setRepositories(ArgumentMatchers.anyObject());
        Mockito.verify(view).setRemoveDeleteRefs(ArgumentMatchers.eq(FetchPresenterTest.NO_REMOVE_DELETE_REFS));
        Mockito.verify(view).setFetchAllBranches(ArgumentMatchers.eq(FetchPresenterTest.FETCH_ALL_BRANCHES));
        Mockito.verify(view).showDialog();
        Mockito.verify(view).setRemoteBranches(ArgumentMatchers.anyObject());
        Mockito.verify(view).setLocalBranches(ArgumentMatchers.anyObject());
    }

    @Test
    public void testOnValueChanged() throws Exception {
        Mockito.when(view.isFetchAllBranches()).thenReturn(FetchPresenterTest.FETCH_ALL_BRANCHES);
        presenter.onValueChanged();
        Mockito.verify(view).setEnableLocalBranchField(ArgumentMatchers.eq(BaseTest.DISABLE_FIELD));
        Mockito.verify(view).setEnableRemoteBranchField(ArgumentMatchers.eq(BaseTest.DISABLE_FIELD));
        Mockito.when(view.isFetchAllBranches()).thenReturn((!(FetchPresenterTest.FETCH_ALL_BRANCHES)));
        presenter.onValueChanged();
        Mockito.verify(view).setEnableLocalBranchField(ArgumentMatchers.eq(BaseTest.ENABLE_FIELD));
        Mockito.verify(view).setEnableRemoteBranchField(ArgumentMatchers.eq(BaseTest.ENABLE_FIELD));
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();
        Mockito.verify(view).close();
    }

    @Test
    public void shouldRefreshRemoteBranchesWhenRepositoryIsChanged() throws Exception {
        final List<Remote> remotes = new ArrayList<>();
        remotes.add(Mockito.mock(Remote.class));
        final List<Branch> branches = new ArrayList<>();
        branches.add(branch);
        Mockito.when(branch.isActive()).thenReturn(BaseTest.ACTIVE_BRANCH);
        presenter.showDialog(project);
        presenter.onRemoteRepositoryChanged();
        Mockito.verify(branchListPromise).then(branchListCaptor.capture());
        branchListCaptor.getValue().apply(branches);
        Mockito.verify(branchListPromise, Mockito.times(2)).then(branchListCaptor.capture());
        branchListCaptor.getValue().apply(branches);
        Mockito.verify(view).setRemoteBranches(ArgumentMatchers.anyObject());
        Mockito.verify(view).setLocalBranches(ArgumentMatchers.anyObject());
        Mockito.verify(view).selectRemoteBranch(ArgumentMatchers.anyString());
    }
}

