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


import ResourceType.SCARD;
import java.io.InputStream;
import java.util.List;
import org.drools.compiler.compiler.ScoreCardProvider;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.scorecards.example.Applicant;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.Resource;
import org.kie.api.pmml.PMML4Data;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.runtime.rule.DataSource;
import org.kie.api.runtime.rule.RuleUnit;
import org.kie.api.runtime.rule.RuleUnitExecutor;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.ScoreCardConfiguration;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;


public class ScorecardProviderTest {
    private static String drl;

    private ScoreCardProvider scoreCardProvider;

    @Test
    public void testDrlWithoutSheetName() {
        InputStream is = ScorecardProviderTest.class.getResourceAsStream("/scoremodel_c.xls");
        Assert.assertNotNull(is);
        ScoreCardConfiguration scconf = KnowledgeBuilderFactory.newScoreCardConfiguration();
        KieBase kbase = scoreCardProvider.getKieBaseFromInputStream(is, scconf);
        Assert.assertNotNull(kbase);
    }

    @Test
    public void testDrlWithSheetName() {
        InputStream is = ScorecardProviderTest.class.getResourceAsStream("/scoremodel_c.xls");
        Assert.assertNotNull(is);
        ScoreCardConfiguration scconf = KnowledgeBuilderFactory.newScoreCardConfiguration();
        scconf.setWorksheetName("scorecards");
        KieBase kbase = scoreCardProvider.getKieBaseFromInputStream(is, scconf);
        Assert.assertNotNull(kbase);
    }

    @Test
    public void testKnowledgeBaseWithExection() {
        InputStream is = ScorecardProviderTest.class.getResourceAsStream("/scoremodel_c.xls");
        ScoreCardConfiguration scconf = KnowledgeBuilderFactory.newScoreCardConfiguration();
        scconf.setWorksheetName("scorecards");
        KieBase kbase = scoreCardProvider.getKieBaseFromInputStream(is, scconf);
        Assert.assertNotNull(kbase);
        RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        Assert.assertNotNull(executor);
        DataSource<PMMLRequestData> data = executor.newDataSource("request");
        DataSource<PMML4Result> resultData = executor.newDataSource("results");
        DataSource<PMML4Data> pmmlData = executor.newDataSource("pmmlData");
        PMMLRequestData request = new PMMLRequestData("123", "SampleScore");
        request.addRequestParam("age", 33.0);
        request.addRequestParam("occupation", "PROGRAMMER");
        request.addRequestParam("residenceState", "KN");
        request.addRequestParam("validLicense", true);
        data.insert(request);
        PMML4Result resultHolder = new PMML4Result("123");
        resultData.insert(resultHolder);
        List<String> possiblePackages = TestUtil.calculatePossiblePackageNames("Sample Score", "org.drools.scorecards.example");
        Class<? extends RuleUnit> ruleUnitClass = TestUtil.getStartingRuleUnit("RuleUnitIndicator", ((InternalKnowledgeBase) (kbase)), possiblePackages);
        int executions = executor.run(ruleUnitClass);
        Assert.assertTrue((executions > 0));
        Double calculatedScore = resultHolder.getResultValue("CalculatedScore", "value", Double.class).orElse(null);
        Assert.assertEquals(56.0, calculatedScore, 1.0E-6);
    }

    @Test
    public void testDrlGenerationWithExternalTypes() {
        Resource resource = ResourceFactory.newClassPathResource("scoremodel_externalmodel.xls");
        ScoreCardConfiguration scconf = KnowledgeBuilderFactory.newScoreCardConfiguration();
        scconf.setWorksheetName("scorecards");
        scconf.setUsingExternalTypes(true);
        resource.setConfiguration(scconf);
        resource.setResourceType(SCARD);
        KieBase kbase = new KieHelper().addResource(resource, SCARD).build();
        Assert.assertNotNull(kbase);
        RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        DataSource<PMMLRequestData> data = executor.newDataSource("request");
        DataSource<PMML4Result> resultData = executor.newDataSource("results");
        DataSource<PMML4Data> pmmlData = executor.newDataSource("pmmlData");
        DataSource<Applicant> applicantData = executor.newDataSource("externalBeanApplicant");
        PMMLRequestData request = new PMMLRequestData("123", "Sample Score");
        Applicant applicant = new Applicant();
        applicant.setAge(33.0);
        applicant.setOccupation("PROGRAMMER");
        applicant.setResidenceState("AP");
        applicant.setValidLicense(true);
        PMML4Result resultHolder = new PMML4Result("123");
        List<String> possiblePackages = TestUtil.calculatePossiblePackageNames("Sample Score", "org.drools.scorecards.example");
        Class<? extends RuleUnit> ruleUnitClass = TestUtil.getStartingRuleUnit("RuleUnitIndicator", ((InternalKnowledgeBase) (kbase)), possiblePackages);
        Assert.assertNotNull(ruleUnitClass);
        resultData.insert(resultHolder);
        data.insert(request);
        applicantData.insert(applicant);
        int count = executor.run(ruleUnitClass);
        Assert.assertTrue((count > 0));
        Double calculatedScore = resultHolder.getResultValue("Scorecard__calculatedScore", "value", Double.class).orElse(null);
        Assert.assertEquals(36.0, calculatedScore, 1.0E-6);
    }
}

