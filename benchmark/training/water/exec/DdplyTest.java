package water.exec;


import org.junit.Test;
import water.Key;
import water.Lockable;
import water.TestUtil;
import water.fvec.Frame;


public class DdplyTest extends TestUtil {
    // This test is intended to use a file large enough to strip across multiple
    // nodes with multiple groups, to test that all generated groups are both
    // built and executed distributed.
    @Test
    public void testDdplyBig() {
        Key k0 = Key.make("cars.hex");
        Key k1 = Key.make("orange.hex");
        try {
            Frame fr0 = TestUtil.parseFrame(k0, "smalldata/cars.csv");
            checkStr("ddply(cars.hex,c(3),nrow)");
            // More complex multi-return
            checkStr("ddply(cars.hex,c(3),function(x) {cbind(mean(x[,2]),mean(x[,3]))})");
            // A big enough file to distribute across multiple nodes.
            // Trimmed down to run in reasonable time.
            // Frame fr1 = parseFrame(k1,"smalldata/unbalanced/orange_small_train.data.zip");
            // checkStr("ddply(orange.hex,c(7),nrow)");
            // checkStr("ddply(orange.hex,c(206,207),function(x){ cbind( mean(x$Var6), sum(x$Var6+x$Var7) ) })");
            // A more complex ddply that works as of 3/1/2014 but is slow for a junit
            // checkStr("ddply(orange.hex,c(206,207),function(x){"+
            // "max6 = max(x$Var6);"+
            // "min6 = min(x$Var6);"+
            // "len  = max6-min6+1;"+
            // "tot  = sum(x$Var7);"+
            // "avg  = tot/len"+
            // "})");
        } finally {
            Lockable.delete(k0);// Remove original hex frame key

            Lockable.delete(k1);// Remove original hex frame key

        }
    }
}

