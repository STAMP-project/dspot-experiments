/**
 * The MIT License
 *
 * Copyright 2018 Victor Martinez.
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


import CLICommandInvoker.Matcher;
import CLICommandInvoker.Result;
import hudson.matrix.Axis;
import hudson.matrix.MatrixProject;
import hudson.maven.MavenModuleSet;
import hudson.model.DirectlyModifiableView;
import hudson.model.FreeStyleProject;
import hudson.model.Label;
import hudson.model.ListView;
import hudson.model.labels.LabelExpression;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockFolder;


public class ListJobsCommandTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    private CLICommand listJobsCommand;

    private CLICommandInvoker command;

    @Test
    public void getAllJobsFromView() throws Exception {
        MockFolder folder = j.createFolder("Folder");
        MockFolder nestedFolder = folder.createProject(MockFolder.class, "NestedFolder");
        FreeStyleProject job = folder.createProject(FreeStyleProject.class, "job");
        FreeStyleProject nestedJob = nestedFolder.createProject(FreeStyleProject.class, "nestedJob");
        ListView view = new ListView("OuterFolder");
        view.setRecurse(true);
        j.jenkins.addView(view);
        ((DirectlyModifiableView) (j.jenkins.getView("OuterFolder"))).add(folder);
        ((DirectlyModifiableView) (j.jenkins.getView("OuterFolder"))).add(job);
        CLICommandInvoker.Result result = command.invokeWithArgs("OuterFolder");
        MatcherAssert.assertThat(result, Matcher.succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("Folder"));
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("job"));
        MatcherAssert.assertThat(result.stdout(), Matchers.not(Matchers.containsString("nestedJob")));
    }

    @Issue("JENKINS-48220")
    @Test
    public void getAllJobsFromFolder() throws Exception {
        MockFolder folder = j.createFolder("Folder");
        MockFolder nestedFolder = folder.createProject(MockFolder.class, "NestedFolder");
        FreeStyleProject job = folder.createProject(FreeStyleProject.class, "job");
        FreeStyleProject nestedJob = nestedFolder.createProject(FreeStyleProject.class, "nestedJob");
        CLICommandInvoker.Result result = command.invokeWithArgs("Folder");
        MatcherAssert.assertThat(result, Matcher.succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("job"));
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("NestedFolder"));
        MatcherAssert.assertThat(result.stdout(), Matchers.not(Matchers.containsString("nestedJob")));
    }

    @Issue("JENKINS-18393")
    @Test
    public void getAllJobsFromFolderWithMatrixProject() throws Exception {
        MockFolder folder = j.createFolder("Folder");
        FreeStyleProject job1 = folder.createProject(FreeStyleProject.class, "job1");
        FreeStyleProject job2 = folder.createProject(FreeStyleProject.class, "job2");
        MatrixProject matrixProject = folder.createProject(MatrixProject.class, "mp");
        matrixProject.setDisplayName("downstream");
        matrixProject.setAxes(new hudson.matrix.AxisList(new Axis("axis", "a", "b")));
        Label label = LabelExpression.get("aws-linux-dummy");
        matrixProject.setAssignedLabel(label);
        CLICommandInvoker.Result result = command.invokeWithArgs("Folder");
        MatcherAssert.assertThat(result, Matcher.succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("job1"));
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("job2"));
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("mp"));
    }

    @Issue("JENKINS-18393")
    @Test
    public void getAllJobsFromFolderWithMavenModuleSet() throws Exception {
        MockFolder folder = j.createFolder("Folder");
        FreeStyleProject job1 = folder.createProject(FreeStyleProject.class, "job1");
        FreeStyleProject job2 = folder.createProject(FreeStyleProject.class, "job2");
        MavenModuleSet mavenProject = folder.createProject(MavenModuleSet.class, "mvn");
        CLICommandInvoker.Result result = command.invokeWithArgs("Folder");
        MatcherAssert.assertThat(result, Matcher.succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("job1"));
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("job2"));
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("mvn"));
    }

    @Issue("JENKINS-18393")
    @Test
    public void failForMatrixProject() throws Exception {
        MatrixProject matrixProject = j.createProject(MatrixProject.class, "mp");
        CLICommandInvoker.Result result = command.invokeWithArgs("MatrixJob");
        MatcherAssert.assertThat(result, Matcher.failedWith(3));
        MatcherAssert.assertThat(result.stdout(), Matchers.isEmptyString());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("No view or item group with the given name 'MatrixJob' found."));
    }
}

