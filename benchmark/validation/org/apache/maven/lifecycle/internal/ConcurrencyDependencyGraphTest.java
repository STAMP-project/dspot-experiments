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
package org.apache.maven.lifecycle.internal;


import java.util.List;
import junit.framework.TestCase;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.ProjectDependencyGraph;
import org.apache.maven.lifecycle.LifecycleNotFoundException;
import org.apache.maven.lifecycle.LifecyclePhaseNotFoundException;
import org.apache.maven.lifecycle.internal.builder.multithreaded.ConcurrencyDependencyGraph;
import org.apache.maven.lifecycle.internal.stub.ProjectDependencyGraphStub;
import org.apache.maven.plugin.InvalidPluginDescriptorException;
import org.apache.maven.plugin.MojoNotFoundException;
import org.apache.maven.plugin.PluginDescriptorParsingException;
import org.apache.maven.plugin.PluginNotFoundException;
import org.apache.maven.plugin.PluginResolutionException;
import org.apache.maven.plugin.prefix.NoPluginFoundForPrefixException;
import org.apache.maven.plugin.version.PluginVersionResolutionException;
import org.apache.maven.project.MavenProject;


/**
 *
 *
 * @author Kristian Rosenvold
 */
public class ConcurrencyDependencyGraphTest extends TestCase {
    public void testConcurrencyGraphPrimaryVersion() throws LifecycleNotFoundException, LifecyclePhaseNotFoundException, InvalidPluginDescriptorException, MojoNotFoundException, PluginDescriptorParsingException, PluginNotFoundException, PluginResolutionException, NoPluginFoundForPrefixException, PluginVersionResolutionException {
        ProjectDependencyGraph dependencyGraph = new ProjectDependencyGraphStub();
        final MavenSession session = ProjectDependencyGraphStub.getMavenSession();
        ConcurrencyDependencyGraph graph = new ConcurrencyDependencyGraph(ProjectDependencyGraphStub.getProjectBuildList(session), dependencyGraph);
        final List<MavenProject> projectBuilds = graph.getRootSchedulableBuilds();
        TestCase.assertEquals(1, projectBuilds.size());
        TestCase.assertEquals(ProjectDependencyGraphStub.A, projectBuilds.iterator().next());
        final List<MavenProject> subsequent = graph.markAsFinished(ProjectDependencyGraphStub.A);
        TestCase.assertEquals(2, subsequent.size());
        TestCase.assertEquals(ProjectDependencyGraphStub.B, subsequent.get(0));
        TestCase.assertEquals(ProjectDependencyGraphStub.C, subsequent.get(1));
        final List<MavenProject> bDescendants = graph.markAsFinished(ProjectDependencyGraphStub.B);
        TestCase.assertEquals(1, bDescendants.size());
        TestCase.assertEquals(ProjectDependencyGraphStub.Y, bDescendants.get(0));
        final List<MavenProject> cDescendants = graph.markAsFinished(ProjectDependencyGraphStub.C);
        TestCase.assertEquals(2, cDescendants.size());
        TestCase.assertEquals(ProjectDependencyGraphStub.X, cDescendants.get(0));
        TestCase.assertEquals(ProjectDependencyGraphStub.Z, cDescendants.get(1));
    }

    public void testConcurrencyGraphDifferentCompletionOrder() throws LifecycleNotFoundException, LifecyclePhaseNotFoundException, InvalidPluginDescriptorException, MojoNotFoundException, PluginDescriptorParsingException, PluginNotFoundException, PluginResolutionException, NoPluginFoundForPrefixException, PluginVersionResolutionException {
        ProjectDependencyGraph dependencyGraph = new ProjectDependencyGraphStub();
        final MavenSession session = ProjectDependencyGraphStub.getMavenSession();
        ConcurrencyDependencyGraph graph = new ConcurrencyDependencyGraph(ProjectDependencyGraphStub.getProjectBuildList(session), dependencyGraph);
        graph.markAsFinished(ProjectDependencyGraphStub.A);
        final List<MavenProject> cDescendants = graph.markAsFinished(ProjectDependencyGraphStub.C);
        TestCase.assertEquals(1, cDescendants.size());
        TestCase.assertEquals(ProjectDependencyGraphStub.Z, cDescendants.get(0));
        final List<MavenProject> bDescendants = graph.markAsFinished(ProjectDependencyGraphStub.B);
        TestCase.assertEquals(2, bDescendants.size());
        TestCase.assertEquals(ProjectDependencyGraphStub.X, bDescendants.get(0));
        TestCase.assertEquals(ProjectDependencyGraphStub.Y, bDescendants.get(1));
    }
}

