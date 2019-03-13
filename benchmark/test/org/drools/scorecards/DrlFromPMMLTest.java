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
import ResourceType.DRL;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;


@Ignore
public class DrlFromPMMLTest {
    private static String drl;

    @Test
    public void testDrlNoNull() throws Exception {
        Assert.assertNotNull(DrlFromPMMLTest.drl);
        Assert.assertTrue(((DrlFromPMMLTest.drl.length()) > 0));
    }

    @Test
    public void testPackage() throws Exception {
        Assert.assertTrue(DrlFromPMMLTest.drl.contains("package org.drools.scorecards.example"));
    }

    @Test
    public void testRuleCount() throws Exception {
        Assert.assertEquals(61, StringUtil.countMatches(DrlFromPMMLTest.drl, "rule \""));
    }

    @Test
    public void testImports() throws Exception {
        Assert.assertEquals(2, StringUtil.countMatches(DrlFromPMMLTest.drl, "import "));
    }

    @Test
    public void testDRLExecution() throws Exception {
        KieServices ks = Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write(ks.getResources().newByteArrayResource(DrlFromPMMLTest.drl.getBytes()).setSourcePath("test_scorecard_rules.drl").setResourceType(DRL));
        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        Results res = kieBuilder.buildAll().getResults();
        KieContainer kieContainer = ks.newKieContainer(kieBuilder.getKieModule().getReleaseId());
        KieBase kbase = kieContainer.getKieBase();
        KieSession session = kbase.newKieSession();
        FactType scorecardType = kbase.getFactType("org.drools.scorecards.example", "SampleScore");
        Object scorecard = scorecardType.newInstance();
        scorecardType.set(scorecard, "age", 10);
        session.insert(scorecard);
        session.fireAllRules();
        session.dispose();
        // occupation = 5, age = 25, validLicence -1
        Assert.assertEquals(29.0, scorecardType.get(scorecard, "scorecard__calculatedScore"));
        session = kbase.newKieSession();
        scorecard = scorecardType.newInstance();
        scorecardType.set(scorecard, "occupation", "SKYDIVER");
        scorecardType.set(scorecard, "age", 0);
        session.insert(scorecard);
        session.fireAllRules();
        session.dispose();
        // occupation = -10, age = +10, validLicense = -1;
        Assert.assertEquals((-1.0), scorecardType.get(scorecard, "scorecard__calculatedScore"));
        session = kbase.newKieSession();
        scorecard = scorecardType.newInstance();
        scorecardType.set(scorecard, "residenceState", "AP");
        scorecardType.set(scorecard, "occupation", "TEACHER");
        scorecardType.set(scorecard, "age", 20);
        scorecardType.set(scorecard, "validLicense", true);
        session.insert(scorecard);
        session.fireAllRules();
        session.dispose();
        // occupation = +10, age = +40, state = -10, validLicense = 1
        Assert.assertEquals(41.0, scorecardType.get(scorecard, "scorecard__calculatedScore"));
    }
}

