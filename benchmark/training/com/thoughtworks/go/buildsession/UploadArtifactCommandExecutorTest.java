/**
 * Copyright 2016 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.buildsession;


import com.thoughtworks.go.domain.ArtifactsRepositoryStub;
import com.thoughtworks.go.domain.BuildCommand;
import com.thoughtworks.go.domain.JobResult;
import com.thoughtworks.go.util.FileUtil;
import java.io.File;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class UploadArtifactCommandExecutorTest extends BuildSessionBasedTestCase {
    @Test
    public void uploadSingleFileArtifact() throws Exception {
        File targetFile = new File(sandbox, "foo");
        Assert.assertTrue(targetFile.createNewFile());
        runBuild(BuildCommand.uploadArtifact("foo", "foo-dest", false).setWorkingDirectory(sandbox.getPath()), JobResult.Passed);
        Assert.assertThat(artifactsRepository.getFileUploaded().size(), Matchers.is(1));
        Assert.assertThat(artifactsRepository.getFileUploaded().get(0).file, Matchers.is(targetFile));
        Assert.assertThat(artifactsRepository.getFileUploaded().get(0).destPath, Matchers.is("foo-dest"));
        Assert.assertThat(artifactsRepository.getFileUploaded().get(0).buildId, Matchers.is("build1"));
    }

    @Test
    public void uploadMultipleArtifact() throws Exception {
        File dir = new File(sandbox, "foo");
        Assert.assertTrue(dir.mkdirs());
        Assert.assertTrue(new File(dir, "bar").createNewFile());
        Assert.assertTrue(new File(dir, "baz").createNewFile());
        runBuild(BuildCommand.uploadArtifact("foo/*", "foo-dest", false), JobResult.Passed);
        Assert.assertThat(artifactsRepository.getFileUploaded().size(), Matchers.is(2));
        Assert.assertThat(artifactsRepository.getFileUploaded().get(0).file, Matchers.is(new File(dir, "bar")));
        Assert.assertThat(artifactsRepository.getFileUploaded().get(1).file, Matchers.is(new File(dir, "baz")));
    }

    @Test
    public void shouldUploadMatchedFolder() throws Exception {
        FileUtil.createFilesByPath(sandbox, "logs/pic/fail.png", "logs/pic/pass.png", "README");
        runBuild(BuildCommand.uploadArtifact("**/*", "mypic", false), JobResult.Passed);
        Assert.assertThat(artifactsRepository.isFileUploaded(new File(sandbox, "logs/pic"), "mypic/logs"), Matchers.is(true));
        Assert.assertThat(artifactsRepository.isFileUploaded(new File(sandbox, "README"), "mypic"), Matchers.is(true));
        Assert.assertThat(artifactsRepository.isFileUploaded(new File(sandbox, "logs/pic/fail.png"), "mypic/logs/pic"), Matchers.is(false));
        Assert.assertThat(artifactsRepository.isFileUploaded(new File(sandbox, "logs/pic/pass.png"), "mypic/logs/pic"), Matchers.is(false));
    }

    @Test
    public void shouldNotUploadFileContainingFolderAgain() throws Exception {
        FileUtil.createFilesByPath(sandbox, "logs/pic/fail.png", "logs/pic/pass.png", "README");
        runBuild(BuildCommand.uploadArtifact("logs/pic/*.png", "mypic", false), JobResult.Passed);
        Assert.assertThat(artifactsRepository.isFileUploaded(new File(sandbox, "logs/pic/pass.png"), "mypic"), Matchers.is(true));
        Assert.assertThat(artifactsRepository.isFileUploaded(new File(sandbox, "logs/pic/fail.png"), "mypic"), Matchers.is(true));
        Assert.assertThat(artifactsRepository.isFileUploaded(new File(sandbox, "logs/pic"), "mypic"), Matchers.is(false));
    }

    @Test
    public void shouldUploadFolderWhenMatchedWithWildCards() throws Exception {
        FileUtil.createFilesByPath(sandbox, "logs/pic-1/fail.png", "logs/pic-1/pass.png", "logs/pic-2/cancel.png", "logs/pic-2/complete.png", "README");
        runBuild(BuildCommand.uploadArtifact("logs/pic-*", "mypic", false), JobResult.Passed);
        Assert.assertThat(artifactsRepository.isFileUploaded(new File(sandbox, "logs/pic-1/pass.png"), "mypic"), Matchers.is(false));
        Assert.assertThat(artifactsRepository.isFileUploaded(new File(sandbox, "logs/pic-1/fail.png"), "mypic"), Matchers.is(false));
        Assert.assertThat(artifactsRepository.isFileUploaded(new File(sandbox, "logs/pic-2/cancel.png"), "mypic"), Matchers.is(false));
        Assert.assertThat(artifactsRepository.isFileUploaded(new File(sandbox, "logs/pic-2/complete.png"), "mypic"), Matchers.is(false));
        Assert.assertThat(artifactsRepository.isFileUploaded(new File(sandbox, "logs/pic-1"), "mypic"), Matchers.is(true));
        Assert.assertThat(artifactsRepository.isFileUploaded(new File(sandbox, "logs/pic-2"), "mypic"), Matchers.is(true));
    }

    @Test
    public void shouldUploadFolderWhenDirectMatch() throws Exception {
        FileUtil.createFilesByPath(sandbox, "logs/pic-1/fail.png", "logs/pic-1/pass.png", "logs/pic-2/cancel.png", "logs/pic-2/complete.png", "README");
        runBuild(BuildCommand.uploadArtifact("logs/pic-1", "mypic", false), JobResult.Passed);
        Assert.assertThat(artifactsRepository.isFileUploaded(new File(sandbox, "logs/pic-1"), "mypic"), Matchers.is(true));
    }

    @Test
    public void shouldFailBuildWhenNothingMatched() throws Exception {
        FileUtil.createFilesByPath(sandbox, "logs/pic-1/fail.png", "logs/pic-1/pass.png", "logs/pic-2/cancel.png", "logs/pic-2/complete.png", "README");
        runBuild(BuildCommand.uploadArtifact("logs/picture", "mypic", false), JobResult.Failed);
        Assert.assertThat(artifactsRepository.getFileUploaded().size(), Matchers.is(0));
        Assert.assertThat(console.output(), printedRuleDoesNotMatchFailure(sandbox.getPath(), "logs/picture"));
    }

    @Test
    public void shouldFailBuildWhenSourceDirectoryDoesNotExist() throws Exception {
        FileUtil.createFilesByPath(sandbox, "logs/pic-1/fail.png", "logs/pic-1/pass.png", "logs/pic-2/cancel.png", "logs/pic-2/complete.png", "README");
        runBuild(BuildCommand.uploadArtifact("not-Exist-Folder", "mypic", false), JobResult.Failed);
        Assert.assertThat(console.output(), printedRuleDoesNotMatchFailure(sandbox.getPath(), "not-Exist-Folder"));
    }

    @Test
    public void shouldFailBuildWhenNothingMatchedUsingMatcherStartDotStart() throws Exception {
        runBuild(BuildCommand.uploadArtifact("target/pkg/*.*", "MYDEST", false), JobResult.Failed);
        Assert.assertThat(console.output(), printedRuleDoesNotMatchFailure(sandbox.getPath(), "target/pkg/*.*"));
    }

    @Test
    public void shouldNotFailBuildWhenNothingMatchedWhenIngnoreUnmatchError() throws Exception {
        runBuild(BuildCommand.uploadArtifact("target/pkg/*.*", "MYDEST", true), JobResult.Passed);
        Assert.assertThat(console.output(), printedRuleDoesNotMatchFailure(sandbox.getPath(), "target/pkg/*.*"));
    }

    @Test
    public void shouldFailBuildWhenUploadErrorHappened() throws Exception {
        FileUtil.createFilesByPath(sandbox, "logs/pic/pass.png", "logs/pic-1/pass.png");
        artifactsRepository.setUploadError(new RuntimeException("upload failed!!"));
        runBuild(BuildCommand.uploadArtifact("**/*.png", "mypic", false), JobResult.Failed);
        Assert.assertThat(artifactsRepository.getFileUploaded().size(), Matchers.is(0));
    }

    @Test
    public void shouldStillFailBuildWhenIgnoreUnmatchErrorButUploadErrorHappened() throws Exception {
        FileUtil.createFilesByPath(sandbox, "logs/pic/pass.png", "logs/pic-1/pass.png");
        artifactsRepository.setUploadError(new RuntimeException("upload failed!!"));
        runBuild(BuildCommand.uploadArtifact("**/*.png", "mypic", true), JobResult.Failed);
        Assert.assertThat(artifactsRepository.getFileUploaded().size(), Matchers.is(0));
    }
}

