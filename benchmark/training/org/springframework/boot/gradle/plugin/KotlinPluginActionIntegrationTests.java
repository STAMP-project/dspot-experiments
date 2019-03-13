/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.gradle.plugin;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.gradle.junit.GradleCompatibilitySuite;
import org.springframework.boot.gradle.testkit.GradleBuild;


/**
 * Integration tests for {@link KotlinPluginAction}.
 *
 * @author Andy Wilkinson
 */
@RunWith(GradleCompatibilitySuite.class)
public class KotlinPluginActionIntegrationTests {
    @Rule
    public GradleBuild gradleBuild;

    @Test
    public void noKotlinVersionPropertyWithoutKotlinPlugin() {
        assertThat(this.gradleBuild.build("kotlinVersion").getOutput()).contains("Kotlin version: none");
    }

    @Test
    public void kotlinVersionPropertyIsSet() {
        String output = this.gradleBuild.build("kotlinVersion", "dependencies", "--configuration", "compileClasspath").getOutput();
        assertThat(output).containsPattern("Kotlin version: [0-9]\\.[0-9]\\.[0-9]+");
    }

    @Test
    public void kotlinCompileTasksUseJavaParametersFlagByDefault() {
        assertThat(this.gradleBuild.build("kotlinCompileTasksJavaParameters").getOutput()).contains("compileKotlin java parameters: true").contains("compileTestKotlin java parameters: true");
    }

    @Test
    public void kotlinCompileTasksCanOverrideDefaultJavaParametersFlag() {
        assertThat(this.gradleBuild.build("kotlinCompileTasksJavaParameters").getOutput()).contains("compileKotlin java parameters: false").contains("compileTestKotlin java parameters: false");
    }
}

