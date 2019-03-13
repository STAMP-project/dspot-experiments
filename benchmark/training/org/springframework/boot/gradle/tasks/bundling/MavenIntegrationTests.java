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
package org.springframework.boot.gradle.tasks.bundling;


import TaskOutcome.SUCCESS;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.gradle.testkit.runner.BuildResult;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.gradle.junit.GradleCompatibilitySuite;
import org.springframework.boot.gradle.testkit.GradleBuild;


/**
 * Integration tests for uploading Boot jars and wars using Gradle's Maven plugin.
 *
 * @author Andy Wilkinson
 */
@RunWith(GradleCompatibilitySuite.class)
public class MavenIntegrationTests {
    @Rule
    public GradleBuild gradleBuild;

    @Test
    public void bootJarCanBeUploaded() throws FileNotFoundException, IOException {
        BuildResult result = this.gradleBuild.build("uploadBootArchives");
        assertThat(result.task(":uploadBootArchives").getOutcome()).isEqualTo(SUCCESS);
        assertThat(artifactWithSuffix("jar")).isFile();
        assertThat(artifactWithSuffix("pom")).is(pomWith().groupId("com.example").artifactId(this.gradleBuild.getProjectDir().getName()).version("1.0").noPackaging().noDependencies());
    }

    @Test
    public void bootWarCanBeUploaded() throws IOException {
        BuildResult result = this.gradleBuild.build("uploadBootArchives");
        assertThat(result.task(":uploadBootArchives").getOutcome()).isEqualTo(SUCCESS);
        assertThat(artifactWithSuffix("war")).isFile();
        assertThat(artifactWithSuffix("pom")).is(pomWith().groupId("com.example").artifactId(this.gradleBuild.getProjectDir().getName()).version("1.0").packaging("war").noDependencies());
    }
}

