/**
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.config.server.environment;


import JGitEnvironmentRepository.JGitFactory;
import SearchPathLocator.Locations;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.DeleteBranchCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NotMergedException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefDatabase;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.CredentialItem;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.ReceiveCommand;
import org.eclipse.jgit.transport.TrackingRefUpdate;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.server.support.AwsCodeCommitCredentialProvider;
import org.springframework.cloud.config.server.support.GitSkipSslValidationCredentialsProvider;
import org.springframework.cloud.config.server.support.PassphraseCredentialsProvider;
import org.springframework.cloud.config.server.test.ConfigServerTestUtils;
import org.springframework.core.env.StandardEnvironment;

import static org.eclipse.jgit.transport.ReceiveCommand.Type.DELETE;


/**
 *
 *
 * @author Dave Syer
 * @author Gareth Clay
 */
public class JGitEnvironmentRepositoryTests {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    RefDatabase database = Mockito.mock(RefDatabase.class);

    private StandardEnvironment environment = new StandardEnvironment();

    private JGitEnvironmentRepository repository;

    private File basedir = new File("target/config");

    @Test
    public void vanilla() {
        this.repository.findOne("bar", "staging", "master");
        Environment environment = this.repository.findOne("bar", "staging", "master");
        assertThat(environment.getPropertySources().size()).isEqualTo(2);
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo(((this.repository.getUri()) + "/bar.properties"));
        assertVersion(environment);
    }

    @Test
    public void nested() throws IOException {
        String uri = ConfigServerTestUtils.prepareLocalRepo("another-config-repo");
        this.repository.setUri(uri);
        this.repository.setSearchPaths(new String[]{ "sub" });
        this.repository.findOne("bar", "staging", "master");
        Environment environment = this.repository.findOne("bar", "staging", "master");
        assertThat(environment.getPropertySources().size()).isEqualTo(2);
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo(((this.repository.getUri()) + "/sub/application.yml"));
        assertVersion(environment);
    }

    @Test
    public void placeholderInSearchPath() throws IOException {
        String uri = ConfigServerTestUtils.prepareLocalRepo("another-config-repo");
        this.repository.setUri(uri);
        this.repository.setSearchPaths(new String[]{ "{application}" });
        this.repository.findOne("sub", "staging", "master");
        Environment environment = this.repository.findOne("sub", "staging", "master");
        assertThat(environment.getPropertySources().size()).isEqualTo(1);
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo(((this.repository.getUri()) + "/sub/application.yml"));
        assertVersion(environment);
    }

    @Test
    public void nestedPattern() throws IOException {
        String uri = ConfigServerTestUtils.prepareLocalRepo("another-config-repo");
        this.repository.setUri(uri);
        this.repository.setSearchPaths(new String[]{ "sub*" });
        this.repository.findOne("bar", "staging", "master");
        Environment environment = this.repository.findOne("bar", "staging", "master");
        assertThat(environment.getPropertySources().size()).isEqualTo(2);
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo(((this.repository.getUri()) + "/sub/application.yml"));
        assertVersion(environment);
    }

    @Test
    public void branch() {
        this.repository.setBasedir(this.basedir);
        Environment environment = this.repository.findOne("bar", "staging", "raw");
        assertThat(environment.getPropertySources().size()).isEqualTo(2);
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo(((this.repository.getUri()) + "/bar.properties"));
        assertVersion(environment);
    }

    @Test
    public void tag() {
        this.repository.setBasedir(this.basedir);
        Environment environment = this.repository.findOne("bar", "staging", "foo");
        assertThat(environment.getPropertySources().size()).isEqualTo(2);
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo(((this.repository.getUri()) + "/bar.properties"));
        assertVersion(environment);
    }

    @Test
    public void basedir() {
        this.repository.setBasedir(this.basedir);
        this.repository.findOne("bar", "staging", "master");
        Environment environment = this.repository.findOne("bar", "staging", "master");
        assertThat(environment.getPropertySources().size()).isEqualTo(2);
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo(((this.repository.getUri()) + "/bar.properties"));
        assertVersion(environment);
    }

    @Test
    public void basedirExists() throws Exception {
        assertThat(this.basedir.mkdirs()).isTrue();
        assertThat(new File(this.basedir, ".nothing").createNewFile()).isTrue();
        this.repository.setBasedir(this.basedir);
        this.repository.findOne("bar", "staging", "master");
        Environment environment = this.repository.findOne("bar", "staging", "master");
        assertThat(environment.getPropertySources().size()).isEqualTo(2);
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo(((this.repository.getUri()) + "/bar.properties"));
        assertVersion(environment);
    }

    @Test
    public void uriWithHostOnly() throws Exception {
        this.repository.setUri("git://localhost");
        assertThat(this.repository.getUri()).isEqualTo("git://localhost/");
    }

    @Test
    public void uriWithHostAndPath() throws Exception {
        this.repository.setUri("git://localhost/foo/");
        assertThat(this.repository.getUri()).isEqualTo("git://localhost/foo");
    }

    @Test
    public void afterPropertiesSet_CloneOnStartTrue_CloneAndFetchCalled() throws Exception {
        Git mockGit = Mockito.mock(Git.class);
        CloneCommand mockCloneCommand = Mockito.mock(CloneCommand.class);
        Mockito.when(mockCloneCommand.setURI(ArgumentMatchers.anyString())).thenReturn(mockCloneCommand);
        Mockito.when(mockCloneCommand.setDirectory(ArgumentMatchers.any(File.class))).thenReturn(mockCloneCommand);
        JGitEnvironmentRepository envRepository = new JGitEnvironmentRepository(this.environment, new JGitEnvironmentProperties());
        envRepository.setGitFactory(new JGitEnvironmentRepositoryTests.MockGitFactory(mockGit, mockCloneCommand));
        envRepository.setUri("http://somegitserver/somegitrepo");
        envRepository.setCloneOnStart(true);
        envRepository.afterPropertiesSet();
        Mockito.verify(mockCloneCommand, Mockito.times(1)).call();
    }

