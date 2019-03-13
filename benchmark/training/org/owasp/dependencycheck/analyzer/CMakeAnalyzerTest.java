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
import java.io.File;
import java.util.regex.Pattern;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseDBTestCase;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.Engine;
import org.owasp.dependencycheck.analyzer.exception.AnalysisException;
import org.owasp.dependencycheck.data.nvdcve.DatabaseException;
import org.owasp.dependencycheck.dependency.Dependency;


/**
 * Unit tests for CmakeAnalyzer.
 *
 * @author Dale Visser
 */
public class CMakeAnalyzerTest extends BaseDBTestCase {
    /**
     * The package analyzer to test.
     */
    private CMakeAnalyzer analyzer;

    /**
     * Test of getName method, of class PythonPackageAnalyzer.
     */
    @Test
    public void testGetName() {
        Assert.assertThat(analyzer.getName(), CoreMatchers.is(CoreMatchers.equalTo("CMake Analyzer")));
    }

    /**
     * Test of supportsExtension method, of class PythonPackageAnalyzer.
     */
    @Test
    public void testAccept() {
        Assert.assertTrue("Should support \"CMakeLists.txt\" name.", analyzer.accept(new File("CMakeLists.txt")));
        Assert.assertTrue("Should support \"cmake\" extension.", analyzer.accept(new File("test.cmake")));
    }

    /**
     * Test whether expected evidence is gathered from OpenCV's CMakeLists.txt.
     *
     * @throws AnalysisException
     * 		is thrown when an exception occurs.
     */
    @Test
    public void testAnalyzeCMakeListsOpenCV() throws AnalysisException {
        final Dependency result = new Dependency(BaseTest.getResourceAsFile(this, "cmake/opencv/CMakeLists.txt"));
        analyzer.analyze(result, null);
        final String product = "OpenCV";
        assertProductEvidence(result, product);
    }

    /**
     * Test whether expected evidence is gathered from OpenCV's CMakeLists.txt.
     *
     * @throws AnalysisException
     * 		is thrown when an exception occurs.
     */
    @Test
    public void testAnalyzeCMakeListsZlib() throws AnalysisException {
        final Dependency result = new Dependency(BaseTest.getResourceAsFile(this, "cmake/zlib/CMakeLists.txt"));
        analyzer.analyze(result, null);
        final String product = "zlib";
        assertProductEvidence(result, product);
    }

    /**
     * Test whether expected evidence is gathered from OpenCV's CVDetectPython.
     *
     * @throws AnalysisException
     * 		is thrown when an exception occurs.
     */
    @Test
    public void testAnalyzeCMakeListsPython() throws AnalysisException {
        final Dependency result = new Dependency(BaseTest.getResourceAsFile(this, "cmake/opencv/cmake/OpenCVDetectPython.cmake"));
        analyzer.analyze(result, null);
        // this one finds nothing so it falls through to the filename. Can we do better?
        Assert.assertEquals("OpenCVDetectPython.cmake", result.getDisplayFileName());
    }

    /**
     * Test whether expected version evidence is gathered from OpenCV's third
     * party cmake files.
     *
     * @throws AnalysisException
     * 		is thrown when an exception occurs.
     */
    @Test
    public void testAnalyzeCMakeListsOpenCV3rdParty() throws AnalysisException, DatabaseException {
        try (Engine engine = new Engine(getSettings())) {
            final Dependency result = new Dependency(BaseTest.getResourceAsFile(this, "cmake/opencv/3rdparty/ffmpeg/ffmpeg_version.cmake"));
            analyzer.analyze(result, engine);
            assertProductEvidence(result, "libavcodec");
            assertVersionEvidence(result, "55.18.102");
            Assert.assertFalse("ALIASOF_ prefix shouldn't be present.", Pattern.compile("\\bALIASOF_\\w+").matcher(result.getEvidence(PRODUCT).toString()).find());
            final Dependency[] dependencies = engine.getDependencies();
            Assert.assertEquals("Number of additional dependencies should be 4.", 4, dependencies.length);
            final Dependency last = dependencies[3];
            assertProductEvidence(last, "libavresample");
            assertVersionEvidence(last, "1.0.1");
        }
    }
}

