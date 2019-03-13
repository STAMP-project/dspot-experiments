package io.fabric8.maven.docker.util;


import RunVolumeConfiguration.Builder;
import io.fabric8.maven.docker.config.RunVolumeConfiguration;
import java.io.File;
import java.util.Collections;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import static io.fabric8.maven.docker.util.PathTestUtil.TMP_FILE_PRESERVE_MODE.DELETE_IMMEDIATELY;


/**
 *
 */
public class VolumeBindingUtilTest {
    private static final String CLASS_NAME = VolumeBindingUtilTest.class.getSimpleName();

    private static final String SEP = System.getProperty("file.separator");

    /**
     * An absolute file path that represents a base directory.  It is important for the JVM to create the the file so
     * that the absolute path representation of the test platform is used.
     */
    private static final File ABS_BASEDIR = PathTestUtil.createTmpFile(VolumeBindingUtilTest.CLASS_NAME, DELETE_IMMEDIATELY);

    /**
     * Host portion of a volume binding string representing a directory relative to the current working directory.
     */
    private static final String RELATIVE_PATH = ((PathTestUtil.DOT) + (VolumeBindingUtilTest.SEP)) + "rel";// ./rel


    /**
     * Host portion of a volume binding string representing a directory relative to the current user's home directory.
     */
    private static final String USER_PATH = ((PathTestUtil.TILDE) + (VolumeBindingUtilTest.SEP)) + "relUser";// ~/relUser


    /**
     * Host portion of a volume binding string representing the current user's home directory.
     */
    private static final String USER_HOME = (PathTestUtil.TILDE) + "user";// ~user


    /**
     * Container portion of a volume binding string; the location in the container where the host portion is mounted.
     */
    private static final String CONTAINER_PATH = "/path/to/container/dir";

    /**
     * Format of a volume binding string that does not have any access controls.  Format is: host binding string
     * portion, container binding string portion
     */
    private final String BIND_STRING_FMT = "%s:%s";

    /**
     * Format of a volume binding string that contains access controls.  Format is: host binding string portion,
     * container binding string portion, access control portion.
     */
    private final String BIND_STRING_WITH_ACCESS_FMT = "%s:%s:%s";

    /**
     * Access control portion of a volume binding string.
     */
    private final String RO_ACCESS = "ro";

    /**
     * Insures the supplied base directory is absolute.
     */
    @Test(expected = IllegalArgumentException.class)
    public void relativeBaseDir() {
        VolumeBindingUtil.resolveRelativeVolumeBinding(new File("relative/"), String.format(BIND_STRING_FMT, VolumeBindingUtilTest.RELATIVE_PATH, VolumeBindingUtilTest.CONTAINER_PATH));
    }

    /**
     * Insures that a host volume binding string that contains a path relative to the current working directory is
     * resolved to the supplied base directory.
     */
    @Test
    public void testResolveRelativeVolumePath() {
        String volumeString = String.format(BIND_STRING_FMT, VolumeBindingUtilTest.RELATIVE_PATH, VolumeBindingUtilTest.CONTAINER_PATH);
        // './rel:/path/to/container/dir' to '/absolute/basedir/rel:/path/to/container/dir'
        String relativizedVolumeString = VolumeBindingUtil.resolveRelativeVolumeBinding(VolumeBindingUtilTest.ABS_BASEDIR, volumeString);
        String expectedBindingString = String.format(BIND_STRING_FMT, new File(VolumeBindingUtilTest.ABS_BASEDIR, PathTestUtil.stripLeadingPeriod(VolumeBindingUtilTest.RELATIVE_PATH)), VolumeBindingUtilTest.CONTAINER_PATH);
        Assert.assertEquals(expectedBindingString, relativizedVolumeString);
    }

