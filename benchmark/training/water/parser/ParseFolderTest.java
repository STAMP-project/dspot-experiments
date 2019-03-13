package water.parser;


import ParseSetup.GUESS_HEADER;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import water.Key;
import water.Scope;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.NFSFileVec;
import water.util.FileUtils;


public class ParseFolderTest extends TestUtil {
    @Test
    public void testProstate() {
        Frame k1 = null;
        Frame k2 = null;
        try {
            k2 = parse_test_folder("smalldata/junit/parse_folder");
            k1 = TestUtil.parse_test_file("smalldata/junit/parse_folder_gold.csv");
            Assert.assertTrue("parsed values do not match!", TestUtil.isBitIdentical(k1, k2));
        } finally {
            if (k1 != null)
                k1.delete();

            if (k2 != null)
                k2.delete();

        }
    }

    // test skipped some columns
    @Test
    public void testFolderSkipColumnsSome() {
        Scope.enter();
        Frame k1 = null;
        Frame k2 = null;
        Frame k3 = null;
        Frame k4 = null;
        int[] skippedColumns = new int[]{ 0, 1 };
        try {
            k1 = TestUtil.parse_test_file("smalldata/junit/parse_folder_gold.csv");
            k2 = parse_test_folder("smalldata/junit/parse_folder");
            Scope.track(k1, k2);
            Assert.assertTrue("parsed values do not match!", TestUtil.isBitIdentical(k1, k2));
            k3 = TestUtil.parse_test_file("smalldata/junit/parse_folder_gold.csv", skippedColumns);
            k4 = parse_test_folder("smalldata/junit/parse_folder", skippedColumns);
            Scope.track(k3, k4);
            Assert.assertTrue("parsed values do not match!", TestUtil.isBitIdentical(k3, k4));
        } finally {
            Scope.exit();
        }
    }

    // test skipped some columns
    @Test
    public void testFolderSkipColumnsAll() {
        Scope.enter();
        Frame k1 = null;
        int[] skippedColumns = new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8 };
        try {
            k1 = parse_test_folder("smalldata/junit/parse_folder", skippedColumns);
            Assert.assertTrue("Error:  Should have thrown an exception but did not!", (1 == 2));
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testSameFile() {
        File f = FileUtils.locateFile("smalldata/iris/iris_wheader.csv");
        NFSFileVec nfs1 = NFSFileVec.make(f);
        NFSFileVec nfs2 = NFSFileVec.make(f);
        Frame fr = null;
        try {
            fr = /* delete on done */
            ParseDataset.parse(Key.make(), new Key[]{ nfs1._key, nfs2._key }, false, false, GUESS_HEADER);
        } finally {
            if (fr != null)
                fr.delete();

            if (nfs1 != null)
                nfs1.remove();

        }
    }
}

