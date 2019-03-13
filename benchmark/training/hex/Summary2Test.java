package hex;


import Summary2.BasicStat;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.Vec;


public class Summary2Test extends TestUtil {
    @Test
    public void testConstColumn() {
        Key key = Key.make("testConst.hex");
        Frame fr = TestUtil.parseFrame(key, "./smalldata/constantColumn.csv");
        Futures fs = new Futures();
        for (Vec vec : fr.vecs())
            vec.rollupStats(fs);

        fs.blockForPending();
        Vec vec = fr.vecs()[0];
        Summary2.BasicStat basicStat = new Summary2.PrePass().doAll(fr).finishUp()._basicStats[0];
        Summary2 s = new Summary2(vec, "", basicStat);
        s.add(vec.chunkForRow(0));
        for (int i = 1; i < (vec.nChunks()); i++) {
            Summary2 s1 = new Summary2(vec, "", basicStat);
            s1.add(vec.chunkForRow(i));
            s.add(s1);
        }
        s.finishUp(vec);
        Assert.assertEquals(1, s.hcnt.length);
        Assert.assertEquals(528, s.hcnt[0]);
        for (double pv : s._pctile)
            Assert.assertEquals(0.1, pv, 1.0E-5);

        fr.delete();
    }

    @Test
    public void testEnumColumn() {
        Key key = Key.make("cars.hex");
        Frame fr = TestUtil.parseFrame(key, "./smalldata/cars.csv");
        Futures fs = new Futures();
        for (Vec vec : fr.vecs())
            vec.rollupStats(fs);

        fs.blockForPending();
        Vec vec = fr.vecs()[fr.find("name")];
        Summary2.BasicStat basicStat = new Summary2.PrePass().doAll(fr).finishUp()._basicStats[fr.find("name")];
        Summary2 s = new Summary2(vec, "", basicStat);
        s.add(vec.chunkForRow(0));
        for (int i = 1; i < (vec.nChunks()); i++) {
            Summary2 s1 = new Summary2(vec, "", basicStat);
            s1.add(vec.chunkForRow(i));
            s.add(s1);
        }
        s.finishUp(vec);
        Assert.assertEquals(306, s.hcnt.length);
        fr.delete();
    }

    @Test
    public void testIntColumn() {
        Key key = Key.make("cars.hex");
        Frame fr = TestUtil.parseFrame(key, "./smalldata/cars.csv");
        Futures fs = new Futures();
        for (Vec vec : fr.vecs())
            vec.rollupStats(fs);

        fs.blockForPending();
        Vec vec = fr.vecs()[fr.find("cylinders")];
        Summary2.BasicStat basicStat = new Summary2.PrePass().doAll(fr).finishUp()._basicStats[fr.find("cylinders")];
        Summary2 s = new Summary2(vec, "", basicStat);
        s.add(vec.chunkForRow(0));
        for (int i = 1; i < (vec.nChunks()); i++) {
            Summary2 s1 = new Summary2(vec, "", basicStat);
            s1.add(vec.chunkForRow(i));
            s.add(s1);
        }
        s.finishUp(vec);
        Assert.assertEquals(0, s.hcnt[4]);// no 7 cylinder cars

        // kbn 2/28. 1% quantile for 0 should expect 4
        // I changed Summary2 to be .1%, 1% ...99%, 99.9% quantiles. So answer is 3 for [0]
        Assert.assertEquals(3, ((int) (s._pctile[0])));
        Assert.assertEquals(8, ((int) (s._pctile[((s._pctile.length) - 1)])));
        fr.delete();
    }
}