    /**
     * Insures that a host volume binding string that contains a path relative to the current working directory <em>and
     * </em> specifies access controls resolves to the supplied base directory <em>and</em> that the access controls are
     * preserved through the operation.
     */
    @Test
    public void testResolveRelativeVolumePathWithAccessSpecifications() {
        String volumeString = String.format(BIND_STRING_WITH_ACCESS_FMT, VolumeBindingUtilTest.RELATIVE_PATH, VolumeBindingUtilTest.CONTAINER_PATH, RO_ACCESS);
        // './rel:/path/to/container/dir:ro' to '/absolute/basedir/rel:/path/to/container/dir:ro'
        String relativizedVolumeString = VolumeBindingUtil.resolveRelativeVolumeBinding(VolumeBindingUtilTest.ABS_BASEDIR, volumeString);
        String expectedBindingString = String.format(BIND_STRING_WITH_ACCESS_FMT, new File(VolumeBindingUtilTest.ABS_BASEDIR, PathTestUtil.stripLeadingPeriod(VolumeBindingUtilTest.RELATIVE_PATH)), VolumeBindingUtilTest.CONTAINER_PATH, RO_ACCESS);
        Assert.assertEquals(expectedBindingString, relativizedVolumeString);
    }

    /**
     * Insures that a host volume binding string that contains a path relative to the user's home directory resolves to
     * the user's home directory and not the supplied base directory.
     */
    @Test
    public void testResolveUserVolumePath() {
        String volumeString = String.format(BIND_STRING_FMT, VolumeBindingUtilTest.USER_PATH, VolumeBindingUtilTest.CONTAINER_PATH);
        // '~/rel:/path/to/container/dir' to '/user/home/rel:/path/to/container/dir'
        String relativizedVolumeString = VolumeBindingUtil.resolveRelativeVolumeBinding(new File("ignored"), volumeString);
        String expectedBindingString = String.format(BIND_STRING_FMT, new File(System.getProperty("user.home"), PathTestUtil.stripLeadingTilde(VolumeBindingUtilTest.USER_PATH)), VolumeBindingUtilTest.CONTAINER_PATH);
        Assert.assertEquals(expectedBindingString, relativizedVolumeString);
    }

    /**
     * Resolving arbitrary user home paths, e.g. represented as {@code ~user}, is not supported.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testResolveUserHomeVolumePath() {
        String volumeString = String.format(BIND_STRING_FMT, VolumeBindingUtilTest.USER_HOME, VolumeBindingUtilTest.CONTAINER_PATH);
        // '~user:/path/to/container/dir' to '/home/user:/path/to/container/dir'
        String relativizedVolumeString = VolumeBindingUtil.resolveRelativeVolumeBinding(new File("ignored"), volumeString);
    }

    /**
     * Insures that volume binding strings referencing a named volume are preserved untouched.
     */
    @Test
    public void testResolveNamedVolume() throws Exception {
        String volumeName = "volname";
        String volumeString = String.format(BIND_STRING_FMT, volumeName, VolumeBindingUtilTest.CONTAINER_PATH);
        // volumeString should be untouched
        Assert.assertEquals(volumeString, VolumeBindingUtil.resolveRelativeVolumeBinding(VolumeBindingUtilTest.ABS_BASEDIR, volumeString));
    }

    /**
     * Insures that volume binding strings that contain an absolute path for the host portion are preserved untouched.
     */
    @Test
    public void testResolveAbsolutePathMapping() {
        String absolutePath = PathTestUtil.createTmpFile(VolumeBindingUtilTest.class.getSimpleName(), DELETE_IMMEDIATELY).getAbsolutePath();
        String volumeString = String.format(BIND_STRING_FMT, absolutePath, VolumeBindingUtilTest.CONTAINER_PATH);
        // volumeString should be untouched
        Assert.assertEquals(volumeString, VolumeBindingUtil.resolveRelativeVolumeBinding(VolumeBindingUtilTest.ABS_BASEDIR, volumeString));
    }

    /**
     * Insures that volume binding strings with an absolute host portion are returned unchanged (no resolution necessary
     * because the the path is absolute)
     */
    @Test
    public void testResolveSinglePath() {
        String absolutePath = PathTestUtil.createTmpFile(VolumeBindingUtilTest.class.getSimpleName(), DELETE_IMMEDIATELY).getAbsolutePath();
        // volumeString should be untouched
        Assert.assertEquals(absolutePath, VolumeBindingUtil.resolveRelativeVolumeBinding(VolumeBindingUtilTest.ABS_BASEDIR, absolutePath));
    }

