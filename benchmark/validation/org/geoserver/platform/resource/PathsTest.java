/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.platform.resource;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;


public class PathsTest {
    final String BASE = "";

    final String DIRECTORY = "directory";

    final String FILE = "directory/file.txt";

    final String SIDECAR = "directory/file.prj";

    final String FILE2 = "directory/file2.txt";

    final String SUBFOLDER = "directory/folder";

    final String FILE3 = "directory/folder/file3.txt";

    @Test
    public void pathTest() {
        Assert.assertEquals(2, names("a/b").size());
        Assert.assertEquals(1, names("a/").size());
        Assert.assertEquals(1, names("a").size());
        Assert.assertEquals(0, names("").size());
        Assert.assertEquals(BASE, path(""));
        Assert.assertEquals("directory/file.txt", path("directory", "file.txt"));
        Assert.assertEquals("directory/folder/file3.txt", path("directory/folder", "file3.txt"));
        // handling invalid values
        Assert.assertNull(path(((String) (null))));// edge case

        Assert.assertEquals("foo", path("foo/"));
        try {
            Assert.assertEquals("foo", path(".", "foo"));
            Assert.fail(". invalid relative path");
        } catch (IllegalArgumentException expected) {
        }
        try {
            Assert.assertEquals("foo", path("foo/bar", ".."));
            Assert.fail(".. invalid relative path");
        } catch (IllegalArgumentException expected) {
        }
        // test path elements that are always valid regardless of strictPath
        for (String name : new String[]{ "foo", "foo.txt", "directory/bar" }) {
            Assert.assertEquals(name, Paths.Paths.path(true, name));
            Assert.assertEquals(name, Paths.Paths.path(false, name));
        }
        // test path elements that are always invalid regardless of strictPath
        for (String name : new String[]{ ".", "..", "foo\\" }) {
            try {
                Assert.assertEquals(name, Paths.Paths.path(true, name));
                Assert.fail(("invalid: " + name));
            } catch (IllegalArgumentException expected) {
                // ignore
            }
            try {
                Assert.assertEquals(name, Paths.Paths.path(false, name));
                Assert.fail(("invalid: " + name));
            } catch (IllegalArgumentException expected) {
                // ignore
            }
        }
        // test path elements that are invalid if and only if strictPath is true
        for (char c : "*:,\'&?\"<>|".toCharArray()) {
            for (String prefix : new String[]{ "foo", "" }) {
                for (String suffix : new String[]{ "bar", "" }) {
                    String name = (prefix + c) + suffix;
                    try {
                        Assert.assertEquals(name, Paths.Paths.path(true, name));
                        Assert.fail(("invalid: " + name));
                    } catch (IllegalArgumentException expected) {
                        // ignore
                    }
                    Assert.assertEquals(name, Paths.Paths.path(false, name));
                }
            }
        }
    }

    @Test
    public void validTest() {
        // test path elements that are always valid regardless of strictPath
        for (String name : new String[]{ "foo", "foo.txt", "directory/bar" }) {
            Assert.assertEquals(name, Paths.Paths.valid(true, name));
            Assert.assertEquals(name, Paths.Paths.valid(false, name));
        }
        // test path elements that are always invalid regardless of strictPath
        for (String name : new String[]{ ".", "..", "foo\\" }) {
            try {
                Assert.assertEquals(name, Paths.Paths.valid(true, name));
                Assert.fail(("invalid: " + name));
            } catch (IllegalArgumentException expected) {
                // ignore
            }
            try {
                Assert.assertEquals(name, Paths.Paths.valid(false, name));
                Assert.fail(("invalid: " + name));
            } catch (IllegalArgumentException expected) {
                // ignore
            }
        }
        // test path elements that are invalid if and only if strictPath is true
        for (char c : "*:,\'&?\"<>|".toCharArray()) {
            for (String prefix : new String[]{ "foo", "" }) {
                for (String suffix : new String[]{ "bar", "" }) {
                    String name = (prefix + c) + suffix;
                    try {
                        Assert.assertEquals(name, Paths.Paths.valid(true, name));
                        Assert.fail(("invalid: " + name));
                    } catch (IllegalArgumentException expected) {
                        // ignore
                    }
                    Assert.assertEquals(name, Paths.Paths.valid(false, name));
                }
            }
        }
    }

    @Test
    public void parentTest() {
        Assert.assertEquals(DIRECTORY, parent(FILE));
        Assert.assertEquals(BASE, parent(DIRECTORY));
        Assert.assertNull(parent(BASE));
        // handling invalid values
        Assert.assertNull(null, parent(null));
        Assert.assertEquals("foo", parent("foo/"));
    }

    @Test
    public void naming() {
        Assert.assertEquals("file.txt", name("directory/file.txt"));
        Assert.assertEquals("txt", extension("directory/file.txt"));
        Assert.assertEquals("directory/file.txt", sidecar("directory/file", "txt"));
        Assert.assertEquals("directory/file.prj", sidecar("directory/file.txt", "prj"));
    }

    @Test
    public void convert1() {
        File folder = new File("folder");
        File file1 = new File("file1");
        File file2 = new File(folder, "file2");
        Assert.assertEquals("folder", Paths.Paths.convert(folder.getPath()));
        Assert.assertEquals("folder/file2", Paths.Paths.convert(file2.getPath()));
        Assert.assertEquals("file1", Paths.Paths.convert(file1.getPath()));
    }

    @Test
    public void convert2() {
        File home = new File(System.getProperty("user.home"));
        File directory = new File(home, "directory");
        File folder = new File(directory, "folder");
        File file1 = new File(directory, "file1");
        File file2 = new File(folder, "file2");
        File relative = new File(new File(".."), "file1");
        Assert.assertEquals("folder", Paths.Paths.convert(directory, folder));
        Assert.assertEquals("folder/file2", Paths.Paths.convert(directory, file2));
        Assert.assertEquals("file1", Paths.Paths.convert(directory, file1));
        String relativePath = relative.getPath();
        Assert.assertEquals("file1", Paths.Paths.convert(directory, folder, relativePath));
    }
}

