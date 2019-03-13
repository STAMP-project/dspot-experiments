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
 * Copyright (c) 2012 Jeremy Long. All Rights Reserved.
 */
package org.owasp.dependencycheck.analyzer;


import Settings.KEYS.ANALYZER_EXPERIMENTAL_ENABLED;
import Settings.KEYS.ANALYZER_RETIRED_ENABLED;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseDBTestCase;


/**
 *
 *
 * @author Jeremy Long
 */
public class AnalyzerServiceTest extends BaseDBTestCase {
    /**
     * Test of getAnalyzers method, of class AnalyzerService.
     */
    @Test
    public void testGetAnalyzers() {
        AnalyzerService instance = new AnalyzerService(Thread.currentThread().getContextClassLoader(), getSettings());
        List<Analyzer> result = instance.getAnalyzers();
        boolean found = false;
        for (Analyzer a : result) {
            if ("Jar Analyzer".equals(a.getName())) {
                found = true;
                break;
            }
        }
        Assert.assertTrue("JarAnalyzer loaded", found);
    }

    /**
     * Test of getAnalyzers method, of class AnalyzerService.
     */
    @Test
    public void testGetAnalyzers_SpecificPhases() throws Exception {
        AnalyzerService instance = new AnalyzerService(Thread.currentThread().getContextClassLoader(), getSettings());
        List<Analyzer> result = instance.getAnalyzers(AnalysisPhase.INITIAL, AnalysisPhase.FINAL);
        for (Analyzer a : result) {
            if (((a.getAnalysisPhase()) != (AnalysisPhase.INITIAL)) && ((a.getAnalysisPhase()) != (AnalysisPhase.FINAL))) {
                Assert.fail(((("Only expecting analyzers for phases " + (AnalysisPhase.INITIAL)) + " and ") + (AnalysisPhase.FINAL)));
            }
        }
    }

    /**
     * Test of getAnalyzers method, of class AnalyzerService.
     */
    @Test
    public void testGetExperimentalAnalyzers() {
        AnalyzerService instance = new AnalyzerService(Thread.currentThread().getContextClassLoader(), getSettings());
        List<Analyzer> result = instance.getAnalyzers();
        String experimental = "CMake Analyzer";
        boolean found = false;
        boolean retiredFound = false;
        for (Analyzer a : result) {
            if (experimental.equals(a.getName())) {
                found = true;
            }
        }
        Assert.assertFalse("Experimental analyzer loaded when set to false", found);
        Assert.assertFalse("Retired analyzer loaded when set to false", retiredFound);
        getSettings().setBoolean(ANALYZER_EXPERIMENTAL_ENABLED, true);
        instance = new AnalyzerService(Thread.currentThread().getContextClassLoader(), getSettings());
        result = instance.getAnalyzers();
        found = false;
        retiredFound = false;
        for (Analyzer a : result) {
            if (experimental.equals(a.getName())) {
                found = true;
            }
        }
        Assert.assertTrue("Experimental analyzer not loaded when set to true", found);
        Assert.assertFalse("Retired analyzer loaded when set to false", retiredFound);
        getSettings().setBoolean(ANALYZER_EXPERIMENTAL_ENABLED, false);
        getSettings().setBoolean(ANALYZER_RETIRED_ENABLED, true);
        instance = new AnalyzerService(Thread.currentThread().getContextClassLoader(), getSettings());
        result = instance.getAnalyzers();
        found = false;
        retiredFound = false;
        for (Analyzer a : result) {
            if (experimental.equals(a.getName())) {
                found = true;
            }
        }
        Assert.assertFalse("Experimental analyzer loaded when set to false", found);
        // assertTrue("Retired analyzer not loaded when set to true", retiredFound);
    }
}