    @Test
    public void afterPropertiesSet_CloneOnStartFalse_CloneAndFetchNotCalled() throws Exception {
        Git mockGit = Mockito.mock(Git.class);
        CloneCommand mockCloneCommand = Mockito.mock(CloneCommand.class);
        Mockito.when(mockCloneCommand.setURI(ArgumentMatchers.anyString())).thenReturn(mockCloneCommand);
        Mockito.when(mockCloneCommand.setDirectory(ArgumentMatchers.any(File.class))).thenReturn(mockCloneCommand);
        JGitEnvironmentRepository envRepository = new JGitEnvironmentRepository(this.environment, new JGitEnvironmentProperties());
        envRepository.setGitFactory(new JGitEnvironmentRepositoryTests.MockGitFactory(mockGit, mockCloneCommand));
        envRepository.setUri("http://somegitserver/somegitrepo");
        envRepository.afterPropertiesSet();
        Mockito.verify(mockCloneCommand, Mockito.times(0)).call();
        Mockito.verify(mockGit, Mockito.times(0)).fetch();
    }

    @Test
    public void afterPropertiesSet_CloneOnStartTrueWithFileURL_CloneAndFetchNotCalled() throws Exception {
        Git mockGit = Mockito.mock(Git.class);
        CloneCommand mockCloneCommand = Mockito.mock(CloneCommand.class);
        Mockito.when(mockCloneCommand.setURI(ArgumentMatchers.anyString())).thenReturn(mockCloneCommand);
        Mockito.when(mockCloneCommand.setDirectory(ArgumentMatchers.any(File.class))).thenReturn(mockCloneCommand);
        JGitEnvironmentRepository envRepository = new JGitEnvironmentRepository(this.environment, new JGitEnvironmentProperties());
        envRepository.setGitFactory(new JGitEnvironmentRepositoryTests.MockGitFactory(mockGit, mockCloneCommand));
        envRepository.setUri("file://somefilesystem/somegitrepo");
        envRepository.setCloneOnStart(true);
        envRepository.afterPropertiesSet();
        Mockito.verify(mockCloneCommand, Mockito.times(0)).call();
        Mockito.verify(mockGit, Mockito.times(0)).fetch();
    }

    @Test
    public void shouldPullForcepullNotClean() throws Exception {
        Git git = Mockito.mock(Git.class);
        StatusCommand statusCommand = Mockito.mock(StatusCommand.class);
        Status status = Mockito.mock(Status.class);
        Repository repository = Mockito.mock(Repository.class);
        StoredConfig storedConfig = Mockito.mock(StoredConfig.class);
        Mockito.when(git.status()).thenReturn(statusCommand);
        Mockito.when(git.getRepository()).thenReturn(repository);
        Mockito.when(repository.getConfig()).thenReturn(storedConfig);
        Mockito.when(storedConfig.getString("remote", "origin", "url")).thenReturn("http://example/git");
        Mockito.when(statusCommand.call()).thenReturn(status);
        Mockito.when(status.isClean()).thenReturn(false);
        JGitEnvironmentRepository repo = new JGitEnvironmentRepository(this.environment, new JGitEnvironmentProperties());
        repo.setForcePull(true);
        boolean shouldPull = repo.shouldPull(git);
        assertThat(shouldPull).as("shouldPull was false").isTrue();
    }

    @Test
    public void shouldPullNotClean() throws Exception {
        Git git = Mockito.mock(Git.class);
        StatusCommand statusCommand = Mockito.mock(StatusCommand.class);
        Status status = Mockito.mock(Status.class);
        Repository repository = Mockito.mock(Repository.class);
        StoredConfig storedConfig = Mockito.mock(StoredConfig.class);
        Mockito.when(git.status()).thenReturn(statusCommand);
        Mockito.when(git.getRepository()).thenReturn(repository);
        Mockito.when(repository.getConfig()).thenReturn(storedConfig);
        Mockito.when(storedConfig.getString("remote", "origin", "url")).thenReturn("http://example/git");
        Mockito.when(statusCommand.call()).thenReturn(status);
        Mockito.when(status.isClean()).thenReturn(false);
        JGitEnvironmentRepository repo = new JGitEnvironmentRepository(this.environment, new JGitEnvironmentProperties());
        boolean shouldPull = repo.shouldPull(git);
        assertThat(shouldPull).as("shouldPull was true").isFalse();
    }

    @Test
    public void shouldPullClean() throws Exception {
        Git git = Mockito.mock(Git.class);
        StatusCommand statusCommand = Mockito.mock(StatusCommand.class);
        Status status = Mockito.mock(Status.class);
        Repository repository = Mockito.mock(Repository.class);
        StoredConfig storedConfig = Mockito.mock(StoredConfig.class);
        Mockito.when(git.status()).thenReturn(statusCommand);
        Mockito.when(git.getRepository()).thenReturn(repository);
        Mockito.when(repository.getConfig()).thenReturn(storedConfig);
        Mockito.when(storedConfig.getString("remote", "origin", "url")).thenReturn("http://example/git");
        Mockito.when(statusCommand.call()).thenReturn(status);
        Mockito.when(status.isClean()).thenReturn(true);
        JGitEnvironmentRepository repo = new JGitEnvironmentRepository(this.environment, new JGitEnvironmentProperties());
        boolean shouldPull = repo.shouldPull(git);
        assertThat(shouldPull).as("shouldPull was false").isTrue();
    }

