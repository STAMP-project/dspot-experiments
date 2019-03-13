/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 */
package org.opengrok.web.api.v1.controller;


import RepositoryInstalled.GitInstalled;
import RepositoryInstalled.MercurialInstalled;
import RepositoryInstalled.SubversionInstalled;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.core.GenericType;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.opengrok.indexer.condition.ConditionalRun;
import org.opengrok.indexer.condition.ConditionalRunRule;
import org.opengrok.indexer.condition.CtagsInstalled;
import org.opengrok.indexer.configuration.Group;
import org.opengrok.indexer.configuration.Project;
import org.opengrok.indexer.configuration.RuntimeEnvironment;
import org.opengrok.indexer.history.HistoryGuru;
import org.opengrok.indexer.history.MercurialRepositoryTest;
import org.opengrok.indexer.history.Repository;
import org.opengrok.indexer.history.RepositoryInfo;
import org.opengrok.indexer.index.IndexDatabase;
import org.opengrok.indexer.index.Indexer;
import org.opengrok.indexer.index.IndexerException;
import org.opengrok.indexer.util.TestRepository;
import org.opengrok.web.api.v1.suggester.provider.service.SuggesterService;


@ConditionalRun(MercurialInstalled.class)
@ConditionalRun(GitInstalled.class)
@ConditionalRun(SubversionInstalled.class)
public class ProjectsControllerTest extends JerseyTest {
    private RuntimeEnvironment env = RuntimeEnvironment.getInstance();

    private TestRepository repository;

    @Rule
    public ConditionalRunRule rule = new ConditionalRunRule();

    @Mock
    private SuggesterService suggesterService;

    @Test
    public void testAddInherit() {
        Assert.assertTrue(env.getRepositories().isEmpty());
        Assert.assertTrue(env.getProjects().isEmpty());
        Assert.assertTrue(env.isHandleHistoryOfRenamedFiles());
        addProject("git");
        Assert.assertTrue(env.getProjects().containsKey("git"));
        Assert.assertEquals(1, env.getProjects().size());
        Project proj = env.getProjects().get("git");
        Assert.assertNotNull(proj);
        Assert.assertTrue(proj.isHandleRenamedFiles());
    }

    /**
     * Verify that added project correctly inherits a property
     * from configuration. Ideally, this should test all properties of Project.
     */
    @Test
    public void testAdd() throws Exception {
        Assert.assertTrue(env.getRepositories().isEmpty());
        Assert.assertTrue(env.getProjects().isEmpty());
        // Add a group matching the project to be added.
        String groupName = "mercurialgroup";
        Group group = new Group(groupName, "mercurial.*");
        env.getGroups().add(group);
        Assert.assertTrue(env.hasGroups());
        Assert.assertEquals(1, env.getGroups().stream().filter(( g) -> g.getName().equals(groupName)).collect(Collectors.toSet()).size());
        Assert.assertEquals(0, group.getRepositories().size());
        Assert.assertEquals(0, group.getProjects().size());
        // Add a sub-repository.
        String repoPath = ((repository.getSourceRoot()) + (File.separator)) + "mercurial";
        File mercurialRoot = new File(repoPath);
        File subDir = new File(mercurialRoot, "usr");
        Assert.assertTrue(subDir.mkdir());
        String subRepoPath = (((repoPath + (File.separator)) + "usr") + (File.separator)) + "closed";
        File mercurialSubRoot = new File(subRepoPath);
        MercurialRepositoryTest.runHgCommand(mercurialRoot, "clone", mercurialRoot.getAbsolutePath(), subRepoPath);
        // Add the project.
        env.setScanningDepth(3);
        addProject("mercurial");
        // Check that the project was added properly.
        Assert.assertTrue(env.getProjects().containsKey("mercurial"));
        Assert.assertEquals(1, env.getProjects().size());
        Assert.assertEquals(2, env.getRepositories().size());
        Assert.assertEquals(1, group.getRepositories().size());
        Assert.assertEquals(0, group.getProjects().size());
        Assert.assertEquals(1, group.getRepositories().stream().filter(( p) -> p.getName().equals("mercurial")).collect(Collectors.toSet()).size());
        // Check that HistoryGuru now includes the project in its list.
        Set<String> directoryNames = HistoryGuru.getInstance().getRepositories().stream().map(( ri) -> ri.getDirectoryName()).collect(Collectors.toSet());
        Assert.assertTrue("though it should contain the top root,", ((directoryNames.contains(repoPath)) || (directoryNames.contains(mercurialRoot.getCanonicalPath()))));
        Assert.assertTrue("though it should contain the sub-root,", ((directoryNames.contains(subRepoPath)) || (directoryNames.contains(mercurialSubRoot.getCanonicalPath()))));
        // Add more projects and check that they have been added incrementally.
        // At the same time, it checks that multiple projects can be added
        // with single message.
        addProject("git");
        addProject("svn");
        Assert.assertEquals(3, env.getProjects().size());
        Assert.assertEquals(4, env.getRepositories().size());
        Assert.assertTrue(env.getProjects().containsKey("git"));
        Assert.assertTrue(env.getProjects().containsKey("svn"));
        Assert.assertFalse(HistoryGuru.getInstance().getRepositories().stream().map(( ri) -> ri.getDirectoryName()).collect(Collectors.toSet()).contains("git"));
        Assert.assertFalse(HistoryGuru.getInstance().getRepositories().stream().map(( ri) -> ri.getDirectoryName()).collect(Collectors.toSet()).contains("svn"));
    }

