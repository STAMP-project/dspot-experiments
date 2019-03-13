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


import ClassPathAddMvnCellMagicCommand.MVN_CELL_FORMAT_ERROR_MESSAGE;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.MagicCommandConfigurationMock;
import com.twosigma.beakerx.MessageFactorTest;
import com.twosigma.beakerx.evaluator.EvaluatorResultTestWatcher;
import com.twosigma.beakerx.evaluator.EvaluatorTest;
import com.twosigma.beakerx.kernel.Code;
import com.twosigma.beakerx.kernel.magic.command.functionality.ClassPathAddMvnCellMagicCommand;
import com.twosigma.beakerx.kernel.magic.command.outcome.MagicCommandOutcomeItem;
import com.twosigma.beakerx.message.Message;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class ClasspathAddMvnDepsCellMagicCommandTest {
    private static final String SRC_TEST_RESOURCES_TEST_MVN_CACHE = "src/test/resources/testMvnCache";

    public static final String BUILD_PATH = "build";

    public static final ArrayList<MagicCommandOutcomeItem> NO_ERRORS = new ArrayList<>();

    private static KernelTest kernel;

    private static EvaluatorTest evaluator;

    private MagicCommandConfigurationMock configuration = new MagicCommandConfigurationMock();

    @Test
    public void handleAddDeps() throws Exception {
        // given
        String allCode = (((ClassPathAddMvnCellMagicCommand.CLASSPATH_ADD_MVN_CELL) + "\n") + "org.slf4j slf4j-api 1.7.5\n") + "com.google.code.gson gson 2.6.2";
        List<String> expected = Arrays.asList("slf4j-api-1.7.5.jar", "gson-2.6.2.jar");
        handleCellClasspathAddMvnDep(allCode, expected);
    }

    @Test
    public void handleAddDepsMixedSyntax() throws Exception {
        String allCode = (((ClassPathAddMvnCellMagicCommand.CLASSPATH_ADD_MVN_CELL) + "\n") + "org.slf4j slf4j-api 1.7.5\n") + "com.google.code.gson:gson:2.6.2";
        List<String> expected = Arrays.asList("slf4j-api-1.7.5.jar", "gson-2.6.2.jar");
        handleCellClasspathAddMvnDep(allCode, expected);
    }

    @Test
    public void handleUnresolvedDep() throws Exception {
        String allCode = "%%classpath add mvn\n" + "com.google.code.XXXX:gson:2.6.2";
        // given
        MagicCommand command = new MagicCommand(new ClassPathAddMvnCellMagicCommand(configuration.mavenResolverParam(ClasspathAddMvnDepsCellMagicCommandTest.kernel), ClasspathAddMvnDepsCellMagicCommandTest.kernel), allCode);
        Code code = Code.createCode(allCode, Collections.singletonList(command), ClasspathAddMvnDepsCellMagicCommandTest.NO_ERRORS, MessageFactorTest.commMsg());
        // when
        code.execute(ClasspathAddMvnDepsCellMagicCommandTest.kernel, 1);
        // then
        List<Message> stderr = EvaluatorResultTestWatcher.getStderr(ClasspathAddMvnDepsCellMagicCommandTest.kernel.getPublishedMessages());
        String text = ((String) (stderr.get(0).getContent().get("text")));
        Assertions.assertThat(text).contains("Could not resolve dependencies for:");
        Assertions.assertThat(text).contains("com.google.code.XXXX : gson : 2.6.2");
    }

    @Test
    public void handleIncorrectSyntax() throws Exception {
        String singleLine = "%%classpath add mvn\n" + "org.slf4j slf4j-api";
        String additionalCode = "%%classpath add mvn\n" + ("org.slf4j slf4j-api 1.7.5\n" + "println(\"test\")");
        processMagicCommand(singleLine);
        List<Message> stderr = EvaluatorResultTestWatcher.getStderr(ClasspathAddMvnDepsCellMagicCommandTest.kernel.getPublishedMessages());
        String text = ((String) (stderr.get(0).getContent().get("text")));
        Assertions.assertThat(text).contains(MVN_CELL_FORMAT_ERROR_MESSAGE);
        processMagicCommand(additionalCode);
        List<Message> stderr2 = EvaluatorResultTestWatcher.getStderr(ClasspathAddMvnDepsCellMagicCommandTest.kernel.getPublishedMessages());
        String text2 = ((String) (stderr2.get(0).getContent().get("text")));
        Assertions.assertThat(text2).contains(MVN_CELL_FORMAT_ERROR_MESSAGE);
    }
}

