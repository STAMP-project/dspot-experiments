package hex.coxph;


import CoxPH.CoxPHTask;
import CoxPHModel.CoxPHParameters.CoxPHTies;
import DataInfo.Row;
import Vec.T_CAT;
import Vec.T_NUM;
import hex.DataInfo;
import org.junit.Assert;
import org.junit.Test;
import water.Scope;
import water.TestUtil;
import water.fvec.Chunk;
import water.fvec.Frame;
import water.fvec.TestFrameBuilder;


public class EfronDJKTermTaskTest extends TestUtil {
    @Test
    public void testDJKTermMatrix() throws Exception {
        try {
            Scope.enter();
            final Frame fr = Scope.track(new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB", "ColC", "Event", "Stop").withVecTypes(T_NUM, T_CAT, T_CAT, T_NUM, T_NUM).withDataForCol(0, ard(3.2, 1, 2, 3, 4, 5.6, 7)).withDataForCol(1, ar("A", "B", "C", "E", "F", "I", "J")).withDataForCol(2, ar("A", "B,", "A", "C", "A", "B", "A")).withDataForCol(3, ard(1, 0, 2, 3, 4, 3, 1)).withDataForCol(4, ard(1, 1, 1, 1, 1, 1, 1)).withChunkLayout(7).build());
            final DataInfo dinfo = EfronDJKTermTaskTest.makeDataInfo(fr, 2);
            final DataInfo dinfoNoResp = EfronDJKTermTaskTest.makeDataInfo(fr.subframe(new String[]{ "ColA", "ColB", "ColC" }), 0);
            CoxPH.CoxPHTask coxMR = new CoxPH.CoxPHTask(dinfo, new double[dinfo.fullN()], new double[1], 0, 0, false, null, false, CoxPHTies.efron);
            EfronDJKSetupFun efronDJKSetupFun = new EfronDJKSetupFun();
            efronDJKSetupFun._cumsumRiskTerm = new double[]{ 0, 1, 2, 3, 4 };
            efronDJKSetupFun._riskTermT2 = new double[]{ 1, 2, 3, 4, 5 };
            EfronDJKTermTask djkTermTask = new EfronDJKTermTask(dinfo, coxMR, efronDJKSetupFun);
            double[][] djkTerm = CoxPHUtils.malloc2DArray(dinfo.fullN(), dinfo.fullN());
            djkTermTask._djkTerm = djkTerm;
            djkTermTask.setupLocal();
            Chunk[] cs = EfronDJKTermTaskTest.chunks(dinfo, 0);
            Chunk[] csNoResp = EfronDJKTermTaskTest.chunks(dinfoNoResp, 0);
            // Compare vector*vectorT products
            double[][] expected = CoxPHUtils.malloc2DArray(dinfo.fullN(), dinfo.fullN());
            for (int i = 0; i < (cs[0]._len); i++) {
                DataInfo.Row rowNoResp = dinfoNoResp.extractDenseRow(csNoResp, i, dinfoNoResp.newDenseRow());
                EfronDJKTermTaskTest.vvT(rowNoResp, expected);
                DataInfo.Row row = dinfo.extractDenseRow(cs, i, dinfo.newDenseRow());
                djkTermTask.processRow(row);
            }
            djkTermTask.postGlobal();
            for (int j = 0; j < (expected.length); j++) {
                Assert.assertArrayEquals(expected[j], djkTerm[j], 1.0E-8);
            }
        } finally {
            Scope.exit();
        }
    }
}

