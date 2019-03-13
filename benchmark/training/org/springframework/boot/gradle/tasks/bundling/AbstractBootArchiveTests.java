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
package org.springframework.boot.gradle.tasks.bundling;


import BootZipCopyAction.CONSTANT_TIME_FOR_ZIP_ENTRIES;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.gradle.api.Project;
import org.gradle.api.tasks.bundling.Jar;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.boot.loader.tools.DefaultLaunchScript;


/**
 * Abstract base class for testing {@link BootArchive} implementations.
 *
 * @param <T>
 * 		the type of the concrete BootArchive implementation
 * @author Andy Wilkinson
 */
public abstract class AbstractBootArchiveTests<T extends Jar & BootArchive> {
    @Rule
    public final TemporaryFolder temp = new TemporaryFolder();

    private final Class<T> taskClass;

    private final String launcherClass;

    private final String libPath;

    private final String classesPath;

    private Project project;

    private T task;

    protected AbstractBootArchiveTests(Class<T> taskClass, String launcherClass, String libPath, String classesPath) {
        this.taskClass = taskClass;
        this.launcherClass = launcherClass;
        this.libPath = libPath;
        this.classesPath = classesPath;
    }

    @Test
    public void basicArchiveCreation() throws IOException {
        setMainClassName("com.example.Main");
        executeTask();
        assertThat(getArchivePath()).exists();
        try (JarFile jarFile = new JarFile(getArchivePath())) {
            AbstractBootArchiveTests.assertThat(jarFile.getManifest().getMainAttributes().getValue("Main-Class")).isEqualTo(this.launcherClass);
            AbstractBootArchiveTests.assertThat(jarFile.getManifest().getMainAttributes().getValue("Start-Class")).isEqualTo("com.example.Main");
        }
    }

    @Test
    public void classpathJarsArePackagedBeneathLibPath() throws IOException {
        setMainClassName("com.example.Main");
        this.task.classpath(jarFile("one.jar"), jarFile("two.jar"));
        executeTask();
        try (JarFile jarFile = new JarFile(getArchivePath())) {
            AbstractBootArchiveTests.assertThat(jarFile.getEntry(((this.libPath) + "/one.jar"))).isNotNull();
            AbstractBootArchiveTests.assertThat(jarFile.getEntry(((this.libPath) + "/two.jar"))).isNotNull();
        }
    }

    @Test
    public void classpathFoldersArePackagedBeneathClassesPath() throws IOException {
        setMainClassName("com.example.Main");
        File classpathFolder = this.temp.newFolder();
        File applicationClass = new File(classpathFolder, "com/example/Application.class");
        applicationClass.getParentFile().mkdirs();
        applicationClass.createNewFile();
        this.task.classpath(classpathFolder);
        executeTask();
        try (JarFile jarFile = new JarFile(getArchivePath())) {
            AbstractBootArchiveTests.assertThat(jarFile.getEntry(((this.classesPath) + "/com/example/Application.class"))).isNotNull();
        }
    }

    @Test
    public void moduleInfoClassIsPackagedInTheRootOfTheArchive() throws IOException {
        setMainClassName("com.example.Main");
        File classpathFolder = this.temp.newFolder();
        File moduleInfoClass = new File(classpathFolder, "module-info.class");
        moduleInfoClass.getParentFile().mkdirs();
        moduleInfoClass.createNewFile();
        File applicationClass = new File(classpathFolder, "com/example/Application.class");
        applicationClass.getParentFile().mkdirs();
        applicationClass.createNewFile();
        this.task.classpath(classpathFolder);
        execute();
        try (JarFile jarFile = new JarFile(getArchivePath())) {
            AbstractBootArchiveTests.assertThat(jarFile.getEntry(((this.classesPath) + "/com/example/Application.class"))).isNotNull();
            AbstractBootArchiveTests.assertThat(jarFile.getEntry("com/example/Application.class")).isNull();
            AbstractBootArchiveTests.assertThat(jarFile.getEntry("module-info.class")).isNotNull();
            AbstractBootArchiveTests.assertThat(jarFile.getEntry(((this.classesPath) + "/module-info.class"))).isNull();
        }
    }

