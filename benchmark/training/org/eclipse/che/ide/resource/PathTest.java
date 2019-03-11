/**
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.ide.resource;


import Path.EMPTY;
import Path.ROOT;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link Path}.
 *
 * @author Vlad Zhukovskyi
 * @see Path
 */
public class PathTest {
    @Test
    public void testShouldReturnNewPathBasedOnValue() throws Exception {
        final String sPath = "/path/segment";
        final Path path = Path.valueOf(sPath);
        Assert.assertEquals(path.toString(), "/path/segment");
    }

    @Test
    public void testShouldReturnNewPathWithCallingConstructor() throws Exception {
        final String sPath = "/path/segment";
        final Path path = new Path(sPath);
        Assert.assertEquals(path.toString(), "/path/segment");
    }

    @Test
    public void testShouldReturnNewPathWithDeviceId() throws Exception {
        final String sPath = "/path/segment";
        final String sDevice = "/mnt";
        final Path path = new Path(sDevice, sPath);
        Assert.assertEquals(path.toString(), "/mnt/path/segment");
    }

    @Test
    public void testShouldReturnNewPathAfterAddingExtension() throws Exception {
        final Path path = Path.valueOf("/foo");
        final Path file = path.addFileExtension("ext");
        Assert.assertNotSame(path, file);
        Assert.assertTrue(path.toString().equals("/foo"));
        Assert.assertTrue(file.toString().equals("/foo.ext"));
    }

    @Test
    public void testShouldReturnNewPathAfterOperationWithTrailingSeparatorToPath() throws Exception {
        final Path path = Path.valueOf("/foo");
        Assert.assertFalse(path.hasTrailingSeparator());
        final Path pathWithSeparator = path.addTrailingSeparator();
        Assert.assertNotSame(path, pathWithSeparator);
        Assert.assertFalse(path.hasTrailingSeparator());
        Assert.assertTrue(pathWithSeparator.hasTrailingSeparator());
        Assert.assertTrue(path.toString().equals("/foo"));
        Assert.assertTrue(pathWithSeparator.hasTrailingSeparator());
        Assert.assertTrue(pathWithSeparator.toString().equals("/foo/"));
        final Path pathWithRemovedSeparator = pathWithSeparator.removeTrailingSeparator();
        Assert.assertTrue(path.equals(pathWithRemovedSeparator));
        Assert.assertFalse(pathWithRemovedSeparator.hasTrailingSeparator());
    }

    @Test
    public void testShouldReturnNewPathAfterAppendingAnotherPath() throws Exception {
        final Path path = Path.valueOf("/foo");
        final Path tmpPath = Path.valueOf("/tmp");
        final Path appended = path.append(tmpPath);
        Assert.assertNotSame(path, appended);
        Assert.assertTrue(path.toString().equals("/foo"));
        Assert.assertTrue(tmpPath.toString().equals("/tmp"));
        Assert.assertTrue(appended.toString().equals("/foo/tmp"));
    }

    @Test
    public void testShouldReturnNewPathAfterAppendingAnotherStringPath() throws Exception {
        final Path path = Path.valueOf("/foo");
        final Path appended = path.append("/tmp");
        Assert.assertNotSame(path, appended);
        Assert.assertTrue(path.toString().equals("/foo"));
        Assert.assertTrue(appended.toString().equals("/foo/tmp"));
    }

    @Test
    public void testShouldCheckPathEquality() throws Exception {
        Assert.assertTrue(Path.valueOf("/foo").equals(Path.valueOf("/foo")));
        Assert.assertTrue(Path.valueOf("/foo").equals(Path.valueOf("/foo/")));
        Assert.assertTrue(new Path("/mnt", "/foo").equals(new Path("/mnt", "/foo")));
        Assert.assertFalse(Path.valueOf("/foo").equals(Path.valueOf("/foo/a/b")));
        Assert.assertFalse(new Path("/mnt", "/foo").equals(new Path("/mount", "/foo")));
        Assert.assertFalse(Path.valueOf("/foo").equals(Path.valueOf("foo/")));
    }

