/**
 * The MIT License
 *
 * Copyright 2013 Red Hat, Inc.
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
import View.CREATE;
import hudson.model.ListView;
import hudson.model.View;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static Matcher.failedWith;
import static Matcher.hasNoStandardOutput;
import static Matcher.succeededSilently;


public class CreateViewCommandTest {
    private CLICommandInvoker command;

    @Rule
    public final JenkinsRule j = new JenkinsRule();

    @Test
    public void createViewShouldFailWithoutViewCreatePermission() {
        final CLICommandInvoker.Result result = command.authorizedTo(READ).withStdin(this.getClass().getResourceAsStream("/hudson/cli/view.xml")).invoke();
        MatcherAssert.assertThat(result, failedWith(6));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: user is missing the View/Create permission"));
    }

    @Test
    public void createViewShouldSucceed() {
        final CLICommandInvoker.Result result = command.authorizedTo(CREATE, READ).withStdin(this.getClass().getResourceAsStream("/hudson/cli/view.xml")).invoke();
        MatcherAssert.assertThat(result, succeededSilently());
        final View updatedView = j.jenkins.getView("ViewFromXML");
        MatcherAssert.assertThat(updatedView.getViewName(), Matchers.equalTo("ViewFromXML"));
        MatcherAssert.assertThat(updatedView.isFilterExecutors(), Matchers.equalTo(true));
        MatcherAssert.assertThat(updatedView.isFilterQueue(), Matchers.equalTo(false));
    }

    @Test
    public void createViewSpecifyingNameExplicitlyShouldSucceed() {
        final CLICommandInvoker.Result result = command.authorizedTo(CREATE, READ).withStdin(this.getClass().getResourceAsStream("/hudson/cli/view.xml")).invokeWithArgs("CustomViewName");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat("A view with original name should not exist", j.jenkins.getView("ViewFromXML"), Matchers.nullValue());
        final View updatedView = j.jenkins.getView("CustomViewName");
        MatcherAssert.assertThat(updatedView.getViewName(), Matchers.equalTo("CustomViewName"));
        MatcherAssert.assertThat(updatedView.isFilterExecutors(), Matchers.equalTo(true));
        MatcherAssert.assertThat(updatedView.isFilterQueue(), Matchers.equalTo(false));
    }

    @Test
    public void createViewShouldFailIfViewAlreadyExists() throws IOException {
        j.jenkins.addView(new ListView("ViewFromXML"));
        final CLICommandInvoker.Result result = command.authorizedTo(CREATE, READ).withStdin(this.getClass().getResourceAsStream("/hudson/cli/view.xml")).invoke();
        MatcherAssert.assertThat(result, failedWith(4));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: View 'ViewFromXML' already exists"));
    }

    @Test
    public void createViewShouldFailUsingInvalidName() {
        final CLICommandInvoker.Result result = command.authorizedTo(CREATE, READ).withStdin(this.getClass().getResourceAsStream("/hudson/cli/view.xml")).invokeWithArgs("..");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Invalid view name"));
    }
}