    @Test
    public void shouldNotRefresh() throws Exception {
        Git git = Mockito.mock(Git.class);
        StatusCommand statusCommand = Mockito.mock(StatusCommand.class);
        Status status = Mockito.mock(Status.class);
        Repository repository = Mockito.mock(Repository.class);
        StoredConfig storedConfig = Mockito.mock(StoredConfig.class);
        Mockito.when(git.status()).thenReturn(statusCommand);
        Mockito.when(git.getRepository()).thenReturn(repository);
        Mockito.when(repository.getConfig()).thenReturn(storedConfig);
        Mockito.when(storedConfig.getString("remote", "origin", "url")).thenReturn("http://example/git");
        Mockito.when(statusCommand.call()).thenReturn(status);
        Mockito.when(status.isClean()).thenReturn(true);
        JGitEnvironmentProperties properties = new JGitEnvironmentProperties();
        properties.setRefreshRate(2);
        JGitEnvironmentRepository repo = new JGitEnvironmentRepository(this.environment, properties);
        repo.setLastRefresh(((System.currentTimeMillis()) - 5000));
        boolean shouldPull = repo.shouldPull(git);
        assertThat(shouldPull).as("shouldPull was false").isTrue();
        repo.setRefreshRate(30);
        shouldPull = repo.shouldPull(git);
        assertThat(shouldPull).as("shouldPull was true").isFalse();
    }

    @Test
    public void shouldUpdateLastRefresh() throws Exception {
        Git git = Mockito.mock(Git.class);
        StatusCommand statusCommand = Mockito.mock(StatusCommand.class);
        Status status = Mockito.mock(Status.class);
        Repository repository = Mockito.mock(Repository.class);
        StoredConfig storedConfig = Mockito.mock(StoredConfig.class);
        FetchCommand fetchCommand = Mockito.mock(FetchCommand.class);
        FetchResult fetchResult = Mockito.mock(FetchResult.class);
        Mockito.when(git.status()).thenReturn(statusCommand);
        Mockito.when(git.getRepository()).thenReturn(repository);
        Mockito.when(fetchCommand.call()).thenReturn(fetchResult);
        Mockito.when(git.fetch()).thenReturn(fetchCommand);
        Mockito.when(repository.getConfig()).thenReturn(storedConfig);
        Mockito.when(storedConfig.getString("remote", "origin", "url")).thenReturn("http://example/git");
        Mockito.when(statusCommand.call()).thenReturn(status);
        Mockito.when(status.isClean()).thenReturn(true);
        JGitEnvironmentProperties properties = new JGitEnvironmentProperties();
        properties.setRefreshRate(1000);
        JGitEnvironmentRepository repo = new JGitEnvironmentRepository(this.environment, properties);
        repo.setLastRefresh(0);
        repo.fetch(git, "master");
        long timeDiff = (System.currentTimeMillis()) - (repo.getLastRefresh());
        assertThat((timeDiff < 1000L)).as((("time difference (" + timeDiff) + ") was longer than 1 second")).isTrue();
    }

    @Test
    public void testFetchException() throws Exception {
        Git git = Mockito.mock(Git.class);
        CloneCommand cloneCommand = Mockito.mock(CloneCommand.class);
        JGitEnvironmentRepositoryTests.MockGitFactory factory = new JGitEnvironmentRepositoryTests.MockGitFactory(git, cloneCommand);
        this.repository.setGitFactory(factory);
        this.repository.setDeleteUntrackedBranches(true);
        // refresh()->shouldPull
        StatusCommand statusCommand = Mockito.mock(StatusCommand.class);
        Status status = Mockito.mock(Status.class);
        Mockito.when(git.status()).thenReturn(statusCommand);
        Repository repository = stubbedRepo();
        Mockito.when(git.getRepository()).thenReturn(repository);
        StoredConfig storedConfig = Mockito.mock(StoredConfig.class);
        Mockito.when(repository.getConfig()).thenReturn(storedConfig);
        Mockito.when(storedConfig.getString("remote", "origin", "url")).thenReturn("http://example/git");
        Mockito.when(statusCommand.call()).thenReturn(status);
        Mockito.when(status.isClean()).thenReturn(true);
        // refresh()->fetch
        FetchCommand fetchCommand = Mockito.mock(FetchCommand.class);
        Mockito.when(git.fetch()).thenReturn(fetchCommand);
        Mockito.when(fetchCommand.setRemote(ArgumentMatchers.anyString())).thenReturn(fetchCommand);
        Mockito.when(fetchCommand.call()).thenThrow(new InvalidRemoteException("invalid mock remote"));// here

        // is
        // our
        // exception
        // we
        // are
        // testing
        // refresh()->checkout
        CheckoutCommand checkoutCommand = Mockito.mock(CheckoutCommand.class);
        // refresh()->checkout->containsBranch
        ListBranchCommand listBranchCommand = Mockito.mock(ListBranchCommand.class);
        Mockito.when(git.checkout()).thenReturn(checkoutCommand);
        Mockito.when(git.branchList()).thenReturn(listBranchCommand);
        List<Ref> refs = new ArrayList<>();
        Ref ref = Mockito.mock(Ref.class);
        refs.add(ref);
        Mockito.when(ref.getName()).thenReturn("/master");
        Mockito.when(listBranchCommand.call()).thenReturn(refs);
        // refresh()->merge
        MergeCommand mergeCommand = Mockito.mock(MergeCommand.class);
        Mockito.when(git.merge()).thenReturn(mergeCommand);
        Mockito.when(mergeCommand.call()).thenThrow(new NotMergedException());// here

        // is
        // our
        // exception
        // we
        // are
        // testing
        // refresh()->return
        // git.getRepository().findRef("HEAD").getObjectId().getName();
        Ref headRef = Mockito.mock(Ref.class);
        Mockito.when(this.database.getRef(ArgumentMatchers.anyString())).thenReturn(headRef);
        ObjectId newObjectId = ObjectId.fromRaw(new int[]{ 1, 2, 3, 4, 5 });
        Mockito.when(headRef.getObjectId()).thenReturn(newObjectId);
        SearchPathLocator.Locations locations = this.repository.getLocations("bar", "staging", null);
        assertThat(newObjectId.getName()).isEqualTo(locations.getVersion());
        Mockito.verify(git, Mockito.times(0)).branchDelete();
    }

