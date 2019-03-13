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
import RubyGemspecAnalyzer.DEPENDENCY_ECOSYSTEM;
import java.io.File;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.analyzer.exception.AnalysisException;
import org.owasp.dependencycheck.dependency.Dependency;


/**
 * Unit tests for {@link RubyGemspecAnalyzer}.
 *
 * @author Dale Visser
 */
// /**
// * Test Rakefile analysis.
// *
// * @throws AnalysisException is thrown when an exception occurs.
// */
// @Test
// @Ignore
// //TODO: place holder to test Rakefile support
// public void testAnalyzeRakefile() throws AnalysisException {
// final Dependency result = new Dependency(BaseTest.getResourceAsFile(this,
// "ruby/vulnerable/gems/rails-4.1.15/vendor/bundle/ruby/2.2.0/gems/pg-0.18.4/Rakefile"));
// analyzer.analyze(result, null);
// assertTrue(result.size() > 0);
// assertEquals(RubyGemspecAnalyzer.DEPENDENCY_ECOSYSTEM, result.getEcosystem());
// assertEquals("pg", result.getName());
// assertEquals("0.18.4", result.getVersion());
// assertEquals("pg:0.18.4", result.getDisplayFileName());
// }
public class RubyGemspecAnalyzerTest extends BaseTest {
    /**
     * The analyzer to test.
     */
    private RubyGemspecAnalyzer analyzer;

    /**
     * Test Ruby Gemspec name.
     */
    @Test
    public void testGetName() {
        Assert.assertThat(analyzer.getName(), CoreMatchers.is("Ruby Gemspec Analyzer"));
    }

    /**
     * Test Ruby Gemspec file support.
     */
    @Test
    public void testSupportsFiles() {
        Assert.assertThat(analyzer.accept(new File("test.gemspec")), CoreMatchers.is(true));
        Assert.assertThat(analyzer.accept(new File("gemspec.lock")), CoreMatchers.is(false));
        // assertThat(analyzer.accept(new File("Rakefile")), is(true));
    }

    /**
     * Test Ruby Gemspec analysis.
     *
     * @throws AnalysisException
     * 		is thrown when an exception occurs.
     */
    @Test
    public void testAnalyzePackageJson() throws AnalysisException {
        final Dependency result = new Dependency(BaseTest.getResourceAsFile(this, "ruby/vulnerable/gems/specifications/rest-client-1.7.2.gemspec"));
        analyzer.analyze(result, null);
        final String vendorString = result.getEvidence(VENDOR).toString();
        Assert.assertEquals(DEPENDENCY_ECOSYSTEM, result.getEcosystem());
        Assert.assertThat(vendorString, CoreMatchers.containsString("REST Client Team"));
        Assert.assertThat(vendorString, CoreMatchers.containsString("rest-client_project"));
        Assert.assertThat(vendorString, CoreMatchers.containsString("rest.client@librelist.com"));
        Assert.assertThat(vendorString, CoreMatchers.containsString("https://github.com/rest-client/rest-client"));
        Assert.assertThat(result.getEvidence(PRODUCT).toString(), CoreMatchers.containsString("rest-client"));
        Assert.assertThat(result.getEvidence(VERSION).toString(), CoreMatchers.containsString("1.7.2"));
        Assert.assertEquals("rest-client", result.getName());
        Assert.assertEquals("1.7.2", result.getVersion());
        Assert.assertEquals("rest-client:1.7.2", result.getDisplayFileName());
    }
}

