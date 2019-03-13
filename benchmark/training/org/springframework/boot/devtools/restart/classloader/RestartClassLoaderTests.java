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
package org.springframework.boot.devtools.restart.classloader;


import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.boot.devtools.restart.classloader.ClassLoaderFile.Kind;
import org.springframework.util.FileCopyUtils;


/**
 * Tests for {@link RestartClassLoader}.
 *
 * @author Phillip Webb
 */
@SuppressWarnings("resource")
public class RestartClassLoaderTests {
    private static final String PACKAGE = RestartClassLoaderTests.class.getPackage().getName();

    private static final String PACKAGE_PATH = RestartClassLoaderTests.PACKAGE.replace('.', '/');

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private File sampleJarFile;

    private URLClassLoader parentClassLoader;

    private ClassLoaderFiles updatedFiles;

    private RestartClassLoader reloadClassLoader;

    @Test
    public void parentMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new RestartClassLoader(null, new URL[]{  })).withMessageContaining("Parent must not be null");
    }

    @Test
    public void updatedFilesMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new RestartClassLoader(this.parentClassLoader, new URL[]{  }, null)).withMessageContaining("UpdatedFiles must not be null");
    }

    @Test
    public void getResourceFromReloadableUrl() throws Exception {
        String content = readString(this.reloadClassLoader.getResourceAsStream(((RestartClassLoaderTests.PACKAGE_PATH) + "/Sample.txt")));
        assertThat(content).startsWith("fromchild");
    }

    @Test
    public void getResourceFromParent() throws Exception {
        String content = readString(this.reloadClassLoader.getResourceAsStream(((RestartClassLoaderTests.PACKAGE_PATH) + "/Parent.txt")));
        assertThat(content).startsWith("fromparent");
    }

    @Test
    public void getResourcesFiltersDuplicates() throws Exception {
        List<URL> resources = toList(this.reloadClassLoader.getResources(((RestartClassLoaderTests.PACKAGE_PATH) + "/Sample.txt")));
        assertThat(resources.size()).isEqualTo(1);
    }

    @Test
    public void loadClassFromReloadableUrl() throws Exception {
        Class<?> loaded = this.reloadClassLoader.loadClass(((RestartClassLoaderTests.PACKAGE) + ".Sample"));
        assertThat(loaded.getClassLoader()).isEqualTo(this.reloadClassLoader);
    }

    @Test
    public void loadClassFromParent() throws Exception {
        Class<?> loaded = this.reloadClassLoader.loadClass(((RestartClassLoaderTests.PACKAGE) + ".SampleParent"));
        assertThat(loaded.getClassLoader()).isEqualTo(getClass().getClassLoader());
    }

    @Test
    public void getDeletedResource() {
        String name = (RestartClassLoaderTests.PACKAGE_PATH) + "/Sample.txt";
        this.updatedFiles.addFile(name, new ClassLoaderFile(Kind.DELETED, null));
        assertThat(this.reloadClassLoader.getResource(name)).isNull();
    }

    @Test
    public void getDeletedResourceAsStream() {
        String name = (RestartClassLoaderTests.PACKAGE_PATH) + "/Sample.txt";
        this.updatedFiles.addFile(name, new ClassLoaderFile(Kind.DELETED, null));
        assertThat(this.reloadClassLoader.getResourceAsStream(name)).isNull();
    }

    @Test
    public void getUpdatedResource() throws Exception {
        String name = (RestartClassLoaderTests.PACKAGE_PATH) + "/Sample.txt";
        byte[] bytes = "abc".getBytes();
        this.updatedFiles.addFile(name, new ClassLoaderFile(Kind.MODIFIED, bytes));
        URL resource = this.reloadClassLoader.getResource(name);
        assertThat(FileCopyUtils.copyToByteArray(resource.openStream())).isEqualTo(bytes);
    }

    @Test
    public void getResourcesWithDeleted() throws Exception {
        String name = (RestartClassLoaderTests.PACKAGE_PATH) + "/Sample.txt";
        this.updatedFiles.addFile(name, new ClassLoaderFile(Kind.DELETED, null));
        List<URL> resources = toList(this.reloadClassLoader.getResources(name));
        assertThat(resources).isEmpty();
    }

    @Test
    public void getResourcesWithUpdated() throws Exception {
        String name = (RestartClassLoaderTests.PACKAGE_PATH) + "/Sample.txt";
        byte[] bytes = "abc".getBytes();
        this.updatedFiles.addFile(name, new ClassLoaderFile(Kind.MODIFIED, bytes));
        List<URL> resources = toList(this.reloadClassLoader.getResources(name));
        assertThat(FileCopyUtils.copyToByteArray(resources.get(0).openStream())).isEqualTo(bytes);
    }

    @Test
    public void getDeletedClass() throws Exception {
        String name = (RestartClassLoaderTests.PACKAGE_PATH) + "/Sample.class";
        this.updatedFiles.addFile(name, new ClassLoaderFile(Kind.DELETED, null));
        assertThatExceptionOfType(ClassNotFoundException.class).isThrownBy(() -> this.reloadClassLoader.loadClass(((PACKAGE) + ".Sample")));
    }

    @Test
    public void getUpdatedClass() throws Exception {
        String name = (RestartClassLoaderTests.PACKAGE_PATH) + "/Sample.class";
        this.updatedFiles.addFile(name, new ClassLoaderFile(Kind.MODIFIED, new byte[10]));
        assertThatExceptionOfType(ClassFormatError.class).isThrownBy(() -> this.reloadClassLoader.loadClass(((PACKAGE) + ".Sample")));
    }

    @Test
    public void getAddedClass() throws Exception {
        String name = (RestartClassLoaderTests.PACKAGE_PATH) + "/SampleParent.class";
        byte[] bytes = FileCopyUtils.copyToByteArray(getClass().getResourceAsStream("SampleParent.class"));
        this.updatedFiles.addFile(name, new ClassLoaderFile(Kind.ADDED, bytes));
        Class<?> loaded = this.reloadClassLoader.loadClass(((RestartClassLoaderTests.PACKAGE) + ".SampleParent"));
        assertThat(loaded.getClassLoader()).isEqualTo(this.reloadClassLoader);
    }
}

