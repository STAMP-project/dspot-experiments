/**
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.api.git;


import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link GitConfigurationChecker}.
 *
 * @author Artem Zatsarynnyi
 */
public class GitConfigurationCheckerTest {
    private static final String GITIGNORE_FILE_CONTENT = "\n" + (("# Codenvy files\n" + ".che/\n") + ".vfs/\n");

    private GitConfigurationChecker checker;

    private static String excludesfilePropertyContent;

    private static Path globalGitconfigFilePath;

    private static Path gitignoreFilePath;

    private static Path existingGitignoreFilePath;

    @Test
    public void testWhenNoGitconfigFile() throws Exception {
        GitConfigurationCheckerTest.excludesfilePropertyContent = String.format(("\n" + (("[core]\n" + "\texcludesfile = %s") + "\n")), GitConfigurationCheckerTest.gitignoreFilePath);
        checker.start();
        Assert.assertTrue("New global .gitconfig file should be created in case it doesn't exist.", Files.exists(GitConfigurationCheckerTest.globalGitconfigFilePath));
        Assert.assertTrue("New global .gitignore file should be created.", Files.exists(GitConfigurationCheckerTest.gitignoreFilePath));
        Assert.assertEquals(GitConfigurationCheckerTest.excludesfilePropertyContent, new String(Files.readAllBytes(GitConfigurationCheckerTest.globalGitconfigFilePath)));
        Assert.assertEquals(GitConfigurationCheckerTest.GITIGNORE_FILE_CONTENT, new String(Files.readAllBytes(GitConfigurationCheckerTest.gitignoreFilePath)));
    }

    @Test
    public void testWhenNoExcludesfilePropertyInGitconfigFile() throws Exception {
        GitConfigurationCheckerTest.excludesfilePropertyContent = String.format(("\n" + (("[core]\n" + "\texcludesfile = %s") + "\n")), GitConfigurationCheckerTest.gitignoreFilePath);
        GitConfigurationCheckerTest.createGitconfigFile(false);
        final String existingGitconfigFileContent = new String(Files.readAllBytes(GitConfigurationCheckerTest.globalGitconfigFilePath));
        checker.start();
        Assert.assertTrue(Files.exists(GitConfigurationCheckerTest.globalGitconfigFilePath));
        Assert.assertEquals("'core.excludesfile' property should be appended to the existing global .gitconfig file", (existingGitconfigFileContent + (GitConfigurationCheckerTest.excludesfilePropertyContent)), new String(Files.readAllBytes(GitConfigurationCheckerTest.globalGitconfigFilePath)));
        Assert.assertEquals(GitConfigurationCheckerTest.GITIGNORE_FILE_CONTENT, new String(Files.readAllBytes(GitConfigurationCheckerTest.gitignoreFilePath)));
    }

    @Test
    public void testWithExcludesfilePropertyInGitconfigFile() throws Exception {
        GitConfigurationCheckerTest.excludesfilePropertyContent = String.format(("\n" + (("[core]\n" + "\texcludesfile = %s") + "\n")), GitConfigurationCheckerTest.existingGitignoreFilePath);
        GitConfigurationCheckerTest.createGitconfigFile(true);
        final byte[] existingGitconfigFileContent = Files.readAllBytes(GitConfigurationCheckerTest.globalGitconfigFilePath);
        final String existingGitignoreFileContent = new String(Files.readAllBytes(GitConfigurationCheckerTest.existingGitignoreFilePath));
        checker.start();
        Assert.assertArrayEquals("Existing global .gitconfig file shouldn't be touched in case it already contains 'core.excludesfile' property.", existingGitconfigFileContent, Files.readAllBytes(GitConfigurationCheckerTest.globalGitconfigFilePath));
        Assert.assertFalse("New .gitignore file shouldn't be created in case existing global .gitconfig file already contains 'core.excludesfile' property.", Files.exists(GitConfigurationCheckerTest.gitignoreFilePath));
        Assert.assertEquals("New content should be appended to the existing global .gitignore file.", (existingGitignoreFileContent + (GitConfigurationCheckerTest.GITIGNORE_FILE_CONTENT)), new String(Files.readAllBytes(GitConfigurationCheckerTest.existingGitignoreFilePath)));
    }
}

