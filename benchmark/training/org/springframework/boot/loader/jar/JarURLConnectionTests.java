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
package org.springframework.boot.loader.jar;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for {@link JarURLConnection}.
 *
 * @author Andy Wilkinson
 * @author Phillip Webb
 * @author Rostyslav Dudka
 */
public class JarURLConnectionTests {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder(new File("target"));

    private File rootJarFile;

    private JarFile jarFile;

    @Test
    public void connectionToRootUsingAbsoluteUrl() throws Exception {
        URL url = new URL((("jar:file:" + (getAbsolutePath())) + "!/"));
        assertThat(JarURLConnection.get(url, this.jarFile).getContent()).isSameAs(this.jarFile);
    }

    @Test
    public void connectionToRootUsingRelativeUrl() throws Exception {
        URL url = new URL((("jar:file:" + (getRelativePath())) + "!/"));
        assertThat(JarURLConnection.get(url, this.jarFile).getContent()).isSameAs(this.jarFile);
    }

    @Test
    public void connectionToEntryUsingAbsoluteUrl() throws Exception {
        URL url = new URL((("jar:file:" + (getAbsolutePath())) + "!/1.dat"));
        assertThat(JarURLConnection.get(url, this.jarFile).getInputStream()).hasSameContentAs(new ByteArrayInputStream(new byte[]{ 1 }));
    }

    @Test
    public void connectionToEntryUsingRelativeUrl() throws Exception {
        URL url = new URL((("jar:file:" + (getRelativePath())) + "!/1.dat"));
        assertThat(JarURLConnection.get(url, this.jarFile).getInputStream()).hasSameContentAs(new ByteArrayInputStream(new byte[]{ 1 }));
    }

    @Test
    public void connectionToEntryUsingAbsoluteUrlWithFileColonSlashSlashPrefix() throws Exception {
        URL url = new URL((("jar:file:/" + (getAbsolutePath())) + "!/1.dat"));
        assertThat(JarURLConnection.get(url, this.jarFile).getInputStream()).hasSameContentAs(new ByteArrayInputStream(new byte[]{ 1 }));
    }

    @Test
    public void connectionToEntryUsingAbsoluteUrlForNestedEntry() throws Exception {
        URL url = new URL((("jar:file:" + (getAbsolutePath())) + "!/nested.jar!/3.dat"));
        assertThat(JarURLConnection.get(url, this.jarFile).getInputStream()).hasSameContentAs(new ByteArrayInputStream(new byte[]{ 3 }));
    }

    @Test
    public void connectionToEntryUsingRelativeUrlForNestedEntry() throws Exception {
        URL url = new URL((("jar:file:" + (getRelativePath())) + "!/nested.jar!/3.dat"));
        assertThat(JarURLConnection.get(url, this.jarFile).getInputStream()).hasSameContentAs(new ByteArrayInputStream(new byte[]{ 3 }));
    }

    @Test
    public void connectionToEntryUsingAbsoluteUrlForEntryFromNestedJarFile() throws Exception {
        URL url = new URL((("jar:file:" + (getAbsolutePath())) + "!/nested.jar!/3.dat"));
        JarFile nested = this.jarFile.getNestedJarFile(this.jarFile.getEntry("nested.jar"));
        assertThat(JarURLConnection.get(url, nested).getInputStream()).hasSameContentAs(new ByteArrayInputStream(new byte[]{ 3 }));
    }

    @Test
    public void connectionToEntryUsingRelativeUrlForEntryFromNestedJarFile() throws Exception {
        URL url = new URL((("jar:file:" + (getRelativePath())) + "!/nested.jar!/3.dat"));
        JarFile nested = this.jarFile.getNestedJarFile(this.jarFile.getEntry("nested.jar"));
        assertThat(JarURLConnection.get(url, nested).getInputStream()).hasSameContentAs(new ByteArrayInputStream(new byte[]{ 3 }));
    }

