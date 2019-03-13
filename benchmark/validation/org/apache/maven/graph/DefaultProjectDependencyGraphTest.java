/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.maven.graph;


import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import org.apache.maven.execution.ProjectDependencyGraph;
import org.apache.maven.project.DuplicateProjectException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.dag.CycleDetectedException;


/**
 *
 *
 * @author Kristian Rosenvold
 */
public class DefaultProjectDependencyGraphTest extends TestCase {
    private final MavenProject aProject = DefaultProjectDependencyGraphTest.createA();

    private final MavenProject depender1 = DefaultProjectDependencyGraphTest.createProject(Arrays.asList(DefaultProjectDependencyGraphTest.toDependency(aProject)), "depender1");

    private final MavenProject depender2 = DefaultProjectDependencyGraphTest.createProject(Arrays.asList(DefaultProjectDependencyGraphTest.toDependency(aProject)), "depender2");

    private final MavenProject depender3 = DefaultProjectDependencyGraphTest.createProject(Arrays.asList(DefaultProjectDependencyGraphTest.toDependency(aProject)), "depender3");

    private final MavenProject depender4 = DefaultProjectDependencyGraphTest.createProject(Arrays.asList(DefaultProjectDependencyGraphTest.toDependency(aProject), DefaultProjectDependencyGraphTest.toDependency(depender3)), "depender4");

    private final MavenProject transitiveOnly = DefaultProjectDependencyGraphTest.createProject(Arrays.asList(DefaultProjectDependencyGraphTest.toDependency(depender3)), "depender5");

    public void testGetSortedProjects() throws DuplicateProjectException, CycleDetectedException {
        ProjectDependencyGraph graph = new DefaultProjectDependencyGraph(Arrays.asList(depender1, aProject));
        final List<MavenProject> sortedProjects = graph.getSortedProjects();
        TestCase.assertEquals(aProject, sortedProjects.get(0));
        TestCase.assertEquals(depender1, sortedProjects.get(1));
    }

    public void testVerifyExpectedParentStructure() throws DuplicateProjectException, CycleDetectedException {
        // This test verifies the baseline structure used in subsequent tests. If this fails, the rest will fail.
        ProjectDependencyGraph graph = threeProjectsDependingOnASingle();
        final List<MavenProject> sortedProjects = graph.getSortedProjects();
        TestCase.assertEquals(aProject, sortedProjects.get(0));
        TestCase.assertEquals(depender1, sortedProjects.get(1));
        TestCase.assertEquals(depender2, sortedProjects.get(2));
        TestCase.assertEquals(depender3, sortedProjects.get(3));
    }

    public void testVerifyThatDownstreamProjectsComeInSortedOrder() throws DuplicateProjectException, CycleDetectedException {
        final List<MavenProject> downstreamProjects = threeProjectsDependingOnASingle().getDownstreamProjects(aProject, true);
        TestCase.assertEquals(depender1, downstreamProjects.get(0));
        TestCase.assertEquals(depender2, downstreamProjects.get(1));
        TestCase.assertEquals(depender3, downstreamProjects.get(2));
    }

    public void testTransitivesInOrder() throws DuplicateProjectException, CycleDetectedException {
        final ProjectDependencyGraph graph = new DefaultProjectDependencyGraph(Arrays.asList(depender1, depender4, depender2, depender3, aProject));
        final List<MavenProject> downstreamProjects = graph.getDownstreamProjects(aProject, true);
        TestCase.assertEquals(depender1, downstreamProjects.get(0));
        TestCase.assertEquals(depender3, downstreamProjects.get(1));
        TestCase.assertEquals(depender4, downstreamProjects.get(2));
        TestCase.assertEquals(depender2, downstreamProjects.get(3));
    }

    public void testNonTransitivesInOrder() throws DuplicateProjectException, CycleDetectedException {
        final ProjectDependencyGraph graph = new DefaultProjectDependencyGraph(Arrays.asList(depender1, depender4, depender2, depender3, aProject));
        final List<MavenProject> downstreamProjects = graph.getDownstreamProjects(aProject, false);
        TestCase.assertEquals(depender1, downstreamProjects.get(0));
        TestCase.assertEquals(depender3, downstreamProjects.get(1));
        TestCase.assertEquals(depender4, downstreamProjects.get(2));
        TestCase.assertEquals(depender2, downstreamProjects.get(3));
    }

    public void testWithTransitiveOnly() throws DuplicateProjectException, CycleDetectedException {
        final ProjectDependencyGraph graph = new DefaultProjectDependencyGraph(Arrays.asList(depender1, transitiveOnly, depender2, depender3, aProject));
        final List<MavenProject> downstreamProjects = graph.getDownstreamProjects(aProject, true);
        TestCase.assertEquals(depender1, downstreamProjects.get(0));
        TestCase.assertEquals(depender3, downstreamProjects.get(1));
        TestCase.assertEquals(transitiveOnly, downstreamProjects.get(2));
        TestCase.assertEquals(depender2, downstreamProjects.get(3));
    }

    public void testWithMissingTransitiveOnly() throws DuplicateProjectException, CycleDetectedException {
        final ProjectDependencyGraph graph = new DefaultProjectDependencyGraph(Arrays.asList(depender1, transitiveOnly, depender2, depender3, aProject));
        final List<MavenProject> downstreamProjects = graph.getDownstreamProjects(aProject, false);
        TestCase.assertEquals(depender1, downstreamProjects.get(0));
        TestCase.assertEquals(depender3, downstreamProjects.get(1));
        TestCase.assertEquals(depender2, downstreamProjects.get(2));
    }

    public void testGetUpstreamProjects() throws DuplicateProjectException, CycleDetectedException {
        ProjectDependencyGraph graph = threeProjectsDependingOnASingle();
        final List<MavenProject> downstreamProjects = graph.getUpstreamProjects(depender1, true);
        TestCase.assertEquals(aProject, downstreamProjects.get(0));
    }
}

