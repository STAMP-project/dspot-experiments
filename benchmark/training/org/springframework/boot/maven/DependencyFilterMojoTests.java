/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.maven;


import Artifact.SCOPE_RUNTIME;
import Artifact.SCOPE_SYSTEM;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.shared.artifact.filter.collection.ArtifactsFilter;
import org.junit.Test;


/**
 * Tests for {@link AbstractDependencyFilterMojo}.
 *
 * @author Stephane Nicoll
 */
public class DependencyFilterMojoTests {
    @Test
    public void filterDependencies() throws MojoExecutionException {
        DependencyFilterMojoTests.TestableDependencyFilterMojo mojo = new DependencyFilterMojoTests.TestableDependencyFilterMojo(Collections.emptyList(), "com.foo");
        Artifact artifact = DependencyFilterMojoTests.createArtifact("com.bar", "one");
        Set<Artifact> artifacts = mojo.filterDependencies(DependencyFilterMojoTests.createArtifact("com.foo", "one"), DependencyFilterMojoTests.createArtifact("com.foo", "two"), artifact);
        assertThat(artifacts).hasSize(1);
        assertThat(artifacts.iterator().next()).isSameAs(artifact);
    }

    @Test
    public void filterGroupIdExactMatch() throws MojoExecutionException {
        DependencyFilterMojoTests.TestableDependencyFilterMojo mojo = new DependencyFilterMojoTests.TestableDependencyFilterMojo(Collections.emptyList(), "com.foo");
        Artifact artifact = DependencyFilterMojoTests.createArtifact("com.foo.bar", "one");
        Set<Artifact> artifacts = mojo.filterDependencies(DependencyFilterMojoTests.createArtifact("com.foo", "one"), DependencyFilterMojoTests.createArtifact("com.foo", "two"), artifact);
        assertThat(artifacts).hasSize(1);
        assertThat(artifacts.iterator().next()).isSameAs(artifact);
    }

    @Test
    public void filterScopeKeepOrder() throws MojoExecutionException {
        DependencyFilterMojoTests.TestableDependencyFilterMojo mojo = new DependencyFilterMojoTests.TestableDependencyFilterMojo(Collections.emptyList(), "", new org.apache.maven.shared.artifact.filter.collection.ScopeFilter(null, Artifact.SCOPE_SYSTEM));
        Artifact one = DependencyFilterMojoTests.createArtifact("com.foo", "one");
        Artifact two = DependencyFilterMojoTests.createArtifact("com.foo", "two", SCOPE_SYSTEM);
        Artifact three = DependencyFilterMojoTests.createArtifact("com.foo", "three", SCOPE_RUNTIME);
        Set<Artifact> artifacts = mojo.filterDependencies(one, two, three);
        assertThat(artifacts).containsExactly(one, three);
    }

    @Test
    public void filterGroupIdKeepOrder() throws MojoExecutionException {
        DependencyFilterMojoTests.TestableDependencyFilterMojo mojo = new DependencyFilterMojoTests.TestableDependencyFilterMojo(Collections.emptyList(), "com.foo");
        Artifact one = DependencyFilterMojoTests.createArtifact("com.foo", "one");
        Artifact two = DependencyFilterMojoTests.createArtifact("com.bar", "two");
        Artifact three = DependencyFilterMojoTests.createArtifact("com.bar", "three");
        Artifact four = DependencyFilterMojoTests.createArtifact("com.foo", "four");
        Set<Artifact> artifacts = mojo.filterDependencies(one, two, three, four);
        assertThat(artifacts).containsExactly(two, three);
    }

    @Test
    public void filterExcludeKeepOrder() throws MojoExecutionException {
        Exclude exclude = new Exclude();
        exclude.setGroupId("com.bar");
        exclude.setArtifactId("two");
        DependencyFilterMojoTests.TestableDependencyFilterMojo mojo = new DependencyFilterMojoTests.TestableDependencyFilterMojo(Collections.singletonList(exclude), "");
        Artifact one = DependencyFilterMojoTests.createArtifact("com.foo", "one");
        Artifact two = DependencyFilterMojoTests.createArtifact("com.bar", "two");
        Artifact three = DependencyFilterMojoTests.createArtifact("com.bar", "three");
        Artifact four = DependencyFilterMojoTests.createArtifact("com.foo", "four");
        Set<Artifact> artifacts = mojo.filterDependencies(one, two, three, four);
        assertThat(artifacts).containsExactly(one, three, four);
    }

    private static final class TestableDependencyFilterMojo extends AbstractDependencyFilterMojo {
        private final ArtifactsFilter[] additionalFilters;

        private TestableDependencyFilterMojo(List<Exclude> excludes, String excludeGroupIds, ArtifactsFilter... additionalFilters) {
            setExcludes(excludes);
            setExcludeGroupIds(excludeGroupIds);
            this.additionalFilters = additionalFilters;
        }

        public Set<Artifact> filterDependencies(Artifact... artifacts) throws MojoExecutionException {
            Set<Artifact> input = new java.util.LinkedHashSet(Arrays.asList(artifacts));
            return filterDependencies(input, getFilters(this.additionalFilters));
        }

        @Override
        public void execute() {
        }
    }
}

