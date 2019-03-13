package water.rapids;


import org.junit.Assert;
import org.junit.Test;
import water.DKV;
import water.Key;
import water.Keyed;
import water.TestUtil;
import water.fvec.Frame;


public class GroupByTest extends TestUtil {
    @Test
    public void testBasic() {
        Frame fr = null;
        String tree = "(GB hex [1] mean 2 \"all\")";// Group-By on col 1 (not 0), no order-by, mean of col 2

        try {
            fr = chkTree(tree, "smalldata/iris/iris_wheader.csv");
            chkDim(fr, 2, 23);
            chkFr(fr, 0, 0, 2.0);
            // Group 2.0, mean is 3.5
            chkFr(fr, 1, 0, 3.5);
            chkFr(fr, 0, 1, 2.2);
            // Group 2.2, mean is 4.5
            chkFr(fr, 1, 1, 4.5);
            chkFr(fr, 0, 7, 2.8);
            // Group 2.8, mean is 5.043, largest group
            chkFr(fr, 1, 7, 5.042857142857143);
            chkFr(fr, 0, 22, 4.4);// Group 4.4, mean is 1.5, last group

            chkFr(fr, 1, 22, 1.5);
        } finally {
            if (fr != null)
                fr.delete();

            Keyed.remove(Key.make("hex"));
        }
    }

    @Test
    public void testCatGroup() {
        Frame fr = null;
        String tree = "(GB hex [4] nrow 0 \"all\" mean 2 \"all\")";// Group-By on col 4, no order-by, nrow and mean of col 2

        try {
            fr = chkTree(tree, "smalldata/iris/iris_wheader.csv");
            chkDim(fr, 3, 3);
            chkFr(fr, 0, 0, "Iris-setosa");
            chkFr(fr, 1, 0, 50);
            chkFr(fr, 2, 0, 1.464);
            chkFr(fr, 0, 1, "Iris-versicolor");
            chkFr(fr, 1, 1, 50);
            chkFr(fr, 2, 1, 4.26);
            chkFr(fr, 0, 2, "Iris-virginica");
            chkFr(fr, 1, 2, 50);
            chkFr(fr, 2, 2, 5.552);
            fr.delete();
            fr = chkTree("(GB hex [1] mode 4 \"all\" )", "smalldata/iris/iris_wheader.csv");
            chkDim(fr, 2, 23);
        } finally {
            if (fr != null)
                fr.delete();

            Keyed.remove(Key.make("hex"));
        }
    }

    @Test
    public void testNAHandle() {
        Frame fr = null;
        try {
            String tree = "(GB hex [7] nrow 0 \"all\" mean 1 \"all\")";// Group-By on year, no order-by, mean of economy

            fr = chkTree(tree, "smalldata/junit/cars.csv");
            chkDim(fr, 3, 13);
            chkFr(fr, 0, 0, 70);
            // 1970, 35 cars, NA in economy
            chkFr(fr, 1, 0, 35);
            chkFr(fr, 2, 0, Double.NaN);
            chkFr(fr, 0, 2, 72);
            // 1972, 28 cars, 18.714 in economy
            chkFr(fr, 1, 2, 28);
            chkFr(fr, 2, 2, 18.714, 0.1);
            fr.delete();
            tree = "(GB hex [7] nrow 1 \"all\" nrow 1 \"rm\" nrow 1 \"ignore\")";// Group-By on year, no order-by, nrow of economy

            fr = chkTree(tree, "smalldata/junit/cars.csv");
            chkDim(fr, 4, 13);
            chkFr(fr, 0, 0, 70);
            // 1970, 35 cars, 29 have economy
            chkFr(fr, 1, 0, 35);
            // ALL
            chkFr(fr, 2, 0, 29);
            // RM
            chkFr(fr, 3, 0, 29);
            // IGNORE
            fr.delete();
            tree = "(GB hex [7] mean 1 \"all\" mean 1 \"rm\" mean 1 \"ignore\")";// Group-By on year, no order-by, mean of economy

            fr = chkTree(tree, "smalldata/junit/cars.csv");
            chkDim(fr, 4, 13);
            chkFr(fr, 0, 0, 70);
            // 1970, 35 cars, 29 have economy
            chkFr(fr, 1, 0, Double.NaN);// ALL

            chkFr(fr, 2, 0, 17.69, 0.1);// RM

            chkFr(fr, 3, 0, 14.66, 0.1);// IGNORE

        } finally {
            if (fr != null)
                fr.delete();

            Keyed.remove(Key.make("hex"));
        }
    }

