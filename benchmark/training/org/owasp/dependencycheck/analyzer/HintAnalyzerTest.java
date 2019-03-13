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


import Confidence.HIGH;
import EvidenceType.PRODUCT;
import EvidenceType.VENDOR;
import EvidenceType.VERSION;
import Settings.KEYS.ANALYZER_CENTRAL_ENABLED;
import Settings.KEYS.ANALYZER_NEXUS_ENABLED;
import Settings.KEYS.AUTO_UPDATE;
import Settings.KEYS.HINTS_FILE;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseDBTestCase;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.Engine;
import org.owasp.dependencycheck.dependency.Confidence;
import org.owasp.dependencycheck.dependency.Dependency;
import org.owasp.dependencycheck.dependency.Evidence;

import static AnalysisPhase.PRE_IDENTIFIER_ANALYSIS;


/**
 *
 *
 * @author Jeremy Long
 */
public class HintAnalyzerTest extends BaseDBTestCase {
    /**
     * Test of getName method, of class HintAnalyzer.
     */
    @Test
    public void testGetName() {
        HintAnalyzer instance = new HintAnalyzer();
        String expResult = "Hint Analyzer";
        String result = instance.getName();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getAnalysisPhase method, of class HintAnalyzer.
     */
    @Test
    public void testGetAnalysisPhase() {
        HintAnalyzer instance = new HintAnalyzer();
        AnalysisPhase expResult = PRE_IDENTIFIER_ANALYSIS;
        AnalysisPhase result = instance.getAnalysisPhase();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of analyze method, of class HintAnalyzer.
     */
    @Test
    public void testAnalyze() throws Exception {
        // File guice = new File(this.getClass().getClassLoader().getResource("guice-3.0.jar").getPath());
        File guice = BaseTest.getResourceAsFile(this, "guice-3.0.jar");
        // Dependency guice = new EngineDependency(fileg);
        // File spring = new File(this.getClass().getClassLoader().getResource("spring-core-3.0.0.RELEASE.jar").getPath());
        File spring = BaseTest.getResourceAsFile(this, "spring-core-3.0.0.RELEASE.jar");
        // Dependency spring = new Dependency(files);
        getSettings().setBoolean(AUTO_UPDATE, false);
        getSettings().setBoolean(ANALYZER_NEXUS_ENABLED, false);
        getSettings().setBoolean(ANALYZER_CENTRAL_ENABLED, false);
        try (Engine engine = new Engine(getSettings())) {
            engine.scan(guice);
            engine.scan(spring);
            engine.analyzeDependencies();
            Dependency gdep = null;
            Dependency sdep = null;
            for (Dependency d : engine.getDependencies()) {
                if (d.getActualFile().equals(guice)) {
                    gdep = d;
                } else
                    if (d.getActualFile().equals(spring)) {
                        sdep = d;
                    }

            }
            final Evidence springTest1 = new Evidence("hint analyzer", "product", "springsource_spring_framework", Confidence.HIGH);
            final Evidence springTest2 = new Evidence("hint analyzer", "vendor", "SpringSource", Confidence.HIGH);
            final Evidence springTest3 = new Evidence("hint analyzer", "vendor", "vmware", Confidence.HIGH);
            final Evidence springTest4 = new Evidence("hint analyzer", "product", "springsource_spring_framework", Confidence.HIGH);
            final Evidence springTest5 = new Evidence("hint analyzer", "vendor", "vmware", Confidence.HIGH);
            Assert.assertFalse(gdep.contains(PRODUCT, springTest1));
            Assert.assertFalse(gdep.contains(VENDOR, springTest2));
            Assert.assertFalse(gdep.contains(VENDOR, springTest3));
            Assert.assertFalse(gdep.contains(PRODUCT, springTest4));
            Assert.assertFalse(gdep.contains(VENDOR, springTest5));
            Assert.assertTrue(sdep.contains(PRODUCT, springTest1));
            Assert.assertTrue(sdep.contains(VENDOR, springTest2));
            Assert.assertTrue(sdep.contains(VENDOR, springTest3));
            // assertTrue(evidence.contains(springTest4));
            // assertTrue(evidence.contains(springTest5));
        }
    }

    /**
     * Test of analyze method, of class HintAnalyzer.
     */
    @Test
    public void testAnalyze_1() throws Exception {
        File path = BaseTest.getResourceAsFile(this, "hints_12.xml");
        getSettings().setString(HINTS_FILE, path.getPath());
        HintAnalyzer instance = new HintAnalyzer();
        instance.initialize(getSettings());
        instance.prepare(null);
        Dependency d = new Dependency();
        d.addEvidence(VERSION, "version source", "given version name", "1.2.3", HIGH);
        d.addEvidence(VERSION, "hint analyzer", "remove version name", "value", HIGH);
        d.addEvidence(VENDOR, "hint analyzer", "remove vendor name", "vendor", HIGH);
        d.addEvidence(PRODUCT, "hint analyzer", "remove product name", "product", HIGH);
        d.addEvidence(VERSION, "hint analyzer", "other version name", "value", HIGH);
        d.addEvidence(VENDOR, "hint analyzer", "other vendor name", "vendor", HIGH);
        d.addEvidence(PRODUCT, "hint analyzer", "other product name", "product", HIGH);
        Assert.assertEquals("vendor evidence mismatch", 2, d.getEvidence(VENDOR).size());
        Assert.assertEquals("product evidence mismatch", 2, d.getEvidence(PRODUCT).size());
        Assert.assertEquals("version evidence mismatch", 3, d.getEvidence(VERSION).size());
        instance.analyze(d, null);
        Assert.assertEquals("vendor evidence mismatch", 1, d.getEvidence(VENDOR).size());
        Assert.assertEquals("product evidence mismatch", 1, d.getEvidence(PRODUCT).size());
        Assert.assertEquals("version evidence mismatch", 2, d.getEvidence(VERSION).size());
    }
}

