/**
 * The MIT License
 * Copyright ? 2010 JmxTrans team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.googlecode.jmxtrans.cli;


import java.io.File;
import java.io.IOException;
import org.apache.commons.cli.ParseException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public abstract class CliArgumentParserBase {
    @Rule
    public TemporaryFolder mockConfigurationDirectory = new TemporaryFolder();

    public File mockConfigurationFile;

    @Test
    public void noExceptionThrownWhenHelpIsAsked() throws OptionsException, IOException, ParseException {
        parseConfiguration(new String[]{ "-h" });
    }

    @Test(expected = Exception.class)
    public void jsonDirectoryOrJsonFileIsRequired() throws OptionsException, IOException, ParseException {
        try {
            parseConfiguration(new String[]{ "" });
        } catch (OptionsException oe) {
            MatcherAssert.assertThat(oe.getMessage(), Matchers.is("Please specify either the -f or -j option."));
            throw oe;
        }
    }

    @Test
    public void continueOnJsonErrorIsFalseByDefault() throws OptionsException, IOException, ParseException {
        JmxTransConfiguration configuration = parseConfiguration(requiredOptions());
        MatcherAssert.assertThat(configuration.isContinueOnJsonError(), Matchers.is(false));
    }

    @Test
    public void continueOnJsonErrorIsCanBeSetToTrueOrFalse() throws OptionsException, IOException, ParseException {
        JmxTransConfiguration configuration = parseConfiguration(requiredOptionsAnd("-c", "true"));
        MatcherAssert.assertThat(configuration.isContinueOnJsonError(), Matchers.is(true));
        configuration = parseConfiguration(requiredOptionsAnd("-c", "false"));
        MatcherAssert.assertThat(configuration.isContinueOnJsonError(), Matchers.is(false));
    }

    @Test(expected = Exception.class)
    public void jsonConfigDirectoryCannotBeAFile() throws OptionsException, IOException, ParseException {
        try {
            parseConfiguration(new String[]{ "-j", mockConfigurationFile.getAbsolutePath() });
        } catch (OptionsException oe) {
            MatcherAssert.assertThat(oe.getMessage(), Matchers.startsWith("Path to json directory is invalid"));
            throw oe;
        }
    }

    @Test(expected = Exception.class)
    public void jsonConfigDirectoryMustExist() throws OptionsException, IOException, ParseException {
        try {
            parseConfiguration(new String[]{ "-j", new File(mockConfigurationDirectory.getRoot(), "non-existing").getAbsolutePath() });
        } catch (OptionsException oe) {
            MatcherAssert.assertThat(oe.getMessage(), Matchers.startsWith("Path to json directory is invalid"));
            throw oe;
        }
    }

    @Test(expected = Exception.class)
    public void jsonConfigFileCannotBeADirectory() throws OptionsException, IOException, ParseException {
        try {
            parseConfiguration(new String[]{ "-f", mockConfigurationDirectory.getRoot().getAbsolutePath() });
        } catch (OptionsException oe) {
            MatcherAssert.assertThat(oe.getMessage(), Matchers.startsWith("Path to json file is invalid"));
            throw oe;
        }
    }

    @Test(expected = Exception.class)
    public void jsonConfigFileMustExist() throws OptionsException, IOException, ParseException {
        try {
            parseConfiguration(new String[]{ "-f", new File(mockConfigurationDirectory.getRoot(), "non-existing").getAbsolutePath() });
        } catch (OptionsException oe) {
            MatcherAssert.assertThat(oe.getMessage(), Matchers.startsWith("Path to json file is invalid"));
            throw oe;
        }
    }

    @Test
    public void canParseRunInterval() throws OptionsException, IOException, ParseException {
        JmxTransConfiguration configuration = parseConfiguration(requiredOptionsAnd("-s", "20"));
        MatcherAssert.assertThat(configuration.getRunPeriod(), Matchers.is(20));
    }

    @Test(expected = Exception.class)
    public void runIntervalMustBeInteger() throws OptionsException, IOException, ParseException {
        try {
            parseConfiguration(requiredOptionsAnd("-s", "abc"));
        } catch (OptionsException oe) {
            MatcherAssert.assertThat(oe.getMessage(), Matchers.startsWith("Seconds between server job runs must be an integer"));
            throw oe;
        }
    }
}

