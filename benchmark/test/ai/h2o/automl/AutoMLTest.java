package ai.h2o.automl;


import AutoML.WorkAllocations;
import hex.Model;
import hex.SplitFrame;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import water.DKV;
import water.Key;
import water.Lockable;
import water.TestUtil;
import water.fvec.Frame;

import static Algo.DRF;
import static Algo.DeepLearning;
import static Algo.GLM;
import static Algo.XGBoost;


public class AutoMLTest extends TestUtil {
    @Test
    public void test_basic_automl_behaviour_using_cv() {
        AutoML aml = null;
        Frame fr = null;
        try {
            AutoMLBuildSpec autoMLBuildSpec = new AutoMLBuildSpec();
            fr = parse_test_file("./smalldata/logreg/prostate_train.csv");
            autoMLBuildSpec.input_spec.training_frame = fr._key;
            autoMLBuildSpec.input_spec.response_column = "CAPSULE";
            autoMLBuildSpec.build_control.stopping_criteria.set_max_models(3);
            autoMLBuildSpec.build_control.keep_cross_validation_models = false;// Prevent leaked keys from CV models

            autoMLBuildSpec.build_control.keep_cross_validation_predictions = false;// Prevent leaked keys from CV predictions

            aml = AutoML.startAutoML(autoMLBuildSpec);
            aml.get();
            Key[] modelKeys = aml.leaderboard().getModelKeys();
            int count_se = 0;
            int count_non_se = 0;
            for (Key k : modelKeys)
                if (k.toString().startsWith("StackedEnsemble"))
                    count_se++;
                else
                    count_non_se++;


            Assert.assertEquals("wrong amount of standard models", 3, count_non_se);
            Assert.assertEquals("wrong amount of SE models", 2, count_se);
            Assert.assertEquals((3 + 2), aml.leaderboard().getModelCount());
        } finally {
            // Cleanup
            if (aml != null)
                aml.deleteWithChildren();

            if (fr != null)
                fr.delete();

        }
    }

    // important test: the basic execution path is very different when CV is disabled
    // being for model training but also default leaderboard scoring
    // also allows us to keep an eye on memory leaks.
    @Test
    public void test_automl_with_cv_disabled() {
        AutoML aml = null;
        Frame fr = null;
        try {
            AutoMLBuildSpec autoMLBuildSpec = new AutoMLBuildSpec();
            fr = parse_test_file("./smalldata/logreg/prostate_train.csv");
            autoMLBuildSpec.input_spec.training_frame = fr._key;
            autoMLBuildSpec.input_spec.response_column = "CAPSULE";
            autoMLBuildSpec.build_control.stopping_criteria.set_max_models(3);
            autoMLBuildSpec.build_control.nfolds = 0;
            aml = AutoML.startAutoML(autoMLBuildSpec);
            aml.get();
            Key[] modelKeys = aml.leaderboard().getModelKeys();
            int count_se = 0;
            int count_non_se = 0;
            for (Key k : modelKeys)
                if (k.toString().startsWith("StackedEnsemble"))
                    count_se++;
                else
                    count_non_se++;


            Assert.assertEquals("wrong amount of standard models", 3, count_non_se);
            Assert.assertEquals("no Stacked Ensemble expected if cross-validation is disabled", 0, count_se);
            Assert.assertEquals(3, aml.leaderboard().getModelCount());
        } finally {
            // Cleanup
            if (aml != null)
                aml.deleteWithChildren();

            if (fr != null)
                fr.delete();

        }
    }

