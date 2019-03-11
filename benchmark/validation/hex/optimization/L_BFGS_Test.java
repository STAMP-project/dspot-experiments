package hex.optimization;


import DataInfo.TransformType;
import Family.binomial;
import L_BFGS.Result;
import hex.DataInfo;
import hex.glm.GLM.GLMGradientSolver;
import hex.glm.GLMModel.GLMParameters;
import hex.glm.GLMModel.GLMParameters.Family;
import hex.optimization.OptimizationUtils.GradientInfo;
import hex.optimization.OptimizationUtils.GradientSolver;
import org.junit.Assert;
import org.junit.Test;
import water.fvec.Frame;
import water.util.ArrayUtils;


/**
 * Created by tomasnykodym on 9/16/14.
 */
public class L_BFGS_Test extends TestUtil {
    // test on Rosenbrock's function (known optimum at (a,a^2), minimum = 0)
    @Test
    public void rosenbrock() {
        final double a = 1;
        final double b = 100;
        GradientSolver gs = new GradientSolver() {
            @Override
            public GradientInfo getGradient(double[] beta) {
                final double[] g = new double[2];
                final double x = beta[0];
                final double y = beta[1];
                final double xx = x * x;
                g[0] = (((-2) * a) + (2 * x)) - ((4 * b) * ((y * x) - (x * xx)));
                g[1] = (2 * b) * (y - xx);
                double objVal = ((a - x) * (a - x)) + ((b * (y - xx)) * (y - xx));
                return new GradientInfo(objVal, g);
            }

            @Override
            public GradientInfo getObjective(double[] beta) {
                return getGradient(beta);
            }
        };
        L_BFGS lbfgs = new L_BFGS().setGradEps(1.0E-12);
        L_BFGS.Result r = lbfgs.solve(gs, L_BFGS.startCoefs(2, 987654321));
        Assert.assertTrue("LBFGS failed to solve Rosenbrock function optimization", ((r.ginfo._objVal) < 1.0E-4));
    }

    @Test
    public void logistic() {
        Key parsedKey = Key.make("prostate");
        DataInfo dinfo = null;
        try {
            GLMParameters glmp = new GLMParameters(Family.binomial, binomial.defaultLink);
            glmp._alpha = new double[]{ 0 };
            glmp._lambda = new double[]{ 1.0E-5 };
            Frame source = parse_test_file(parsedKey, "smalldata/glm_test/prostate_cat_replaced.csv");
            source.add("CAPSULE", source.remove("CAPSULE"));
            source.remove("ID").remove();
            Frame valid = new Frame(source._names.clone(), source.vecs().clone());
            dinfo = /* weights */
            /* offset */
            /* fold */
            new DataInfo(source, valid, 1, false, TransformType.STANDARDIZE, TransformType.NONE, true, false, false, false, false, false);
            DKV.put(dinfo._key, dinfo);
            glmp._obj_reg = 1 / 380.0;
            GLMGradientSolver solver = new GLMGradientSolver(null, glmp, dinfo, 1.0E-5, null);
            L_BFGS lbfgs = new L_BFGS().setGradEps(1.0E-8);
            double[] beta = MemoryManager.malloc8d(((dinfo.fullN()) + 1));
            beta[((beta.length) - 1)] = new hex.glm.GLMModel.GLMWeightsFun(glmp).link(source.vec("CAPSULE").mean());
            L_BFGS.Result r = lbfgs.solve(solver, beta, solver.getGradient(beta), new L_BFGS.ProgressMonitor() {
                int _i = 0;

                public boolean progress(double[] beta, GradientInfo ginfo) {
                    System.out.println((((((++(_i)) + ":") + (ginfo._objVal)) + ", ") + (ArrayUtils.l2norm2(ginfo._gradient, false))));
                    return true;
                }
            });
            Assert.assertEquals(378.34, ((2 * (r.ginfo._objVal)) * (source.numRows())), 0.1);
        } finally {
            if (dinfo != null)
                DKV.remove(dinfo._key);

            Value v = DKV.get(parsedKey);
            if (v != null) {
                v.<Frame>get().delete();
            }
        }
    }

    // Test LSM on arcene - wide dataset with ~10k columns
    // test warm start and max #iteratoions
    @Test
    public void testArcene() {
        Key parsedKey = Key.make("arcene_parsed");
        DataInfo dinfo = null;
        try {
            Frame source = parse_test_file(parsedKey, "smalldata/glm_test/arcene.csv");
            Frame valid = new Frame(source._names.clone(), source.vecs().clone());
            GLMParameters glmp = new GLMParameters(Family.gaussian);
            glmp._lambda = new double[]{ 1.0E-5 };
            glmp._alpha = new double[]{ 0 };
            glmp._obj_reg = 0.01;
            dinfo = /* weights */
            /* offset */
            /* fold */
            new DataInfo(source, valid, 1, false, TransformType.STANDARDIZE, TransformType.NONE, true, false, false, false, false, false);
            DKV.put(dinfo._key, dinfo);
            GradientSolver solver = new GLMGradientSolver(null, glmp, dinfo, 1.0E-5, null);
            L_BFGS lbfgs = new L_BFGS().setMaxIter(20);
            double[] beta = MemoryManager.malloc8d(((dinfo.fullN()) + 1));
            beta[((beta.length) - 1)] = new hex.glm.GLMModel.GLMWeightsFun(glmp).link(source.lastVec().mean());
            L_BFGS.Result r1 = lbfgs.solve(solver, beta.clone(), solver.getGradient(beta), new L_BFGS.ProgressMonitor() {
                int _i = 0;

                public boolean progress(double[] beta, GradientInfo ginfo) {
                    System.out.println((((++(_i)) + ":") + (ginfo._objVal)));
                    return true;
                }
            });
            lbfgs.setMaxIter(50);
            final int iter = r1.iter;
            L_BFGS.Result r2 = lbfgs.solve(solver, r1.coefs, r1.ginfo, new L_BFGS.ProgressMonitor() {
                int _i = 0;

                public boolean progress(double[] beta, GradientInfo ginfo) {
                    System.out.println(((((iter + " + ") + (++(_i))) + ":") + (ginfo._objVal)));
                    return true;
                }
            });
            System.out.println();
            lbfgs = new L_BFGS().setMaxIter(100);
            L_BFGS.Result r3 = lbfgs.solve(solver, beta.clone(), solver.getGradient(beta), new L_BFGS.ProgressMonitor() {
                int _i = 0;

                public boolean progress(double[] beta, GradientInfo ginfo) {
                    System.out.println((((((++(_i)) + ":") + (ginfo._objVal)) + ", ") + (ArrayUtils.l2norm2(ginfo._gradient, false))));
                    return true;
                }
            });
            Assert.assertEquals(r1.iter, 20);
            // assertEquals (r1.iter + r2.iter,r3.iter); // should be equal? got mismatch by 2
            Assert.assertEquals(r2.ginfo._objVal, r3.ginfo._objVal, 1.0E-8);
            Assert.assertEquals((((0.5 * (glmp._lambda[0])) * (ArrayUtils.l2norm(r3.coefs, true))) + (r3.ginfo._objVal)), 1.0E-4, 5.0E-4);
            Assert.assertTrue(("iter# expected < 100, got " + (r3.iter)), ((r3.iter) < 100));
        } finally {
            if (dinfo != null)
                DKV.remove(dinfo._key);

            Value v = DKV.get(parsedKey);
            if (v != null) {
                v.<Frame>get().delete();
            }
        }
    }
}

