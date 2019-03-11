package water.fvec;


import org.junit.Test;
import water.TestUtil;


public class CategoricalTest extends TestUtil {
    @Test
    public void testCancelSparseCategoricals() {
        Frame frame = null;
        Vec stringVec = null;
        Vec catVec = null;
        try {
            frame = TestUtil.parse_test_file("smalldata/junit/iris.csv");
            Vec vec = frame.lastVec();
            assert (vec.naCnt()) == 0;
            stringVec = vec.toStringVec();
            assert (stringVec.naCnt()) == 0;
            catVec = stringVec.toCategoricalVec();
            assert (catVec.naCnt()) == 0;
        } finally {
            if (frame != null)
                frame.delete();

            if (stringVec != null)
                stringVec.remove();

            if (catVec != null)
                catVec.remove();

        }
    }
}

