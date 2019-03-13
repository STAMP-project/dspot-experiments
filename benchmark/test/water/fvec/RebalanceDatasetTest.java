package water.fvec;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;


/**
 * Created by tomasnykodym on 4/1/14.
 */
public class RebalanceDatasetTest extends TestUtil {
    @Test
    public void testProstate() {
        Key hex = Key.make("p.hex");
        Key rebalancedKey = Key.make("rebalanced");
        Key raw = NFSFileVec.make(new File("smalldata/logreg/prostate.csv"));
        try {
            ParseDataset2.parse(hex, new Key[]{ raw });
            Frame fr = UKV.get(hex);
            RebalanceDataSet rb = new RebalanceDataSet(fr, rebalancedKey, 300);
            H2O.submitTask(rb);
            rb.join();
            Frame rebalanced = UKV.get(rebalancedKey);
            Assert.assertEquals(rebalanced.numRows(), fr.numRows());
            Assert.assertEquals(rebalanced.anyVec()._espc.length, 301);
            for (long l = 0; l < (fr.numRows()); ++l)
                for (int i = 0; i < (fr.numCols()); ++i)
                    Assert.assertEquals(fr.vecs()[i].at(l), rebalanced.vecs()[i].at(l), 1.0E-8);


        } finally {
            Frame fr = UKV.get(hex);
            fr.delete();
            fr = UKV.get(rebalancedKey);
            fr.delete();
        }
        TestUtil.checkLeakedKeys();
    }
}

