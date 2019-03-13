/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.project;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;
import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Extension;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;
import org.codehaus.plexus.util.dag.CycleDetectedException;


/**
 * Test sorting projects by dependencies.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 */
public class ProjectSorterTest extends TestCase {
    public void testShouldNotFailWhenPluginDepReferencesCurrentProject() throws DuplicateProjectException, CycleDetectedException {
        MavenProject project = ProjectSorterTest.createProject("group", "artifact", "1.0");
        Build build = project.getModel().getBuild();
        Plugin plugin = createPlugin("other.group", "other-artifact", "1.0");
        Dependency dep = createDependency("group", "artifact", "1.0");
        plugin.addDependency(dep);
        build.addPlugin(plugin);
        new ProjectSorter(Collections.singletonList(project));
    }

    public void testShouldNotFailWhenManagedPluginDepReferencesCurrentProject() throws DuplicateProjectException, CycleDetectedException {
        MavenProject project = ProjectSorterTest.createProject("group", "artifact", "1.0");
        Build build = project.getModel().getBuild();
        PluginManagement pMgmt = new PluginManagement();
        Plugin plugin = createPlugin("other.group", "other-artifact", "1.0");
        Dependency dep = createDependency("group", "artifact", "1.0");
        plugin.addDependency(dep);
        pMgmt.addPlugin(plugin);
        build.setPluginManagement(pMgmt);
        new ProjectSorter(Collections.singletonList(project));
    }

    public void testShouldNotFailWhenProjectReferencesNonExistentProject() throws DuplicateProjectException, CycleDetectedException {
        MavenProject project = ProjectSorterTest.createProject("group", "artifact", "1.0");
        Build build = project.getModel().getBuild();
        Extension extension = createExtension("other.group", "other-artifact", "1.0");
        build.addExtension(extension);
        new ProjectSorter(Collections.singletonList(project));
    }

    public void testMatchingArtifactIdsDifferentGroupIds() throws DuplicateProjectException, CycleDetectedException {
        List<MavenProject> projects = new ArrayList<>();
        MavenProject project1 = ProjectSorterTest.createProject("groupId1", "artifactId", "1.0");
        projects.add(project1);
        MavenProject project2 = ProjectSorterTest.createProject("groupId2", "artifactId", "1.0");
        projects.add(project2);
        project1.getDependencies().add(createDependency(project2));
        projects = getSortedProjects();
        TestCase.assertEquals(project2, projects.get(0));
        TestCase.assertEquals(project1, projects.get(1));
    }

    public void testMatchingGroupIdsDifferentArtifactIds() throws DuplicateProjectException, CycleDetectedException {
        List<MavenProject> projects = new ArrayList<>();
        MavenProject project1 = ProjectSorterTest.createProject("groupId", "artifactId1", "1.0");
        projects.add(project1);
        MavenProject project2 = ProjectSorterTest.createProject("groupId", "artifactId2", "1.0");
        projects.add(project2);
        project1.getDependencies().add(createDependency(project2));
        projects = getSortedProjects();
        TestCase.assertEquals(project2, projects.get(0));
        TestCase.assertEquals(project1, projects.get(1));
    }

    public void testMatchingIdsAndVersions() throws CycleDetectedException {
        List<MavenProject> projects = new ArrayList<>();
        MavenProject project1 = ProjectSorterTest.createProject("groupId", "artifactId", "1.0");
        projects.add(project1);
        MavenProject project2 = ProjectSorterTest.createProject("groupId", "artifactId", "1.0");
        projects.add(project2);
        try {
            projects = getSortedProjects();
            TestCase.fail("Duplicate projects should fail");
        } catch (DuplicateProjectException e) {
            // expected
            TestCase.assertTrue(true);
        }
    }

    public void testMatchingIdsAndDifferentVersions() throws DuplicateProjectException, CycleDetectedException {
        List<MavenProject> projects = new ArrayList<>();
        MavenProject project1 = ProjectSorterTest.createProject("groupId", "artifactId", "1.0");
        projects.add(project1);
        MavenProject project2 = ProjectSorterTest.createProject("groupId", "artifactId", "2.0");
        projects.add(project2);
        projects = getSortedProjects();
        TestCase.assertEquals(project1, projects.get(0));
        TestCase.assertEquals(project2, projects.get(1));
    }