    @Test
    public void test_stacked_ensembles_trained_with_blending_frame_if_provided() {
        List<Lockable> deletables = new ArrayList<>();
        try {
            final int seed = 62832;
            final Frame fr = parse_test_file("./smalldata/logreg/prostate_train.csv");
            deletables.add(fr);
            final Frame test = parse_test_file("./smalldata/logreg/prostate_test.csv");
            deletables.add(test);
            String target = "CAPSULE";
            int tidx = fr.find(target);
            fr.replace(tidx, fr.vec(tidx).toCategoricalVec()).remove();
            DKV.put(fr);
            deletables.add(fr);
            test.replace(tidx, test.vec(tidx).toCategoricalVec()).remove();
            DKV.put(test);
            deletables.add(test);
            SplitFrame sf = new SplitFrame(fr, new double[]{ 0.7, 0.3 }, null);
            sf.exec().get();
            Key<Frame>[] ksplits = sf._destination_frames;
            final Frame train = ksplits[0].get();
            deletables.add(train);
            final Frame blending = ksplits[1].get();
            deletables.add(blending);
            AutoMLBuildSpec autoMLBuildSpec = new AutoMLBuildSpec();
            autoMLBuildSpec.input_spec.training_frame = train._key;
            autoMLBuildSpec.input_spec.blending_frame = blending._key;
            autoMLBuildSpec.input_spec.leaderboard_frame = test._key;
            autoMLBuildSpec.input_spec.response_column = target;
            autoMLBuildSpec.build_control.stopping_criteria.set_max_models(3);
            autoMLBuildSpec.build_control.nfolds = 0;
            autoMLBuildSpec.build_control.stopping_criteria.set_seed(seed);
            AutoML aml = AutoML.startAutoML(autoMLBuildSpec);
            deletables.add(aml);
            aml.get();
            Key[] modelKeys = aml.leaderboard().getModelKeys();
            int count_se = 0;
            int count_non_se = 0;
            for (Key k : modelKeys)
                if (k.toString().startsWith("StackedEnsemble"))
                    count_se++;
                else
                    count_non_se++;


            Assert.assertEquals("wrong amount of standard models", 3, count_non_se);
            Assert.assertEquals("wrong amount of SE models", 2, count_se);
            Assert.assertEquals(5, aml.leaderboard().getModelCount());
        } finally {
            // Cleanup
            for (Lockable l : deletables) {
                if (l instanceof AutoML)
                    deleteWithChildren();

                l.delete();
            }
        }
    }

    // timeout can cause interruption of steps at various levels, for example from top to bottom:
    // - interruption after an AutoML model has been trained, preventing addition of more models
    // - interruption when building the main model (if CV enabled)
    // - interruption when building a CV model (for example right after building a tree)
    // we want to leave the memory in a clean state after any of those interruptions.
    // this test uses a slightly random timeout to ensure it will interrupt the training at various steps
    @Test
    public void test_automl_basic_behaviour_on_timeout() {
        AutoML aml = null;
        Frame fr = null;
        try {
            AutoMLBuildSpec autoMLBuildSpec = new AutoMLBuildSpec();
            fr = parse_test_file("./smalldata/logreg/prostate_train.csv");
            autoMLBuildSpec.input_spec.training_frame = fr._key;
            autoMLBuildSpec.input_spec.response_column = "CAPSULE";
            autoMLBuildSpec.build_control.stopping_criteria.set_max_runtime_secs(new Random().nextInt(30));
            autoMLBuildSpec.build_control.keep_cross_validation_models = false;// Prevent leaked keys from CV models

            autoMLBuildSpec.build_control.keep_cross_validation_predictions = false;// Prevent leaked keys from CV predictions

            aml = AutoML.startAutoML(autoMLBuildSpec);
            aml.get();
            // no assertion, we just want to check leaked keys
        } finally {
            // Cleanup
            if (aml != null)
                aml.deleteWithChildren();

            if (fr != null)
                fr.delete();

        }
    }

    @Test
    public void test_automl_basic_behaviour_on_grid_timeout() {
        AutoML aml = null;
        Frame fr = null;
        try {
            AutoMLBuildSpec autoMLBuildSpec = new AutoMLBuildSpec();
            fr = parse_test_file("./smalldata/logreg/prostate_train.csv");
            autoMLBuildSpec.input_spec.training_frame = fr._key;
            autoMLBuildSpec.input_spec.response_column = "CAPSULE";
            autoMLBuildSpec.build_models.exclude_algos = new Algo[]{ DeepLearning, DRF, GLM };
            autoMLBuildSpec.build_control.stopping_criteria.set_max_runtime_secs(8);
            // autoMLBuildSpec.build_control.stopping_criteria.set_max_runtime_secs(new Random().nextInt(30));
            autoMLBuildSpec.build_control.keep_cross_validation_models = false;// Prevent leaked keys from CV models

            autoMLBuildSpec.build_control.keep_cross_validation_predictions = false;// Prevent leaked keys from CV predictions

            aml = AutoML.startAutoML(autoMLBuildSpec);
            aml.get();
            // no assertion, we just want to check leaked keys
        } finally {
            // Cleanup
            if (aml != null)
                aml.deleteWithChildren();

            if (fr != null)
                fr.delete();

        }
    }

