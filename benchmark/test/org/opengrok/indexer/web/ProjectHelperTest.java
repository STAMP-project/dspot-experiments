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
 * Copyright (c) 2016, 2018, Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright (c) 2017, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.indexer.web;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.opengrok.indexer.configuration.Group;
import org.opengrok.indexer.configuration.Project;
import org.opengrok.indexer.configuration.RuntimeEnvironment;
import org.opengrok.indexer.history.RepoRepository;
import org.opengrok.indexer.history.RepositoryInfo;


public class ProjectHelperTest extends ProjectHelperTestBase {
    /**
     * Test of getInstance method, of class ProjectHelper.
     */
    @Test
    public void testGetInstance() {
        ProjectHelper result = ProjectHelper.getInstance(cfg);
        Assert.assertNotNull("Project helper should not be null", result);
        Assert.assertSame(result.getClass(), ProjectHelper.class);
    }

    /**
     * Test if projects and groups are always reloaded fully from the env.
     *
     * This ensures that when the RuntimeEnvironment changes that it also
     * updates the projects in the UI.
     */
    @Test
    public void testSynchronization() {
        HashMap<String, Project> oldProjects = new HashMap(ProjectHelperTestBase.env.getProjects());
        List<RepositoryInfo> oldRepositories = new ArrayList(ProjectHelperTestBase.env.getRepositories());
        Set<Group> oldGroups = new java.util.TreeSet(ProjectHelperTestBase.env.getGroups());
        Map<Project, List<RepositoryInfo>> oldMap = new java.util.TreeMap(ProjectHelperTestBase.getRepositoriesMap());
        ProjectHelperTestBase.env.getAuthorizationFramework().removeAll();
        ProjectHelperTestBase.env.setSourceRoot("/src");// needed for setDirectoryName() below

        cfg = PageConfig.get(getRequest());
        helper = cfg.getProjectHelper();
        // basic setup
        Assert.assertEquals("There should be 40 env projects", 40, ProjectHelperTestBase.env.getProjects().size());
        Assert.assertEquals("There should be 20 env repositories", 20, ProjectHelperTestBase.env.getRepositories().size());
        Assert.assertEquals("There should be 4 env groups", 4, ProjectHelperTestBase.env.getGroups().size());
        Assert.assertEquals("There are 8 ungrouped projects", 8, helper.getAllUngrouped().size());
        Assert.assertEquals("There are 40 projects", 40, helper.getAllProjects().size());
        Assert.assertEquals("There are 4 projects", 4, helper.getRepositories().size());
        Assert.assertEquals("There are 4 groups", 4, helper.getGroups().size());
        // project
        Project p = new Project("some random name not in any group");
        p.setIndexed(true);
        // group
        Group g = new Group("some random name of a group");
        // repository
        Project repo = new Project("some random name not in any other group");
        repo.setIndexed(true);
        RepositoryInfo info = new RepoRepository();
        info.setParent(repo.getName());
        info.setDirectoryName(new File("/foo"));
        List<RepositoryInfo> infos = ProjectHelperTestBase.getRepositoriesMap().get(repo);
        if (infos == null) {
            infos = new ArrayList();
        }
        infos.add(info);
        ProjectHelperTestBase.getRepositoriesMap().put(repo, infos);
        ProjectHelperTestBase.env.getRepositories().add(info);
        ProjectHelperTestBase.env.getProjects().put("foo", p);
        ProjectHelperTestBase.env.getProjects().put("bar", repo);
        ProjectHelperTestBase.env.getGroups().add(g);
        Assert.assertEquals(42, ProjectHelperTestBase.env.getProjects().size());
        Assert.assertEquals(21, ProjectHelperTestBase.env.getRepositories().size());
        Assert.assertEquals(5, ProjectHelperTestBase.env.getGroups().size());
        // simulate another request
        cfg = PageConfig.get(getRequest());
        helper = cfg.getProjectHelper();
        // check for updates
        Assert.assertEquals("The helper state should refresh", 10, helper.getAllUngrouped().size());
        Assert.assertEquals("The helper state should refresh", 42, helper.getAllProjects().size());
        Assert.assertEquals("The helper state should refresh", 5, helper.getRepositories().size());
        Assert.assertEquals("The helper state should refresh", 5, helper.getGroups().size());
        ProjectHelperTestBase.setRepositoriesMap(oldMap);
        ProjectHelperTestBase.env.setProjects(oldProjects);
        ProjectHelperTestBase.env.setRepositories(oldRepositories);
        ProjectHelperTestBase.env.setGroups(oldGroups);
    }

    /**
     * Test of getRepositoryInfo method, of class ProjectHelper.
     */
    @Test
    public void testUnAllowedGetRepositoryInfo() {
        Project p = new Project("repository_2_1");
        p.setIndexed(true);
        List<RepositoryInfo> result = helper.getRepositoryInfo(p);
        Assert.assertEquals("this project is not allowed", 0, result.size());
    }

    /**
     * Test of getRepositoryInfo method, of class ProjectHelper.
     */
    @Test
    public void testAllowedGetRepositoryInfo() {
        Project p = new Project("allowed_grouped_repository_0_1");
        p.setIndexed(true);
        List<RepositoryInfo> result = helper.getRepositoryInfo(p);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(("allowed_grouped_repository_0_1_" + 0), result.get(0).getParent());
    }

    /**
     * Test of getGroups method, of class ProjectHelper.
     */
    @Test
    public void testGetAllowedGroups() {
        Set<Group> result = helper.getGroups();
        Assert.assertEquals(2, result.size());
        for (Group g : result) {
            Assert.assertTrue(g.getName().startsWith("allowed_"));
        }
    }

