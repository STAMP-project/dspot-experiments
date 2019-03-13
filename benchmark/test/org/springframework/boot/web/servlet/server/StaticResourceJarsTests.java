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
package org.springframework.boot.web.servlet.server;


import java.io.File;
import java.net.URL;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for {@link StaticResourceJars}.
 *
 * @author Rupert Madden-Abbott
 * @author Andy Wilkinson
 */
public class StaticResourceJarsTests {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void includeJarWithStaticResources() throws Exception {
        File jarFile = createResourcesJar("test-resources.jar");
        List<URL> staticResourceJarUrls = new StaticResourceJars().getUrlsFrom(jarFile.toURI().toURL());
        assertThat(staticResourceJarUrls).hasSize(1);
    }

    @Test
    public void includeJarWithStaticResourcesWithUrlEncodedSpaces() throws Exception {
        File jarFile = createResourcesJar("test resources.jar");
        List<URL> staticResourceJarUrls = new StaticResourceJars().getUrlsFrom(jarFile.toURI().toURL());
        assertThat(staticResourceJarUrls).hasSize(1);
    }

    @Test
    public void includeJarWithStaticResourcesWithPlusInItsPath() throws Exception {
        File jarFile = createResourcesJar("test + resources.jar");
        List<URL> staticResourceJarUrls = new StaticResourceJars().getUrlsFrom(jarFile.toURI().toURL());
        assertThat(staticResourceJarUrls).hasSize(1);
    }

    @Test
    public void excludeJarWithoutStaticResources() throws Exception {
        File jarFile = createJar("dependency.jar");
        List<URL> staticResourceJarUrls = new StaticResourceJars().getUrlsFrom(jarFile.toURI().toURL());
        assertThat(staticResourceJarUrls).hasSize(0);
    }

    @Test
    public void uncPathsAreTolerated() throws Exception {
        File jarFile = createResourcesJar("test-resources.jar");
        List<URL> staticResourceJarUrls = new StaticResourceJars().getUrlsFrom(jarFile.toURI().toURL(), new URL("file://unc.example.com/test.jar"));
        assertThat(staticResourceJarUrls).hasSize(1);
    }
}

