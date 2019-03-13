package water.rapids.ast.prims.mungers;


import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;
import water.rapids.Rapids;
import water.rapids.Val;
import water.util.ArrayUtils;


/**
 *
 */
public class AstNaOmitTest extends TestUtil {
    /**
     * Test written by Nidhi to test that NaOmit actaully remove the rows with NAs in them.
     */
    @Test
    public void TestNaOmit() {
        Frame f = null;
        Frame fNew = null;
        try {
            f = ArrayUtils.frame(TestUtil.ar("A", "B"), TestUtil.ard(1.0, Double.NaN), TestUtil.ard(2.0, 23.3), TestUtil.ard(3.0, 3.3), TestUtil.ard(Double.NaN, 3.3));
            String x = String.format("(na.omit %s)", f._key);
            Val res = Rapids.exec(x);
            // make the call the remove NAs in frame
            fNew = res.getFrame();
            // get frame without any NAs
            Assert.assertEquals(((f.numRows()) - (fNew.numRows())), 2);// 2 rows of NAs removed.

        } finally {
            if (f != null)
                f.delete();

            if (fNew != null)
                fNew.delete();

        }
    }
}

