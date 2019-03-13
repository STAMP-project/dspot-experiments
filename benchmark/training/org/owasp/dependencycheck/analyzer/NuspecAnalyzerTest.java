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
 * Copyright (c) 2013 Jeremy Long. All Rights Reserved.
 */
package org.owasp.dependencycheck.analyzer;


import AnalysisPhase.INFORMATION_COLLECTION;
import EvidenceType.PRODUCT;
import EvidenceType.VENDOR;
import EvidenceType.VERSION;
import NuspecAnalyzer.DEPENDENCY_ECOSYSTEM;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.dependency.Dependency;


public class NuspecAnalyzerTest extends BaseTest {
    private NuspecAnalyzer instance;

    @Test
    public void testGetAnalyzerName() {
        Assert.assertEquals("Nuspec Analyzer", instance.getName());
    }

    @Test
    public void testSupportsFileExtensions() {
        Assert.assertTrue(instance.accept(new File("test.nuspec")));
        Assert.assertFalse(instance.accept(new File("test.nupkg")));
    }

    @Test
    public void testGetAnalysisPhaze() {
        Assert.assertEquals(INFORMATION_COLLECTION, instance.getAnalysisPhase());
    }

    @Test
    public void testNuspecAnalysis() throws Exception {
        File file = BaseTest.getResourceAsFile(this, "nuspec/test.nuspec");
        Dependency result = new Dependency(file);
        instance.analyze(result, null);
        Assert.assertEquals(DEPENDENCY_ECOSYSTEM, result.getEcosystem());
        // checking the owner field
        Assert.assertTrue(result.getEvidence(VENDOR).toString().toLowerCase().contains("bobsmack"));
        // checking the author field
        Assert.assertTrue(result.getEvidence(VENDOR).toString().toLowerCase().contains("brianfox"));
        // checking the id field
        Assert.assertTrue(result.getEvidence(PRODUCT).toString().contains("TestDepCheck"));
        // checking the title field
        Assert.assertTrue(result.getEvidence(PRODUCT).toString().contains("Test Package"));
        Assert.assertTrue(result.getEvidence(VERSION).toString().contains("1.0.0"));
        Assert.assertEquals("1.0.0", result.getVersion());
        Assert.assertEquals("TestDepCheck", result.getName());
        Assert.assertEquals("TestDepCheck:1.0.0", result.getDisplayFileName());
    }
}

