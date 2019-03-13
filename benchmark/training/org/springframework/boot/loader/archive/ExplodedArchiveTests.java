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
package org.springframework.boot.loader.archive;


import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.boot.loader.archive.Archive.Entry;


/**
 * Tests for {@link ExplodedArchive}.
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @author Andy Wilkinson
 */
public class ExplodedArchiveTests {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File rootFolder;

    private ExplodedArchive archive;

    @Test
    public void getManifest() throws Exception {
        assertThat(this.archive.getManifest().getMainAttributes().getValue("Built-By")).isEqualTo("j1");
    }

    @Test
    public void getEntries() {
        Map<String, Archive.Entry> entries = getEntriesMap(this.archive);
        assertThat(entries.size()).isEqualTo(12);
    }

    @Test
    public void getUrl() throws Exception {
        assertThat(this.archive.getUrl()).isEqualTo(this.rootFolder.toURI().toURL());
    }

    @Test
    public void getUrlWithSpaceInPath() throws Exception {
        createArchive("spaces in the name");
        assertThat(this.archive.getUrl()).isEqualTo(this.rootFolder.toURI().toURL());
    }

    @Test
    public void getNestedArchive() throws Exception {
        Entry entry = getEntriesMap(this.archive).get("nested.jar");
        Archive nested = this.archive.getNestedArchive(entry);
        assertThat(nested.getUrl().toString()).isEqualTo((("jar:" + (this.rootFolder.toURI())) + "nested.jar!/"));
    }

    @Test
    public void nestedDirArchive() throws Exception {
        Entry entry = getEntriesMap(this.archive).get("d/");
        Archive nested = this.archive.getNestedArchive(entry);
        Map<String, Entry> nestedEntries = getEntriesMap(nested);
        assertThat(nestedEntries.size()).isEqualTo(1);
        assertThat(nested.getUrl().toString()).isEqualTo((("file:" + (this.rootFolder.toURI().getPath())) + "d/"));
    }

    @Test
    public void getNonRecursiveEntriesForRoot() {
        ExplodedArchive archive = new ExplodedArchive(new File("/"), false);
        Map<String, Archive.Entry> entries = getEntriesMap(archive);
        assertThat(entries.size()).isGreaterThan(1);
    }

    @Test
    public void getNonRecursiveManifest() throws Exception {
        ExplodedArchive archive = new ExplodedArchive(new File("src/test/resources/root"));
        assertThat(archive.getManifest()).isNotNull();
        Map<String, Archive.Entry> entries = getEntriesMap(archive);
        assertThat(entries.size()).isEqualTo(4);
    }

    @Test
    public void getNonRecursiveManifestEvenIfNonRecursive() throws Exception {
        ExplodedArchive archive = new ExplodedArchive(new File("src/test/resources/root"), false);
        assertThat(archive.getManifest()).isNotNull();
        Map<String, Archive.Entry> entries = getEntriesMap(archive);
        assertThat(entries.size()).isEqualTo(3);
    }

    @Test
    public void getResourceAsStream() throws Exception {
        ExplodedArchive archive = new ExplodedArchive(new File("src/test/resources/root"));
        assertThat(archive.getManifest()).isNotNull();
        URLClassLoader loader = new URLClassLoader(new URL[]{ archive.getUrl() });
        assertThat(loader.getResourceAsStream("META-INF/spring/application.xml")).isNotNull();
        loader.close();
    }

    @Test
    public void getResourceAsStreamNonRecursive() throws Exception {
        ExplodedArchive archive = new ExplodedArchive(new File("src/test/resources/root"), false);
        assertThat(archive.getManifest()).isNotNull();
        URLClassLoader loader = new URLClassLoader(new URL[]{ archive.getUrl() });
        assertThat(loader.getResourceAsStream("META-INF/spring/application.xml")).isNotNull();
        loader.close();
    }
}