    /**
     * Insures that relative paths in the host portion of a volume binding string are properly resolved against a base
     * directory when present in a {@link RunVolumeConfiguration}.
     */
    @Test
    public void testResolveVolumeBindingsWithRunVolumeConfiguration() {
        RunVolumeConfiguration.Builder builder = new RunVolumeConfiguration.Builder();
        builder.bind(Collections.singletonList(String.format(BIND_STRING_FMT, VolumeBindingUtilTest.RELATIVE_PATH, VolumeBindingUtilTest.CONTAINER_PATH)));
        RunVolumeConfiguration volumeConfiguration = builder.build();
        // './rel:/path/to/container/dir' to '/absolute/basedir/rel:/path/to/container/dir'
        VolumeBindingUtil.resolveRelativeVolumeBindings(VolumeBindingUtilTest.ABS_BASEDIR, volumeConfiguration);
        String expectedBindingString = String.format(BIND_STRING_FMT, PathTestUtil.join("", VolumeBindingUtilTest.ABS_BASEDIR.getAbsolutePath(), PathTestUtil.stripLeadingPeriod(VolumeBindingUtilTest.RELATIVE_PATH)), VolumeBindingUtilTest.CONTAINER_PATH);
        Assert.assertEquals(expectedBindingString, volumeConfiguration.getBind().get(0));
    }

    /**
     * Insures that a relative path referencing the parent directory are properly resolved against a base directory.
     */
    @Test
    public void testResolveParentRelativeVolumePath() {
        String relativePath = (PathTestUtil.DOT) + (VolumeBindingUtilTest.RELATIVE_PATH);// '../rel'

        String volumeString = String.format(BIND_STRING_FMT, relativePath, VolumeBindingUtilTest.CONTAINER_PATH);
        // '../rel:/path/to/container/dir to '/absolute/rel:/path/to/container/dir'
        String relativizedVolumeString = VolumeBindingUtil.resolveRelativeVolumeBinding(VolumeBindingUtilTest.ABS_BASEDIR, volumeString);
        String expectedBindingString = String.format(BIND_STRING_FMT, new File(VolumeBindingUtilTest.ABS_BASEDIR.getParent(), PathTestUtil.stripLeadingPeriod(VolumeBindingUtilTest.RELATIVE_PATH)), VolumeBindingUtilTest.CONTAINER_PATH);
        Assert.assertEquals(expectedBindingString, relativizedVolumeString);
    }

    /**
     * The volume binding string: {@code rel:/path/to/container/mountpoint} is not resolved, because {@code rel} is
     * considered a <em>named volume</em>.
     */
    @Test
    public void testResolveRelativeVolumePathWithoutCurrentDirectory() throws Exception {
        String relativePath = "rel";
        String volumeString = String.format(BIND_STRING_FMT, relativePath, VolumeBindingUtilTest.CONTAINER_PATH);
        // 'rel:/path/to/container/dir' to 'rel:/path/to/container/dir'
        String relativizedVolumeString = VolumeBindingUtil.resolveRelativeVolumeBinding(VolumeBindingUtilTest.ABS_BASEDIR, volumeString);
        String expectedBindingString = String.format(BIND_STRING_FMT, relativePath, VolumeBindingUtilTest.CONTAINER_PATH);
        Assert.assertEquals(expectedBindingString, relativizedVolumeString);
    }

