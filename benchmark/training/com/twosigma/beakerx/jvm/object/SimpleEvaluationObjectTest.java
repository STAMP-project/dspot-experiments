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
package com.twosigma.beakerx.jvm.object;


import SimpleEvaluationObject.EvaluationStdError;
import SimpleEvaluationObject.EvaluationStdOutput;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.jvm.ObserverObjectTest;
import com.twosigma.beakerx.jvm.threads.BeakerOutputHandler;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class SimpleEvaluationObjectTest {
    private SimpleEvaluationObject seo;

    private ObserverObjectTest observer;

    private KernelTest kernel;

    @Test
    public void createWithParam_hasExpressionIsParamAndStatusIsQueued() throws Exception {
        // when
        SimpleEvaluationObject object = new SimpleEvaluationObject("code");
        // then
        Assertions.assertThat(object.getExpression()).isEqualTo("code");
        Assertions.assertThat(object.getStatus()).isEqualTo(QUEUED);
    }

    @Test
    public void seoStarted_shouldNotifyObserver() throws Exception {
        // when
        seo.started();
        // then
        Assertions.assertThat(observer.getObjectList().get(0)).isEqualTo(seo);
    }

    @Test
    public void seoFinished_shouldNotifyObserver() throws Exception {
        // when
        seo.finished(new Object());
        // then
        Assertions.assertThat(observer.getObjectList().get(0)).isEqualTo(seo);
    }

    @Test
    public void seoError_shouldNotifyObserver() throws Exception {
        // when
        seo.error(new Object());
        // then
        Assertions.assertThat(observer.getObjectList().get(0)).isEqualTo(seo);
    }

    @Test
    public void seoUpdate_shouldNotifyObserver() throws Exception {
        // when
        seo.update(new Object());
        // then
        Assertions.assertThat(observer.getObjectList().get(0)).isEqualTo(seo);
    }

    @Test
    public void seoStarted_hasRunningStatus() throws Exception {
        // when
        seo.started();
        // then
        Assertions.assertThat(seo.getStatus()).isEqualTo(RUNNING);
    }

    @Test
    public void seoFinished_hasFinishedStatus() throws Exception {
        // when
        seo.finished(new Object());
        // then
        Assertions.assertThat(seo.getStatus()).isEqualTo(FINISHED);
    }

    @Test
    public void seoError_hasErrorStatus() throws Exception {
        // when
        seo.error(new Object());
        // then
        Assertions.assertThat(seo.getStatus()).isEqualTo(ERROR);
    }

    @Test
    public void seoUpdate_hasRunningStatus() throws Exception {
        // when
        seo.update(new Object());
        // then
        Assertions.assertThat(seo.getStatus()).isEqualTo(RUNNING);
    }

    @Test
    public void seoUpdate_shouldSetPayload() throws Exception {
        // given
        Object payload = new Object();
        // when
        seo.update(payload);
        // then
        Assertions.assertThat(seo.getPayload()).isEqualTo(payload);
    }

    @Test
    public void seoFinished_shouldSetPayload() throws Exception {
        // given
        Object payload = new Object();
        // when
        seo.finished(payload);
        // then
        Assertions.assertThat(seo.getPayload()).isEqualTo(payload);
    }

    @Test
    public void seoError_shouldSetPayload() throws Exception {
        // given
        Object payload = new Object();
        // when
        seo.error(payload);
        // then
        Assertions.assertThat(seo.getPayload()).isEqualTo(payload);
    }

    @Test
    public void beakerOutputHandlerWriteBytesWithLength_shouldNotifyObserver() throws Exception {
        // given
        BeakerOutputHandler handler = seo.getStdOutputHandler();
        // when
        handler.write("test");
        // then
        Assertions.assertThat(observer.getObjectList().get(0)).isEqualTo(seo);
    }

    @Test
    public void beakerOutputHandlerWriteInt_shouldNotifyObserver() throws Exception {
        // given
        BeakerOutputHandler handler = seo.getStdOutputHandler();
        // when
        handler.write("t");
        // then
        Assertions.assertThat(observer.getObjectList().get(0)).isEqualTo(seo);
    }

    @Test
    public void beakerOutputHandlerWriteBytes_shouldNotifyObserver() throws Exception {
        // given
        BeakerOutputHandler handler = seo.getStdOutputHandler();
        // when
        handler.write("test");
        // then
        Assertions.assertThat(observer.getObjectList().get(0)).isEqualTo(seo);
    }

    @Test
    public void stdErrorHandlerWrite_addConsoleOutput() throws Exception {
        // given
        BeakerOutputHandler handler = seo.getStdErrorHandler();
        // when
        handler.write("test");
        // then
        Assertions.assertThat(seo.getConsoleOutput()).isNotEmpty();
    }

    @Test
    public void stdOutputHandlerWrite_addConsoleOutput() throws Exception {
        // given
        BeakerOutputHandler handler = seo.getStdOutputHandler();
        // when
        handler.write("test");
        // then
        Assertions.assertThat(seo.getConsoleOutput()).isNotEmpty();
    }

    @Test
    public void appendOutput_shouldNotifyObserver() throws Exception {
        // when
        seo.appendOutput("test\n");
        // then
        Assertions.assertThat(observer.getObjectList().get(0)).isEqualTo(seo);
    }

    @Test
    public void appendOutputWithDoubleMaxSizeString_outputDataHasTwoLines() throws Exception {
        // when
        seo.appendOutput(("test\n" + (generateDoubleMaxSizeString())));
        // then
        Assertions.assertThat(seo.getOutputdata().get(0)).isInstanceOf(EvaluationStdOutput.class);
        SimpleEvaluationObject.EvaluationStdOutput stdOut = ((SimpleEvaluationObject.EvaluationStdOutput) (seo.getOutputdata().get(0)));
        Assertions.assertThat(stdOut.payload.split("\n").length).isEqualTo(2);
    }

    @Test
    public void appendError_shouldNotifyObserver() throws Exception {
        // when
        seo.appendError("test\n");
        // then
        Assertions.assertThat(observer.getObjectList().get(0)).isEqualTo(seo);
    }

    @Test
    public void appendErrorWithDoubleMaxSizeString_outputDataHasTwoLines() throws Exception {
        // when
        seo.appendError(("test\n" + (generateDoubleMaxSizeString())));
        // then
        Assertions.assertThat(seo.getOutputdata().get(0)).isInstanceOf(EvaluationStdError.class);
        SimpleEvaluationObject.EvaluationStdError stdErr = ((SimpleEvaluationObject.EvaluationStdError) (seo.getOutputdata().get(0)));
        Assertions.assertThat(stdErr.payload.split("\n").length).isEqualTo(2);
    }

    @Test
    public void appendErrorWithNotDesirableMessage_outputDataDoesNotContainThatMessage() throws Exception {
        String notDesirableMessage = "JavaSourceCompilerImpl compile";
        // when
        seo.appendError((("test\n" + notDesirableMessage) + "\n"));
        // then
        SimpleEvaluationObject.EvaluationStdError stdErr = ((SimpleEvaluationObject.EvaluationStdError) (seo.getOutputdata().get(0)));
        Assertions.assertThat(stdErr.payload).doesNotContain(notDesirableMessage);
        Assertions.assertThat(stdErr.payload).contains("test");
    }

    @Test
    public void structuredUpdate_shouldPublishMessages() throws Exception {
        // when
        seo.structuredUpdate("create progressReporting", 10);
        // then
        Assertions.assertThat(kernel.getPublishedMessages()).isNotEmpty();
    }

    @Test
    public void clrOutputHandler_shouldPublishMessages() throws Exception {
        // given
        seo.structuredUpdate("create progressReporting", 10);
        kernel.clearPublishedMessages();
        // when
        seo.clrOutputHandler();
        // then
        Assertions.assertThat(kernel.getPublishedMessages()).isNotEmpty();
    }
}

