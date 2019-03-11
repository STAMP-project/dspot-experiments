package water.rapids.ast.prims.string;


import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.Vec;
import water.rapids.Rapids;
import water.rapids.vals.ValFrame;


public class AstTokenizeTest extends TestUtil {
    @Test
    public void testTokenize() {
        Frame fr = makeTestFrame();
        Vec expected = TestUtil.svec("Foot", "on", "the", "pedal", "Never", "ever", "false", "metal", null, "Engine", "running", "hotter", "than", "a", "boiling", "kettle", "My", "job", "ain't", "a", "job", null, "It's", "a", "damn", "good", "time", "City", "to", "city", "I'm", "running", "my", "rhymes", null);
        Frame res = null;
        try {
            ValFrame val = ((ValFrame) (Rapids.exec("(tmp= py_1 (tokenize data \"\\\\s\"))")));
            res = val.getFrame();
            Vec actual = res.anyVec();
            TestUtil.assertStringVecEquals(expected, actual);
        } finally {
            fr.remove();
            expected.remove();
            if (res != null)
                res.remove();

        }
    }
}

