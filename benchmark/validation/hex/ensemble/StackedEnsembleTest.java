package hex.ensemble;


import DistributionFamily.bernoulli;
import DistributionFamily.gaussian;
import DistributionFamily.multinomial;
import GBMModel.GBMParameters;
import GLMModel.GLMParameters;
import GLMModel.GLMParameters.Family;
import H2O.ARGS;
import Metalearner.Algorithm.AUTO;
import Metalearner.Algorithm.deeplearning;
import Metalearner.Algorithm.drf;
import Metalearner.Algorithm.gbm;
import Metalearner.Algorithm.glm;
import Model.Parameters.FoldAssignmentScheme;
import hex.GLMHelper;
import hex.Model;
import hex.SplitFrame;
import hex.ensemble.StackedEnsembleModel.StackedEnsembleParameters;
import hex.genmodel.utils.DistributionFamily;
import hex.glm.GLM;
import hex.glm.GLMModel;
import hex.grid.Grid;
import hex.grid.GridSearch;
import hex.splitframe.ShuffleSplitFrame;
import hex.tree.gbm.GBMModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import water.fvec.Chunk;
import water.fvec.Frame;
import water.fvec.NewChunk;
import water.fvec.Vec;
import water.util.ArrayUtils;

import static water.TestUtil.<init>;


public class StackedEnsembleTest extends TestUtil {
    private abstract class PrepData {
        abstract int prep(Frame fr);
    }

    static final String[] ignored_aircols = new String[]{ "DepTime", "ArrTime", "AirTime", "ArrDelay", "DepDelay", "TaxiIn", "TaxiOut", "Cancelled", "CancellationCode", "Diverted", "CarrierDelay", "WeatherDelay", "NASDelay", "SecurityDelay", "LateAircraftDelay", "IsDepDelayed" };

