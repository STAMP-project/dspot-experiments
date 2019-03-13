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
package org.springframework.boot.cli.compiler.dependencies;


import org.hamcrest.Matchers;
import org.junit.Test;


/**
 * Tests for {@link SpringBootDependenciesDependencyManagement}
 *
 * @author Andy Wilkinson
 */
public class SpringBootDependenciesDependencyManagementTests {
    private final DependencyManagement dependencyManagement = new SpringBootDependenciesDependencyManagement();

    @Test
    public void springBootVersion() {
        assertThat(this.dependencyManagement.getSpringBootVersion()).isNotNull();
    }

    @Test
    public void find() {
        Dependency dependency = this.dependencyManagement.find("spring-boot");
        assertThat(dependency).isNotNull();
        assertThat(dependency.getGroupId()).isEqualTo("org.springframework.boot");
        assertThat(dependency.getArtifactId()).isEqualTo("spring-boot");
    }

    @Test
    public void getDependencies() {
        assertThat(this.dependencyManagement.getDependencies()).isNotEqualTo(Matchers.empty());
    }
}

