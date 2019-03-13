/**
 * This file is part of dependency-check-cofre.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) 2018 Jeremy Long. All Rights Reserved.
 */
package org.owasp.dependencycheck.analyzer;


import EvidenceType.PRODUCT;
import EvidenceType.VERSION;
import Settings.KEYS;
import java.io.File;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseDBTestCase;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.Engine;
import org.owasp.dependencycheck.dependency.Dependency;
import org.owasp.dependencycheck.dependency.Evidence;
import org.owasp.dependencycheck.dependency.Vulnerability;

import static AnalysisPhase.FINDING_ANALYSIS;


public class RetireJsAnalyzerIT extends BaseDBTestCase {
    private RetireJsAnalyzer analyzer;

    private Engine engine;

    @Test
    public void testGetName() {
        Assert.assertThat(analyzer.getName(), CoreMatchers.is("RetireJS Analyzer"));
    }

    /**
     * Test of getSupportedExtensions method.
     */
    @Test
    public void testAcceptSupportedExtensions() throws Exception {
        analyzer.setEnabled(true);
        String[] files = new String[]{ "test.js", "test.min.js" };
        for (String name : files) {
            Assert.assertTrue(name, analyzer.accept(new File(name)));
        }
    }

    /**
     * Test of getAnalysisPhase method.
     */
    @Test
    public void testGetAnalysisPhase() {
        AnalysisPhase expResult = FINDING_ANALYSIS;
        AnalysisPhase result = analyzer.getAnalysisPhase();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getAnalyzerEnabledSettingKey method.
     */
    @Test
    public void testGetAnalyzerEnabledSettingKey() {
        String expResult = KEYS.ANALYZER_RETIREJS_ENABLED;
        String result = analyzer.getAnalyzerEnabledSettingKey();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of inspect method.
     *
     * @throws Exception
     * 		is thrown when an exception occurs.
     */
    @Test
    public void testJquery() throws Exception {
        File file = BaseTest.getResourceAsFile(this, "javascript/jquery-1.6.2.js");
        Dependency dependency = new Dependency(file);
        analyzer.analyze(dependency, engine);
        Assert.assertEquals("jquery", dependency.getName());
        Assert.assertEquals("1.6.2", dependency.getVersion());
        Assert.assertEquals(1, dependency.getEvidence(PRODUCT).size());
        Evidence product = dependency.getEvidence(PRODUCT).iterator().next();
        Assert.assertEquals("name", product.getName());
        Assert.assertEquals("jquery", product.getValue());
        Assert.assertEquals(1, dependency.getEvidence(VERSION).size());
        Evidence version = dependency.getEvidence(VERSION).iterator().next();
        Assert.assertEquals("version", version.getName());
        Assert.assertEquals("1.6.2", version.getValue());
        Assert.assertEquals(3, dependency.getVulnerabilities().size());
        Assert.assertTrue(dependency.getVulnerabilities().contains(new Vulnerability("CVE-2015-9251")));
        Assert.assertTrue(dependency.getVulnerabilities().contains(new Vulnerability("CVE-2011-4969")));
        Assert.assertTrue(dependency.getVulnerabilities().contains(new Vulnerability("CVE-2012-6708")));
    }

    /**
     * Test of inspect method.
     *
     * @throws Exception
     * 		is thrown when an exception occurs.
     */
    @Test
    public void testAngular() throws Exception {
        File file = BaseTest.getResourceAsFile(this, "javascript/angular.safe.js");
        Dependency dependency = new Dependency(file);
        analyzer.analyze(dependency, engine);
        Assert.assertEquals("angularjs", dependency.getName());
        Assert.assertEquals("1.2.27", dependency.getVersion());
        Assert.assertEquals(1, dependency.getEvidence(PRODUCT).size());
        Evidence product = dependency.getEvidence(PRODUCT).iterator().next();
        Assert.assertEquals("name", product.getName());
        Assert.assertEquals("angularjs", product.getValue());
        Assert.assertEquals(1, dependency.getEvidence(VERSION).size());
        Evidence version = dependency.getEvidence(VERSION).iterator().next();
        Assert.assertEquals("version", version.getName());
        Assert.assertEquals("1.2.27", version.getValue());
        Assert.assertEquals(4, dependency.getVulnerabilities().size());
        Assert.assertTrue(dependency.getVulnerabilities().contains(new Vulnerability("Universal CSP bypass via add-on in Firefox")));
        Assert.assertTrue(dependency.getVulnerabilities().contains(new Vulnerability("XSS in $sanitize in Safari/Firefox")));
        Assert.assertTrue(dependency.getVulnerabilities().contains(new Vulnerability("DOS in $sanitize")));
        Assert.assertTrue(dependency.getVulnerabilities().contains(new Vulnerability("The attribute usemap can be used as a security exploit")));
    }

    /**
     * Test of inspect method.
     *
     * @throws Exception
     * 		is thrown when an exception occurs.
     */
    @Test
    public void testEmber() throws Exception {
        File file = BaseTest.getResourceAsFile(this, "javascript/ember.js");
        Dependency dependency = new Dependency(file);
        analyzer.analyze(dependency, engine);
        Assert.assertEquals("ember", dependency.getName());
        Assert.assertEquals("1.3.0", dependency.getVersion());
        Assert.assertEquals(1, dependency.getEvidence(PRODUCT).size());
        Evidence product = dependency.getEvidence(PRODUCT).iterator().next();
        Assert.assertEquals("name", product.getName());
        Assert.assertEquals("ember", product.getValue());
        Assert.assertEquals(1, dependency.getEvidence(VERSION).size());
        Evidence version = dependency.getEvidence(VERSION).iterator().next();
        Assert.assertEquals("version", version.getName());
        Assert.assertEquals("1.3.0", version.getValue());
        Assert.assertEquals(3, dependency.getVulnerabilities().size());
        Assert.assertTrue(dependency.getVulnerabilities().contains(new Vulnerability("CVE-2014-0013")));
        Assert.assertTrue(dependency.getVulnerabilities().contains(new Vulnerability("CVE-2014-0014")));
        Assert.assertTrue(dependency.getVulnerabilities().contains(new Vulnerability("CVE-2014-0046")));
    }
}

