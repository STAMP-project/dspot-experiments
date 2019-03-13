/**
 * The MIT License
 *
 * Copyright 2012 Jesse Glick.
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
import Item.CREATE;
import Item.EXTENDED_READ;
import Jenkins.READ;
import hudson.model.FreeStyleProject;
import hudson.model.User;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockAuthorizationStrategy;
import org.jvnet.hudson.test.MockFolder;


public class CopyJobCommandTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    private CLICommand copyJobCommand;

    private CLICommandInvoker command;

    @Test
    public void copyBetweenFolders() throws Exception {
        MockFolder dir1 = j.createFolder("dir1");
        MockFolder dir2 = j.createFolder("dir2");
        FreeStyleProject p = dir1.createProject(FreeStyleProject.class, "p1");
        CLICommandInvoker.Result result = command.invokeWithArgs("dir1/p1", "dir2/p2");
        MatcherAssert.assertThat(result, succeededSilently());
        Assert.assertNotNull(j.jenkins.getItemByFullName("dir2/p2"));
        // TODO test copying from/to root, or into nonexistent folder
    }

    @Issue("JENKINS-22262")
    @Test
    public void folderPermissions() throws Exception {
        final MockFolder d1 = j.createFolder("d1");
        final FreeStyleProject p = d1.createProject(FreeStyleProject.class, "p");
        final MockFolder d2 = j.createFolder("d2");
        // alice has no real permissions. bob has READ on everything but no more. charlie has CREATE on d2 but not EXTENDED_READ on p. debbie has both.
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        j.jenkins.setAuthorizationStrategy(// including alice
        new MockAuthorizationStrategy().grant(READ).everywhere().toAuthenticated().grant(Item.READ).onItems(d1, p, d2).to("bob", "charlie", "debbie").grant(CREATE).onItems(d2).to("charlie", "debbie").grant(EXTENDED_READ).onItems(p).to("debbie"));
        copyJobCommand.setTransportAuth(User.get("alice").impersonate());
        MatcherAssert.assertThat(command.invokeWithArgs("d1/p", "d2/p"), failedWith(3));
        copyJobCommand.setTransportAuth(User.get("bob").impersonate());
        MatcherAssert.assertThat(command.invokeWithArgs("d1/p", "d2/p"), failedWith(6));
        copyJobCommand.setTransportAuth(User.get("charlie").impersonate());
        MatcherAssert.assertThat(command.invokeWithArgs("d1/p", "d2/p"), failedWith(6));
        copyJobCommand.setTransportAuth(User.get("debbie").impersonate());
        MatcherAssert.assertThat(command.invokeWithArgs("d1/p", "d2/p"), succeededSilently());
        Assert.assertNotNull(d2.getItem("p"));
    }

    // hold off build until saved only makes sense on the UI with config screen shown after copying;
    // expect the CLI copy command to leave the job buildable
    @Test
    public void copiedJobIsBuildable() throws Exception {
        FreeStyleProject p1 = j.createFreeStyleProject();
        String copiedProjectName = "p2";
        CLICommandInvoker.Result result = command.invokeWithArgs(p1.getName(), copiedProjectName);
        MatcherAssert.assertThat(result, succeededSilently());
        FreeStyleProject p2 = ((FreeStyleProject) (j.jenkins.getItem(copiedProjectName)));
        Assert.assertNotNull(p2);
        Assert.assertTrue(p2.isBuildable());
    }
}

