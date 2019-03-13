package water.rapids;


import org.junit.Test;
import water.Key;
import water.Keyed;
import water.TestUtil;
import water.fvec.Frame;


public class TableTest extends TestUtil {
    @Test
    public void testBasic() {
        Frame fr = null;
        String tree = "(table (cols_py hex [\"AGE\" \"RACE\"]) FALSE)";
        try {
            fr = chkTree(tree, "smalldata/prostate/prostate.csv");
        } finally {
            if (fr != null)
                fr.delete();

            Keyed.remove(Key.make("hex"));
        }
    }

    @Test
    public void testBasicDdply() {
        Frame fr = null;
        String tree = "(table (cols_py hex [\"VOL\"]) FALSE)";
        try {
            fr = chkTree(tree, "smalldata/prostate/prostate.csv");
        } finally {
            if (fr != null)
                fr.delete();

            Keyed.remove(Key.make("hex"));
        }
    }
}