    /**
     * Test that if the add is applied on already existing project,
     * the repository list is refreshed.
     */
    @Test
    public void testRepositoryRefresh() throws Exception {
        addProject("mercurial");
        File mercurialRoot = new File((((repository.getSourceRoot()) + (File.separator)) + "mercurial"));
        MercurialRepositoryTest.runHgCommand(mercurialRoot, "clone", mercurialRoot.getAbsolutePath(), (((mercurialRoot.getAbsolutePath()) + (File.separator)) + "closed"));
        addProject("mercurial");
        Assert.assertEquals(2, env.getRepositories().size());
        Assert.assertEquals(2, env.getProjectRepositoriesMap().get(Project.getProject(mercurialRoot)).size());
        // Delete the newly added repository to verify it will be removed from
        // configuration after the message is reapplied. This is necessary anyway
        // for proper per-test cleanup.
        removeRecursive(new File((((mercurialRoot.getAbsolutePath()) + (File.separator)) + "closed")).toPath());
        addProject("mercurial");
        Assert.assertEquals(1, env.getRepositories().size());
        Assert.assertEquals(1, env.getProjectRepositoriesMap().get(Project.getProject(mercurialRoot)).size());
    }

    /**
     * This test needs to perform indexing so that it can be verified that
     * the delete handling performs removal of the index data.
     */
    @Test
    @ConditionalRun(CtagsInstalled.class)
    public void testDelete() throws Exception {
        String[] projectsToDelete = new String[]{ "git", "svn" };
        // Add a group matching the project to be added.
        String groupName = "gitgroup";
        Group group = new Group(groupName, "git.*");
        env.getGroups().add(group);
        Assert.assertTrue(env.hasGroups());
        Assert.assertEquals(1, env.getGroups().stream().filter(( g) -> g.getName().equals(groupName)).collect(Collectors.toSet()).size());
        Assert.assertEquals(0, group.getRepositories().size());
        Assert.assertEquals(0, group.getProjects().size());
        Assert.assertEquals(0, env.getProjects().size());
        Assert.assertEquals(0, env.getRepositories().size());
        Assert.assertEquals(0, env.getProjectRepositoriesMap().size());
        addProject("mercurial");
        addProject("git");
        addProject("svn");
        Assert.assertEquals(3, env.getProjects().size());
        Assert.assertEquals(3, env.getRepositories().size());
        Assert.assertEquals(3, env.getProjectRepositoriesMap().size());
        // Check the group was populated properly.
        Assert.assertEquals(1, group.getRepositories().size());
        Assert.assertEquals(0, group.getProjects().size());
        Assert.assertEquals(1, group.getRepositories().stream().filter(( p) -> p.getName().equals("git")).collect(Collectors.toSet()).size());
        // Run the indexer (ala 'indexpart') so that data directory is populated.
        ArrayList<String> subFiles = new ArrayList<>();
        subFiles.add("/git");
        subFiles.add("/mercurial");
        subFiles.add("/svn");
        ArrayList<String> repos = new ArrayList<>();
        repos.add("/git");
        repos.add("/mercurial");
        repos.add("/svn");
        // This is necessary so that repositories in HistoryGuru get populated.
        // When 'indexpart' is run, this is called from setConfiguration() because
        // of the -R option is present.
        HistoryGuru.getInstance().invalidateRepositories(env.getRepositories(), null, false);
        env.setHistoryEnabled(true);
        // don't search for repositories
        // don't scan and add projects
        // don't create dictionary
        // subFiles - needed when refreshing history partially
        Indexer.getInstance().prepareIndexer(env, false, false, false, subFiles, repos);// repositories - needed when refreshing history partially

        Indexer.getInstance().doIndexerExecution(true, null, null);
        for (String proj : projectsToDelete) {
            delete(proj);
        }
        Assert.assertEquals(1, env.getProjects().size());
        Assert.assertEquals(1, env.getRepositories().size());
        Assert.assertEquals(1, env.getProjectRepositoriesMap().size());
        // Test data removal.
        for (String projectName : projectsToDelete) {
            for (String dirName : new String[]{ "historycache", IndexDatabase.XREF_DIR, IndexDatabase.INDEX_DIR }) {
                File dir = new File(env.getDataRootFile(), ((dirName + (File.separator)) + projectName));
                Assert.assertFalse(dir.exists());
            }
        }
        // Check that HistoryGuru no longer maintains the removed projects.
        for (String p : projectsToDelete) {
            Assert.assertFalse(HistoryGuru.getInstance().getRepositories().stream().map(( ri) -> ri.getDirectoryName()).collect(Collectors.toSet()).contains((((repository.getSourceRoot()) + (File.separator)) + p)));
        }
        // Check the group no longer contains the removed project.
        Assert.assertEquals(0, group.getRepositories().size());
        Assert.assertEquals(0, group.getProjects().size());
    }