    public void testPluginDependenciesInfluenceSorting() throws Exception {
        List<MavenProject> projects = new ArrayList<>();
        MavenProject parentProject = ProjectSorterTest.createProject("groupId", "parent", "1.0");
        projects.add(parentProject);
        MavenProject declaringProject = ProjectSorterTest.createProject("groupId", "declarer", "1.0");
        declaringProject.setParent(parentProject);
        declaringProject.getModel().setParent(createParent(parentProject));
        projects.add(declaringProject);
        MavenProject pluginLevelDepProject = ProjectSorterTest.createProject("groupId", "plugin-level-dep", "1.0");
        pluginLevelDepProject.setParent(parentProject);
        pluginLevelDepProject.getModel().setParent(createParent(parentProject));
        projects.add(pluginLevelDepProject);
        MavenProject pluginProject = ProjectSorterTest.createProject("groupId", "plugin", "1.0");
        pluginProject.setParent(parentProject);
        pluginProject.getModel().setParent(createParent(parentProject));
        projects.add(pluginProject);
        Plugin plugin = createPlugin(pluginProject);
        plugin.addDependency(createDependency(pluginLevelDepProject));
        Build build = declaringProject.getModel().getBuild();
        build.addPlugin(plugin);
        projects = getSortedProjects();
        TestCase.assertEquals(parentProject, projects.get(0));
        // the order of these two is non-deterministic, based on when they're added to the reactor.
        TestCase.assertTrue(projects.contains(pluginProject));
        TestCase.assertTrue(projects.contains(pluginLevelDepProject));
        // the declaring project MUST be listed after the plugin and its plugin-level dep, though.
        TestCase.assertEquals(declaringProject, projects.get(3));
    }

    public void testPluginDependenciesInfluenceSorting_DeclarationInParent() throws Exception {
        List<MavenProject> projects = new ArrayList<>();
        MavenProject parentProject = ProjectSorterTest.createProject("groupId", "parent-declarer", "1.0");
        projects.add(parentProject);
        MavenProject pluginProject = ProjectSorterTest.createProject("groupId", "plugin", "1.0");
        pluginProject.setParent(parentProject);
        pluginProject.getModel().setParent(createParent(parentProject));
        projects.add(pluginProject);
        MavenProject pluginLevelDepProject = ProjectSorterTest.createProject("groupId", "plugin-level-dep", "1.0");
        pluginLevelDepProject.setParent(parentProject);
        pluginLevelDepProject.getModel().setParent(createParent(parentProject));
        projects.add(pluginLevelDepProject);
        Plugin plugin = createPlugin(pluginProject);
        plugin.addDependency(createDependency(pluginLevelDepProject));
        Build build = parentProject.getModel().getBuild();
        build.addPlugin(plugin);
        projects = getSortedProjects();
        System.out.println(projects);
        TestCase.assertEquals(parentProject, projects.get(0));
        // the order of these two is non-deterministic, based on when they're added to the reactor.
        TestCase.assertTrue(projects.contains(pluginProject));
        TestCase.assertTrue(projects.contains(pluginLevelDepProject));
    }

    public void testPluginVersionsAreConsidered() throws Exception {
        List<MavenProject> projects = new ArrayList<>();
        MavenProject pluginProjectA = ProjectSorterTest.createProject("group", "plugin-a", "2.0-SNAPSHOT");
        projects.add(pluginProjectA);
        pluginProjectA.getModel().getBuild().addPlugin(createPlugin("group", "plugin-b", "1.0"));
        MavenProject pluginProjectB = ProjectSorterTest.createProject("group", "plugin-b", "2.0-SNAPSHOT");
        projects.add(pluginProjectB);
        pluginProjectB.getModel().getBuild().addPlugin(createPlugin("group", "plugin-a", "1.0"));
        projects = getSortedProjects();
        TestCase.assertTrue(projects.contains(pluginProjectA));
        TestCase.assertTrue(projects.contains(pluginProjectB));
    }

    public void testDependencyPrecedesProjectThatUsesSpecificDependencyVersion() throws Exception {
        List<MavenProject> projects = new ArrayList<>();
        MavenProject usingProject = ProjectSorterTest.createProject("group", "project", "1.0");
        projects.add(usingProject);
        usingProject.getModel().addDependency(createDependency("group", "dependency", "1.0"));
        MavenProject pluginProject = ProjectSorterTest.createProject("group", "dependency", "1.0");
        projects.add(pluginProject);
        projects = getSortedProjects();
        TestCase.assertEquals(pluginProject, projects.get(0));
        TestCase.assertEquals(usingProject, projects.get(1));
    }

    public void testDependencyPrecedesProjectThatUsesUnresolvedDependencyVersion() throws Exception {
        List<MavenProject> projects = new ArrayList<>();
        MavenProject usingProject = ProjectSorterTest.createProject("group", "project", "1.0");
        projects.add(usingProject);
        usingProject.getModel().addDependency(createDependency("group", "dependency", "[1.0,)"));
        MavenProject pluginProject = ProjectSorterTest.createProject("group", "dependency", "1.0");
        projects.add(pluginProject);
        projects = getSortedProjects();
        TestCase.assertEquals(pluginProject, projects.get(0));
        TestCase.assertEquals(usingProject, projects.get(1));
    }
}