    @Test
    public void classpathCanBeSetUsingAFileCollection() throws IOException {
        setMainClassName("com.example.Main");
        this.task.classpath(jarFile("one.jar"));
        this.task.setClasspath(getProject().files(jarFile("two.jar")));
        executeTask();
        try (JarFile jarFile = new JarFile(getArchivePath())) {
            AbstractBootArchiveTests.assertThat(jarFile.getEntry(((this.libPath) + "/one.jar"))).isNull();
            AbstractBootArchiveTests.assertThat(jarFile.getEntry(((this.libPath) + "/two.jar"))).isNotNull();
        }
    }

    @Test
    public void classpathCanBeSetUsingAnObject() throws IOException {
        setMainClassName("com.example.Main");
        this.task.classpath(jarFile("one.jar"));
        setClasspath(jarFile("two.jar"));
        executeTask();
        try (JarFile jarFile = new JarFile(getArchivePath())) {
            AbstractBootArchiveTests.assertThat(jarFile.getEntry(((this.libPath) + "/one.jar"))).isNull();
            AbstractBootArchiveTests.assertThat(jarFile.getEntry(((this.libPath) + "/two.jar"))).isNotNull();
        }
    }

    @Test
    public void filesOnTheClasspathThatAreNotZipFilesAreSkipped() throws IOException {
        setMainClassName("com.example.Main");
        this.task.classpath(this.temp.newFile("test.pom"));
        execute();
        try (JarFile jarFile = new JarFile(getArchivePath())) {
            AbstractBootArchiveTests.assertThat(jarFile.getEntry(((this.libPath) + "/test.pom"))).isNull();
        }
    }

    @Test
    public void loaderIsWrittenToTheRootOfTheJar() throws IOException {
        setMainClassName("com.example.Main");
        executeTask();
        try (JarFile jarFile = new JarFile(getArchivePath())) {
            AbstractBootArchiveTests.assertThat(jarFile.getEntry("org/springframework/boot/loader/LaunchedURLClassLoader.class")).isNotNull();
            AbstractBootArchiveTests.assertThat(jarFile.getEntry("org/springframework/boot/loader/")).isNotNull();
        }
    }

    @Test
    public void loaderIsWrittenToTheRootOfTheJarWhenUsingThePropertiesLauncher() throws IOException {
        setMainClassName("com.example.Main");
        executeTask();
        getManifest().getAttributes().put("Main-Class", "org.springframework.boot.loader.PropertiesLauncher");
        try (JarFile jarFile = new JarFile(getArchivePath())) {
            AbstractBootArchiveTests.assertThat(jarFile.getEntry("org/springframework/boot/loader/LaunchedURLClassLoader.class")).isNotNull();
            AbstractBootArchiveTests.assertThat(jarFile.getEntry("org/springframework/boot/loader/")).isNotNull();
        }
    }

    @Test
    public void unpackCommentIsAddedToEntryIdentifiedByAPattern() throws IOException {
        setMainClassName("com.example.Main");
        this.task.classpath(jarFile("one.jar"), jarFile("two.jar"));
        requiresUnpack("**/one.jar");
        executeTask();
        try (JarFile jarFile = new JarFile(getArchivePath())) {
            AbstractBootArchiveTests.assertThat(jarFile.getEntry(((this.libPath) + "/one.jar")).getComment()).startsWith("UNPACK:");
            AbstractBootArchiveTests.assertThat(jarFile.getEntry(((this.libPath) + "/two.jar")).getComment()).isNull();
        }
    }

    @Test
    public void unpackCommentIsAddedToEntryIdentifiedByASpec() throws IOException {
        setMainClassName("com.example.Main");
        this.task.classpath(jarFile("one.jar"), jarFile("two.jar"));
        this.task.requiresUnpack(( element) -> element.getName().endsWith("two.jar"));
        executeTask();
        try (JarFile jarFile = new JarFile(getArchivePath())) {
            AbstractBootArchiveTests.assertThat(jarFile.getEntry(((this.libPath) + "/two.jar")).getComment()).startsWith("UNPACK:");
            AbstractBootArchiveTests.assertThat(jarFile.getEntry(((this.libPath) + "/one.jar")).getComment()).isNull();
        }
    }

