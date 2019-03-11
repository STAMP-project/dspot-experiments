/**
 * Copyright 2016 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.tfssdk14;


import GetOptions.GET_ALL;
import GetOptions.NONE;
import com.microsoft.tfs.core.TFSTeamProjectCollection;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.Changeset;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.GetRequest;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.WorkingFolder;
import com.microsoft.tfs.core.clients.versioncontrol.specs.version.ChangesetVersionSpec;
import com.thoughtworks.go.domain.materials.Modification;
import com.thoughtworks.go.domain.materials.mercurial.StringRevision;
import com.thoughtworks.go.tfssdk14.wrapper.GoTfsVersionControlClient;
import com.thoughtworks.go.tfssdk14.wrapper.GoTfsWorkspace;
import com.thoughtworks.go.util.command.StringArgument;
import java.io.File;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TfsSDKCommandTest {
    private TfsSDKCommand tfsCommand;

    private final String DOMAIN = "domain";

    private final String USERNAME = "username";

    private final String PASSWORD = "password";

    private final String TFS_COLLECTION = "http://some.repo.local:8000/";

    private final String TFS_PROJECT = "$/project_path";

    private final String TFS_WORKSPACE = "workspace";

    private GoTfsVersionControlClient client;

    private TFSTeamProjectCollection collection;

    @Test
    public void shouldGetLatestModifications() throws Exception {
        Changeset[] changeSets = getChangeSets(42);
        Mockito.when(client.queryHistory(TFS_PROJECT, null, 1)).thenReturn(changeSets);
        TfsSDKCommand spy = Mockito.spy(tfsCommand);
        Mockito.doReturn(null).when(spy).getModifiedFiles(changeSets[0]);
        Assert.assertThat(spy.latestModification(null).isEmpty(), Matchers.is(false));
        Mockito.verify(client).queryHistory(TFS_PROJECT, null, 1);
        Mockito.verify(spy).getModifiedFiles(changeSets[0]);
    }

    @Test
    public void shouldCheckConnectionSuccessfullyIfAllCredentialsAreValid() throws Exception {
        Changeset[] changeSets = getChangeSets(42);
        Mockito.when(client.queryHistory(TFS_PROJECT, null, 1)).thenReturn(changeSets);
        TfsSDKCommand spy = Mockito.spy(tfsCommand);
        Mockito.doReturn(null).when(spy).getModifiedFiles(changeSets[0]);
        try {
            spy.checkConnection();
        } catch (Exception e) {
            Assert.fail("Should not have thrown exception");
        }
        Mockito.verify(client).queryHistory(TFS_PROJECT, null, 1);
        Mockito.verify(spy).getModifiedFiles(changeSets[0]);
    }

    @Test
    public void shouldThrowExceptionDuringCheckConnectionIfInvalid() throws Exception {
        Mockito.when(client.queryHistory(TFS_PROJECT, null, 1)).thenThrow(new RuntimeException("could not connect"));
        try {
            tfsCommand.checkConnection();
            Assert.fail("should have thrown an exception");
        } catch (RuntimeException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Failed while checking connection using Url: http://some.repo.local:8000/, Project Path: $/project_path, Username: username, Domain: domain, Root Cause: could not connect"));
        }
        Mockito.verify(client).queryHistory(TFS_PROJECT, null, 1);
    }

    @Test
    public void shouldReturnChangeSetsFromAPreviouslyKnownRevisionUptilTheLatest() throws Exception {
        Changeset[] changeSets = getChangeSets(42);
        Mockito.when(client.queryHistory(ArgumentMatchers.eq(TFS_PROJECT), AdditionalMatchers.or(ArgumentMatchers.isNull(), ArgumentMatchers.any(ChangesetVersionSpec.class)), ArgumentMatchers.anyInt())).thenReturn(changeSets);
        TfsSDKCommand spy = Mockito.spy(tfsCommand);
        Mockito.doReturn(null).when(spy).getModifiedFiles(changeSets[0]);
        List<Modification> modifications = spy.modificationsSince(null, new StringRevision("2"));
        Assert.assertThat(modifications.isEmpty(), Matchers.is(false));
        Mockito.verify(client, Mockito.times(2)).queryHistory(ArgumentMatchers.eq(TFS_PROJECT), AdditionalMatchers.or(ArgumentMatchers.isNull(), ArgumentMatchers.any(ChangesetVersionSpec.class)), ArgumentMatchers.anyInt());
    }

    @Test
    public void shouldCreateWorkspaceAndMapDirectory() throws Exception {
        File workingDirectory = Mockito.mock(File.class);
        Mockito.when(workingDirectory.exists()).thenReturn(false);
        Mockito.when(workingDirectory.getCanonicalPath()).thenReturn("/some-random-path/");
        GoTfsWorkspace[] workspaces = new GoTfsWorkspace[]{  };
        Mockito.when(client.queryWorkspaces(TFS_WORKSPACE, USERNAME)).thenReturn(workspaces);
        GoTfsWorkspace workspace = Mockito.mock(GoTfsWorkspace.class);
        Mockito.when(client.createWorkspace(TFS_WORKSPACE)).thenReturn(workspace);
        Mockito.when(workspace.isLocalPathMapped("/some-random-path/")).thenReturn(false);
        Mockito.doNothing().when(workspace).createWorkingFolder(ArgumentMatchers.any(WorkingFolder.class));
        TfsSDKCommand spy = Mockito.spy(tfsCommand);
        Mockito.doNothing().when(spy).retrieveFiles(workingDirectory, null);
        spy.checkout(workingDirectory, null);
        Mockito.verify(client, Mockito.times(1)).queryWorkspaces(TFS_WORKSPACE, USERNAME);
        Mockito.verify(client, Mockito.times(1)).createWorkspace(TFS_WORKSPACE);
        Mockito.verify(workspace, Mockito.times(1)).isLocalPathMapped(ArgumentMatchers.anyString());
        Mockito.verify(workspace, Mockito.times(1)).createWorkingFolder(ArgumentMatchers.any(WorkingFolder.class));
        Mockito.verify(spy).retrieveFiles(workingDirectory, null);
    }

    @Test
    public void shouldOnlyMapDirectoryAndNotCreateAWorkspaceIfWorkspaceIsAlreadyCreated() throws Exception {
        File workingDirectory = Mockito.mock(File.class);
        Mockito.when(workingDirectory.exists()).thenReturn(false);
        Mockito.when(workingDirectory.getCanonicalPath()).thenReturn("/some-random-path/");
        GoTfsWorkspace workspace = Mockito.mock(GoTfsWorkspace.class);
        GoTfsWorkspace[] workspaces = new GoTfsWorkspace[]{ workspace };
        Mockito.when(client.queryWorkspaces(TFS_WORKSPACE, USERNAME)).thenReturn(workspaces);
        Mockito.when(workspace.isLocalPathMapped("/some-random-path/")).thenReturn(false);
        Mockito.doNothing().when(workspace).createWorkingFolder(ArgumentMatchers.any(WorkingFolder.class));
        TfsSDKCommand spy = Mockito.spy(tfsCommand);
        Mockito.doNothing().when(spy).retrieveFiles(workingDirectory, null);
        spy.checkout(workingDirectory, null);
        Mockito.verify(client, Mockito.times(1)).queryWorkspaces(TFS_WORKSPACE, USERNAME);
        Mockito.verify(client, Mockito.never()).createWorkspace(TFS_WORKSPACE);
        Mockito.verify(workspace, Mockito.times(1)).isLocalPathMapped("/some-random-path/");
        Mockito.verify(workspace, Mockito.times(1)).createWorkingFolder(ArgumentMatchers.any(WorkingFolder.class));
        Mockito.verify(spy).retrieveFiles(workingDirectory, null);
    }

    @Test
    public void shouldThrowUpWhenUrlIsInvalid() throws Exception {
        TfsSDKCommand tfsCommandForInvalidCollection = new TfsSDKCommand(null, new StringArgument("invalid_url"), DOMAIN, USERNAME, PASSWORD, TFS_WORKSPACE, TFS_PROJECT);
        try {
            tfsCommandForInvalidCollection.init();
        } catch (RuntimeException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Unable to connect to TFS Collection invalid_url java.lang.RuntimeException: [TFS] Failed when converting the url string to a uri: invalid_url, Project Path: $/project_path, Username: username, Domain: domain"));
        }
    }

    @Test
    public void shouldCheckoutAllFilesWhenWorkingDirectoryIsDeleted() throws Exception {
        File workingDirectory = Mockito.mock(File.class);
        Mockito.when(workingDirectory.exists()).thenReturn(false);
        Mockito.when(workingDirectory.getCanonicalPath()).thenReturn("canonical_path");
        Mockito.when(workingDirectory.listFiles()).thenReturn(null);
        TfsSDKCommand spy = Mockito.spy(tfsCommand);
        Mockito.doNothing().when(spy).initializeWorkspace(workingDirectory);
        GoTfsWorkspace workspace = Mockito.mock(GoTfsWorkspace.class);
        Mockito.when(client.queryWorkspace(TFS_WORKSPACE, USERNAME)).thenReturn(workspace);
        Mockito.doNothing().when(workspace).get(ArgumentMatchers.any(GetRequest.class), ArgumentMatchers.eq(GET_ALL));
        spy.checkout(workingDirectory, null);
        Mockito.verify(workingDirectory).getCanonicalPath();
        Mockito.verify(workingDirectory).listFiles();
        Mockito.verify(workspace).get(ArgumentMatchers.any(GetRequest.class), ArgumentMatchers.eq(GET_ALL));
    }

    @Test
    public void should_GetLatestRevisions_WhenCheckingOutToLaterRevision() throws Exception {
        File workingDirectory = Mockito.mock(File.class);
        Mockito.when(workingDirectory.exists()).thenReturn(false);
        Mockito.when(workingDirectory.getCanonicalPath()).thenReturn("canonical_path");
        File[] checkedOutFiles = new File[]{ Mockito.mock(File.class) };
        Mockito.when(workingDirectory.listFiles()).thenReturn(checkedOutFiles);
        TfsSDKCommand spy = Mockito.spy(tfsCommand);
        Mockito.doNothing().when(spy).initializeWorkspace(workingDirectory);
        GoTfsWorkspace workspace = Mockito.mock(GoTfsWorkspace.class);
        Mockito.when(client.queryWorkspace(TFS_WORKSPACE, USERNAME)).thenReturn(workspace);
        Mockito.doNothing().when(workspace).get(ArgumentMatchers.any(GetRequest.class), ArgumentMatchers.eq(NONE));
        spy.checkout(workingDirectory, null);
        Mockito.verify(workingDirectory).getCanonicalPath();
        Mockito.verify(workingDirectory).listFiles();
        Mockito.verify(workspace).get(ArgumentMatchers.any(GetRequest.class), ArgumentMatchers.eq(NONE));
    }

    @Test
    public void shouldClearWorkingDirectoryBeforeCheckingOut() throws Exception {
        File workingDirectory = Mockito.mock(File.class);
        Mockito.when(workingDirectory.exists()).thenReturn(true);
        TfsSDKCommand spy = Mockito.spy(tfsCommand);
        Mockito.doNothing().when(spy).initializeWorkspace(workingDirectory);
        Mockito.doNothing().when(spy).retrieveFiles(workingDirectory, null);
        spy.checkout(workingDirectory, null);
        Mockito.verify(workingDirectory).exists();
    }

    @Test
    public void shouldDeleteWorkspace() throws Exception {
        GoTfsWorkspace workspace = Mockito.mock(GoTfsWorkspace.class);
        Mockito.when(client.queryWorkspace(TFS_WORKSPACE, USERNAME)).thenReturn(workspace);
        Mockito.doNothing().when(client).deleteWorkspace(workspace);
        tfsCommand.deleteWorkspace();
        Mockito.verify(client).queryWorkspace(TFS_WORKSPACE, USERNAME);
        Mockito.verify(client).deleteWorkspace(workspace);
    }

    @Test
    public void destroyShouldCloseClientAndCollection() throws Exception {
        Mockito.doNothing().when(client).close();
        Mockito.doNothing().when(collection).close();
        tfsCommand.destroy();
        Mockito.verify(client).close();
        Mockito.verify(collection).close();
    }
}

