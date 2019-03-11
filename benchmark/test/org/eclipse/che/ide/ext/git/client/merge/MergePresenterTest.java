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
package org.eclipse.che.ide.ext.git.client.merge;


import java.util.ArrayList;
import java.util.List;
import org.eclipse.che.api.git.shared.Branch;
import org.eclipse.che.api.git.shared.MergeResult;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.ide.api.resources.VirtualFile;
import org.eclipse.che.ide.ext.git.client.BaseTest;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Testing {@link MergePresenter} functionality.
 *
 * @author Andrey Plotnikov
 * @author Vlad Zhukovskyi
 */
public class MergePresenterTest extends BaseTest {
    public static final String DISPLAY_NAME = "displayName";

    @Mock
    private MergeView view;

    @Mock
    private MergeResult mergeResult;

    @Mock
    private Reference selectedReference;

    @Mock
    private VirtualFile file;

    @Mock
    private Promise<List<Branch>> remoteListBranchPromise;

    @Captor
    private ArgumentCaptor<Operation<List<Branch>>> remoteListBranchCaptor;

    @Captor
    protected ArgumentCaptor<Operation<PromiseError>> secondPromiseErrorCaptor;

    private MergePresenter presenter;

    @Test
    public void testShowDialogWhenAllOperationsAreSuccessful() throws Exception {
        final List<Branch> branches = new ArrayList<>();
        branches.add(Mockito.mock(Branch.class));
        presenter.showDialog(project);
        Mockito.verify(branchListPromise).then(branchListCaptor.capture());
        branchListCaptor.getValue().apply(branches);
        Mockito.verify(remoteListBranchPromise).then(remoteListBranchCaptor.capture());
        remoteListBranchCaptor.getValue().apply(branches);
        Mockito.verify(view).setEnableMergeButton(ArgumentMatchers.eq(BaseTest.DISABLE_BUTTON));
        Mockito.verify(view).showDialog();
        Mockito.verify(view).setRemoteBranches(ArgumentMatchers.anyObject());
        Mockito.verify(view).setLocalBranches(ArgumentMatchers.anyObject());
        Mockito.verify(console, Mockito.never()).printError(ArgumentMatchers.anyString());
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();
        Mockito.verify(view).close();
    }

    @Test
    public void testDialogWhenListOfBranchesAreEmpty() throws Exception {
        final ArrayList<Reference> emptyReferenceList = new ArrayList<>();
        final List<Branch> emptyBranchList = new ArrayList<>();
        presenter.showDialog(project);
        Mockito.verify(branchListPromise).then(branchListCaptor.capture());
        branchListCaptor.getValue().apply(emptyBranchList);
        Mockito.verify(remoteListBranchPromise).then(remoteListBranchCaptor.capture());
        remoteListBranchCaptor.getValue().apply(emptyBranchList);
        Mockito.verify(view).showDialog();
        Mockito.verify(view).setLocalBranches(ArgumentMatchers.eq(emptyReferenceList));
        Mockito.verify(view).setRemoteBranches(ArgumentMatchers.eq(emptyReferenceList));
    }
}

