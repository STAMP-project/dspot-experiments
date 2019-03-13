/**
 * Copyright 2011 gitblit.com.
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
package com.gitblit.tests;


import com.gitblit.GitBlitException;
import com.gitblit.models.RepositoryModel;
import com.gitblit.models.TeamModel;
import com.gitblit.utils.StringUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.ReceiveCommand;
import org.junit.Assert;
import org.junit.Test;

import static org.eclipse.jgit.transport.ReceiveCommand.Result.REJECTED_NODELETE;


/**
 * Test class for Groovy scripts. Mostly this is to facilitate development.
 *
 * @author James Moger
 */
public class GroovyScriptTest extends GitblitUnitTest {
    private static final AtomicBoolean started = new AtomicBoolean(false);

    @Test
    public void testFogbugz() throws Exception {
        GroovyScriptTest.MockGitblit gitblit = new GroovyScriptTest.MockGitblit();
        GroovyScriptTest.MockLogger logger = new GroovyScriptTest.MockLogger();
        GroovyScriptTest.MockClientLogger clientLogger = new GroovyScriptTest.MockClientLogger();
        List<ReceiveCommand> commands = new ArrayList<ReceiveCommand>();
        commands.add(new ReceiveCommand(ObjectId.fromString("c18877690322dfc6ae3e37bb7f7085a24e94e887"), ObjectId.fromString("3fa7c46d11b11d61f1cbadc6888be5d0eae21969"), "refs/heads/master"));
        commands.add(new ReceiveCommand(ObjectId.fromString("c18877690322dfc6ae3e37bb7f7085a24e94e887"), ObjectId.fromString("3fa7c46d11b11d61f1cbadc6888be5d0eae21969"), "refs/heads/master2"));
        RepositoryModel repository = GitblitUnitTest.repositories().getRepositoryModel("helloworld.git");
        repository.customFields = new HashMap<String, String>();
        repository.customFields.put("fogbugzUrl", "http://bugs.test.com");
        repository.customFields.put("fogbugzRepositoryId", "1");
        repository.customFields.put("fogbugzCommitMessageRegex", "\\s*[Bb][Uu][Gg][(Zz)(Ss)]*\\s*[(IDs)]*\\s*[#:; ]+((\\d+[ ,:;#]*)+)");
        test("fogbugz.groovy", gitblit, logger, clientLogger, commands, repository);
    }

    @Test
    public void testSendHtmlMail() throws Exception {
        GroovyScriptTest.MockGitblit gitblit = new GroovyScriptTest.MockGitblit();
        GroovyScriptTest.MockLogger logger = new GroovyScriptTest.MockLogger();
        GroovyScriptTest.MockClientLogger clientLogger = new GroovyScriptTest.MockClientLogger();
        List<ReceiveCommand> commands = new ArrayList<ReceiveCommand>();
        commands.add(new ReceiveCommand(ObjectId.fromString("c18877690322dfc6ae3e37bb7f7085a24e94e887"), ObjectId.fromString("3fa7c46d11b11d61f1cbadc6888be5d0eae21969"), "refs/heads/master"));
        commands.add(new ReceiveCommand(ObjectId.fromString("c18877690322dfc6ae3e37bb7f7085a24e94e887"), ObjectId.fromString("3fa7c46d11b11d61f1cbadc6888be5d0eae21969"), "refs/heads/master2"));
        RepositoryModel repository = GitblitUnitTest.repositories().getRepositoryModel("helloworld.git");
        repository.mailingLists.add("list@helloworld.git");
        test("sendmail-html.groovy", gitblit, logger, clientLogger, commands, repository);
        Assert.assertEquals(1, logger.messages.size());
        Assert.assertEquals(1, gitblit.messages.size());
        GroovyScriptTest.MockMail m = gitblit.messages.get(0);
        Assert.assertEquals(5, m.toAddresses.size());
        Assert.assertTrue(m.message.contains("BIT"));
        Assert.assertTrue(m.message.contains("<html>"));
    }

