package ai.h2o.automl.targetencoding;


import GBMModel.GBMParameters;
import ScoreKeeper.StoppingMetric;
import TargetEncoder.DataLeakageHandlingStrategy.KFold;
import TargetEncoder.DataLeakageHandlingStrategy.None;
import hex.ModelMetricsBinomial;
import hex.genmodel.utils.DistributionFamily;
import hex.tree.gbm.GBM;
import hex.tree.gbm.GBMModel;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import water.Key;
import water.Scope;
import water.TestUtil;
import water.fvec.Frame;


/* Be aware that `smalldata/airlines/target_encoding/airlines_*.csv` files are not present in the repo. Replace with your own splits. */
public class TargetEncodingAirlinesBenchmark extends TestUtil {
    @Test
    public void KFoldHoldoutTypeTest() {
        Scope.enter();
        GBMModel gbm = null;
        Map<String, Frame> encodingMap = null;
        try {
            Frame airlinesTrainWithTEH = parse_test_file(Key.make("airlines_train"), "smalldata/airlines/target_encoding/airlines_train_with_teh.csv");
            Frame airlinesValid = parse_test_file(Key.make("airlines_valid"), "smalldata/airlines/target_encoding/airlines_valid.csv");
            Frame airlinesTestFrame = parse_test_file(Key.make("airlines_test"), "smalldata/airlines/target_encoding/airlines_test.csv");
            Scope.track(airlinesTrainWithTEH, airlinesValid, airlinesTestFrame);
            long startTimeEncoding = System.currentTimeMillis();
            String foldColumnName = "fold";
            TargetEncoderFrameHelper.addKFoldColumn(airlinesTrainWithTEH, foldColumnName, 5, 1234L);
            BlendingParams params = new BlendingParams(5, 1);
            String[] teColumns = new String[]{ "Origin", "Dest" };
            TargetEncoder tec = new TargetEncoder(teColumns, params);
            String targetColumnName = "IsDepDelayed";
            boolean withBlendedAvg = true;
            boolean withNoiseOnlyForTraining = true;
            boolean withImputationForNAsInOriginalColumns = true;
            // Create encoding
            encodingMap = tec.prepareEncodingMap(airlinesTrainWithTEH, targetColumnName, foldColumnName, true);
            // Apply encoding to the training set
            Frame trainEncoded;
            int seed = 1234;
            int seedForGBM = 1234;
            if (withNoiseOnlyForTraining) {
                trainEncoded = tec.applyTargetEncoding(airlinesTrainWithTEH, targetColumnName, encodingMap, KFold, foldColumnName, withBlendedAvg, withImputationForNAsInOriginalColumns, seed);
            } else {
                trainEncoded = tec.applyTargetEncoding(airlinesTrainWithTEH, targetColumnName, encodingMap, KFold, foldColumnName, withBlendedAvg, 0, withImputationForNAsInOriginalColumns, seed);
            }
            // Applying encoding to the valid set
            Frame validEncoded = tec.applyTargetEncoding(airlinesValid, targetColumnName, encodingMap, None, foldColumnName, withBlendedAvg, 0, withImputationForNAsInOriginalColumns, seed);
            // Applying encoding to the test set
            Frame testEncoded = tec.applyTargetEncoding(airlinesTestFrame, targetColumnName, encodingMap, None, foldColumnName, withBlendedAvg, 0, withImputationForNAsInOriginalColumns, seed);
            printOutColumnsMetadata(testEncoded);
            testEncoded = tec.ensureTargetColumnIsBinaryCategorical(testEncoded, targetColumnName);
            Scope.track(trainEncoded, validEncoded, testEncoded);
            // Frame.export(trainEncoded, "airlines_train_kfold_dest_noise_noblend.csv", trainEncoded._key.toString(), true, 1);
            // Frame.export(validEncoded, "airlines_valid_kfold_dest_noise_noblend.csv", validEncoded._key.toString(), true, 1);
            // Frame.export(testEncoded, "airlines_test_kfold_dest_noise_noblend.csv", testEncoded._key.toString(), true, 1);
            long finishTimeEncoding = System.currentTimeMillis();
            System.out.println(("Calculation of encodings took: " + (finishTimeEncoding - startTimeEncoding)));
            // With target encoded columns
            long startTime = System.currentTimeMillis();
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = trainEncoded._key;
            parms._response_column = targetColumnName;
            parms._score_tree_interval = 10;
            parms._ntrees = 1000;
            parms._max_depth = 5;
            parms._distribution = DistributionFamily.AUTO;
            parms._valid = validEncoded._key;
            parms._stopping_tolerance = 0.001;
            parms._stopping_metric = StoppingMetric.AUC;
            parms._stopping_rounds = 5;
            parms._ignored_columns = TargetEncodingAirlinesBenchmark.concat(new String[]{ "IsDepDelayed_REC", foldColumnName }, teColumns);
            parms._seed = seedForGBM;
            GBM job = new GBM(parms);
            gbm = job.trainModel().get();
            Assert.assertTrue(job.isStopped());
            long finishTime = System.currentTimeMillis();
            System.out.println(("Calculation took: " + (finishTime - startTime)));
            Frame preds = gbm.score(testEncoded);
            Scope.track(preds);
            ModelMetricsBinomial mm = ModelMetricsBinomial.make(preds.vec(2), testEncoded.vec(parms._response_column));
            double auc = mm._auc._auc;
            // Without target encoding
            double auc2 = trainDefaultGBM(targetColumnName, tec);
            System.out.println(("AUC with encoding:" + auc));
            System.out.println(("AUC without encoding:" + auc2));
            Assert.assertTrue((auc2 < auc));
        } finally {
            encodingMapCleanUp(encodingMap);
            if (gbm != null) {
                gbm.delete();
                gbm.deleteCrossValidationModels();
            }
            Scope.exit();
        }
    }

