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
package com.thoughtworks.go.domain;


import JobResult.Cancelled;
import JobResult.Failed;
import JobResult.JOB_RESULT_COMPARATOR;
import JobResult.Passed;
import JobResult.Unknown;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class JobResultTest {
    @Test
    public void shouldConvertToCctrayStatus() throws Exception {
        Assert.assertThat(Passed.toCctrayStatus(), Matchers.is("Success"));
        Assert.assertThat(Failed.toCctrayStatus(), Matchers.is("Failure"));
        Assert.assertThat(Cancelled.toCctrayStatus(), Matchers.is("Failure"));
        Assert.assertThat(Unknown.toCctrayStatus(), Matchers.is("Success"));
    }

    @Test
    public void compatorShouldOrderBy_failed_passed_and_then_unknown() throws Exception {
        Assert.assertThat(JOB_RESULT_COMPARATOR.compare(Unknown, Failed), Matchers.is(1));
        Assert.assertThat(JOB_RESULT_COMPARATOR.compare(Unknown, Passed), Matchers.is((-1)));
        Assert.assertThat(JOB_RESULT_COMPARATOR.compare(Failed, Passed), Matchers.is((-1)));
        Assert.assertThat(JOB_RESULT_COMPARATOR.compare(Passed, Failed), Matchers.is(1));
        Assert.assertThat(JOB_RESULT_COMPARATOR.compare(Passed, Passed), Matchers.is(0));
        Assert.assertThat(JOB_RESULT_COMPARATOR.compare(Cancelled, Failed), Matchers.is(0));
        Assert.assertThat(JOB_RESULT_COMPARATOR.compare(Cancelled, Passed), Matchers.is((-1)));
        Assert.assertThat(JOB_RESULT_COMPARATOR.compare(Cancelled, Unknown), Matchers.is((-1)));
        Assert.assertThat(JOB_RESULT_COMPARATOR.compare(Unknown, Unknown), Matchers.is(0));
        Assert.assertThat(JOB_RESULT_COMPARATOR.compare(Unknown, Cancelled), Matchers.is(1));
    }
}