    /**
     * The volume binding string: {@code src/test/docker:/path/to/container/mountpoint} is resolved, because {@code src/
     * test/docker} is considered a <em>relative path</em>.
     */
    @Test
    public void testResolveRelativeVolumePathContainingSlashes() throws Exception {
        String relativePath = ((("src" + (VolumeBindingUtilTest.SEP)) + "test") + (VolumeBindingUtilTest.SEP)) + "docker";
        String volumeString = String.format(BIND_STRING_FMT, relativePath, VolumeBindingUtilTest.CONTAINER_PATH);
        // 'src/test/docker:/path/to/container/dir' to '/absolute/basedir/src/test/docker:/path/to/container/dir'
        String relativizedVolumeString = VolumeBindingUtil.resolveRelativeVolumeBinding(VolumeBindingUtilTest.ABS_BASEDIR, volumeString);
        String expectedBindingString = String.format(BIND_STRING_FMT, new File(VolumeBindingUtilTest.ABS_BASEDIR, relativePath), VolumeBindingUtilTest.CONTAINER_PATH);
        Assert.assertEquals(expectedBindingString, relativizedVolumeString);
    }

    @Test
    public void testIsRelativePath() throws Exception {
        TestCase.assertTrue(VolumeBindingUtil.isRelativePath(("rel" + (VolumeBindingUtilTest.SEP))));
        // rel/
        TestCase.assertTrue(VolumeBindingUtil.isRelativePath(PathTestUtil.join(VolumeBindingUtilTest.SEP, "src", "test", "docker")));// src/test/docker

        TestCase.assertTrue(VolumeBindingUtil.isRelativePath(PathTestUtil.join(VolumeBindingUtilTest.SEP, PathTestUtil.DOT, "rel")));
        // ./rel
        TestCase.assertTrue(VolumeBindingUtil.isRelativePath(PathTestUtil.join(VolumeBindingUtilTest.SEP, PathTestUtil.TILDE, "rel")));
        // ~/rel
        TestCase.assertTrue(VolumeBindingUtil.isRelativePath(PathTestUtil.join(VolumeBindingUtilTest.SEP, ((PathTestUtil.DOT) + (PathTestUtil.DOT)), "rel")));
        // ../rel
        Assert.assertFalse(VolumeBindingUtil.isRelativePath("rel"));
        // 'rel' is a named volume in this case
        Assert.assertFalse(VolumeBindingUtil.isRelativePath(PathTestUtil.createTmpFile(VolumeBindingUtilTest.class.getSimpleName(), DELETE_IMMEDIATELY).getAbsolutePath()));// is absolute

    }

    @Test
    public void testIsUserRelativeHomeDir() throws Exception {
        Assert.assertFalse(VolumeBindingUtil.isUserHomeRelativePath(PathTestUtil.join(PathTestUtil.TILDE, "foo", "bar")));// foo~bar

        Assert.assertFalse(VolumeBindingUtil.isUserHomeRelativePath(("foo" + (PathTestUtil.TILDE))));
        // foo~
        Assert.assertFalse(VolumeBindingUtil.isUserHomeRelativePath("foo"));
        // foo
        TestCase.assertTrue(VolumeBindingUtil.isUserHomeRelativePath(((PathTestUtil.TILDE) + "user")));
        // ~user
        TestCase.assertTrue(VolumeBindingUtil.isUserHomeRelativePath(PathTestUtil.join(VolumeBindingUtilTest.SEP, PathTestUtil.TILDE, "dir")));
        // ~/dir
        TestCase.assertTrue(VolumeBindingUtil.isUserHomeRelativePath(PathTestUtil.join(VolumeBindingUtilTest.SEP, ((PathTestUtil.TILDE) + "user"), "dir")));// ~user/dir

    }

    /**
     * Test windows paths even if the test JVM runtime is on *nix, specifically the consideration of an 'absolute'
     * path by {@link VolumeBindingUtil#isRelativePath(String)}.
     */
    @Test
    public void testIsRelativePathForWindows() {
        Assert.assertFalse(VolumeBindingUtil.isRelativePath("C:\\foo"));
        // C:\foo
        Assert.assertFalse(VolumeBindingUtil.isRelativePath("x:\\bar"));
        // x:\bar
        Assert.assertFalse(VolumeBindingUtil.isRelativePath("C:\\"));
        // C:\
        Assert.assertFalse(VolumeBindingUtil.isRelativePath("\\"));// \

    }
}