    @Test
    public void KeepCrossValidationFoldAssignmentEnabledTest() {
        AutoML aml = null;
        Frame fr = null;
        Model leader = null;
        try {
            AutoMLBuildSpec autoMLBuildSpec = new AutoMLBuildSpec();
            fr = parse_test_file("./smalldata/logreg/prostate_train.csv");
            autoMLBuildSpec.input_spec.training_frame = fr._key;
            autoMLBuildSpec.input_spec.response_column = "CAPSULE";
            autoMLBuildSpec.build_control.stopping_criteria.set_max_models(1);
            autoMLBuildSpec.build_control.stopping_criteria.set_max_runtime_secs(30);
            autoMLBuildSpec.build_control.keep_cross_validation_fold_assignment = true;
            aml = AutoML.makeAutoML(Key.<AutoML>make(), new Date(), autoMLBuildSpec);
            AutoML.startAutoML(aml);
            aml.get();
            leader = aml.leader();
            TestCase.assertTrue(((leader != null) && (leader._parms._keep_cross_validation_fold_assignment)));
            TestCase.assertNotNull(leader._output._cross_validation_fold_assignment_frame_id);
        } finally {
            if (aml != null)
                aml.deleteWithChildren();

            if (fr != null)
                fr.remove();

            if (leader != null) {
                Frame cvFoldAssignmentFrame = DKV.getGet(leader._output._cross_validation_fold_assignment_frame_id);
                cvFoldAssignmentFrame.delete();
            }
        }
    }

    @Test
    public void KeepCrossValidationFoldAssignmentDisabledTest() {
        AutoML aml = null;
        Frame fr = null;
        Model leader = null;
        try {
            AutoMLBuildSpec autoMLBuildSpec = new AutoMLBuildSpec();
            fr = parse_test_file("./smalldata/airlines/allyears2k_headers.zip");
            autoMLBuildSpec.input_spec.training_frame = fr._key;
            autoMLBuildSpec.input_spec.response_column = "IsDepDelayed";
            autoMLBuildSpec.build_control.stopping_criteria.set_max_models(1);
            autoMLBuildSpec.build_control.stopping_criteria.set_max_runtime_secs(30);
            autoMLBuildSpec.build_control.keep_cross_validation_fold_assignment = false;
            aml = AutoML.makeAutoML(Key.<AutoML>make(), new Date(), autoMLBuildSpec);
            AutoML.startAutoML(aml);
            aml.get();
            leader = aml.leader();
            TestCase.assertTrue(((leader != null) && (!(leader._parms._keep_cross_validation_fold_assignment))));
            TestCase.assertNull(leader._output._cross_validation_fold_assignment_frame_id);
        } finally {
            if (aml != null)
                aml.deleteWithChildren();

            if (fr != null)
                fr.delete();

        }
    }

    @Test
    public void testWorkPlan() {
        AutoML aml = null;
        Frame fr = null;
        try {
            AutoMLBuildSpec autoMLBuildSpec = new AutoMLBuildSpec();
            fr = parse_test_file("./smalldata/airlines/allyears2k_headers.zip");
            autoMLBuildSpec.input_spec.training_frame = fr._key;
            autoMLBuildSpec.input_spec.response_column = "IsDepDelayed";
            aml = new AutoML(Key.<AutoML>make(), new Date(), autoMLBuildSpec);
            AutoML.WorkAllocations workPlan = aml.planWork();
            int max_total_work = ((((((((1 * 10) + (3 * 20))// DL
             + (2 * 10))// DRF
             + (5 * 10)) + (1 * 60))// GBM
             + (1 * 20))// GLM
             + (3 * 10)) + (1 * 100))// XGBoost
             + (2 * 15);
            // SE
            Assert.assertEquals(workPlan.remainingWork(), max_total_work);
            autoMLBuildSpec.build_models.exclude_algos = new Algo[]{ DeepLearning, XGBoost };
            workPlan = aml.planWork();
            Assert.assertEquals(workPlan.remainingWork(), (max_total_work - ((((1 * 10) + (3 * 20)) + (3 * 10)) + (1 * 100))));
        } finally {
            if (aml != null)
                aml.deleteWithChildren();

            if (fr != null)
                fr.remove();

        }
    }

    @Test
    public void test_training_frame_partition_when_cv_disabled_and_validation_frame_missing() {
        AutoML aml = null;
        Frame fr = null;
        Frame test = null;
        try {
            AutoMLBuildSpec autoMLBuildSpec = new AutoMLBuildSpec();
            fr = parse_test_file("./smalldata/logreg/prostate_train.csv");
            test = parse_test_file("./smalldata/logreg/prostate_test.csv");
            autoMLBuildSpec.input_spec.response_column = "CAPSULE";
            autoMLBuildSpec.input_spec.training_frame = fr._key;
            autoMLBuildSpec.input_spec.validation_frame = null;
            autoMLBuildSpec.input_spec.leaderboard_frame = test._key;
            autoMLBuildSpec.build_control.nfolds = 0;
            autoMLBuildSpec.build_control.stopping_criteria.set_max_models(1);
            autoMLBuildSpec.build_control.stopping_criteria.set_seed(1);
            aml = AutoML.startAutoML(autoMLBuildSpec);
            aml.get();
            double tolerance = 0.01;
            Assert.assertEquals(0.9, (((double) (aml.getTrainingFrame().numRows())) / (fr.numRows())), tolerance);
            Assert.assertEquals(0.1, (((double) (aml.getValidationFrame().numRows())) / (fr.numRows())), tolerance);
            Assert.assertEquals(test.numRows(), aml.getLeaderboardFrame().numRows());
        } finally {
            if (aml != null)
                aml.deleteWithChildren();

            if (fr != null)
                fr.remove();

            if (test != null)
                test.remove();

        }
    }

