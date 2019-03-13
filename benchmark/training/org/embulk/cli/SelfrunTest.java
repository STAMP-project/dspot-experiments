package org.embulk.cli;


import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class SelfrunTest {
    private static File testSelfrunFile;

    @Test
    public void testNoArgument() throws Exception {
        List<String> args = execute();
        Assert.assertEquals(Arrays.asList("-XX:+AggressiveOpts", "-XX:+TieredCompilation", "-XX:TieredStopAtLevel=1", "-Xverify:none", "-jar", SelfrunTest.testSelfrunFile.getAbsolutePath()), args);
    }

    @Test
    public void testArguments() throws Exception {
        List<String> args = execute("a1", "a2", "\"a3=v3\"");
        Assert.assertEquals(Arrays.asList("-XX:+AggressiveOpts", "-XX:+TieredCompilation", "-XX:TieredStopAtLevel=1", "-Xverify:none", "-jar", SelfrunTest.testSelfrunFile.getAbsolutePath(), "a1", "a2", "a3=v3"), args);
    }

    @Test
    public void testRun() throws Exception {
        List<String> args = execute("run", "a1");
        Assert.assertEquals(Arrays.asList("-XX:+AggressiveOpts", "-XX:+UseConcMarkSweepGC", "-jar", SelfrunTest.testSelfrunFile.getAbsolutePath(), "run", "a1"), args);
    }

    @Test
    public void testJpO() throws Exception {
        List<String> args = execute("-J+O", "a1", "a2");
        Assert.assertEquals(Arrays.asList("-XX:+AggressiveOpts", "-XX:+UseConcMarkSweepGC", "-jar", SelfrunTest.testSelfrunFile.getAbsolutePath(), "a1", "a2"), args);
    }

    @Test
    public void testJmO() throws Exception {
        List<String> args = execute("-J-O", "a1", "a2");
        Assert.assertEquals(Arrays.asList("-XX:+AggressiveOpts", "-XX:+TieredCompilation", "-XX:TieredStopAtLevel=1", "-Xverify:none", "-jar", SelfrunTest.testSelfrunFile.getAbsolutePath(), "a1", "a2"), args);
    }

    @Test
    public void testR1() throws Exception {
        List<String> args = execute("-Rr1", "a1", "a2");
        Assert.assertEquals(Arrays.asList("-XX:+AggressiveOpts", "-XX:+TieredCompilation", "-XX:TieredStopAtLevel=1", "-Xverify:none", "-jar", SelfrunTest.testSelfrunFile.getAbsolutePath(), "-Rr1", "a1", "a2"), args);
    }

    @Test
    public void testR2() throws Exception {
        List<String> args = execute("\"-Rr1=v1\"", "\"-Rr2=v2\"", "a1", "a2");
        Assert.assertEquals(Arrays.asList("-XX:+AggressiveOpts", "-XX:+TieredCompilation", "-XX:TieredStopAtLevel=1", "-Xverify:none", "-jar", SelfrunTest.testSelfrunFile.getAbsolutePath(), "-Rr1=v1", "-Rr2=v2", "a1", "a2"), args);
    }

    @Test
    public void testRRun() throws Exception {
        List<String> args = execute("-Rr1", "run", "a1");
        Assert.assertEquals(Arrays.asList("-XX:+AggressiveOpts", "-XX:+UseConcMarkSweepGC", "-jar", SelfrunTest.testSelfrunFile.getAbsolutePath(), "-Rr1", "run", "a1"), args);
    }

    @Test
    public void testJ1() throws Exception {
        List<String> args = execute("-J-Dj1", "a1", "a2");
        Assert.assertEquals(Arrays.asList("-XX:+AggressiveOpts", "-XX:+TieredCompilation", "-XX:TieredStopAtLevel=1", "-Xverify:none", "-Dj1", "-jar", SelfrunTest.testSelfrunFile.getAbsolutePath(), "a1", "a2"), args);
    }

    @Test
    public void testJ2() throws Exception {
        List<String> args = execute("\"-J-Dj1=v1\"", "\"-J-Dj2=v2\"", "a1", "a2");
        Assert.assertEquals(Arrays.asList("-XX:+AggressiveOpts", "-XX:+TieredCompilation", "-XX:TieredStopAtLevel=1", "-Xverify:none", "-Dj1=v1", "-Dj2=v2", "-jar", SelfrunTest.testSelfrunFile.getAbsolutePath(), "a1", "a2"), args);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testJR() throws Exception {
        List<String> args = execute("-Jj1", "-Rr1", "a1", "a2");
        Assert.assertEquals(Arrays.asList("-XX:+AggressiveOpts", "-XX:+TieredCompilation", "-XX:TieredStopAtLevel=1", "-Xverify:none", "j1", "-jar", SelfrunTest.testSelfrunFile.getAbsolutePath(), "-Rr1", "a1", "a2"), args);
    }

    @Test
    public void testJFile() throws Exception {
        File javaArgsFile = new File(SelfrunTest.testSelfrunFile.getParentFile(), "java_args.txt");
        FileSystem fileSystem = FileSystems.getDefault();
        Files.write(fileSystem.getPath(javaArgsFile.getAbsolutePath()), "j1 j2 j3".getBytes(Charset.defaultCharset()), StandardOpenOption.CREATE);
        List<String> args = execute("-J", javaArgsFile.getAbsolutePath(), "a1", "a2");
        Assert.assertEquals(Arrays.asList("-XX:+AggressiveOpts", "-XX:+TieredCompilation", "-XX:TieredStopAtLevel=1", "-Xverify:none", "j1", "j2", "j3", "-jar", SelfrunTest.testSelfrunFile.getAbsolutePath(), "a1", "a2"), args);
    }
}

