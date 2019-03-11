package water.parser;


import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;


public class ParseUUIDTest extends TestUtil {
    @Test
    public void testUUIDParse1() {
        long[][] exp = new long[][]{ l(1, -6920645892202544848L, -7292965138788753455L, 1), l(2, -6044362161924848758L, -8816779826991220756L, 1), l(3, 7525781432296623477L, -5931071604716118726L, 0), l(4, -2820851224201048577L, -5489586145145168576L, 1), l(5, 2724137185621921333L, -4765699621363809814L, 0), l(6, 3322839861963671140L, -8490411335464879133L, 0), l(1000010407, -8509968003554720838L, -4827671619169354582L, 1), l(1000024046, 4635793065362015984L, -7120526345870393216L, 0), l(1000054511, 5319117908729807773L, -5428920721156775889L, 0), l(1000065922, 5634487650353761931L, -4715448278518188620L, 0), l(1000066478, 3322839861963671140L, -8490411335464879133L, 0), l(1000067268, 2724137185621921333L, -4765699621363809814L, 0), l(100007536, -2820851224201048577L, -5489586145145168576L, 1), l(1000079839, 7525781432296623477L, -5931071604716118726L, 0), l(10000913, -6044362161924848758L, -8816779826991220756L, 0), l(1000104538, -6920645892202544848L, -7292965138788753455L, 1), l(7, 0L, 0L, 0), l(8, -9223372036854775808L, 0L, 0), l(9, -1L, -1L, 1) };
        Frame fr = TestUtil.parse_test_file("smalldata/junit/test_uuid.csv");
        Vec[] vecs = fr.vecs();
        try {
            Assert.assertEquals(exp.length, fr.numRows());
            for (int row = 0; row < (exp.length); row++) {
                int col2 = 0;
                for (int col = 0; col < (fr.numCols()); col++) {
                    if (vecs[col].isUUID()) {
                        if (C16Chunk.isNA(exp[row][col2], exp[row][(col2 + 1)])) {
                            Assert.assertTrue((((((("Frame " + (fr._key)) + ", row=") + row) + ", col=") + col) + ", expect=NA"), vecs[col].isNA(row));
                        } else {
                            long lo = vecs[col].at16l(row);
                            long hi = vecs[col].at16h(row);
                            Assert.assertTrue(((((((((("Frame " + (fr._key)) + ", row=") + row) + ", col=") + col) + ", expect=") + (Long.toHexString(exp[row][col2]))) + ", found=") + lo), (((exp[row][col2]) == lo) && ((exp[row][(col2 + 1)]) == hi)));
                        }
                        col2 += 2;
                    } else {
                        long lo = vecs[col].at8(row);
                        Assert.assertTrue(((((((((("Frame " + (fr._key)) + ", row=") + row) + ", col=") + col) + ", expect=") + (exp[row][col2])) + ", found=") + lo), ((exp[row][col2]) == lo));
                        col2 += 1;
                    }
                }
                Assert.assertEquals(exp[row].length, col2);
            }
        } finally {
            fr.delete();
        }
    }
}

