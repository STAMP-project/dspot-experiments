package io.fabric8.maven.docker.config.handler.compose;


import io.fabric8.maven.docker.util.PathTestUtil;
import java.io.File;
import mockit.Expectations;
import mockit.Mocked;
import mockit.VerificationsInOrder;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class ComposeUtilsTest {
    private final String className = ComposeUtilsTest.class.getSimpleName();

    private final String ABS_BASEDIR = PathTestUtil.createTmpFile(className).getAbsolutePath();

    @Mocked
    private MavenProject project;

    @Test
    public void resolveComposeFileWithAbsoluteComposeFile() throws Exception {
        String absComposeFile = ((PathTestUtil.createTmpFile(className).getAbsolutePath()) + (PathTestUtil.SEP)) + "docker-compose.yaml";
        Assert.assertEquals(new File(absComposeFile), ComposeUtils.resolveComposeFileAbsolutely(null, absComposeFile, null));
    }

    @Test
    public void resolveComposeFileWithRelativeComposeFileAndAbsoluteBaseDir() throws Exception {
        String relComposeFile = PathTestUtil.join(PathTestUtil.SEP, "relative", "path", "to", "docker-compose.yaml");// relative/path/to/docker-compose.yaml

        final String absMavenProjectDir = PathTestUtil.createTmpFile(className).getAbsolutePath();
        new Expectations() {
            {
                project.getBasedir();
                result = new File(absMavenProjectDir);
            }
        };
        Assert.assertEquals(new File(ABS_BASEDIR, relComposeFile), ComposeUtils.resolveComposeFileAbsolutely(ABS_BASEDIR, relComposeFile, project));
        new VerificationsInOrder() {
            {
                project.getBasedir();
            }
        };
    }

    @Test
    public void resolveComposeFileWithRelativeComposeFileAndRelativeBaseDir() throws Exception {
        String relComposeFile = PathTestUtil.join(PathTestUtil.SEP, "relative", "path", "to", "docker-compose.yaml");// relative/path/to/docker-compose.yaml

        String relBaseDir = "basedir" + (PathTestUtil.SEP);
        final String absMavenProjectDir = PathTestUtil.createTmpFile(className).getAbsolutePath();
        new Expectations() {
            {
                project.getBasedir();
                result = new File(absMavenProjectDir);
            }
        };
        Assert.assertEquals(new File(new File(absMavenProjectDir, relBaseDir), relComposeFile), ComposeUtils.resolveComposeFileAbsolutely(relBaseDir, relComposeFile, project));
        new VerificationsInOrder() {
            {
                project.getBasedir();
            }
        };
    }

    @Test
    public void resolveComposesFileWithRelativeComposeFileParentDirectory() throws Exception {
        String relComposeFile = PathTestUtil.join(PathTestUtil.SEP, ((PathTestUtil.DOT) + (PathTestUtil.DOT)), "relative", "path", "to", "docker-compose.yaml");// ../relative/path/to/docker-compose.yaml

        File tmpDir = PathTestUtil.createTmpFile(ComposeUtilsTest.class.getName());
        String absBaseDir = tmpDir.getAbsolutePath();
        Assert.assertEquals(new File(tmpDir.getParentFile(), relComposeFile.substring(3)), ComposeUtils.resolveComposeFileAbsolutely(absBaseDir, relComposeFile, null));
    }
}

