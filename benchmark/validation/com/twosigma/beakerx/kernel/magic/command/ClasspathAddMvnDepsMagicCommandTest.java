/**
 * Copyright 2017 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx.kernel.magic.command;


import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.MagicCommandConfigurationMock;
import com.twosigma.beakerx.evaluator.EvaluatorResultTestWatcher;
import com.twosigma.beakerx.evaluator.EvaluatorTest;
import com.twosigma.beakerx.kernel.Code;
import com.twosigma.beakerx.kernel.magic.command.functionality.ClasspathAddMvnMagicCommand;
import com.twosigma.beakerx.kernel.magic.command.functionality.ClasspathResetMagicCommand;
import com.twosigma.beakerx.kernel.magic.command.outcome.MagicCommandOutcomeItem;
import com.twosigma.beakerx.kernel.msg.JupyterMessages;
import com.twosigma.beakerx.message.Message;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ClasspathAddMvnDepsMagicCommandTest {
    private static final String SRC_TEST_RESOURCES_TEST_MVN_CACHE = "src/test/resources/testMvnCache";

    public static final String BUILD_PATH = "build";

    public static final String TEST_MVN_CACHE = (ClasspathAddMvnDepsMagicCommandTest.BUILD_PATH) + "/testMvnCache";

    public static final ArrayList<MagicCommandOutcomeItem> NO_ERRORS = new ArrayList<>();

    private static KernelTest kernel;

    private static EvaluatorTest evaluator;

    private MagicCommandConfigurationMock configuration = new MagicCommandConfigurationMock();

    @Test
    public void handleClasspathAddMvnDep() throws Exception {
        // given
        String allCode = (ClasspathAddMvnMagicCommand.CLASSPATH_ADD_MVN) + " org.slf4j slf4j-api 1.7.5";
        handleClasspathAddMvnDep(allCode, "slf4j-api-1.7.5.jar");
    }

    @Test
    public void handleClasspathAddMvnDepUsingGradleSyntax() throws Exception {
        // given
        String allCode = (ClasspathAddMvnMagicCommand.CLASSPATH_ADD_MVN) + " com.google.code.gson:gson:2.6.2";
        handleClasspathAddMvnDep(allCode, "gson-2.6.2.jar");
    }

    @Test
    public void unresolvedDependency() {
        // given
        String allCode = (ClasspathAddMvnMagicCommand.CLASSPATH_ADD_MVN) + " com.google.code.XXXX gson 2.6.2";
        MagicCommand command = new MagicCommand(new ClasspathAddMvnMagicCommand(configuration.mavenResolverParam(ClasspathAddMvnDepsMagicCommandTest.kernel), ClasspathAddMvnDepsMagicCommandTest.kernel), allCode);
        Code code = Code.createCode(allCode, Collections.singletonList(command), ClasspathAddMvnDepsMagicCommandTest.NO_ERRORS, new Message(new com.twosigma.beakerx.message.Header(JupyterMessages.COMM_MSG, "session1")));
        // when
        code.execute(ClasspathAddMvnDepsMagicCommandTest.kernel, 1);
        // then
        List<Message> stderr = EvaluatorResultTestWatcher.getStderr(ClasspathAddMvnDepsMagicCommandTest.kernel.getPublishedMessages());
        String text = ((String) (stderr.get(0).getContent().get("text")));
        assertThat(text).contains("Could not resolve dependencies for:");
        assertThat(text).contains("com.google.code.XXXX : gson : 2.6.2");
    }

    @Test
    public void handleClasspathReset() throws Exception {
        // given
        String allCode = (ClasspathAddMvnMagicCommand.CLASSPATH_ADD_MVN) + " com.google.code.gson:gson:2.6.2";
        handleClasspathAddMvnDep(allCode, "gson-2.6.2.jar");
        ClasspathAddMvnDepsMagicCommandTest.kernel.clearMessages();
        ClasspathAddMvnMagicCommand mvnMagicCommand = configuration.getClasspathAddMvnMagicCommand(ClasspathAddMvnDepsMagicCommandTest.kernel);
        mvnMagicCommand.addRepo("jcenter", "jcenter");
        Assert.assertTrue(((mvnMagicCommand.getRepos().get().size()) == ((ClasspathAddMvnMagicCommand.DEFAULT_MAVEN_REPOS.size()) + 1)));
        // when
        String resetCode = ClasspathResetMagicCommand.CLASSPATH_RESET;
        ClasspathResetMagicCommand resetMagicCommand = configuration.getClasspathResetMagicCommand(ClasspathAddMvnDepsMagicCommandTest.kernel);
        MagicCommand command = new MagicCommand(resetMagicCommand, resetCode);
        Code code = Code.createCode(resetCode, Collections.singletonList(command), ClasspathAddMvnDepsMagicCommandTest.NO_ERRORS, new Message(new com.twosigma.beakerx.message.Header(JupyterMessages.COMM_MSG, "session1")));
        code.execute(ClasspathAddMvnDepsMagicCommandTest.kernel, 1);
        // then
        List<Message> stderr = EvaluatorResultTestWatcher.getStdouts(ClasspathAddMvnDepsMagicCommandTest.kernel.getPublishedMessages());
        String text = ((String) (stderr.get(0).getContent().get("text")));
        assertThat(text).contains("Reset done");
        List<String> deletedFiles = ClasspathAddMvnDepsMagicCommandTest.kernel.getFileService().getDeletedFiles();
        Assert.assertTrue(deletedFiles.contains(mvnMagicCommand.getCommandParams().getPathToCache()));
        Assert.assertTrue(deletedFiles.contains(mvnMagicCommand.getCommandParams().getPathToNotebookJars()));
        Assert.assertTrue(((mvnMagicCommand.getRepos().get().size()) == (ClasspathAddMvnMagicCommand.DEFAULT_MAVEN_REPOS.size())));
    }

    @Test
    public void wrongCommandFormat() {
        // given
        String allCode = (ClasspathAddMvnMagicCommand.CLASSPATH_ADD_MVN) + " com.google.code.XXXX gson";
        MagicCommand command = new MagicCommand(new ClasspathAddMvnMagicCommand(configuration.mavenResolverParam(ClasspathAddMvnDepsMagicCommandTest.kernel), ClasspathAddMvnDepsMagicCommandTest.kernel), allCode);
        Code code = Code.createCode(allCode, Collections.singletonList(command), ClasspathAddMvnDepsMagicCommandTest.NO_ERRORS, new Message(new com.twosigma.beakerx.message.Header(JupyterMessages.COMM_MSG, "session1")));
        // when
        code.execute(ClasspathAddMvnDepsMagicCommandTest.kernel, 1);
        // then
        List<Message> stderr = EvaluatorResultTestWatcher.getStderr(ClasspathAddMvnDepsMagicCommandTest.kernel.getPublishedMessages());
        String text = ((String) (stderr.get(0).getContent().get("text")));
        assertThat(text).contains(((ClasspathAddMvnMagicCommand.ADD_MVN_FORMAT_ERROR_MESSAGE) + "\n"));
    }
}

