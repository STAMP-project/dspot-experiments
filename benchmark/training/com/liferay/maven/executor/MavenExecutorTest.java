/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.maven.executor;


import MavenExecutor.Result;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 *
 *
 * @author Andrea Di Giorgi
 */
public class MavenExecutorTest {
    @Test
    public void testBefore() throws Throwable {
        _mavenExecutor.before();
        Path mavenHomeDirPath = _mavenExecutor.getMavenHomeDirPath();
        Assert.assertTrue(Files.isExecutable(mavenHomeDirPath.resolve("bin/mvn")));
        Assert.assertTrue(Files.isRegularFile(mavenHomeDirPath.resolve("bin/mvn.cmd")));
    }

    @Test
    public void testExecuteGoal() throws Throwable {
        File projectDir = temporaryFolder.getRoot();
        Path projectDirPath = projectDir.toPath();
        Files.copy(MavenExecutorTest.class.getResourceAsStream("dependencies/echo.xml"), projectDirPath.resolve("pom.xml"));
        testBefore();
        MavenExecutor.Result result = _mavenExecutor.execute(projectDir, "initialize");
        Assert.assertEquals(result.output, 0, result.exitCode);
        Assert.assertTrue(result.output, result.output.contains("This is the text which will be printed out."));
    }

    @Test
    public void testExecuteVersion() throws Throwable {
        testBefore();
        MavenExecutor.Result result = _mavenExecutor.execute(temporaryFolder.getRoot(), "--version");
        Assert.assertEquals(result.output, 0, result.exitCode);
        Assert.assertTrue(result.output, result.output.startsWith("Apache Maven "));
    }

    @Test(expected = IllegalStateException.class)
    public void testExecuteWithoutBefore() throws Exception {
        _mavenExecutor.execute(temporaryFolder.getRoot(), "--version");
    }

    @Test(expected = IllegalStateException.class)
    public void testGetMavenHomeDirPathWithoutBefore() {
        _mavenExecutor.getMavenHomeDirPath();
    }

    @Test
    public void testProxy() throws Throwable {
        File projectDir = temporaryFolder.getRoot();
        Path projectDirPath = projectDir.toPath();
        Files.copy(MavenExecutorTest.class.getResourceAsStream("dependencies/wrong-plugin.xml"), projectDirPath.resolve("pom.xml"));
        int fakeProxyPort = MavenExecutorTest._getFakePort(0);
        int fakeRepositoryPort = MavenExecutorTest._getFakePort(fakeProxyPort);
        String proxyHost = MavenExecutorTest._setSystemProperty("http.proxyHost", "127.0.0.1");
        String proxyPort = MavenExecutorTest._setSystemProperty("http.proxyPort", String.valueOf(fakeProxyPort));
        String repositoryUrl = MavenExecutorTest._setSystemProperty("repository.url", ("http://127.0.0.1:" + fakeRepositoryPort));
        try {
            testBefore();
            MavenExecutor.Result result = _mavenExecutor.execute(projectDir, "initialize");
            Assert.assertNotEquals(result.output, 0, result.exitCode);
            Assert.assertTrue(result.output, result.output.contains(("127.0.0.1:" + fakeProxyPort)));
        } finally {
            MavenExecutorTest._setSystemProperty("http.proxyHost", proxyHost);
            MavenExecutorTest._setSystemProperty("http.proxyPort", proxyPort);
            MavenExecutorTest._setSystemProperty("repository.url", repositoryUrl);
        }
    }

    @Test
    public void testRepositoryUrl() throws Throwable {
        File projectDir = temporaryFolder.getRoot();
        Path projectDirPath = projectDir.toPath();
        Files.copy(MavenExecutorTest.class.getResourceAsStream("dependencies/wrong-plugin.xml"), projectDirPath.resolve("pom.xml"));
        int fakeRepositoryPort = MavenExecutorTest._getFakePort(0);
        String repositoryUrl = MavenExecutorTest._setSystemProperty("repository.url", ("http://127.0.0.1:" + fakeRepositoryPort));
        try {
            testBefore();
            MavenExecutor.Result result = _mavenExecutor.execute(projectDir, "initialize");
            Assert.assertNotEquals(result.output, 0, result.exitCode);
            Assert.assertTrue(result.output, result.output.contains(("127.0.0.1:" + fakeRepositoryPort)));
        } finally {
            MavenExecutorTest._setSystemProperty("repository.url", repositoryUrl);
        }
    }

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final int _FAKE_PORT_RETRIES = 20;

    private static String _mavenDistributionFileName;

    private MavenExecutor _mavenExecutor;
}