    @Test
    public void testBasicEnsembleAUTOMetalearner() {
        basicEnsemble("./smalldata/junit/cars.csv", null, new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                fr.remove("name").remove();
                return ~(fr.find("economy (mpg)"));
            }
        }, false, gaussian, AUTO, false);
        basicEnsemble("./smalldata/airlines/allyears2k_headers.zip", null, new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                for (String s : StackedEnsembleTest.ignored_aircols)
                    fr.remove(s).remove();

                return fr.find("IsArrDelayed");
            }
        }, false, bernoulli, AUTO, false);
        basicEnsemble("./smalldata/iris/iris_wheader.csv", null, new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                return fr.find("class");
            }
        }, false, multinomial, AUTO, false);
        basicEnsemble("./smalldata/logreg/prostate_train.csv", "./smalldata/logreg/prostate_test.csv", new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                return fr.find("CAPSULE");
            }
        }, false, bernoulli, AUTO, false);
    }

    @Test
    public void testBasicEnsembleGBMMetalearner() {
        basicEnsemble("./smalldata/junit/cars.csv", null, new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                fr.remove("name").remove();
                return ~(fr.find("economy (mpg)"));
            }
        }, false, gaussian, gbm, false);
        basicEnsemble("./smalldata/airlines/allyears2k_headers.zip", null, new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                for (String s : StackedEnsembleTest.ignored_aircols)
                    fr.remove(s).remove();

                return fr.find("IsArrDelayed");
            }
        }, false, bernoulli, gbm, false);
        basicEnsemble("./smalldata/iris/iris_wheader.csv", null, new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                return fr.find("class");
            }
        }, false, multinomial, gbm, false);
        basicEnsemble("./smalldata/logreg/prostate_train.csv", "./smalldata/logreg/prostate_test.csv", new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                return fr.find("CAPSULE");
            }
        }, false, bernoulli, gbm, false);
    }

    @Test
    public void testBasicEnsembleDRFMetalearner() {
        basicEnsemble("./smalldata/junit/cars.csv", null, new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                fr.remove("name").remove();
                return ~(fr.find("economy (mpg)"));
            }
        }, false, gaussian, drf, false);
        basicEnsemble("./smalldata/airlines/allyears2k_headers.zip", null, new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                for (String s : StackedEnsembleTest.ignored_aircols)
                    fr.remove(s).remove();

                return fr.find("IsArrDelayed");
            }
        }, false, bernoulli, drf, false);
        basicEnsemble("./smalldata/iris/iris_wheader.csv", null, new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                return fr.find("class");
            }
        }, false, multinomial, drf, false);
        basicEnsemble("./smalldata/logreg/prostate_train.csv", "./smalldata/logreg/prostate_test.csv", new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                return fr.find("CAPSULE");
            }
        }, false, bernoulli, drf, false);
    }

    @Test
    public void testBasicEnsembleDeepLearningMetalearner() {
        basicEnsemble("./smalldata/junit/cars.csv", null, new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                fr.remove("name").remove();
                return ~(fr.find("economy (mpg)"));
            }
        }, false, gaussian, deeplearning, false);
        basicEnsemble("./smalldata/airlines/allyears2k_headers.zip", null, new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                for (String s : StackedEnsembleTest.ignored_aircols)
                    fr.remove(s).remove();

                return fr.find("IsArrDelayed");
            }
        }, false, bernoulli, deeplearning, false);
        basicEnsemble("./smalldata/iris/iris_wheader.csv", null, new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                return fr.find("class");
            }
        }, false, multinomial, deeplearning, false);
        basicEnsemble("./smalldata/logreg/prostate_train.csv", "./smalldata/logreg/prostate_test.csv", new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                return fr.find("CAPSULE");
            }
        }, false, bernoulli, deeplearning, false);
    }

    @Test
    public void testBasicEnsembleGLMMetalearner() {
        // Regression tests
        basicEnsemble("./smalldata/junit/cars.csv", null, new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                fr.remove("name").remove();
                return ~(fr.find("economy (mpg)"));
            }
        }, false, gaussian, glm, false);
        // Binomial tests
        basicEnsemble("./smalldata/junit/test_tree_minmax.csv", null, new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                return fr.find("response");
            }
        }, false, bernoulli, glm, false);
        basicEnsemble("./smalldata/logreg/prostate.csv", null, new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("CAPSULE");
            }
        }, false, bernoulli, glm, false);
        basicEnsemble("./smalldata/logreg/prostate_train.csv", "./smalldata/logreg/prostate_test.csv", new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                return fr.find("CAPSULE");
            }
        }, false, bernoulli, glm, false);
        basicEnsemble("./smalldata/gbm_test/alphabet_cattest.csv", null, new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                return fr.find("y");
            }
        }, false, bernoulli, glm, false);
        basicEnsemble("./smalldata/airlines/allyears2k_headers.zip", null, new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                for (String s : StackedEnsembleTest.ignored_aircols)
                    fr.remove(s).remove();

                return fr.find("IsArrDelayed");
            }
        }, false, bernoulli, glm, false);
        // Multinomial tests
        basicEnsemble("./smalldata/logreg/prostate.csv", null, new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("RACE");
            }
        }, false, multinomial, glm, false);
        basicEnsemble("./smalldata/junit/cars.csv", null, new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                fr.remove("name").remove();
                return fr.find("cylinders");
            }
        }, false, multinomial, glm, false);
        basicEnsemble("./smalldata/iris/iris_wheader.csv", null, new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                return fr.find("class");
            }
        }, false, multinomial, glm, false);
    }

    @Test
    public void testPubDev6157() {
        try {
            Scope.enter();
            // 1. Create synthetic training frame and train multinomial GLM
            Vec v = Vec.makeConN(((long) (100000.0)), ((ARGS.nthreads) * 4));
            Scope.track(v);
            final int nclasses = 4;// #of response classes in iris_wheader + 1

            byte[] types = new byte[]{ Vec.T_NUM, Vec.T_CAT };
            String[][] domains = new String[types.length][];
            domains[((domains.length) - 1)] = new String[nclasses];
            for (int i = 0; i < nclasses; i++)
                domains[((domains.length) - 1)][i] = "Level" + i;

            final Frame training = new MRTask() {
                @Override
                public void map(Chunk[] cs, NewChunk[] ncs) {
                    Random r = new Random();
                    NewChunk predictor = ncs[0];
                    NewChunk response = ncs[1];
                    for (int i = 0; i < (cs[0]._len); i++) {
                        long rowNum = (cs[0].start()) + i;
                        predictor.addNum(r.nextDouble());// noise

                        long respValue;
                        if ((rowNum % 2) == 0) {
                            respValue = nclasses - 1;
                        } else {
                            respValue = rowNum % nclasses;
                        }
                        response.addNum(respValue);// more than 50% rows have last class as the response value

                    }
                }
            }.doAll(types, v).outputFrame(Key.<Frame>make(), null, domains);
            Scope.track(training);
            GLMModel.GLMParameters parms = new GLMModel.GLMParameters();
            parms._train = training._key;
            parms._response_column = training.lastVecName();
            parms._family = Family.multinomial;
            parms._max_iterations = 1;
            parms._seed = 42;
            parms._auto_rebalance = false;
            GLM glm = new GLM(parms);
            final GLMModel model = glm.trainModelOnH2ONode().get();
            Scope.track_generic(model);
            Assert.assertNotNull(model);
            final Job j = new Job(Key.make(), parms.javaName(), parms.algoName());
            j.start(new H2O.H2OCountedCompleter() {
                @Override
                public void compute2() {
                    GLMHelper.runBigScore(model, training, false, false, j);
                    tryComplete();
                }
            }, 1).get();
            // 2. Train multinomial Stacked Ensembles with GLM metalearner - it should not crash
            basicEnsemble("./smalldata/iris/iris_wheader.csv", null, new StackedEnsembleTest.PrepData() {
                int prep(Frame fr) {
                    return fr.find("class");
                }
            }, false, multinomial, glm, false);
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testBlending() {
        basicEnsemble("./smalldata/junit/cars.csv", null, new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                fr.remove("name").remove();
                return ~(fr.find("economy (mpg)"));
            }
        }, false, gaussian, AUTO, true);
        basicEnsemble("./smalldata/airlines/allyears2k_headers.zip", null, new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                for (String s : StackedEnsembleTest.ignored_aircols)
                    fr.remove(s).remove();

                return fr.find("IsArrDelayed");
            }
        }, false, bernoulli, AUTO, true);
        basicEnsemble("./smalldata/iris/iris_wheader.csv", null, new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                return fr.find("class");
            }
        }, false, multinomial, AUTO, true);
        basicEnsemble("./smalldata/logreg/prostate_train.csv", "./smalldata/logreg/prostate_test.csv", new StackedEnsembleTest.PrepData() {
            int prep(Frame fr) {
                return fr.find("CAPSULE");
            }
        }, false, bernoulli, AUTO, true);
    }

    @Test
    public void testBaseModelPredictionsCaching() {
        Grid grid = null;
        List<StackedEnsembleModel> seModels = new ArrayList<>();
        List<Frame> frames = new ArrayList<>();
        try {
            Scope.enter();
            long seed = (6 << 6) << 6;
            Frame train = parse_test_file("./smalldata/junit/cars.csv");
            String target = "economy (mpg)";
            Frame[] splits = ShuffleSplitFrame.shuffleSplitFrame(train, new Key[]{ Key.make(((train._key) + "_train")), Key.make(((train._key) + "_blending")), Key.make(((train._key) + "_valid")) }, new double[]{ 0.5, 0.3, 0.2 }, seed);
            train.remove();
            train = splits[0];
            Frame blend = splits[1];
            Frame valid = splits[2];
            frames.addAll(Arrays.asList(train, blend, valid));
            // generate a few base models
            GBMModel.GBMParameters params = new GBMModel.GBMParameters();
            params._distribution = DistributionFamily.gaussian;
            params._train = train._key;
            params._response_column = target;
            params._seed = seed;
            Job<Grid> gridSearch = GridSearch.startGridSearch(null, params, new HashMap<String, Object[]>() {
                {
                    put("_ntrees", new Integer[]{ 3, 5 });
                    put("_learn_rate", new Double[]{ 0.1, 0.2 });
                }
            });
            grid = gridSearch.get();
            Model[] models = grid.getModels();
            Assert.assertEquals(4, models.length);
            StackedEnsembleParameters seParams = new StackedEnsembleParameters();
            seParams._distribution = DistributionFamily.bernoulli;
            seParams._train = train._key;
            seParams._blending = blend._key;
            seParams._response_column = target;
            seParams._base_models = grid.getModelKeys();
            seParams._seed = seed;
            // running a first blending SE without keeping predictions
            seParams._keep_base_model_predictions = false;
            StackedEnsemble seJob = new StackedEnsemble(seParams);
            StackedEnsembleModel seModel = seJob.trainModel().get();
            seModels.add(seModel);
            Assert.assertNull(seModel._output._base_model_predictions_keys);
            // running another SE, but this time caching predictions
            seParams._keep_base_model_predictions = true;
            seJob = new StackedEnsemble(seParams);
            seModel = seJob.trainModel().get();
            seModels.add(seModel);
            Assert.assertNotNull(seModel._output._base_model_predictions_keys);
            Assert.assertEquals(models.length, seModel._output._base_model_predictions_keys.length);
            water.Key<Frame>[] first_se_pred_keys = seModel._output._base_model_predictions_keys;
            for (Key k : first_se_pred_keys) {
                Assert.assertNotNull("prediction key is not stored in DKV", DKV.get(k));
            }
            // running again another SE, caching predictions, and checking that no new prediction is created
            seParams._keep_base_model_predictions = true;
            seJob = new StackedEnsemble(seParams);
            seModel = seJob.trainModel().get();
            seModels.add(seModel);
            Assert.assertNotNull(seModel._output._base_model_predictions_keys);
            Assert.assertEquals(models.length, seModel._output._base_model_predictions_keys.length);
            Assert.assertArrayEquals(first_se_pred_keys, seModel._output._base_model_predictions_keys);
            // running a last SE, with validation frame, and check that new predictions are added
            seParams._keep_base_model_predictions = true;
            seParams._valid = valid._key;
            seJob = new StackedEnsemble(seParams);
            seModel = seJob.trainModel().get();
            seModels.add(seModel);
            Assert.assertNotNull(seModel._output._base_model_predictions_keys);
            Assert.assertEquals(((models.length) * 2), seModel._output._base_model_predictions_keys.length);
            for (Key<Frame> first_pred_key : first_se_pred_keys) {
                Assert.assertTrue(ArrayUtils.contains(seModel._output._base_model_predictions_keys, first_pred_key));
            }
            seModel.deleteBaseModelPredictions();
            Assert.assertNull(seModel._output._base_model_predictions_keys);
            for (Key k : first_se_pred_keys) {
                Assert.assertNull(DKV.get(k));
            }
        } finally {
            Scope.exit();
            if (grid != null) {
                grid.delete();
            }
            for (Model m : seModels)
                m.delete();

            for (Frame f : frames)
                f.remove();

        }
    }

    @Test
    public void test_SE_scoring_with_blending() {
        List<Lockable> deletables = new ArrayList<>();
        try {
            final int seed = 62832;
            final Frame fr = parse_test_file("./smalldata/logreg/prostate_train.csv");
            deletables.add(fr);
            final Frame test = parse_test_file("./smalldata/logreg/prostate_test.csv");
            deletables.add(test);
            final String target = "CAPSULE";
            int tidx = fr.find(target);
            fr.replace(tidx, fr.vec(tidx).toCategoricalVec()).remove();
            DKV.put(fr);
            test.replace(tidx, test.vec(tidx).toCategoricalVec()).remove();
            DKV.put(test);
            SplitFrame sf = new SplitFrame(fr, new double[]{ 0.7, 0.3 }, null);
            sf.exec().get();
            water.Key<Frame>[] ksplits = sf._destination_frames;
            final Frame train = ksplits[0].get();
            deletables.add(train);
            final Frame blending = ksplits[1].get();
            deletables.add(blending);
            // generate a few base models
            GBMModel.GBMParameters params = new GBMModel.GBMParameters();
            params._train = train._key;
            params._response_column = target;
            params._seed = seed;
            Job<Grid> gridSearch = GridSearch.startGridSearch(null, params, new HashMap<String, Object[]>() {
                {
                    put("_ntrees", new Integer[]{ 3, 5 });
                    put("_learn_rate", new Double[]{ 0.1, 0.2 });
                }
            });
            Grid grid = gridSearch.get();
            deletables.add(grid);
            Model[] gridModels = grid.getModels();
            deletables.addAll(Arrays.asList(gridModels));
            Assert.assertEquals(4, gridModels.length);
            StackedEnsembleParameters seParams = new StackedEnsembleParameters();
            seParams._train = train._key;
            seParams._blending = blending._key;
            seParams._response_column = target;
            seParams._base_models = grid.getModelKeys();
            seParams._seed = seed;
            StackedEnsembleModel se = trainModel().get();
            deletables.add(se);
            // mainly ensuring that no exception is thrown due to unmet categorical in test dataset.
            Frame predictions = se.score(test);
            deletables.add(predictions);
            Assert.assertEquals((2 + 1), predictions.numCols());// binomial: 2 probabilities + 1 response prediction

            Assert.assertEquals(test.numRows(), predictions.numRows());
        } finally {
            for (Lockable l : deletables) {
                if (l instanceof Model)
                    deleteCrossValidationPreds();

                l.delete();
            }
        }
    }

    @Test
    public void test_SE_with_GLM_can_do_predictions_on_frames_with_unseen_categorical_values() {
        // test for PUBDEV-6266
        List<Lockable> deletables = new ArrayList<>();
        try {
            final int seed = 62832;
            final Frame train = parse_test_file("./smalldata/testng/cars_train.csv");
            deletables.add(train);
            final Frame test = parse_test_file("./smalldata/testng/cars_test.csv");
            deletables.add(test);
            final String target = "economy (mpg)";
            int cyl_idx = test.find("cylinders");
            Assert.assertTrue(test.vec(cyl_idx).isInt());
            Vec cyl_test = test.vec(cyl_idx);
            cyl_test.set(((cyl_test.length()) - 1), 7);// that's a new engine concept

            test.replace(cyl_idx, cyl_test.toCategoricalVec()).remove();
            DKV.put(test);
            Assert.assertTrue(test.vec(cyl_idx).isCategorical());
            train.replace(cyl_idx, train.vec(cyl_idx).toCategoricalVec()).remove();
            DKV.put(train);
            // generate a few base models
            GBMModel.GBMParameters params = new GBMModel.GBMParameters();
            params._train = train._key;
            params._response_column = target;
            params._seed = seed;
            params._keep_cross_validation_models = false;
            params._keep_cross_validation_predictions = true;
            params._fold_assignment = FoldAssignmentScheme.Modulo;
            params._nfolds = 5;
            Job<Grid> gridSearch = GridSearch.startGridSearch(null, params, new HashMap<String, Object[]>() {
                {
                    put("_ntrees", new Integer[]{ 3, 5 });
                    put("_learn_rate", new Double[]{ 0.1, 0.2 });
                }
            });
            Grid grid = gridSearch.get();
            deletables.add(grid);
            Model[] gridModels = grid.getModels();
            deletables.addAll(Arrays.asList(gridModels));
            Assert.assertEquals(4, gridModels.length);
            GLMModel.GLMParameters glm_params = new GLMModel.GLMParameters();
            glm_params._train = train._key;
            glm_params._response_column = target;
            glm_params._seed = seed;
            glm_params._keep_cross_validation_models = false;
            glm_params._keep_cross_validation_predictions = true;
            glm_params._fold_assignment = FoldAssignmentScheme.Modulo;
            glm_params._nfolds = 5;
            glm_params._alpha = new double[]{ 0.1, 0.2, 0.4 };
            glm_params._lambda_search = true;
            GLMModel glm = trainModel().get();
            deletables.add(glm);
            StackedEnsembleParameters seParams = new StackedEnsembleParameters();
            seParams._train = train._key;
            seParams._response_column = target;
            seParams._base_models = ArrayUtils.append(grid.getModelKeys(), glm._key);
            seParams._seed = seed;
            StackedEnsembleModel se = trainModel().get();
            deletables.add(se);
            // mainly ensuring that no exception is thrown due to unmet categorical in test dataset.
            Frame predictions = se.score(test);
            deletables.add(predictions);
            Assert.assertTrue(((predictions.vec(0).at(((cyl_test.length()) - 1))) > 0));
        } finally {
            for (Lockable l : deletables) {
                if (l instanceof Model)
                    deleteCrossValidationPreds();

                l.delete();
            }
        }
    }
}

