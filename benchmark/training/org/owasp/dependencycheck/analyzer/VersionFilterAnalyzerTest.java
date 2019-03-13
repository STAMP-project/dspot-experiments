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
 * Copyright (c) 2017 Jeremy Long. All Rights Reserved.
 */
package org.owasp.dependencycheck.analyzer;


import Confidence.HIGHEST;
import EvidenceType.VERSION;
import Settings.KEYS;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.dependency.Dependency;

import static AnalysisPhase.POST_INFORMATION_COLLECTION;


/**
 *
 *
 * @author Jeremy Long
 */
public class VersionFilterAnalyzerTest extends BaseTest {
    /**
     * Test of getName method, of class VersionFilterAnalyzer.
     */
    @Test
    public void testGetName() {
        VersionFilterAnalyzer instance = new VersionFilterAnalyzer();
        String expResult = "Version Filter Analyzer";
        String result = instance.getName();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getAnalysisPhase method, of class VersionFilterAnalyzer.
     */
    @Test
    public void testGetAnalysisPhase() {
        VersionFilterAnalyzer instance = new VersionFilterAnalyzer();
        instance.initialize(getSettings());
        AnalysisPhase expResult = POST_INFORMATION_COLLECTION;
        AnalysisPhase result = instance.getAnalysisPhase();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getAnalyzerEnabledSettingKey method, of class
     * VersionFilterAnalyzer.
     */
    @Test
    public void testGetAnalyzerEnabledSettingKey() {
        VersionFilterAnalyzer instance = new VersionFilterAnalyzer();
        instance.initialize(getSettings());
        String expResult = KEYS.ANALYZER_VERSION_FILTER_ENABLED;
        String result = instance.getAnalyzerEnabledSettingKey();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of analyzeDependency method, of class VersionFilterAnalyzer.
     */
    @Test
    public void testAnalyzeDependency() throws Exception {
        Dependency dependency = new Dependency();
        dependency.addEvidence(VERSION, "util", "version", "33.3", HIGHEST);
        dependency.addEvidence(VERSION, "other", "version", "alpha", HIGHEST);
        dependency.addEvidence(VERSION, "other", "Implementation-Version", "1.2.3", HIGHEST);
        VersionFilterAnalyzer instance = new VersionFilterAnalyzer();
        instance.initialize(getSettings());
        instance.analyzeDependency(dependency, null);
        Assert.assertEquals(3, dependency.getEvidence(VERSION).size());
        dependency.addEvidence(VERSION, "pom", "version", "1.2.3", HIGHEST);
        instance.analyzeDependency(dependency, null);
        Assert.assertEquals(4, dependency.getEvidence(VERSION).size());
        dependency.addEvidence(VERSION, "file", "version", "1.2.3", HIGHEST);
        instance.analyzeDependency(dependency, null);
        Assert.assertEquals(2, dependency.getEvidence(VERSION).size());
        dependency.addEvidence(VERSION, "Manifest", "Implementation-Version", "1.2.3", HIGHEST);
        instance.analyzeDependency(dependency, null);
        Assert.assertEquals(3, dependency.getEvidence(VERSION).size());
        dependency.addEvidence(VERSION, "nexus", "version", "1.2.3", HIGHEST);
        dependency.addEvidence(VERSION, "other", "version", "alpha", HIGHEST);
        instance.analyzeDependency(dependency, null);
        Assert.assertEquals(4, dependency.getEvidence(VERSION).size());
        dependency.addEvidence(VERSION, "central", "version", "1.2.3", HIGHEST);
        dependency.addEvidence(VERSION, "other", "version", "alpha", HIGHEST);
        instance.analyzeDependency(dependency, null);
        Assert.assertEquals(5, dependency.getEvidence(VERSION).size());
    }

    /**
     * Test of analyzeDependency method, of class VersionFilterAnalyzer.
     */
    @Test
    public void testAnalyzeDependencyFilePom() throws Exception {
        Dependency dependency = new Dependency();
        dependency.addEvidence(VERSION, "util", "version", "33.3", HIGHEST);
        dependency.addEvidence(VERSION, "other", "version", "alpha", HIGHEST);
        dependency.addEvidence(VERSION, "other", "Implementation-Version", "1.2.3", HIGHEST);
        VersionFilterAnalyzer instance = new VersionFilterAnalyzer();
        instance.initialize(getSettings());
        instance.analyzeDependency(dependency, null);
        Assert.assertEquals(3, dependency.getEvidence(VERSION).size());
        dependency.addEvidence(VERSION, "pom", "version", "1.2.3", HIGHEST);
        instance.analyzeDependency(dependency, null);
        Assert.assertEquals(4, dependency.getEvidence(VERSION).size());
        dependency.addEvidence(VERSION, "file", "version", "1.2.3", HIGHEST);
        instance.analyzeDependency(dependency, null);
        Assert.assertEquals(2, dependency.getEvidence(VERSION).size());
        dependency.addEvidence(VERSION, "nexus", "version", "1.2.3", HIGHEST);
        dependency.addEvidence(VERSION, "other", "version", "alpha", HIGHEST);
        instance.analyzeDependency(dependency, null);
        Assert.assertEquals(3, dependency.getEvidence(VERSION).size());
        dependency.addEvidence(VERSION, "central", "version", "1.2.3", HIGHEST);
        dependency.addEvidence(VERSION, "other", "version", "alpha", HIGHEST);
        instance.analyzeDependency(dependency, null);
        Assert.assertEquals(4, dependency.getEvidence(VERSION).size());
    }

    /**
     * Test of analyzeDependency method, of class VersionFilterAnalyzer.
     */
    @Test
    public void testAnalyzeDependencyFileManifest() throws Exception {
        Dependency dependency = new Dependency();
        dependency.addEvidence(VERSION, "util", "version", "33.3", HIGHEST);
        dependency.addEvidence(VERSION, "other", "version", "alpha", HIGHEST);
        dependency.addEvidence(VERSION, "other", "Implementation-Version", "1.2.3", HIGHEST);
        VersionFilterAnalyzer instance = new VersionFilterAnalyzer();
        instance.initialize(getSettings());
        instance.analyzeDependency(dependency, null);
        Assert.assertEquals(3, dependency.getEvidence(VERSION).size());
        dependency.addEvidence(VERSION, "Manifest", "Implementation-Version", "1.2.3", HIGHEST);
        instance.analyzeDependency(dependency, null);
        Assert.assertEquals(4, dependency.getEvidence(VERSION).size());
        dependency.addEvidence(VERSION, "file", "version", "1.2.3", HIGHEST);
        instance.analyzeDependency(dependency, null);
        Assert.assertEquals(2, dependency.getEvidence(VERSION).size());
    }

    /**
     * Test of analyzeDependency method, of class VersionFilterAnalyzer.
     */
    @Test
    public void testAnalyzeDependencyPomManifest() throws Exception {
        Dependency dependency = new Dependency();
        dependency.addEvidence(VERSION, "util", "version", "33.3", HIGHEST);
        dependency.addEvidence(VERSION, "other", "version", "alpha", HIGHEST);
        dependency.addEvidence(VERSION, "other", "Implementation-Version", "1.2.3", HIGHEST);
        VersionFilterAnalyzer instance = new VersionFilterAnalyzer();
        instance.initialize(getSettings());
        instance.analyzeDependency(dependency, null);
        Assert.assertEquals(3, dependency.getEvidence(VERSION).size());
        dependency.addEvidence(VERSION, "pom", "version", "1.2.3", HIGHEST);
        instance.analyzeDependency(dependency, null);
        Assert.assertEquals(4, dependency.getEvidence(VERSION).size());
        Assert.assertNull(dependency.getVersion());
        dependency.addEvidence(VERSION, "Manifest", "Implementation-Version", "1.2.3", HIGHEST);
        instance.analyzeDependency(dependency, null);
        Assert.assertEquals("1.2.3", dependency.getVersion());
        Assert.assertEquals(2, dependency.getEvidence(VERSION).size());
        dependency.addEvidence(VERSION, "nexus", "version", "1.2.3", HIGHEST);
        dependency.addEvidence(VERSION, "other", "version", "alpha", HIGHEST);
        instance.analyzeDependency(dependency, null);
        Assert.assertEquals(3, dependency.getEvidence(VERSION).size());
        dependency.addEvidence(VERSION, "central", "version", "1.2.3", HIGHEST);
        dependency.addEvidence(VERSION, "other", "version", "alpha", HIGHEST);
        instance.analyzeDependency(dependency, null);
        Assert.assertEquals(4, dependency.getEvidence(VERSION).size());
    }
}

