package org.baeldung.java.io;


import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class JavaFilePathUnitTest {
    private static String userDir;

    @Test
    public void whenPathResolved_thenSuccess() {
        File file = new File("baeldung/foo/foo-one.txt");
        String expectedPath = (JavaFilePathUnitTest.isWindows()) ? "baeldung\\foo\\foo-one.txt" : "baeldung/foo/foo-one.txt";
        String actualPath = file.getPath();
        Assert.assertEquals(expectedPath, actualPath);
    }

    @Test
    public void whenAbsolutePathResolved_thenSuccess() {
        File file = new File("baeldung/foo/foo-one.txt");
        String expectedPath = (JavaFilePathUnitTest.isWindows()) ? (JavaFilePathUnitTest.userDir) + "\\baeldung\\foo\\foo-one.txt" : (JavaFilePathUnitTest.userDir) + "/baeldung/foo/foo-one.txt";
        String actualPath = file.getAbsolutePath();
        Assert.assertEquals(expectedPath, actualPath);
    }

    @Test
    public void whenAbsolutePathWithShorthandResolved_thenSuccess() {
        File file = new File("baeldung/bar/baz/../bar-one.txt");
        String expectedPath = (JavaFilePathUnitTest.isWindows()) ? (JavaFilePathUnitTest.userDir) + "\\baeldung\\bar\\baz\\..\\bar-one.txt" : (JavaFilePathUnitTest.userDir) + "/baeldung/bar/baz/../bar-one.txt";
        String actualPath = file.getAbsolutePath();
        Assert.assertEquals(expectedPath, actualPath);
    }

    @Test
    public void whenCanonicalPathWithShorthandResolved_thenSuccess() throws IOException {
        File file = new File("baeldung/bar/baz/../bar-one.txt");
        String expectedPath = (JavaFilePathUnitTest.isWindows()) ? (JavaFilePathUnitTest.userDir) + "\\baeldung\\bar\\bar-one.txt" : (JavaFilePathUnitTest.userDir) + "/baeldung/bar/bar-one.txt";
        String actualPath = file.getCanonicalPath();
        Assert.assertEquals(expectedPath, actualPath);
    }

    @Test
    public void whenCanonicalPathWithDotShorthandResolved_thenSuccess() throws IOException {
        File file = new File("baeldung/bar/baz/./baz-one.txt");
        String expectedPath = (JavaFilePathUnitTest.isWindows()) ? (JavaFilePathUnitTest.userDir) + "\\baeldung\\bar\\baz\\baz-one.txt" : (JavaFilePathUnitTest.userDir) + "/baeldung/bar/baz/baz-one.txt";
        String actualPath = file.getCanonicalPath();
        Assert.assertEquals(expectedPath, actualPath);
    }

    @Test(expected = IOException.class)
    public void givenWindowsOs_whenCanonicalPathWithWildcard_thenIOException() throws IOException {
        Assume.assumeTrue(JavaFilePathUnitTest.isWindows());
        new File("*").getCanonicalPath();
    }
}

