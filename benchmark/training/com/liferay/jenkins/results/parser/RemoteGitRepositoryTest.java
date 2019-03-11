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


import org.json.JSONObject;
import org.junit.Test;


/**
 *
 *
 * @author Michael Hashimoto
 */
public class RemoteGitRepositoryTest extends GitRepositoryTest {
    @Test
    public void testGetHostname() {
        RemoteGitRepository remoteGitRepository = _getRemoteGitRepository();
        if (!(GitRepositoryTest.REPOSITORY_HOSTNAME.equals(remoteGitRepository.getHostname()))) {
            errorCollector.addError(new Throwable(("The hostname should be " + (GitRepositoryTest.REPOSITORY_HOSTNAME))));
        }
    }

    @Test
    public void testGetJSONObject() {
        RemoteGitRepository remoteGitRepository = _getRemoteGitRepository();
        JSONObject expectedJSONObject = new JSONObject();
        expectedJSONObject.put("hostname", GitRepositoryTest.REPOSITORY_HOSTNAME);
        expectedJSONObject.put("name", GitRepositoryTest.REPOSITORY_NAME);
        expectedJSONObject.put("username", GitRepositoryTest.REPOSITORY_USERNAME);
        JSONObject actualJSONObject = remoteGitRepository.getJSONObject();
        if (!(JenkinsResultsParserUtil.isJSONObjectEqual(expectedJSONObject, actualJSONObject))) {
            errorCollector.addError(new Throwable(JenkinsResultsParserUtil.combine("Expected does not match actual\nExpected: ", expectedJSONObject.toString(), "\nActual:   ", actualJSONObject.toString())));
        }
    }

    @Test
    public void testGetName() {
        RemoteGitRepository remoteGitRepository = _getRemoteGitRepository();
        if (!(GitRepositoryTest.REPOSITORY_NAME.equals(remoteGitRepository.getName()))) {
            errorCollector.addError(new Throwable(("The repository name should be " + (GitRepositoryTest.REPOSITORY_NAME))));
        }
    }

    @Test
    public void testGetRemoteURL() {
        RemoteGitRepository remoteGitRepository = _getRemoteGitRepository();
        String remoteURL = JenkinsResultsParserUtil.combine("git@", GitRepositoryTest.REPOSITORY_HOSTNAME, ":", GitRepositoryTest.REPOSITORY_USERNAME, "/", GitRepositoryTest.REPOSITORY_NAME);
        if (!(remoteURL.equals(remoteGitRepository.getRemoteURL()))) {
            errorCollector.addError(new Throwable(("The remote URL should be " + remoteURL)));
        }
    }

    @Test
    public void testGetUsername() {
        RemoteGitRepository remoteGitRepository = _getRemoteGitRepository();
        if (!(GitRepositoryTest.REPOSITORY_USERNAME.equals(remoteGitRepository.getUsername()))) {
            errorCollector.addError(new Throwable(("The username should be " + (GitRepositoryTest.REPOSITORY_USERNAME))));
        }
    }
}

