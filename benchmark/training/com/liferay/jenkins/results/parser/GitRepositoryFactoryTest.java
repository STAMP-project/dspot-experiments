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
public class GitRepositoryFactoryTest extends GitRepositoryTest {
    @Test
    public void testGetLocalGitRepository() {
        LocalGitRepository localGitRepository = GitRepositoryFactory.getLocalGitRepository(GitRepositoryTest.REPOSITORY_NAME, GitRepositoryTest.REPOSITORY_UPSTREAM_BRANCH_NAME);
        if (!(localGitRepository instanceof DefaultLocalGitRepository)) {
            errorCollector.addError(new Throwable("Invalid LocalGitRepository instance"));
        }
    }

    @Test
    public void testGetRemoteGitRepository() {
        RemoteGitRepository gitHubRemoteGitRepository = GitRepositoryFactory.getRemoteGitRepository("github.com", GitRepositoryTest.REPOSITORY_NAME, GitRepositoryTest.REPOSITORY_USERNAME);
        if (!(gitHubRemoteGitRepository instanceof GitHubRemoteGitRepository)) {
            errorCollector.addError(new Throwable("Invalid GitHubRemoteGitRepository instance"));
        }
        RemoteGitRepository remoteGitRepository = GitRepositoryFactory.getRemoteGitRepository("github-dev.liferay.com", GitRepositoryTest.REPOSITORY_NAME, GitRepositoryTest.REPOSITORY_USERNAME);
        if (!(remoteGitRepository instanceof DefaultRemoteGitRepository)) {
            errorCollector.addError(new Throwable("Invalid DefaultRemoteGitRepository instance"));
        }
    }
}

