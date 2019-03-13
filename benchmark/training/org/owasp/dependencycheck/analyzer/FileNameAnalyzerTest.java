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
 * Copyright (c) 2012 Jeremy Long. All Rights Reserved.
 */
package org.owasp.dependencycheck.analyzer;


import EvidenceType.VENDOR;
import EvidenceType.VERSION;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.dependency.Dependency;
import org.owasp.dependencycheck.exception.InitializationException;

import static AnalysisPhase.INFORMATION_COLLECTION;


/**
 *
 *
 * @author Jeremy Long
 */
public class FileNameAnalyzerTest extends BaseTest {
    /**
     * Test of getName method, of class FileNameAnalyzer.
     */
    @Test
    public void testGetName() {
        FileNameAnalyzer instance = new FileNameAnalyzer();
        String expResult = "File Name Analyzer";
        String result = instance.getName();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getAnalysisPhase method, of class FileNameAnalyzer.
     */
    @Test
    public void testGetAnalysisPhase() {
        FileNameAnalyzer instance = new FileNameAnalyzer();
        AnalysisPhase expResult = INFORMATION_COLLECTION;
        AnalysisPhase result = instance.getAnalysisPhase();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of analyze method, of class FileNameAnalyzer.
     */
    @Test
    public void testAnalyze() throws Exception {
        // File struts = new File(this.getClass().getClassLoader().getResource("struts2-core-2.1.2.jar").getPath());
        File struts = BaseTest.getResourceAsFile(this, "struts2-core-2.1.2.jar");
        Dependency resultStruts = new Dependency(struts);
        // File axis = new File(this.getClass().getClassLoader().getResource("axis2-adb-1.4.1.jar").getPath());
        File axis = BaseTest.getResourceAsFile(this, "axis2-adb-1.4.1.jar");
        Dependency resultAxis = new Dependency(axis);
        FileNameAnalyzer instance = new FileNameAnalyzer();
        instance.analyze(resultStruts, null);
        Assert.assertTrue(resultStruts.getEvidence(VENDOR).toString().toLowerCase().contains("struts"));
        instance.analyze(resultAxis, null);
        Assert.assertTrue(resultStruts.getEvidence(VERSION).toString().toLowerCase().contains("2.1.2"));
    }

    /**
     * Test of prepare method, of class FileNameAnalyzer.
     */
    @Test
    public void testInitialize() {
        FileNameAnalyzer instance = new FileNameAnalyzer();
        try {
            instance.initialize(getSettings());
            instance.prepare(null);
        } catch (InitializationException ex) {
            Assert.fail(ex.getMessage());
        }
        Assert.assertTrue(instance.isEnabled());
    }

    /**
     * Test of close method, of class FileNameAnalyzer.
     */
    @Test
    public void testClose() {
        FileNameAnalyzer instance = new FileNameAnalyzer();
        try {
            instance.close();
        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
        }
    }
}

