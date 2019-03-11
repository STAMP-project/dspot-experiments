package org.testcontainers.utility;


import java.nio.file.Path;
import java.util.function.Consumer;
import lombok.Cleanup;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.junit.Test;


public class MountableFileTest {
    private static final int TEST_FILE_MODE = 346;

    private static final int BASE_FILE_MODE = 32768;

    private static final int BASE_DIR_MODE = 16384;

    @Test
    public void forClasspathResource() throws Exception {
        final MountableFile mountableFile = MountableFile.forClasspathResource("mappable-resource/test-resource.txt");
        performChecks(mountableFile);
    }

    @Test
    public void forClasspathResourceWithAbsolutePath() throws Exception {
        final MountableFile mountableFile = MountableFile.forClasspathResource("/mappable-resource/test-resource.txt");
        performChecks(mountableFile);
    }

    @Test
    public void forClasspathResourceFromJar() throws Exception {
        final MountableFile mountableFile = MountableFile.forClasspathResource("META-INF/dummy_unique_name.txt");
        performChecks(mountableFile);
    }

    @Test
    public void forClasspathResourceFromJarWithAbsolutePath() throws Exception {
        final MountableFile mountableFile = MountableFile.forClasspathResource("/META-INF/dummy_unique_name.txt");
        performChecks(mountableFile);
    }

    @Test
    public void forHostPath() throws Exception {
        final Path file = createTempFile("somepath");
        final MountableFile mountableFile = MountableFile.forHostPath(file.toString());
        performChecks(mountableFile);
    }

    @Test
    public void forHostPathWithSpaces() throws Exception {
        final Path file = createTempFile("some path");
        final MountableFile mountableFile = MountableFile.forHostPath(file.toString());
        performChecks(mountableFile);
        assertTrue("The resolved path contains the original space", mountableFile.getResolvedPath().contains(" "));
        assertFalse("The resolved path does not contain an escaped space", mountableFile.getResolvedPath().contains("\\ "));
    }

    @Test
    public void forHostPathWithPlus() throws Exception {
        final Path file = createTempFile("some+path");
        final MountableFile mountableFile = MountableFile.forHostPath(file.toString());
        performChecks(mountableFile);
        assertTrue("The resolved path contains the original space", mountableFile.getResolvedPath().contains("+"));
        assertFalse("The resolved path does not contain an escaped space", mountableFile.getResolvedPath().contains(" "));
    }

    @Test
    public void forClasspathResourceWithPermission() throws Exception {
        final MountableFile mountableFile = MountableFile.forClasspathResource("mappable-resource/test-resource.txt", MountableFileTest.TEST_FILE_MODE);
        performChecks(mountableFile);
        assertEquals("Valid file mode.", ((MountableFileTest.BASE_FILE_MODE) | (MountableFileTest.TEST_FILE_MODE)), mountableFile.getFileMode());
    }

    @Test
    public void forHostFilePathWithPermission() throws Exception {
        final Path file = createTempFile("somepath");
        final MountableFile mountableFile = MountableFile.forHostPath(file.toString(), MountableFileTest.TEST_FILE_MODE);
        performChecks(mountableFile);
        assertEquals("Valid file mode.", ((MountableFileTest.BASE_FILE_MODE) | (MountableFileTest.TEST_FILE_MODE)), mountableFile.getFileMode());
    }

    @Test
    public void forHostDirPathWithPermission() throws Exception {
        final Path dir = createTempDir();
        final MountableFile mountableFile = MountableFile.forHostPath(dir.toString(), MountableFileTest.TEST_FILE_MODE);
        performChecks(mountableFile);
        assertEquals("Valid dir mode.", ((MountableFileTest.BASE_DIR_MODE) | (MountableFileTest.TEST_FILE_MODE)), mountableFile.getFileMode());
    }

    @Test
    public void noTrailingSlashesInTarEntryNames() throws Exception {
        final MountableFile mountableFile = MountableFile.forClasspathResource("mappable-resource/test-resource.txt");
        @Cleanup
        final TarArchiveInputStream tais = intoTarArchive(( taos) -> {
            mountableFile.transferTo(taos, "/some/path.txt");
            mountableFile.transferTo(taos, "/path.txt");
            mountableFile.transferTo(taos, "path.txt");
        });
        ArchiveEntry entry;
        while ((entry = tais.getNextEntry()) != null) {
            assertFalse("no entries should have a trailing slash", entry.getName().endsWith("/"));
        } 
    }
}

