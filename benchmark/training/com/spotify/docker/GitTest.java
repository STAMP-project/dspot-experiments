/**
 * Copyright (c) 2014 Spotify AB.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.spotify.docker;


import com.spotify.docker.client.exceptions.DockerException;
import java.io.IOException;
import org.apache.maven.plugin.MojoExecutionException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.RefDatabase;
import org.eclipse.jgit.lib.Repository;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class GitTest {
    @Mock
    private Repository repo;

    @Mock
    private RefDatabase refDatabase;

    @Test(expected = MojoExecutionException.class)
    public void testGetCommitIdInNonGitDirThrows() throws DockerException, IOException, MojoExecutionException, GitAPIException {
        final Git git = new Git();
        git.setRepo(null);
        git.getCommitId();
    }

    @Test
    public void testGetCommitIdInGitDirWithNoCommitsReturnsNull() throws DockerException, IOException, MojoExecutionException, GitAPIException {
        Mockito.when(repo.resolve("HEAD")).thenReturn(null);
        final Git git = new Git();
        git.setRepo(repo);
        final String commitId = git.getCommitId();
        MatcherAssert.assertThat(commitId, CoreMatchers.is(IsEqual.equalTo(null)));
    }
}

