/**
 * The MIT License
 *
 * Copyright 2013-5 Red Hat, Inc.
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


import AllView.DEFAULT_VIEW_NAME;
import CLICommandInvoker.Result;
import View.DELETE;
import View.READ;
import hudson.model.AllView;
import hudson.model.ListView;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static CLICommand.CLI_LISTPARAM_SUMMARY_ERROR_TEXT;
import static Matcher.failedWith;
import static Matcher.hasNoStandardOutput;
import static Matcher.succeededSilently;


/**
 *
 *
 * @author ogondza, pjanouse
 */
public class DeleteViewCommandTest {
    private CLICommandInvoker command;

    @Rule
    public final JenkinsRule j = new JenkinsRule();

    @Test
    public void deleteViewShouldFailWithoutViewDeletePermission() throws IOException {
        j.jenkins.addView(new ListView("aView"));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, Jenkins.READ).invokeWithArgs("aView");
        MatcherAssert.assertThat(result, failedWith(6));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: user is missing the View/Delete permission"));
    }

    @Test
    public void deleteViewShouldFailWithoutViewReadPermission() throws IOException {
        j.jenkins.addView(new ListView("aView"));
        final CLICommandInvoker.Result result = command.authorizedTo(DELETE, Jenkins.READ).invokeWithArgs("aView");
        MatcherAssert.assertThat(result, failedWith(6));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: user is missing the View/Read permission"));
    }

    @Test
    public void deleteViewShouldSucceed() throws Exception {
        j.jenkins.addView(new ListView("aView"));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, DELETE, Jenkins.READ).invokeWithArgs("aView");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(j.jenkins.getView("aView"), Matchers.nullValue());
    }

    @Test
    public void deleteViewShouldFailIfViewDoesNotExist() {
        final CLICommandInvoker.Result result = command.authorizedTo(READ, DELETE, Jenkins.READ).invokeWithArgs("never_created");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No view named never_created inside view Jenkins"));
    }

    // ViewGroup.canDelete()
    @Test
    public void deleteViewShouldFailIfViewGroupDoesNotAllowDeletion() {
        final CLICommandInvoker.Result result = command.authorizedTo(READ, DELETE, Jenkins.READ).invokeWithArgs(DEFAULT_VIEW_NAME);
        MatcherAssert.assertThat(result, failedWith(4));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(j.jenkins.getView(DEFAULT_VIEW_NAME), Matchers.notNullValue());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString((("ERROR: Jenkins does not allow to delete '" + (AllView.DEFAULT_VIEW_NAME)) + "' view")));
    }

    @Test
    public void deleteViewShouldFailIfViewNameIsEmpty() {
        final CLICommandInvoker.Result result = command.authorizedTo(READ, DELETE, Jenkins.READ).invokeWithArgs("");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: View name is empty"));
    }

    @Test
    public void deleteViewShouldFailIfViewNameIsSpace() {
        final CLICommandInvoker.Result result = command.authorizedTo(READ, DELETE, Jenkins.READ).invokeWithArgs(" ");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No view named   inside view Jenkins"));
    }

    @Test
    public void deleteViewManyShouldSucceed() throws Exception {
        j.jenkins.addView(new ListView("aView1"));
        j.jenkins.addView(new ListView("aView2"));
        j.jenkins.addView(new ListView("aView3"));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, DELETE, Jenkins.READ).invokeWithArgs("aView1", "aView2", "aView3");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(j.jenkins.getView("aView1"), Matchers.nullValue());
        MatcherAssert.assertThat(j.jenkins.getView("aView2"), Matchers.nullValue());
        MatcherAssert.assertThat(j.jenkins.getView("aView3"), Matchers.nullValue());
    }

    @Test
    public void deleteViewManyShouldFailIfFirstViewDoesNotExist() throws Exception {
        j.jenkins.addView(new ListView("aView1"));
        j.jenkins.addView(new ListView("aView2"));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, DELETE, Jenkins.READ).invokeWithArgs("never_created", "aView1", "aView2");
        MatcherAssert.assertThat(result, failedWith(5));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("never_created: No view named never_created inside view Jenkins"));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString(("ERROR: " + (CLI_LISTPARAM_SUMMARY_ERROR_TEXT))));
        MatcherAssert.assertThat(j.jenkins.getView("aView1"), Matchers.nullValue());
        MatcherAssert.assertThat(j.jenkins.getView("aView2"), Matchers.nullValue());
        MatcherAssert.assertThat(j.jenkins.getView("never_created"), Matchers.nullValue());
    }

    @Test
    public void deleteViewManyShouldFailIfMiddleViewDoesNotExist() throws Exception {
        j.jenkins.addView(new ListView("aView1"));
        j.jenkins.addView(new ListView("aView2"));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, DELETE, Jenkins.READ).invokeWithArgs("aView1", "never_created", "aView2");
        MatcherAssert.assertThat(result, failedWith(5));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("never_created: No view named never_created inside view Jenkins"));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString(("ERROR: " + (CLI_LISTPARAM_SUMMARY_ERROR_TEXT))));
        MatcherAssert.assertThat(j.jenkins.getView("aView1"), Matchers.nullValue());
        MatcherAssert.assertThat(j.jenkins.getView("aView2"), Matchers.nullValue());
        MatcherAssert.assertThat(j.jenkins.getView("never_created"), Matchers.nullValue());
    }

    @Test
    public void deleteViewManyShouldFailIfLastViewDoesNotExist() throws Exception {
        j.jenkins.addView(new ListView("aView1"));
        j.jenkins.addView(new ListView("aView2"));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, DELETE, Jenkins.READ).invokeWithArgs("aView1", "aView2", "never_created");
        MatcherAssert.assertThat(result, failedWith(5));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("never_created: No view named never_created inside view Jenkins"));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString(("ERROR: " + (CLI_LISTPARAM_SUMMARY_ERROR_TEXT))));
        MatcherAssert.assertThat(j.jenkins.getView("aView1"), Matchers.nullValue());
        MatcherAssert.assertThat(j.jenkins.getView("aView2"), Matchers.nullValue());
        MatcherAssert.assertThat(j.jenkins.getView("never_created"), Matchers.nullValue());
    }

    @Test
    public void deleteViewManyShouldFailIfMoreViewsDoNotExist() throws Exception {
        j.jenkins.addView(new ListView("aView1"));
        j.jenkins.addView(new ListView("aView2"));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, DELETE, Jenkins.READ).invokeWithArgs("aView1", "never_created1", "never_created2", "aView2");
        MatcherAssert.assertThat(result, failedWith(5));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("never_created1: No view named never_created1 inside view Jenkins"));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("never_created2: No view named never_created2 inside view Jenkins"));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString(("ERROR: " + (CLI_LISTPARAM_SUMMARY_ERROR_TEXT))));
        MatcherAssert.assertThat(j.jenkins.getView("aView1"), Matchers.nullValue());
        MatcherAssert.assertThat(j.jenkins.getView("aView2"), Matchers.nullValue());
        MatcherAssert.assertThat(j.jenkins.getView("never_created1"), Matchers.nullValue());
        MatcherAssert.assertThat(j.jenkins.getView("never_created2"), Matchers.nullValue());
    }

    @Test
    public void deleteViewManyShouldSucceedEvenAViewSpecifiedTwice() throws Exception {
        j.jenkins.addView(new ListView("aView1"));
        j.jenkins.addView(new ListView("aView2"));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, DELETE, Jenkins.READ).invokeWithArgs("aView1", "aView2", "aView1");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(j.jenkins.getView("aView1"), Matchers.nullValue());
        MatcherAssert.assertThat(j.jenkins.getView("aView2"), Matchers.nullValue());
    }

    @Test
    public void deleteViewManyShouldFailWithoutViewDeletePermissionButOthersShouldBeDeleted() throws Exception {
        j.jenkins.addView(new ListView("aView1"));
        j.jenkins.addView(new ListView("aView2"));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, DELETE, Jenkins.READ).invokeWithArgs("aView1", "aView2", DEFAULT_VIEW_NAME);
        MatcherAssert.assertThat(result, failedWith(5));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString(((((AllView.DEFAULT_VIEW_NAME) + ": Jenkins does not allow to delete '") + (AllView.DEFAULT_VIEW_NAME)) + "' view")));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString(("ERROR: " + (CLI_LISTPARAM_SUMMARY_ERROR_TEXT))));
        MatcherAssert.assertThat(j.jenkins.getView("aView1"), Matchers.nullValue());
        MatcherAssert.assertThat(j.jenkins.getView("aView2"), Matchers.nullValue());
        MatcherAssert.assertThat(j.jenkins.getView(DEFAULT_VIEW_NAME), Matchers.notNullValue());
    }
}

