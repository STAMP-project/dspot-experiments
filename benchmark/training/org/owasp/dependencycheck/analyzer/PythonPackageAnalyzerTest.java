/**
 * This file is part of dependency-check-core.
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
 * Copyright (c) 2015 Institute for Defense Analyses. All Rights Reserved.
 */
package org.owasp.dependencycheck.analyzer;


import EvidenceType.VENDOR;
import EvidenceType.VERSION;
import PythonPackageAnalyzer.DEPENDENCY_ECOSYSTEM;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.analyzer.exception.AnalysisException;
import org.owasp.dependencycheck.dependency.Dependency;
import org.owasp.dependencycheck.dependency.Evidence;


/**
 * Unit tests for PythonPackageAnalyzer.
 *
 * @author Dale Visser
 */
public class PythonPackageAnalyzerTest extends BaseTest {
    /**
     * The package analyzer to test.
     */
    private PythonPackageAnalyzer analyzer;

    /**
     * Test of getName method, of class PythonPackageAnalyzer.
     */
    @Test
    public void testGetName() {
        Assert.assertEquals("Analyzer name wrong.", "Python Package Analyzer", analyzer.getName());
    }

    /**
     * Test of supportsExtension method, of class PythonPackageAnalyzer.
     */
    @Test
    public void testSupportsFileExtension() {
        Assert.assertTrue("Should support \"py\" extension.", analyzer.accept(new File("test.py")));
    }

    @Test
    public void testAnalyzeSourceMetadata() throws AnalysisException {
        boolean found = false;
        final Dependency result = new Dependency(BaseTest.getResourceAsFile(this, "python/eggtest/__init__.py"));
        analyzer.analyze(result, null);
        Assert.assertTrue("Expected vendor evidence to contain \"example\".", result.getEvidence(VENDOR).toString().contains("example"));
        for (final Evidence e : result.getEvidence(VERSION)) {
            if ("0.0.1".equals(e.getValue())) {
                found = true;
                break;
            }
        }
        Assert.assertTrue("Version 0.0.1 not found in EggTest dependency.", found);
        Assert.assertEquals("0.0.1", result.getVersion());
        Assert.assertEquals("eggtest", result.getName());
        Assert.assertEquals("eggtest:0.0.1", result.getDisplayFileName());
        Assert.assertEquals(DEPENDENCY_ECOSYSTEM, result.getEcosystem());
    }
}

