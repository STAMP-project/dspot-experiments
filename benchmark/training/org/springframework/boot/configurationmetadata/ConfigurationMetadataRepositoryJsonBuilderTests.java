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
package org.springframework.boot.configurationmetadata;


import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;


/**
 * Tests for {@link ConfigurationMetadataRepository}.
 *
 * @author Stephane Nicoll
 */
public class ConfigurationMetadataRepositoryJsonBuilderTests extends AbstractConfigurationMetadataTests {
    @Test
    public void nullResource() throws IOException {
        assertThatIllegalArgumentException().isThrownBy(() -> ConfigurationMetadataRepositoryJsonBuilder.create().withJsonResource(null));
    }

    @Test
    public void simpleRepository() throws IOException {
        try (InputStream foo = getInputStreamFor("foo")) {
            ConfigurationMetadataRepository repo = ConfigurationMetadataRepositoryJsonBuilder.create(foo).build();
            validateFoo(repo);
            assertThat(repo.getAllGroups()).hasSize(1);
            contains(repo.getAllProperties(), "spring.foo.name", "spring.foo.description", "spring.foo.counter");
            assertThat(repo.getAllProperties()).hasSize(3);
        }
    }

    @Test
    public void hintsOnMaps() throws IOException {
        try (InputStream map = getInputStreamFor("map")) {
            ConfigurationMetadataRepository repo = ConfigurationMetadataRepositoryJsonBuilder.create(map).build();
            validateMap(repo);
            assertThat(repo.getAllGroups()).hasSize(1);
            contains(repo.getAllProperties(), "spring.map.first", "spring.map.second", "spring.map.keys", "spring.map.values");
            assertThat(repo.getAllProperties()).hasSize(4);
        }
    }

    @Test
    public void severalRepositoriesNoConflict() throws IOException {
        try (InputStream foo = getInputStreamFor("foo");InputStream bar = getInputStreamFor("bar")) {
            ConfigurationMetadataRepository repo = ConfigurationMetadataRepositoryJsonBuilder.create(foo, bar).build();
            validateFoo(repo);
            validateBar(repo);
            assertThat(repo.getAllGroups()).hasSize(2);
            contains(repo.getAllProperties(), "spring.foo.name", "spring.foo.description", "spring.foo.counter", "spring.bar.name", "spring.bar.description", "spring.bar.counter");
            assertThat(repo.getAllProperties()).hasSize(6);
        }
    }

    @Test
    public void repositoryWithRoot() throws IOException {
        try (InputStream foo = getInputStreamFor("foo");InputStream root = getInputStreamFor("root")) {
            ConfigurationMetadataRepository repo = ConfigurationMetadataRepositoryJsonBuilder.create(foo, root).build();
            validateFoo(repo);
            assertThat(repo.getAllGroups()).hasSize(2);
            contains(repo.getAllProperties(), "spring.foo.name", "spring.foo.description", "spring.foo.counter", "spring.root.name", "spring.root2.name");
            assertThat(repo.getAllProperties()).hasSize(5);
        }
    }

    @Test
    public void severalRepositoriesIdenticalGroups() throws IOException {
        try (InputStream foo = getInputStreamFor("foo");InputStream foo2 = getInputStreamFor("foo2")) {
            ConfigurationMetadataRepository repo = ConfigurationMetadataRepositoryJsonBuilder.create(foo, foo2).build();
            assertThat(repo.getAllGroups()).hasSize(1);
            ConfigurationMetadataGroup group = repo.getAllGroups().get("spring.foo");
            contains(group.getSources(), "org.acme.Foo", "org.acme.Foo2", "org.springframework.boot.FooProperties");
            assertThat(group.getSources()).hasSize(3);
            contains(group.getProperties(), "spring.foo.name", "spring.foo.description", "spring.foo.counter", "spring.foo.enabled", "spring.foo.type");
            assertThat(group.getProperties()).hasSize(5);
            contains(repo.getAllProperties(), "spring.foo.name", "spring.foo.description", "spring.foo.counter", "spring.foo.enabled", "spring.foo.type");
            assertThat(repo.getAllProperties()).hasSize(5);
        }
    }

    @Test
    public void emptyGroups() throws IOException {
        try (InputStream in = getInputStreamFor("empty-groups")) {
            ConfigurationMetadataRepository repo = ConfigurationMetadataRepositoryJsonBuilder.create(in).build();
            validateEmptyGroup(repo);
            assertThat(repo.getAllGroups()).hasSize(1);
            contains(repo.getAllProperties(), "name", "title");
            assertThat(repo.getAllProperties()).hasSize(2);
        }
    }

    @Test
    public void builderInstancesAreIsolated() throws IOException {
        try (InputStream foo = getInputStreamFor("foo");InputStream bar = getInputStreamFor("bar")) {
            ConfigurationMetadataRepositoryJsonBuilder builder = ConfigurationMetadataRepositoryJsonBuilder.create();
            ConfigurationMetadataRepository firstRepo = builder.withJsonResource(foo).build();
            validateFoo(firstRepo);
            ConfigurationMetadataRepository secondRepo = builder.withJsonResource(bar).build();
            validateFoo(secondRepo);
            validateBar(secondRepo);
            // first repo not impacted by second build
            assertThat(secondRepo).isNotEqualTo(firstRepo);
            assertThat(firstRepo.getAllGroups()).hasSize(1);
            assertThat(firstRepo.getAllProperties()).hasSize(3);
            assertThat(secondRepo.getAllGroups()).hasSize(2);
            assertThat(secondRepo.getAllProperties()).hasSize(6);
        }
    }
}

