package water.rapids.ast.prims.assign;


import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.Vec;
import water.rapids.Rapids;
import water.rapids.Val;
import water.rapids.vals.ValFrame;


public class AstRectangleConditionalAssignTest extends TestUtil {
    @Test
    public void testConditionalAssignNumber() {
        Frame fr = makeTestFrame();
        Vec expected = TestUtil.dvec(11.2, (-1), 33.6, (-1), 56.0);
        try {
            Val val = Rapids.exec("(tmp= py_1 (:= data -1 1 (== (cols_py data 4) \"a\")))");
            if (val instanceof ValFrame) {
                Frame fr2 = val.getFrame();
                TestUtil.assertVecEquals(expected, fr2.vec(1), 1.0E-5);
                fr2.remove();
            }
        } finally {
            fr.remove();
            expected.remove();
        }
    }

    @Test
    public void testConditionalAssignUUID() {
        UUID newUUID = UUID.randomUUID();
        Frame fr = makeTestFrame();
        Vec expected = TestUtil.uvec(new UUID(10, 1), newUUID, new UUID(30, 3), newUUID, new UUID(50, 5));
        try {
            Val val = Rapids.exec((("(tmp= py_1 (:= data \"" + (newUUID.toString())) + "\" 2 (== (cols_py data 4) \"a\")))"));
            if (val instanceof ValFrame) {
                Frame fr2 = val.getFrame();
                TestUtil.assertUUIDVecEquals(expected, fr2.vec(2));
                fr2.remove();
            }
        } finally {
            fr.remove();
            expected.remove();
        }
    }

    @Test
    public void testConditionalAssignString() {
        Frame fr = makeTestFrame();
        Vec expected = TestUtil.svec("row1", "tst", "row3", "tst", "row5");
        try {
            Val val = Rapids.exec("(tmp= py_1 (:= data \"tst\" 3 (== (cols_py data 4) \"a\")))");
            if (val instanceof ValFrame) {
                Frame fr2 = val.getFrame();
                TestUtil.assertStringVecEquals(expected, fr2.vec(3));
                fr2.remove();
            }
        } finally {
            fr.remove();
            expected.remove();
        }
    }

    @Test
    public void testConditionalAssignCategorical() {
        Frame fr = makeTestFrame();
        Vec expected = TestUtil.cvec(new String[]{ "a", "b" }, "b", "b", "b", "b", "b");
        try {
            Val val = Rapids.exec("(tmp= py_1 (:= data \"b\" 4 (== (cols_py data 4) \"a\")))");
            if (val instanceof ValFrame) {
                Frame fr2 = val.getFrame();
                TestUtil.assertCatVecEquals(expected, fr2.vec(4));
                fr2.remove();
            }
        } finally {
            fr.remove();
            expected.remove();
        }
    }

    @Test
    public void testConditionalAssignDense() {
        Frame fr = makeTestFrame();
        Vec expected = TestUtil.ivec((-1), (-1), (-1), (-1), (-1));
        try {
            Val val = Rapids.exec("(tmp= py_1 (:= data -1 2 (> (cols_py data 0) 0)))");
            if (val instanceof ValFrame) {
                Frame fr2 = val.getFrame();
                TestUtil.assertVecEquals(expected, fr2.vec(2), 1.0E-5);
                fr2.remove();
            }
        } finally {
            fr.remove();
            expected.remove();
        }
    }

    @Test
    public void testWrongTypeAssignString() {
        Frame fr = makeTestFrame();
        try {
            Rapids.exec("(tmp= py_1 (:= data \"tst\" 1 (== (cols_py data 4) \"a\")))");
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Cannot assign value tst into a vector of type Numeric.", e.getMessage());
        } finally {
            fr.remove();
        }
    }

    @Test
    public void testWrongTypeAssignNum() {
        Frame fr = makeTestFrame();
        try {
            Rapids.exec("(tmp= py_1 (:= data 9 3 (== (cols_py data 4) \"a\")))");
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Cannot assign value 9.0 into a vector of type String.", e.getMessage());
        } finally {
            fr.remove();
        }
    }

    @Test
    public void testWrongCategoricalAssign() {
        Frame fr = makeTestFrame();
        try {
            Rapids.exec("(tmp= py_1 (:= data \"c\" 4 (== (cols_py data 4) \"a\")))");
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Cannot assign value c into a vector of type Enum.", e.getMessage());
        } finally {
            fr.remove();
        }
    }
}

