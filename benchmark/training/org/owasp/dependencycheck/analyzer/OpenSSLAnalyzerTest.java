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
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.analyzer.exception.AnalysisException;
import org.owasp.dependencycheck.dependency.Dependency;


/**
 * Unit tests for OpenSSLAnalyzerAnalyzer.
 *
 * @author Dale Visser
 */
public class OpenSSLAnalyzerTest extends BaseTest {
    /**
     * The package analyzer to test.
     */
    private OpenSSLAnalyzer analyzer;

    /**
     * Test of getName method, of class OpenSSLAnalyzer.
     */
    @Test
    public void testGetName() {
        Assert.assertEquals("Analyzer name wrong.", "OpenSSL Source Analyzer", analyzer.getName());
    }

    /**
     * Test of supportsExtension method, of class PythonPackageAnalyzer.
     */
    @Test
    public void testAccept() {
        Assert.assertTrue("Should support files named \"opensslv.h\".", analyzer.accept(new File("opensslv.h")));
    }

    @Test
    public void testVersionConstantExamples() {
        final long[] constants = new long[]{ 268443711L, 9449472, 9449473, 9449474L, 9449487, 9449503, 9453583, 270545327 };
        final String[] versions = new String[]{ "1.0.2c", "0.9.3-dev", "0.9.3-beta1", "0.9.3-beta2", "0.9.3", "0.9.3a", "0.9.4", "1.2.3z" };
        Assert.assertEquals(constants.length, versions.length);
        for (int i = 0; i < (constants.length); i++) {
            Assert.assertEquals(versions[i], OpenSSLAnalyzer.getOpenSSLVersion(constants[i]));
        }
    }

    @Test
    public void testOpenSSLVersionHeaderFile() throws AnalysisException {
        final Dependency result = new Dependency(BaseTest.getResourceAsFile(this, "openssl/opensslv.h"));
        analyzer.analyze(result, null);
        Assert.assertThat(result.getEvidence(PRODUCT).toString(), CoreMatchers.containsString("OpenSSL"));
        Assert.assertThat(result.getEvidence(VENDOR).toString(), CoreMatchers.containsString("OpenSSL"));
        Assert.assertThat(result.getEvidence(VERSION).toString(), CoreMatchers.containsString("1.0.2c"));
    }
}

