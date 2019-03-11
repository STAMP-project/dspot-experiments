package azkaban.jobtype;


import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Test;


// TODO kunkun-tang: This test class needs more refactors.
public class TestHadoopJobUtilsResolveJarSpec {
    private Logger logger = Logger.getRootLogger();

    private static String currentDirString = System.getProperty("user.dir");

    private static String workingDirString = null;

    private static File workingDirFile = null;

    private static File libFolderFile = null;

    private static File executionJarFile = null;

    private static File libraryJarFile = null;

    // nothing should happen
    @Test(expected = IllegalStateException.class)
    public void testJarDoesNotExist() throws IOException {
        HadoopJobUtils.resolveExecutionJarName(TestHadoopJobUtilsResolveJarSpec.workingDirString, "/lib/abc.jar", logger);
    }

    @Test(expected = IllegalStateException.class)
    public void testNoLibFolder() throws IOException {
        FileUtils.deleteDirectory(TestHadoopJobUtilsResolveJarSpec.libFolderFile);
        HadoopJobUtils.resolveExecutionJarName(TestHadoopJobUtilsResolveJarSpec.workingDirString, "/lib/abc.jar", logger);
    }
}