    @Test
    public void testMergeException() throws Exception {
        Git git = Mockito.mock(Git.class);
        CloneCommand cloneCommand = Mockito.mock(CloneCommand.class);
        JGitEnvironmentRepositoryTests.MockGitFactory factory = new JGitEnvironmentRepositoryTests.MockGitFactory(git, cloneCommand);
        this.repository.setGitFactory(factory);
        // refresh()->shouldPull
        StatusCommand statusCommand = Mockito.mock(StatusCommand.class);
        Status status = Mockito.mock(Status.class);
        Mockito.when(git.status()).thenReturn(statusCommand);
        Repository repository = stubbedRepo();
        Mockito.when(git.getRepository()).thenReturn(repository);
        StoredConfig storedConfig = Mockito.mock(StoredConfig.class);
        Mockito.when(repository.getConfig()).thenReturn(storedConfig);
        Mockito.when(storedConfig.getString("remote", "origin", "url")).thenReturn("http://example/git");
        Mockito.when(statusCommand.call()).thenReturn(status);
        Mockito.when(status.isClean()).thenReturn(true);
        // refresh()->fetch
        FetchCommand fetchCommand = Mockito.mock(FetchCommand.class);
        FetchResult fetchResult = Mockito.mock(FetchResult.class);
        Mockito.when(git.fetch()).thenReturn(fetchCommand);
        Mockito.when(fetchCommand.setRemote(ArgumentMatchers.anyString())).thenReturn(fetchCommand);
        Mockito.when(fetchCommand.call()).thenReturn(fetchResult);
        Mockito.when(fetchResult.getTrackingRefUpdates()).thenReturn(Collections.<TrackingRefUpdate>emptyList());
        // refresh()->checkout
        CheckoutCommand checkoutCommand = Mockito.mock(CheckoutCommand.class);
        // refresh()->checkout->containsBranch
        ListBranchCommand listBranchCommand = Mockito.mock(ListBranchCommand.class);
        Mockito.when(git.checkout()).thenReturn(checkoutCommand);
        Mockito.when(git.branchList()).thenReturn(listBranchCommand);
        List<Ref> refs = new ArrayList<>();
        Ref ref = Mockito.mock(Ref.class);
        refs.add(ref);
        Mockito.when(ref.getName()).thenReturn("/master");
        Mockito.when(listBranchCommand.call()).thenReturn(refs);
        // refresh()->merge
        MergeCommand mergeCommand = Mockito.mock(MergeCommand.class);
        Mockito.when(git.merge()).thenReturn(mergeCommand);
        Mockito.when(mergeCommand.call()).thenThrow(new NotMergedException());// here is our

        // exception we
        // are testing
        // refresh()->return git.getRepository().findRef("HEAD").getObjectId().getName();
        Ref headRef = Mockito.mock(Ref.class);
        Mockito.when(this.database.getRef(ArgumentMatchers.anyString())).thenReturn(headRef);
        ObjectId newObjectId = ObjectId.fromRaw(new int[]{ 1, 2, 3, 4, 5 });
        Mockito.when(headRef.getObjectId()).thenReturn(newObjectId);
        SearchPathLocator.Locations locations = this.repository.getLocations("bar", "staging", "master");
        assertThat(newObjectId.getName()).isEqualTo(locations.getVersion());
        Mockito.verify(git, Mockito.times(0)).branchDelete();
    }

    @Test
    public void testRefreshWithoutFetch() throws Exception {
        Git git = Mockito.mock(Git.class);
        CloneCommand cloneCommand = Mockito.mock(CloneCommand.class);
        Mockito.when(cloneCommand.setURI(ArgumentMatchers.anyString())).thenReturn(cloneCommand);
        Mockito.when(cloneCommand.setDirectory(ArgumentMatchers.any(File.class))).thenReturn(cloneCommand);
        Mockito.when(cloneCommand.call()).thenReturn(git);
        JGitEnvironmentRepositoryTests.MockGitFactory factory = new JGitEnvironmentRepositoryTests.MockGitFactory(git, cloneCommand);
        StatusCommand statusCommand = Mockito.mock(StatusCommand.class);
        CheckoutCommand checkoutCommand = Mockito.mock(CheckoutCommand.class);
        Status status = Mockito.mock(Status.class);
        Repository repository = Mockito.mock(Repository.class, Mockito.RETURNS_DEEP_STUBS);
        StoredConfig storedConfig = Mockito.mock(StoredConfig.class);
        Ref ref = Mockito.mock(Ref.class);
        ListBranchCommand listBranchCommand = Mockito.mock(ListBranchCommand.class);
        FetchCommand fetchCommand = Mockito.mock(FetchCommand.class);
        FetchResult fetchResult = Mockito.mock(FetchResult.class);
        Ref branch1Ref = Mockito.mock(Ref.class);
        Mockito.when(git.branchList()).thenReturn(listBranchCommand);
        Mockito.when(git.status()).thenReturn(statusCommand);
        Mockito.when(git.getRepository()).thenReturn(repository);
        Mockito.when(git.checkout()).thenReturn(checkoutCommand);
        Mockito.when(git.fetch()).thenReturn(fetchCommand);
        Mockito.when(git.merge()).thenReturn(Mockito.mock(MergeCommand.class, Mockito.RETURNS_DEEP_STUBS));
        Mockito.when(repository.getConfig()).thenReturn(storedConfig);
        Mockito.when(storedConfig.getString("remote", "origin", "url")).thenReturn("http://example/git");
        Mockito.when(statusCommand.call()).thenReturn(status);
        Mockito.when(checkoutCommand.call()).thenReturn(ref);
        Mockito.when(listBranchCommand.call()).thenReturn(Arrays.asList(branch1Ref));
        Mockito.when(fetchCommand.call()).thenReturn(fetchResult);
        Mockito.when(branch1Ref.getName()).thenReturn("origin/master");
        Mockito.when(status.isClean()).thenReturn(true);
        JGitEnvironmentRepository repo = new JGitEnvironmentRepository(this.environment, new JGitEnvironmentProperties());
        repo.setGitFactory(factory);
        repo.setUri("http://somegitserver/somegitrepo");
        repo.setBasedir(this.basedir);
        // Set the refresh rate to 2 seconds and last update before 100ms. There should be
        // no remote repo fetch.
        repo.setLastRefresh(((System.currentTimeMillis()) - 100));
        repo.setRefreshRate(2);
        repo.refresh("master");
        // Verify no fetch but merge only.
        Mockito.verify(git, Mockito.times(0)).fetch();
        Mockito.verify(git).merge();
    }

