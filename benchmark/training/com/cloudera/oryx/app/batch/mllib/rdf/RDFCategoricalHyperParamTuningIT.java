/**
 * Copyright (c) 2015, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.app.batch.mllib.rdf;


import MLUpdate.MODEL_FILE_NAME;
import com.cloudera.oryx.app.batch.mllib.AbstractAppMLlibIT;
import com.cloudera.oryx.app.classreg.example.NumericFeature;
import com.cloudera.oryx.app.classreg.predict.CategoricalPrediction;
import com.cloudera.oryx.app.rdf.RDFPMMLUtils;
import com.cloudera.oryx.app.rdf.tree.DecisionForest;
import com.cloudera.oryx.app.schema.CategoricalValueEncodings;
import com.cloudera.oryx.common.collection.Pair;
import com.cloudera.oryx.common.io.IOUtils;
import com.cloudera.oryx.common.pmml.PMMLUtils;
import com.cloudera.oryx.common.settings.ConfigUtils;
import com.typesafe.config.Config;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dmg.pmml.PMML;
import org.junit.Test;


public final class RDFCategoricalHyperParamTuningIT extends AbstractRDFIT {
    private static final int DATA_TO_WRITE = 10000;

    private static final int WRITE_INTERVAL_MSEC = 2;

    @Test
    public void testRDF() throws Exception {
        Path tempDir = getTempDir();
        Path dataDir = tempDir.resolve("data");
        Path modelDir = tempDir.resolve("model");
        Map<String, Object> overlayConfig = new HashMap<>();
        overlayConfig.put("oryx.batch.update-class", RDFUpdate.class.getName());
        ConfigUtils.set(overlayConfig, "oryx.batch.storage.data-dir", dataDir);
        ConfigUtils.set(overlayConfig, "oryx.batch.storage.model-dir", modelDir);
        overlayConfig.put("oryx.batch.streaming.generation-interval-sec", AbstractAppMLlibIT.GEN_INTERVAL_SEC);
        overlayConfig.put("oryx.rdf.num-trees", 10);
        // Low values like 1 are deliberately bad, won't work
        overlayConfig.put("oryx.rdf.hyperparams.max-depth", (("[1," + (AbstractRDFIT.MAX_DEPTH)) + "]"));
        overlayConfig.put("oryx.rdf.hyperparams.max-split-candidates", AbstractRDFIT.MAX_SPLIT_CANDIDATES);
        overlayConfig.put("oryx.input-schema.num-features", 5);
        overlayConfig.put("oryx.input-schema.categorical-features", "[\"4\"]");
        overlayConfig.put("oryx.input-schema.id-features", "[\"0\"]");
        overlayConfig.put("oryx.input-schema.target-feature", "\"4\"");
        overlayConfig.put("oryx.ml.eval.candidates", 2);
        overlayConfig.put("oryx.ml.eval.parallelism", 2);
        Config config = ConfigUtils.overlayOn(overlayConfig, getConfig());
        startMessaging();
        startServerProduceConsumeTopics(config, new RandomCategoricalRDFDataGenerator(3), RDFCategoricalHyperParamTuningIT.DATA_TO_WRITE, RDFCategoricalHyperParamTuningIT.WRITE_INTERVAL_MSEC);
        List<Path> modelInstanceDirs = IOUtils.listFiles(modelDir, "*");
        checkIntervals(modelInstanceDirs.size(), RDFCategoricalHyperParamTuningIT.DATA_TO_WRITE, RDFCategoricalHyperParamTuningIT.WRITE_INTERVAL_MSEC, AbstractAppMLlibIT.GEN_INTERVAL_SEC);
        Path latestModelDir = modelInstanceDirs.get(((modelInstanceDirs.size()) - 1));
        Path modelFile = latestModelDir.resolve(MODEL_FILE_NAME);
        assertTrue(("No such model file: " + modelFile), Files.exists(modelFile));
        PMML pmml = PMMLUtils.read(modelFile);
        assertEquals(3, pmml.getExtensions().size());
        Map<String, Object> expected = new HashMap<>();
        expected.put("maxSplitCandidates", AbstractRDFIT.MAX_SPLIT_CANDIDATES);
        expected.put("maxDepth", AbstractRDFIT.MAX_DEPTH);
        expected.put("impurity", AbstractRDFIT.IMPURITY);
        AbstractAppMLlibIT.checkExtensions(pmml, expected);
        Pair<DecisionForest, CategoricalValueEncodings> forestEncoding = RDFPMMLUtils.read(pmml);
        DecisionForest forest = forestEncoding.getFirst();
        CategoricalValueEncodings encoding = forestEncoding.getSecond();
        Map<String, Integer> targetEncoding = encoding.getValueEncodingMap(4);
        int[] zeroOne = new int[]{ 0, 1 };
        for (int f1 : zeroOne) {
            for (int f2 : zeroOne) {
                for (int f3 : zeroOne) {
                    CategoricalPrediction prediction = ((CategoricalPrediction) (forest.predict(new com.cloudera.oryx.app.classreg.example.Example(null, null, NumericFeature.forValue(f1), NumericFeature.forValue(f2), NumericFeature.forValue(f3)))));
                    boolean expectedPositive = ((f1 == 1) && (f2 == 1)) && (f3 == 1);
                    assertEquals(targetEncoding.get(Boolean.toString(expectedPositive)).intValue(), prediction.getMostProbableCategoryEncoding());
                }
            }
        }
    }
}

