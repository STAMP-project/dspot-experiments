package hex.quantile;


import QuantileModel.CombineMethod;
import QuantileModel.QuantileParameters;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import water.Job;
import water.TestUtil;
import water.fvec.Frame;
import water.util.ArrayUtils;


public class QuantileTest extends TestUtil {
    @Test
    public void testIris() {
        QuantileModel kmm = null;
        Frame fr = null;
        try {
            long start = System.currentTimeMillis();
            System.out.println("Start Parse");
            fr = parse_test_file("smalldata/iris/iris_wheader.csv");
            // fr = parse_test_file("../../datasets/UCI/UCI-large/covtype/covtype.data");
            // fr = parse_test_file("../../datasets/billion_rows.csv.gz");
            System.out.println(("Done Parse: " + ((System.currentTimeMillis()) - start)));
            QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
            parms._train = fr._key;
            // parms._max_iterations = 10;
            Job<QuantileModel> job = trainModel();
            kmm = job.get();
            job.remove();
        } finally {
            if (fr != null)
                fr.remove();

            if (kmm != null)
                kmm.delete();

        }
    }

    @Test
    public void testAllNAS() {
        QuantileModel kmm = null;
        Frame fr = null;
        try {
            fr = ArrayUtils.frame(new double[][]{ new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN }, new double[]{ Double.NaN } });
            QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
            parms._train = fr._key;
            Job<QuantileModel> job = trainModel();
            kmm = job.get();
            job.remove();
            for (int i = 0; i < 11; i++)
                Assert.assertTrue(Double.isNaN(kmm._output._quantiles[0][i]));

        } finally {
            if (fr != null)
                fr.remove();

            if (kmm != null)
                kmm.delete();

        }
    }

    @Test
    public void testInts() {
        QuantileModel kmm = null;
        Frame fr = null;
        try {
            fr = ArrayUtils.frame(new double[][]{ new double[]{ 0 }, new double[]{ 0 }, new double[]{ 0 }, new double[]{ 0 }, new double[]{ 0 }, new double[]{ 0 }, new double[]{ 0 }, new double[]{ 0 }, new double[]{ 0 }, new double[]{ 0 }, new double[]{ 1 }, new double[]{ 1 }, new double[]{ 1 }, new double[]{ 1 }, new double[]{ 1 }, new double[]{ 1 }, new double[]{ 1 }, new double[]{ 1 }, new double[]{ 1 }, new double[]{ 1 }, new double[]{ 2 }, new double[]{ 2 }, new double[]{ 2 }, new double[]{ 2 }, new double[]{ 2 }, new double[]{ 2 }, new double[]{ 2 }, new double[]{ 2 }, new double[]{ 2 }, new double[]{ 2 } });
            QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
            parms._train = fr._key;
            Job<QuantileModel> job = trainModel();
            kmm = job.get();
            job.remove();
        } finally {
            if (fr != null)
                fr.remove();

            if (kmm != null)
                kmm.delete();

        }
    }

    @Test
    public void test50pct() {
        QuantileModel kmm = null;
        Frame fr = null;
        try {
            double[][] d = new double[][]{ new double[]{ 1.56386606237 }, new double[]{ 0.812834256224 }, new double[]{ 3.68417563302 }, new double[]{ 3.1270221088 }, new double[]{ 5.51277746586 } };
            fr = ArrayUtils.frame(d);
            QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
            parms._train = fr._key;
            Job<QuantileModel> job = trainModel();
            kmm = job.get();
            job.remove();
            Assert.assertTrue(((kmm._output._quantiles[0][5]) == (d[3][0])));
        } finally {
            if (fr != null)
                fr.remove();

            if (kmm != null)
                kmm.delete();

        }
    }

    @Test
    public void testDirectMatch() {
        QuantileModel kmm = null;
        Frame fr = null;
        try {
            double[][] d = new double[][]{ new double[]{ 1 }, new double[]{ 1 }, new double[]{ 1 }, new double[]{ 2 } };
            fr = ArrayUtils.frame(d);
            QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
            parms._train = fr._key;
            parms._probs = new double[]{ 0.5 };
            Job<QuantileModel> job = trainModel();
            kmm = job.get();
            job.remove();
            Assert.assertTrue(((kmm._output._quantiles[0][0]) == 1));
        } finally {
            if (fr != null)
                fr.remove();

            if (kmm != null)
                kmm.delete();

        }
    }

    @Test
    public void testInterpolate1() {
        QuantileModel kmm = null;
        Frame fr = null;
        try {
            double[][] d = new double[][]{ new double[]{ 1 }, new double[]{ 1 }, new double[]{ 2 }, new double[]{ 2 } };
            fr = ArrayUtils.frame(d);
            QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
            parms._train = fr._key;
            parms._probs = new double[]{ 0.5 };
            Job<QuantileModel> job = trainModel();
            kmm = job.get();
            job.remove();
            Assert.assertTrue(((kmm._output._quantiles[0][0]) == 1.5));
        } finally {
            if (fr != null)
                fr.remove();

            if (kmm != null)
                kmm.delete();

        }
    }

    @Test
    public void testInterpolate2() {
        QuantileModel kmm = null;
        Frame fr = null;
        try {
            double[][] d = new double[][]{ new double[]{ 1 }, new double[]{ 1 }, new double[]{ 3 }, new double[]{ 2 } };
            fr = ArrayUtils.frame(d);
            QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
            parms._train = fr._key;
            parms._probs = new double[]{ 0.5 };
            Job<QuantileModel> job = trainModel();
            kmm = job.get();
            job.remove();
            Assert.assertTrue(((kmm._output._quantiles[0][0]) == 1.5));
        } finally {
            if (fr != null)
                fr.remove();

            if (kmm != null)
                kmm.delete();

        }
    }

    @Test
    public void testInterpolateLow() {
        QuantileModel kmm = null;
        Frame fr = null;
        try {
            double[][] d = new double[][]{ new double[]{ 1 }, new double[]{ 2 }, new double[]{ 3 } };
            fr = ArrayUtils.frame(d);
            QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
            parms._train = fr._key;
            parms._probs = new double[]{ 0.49 };
            Job<QuantileModel> job = trainModel();
            kmm = job.get();
            job.remove();
            Assert.assertTrue(((kmm._output._quantiles[0][0]) == 1.98));
        } finally {
            if (fr != null)
                fr.remove();

            if (kmm != null)
                kmm.delete();

        }
    }

    @Test
    public void testInterpolateHigh() {
        QuantileModel kmm = null;
        Frame fr = null;
        try {
            double[][] d = new double[][]{ new double[]{ 1 }, new double[]{ 2 }, new double[]{ 3 } };
            fr = ArrayUtils.frame(d);
            QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
            parms._train = fr._key;
            parms._probs = new double[]{ 0.51 };
            Job<QuantileModel> job = trainModel();
            kmm = job.get();
            job.remove();
            Assert.assertTrue(((kmm._output._quantiles[0][0]) == 2.02));
        } finally {
            if (fr != null)
                fr.remove();

            if (kmm != null)
                kmm.delete();

        }
    }

    @Test
    public void testInterpolateHighWeighted() {
        QuantileModel kmm = null;
        Frame fr = null;
        try {
            double[][] d = new double[][]{ new double[]{ 1, 0 }, new double[]{ 2, 1 }, new double[]{ 3, 1 } };
            fr = ArrayUtils.frame(new String[]{ "x", "weights" }, d);
            QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
            parms._train = fr._key;
            parms._weights_column = "weights";
            parms._probs = new double[]{ 0.51 };
            Job<QuantileModel> job = trainModel();
            kmm = job.get();
            job.remove();
            Assert.assertTrue(((kmm._output._quantiles[0][0]) == 2.51));
        } finally {
            if (fr != null)
                fr.remove();

            if (kmm != null)
                kmm.delete();

        }
    }

    @Test
    public void testInterpolateHighWeighted2() {
        QuantileModel kmm = null;
        Frame fr = null;
        try {
            double[][] d = new double[][]{ new double[]{ 1, 2 }, new double[]{ 2, 1 }, new double[]{ 3, 1 } };
            fr = ArrayUtils.frame(new String[]{ "x", "weights" }, d);
            QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
            parms._train = fr._key;
            parms._weights_column = "weights";
            parms._probs = new double[]{ 0.43 };
            Job<QuantileModel> job = trainModel();
            kmm = job.get();
            job.remove();
            Assert.assertTrue(((kmm._output._quantiles[0][0]) == 1.29));
        } finally {
            if (fr != null)
                fr.remove();

            if (kmm != null)
                kmm.delete();

        }
    }

    @Test
    public void testInterpolateHighWeighted3() {
        QuantileModel kmm = null;
        Frame fr = null;
        try {
            double[][] d = new double[][]{ new double[]{ 1, 3 }, new double[]{ 2, 2 }, new double[]{ 3, 5 } };
            fr = ArrayUtils.frame(new String[]{ "x", "weights" }, d);
            QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
            parms._train = fr._key;
            parms._weights_column = "weights";
            parms._probs = new double[]{ 0.31 };
            Job<QuantileModel> job = trainModel();
            kmm = job.get();
            job.remove();
            Assert.assertTrue(((kmm._output._quantiles[0][0]) == 1.79));
        } finally {
            if (fr != null)
                fr.remove();

            if (kmm != null)
                kmm.delete();

        }
    }

    @Test
    public void testInterpolateHighWeighted4() {
        QuantileModel kmm = null;
        Frame fr = null;
        try {
            double[][] d = new double[][]{ new double[]{ 1, 1.005 }, new double[]{ 2, 1 }, new double[]{ 3, 1 } };
            fr = ArrayUtils.frame(new String[]{ "x", "weights" }, d);
            QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
            parms._train = fr._key;
            parms._weights_column = "weights";
            parms._probs = new double[]{ 0.71 };
            Job<QuantileModel> job = trainModel();
            kmm = job.get();
            job.remove();
            Assert.assertTrue(("Got: " + (kmm._output._quantiles[0][0])), ((Math.abs(((kmm._output._quantiles[0][0]) - 2.42355))) < 1.0E-10));
        } finally {
            if (fr != null)
                fr.remove();

            if (kmm != null)
                kmm.delete();

        }
    }

    @Test
    public void testInterpolateHighWeighted5() {
        QuantileModel kmm = null;
        Frame fr = null;
        try {
            double[][] d = new double[][]{ new double[]{ 1, 5 }, new double[]{ 2, 4 }, new double[]{ 3, 3 } };
            fr = ArrayUtils.frame(new String[]{ "x", "weights" }, d);
            QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
            parms._train = fr._key;
            parms._weights_column = "weights";
            parms._probs = new double[]{ 0.43 };
            Job<QuantileModel> job = trainModel();
            kmm = job.get();
            job.remove();
            Assert.assertTrue(((kmm._output._quantiles[0][0]) == 1.73));
        } finally {
            if (fr != null)
                fr.remove();

            if (kmm != null)
                kmm.delete();

        }
    }

    @Test
    public void testInterpolateHighWeighted6() {
        QuantileModel kmm = null;
        Frame fr = null;
        try {
            double[][] d = new double[][]{ new double[]{ 1, 2 }, new double[]{ 2, 2 }, new double[]{ 3, 2 } };
            fr = ArrayUtils.frame(new String[]{ "x", "weights" }, d);
            QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
            parms._train = fr._key;
            parms._weights_column = "weights";
            parms._probs = new double[]{ 0.33 };
            Job<QuantileModel> job = trainModel();
            kmm = job.get();
            job.remove();
            Assert.assertTrue(((kmm._output._quantiles[0][0]) == 1.65));
        } finally {
            if (fr != null)
                fr.remove();

            if (kmm != null)
                kmm.delete();

        }
    }

    @Test
    public void testInterpolateHighWeighted7() {
        QuantileModel kmm = null;
        Frame fr = null;
        try {
            double[][] d = new double[][]{ new double[]{ 1, 1 }, new double[]{ 2, 1 }, new double[]{ 3, 1 } };
            fr = ArrayUtils.frame(new String[]{ "x", "weights" }, d);
            QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
            parms._train = fr._key;
            parms._weights_column = "weights";
            parms._probs = new double[]{ 0, 0.25, 0.5, 0.75, 1 };
            Job<QuantileModel> job = trainModel();
            kmm = job.get();
            job.remove();
            Assert.assertTrue(Arrays.equals(kmm._output._quantiles[0], new double[]{ 1, 1.5, 2, 2.5, 3 }));
        } finally {
            if (fr != null)
                fr.remove();

            if (kmm != null)
                kmm.delete();

        }
    }

    @Test
    public void testInterpolateHighWeighted8() {
        QuantileModel kmm = null;
        Frame fr = null;
        try {
            double[][] d = new double[][]{ new double[]{ 1, 2 }, new double[]{ 2, 2 }, new double[]{ 3, 2 } };
            fr = ArrayUtils.frame(new String[]{ "x", "weights" }, d);
            QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
            parms._train = fr._key;
            parms._weights_column = "weights";
            parms._probs = new double[]{ 0, 0.25, 0.5, 0.75, 1 };
            Job<QuantileModel> job = trainModel();
            kmm = job.get();
            job.remove();
            Assert.assertTrue(Arrays.equals(kmm._output._quantiles[0], new double[]{ 1, 1.25, 2, 2.75, 3 }));
        } finally {
            if (fr != null)
                fr.remove();

            if (kmm != null)
                kmm.delete();

        }
    }

    @Test
    public void testInterpolateWideRangeWeighted() {
        QuantileModel kmm = null;
        Frame fr = null;
        try {
            double[][] d = new double[][]{ new double[]{ 1.0E-100, 1 }, new double[]{ 1.0E-10, 4 }, new double[]{ 1.0E-4, 2 }, new double[]{ 0.01, 4 }, new double[]{ 0.1, 5 }, new double[]{ 1, 3 } };
            fr = ArrayUtils.frame(new String[]{ "x", "weights" }, d);
            QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
            parms._train = fr._key;
            parms._weights_column = "weights";
            // parms._probs = new double[]{0,0.25,0.5,0.75,1};
            parms._probs = new double[]{ 0.25, 0.5, 0.75, 1 };
            Job<QuantileModel> job = trainModel();
            kmm = job.get();
            job.remove();
            // double[] exp = new double[]{1e-100,5e-5,1e-2,1e-1,1};
            double[] exp = new double[]{ 5.0E-5, 0.01, 0.1, 1 };
            double[] act = kmm._output._quantiles[0];
            for (int i = 0; i < (exp.length); ++i)
                Assert.assertTrue(((("Got " + (act[i])) + " but expected ") + (exp[i])), (((Math.abs(((act[i]) - (exp[i])))) / (exp[i])) < 1.0E-5));

        } finally {
            if (fr != null)
                fr.remove();

            if (kmm != null)
                kmm.delete();

        }
    }

    @Test
    public void testInterpolateWideRange() {
        QuantileModel kmm = null;
        Frame fr = null;
        try {
            double[][] d = new double[][]{ new double[]{ 1.0E-100 }, new double[]{ 1.0E-10 }, new double[]{ 1.0E-4 }, new double[]{ 0.01 }, new double[]{ 0.1 }, new double[]{ 1 } };
            fr = ArrayUtils.frame(new String[]{ "x" }, d);
            QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
            parms._train = fr._key;
            parms._probs = new double[]{ 0, 0.25, 0.5, 0.75, 1 };
            Job<QuantileModel> job = trainModel();
            kmm = job.get();
            job.remove();
            double[] exp = new double[]{ 1.0E-100, 2.5E-5, 0.00505, 0.0775, 1 };
            double[] act = kmm._output._quantiles[0];
            for (int i = 0; i < (exp.length); ++i)
                Assert.assertTrue(((("Got " + (act[i])) + " but expected ") + (exp[i])), (((Math.abs(((act[i]) - (exp[i])))) / (exp[i])) < 1.0E-5));

        } finally {
            if (fr != null)
                fr.remove();

            if (kmm != null)
                kmm.delete();

        }
    }

    @Test
    public void testInterpolateWideRangeWeighted2() {
        QuantileModel kmm = null;
        Frame fr = null;
        try {
            double[][] d = new double[][]{ new double[]{ 1.0E-100, 10 }, new double[]{ 1.0E-10, 4 }, new double[]{ 1.0E-4, 2 }, new double[]{ 0.01, 4 }, new double[]{ 0.1, 5 }, new double[]{ 1, 3 } };
            fr = ArrayUtils.frame(new String[]{ "x", "weights" }, d);
            QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
            parms._train = fr._key;
            parms._weights_column = "weights";
            parms._probs = new double[]{ 0, 0.25, 0.5, 0.75, 1 };
            Job<QuantileModel> job = trainModel();
            kmm = job.get();
            job.remove();
            double[] exp = new double[]{ 1.0E-100, 1.0E-100, 5.000005E-5, 0.1, 1 };
            double[] act = kmm._output._quantiles[0];
            for (int i = 0; i < (exp.length); ++i)
                Assert.assertTrue(((("Got " + (act[i])) + " but expected ") + (exp[i])), (((Math.abs(((act[i]) - (exp[i])))) / (exp[i])) < 1.0E-5));

        } finally {
            if (fr != null)
                fr.remove();

            if (kmm != null)
                kmm.delete();

        }
    }

    @Test
    public void testInterpolateLargeWeights() {
        QuantileModel kmm = null;
        Frame fr = null;
        try {
            double[][] d = new double[][]{ new double[]{ 0, 10 }, new double[]{ 1, 10 } };
            fr = ArrayUtils.frame(new String[]{ "x", "weights" }, d);
            QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
            parms._train = fr._key;
            parms._weights_column = "weights";
            parms._probs = new double[]{ 0.475, 0.48, 0.49 };
            Job<QuantileModel> job = trainModel();
            kmm = job.get();
            job.remove();
            double[] exp = new double[]{ 0.025, 0.12, 0.31 };
            double[] act = kmm._output._quantiles[0];
            for (int i = 0; i < (exp.length); ++i)
                Assert.assertTrue(((("Got " + (act[i])) + " but expected ") + (exp[i])), (((Math.abs(((act[i]) - (exp[i])))) / (exp[i])) < 1.0E-5));

        } finally {
            if (fr != null)
                fr.remove();

            if (kmm != null)
                kmm.delete();

        }
    }

    @Test
    public void testShuffled() {
        QuantileModel kmm1;
        QuantileModel kmm2;
        Frame fr1 = null;
        Frame fr2 = null;
        try {
            fr1 = parse_test_file("smalldata/junit/no_weights.csv");
            fr2 = parse_test_file("smalldata/junit/no_weights_shuffled.csv");
            for (QuantileModel.CombineMethod comb : new QuantileModel.CombineMethod[]{ CombineMethod.AVERAGE, CombineMethod.LOW, CombineMethod.HIGH, CombineMethod.INTERPOLATE }) {
                {
                    QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
                    parms._train = fr1._key;
                    parms._combine_method = comb;
                    Job<QuantileModel> job1 = trainModel();
                    kmm1 = job1.get();
                    job1.remove();
                }
                {
                    QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
                    parms._train = fr2._key;
                    parms._combine_method = comb;
                    Job<QuantileModel> job2 = trainModel();
                    kmm2 = job2.get();
                    job2.remove();
                }
                try {
                    Assert.assertTrue(Arrays.deepEquals(kmm1._output._quantiles, kmm2._output._quantiles));
                } finally {
                    if (kmm1 != null)
                        kmm1.delete();

                    if (kmm2 != null)
                        kmm2.delete();

                }
            }
        } finally {
            if (fr1 != null)
                fr1.remove();

            if (fr2 != null)
                fr2.remove();

        }
    }

    @Test
    public void testWeights0() {
        QuantileModel kmm1;
        QuantileModel kmm2;
        Frame fr1 = null;
        Frame fr2 = null;
        try {
            fr1 = parse_test_file("smalldata/junit/no_weights.csv");
            fr2 = parse_test_file("smalldata/junit/weights_all_ones.csv");
            for (QuantileModel.CombineMethod comb : new QuantileModel.CombineMethod[]{ CombineMethod.AVERAGE, CombineMethod.LOW, CombineMethod.HIGH, CombineMethod.INTERPOLATE }) {
                {
                    QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
                    parms._train = fr1._key;
                    parms._combine_method = comb;
                    parms._weights_column = null;
                    Job<QuantileModel> job1 = trainModel();
                    kmm1 = job1.get();
                    job1.remove();
                }
                {
                    QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
                    parms._train = fr2._key;
                    parms._combine_method = comb;
                    parms._weights_column = "weight";
                    Job<QuantileModel> job2 = trainModel();
                    kmm2 = job2.get();
                    job2.remove();
                }
                try {
                    Assert.assertTrue(Arrays.deepEquals(kmm1._output._quantiles, kmm2._output._quantiles));
                } finally {
                    if (kmm1 != null)
                        kmm1.delete();

                    if (kmm2 != null)
                        kmm2.delete();

                }
            }
        } finally {
            if (fr1 != null)
                fr1.remove();

            if (fr2 != null)
                fr2.remove();

        }
    }

    @Test
    public void testWeights1() {
        QuantileModel kmm1;
        QuantileModel kmm2;
        Frame fr1 = null;
        Frame fr2 = null;
        try {
            fr1 = parse_test_file("smalldata/junit/no_weights.csv");
            fr2 = parse_test_file("smalldata/junit/weights.csv");
            for (QuantileModel.CombineMethod comb : new QuantileModel.CombineMethod[]{ CombineMethod.AVERAGE, CombineMethod.LOW, CombineMethod.HIGH, CombineMethod.INTERPOLATE }) {
                {
                    QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
                    parms._train = fr1._key;
                    parms._combine_method = comb;
                    parms._weights_column = null;
                    Job<QuantileModel> job1 = trainModel();
                    kmm1 = job1.get();
                    job1.remove();
                }
                {
                    QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
                    parms._train = fr2._key;
                    parms._combine_method = comb;
                    parms._weights_column = "weight";
                    Job<QuantileModel> job2 = trainModel();
                    kmm2 = job2.get();
                    job2.remove();
                }
                try {
                    Assert.assertTrue(Arrays.deepEquals(kmm1._output._quantiles, kmm2._output._quantiles));
                } finally {
                    if (kmm1 != null)
                        kmm1.delete();

                    if (kmm2 != null)
                        kmm2.delete();

                }
            }
        } finally {
            if (fr1 != null)
                fr1.remove();

            if (fr2 != null)
                fr2.remove();

        }
    }
}

