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
package org.springframework.boot.gradle.docs;


import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.gradle.junit.GradleMultiDslSuite;
import org.springframework.boot.gradle.testkit.GradleBuild;


/**
 * Tests for the generating build info documentation.
 *
 * @author Andy Wilkinson
 * @author Jean-Baptiste Nizet
 */
@RunWith(GradleMultiDslSuite.class)
public class IntegratingWithActuatorDocumentationTests {
    @Rule
    public GradleBuild gradleBuild;

    @Test
    public void basicBuildInfo() throws IOException {
        this.gradleBuild.script("src/main/gradle/integrating-with-actuator/build-info-basic").build("bootBuildInfo");
        assertThat(new File(this.gradleBuild.getProjectDir(), "build/resources/main/META-INF/build-info.properties")).isFile();
    }

    @Test
    public void buildInfoCustomValues() throws IOException {
        this.gradleBuild.script("src/main/gradle/integrating-with-actuator/build-info-custom-values").build("bootBuildInfo");
        File file = new File(this.gradleBuild.getProjectDir(), "build/resources/main/META-INF/build-info.properties");
        assertThat(file).isFile();
        Properties properties = buildInfoProperties(file);
        assertThat(properties).containsEntry("build.artifact", "example-app");
        assertThat(properties).containsEntry("build.version", "1.2.3");
        assertThat(properties).containsEntry("build.group", "com.example");
        assertThat(properties).containsEntry("build.name", "Example application");
    }

    @Test
    public void buildInfoAdditional() throws IOException {
        this.gradleBuild.script("src/main/gradle/integrating-with-actuator/build-info-additional").build("bootBuildInfo");
        File file = new File(this.gradleBuild.getProjectDir(), "build/resources/main/META-INF/build-info.properties");
        assertThat(file).isFile();
        Properties properties = buildInfoProperties(file);
        assertThat(properties).containsEntry("build.a", "alpha");
        assertThat(properties).containsEntry("build.b", "bravo");
    }
}

