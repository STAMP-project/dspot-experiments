package water;


import H2O.H2OCountedCompleter;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import water.util.ArrayUtils;


/**
 * Created by tomas on 11/6/16.
 */
public class LocalMRTest extends TestUtil {
    private static class MrFunTest1 extends MrFun<LocalMRTest.MrFunTest1> {
        int _exId;

        public int[] _val;

        public MrFunTest1(int exId) {
            _exId = exId;
        }

        public void map(int id) {
            if ((_val) == null)
                _val = new int[]{ id };
            else
                _val = ArrayUtils.append(_val, id);

        }

        public void reduce(LocalMRTest.MrFunTest1 other) {
            if ((_val) == null)
                _val = other._val;
            else
                if ((other._val) != null)
                    _val = ArrayUtils.sortedMerge(_val, other._val);


        }
    }

    @Test
    public void testNormal() {
        testCnt(1);
        testCnt(2);
        testCnt(3);
        testCnt(4);
        testCnt(5);
        testCnt(10);
        testCnt(15);
        testCnt(53);
        testCnt(64);
        testCnt(100);
        testCnt(111);
    }

    @Test
    public void testIAE() {
        try {
            testCnt(0);
            Assert.assertTrue("should've thrown IAE", false);
        } catch (IllegalArgumentException e) {
        }
        try {
            testCnt((-1));
            Assert.assertTrue("should've thrown IAE", false);
        } catch (IllegalArgumentException e) {
        }
    }

    private static class TestException extends RuntimeException {}

    private static class MrFunTest2 extends MrFun<LocalMRTest.MrFunTest2> {
        final int exId;

        String s;

        AtomicInteger _active;

        public MrFunTest2(int exId, AtomicInteger activeCnt) {
            this.exId = exId;
            _active = activeCnt;
        }

        @Override
        protected void map(int id) {
            if ((id % (exId)) == 0)
                throw new LocalMRTest.TestException();

            _active.incrementAndGet();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
            s = "" + id;
            _active.decrementAndGet();
        }

        @Override
        public void reduce(LocalMRTest.MrFunTest2 other) {
            s = ((s) + ", ") + (other.s);
        }
    }

    @Test
    public void testThrow() {
        long seed = 87654321;
        Random rnd = new Random(seed);
        for (int k = 0; k < 10; ++k) {
            int cnt = Math.max(1, rnd.nextInt(50));
            final int exId = Math.max(1, rnd.nextInt(cnt));
            final AtomicInteger active = new AtomicInteger();
            // test correct throw behavior with blocking call
            try {
                H2O.submitTask(new LocalMR(new LocalMRTest.MrFunTest2(exId, active), cnt)).join();
                Assert.assertTrue("should've thrown test exception", false);
            } catch (LocalMRTest.TestException t) {
                Assert.assertEquals(0, active.get());
            }
            // and with completer
            try {
                H2O.H2OCountedCompleter cc = new H2O.H2OCountedCompleter() {};
                H2O.submitTask(new LocalMR(new LocalMRTest.MrFunTest2(exId, active), cnt, cc));
                cc.join();
                Assert.assertTrue("should've thrown test exception", false);
            } catch (LocalMRTest.TestException t) {
                Assert.assertEquals(0, active.get());
            }
        }
    }
}

