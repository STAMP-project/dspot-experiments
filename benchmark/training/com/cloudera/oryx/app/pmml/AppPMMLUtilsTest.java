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
package com.cloudera.oryx.app.pmml;


import com.cloudera.oryx.app.schema.CategoricalValueEncodings;
import com.cloudera.oryx.common.OryxTest;
import com.cloudera.oryx.common.pmml.PMMLUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dmg.pmml.Array;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.MiningField;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.OpType;
import org.dmg.pmml.PMML;
import org.dmg.pmml.Value;
import org.junit.Test;


public final class AppPMMLUtilsTest extends OryxTest {
    @Test
    public void testExtensionValue() throws Exception {
        PMML model = AppPMMLUtilsTest.buildDummyModel();
        assertNull(AppPMMLUtils.getExtensionValue(model, "foo"));
        AppPMMLUtils.addExtension(model, "foo", "bar");
        PMML reserializedModel = PMMLUtils.fromString(PMMLUtils.toString(model));
        assertEquals("bar", AppPMMLUtils.getExtensionValue(reserializedModel, "foo"));
    }

    @Test
    public void testExtensionContent() throws Exception {
        PMML model = AppPMMLUtilsTest.buildDummyModel();
        assertNull(AppPMMLUtils.getExtensionContent(model, "foo"));
        AppPMMLUtils.addExtensionContent(model, "foo1", Arrays.asList("bar", "baz"));
        AppPMMLUtils.addExtensionContent(model, "foo2", Collections.emptyList());
        AppPMMLUtils.addExtensionContent(model, "foo3", Arrays.asList(" c\" d \"e ", " c\" d \"e "));
        PMML reserializedModel = PMMLUtils.fromString(PMMLUtils.toString(model));
        assertEquals(Arrays.asList("bar", "baz"), AppPMMLUtils.getExtensionContent(reserializedModel, "foo1"));
        assertNull(AppPMMLUtils.getExtensionContent(reserializedModel, "foo2"));
        assertEquals(Arrays.asList(" c\" d \"e ", " c\" d \"e "), AppPMMLUtils.getExtensionContent(reserializedModel, "foo3"));
    }

    @Test
    public void testToArrayDouble() {
        Array a = AppPMMLUtils.toArray((-1.0), 2.01, 3.5);
        assertEquals(3, a.getN().intValue());
        assertEquals(Array.Type.REAL, a.getType());
        assertEquals("-1.0 2.01 3.5", a.getValue());
    }

    @Test
    public void testBuildMiningSchema() {
        MiningSchema miningSchema = AppPMMLUtils.buildMiningSchema(AppPMMLUtilsTest.buildTestSchema());
        List<MiningField> miningFields = miningSchema.getMiningFields();
        assertEquals(4, miningFields.size());
        String[] fieldNames = new String[]{ "foo", "bar", "baz", "bing" };
        for (int i = 0; i < (fieldNames.length); i++) {
            assertEquals(fieldNames[i], miningFields.get(i).getName().getValue());
        }
        assertEquals(MiningField.UsageType.SUPPLEMENTARY, miningFields.get(0).getUsageType());
        assertEquals(MiningField.UsageType.PREDICTED, miningFields.get(1).getUsageType());
        assertEquals(MiningField.UsageType.SUPPLEMENTARY, miningFields.get(2).getUsageType());
        assertEquals(MiningField.UsageType.ACTIVE, miningFields.get(3).getUsageType());
        assertEquals(OpType.CATEGORICAL, miningFields.get(1).getOpType());
        assertEquals(OpType.CONTINUOUS, miningFields.get(3).getOpType());
    }