    @Test
    public void testShouldCheckDeviceId() throws Exception {
        final Path path = new Path("/mnt", "/foo");
        Assert.assertTrue(path.getDevice().equals("/mnt"));
    }

    @Test
    public void testShouldCheckFileExtension() throws Exception {
        final Path path = Path.valueOf("/foo");
        Assert.assertNull(path.getFileExtension());
        final Path extPath = path.addFileExtension("ext");
        Assert.assertNotSame(path, extPath);
        Assert.assertNull(path.getFileExtension());
        Assert.assertNotNull(extPath.getFileExtension());
        Assert.assertTrue(extPath.getFileExtension().equals("ext"));
        Assert.assertTrue(extPath.toString().equals("/foo.ext"));
        final Path removedExtPath = extPath.removeFileExtension();
        Assert.assertNotSame(path, removedExtPath);
        Assert.assertNotSame(extPath, removedExtPath);
        Assert.assertTrue(path.equals(removedExtPath));
        Assert.assertTrue(removedExtPath.toString().equals("/foo"));
    }

    @Test
    public void testShouldReturnTrueWhenTwoEqualPathHaveSameHashCode() throws Exception {
        Assert.assertTrue(((Path.valueOf("/foo").hashCode()) == (Path.valueOf("/foo").hashCode())));
        Assert.assertTrue(((Path.valueOf("/foo").hashCode()) == (Path.valueOf("/foo/").hashCode())));
    }

    @Test
    public void testShouldCheckExistenceOfTrailingSeparator() throws Exception {
        Assert.assertTrue(Path.valueOf("/foo/").hasTrailingSeparator());
        Assert.assertFalse(Path.valueOf("/foo").hasTrailingSeparator());
        Assert.assertFalse(Path.valueOf("/").hasTrailingSeparator());
        Assert.assertFalse(Path.valueOf("").hasTrailingSeparator());
    }

    @Test
    public void testShouldCheckAbsolutePath() throws Exception {
        Assert.assertTrue(Path.valueOf("/foo").isAbsolute());
        Assert.assertFalse(Path.valueOf("foo").isAbsolute());
    }

    @Test
    public void testShouldCheckEmptiness() throws Exception {
        Assert.assertTrue(Path.valueOf("").isEmpty());
        Assert.assertFalse(Path.valueOf("/").isEmpty());
    }

    @Test
    public void testShouldCheckPathPrefix() throws Exception {
        Assert.assertTrue(Path.valueOf("").isPrefixOf(Path.valueOf("")));
        Assert.assertTrue(Path.valueOf("/").isPrefixOf(Path.valueOf("")));
        Assert.assertTrue(Path.valueOf("").isPrefixOf(Path.valueOf("/")));
        Assert.assertTrue(Path.valueOf("/").isPrefixOf(Path.valueOf("/")));
        Assert.assertTrue(Path.valueOf("/").isPrefixOf(Path.valueOf("/foo/a/b/c")));
        Assert.assertTrue(Path.valueOf("/foo").isPrefixOf(Path.valueOf("/foo/a/b/c")));
        Assert.assertTrue(Path.valueOf("/foo/").isPrefixOf(Path.valueOf("/foo/a/b/c")));
        Assert.assertTrue(Path.valueOf("foo").isPrefixOf(Path.valueOf("/foo/a/b/c")));
        Assert.assertTrue(Path.valueOf("/foo/a/b/c").isPrefixOf(Path.valueOf("/foo/a/b/c")));
        Assert.assertFalse(Path.valueOf("/foo/a/b/c/d").isPrefixOf(Path.valueOf("/foo/a/b/c")));
        Assert.assertFalse(Path.valueOf("/foo/a/x/c").isPrefixOf(Path.valueOf("/foo/a/b/c")));
        Assert.assertFalse(Path.valueOf("foo/a/x/c").isPrefixOf(Path.valueOf("/foo/a/b/c")));
        Assert.assertFalse(Path.valueOf("abc").isPrefixOf(Path.valueOf("def")));
    }

