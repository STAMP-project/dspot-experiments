/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.jenkins.results.parser;


import org.junit.Test;


/**
 *
 *
 * @author Michael Hashimoto
 */
public class RemoteGitBranchTest extends GitRefTest {
    @Test
    public void testGetName() {
        RemoteGitBranch remoteGitBranch = _getRemoteGitBranch();
        if (!(GitRefTest.REF_NAME.equals(remoteGitBranch.getName()))) {
            errorCollector.addError(new Throwable(getMismatchMessage(GitRefTest.REF_NAME, remoteGitBranch.getName(), "branch name")));
        }
    }

    @Test
    public void testGetRemoteGitRepository() {
        RemoteGitBranch remoteGitBranch = _getRemoteGitBranch();
        RemoteGitRepository remoteGitRepository = _getRemoteGitRepository();
        if (!(remoteGitRepository.equals(remoteGitBranch.getRemoteGitRepository()))) {
            errorCollector.addError(new Throwable("The remote Git repository does not match"));
        }
    }

    @Test
    public void testGetRemoteURL() {
        RemoteGitBranch remoteGitBranch = _getRemoteGitBranch();
        String remoteURL = JenkinsResultsParserUtil.combine("git@", GitRepositoryTest.REPOSITORY_HOSTNAME, ":", GitRepositoryTest.REPOSITORY_USERNAME, "/", GitRepositoryTest.REPOSITORY_NAME);
        if (!(remoteURL.equals(remoteGitBranch.getRemoteURL()))) {
            errorCollector.addError(new Throwable(getMismatchMessage(remoteURL, remoteGitBranch.getRemoteURL(), "remote URL")));
        }
    }

    @Test
    public void testGetRepositoryName() {
        RemoteGitBranch remoteGitBranch = _getRemoteGitBranch();
        if (!(GitRepositoryTest.REPOSITORY_NAME.equals(remoteGitBranch.getRepositoryName()))) {
            errorCollector.addError(new Throwable(getMismatchMessage(GitRepositoryTest.REPOSITORY_NAME, remoteGitBranch.getRepositoryName(), "repository name")));
        }
    }

    @Test
    public void testGetSHA() {
        RemoteGitBranch remoteGitBranch = _getRemoteGitBranch();
        if (!(GitRefTest.REF_SHA.equals(remoteGitBranch.getSHA()))) {
            errorCollector.addError(new Throwable(getMismatchMessage(GitRefTest.REF_SHA, remoteGitBranch.getSHA(), "branch SHA")));
        }
    }

    @Test
    public void testGetUsername() {
        RemoteGitBranch remoteGitBranch = _getRemoteGitBranch();
        if (!(GitRepositoryTest.REPOSITORY_USERNAME.equals(remoteGitBranch.getUsername()))) {
            errorCollector.addError(new Throwable(getMismatchMessage(GitRepositoryTest.REPOSITORY_USERNAME, remoteGitBranch.getUsername(), "username")));
        }
    }
}

