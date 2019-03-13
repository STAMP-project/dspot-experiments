/**
 * Copyright (c) 2014, Cloudera, Inc. All Rights Reserved.
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
import com.cloudera.oryx.api.KeyMessage;
import com.cloudera.oryx.app.batch.mllib.AbstractAppMLlibIT;
import com.cloudera.oryx.app.pmml.AppPMMLUtils;
import com.cloudera.oryx.app.schema.InputSchema;
import com.cloudera.oryx.common.io.IOUtils;
import com.cloudera.oryx.common.pmml.PMMLUtils;
import com.cloudera.oryx.common.settings.ConfigUtils;
import com.typesafe.config.Config;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dmg.pmml.Model;
import org.dmg.pmml.PMML;
import org.dmg.pmml.True;
import org.dmg.pmml.mining.MiningModel;
import org.dmg.pmml.mining.Segment;
import org.dmg.pmml.mining.Segmentation;
import org.dmg.pmml.tree.TreeModel;
import org.junit.Test;


public final class RDFUpdateIT extends AbstractRDFIT {
    private static final int DATA_TO_WRITE = 2000;

    private static final int WRITE_INTERVAL_MSEC = 10;

    private static final int NUM_TREES = 2;

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
        overlayConfig.put("oryx.rdf.num-trees", RDFUpdateIT.NUM_TREES);
        overlayConfig.put("oryx.rdf.hyperparams.max-depth", AbstractRDFIT.MAX_DEPTH);
        overlayConfig.put("oryx.rdf.hyperparams.max-split-candidates", AbstractRDFIT.MAX_SPLIT_CANDIDATES);
        overlayConfig.put("oryx.rdf.hyperparams.impurity", AbstractRDFIT.IMPURITY);
        overlayConfig.put("oryx.input-schema.num-features", 5);
        overlayConfig.put("oryx.input-schema.categorical-features", "[\"4\"]");
        overlayConfig.put("oryx.input-schema.id-features", "[\"0\"]");
        overlayConfig.put("oryx.input-schema.target-feature", "\"4\"");
        Config config = ConfigUtils.overlayOn(overlayConfig, getConfig());
        startMessaging();
        List<KeyMessage<String, String>> updates = startServerProduceConsumeTopics(config, new RandomCategoricalRDFDataGenerator(3), RDFUpdateIT.DATA_TO_WRITE, RDFUpdateIT.WRITE_INTERVAL_MSEC);
        List<Path> modelInstanceDirs = IOUtils.listFiles(modelDir, "*");
        int generations = modelInstanceDirs.size();
        checkIntervals(generations, RDFUpdateIT.DATA_TO_WRITE, RDFUpdateIT.WRITE_INTERVAL_MSEC, AbstractAppMLlibIT.GEN_INTERVAL_SEC);
        for (Path modelInstanceDir : modelInstanceDirs) {
            Path modelFile = modelInstanceDir.resolve(MODEL_FILE_NAME);
            assertNonEmpty(modelFile);
            PMMLUtils.read(modelFile);// Shouldn't throw exception

        }
        InputSchema schema = new InputSchema(config);
        for (KeyMessage<String, String> km : updates) {
            String type = km.getKey();
            String value = km.getMessage();
            assertContains(Arrays.asList("MODEL", "MODEL-REF"), type);
            PMML pmml = AppPMMLUtils.readPMMLFromUpdateKeyMessage(type, value, null);
            assertNotNull(pmml);
            AbstractAppMLlibIT.checkHeader(pmml.getHeader());
            assertEquals(3, pmml.getExtensions().size());
            Map<String, Object> expected = new HashMap<>();
            expected.put("maxDepth", AbstractRDFIT.MAX_DEPTH);
            expected.put("maxSplitCandidates", AbstractRDFIT.MAX_SPLIT_CANDIDATES);
            expected.put("impurity", AbstractRDFIT.IMPURITY);
            AbstractAppMLlibIT.checkExtensions(pmml, expected);
            AbstractAppMLlibIT.checkDataDictionary(schema, pmml.getDataDictionary());
            Model rootModel = pmml.getModels().get(0);
            if (rootModel instanceof TreeModel) {
                assertEquals(RDFUpdateIT.NUM_TREES, 1);
                TreeModel treeModel = ((TreeModel) (rootModel));
                RDFUpdateIT.checkTreeModel(treeModel);
            } else
                if (rootModel instanceof MiningModel) {
                    MiningModel miningModel = ((MiningModel) (rootModel));
                    Segmentation segmentation = miningModel.getSegmentation();
                    if (schema.isClassification()) {
                        assertEquals(Segmentation.MultipleModelMethod.WEIGHTED_MAJORITY_VOTE, segmentation.getMultipleModelMethod());
                    } else {
                        assertEquals(Segmentation.MultipleModelMethod.WEIGHTED_AVERAGE, segmentation.getMultipleModelMethod());
                    }
                    List<Segment> segments = segmentation.getSegments();
                    assertEquals(RDFUpdateIT.NUM_TREES, segments.size());
                    for (int i = 0; i < (segments.size()); i++) {
                        Segment segment = segments.get(i);
                        assertEquals(Integer.toString(i), segment.getId());
                        assertInstanceOf(segment.getPredicate(), True.class);
                        assertEquals(1.0, segment.getWeight().doubleValue());
                        assertInstanceOf(segment.getModel(), TreeModel.class);
                        RDFUpdateIT.checkTreeModel(((TreeModel) (segment.getModel())));
                    }
                } else {
                    fail(("Wrong model type: " + (rootModel.getClass())));
                    return;
                }

            if (schema.isClassification()) {
                assertEquals(MiningFunction.CLASSIFICATION, rootModel.getMiningFunction());
            } else {
                assertEquals(MiningFunction.REGRESSION, rootModel.getMiningFunction());
            }
            AbstractAppMLlibIT.checkMiningSchema(schema, rootModel.getMiningSchema());
        }
    }
}

