/**
 * Copyright 2014 OWASP.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.owasp.dependencycheck.analyzer;


import Confidence.HIGHEST;
import EvidenceType.PRODUCT;
import Part.APPLICATION;
import Settings.KEYS;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.Engine;
import org.owasp.dependencycheck.dependency.Confidence;
import org.owasp.dependencycheck.dependency.Dependency;
import org.owasp.dependencycheck.dependency.naming.CpeIdentifier;
import us.springett.parsers.cpe.Cpe;
import us.springett.parsers.cpe.CpeBuilder;

import static AnalysisPhase.POST_IDENTIFIER_ANALYSIS;


/**
 *
 *
 * @author Jeremy Long
 */
public class FalsePositiveAnalyzerTest extends BaseTest {
    /**
     * A CPE builder object.
     */
    private CpeBuilder builder = new CpeBuilder();

    /**
     * Test of getName method, of class FalsePositiveAnalyzer.
     */
    @Test
    public void testGetName() {
        FalsePositiveAnalyzer instance = new FalsePositiveAnalyzer();
        String expResult = "False Positive Analyzer";
        String result = instance.getName();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getAnalysisPhase method, of class FalsePositiveAnalyzer.
     */
    @Test
    public void testGetAnalysisPhase() {
        FalsePositiveAnalyzer instance = new FalsePositiveAnalyzer();
        AnalysisPhase expResult = POST_IDENTIFIER_ANALYSIS;
        AnalysisPhase result = instance.getAnalysisPhase();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getAnalyzerEnabledSettingKey method, of class
     * FalsePositiveAnalyzer.
     */
    @Test
    public void testGetAnalyzerEnabledSettingKey() {
        FalsePositiveAnalyzer instance = new FalsePositiveAnalyzer();
        String expResult = KEYS.ANALYZER_FALSE_POSITIVE_ENABLED;
        String result = instance.getAnalyzerEnabledSettingKey();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of analyzeDependency method, of class FalsePositiveAnalyzer.
     */
    @Test
    public void testAnalyzeDependency() throws Exception {
        Dependency dependency = new Dependency();
        dependency.setFileName("pom.xml");
        dependency.setFilePath("pom.xml");
        Cpe cpe = builder.part(APPLICATION).vendor("file").product("file").version("1.2.1").build();
        CpeIdentifier id = new CpeIdentifier(cpe, "http://some.org/url", Confidence.HIGHEST);
        dependency.addVulnerableSoftwareIdentifier(id);
        Engine engine = null;
        FalsePositiveAnalyzer instance = new FalsePositiveAnalyzer();
        int before = dependency.getVulnerableSoftwareIdentifiers().size();
        instance.analyze(dependency, engine);
        int after = dependency.getVulnerableSoftwareIdentifiers().size();
        Assert.assertTrue((before > after));
    }

    /**
     * Test of removeBadMatches method, of class FalsePositiveAnalyzer.
     */
    @Test
    public void testRemoveBadMatches() throws Exception {
        Dependency dependency = new Dependency();
        dependency.setFileName("some.jar");
        dependency.setFilePath("some.jar");
        Cpe cpe = builder.part(APPLICATION).vendor("m-core").product("m-core").build();
        CpeIdentifier id = new CpeIdentifier(cpe, Confidence.HIGHEST);
        dependency.addVulnerableSoftwareIdentifier(id);
        Assert.assertEquals(1, dependency.getVulnerableSoftwareIdentifiers().size());
        FalsePositiveAnalyzer instance = new FalsePositiveAnalyzer();
        instance.removeBadMatches(dependency);
        Assert.assertEquals(0, dependency.getVulnerableSoftwareIdentifiers().size());
        dependency.addVulnerableSoftwareIdentifier(id);
        dependency.addEvidence(PRODUCT, "test", "name", "m-core", HIGHEST);
        instance.removeBadMatches(dependency);
        Assert.assertEquals(1, dependency.getVulnerableSoftwareIdentifiers().size());
    }
}

