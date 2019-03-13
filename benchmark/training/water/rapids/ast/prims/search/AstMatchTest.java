package water.rapids.ast.prims.search;


import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;
import water.rapids.Rapids;
import water.rapids.Val;


public class AstMatchTest extends TestUtil {
    @Test
    public void testMatchNumList() throws Exception {
        final Frame data = makeTestFrame();
        Frame output = null;
        try {
            String numList = idx(data.vec(2), "cB", "cC", "cD");
            String rapids = ("(tmp= tst (match (cols data [2]) [" + numList) + "] -1 ignored))";
            Val val = Rapids.exec(rapids);
            output = val.getFrame();
            TestUtil.assertVecEquals(data.vec(0), output.vec(0), 0.0);
        } finally {
            data.delete();
            if (output != null) {
                output.delete();
            }
        }
    }

    @Test
    public void testMatchCatList() throws Exception {
        final Frame data = makeTestFrame();
        Frame output = null;
        try {
            String rapids = "(tmp= tst (match (cols data [2]) [\"cD\",\"cC\",\"cB\"] -1 ignored))";
            Val val = Rapids.exec(rapids);
            output = val.getFrame();
            TestUtil.assertVecEquals(data.vec(0), output.vec(0), 0.0);
        } finally {
            data.delete();
            if (output != null) {
                output.delete();
            }
        }
    }

    @Test
    public void testMatchStrList() throws Exception {
        final Frame data = makeTestFrame();
        Frame output = null;
        try {
            String rapids = "(tmp= tst (match (cols data [1]) [\"sD\",\"sC\",\"sB\"] -1 ignored))";
            Val val = Rapids.exec(rapids);
            output = val.getFrame();
            TestUtil.assertVecEquals(data.vec(0), output.vec(0), 0.0);
        } finally {
            data.delete();
            if (output != null) {
                output.delete();
            }
        }
    }
}

