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


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/* context.checking(new Expectations(){{
one(reporter).failing("Command reported [BUILD FAILED].");
}});
detector.consumeLine(" BUILD FAILED ");
 */
/* [junit] Testsuite: com.thoughtworks.go.agent.service.SslInfrastructureServiceTest
[junit] Tests run: 1, Failures: 0, Errors: 0, Time elapsed: 10.561 sec
[junit] Tests run: 1, Failures: 0, Errors: 0, Time elapsed: 10.561 sec
 */
public class AntTestFailureDetectorTest {
    private Reporter reporter;

    private AntTestFailureDetector detector;

    @Test
    public void shouldReportNothingByDefault() throws Exception {
        detector.consumeLine("Something normal happened");
        Assert.assertThat(detector.getCount(), Matchers.is(0));
    }

    @Test
    public void shouldCountTests() throws Exception {
        detector.consumeLine("[junit] Testsuite: com.thoughtworks.go.agent.service.SslInfrastructureServiceTest");
        detector.consumeLine("[junit] Tests run: 1, Failures: 0, Errors: 0, Time elapsed: 10.561 sec");
        Assert.assertThat(detector.getCount(), Matchers.is(1));
    }

    @Test
    public void shouldIgnoreMultipleOutputLines() throws Exception {
        detector.consumeLine("[junit] Testsuite: com.thoughtworks.go.agent.service.SslInfrastructureServiceTest");
        detector.consumeLine("[junit] Tests run: 5, Failures: 6, Errors: 7, Time elapsed: 10.561 sec");
        detector.consumeLine("[junit] Tests run: 5, Failures: 6, Errors: 7, Time elapsed: 10.561 sec");
        detector.consumeLine("[junit] Tests run: 5, Failures: 6, Errors: 7, Time elapsed: 10.561 sec");
        Assert.assertThat(detector.getCount(), Matchers.is(5));
    }

    @Test
    public void shouldCountNumberOfTests() throws Exception {
        detector.consumeLine("[junit] Testsuite: com.thoughtworks.go.agent.service.SslInfrastructureServiceTest");
        detector.consumeLine("[junit] Tests run: 5, Failures: 6, Errors: 7, Time elapsed: 10.561 sec");
        Assert.assertThat(detector.getCount(), Matchers.is(5));
    }

    @Test
    public void shouldCountNumberOfFailures() throws Exception {
        detector.consumeLine("[junit] Testsuite: com.thoughtworks.go.agent.service.SslInfrastructureServiceTest");
        detector.consumeLine("[junit] Tests run: 5, Failures: 6, Errors: 7, Time elapsed: 10.561 sec");
        Assert.assertThat(detector.getFailures(), Matchers.is(6));
    }

    @Test
    public void shouldCountNumberOfErrors() throws Exception {
        detector.consumeLine("[junit] Testsuite: com.thoughtworks.go.agent.service.SslInfrastructureServiceTest");
        detector.consumeLine("[junit] Tests run: 5, Failures: 6, Errors: 7, Time elapsed: 10.561 sec");
        Assert.assertThat(detector.getErrors(), Matchers.is(7));
    }

    @Test
    public void shouldCountTotalTime() throws Exception {
        detector.consumeLine("[junit] Testsuite: com.thoughtworks.go.agent.service.SslInfrastructureServiceTest");
        detector.consumeLine("[junit] Tests run: 5, Failures: 6, Errors: 7, Time elapsed: 10.561 sec");
        Assert.assertThat(detector.getTotalTime(), Matchers.is(10561L));
    }

    @Test
    public void shouldNotFailingtheBuildWhenTestsFail() throws Exception {
        detector.consumeLine("[junit] Testsuite: com.thoughtworks.go.agent.service.SslInfrastructureServiceTest");
        detector.consumeLine("[junit] Tests run: 5, Failures: 6, Errors: 7, Time elapsed: 10.561 sec");
        Assert.assertThat(detector.getTotalTime(), Matchers.is(10561L));
        Mockito.verify(reporter, Mockito.never()).failing(null);
    }
}