    @Test
    public void testIndexed() throws IOException {
        String projectName = "mercurial";
        // When a project is added, it should be marked as not indexed.
        addProject(projectName);
        Assert.assertFalse(env.getProjects().get(projectName).isIndexed());
        // Get repository info for the project.
        Project project = env.getProjects().get(projectName);
        Assert.assertNotNull(project);
        List<RepositoryInfo> riList = env.getProjectRepositoriesMap().get(project);
        Assert.assertNotNull(riList);
        Assert.assertEquals("there should be just 1 repository", 1, riList.size());
        RepositoryInfo ri = riList.get(0);
        Assert.assertNotNull(ri);
        Assert.assertTrue(ri.getCurrentVersion().contains("8b340409b3a8"));
        // Add some changes to the repository.
        File mercurialRoot = new File((((repository.getSourceRoot()) + (File.separator)) + "mercurial"));
        // copy file from jar to a temp file
        Path temp = Files.createTempFile("opengrok", "temp");
        Files.copy(HistoryGuru.getInstance().getClass().getResourceAsStream("/history/hg-export-subdir.txt"), temp, StandardCopyOption.REPLACE_EXISTING);
        MercurialRepositoryTest.runHgCommand(mercurialRoot, "import", temp.toString());
        temp.toFile().delete();
        // Test that the project's indexed flag becomes true only after
        // the message is applied.
        markIndexed(projectName);
        Assert.assertTrue("indexed flag should be set to true", env.getProjects().get(projectName).isIndexed());
        // Test that the "indexed" message triggers refresh of current version
        // info in related repositories.
        riList = env.getProjectRepositoriesMap().get(project);
        Assert.assertNotNull(riList);
        ri = riList.get(0);
        Assert.assertNotNull(ri);
        Assert.assertTrue("current version should be refreshed", ri.getCurrentVersion().contains("c78fa757c524"));
    }

