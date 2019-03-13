package org.junit.rules;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;


public class TempFolderRuleTest {
    private static File[] createdFiles = new File[20];

    public static class HasTempFolder {
        @Rule
        public TemporaryFolder folder = new TemporaryFolder();

        @Test
        public void testUsingTempFolder() throws IOException {
            TempFolderRuleTest.createdFiles[0] = folder.newFile("myfile.txt");
            Assert.assertTrue(TempFolderRuleTest.createdFiles[0].exists());
        }

        @Test
        public void testTempFolderLocation() throws IOException {
            File folderRoot = folder.getRoot();
            String tmpRoot = System.getProperty("java.io.tmpdir");
            Assert.assertTrue(folderRoot.toString().startsWith(tmpRoot));
        }
    }

    @Test
    public void tempFolderIsDeleted() {
        Assert.assertThat(PrintableResult.testResult(TempFolderRuleTest.HasTempFolder.class), ResultMatchers.isSuccessful());
        Assert.assertFalse(TempFolderRuleTest.createdFiles[0].exists());
    }

    public static class CreatesSubFolder {
        @Rule
        public TemporaryFolder folder = new TemporaryFolder();

        @Test
        public void testUsingTempFolderStringReflection() throws Exception {
            String subfolder = "subfolder";
            String filename = "a.txt";
            // force usage of folder.newFolder(String),
            // check is available and works, to avoid a potential NoSuchMethodError with non-recompiled code.
            Method method = folder.getClass().getMethod("newFolder", new Class<?>[]{ String.class });
            TempFolderRuleTest.createdFiles[0] = ((File) (method.invoke(folder, subfolder)));
            new File(TempFolderRuleTest.createdFiles[0], filename).createNewFile();
            File expectedFile = new File(folder.getRoot(), join(subfolder, filename));
            Assert.assertTrue(expectedFile.exists());
        }

        @Test
        public void testUsingTempFolderString() throws IOException {
            String subfolder = "subfolder";
            String filename = "a.txt";
            // this uses newFolder(String), ensure that a single String works
            TempFolderRuleTest.createdFiles[0] = folder.newFolder(subfolder);
            new File(TempFolderRuleTest.createdFiles[0], filename).createNewFile();
            File expectedFile = new File(folder.getRoot(), join(subfolder, filename));
            Assert.assertTrue(expectedFile.exists());
        }

        @Test
        public void testUsingTempTreeFolders() throws IOException {
            String subfolder = "subfolder";
            String anotherfolder = "anotherfolder";
            String filename = "a.txt";
            TempFolderRuleTest.createdFiles[0] = folder.newFolder(subfolder, anotherfolder);
            new File(TempFolderRuleTest.createdFiles[0], filename).createNewFile();
            File expectedFile = new File(folder.getRoot(), join(subfolder, anotherfolder, filename));
            Assert.assertTrue(expectedFile.exists());
        }

        private String join(String... folderNames) {
            StringBuilder path = new StringBuilder();
            for (String folderName : folderNames) {
                path.append(File.separator).append(folderName);
            }
            return path.toString();
        }
    }

    @Test
    public void subFolderIsDeleted() {
        Assert.assertThat(PrintableResult.testResult(TempFolderRuleTest.CreatesSubFolder.class), ResultMatchers.isSuccessful());
        Assert.assertFalse(TempFolderRuleTest.createdFiles[0].exists());
    }

    public static class CreatesRandomSubFolders {
        @Rule
        public TemporaryFolder folder = new TemporaryFolder();

        @Test
        public void testUsingRandomTempFolders() throws IOException {
            for (int i = 0; i < 20; i++) {
                File newFolder = folder.newFolder();
                Assert.assertThat(Arrays.asList(TempFolderRuleTest.createdFiles), IsNot.not(CoreMatchers.hasItem(newFolder)));
                TempFolderRuleTest.createdFiles[i] = newFolder;
                new File(newFolder, "a.txt").createNewFile();
                Assert.assertTrue(newFolder.exists());
            }
        }
    }

