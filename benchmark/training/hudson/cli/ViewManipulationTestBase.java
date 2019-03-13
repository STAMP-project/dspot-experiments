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
import View.CONFIGURE;
import hudson.model.FreeStyleProject;
import hudson.model.ListView;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static Matcher.failedWith;
import static Matcher.hasNoStandardOutput;


/**
 *
 *
 * @author pjanouse
 */
public abstract class ViewManipulationTestBase {
    protected CLICommandInvoker command;

    @Rule
    public final JenkinsRule j = new JenkinsRule();

    @Test
    public void jobViewManipulationShouldFailWithJenkinsReadPermissionOnly() throws IOException {
        j.jenkins.addView(new ListView("aView"));
        j.createFreeStyleProject("aProject");
        final CLICommandInvoker.Result result = command.authorizedTo(READ).invokeWithArgs("aView", "aProject");
        MatcherAssert.assertThat(result, failedWith(6));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: user is missing the View/Read permission"));
    }

    @Test
    public void jobViewManipulationShouldFailWithViewReadPermissionOnly() throws IOException {
        j.jenkins.addView(new ListView("aView"));
        j.createFreeStyleProject("aProject");
        final CLICommandInvoker.Result result = command.authorizedTo(READ, View.READ).invokeWithArgs("aView", "aProject");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such job 'aProject'"));
    }

    @Test
    public void jobViewManipulationShouldFailWithViewReadAndJobReadPermissionsOnly() throws IOException {
        j.jenkins.addView(new ListView("aView"));
        j.createFreeStyleProject("aProject");
        final CLICommandInvoker.Result result = command.authorizedTo(READ, View.READ, Job.READ).invokeWithArgs("aView", "aProject");
        MatcherAssert.assertThat(result, failedWith(6));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: user is missing the View/Configure permission"));
    }

    @Test
    public void jobViewManipulationShouldFailIfTheViewIsNotDirectlyModifiable() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        MatcherAssert.assertThat(j.jenkins.getView("All").getAllItems().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(j.jenkins.getView("All").contains(project), Matchers.equalTo(true));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, View.READ, Job.READ, CONFIGURE).invokeWithArgs("All", "aProject");
        MatcherAssert.assertThat(result, failedWith(4));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: 'All' view can not be modified directly"));
        MatcherAssert.assertThat(j.jenkins.getView("All").getAllItems().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(j.jenkins.getView("All").contains(project), Matchers.equalTo(true));
    }

    @Test
    public void jobViewManipulationShouldFailIfTheJobDoesNotExist() throws Exception {
        j.jenkins.addView(new ListView("aView"));
        MatcherAssert.assertThat(j.jenkins.getView("aView").getAllItems().size(), Matchers.equalTo(0));
        CLICommandInvoker.Result result = command.authorizedTo(READ, View.READ, Job.READ, CONFIGURE).invokeWithArgs("aView", "never_created");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such job 'never_created'"));
        MatcherAssert.assertThat(j.jenkins.getView("aView").getAllItems().size(), Matchers.equalTo(0));
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        MatcherAssert.assertThat(j.jenkins.getView("aView").getAllItems().size(), Matchers.equalTo(0));
        result = command.authorizedTo(READ, View.READ, Job.READ, CONFIGURE).invokeWithArgs("aView", "aProject1");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such job 'aProject1'; perhaps you meant 'aProject'?"));
        MatcherAssert.assertThat(j.jenkins.getView("aView").getAllItems().size(), Matchers.equalTo(0));
        MatcherAssert.assertThat(j.jenkins.getView("aView").contains(project), Matchers.equalTo(false));
    }

