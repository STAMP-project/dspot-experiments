package water.rapids.ast.prims.string;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import water.TestUtil;
import water.fvec.Frame;
import water.rapids.Rapids;
import water.rapids.Val;


@RunWith(Parameterized.class)
public class AstGrepTest extends TestUtil {
    @Parameterized.Parameter
    public String _regex;

    @Parameterized.Parameter(1)
    public int _ignoreCase;

    @Parameterized.Parameter(2)
    public int _col;

    @Parameterized.Parameter(3)
    public int _invert;

    @Parameterized.Parameter(4)
    public String _description;// not used


    @Test
    public void testGrep() throws Exception {
        final Frame data = makeTestFrame();
        Frame output = null;
        try {
            String rapids = ((((((("(tmp= tst (grep (cols data [" + (_col)) + "]) \"") + (_regex)) + "\" ") + (_ignoreCase)) + " ") + (_invert)) + " 0))";
            Val val = Rapids.exec(rapids);
            output = val.getFrame();
            int length = ((int) (output.vec(0).length()));
            int lastPos = -1;
            for (int i = 0; i < length; i++) {
                int pos = ((int) (output.vec(0).at8(i)));
                for (int j = lastPos + 1; j < pos; j++) {
                    Assert.assertEquals(0L, data.vec(0).at8(j));
                }
                Assert.assertEquals(1L, data.vec(0).at8(pos));
                lastPos = pos;
            }
        } finally {
            data.delete();
            if (output != null) {
                output.delete();
            }
        }
    }

    @Test
    public void testGrep_outputLogical() throws Exception {
        final Frame data = makeTestFrame();
        Frame output = null;
        try {
            String rapids = ((((((("(tmp= tst (grep (cols data [" + (_col)) + "]) \"") + (_regex)) + "\" ") + (_ignoreCase)) + " ") + (_invert)) + " 1))";
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

