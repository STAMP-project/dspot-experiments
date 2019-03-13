/**
 * The MIT License
 *
 * Copyright (c) 2018, CloudBees, Inc
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
package hudson.model;


import JenkinsRule.WebClient;
import Result.FAILURE;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.FilePath;
import hudson.Functions;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;


public class FileParameterValueTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    @Issue("SECURITY-1074")
    public void fileParameter_cannotCreateFile_outsideOfBuildFolder() throws Exception {
        // you can test the behavior before the correction by setting FileParameterValue.ALLOW_FOLDER_TRAVERSAL_OUTSIDE_WORKSPACE to true
        FilePath root = j.jenkins.getRootPath();
        FreeStyleProject p = j.createFreeStyleProject();
        p.addProperty(new ParametersDefinitionProperty(Collections.singletonList(new FileParameterDefinition("../../../../../root-level.txt", null))));
        Assert.assertThat(root.child("root-level.txt").exists(), CoreMatchers.equalTo(false));
        String uploadedContent = "test-content";
        File uploadedFile = tmp.newFile();
        FileUtils.write(uploadedFile, uploadedContent);
        FreeStyleBuild build = p.scheduleBuild2(0, new Cause.UserIdCause(), new ParametersAction(new FileParameterValue("../../../../../root-level.txt", uploadedFile, "uploaded-file.txt"))).get();
        Assert.assertThat(build.getResult(), CoreMatchers.equalTo(FAILURE));
        Assert.assertThat(root.child("root-level.txt").exists(), CoreMatchers.equalTo(false));
        // ensure also the file is not reachable by request
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
        checkUrlNot200AndNotContains(wc, ((build.getUrl()) + "parameters/parameter/..%2F..%2F..%2F..%2F..%2Froot-level.txt/uploaded-file.txt"), uploadedContent);
        // encoding dots
        checkUrlNot200AndNotContains(wc, ((build.getUrl()) + "parameters/parameter/%2E%2E%2F%2E%2E%2F%2E%2E%2F%2E%2E%2F%2E%2E%2Froot-level.txt/uploaded-file.txt"), uploadedContent);
        // 16-bit encoding
        checkUrlNot200AndNotContains(wc, ((build.getUrl()) + "parameters/parameter/%u002e%u002e%u2215%u002e%u002e%u2215%u002e%u002e%u2215%u002e%u002e%u2215%u002e%u002e%u2215root-level.txt/uploaded-file.txt"), uploadedContent);
        // double encoding
        checkUrlNot200AndNotContains(wc, ((build.getUrl()) + "parameters/parameter/%252e%252e%252f%252e%252e%252f%252e%252e%252f%252e%252e%252f%252e%252e%252froot-level.txt/uploaded-file.txt"), uploadedContent);
        // overlong utf-8 encoding
        checkUrlNot200AndNotContains(wc, ((build.getUrl()) + "parameters/parameter/%c0%2e%c0%2e%c0%af%c0%2e%c0%2e%c0%af%c0%2e%c0%2e%c0%af%c0%2e%c0%2e%c0%af%c0%2e%c0%2e%c0%afroot-level.txt/uploaded-file.txt"), uploadedContent);
    }

    @Test
    @Issue("SECURITY-1074")
    public void fileParameter_cannotCreateFile_outsideOfBuildFolder_backslashEdition() throws Exception {
        Assume.assumeTrue("Backslash are only dangerous on Windows", Functions.isWindows());
        // you can test the behavior before the correction by setting FileParameterValue.ALLOW_FOLDER_TRAVERSAL_OUTSIDE_WORKSPACE to true
        FilePath root = j.jenkins.getRootPath();
        FreeStyleProject p = j.createFreeStyleProject();
        p.addProperty(new ParametersDefinitionProperty(Collections.singletonList(new FileParameterDefinition("..\\..\\..\\..\\..\\root-level.txt", null))));
        Assert.assertThat(root.child("root-level.txt").exists(), CoreMatchers.equalTo(false));
        String uploadedContent = "test-content";
        File uploadedFile = tmp.newFile();
        FileUtils.write(uploadedFile, uploadedContent);
        FreeStyleBuild build = p.scheduleBuild2(0, new Cause.UserIdCause(), new ParametersAction(new FileParameterValue("..\\..\\..\\..\\..\\root-level.txt", uploadedFile, "uploaded-file.txt"))).get();
        Assert.assertThat(build.getResult(), CoreMatchers.equalTo(FAILURE));
        Assert.assertThat(root.child("root-level.txt").exists(), CoreMatchers.equalTo(false));
        // ensure also the file is not reachable by request
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
        checkUrlNot200AndNotContains(wc, ((build.getUrl()) + "parameters/parameter/..\\..\\..\\..\\..\\root-level.txt/uploaded-file.txt"), uploadedContent);
        checkUrlNot200AndNotContains(wc, ((build.getUrl()) + "parameters/parameter/..%2F..%2F..%2F..%2F..%2Froot-level.txt/uploaded-file.txt"), uploadedContent);
    }

    @Test
    @Issue("SECURITY-1074")
    public void fileParameter_withSingleDot() throws Exception {
        // this case was not working even before the patch
        FreeStyleProject p = j.createFreeStyleProject();
        p.addProperty(new ParametersDefinitionProperty(Collections.singletonList(new FileParameterDefinition(".", null))));
        String uploadedContent = "test-content";
        File uploadedFile = tmp.newFile();
        FileUtils.write(uploadedFile, uploadedContent);
        FreeStyleBuild build = p.scheduleBuild2(0, new Cause.UserIdCause(), new ParametersAction(new FileParameterValue(".", uploadedFile, "uploaded-file.txt"))).get();
        Assert.assertThat(build.getResult(), CoreMatchers.equalTo(FAILURE));
        // ensure also the file is not reachable by request
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
        checkUrlNot200AndNotContains(wc, ((build.getUrl()) + "parameters/parameter/uploaded-file.txt"), uploadedContent);
        checkUrlNot200AndNotContains(wc, ((build.getUrl()) + "parameters/parameter/./uploaded-file.txt"), uploadedContent);
    }

    @Test
    @Issue("SECURITY-1074")
    public void fileParameter_withDoubleDot() throws Exception {
        // this case was not working even before the patch
        FreeStyleProject p = j.createFreeStyleProject();
        p.addProperty(new ParametersDefinitionProperty(Collections.singletonList(new FileParameterDefinition("..", null))));
        String uploadedContent = "test-content";
        File uploadedFile = tmp.newFile();
        FileUtils.write(uploadedFile, uploadedContent);
        FreeStyleBuild build = p.scheduleBuild2(0, new Cause.UserIdCause(), new ParametersAction(new FileParameterValue("..", uploadedFile, "uploaded-file.txt"))).get();
        Assert.assertThat(build.getResult(), CoreMatchers.equalTo(FAILURE));
        // ensure also the file is not reachable by request
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
        checkUrlNot200AndNotContains(wc, ((build.getUrl()) + "parameters/uploaded-file.txt"), uploadedContent);
        checkUrlNot200AndNotContains(wc, ((build.getUrl()) + "parameters/parameter/../uploaded-file.txt"), uploadedContent);
    }

    @Test
    @Issue("SECURITY-1074")
    public void fileParameter_cannotEraseFile_outsideOfBuildFolder() throws Exception {
        // you can test the behavior before the correction by setting FileParameterValue.ALLOW_FOLDER_TRAVERSAL_OUTSIDE_WORKSPACE to true
        FilePath root = j.jenkins.getRootPath();
        FreeStyleProject p = j.createFreeStyleProject();
        p.addProperty(new ParametersDefinitionProperty(Collections.singletonList(new FileParameterDefinition("../../../../../root-level.txt", null))));
        Assert.assertThat(root.child("root-level.txt").exists(), CoreMatchers.equalTo(false));
        String initialContent = "do-not-erase-me";
        root.child("root-level.txt").write(initialContent, StandardCharsets.UTF_8.name());
        String uploadedContent = "test-content";
        File uploadedFile = tmp.newFile();
        FileUtils.write(uploadedFile, uploadedContent);
        FreeStyleBuild build = p.scheduleBuild2(0, new Cause.UserIdCause(), new ParametersAction(new FileParameterValue("../../../../../root-level.txt", uploadedFile, "uploaded-file.txt"))).get();
        Assert.assertThat(build.getResult(), CoreMatchers.equalTo(FAILURE));
        Assert.assertThat(root.child("root-level.txt").readToString(), CoreMatchers.equalTo(initialContent));
        // ensure also the file is not reachable by request
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
        checkUrlNot200AndNotContains(wc, ((build.getUrl()) + "parameters/parameter/..%2F..%2F..%2F..%2F..%2Froot-level.txt/uploaded-file.txt"), uploadedContent);
    }

    @Test
    public void fileParameter_canStillUse_internalHierarchy() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        p.addProperty(new ParametersDefinitionProperty(Arrays.asList(new FileParameterDefinition("direct-child1.txt", null), new FileParameterDefinition("parent/child2.txt", null))));
        File uploadedFile1 = tmp.newFile();
        FileUtils.write(uploadedFile1, "test1");
        File uploadedFile2 = tmp.newFile();
        FileUtils.write(uploadedFile2, "test2");
        FreeStyleBuild build = j.assertBuildStatusSuccess(p.scheduleBuild2(0, new Cause.UserIdCause(), new ParametersAction(new FileParameterValue("direct-child1.txt", uploadedFile1, "uploaded-file-1.txt"), new FileParameterValue("parent/child2.txt", uploadedFile2, "uploaded-file-2.txt"))));
        // files are correctly saved in the build "fileParameters" folder
        File directChild = new File(build.getRootDir(), ("fileParameters/" + "direct-child1.txt"));
        Assert.assertTrue(directChild.exists());
        File parentChild = new File(build.getRootDir(), ("fileParameters/" + "parent/child2.txt"));
        Assert.assertTrue(parentChild.exists());
        // both are correctly copied inside the workspace
        Assert.assertTrue(build.getWorkspace().child("direct-child1.txt").exists());
        Assert.assertTrue(build.getWorkspace().child("parent").child("child2.txt").exists());
        // and reachable using request
        JenkinsRule.WebClient wc = j.createWebClient();
        HtmlPage workspacePage = wc.goTo(((p.getUrl()) + "ws"));
        String workspaceContent = workspacePage.getWebResponse().getContentAsString();
        Assert.assertThat(workspaceContent, CoreMatchers.allOf(CoreMatchers.containsString("direct-child1.txt"), CoreMatchers.containsString("parent")));
        HtmlPage workspaceParentPage = wc.goTo((((p.getUrl()) + "ws") + "/parent"));
        String workspaceParentContent = workspaceParentPage.getWebResponse().getContentAsString();
        Assert.assertThat(workspaceParentContent, CoreMatchers.containsString("child2.txt"));
    }
}

