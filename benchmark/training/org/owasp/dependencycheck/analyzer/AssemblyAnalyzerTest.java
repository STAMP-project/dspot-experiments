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


import EvidenceType.PRODUCT;
import EvidenceType.VENDOR;
import EvidenceType.VERSION;
import Settings.KEYS.ANALYZER_ASSEMBLY_DOTNET_PATH;
import java.io.File;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.analyzer.exception.AnalysisException;
import org.owasp.dependencycheck.dependency.Confidence;
import org.owasp.dependencycheck.dependency.Dependency;
import org.owasp.dependencycheck.exception.InitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for the AssemblyAnalyzer.
 *
 * @author colezlaw
 */
public class AssemblyAnalyzerTest extends BaseTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssemblyAnalyzerTest.class);

    private static final String LOG_KEY = "org.slf4j.simpleLogger.org.owasp.dependencycheck.analyzer.AssemblyAnalyzer";

    private AssemblyAnalyzer analyzer;

    /**
     * Tests to make sure the name is correct.
     */
    @Test
    public void testGetName() {
        Assert.assertEquals("Assembly Analyzer", analyzer.getName());
    }

    @Test
    public void testAnalysis() throws Exception {
        Assume.assumeNotNull(analyzer.buildArgumentList());
        File f = analyzer.getGrokAssemblyPath();
        Dependency d = new Dependency(f);
        analyzer.analyze(d, null);
        Assert.assertTrue(d.contains(VENDOR, new org.owasp.dependencycheck.dependency.Evidence("grokassembly", "CompanyName", "OWASP Contributors", Confidence.HIGHEST)));
        Assert.assertTrue(d.contains(PRODUCT, new org.owasp.dependencycheck.dependency.Evidence("grokassembly", "ProductName", "GrokAssembly", Confidence.HIGHEST)));
    }

    @Test
    public void testLog4Net() throws Exception {
        Assume.assumeNotNull(analyzer.buildArgumentList());
        File f = BaseTest.getResourceAsFile(this, "log4net.dll");
        Dependency d = new Dependency(f);
        analyzer.analyze(d, null);
        Assert.assertTrue(d.contains(VERSION, new org.owasp.dependencycheck.dependency.Evidence("grokassembly", "FileVersion", "1.2.13.0", Confidence.HIGHEST)));
        Assert.assertEquals("1.2.13.0", d.getVersion());
        Assert.assertTrue(d.contains(VENDOR, new org.owasp.dependencycheck.dependency.Evidence("grokassembly", "CompanyName", "The Apache Software Foundation", Confidence.HIGHEST)));
        Assert.assertTrue(d.contains(PRODUCT, new org.owasp.dependencycheck.dependency.Evidence("grokassembly", "ProductName", "log4net", Confidence.HIGHEST)));
        Assert.assertEquals("log4net", d.getName());
    }

    @Test
    public void testNonexistent() {
        Assume.assumeNotNull(analyzer.buildArgumentList());
        // Tweak the log level so the warning doesn't show in the console
        String oldProp = System.getProperty(AssemblyAnalyzerTest.LOG_KEY, "info");
        File f = BaseTest.getResourceAsFile(this, "log4net.dll");
        File test = new File(f.getParent(), "nonexistent.dll");
        Dependency d = new Dependency(test);
        try {
            analyzer.analyze(d, null);
            Assert.fail("Expected an AnalysisException");
        } catch (AnalysisException ae) {
            Assert.assertTrue(ae.getMessage().contains("nonexistent.dll does not exist and cannot be analyzed by dependency-check"));
        } finally {
            System.setProperty(AssemblyAnalyzerTest.LOG_KEY, oldProp);
        }
    }

    @Test
    public void testWithSettingMono() throws Exception {
        // This test doesn't work on Windows.
        Assume.assumeFalse(System.getProperty("os.name").startsWith("Windows"));
        String oldValue = getSettings().getString(ANALYZER_ASSEMBLY_DOTNET_PATH);
        // if oldValue is null, that means that neither the system property nor the setting has
        // been set. If that's the case, then we have to make it such that when we recover,
        // null still comes back. But you can't put a null value in a HashMap, so we have to set
        // the system property rather than the setting.
        System.setProperty(ANALYZER_ASSEMBLY_DOTNET_PATH, "/yooser/bine/mono");
        String oldProp = System.getProperty(AssemblyAnalyzerTest.LOG_KEY, "info");
        try {
            // Tweak the logging to swallow the warning when testing
            System.setProperty(AssemblyAnalyzerTest.LOG_KEY, "error");
            // Have to make a NEW analyzer because during setUp, it would have gotten the correct one
            AssemblyAnalyzer aanalyzer = new AssemblyAnalyzer();
            aanalyzer.initialize(getSettings());
            aanalyzer.accept(new File("test.dll"));// trick into "thinking it is active"

            aanalyzer.prepare(null);
            Assert.fail("Expected an InitializationException");
        } catch (InitializationException ae) {
            Assert.assertEquals("An error occurred with the .NET AssemblyAnalyzer", ae.getMessage());
        } finally {
            System.setProperty(AssemblyAnalyzerTest.LOG_KEY, oldProp);
            // Recover the logger
            // Now recover the way we came in. If we had to set a System property, delete it. Otherwise,
            // reset the old value
            if (oldValue == null) {
                System.getProperties().remove(ANALYZER_ASSEMBLY_DOTNET_PATH);
            } else {
                System.setProperty(ANALYZER_ASSEMBLY_DOTNET_PATH, oldValue);
            }
        }
    }
}