    @Test
    public void noneHoldoutTypeTest() {
        Scope.enter();
        GBMModel gbm;
        try {
            Frame airlinesTrainWithoutTEH = parse_test_file(Key.make("airlines_train"), "smalldata/airlines/target_encoding/airlines_train_without_teh.csv");
            Frame airlinesTEHoldout = parse_test_file(Key.make("airlines_te_holdout"), "smalldata/airlines/target_encoding/airlines_te_holdout.csv");
            Frame airlinesValid = parse_test_file(Key.make("airlines_valid"), "smalldata/airlines/target_encoding/airlines_valid.csv");
            Frame airlinesTestFrame = parse_test_file(Key.make("airlines_test"), "smalldata/airlines/AirlinesTest.csv.zip");
            Scope.track(airlinesTrainWithoutTEH, airlinesTEHoldout, airlinesValid, airlinesTestFrame);
            long startTimeEncoding = System.currentTimeMillis();
            BlendingParams params = new BlendingParams(3, 1);
            String[] teColumns = new String[]{ "Origin", "Dest" };
            TargetEncoder tec = new TargetEncoder(teColumns, params);
            String targetColumnName = "IsDepDelayed";
            boolean withBlendedAvg = true;
            boolean withImputationForNAsInOriginalColumns = true;
            // Create encoding
            Map<String, Frame> encodingMap = tec.prepareEncodingMap(airlinesTEHoldout, targetColumnName, null);
            // Apply encoding to the training set
            Frame trainEncoded = tec.applyTargetEncoding(airlinesTrainWithoutTEH, targetColumnName, encodingMap, None, withBlendedAvg, 0, withImputationForNAsInOriginalColumns, 1234);
            // Applying encoding to the valid set
            Frame validEncoded = tec.applyTargetEncoding(airlinesValid, targetColumnName, encodingMap, None, withBlendedAvg, 0, withImputationForNAsInOriginalColumns, 1234);
            // Applying encoding to the test set
            Frame testEncoded = tec.applyTargetEncoding(airlinesTestFrame, targetColumnName, encodingMap, None, withBlendedAvg, 0, withImputationForNAsInOriginalColumns, 1234);
            // We do it manually just to be able to measure metrics in the end. TargetEncoder should not be aware of target column for test dataset.
            testEncoded = tec.ensureTargetColumnIsBinaryCategorical(testEncoded, targetColumnName);
            Scope.track(trainEncoded, validEncoded, testEncoded);
            long finishTimeEncoding = System.currentTimeMillis();
            System.out.println(("Calculation of encodings took: " + (finishTimeEncoding - startTimeEncoding)));
            // With target encoded  columns
            checkNumRows(airlinesTrainWithoutTEH, trainEncoded);
            checkNumRows(airlinesValid, validEncoded);
            checkNumRows(airlinesTestFrame, testEncoded);
            long startTime = System.currentTimeMillis();
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = trainEncoded._key;
            parms._response_column = "IsDepDelayed";
            parms._score_tree_interval = 10;
            parms._ntrees = 1000;
            parms._max_depth = 5;
            parms._distribution = DistributionFamily.AUTO;
            parms._valid = validEncoded._key;
            parms._stopping_tolerance = 0.001;
            parms._stopping_metric = StoppingMetric.AUC;
            parms._stopping_rounds = 5;
            parms._ignored_columns = TargetEncodingAirlinesBenchmark.concat(new String[]{ "IsDepDelayed_REC" }, teColumns);
            parms._seed = 1234L;
            GBM job = new GBM(parms);
            gbm = job.trainModel().get();
            Assert.assertTrue(job.isStopped());
            long finishTime = System.currentTimeMillis();
            System.out.println(("Calculation took: " + (finishTime - startTime)));
            Frame preds = gbm.score(testEncoded);
            Scope.track(preds);
            ModelMetricsBinomial mm = ModelMetricsBinomial.make(preds.vec(2), testEncoded.vec(parms._response_column));
            double auc = mm._auc._auc;
            // Without target encoded Origin column
            double auc2 = trainDefaultGBM(targetColumnName, tec);
            System.out.println(("AUC with encoding:" + auc));
            System.out.println(("AUC without encoding:" + auc2));
            encodingMapCleanUp(encodingMap);
            if (gbm != null) {
                gbm.delete();
                gbm.deleteCrossValidationModels();
            }
            Assert.assertTrue((auc2 < auc));
        } finally {
            Scope.exit();
        }
    }
}

