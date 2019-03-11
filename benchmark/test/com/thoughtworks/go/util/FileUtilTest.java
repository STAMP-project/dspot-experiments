/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.util;


import com.googlecode.junit.ext.JunitExtRunner;
import com.googlecode.junit.ext.RunIf;
import com.thoughtworks.go.junitext.EnhancedOSChecker;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(JunitExtRunner.class)
public class FileUtilTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldBeHiddenIfFileStartWithDot() {
        Assert.assertTrue(FileUtil.isHidden(new File(".svn")));
    }

    @Test
    public void shouldBeHiddenIfFileIsHidden() {
        File mockFile = Mockito.mock(File.class);
        Mockito.when(mockFile.isHidden()).thenReturn(true);
        Assert.assertTrue(FileUtil.isHidden(mockFile));
    }

    @Test
    public void shouldUseSpeficiedFolderIfAbsolute() throws Exception {
        final File absolutePath = new File("zx").getAbsoluteFile();
        Assert.assertThat(FileUtil.applyBaseDirIfRelative(new File("xyz"), absolutePath), Matchers.is(absolutePath));
    }

    @Test
    public void shouldUseSpeficiedFolderIfBaseDirIsEmpty() throws Exception {
        Assert.assertThat(FileUtil.applyBaseDirIfRelative(new File(""), new File("zx")), Matchers.is(new File("zx")));
    }

    @Test
    public void shouldAppendToDefaultIfRelative() throws Exception {
        final File relativepath = new File("zx");
        Assert.assertThat(FileUtil.applyBaseDirIfRelative(new File("xyz"), relativepath), Matchers.is(new File("xyz", relativepath.getPath())));
    }

    @Test
    public void shouldUseDefaultIfActualisNull() throws Exception {
        final File baseFile = new File("xyz");
        Assert.assertThat(FileUtil.applyBaseDirIfRelative(baseFile, null), Matchers.is(baseFile));
    }

    @Test
    public void shouldCreateUniqueHashForFolders() throws Exception {
        File file = new File("c:a/b/c/d/e");
        File file2 = new File("c:foo\\bar\\baz");
        Assert.assertThat(FileUtil.filesystemSafeFileHash(file).matches("[0-9a-zA-Z\\.\\-]*"), Matchers.is(true));
        Assert.assertThat(FileUtil.filesystemSafeFileHash(file2), Matchers.not(FileUtil.filesystemSafeFileHash(file)));
    }

    @Test
    public void shouldDetectSubfolders() throws Exception {
        Assert.assertThat(FileUtil.isSubdirectoryOf(new File("a"), new File("a")), Matchers.is(true));
        Assert.assertThat(FileUtil.isSubdirectoryOf(new File("a"), new File("a/b")), Matchers.is(true));
        Assert.assertThat(FileUtil.isSubdirectoryOf(new File("a"), new File("aaaa")), Matchers.is(false));
        Assert.assertThat(FileUtil.isSubdirectoryOf(new File("a/b/c/d"), new File("a/b/c/d/e")), Matchers.is(true));
        Assert.assertThat(FileUtil.isSubdirectoryOf(new File("a/b/c/d/e"), new File("a/b/c/d")), Matchers.is(false));
        Assert.assertThat(FileUtil.isSubdirectoryOf(new File("/a/b"), new File("c/d")), Matchers.is(false));
    }

    @Test
    public void shouldDetectSubfoldersWhenUsingRelativePaths() throws Exception {
        File parent = new File("/a/b");
        Assert.assertThat(FileUtil.isSubdirectoryOf(parent, new File(parent, "../../..")), Matchers.is(false));
    }

    @Test
    @RunIf(value = EnhancedOSChecker.class, arguments = { DO_NOT_RUN_ON, WINDOWS })
    public void shouldCreateFileURIForFile() {
        Assert.assertThat(FileUtil.toFileURI(new File("/var/lib/foo/")), Matchers.is("file:///var/lib/foo"));
        Assert.assertThat(FileUtil.toFileURI(new File("/var/a dir with spaces/foo")), Matchers.is("file:///var/a%20dir%20with%20spaces/foo"));
        Assert.assertThat(FileUtil.toFileURI(new File("/var/?????/foo")), Matchers.is("file:///var/%E5%8F%B8%E5%BE%92%E7%A9%BA%E5%9C%A8%E6%AD%A4/foo"));
    }

    @Test
    @RunIf(value = EnhancedOSChecker.class, arguments = { EnhancedOSChecker.WINDOWS })
    public void shouldCreateFileURIForFileOnWindows() {
        Assert.assertThat(FileUtil.toFileURI(new File("c:\\foo")).startsWith("file:///c:/foo"), Matchers.is(true));
        Assert.assertThat(FileUtil.toFileURI(new File("c:\\a dir with spaces\\foo")).startsWith("file:///c:/a%20dir%20with%20spaces/foo"), Matchers.is(true));
    }

    @Test
    @RunIf(value = EnhancedOSChecker.class, arguments = { EnhancedOSChecker.WINDOWS })
    public void shouldReturnFalseForInvalidWindowsUNCFilePath() {
        Assert.assertThat(FileUtil.isAbsolutePath("\\\\host\\"), Matchers.is(false));
        Assert.assertThat(FileUtil.isAbsolutePath("\\\\host"), Matchers.is(false));
    }

    @Test
    @RunIf(value = EnhancedOSChecker.class, arguments = { EnhancedOSChecker.WINDOWS })
    public void shouldReturnTrueForValidWindowsUNCFilePath() {
        Assert.assertThat(FileUtil.isAbsolutePath("\\\\host\\share"), Matchers.is(true));
        Assert.assertThat(FileUtil.isAbsolutePath("\\\\host\\share\\dir"), Matchers.is(true));
    }

    @Test
    public void FolderIsEmptyWhenItHasNoContents() throws Exception {
        File folder = temporaryFolder.newFolder();
        Assert.assertThat(FileUtil.isFolderEmpty(folder), Matchers.is(true));
    }

    @Test
    public void FolderIsNotEmptyWhenItHasContents() throws Exception {
        File folder = temporaryFolder.newFolder();
        new File(folder, "subfolder").createNewFile();
        Assert.assertThat(FileUtil.isFolderEmpty(folder), Matchers.is(false));
    }

    @Test
    public void shouldReturnCanonicalPath() throws IOException {
        File f = temporaryFolder.newFolder();
        Assert.assertThat(FileUtil.getCanonicalPath(f), Matchers.is(f.getCanonicalPath()));
        File spyFile = Mockito.spy(new File("/xyz/non-existent-file"));
        IOException canonicalPathException = new IOException("Failed to build the canonical path");
        Mockito.when(spyFile.getCanonicalPath()).thenThrow(canonicalPathException);
        try {
            FileUtil.getCanonicalPath(spyFile);
        } catch (RuntimeException e) {
            Assert.assertThat(e.getCause(), Matchers.is(canonicalPathException));
        }
    }

    @Test
    public void shouldRemoveLeadingFilePathFromAFilePath() throws Exception {
        File file = new File("/var/command-repo/default/windows/echo.xml");
        File base = new File("/var/command-repo/default");
        Assert.assertThat(FileUtil.removeLeadingPath(base.getAbsolutePath(), file.getAbsolutePath()), TestUtils.isSameAsPath("/windows/echo.xml"));
        Assert.assertThat(FileUtil.removeLeadingPath(new File("/var/command-repo/default/").getAbsolutePath(), new File("/var/command-repo/default/windows/echo.xml").getAbsolutePath()), TestUtils.isSameAsPath("/windows/echo.xml"));
        Assert.assertThat(FileUtil.removeLeadingPath("/some/random/path", "/var/command-repo/default/windows/echo.xml"), Matchers.is("/var/command-repo/default/windows/echo.xml"));
        Assert.assertThat(FileUtil.removeLeadingPath(new File("C:/blah").getAbsolutePath(), new File("C:/blah/abcd.txt").getAbsolutePath()), TestUtils.isSameAsPath("/abcd.txt"));
        Assert.assertThat(FileUtil.removeLeadingPath(new File("C:/blah/").getAbsolutePath(), new File("C:/blah/abcd.txt").getAbsolutePath()), TestUtils.isSameAsPath("/abcd.txt"));
        Assert.assertThat(FileUtil.removeLeadingPath(null, new File("/blah/abcd.txt").getAbsolutePath()), TestUtils.isSameAsPath(new File("/blah/abcd.txt").getAbsolutePath()));
        Assert.assertThat(FileUtil.removeLeadingPath("", new File("/blah/abcd.txt").getAbsolutePath()), TestUtils.isSameAsPath(new File("/blah/abcd.txt").getAbsolutePath()));
    }

    @Test
    public void shouldReturnTrueIfDirectoryIsReadable() throws IOException {
        File readableDirectory = Mockito.mock(File.class);
        Mockito.when(readableDirectory.canRead()).thenReturn(true);
        Mockito.when(readableDirectory.canExecute()).thenReturn(true);
        Mockito.when(readableDirectory.listFiles()).thenReturn(new File[]{  });
        Assert.assertThat(FileUtil.isDirectoryReadable(readableDirectory), Matchers.is(true));
        File unreadableDirectory = Mockito.mock(File.class);
        Mockito.when(readableDirectory.canRead()).thenReturn(false);
        Mockito.when(readableDirectory.canExecute()).thenReturn(false);
        Assert.assertThat(FileUtil.isDirectoryReadable(unreadableDirectory), Matchers.is(false));
        Mockito.verify(readableDirectory).canRead();
        Mockito.verify(readableDirectory).canExecute();
        Mockito.verify(readableDirectory).listFiles();
        Mockito.verify(unreadableDirectory).canRead();
        Mockito.verify(unreadableDirectory, Mockito.never()).canExecute();
    }

    @Test
    public void shouldCalculateSha1Digest() throws IOException {
        File tempFile = temporaryFolder.newFile();
        FileUtils.writeStringToFile(tempFile, "12345", StandardCharsets.UTF_8);
        Assert.assertThat(FileUtil.sha1Digest(tempFile), Matchers.is("jLIjfQZ5yojbZGTqxg2pY0VROWQ="));
    }
}