    @Test
    public void testAllAggs() {
        Frame fr = null;
        try {
            String tree = "(GB hex [4] nrow 0 \"rm\"  mean 1 \"rm\"  sum 1 \"rm\"  min 1 \"rm\"  max 1 \"rm\" )";
            fr = chkTree(tree, "smalldata/iris/iris_wheader.csv");
            chkDim(fr, 6, 3);
            chkFr(fr, 0, 0, "Iris-setosa");
            chkFr(fr, 1, 0, 50);
            // nrow
            chkFr(fr, 2, 0, 3.418);// mean

            chkFr(fr, 3, 0, 170.9);// sum

            chkFr(fr, 4, 0, 2.3);// min

            chkFr(fr, 5, 0, 4.4);// max

            chkFr(fr, 0, 1, "Iris-versicolor");
            chkFr(fr, 1, 1, 50);
            // nrow
            chkFr(fr, 2, 1, 2.77);// mean

            chkFr(fr, 3, 1, 138.5);// sum

            chkFr(fr, 4, 1, 2.0);// min

            chkFr(fr, 5, 1, 3.4);// max

            chkFr(fr, 0, 2, "Iris-virginica");
            chkFr(fr, 1, 2, 50);
            // nrow
            chkFr(fr, 2, 2, 2.974);// mean

            chkFr(fr, 3, 2, 148.7);// sum

            chkFr(fr, 4, 2, 2.2);// min

            chkFr(fr, 5, 2, 3.8);// max

        } finally {
            if (fr != null)
                fr.delete();

            Keyed.remove(Key.make("hex"));
        }
    }

    @Test
    public void testImpute() {
        Frame fr = null;
        Frame fr2 = null;
        try {
            // Impute fuel economy via the "mean" method, no.
            String tree = "(h2o.impute hex 1 \"mean\" \"low\" [] _ _)";// (h2o.impute data col method combine_method groupby groupByFrame values)

            chkTree(tree, "smalldata/junit/cars.csv", 1.0F);
            fr = DKV.getGet("hex");
            chkDim(fr, 8, 406);
            Assert.assertEquals(0, fr.vec(1).naCnt());// No NAs anymore

            Assert.assertEquals(23.51, fr.vec(1).at(26), 0.1);// Row 26 was an NA, now as mean economy

            fr.delete();
            // Impute fuel economy via the "mean" method, after grouping by year.  Update in place.
            tree = "(h2o.impute hex 1 \"mean\" \"low\" [7] _ _)";
            fr2 = chkTree(tree, "smalldata/junit/cars.csv", 1.0F);
            fr = DKV.getGet("hex");
            chkDim(fr, 8, 406);
            Assert.assertEquals(0, fr.vec(1).naCnt());// No NAs anymore

            Assert.assertEquals(17.69, fr.vec(1).at(26), 0.1);// Row 26 was an NA, now as 1970 mean economy

        } finally {
            if (fr != null)
                fr.delete();

            if (fr2 != null)
                fr2.delete();

            Keyed.remove(Key.make("hex"));
        }
    }

    @Test
    public void testBasicDdply() {
        Frame fr = null;
        String tree = "(ddply hex [1] {x . (flatten (mean (cols x 2) TRUE))})";// Group-By on col 1 (not 0) mean of col 2

        try {
            fr = chkTree(tree, "smalldata/iris/iris_wheader.csv");
            chkDim(fr, 2, 23);
            chkFr(fr, 0, 0, 2.0);
            // Group 2.0, mean is 3.5
            chkFr(fr, 1, 0, 3.5);
            chkFr(fr, 0, 1, 2.2);
            // Group 2.2, mean is 4.5
            chkFr(fr, 1, 1, 4.5);
            chkFr(fr, 0, 7, 2.8);
            // Group 2.8, mean is 5.043, largest group
            chkFr(fr, 1, 7, 5.042857142857143);
            chkFr(fr, 0, 22, 4.4);// Group 4.4, mean is 1.5, last group

            chkFr(fr, 1, 22, 1.5);
            fr.delete();
            fr = chkTree("(ddply hex [1] {x . (sum (* (cols x 2) (cols x 3)))})", "smalldata/iris/iris_wheader.csv");
            chkDim(fr, 2, 23);
        } finally {
            if (fr != null)
                fr.delete();

            Keyed.remove(Key.make("hex"));
        }
    }

    // covtype.altered response column has this distribution:
    // -1  20510
    // 1 211840
    // 2 283301
    // 3  35754
    // 4   2747
    // 6  17367
    // 10000   9493
    @Test
    public void testSplitCats() throws InterruptedException {
        Frame cov = TestUtil.parse_test_file(Key.make("cov"), "smalldata/covtype/covtype.altered.gz");
        System.out.println(cov.toString(0, 10));
        Val v_ddply = Rapids.exec("(ddply cov [54] nrow)");
        System.out.println(v_ddply.toString());
        v_ddply.getFrame().delete();
        Val v_groupby = Rapids.exec("(GB cov [54] nrow 54 \"all\")");
        System.out.println(v_groupby.toString());
        v_groupby.getFrame().delete();
        cov.delete();
    }

    @Test
    public void testGroupbyTableSpeed() {
        Frame ids = TestUtil.parse_test_file(Key.make("cov"), "smalldata/junit/id_cols.csv");
        ids.replace(0, ids.anyVec().toCategoricalVec()).remove();
        System.out.println(ids.toString(0, 10));
        long start = System.currentTimeMillis();
        Val v_gb = Rapids.exec("(GB cov [0] nrow 0 \"all\")");
        System.out.println((("GB Time= " + ((System.currentTimeMillis()) - start)) + "msec"));
        System.out.println(v_gb.toString());
        v_gb.getFrame().delete();
        long start2 = System.currentTimeMillis();
        Val v_tb = Rapids.exec("(table cov FALSE)");
        System.out.println((("Table Time= " + ((System.currentTimeMillis()) - start2)) + "msec"));
        System.out.println(v_tb.toString());
        v_tb.getFrame().delete();
        ids.delete();
        Keyed.remove(Key.make("cov"));
    }
}

