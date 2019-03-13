/**
 * Copyright 2018 TWO SIGMA OPEN SOURCE, LLC
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
package com.twosigma.beakerx.widget;


import Output.MODEL_NAME_VALUE;
import Output.NAME;
import Output.OUTPUTS;
import Output.STDERR;
import Output.STDOUT;
import Output.STREAM;
import Output.TEXT;
import Output.VIEW_NAME_VALUE;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.evaluator.EvaluatorResultTestWatcher;
import com.twosigma.beakerx.message.Message;
import java.util.List;
import org.junit.Test;


public class OutputWidgetTest {
    private KernelTest groovyKernel;

    @Test
    public void shouldSendCommOpenWhenCreate() throws Exception {
        // given
        // when
        new Output();
        // then
        TestWidgetUtils.verifyOpenCommMsg(groovyKernel.getPublishedMessages(), MODEL_NAME_VALUE, VIEW_NAME_VALUE);
    }

    @Test
    public void shouldSendCommMsgWhenAppendStdout() throws Exception {
        // given
        Output output = new Output();
        groovyKernel.clearPublishedMessages();
        // when
        output.appendStdout("Hello 1");
        // then
        Message streamMessage = EvaluatorResultTestWatcher.getStreamMessage(groovyKernel).get();
        assertThat(streamMessage.getContent().get(Output.OUTPUT_TYPE)).isEqualTo(STREAM.toString());
        assertThat(streamMessage.getContent().get(NAME)).isEqualTo(STDOUT);
        assertThat(streamMessage.getContent().get(TEXT)).isEqualTo("Hello 1\n");
    }

    @Test
    public void shouldSendCommMsgWhenAppendStderr() throws Exception {
        // given
        Output output = new Output();
        groovyKernel.clearPublishedMessages();
        // when
        output.appendStderr("Error 1");
        // then
        Message streamMessage = EvaluatorResultTestWatcher.getStreamMessage(groovyKernel).get();
        assertThat(streamMessage.getContent().get(Output.OUTPUT_TYPE)).isEqualTo(STREAM.toString());
        assertThat(streamMessage.getContent().get(NAME)).isEqualTo(STDERR);
        assertThat(streamMessage.getContent().get(TEXT)).isEqualTo("Error 1\n");
    }

    @Test
    public void shouldSendCommMsgClear() throws Exception {
        // given
        Output output = new Output();
        output.appendStderr("Error 1");
        groovyKernel.clearPublishedMessages();
        // when
        output.clearOutput();
        // then
        List value = TestWidgetUtils.getValueForProperty(groovyKernel, OUTPUTS, List.class);
        assertThat(value).isEmpty();
    }
}

