/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.scorecards;


import PMMLExtensionNames.MODEL_IMPORTS;
import PMMLExtensionNames.MODEL_PACKAGE;
import org.drools.scorecards.pmml.ScorecardPMMLUtils;
import org.junit.Assert;
import org.junit.Test;


public class PMMLDocumentTest {
    private static PMML pmmlDocument;

    private static ScorecardCompiler scorecardCompiler;

    @Test
    public void testPMMLDocument() throws Exception {
        Assert.assertNotNull(PMMLDocumentTest.pmmlDocument);
        String pmml = PMMLDocumentTest.scorecardCompiler.getPMML();
        Assert.assertNotNull(pmml);
        Assert.assertTrue(((pmml.length()) > 0));
    }

    @Test
    public void testHeader() throws Exception {
        Header header = PMMLDocumentTest.pmmlDocument.getHeader();
        Assert.assertNotNull(header);
        Assert.assertNotNull(ScorecardPMMLUtils.getExtensionValue(header.getExtensions(), MODEL_PACKAGE));
        Assert.assertNotNull(ScorecardPMMLUtils.getExtensionValue(header.getExtensions(), MODEL_IMPORTS));
    }

    @Test
    public void testDataDictionary() throws Exception {
        DataDictionary dataDictionary = PMMLDocumentTest.pmmlDocument.getDataDictionary();
        Assert.assertNotNull(dataDictionary);
        Assert.assertEquals(5, dataDictionary.getNumberOfFields().intValue());
        Assert.assertEquals("age", dataDictionary.getDataFields().get(0).getName());
        Assert.assertEquals("occupation", dataDictionary.getDataFields().get(1).getName());
        Assert.assertEquals("residenceState", dataDictionary.getDataFields().get(2).getName());
        Assert.assertEquals("validLicense", dataDictionary.getDataFields().get(3).getName());
    }

    @Test
    public void testMiningSchema() throws Exception {
        for (Object serializable : PMMLDocumentTest.pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()) {
            if (serializable instanceof Scorecard) {
                for (Object obj : getExtensionsAndCharacteristicsAndMiningSchemas()) {
                    if (obj instanceof MiningSchema) {
                        MiningSchema miningSchema = ((MiningSchema) (obj));
                        Assert.assertEquals(5, miningSchema.getMiningFields().size());
                        Assert.assertEquals("age", miningSchema.getMiningFields().get(0).getName());
                        Assert.assertEquals("occupation", miningSchema.getMiningFields().get(1).getName());
                        Assert.assertEquals("residenceState", miningSchema.getMiningFields().get(2).getName());
                        Assert.assertEquals("validLicense", miningSchema.getMiningFields().get(3).getName());
                        return;
                    }
                }
            }
        }
        Assert.fail();
    }

    @Test
    public void testCharacteristicsAndAttributes() throws Exception {
        for (Object serializable : PMMLDocumentTest.pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()) {
            if (serializable instanceof Scorecard) {
                for (Object obj : getExtensionsAndCharacteristicsAndMiningSchemas()) {
                    if (obj instanceof Characteristics) {
                        Characteristics characteristics = ((Characteristics) (obj));
                        Assert.assertEquals(4, characteristics.getCharacteristics().size());
                        Assert.assertEquals("AgeScore", characteristics.getCharacteristics().get(0).getName());
                        Assert.assertEquals("$B$8", ScorecardPMMLUtils.getExtensionValue(characteristics.getCharacteristics().get(0).getExtensions(), "cellRef"));
                        Assert.assertEquals("OccupationScore", characteristics.getCharacteristics().get(1).getName());
                        Assert.assertEquals("$B$16", ScorecardPMMLUtils.getExtensionValue(characteristics.getCharacteristics().get(1).getExtensions(), "cellRef"));
                        Assert.assertEquals("ResidenceStateScore", characteristics.getCharacteristics().get(2).getName());
                        Assert.assertEquals("$B$22", ScorecardPMMLUtils.getExtensionValue(characteristics.getCharacteristics().get(2).getExtensions(), "cellRef"));
                        Assert.assertEquals("ValidLicenseScore", characteristics.getCharacteristics().get(3).getName());
                        Assert.assertEquals("$B$28", ScorecardPMMLUtils.getExtensionValue(characteristics.getCharacteristics().get(3).getExtensions(), "cellRef"));
                        return;
                    }
                }
            }
        }
        Assert.fail();
    }