    @Test
    public void testResetHardException() throws Exception {
        Git git = Mockito.mock(Git.class);
        CloneCommand cloneCommand = Mockito.mock(CloneCommand.class);
        JGitEnvironmentRepositoryTests.MockGitFactory factory = new JGitEnvironmentRepositoryTests.MockGitFactory(git, cloneCommand);
        this.repository.setGitFactory(factory);
        // refresh()->shouldPull
        StatusCommand statusCommand = Mockito.mock(StatusCommand.class);
        Status status = Mockito.mock(Status.class);
        Mockito.when(git.status()).thenReturn(statusCommand);
        Repository repository = stubbedRepo();
        Mockito.when(git.getRepository()).thenReturn(repository);
        StoredConfig storedConfig = Mockito.mock(StoredConfig.class);
        Mockito.when(repository.getConfig()).thenReturn(storedConfig);
        Mockito.when(storedConfig.getString("remote", "origin", "url")).thenReturn("http://example/git");
        Mockito.when(statusCommand.call()).thenReturn(status);
        Mockito.when(status.isClean()).thenReturn(true).thenReturn(false);
        // refresh()->fetch
        FetchCommand fetchCommand = Mockito.mock(FetchCommand.class);
        FetchResult fetchResult = Mockito.mock(FetchResult.class);
        Mockito.when(git.fetch()).thenReturn(fetchCommand);
        Mockito.when(fetchCommand.setRemote(ArgumentMatchers.anyString())).thenReturn(fetchCommand);
        Mockito.when(fetchCommand.call()).thenReturn(fetchResult);
        Mockito.when(fetchResult.getTrackingRefUpdates()).thenReturn(Collections.<TrackingRefUpdate>emptyList());
        // refresh()->checkout
        CheckoutCommand checkoutCommand = Mockito.mock(CheckoutCommand.class);
        // refresh()->checkout->containsBranch
        ListBranchCommand listBranchCommand = Mockito.mock(ListBranchCommand.class);
        Mockito.when(git.checkout()).thenReturn(checkoutCommand);
        Mockito.when(git.branchList()).thenReturn(listBranchCommand);
        List<Ref> refs = new ArrayList<>();
        Ref ref = Mockito.mock(Ref.class);
        refs.add(ref);
        Mockito.when(ref.getName()).thenReturn("/master");
        Mockito.when(listBranchCommand.call()).thenReturn(refs);
        // refresh()->merge
        MergeCommand mergeCommand = Mockito.mock(MergeCommand.class);
        Mockito.when(git.merge()).thenReturn(mergeCommand);
        Mockito.when(mergeCommand.call()).thenThrow(new NotMergedException());// here

        // is
        // our
        // exception
        // we
        // are
        // testing
        // refresh()->hardReset
        ResetCommand resetCommand = Mockito.mock(ResetCommand.class);
        Mockito.when(git.reset()).thenReturn(resetCommand);
        Mockito.when(resetCommand.call()).thenReturn(ref);
        // refresh()->return
        // git.getRepository().findRef("HEAD").getObjectId().getName();
        Ref headRef = Mockito.mock(Ref.class);
        Mockito.when(this.database.getRef(ArgumentMatchers.anyString())).thenReturn(headRef);
        ObjectId newObjectId = ObjectId.fromRaw(new int[]{ 1, 2, 3, 4, 5 });
        Mockito.when(headRef.getObjectId()).thenReturn(newObjectId);
        SearchPathLocator.Locations locations = this.repository.getLocations("bar", "staging", "master");
        assertThat(newObjectId.getName()).isEqualTo(locations.getVersion());
        Mockito.verify(git, Mockito.times(0)).branchDelete();
    }

    @Test
    public void shouldDeleteBaseDirWhenCloneFails() throws Exception {
        Git mockGit = Mockito.mock(Git.class);
        CloneCommand mockCloneCommand = Mockito.mock(CloneCommand.class);
        Mockito.when(mockCloneCommand.setURI(ArgumentMatchers.anyString())).thenReturn(mockCloneCommand);
        Mockito.when(mockCloneCommand.setDirectory(ArgumentMatchers.any(File.class))).thenReturn(mockCloneCommand);
        Mockito.when(mockCloneCommand.call()).thenThrow(new TransportException("failed to clone"));
        JGitEnvironmentRepository envRepository = new JGitEnvironmentRepository(this.environment, new JGitEnvironmentProperties());
        envRepository.setGitFactory(new JGitEnvironmentRepositoryTests.MockGitFactory(mockGit, mockCloneCommand));
        envRepository.setUri("http://somegitserver/somegitrepo");
        envRepository.setBasedir(this.basedir);
        try {
            envRepository.findOne("bar", "staging", "master");
        } catch (Exception ex) {
            // expected - ignore
        }
        assertThat(((this.basedir.listFiles().length) > 0)).as("baseDir should be deleted when clone fails").isFalse();
    }

    @Test
    public void usernamePasswordShouldSetCredentials() throws Exception {
        Git mockGit = Mockito.mock(Git.class);
        JGitEnvironmentRepositoryTests.MockCloneCommand mockCloneCommand = new JGitEnvironmentRepositoryTests.MockCloneCommand(mockGit);
        JGitEnvironmentRepository envRepository = new JGitEnvironmentRepository(this.environment, new JGitEnvironmentProperties());
        envRepository.setGitFactory(new JGitEnvironmentRepositoryTests.MockGitFactory(mockGit, mockCloneCommand));
        envRepository.setUri("git+ssh://git@somegitserver/somegitrepo");
        envRepository.setBasedir(new File("./mybasedir"));
        final String username = "someuser";
        final String password = "mypassword";
        envRepository.setUsername(username);
        envRepository.setPassword(password);
        envRepository.setCloneOnStart(true);
        envRepository.afterPropertiesSet();
        TestCase.assertTrue(((mockCloneCommand.getCredentialsProvider()) instanceof UsernamePasswordCredentialsProvider));
        CredentialsProvider provider = mockCloneCommand.getCredentialsProvider();
        CredentialItem.Username usernameCredential = new CredentialItem.Username();
        CredentialItem.Password passwordCredential = new CredentialItem.Password();
        assertThat(provider.supports(usernameCredential)).isTrue();
        assertThat(provider.supports(passwordCredential)).isTrue();
        provider.get(new URIish(), usernameCredential);
        assertThat(username).isEqualTo(usernameCredential.getValue());
        provider.get(new URIish(), passwordCredential);
        assertThat(password).isEqualTo(String.valueOf(passwordCredential.getValue()));
    }

