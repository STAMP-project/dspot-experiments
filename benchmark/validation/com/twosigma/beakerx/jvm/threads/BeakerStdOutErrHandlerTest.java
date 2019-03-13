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
package com.twosigma.beakerx.jvm.threads;


import com.twosigma.beakerx.jvm.object.SimpleEvaluationObject;
import org.junit.Test;


public class BeakerStdOutErrHandlerTest {
    private static SimpleEvaluationObject seo;

    private BeakerStdOutErrHandlerTest.SimpleOutputHandlerTest stdout;

    private BeakerStdOutErrHandlerTest.SimpleErrHandlerTest stderr;

    @Test
    public void shouldHandleStdout() throws Exception {
        // given
        // when
        System.out.print("Hello");
        // then
        assertThat(stdout.text).isNotEmpty();
    }

    @Test
    public void shouldHandleStderr() throws Exception {
        // given
        // when
        System.err.print("Error");
        // then
        assertThat(stderr.text).isNotEmpty();
    }

    static class SimpleOutputHandlerTest implements BeakerOutputHandler {
        private String text;

        @Override
        public void write(String b) {
            this.text = b;
        }
    }

    static class SimpleErrHandlerTest implements BeakerOutputHandler {
        private String text;

        @Override
        public void write(String b) {
            this.text = b;
        }
    }
}

