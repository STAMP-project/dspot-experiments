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
import hudson.model.FreeStyleProject;
import hudson.model.ListView;
import hudson.model.Node;
import hudson.model.User;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockAuthorizationStrategy;

import static Matcher.failedWith;
import static Matcher.hasNoStandardOutput;


/**
 *
 *
 * @author pjanouse
 */
public class ReloadConfigurationCommandTest {
    private CLICommandInvoker command;

    @Rule
    public final JenkinsRule j = new JenkinsRule();

    @Test
    public void reloadConfigurationShouldFailWithoutAdministerPermission() throws Exception {
        j.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy().grant(READ).everywhere().toAuthenticated());
        final CLICommandInvoker.Result result = command.invoke();
        MatcherAssert.assertThat(result, failedWith(6));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("user is missing the Overall/Administer permission"));
    }

    @Test
    public void reloadMasterConfig() throws Exception {
        Node node = j.jenkins;
        node.setLabelString("oldLabel");
        modifyNode(node);
        MatcherAssert.assertThat(node.getLabelString(), Matchers.equalTo("newLabel"));
    }

    @Test
    public void reloadSlaveConfig() throws Exception {
        Node node = j.createSlave("a_slave", "oldLabel", null);
        modifyNode(node);
        node = j.jenkins.getNode("a_slave");
        MatcherAssert.assertThat(node.getLabelString(), Matchers.equalTo("newLabel"));
    }

    @Test
    public void reloadUserConfig() throws Exception {
        String originalName = "oldName";
        String temporaryName = "newName";
        {
            User user = User.get("some_user", true, null);
            user.setFullName(originalName);
            user.save();
            MatcherAssert.assertThat(user.getFullName(), Matchers.equalTo(originalName));
            user.setFullName(temporaryName);
            MatcherAssert.assertThat(user.getFullName(), Matchers.equalTo(temporaryName));
        }
        reloadJenkinsConfigurationViaCliAndWait();
        {
            User user = User.getById("some_user", false);
            MatcherAssert.assertThat(user.getFullName(), Matchers.equalTo(originalName));
        }
    }

    @Test
    public void reloadJobConfig() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("a_project");
        project.setDescription("oldDescription");
        replace("jobs/a_project/config.xml", "oldDescription", "newDescription");
        MatcherAssert.assertThat(project.getDescription(), Matchers.equalTo("oldDescription"));
        reloadJenkinsConfigurationViaCliAndWait();
        project = j.jenkins.getItem("a_project", j.jenkins, FreeStyleProject.class);
        MatcherAssert.assertThat(project.getDescription(), Matchers.equalTo("newDescription"));
    }

    @Test
    public void reloadViewConfig() throws Exception {
        ListView view = new ListView("a_view");
        j.jenkins.addView(view);
        view.setIncludeRegex("oldIncludeRegex");
        view.save();
        replace("config.xml", "oldIncludeRegex", "newIncludeRegex");
        MatcherAssert.assertThat(view.getIncludeRegex(), Matchers.equalTo("oldIncludeRegex"));
        reloadJenkinsConfigurationViaCliAndWait();
        view = ((ListView) (j.jenkins.getView("a_view")));
        MatcherAssert.assertThat(view.getIncludeRegex(), Matchers.equalTo("newIncludeRegex"));
    }
}