    @Test
    public void passphraseShouldSetCredentials() throws Exception {
        final String passphrase = "mypassphrase";
        Git mockGit = Mockito.mock(Git.class);
        JGitEnvironmentRepositoryTests.MockCloneCommand mockCloneCommand = new JGitEnvironmentRepositoryTests.MockCloneCommand(mockGit);
        JGitEnvironmentRepository envRepository = new JGitEnvironmentRepository(this.environment, new JGitEnvironmentProperties());
        envRepository.setGitFactory(new JGitEnvironmentRepositoryTests.MockGitFactory(mockGit, mockCloneCommand));
        envRepository.setUri("git+ssh://git@somegitserver/somegitrepo");
        envRepository.setBasedir(new File("./mybasedir"));
        envRepository.setPassphrase(passphrase);
        envRepository.setCloneOnStart(true);
        envRepository.afterPropertiesSet();
        assertThat(mockCloneCommand.hasPassphraseCredentialsProvider()).isTrue();
        CredentialsProvider provider = mockCloneCommand.getCredentialsProvider();
        assertThat(provider.isInteractive()).isFalse();
        CredentialItem.StringType stringCredential = new CredentialItem.StringType(PassphraseCredentialsProvider.PROMPT, true);
        assertThat(provider.supports(stringCredential)).isTrue();
        provider.get(new URIish(), stringCredential);
        assertThat(passphrase).isEqualTo(stringCredential.getValue());
    }

    @Test
    public void gitCredentialsProviderFactoryCreatesPassphraseProvider() throws Exception {
        final String passphrase = "mypassphrase";
        final String gitUri = "git+ssh://git@somegitserver/somegitrepo";
        Git mockGit = Mockito.mock(Git.class);
        JGitEnvironmentRepositoryTests.MockCloneCommand mockCloneCommand = new JGitEnvironmentRepositoryTests.MockCloneCommand(mockGit);
        JGitEnvironmentRepository envRepository = new JGitEnvironmentRepository(this.environment, new JGitEnvironmentProperties());
        envRepository.setGitFactory(new JGitEnvironmentRepositoryTests.MockGitFactory(mockGit, mockCloneCommand));
        envRepository.setUri(gitUri);
        envRepository.setBasedir(new File("./mybasedir"));
        envRepository.setPassphrase(passphrase);
        envRepository.setCloneOnStart(true);
        envRepository.afterPropertiesSet();
        assertThat(mockCloneCommand.hasPassphraseCredentialsProvider()).isTrue();
        CredentialsProvider provider = mockCloneCommand.getCredentialsProvider();
        assertThat(provider.isInteractive()).isFalse();
        CredentialItem.StringType stringCredential = new CredentialItem.StringType(PassphraseCredentialsProvider.PROMPT, true);
        assertThat(provider.supports(stringCredential)).isTrue();
        provider.get(new URIish(), stringCredential);
        assertThat(passphrase).isEqualTo(stringCredential.getValue());
    }

    @Test
    public void gitCredentialsProviderFactoryCreatesUsernamePasswordProvider() throws Exception {
        Git mockGit = Mockito.mock(Git.class);
        JGitEnvironmentRepositoryTests.MockCloneCommand mockCloneCommand = new JGitEnvironmentRepositoryTests.MockCloneCommand(mockGit);
        final String username = "someuser";
        final String password = "mypassword";
        JGitEnvironmentRepository envRepository = new JGitEnvironmentRepository(this.environment, new JGitEnvironmentProperties());
        envRepository.setGitFactory(new JGitEnvironmentRepositoryTests.MockGitFactory(mockGit, mockCloneCommand));
        envRepository.setUri("git+ssh://git@somegitserver/somegitrepo");
        envRepository.setBasedir(new File("./mybasedir"));
        envRepository.setUsername(username);
        envRepository.setPassword(password);
        envRepository.setCloneOnStart(true);
        envRepository.afterPropertiesSet();
        TestCase.assertTrue(((mockCloneCommand.getCredentialsProvider()) instanceof UsernamePasswordCredentialsProvider));
        CredentialsProvider provider = mockCloneCommand.getCredentialsProvider();
        CredentialItem.Username usernameCredential = new CredentialItem.Username();
        CredentialItem.Password passwordCredential = new CredentialItem.Password();
        assertThat(provider.supports(usernameCredential)).isTrue();
        assertThat(provider.supports(passwordCredential)).isTrue();
        provider.get(new URIish(), usernameCredential);
        assertThat(username).isEqualTo(usernameCredential.getValue());
        provider.get(new URIish(), passwordCredential);
        assertThat(password).isEqualTo(String.valueOf(passwordCredential.getValue()));
    }

    @Test
    public void gitCredentialsProviderFactoryCreatesAwsCodeCommitProvider() throws Exception {
        Git mockGit = Mockito.mock(Git.class);
        JGitEnvironmentRepositoryTests.MockCloneCommand mockCloneCommand = new JGitEnvironmentRepositoryTests.MockCloneCommand(mockGit);
        final String awsUri = "https://git-codecommit.us-east-1.amazonaws.com/v1/repos/test";
        JGitEnvironmentRepository envRepository = new JGitEnvironmentRepository(this.environment, new JGitEnvironmentProperties());
        envRepository.setGitFactory(new JGitEnvironmentRepositoryTests.MockGitFactory(mockGit, mockCloneCommand));
        envRepository.setUri(awsUri);
        envRepository.setCloneOnStart(true);
        envRepository.afterPropertiesSet();
        TestCase.assertTrue(((mockCloneCommand.getCredentialsProvider()) instanceof AwsCodeCommitCredentialProvider));
    }