    @Test
    public void connectionToEntryInNestedJarFromUrlThatUsesExistingUrlAsContext() throws Exception {
        URL url = new URL(new URL("jar", null, (-1), (("file:" + (getAbsolutePath())) + "!/nested.jar!/"), new Handler()), "/3.dat");
        JarFile nested = this.jarFile.getNestedJarFile(this.jarFile.getEntry("nested.jar"));
        assertThat(JarURLConnection.get(url, nested).getInputStream()).hasSameContentAs(new ByteArrayInputStream(new byte[]{ 3 }));
    }

    @Test
    public void connectionToEntryWithSpaceNestedEntry() throws Exception {
        URL url = new URL((("jar:file:" + (getRelativePath())) + "!/space nested.jar!/3.dat"));
        assertThat(JarURLConnection.get(url, this.jarFile).getInputStream()).hasSameContentAs(new ByteArrayInputStream(new byte[]{ 3 }));
    }

    @Test
    public void connectionToEntryWithEncodedSpaceNestedEntry() throws Exception {
        URL url = new URL((("jar:file:" + (getRelativePath())) + "!/space%20nested.jar!/3.dat"));
        assertThat(JarURLConnection.get(url, this.jarFile).getInputStream()).hasSameContentAs(new ByteArrayInputStream(new byte[]{ 3 }));
    }

    @Test
    public void connectionToEntryUsingWrongAbsoluteUrlForEntryFromNestedJarFile() throws Exception {
        URL url = new URL((("jar:file:" + (getAbsolutePath())) + "!/w.jar!/3.dat"));
        JarFile nested = this.jarFile.getNestedJarFile(this.jarFile.getEntry("nested.jar"));
        assertThatExceptionOfType(FileNotFoundException.class).isThrownBy(JarURLConnection.get(url, nested)::getInputStream);
    }

    @Test
    public void getContentLengthReturnsLengthOfUnderlyingEntry() throws Exception {
        URL url = new URL(new URL("jar", null, (-1), (("file:" + (getAbsolutePath())) + "!/nested.jar!/"), new Handler()), "/3.dat");
        assertThat(url.openConnection().getContentLength()).isEqualTo(1);
    }

    @Test
    public void getContentLengthLongReturnsLengthOfUnderlyingEntry() throws Exception {
        URL url = new URL(new URL("jar", null, (-1), (("file:" + (getAbsolutePath())) + "!/nested.jar!/"), new Handler()), "/3.dat");
        assertThat(url.openConnection().getContentLengthLong()).isEqualTo(1);
    }

    @Test
    public void getLastModifiedReturnsLastModifiedTimeOfJarEntry() throws Exception {
        URL url = new URL((("jar:file:" + (getAbsolutePath())) + "!/1.dat"));
        JarURLConnection connection = JarURLConnection.get(url, this.jarFile);
        assertThat(connection.getLastModified()).isEqualTo(connection.getJarEntry().getTime());
    }

    @Test
    public void jarEntryBasicName() {
        assertThat(new org.springframework.boot.loader.jar.JarURLConnection.JarEntryName(new StringSequence("a/b/C.class")).toString()).isEqualTo("a/b/C.class");
    }

    @Test
    public void jarEntryNameWithSingleByteEncodedCharacters() {
        assertThat(new org.springframework.boot.loader.jar.JarURLConnection.JarEntryName(new StringSequence("%61/%62/%43.class")).toString()).isEqualTo("a/b/C.class");
    }

    @Test
    public void jarEntryNameWithDoubleByteEncodedCharacters() {
        assertThat(new org.springframework.boot.loader.jar.JarURLConnection.JarEntryName(new StringSequence("%c3%a1/b/C.class")).toString()).isEqualTo("\u00e1/b/C.class");
    }

    @Test
    public void jarEntryNameWithMixtureOfEncodedAndUnencodedDoubleByteCharacters() {
        assertThat(new org.springframework.boot.loader.jar.JarURLConnection.JarEntryName(new StringSequence("%c3%a1/b/\u00c7.class")).toString()).isEqualTo("\u00e1/b/\u00c7.class");
    }
}