    @Test
    public void launchScriptCanBePrepended() throws IOException {
        setMainClassName("com.example.Main");
        this.task.launchScript();
        executeTask();
        Map<String, String> properties = new HashMap<>();
        properties.put("initInfoProvides", getBaseName());
        properties.put("initInfoShortDescription", this.project.getDescription());
        properties.put("initInfoDescription", this.project.getDescription());
        assertThat(Files.readAllBytes(getArchivePath().toPath())).startsWith(new DefaultLaunchScript(null, properties).toByteArray());
        try {
            Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(getArchivePath().toPath());
            AbstractBootArchiveTests.assertThat(permissions).contains(PosixFilePermission.OWNER_EXECUTE);
        } catch (UnsupportedOperationException ex) {
            // Windows, presumably. Continue
        }
    }

    @Test
    public void customLaunchScriptCanBePrepended() throws IOException {
        setMainClassName("com.example.Main");
        File customScript = this.temp.newFile("custom.script");
        Files.write(customScript.toPath(), Arrays.asList("custom script"), StandardOpenOption.CREATE);
        launchScript(( configuration) -> configuration.setScript(customScript));
        executeTask();
        assertThat(Files.readAllBytes(getArchivePath().toPath())).startsWith("custom script".getBytes());
    }

    @Test
    public void launchScriptInitInfoPropertiesCanBeCustomized() throws IOException {
        setMainClassName("com.example.Main");
        launchScript(( configuration) -> {
            configuration.getProperties().put("initInfoProvides", "provides");
            configuration.getProperties().put("initInfoShortDescription", "short description");
            configuration.getProperties().put("initInfoDescription", "description");
        });
        executeTask();
        byte[] bytes = Files.readAllBytes(getArchivePath().toPath());
        AbstractBootArchiveTests.assertThat(bytes).containsSequence("Provides:          provides".getBytes());
        AbstractBootArchiveTests.assertThat(bytes).containsSequence("Short-Description: short description".getBytes());
        AbstractBootArchiveTests.assertThat(bytes).containsSequence("Description:       description".getBytes());
    }

    @Test
    public void customMainClassInTheManifestIsHonored() throws IOException {
        setMainClassName("com.example.Main");
        getManifest().getAttributes().put("Main-Class", "com.example.CustomLauncher");
        executeTask();
        assertThat(getArchivePath()).exists();
        try (JarFile jarFile = new JarFile(getArchivePath())) {
            AbstractBootArchiveTests.assertThat(jarFile.getManifest().getMainAttributes().getValue("Main-Class")).isEqualTo("com.example.CustomLauncher");
            AbstractBootArchiveTests.assertThat(jarFile.getManifest().getMainAttributes().getValue("Start-Class")).isEqualTo("com.example.Main");
            AbstractBootArchiveTests.assertThat(jarFile.getEntry("org/springframework/boot/loader/LaunchedURLClassLoader.class")).isNull();
        }
    }

    @Test
    public void customStartClassInTheManifestIsHonored() throws IOException {
        setMainClassName("com.example.Main");
        getManifest().getAttributes().put("Start-Class", "com.example.CustomMain");
        executeTask();
        assertThat(getArchivePath()).exists();
        try (JarFile jarFile = new JarFile(getArchivePath())) {
            AbstractBootArchiveTests.assertThat(jarFile.getManifest().getMainAttributes().getValue("Main-Class")).isEqualTo(this.launcherClass);
            AbstractBootArchiveTests.assertThat(jarFile.getManifest().getMainAttributes().getValue("Start-Class")).isEqualTo("com.example.CustomMain");
        }
    }