    @Test
    public void gitCredentialsProviderFactoryCreatesSkipSslValidationProvider() throws Exception {
        Git mockGit = Mockito.mock(Git.class);
        JGitEnvironmentRepositoryTests.MockCloneCommand mockCloneCommand = new JGitEnvironmentRepositoryTests.MockCloneCommand(mockGit);
        final String username = "someuser";
        final String password = "mypassword";
        JGitEnvironmentRepository envRepository = new JGitEnvironmentRepository(this.environment, new JGitEnvironmentProperties());
        envRepository.setGitFactory(new JGitEnvironmentRepositoryTests.MockGitFactory(mockGit, mockCloneCommand));
        envRepository.setUri("https://somegitserver/somegitrepo");
        envRepository.setBasedir(new File("./mybasedir"));
        envRepository.setUsername(username);
        envRepository.setPassword(password);
        envRepository.setSkipSslValidation(true);
        envRepository.setCloneOnStart(true);
        envRepository.afterPropertiesSet();
        CredentialsProvider provider = mockCloneCommand.getCredentialsProvider();
        assertThat((provider instanceof GitSkipSslValidationCredentialsProvider)).isTrue();
        CredentialItem.Username usernameCredential = new CredentialItem.Username();
        CredentialItem.Password passwordCredential = new CredentialItem.Password();
        assertThat(provider.supports(usernameCredential)).isTrue();
        assertThat(provider.supports(passwordCredential)).isTrue();
        provider.get(new URIish(), usernameCredential);
        assertThat(username).isEqualTo(usernameCredential.getValue());
        provider.get(new URIish(), passwordCredential);
        assertThat(password).isEqualTo(String.valueOf(passwordCredential.getValue()));
    }

    @Test
    public void shouldPrintStacktraceIfDebugEnabled() throws Exception {
        final Log mockLogger = Mockito.mock(Log.class);
        JGitEnvironmentRepository envRepository = new JGitEnvironmentRepository(this.environment, new JGitEnvironmentProperties()) {
            @Override
            public void afterPropertiesSet() throws Exception {
                this.logger = mockLogger;
            }
        };
        envRepository.afterPropertiesSet();
        Mockito.when(mockLogger.isDebugEnabled()).thenReturn(true);
        envRepository.warn("", new RuntimeException());
        Mockito.verify(mockLogger).warn(ArgumentMatchers.eq(""));
        Mockito.verify(mockLogger).debug(ArgumentMatchers.eq("Stacktrace for: "), ArgumentMatchers.any(RuntimeException.class));
        int numberOfInvocations = Mockito.mockingDetails(mockLogger).getInvocations().size();
        assertThat(numberOfInvocations).as("should call isDebugEnabled warn and debug").isEqualTo(3);
    }

    @Test
    public void shouldSetTransportConfigCallbackOnCloneAndFetch() throws Exception {
        Git mockGit = Mockito.mock(Git.class);
        FetchCommand fetchCommand = Mockito.mock(FetchCommand.class);
        Mockito.when(mockGit.fetch()).thenReturn(fetchCommand);
        Mockito.when(fetchCommand.call()).thenReturn(Mockito.mock(FetchResult.class));
        CloneCommand mockCloneCommand = Mockito.mock(CloneCommand.class);
        Mockito.when(mockCloneCommand.setURI(ArgumentMatchers.anyString())).thenReturn(mockCloneCommand);
        Mockito.when(mockCloneCommand.setDirectory(ArgumentMatchers.any(File.class))).thenReturn(mockCloneCommand);
        TransportConfigCallback configCallback = Mockito.mock(TransportConfigCallback.class);
        JGitEnvironmentRepository envRepository = new JGitEnvironmentRepository(this.environment, new JGitEnvironmentProperties());
        envRepository.setGitFactory(new JGitEnvironmentRepositoryTests.MockGitFactory(mockGit, mockCloneCommand));
        envRepository.setUri("http://somegitserver/somegitrepo");
        envRepository.setTransportConfigCallback(configCallback);
        envRepository.setCloneOnStart(true);
        envRepository.afterPropertiesSet();
        Mockito.verify(mockCloneCommand, Mockito.times(1)).setTransportConfigCallback(configCallback);
        envRepository.fetch(mockGit, "master");
        Mockito.verify(fetchCommand, Mockito.times(1)).setTransportConfigCallback(configCallback);
    }

    @Test
    public void shouldSetRemoveBranchesFlagToFetchCommand() throws Exception {
        Git mockGit = Mockito.mock(Git.class);
        FetchCommand fetchCommand = Mockito.mock(FetchCommand.class);
        Mockito.when(mockGit.fetch()).thenReturn(fetchCommand);
        Mockito.when(fetchCommand.call()).thenReturn(Mockito.mock(FetchResult.class));
        JGitEnvironmentRepository envRepository = new JGitEnvironmentRepository(this.environment, new JGitEnvironmentProperties());
        envRepository.setGitFactory(new JGitEnvironmentRepositoryTests.MockGitFactory(mockGit, Mockito.mock(CloneCommand.class)));
        envRepository.setUri("http://somegitserver/somegitrepo");
        envRepository.setDeleteUntrackedBranches(true);
        envRepository.fetch(mockGit, "master");
        Mockito.verify(fetchCommand, Mockito.times(1)).setRemoveDeletedRefs(true);
        Mockito.verify(fetchCommand, Mockito.times(1)).call();
    }

