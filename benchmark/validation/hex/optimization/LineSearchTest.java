package hex.optimization;


import hex.optimization.OptimizationUtils.GradientInfo;
import hex.optimization.OptimizationUtils.GradientSolver;
import hex.optimization.OptimizationUtils.MoreThuente;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;


/**
 * Created by tomasnykodym on 9/29/15.
 */
public class LineSearchTest extends TestUtil {
    @Test
    public void testMoreThuenteMethod() {
        GradientSolver f = new GradientSolver() {
            @Override
            public GradientInfo getGradient(double[] beta) {
                GradientInfo ginfo = new GradientInfo(0, new double[1]);
                double x = beta[0];
                double b = 2;
                double xx = x * x;
                ginfo._gradient[0] = (xx - b) / ((b + xx) * (b + xx));
                ginfo._objVal = (-x) / (xx + b);
                return ginfo;
            }

            @Override
            public GradientInfo getObjective(double[] beta) {
                return getGradient(beta);
            }
        };
        double stp = 1;
        double x = 100;
        MoreThuente ls = setInitialStep(stp);
        boolean succ = ls.evaluate(new double[]{ -1 });
        Assert.assertTrue(succ);
        Assert.assertEquals(1, ls._returnStatus);
        Assert.assertEquals(18, ls.nfeval());
        Assert.assertEquals((-0.35355), ls.ginfo()._objVal, 1.0E-5);
        Assert.assertEquals(98586, Math.round((1000 * (ls.step()))), 1.0E-5);
        x = 0;
        stp = 100;
        ls = new OptimizationUtils.MoreThuente(f, new double[]{ x }, f.getGradient(new double[]{ x }), 0.1, 0.1, 1.0E-5).setInitialStep(stp);
        succ = ls.evaluate(new double[]{ 1 });
        Assert.assertTrue(succ);
        Assert.assertEquals(ls._returnStatus, 1);
        Assert.assertEquals(5, ls.nfeval());
        Assert.assertEquals((-0.34992), ls.ginfo()._objVal, 1.0E-5);
        Assert.assertEquals(1.6331, ls.step(), 1.0E-5);
        x = 0;
        stp = 10;
        ls = new OptimizationUtils.MoreThuente(f, new double[]{ x }, f.getGradient(new double[]{ x }), 0.001, 0.1, 1.0E-5).setInitialStep(stp);
        succ = ls.evaluate(new double[]{ 1 });
        Assert.assertTrue(succ);
        Assert.assertEquals(1, ls.nfeval());
        x = 0;
        stp = 1000;
        ls = new OptimizationUtils.MoreThuente(f, new double[]{ x }, f.getGradient(new double[]{ x }), 0.001, 0.1, 1.0E-5).setInitialStep(stp);
        succ = ls.evaluate(new double[]{ 1 });
        Assert.assertEquals(ls._returnStatus, 1);
        Assert.assertTrue(succ);
        Assert.assertEquals(4, ls.nfeval());
        Assert.assertEquals(37, Math.round(ls.step()));
        x = 0;
        stp = 0.001;
        ls = new OptimizationUtils.MoreThuente(f, new double[]{ x }, f.getGradient(new double[]{ x }), 0.001, 0.1, 1.0E-5).setInitialStep(stp);
        succ = ls.evaluate(new double[]{ 1 });
        Assert.assertEquals(ls._returnStatus, 1);
        Assert.assertTrue(succ);
        Assert.assertEquals(6, ls.nfeval());
        Assert.assertEquals(14, Math.round((10 * (ls.step()))));
        f = new GradientSolver() {
            @Override
            public GradientInfo getGradient(double[] beta) {
                GradientInfo ginfo = new GradientInfo(0, new double[1]);
                double x = beta[0];
                double b = 0.004;
                ginfo._objVal = (Math.pow((x + b), 5)) - (2 * (Math.pow((x + b), 4)));
                ginfo._gradient[0] = (Math.pow((b + x), 3)) * ((5 * (b + x)) - 8);
                return ginfo;
            }

            @Override
            public GradientInfo getObjective(double[] beta) {
                return getGradient(beta);
            }
        };
        ls = new OptimizationUtils.MoreThuente(f, new double[]{ x }, f.getGradient(new double[]{ x }), 0.1, 0.1, 1.0E-5).setInitialStep(stp);
        succ = ls.evaluate(new double[]{ 1 });
        Assert.assertTrue(succ);
        Assert.assertEquals(ls._returnStatus, 1);
        Assert.assertEquals(12, ls.nfeval());
        Assert.assertEquals(16, Math.round((10 * (ls.step()))));
        stp = 0.1;
        ls = new OptimizationUtils.MoreThuente(f, new double[]{ x }, f.getGradient(new double[]{ x }), 0.1, 0.1, 1.0E-5).setInitialStep(stp);
        succ = ls.evaluate(new double[]{ 1 });
        Assert.assertTrue(succ);
        Assert.assertEquals(8, ls.nfeval());
        Assert.assertEquals(16, Math.round((10 * (ls.step()))));
        stp = 10;
        ls = new OptimizationUtils.MoreThuente(f, new double[]{ x }, f.getGradient(new double[]{ x }), 0.1, 0.1, 1.0E-5).setInitialStep(stp);
        succ = ls.evaluate(new double[]{ 1 });
        Assert.assertTrue(succ);
        Assert.assertEquals(ls._returnStatus, 1);
        Assert.assertEquals(8, ls.nfeval());
        Assert.assertEquals(16, Math.round((10 * (ls.step()))));
        stp = 1000;
        ls = new OptimizationUtils.MoreThuente(f, new double[]{ x }, f.getGradient(new double[]{ x }), 0.1, 0.1, 1.0E-5).setInitialStep(stp);
        succ = ls.evaluate(new double[]{ 1 });
        Assert.assertTrue(succ);
        Assert.assertEquals(ls._returnStatus, 1);
        Assert.assertEquals(11, ls.nfeval());
        Assert.assertEquals(16, Math.round((10 * (ls.step()))));
        f = new GradientSolver() {
            final double beta = 0.01;

            final double l = 39;

            double phi0(double x) {
                if (x <= (1 - (beta))) {
                    return 1 - x;
                } else
                    if (x >= (1 + (beta))) {
                        return x - 1;
                    } else {
                        return 0.5 * ((((x - 1) * (x - 1)) / (beta)) + (beta));
                    }

            }

            double phi0Prime(double x) {
                if (x <= (1 - (beta))) {
                    return -1;
                } else
                    if (x >= (1 + (beta))) {
                        return 1;
                    } else {
                        return (x - 1) / (beta);// .5*((x-1)*(x-1)/beta + beta);

                    }

            }

            @Override
            public GradientInfo getGradient(double[] ary) {
                GradientInfo ginfo = new GradientInfo(0, new double[1]);
                double x = ary[0];
                double a = (2 * (1 - (beta))) / ((Math.PI) * (l));
                double b = (0.5 * (l)) * (Math.PI);
                ginfo._objVal = (phi0(x)) + (a * (Math.sin((b * x))));
                ginfo._gradient[0] = (phi0Prime(x)) + ((a * b) * (Math.cos((b * x))));
                return ginfo;
            }

            @Override
            public GradientInfo getObjective(double[] beta) {
                return getGradient(beta);
            }
        };
        stp = 0.001;
        ls = new OptimizationUtils.MoreThuente(f, new double[]{ x }, f.getGradient(new double[]{ x }), 0.1, 0.1, 1.0E-5).setInitialStep(stp);
        succ = ls.evaluate(new double[]{ 1 });
        Assert.assertEquals(ls._returnStatus, 1);
        Assert.assertTrue(succ);
        Assert.assertEquals(12, ls.nfeval());
        Assert.assertEquals(10, Math.round((10 * (ls.step()))));
        stp = 0.1;
        ls = new OptimizationUtils.MoreThuente(f, new double[]{ x }, f.getGradient(new double[]{ x }), 0.1, 0.1, 1.0E-5).setInitialStep(stp);
        succ = ls.evaluate(new double[]{ 1 });
        Assert.assertTrue(succ);
        Assert.assertEquals(ls._returnStatus, 1);
        Assert.assertEquals(12, ls.nfeval());
        Assert.assertEquals(10, Math.round((10 * (ls.step()))));
        stp = 10;
        ls = new OptimizationUtils.MoreThuente(f, new double[]{ x }, f.getGradient(new double[]{ x }), 0.1, 0.1, 1.0E-5).setInitialStep(stp);
        succ = ls.evaluate(new double[]{ 1 });
        Assert.assertEquals(ls._returnStatus, 1);
        Assert.assertTrue(succ);
        Assert.assertEquals(10, ls.nfeval());
        Assert.assertEquals(10, Math.round((10 * (ls.step()))));
        stp = 1000;
        ls = new OptimizationUtils.MoreThuente(f, new double[]{ x }, f.getGradient(new double[]{ x }), 0.1, 0.1, 1.0E-5).setInitialStep(stp);
        succ = ls.evaluate(new double[]{ 1 });
        Assert.assertTrue(succ);
        Assert.assertEquals(ls._returnStatus, 1);
        Assert.assertEquals(13, ls.nfeval());
        Assert.assertEquals(10, Math.round((10 * (ls.step()))));
        f = new LineSearchTest.F(0.001, 0.001);
        stp = 0.001;
        ls = new OptimizationUtils.MoreThuente(f, new double[]{ x }, f.getGradient(new double[]{ x }), 0.001, 0.001, 1.0E-5).setInitialStep(stp);
        succ = ls.evaluate(new double[]{ 1 });
        Assert.assertTrue(succ);
        Assert.assertEquals(ls._returnStatus, 1);
        Assert.assertEquals(4, ls.nfeval());
        Assert.assertEquals(9, Math.round((100 * (ls.step()))));
        stp = 0.1;
        ls = new OptimizationUtils.MoreThuente(f, new double[]{ x }, f.getGradient(new double[]{ x }), 0.001, 0.001, 1.0E-5).setInitialStep(stp);
        succ = ls.evaluate(new double[]{ 1 });
        Assert.assertTrue(succ);
        Assert.assertEquals(ls._returnStatus, 1);
        Assert.assertEquals(1, ls.nfeval());
        Assert.assertEquals(10, Math.round((100 * (ls.step()))));
        stp = 10;
        ls = new OptimizationUtils.MoreThuente(f, new double[]{ x }, f.getGradient(new double[]{ x }), 0.001, 0.001, 1.0E-5).setInitialStep(stp);
        succ = ls.evaluate(new double[]{ 1 });
        Assert.assertTrue(succ);
        Assert.assertEquals(3, ls.nfeval());
        Assert.assertEquals(35, Math.round((100 * (ls.step()))));
        stp = 1000;
        ls = new OptimizationUtils.MoreThuente(f, new double[]{ x }, f.getGradient(new double[]{ x }), 0.001, 0.001, 1.0E-5).setInitialStep(stp);
        succ = ls.evaluate(new double[]{ 1 });
        Assert.assertTrue(succ);
        Assert.assertEquals(ls._returnStatus, 1);
        Assert.assertEquals(4, ls.nfeval());
        Assert.assertEquals(83, Math.round((100 * (ls.step()))));
        f = new LineSearchTest.F(0.01, 0.001);
        stp = 0.001;
        ls = new OptimizationUtils.MoreThuente(f, new double[]{ x }, f.getGradient(new double[]{ x }), 0.001, 0.001, 1.0E-5).setInitialStep(stp);
        succ = ls.evaluate(new double[]{ 1 });
        Assert.assertTrue(succ);
        Assert.assertEquals(6, ls.nfeval());
        Assert.assertEquals(75, Math.round((1000 * (ls.step()))));
        stp = 0.1;
        ls = new OptimizationUtils.MoreThuente(f, new double[]{ x }, f.getGradient(new double[]{ x }), 0.001, 0.001, 1.0E-5).setInitialStep(stp);
        succ = ls.evaluate(new double[]{ 1 });
        Assert.assertTrue(succ);
        Assert.assertEquals(ls._returnStatus, 1);
        Assert.assertEquals(3, ls.nfeval());
        Assert.assertEquals(78, Math.round((1000 * (ls.step()))));
        stp = 10;
        ls = new OptimizationUtils.MoreThuente(f, new double[]{ x }, f.getGradient(new double[]{ x }), 0.001, 0.001, 1.0E-5).setInitialStep(stp);
        succ = ls.evaluate(new double[]{ 1 });
        Assert.assertTrue(succ);
        Assert.assertEquals(ls._returnStatus, 1);
        Assert.assertEquals(7, ls.nfeval());
        Assert.assertEquals(73, Math.round((1000 * (ls.step()))));
        stp = 1000;
        ls = new OptimizationUtils.MoreThuente(f, new double[]{ x }, f.getGradient(new double[]{ x }), 0.001, 0.001, 1.0E-5).setInitialStep(stp);
        succ = ls.evaluate(new double[]{ 1 });
        Assert.assertTrue(succ);
        Assert.assertEquals(ls._returnStatus, 1);
        Assert.assertEquals(8, ls.nfeval());
        Assert.assertEquals(76, Math.round((1000 * (ls.step()))));
        f = new LineSearchTest.F(0.001, 0.01);
        stp = 0.001;
        ls = new OptimizationUtils.MoreThuente(f, new double[]{ x }, f.getGradient(new double[]{ x }), 0.001, 0.001, 1.0E-5).setInitialStep(stp);
        succ = ls.evaluate(new double[]{ 1 });
        Assert.assertTrue(succ);
        Assert.assertEquals(ls._returnStatus, 1);
        Assert.assertEquals(13, ls.nfeval());
        Assert.assertEquals(93, Math.round((100 * (ls.step()))));
        stp = 0.1;
        ls = new OptimizationUtils.MoreThuente(f, new double[]{ x }, f.getGradient(new double[]{ x }), 0.001, 0.001, 1.0E-5).setInitialStep(stp);
        succ = ls.evaluate(new double[]{ 1 });
        Assert.assertTrue(succ);
        Assert.assertEquals(ls._returnStatus, 1);
        Assert.assertEquals(11, ls.nfeval());
        Assert.assertEquals(93, Math.round((100 * (ls.step()))));
        stp = 10;
        ls = new OptimizationUtils.MoreThuente(f, new double[]{ x }, f.getGradient(new double[]{ x }), 0.001, 0.001, 1.0E-5).setInitialStep(stp);
        succ = ls.evaluate(new double[]{ 1 });
        Assert.assertTrue(succ);
        Assert.assertEquals(8, ls.nfeval());
        Assert.assertEquals(92, Math.round((100 * (ls.step()))));
        stp = 1000;
        ls = new OptimizationUtils.MoreThuente(f, new double[]{ x }, f.getGradient(new double[]{ x }), 0.001, 0.001, 1.0E-5).setInitialStep(stp);
        succ = ls.evaluate(new double[]{ 1 });
        Assert.assertTrue(succ);
        Assert.assertEquals(ls._returnStatus, 1);
        Assert.assertEquals(11, ls.nfeval());
        Assert.assertEquals(92, Math.round((100 * (ls.step()))));
    }

    private static class F implements GradientSolver {
        final double a;

        final double b;

        public F(double a, double b) {
            this.a = a;
            this.b = b;
        }

        private double gamma(double x) {
            return (Math.sqrt(((x * x) + 1))) - x;
        }

        @Override
        public GradientInfo getGradient(double[] beta) {
            double x = beta[0];
            double ga = gamma(a);
            double gb = gamma(b);
            GradientInfo ginfo = new GradientInfo(0, new double[1]);
            ginfo._objVal = (ga * (Math.sqrt((((1 - x) * (1 - x)) + ((b) * (b)))))) + (gb * (Math.sqrt(((x * x) + ((a) * (a))))));
            ginfo._gradient[0] = ((ga * (x - 1)) / (Math.sqrt((((1 - x) * (1 - x)) + ((b) * (b)))))) + ((gb * x) / (Math.sqrt(((x * x) + ((a) * (a))))));
            return ginfo;
        }

        @Override
        public GradientInfo getObjective(double[] beta) {
            return getGradient(beta);
        }
    }
}

