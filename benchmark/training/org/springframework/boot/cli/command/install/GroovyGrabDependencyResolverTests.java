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
package org.springframework.boot.cli.command.install;


import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Condition;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.boot.testsupport.assertj.Matched;


/**
 * Tests for {@link GroovyGrabDependencyResolver}.
 *
 * @author Andy Wilkinson
 */
public class GroovyGrabDependencyResolverTests {
    private DependencyResolver resolver;

    @Test
    public void resolveArtifactWithNoDependencies() throws Exception {
        List<File> resolved = this.resolver.resolve(Arrays.asList("commons-logging:commons-logging:1.1.3"));
        assertThat(resolved).hasSize(1);
        assertThat(getNames(resolved)).containsOnly("commons-logging-1.1.3.jar");
    }

    @Test
    public void resolveArtifactWithDependencies() throws Exception {
        List<File> resolved = this.resolver.resolve(Arrays.asList("org.springframework:spring-core:4.1.1.RELEASE"));
        assertThat(resolved).hasSize(2);
        assertThat(getNames(resolved)).containsOnly("commons-logging-1.1.3.jar", "spring-core-4.1.1.RELEASE.jar");
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void resolveShorthandArtifactWithDependencies() throws Exception {
        List<File> resolved = this.resolver.resolve(Arrays.asList("spring-beans"));
        assertThat(resolved).hasSize(3);
        assertThat(getNames(resolved)).has(((Condition) (Matched.by(Matchers.hasItems(Matchers.startsWith("spring-core-"), Matchers.startsWith("spring-beans-"), Matchers.startsWith("spring-jcl-"))))));
    }

    @Test
    public void resolveMultipleArtifacts() throws Exception {
        List<File> resolved = this.resolver.resolve(Arrays.asList("junit:junit:4.11", "commons-logging:commons-logging:1.1.3"));
        assertThat(resolved).hasSize(3);
        assertThat(getNames(resolved)).containsOnly("junit-4.11.jar", "commons-logging-1.1.3.jar", "hamcrest-core-1.3.jar");
    }
}