    @Test
    public void testSendMail() throws Exception {
        GroovyScriptTest.MockGitblit gitblit = new GroovyScriptTest.MockGitblit();
        GroovyScriptTest.MockLogger logger = new GroovyScriptTest.MockLogger();
        GroovyScriptTest.MockClientLogger clientLogger = new GroovyScriptTest.MockClientLogger();
        List<ReceiveCommand> commands = new ArrayList<ReceiveCommand>();
        commands.add(new ReceiveCommand(ObjectId.fromString("c18877690322dfc6ae3e37bb7f7085a24e94e887"), ObjectId.fromString("3fa7c46d11b11d61f1cbadc6888be5d0eae21969"), "refs/heads/master"));
        commands.add(new ReceiveCommand(ObjectId.fromString("c18877690322dfc6ae3e37bb7f7085a24e94e887"), ObjectId.fromString("3fa7c46d11b11d61f1cbadc6888be5d0eae21969"), "refs/heads/master2"));
        RepositoryModel repository = GitblitUnitTest.repositories().getRepositoryModel("helloworld.git");
        repository.mailingLists.add("list@helloworld.git");
        test("sendmail.groovy", gitblit, logger, clientLogger, commands, repository);
        Assert.assertEquals(1, logger.messages.size());
        Assert.assertEquals(1, gitblit.messages.size());
        GroovyScriptTest.MockMail m = gitblit.messages.get(0);
        Assert.assertEquals(5, m.toAddresses.size());
        Assert.assertTrue(m.message.contains("BIT"));
    }

    @Test
    public void testProtectRefsCreateBranch() throws Exception {
        GroovyScriptTest.MockGitblit gitblit = new GroovyScriptTest.MockGitblit();
        GroovyScriptTest.MockLogger logger = new GroovyScriptTest.MockLogger();
        GroovyScriptTest.MockClientLogger clientLogger = new GroovyScriptTest.MockClientLogger();
        List<ReceiveCommand> commands = new ArrayList<ReceiveCommand>();
        commands.add(new ReceiveCommand(ObjectId.zeroId(), ObjectId.fromString("3fa7c46d11b11d61f1cbadc6888be5d0eae21969"), "refs/heads/master"));
        RepositoryModel repository = new RepositoryModel("ex@mple.git", "", "admin", new Date());
        test("protect-refs.groovy", gitblit, logger, clientLogger, commands, repository);
    }

    @Test
    public void testProtectRefsCreateTag() throws Exception {
        GroovyScriptTest.MockGitblit gitblit = new GroovyScriptTest.MockGitblit();
        GroovyScriptTest.MockLogger logger = new GroovyScriptTest.MockLogger();
        GroovyScriptTest.MockClientLogger clientLogger = new GroovyScriptTest.MockClientLogger();
        List<ReceiveCommand> commands = new ArrayList<ReceiveCommand>();
        commands.add(new ReceiveCommand(ObjectId.zeroId(), ObjectId.fromString("3fa7c46d11b11d61f1cbadc6888be5d0eae21969"), "refs/tags/v1.0"));
        RepositoryModel repository = new RepositoryModel("ex@mple.git", "", "admin", new Date());
        test("protect-refs.groovy", gitblit, logger, clientLogger, commands, repository);
        Assert.assertEquals(0, logger.messages.size());
    }

    @Test
    public void testProtectRefsFastForward() throws Exception {
        GroovyScriptTest.MockGitblit gitblit = new GroovyScriptTest.MockGitblit();
        GroovyScriptTest.MockLogger logger = new GroovyScriptTest.MockLogger();
        GroovyScriptTest.MockClientLogger clientLogger = new GroovyScriptTest.MockClientLogger();
        List<ReceiveCommand> commands = new ArrayList<ReceiveCommand>();
        commands.add(new ReceiveCommand(ObjectId.fromString("c18877690322dfc6ae3e37bb7f7085a24e94e887"), ObjectId.fromString("3fa7c46d11b11d61f1cbadc6888be5d0eae21969"), "refs/heads/master"));
        RepositoryModel repository = new RepositoryModel("ex@mple.git", "", "admin", new Date());
        test("protect-refs.groovy", gitblit, logger, clientLogger, commands, repository);
        Assert.assertEquals(0, logger.messages.size());
    }