    @Test
    public void shouldHandleExceptionWhileRemovingBranches() throws Exception {
        Git git = Mockito.mock(Git.class);
        CloneCommand cloneCommand = Mockito.mock(CloneCommand.class);
        JGitEnvironmentRepositoryTests.MockGitFactory factory = new JGitEnvironmentRepositoryTests.MockGitFactory(git, cloneCommand);
        this.repository.setGitFactory(factory);
        this.repository.setDeleteUntrackedBranches(true);
        // refresh()->shouldPull
        StatusCommand statusCommand = Mockito.mock(StatusCommand.class);
        Status status = Mockito.mock(Status.class);
        Mockito.when(git.status()).thenReturn(statusCommand);
        Repository repository = stubbedRepo();
        Mockito.when(git.getRepository()).thenReturn(repository);
        StoredConfig storedConfig = Mockito.mock(StoredConfig.class);
        Mockito.when(repository.getConfig()).thenReturn(storedConfig);
        Mockito.when(storedConfig.getString("remote", "origin", "url")).thenReturn("http://example/git");
        Mockito.when(statusCommand.call()).thenReturn(status);
        Mockito.when(status.isClean()).thenReturn(true);
        // refresh()->fetch
        FetchCommand fetchCommand = Mockito.mock(FetchCommand.class);
        FetchResult fetchResult = Mockito.mock(FetchResult.class);
        TrackingRefUpdate trackingRefUpdate = Mockito.mock(TrackingRefUpdate.class);
        Collection<TrackingRefUpdate> trackingRefUpdates = Collections.singletonList(trackingRefUpdate);
        Mockito.when(git.fetch()).thenReturn(fetchCommand);
        Mockito.when(fetchCommand.setRemote(ArgumentMatchers.anyString())).thenReturn(fetchCommand);
        Mockito.when(fetchCommand.call()).thenReturn(fetchResult);
        Mockito.when(fetchResult.getTrackingRefUpdates()).thenReturn(trackingRefUpdates);
        // refresh()->deleteBranch
        ReceiveCommand receiveCommand = Mockito.mock(ReceiveCommand.class);
        Mockito.when(trackingRefUpdate.asReceiveCommand()).thenReturn(receiveCommand);
        Mockito.when(receiveCommand.getType()).thenReturn(DELETE);
        Mockito.when(trackingRefUpdate.getLocalName()).thenReturn("refs/remotes/origin/feature/deletedBranchFromOrigin");
        DeleteBranchCommand deleteBranchCommand = Mockito.mock(DeleteBranchCommand.class);
        Mockito.when(git.branchDelete()).thenReturn(deleteBranchCommand);
        Mockito.when(deleteBranchCommand.setBranchNames(ArgumentMatchers.eq("feature/deletedBranchFromOrigin"))).thenReturn(deleteBranchCommand);
        Mockito.when(deleteBranchCommand.setForce(true)).thenReturn(deleteBranchCommand);
        Mockito.when(deleteBranchCommand.call()).thenThrow(new NotMergedException());// here

        // is
        // our
        // exception
        // we
        // are
        // testing
        // refresh()->checkout
        CheckoutCommand checkoutCommand = Mockito.mock(CheckoutCommand.class);
        // refresh()->checkout->containsBranch
        ListBranchCommand listBranchCommand = Mockito.mock(ListBranchCommand.class);
        Mockito.when(git.checkout()).thenReturn(checkoutCommand);
        Mockito.when(git.branchList()).thenReturn(listBranchCommand);
        List<Ref> refs = new ArrayList<>();
        Ref ref = Mockito.mock(Ref.class);
        refs.add(ref);
        Mockito.when(ref.getName()).thenReturn("/master");
        Mockito.when(listBranchCommand.call()).thenReturn(refs);
        // refresh()->merge
        MergeResult mergeResult = Mockito.mock(MergeResult.class);
        MergeResult.MergeStatus mergeStatus = Mockito.mock(MergeResult.MergeStatus.class);
        MergeCommand mergeCommand = Mockito.mock(MergeCommand.class);
        Mockito.when(git.merge()).thenReturn(mergeCommand);
        Mockito.when(mergeCommand.call()).thenReturn(mergeResult);
        Mockito.when(mergeResult.getMergeStatus()).thenReturn(mergeStatus);
        Mockito.when(mergeStatus.isSuccessful()).thenReturn(true);
        // refresh()->return
        // git.getRepository().findRef("HEAD").getObjectId().getName();
        Ref headRef = Mockito.mock(Ref.class);
        Mockito.when(this.database.getRef(ArgumentMatchers.anyString())).thenReturn(headRef);
        ObjectId newObjectId = ObjectId.fromRaw(new int[]{ 1, 2, 3, 4, 5 });
        Mockito.when(headRef.getObjectId()).thenReturn(newObjectId);
        SearchPathLocator.Locations locations = this.repository.getLocations("bar", "staging", "master");
        assertThat(newObjectId.getName()).isEqualTo(locations.getVersion());
        Mockito.verify(deleteBranchCommand).setBranchNames(ArgumentMatchers.eq("feature/deletedBranchFromOrigin"));
        Mockito.verify(deleteBranchCommand).setForce(true);
        Mockito.verify(deleteBranchCommand).call();
    }

    class MockCloneCommand extends CloneCommand {
        private Git mockGit;

        MockCloneCommand(Git mockGit) {
            this.mockGit = mockGit;
        }

        @Override
        public Git call() throws GitAPIException, InvalidRemoteException {
            return this.mockGit;
        }

        public boolean hasPassphraseCredentialsProvider() {
            return (this.credentialsProvider) instanceof PassphraseCredentialsProvider;
        }

        public CredentialsProvider getCredentialsProvider() {
            return this.credentialsProvider;
        }
    }

    class MockGitFactory extends JGitEnvironmentRepository.JGitFactory {
        private Git mockGit;

        private CloneCommand mockCloneCommand;

        MockGitFactory(Git mockGit, CloneCommand mockCloneCommand) {
            this.mockGit = mockGit;
            this.mockCloneCommand = mockCloneCommand;
        }

        @Override
        public Git getGitByOpen(File file) throws IOException {
            return this.mockGit;
        }

        @Override
        public CloneCommand getCloneCommandByCloneRepository() {
            return this.mockCloneCommand;
        }
    }
}