    @Test
    public void testBuildDataDictionary() {
        Map<Integer, Collection<String>> distinctValues = new HashMap<>();
        distinctValues.put(1, Arrays.asList("one", "two", "three", "four", "five"));
        CategoricalValueEncodings categoricalValueEncodings = new CategoricalValueEncodings(distinctValues);
        DataDictionary dictionary = AppPMMLUtils.buildDataDictionary(AppPMMLUtilsTest.buildTestSchema(), categoricalValueEncodings);
        assertEquals(4, dictionary.getNumberOfFields().intValue());
        AppPMMLUtilsTest.checkDataField(dictionary.getDataFields().get(0), "foo", null);
        AppPMMLUtilsTest.checkDataField(dictionary.getDataFields().get(1), "bar", true);
        AppPMMLUtilsTest.checkDataField(dictionary.getDataFields().get(2), "baz", null);
        AppPMMLUtilsTest.checkDataField(dictionary.getDataFields().get(3), "bing", false);
        List<Value> dfValues = dictionary.getDataFields().get(1).getValues();
        assertEquals(5, dfValues.size());
        String[] categoricalValues = new String[]{ "one", "two", "three", "four", "five" };
        for (int i = 0; i < (categoricalValues.length); i++) {
            assertEquals(categoricalValues[i], dfValues.get(i).getValue());
        }
    }

    @Test
    public void testListFeaturesDD() {
        Map<Integer, Collection<String>> distinctValues = new HashMap<>();
        distinctValues.put(1, Arrays.asList("one", "two", "three", "four", "five"));
        CategoricalValueEncodings categoricalValueEncodings = new CategoricalValueEncodings(distinctValues);
        DataDictionary dictionary = AppPMMLUtils.buildDataDictionary(AppPMMLUtilsTest.buildTestSchema(), categoricalValueEncodings);
        List<String> featureNames = AppPMMLUtils.getFeatureNames(dictionary);
        assertEquals(Arrays.asList("foo", "bar", "baz", "bing"), featureNames);
    }

    @Test
    public void testListFeaturesMS() {
        MiningSchema miningSchema = AppPMMLUtils.buildMiningSchema(AppPMMLUtilsTest.buildTestSchema());
        List<String> featureNames = AppPMMLUtils.getFeatureNames(miningSchema);
        assertEquals(Arrays.asList("foo", "bar", "baz", "bing"), featureNames);
    }

    @Test
    public void testBuildCategoricalEncoding() {
        List<DataField> dataFields = new ArrayList<>();
        dataFields.add(new DataField(FieldName.create("foo"), OpType.CONTINUOUS, DataType.DOUBLE));
        DataField barField = new DataField(FieldName.create("bar"), OpType.CATEGORICAL, DataType.STRING);
        barField.addValues(new Value("b"), new Value("a"));
        dataFields.add(barField);
        DataDictionary dictionary = new DataDictionary(dataFields).setNumberOfFields(dataFields.size());
        CategoricalValueEncodings encodings = AppPMMLUtils.buildCategoricalValueEncodings(dictionary);
        assertEquals(2, encodings.getValueCount(1));
        assertEquals(0, encodings.getValueEncodingMap(1).get("b").intValue());
        assertEquals(1, encodings.getValueEncodingMap(1).get("a").intValue());
        assertEquals("b", encodings.getEncodingValueMap(1).get(0));
        assertEquals("a", encodings.getEncodingValueMap(1).get(1));
        assertEquals(Collections.singletonMap(1, 2), encodings.getCategoryCounts());
    }

    @Test
    public void testReadPMMLFromMessage() throws Exception {
        PMML pmml = PMMLUtils.buildSkeletonPMML();
        String pmmlString = PMMLUtils.toString(pmml);
        assertEquals(PMMLUtils.VERSION, AppPMMLUtils.readPMMLFromUpdateKeyMessage("MODEL", pmmlString, null).getVersion());
        Path pmmlPath = getTempDir().resolve("out.pmml");
        Files.write(pmmlPath, Collections.singleton(pmmlString));
        assertEquals(PMMLUtils.VERSION, AppPMMLUtils.readPMMLFromUpdateKeyMessage("MODEL-REF", pmmlPath.toAbsolutePath().toString(), null).getVersion());
        assertNull(AppPMMLUtils.readPMMLFromUpdateKeyMessage("MODEL-REF", "no-such-path", null));
    }
}

