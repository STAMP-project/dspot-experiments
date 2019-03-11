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


import KEYS.SUPPRESSION_FILE;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.Engine;
import org.owasp.dependencycheck.analyzer.exception.AnalysisException;
import org.owasp.dependencycheck.dependency.Dependency;
import org.owasp.dependencycheck.exception.InitializationException;


/**
 *
 *
 * @author Jeremy Long
 */
public class AbstractSuppressionAnalyzerTest extends BaseTest {
    /**
     * A second suppression file to test with.
     */
    private static final String OTHER_SUPPRESSIONS_FILE = "other-suppressions.xml";

    /**
     * Suppression file to test with.
     */
    private static final String SUPPRESSIONS_FILE = "suppressions.xml";

    private AbstractSuppressionAnalyzer instance;

    /**
     * Test of getSupportedExtensions method, of class
     * AbstractSuppressionAnalyzer.
     */
    @Test
    public void testGetSupportedExtensions() {
        Set<String> result = instance.getSupportedExtensions();
        Assert.assertNull(result);
    }

    /**
     * Test of getRules method, of class AbstractSuppressionAnalyzer for
     * suppression file declared as URL.
     */
    @Test
    public void testGetRulesFromSuppressionFileFromURL() throws Exception {
        final String fileUrl = getClass().getClassLoader().getResource(AbstractSuppressionAnalyzerTest.SUPPRESSIONS_FILE).toURI().toURL().toString();
        final int numberOfExtraLoadedRules = (getNumberOfRulesLoadedFromPath(fileUrl)) - (getNumberOfRulesLoadedInCoreFile());
        Assert.assertEquals("Expected 5 extra rules in the given path", 5, numberOfExtraLoadedRules);
    }

    /**
     * Test of getRules method, of class AbstractSuppressionAnalyzer for
     * suppression file on the class path.
     */
    @Test
    public void testGetRulesFromSuppressionFileInClasspath() throws Exception {
        final int numberOfExtraLoadedRules = (getNumberOfRulesLoadedFromPath(AbstractSuppressionAnalyzerTest.SUPPRESSIONS_FILE)) - (getNumberOfRulesLoadedInCoreFile());
        Assert.assertEquals("Expected 5 extra rules in the given file", 5, numberOfExtraLoadedRules);
    }

    /**
     * Assert that rules are loaded from multiple files if multiple files are
     * defined in the {@link Settings}.
     */
    @Test
    public void testGetRulesFromMultipleSuppressionFiles() throws Exception {
        final int rulesInCoreFile = getNumberOfRulesLoadedInCoreFile();
        // GIVEN suppression rules from one file
        final int rulesInFirstFile = (getNumberOfRulesLoadedFromPath(AbstractSuppressionAnalyzerTest.SUPPRESSIONS_FILE)) - rulesInCoreFile;
        // AND suppression rules from another file
        final int rulesInSecondFile = (getNumberOfRulesLoadedFromPath(AbstractSuppressionAnalyzerTest.OTHER_SUPPRESSIONS_FILE)) - rulesInCoreFile;
        // WHEN initializing with both suppression files
        final String[] suppressionFiles = new String[]{ AbstractSuppressionAnalyzerTest.SUPPRESSIONS_FILE, AbstractSuppressionAnalyzerTest.OTHER_SUPPRESSIONS_FILE };
        getSettings().setArrayIfNotEmpty(SUPPRESSION_FILE, suppressionFiles);
        instance.initialize(getSettings());
        instance.prepare(null);
        // THEN rules from both files were loaded
        final int expectedSize = (rulesInFirstFile + rulesInSecondFile) + rulesInCoreFile;
        Assert.assertThat("Expected suppressions from both files", instance.getRuleCount(), CoreMatchers.is(expectedSize));
    }

    @Test(expected = InitializationException.class)
    public void testFailureToLocateSuppressionFileAnywhere() throws Exception {
        getSettings().setString(Settings.KEYS.SUPPRESSION_FILE, "doesnotexist.xml");
        instance.initialize(getSettings());
        instance.prepare(null);
    }

    public static class AbstractSuppressionAnalyzerImpl extends AbstractSuppressionAnalyzer {
        @Override
        public void analyzeDependency(Dependency dependency, Engine engine) throws AnalysisException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public String getName() {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public AnalysisPhase getAnalysisPhase() {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        protected String getAnalyzerEnabledSettingKey() {
            return "unknown";
        }
    }
}

