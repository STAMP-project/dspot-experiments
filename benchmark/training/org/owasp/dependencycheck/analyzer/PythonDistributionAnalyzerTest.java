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


import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.analyzer.exception.AnalysisException;
import org.owasp.dependencycheck.dependency.Dependency;


/**
 * Unit tests for PythonDistributionAnalyzer.
 *
 * @author Dale Visser
 */
public class PythonDistributionAnalyzerTest extends BaseTest {
    /**
     * The analyzer to test.
     */
    private PythonDistributionAnalyzer analyzer;

    /**
     * Test of getName method, of class PythonDistributionAnalyzer.
     */
    @Test
    public void testGetName() {
        Assert.assertEquals("Analyzer name wrong.", "Python Distribution Analyzer", analyzer.getName());
    }

    /**
     * Test of supportsExtension method, of class PythonDistributionAnalyzer.
     */
    @Test
    public void testSupportsFiles() {
        Assert.assertTrue("Should support \"whl\" extension.", analyzer.accept(new File("test.whl")));
        Assert.assertTrue("Should support \"egg\" extension.", analyzer.accept(new File("test.egg")));
        Assert.assertTrue("Should support \"zip\" extension.", analyzer.accept(new File("test.zip")));
        Assert.assertTrue("Should support \"METADATA\" extension.", analyzer.accept(new File("METADATA")));
        Assert.assertTrue("Should support \"PKG-INFO\" extension.", analyzer.accept(new File("PKG-INFO")));
    }

    /**
     * Test of inspect method, of class PythonDistributionAnalyzer.
     */
    @Test
    public void testAnalyzeWheel() {
        try {
            djangoAssertions(new Dependency(BaseTest.getResourceAsFile(this, "python/Django-1.7.2-py2.py3-none-any.whl")));
        } catch (AnalysisException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    /**
     * Test of inspect method, of class PythonDistributionAnalyzer.
     *
     * @throws AnalysisException
     * 		is thrown when an exception occurs.
     */
    @Test
    public void testAnalyzeSitePackage() throws AnalysisException {
        final Dependency result = new Dependency(BaseTest.getResourceAsFile(this, "python/site-packages/Django-1.7.2.dist-info/METADATA"));
        djangoAssertions(result);
    }

    @Test
    public void testAnalyzeEggInfoFolder() {
        try {
            eggtestAssertions(this, "python/site-packages/EggTest.egg-info/PKG-INFO");
        } catch (AnalysisException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testAnalyzeEggArchive() {
        try {
            eggtestAssertions(this, "python/dist/EggTest-0.0.1-py2.7.egg");
        } catch (AnalysisException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testAnalyzeEggArchiveNamedZip() {
        try {
            eggtestAssertions(this, "python/dist/EggTest-0.0.1-py2.7.zip");
        } catch (AnalysisException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testAnalyzeEggFolder() {
        try {
            eggtestAssertions(this, "python/site-packages/EggTest-0.0.1-py2.7.egg/EGG-INFO/PKG-INFO");
        } catch (AnalysisException ex) {
            Assert.fail(ex.getMessage());
        }
    }
}