    @Test
    public void testProtectRefsDeleteMasterBranch() throws Exception {
        GroovyScriptTest.MockGitblit gitblit = new GroovyScriptTest.MockGitblit();
        GroovyScriptTest.MockLogger logger = new GroovyScriptTest.MockLogger();
        GroovyScriptTest.MockClientLogger clientLogger = new GroovyScriptTest.MockClientLogger();
        List<ReceiveCommand> commands = new ArrayList<ReceiveCommand>();
        ReceiveCommand command = new ReceiveCommand(ObjectId.fromString("3fa7c46d11b11d61f1cbadc6888be5d0eae21969"), ObjectId.zeroId(), "refs/heads/master");
        commands.add(command);
        RepositoryModel repository = new RepositoryModel("ex@mple.git", "", "admin", new Date());
        test("protect-refs.groovy", gitblit, logger, clientLogger, commands, repository);
        Assert.assertEquals(REJECTED_NODELETE, command.getResult());
        Assert.assertEquals(0, logger.messages.size());
    }

    @Test
    public void testProtectRefsDeleteOtherBranch() throws Exception {
        GroovyScriptTest.MockGitblit gitblit = new GroovyScriptTest.MockGitblit();
        GroovyScriptTest.MockLogger logger = new GroovyScriptTest.MockLogger();
        GroovyScriptTest.MockClientLogger clientLogger = new GroovyScriptTest.MockClientLogger();
        List<ReceiveCommand> commands = new ArrayList<ReceiveCommand>();
        commands.add(new ReceiveCommand(ObjectId.fromString("3fa7c46d11b11d61f1cbadc6888be5d0eae21969"), ObjectId.zeroId(), "refs/heads/other"));
        RepositoryModel repository = new RepositoryModel("ex@mple.git", "", "admin", new Date());
        test("protect-refs.groovy", gitblit, logger, clientLogger, commands, repository);
        Assert.assertEquals(0, logger.messages.size());
    }

    @Test
    public void testProtectRefsDeleteTag() throws Exception {
        GroovyScriptTest.MockGitblit gitblit = new GroovyScriptTest.MockGitblit();
        GroovyScriptTest.MockLogger logger = new GroovyScriptTest.MockLogger();
        GroovyScriptTest.MockClientLogger clientLogger = new GroovyScriptTest.MockClientLogger();
        List<ReceiveCommand> commands = new ArrayList<ReceiveCommand>();
        ReceiveCommand command = new ReceiveCommand(ObjectId.fromString("3fa7c46d11b11d61f1cbadc6888be5d0eae21969"), ObjectId.zeroId(), "refs/tags/v1.0");
        commands.add(command);
        RepositoryModel repository = new RepositoryModel("ex@mple.git", "", "admin", new Date());
        test("protect-refs.groovy", gitblit, logger, clientLogger, commands, repository);
        Assert.assertEquals(REJECTED_NODELETE, command.getResult());
        Assert.assertEquals(0, logger.messages.size());
    }

    @Test
    public void testBlockPush() throws Exception {
        GroovyScriptTest.MockGitblit gitblit = new GroovyScriptTest.MockGitblit();
        GroovyScriptTest.MockLogger logger = new GroovyScriptTest.MockLogger();
        GroovyScriptTest.MockClientLogger clientLogger = new GroovyScriptTest.MockClientLogger();
        List<ReceiveCommand> commands = new ArrayList<ReceiveCommand>();
        commands.add(new ReceiveCommand(ObjectId.fromString("c18877690322dfc6ae3e37bb7f7085a24e94e887"), ObjectId.fromString("3fa7c46d11b11d61f1cbadc6888be5d0eae21969"), "refs/heads/master"));
        RepositoryModel repository = new RepositoryModel("ex@mple.git", "", "admin", new Date());
        try {
            test("blockpush.groovy", gitblit, logger, clientLogger, commands, repository);
            Assert.assertTrue("blockpush should have failed!", false);
        } catch (GitBlitException e) {
            Assert.assertTrue(e.getMessage().contains("failed"));
        }
    }

