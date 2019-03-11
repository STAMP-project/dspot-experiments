/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.datasource;


import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class FileDataSourceTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private static final String SOMEFILE_DIR = "path/";

    private static final String SOMEFILE_TXT = "somefile.txt";

    private static final String SOMEFILE_TXT_FULL_PATH = (FileDataSourceTest.SOMEFILE_DIR) + (FileDataSourceTest.SOMEFILE_TXT);

    private FileDataSource ds;

    private File someFile;

    private File someFolder;

    @Test
    public void testShortNamesSingleFile() {
        Assert.assertEquals(FileDataSourceTest.SOMEFILE_TXT, ds.getNiceFileName(true, someFile.getAbsolutePath()));
    }

    @Test
    public void testShortNamesSingleDir() {
        Assert.assertEquals(FileDataSourceTest.SOMEFILE_TXT, ds.getNiceFileName(true, someFolder.getAbsolutePath()));
    }

    @Test
    public void testShortNamesNullBase() {
        Assert.assertEquals(FileDataSourceTest.SOMEFILE_TXT, ds.getNiceFileName(true, null));
    }

    @Test
    public void testShortNamesCommaSeparatedDirs() {
        // use 2 dirs, one relative (similar, but not resolving to the same location) and one absolute
        Assert.assertEquals(FileDataSourceTest.SOMEFILE_TXT, ds.getNiceFileName(true, (((FileDataSourceTest.SOMEFILE_DIR) + ",") + (someFolder.getAbsolutePath()))));
    }

    @Test
    public void testShortNamesCommaSeparatedFiles() {
        // use 2 files, one relative (similar, but not resolving to the same location) and one absolute
        Assert.assertEquals(FileDataSourceTest.SOMEFILE_TXT, ds.getNiceFileName(true, (((FileDataSourceTest.SOMEFILE_TXT_FULL_PATH) + ",") + (someFile.getAbsolutePath()))));
    }

    @Test
    public void testShortNamesCommaSeparatedMixed() {
        // use a file and a dir, one relative (similar, but not resolving to the same location) and one absolute
        Assert.assertEquals(FileDataSourceTest.SOMEFILE_TXT, ds.getNiceFileName(true, (((FileDataSourceTest.SOMEFILE_TXT_FULL_PATH) + ",") + (someFolder.getAbsolutePath()))));
    }

    @Test
    public void testLongNamesSingleFile() throws IOException {
        Assert.assertEquals(someFile.getCanonicalFile().getAbsolutePath(), ds.getNiceFileName(false, someFile.getAbsolutePath()));
    }

    @Test
    public void testLongNamesSingleDir() throws IOException {
        Assert.assertEquals(someFile.getCanonicalFile().getAbsolutePath(), ds.getNiceFileName(false, someFolder.getAbsolutePath()));
    }

    @Test
    public void testLongNamesNullBase() throws IOException {
        Assert.assertEquals(someFile.getCanonicalFile().getAbsolutePath(), ds.getNiceFileName(false, null));
    }

    @Test
    public void testLongNamesCommaSeparatedDirs() throws IOException {
        // use 2 dirs, one relative (similar, but not resolving to the same location) and one absolute
        Assert.assertEquals(someFile.getCanonicalFile().getAbsolutePath(), ds.getNiceFileName(false, (((FileDataSourceTest.SOMEFILE_DIR) + ",") + (someFolder.getAbsolutePath()))));
    }

    @Test
    public void testLongNamesCommaSeparatedFiles() throws IOException {
        // use 2 files, one relative (similar, but not resolving to the same location) and one absolute
        Assert.assertEquals(someFile.getCanonicalFile().getAbsolutePath(), ds.getNiceFileName(false, (((FileDataSourceTest.SOMEFILE_TXT_FULL_PATH) + ",") + (someFile.getAbsolutePath()))));
    }

    @Test
    public void testLongNamesCommaSeparatedMixed() throws IOException {
        // use a file and a dir, one relative (similar, but not resolving to the same location) and one absolute
        Assert.assertEquals(someFile.getCanonicalFile().getAbsolutePath(), ds.getNiceFileName(false, (((FileDataSourceTest.SOMEFILE_TXT_FULL_PATH) + ",") + (someFolder.getAbsolutePath()))));
    }
}

