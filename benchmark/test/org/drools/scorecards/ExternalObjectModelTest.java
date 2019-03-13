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


import PMMLExtensionNames.EXTERNAL_CLASS;
import ResourceType.SCARD;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dmg.pmml.pmml_4_2.descr.Extension;
import org.dmg.pmml.pmml_4_2.descr.Output;
import org.dmg.pmml.pmml_4_2.descr.OutputField;
import org.dmg.pmml.pmml_4_2.descr.PMML;
import org.dmg.pmml.pmml_4_2.descr.Scorecard;
import org.drools.compiler.compiler.ScoreCardProvider;
import org.drools.scorecards.example.Applicant;
import org.drools.scorecards.pmml.ScorecardPMMLUtils;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.io.Resource;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.ScoreCardConfiguration;
import org.kie.internal.io.ResourceFactory;
import org.kie.pmml.pmml_4_2.PMML4Compiler;
import org.kie.pmml.pmml_4_2.PMML4ExecutionHelper;
import org.kie.pmml.pmml_4_2.PMML4ExecutionHelper.PMML4ExecutionHelperFactory;


// @Ignore
public class ExternalObjectModelTest {
    private static ScorecardCompiler scorecardCompiler;

    private static ScoreCardProvider scorecardProvider;

    @Test
    public void testPMMLCustomOutput() throws Exception {
        PMML pmmlDocument = null;
        String drl = null;
        if (ExternalObjectModelTest.scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_externalmodel.xls"))) {
            pmmlDocument = ExternalObjectModelTest.scorecardCompiler.getPMMLDocument();
            Assert.assertNotNull(pmmlDocument);
            PMML4Compiler.dumpModel(pmmlDocument, System.out);
            drl = ExternalObjectModelTest.scorecardCompiler.getDRL();
            Assert.assertTrue(((drl != null) && (!(drl.isEmpty()))));
            // System.out.println(drl);
        } else {
            Assert.fail("failed to parse scoremodel Excel.");
        }
        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()) {
            if (serializable instanceof Scorecard) {
                Scorecard scorecard = ((Scorecard) (serializable));
                for (Object obj : scorecard.getExtensionsAndCharacteristicsAndMiningSchemas()) {
                    if (obj instanceof Output) {
                        Output output = ((Output) (obj));
                        final List<OutputField> outputFields = output.getOutputFields();
                        Assert.assertEquals(1, outputFields.size());
                        final OutputField outputField = outputFields.get(0);
                        Assert.assertNotNull(outputField);
                        Assert.assertEquals("totalScore", outputField.getName());
                        Assert.assertEquals("Final Score", outputField.getDisplayName());
                        Assert.assertEquals("double", outputField.getDataType().value());
                        Assert.assertEquals("predictedValue", outputField.getFeature().value());
                        final Extension extension = ScorecardPMMLUtils.getExtension(outputField.getExtensions(), EXTERNAL_CLASS);
                        Assert.assertNotNull(extension);
                        Assert.assertEquals("org.drools.scorecards.example.Applicant", extension.getValue());
                        return;
                    }
                }
            }
        }
        Assert.fail();
    }

    @Test
    public void testWithInitialScore() throws Exception {
        Map<String, List<Object>> externalData = new HashMap<>();
        List<Object> applicantValues = new ArrayList<>();
        Resource resource = ResourceFactory.newClassPathResource("scoremodel_externalmodel.xls");
        Assert.assertNotNull(resource);
        ScoreCardConfiguration scconf = KnowledgeBuilderFactory.newScoreCardConfiguration();
        scconf.setUsingExternalTypes(true);
        scconf.setWorksheetName("scorecards_initialscore");
        resource.setConfiguration(scconf);
        resource.setResourceType(SCARD);
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("Sample Score", resource, null, false);
        helper.addExternalDataSource("externalBeanApplicant");
        helper.addPossiblePackageName("org.drools.scorecards.example");
        Applicant applicant = new Applicant();
        applicant.setAge(10.0);
        applicantValues.add(applicant);
        externalData.put("externalBeanApplicant", applicantValues);
        PMMLRequestData request = new PMMLRequestData("123", "Sample Score");
        PMML4Result resultHolder = helper.submitRequest(request, externalData);
        // occupation = 0, age = 30, validLicence -1, initialScore=100
        checkResults(129.0, resultHolder);
        applicant = new Applicant();
        applicant.setOccupation("SKYDIVER");
        applicant.setAge(0);
        applicantValues.clear();
        applicantValues.add(applicant);
        request = new PMMLRequestData("234", "Sample Score");
        resultHolder = helper.submitRequest(request, externalData);
        // occupation = -10, age = +10, validLicense = -1, initialScore=100;
        checkResults(99.0, resultHolder);
        applicant = new Applicant();
        applicant.setResidenceState("AP");
        applicant.setOccupation("TEACHER");
        applicant.setAge(20);
        applicant.setValidLicense(true);
        applicantValues.clear();
        applicantValues.add(applicant);
        request = new PMMLRequestData("345", "Sample Score");
        resultHolder = helper.submitRequest(request, externalData);
        // occupation = +10, age = +40, state = -10, validLicense = 1, initialScore=100
        checkResults(141.0, resultHolder);
    }

    @Test
    public void testWithReasonCodes() throws Exception {
        Map<String, List<Object>> externalData = new HashMap<>();
        List<Object> applicantValues = new ArrayList<>();
        Resource resource = ResourceFactory.newClassPathResource("scoremodel_externalmodel.xls");
        Assert.assertNotNull(resource);
        ScoreCardConfiguration scconf = KnowledgeBuilderFactory.newScoreCardConfiguration();
        scconf.setUsingExternalTypes(true);
        scconf.setWorksheetName("scorecards_reasoncode");
        resource.setConfiguration(scconf);
        resource.setResourceType(SCARD);
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("Sample Score", resource, null, false);
        helper.addExternalDataSource("externalBeanApplicant");
        helper.addPossiblePackageName("org.drools.scorecards.example");
        Applicant applicant = new Applicant();
        applicant.setAge(10);
        applicantValues.add(applicant);
        externalData.put("externalBeanApplicant", applicantValues);
        PMMLRequestData request = new PMMLRequestData("123", "Sample Score");
        PMML4Result resultHolder = helper.submitRequest(request, externalData);
        // occupation = 0, age = 30, validLicence -1, initialScore=100
        checkResults(129.0, "VL0099", Arrays.asList("VL0099", "AGE02"), resultHolder);
        applicant = new Applicant();
        applicant.setOccupation("SKYDIVER");
        applicant.setAge(0);
        applicantValues.clear();
        applicantValues.add(applicant);
        request = new PMMLRequestData("234", "Sample Score");
        resultHolder = helper.submitRequest(request, externalData);
        // occupation = -10, age = +10, validLicense = -1, initialScore=100;
        checkResults(99.0, "OC0099", Arrays.asList("OC0099", "VL0099", "AGE01"), resultHolder);
        applicant = new Applicant();
        applicant.setResidenceState("AP");
        applicant.setOccupation("TEACHER");
        applicant.setAge(20);
        applicant.setValidLicense(true);
        applicantValues.clear();
        applicantValues.add(applicant);
        request = new PMMLRequestData("234", "Sample Score");
        resultHolder = helper.submitRequest(request, externalData);
        // occupation = +10, age = +40, state = -10, validLicense = 1, initialScore=100
        checkResults(141.0, "RS001", Arrays.asList("RS001", "VL001", "OC0099", "AGE03"), resultHolder);
    }
}