    @Test
    public void randomSubFoldersAreDeleted() {
        Assert.assertThat(PrintableResult.testResult(TempFolderRuleTest.CreatesRandomSubFolders.class), ResultMatchers.isSuccessful());
        for (File f : TempFolderRuleTest.createdFiles) {
            Assert.assertFalse(f.exists());
        }
    }

    public static class CreatesRandomFiles {
        @Rule
        public TemporaryFolder folder = new TemporaryFolder();

        @Test
        public void testUsingRandomTempFiles() throws IOException {
            for (int i = 0; i < 20; i++) {
                File newFile = folder.newFile();
                Assert.assertThat(Arrays.asList(TempFolderRuleTest.createdFiles), IsNot.not(CoreMatchers.hasItem(newFile)));
                TempFolderRuleTest.createdFiles[i] = newFile;
                Assert.assertTrue(newFile.exists());
            }
        }
    }

    @Test
    public void randomFilesAreDeleted() {
        Assert.assertThat(PrintableResult.testResult(TempFolderRuleTest.CreatesRandomFiles.class), ResultMatchers.isSuccessful());
        for (File f : TempFolderRuleTest.createdFiles) {
            Assert.assertFalse(f.exists());
        }
    }

    @Test
    public void recursiveDeleteFolderWithOneElement() throws IOException {
        TemporaryFolder folder = new TemporaryFolder();
        folder.create();
        File file = folder.newFile("a");
        folder.delete();
        Assert.assertFalse(file.exists());
        Assert.assertFalse(folder.getRoot().exists());
    }

    @Test
    public void recursiveDeleteFolderWithOneRandomElement() throws IOException {
        TemporaryFolder folder = new TemporaryFolder();
        folder.create();
        File file = folder.newFile();
        folder.delete();
        Assert.assertFalse(file.exists());
        Assert.assertFalse(folder.getRoot().exists());
    }

    @Test
    public void recursiveDeleteFolderWithZeroElements() throws IOException {
        TemporaryFolder folder = new TemporaryFolder();
        folder.create();
        folder.delete();
        Assert.assertFalse(folder.getRoot().exists());
    }

    public static class NameClashes {
        @Rule
        public TemporaryFolder folder = new TemporaryFolder();

        @Test
        public void fileWithFileClash() throws IOException {
            folder.newFile("something.txt");
            folder.newFile("something.txt");
        }

        @Test
        public void fileWithFolderTest() throws IOException {
            folder.newFolder("dummy");
            folder.newFile("dummy");
        }
    }

    @Test
    public void nameClashesResultInTestFailures() {
        Assert.assertThat(PrintableResult.testResult(TempFolderRuleTest.NameClashes.class), ResultMatchers.failureCountIs(2));
    }

    private static final String GET_ROOT_DUMMY = "dummy-getRoot";

    private static final String NEW_FILE_DUMMY = "dummy-newFile";

    private static final String NEW_FOLDER_DUMMY = "dummy-newFolder";

    public static class IncorrectUsage {
        public TemporaryFolder folder = new TemporaryFolder();

        @Test
        public void testGetRoot() throws IOException {
            new File(folder.getRoot(), TempFolderRuleTest.GET_ROOT_DUMMY).createNewFile();
        }

        @Test
        public void testNewFile() throws IOException {
            folder.newFile(TempFolderRuleTest.NEW_FILE_DUMMY);
        }

        @Test
        public void testNewFolder() throws IOException {
            folder.newFolder(TempFolderRuleTest.NEW_FOLDER_DUMMY);
        }
    }

    @Test
    public void incorrectUsageWithoutApplyingTheRuleShouldNotPolluteTheCurrentWorkingDirectory() {
        Assert.assertThat(PrintableResult.testResult(TempFolderRuleTest.IncorrectUsage.class), ResultMatchers.failureCountIs(3));
        Assert.assertFalse("getRoot should have failed early", new File(TempFolderRuleTest.GET_ROOT_DUMMY).exists());
        Assert.assertFalse("newFile should have failed early", new File(TempFolderRuleTest.NEW_FILE_DUMMY).exists());
        Assert.assertFalse("newFolder should have failed early", new File(TempFolderRuleTest.NEW_FOLDER_DUMMY).exists());
    }
}

