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


import java.util.Arrays;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;


/**
 * Tests for {@link CompositeDependencyManagement}
 *
 * @author Andy Wilkinson
 */
public class CompositeDependencyManagementTests {
    @Mock
    private DependencyManagement dependencyManagement1;

    @Mock
    private DependencyManagement dependencyManagement2;

    @Test
    public void unknownSpringBootVersion() {
        BDDMockito.given(this.dependencyManagement1.getSpringBootVersion()).willReturn(null);
        BDDMockito.given(this.dependencyManagement2.getSpringBootVersion()).willReturn(null);
        assertThat(getSpringBootVersion()).isNull();
    }

    @Test
    public void knownSpringBootVersion() {
        BDDMockito.given(this.dependencyManagement1.getSpringBootVersion()).willReturn("1.2.3");
        BDDMockito.given(this.dependencyManagement2.getSpringBootVersion()).willReturn("1.2.4");
        assertThat(getSpringBootVersion()).isEqualTo("1.2.3");
    }

    @Test
    public void unknownDependency() {
        BDDMockito.given(this.dependencyManagement1.find("artifact")).willReturn(null);
        BDDMockito.given(this.dependencyManagement2.find("artifact")).willReturn(null);
        assertThat(find("artifact")).isNull();
    }

    @Test
    public void knownDependency() {
        BDDMockito.given(this.dependencyManagement1.find("artifact")).willReturn(new Dependency("test", "artifact", "1.2.3"));
        BDDMockito.given(this.dependencyManagement2.find("artifact")).willReturn(new Dependency("test", "artifact", "1.2.4"));
        assertThat(find("artifact")).isEqualTo(new Dependency("test", "artifact", "1.2.3"));
    }

    @Test
    public void getDependencies() {
        BDDMockito.given(this.dependencyManagement1.getDependencies()).willReturn(Arrays.asList(new Dependency("test", "artifact", "1.2.3")));
        BDDMockito.given(this.dependencyManagement2.getDependencies()).willReturn(Arrays.asList(new Dependency("test", "artifact", "1.2.4")));
        assertThat(new CompositeDependencyManagement(this.dependencyManagement1, this.dependencyManagement2).getDependencies()).containsOnly(new Dependency("test", "artifact", "1.2.3"), new Dependency("test", "artifact", "1.2.4"));
    }
}

