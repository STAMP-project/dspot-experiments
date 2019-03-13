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
package org.springframework.boot.cli.compiler;


import org.junit.Test;
import org.springframework.boot.cli.compiler.grape.RepositoryConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;


/**
 * Tests for {@link RepositoryConfigurationFactory}
 *
 * @author Andy Wilkinson
 */
public class RepositoryConfigurationFactoryTests {
    @Test
    public void defaultRepositories() {
        TestPropertyValues.of("user.home:src/test/resources/maven-settings/basic").applyToSystemProperties(() -> {
            List<RepositoryConfiguration> repositoryConfiguration = RepositoryConfigurationFactory.createDefaultRepositoryConfiguration();
            assertRepositoryConfiguration(repositoryConfiguration, "central", "local", "spring-snapshot", "spring-milestone");
            return null;
        });
    }

    @Test
    public void snapshotRepositoriesDisabled() {
        TestPropertyValues.of("user.home:src/test/resources/maven-settings/basic", "disableSpringSnapshotRepos:true").applyToSystemProperties(() -> {
            List<RepositoryConfiguration> repositoryConfiguration = RepositoryConfigurationFactory.createDefaultRepositoryConfiguration();
            assertRepositoryConfiguration(repositoryConfiguration, "central", "local");
            return null;
        });
    }

    @Test
    public void activeByDefaultProfileRepositories() {
        TestPropertyValues.of("user.home:src/test/resources/maven-settings/active-profile-repositories").applyToSystemProperties(() -> {
            List<RepositoryConfiguration> repositoryConfiguration = RepositoryConfigurationFactory.createDefaultRepositoryConfiguration();
            assertRepositoryConfiguration(repositoryConfiguration, "central", "local", "spring-snapshot", "spring-milestone", "active-by-default");
            return null;
        });
    }

    @Test
    public void activeByPropertyProfileRepositories() {
        TestPropertyValues.of("user.home:src/test/resources/maven-settings/active-profile-repositories", "foo:bar").applyToSystemProperties(() -> {
            List<RepositoryConfiguration> repositoryConfiguration = RepositoryConfigurationFactory.createDefaultRepositoryConfiguration();
            assertRepositoryConfiguration(repositoryConfiguration, "central", "local", "spring-snapshot", "spring-milestone", "active-by-property");
            return null;
        });
    }

    @Test
    public void interpolationProfileRepositories() {
        TestPropertyValues.of("user.home:src/test/resources/maven-settings/active-profile-repositories", "interpolate:true").applyToSystemProperties(() -> {
            List<RepositoryConfiguration> repositoryConfiguration = RepositoryConfigurationFactory.createDefaultRepositoryConfiguration();
            assertRepositoryConfiguration(repositoryConfiguration, "central", "local", "spring-snapshot", "spring-milestone", "interpolate-releases", "interpolate-snapshots");
            return null;
        });
    }
}

