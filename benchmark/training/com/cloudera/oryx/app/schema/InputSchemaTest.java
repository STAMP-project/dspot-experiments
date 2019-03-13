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
package com.cloudera.oryx.app.schema;


import com.cloudera.oryx.common.OryxTest;
import com.cloudera.oryx.common.settings.ConfigUtils;
import com.typesafe.config.Config;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;


public final class InputSchemaTest extends OryxTest {
    @Test(expected = IllegalArgumentException.class)
    public void testRejectDefault() {
        new InputSchema(ConfigUtils.getDefault());
    }

    @Test
    public void testBasicConfig() {
        Map<String, Object> overlayConfig = new HashMap<>();
        overlayConfig.put("oryx.input-schema.num-features", 2);
        overlayConfig.put("oryx.input-schema.numeric-features", "[]");
        Config config = ConfigUtils.overlayOn(overlayConfig, ConfigUtils.getDefault());
        InputSchema schema = new InputSchema(config);
        assertEquals(2, schema.getNumFeatures());
        assertEquals(2, schema.getNumPredictors());
        assertTrue(schema.getFeatureNames().containsAll(Arrays.asList("0", "1")));
        for (int i = 0; i < 2; i++) {
            assertFalse(schema.isID(i));
            assertFalse(schema.isID(schema.getFeatureNames().get(i)));
        }
        for (int i = 0; i < 2; i++) {
            assertTrue(schema.isActive(i));
            assertTrue(schema.isActive(schema.getFeatureNames().get(i)));
        }
        for (int i = 0; i < 2; i++) {
            assertFalse(schema.isNumeric(i));
            assertFalse(schema.isNumeric(schema.getFeatureNames().get(i)));
        }
        for (int i = 0; i < 2; i++) {
            assertTrue(schema.isCategorical(i));
            assertTrue(schema.isCategorical(schema.getFeatureNames().get(i)));
        }
        for (int i = 0; i < 2; i++) {
            assertFalse(schema.isTarget(i));
            assertFalse(schema.isTarget(schema.getFeatureNames().get(i)));
        }
        assertFalse(schema.hasTarget());
        try {
            schema.getTargetFeature();
            fail();
        } catch (IllegalStateException ise) {
            // good
        }
        try {
            schema.getTargetFeatureIndex();
            fail();
        } catch (IllegalStateException ise) {
            // good
        }
    }

    @Test
    public void testConfig() {
        Map<String, Object> overlayConfig = new HashMap<>();
        overlayConfig.put("oryx.input-schema.feature-names", "[\"foo\",\"bar\",\"baz\",\"bing\"]");
        overlayConfig.put("oryx.input-schema.id-features", "[\"baz\"]");
        overlayConfig.put("oryx.input-schema.ignored-features", "[\"foo\"]");
        overlayConfig.put("oryx.input-schema.categorical-features", "[\"bar\"]");
        overlayConfig.put("oryx.input-schema.target-feature", "\"bar\"");
        Config config = ConfigUtils.overlayOn(overlayConfig, ConfigUtils.getDefault());
        InputSchema schema = new InputSchema(config);
        assertEquals(4, schema.getNumFeatures());
        assertEquals(1, schema.getNumPredictors());
        assertTrue(schema.getFeatureNames().containsAll(Arrays.asList("foo", "bar", "baz", "bing")));
        for (int i = 0; i < 4; i++) {
            assertEquals((i == 2), schema.isID(i));
            assertEquals((i == 2), schema.isID(schema.getFeatureNames().get(i)));
        }
        for (int i = 0; i < 4; i++) {
            assertEquals(((i == 1) || (i == 3)), schema.isActive(i));
            assertEquals(((i == 1) || (i == 3)), schema.isActive(schema.getFeatureNames().get(i)));
        }
        for (int i = 0; i < 4; i++) {
            assertEquals((i == 3), schema.isNumeric(i));
            assertEquals((i == 3), schema.isNumeric(schema.getFeatureNames().get(i)));
        }
        for (int i = 0; i < 4; i++) {
            assertEquals((i == 1), schema.isCategorical(i));
            assertEquals((i == 1), schema.isCategorical(schema.getFeatureNames().get(i)));
        }
        for (int i = 0; i < 4; i++) {
            assertEquals((i == 1), schema.isTarget(i));
            assertEquals((i == 1), schema.isTarget(schema.getFeatureNames().get(i)));
        }
        assertTrue(schema.hasTarget());
        assertEquals("bar", schema.getTargetFeature());
        assertEquals(1, schema.getTargetFeatureIndex());
    }

    @Test
    public void testActiveFeatureIndexMapping() {
        Map<String, Object> overlayConfig = new HashMap<>();
        overlayConfig.put("oryx.input-schema.feature-names", "[\"foo\",\"bar\",\"baz\",\"bing\"]");
        overlayConfig.put("oryx.input-schema.ignored-features", "[\"foo\",\"baz\"]");
        overlayConfig.put("oryx.input-schema.categorical-features", "[]");
        Config config = ConfigUtils.overlayOn(overlayConfig, ConfigUtils.getDefault());
        InputSchema schema = new InputSchema(config);
        assertEquals(0, schema.featureToPredictorIndex(1));
        assertEquals(1, schema.featureToPredictorIndex(3));
        assertEquals(1, schema.predictorToFeatureIndex(0));
        assertEquals(3, schema.predictorToFeatureIndex(1));
        try {
            schema.featureToPredictorIndex(2);
            fail();
        } catch (IllegalArgumentException iae) {
            // good
        }
        try {
            schema.predictorToFeatureIndex(2);
            fail();
        } catch (IllegalArgumentException iae) {
            // good
        }
    }

    @Test
    public void testActiveFeatureIndexMapping2() {
        Map<String, Object> overlayConfig = new HashMap<>();
        overlayConfig.put("oryx.input-schema.feature-names", "[\"foo\",\"bar\",\"baz\",\"bing\"]");
        overlayConfig.put("oryx.input-schema.ignored-features", "[\"foo\",\"baz\"]");
        overlayConfig.put("oryx.input-schema.target-feature", "\"bar\"");
        overlayConfig.put("oryx.input-schema.categorical-features", "[]");
        Config config = ConfigUtils.overlayOn(overlayConfig, ConfigUtils.getDefault());
        InputSchema schema = new InputSchema(config);
        assertEquals(0, schema.featureToPredictorIndex(3));
        assertEquals(3, schema.predictorToFeatureIndex(0));
    }
}

