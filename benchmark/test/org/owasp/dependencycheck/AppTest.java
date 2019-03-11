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
 * Copyright (c) 2017 The OWASP Foundation. All Rights Reserved.
 */
package org.owasp.dependencycheck;


import KEYS.SUPPRESSION_FILE;
import Settings.KEYS.ANALYZER_ARCHIVE_ENABLED;
import Settings.KEYS.AUTO_UPDATE;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests for the {@link AppTest} class.
 */
public class AppTest extends BaseTest {
    /**
     * Test rule for asserting exceptions and their contents.
     */
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * Test of ensureCanonicalPath method, of class App.
     */
    @Test
    public void testEnsureCanonicalPath() {
        String file = "../*.jar";
        App instance = new App(getSettings());
        String result = instance.ensureCanonicalPath(file);
        Assert.assertFalse(result.contains(".."));
        Assert.assertTrue(result.endsWith("*.jar"));
        file = "../some/skip/../path/file.txt";
        String expResult = "/some/path/file.txt";
        result = instance.ensureCanonicalPath(file);
        Assert.assertTrue(("result=" + result), result.endsWith(expResult));
    }

    /**
     * Assert that boolean properties can be set on the CLI and parsed into the
     * {@link Settings}.
     *
     * @throws Exception
     * 		the unexpected {@link Exception}.
     */
    @Test
    public void testPopulateSettings() throws Exception {
        File prop = new File(this.getClass().getClassLoader().getResource("sample.properties").toURI().getPath());
        String[] args = new String[]{ "-P", prop.getAbsolutePath() };
        Map<String, Boolean> expected = new HashMap<>();
        expected.put(AUTO_UPDATE, Boolean.FALSE);
        expected.put(ANALYZER_ARCHIVE_ENABLED, Boolean.TRUE);
        Assert.assertTrue(testBooleanProperties(args, expected));
        String[] args2 = new String[]{ "-n" };
        expected.put(AUTO_UPDATE, Boolean.FALSE);
        expected.put(ANALYZER_ARCHIVE_ENABLED, Boolean.TRUE);
        Assert.assertTrue(testBooleanProperties(args2, expected));
        String[] args3 = new String[]{ "-h" };
        expected.put(AUTO_UPDATE, Boolean.TRUE);
        expected.put(ANALYZER_ARCHIVE_ENABLED, Boolean.TRUE);
        Assert.assertTrue(testBooleanProperties(args3, expected));
        String[] args4 = new String[]{ "--disableArchive" };
        expected.put(AUTO_UPDATE, Boolean.TRUE);
        expected.put(ANALYZER_ARCHIVE_ENABLED, Boolean.FALSE);
        Assert.assertTrue(testBooleanProperties(args4, expected));
        String[] args5 = new String[]{ "-P", prop.getAbsolutePath(), "--disableArchive" };
        expected.put(AUTO_UPDATE, Boolean.FALSE);
        expected.put(ANALYZER_ARCHIVE_ENABLED, Boolean.FALSE);
        Assert.assertTrue(testBooleanProperties(args5, expected));
        prop = new File(this.getClass().getClassLoader().getResource("sample2.properties").toURI().getPath());
        String[] args6 = new String[]{ "-P", prop.getAbsolutePath(), "--disableArchive" };
        expected.put(AUTO_UPDATE, Boolean.TRUE);
        expected.put(ANALYZER_ARCHIVE_ENABLED, Boolean.FALSE);
        Assert.assertTrue(testBooleanProperties(args6, expected));
        String[] args7 = new String[]{ "-P", prop.getAbsolutePath(), "--noupdate" };
        expected.put(AUTO_UPDATE, Boolean.FALSE);
        expected.put(ANALYZER_ARCHIVE_ENABLED, Boolean.FALSE);
        Assert.assertTrue(testBooleanProperties(args7, expected));
        String[] args8 = new String[]{ "-P", prop.getAbsolutePath(), "--noupdate", "--disableArchive" };
        expected.put(AUTO_UPDATE, Boolean.FALSE);
        expected.put(ANALYZER_ARCHIVE_ENABLED, Boolean.FALSE);
        Assert.assertTrue(testBooleanProperties(args8, expected));
    }

    /**
     * Assert that an {@link UnrecognizedOptionException} is thrown when a
     * property that is not supported is specified on the CLI.
     *
     * @throws Exception
     * 		the unexpected {@link Exception}.
     */
    @Test
    public void testPopulateSettingsException() throws Exception {
        String[] args = new String[]{ "-invalidPROPERTY" };
        expectedException.expect(UnrecognizedOptionException.class);
        expectedException.expectMessage("Unrecognized option: -invalidPROPERTY");
        testBooleanProperties(args, null);
    }

    /**
     * Assert that a single suppression file can be set using the CLI.
     *
     * @throws Exception
     * 		the unexpected {@link Exception}.
     */
    @Test
    public void testPopulatingSuppressionSettingsWithASingleFile() throws Exception {
        // GIVEN CLI properties with the mandatory arguments
        File prop = new File(this.getClass().getClassLoader().getResource("sample.properties").toURI().getPath());
        // AND a single suppression file
        String[] args = new String[]{ "-P", prop.getAbsolutePath(), "--suppression", "another-file.xml" };
        // WHEN parsing the CLI arguments
        final CliParser cli = new CliParser(getSettings());
        cli.parse(args);
        final App classUnderTest = new App(getSettings());
        classUnderTest.populateSettings(cli);
        // THEN the suppression file is set in the settings for use in the application core
        String[] suppressionFiles = getSettings().getArray(SUPPRESSION_FILE);
        Assert.assertThat("Expected the suppression file to be set in the Settings", suppressionFiles[0], Is.is("another-file.xml"));
    }

    /**
     * Assert that multiple suppression files can be set using the CLI.
     *
     * @throws Exception
     * 		the unexpected {@link Exception}.
     */
    @Test
    public void testPopulatingSuppressionSettingsWithMultipleFiles() throws Exception {
        // GIVEN CLI properties with the mandatory arguments
        File prop = new File(this.getClass().getClassLoader().getResource("sample.properties").toURI().getPath());
        // AND a single suppression file
        String[] args = new String[]{ "-P", prop.getAbsolutePath(), "--suppression", "first-file.xml", "another-file.xml" };
        // WHEN parsing the CLI arguments
        final CliParser cli = new CliParser(getSettings());
        cli.parse(args);
        final App classUnderTest = new App(getSettings());
        classUnderTest.populateSettings(cli);
        // THEN the suppression file is set in the settings for use in the application core
        Assert.assertThat("Expected the suppression files to be set in the Settings with a separator", getSettings().getString(SUPPRESSION_FILE), Is.is("[\"first-file.xml\",\"another-file.xml\"]"));
    }
}

