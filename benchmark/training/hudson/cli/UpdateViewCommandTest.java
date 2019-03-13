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
import View.CONFIGURE;
import View.READ;
import hudson.model.ListView;
import hudson.model.TreeView;
import hudson.model.View;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static Matcher.failedWith;
import static Matcher.hasNoStandardOutput;
import static Matcher.succeededSilently;


public class UpdateViewCommandTest {
    private CLICommandInvoker command;

    @Rule
    public final JenkinsRule j = new JenkinsRule();

    @Test
    public void updateViewShouldFailWithoutViewConfigurePermission() throws Exception {
        j.jenkins.addView(new ListView("aView"));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, Jenkins.READ).withStdin(this.getClass().getResourceAsStream("/hudson/cli/view.xml")).invokeWithArgs("aView");
        MatcherAssert.assertThat(result, failedWith(6));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: user is missing the View/Configure permission"));
    }

    /**
     * This test shows that updating a view using an XML that will be
     * converted by XStream via an alias will rightfully succeed.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void updateViewWithRenamedClass() throws Exception {
        ListView tv = new ListView("tView");
        j.jenkins.addView(tv);
        j.jenkins.XSTREAM2.addCompatibilityAlias("org.acme.old.Foo", ListView.class);
        final CLICommandInvoker.Result result = command.authorizedTo(READ, CONFIGURE, Jenkins.READ).withStdin(this.getClass().getResourceAsStream("/hudson/cli/testview-foo.xml")).invokeWithArgs("tView");
        MatcherAssert.assertThat(result, succeededSilently());
    }

    @Test
    public void updateViewWithWrongViewTypeShouldFail() throws Exception {
        TreeView tv = new TreeView("aView");
        j.jenkins.addView(tv);
        final CLICommandInvoker.Result result = command.authorizedTo(READ, CONFIGURE, Jenkins.READ).withStdin(this.getClass().getResourceAsStream("/hudson/cli/view.xml")).invokeWithArgs("aView");
        MatcherAssert.assertThat(result, failedWith(1));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString((("Expecting view type: " + (tv.getClass())) + " but got: class hudson.model.ListView instead.")));
    }

    @Test
    public void updateViewShouldModifyViewConfiguration() throws Exception {
        j.jenkins.addView(new ListView("aView"));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, CONFIGURE, Jenkins.READ).withStdin(this.getClass().getResourceAsStream("/hudson/cli/view.xml")).invokeWithArgs("aView");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat("Update should not modify view name", j.jenkins.getView("ViewFromXML"), Matchers.nullValue());
        final View updatedView = j.jenkins.getView("aView");
        MatcherAssert.assertThat(updatedView.getViewName(), Matchers.equalTo("aView"));
        MatcherAssert.assertThat(updatedView.isFilterExecutors(), Matchers.equalTo(true));
        MatcherAssert.assertThat(updatedView.isFilterQueue(), Matchers.equalTo(false));
    }

    @Test
    public void updateViewShouldFailIfViewDoesNotExist() {
        final CLICommandInvoker.Result result = command.authorizedTo(READ, CONFIGURE, Jenkins.READ).withStdin(this.getClass().getResourceAsStream("/hudson/cli/view.xml")).invokeWithArgs("not_created");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No view named not_created inside view Jenkins"));
    }
}

