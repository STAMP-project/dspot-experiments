package com.github.dockerjava.core;


import com.github.dockerjava.core.util.CompressArchiveUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class CompressArchiveUtilTest {
    @Test
    public void testExecutableFlagIsPreserved() throws Exception {
        File executableFile = createExecutableFile();
        File archive = CompressArchiveUtil.archiveTARFiles(executableFile.getParentFile(), Arrays.asList(executableFile), "archive");
        File expectedFile = extractFileByName(archive, "executableFile.sh", "executableFile.sh.result");
        MatcherAssert.assertThat("should be executable", expectedFile.canExecute());
        expectedFile.delete();
        archive.delete();
    }

    @Test
    public void testSymbolicLinkDir() throws IOException {
        Path uploadDir = Files.createTempDirectory("upload");
        Path linkTarget = Files.createTempDirectory("ink-target");
        Path tmpFile = Files.createTempFile(linkTarget, "link-dir", "rand");
        Files.createSymbolicLink(uploadDir.resolve("link-folder"), linkTarget);
        Path tarGzFile = Files.createTempFile("docker-java", ".tar.gz");
        // follow link only works for childrenOnly=false
        CompressArchiveUtil.tar(uploadDir, tarGzFile, true, false);
        File expectedFile = extractFileByName(tarGzFile.toFile(), tmpFile.toFile().getName(), "foo1");
        MatcherAssert.assertThat(expectedFile.canRead(), CoreMatchers.is(true));
        uploadDir.toFile().delete();
        linkTarget.toFile().delete();
        tarGzFile.toFile().delete();
    }

    @Test
    public void testSymbolicLinkFile() throws IOException {
        Path uploadDir = Files.createTempDirectory("upload");
        Path tmpFile = Files.createTempFile("src", "");
        Files.createSymbolicLink(uploadDir.resolve("link-file"), tmpFile);
        Path tarGzFile = Files.createTempFile("docker-java", ".tar.gz");
        boolean childrenOnly = false;
        CompressArchiveUtil.tar(uploadDir, tarGzFile, true, childrenOnly);
        File expectedFile = extractFileByName(tarGzFile.toFile(), "link-file", "foo1");
        MatcherAssert.assertThat(expectedFile.canRead(), CoreMatchers.is(true));
        childrenOnly = true;
        CompressArchiveUtil.tar(uploadDir, tarGzFile, true, childrenOnly);
        extractFileByName(tarGzFile.toFile(), "link-file", "foo1");
        MatcherAssert.assertThat(expectedFile.canRead(), CoreMatchers.is(true));
        uploadDir.toFile().delete();
        tmpFile.toFile().delete();
        tarGzFile.toFile().delete();
    }
}

