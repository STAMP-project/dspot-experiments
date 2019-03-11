package water.udf;


import Vec.T_NUM;
import org.junit.Assert;
import org.junit.Test;
import water.DKV;
import water.MRTask;
import water.TestUtil;
import water.fvec.Chunk;
import water.fvec.Frame;
import water.fvec.NewChunk;
import water.util.FrameUtils;


public class JFuncTest extends TestUtil {
    @Test
    public void testFunc2Invocation() throws Exception {
        CFuncRef testFuncDef = JFuncUtils.loadTestFunc("func.key", TestCFunc2.class);
        try {
            ClassLoader cl = new DkvClassLoader(testFuncDef, JFuncUtils.getSkippingClassloader(JFuncTest.class.getClassLoader(), TestUtil.ar(testFuncDef.funcName)));
            Class testFuncKlazz = cl.loadClass(testFuncDef.funcName);
            Assert.assertEquals("Test func needs to be loaded via our test classloader", cl, testFuncKlazz.getClassLoader());
            Assert.assertNotEquals("Test func and actual code cannot be loaded by the same classloader", TestCFunc2.class.getClassLoader(), testFuncKlazz.getClassLoader());
            CFunc2 testFunc = ((CFunc2) (testFuncKlazz.newInstance()));
            CFunc2 codeFunc = new TestCFunc2();
            CBlock.CRow crow1 = JFuncTest.mockedRow(10, 1.0);
            CBlock.CRow crow2 = JFuncTest.mockedRow(5, 1.0);
            Assert.assertEquals("Test func call should return expected value", ((10 * 1.0) + (5 * 1.0)), testFunc.apply(crow1, crow2), 1.0E-10);
            Assert.assertEquals("Test func call should return the same value as the function defined in source code", codeFunc.apply(crow1, crow2), testFunc.apply(crow1, crow2), 1.0E-10);
        } finally {
            // Note: we cannot call key.remove() here (try it to see what happens)
            DKV.remove(testFuncDef.getKey());
        }
    }

    @Test
    public void testFunc1RemoteInvocation() throws Exception {
        final CFuncRef testFuncDef = JFuncUtils.loadTestFunc("id.func.key", TestCFunc1Id.class);
        final String testFuncName = testFuncDef.funcName;
        Frame inFr = null;
        Frame outFr = null;
        try {
            inFr = TestUtil.parse_test_file("./smalldata/logreg/prostate.csv");
            outFr = new CFunc1Task(testFuncDef, 1, 1) {
                @Override
                protected ClassLoader getParentClassloader() {
                    return JFuncUtils.getSkippingClassloader(super.getParentClassloader(), TestUtil.ar(testFuncName));
                }
            }.doAll(T_NUM, inFr).outputFrame();
            // Verify identity
            TestUtil.Cmp1 comparator = new TestUtil.Cmp1(1.0E-10).doAll(inFr.vec(1), outFr.vec(0));
            Assert.assertFalse("Identity function produces identity results", comparator._unequal);
        } finally {
            FrameUtils.delete(inFr, outFr);
            DKV.remove(testFuncDef.getKey());
        }
    }

    @Test
    public void testFunc2RemoteInvocation() throws Exception {
        CFuncRef testFuncDef = JFuncUtils.loadTestFunc("func.key", TestCFunc2.class);
        final String testFuncName = testFuncDef.funcName;
        Frame inFr = null;
        Frame outFr = null;
        Frame expFr = null;
        try {
            inFr = TestUtil.parse_test_file("./smalldata/logreg/prostate.csv");
            // Execute sum CAPSULE + AGE
            outFr = new CFunc2Task(testFuncDef, 1, 1, 2, 1) {
                @Override
                protected ClassLoader getParentClassloader() {
                    return JFuncUtils.getSkippingClassloader(super.getParentClassloader(), TestUtil.ar(testFuncName));
                }
            }.doAll(T_NUM, inFr).outputFrame();
            // Expected frame: x,y => sum(x) + sum(y)
            expFr = new MRTask() {
                @Override
                public void map(Chunk[] c, NewChunk nc) {
                    for (int i = 0; i < (c[0]._len); i++) {
                        nc.addNum(((c[0].atd(i)) + (c[1].atd(i))));
                    }
                }
            }.doAll(T_NUM, inFr.vec(1), inFr.vec(2)).outputFrame();
            // Verify identity
            TestUtil.Cmp1 comparator = new TestUtil.Cmp1(1.0E-10).doAll(expFr.vec(0), outFr.vec(0));
            Assert.assertFalse("Identity function produces identity results", comparator._unequal);
        } finally {
            FrameUtils.delete(inFr, outFr, expFr);
            inFr.delete();
            outFr.delete();
            DKV.remove(testFuncDef.getKey());
        }
    }
}

