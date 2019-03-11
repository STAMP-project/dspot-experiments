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
 * Copyright (c) 2018 Paul Irwin. All Rights Reserved.
 */
package org.owasp.dependencycheck.analyzer;


import AnalysisPhase.INFORMATION_COLLECTION;
import EvidenceType.PRODUCT;
import EvidenceType.VENDOR;
import EvidenceType.VERSION;
import java.io.File;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.Engine;
import org.owasp.dependencycheck.dependency.Dependency;


public class MSBuildProjectAnalyzerTest extends BaseTest {
    private MSBuildProjectAnalyzer instance;

    @Test
    public void testGetAnalyzerName() {
        Assert.assertEquals("MSBuild Project Analyzer", instance.getName());
    }

    @Test
    public void testSupportsFileExtensions() {
        TestCase.assertTrue(instance.accept(new File("test.csproj")));
        TestCase.assertTrue(instance.accept(new File("test.vbproj")));
        Assert.assertFalse(instance.accept(new File("test.nuspec")));
    }

    @Test
    public void testGetAnalysisPhaze() {
        Assert.assertEquals(INFORMATION_COLLECTION, instance.getAnalysisPhase());
    }

    @Test
    public void testMSBuildProjectAnalysis() throws Exception {
        try (Engine engine = new Engine(getSettings())) {
            File file = BaseTest.getResourceAsFile(this, "msbuild/test.csproj");
            Dependency toScan = new Dependency(file);
            MSBuildProjectAnalyzer analyzer = new MSBuildProjectAnalyzer();
            analyzer.setFilesMatched(true);
            analyzer.initialize(getSettings());
            analyzer.prepare(engine);
            analyzer.setEnabled(true);
            analyzer.analyze(toScan, engine);
            Assert.assertEquals("3 dependencies should be found", 3, engine.getDependencies().length);
            int foundCount = 0;
            for (Dependency result : engine.getDependencies()) {
                Assert.assertEquals(NuspecAnalyzer.DEPENDENCY_ECOSYSTEM, result.getEcosystem());
                TestCase.assertTrue(result.isVirtual());
                if ("Humanizer".equals(result.getName())) {
                    foundCount++;
                    TestCase.assertTrue(result.getEvidence(VENDOR).toString().contains("Humanizer"));
                    TestCase.assertTrue(result.getEvidence(PRODUCT).toString().contains("Humanizer"));
                    TestCase.assertTrue(result.getEvidence(VERSION).toString().contains("2.2.0"));
                } else
                    if ("JetBrains.Annotations".equals(result.getName())) {
                        foundCount++;
                        TestCase.assertTrue(result.getEvidence(VENDOR).toString().contains("JetBrains"));
                        TestCase.assertTrue(result.getEvidence(PRODUCT).toString().contains("JetBrains.Annotations"));
                        TestCase.assertTrue(result.getEvidence(PRODUCT).toString().contains("Annotations"));
                        TestCase.assertTrue(result.getEvidence(VERSION).toString().contains("11.1.0"));
                    } else
                        if ("Microsoft.AspNetCore.All".equals(result.getName())) {
                            foundCount++;
                            TestCase.assertTrue(result.getEvidence(VENDOR).toString().contains("Microsoft"));
                            TestCase.assertTrue(result.getEvidence(PRODUCT).toString().contains("Microsoft.AspNetCore.All"));
                            TestCase.assertTrue(result.getEvidence(PRODUCT).toString().contains("AspNetCore"));
                            TestCase.assertTrue(result.getEvidence(PRODUCT).toString().contains("AspNetCore.All"));
                            TestCase.assertTrue(result.getEvidence(VERSION).toString().contains("2.0.5"));
                        }


            }
            Assert.assertEquals("3 expected dependencies should be found", 3, foundCount);
        }
    }
}

