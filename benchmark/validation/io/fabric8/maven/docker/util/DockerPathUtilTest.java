package io.fabric8.maven.docker.util;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;


/**
 * Path manipulation tests
 */
public class DockerPathUtilTest {
    private final String className = DockerPathUtilTest.class.getSimpleName();

    /**
     * A sample relative path, that does not begin or end with a file.separator character
     */
    private final String RELATIVE_PATH = ("relative" + (PathTestUtil.SEP)) + "path";

    /**
     * A sample absolute path, which begins with a file.separator character (or drive letter on the Windows platform)
     */
    private final String ABS_BASE_DIR = PathTestUtil.createTmpFile(className).getAbsolutePath();

    /**
     * A sample relative path (no different than {@link #RELATIVE_PATH}), provided as the member name is
     * self-documenting in the test.
     */
    private final String REL_BASE_DIR = ("base" + (PathTestUtil.SEP)) + "directory";

    @Test
    public void resolveAbsolutelyWithRelativePath() {
        String toResolve = RELATIVE_PATH;// relative/path

        String absBaseDir = ABS_BASE_DIR;// /base/directory

        // '/base/directory' and 'relative/path' to '/base/directory/relative/path'
        Assert.assertEquals(new File(((absBaseDir + (PathTestUtil.SEP)) + toResolve)), DockerPathUtil.resolveAbsolutely(toResolve, absBaseDir));
    }

    @Test
    public void resolveAbsolutelyWithRelativePathAndTrailingSlash() {
        String toResolve = (RELATIVE_PATH) + (PathTestUtil.SEP);// relative/path/

        String absBaseDir = ABS_BASE_DIR;// /base/directory

        // '/base/directory' and 'relative/path/' to '/base/directory/relative/path'
        Assert.assertEquals(new File(((absBaseDir + (PathTestUtil.SEP)) + toResolve)), DockerPathUtil.resolveAbsolutely(toResolve, absBaseDir));
    }

    @Test
    public void resolveAbsolutelyWithTrailingSlashWithRelativePath() {
        String toResolve = RELATIVE_PATH;// relative/path

        String absBaseDir = (ABS_BASE_DIR) + (PathTestUtil.SEP);// /base/directory/

        // '/base/directory/' and 'relative/path' to '/base/directory/relative/path'
        Assert.assertEquals(new File((absBaseDir + toResolve)), DockerPathUtil.resolveAbsolutely(toResolve, absBaseDir));
    }

    @Test(expected = IllegalArgumentException.class)
    public void resolveAbsolutelyWithRelativePathAndRelativeBaseDir() throws IllegalArgumentException {
        DockerPathUtil.resolveAbsolutely(RELATIVE_PATH, REL_BASE_DIR);
    }

    /**
     * The supplied base directory is relative, but isn't used because the supplied path is absolute.
     */
    @Test
    public void resolveAbsolutelyWithAbsolutePathAndRelativeBaseDir() {
        String absolutePath = PathTestUtil.createTmpFile(className).getAbsolutePath();
        Assert.assertEquals(new File(absolutePath), DockerPathUtil.resolveAbsolutely(absolutePath, REL_BASE_DIR));
    }

    @Test
    public void resolveAbsolutelyWithExtraSlashes() throws Exception {
        String toResolve = ((RELATIVE_PATH) + (PathTestUtil.SEP)) + (PathTestUtil.SEP);// relative/path//

        // '/base/directory' and 'relative/path//' to '/base/directory/relative/path'
        Assert.assertEquals(new File((((ABS_BASE_DIR) + (PathTestUtil.SEP)) + (RELATIVE_PATH))), DockerPathUtil.resolveAbsolutely(toResolve, ABS_BASE_DIR));
    }

    @Test
    public void resolveAbsolutelyWithRelativeParentPath() throws Exception {
        String toResolve = PathTestUtil.join(PathTestUtil.SEP, ((PathTestUtil.DOT) + (PathTestUtil.DOT)), RELATIVE_PATH);// ../relative/path

        // '/base/directory' and '../relative/path' to '/base/relative/path'
        Assert.assertEquals(new File(new File(ABS_BASE_DIR).getParent(), RELATIVE_PATH), DockerPathUtil.resolveAbsolutely(toResolve, ABS_BASE_DIR));
    }
}

