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
package org.springframework.boot.devtools.restart.server;


import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.boot.devtools.restart.classloader.ClassLoaderFile;
import org.springframework.boot.devtools.restart.classloader.ClassLoaderFile.Kind;
import org.springframework.boot.devtools.restart.classloader.ClassLoaderFiles;
import org.springframework.util.FileCopyUtils;


/**
 * Tests for {@link RestartServer}.
 *
 * @author Phillip Webb
 */
public class RestartServerTests {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void sourceFolderUrlFilterMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new RestartServer(((SourceFolderUrlFilter) (null)))).withMessageContaining("SourceFolderUrlFilter must not be null");
    }

    @Test
    public void updateAndRestart() throws Exception {
        URL url1 = new URL("file:/proj/module-a.jar!/");
        URL url2 = new URL("file:/proj/module-b.jar!/");
        URL url3 = new URL("file:/proj/module-c.jar!/");
        URL url4 = new URL("file:/proj/module-d.jar!/");
        URLClassLoader classLoaderA = new URLClassLoader(new URL[]{ url1, url2 });
        URLClassLoader classLoaderB = new URLClassLoader(new URL[]{ url3, url4 }, classLoaderA);
        SourceFolderUrlFilter filter = new DefaultSourceFolderUrlFilter();
        RestartServerTests.MockRestartServer server = new RestartServerTests.MockRestartServer(filter, classLoaderB);
        ClassLoaderFiles files = new ClassLoaderFiles();
        ClassLoaderFile fileA = new ClassLoaderFile(Kind.ADDED, new byte[0]);
        ClassLoaderFile fileB = new ClassLoaderFile(Kind.ADDED, new byte[0]);
        files.addFile("my/module-a", "ClassA.class", fileA);
        files.addFile("my/module-c", "ClassB.class", fileB);
        server.updateAndRestart(files);
        Set<URL> expectedUrls = new LinkedHashSet<>(Arrays.asList(url1, url3));
        assertThat(server.restartUrls).isEqualTo(expectedUrls);
        assertThat(server.restartFiles).isEqualTo(files);
    }

    @Test
    public void updateSetsJarLastModified() throws Exception {
        long startTime = System.currentTimeMillis();
        File folder = this.temp.newFolder();
        File jarFile = new File(folder, "module-a.jar");
        new FileOutputStream(jarFile).close();
        jarFile.setLastModified(0);
        URL url = jarFile.toURI().toURL();
        URLClassLoader classLoader = new URLClassLoader(new URL[]{ url });
        SourceFolderUrlFilter filter = new DefaultSourceFolderUrlFilter();
        RestartServerTests.MockRestartServer server = new RestartServerTests.MockRestartServer(filter, classLoader);
        ClassLoaderFiles files = new ClassLoaderFiles();
        ClassLoaderFile fileA = new ClassLoaderFile(Kind.ADDED, new byte[0]);
        files.addFile("my/module-a", "ClassA.class", fileA);
        server.updateAndRestart(files);
        assertThat(jarFile.lastModified()).isGreaterThan((startTime - 1000));
    }

    @Test
    public void updateReplacesLocalFilesWhenPossible() throws Exception {
        // This is critical for Cloud Foundry support where the application is
        // run exploded and resources can be found from the servlet root (outside of the
        // classloader)
        File folder = this.temp.newFolder();
        File classFile = new File(folder, "ClassA.class");
        FileCopyUtils.copy("abc".getBytes(), classFile);
        URL url = folder.toURI().toURL();
        URLClassLoader classLoader = new URLClassLoader(new URL[]{ url });
        SourceFolderUrlFilter filter = new DefaultSourceFolderUrlFilter();
        RestartServerTests.MockRestartServer server = new RestartServerTests.MockRestartServer(filter, classLoader);
        ClassLoaderFiles files = new ClassLoaderFiles();
        ClassLoaderFile fileA = new ClassLoaderFile(Kind.ADDED, "def".getBytes());
        files.addFile("my/module-a", "ClassA.class", fileA);
        server.updateAndRestart(files);
        assertThat(FileCopyUtils.copyToByteArray(classFile)).isEqualTo("def".getBytes());
    }

    private static class MockRestartServer extends RestartServer {
        MockRestartServer(SourceFolderUrlFilter sourceFolderUrlFilter, ClassLoader classLoader) {
            super(sourceFolderUrlFilter, classLoader);
        }

        private Set<URL> restartUrls;

        private ClassLoaderFiles restartFiles;

        @Override
        protected void restart(Set<URL> urls, ClassLoaderFiles files) {
            this.restartUrls = urls;
            this.restartFiles = files;
        }
    }
}