    @Test
    public void testList() {
        addProject("mercurial");
        markIndexed("mercurial");
        // Add another project.
        addProject("git");
        GenericType<List<String>> type = new GenericType<List<String>>() {};
        List<String> projects = target("projects").request().get(type);
        Assert.assertTrue(projects.contains("mercurial"));
        Assert.assertTrue(projects.contains("git"));
        List<String> indexed = target("projects").path("indexed").request().get(type);
        Assert.assertTrue(indexed.contains("mercurial"));
        Assert.assertFalse(indexed.contains("git"));
    }

    @Test
    public void testGetReposForNonExistentProject() throws Exception {
        GenericType<List<String>> type = new GenericType<List<String>>() {};
        // Try to get repos for non-existent project first.
        List<String> repos = target("projects").path("totally-nonexistent-project").path("repositories").request().get(type);
        Assert.assertTrue(repos.isEmpty());
    }

    @Test
    public void testGetRepos() throws Exception {
        GenericType<List<String>> type = new GenericType<List<String>>() {};
        // Create subrepository.
        File mercurialRoot = new File((((repository.getSourceRoot()) + (File.separator)) + "mercurial"));
        MercurialRepositoryTest.runHgCommand(mercurialRoot, "clone", mercurialRoot.getAbsolutePath(), (((mercurialRoot.getAbsolutePath()) + (File.separator)) + "closed"));
        addProject("mercurial");
        // Get repositories of the project.
        List<String> repos = target("projects").path("mercurial").path("repositories").request().get(type);
        // Perform cleanup of the subrepository in order not to interfere
        // with other tests.
        removeRecursive(new File((((mercurialRoot.getAbsolutePath()) + (File.separator)) + "closed")).toPath());
        // test
        Assert.assertEquals(new ArrayList<>(Arrays.asList(Paths.get("/mercurial").toString(), Paths.get("/mercurial/closed").toString())), repos);
        // Test the types. There should be only one type for project with
        // multiple nested Mercurial repositories.
        List<String> types = target("projects").path("mercurial").path("repositories/type").request().get(type);
        Assert.assertEquals(Collections.singletonList("Mercurial"), types);
    }

    @Test
    public void testSetGet() throws Exception {
        Assert.assertTrue(env.isHandleHistoryOfRenamedFiles());
        String[] projects = new String[]{ "mercurial", "git" };
        for (String proj : projects) {
            addProject(proj);
        }
        Assert.assertEquals(2, env.getProjectList().size());
        // Change their property.
        for (String proj : projects) {
            setHandleRenamedFilesToFalse(proj);
        }
        // Verify the property was set on each project and its repositories.
        for (String proj : projects) {
            Project project = env.getProjects().get(proj);
            Assert.assertNotNull(project);
            Assert.assertFalse(project.isHandleRenamedFiles());
            List<RepositoryInfo> riList = env.getProjectRepositoriesMap().get(project);
            Assert.assertNotNull(riList);
            for (RepositoryInfo ri : riList) {
                Repository repo = getRepository(ri, false);
                Assert.assertFalse(repo.isHandleRenamedFiles());
            }
        }
        // Verify the property can be retrieved via message.
        for (String proj : projects) {
            boolean value = target("projects").path(proj).path("property/handleRenamedFiles").request().get(boolean.class);
            Assert.assertFalse(value);
        }
    }

    @Test
    public void testListFiles() throws IOException, IndexerException {
        final String projectName = "mercurial";
        GenericType<List<String>> type = new GenericType<List<String>>() {};
        // don't search for repositories
        // add projects
        // don't create dictionary
        // subFiles - needed when refreshing history partially
        Indexer.getInstance().prepareIndexer(env, false, true, false, new ArrayList(), new ArrayList());// repositories - needed when refreshing history partially

        Indexer.getInstance().doIndexerExecution(true, null, null);
        List<String> filesFromRequest = target("projects").path(projectName).path("files").request().get(type);
        filesFromRequest.sort(String::compareTo);
        String[] files = new String[]{ "Makefile", "bar.txt", "header.h", "main.c", "novel.txt" };
        for (int i = 0; i < (files.length); i++) {
            files[i] = (("/" + projectName) + "/") + (files[i]);
        }
        List<String> expectedFiles = Arrays.asList(files);
        expectedFiles.sort(String::compareTo);
        Assert.assertEquals(expectedFiles, filesFromRequest);
    }
}

