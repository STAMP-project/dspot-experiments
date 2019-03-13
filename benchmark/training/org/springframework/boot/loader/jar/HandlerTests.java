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
package org.springframework.boot.loader.jar;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.boot.loader.TestJarCreator;


/**
 * Tests for {@link Handler}.
 *
 * @author Andy Wilkinson
 */
public class HandlerTests {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private final Handler handler = new Handler();

    @Test
    public void parseUrlWithJarRootContextAndAbsoluteSpecThatUsesContext() throws MalformedURLException {
        String spec = "/entry.txt";
        URL context = createUrl("file:example.jar!/");
        this.handler.parseURL(context, spec, 0, spec.length());
        assertThat(context.toExternalForm()).isEqualTo("jar:file:example.jar!/entry.txt");
    }

    @Test
    public void parseUrlWithDirectoryEntryContextAndAbsoluteSpecThatUsesContext() throws MalformedURLException {
        String spec = "/entry.txt";
        URL context = createUrl("file:example.jar!/dir/");
        this.handler.parseURL(context, spec, 0, spec.length());
        assertThat(context.toExternalForm()).isEqualTo("jar:file:example.jar!/entry.txt");
    }

    @Test
    public void parseUrlWithJarRootContextAndRelativeSpecThatUsesContext() throws MalformedURLException {
        String spec = "entry.txt";
        URL context = createUrl("file:example.jar!/");
        this.handler.parseURL(context, spec, 0, spec.length());
        assertThat(context.toExternalForm()).isEqualTo("jar:file:example.jar!/entry.txt");
    }

    @Test
    public void parseUrlWithDirectoryEntryContextAndRelativeSpecThatUsesContext() throws MalformedURLException {
        String spec = "entry.txt";
        URL context = createUrl("file:example.jar!/dir/");
        this.handler.parseURL(context, spec, 0, spec.length());
        assertThat(context.toExternalForm()).isEqualTo("jar:file:example.jar!/dir/entry.txt");
    }

    @Test
    public void parseUrlWithFileEntryContextAndRelativeSpecThatUsesContext() throws MalformedURLException {
        String spec = "entry.txt";
        URL context = createUrl("file:example.jar!/dir/file");
        this.handler.parseURL(context, spec, 0, spec.length());
        assertThat(context.toExternalForm()).isEqualTo("jar:file:example.jar!/dir/entry.txt");
    }

    @Test
    public void parseUrlWithSpecThatIgnoresContext() throws MalformedURLException {
        JarFile.registerUrlProtocolHandler();
        String spec = "jar:file:/other.jar!/nested!/entry.txt";
        URL context = createUrl("file:example.jar!/dir/file");
        this.handler.parseURL(context, spec, 0, spec.length());
        assertThat(context.toExternalForm()).isEqualTo("jar:jar:file:/other.jar!/nested!/entry.txt");
    }

    @Test
    public void sameFileReturnsFalseForUrlsWithDifferentProtocols() throws MalformedURLException {
        assertThat(this.handler.sameFile(new URL("jar:file:foo.jar!/content.txt"), new URL("file:/foo.jar"))).isFalse();
    }

    @Test
    public void sameFileReturnsFalseForDifferentFileInSameJar() throws MalformedURLException {
        assertThat(this.handler.sameFile(new URL("jar:file:foo.jar!/the/path/to/the/first/content.txt"), new URL("jar:file:/foo.jar!/content.txt"))).isFalse();
    }

    @Test
    public void sameFileReturnsFalseForSameFileInDifferentJars() throws MalformedURLException {
        assertThat(this.handler.sameFile(new URL("jar:file:/the/path/to/the/first.jar!/content.txt"), new URL("jar:file:/second.jar!/content.txt"))).isFalse();
    }

    @Test
    public void sameFileReturnsTrueForSameFileInSameJar() throws MalformedURLException {
        assertThat(this.handler.sameFile(new URL("jar:file:/the/path/to/the/first.jar!/content.txt"), new URL("jar:file:/the/path/to/the/first.jar!/content.txt"))).isTrue();
    }

    @Test
    public void sameFileReturnsTrueForUrlsThatReferenceSameFileViaNestedArchiveAndFromRootOfJar() throws MalformedURLException {
        assertThat(this.handler.sameFile(new URL("jar:file:/test.jar!/BOOT-INF/classes!/foo.txt"), new URL("jar:file:/test.jar!/BOOT-INF/classes/foo.txt"))).isTrue();
    }

    @Test
    public void hashCodesAreEqualForUrlsThatReferenceSameFileViaNestedArchiveAndFromRootOfJar() throws MalformedURLException {
        assertThat(this.handler.hashCode(new URL("jar:file:/test.jar!/BOOT-INF/classes!/foo.txt"))).isEqualTo(this.handler.hashCode(new URL("jar:file:/test.jar!/BOOT-INF/classes/foo.txt")));
    }

    @Test
    public void urlWithSpecReferencingParentDirectory() throws MalformedURLException {
        assertStandardAndCustomHandlerUrlsAreEqual("file:/test.jar!/BOOT-INF/classes!/xsd/folderA/a.xsd", "../folderB/b.xsd");
    }

    @Test
    public void urlWithSpecReferencingAncestorDirectoryOutsideJarStopsAtJarRoot() throws MalformedURLException {
        assertStandardAndCustomHandlerUrlsAreEqual("file:/test.jar!/BOOT-INF/classes!/xsd/folderA/a.xsd", "../../../../../../folderB/b.xsd");
    }

    @Test
    public void urlWithSpecReferencingCurrentDirectory() throws MalformedURLException {
        assertStandardAndCustomHandlerUrlsAreEqual("file:/test.jar!/BOOT-INF/classes!/xsd/folderA/a.xsd", "./folderB/./b.xsd");
    }

    @Test
    public void urlWithRef() throws MalformedURLException {
        assertStandardAndCustomHandlerUrlsAreEqual("file:/test.jar!/BOOT-INF/classes", "!/foo.txt#alpha");
    }

    @Test
    public void urlWithQuery() throws MalformedURLException {
        assertStandardAndCustomHandlerUrlsAreEqual("file:/test.jar!/BOOT-INF/classes", "!/foo.txt?alpha");
    }

    @Test
    public void fallbackToJdksJarUrlStreamHandler() throws Exception {
        File testJar = this.temporaryFolder.newFile("test.jar");
        TestJarCreator.createTestJar(testJar);
        URLConnection connection = new URL(null, (("jar:file:" + (testJar.getAbsolutePath())) + "!/nested.jar!/"), this.handler).openConnection();
        assertThat(connection).isInstanceOf(JarURLConnection.class);
        URLConnection jdkConnection = new URL(null, (("jar:file:file:" + (testJar.getAbsolutePath())) + "!/nested.jar!/"), this.handler).openConnection();
        assertThat(jdkConnection).isNotInstanceOf(JarURLConnection.class);
    }
}

