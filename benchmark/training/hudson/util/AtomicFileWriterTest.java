package hudson.util;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.Issue;


public class AtomicFileWriterTest {
    private static final String PREVIOUS = "previous value \n blah";

    /**
     * Provides access to the default permissions given to a new file. (i.e. indirect way to get umask settings).
     * <p><strong>BEWARE: null on non posix systems</strong></p>
     */
    @Nullable
    private static Set<PosixFilePermission> DEFAULT_GIVEN_PERMISSIONS;

    @ClassRule
    public static TemporaryFolder tmp = new TemporaryFolder();

    File af;

    AtomicFileWriter afw;

    String expectedContent = "hello world";

    @Test
    public void symlinkToDirectory() throws Exception {
        final File folder = AtomicFileWriterTest.tmp.newFolder();
        final File containingSymlink = AtomicFileWriterTest.tmp.newFolder();
        final Path zeSymlink = Files.createSymbolicLink(Paths.get(containingSymlink.getAbsolutePath(), "ze_symlink"), folder.toPath());
        final Path childFileInSymlinkToDir = Paths.get(zeSymlink.toString(), "childFileInSymlinkToDir");
        new AtomicFileWriter(childFileInSymlinkToDir, Charset.forName("UTF-8"));
    }

    @Test
    public void createFile() throws Exception {
        // Verify the file we created exists
        Assert.assertTrue(Files.exists(afw.getTemporaryPath()));
    }

    @Test
    public void writeToAtomicFile() throws Exception {
        // Given
        afw.write(expectedContent, 0, expectedContent.length());
        afw.write(expectedContent);
        afw.write(' ');
        // When
        afw.flush();
        // Then
        Assert.assertEquals("File writer did not properly flush to temporary file", (((expectedContent.length()) * 2) + 1), Files.size(afw.getTemporaryPath()));
    }

    @Test
    public void commitToFile() throws Exception {
        // Given
        afw.write(expectedContent, 0, expectedContent.length());
        afw.write(new char[]{ 'h', 'e', 'y' }, 0, 3);
        // When
        afw.commit();
        // Then
        Assert.assertEquals(((expectedContent.length()) + 3), Files.size(af.toPath()));
        Assert.assertEquals(((expectedContent) + "hey"), FileUtils.readFileToString(af));
    }

    @Test
    public void abortDeletesTmpFile() throws Exception {
        // Given
        afw.write(expectedContent, 0, expectedContent.length());
        // When
        afw.abort();
        // Then
        Assert.assertTrue(Files.notExists(afw.getTemporaryPath()));
        Assert.assertEquals(AtomicFileWriterTest.PREVIOUS, FileUtils.readFileToString(af));
    }

    @Test
    public void indexOutOfBoundsLeavesOriginalUntouched() throws Exception {
        // Given
        try {
            afw.write(expectedContent, 0, ((expectedContent.length()) + 10));
            Assert.fail("exception expected");
        } catch (IndexOutOfBoundsException e) {
        }
        Assert.assertEquals(AtomicFileWriterTest.PREVIOUS, FileUtils.readFileToString(af));
    }

    @Test
    public void badPath() throws Exception {
        final File newFile = AtomicFileWriterTest.tmp.newFile();
        File parentExistsAndIsAFile = new File(newFile, "badChild");
        Assert.assertTrue(newFile.exists());
        Assert.assertFalse(parentExistsAndIsAFile.exists());
        try {
            new AtomicFileWriter(parentExistsAndIsAFile.toPath(), Charset.forName("UTF-8"));
            Assert.fail("Expected a failure");
        } catch (IOException e) {
            Assert.assertThat(e.getMessage(), StringContains.containsString("exists and is neither a directory nor a symlink to a directory"));
        }
    }

    @Issue("JENKINS-48407")
    @Test
    public void checkPermissionsRespectUmask() throws IOException, InterruptedException {
        final File newFile = AtomicFileWriterTest.tmp.newFile();
        boolean posixSupported = AtomicFileWriterTest.isPosixSupported(newFile);
        Assume.assumeThat(posixSupported, Is.is(true));
        // given
        Path filePath = newFile.toPath();
        // when
        AtomicFileWriter w = new AtomicFileWriter(filePath, StandardCharsets.UTF_8);
        w.write("whatever");
        w.commit();
        // then
        Assert.assertFalse(w.getTemporaryPath().toFile().exists());
        Assert.assertTrue(filePath.toFile().exists());
        Assert.assertThat(Files.getPosixFilePermissions(filePath), CoreMatchers.equalTo(AtomicFileWriterTest.DEFAULT_GIVEN_PERMISSIONS));
    }
}

