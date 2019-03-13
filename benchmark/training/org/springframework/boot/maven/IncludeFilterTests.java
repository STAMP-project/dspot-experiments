/**
 * Copyright 2012-2017 the original author or authors.
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


import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.artifact.filter.collection.ArtifactFilterException;
import org.junit.Test;


/**
 * Tests for {@link org.springframework.boot.maven.IncludeFilter}.
 *
 * @author David Turanski
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class IncludeFilterTests {
    @Test
    public void includeSimple() throws ArtifactFilterException {
        IncludeFilter filter = new IncludeFilter(Arrays.asList(createInclude("com.foo", "bar")));
        Artifact artifact = createArtifact("com.foo", "bar");
        Set result = filter.filter(Collections.singleton(artifact));
        assertThat(result).hasSize(1);
        assertThat(result.iterator().next()).isSameAs(artifact);
    }

    @Test
    public void includeGroupIdNoMatch() throws ArtifactFilterException {
        IncludeFilter filter = new IncludeFilter(Arrays.asList(createInclude("com.foo", "bar")));
        Artifact artifact = createArtifact("com.baz", "bar");
        Set result = filter.filter(Collections.singleton(artifact));
        assertThat(result).isEmpty();
    }

    @Test
    public void includeArtifactIdNoMatch() throws ArtifactFilterException {
        IncludeFilter filter = new IncludeFilter(Arrays.asList(createInclude("com.foo", "bar")));
        Artifact artifact = createArtifact("com.foo", "biz");
        Set result = filter.filter(Collections.singleton(artifact));
        assertThat(result).isEmpty();
    }

    @Test
    public void includeClassifier() throws ArtifactFilterException {
        IncludeFilter filter = new IncludeFilter(Arrays.asList(createInclude("com.foo", "bar", "jdk5")));
        Artifact artifact = createArtifact("com.foo", "bar", "jdk5");
        Set result = filter.filter(Collections.singleton(artifact));
        assertThat(result).hasSize(1);
        assertThat(result.iterator().next()).isSameAs(artifact);
    }

    @Test
    public void includeClassifierNoTargetClassifier() throws ArtifactFilterException {
        IncludeFilter filter = new IncludeFilter(Arrays.asList(createInclude("com.foo", "bar", "jdk5")));
        Artifact artifact = createArtifact("com.foo", "bar");
        Set result = filter.filter(Collections.singleton(artifact));
        assertThat(result).isEmpty();
    }

    @Test
    public void includeClassifierNoMatch() throws ArtifactFilterException {
        IncludeFilter filter = new IncludeFilter(Arrays.asList(createInclude("com.foo", "bar", "jdk5")));
        Artifact artifact = createArtifact("com.foo", "bar", "jdk6");
        Set result = filter.filter(Collections.singleton(artifact));
        assertThat(result).isEmpty();
    }

    @Test
    public void includeMulti() throws ArtifactFilterException {
        IncludeFilter filter = new IncludeFilter(Arrays.asList(createInclude("com.foo", "bar"), createInclude("com.foo", "bar2"), createInclude("org.acme", "app")));
        Set<Artifact> artifacts = new HashSet<>();
        artifacts.add(createArtifact("com.foo", "bar"));
        artifacts.add(createArtifact("com.foo", "bar"));
        Artifact anotherAcme = createArtifact("org.acme", "another-app");
        artifacts.add(anotherAcme);
        Set result = filter.filter(artifacts);
        assertThat(result).hasSize(2);
    }
}

