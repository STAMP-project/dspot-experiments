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
public class GitBranchFactoryTest extends GitRefTest {
    @Test
    public void testNewLocalGitBranch() {
        LocalGitRepository localGitRepository = _getLocalGitRepository();
        LocalGitBranch localGitBranch = GitBranchFactory.newLocalGitBranch(localGitRepository, GitRefTest.REF_NAME, GitRefTest.REF_SHA);
        if (localGitBranch == null) {
            errorCollector.addError(new Throwable("Local Git branch is null"));
        }
    }
}

