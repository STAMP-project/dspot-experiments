/**
 * The MIT License
 *
 * Copyright 2016 Red Hat, Inc.
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
package hudson.cli;


import CLICommandInvoker.Result;
import Jenkins.READ;
import hudson.Extension;
import hudson.model.FreeStyleProject;
import hudson.model.Run;
import java.io.IOException;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static Matcher.failedWith;
import static Matcher.hasNoStandardOutput;
import static Matcher.succeeded;


/**
 *
 *
 * @author pjanouse
 */
public class RunRangeCommandTest {
    private static CLICommandInvoker command = null;

    private static FreeStyleProject project = null;

    private static final String PROJECT_NAME = "aProject";

    private static final int BUILDS = 10;

    private static final int[] deleted = new int[]{ 5, 8, 9 };

    @ClassRule
    public static final JenkinsRule j = new JenkinsRule();

    @Test
    public void dummyRangeShouldFailWithoutJobReadPermission() throws Exception {
        final CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString(String.format("ERROR: No such job '%s'", RunRangeCommandTest.PROJECT_NAME)));
    }

    @Test
    public void dummyRangeShouldFailIfJobDesNotExist() throws Exception {
        final CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs("never_created", "1");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such job 'never_created'"));
    }

    @Test
    public void dummyRangeShouldFailIfJobNameIsEmpty() throws Exception {
        final CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs("", "1");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString(String.format("ERROR: No such job ''; perhaps you meant '%s'?", RunRangeCommandTest.PROJECT_NAME)));
    }

    @Test
    public void dummyRangeShouldFailIfJobNameIsSpace() throws Exception {
        final CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(" ", "1");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString(String.format("ERROR: No such job ' '; perhaps you meant '%s'?", RunRangeCommandTest.PROJECT_NAME)));
    }

    @Test
    public void dummyRangeShouldSuccessIfBuildDoesNotExist() throws Exception {
        CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, String.valueOf(((RunRangeCommandTest.BUILDS) + 1)));
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: " + (System.lineSeparator()))));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, String.valueOf(RunRangeCommandTest.deleted[0]));
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: " + (System.lineSeparator()))));
    }

    @Test
    public void dummyRangeNumberSingleShouldSuccess() throws Exception {
        // First
        CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: 1" + (System.lineSeparator()))));
        // First with plus symbol '+'
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "+1");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: 1" + (System.lineSeparator()))));
        // In the middle
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "10");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: 10" + (System.lineSeparator()))));
        // In the middle with plus symbol '+'
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "+10");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: 10" + (System.lineSeparator()))));
        // Last
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, String.valueOf(RunRangeCommandTest.BUILDS));
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(String.format(("Builds: %s" + (System.lineSeparator())), String.valueOf(RunRangeCommandTest.BUILDS))));
        // Last with the plus symbol '+'
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, ('+' + (String.valueOf(RunRangeCommandTest.BUILDS))));
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(String.format(("Builds: %s" + (System.lineSeparator())), String.valueOf(RunRangeCommandTest.BUILDS))));
    }

    @Test
    public void dummyRangeNumberSingleShouldSuccessIfBuildNumberIsZero() throws Exception {
        CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "0");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: " + (System.lineSeparator()))));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "+0");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: " + (System.lineSeparator()))));
    }

    @Test
    public void dummyRangeNumberSingleShouldFailIfBuildNumberIsNegative() throws Exception {
        final CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "-1");
        MatcherAssert.assertThat(result, failedWith(2));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: \"-1\" is not a valid option"));
    }

    @Test
    public void dummyRangeNumberSingleShouldFailIfBuildNumberIsTooBig() throws Exception {
        final CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "2147483648");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '2147483648', expected number"));
    }

    @Test
    public void dummyRangeNumberSingleShouldFailIfBuildNumberIsInvalid() throws Exception {
        CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1a");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '1a', expected number"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "aa");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse 'aa', expected number"));
    }

    @Test
    public void dummyRangeNumberSingleShouldSuccessIfBuildNumberIsEmpty() throws Exception {
        final CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: " + (System.lineSeparator()))));
    }

    @Test
    public void dummyRangeNumberSingleShouldFailIfBuildNumberIsSpace() throws Exception {
        final CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, " ");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse ' ', expected number"));
    }

    @Test
    public void dummyRangeNumberSingleShouldSuccessIfBuildNumberIsComma() throws Exception {
        final CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, ",");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: " + (System.lineSeparator()))));
    }

    @Test
    public void dummyRangeNumberSingleShouldFailIfBuildNumberIsHyphen() throws Exception {
        final CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "-");
        MatcherAssert.assertThat(result, failedWith(2));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: \"-\" is not a valid option"));
    }

    @Test
    public void dummyRangeNumberMultiShouldSuccess() throws Exception {
        CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1,2");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: 1,2" + (System.lineSeparator()))));
        // With plus symbol '+'
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1,+2,4");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: 1,2,4" + (System.lineSeparator()))));
        // Build specified twice
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1,1");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: 1,1" + (System.lineSeparator()))));
        // Build with zero build number
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "0,1,2");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: 1,2" + (System.lineSeparator()))));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1,0,2");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: 1,2" + (System.lineSeparator()))));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1,2,0");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: 1,2" + (System.lineSeparator()))));
    }

    @Test
    public void dummyRangeNumberMultiShouldSuccessIfSomeBuildDoesNotExist() throws Exception {
        CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, ("1,2," + (RunRangeCommandTest.deleted[0])));
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: 1,2" + (System.lineSeparator()))));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, String.format("1,%d,%d", RunRangeCommandTest.deleted[0], ((RunRangeCommandTest.deleted[0]) + 1)));
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(String.format(("Builds: 1,%d" + (System.lineSeparator())), ((RunRangeCommandTest.deleted[0]) + 1))));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, String.format("%d,%d,%d", ((RunRangeCommandTest.deleted[0]) - 1), RunRangeCommandTest.deleted[0], ((RunRangeCommandTest.deleted[0]) + 1)));
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(String.format(("Builds: %d,%d" + (System.lineSeparator())), ((RunRangeCommandTest.deleted[0]) - 1), ((RunRangeCommandTest.deleted[0]) + 1))));
    }

    @Test
    public void dummyRangeNumberMultiShouldFailIfBuildNumberIsNegative() throws Exception {
        CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "-1,2,3");
        MatcherAssert.assertThat(result, failedWith(2));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: \"-1,2,3\" is not a valid option"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1,-2,3");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse \'1,-2,3\', expected string with a range M-N"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1,2,-3");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse \'1,2,-3\', expected string with a range M-N"));
    }

    @Test
    public void dummyRangeNumberMultiShouldFailIfBuildNumberIsTooBig() throws Exception {
        CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "2147483648,2,3");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '2147483648,2,3', expected number"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1,2147483648,3");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '1,2147483648,3', expected number"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1,2,2147483648");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '1,2,2147483648', expected number"));
    }

    @Test
    public void dummyRangeNumberMultiShouldFailIfBuildNumberIsInvalid() throws Exception {
        CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1a,2,3");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '1a,2,3', expected number"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "aa,2,3");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse 'aa,2,3', expected number"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1,2a,3");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '1,2a,3', expected number"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1,aa,3");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '1,aa,3', expected number"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1,2,3a");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '1,2,3a', expected number"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1,2,aa");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '1,2,aa', expected number"));
    }

    @Test
    public void dummyRangeNumberMultiShouldFailIfBuildNumberIsEmpty() throws Exception {
        CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, ",2,3");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse ',2,3', expected correct notation M,N or M-N"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1,,3");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '1,,3', expected correct notation M,N or M-N"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1,2,");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '1,2,', expected correct notation M,N or M-N"));
    }

    @Test
    public void dummyRangeNumberMultiShouldFailIfBuildNumberIsSpace() throws Exception {
        CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, " ,2,3");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse ' ,2,3', expected number"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1, ,3");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '1, ,3', expected number"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1,2, ");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '1,2, ', expected number"));
    }

    @Test
    public void dummyRangeNumberMultiShouldFailIfBuildNumberIsComma() throws Exception {
        CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, ",,2,3");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse ',,2,3', expected correct notation M,N or M-N"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1,,,3");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '1,,,3', expected correct notation M,N or M-N"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1,2,,");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '1,2,,', expected correct notation M,N or M-N"));
    }

    @Test
    public void dummyRangeNumberMultiShouldFailIfBuildNumberIsHyphen() throws Exception {
        CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "-,2,3");
        MatcherAssert.assertThat(result, failedWith(2));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: \"-,2,3\" is not a valid option"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1,-,3");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '1,-,3', expected string with a range M-N"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1,2,-");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '1,2,-', expected string with a range M-N"));
    }

    @Test
    public void dummyRangeRangeSingleShouldSuccess() throws Exception {
        CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1-2");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: 1,2" + (System.lineSeparator()))));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "+1-+2");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: 1,2" + (System.lineSeparator()))));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1-1");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: 1" + (System.lineSeparator()))));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "+1-+1");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: 1" + (System.lineSeparator()))));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, ("1-" + (RunRangeCommandTest.deleted[0])));
        MatcherAssert.assertThat(result, succeeded());
        String builds = "";
        boolean next = false;
        for (int i = 1; i < (RunRangeCommandTest.deleted[0]); i++) {
            if (next)
                builds += ",";

            builds += i;
            next = true;
        }
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString((("Builds: " + builds) + (System.lineSeparator()))));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, ("+1-+" + (RunRangeCommandTest.deleted[0])));
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString((("Builds: " + builds) + (System.lineSeparator()))));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "0-1");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: 1" + (System.lineSeparator()))));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "+0-+1");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: 1" + (System.lineSeparator()))));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "0-2");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: 1,2" + (System.lineSeparator()))));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "+0-+2");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: 1,2" + (System.lineSeparator()))));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "0-0");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: " + (System.lineSeparator()))));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "+0-+0");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: " + (System.lineSeparator()))));
    }

    @Test
    public void dummyRangeRangeSingleShouldSuccessIfSomeBuildDoesNotExist() throws Exception {
        CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, String.format("%d-%d", RunRangeCommandTest.deleted[0], ((RunRangeCommandTest.deleted[0]) + 1)));
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(String.format(("Builds: %d" + (System.lineSeparator())), ((RunRangeCommandTest.deleted[0]) + 1))));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, String.format("%d-%d", ((RunRangeCommandTest.deleted[0]) - 1), ((RunRangeCommandTest.deleted[0]) + 1)));
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(String.format(("Builds: %d,%d" + (System.lineSeparator())), ((RunRangeCommandTest.deleted[0]) - 1), ((RunRangeCommandTest.deleted[0]) + 1))));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, String.format("%d-%d", ((RunRangeCommandTest.deleted[0]) - 1), RunRangeCommandTest.deleted[0]));
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(String.format(("Builds: %d" + (System.lineSeparator())), ((RunRangeCommandTest.deleted[0]) - 1))));
    }

    @Test
    public void dummyRangeRangeSingleShouldFailIfBuildRangeContainsZeroAndNegative() throws Exception {
        CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "0--1");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '0--1', expected correct notation M,N or M-N"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "+0--1");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '+0--1', expected correct notation M,N or M-N"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "0--2");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '0--2', expected correct notation M,N or M-N"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "+0--2");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '+0--2', expected correct notation M,N or M-N"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1-0");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '1-0', expected string with a range M-N where M<N"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "+1-+0");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '+1-+0', expected string with a range M-N where M<N"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "2-0");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '2-0', expected string with a range M-N where M<N"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "+2-+0");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '+2-+0', expected string with a range M-N where M<N"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "-1-0");
        MatcherAssert.assertThat(result, failedWith(2));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: \"-1-0\" is not a valid option"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "-1-+0");
        MatcherAssert.assertThat(result, failedWith(2));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: \"-1-+0\" is not a valid option"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "-2-0");
        MatcherAssert.assertThat(result, failedWith(2));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: \"-2-0\" is not a valid option"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "-2-+0");
        MatcherAssert.assertThat(result, failedWith(2));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: \"-2-+0\" is not a valid option"));
    }

    @Test
    public void dummyRangeRangeSingleShouldFailIfBuildRangeContainsANegativeNumber() throws Exception {
        CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "-1-1");
        MatcherAssert.assertThat(result, failedWith(2));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: \"-1-1\" is not a valid option"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "-1-+1");
        MatcherAssert.assertThat(result, failedWith(2));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: \"-1-+1\" is not a valid option"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "-1-2");
        MatcherAssert.assertThat(result, failedWith(2));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: \"-1-2\" is not a valid option"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "-1-+2");
        MatcherAssert.assertThat(result, failedWith(2));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: \"-1-+2\" is not a valid option"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1--1");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '1--1', expected correct notation M,N or M-N"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "+1--1");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '+1--1', expected correct notation M,N or M-N"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1--2");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '1--2', expected correct notation M,N or M-N"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "+1--2");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '+1--2', expected correct notation M,N or M-N"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "-1--1");
        MatcherAssert.assertThat(result, failedWith(2));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: \"-1--1\" is not a valid option"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "-2--1");
        MatcherAssert.assertThat(result, failedWith(2));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: \"-2--1\" is not a valid option"));
    }

    @Test
    public void dummyRangeRangeSingleShouldFailIfBuildRangeContainsTooBigNumber() throws Exception {
        CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1-2147483648");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '1-2147483648', expected number"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "2147483648-1");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '2147483648-1', expected number"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "2147483648-2147483648");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '2147483648-2147483648', expected number"));
    }

    @Test
    public void dummyRangeRangeSingleShouldFailIfBuildRangeContainsInvalidNumber() throws Exception {
        CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1-2a");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '1-2a', expected number"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1-aa");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '1-aa', expected number"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "2a-2");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '2a-2', expected number"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "aa-2");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse 'aa-2', expected number"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "2a-2a");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '2a-2a', expected number"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "aa-aa");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse 'aa-aa', expected number"));
    }

    @Test
    public void dummyRangeRangeSingleShouldFailIfBuildRangeContainsEmptyNumber() throws Exception {
        CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "-1");
        MatcherAssert.assertThat(result, failedWith(2));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: \"-1\" is not a valid option"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1-");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '1-', expected string with a range M-N"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "-");
        MatcherAssert.assertThat(result, failedWith(2));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: \"-\" is not a valid option"));
    }

    @Test
    public void dummyRangeRangeSingleShouldFailIfBuildRangeContainsSpace() throws Exception {
        CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, " -1");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse ' -1', expected string with a range M-N"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1- ");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '1- ', expected string with a range M-N"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, " - ");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse ' - ', expected string with a range M-N"));
    }

    @Test
    public void dummyRangeRangeSingleShouldFailIfBuildRangeContainsComma() throws Exception {
        CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, ",-1");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse ',-1', expected string with a range M-N"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1-,");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '1-,', expected string with a range M-N"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, ",-,");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse ',-,', expected string with a range M-N"));
    }

    @Test
    public void dummyRangeRangeSingleShouldFailIfBuildRangeContainsHyphen() throws Exception {
        CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "--1");
        MatcherAssert.assertThat(result, failedWith(2));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: \"--1\" is not a valid option"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1--");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '1--', expected correct notation M,N or M-N"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "---");
        MatcherAssert.assertThat(result, failedWith(2));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: \"---\" is not a valid option"));
    }

    @Test
    public void dummyRangeRangeSingleShouldFailIfBuildRangeIsInverse() throws Exception {
        CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "2-1");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '2-1', expected string with a range M-N where M<N"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "10-1");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '10-1', expected string with a range M-N where M<N"));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "-1--2");
        MatcherAssert.assertThat(result, failedWith(2));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: \"-1--2\" is not a valid option"));
    }

    @Test
    public void dummyRangeRangeSingleShouldFailIfBuildRangeIsInvalid() throws Exception {
        final CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1-3-");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Unable to parse '1-3-', expected correct notation M,N or M-N"));
    }

    @Test
    public void dummyRangeRangeMultiShouldSuccess() throws Exception {
        CLICommandInvoker.Result result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1-2,3-4");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: 1,2,3,4" + (System.lineSeparator()))));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1-3,3-4");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: 1,2,3,3,4" + (System.lineSeparator()))));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1-4,2-3");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: 1,2,3,4,2,3" + (System.lineSeparator()))));
        result = RunRangeCommandTest.command.authorizedTo(READ, Job.READ).invokeWithArgs(RunRangeCommandTest.PROJECT_NAME, "1-2,4-5");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: 1,2,4" + (System.lineSeparator()))));
    }

    @Extension
    public static class DummyRangeCommand extends RunRangeCommand {
        @Override
        public String getShortDescription() {
            return "DummyRangeCommand";
        }

        @Override
        protected int act(List<Run<?, ?>> builds) throws IOException {
            boolean comma = false;
            stdout.print("Builds: ");
            for (Run<?, ?> build : builds) {
                if (comma)
                    stdout.print(",");
                else
                    comma = true;

                stdout.print(build.getNumber());
            }
            stdout.println("");
            return 0;
        }
    }
}

