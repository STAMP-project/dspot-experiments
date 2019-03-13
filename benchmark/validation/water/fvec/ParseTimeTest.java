package water.fvec;


import org.junit.Assert;
import org.junit.Test;
import water.Key;
import water.TestUtil;
import water.exec.Env;

import static C16Chunk._HI_NA;
import static C16Chunk._LO_NA;


public class ParseTimeTest extends TestUtil {
    // Parse click & query times from a subset of kaggle bestbuy data
    @Test
    public void testTimeParse1() {
        Frame fr = TestUtil.parseFrame(null, "smalldata/test/test_time.csv");
        Frame fr2 = fr.subframe(new String[]{ "click_time", "query_time" });
        double[][] exp = new double[][]{ d(1314945892533L, 1314945839752L), d(1315250737042L, 1315250701187L), d(1314215818091L, 1314215713012L), d(1319552294722L, 1319552211759L), d(1319552391697L, 1319552211759L), d(1315436087956L, 1315436004353L), d(1316974022603L, 1316973926996L), d(1316806820871L, 1316806814845L), d(1314650252903L, 1314650003249L), d(1319608558683L, 1319608485926L), d(1315770524139L, 1315770378466L), d(1318983693919L, 1318983686057L), d(1315158920427L, 1315158910874L), d(1319844389203L, 1319844380358L), d(1318232126858L, 1318232070708L), d(1316841248965L, 1316841217043L), d(1315681493645L, 1315681470805L), d(1319395475074L, 1319395407011L), d(1319395524416L, 1319395407011L) };
        ParserTest2.testParsed(fr2, exp, exp.length);
        fr.delete();
    }

    @Test
    public void testTimeParse2() {
        double[][] exp = new double[][]{ d(1, 115200000L, 1136275200000L, 1136275200000L, 1, 1, 1, 1, 19700102, 0), d(1500, 129625200000L, 1247641200000L, 1247641200000L, 0, 0, 0, 0, 19740209, 1), d(15000, 1296028800000L, 1254294000000L, 1254294000000L, 2, 2, 2, 2, 20110126, 2) };
        ParserTest2.testParsed(TestUtil.parseFrame(null, "smalldata/jira/v-11.csv"), exp, exp.length);
    }

    @Test
    public void testUUIDParse1() {
        long[][] exp = new long[][]{ l(1, -6920645892202544848L, -7292965138788753455L, 1), l(2, -6044362161924848758L, -8816779826991220756L, 1), l(3, 7525781432296623477L, -5931071604716118726L, 0), l(4, -2820851224201048577L, -5489586145145168576L, 1), l(5, 2724137185621921333L, -4765699621363809814L, 0), l(6, 3322839861963671140L, -8490411335464879133L, 0), l(1000010407, -8509968003554720838L, -4827671619169354582L, 1), l(1000024046, 4635793065362015984L, -7120526345870393216L, 0), l(1000054511, 5319117908729807773L, -5428920721156775889L, 0), l(1000065922, 5634487650353761931L, -4715448278518188620L, 0), l(1000066478, 3322839861963671140L, -8490411335464879133L, 0), l(1000067268, 2724137185621921333L, -4765699621363809814L, 0), l(100007536, -2820851224201048577L, -5489586145145168576L, 1), l(1000079839, 7525781432296623477L, -5931071604716118726L, 0), l(10000913, -6044362161924848758L, -8816779826991220756L, 0), l(1000104538, -6920645892202544848L, -7292965138788753455L, 1), l(7, 0L, 0L, 0), l(8, -9223372036854775808L, 0L, 0), l(9, -1L, -1L, 1) };
        Frame fr = TestUtil.parseFrame(Key.make("uuid.hex"), "smalldata/test/test_uuid.csv");
        Vec[] vecs = fr.vecs();
        try {
            Assert.assertEquals(exp.length, fr.numRows());
            for (int row = 0; row < (exp.length); row++) {
                int col2 = 0;
                for (int col = 0; col < (fr.numCols()); col++) {
                    if (vecs[col]._isUUID) {
                        if (((exp[row][col2]) == (_LO_NA)) && ((exp[row][(col2 + 1)]) == (_HI_NA))) {
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
            // Slice with UUIDs
            water.exec.Env env = null;
            try {
                env = water.exec.Exec2.exec("uuid.hex[seq(1,10,1),]");
                Frame res = env.popAry();
                System.out.println(res.toStringAll());
                String skey = env.key();
                env.subRef(res, skey);// But then end lifetime

            } catch (IllegalArgumentException iae) {
                if (env != null)
                    env.remove_and_unlock();

                throw iae;
            }
        } finally {
            fr.delete();
        }
    }
}