    @Test
    public void jobViewManipulationShouldFailIfTheJobNameIsEmpty() throws Exception {
        j.jenkins.addView(new ListView("aView"));
        MatcherAssert.assertThat(j.jenkins.getView("aView").getAllItems().size(), Matchers.equalTo(0));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, View.READ, Job.READ, CONFIGURE).invokeWithArgs("aView", "");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such job ''"));
        MatcherAssert.assertThat(j.jenkins.getView("aView").getAllItems().size(), Matchers.equalTo(0));
    }

    @Test
    public void jobViewManipulationManyShouldFailIfFirstJobDoesNotExist() throws Exception {
        j.jenkins.addView(new ListView("aView"));
        FreeStyleProject project1 = j.createFreeStyleProject("aProject1");
        FreeStyleProject project2 = j.createFreeStyleProject("aProject2");
        MatcherAssert.assertThat(j.jenkins.getView("aView").getAllItems().size(), Matchers.equalTo(0));
        MatcherAssert.assertThat(j.jenkins.getView("aView").contains(project1), Matchers.equalTo(false));
        MatcherAssert.assertThat(j.jenkins.getView("aView").contains(project2), Matchers.equalTo(false));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, View.READ, Job.READ, CONFIGURE).invokeWithArgs("aView", "never_created", "aProject1", "aProject2");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such job 'never_created'; perhaps you meant 'aProject1'?"));
        MatcherAssert.assertThat(j.jenkins.getView("aView").getAllItems().size(), Matchers.equalTo(0));
        MatcherAssert.assertThat(j.jenkins.getView("aView").contains(project1), Matchers.equalTo(false));
        MatcherAssert.assertThat(j.jenkins.getView("aView").contains(project2), Matchers.equalTo(false));
    }

    @Test
    public void jobViewManipulationManyShouldFailIfMiddleJobDoesNotExist() throws Exception {
        j.jenkins.addView(new ListView("aView"));
        FreeStyleProject project1 = j.createFreeStyleProject("aProject1");
        FreeStyleProject project2 = j.createFreeStyleProject("aProject2");
        MatcherAssert.assertThat(j.jenkins.getView("aView").getAllItems().size(), Matchers.equalTo(0));
        MatcherAssert.assertThat(j.jenkins.getView("aView").contains(project1), Matchers.equalTo(false));
        MatcherAssert.assertThat(j.jenkins.getView("aView").contains(project2), Matchers.equalTo(false));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, View.READ, Job.READ, CONFIGURE).invokeWithArgs("aView", "aProject1", "never_created", "aProject2");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such job 'never_created'; perhaps you meant 'aProject1'?"));
        MatcherAssert.assertThat(j.jenkins.getView("aView").getAllItems().size(), Matchers.equalTo(0));
        MatcherAssert.assertThat(j.jenkins.getView("aView").contains(project1), Matchers.equalTo(false));
        MatcherAssert.assertThat(j.jenkins.getView("aView").contains(project2), Matchers.equalTo(false));
    }

    @Test
    public void jobViewManipulationManyShouldFailIfLastJobDoesNotExist() throws Exception {
        j.jenkins.addView(new ListView("aView"));
        FreeStyleProject project1 = j.createFreeStyleProject("aProject1");
        FreeStyleProject project2 = j.createFreeStyleProject("aProject2");
        MatcherAssert.assertThat(j.jenkins.getView("aView").getAllItems().size(), Matchers.equalTo(0));
        MatcherAssert.assertThat(j.jenkins.getView("aView").contains(project1), Matchers.equalTo(false));
        MatcherAssert.assertThat(j.jenkins.getView("aView").contains(project2), Matchers.equalTo(false));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, View.READ, Job.READ, CONFIGURE).invokeWithArgs("aView", "aProject1", "aProject2", "never_created");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such job 'never_created'; perhaps you meant 'aProject1'?"));
        MatcherAssert.assertThat(j.jenkins.getView("aView").getAllItems().size(), Matchers.equalTo(0));
        MatcherAssert.assertThat(j.jenkins.getView("aView").contains(project1), Matchers.equalTo(false));
        MatcherAssert.assertThat(j.jenkins.getView("aView").contains(project2), Matchers.equalTo(false));
    }

    @Test
    public void jobViewManipulationManyShouldFailIfMoreJobsDoNotExist() throws Exception {
        j.jenkins.addView(new ListView("aView"));
        FreeStyleProject project1 = j.createFreeStyleProject("aProject1");
        FreeStyleProject project2 = j.createFreeStyleProject("aProject2");
        MatcherAssert.assertThat(j.jenkins.getView("aView").getAllItems().size(), Matchers.equalTo(0));
        MatcherAssert.assertThat(j.jenkins.getView("aView").contains(project1), Matchers.equalTo(false));
        MatcherAssert.assertThat(j.jenkins.getView("aView").contains(project2), Matchers.equalTo(false));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, View.READ, Job.READ, CONFIGURE).invokeWithArgs("aView", "aProject1", "never_created", "aProject2", "never_created");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such job 'never_created'; perhaps you meant 'aProject1'?"));
        MatcherAssert.assertThat(j.jenkins.getView("aView").getAllItems().size(), Matchers.equalTo(0));
        MatcherAssert.assertThat(j.jenkins.getView("aView").contains(project1), Matchers.equalTo(false));
        MatcherAssert.assertThat(j.jenkins.getView("aView").contains(project2), Matchers.equalTo(false));
    }
}

