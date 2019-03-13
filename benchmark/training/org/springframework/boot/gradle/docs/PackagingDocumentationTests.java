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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.gradle.junit.GradleMultiDslSuite;
import org.springframework.boot.gradle.testkit.Dsl;
import org.springframework.boot.gradle.testkit.GradleBuild;
import org.springframework.util.FileCopyUtils;


/**
 * Tests for the packaging documentation.
 *
 * @author Andy Wilkinson
 * @author Jean-Baptiste Nizet
 */
@RunWith(GradleMultiDslSuite.class)
public class PackagingDocumentationTests {
    @Rule
    public GradleBuild gradleBuild;

    public Dsl dsl;

    @Test
    public void warContainerDependencyEvaluatesSuccessfully() {
        this.gradleBuild.script("src/main/gradle/packaging/war-container-dependency").build();
    }

    @Test
    public void bootJarMainClass() throws IOException {
        this.gradleBuild.script("src/main/gradle/packaging/boot-jar-main-class").build("bootJar");
        File file = new File(this.gradleBuild.getProjectDir(), (("build/libs/" + (this.gradleBuild.getProjectDir().getName())) + ".jar"));
        assertThat(file).isFile();
        try (JarFile jar = new JarFile(file)) {
            assertThat(jar.getManifest().getMainAttributes().getValue("Start-Class")).isEqualTo("com.example.ExampleApplication");
        }
    }

    @Test
    public void bootJarManifestMainClass() throws IOException {
        this.gradleBuild.script("src/main/gradle/packaging/boot-jar-manifest-main-class").build("bootJar");
        File file = new File(this.gradleBuild.getProjectDir(), (("build/libs/" + (this.gradleBuild.getProjectDir().getName())) + ".jar"));
        assertThat(file).isFile();
        try (JarFile jar = new JarFile(file)) {
            assertThat(jar.getManifest().getMainAttributes().getValue("Start-Class")).isEqualTo("com.example.ExampleApplication");
        }
    }

    @Test
    public void applicationPluginMainClass() throws IOException {
        this.gradleBuild.script("src/main/gradle/packaging/application-plugin-main-class").build("bootJar");
        File file = new File(this.gradleBuild.getProjectDir(), (("build/libs/" + (this.gradleBuild.getProjectDir().getName())) + ".jar"));
        assertThat(file).isFile();
        try (JarFile jar = new JarFile(file)) {
            assertThat(jar.getManifest().getMainAttributes().getValue("Start-Class")).isEqualTo("com.example.ExampleApplication");
        }
    }

    @Test
    public void springBootDslMainClass() throws IOException {
        this.gradleBuild.script("src/main/gradle/packaging/spring-boot-dsl-main-class").build("bootJar");
        File file = new File(this.gradleBuild.getProjectDir(), (("build/libs/" + (this.gradleBuild.getProjectDir().getName())) + ".jar"));
        assertThat(file).isFile();
        try (JarFile jar = new JarFile(file)) {
            assertThat(jar.getManifest().getMainAttributes().getValue("Start-Class")).isEqualTo("com.example.ExampleApplication");
        }
    }

    @Test
    public void bootWarIncludeDevtools() throws IOException {
        jarFile(new File(this.gradleBuild.getProjectDir(), "spring-boot-devtools-1.2.3.RELEASE.jar"));
        this.gradleBuild.script("src/main/gradle/packaging/boot-war-include-devtools").build("bootWar");
        File file = new File(this.gradleBuild.getProjectDir(), (("build/libs/" + (this.gradleBuild.getProjectDir().getName())) + ".war"));
        assertThat(file).isFile();
        try (JarFile jar = new JarFile(file)) {
            assertThat(jar.getEntry("WEB-INF/lib/spring-boot-devtools-1.2.3.RELEASE.jar")).isNotNull();
        }
    }

    @Test
    public void bootJarRequiresUnpack() throws IOException {
        this.gradleBuild.script("src/main/gradle/packaging/boot-jar-requires-unpack").build("bootJar");
        File file = new File(this.gradleBuild.getProjectDir(), (("build/libs/" + (this.gradleBuild.getProjectDir().getName())) + ".jar"));
        assertThat(file).isFile();
        try (JarFile jar = new JarFile(file)) {
            JarEntry entry = jar.getJarEntry("BOOT-INF/lib/jruby-complete-1.7.25.jar");
            assertThat(entry).isNotNull();
            assertThat(entry.getComment()).startsWith("UNPACK:");
        }
    }

    @Test
    public void bootJarIncludeLaunchScript() throws IOException {
        this.gradleBuild.script("src/main/gradle/packaging/boot-jar-include-launch-script").build("bootJar");
        File file = new File(this.gradleBuild.getProjectDir(), (("build/libs/" + (this.gradleBuild.getProjectDir().getName())) + ".jar"));
        assertThat(file).isFile();
        assertThat(FileCopyUtils.copyToString(new FileReader(file))).startsWith("#!/bin/bash");
    }

    @Test
    public void bootJarLaunchScriptProperties() throws IOException {
        this.gradleBuild.script("src/main/gradle/packaging/boot-jar-launch-script-properties").build("bootJar");
        File file = new File(this.gradleBuild.getProjectDir(), (("build/libs/" + (this.gradleBuild.getProjectDir().getName())) + ".jar"));
        assertThat(file).isFile();
        assertThat(FileCopyUtils.copyToString(new FileReader(file))).contains("example-app.log");
    }

    @Test
    public void bootJarCustomLaunchScript() throws IOException {
        File customScriptFile = new File(this.gradleBuild.getProjectDir(), "src/custom.script");
        customScriptFile.getParentFile().mkdirs();
        FileCopyUtils.copy("custom", new FileWriter(customScriptFile));
        this.gradleBuild.script("src/main/gradle/packaging/boot-jar-custom-launch-script").build("bootJar");
        File file = new File(this.gradleBuild.getProjectDir(), (("build/libs/" + (this.gradleBuild.getProjectDir().getName())) + ".jar"));
        assertThat(file).isFile();
        assertThat(FileCopyUtils.copyToString(new FileReader(file))).startsWith("custom");
    }

    @Test
    public void bootWarPropertiesLauncher() throws IOException {
        this.gradleBuild.script("src/main/gradle/packaging/boot-war-properties-launcher").build("bootWar");
        File file = new File(this.gradleBuild.getProjectDir(), (("build/libs/" + (this.gradleBuild.getProjectDir().getName())) + ".war"));
        assertThat(file).isFile();
        try (JarFile jar = new JarFile(file)) {
            assertThat(jar.getManifest().getMainAttributes().getValue("Main-Class")).isEqualTo("org.springframework.boot.loader.PropertiesLauncher");
        }
    }

    @Test
    public void bootJarAndJar() {
        this.gradleBuild.script("src/main/gradle/packaging/boot-jar-and-jar").build("assemble");
        File jar = new File(this.gradleBuild.getProjectDir(), (("build/libs/" + (this.gradleBuild.getProjectDir().getName())) + ".jar"));
        assertThat(jar).isFile();
        File bootJar = new File(this.gradleBuild.getProjectDir(), (("build/libs/" + (this.gradleBuild.getProjectDir().getName())) + "-boot.jar"));
        assertThat(bootJar).isFile();
    }
}