    @Test
    public void testShouldCheckIsPathRoot() throws Exception {
        Assert.assertTrue(Path.valueOf("/").isRoot());
        Assert.assertFalse(Path.valueOf("/foo").isRoot());
        Assert.assertFalse(Path.valueOf("").isRoot());
        Assert.assertFalse(Path.valueOf("foo").isRoot());
    }

    @Test
    public void testShouldCheckIfPathIsUnc() throws Exception {
        final Path path = Path.valueOf("//foo");
        Assert.assertNull(path.getDevice());
        Assert.assertTrue(path.isUNC());
        Assert.assertFalse(Path.valueOf("/foo").isUNC());
    }

    @Test
    public void testShouldCheckPathValidity() throws Exception {
        Assert.assertTrue(Path.isValidPath("/"));
        Assert.assertTrue(Path.isValidPath("/foo/bar"));
        Assert.assertFalse(Path.isValidSegment(""));
        Assert.assertFalse(Path.isValidSegment("foo/bar"));
    }

    @Test
    public void testShouldCheckLastSegmentOperations() throws Exception {
        final Path path = Path.valueOf("/foo/a/b/c/bar");
        Assert.assertTrue(path.lastSegment().equals("bar"));
        Assert.assertTrue(((path.segmentCount()) == 5));
        Assert.assertTrue(Arrays.equals(path.segments(), new String[]{ "foo", "a", "b", "c", "bar" }));
        Assert.assertEquals(path.segment(0), "foo");
        Assert.assertEquals(path.segment(1), "a");
        Assert.assertEquals(path.segment(2), "b");
        Assert.assertEquals(path.segment(3), "c");
        Assert.assertEquals(path.segment(4), "bar");
        Assert.assertTrue(((path.matchingFirstSegments(Path.valueOf("/foo/a/b/c/bar"))) == 5));
        Assert.assertTrue(((path.matchingFirstSegments(Path.valueOf("/foo/a/x/x/x"))) == 2));
        Assert.assertTrue(((path.matchingFirstSegments(Path.valueOf("/x/x/x/x/x"))) == 0));
        Assert.assertTrue(path.removeFirstSegments(1).equals(Path.valueOf("a/b/c/bar")));
        Assert.assertTrue(path.removeLastSegments(1).equals(Path.valueOf("/foo/a/b/c")));
        Assert.assertTrue(path.uptoSegment(1).equals(Path.valueOf("/foo")));
        Assert.assertTrue(path.uptoSegment(2).equals(Path.valueOf("/foo/a")));
        Assert.assertTrue(path.uptoSegment(3).equals(Path.valueOf("/foo/a/b")));
        Assert.assertTrue(path.uptoSegment(4).equals(Path.valueOf("/foo/a/b/c")));
    }

    @Test
    public void testShouldSetCorrectDevice() throws Exception {
        final Path path = Path.valueOf("/foo");
        Assert.assertNull(path.getDevice());
        final Path pathWithDevice = path.setDevice("mnt:");
        Assert.assertNotSame(path, pathWithDevice);
        Assert.assertFalse(path.equals(pathWithDevice));
        Assert.assertTrue(pathWithDevice.toString().equals("mnt:/foo"));
    }

    @Test
    public void testShouldReturnCommonPath() throws Exception {
        final Path common = Path.valueOf("/foo");
        final Path path1 = common.append("a/b");
        final Path path2 = common.append("a/c");
        final Path path3 = common.append("a/d");
        final Path path4 = common.append("c/d/b");
        final Path path5 = common.append("a/d/c");
        final Path path6 = common.append("a/c/c");
        final Path result = Path.commonPath(path1, path2, path3, path4, path5, path6);
        Assert.assertEquals(result, common);
    }

    @Test
    public void testShouldReturnRootPathAsCommon() throws Exception {
        final Path path1 = Path.valueOf("/a");
        final Path path2 = Path.valueOf("/b");
        final Path path3 = Path.valueOf("/c");
        final Path path4 = Path.valueOf("/d");
        final Path path5 = Path.valueOf("/e");
        final Path result = Path.commonPath(path1, path2, path3, path4, path5);
        Assert.assertEquals(result, ROOT);
    }

