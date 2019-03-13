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


import EvidenceType.PRODUCT;
import EvidenceType.VENDOR;
import EvidenceType.VERSION;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.analyzer.exception.AnalysisException;
import org.owasp.dependencycheck.dependency.Confidence;
import org.owasp.dependencycheck.dependency.Dependency;


/**
 * Unit tests for AutoconfAnalyzer. The test resources under autoconf/ were
 * obtained from outside open source software projects. Links to those projects
 * are given below.
 *
 * @author Dale Visser
 * @see <a href="http://readable.sourceforge.net/">Readable Lisp S-expressions
Project</a>
 * @see <a href="https://gnu.org/software/binutils/">GNU Binutils</a>
 * @see <a href="https://gnu.org/software/ghostscript/">GNU Ghostscript</a>
 */
public class AutoconfAnalyzerTest extends BaseTest {
    /**
     * The analyzer to test.
     */
    private AutoconfAnalyzer analyzer;

    /**
     * Test whether expected evidence is gathered from Ghostscript's configure.
     *
     * @throws AnalysisException
     * 		is thrown when an exception occurs.
     */
    @Test
    public void testAnalyzeConfigureAC1() throws AnalysisException {
        final Dependency result = new Dependency(BaseTest.getResourceAsFile(this, "autoconf/ghostscript/configure.ac"));
        analyzer.analyze(result, null);
        // TODO fix these
        Assert.assertTrue(result.contains(VENDOR, new org.owasp.dependencycheck.dependency.Evidence("configure.ac", "Bug report address", "gnu-ghostscript-bug@gnu.org", Confidence.HIGH)));
        Assert.assertTrue(result.contains(PRODUCT, new org.owasp.dependencycheck.dependency.Evidence("configure.ac", "Package", "gnu-ghostscript", Confidence.HIGHEST)));
        Assert.assertTrue(result.contains(VERSION, new org.owasp.dependencycheck.dependency.Evidence("configure.ac", "Package Version", "8.62.0", Confidence.HIGHEST)));
    }

    /**
     * Test whether expected evidence is gathered from Readable's configure.ac.
     *
     * @throws AnalysisException
     * 		is thrown when an exception occurs.
     */
    @Test
    public void testAnalyzeConfigureAC2() throws AnalysisException {
        final Dependency result = new Dependency(BaseTest.getResourceAsFile(this, "autoconf/readable-code/configure.ac"));
        analyzer.analyze(result, null);
        Assert.assertTrue(result.contains(VENDOR, new org.owasp.dependencycheck.dependency.Evidence("configure.ac", "Bug report address", "dwheeler@dwheeler.com", Confidence.HIGH)));
        Assert.assertTrue(result.contains(PRODUCT, new org.owasp.dependencycheck.dependency.Evidence("configure.ac", "Package", "readable", Confidence.HIGHEST)));
        Assert.assertTrue(result.contains(VERSION, new org.owasp.dependencycheck.dependency.Evidence("configure.ac", "Package Version", "1.0.7", Confidence.HIGHEST)));
        Assert.assertTrue(result.contains(VENDOR, new org.owasp.dependencycheck.dependency.Evidence("configure.ac", "URL", "http://readable.sourceforge.net/", Confidence.HIGH)));
    }

    /**
     * Test whether expected evidence is gathered from GNU Binutil's configure.
     *
     * @throws AnalysisException
     * 		is thrown when an exception occurs.
     */
    @Test
    public void testAnalyzeConfigureScript() throws AnalysisException {
        final Dependency result = new Dependency(BaseTest.getResourceAsFile(this, "autoconf/binutils/configure"));
        analyzer.analyze(result, null);
        Assert.assertTrue(result.contains(PRODUCT, new org.owasp.dependencycheck.dependency.Evidence("configure", "NAME", "binutils", Confidence.HIGHEST)));
        Assert.assertTrue(result.contains(VERSION, new org.owasp.dependencycheck.dependency.Evidence("configure", "VERSION", "2.25.51", Confidence.HIGHEST)));
    }

    /**
     * Test whether expected evidence is gathered from GNU Ghostscript's
     * configure.
     *
     * @throws AnalysisException
     * 		is thrown when an exception occurs.
     */
    @Test
    public void testAnalyzeReadableConfigureScript() throws AnalysisException {
        final Dependency result = new Dependency(BaseTest.getResourceAsFile(this, "autoconf/readable-code/configure"));
        analyzer.analyze(result, null);
        Assert.assertTrue(result.contains(VENDOR, new org.owasp.dependencycheck.dependency.Evidence("configure", "BUGREPORT", "dwheeler@dwheeler.com", Confidence.HIGH)));
        Assert.assertTrue(result.contains(PRODUCT, new org.owasp.dependencycheck.dependency.Evidence("configure", "NAME", "readable", Confidence.HIGHEST)));
        Assert.assertTrue(result.contains(VERSION, new org.owasp.dependencycheck.dependency.Evidence("configure", "VERSION", "1.0.7", Confidence.HIGHEST)));
        Assert.assertTrue(result.contains(VENDOR, new org.owasp.dependencycheck.dependency.Evidence("configure", "URL", "http://readable.sourceforge.net/", Confidence.HIGH)));
    }

    /**
     * Test of getName method, of {@link AutoconfAnalyzer}.
     */
    @Test
    public void testGetName() {
        Assert.assertEquals("Analyzer name wrong.", "Autoconf Analyzer", analyzer.getName());
    }

    /**
     * Test of {@link AutoconfAnalyzer#accept(File)}.
     */
    @Test
    public void testSupportsFileExtension() {
        Assert.assertTrue("Should support \"ac\" extension.", analyzer.accept(new File("configure.ac")));
        Assert.assertTrue("Should support \"in\" extension.", analyzer.accept(new File("configure.in")));
        Assert.assertTrue("Should support \"configure\" extension.", analyzer.accept(new File("configure")));
    }
}

