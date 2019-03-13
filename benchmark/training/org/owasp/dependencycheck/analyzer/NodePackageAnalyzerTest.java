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
import NodePackageAnalyzer.DEPENDENCY_ECOSYSTEM;
import Settings.KEYS.ANALYZER_NODE_AUDIT_ENABLED;
import Settings.KEYS.ANALYZER_NODE_PACKAGE_ENABLED;
import java.io.File;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.Engine;
import org.owasp.dependencycheck.analyzer.exception.AnalysisException;
import org.owasp.dependencycheck.dependency.Dependency;
import org.owasp.dependencycheck.utils.InvalidSettingException;


/**
 * Unit tests for NodePackageAnalyzer.
 *
 * @author Dale Visser
 */
public class NodePackageAnalyzerTest extends BaseTest {
    /**
     * The analyzer to test.
     */
    private NodePackageAnalyzer analyzer;

    /**
     * A reference to the engine.
     */
    private Engine engine;

    /**
     * Test of getName method, of class PythonDistributionAnalyzer.
     */
    @Test
    public void testGetName() throws InvalidSettingException {
        Assume.assumeThat(getSettings().getBoolean(ANALYZER_NODE_PACKAGE_ENABLED), CoreMatchers.is(true));
        Assume.assumeThat(getSettings().getBoolean(ANALYZER_NODE_AUDIT_ENABLED), CoreMatchers.is(true));
        Assert.assertThat(analyzer.getName(), CoreMatchers.is("Node.js Package Analyzer"));
    }

    /**
     * Test of supportsExtension method, of class PythonDistributionAnalyzer.
     */
    @Test
    public void testSupportsFiles() throws InvalidSettingException {
        Assume.assumeThat(getSettings().getBoolean(ANALYZER_NODE_PACKAGE_ENABLED), CoreMatchers.is(true));
        Assume.assumeThat(getSettings().getBoolean(ANALYZER_NODE_AUDIT_ENABLED), CoreMatchers.is(true));
        Assert.assertThat(analyzer.accept(new File("package-lock.json")), CoreMatchers.is(true));
        Assert.assertThat(analyzer.accept(new File("npm-shrinkwrap.json")), CoreMatchers.is(true));
    }

    /**
     * Test of inspect method, of class PythonDistributionAnalyzer.
     *
     * @throws AnalysisException
     * 		is thrown when an exception occurs.
     */
    @Test
    public void testAnalyzeShrinkwrapJson() throws AnalysisException, InvalidSettingException {
        Assume.assumeThat(getSettings().getBoolean(ANALYZER_NODE_PACKAGE_ENABLED), CoreMatchers.is(true));
        Assume.assumeThat(getSettings().getBoolean(ANALYZER_NODE_AUDIT_ENABLED), CoreMatchers.is(true));
        final Dependency toScan = new Dependency(BaseTest.getResourceAsFile(this, "nodejs/npm-shrinkwrap.json"));
        final Dependency toCombine = new Dependency(BaseTest.getResourceAsFile(this, "nodejs/node_modules/dns-sync/package.json"));
        engine.addDependency(toScan);
        engine.addDependency(toCombine);
        analyzer.analyze(toScan, engine);
        analyzer.analyze(toCombine, engine);
        Assert.assertEquals("Expected 6 dependency", 6, engine.getDependencies().length);
        Dependency result = null;
        for (Dependency dep : engine.getDependencies()) {
            if ("dns-sync".equals(dep.getName())) {
                result = dep;
                break;
            }
        }
        final String vendorString = result.getEvidence(VENDOR).toString();
        Assert.assertThat(vendorString, CoreMatchers.containsString("Sanjeev Koranga"));
        Assert.assertThat(vendorString, CoreMatchers.containsString("dns-sync"));
        Assert.assertThat(result.getEvidence(PRODUCT).toString(), CoreMatchers.containsString("dns-sync"));
        Assert.assertThat(result.getEvidence(VERSION).toString(), CoreMatchers.containsString("0.1.3"));
        Assert.assertEquals(DEPENDENCY_ECOSYSTEM, result.getEcosystem());
        Assert.assertEquals("dns-sync", result.getName());
        Assert.assertEquals("0.1.3", result.getVersion());
    }

    /**
     * Test of inspect method, of class PythonDistributionAnalyzer.
     *
     * @throws AnalysisException
     * 		is thrown when an exception occurs.
     */
    @Test
    public void testAnalyzePackageJsonWithShrinkwrap() throws AnalysisException, InvalidSettingException {
        Assume.assumeThat(getSettings().getBoolean(ANALYZER_NODE_PACKAGE_ENABLED), CoreMatchers.is(true));
        Assume.assumeThat(getSettings().getBoolean(ANALYZER_NODE_AUDIT_ENABLED), CoreMatchers.is(true));
        final Dependency packageJson = new Dependency(BaseTest.getResourceAsFile(this, "nodejs/package.json"));
        final Dependency shrinkwrap = new Dependency(BaseTest.getResourceAsFile(this, "nodejs/npm-shrinkwrap.json"));
        engine.addDependency(packageJson);
        engine.addDependency(shrinkwrap);
        Assert.assertEquals(2, engine.getDependencies().length);
        analyzer.analyze(packageJson, engine);
        Assert.assertEquals(1, engine.getDependencies().length);// package-lock was removed without analysis

        Assert.assertTrue(shrinkwrap.equals(engine.getDependencies()[0]));
        analyzer.analyze(shrinkwrap, engine);
        Assert.assertEquals(6, engine.getDependencies().length);// shrinkwrap was removed with analysis adding 6 dependency

        Assert.assertFalse(shrinkwrap.equals(engine.getDependencies()[0]));
    }
}

