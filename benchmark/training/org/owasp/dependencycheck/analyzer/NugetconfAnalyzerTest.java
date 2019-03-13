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


import EvidenceType.PRODUCT;
import EvidenceType.VERSION;
import java.io.File;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.Engine;
import org.owasp.dependencycheck.dependency.Dependency;


public class NugetconfAnalyzerTest extends BaseTest {
    private NugetconfAnalyzer instance;

    @Test
    public void testGetAnalyzerName() {
        Assert.assertEquals("Nugetconf Analyzer", instance.getName());
    }

    @Test
    public void testSupportedFileNames() {
        TestCase.assertTrue(instance.accept(new File("packages.config")));
        Assert.assertFalse(instance.accept(new File("packages.json")));
    }

    @Test
    public void testNugetconfAnalysis() throws Exception {
        try (Engine engine = new Engine(getSettings())) {
            File file = BaseTest.getResourceAsFile(this, "nugetconf/packages.config");
            Dependency toScan = new Dependency(file);
            NugetconfAnalyzer analyzer = new NugetconfAnalyzer();
            analyzer.setFilesMatched(true);
            analyzer.initialize(getSettings());
            analyzer.prepare(engine);
            analyzer.setEnabled(true);
            analyzer.analyze(toScan, engine);
            int foundCount = 0;
            for (Dependency result : engine.getDependencies()) {
                Assert.assertEquals(NuspecAnalyzer.DEPENDENCY_ECOSYSTEM, result.getEcosystem());
                TestCase.assertTrue(result.isVirtual());
                switch (result.getName()) {
                    case "Autofac" :
                        foundCount++;
                        TestCase.assertTrue(result.getEvidence(PRODUCT).toString().contains("Autofac"));
                        TestCase.assertTrue(result.getEvidence(VERSION).toString().contains("4.6.2"));
                        break;
                    case "Microsoft.AspNet.WebApi.Core" :
                        foundCount++;
                        TestCase.assertTrue(result.getEvidence(PRODUCT).toString().contains("Microsoft.AspNet.WebApi.Core"));
                        TestCase.assertTrue(result.getEvidence(VERSION).toString().contains("5.2.4"));
                        break;
                    case "Microsoft.Owin" :
                        foundCount++;
                        TestCase.assertTrue(result.getEvidence(PRODUCT).toString().contains("Microsoft.Owin"));
                        TestCase.assertTrue(result.getEvidence(VERSION).toString().contains("3.1.0"));
                        break;
                    case "Newtonsoft.Json" :
                        foundCount++;
                        TestCase.assertTrue(result.getEvidence(PRODUCT).toString().contains("Newtonsoft.Json"));
                        TestCase.assertTrue(result.getEvidence(VERSION).toString().contains("10.0.3"));
                        break;
                    default :
                        break;
                }
            }
            Assert.assertEquals("4 dependencies should be found", 4, foundCount);
        }
    }
}

