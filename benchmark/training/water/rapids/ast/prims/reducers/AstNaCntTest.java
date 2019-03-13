package water.rapids.ast.prims.reducers;


import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;
import water.rapids.Rapids;
import water.rapids.Val;
import water.util.ArrayUtils;


/**
 * Test the AstNaCnt.java class
 */
public class AstNaCntTest extends TestUtil {
    // --------------------------------------------------------------------------------------------------------------------
    // Tests
    // --------------------------------------------------------------------------------------------------------------------
    @Test
    public void testAstNaCnt() {
        Frame f = null;
        try {
            f = ArrayUtils.frame(TestUtil.ar("A", "B"), TestUtil.ard(1.0, Double.NaN), TestUtil.ard(2.0, 23.3), TestUtil.ard(3.0, 3.3), TestUtil.ard(Double.NaN, 3.3));
            String x = String.format("(naCnt %s)", f._key);
            Val res = Rapids.exec(x);// make the call to count number of NAs in frame

            // get frame without any NAs
            // assertEquals(f.numRows()-fNew.numRows() ,2);  // 2 rows of NAs removed.
            double[] naCnts = res.getNums();
            double totalNacnts = 0;
            for (int index = 0; index < (f.numCols()); index++) {
                totalNacnts += naCnts[index];
            }
            Assert.assertEquals(((int) (totalNacnts)), 2);
        } finally {
            if (f != null)
                f.delete();

        }
    }
}

