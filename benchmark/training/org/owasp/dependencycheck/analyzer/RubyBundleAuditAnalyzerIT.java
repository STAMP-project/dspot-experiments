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
import EvidenceType.VERSION;
import java.io.File;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.owasp.dependencycheck.BaseDBTestCase;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.Engine;
import org.owasp.dependencycheck.analyzer.exception.AnalysisException;
import org.owasp.dependencycheck.data.nvdcve.DatabaseException;
import org.owasp.dependencycheck.data.update.exception.UpdateException;
import org.owasp.dependencycheck.dependency.Dependency;
import org.owasp.dependencycheck.dependency.Vulnerability;
import org.owasp.dependencycheck.exception.ExceptionCollection;
import org.owasp.dependencycheck.exception.InitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Unit tests for {@link RubyBundleAuditAnalyzer}.
 *
 * @author Dale Visser
 */
public class RubyBundleAuditAnalyzerIT extends BaseDBTestCase {
    private static final Logger LOGGER = LoggerFactory.getLogger(RubyBundleAuditAnalyzerIT.class);

    /**
     * The analyzer to test.
     */
    private RubyBundleAuditAnalyzer analyzer;

    /**
     * Test Ruby Gemspec name.
     */
    @Test
    public void testGetName() {
        Assert.assertThat(analyzer.getName(), CoreMatchers.is("Ruby Bundle Audit Analyzer"));
    }

    /**
     * Test Ruby Bundler Audit file support.
     */
    @Test
    public void testSupportsFiles() {
        Assert.assertThat(analyzer.accept(new File("Gemfile.lock")), CoreMatchers.is(true));
    }

    /**
     * Test Ruby BundlerAudit analysis.
     *
     * @throws AnalysisException
     * 		is thrown when an exception occurs.
     */
    @Test
    public void testAnalysis() throws AnalysisException, DatabaseException {
        try (Engine engine = new Engine(getSettings())) {
            engine.openDatabase();
            analyzer.prepare(engine);
            final String resource = "ruby/vulnerable/gems/rails-4.1.15/Gemfile.lock";
            final Dependency result = new Dependency(BaseTest.getResourceAsFile(this, resource));
            analyzer.analyze(result, engine);
            final Dependency[] dependencies = engine.getDependencies();
            final int size = dependencies.length;
            Assert.assertTrue((size >= 1));
            boolean found = false;
            for (Dependency dependency : dependencies) {
                found = dependency.getEvidence(PRODUCT).toString().toLowerCase().contains("redcarpet");
                found &= dependency.getEvidence(VERSION).toString().toLowerCase().contains("2.2.2");
                found &= dependency.getFilePath().endsWith(resource);
                found &= dependency.getFileName().equals("Gemfile.lock");
                if (found) {
                    break;
                }
            }
            Assert.assertTrue("redcarpet was not identified", found);
        } catch (InitializationException | DatabaseException | AnalysisException e) {
            RubyBundleAuditAnalyzerIT.LOGGER.warn("Exception setting up RubyBundleAuditAnalyzer. Make sure Ruby gem bundle-audit is installed. You may also need to set property \"analyzer.bundle.audit.path\".");
            Assume.assumeNoException("Exception setting up RubyBundleAuditAnalyzer; bundle audit may not be installed, or property \"analyzer.bundle.audit.path\" may not be set.", e);
        }
    }

    /**
     * Test Ruby addCriticalityToVulnerability
     */
    @Test
    public void testAddCriticalityToVulnerability() throws AnalysisException, DatabaseException {
        try (Engine engine = new Engine(getSettings())) {
            engine.doUpdates(true);
            analyzer.prepare(engine);
            final Dependency result = new Dependency(BaseTest.getResourceAsFile(this, "ruby/vulnerable/gems/sinatra/Gemfile.lock"));
            analyzer.analyze(result, engine);
            Dependency dependency = engine.getDependencies()[0];
            Vulnerability vulnerability = dependency.getVulnerabilities(true).iterator().next();
            Assert.assertEquals(5.0F, vulnerability.getCvssV2().getScore(), 0.0);
        } catch (InitializationException | DatabaseException | AnalysisException | UpdateException e) {
            RubyBundleAuditAnalyzerIT.LOGGER.warn("Exception setting up RubyBundleAuditAnalyzer. Make sure Ruby gem bundle-audit is installed. You may also need to set property \"analyzer.bundle.audit.path\".");
            Assume.assumeNoException("Exception setting up RubyBundleAuditAnalyzer; bundle audit may not be installed, or property \"analyzer.bundle.audit.path\" may not be set.", e);
        }
    }

    /**
     * Test when Ruby bundle-audit is not available on the system.
     *
     * @throws AnalysisException
     * 		is thrown when an exception occurs.
     */
    @Test
    public void testMissingBundleAudit() throws AnalysisException, DatabaseException {
        // TODO - this test is invalid as phantom bundle audit may not exist - but if bundle-audit
        // is still on the path then initialization works and the bundle-audit on the path works.
        // set a non-exist bundle-audit
        // getSettings().setString(Settings.KEYS.ANALYZER_BUNDLE_AUDIT_PATH, "phantom-bundle-audit");
        // analyzer.initialize(getSettings());
        // try {
        // //initialize should fail.
        // analyzer.prepare(null);
        // } catch (Exception e) {
        // //expected, so ignore.
        // assertNotNull(e);
        // } finally {
        // assertThat(analyzer.isEnabled(), is(false));
        // LOGGER.info("phantom-bundle-audit is not available. Ruby Bundle Audit Analyzer is disabled as expected.");
        // }
    }

    /**
     * Test Ruby dependencies and their paths.
     *
     * @throws AnalysisException
     * 		is thrown when an exception occurs.
     * @throws DatabaseException
     * 		thrown when an exception occurs
     */
    @Test
    public void testDependenciesPath() throws AnalysisException, DatabaseException {
        try (Engine engine = new Engine(getSettings())) {
            try {
                engine.scan(BaseTest.getResourceAsFile(this, "ruby/vulnerable/gems/rails-4.1.15/"));
                engine.analyzeDependencies();
            } catch (NullPointerException ex) {
                RubyBundleAuditAnalyzerIT.LOGGER.error("NPE", ex);
                Assert.fail(ex.getMessage());
            } catch (ExceptionCollection ex) {
                Assume.assumeNoException("Exception setting up RubyBundleAuditAnalyzer; bundle audit may not be installed, or property \"analyzer.bundle.audit.path\" may not be set.", ex);
                return;
            }
            Dependency[] dependencies = engine.getDependencies();
            RubyBundleAuditAnalyzerIT.LOGGER.info("{} dependencies found.", dependencies.length);
        }
    }
}

