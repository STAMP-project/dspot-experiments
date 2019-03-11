package water.rapids.ast.prims.advmath;


import Vec.T_NUM;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.TestFrameBuilder;
import water.rapids.Rapids;
import water.rapids.Val;


public class AstKFoldTest extends TestUtil {
    private Frame fr = null;

    @Test
    public void basicKFoldTest() {
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA").withVecTypes(T_NUM).withDataForCol(0, TestUtil.ard(1, 2, 3, 4, 5)).build();
        int numberOfFolds = 5;
        int randomSeed = new Random().nextInt();
        String tree = String.format("(kfold_column testFrame %d %d )", numberOfFolds, randomSeed);
        Val val = Rapids.exec(tree);
        Frame results = val.getFrame();
        fr = fr.add(results);
        Assert.assertTrue(((fr.vec(1).at(0)) < 5));
        Assert.assertTrue(((fr.vec(1).at(1)) < 5));
        Assert.assertTrue(((fr.vec(1).at(2)) < 5));
        Assert.assertTrue(((fr.vec(1).at(3)) < 5));
        Assert.assertTrue(((fr.vec(1).at(4)) < 5));
        results.delete();
    }
}

