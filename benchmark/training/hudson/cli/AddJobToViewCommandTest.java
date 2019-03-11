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
import hudson.model.DirectlyModifiableView;
import hudson.model.FreeStyleProject;
import hudson.model.ListView;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import static Matcher.succeededSilently;


/**
 *
 *
 * @author pjanouse
 */
public class AddJobToViewCommandTest extends ViewManipulationTestBase {
    @Test
    public void addJobShouldSucceed() throws Exception {
        j.jenkins.addView(new ListView("aView"));
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        MatcherAssert.assertThat(j.jenkins.getView("aView").getAllItems().size(), Matchers.equalTo(0));
        MatcherAssert.assertThat(j.jenkins.getView("aView").contains(project), Matchers.equalTo(false));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, View.READ, Job.READ, CONFIGURE).invokeWithArgs("aView", "aProject");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(j.jenkins.getView("aView").getAllItems().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(j.jenkins.getView("aView").contains(project), Matchers.equalTo(true));
    }

    @Test
    public void addJobShouldSucceedEvenAlreadyAdded() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        j.jenkins.addView(new ListView("aView"));
        ((DirectlyModifiableView) (j.jenkins.getView("aView"))).add(project);
        MatcherAssert.assertThat(j.jenkins.getView("aView").getAllItems().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(j.jenkins.getView("aView").contains(project), Matchers.equalTo(true));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, View.READ, Job.READ, CONFIGURE).invokeWithArgs("aView", "aProject");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(j.jenkins.getView("aView").getAllItems().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(j.jenkins.getView("aView").contains(project), Matchers.equalTo(true));
    }

    @Test
    public void addJobManyShouldSucceed() throws Exception {
        j.jenkins.addView(new ListView("aView"));
        FreeStyleProject project1 = j.createFreeStyleProject("aProject1");
        FreeStyleProject project2 = j.createFreeStyleProject("aProject2");
        MatcherAssert.assertThat(j.jenkins.getView("aView").getAllItems().size(), Matchers.equalTo(0));
        MatcherAssert.assertThat(j.jenkins.getView("aView").contains(project1), Matchers.equalTo(false));
        MatcherAssert.assertThat(j.jenkins.getView("aView").contains(project2), Matchers.equalTo(false));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, View.READ, Job.READ, CONFIGURE).invokeWithArgs("aView", "aProject1", "aProject2");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(j.jenkins.getView("aView").getAllItems().size(), Matchers.equalTo(2));
        MatcherAssert.assertThat(j.jenkins.getView("aView").contains(project1), Matchers.equalTo(true));
        MatcherAssert.assertThat(j.jenkins.getView("aView").contains(project2), Matchers.equalTo(true));
    }

    @Test
    public void addJobManyShouldSucceedEvenAJobIsSpecifiedTwice() throws Exception {
        j.jenkins.addView(new ListView("aView"));
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        MatcherAssert.assertThat(j.jenkins.getView("aView").getAllItems().size(), Matchers.equalTo(0));
        MatcherAssert.assertThat(j.jenkins.getView("aView").contains(project), Matchers.equalTo(false));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, View.READ, Job.READ, CONFIGURE).invokeWithArgs("aView", "aProject", "aProject");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(j.jenkins.getView("aView").getAllItems().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(j.jenkins.getView("aView").contains(project), Matchers.equalTo(true));
    }
}

