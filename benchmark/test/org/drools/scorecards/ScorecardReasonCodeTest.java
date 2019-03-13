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


import KieServices.Factory;
import ResourceType.SCARD;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import org.dmg.pmml.pmml_4_2.descr.Attribute;
import org.dmg.pmml.pmml_4_2.descr.Characteristic;
import org.dmg.pmml.pmml_4_2.descr.Characteristics;
import org.dmg.pmml.pmml_4_2.descr.PMML;
import org.dmg.pmml.pmml_4_2.descr.Scorecard;
import org.drools.core.builder.conf.impl.ScoreCardConfigurationImpl;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.io.Resource;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.builder.ScoreCardConfiguration;
import org.kie.internal.io.ResourceFactory;
import org.kie.pmml.pmml_4_2.PMML4ExecutionHelper;
import org.kie.pmml.pmml_4_2.PMML4ExecutionHelper.PMML4ExecutionHelperFactory;

import static DrlType.INTERNAL_DECLARED_TYPES;


public class ScorecardReasonCodeTest {
    @Test
    public void testPMMLDocument() {
        final ScorecardCompiler scorecardCompiler = new ScorecardCompiler(INTERNAL_DECLARED_TYPES);
        boolean compileResult = scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_reasoncodes.xls"));
        if (!compileResult) {
            assertErrors(scorecardCompiler);
        }
        Assert.assertNotNull(scorecardCompiler.getPMMLDocument());
    }

    @Test
    public void testAbsenceOfReasonCodes() {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler(INTERNAL_DECLARED_TYPES);
        scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_c.xls"));
        PMML pmml = scorecardCompiler.getPMMLDocument();
        for (Object serializable : pmml.getAssociationModelsAndBaselineModelsAndClusteringModels()) {
            if (serializable instanceof Scorecard) {
                Assert.assertFalse(getUseReasonCodes());
            }
        }
    }

