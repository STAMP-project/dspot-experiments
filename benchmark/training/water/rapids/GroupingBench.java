package water.rapids;


import org.junit.Test;
import water.TestUtil;


public class GroupingBench extends TestUtil {
    @Test
    public void runBench2() {
        Frame f1 = null;
        Frame f2 = null;
        Frame fx = null;
        try {
            // build a hi count cardinality frame
            final long card = ((long) (10000.0));
            f1 = GroupingBench.buildFrame(card, (-1));
            System.out.println(f1.toString(0, 100));
            Vec seq = Vec.makeSeq(card, false);
            f2 = new Frame(seq, seq);
            for (int i = 0; i < 10; i++) {
                long t0 = System.currentTimeMillis();
                fx = Merge.merge(f1, f2, new int[]{ 0 }, new int[]{ 0 }, false, new int[1][]);
                long t1 = System.currentTimeMillis();
                System.out.println(((("MERGE Took " + (t1 - t0)) + " msec for ") + (f1.numRows())));
                // System.out.println(fx.toString(0,100));
                fx.delete();
            }
        } finally {
            if (f1 != null)
                f1.delete();

            if (f2 != null)
                f2.delete();

            if (fx != null)
                fx.delete();

        }
    }
}

