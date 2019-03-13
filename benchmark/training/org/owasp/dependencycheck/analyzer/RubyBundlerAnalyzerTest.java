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
 * Copyright (c) 2016 Bianca Jiang. All Rights Reserved.
 */
package org.owasp.dependencycheck.analyzer;


import EvidenceType.PRODUCT;
import EvidenceType.VENDOR;
import EvidenceType.VERSION;
import RubyBundlerAnalyzer.DEPENDENCY_ECOSYSTEM;
import java.io.File;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.analyzer.exception.AnalysisException;
import org.owasp.dependencycheck.dependency.Dependency;


/**
 * Unit tests for {@link RubyBundlerAnalyzer}.
 *
 * @author Bianca Jiang
 */
public class RubyBundlerAnalyzerTest extends BaseTest {
    /**
     * The analyzer to test.
     */
    private RubyBundlerAnalyzer analyzer;

    /**
     * Test Analyzer name.
     */
    @Test
    public void testGetName() {
        Assert.assertThat(analyzer.getName(), CoreMatchers.is("Ruby Bundler Analyzer"));
    }

    /**
     * Test Ruby Gemspec file support.
     */
    @Test
    public void testSupportsFiles() {
        Assert.assertThat(analyzer.accept(new File("test.gemspec")), CoreMatchers.is(false));
        Assert.assertThat(analyzer.accept(new File((("specifications" + (File.separator)) + "test.gemspec"))), CoreMatchers.is(true));
        Assert.assertThat(analyzer.accept(new File("gemspec.lock")), CoreMatchers.is(false));
    }

    /**
     * Test Ruby Bundler created gemspec analysis.
     *
     * @throws AnalysisException
     * 		is thrown when an exception occurs.
     */
    @Test
    public void testAnalyzeGemspec() throws AnalysisException {
        final Dependency result = new Dependency(BaseTest.getResourceAsFile(this, "ruby/vulnerable/gems/rails-4.1.15/vendor/bundle/ruby/2.2.0/specifications/dalli-2.7.5.gemspec"));
        analyzer.analyze(result, null);
        final String vendorString = result.getEvidence(VENDOR).toString();
        Assert.assertThat(vendorString, CoreMatchers.containsString("Peter M. Goldstein"));
        Assert.assertThat(vendorString, CoreMatchers.containsString("Mike Perham"));
        Assert.assertThat(vendorString, CoreMatchers.containsString("peter.m.goldstein@gmail.com"));
        Assert.assertThat(vendorString, CoreMatchers.containsString("https://github.com/petergoldstein/dalli"));
        Assert.assertThat(vendorString, CoreMatchers.containsString("MIT"));
        Assert.assertThat(result.getEvidence(PRODUCT).toString(), CoreMatchers.containsString("dalli"));
        Assert.assertThat(result.getEvidence(PRODUCT).toString(), CoreMatchers.containsString("High performance memcached client for Ruby"));
        Assert.assertThat(result.getEvidence(VERSION).toString(), CoreMatchers.containsString("2.7.5"));
        Assert.assertEquals("dalli", result.getName());
        Assert.assertEquals("2.7.5", result.getVersion());
        Assert.assertEquals(DEPENDENCY_ECOSYSTEM, result.getEcosystem());
        Assert.assertEquals("dalli:2.7.5", result.getDisplayFileName());
    }
}

