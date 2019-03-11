package water.fvec;


import org.junit.Test;
import water.Chunk;
import water.TestUtil;
import water.util.Log;

import static water.MRTask.<init>;


public class ChunkSpeedTest extends TestUtil {
    final int cols = 100;

    final int rows = 100000;

    final int rep = 10;

    final double[][] raw = new double[cols][rows];

    Chunk[] chunks = new Chunk[cols];

    @Test
    public void run() {
        for (int j = 0; j < (cols); ++j) {
            for (int i = 0; i < (rows); ++i) {
                raw[j][i] = get(j, i);
            }
        }
        for (int j = 0; j < (cols); ++j) {
            chunks[j] = new NewChunk(raw[j]).compress();
            Log.info(((("Column " + j) + " compressed into: ") + (chunks[j].getClass().toString())));
        }
        Log.info(("COLS: " + (cols)));
        Log.info(("ROWS: " + (rows)));
        Log.info(("REPS: " + (rep)));
        int ll = 5;
        for (int i = 0; i < ll; ++i)
            raw();

        for (int i = 0; i < ll; ++i)
            chunks();

        for (int i = 0; i < ll; ++i)
            chunks_bulk();

        for (int i = 0; i < ll; ++i)
            chunks_part();

        for (int i = 0; i < ll; ++i)
            chunks_visitor();

        for (int i = 0; i < ll; ++i)
            chunksInline();

        // for (int i = 0; i < ll; ++i)
        // mrtask(false);
        // for (int i = 0; i < ll; ++i)
        // rollups(false);
        // Log.info("Now doing funny stuff.\n\n");
        // for (int i = 0; i < ll; ++i)
        // mrtask(true);
        // for (int i = 0; i < ll; ++i)
        // rollups(true);
        // for (int i = 0; i < ll; ++i)
        // chunksInverted();
        // for (int i = 0; i < ll; ++i)
        // rawInverted();
    }

    private static class ChunkSum extends ChunkVisitor {
        double sum;

        public void addZeros(int n) {
        }

        public void addValue(double d) {
            sum += d;
        }

        public void addValue(long l) {
            sum += l;
        }

        public void addValue(int i) {
            sum += i;
        }
    }

    class FillTask extends MRTask<ChunkSpeedTest.FillTask> {
        @Override
        public void map(Chunk[] cs) {
            for (int col = 0; col < (cs.length); ++col) {
                for (int row = 0; row < (cs[0]._len); ++row) {
                    cs[col].set(row, raw[col][row]);
                }
            }
        }
    }

    static class SumTask extends MRTask<ChunkSpeedTest.SumTask> {
        double _sum;

        @Override
        public void map(Chunk[] cs) {
            for (int col = 0; col < (cs.length); ++col) {
                for (int row = 0; row < (cs[0]._len); ++row) {
                    _sum += cs[col].atd(row);
                }
            }
        }

        @Override
        public void reduce(ChunkSpeedTest.SumTask other) {
            _sum += other._sum;
        }
    }
}

