/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.domain.command.monitor;


import org.junit.Test;
import org.mockito.Mockito;


public class AntTaskDetectorTest {
    private Reporter reporter;

    private AntTaskDetector detector;

    @Test
    public void shouldReportNothingByDefault() throws Exception {
        detector.consumeLine("Something normal happened");
    }

    @Test
    public void shouldReportWhatTaskIsRunning() throws Exception {
        detector.consumeLine("clean:");
        Mockito.verify(reporter).reportStatusDetail("building [clean]");
    }

    @Test
    public void shouldIgnoreWhenThereIsSpaces() throws Exception {
        detector.consumeLine("this should not show:");
    }

    @Test
    public void shouldIgnoreLinesWithTextAfterTheTarget() throws Exception {
        detector.consumeLine("target: this line should not show");
    }
}

