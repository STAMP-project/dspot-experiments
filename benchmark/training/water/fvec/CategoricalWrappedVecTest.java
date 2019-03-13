package water.fvec;


import org.junit.Assert;
import org.junit.Test;
import water.Scope;
import water.TestUtil;


public class CategoricalWrappedVecTest extends TestUtil {
    @Test
    public void testAdaptToDoubleDomain() {
        Vec v0 = Vec.makeCon(Vec.newKey(), 3.14, 2.7, 0.33333, (-123.456), 1.65E9, (-500.5));
        Vec v1 = v0.adaptTo(new String[]{ "0.270e1", "-123.456" });
        Assert.assertArrayEquals(new String[]{ "0.270e1", "-123.456", "-500.5", "0.33333", "3.14", "1.65E9" }, v1.domain());
        Assert.assertEquals(v1.at8(0), 4);
        Assert.assertEquals(v1.at8(1), 0);
        Assert.assertEquals(v1.at8(2), 3);
        Assert.assertEquals(v1.at8(3), 1);
        Assert.assertEquals(v1.at8(4), 5);
        Assert.assertEquals(v1.at8(5), 2);
        v0.remove();
        v1.remove();
    }

    // Need to move test files over
    @Test
    public void testAdaptTo() {
        Scope.enter();
        Frame v1 = null;
        Frame v2 = null;
        try {
            v1 = TestUtil.parse_test_file("smalldata/junit/mixcat_train.csv");
            v2 = TestUtil.parse_test_file("smalldata/junit/mixcat_test.csv");
            CategoricalWrappedVec vv = ((CategoricalWrappedVec) (v2.vecs()[0].adaptTo(v1.vecs()[0].domain())));
            Assert.assertArrayEquals("Mapping differs", new int[]{ 0, 1, 3 }, vv._map);
            Assert.assertArrayEquals("Mapping differs", new String[]{ "A", "B", "C", "D" }, vv.domain());
            vv.remove();
        } finally {
            if (v1 != null)
                v1.delete();

            if (v2 != null)
                v2.delete();

            Scope.exit();
        }
    }

    /**
     * Verifies that {@link CategoricalWrappedVec#computeMap(String[], String[])} returns
     *  correct values.
     */
    @Test
    public void testModelMappingCall() {
        Scope.enter();
        CategoricalWrappedVecTest.testModelMapping(TestUtil.ar("A", "B", "C"), TestUtil.ar("A", "B", "C"), TestUtil.ari(0, 1, 2));
        CategoricalWrappedVecTest.testModelMapping(TestUtil.ar("A", "B", "C"), TestUtil.ar("B", "C"), TestUtil.ari(1, 2));
        CategoricalWrappedVecTest.testModelMapping(TestUtil.ar("A", "B", "C"), TestUtil.ar("B"), TestUtil.ari(1));
        CategoricalWrappedVecTest.testModelMapping(TestUtil.ar("A", "B", "C"), TestUtil.ar("A", "B", "C", "D"), TestUtil.ari(0, 1, 2, 3));
        CategoricalWrappedVecTest.testModelMapping(TestUtil.ar("A", "B", "C"), TestUtil.ar("B", "C", "D"), TestUtil.ari(1, 2, 3));
        CategoricalWrappedVecTest.testModelMapping(TestUtil.ar("A", "B", "C"), TestUtil.ar("B", "D"), TestUtil.ari(1, 3));
        Scope.exit();
    }
}

