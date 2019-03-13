package hudson.util.io;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.junit.Assert;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;


public class ZipArchiverTest {
    private static final Logger LOGGER = Logger.getLogger(ZipArchiverTest.class.getName());

    private File tmpDir;

    @Issue("JENKINS-9942")
    @Test
    public void backwardsSlashesOnWindows() {
        // create foo/bar/baz/Test.txt
        File tmpFile = null;
        try {
            File baz = new File(new File(new File(tmpDir, "foo"), "bar"), "baz");
            baz.mkdirs();
            tmpFile = new File(baz, "Test.txt");
            tmpFile.createNewFile();
        } catch (IOException e) {
            fail("unable to prepare source directory for zipping", e);
        }
        // a file to store the zip archive in
        File zipFile = null;
        // create zip from tmpDir
        ZipArchiver archiver = null;
        try {
            zipFile = File.createTempFile("test", ".zip");
            archiver = new ZipArchiver(Files.newOutputStream(zipFile.toPath()));
            archiver.visit(tmpFile, "foo\\bar\\baz\\Test.txt");
        } catch (Exception e) {
            fail("exception driving ZipArchiver", e);
        } finally {
            if (archiver != null) {
                try {
                    archiver.close();
                } catch (IOException e) {
                    // ignored
                }
            }
        }
        // examine zip contents and assert that none of the entry names (paths) have
        // back-slashes ("\")
        String zipEntryName = null;
        ZipFile zipFileVerify = null;
        try {
            zipFileVerify = new ZipFile(zipFile);
            zipEntryName = ((ZipEntry) (zipFileVerify.entries().nextElement())).getName();
        } catch (Exception e) {
            fail("failure enumerating zip entries", e);
        } finally {
            if (zipFileVerify != null) {
                try {
                    zipFileVerify.close();
                } catch (IOException e) {
                    // ignored
                }
            }
        }
        Assert.assertEquals("foo/bar/baz/Test.txt", zipEntryName);
    }

    @Test
    public void huge64bitFile() {
        // create huge64bitFileTest.txt
        File hugeFile = new File(tmpDir, "huge64bitFileTest.txt");
        try {
            RandomAccessFile largeFile = new RandomAccessFile(hugeFile, "rw");
            largeFile.setLength(((((4 * 1024) * 1024) * 1024) + 2));
        } catch (IOException e) {
            /* We probably don't have enough free disk space
            That's ok, we'll skip this test...
             */
            ZipArchiverTest.LOGGER.log(Level.SEVERE, "Couldn't set up huge file for huge64bitFile test", e);
            return;
        }
        // a file to store the zip archive in
        File zipFile = null;
        // create zip from tmpDir
        ZipArchiver archiver = null;
        try {
            zipFile = File.createTempFile("test", ".zip");
            archiver = new ZipArchiver(Files.newOutputStream(zipFile.toPath()));
            archiver.visit(hugeFile, "huge64bitFileTest.txt");
        } catch (Exception e) {
            fail("exception driving ZipArchiver", e);
        } finally {
            if (archiver != null) {
                try {
                    archiver.close();
                } catch (IOException e) {
                    // ignored
                }
            }
        }
        // examine zip contents and assert that there's an item there...
        String zipEntryName = null;
        ZipFile zipFileVerify = null;
        try {
            zipFileVerify = new ZipFile(zipFile);
            zipEntryName = ((ZipEntry) (zipFileVerify.entries().nextElement())).getName();
        } catch (Exception e) {
            fail("failure enumerating zip entries", e);
        } finally {
            if (zipFileVerify != null) {
                try {
                    zipFileVerify.close();
                } catch (IOException e) {
                    // ignored
                }
            }
        }
        Assert.assertEquals("huge64bitFileTest.txt", zipEntryName);
    }
}

