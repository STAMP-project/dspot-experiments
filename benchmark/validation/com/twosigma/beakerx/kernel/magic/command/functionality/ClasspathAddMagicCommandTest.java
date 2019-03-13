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
package com.twosigma.beakerx.kernel.magic.command.functionality;


import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.MessageFactorTest;
import com.twosigma.beakerx.evaluator.EvaluatorResultTestWatcher;
import com.twosigma.beakerx.evaluator.EvaluatorTest;
import com.twosigma.beakerx.kernel.Code;
import com.twosigma.beakerx.kernel.magic.command.CodeFactory;
import com.twosigma.beakerx.message.Message;
import java.util.List;
import org.junit.Test;

import static ClasspathAddJarMagicCommand.CLASSPATH;


public class ClasspathAddMagicCommandTest {
    private KernelTest kernel;

    private EvaluatorTest evaluator;

    @Test
    public void errorWhenUnknownMagicCommand() {
        // given
        String allCode = "%unknownClasspath params";
        Code code = CodeFactory.create(allCode, MessageFactorTest.commMsg(), kernel);
        // when
        code.execute(kernel, 1);
        // then
        List<Message> std = EvaluatorResultTestWatcher.getStderr(kernel.getPublishedMessages());
        String text = ((String) (std.get(0).getContent().get("text")));
        assertThat(text).contains((("Inline magic " + allCode) + " not found"));
    }

    @Test
    public void errorWhenIncompleteMagicCommand() {
        // given
        String allCode = (CLASSPATH) + " add garbage";
        Code code = CodeFactory.create(allCode, MessageFactorTest.commMsg(), kernel);
        // when
        code.execute(kernel, 1);
        // then
        List<Message> std = EvaluatorResultTestWatcher.getStderr(kernel.getPublishedMessages());
        String text = ((String) (std.get(0).getContent().get("text")));
        assertThat(text).contains(" not found");
    }
}