    @Test
    public void testUseReasonCodes() {
        final ScorecardCompiler scorecardCompiler = new ScorecardCompiler(INTERNAL_DECLARED_TYPES);
        boolean compileResult = scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_reasoncodes.xls"));
        if (!compileResult) {
            assertErrors(scorecardCompiler);
        }
        final PMML pmmlDocument = scorecardCompiler.getPMMLDocument();
        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()) {
            if (serializable instanceof Scorecard) {
                Assert.assertTrue(getUseReasonCodes());
                Assert.assertEquals(100.0, getInitialScore(), 0.0);
                Assert.assertEquals("pointsBelow", getReasonCodeAlgorithm());
            }
        }
    }

    @Test
    public void testReasonCodes() {
        final ScorecardCompiler scorecardCompiler = new ScorecardCompiler(INTERNAL_DECLARED_TYPES);
        boolean compileResult = scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_reasoncodes.xls"));
        if (!compileResult) {
            assertErrors(scorecardCompiler);
        }
        final PMML pmmlDocument = scorecardCompiler.getPMMLDocument();
        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()) {
            if (serializable instanceof Scorecard) {
                for (Object obj : getExtensionsAndCharacteristicsAndMiningSchemas()) {
                    if (obj instanceof Characteristics) {
                        Characteristics characteristics = ((Characteristics) (obj));
                        Assert.assertEquals(4, characteristics.getCharacteristics().size());
                        for (Characteristic characteristic : characteristics.getCharacteristics()) {
                            for (Attribute attribute : characteristic.getAttributes()) {
                                Assert.assertNotNull(attribute.getReasonCode());
                            }
                        }
                        return;
                    }
                }
            }
        }
        Assert.fail();
    }

    @Test
    public void testBaselineScores() {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler(INTERNAL_DECLARED_TYPES);
        boolean compileResult = scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_reasoncodes.xls"));
        if (!compileResult) {
            assertErrors(scorecardCompiler);
        }
        final PMML pmmlDocument = scorecardCompiler.getPMMLDocument();
        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()) {
            if (serializable instanceof Scorecard) {
                for (Object obj : getExtensionsAndCharacteristicsAndMiningSchemas()) {
                    if (obj instanceof Characteristics) {
                        Characteristics characteristics = ((Characteristics) (obj));
                        Assert.assertEquals(4, characteristics.getCharacteristics().size());
                        Assert.assertEquals(10.0, getBaselineScore(), 0.0);
                        Assert.assertEquals(99.0, getBaselineScore(), 0.0);
                        Assert.assertEquals(12.0, getBaselineScore(), 0.0);
                        Assert.assertEquals(15.0, getBaselineScore(), 0.0);
                        Assert.assertEquals(25.0, getBaselineScore(), 0.0);
                        return;
                    }
                }
            }
        }
        Assert.fail();
    }

    @Test
    public void testMissingReasonCodes() {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler();
        scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_reasoncodes.xls"), "scorecards_reason_error");
        Assert.assertEquals(3, scorecardCompiler.getScorecardParseErrors().size());
        Assert.assertEquals("$F$13", scorecardCompiler.getScorecardParseErrors().get(0).getErrorLocation());
        Assert.assertEquals("$F$22", scorecardCompiler.getScorecardParseErrors().get(1).getErrorLocation());
    }

    @Test
    public void testMissingBaselineScores() {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler(INTERNAL_DECLARED_TYPES);
        scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_reasoncodes.xls"), "scorecards_reason_error");
        Assert.assertEquals(3, scorecardCompiler.getScorecardParseErrors().size());
        Assert.assertEquals("$D$30", scorecardCompiler.getScorecardParseErrors().get(2).getErrorLocation());
    }

    @Test
    public void testReasonCodesCombinations() {
        KieServices ks = Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write(ks.getResources().newClassPathResource("scoremodel_reasoncodes.xls").setSourcePath("scoremodel_reasoncodes.xls").setResourceType(SCARD));
        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        Results res = kieBuilder.buildAll().getResults();
        KieContainer kieContainer = ks.newKieContainer(kieBuilder.getKieModule().getReleaseId());
        KieBase kbase = kieContainer.getKieBase();
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("SampleScore", kbase);
        helper.addPossiblePackageName("org.drools.scorecards.example");
        PMMLRequestData request = addParameter("age", 10.0, Double.class).addParameter("validLicense", false, Boolean.class).build();
        PMML4Result resultHolder = helper.submitRequest(request);
        Assert.assertEquals("OK", resultHolder.getResultCode());
        assertCalculatedScore(resultHolder, 129.0);
        LinkedHashMap<String, Object> lhm = checkAndGetReasonCodeMap(resultHolder, 2);
        Assert.assertEquals(2, lhm.size());
        Assert.assertEquals(16.0, lhm.get("VL002"));
        Assert.assertEquals((-20.0), lhm.get("AGE02"));
        assertReasonCode(resultHolder, "VL002");
        request = addParameter("age", 0.0, Double.class).addParameter("occupation", "SKYDIVER", String.class).addParameter("validLicense", false, Boolean.class).build();
        resultHolder = helper.submitRequest(request);
        assertCalculatedScore(resultHolder, 99.0);
        lhm = checkAndGetReasonCodeMap(resultHolder, 3);
        Assert.assertEquals(109.0, lhm.get("OCC01"));
        Assert.assertEquals(16.0, lhm.get("VL002"));
        Assert.assertEquals(0.0, lhm.get("AGE01"));
        assertReasonCode(resultHolder, "OCC01");
        request = addParameter("age", 20.0, Double.class).addParameter("occupation", "TEACHER", String.class).addParameter("residenceState", "AP", String.class).addParameter("validLicense", true, Boolean.class).build();
        resultHolder = helper.submitRequest(request);
        assertCalculatedScore(resultHolder, 141.0);
        lhm = checkAndGetReasonCodeMap(resultHolder, 4);
        Assert.assertEquals(89.0, lhm.get("OCC02"));
        Assert.assertEquals(22.0, lhm.get("RS001"));
        Assert.assertEquals(14.0, lhm.get("VL001"));
        Assert.assertEquals((-30.0), lhm.get("AGE03"));
        assertReasonCode(resultHolder, "OCC02");
    }

    @Test
    public void testPointsAbove() {
        Resource resource = ResourceFactory.newClassPathResource("scoremodel_reasoncodes.xls").setResourceType(SCARD);
        ScoreCardConfiguration resConf = new ScoreCardConfigurationImpl();
        resConf.setWorksheetName("scorecards_pointsAbove");
        resource.setConfiguration(resConf);
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("SampleScore", resource, null);
        helper.addPossiblePackageName("org.drools.scorecards.example");
        PMMLRequestData request = addParameter("age", 10.0, Double.class).addParameter("validLicense", false, Boolean.class).build();
        PMML4Result resultHolder = helper.submitRequest(request);
        assertCalculatedScore(resultHolder, 29.0);
        LinkedHashMap<String, Object> reasonCodesMap = checkAndGetReasonCodeMap(resultHolder, 2);
        Assert.assertEquals((-16.0), reasonCodesMap.get("VL002"));
        Assert.assertEquals(20.0, reasonCodesMap.get("AGE02"));
        assertReasonCode(resultHolder, "AGE02");
        request = addParameter("age", 0.0, Double.class).addParameter("occupation", "SKYDIVER", String.class).addParameter("validLicense", false, Boolean.class).build();
        resultHolder = helper.submitRequest(request);
        assertCalculatedScore(resultHolder, (-1.0));
        reasonCodesMap = checkAndGetReasonCodeMap(resultHolder, 3);
        Assert.assertEquals((-109.0), reasonCodesMap.get("OCC01"));
        Assert.assertEquals((-16.0), reasonCodesMap.get("VL002"));
        Assert.assertEquals(0.0, reasonCodesMap.get("AGE01"));
        Assert.assertEquals(Arrays.asList("AGE01", "VL002", "OCC01"), new ArrayList(reasonCodesMap.keySet()));
        assertReasonCode(resultHolder, "AGE01");
        request = addParameter("age", 20.0, Double.class).addParameter("occupation", "TEACHER", String.class).addParameter("residenceState", "AP", String.class).addParameter("validLicense", true, Boolean.class).build();
        resultHolder = helper.submitRequest(request);
        assertCalculatedScore(resultHolder, 41.0);
        reasonCodesMap = checkAndGetReasonCodeMap(resultHolder, 4);
        Assert.assertEquals((-89.0), reasonCodesMap.get("OCC02"));
        Assert.assertEquals((-22.0), reasonCodesMap.get("RS001"));
        Assert.assertEquals((-14.0), reasonCodesMap.get("VL001"));
        Assert.assertEquals(30.0, reasonCodesMap.get("AGE03"));
        Assert.assertEquals(Arrays.asList("AGE03", "VL001", "RS001", "OCC02"), new ArrayList(reasonCodesMap.keySet()));
        assertReasonCode(resultHolder, "AGE03");
    }

    @Test
    public void testPointsBelow() {
        Resource resource = ResourceFactory.newClassPathResource("scoremodel_reasoncodes.xls").setResourceType(SCARD);
        ScoreCardConfiguration resConf = new ScoreCardConfigurationImpl();
        resConf.setWorksheetName("scorecards_pointsBelow");
        resource.setConfiguration(resConf);
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("SampleScore", resource, null);
        helper.addPossiblePackageName("org.drools.scorecards.example");
        PMMLRequestData request = addParameter("age", 10.0, Double.class).addParameter("validLicense", false, Boolean.class).build();
        PMML4Result resultHolder = helper.submitRequest(request);
        assertCalculatedScore(resultHolder, 29.0);
        LinkedHashMap<String, Object> reasonCodesMap = checkAndGetReasonCodeMap(resultHolder, 2);
        Assert.assertEquals(16.0, reasonCodesMap.get("VL002"));
        Assert.assertEquals((-20.0), reasonCodesMap.get("AGE02"));
        assertReasonCode(resultHolder, "VL002");
        request = addParameter("age", 0.0, Double.class).addParameter("occupation", "SKYDIVER", String.class).addParameter("validLicense", false, Boolean.class).build();
        resultHolder = helper.submitRequest(request);
        assertCalculatedScore(resultHolder, (-1.0));
        reasonCodesMap = checkAndGetReasonCodeMap(resultHolder, 3);
        Assert.assertEquals(109.0, reasonCodesMap.get("OCC01"));
        Assert.assertEquals(16.0, reasonCodesMap.get("VL002"));
        Assert.assertEquals(0.0, reasonCodesMap.get("AGE01"));
        Assert.assertEquals(Arrays.asList("OCC01", "VL002", "AGE01"), new ArrayList(reasonCodesMap.keySet()));
        assertReasonCode(resultHolder, "OCC01");
        request = addParameter("age", 20.0, Double.class).addParameter("occupation", "TEACHER", String.class).addParameter("residenceState", "AP", String.class).addParameter("validLicense", true, Boolean.class).build();
        resultHolder = helper.submitRequest(request);
        assertCalculatedScore(resultHolder, 41.0);
        reasonCodesMap = checkAndGetReasonCodeMap(resultHolder, 4);
        Assert.assertEquals(89.0, reasonCodesMap.get("OCC02"));
        Assert.assertEquals(22.0, reasonCodesMap.get("RS001"));
        Assert.assertEquals(14.0, reasonCodesMap.get("VL001"));
        Assert.assertEquals((-30.0), reasonCodesMap.get("AGE03"));
        Assert.assertEquals(Arrays.asList("OCC02", "RS001", "VL001", "AGE03"), new ArrayList(reasonCodesMap.keySet()));
        assertReasonCode(resultHolder, "OCC02");
    }
}

