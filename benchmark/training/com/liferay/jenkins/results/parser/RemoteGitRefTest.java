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
public class RemoteGitRefTest extends GitRefTest {
    @Test
    public void testGetName() {
        RemoteGitRef remoteGitRef = _getRemoteGitRef();
        if (!(GitRefTest.REF_NAME.equals(remoteGitRef.getName()))) {
            errorCollector.addError(new Throwable(getMismatchMessage(GitRefTest.REF_NAME, remoteGitRef.getName(), "ref name")));
        }
    }

    @Test
    public void testGetRemoteGitRepository() {
        RemoteGitRef remoteGitRef = _getRemoteGitRef();
        RemoteGitRepository remoteGitRepository = _getRemoteGitRepository();
        if (!(remoteGitRepository.equals(remoteGitRef.getRemoteGitRepository()))) {
            errorCollector.addError(new Throwable("The remote Git repository does not match"));
        }
    }

    @Test
    public void testGetRemoteURL() {
        RemoteGitRef remoteGitRef = _getRemoteGitRef();
        String remoteURL = JenkinsResultsParserUtil.combine("git@", GitRepositoryTest.REPOSITORY_HOSTNAME, ":", GitRepositoryTest.REPOSITORY_USERNAME, "/", GitRepositoryTest.REPOSITORY_NAME);
        if (!(remoteURL.equals(remoteGitRef.getRemoteURL()))) {
            errorCollector.addError(new Throwable(getMismatchMessage(remoteURL, remoteGitRef.getRemoteURL(), "remote URL")));
        }
    }

    @Test
    public void testGetRepositoryName() {
        RemoteGitRef remoteGitRef = _getRemoteGitRef();
        if (!(GitRepositoryTest.REPOSITORY_NAME.equals(remoteGitRef.getRepositoryName()))) {
            errorCollector.addError(new Throwable(getMismatchMessage(GitRepositoryTest.REPOSITORY_NAME, remoteGitRef.getRepositoryName(), "repository name")));
        }
    }

    @Test
    public void testGetSHA() {
        RemoteGitRef remoteGitRef = _getRemoteGitRef();
        if (!(GitRefTest.REF_SHA.equals(remoteGitRef.getSHA()))) {
            errorCollector.addError(new Throwable(getMismatchMessage(GitRefTest.REF_SHA, remoteGitRef.getSHA(), "ref SHA")));
        }
    }

    @Test
    public void testGetUsername() {
        RemoteGitRef remoteGitRef = _getRemoteGitRef();
        if (!(GitRepositoryTest.REPOSITORY_USERNAME.equals(remoteGitRef.getUsername()))) {
            errorCollector.addError(new Throwable(getMismatchMessage(GitRepositoryTest.REPOSITORY_USERNAME, remoteGitRef.getUsername(), "username")));
        }
    }
}