    @Test
    public void testAgeScoreCharacteristic() throws Exception {
        for (Object serializable : PMMLDocumentTest.pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()) {
            if (serializable instanceof Scorecard) {
                for (Object obj : getExtensionsAndCharacteristicsAndMiningSchemas()) {
                    if (obj instanceof Characteristics) {
                        Characteristics characteristics = ((Characteristics) (obj));
                        Assert.assertEquals(4, characteristics.getCharacteristics().size());
                        Assert.assertEquals("AgeScore", characteristics.getCharacteristics().get(0).getName());
                        Assert.assertEquals("$B$8", ScorecardPMMLUtils.getExtensionValue(characteristics.getCharacteristics().get(0).getExtensions(), "cellRef"));
                        Assert.assertNotNull(characteristics.getCharacteristics().get(0).getAttributes());
                        Assert.assertEquals(4, characteristics.getCharacteristics().get(0).getAttributes().size());
                        Attribute attribute = characteristics.getCharacteristics().get(0).getAttributes().get(0);
                        Assert.assertEquals("$C$10", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        Assert.assertNotNull(attribute.getSimplePredicate());
                        attribute = characteristics.getCharacteristics().get(0).getAttributes().get(1);
                        Assert.assertEquals("$C$11", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        Assert.assertNotNull(attribute.getCompoundPredicate());
                        attribute = characteristics.getCharacteristics().get(0).getAttributes().get(2);
                        Assert.assertEquals("$C$12", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        Assert.assertNotNull(attribute.getCompoundPredicate());
                        attribute = characteristics.getCharacteristics().get(0).getAttributes().get(3);
                        Assert.assertEquals("$C$13", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        Assert.assertNotNull(attribute.getSimplePredicate());
                        return;
                    }
                }
            }
        }
        Assert.fail();
    }

    @Test
    public void testOccupationScoreCharacteristic() throws Exception {
        for (Object serializable : PMMLDocumentTest.pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()) {
            if (serializable instanceof Scorecard) {
                for (Object obj : getExtensionsAndCharacteristicsAndMiningSchemas()) {
                    if (obj instanceof Characteristics) {
                        Characteristics characteristics = ((Characteristics) (obj));
                        Assert.assertEquals(4, characteristics.getCharacteristics().size());
                        Assert.assertNotNull(characteristics.getCharacteristics().get(1).getAttributes());
                        Assert.assertEquals(3, characteristics.getCharacteristics().get(1).getAttributes().size());
                        Attribute attribute = characteristics.getCharacteristics().get(1).getAttributes().get(0);
                        Assert.assertEquals("$C$18", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        Assert.assertNotNull(ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "description"));
                        Assert.assertEquals("skydiving is a risky occupation", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "description"));
                        Assert.assertNotNull(attribute.getSimplePredicate());
                        attribute = characteristics.getCharacteristics().get(1).getAttributes().get(1);
                        Assert.assertEquals("$C$19", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        Assert.assertNotNull(attribute.getSimpleSetPredicate());
                        attribute = characteristics.getCharacteristics().get(1).getAttributes().get(2);
                        Assert.assertEquals("$C$20", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        Assert.assertNotNull(attribute.getSimplePredicate());
                        return;
                    }
                }
            }
        }
        Assert.fail();
    }

    @Test
    public void testResidenceStateScoreCharacteristic() throws Exception {
        for (Object serializable : PMMLDocumentTest.pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()) {
            if (serializable instanceof Scorecard) {
                for (Object obj : getExtensionsAndCharacteristicsAndMiningSchemas()) {
                    if (obj instanceof Characteristics) {
                        Characteristics characteristics = ((Characteristics) (obj));
                        Assert.assertEquals(4, characteristics.getCharacteristics().size());
                        Assert.assertNotNull(characteristics.getCharacteristics().get(2).getAttributes());
                        Assert.assertEquals(3, characteristics.getCharacteristics().get(2).getAttributes().size());
                        Attribute attribute = characteristics.getCharacteristics().get(2).getAttributes().get(0);
                        Assert.assertEquals("$C$24", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        Assert.assertNotNull(attribute.getSimplePredicate());
                        attribute = characteristics.getCharacteristics().get(2).getAttributes().get(1);
                        Assert.assertEquals("$C$25", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        Assert.assertNotNull(attribute.getSimplePredicate());
                        attribute = characteristics.getCharacteristics().get(2).getAttributes().get(2);
                        Assert.assertEquals("$C$26", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        Assert.assertNotNull(attribute.getSimplePredicate());
                        return;
                    }
                }
            }
        }
        Assert.fail();
    }

    @Test
    public void testValidLicenseScoreCharacteristic() throws Exception {
        for (Object serializable : PMMLDocumentTest.pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()) {
            if (serializable instanceof Scorecard) {
                for (Object obj : getExtensionsAndCharacteristicsAndMiningSchemas()) {
                    if (obj instanceof Characteristics) {
                        Characteristics characteristics = ((Characteristics) (obj));
                        Assert.assertEquals(4, characteristics.getCharacteristics().size());
                        Assert.assertNotNull(characteristics.getCharacteristics().get(3).getAttributes());
                        Assert.assertEquals(2, characteristics.getCharacteristics().get(3).getAttributes().size());
                        Attribute attribute = characteristics.getCharacteristics().get(3).getAttributes().get(0);
                        Assert.assertEquals("$C$30", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        Assert.assertNotNull(attribute.getSimplePredicate());
                        attribute = characteristics.getCharacteristics().get(3).getAttributes().get(1);
                        Assert.assertEquals("$C$31", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        Assert.assertNotNull(attribute.getSimplePredicate());
                        return;
                    }
                }
            }
        }
        Assert.fail();
    }

    @Test
    public void testScorecardWithExtensions() throws Exception {
        for (Object serializable : PMMLDocumentTest.pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()) {
            if (serializable instanceof Scorecard) {
                Scorecard scorecard = ((Scorecard) (serializable));
                Assert.assertEquals("Sample Score", scorecard.getModelName());
                // assertNotNull(ScorecardPMMLUtils.getExtension(scorecard.getExtensionsAndCharacteristicsAndMiningSchemas(), ScorecardPMMLExtensionNames.SCORECARD_OBJECT_CLASS));
                // assertNotNull(ScorecardPMMLUtils.getExtension(scorecard.getExtensionsAndCharacteristicsAndMiningSchemas(), ScorecardPMMLExtensionNames.SCORECARD_BOUND_VAR_NAME));
                return;
            }
        }
        Assert.fail();
    }

    @Test
    public void testOutput() throws Exception {
        for (Object serializable : PMMLDocumentTest.pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()) {
            if (serializable instanceof Scorecard) {
                Scorecard scorecard = ((Scorecard) (serializable));
                for (Object obj : scorecard.getExtensionsAndCharacteristicsAndMiningSchemas()) {
                    if (obj instanceof Output) {
                        Output output = ((Output) (obj));
                        Assert.assertEquals(1, output.getOutputFields().size());
                        Assert.assertNotNull(output.getOutputFields().get(0));
                        Assert.assertEquals("calculatedScore", output.getOutputFields().get(0).getName());
                        Assert.assertEquals("Final Score", output.getOutputFields().get(0).getDisplayName());
                        Assert.assertEquals("double", output.getOutputFields().get(0).getDataType().value());
                        Assert.assertEquals("predictedValue", output.getOutputFields().get(0).getFeature().value());
                        return;
                    }
                }
            }
        }
        Assert.fail();
    }
}