    @Test
    public void test_training_frame_partition_when_cv_disabled_and_leaderboard_frame_missing() {
        AutoML aml = null;
        Frame fr = null;
        Frame test = null;
        try {
            AutoMLBuildSpec autoMLBuildSpec = new AutoMLBuildSpec();
            fr = parse_test_file("./smalldata/logreg/prostate_train.csv");
            test = parse_test_file("./smalldata/logreg/prostate_test.csv");
            autoMLBuildSpec.input_spec.response_column = "CAPSULE";
            autoMLBuildSpec.input_spec.training_frame = fr._key;
            autoMLBuildSpec.input_spec.validation_frame = test._key;
            autoMLBuildSpec.input_spec.leaderboard_frame = null;
            autoMLBuildSpec.build_control.nfolds = 0;
            autoMLBuildSpec.build_control.stopping_criteria.set_max_models(1);
            autoMLBuildSpec.build_control.stopping_criteria.set_seed(1);
            aml = AutoML.startAutoML(autoMLBuildSpec);
            aml.get();
            double tolerance = 0.01;
            Assert.assertEquals(0.9, (((double) (aml.getTrainingFrame().numRows())) / (fr.numRows())), tolerance);
            Assert.assertEquals(test.numRows(), aml.getValidationFrame().numRows());
            Assert.assertEquals(0.1, (((double) (aml.getLeaderboardFrame().numRows())) / (fr.numRows())), tolerance);
        } finally {
            if (aml != null)
                aml.deleteWithChildren();

            if (fr != null)
                fr.remove();

            if (test != null)
                test.remove();

        }
    }

    @Test
    public void test_training_frame_partition_when_cv_disabled_and_both_validation_and_leaderboard_frames_missing() {
        AutoML aml = null;
        Frame fr = null;
        try {
            AutoMLBuildSpec autoMLBuildSpec = new AutoMLBuildSpec();
            fr = parse_test_file("./smalldata/logreg/prostate_train.csv");
            autoMLBuildSpec.input_spec.response_column = "CAPSULE";
            autoMLBuildSpec.input_spec.training_frame = fr._key;
            autoMLBuildSpec.input_spec.validation_frame = null;
            autoMLBuildSpec.input_spec.leaderboard_frame = null;
            autoMLBuildSpec.build_control.nfolds = 0;
            autoMLBuildSpec.build_control.stopping_criteria.set_max_models(1);
            autoMLBuildSpec.build_control.stopping_criteria.set_seed(1);
            aml = AutoML.startAutoML(autoMLBuildSpec);
            aml.get();
            double tolerance = 0.01;
            Assert.assertEquals(0.8, (((double) (aml.getTrainingFrame().numRows())) / (fr.numRows())), tolerance);
            Assert.assertEquals(0.1, (((double) (aml.getValidationFrame().numRows())) / (fr.numRows())), tolerance);
            Assert.assertEquals(0.1, (((double) (aml.getLeaderboardFrame().numRows())) / (fr.numRows())), tolerance);
        } finally {
            if (aml != null)
                aml.deleteWithChildren();

            if (fr != null)
                fr.remove();

        }
    }

    @Test
    public void test_training_frame_not_partitioned_when_cv_enabled() {
        AutoML aml = null;
        Frame fr = null;
        try {
            AutoMLBuildSpec autoMLBuildSpec = new AutoMLBuildSpec();
            fr = parse_test_file("./smalldata/logreg/prostate_train.csv");
            autoMLBuildSpec.input_spec.response_column = "CAPSULE";
            autoMLBuildSpec.input_spec.training_frame = fr._key;
            autoMLBuildSpec.input_spec.validation_frame = null;
            autoMLBuildSpec.input_spec.leaderboard_frame = null;
            autoMLBuildSpec.build_control.stopping_criteria.set_max_models(1);
            autoMLBuildSpec.build_control.stopping_criteria.set_seed(1);
            aml = AutoML.startAutoML(autoMLBuildSpec);
            aml.get();
            Assert.assertEquals(fr.numRows(), aml.getTrainingFrame().numRows());
            TestCase.assertNull(aml.getValidationFrame());
            TestCase.assertNull(aml.getLeaderboardFrame());
        } finally {
            if (aml != null)
                aml.deleteWithChildren();

            if (fr != null)
                fr.remove();

        }
    }
}