    @Test
    public void fileTimestampPreservationCanBeDisabled() throws IOException {
        setMainClassName("com.example.Main");
        setPreserveFileTimestamps(false);
        executeTask();
        assertThat(getArchivePath()).exists();
        try (JarFile jarFile = new JarFile(getArchivePath())) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                AbstractBootArchiveTests.assertThat(entry.getTime()).isEqualTo(CONSTANT_TIME_FOR_ZIP_ENTRIES);
            } 
        }
    }

    @Test
    public void reproducibleOrderingCanBeEnabled() throws IOException {
        setMainClassName("com.example.Main");
        from(this.temp.newFile("bravo.txt"), this.temp.newFile("alpha.txt"), this.temp.newFile("charlie.txt"));
        setReproducibleFileOrder(true);
        executeTask();
        assertThat(getArchivePath()).exists();
        List<String> textFiles = new ArrayList<>();
        try (JarFile jarFile = new JarFile(getArchivePath())) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".txt")) {
                    textFiles.add(entry.getName());
                }
            } 
        }
        AbstractBootArchiveTests.assertThat(textFiles).containsExactly("alpha.txt", "bravo.txt", "charlie.txt");
    }

    @Test
    public void devtoolsJarIsExcludedByDefault() throws IOException {
        setMainClassName("com.example.Main");
        this.task.classpath(this.temp.newFile("spring-boot-devtools-0.1.2.jar"));
        executeTask();
        assertThat(getArchivePath()).exists();
        try (JarFile jarFile = new JarFile(getArchivePath())) {
            AbstractBootArchiveTests.assertThat(jarFile.getEntry(((this.libPath) + "/spring-boot-devtools-0.1.2.jar"))).isNull();
        }
    }

    @Test
    public void devtoolsJarCanBeIncluded() throws IOException {
        setMainClassName("com.example.Main");
        this.task.classpath(jarFile("spring-boot-devtools-0.1.2.jar"));
        setExcludeDevtools(false);
        executeTask();
        assertThat(getArchivePath()).exists();
        try (JarFile jarFile = new JarFile(getArchivePath())) {
            AbstractBootArchiveTests.assertThat(jarFile.getEntry(((this.libPath) + "/spring-boot-devtools-0.1.2.jar"))).isNotNull();
        }
    }

    @Test
    public void allEntriesUseUnixPlatformAndUtf8NameEncoding() throws IOException {
        setMainClassName("com.example.Main");
        setMetadataCharset("UTF-8");
        File classpathFolder = this.temp.newFolder();
        File resource = new File(classpathFolder, "some-resource.xml");
        resource.getParentFile().mkdirs();
        resource.createNewFile();
        this.task.classpath(classpathFolder);
        executeTask();
        File archivePath = this.task.getArchivePath();
        try (ZipFile zip = new ZipFile(archivePath)) {
            Enumeration<ZipArchiveEntry> entries = zip.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
                AbstractBootArchiveTests.assertThat(entry.getPlatform()).isEqualTo(ZipArchiveEntry.PLATFORM_UNIX);
                AbstractBootArchiveTests.assertThat(entry.getGeneralPurposeBit().usesUTF8ForNames()).isTrue();
            } 
        }
    }

    @Test
    public void loaderIsWrittenFirstThenApplicationClassesThenLibraries() throws IOException {
        setMainClassName("com.example.Main");
        File classpathFolder = this.temp.newFolder();
        File applicationClass = new File(classpathFolder, "com/example/Application.class");
        applicationClass.getParentFile().mkdirs();
        applicationClass.createNewFile();
        classpath(classpathFolder, jarFile("first-library.jar"), jarFile("second-library.jar"), jarFile("third-library.jar"));
        requiresUnpack("second-library.jar");
        executeTask();
        assertThat(getEntryNames(getArchivePath())).containsSubsequence("org/springframework/boot/loader/", ((this.classesPath) + "/com/example/Application.class"), ((this.libPath) + "/first-library.jar"), ((this.libPath) + "/second-library.jar"), ((this.libPath) + "/third-library.jar"));
    }
}

