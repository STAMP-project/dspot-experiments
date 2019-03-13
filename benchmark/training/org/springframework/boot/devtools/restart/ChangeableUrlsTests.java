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
package org.springframework.boot.devtools.restart;


import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for {@link ChangeableUrls}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class ChangeableUrlsTests {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void folderUrl() throws Exception {
        URL url = makeUrl("myproject");
        assertThat(ChangeableUrls.fromUrls(url).size()).isEqualTo(1);
    }

    @Test
    public void fileUrl() throws Exception {
        URL url = this.temporaryFolder.newFile().toURI().toURL();
        assertThat(ChangeableUrls.fromUrls(url)).isEmpty();
    }

    @Test
    public void httpUrl() throws Exception {
        URL url = new URL("http://spring.io");
        assertThat(ChangeableUrls.fromUrls(url)).isEmpty();
    }

    @Test
    public void httpsUrl() throws Exception {
        URL url = new URL("https://spring.io");
        assertThat(ChangeableUrls.fromUrls(url)).isEmpty();
    }

    @Test
    public void skipsUrls() throws Exception {
        ChangeableUrls urls = ChangeableUrls.fromUrls(makeUrl("spring-boot"), makeUrl("spring-boot-autoconfigure"), makeUrl("spring-boot-actuator"), makeUrl("spring-boot-starter"), makeUrl("spring-boot-starter-some-thing"));
        assertThat(urls).isEmpty();
    }

    @Test
    public void urlsFromJarClassPathAreConsidered() throws Exception {
        File relative = this.temporaryFolder.newFolder();
        URL absoluteUrl = this.temporaryFolder.newFolder().toURI().toURL();
        File jarWithClassPath = makeJarFileWithUrlsInManifestClassPath("project-core/target/classes/", "project-web/target/classes/", "does-not-exist/target/classes", ((relative.getName()) + "/"), absoluteUrl);
        new File(jarWithClassPath.getParentFile(), "project-core/target/classes").mkdirs();
        new File(jarWithClassPath.getParentFile(), "project-web/target/classes").mkdirs();
        ChangeableUrls urls = ChangeableUrls.fromClassLoader(new URLClassLoader(new URL[]{ jarWithClassPath.toURI().toURL(), makeJarFileWithNoManifest() }));
        assertThat(urls.toList()).containsExactly(new URL(jarWithClassPath.toURI().toURL(), "project-core/target/classes/"), new URL(jarWithClassPath.toURI().toURL(), "project-web/target/classes/"), relative.toURI().toURL(), absoluteUrl);
    }
}