    @Test
    public void testShouldReturnRootPathAsCommon2() throws Exception {
        final Path path1 = Path.valueOf("/a");
        final Path path2 = Path.valueOf("b");
        final Path path3 = Path.valueOf("/c");
        final Path path4 = Path.valueOf("d");
        final Path path5 = Path.valueOf("/e");
        final Path result = Path.commonPath(path1, path2, path3, path4, path5);
        Assert.assertEquals(result, ROOT);
    }

    @Test
    public void testShouldReturnEmptyPathForEmptyInputArray() throws Exception {
        final Path result = Path.commonPath();
        Assert.assertEquals(result, EMPTY);
    }

    @Test(expected = NullPointerException.class)
    public void testShouldThrowNPEOnNullArgument() throws Exception {
        Path.commonPath(null);
    }

    @Test
    public void testShouldReturnSamePathAsOneGivenAsArgument() throws Exception {
        final Path path = Path.valueOf("/some/path");
        final Path result = Path.commonPath(path);
        Assert.assertEquals(result, path);
    }

    @Test
    public void testShouldReturnCorrectCommonPathIfPathHasSegmentsMoreThanPathCount() throws Exception {
        final Path common = Path.valueOf("/foo");
        final Path path1 = common.append("a/b/c/d/e/f/g/h/i/j/k/l");
        final Path path2 = common.append("b/c");
        final Path result = Path.commonPath(path1, path2);
        Assert.assertEquals(result, common);
    }

    @Test
    public void testShouldCheckExistenceOfLeadingSeparator() {
        Assert.assertTrue(Path.valueOf("/foo").hasLeadingSeparator());
        Assert.assertFalse(Path.valueOf("foo").hasLeadingSeparator());
        Assert.assertTrue(Path.valueOf("/").hasLeadingSeparator());
        Assert.assertFalse(Path.valueOf("").hasLeadingSeparator());
    }

    @Test
    public void shouldReturnNewPathWithAddedLeadingSeparatorIfPathHasNotIt() {
        final Path path = Path.valueOf("Test/src/main/java");
        final Path pathWithSeparator = path.addLeadingSeparator();
        Assert.assertNotEquals(path, pathWithSeparator);
        Assert.assertFalse(path.hasLeadingSeparator());
        Assert.assertTrue(pathWithSeparator.hasLeadingSeparator());
        Assert.assertTrue(path.toString().equals("Test/src/main/java"));
        Assert.assertTrue(pathWithSeparator.toString().equals("/Test/src/main/java"));
    }

    @Test
    public void shouldReturnTheSamePathIfPathAlreadyHasLeadingSeparator() {
        final Path path = Path.valueOf("/Test/src/main/java");
        final Path pathWithSeparator = path.addLeadingSeparator();
        Assert.assertEquals(path, pathWithSeparator);
        Assert.assertTrue(path.hasLeadingSeparator());
        Assert.assertTrue(pathWithSeparator.hasLeadingSeparator());
        Assert.assertTrue(path.toString().equals("/Test/src/main/java"));
        Assert.assertTrue(pathWithSeparator.toString().equals("/Test/src/main/java"));
    }

    @Test
    public void shouldReturnTheSamePathIfPathIsRoot() {
        final Path path = Path.valueOf("/");
        final Path pathWithSeparator = path.addLeadingSeparator();
        Assert.assertEquals(path, pathWithSeparator);
        Assert.assertTrue(path.hasLeadingSeparator());
        Assert.assertTrue(pathWithSeparator.hasLeadingSeparator());
        Assert.assertTrue(path.toString().equals("/"));
        Assert.assertTrue(pathWithSeparator.toString().equals("/"));
    }

    @Test
    public void shouldReturnNewRootPathIfPathIsEmpty() {
        final Path path = Path.valueOf("");
        final Path pathWithSeparator = path.addLeadingSeparator();
        Assert.assertNotEquals(path, pathWithSeparator);
        Assert.assertFalse(path.hasLeadingSeparator());
        Assert.assertTrue(pathWithSeparator.hasLeadingSeparator());
        Assert.assertTrue(path.toString().equals(""));
        Assert.assertTrue(pathWithSeparator.toString().equals("/"));
    }
}

