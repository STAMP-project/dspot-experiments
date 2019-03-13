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
package org.springframework.boot.gradle.dsl;


import TaskOutcome.SUCCESS;
import java.io.IOException;
import java.util.Properties;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.gradle.testkit.GradleBuild;


/**
 * Integration tests for {@link BuildInfo} created using the
 * {@link org.springframework.boot.gradle.dsl.SpringBootExtension DSL}.
 *
 * @author Andy Wilkinson
 */
public class BuildInfoDslIntegrationTests {
    @Rule
    public final GradleBuild gradleBuild = new GradleBuild();

    @Test
    public void basicJar() throws IOException {
        assertThat(this.gradleBuild.build("bootBuildInfo", "--stacktrace").task(":bootBuildInfo").getOutcome()).isEqualTo(SUCCESS);
        Properties properties = buildInfoProperties();
        assertThat(properties).containsEntry("build.name", this.gradleBuild.getProjectDir().getName());
        assertThat(properties).containsEntry("build.artifact", this.gradleBuild.getProjectDir().getName());
        assertThat(properties).containsEntry("build.group", "com.example");
        assertThat(properties).containsEntry("build.version", "1.0");
    }

    @Test
    public void jarWithCustomName() throws IOException {
        assertThat(this.gradleBuild.build("bootBuildInfo", "--stacktrace").task(":bootBuildInfo").getOutcome()).isEqualTo(SUCCESS);
        Properties properties = buildInfoProperties();
        assertThat(properties).containsEntry("build.name", this.gradleBuild.getProjectDir().getName());
        assertThat(properties).containsEntry("build.artifact", "foo");
        assertThat(properties).containsEntry("build.group", "com.example");
        assertThat(properties).containsEntry("build.version", "1.0");
    }

    @Test
    public void basicWar() throws IOException {
        assertThat(this.gradleBuild.build("bootBuildInfo", "--stacktrace").task(":bootBuildInfo").getOutcome()).isEqualTo(SUCCESS);
        Properties properties = buildInfoProperties();
        assertThat(properties).containsEntry("build.name", this.gradleBuild.getProjectDir().getName());
        assertThat(properties).containsEntry("build.artifact", this.gradleBuild.getProjectDir().getName());
        assertThat(properties).containsEntry("build.group", "com.example");
        assertThat(properties).containsEntry("build.version", "1.0");
    }

    @Test
    public void warWithCustomName() throws IOException {
        assertThat(this.gradleBuild.build("bootBuildInfo", "--stacktrace").task(":bootBuildInfo").getOutcome()).isEqualTo(SUCCESS);
        Properties properties = buildInfoProperties();
        assertThat(properties).containsEntry("build.name", this.gradleBuild.getProjectDir().getName());
        assertThat(properties).containsEntry("build.artifact", "foo");
        assertThat(properties).containsEntry("build.group", "com.example");
        assertThat(properties).containsEntry("build.version", "1.0");
    }

    @Test
    public void additionalProperties() throws IOException {
        assertThat(this.gradleBuild.build("bootBuildInfo", "--stacktrace").task(":bootBuildInfo").getOutcome()).isEqualTo(SUCCESS);
        Properties properties = buildInfoProperties();
        assertThat(properties).containsEntry("build.name", this.gradleBuild.getProjectDir().getName());
        assertThat(properties).containsEntry("build.artifact", this.gradleBuild.getProjectDir().getName());
        assertThat(properties).containsEntry("build.group", "com.example");
        assertThat(properties).containsEntry("build.version", "1.0");
        assertThat(properties).containsEntry("build.a", "alpha");
        assertThat(properties).containsEntry("build.b", "bravo");
    }

    @Test
    public void classesDependency() throws IOException {
        assertThat(this.gradleBuild.build("classes", "--stacktrace").task(":bootBuildInfo").getOutcome()).isEqualTo(SUCCESS);
    }
}

