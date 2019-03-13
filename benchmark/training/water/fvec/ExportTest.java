package water.fvec;


import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import water.TestUtil;
import water.util.Log;


public class ExportTest extends TestUtil {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void testExport() throws IOException {
        Frame fr = TestUtil.parse_test_file("smalldata/airlines/airlineUUID.csv");
        Key rebalancedKey = Key.make("rebalanced");
        Frame rebalanced = null;
        Frame imported = null;
        int[] partSpec = new int[]{ 1, 4, 7, 30, -1 };
        int[] expPart = new int[]{ 1, 4, 6, 17, -1 };
        for (int i = 0; i < (partSpec.length); i++) {
            Log.info((("Testing export to " + (partSpec[i])) + " files."));
            try {
                int parts = partSpec[i];
                Scope.enter();
                rebalanced = ExportTest.rebalance(fr, rebalancedKey, 17);
                File folder = tmpFolder.newFolder(("export_" + parts));
                File target = (parts == 1) ? new File(folder, "data.csv") : folder;
                Log.info((((("Should output #" + (expPart[i])) + " part files to ") + (target.getPath())) + "."));
                Frame.export(rebalanced, target.getPath(), "export", false, parts).get();
                // check the number of produced part files (only if the number was given)
                if ((expPart[i]) != (-1)) {
                    Assert.assertEquals(expPart[i], folder.listFiles().length);
                    if (parts == 1) {
                        Assert.assertTrue(target.exists());
                    } else {
                        for (int j = 0; j < (expPart[i]); j++) {
                            String suffix = (j < 10) ? "0000" + j : "000" + j;
                            Assert.assertTrue(new File(folder, ("part-m-" + suffix)).exists());
                        }
                    }
                }
                Assert.assertTrue(target.exists());
                imported = ExportTest.parseFolder(folder);
                Assert.assertEquals(fr.numRows(), imported.numRows());
                Assert.assertTrue(TestUtil.isBitIdentical(fr, imported));
            } finally {
                if (rebalanced != null)
                    rebalanced.delete();

                if (imported != null)
                    imported.delete();

                Scope.exit();
            }
        }
        fr.delete();
    }
}