    /**
     * Test of getProjects method, of class ProjectHelper.
     */
    @Test
    public void testGetAllowedProjects() {
        Set<Project> result = helper.getProjects();
        Assert.assertEquals(2, result.size());
        for (Project p : result) {
            Assert.assertTrue(p.getName().startsWith("allowed_"));
        }
    }

    /**
     * Test of getRepositories method, of class ProjectHelper.
     */
    @Test
    public void testGetRepositories() {
        Set<Project> result = helper.getRepositories();
        Assert.assertEquals(2, result.size());
        for (Project p : result) {
            Assert.assertTrue(p.getName().startsWith("allowed_"));
        }
    }

    /**
     * Test of getProjects method, of class ProjectHelper.
     */
    @Test
    public void testGetProjectsAllowedGroup() {
        for (Group g : RuntimeEnvironment.getInstance().getGroups()) {
            if (g.getName().startsWith("allowed_group_0")) {
                Set<Project> result = helper.getProjects(g);
                Assert.assertEquals(2, result.size());
                for (Project p : result) {
                    Assert.assertTrue(p.getName().startsWith("allowed_"));
                }
            }
        }
    }

    /**
     * Test of getProjects method, of class ProjectHelper.
     */
    @Test
    public void testGetProjectsUnAllowedGroup() {
        for (Group g : RuntimeEnvironment.getInstance().getGroups()) {
            if (g.getName().startsWith("group_0")) {
                Assert.assertEquals(0, helper.getProjects(g).size());
                break;
            }
        }
    }

    /**
     * Test of getRepositories method, of class ProjectHelper.
     */
    @Test
    public void testGetRepositoriesAllowedGroup() {
        for (Group g : RuntimeEnvironment.getInstance().getGroups()) {
            if (g.getName().startsWith("allowed_group_0")) {
                Set<Project> result = helper.getRepositories(g);
                Assert.assertEquals(2, result.size());
                for (Project p : result) {
                    Assert.assertTrue(p.getName().startsWith("allowed_"));
                }
            }
        }
    }

    /**
     * Test of getRepositories method, of class ProjectHelper.
     */
    @Test
    public void testGetRepositoriesUnAllowedGroup() {
        for (Group g : RuntimeEnvironment.getInstance().getGroups()) {
            if (g.getName().startsWith("group_0")) {
                Assert.assertEquals(0, helper.getRepositories(g).size());
                break;
            }
        }
    }

    /**
     * Test of getGroupedProjects method, of class ProjectHelper.
     */
    @Test
    public void testGetGroupedProjects() {
        Set<Project> result = helper.getGroupedProjects();
        Assert.assertEquals(4, result.size());
        for (Project p : result) {
            Assert.assertTrue(p.getName().startsWith("allowed_"));
        }
    }

    /**
     * Test of getGroupedRepositories method, of class ProjectHelper.
     */
    @Test
    public void testGetGroupedRepositories() {
        Set<Project> result = helper.getGroupedRepositories();
        Assert.assertEquals(4, result.size());
        for (Project p : result) {
            Assert.assertTrue(p.getName().startsWith("allowed_"));
        }
    }

    /**
     * Test of getUngroupedProjects method, of class ProjectHelper.
     */
    @Test
    public void testGetUngroupedProjects() {
        Set<Project> result = helper.getUngroupedProjects();
        Assert.assertEquals(2, result.size());
        for (Project p : result) {
            Assert.assertTrue(p.getName().startsWith("allowed_"));
        }
    }

    /**
     * Test of getUngroupedRepositories method, of class ProjectHelper.
     */
    @Test
    public void testGetUngroupedRepositories() {
        Set<Project> result = helper.getUngroupedRepositories();
        Assert.assertEquals(2, result.size());
        for (Project p : result) {
            Assert.assertTrue(p.getName().startsWith("allowed_"));
        }
    }

    /**
     * Test of getAllGrouped method, of class ProjectHelper.
     */
    @Test
    public void testGetAllGrouped() {
        Set<Project> result = helper.getAllGrouped();
        Assert.assertEquals(8, result.size());
        for (Project p : result) {
            Assert.assertTrue(p.getName().startsWith("allowed_"));
        }
    }

    /**
     * Test of getAllGrouped method, of class ProjectHelper.
     */
    @Test
    public void testGetAllGroupedAllowedGroup() {
        for (Group g : RuntimeEnvironment.getInstance().getGroups()) {
            if (g.getName().startsWith("allowed_group_0")) {
                Set<Project> result = helper.getAllGrouped(g);
                Assert.assertEquals(4, result.size());
                for (Project p : result) {
                    Assert.assertTrue(p.getName().startsWith("allowed_"));
                }
            }
        }
    }

    @Test
    public void testGetAllGroupedUnAllowedGroup() {
        for (Group g : RuntimeEnvironment.getInstance().getGroups()) {
            if (g.getName().startsWith("group_0")) {
                Assert.assertEquals(0, helper.getAllGrouped(g).size());
                break;
            }
        }
    }

    /**
     * Test of getAllUngrouped method, of class ProjectHelper.
     */
    @Test
    public void testGetAllUngrouped() {
        Set<Project> result = helper.getAllUngrouped();
        Assert.assertEquals(4, result.size());
        for (Project p : result) {
            Assert.assertTrue(p.getName().startsWith("allowed_"));
        }
    }

    /**
     * Test of getAllProjects method, of class ProjectHelper.
     */
    @Test
    public void testGetAllProjects() {
        Set<Project> result = helper.getAllProjects();
        Assert.assertEquals(12, result.size());
        for (Project p : result) {
            Assert.assertTrue(p.getName().startsWith("allowed_"));
        }
    }
}

