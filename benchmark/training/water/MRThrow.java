package water;


import H2O.CLOUD;
import H2O.CLOUD._memary;
import H2O.SELF;
import java.util.concurrent.ExecutionException;
import jsr166y.CountedCompleter;
import org.junit.Assert;
import org.junit.Test;
import water.fvec.Chunk;
import water.fvec.FileVec;
import water.fvec.Vec;


public class MRThrow extends TestUtil {
    @Test
    public void testLots() {
        for (int i = 0; i < 10; i++)
            testInvokeThrow();

    }

    // ---
    // Map in h2o.jar - a multi-megabyte file - into Arraylets.
    // Run a distributed byte histogram.  Throw an exception in *some* map call,
    // and make sure it's forwarded to the invoke.
    @Test
    public void testInvokeThrow() {
        int sz = CLOUD.size();
        Vec vec = Vec.makeZero((((sz + 1) * (FileVec.DFLT_CHUNK_SIZE)) + 1));
        try {
            for (int i = 0; i < sz; ++i) {
                MRThrow.ByteHistoThrow bh = new MRThrow.ByteHistoThrow(CLOUD._memary[i]);
                Throwable ex = null;
                try {
                    bh.doAll(vec);// invoke should throw DistributedException wrapped up in RunTimeException

                } catch (RuntimeException e) {
                    ex = e;
                    Assert.assertTrue(((e.getMessage().contains("test")) || (e.getCause().getMessage().contains("test"))));
                } catch (Throwable e2) {
                    (ex = e2).printStackTrace();
                    Assert.fail(("Expected RuntimeException, got " + (ex.toString())));
                }
                if (ex == null)
                    Assert.fail("should've thrown");

            }
        } finally {
            if (vec != null)
                vec.remove();
            // remove from DKV

        }
    }

    @Test
    public void testContinuationThrow() throws InterruptedException, ExecutionException {
        int sz = CLOUD.size();
        Vec vec = Vec.makeZero((((sz + 1) * (FileVec.DFLT_CHUNK_SIZE)) + 1));
        try {
            for (int i = 0; i < (_memary.length); ++i) {
                final MRThrow.ByteHistoThrow bh = new MRThrow.ByteHistoThrow(CLOUD._memary[i]);
                final boolean[] ok = new boolean[]{ false };
                try {
                    CountedCompleter cc = new CountedCompleter() {
                        @Override
                        public void compute() {
                            tryComplete();
                        }

                        @Override
                        public boolean onExceptionalCompletion(Throwable ex, CountedCompleter cc) {
                            ok[0] = ex.getMessage().contains("test");
                            return super.onExceptionalCompletion(ex, cc);
                        }
                    };
                    bh.setCompleter(cc);
                    bh.dfork(vec);
                    // If the chosen file is too small for the cluster, some nodes will have *no* work
                    // and so no exception is thrown.
                    cc.join();
                } catch (RuntimeException re) {
                    Assert.assertTrue(((re.getMessage().contains("test")) || (re.getCause().getMessage().contains("test"))));
                    // } catch( ExecutionException e ) { // caught on self
                    // assertTrue(e.getMessage().contains("test"));
                } catch (AssertionError ae) {
                    throw ae;// Standard junit failure reporting assertion

                } catch (Throwable ex) {
                    ex.printStackTrace();
                    Assert.fail(("Unexpected exception" + (ex.toString())));
                }
            }
        } finally {
            if (vec != null)
                vec.remove();
            // remove from DKV

        }
    }

    // Byte-wise histogram
    public static class ByteHistoThrow extends MRTask<MRThrow.ByteHistoThrow> {
        final H2ONode _throwAt;

        int[] _x;

        ByteHistoThrow(H2ONode h2o) {
            _throwAt = h2o;
        }

        // Count occurrences of bytes
        @Override
        public void map(Chunk chk) {
            _x = new int[256];
            // One-time set histogram array
            byte[] bits = chk.getBytes();// Raw file bytes

            // Compute local histogram
            for (byte b : bits)
                (_x[(b & 255)])++;

            if (SELF.equals(_throwAt))
                throw new RuntimeException("test");

        }

        // ADD together all results
        @Override
        public void reduce(MRThrow.ByteHistoThrow bh) {
            water.util.ArrayUtils.add(_x, bh._x);
        }
    }
}

