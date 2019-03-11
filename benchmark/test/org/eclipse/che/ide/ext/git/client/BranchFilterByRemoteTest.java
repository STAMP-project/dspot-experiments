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
package org.eclipse.che.ide.ext.git.client;


import org.eclipse.che.api.git.shared.Branch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Sergii Leschenko
 */
@RunWith(MockitoJUnitRunner.class)
public class BranchFilterByRemoteTest {
    private static final String REMOTE_NAME = "origin";

    private static final String ANOTHER_REMOTE_NAME = "another";

    BranchFilterByRemote branchFilterByRemote;

    Branch branch = Mockito.mock(Branch.class);

    @Test
    public void shouldDetermineLinkBranchToRemote() throws Exception {
        Mockito.when(branch.getName()).thenReturn((("refs/remotes/" + (BranchFilterByRemoteTest.REMOTE_NAME)) + "/master"));
        Assert.assertTrue(branchFilterByRemote.isLinkedTo(branch));
    }

    @Test
    public void shouldNotDetermineLinkBranchToRemote() throws Exception {
        Mockito.when(branch.getName()).thenReturn((("refs/remotes/" + (BranchFilterByRemoteTest.ANOTHER_REMOTE_NAME)) + "/master"));
        Assert.assertFalse(branchFilterByRemote.isLinkedTo(branch));
    }

    @Test
    public void shouldDetermineSimpleBranchNameWhenBranchNameContainsRefs() throws Exception {
        String branchName = "master";
        Mockito.when(branch.getName()).thenReturn(((("refs/remotes/" + (BranchFilterByRemoteTest.REMOTE_NAME)) + "/") + branchName));
        Assert.assertEquals(branchFilterByRemote.getBranchNameWithoutRefs(branch), branchName);
    }

    @Test
    public void shouldJustReturnBranchNameWhenBranchNameContainsRefsToAnotherRemote() throws Exception {
        String branchName = "master";
        Mockito.when(branch.getName()).thenReturn(((("refs/remotes/" + (BranchFilterByRemoteTest.ANOTHER_REMOTE_NAME)) + "/") + branchName));
        Assert.assertNotEquals(branchFilterByRemote.getBranchNameWithoutRefs(branch), branchName);
    }

    @Test
    public void shouldDetermineSimpleBranchNameWhenBranchNameIsSimpleName() throws Exception {
        String branchName = "master";
        Mockito.when(branch.getName()).thenReturn(branchName);
        Assert.assertEquals(branchFilterByRemote.getBranchNameWithoutRefs(branch), branchName);
    }
}