    @Test
    public void testClientLogging() throws Exception {
        GroovyScriptTest.MockGitblit gitblit = new GroovyScriptTest.MockGitblit();
        GroovyScriptTest.MockLogger logger = new GroovyScriptTest.MockLogger();
        GroovyScriptTest.MockClientLogger clientLogger = new GroovyScriptTest.MockClientLogger();
        List<ReceiveCommand> commands = new ArrayList<ReceiveCommand>();
        commands.add(new ReceiveCommand(ObjectId.fromString("c18877690322dfc6ae3e37bb7f7085a24e94e887"), ObjectId.fromString("3fa7c46d11b11d61f1cbadc6888be5d0eae21969"), "refs/heads/master"));
        RepositoryModel repository = new RepositoryModel("ex@mple.git", "", "admin", new Date());
        File groovyDir = GitblitUnitTest.repositories().getHooksFolder();
        File tempScript = File.createTempFile("testClientLogging", "groovy", groovyDir);
        tempScript.deleteOnExit();
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempScript));
        writer.write("clientLogger.info(\'this is a test message\')\n");
        writer.flush();
        writer.close();
        test(tempScript.getName(), gitblit, logger, clientLogger, commands, repository);
        Assert.assertTrue("Message Missing", clientLogger.messages.contains("this is a test message"));
    }

    class MockGitblit {
        List<GroovyScriptTest.MockMail> messages = new ArrayList<GroovyScriptTest.MockMail>();

        public Repository getRepository(String name) throws Exception {
            return GitBlitSuite.getHelloworldRepository();
        }

        public List<String> getStrings(String key) {
            return Arrays.asList("alpha@aaa.com", "beta@bee.com", "gamma@see.com");
        }

        public List<String> getRepositoryTeams(RepositoryModel repository) {
            return Arrays.asList("testteam");
        }

        public TeamModel getTeamModel(String name) {
            TeamModel model = new TeamModel(name);
            model.mailingLists.add((("list@" + name) + ".com"));
            return model;
        }

        public String getString(String key, String dv) {
            return dv;
        }

        public boolean getBoolean(String key, boolean dv) {
            return dv;
        }

        public int getInteger(String key, int defaultValue) {
            return defaultValue;
        }

        public void sendMail(String subject, String message, Collection<String> toAddresses) {
            messages.add(new GroovyScriptTest.MockMail(subject, message, toAddresses));
        }

        public void sendHtmlMail(String subject, String message, Collection<String> toAddresses) {
            messages.add(new GroovyScriptTest.MockMail(subject, message, toAddresses));
        }
    }

    class MockLogger {
        List<String> messages = new ArrayList<String>();

        public void info(String message) {
            messages.add(message);
        }
    }

    class MockClientLogger {
        List<String> messages = new ArrayList<String>();

        public void info(String message) {
            messages.add(message);
        }

        public void error(String message) {
            messages.add(message);
        }

        public void error(String message, Throwable t) {
            PrintWriter writer = new PrintWriter(new StringWriter());
            if (!(StringUtils.isEmpty(message))) {
                writer.append(message);
                writer.append('\n');
            }
            t.printStackTrace(writer);
            messages.add(writer.toString());
        }
    }

    class MockMail {
        final Collection<String> toAddresses;

        final String subject;

        final String message;

        MockMail(String subject, String message, Collection<String> toAddresses) {
            this.subject = subject;
            this.message = message;
            this.toAddresses = toAddresses;
        }

        @Override
        public String toString() {
            return ((((StringUtils.flattenStrings(toAddresses, ", ")) + "\n\n") + (subject)) + "\n\n") + (message);
        }
    }
}

