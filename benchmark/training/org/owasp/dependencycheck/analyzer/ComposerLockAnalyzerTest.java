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
 * Copyright (c) 2015 The OWASP Foundatio. All Rights Reserved.
 */
package org.owasp.dependencycheck.analyzer;


import ComposerLockAnalyzer.DEPENDENCY_ECOSYSTEM;
import java.io.File;
import org.apache.commons.lang3.ArrayUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseDBTestCase;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.Engine;
import org.owasp.dependencycheck.dependency.Dependency;


/**
 * Unit tests for NodePackageAnalyzer.
 *
 * @author Dale Visser
 */
public class ComposerLockAnalyzerTest extends BaseDBTestCase {
    /**
     * The analyzer to test.
     */
    private ComposerLockAnalyzer analyzer;

    /**
     * Test of getName method, of class ComposerLockAnalyzer.
     */
    @Test
    public void testGetName() {
        Assert.assertEquals("Composer.lock analyzer", analyzer.getName());
    }

    /**
     * Test of supportsExtension method, of class ComposerLockAnalyzer.
     */
    @Test
    public void testSupportsFiles() {
        Assert.assertTrue(analyzer.accept(new File("composer.lock")));
    }

    /**
     * Test of inspect method, of class PythonDistributionAnalyzer.
     *
     * @throws AnalysisException
     * 		is thrown when an exception occurs.
     */
    @Test
    public void testAnalyzePackageJson() throws Exception {
        try (Engine engine = new Engine(getSettings())) {
            final Dependency result = new Dependency(BaseTest.getResourceAsFile(this, "composer.lock"));
            // simulate normal operation when the composer.lock is already added to the engine as a dependency
            engine.addDependency(result);
            analyzer.analyze(result, engine);
            // make sure the redundant composer.lock is removed
            Assert.assertFalse(ArrayUtils.contains(engine.getDependencies(), result));
            Assert.assertEquals(30, engine.getDependencies().length);
            boolean found = false;
            for (Dependency d : engine.getDependencies()) {
                if ("classpreloader".equals(d.getName())) {
                    found = true;
                    Assert.assertEquals("2.0.0", d.getVersion());
                    Assert.assertThat(d.getDisplayFileName(), CoreMatchers.equalTo("classpreloader:2.0.0"));
                    Assert.assertEquals(DEPENDENCY_ECOSYSTEM, d.getEcosystem());
                }
            }
            Assert.assertTrue("Expeced to find classpreloader", found);
        }
    }
}

