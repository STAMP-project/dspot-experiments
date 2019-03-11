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
package com.cloudera.oryx.app.rdf;


import com.cloudera.oryx.app.rdf.tree.DecisionForest;
import com.cloudera.oryx.app.schema.CategoricalValueEncodings;
import com.cloudera.oryx.app.schema.InputSchema;
import com.cloudera.oryx.common.OryxTest;
import com.cloudera.oryx.common.collection.Pair;
import com.cloudera.oryx.common.settings.ConfigUtils;
import com.typesafe.config.Config;
import java.util.HashMap;
import java.util.Map;
import org.dmg.pmml.PMML;
import org.junit.Test;


public final class RDFPMMLUtilsTest extends OryxTest {
    @Test
    public void testValidateClassification() {
        PMML pmml = RDFPMMLUtilsTest.buildDummyClassificationModel();
        Map<String, Object> overlayConfig = new HashMap<>();
        overlayConfig.put("oryx.input-schema.feature-names", "[\"color\",\"fruit\"]");
        overlayConfig.put("oryx.input-schema.numeric-features", "[]");
        overlayConfig.put("oryx.input-schema.target-feature", "fruit");
        Config config = ConfigUtils.overlayOn(overlayConfig, ConfigUtils.getDefault());
        InputSchema schema = new InputSchema(config);
        RDFPMMLUtils.validatePMMLVsSchema(pmml, schema);
    }

    @Test
    public void testValidateRegression() {
        PMML pmml = RDFPMMLUtilsTest.buildDummyRegressionModel();
        Map<String, Object> overlayConfig = new HashMap<>();
        overlayConfig.put("oryx.input-schema.feature-names", "[\"foo\",\"bar\"]");
        overlayConfig.put("oryx.input-schema.categorical-features", "[]");
        overlayConfig.put("oryx.input-schema.target-feature", "bar");
        Config config = ConfigUtils.overlayOn(overlayConfig, ConfigUtils.getDefault());
        InputSchema schema = new InputSchema(config);
        RDFPMMLUtils.validatePMMLVsSchema(pmml, schema);
    }

    @Test
    public void testReadClassification() {
        PMML pmml = RDFPMMLUtilsTest.buildDummyClassificationModel();
        Pair<DecisionForest, CategoricalValueEncodings> forestAndEncodings = RDFPMMLUtils.read(pmml);
        DecisionForest forest = forestAndEncodings.getFirst();
        assertEquals(1, forest.getTrees().length);
        assertArrayEquals(new double[]{ 1.0 }, forest.getWeights());
        assertArrayEquals(new double[]{ 0.5, 0.0 }, forest.getFeatureImportances());
        CategoricalValueEncodings encodings = forestAndEncodings.getSecond();
        assertEquals(2, encodings.getValueCount(0));
        assertEquals(2, encodings.getValueCount(1));
    }

    @Test
    public void testReadRegression() {
        PMML pmml = RDFPMMLUtilsTest.buildDummyRegressionModel();
        Pair<DecisionForest, CategoricalValueEncodings> forestAndEncodings = RDFPMMLUtils.read(pmml);
        CategoricalValueEncodings encodings = forestAndEncodings.getSecond();
        assertEquals(0, encodings.getCategoryCounts().size());
    }

    @Test
    public void testReadClassificationForest() {
        PMML pmml = RDFPMMLUtilsTest.buildDummyClassificationModel(3);
        Pair<DecisionForest, CategoricalValueEncodings> forestAndEncodings = RDFPMMLUtils.read(pmml);
        DecisionForest forest = forestAndEncodings.getFirst();
        assertEquals(3, forest.getTrees().length);
    }
}

