/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2018 ThoughtWorks, Inc.
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
package com.thoughtworks.go.server.service.result;


import HealthStateScope.GLOBAL;
import com.thoughtworks.go.serverhealth.HealthStateScope;
import com.thoughtworks.go.serverhealth.HealthStateType;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class HttpOperationResultTest {
    private HttpOperationResult httpOperationResult;

    @Test
    public void shouldReturn202IfEverythingWorks() throws Exception {
        httpOperationResult.accepted("Request to schedule pipeline 'baboon' accepted", "blah blah", HealthStateType.general(HealthStateScope.forPipeline("baboon")));
        Assert.assertThat(httpOperationResult.httpCode(), Matchers.is(202));
        Assert.assertThat(httpOperationResult.canContinue(), Matchers.is(true));
        Assert.assertThat(httpOperationResult.message(), Matchers.is("Request to schedule pipeline 'baboon' accepted"));
    }

    @Test
    public void shouldReturn409IfPipelineCannotBeScheduled() throws Exception {
        httpOperationResult.conflict("Pipeline is already scheduled", "", null);
        Assert.assertThat(httpOperationResult.httpCode(), Matchers.is(409));
        Assert.assertThat(httpOperationResult.canContinue(), Matchers.is(false));
        Assert.assertThat(httpOperationResult.message(), Matchers.is("Pipeline is already scheduled"));
    }

    @Test
    public void shouldReturn404ForPipelineThatDoesntExist() throws Exception {
        httpOperationResult.notFound("pipeline baboon doesn't exist", "", null);
        Assert.assertThat(httpOperationResult.httpCode(), Matchers.is(404));
        Assert.assertThat(httpOperationResult.canContinue(), Matchers.is(false));
        Assert.assertThat(httpOperationResult.message(), Matchers.is("pipeline baboon doesn't exist"));
    }

    @Test
    public void shouldReturn406ForNotAcceptable() {
        httpOperationResult.notAcceptable("not acceptable", HealthStateType.general(GLOBAL));
        Assert.assertThat(httpOperationResult.httpCode(), Matchers.is(406));
        Assert.assertThat(httpOperationResult.canContinue(), Matchers.is(false));
        Assert.assertThat(httpOperationResult.message(), Matchers.is("not acceptable"));
    }

    @Test
    public void shouldReturnMessageWithDescription() {
        httpOperationResult.notAcceptable("message", "description", HealthStateType.general(GLOBAL));
        Assert.assertThat(httpOperationResult.httpCode(), Matchers.is(406));
        Assert.assertThat(httpOperationResult.message(), Matchers.is("message"));
        Assert.assertThat(httpOperationResult.detailedMessage(), Matchers.is("message { description }\n"));
    }

    @Test
    public void successShouldReturnTrueIfStatusIs2xx() {
        Assert.assertThat(httpOperationResult.isSuccess(), Matchers.is(true));
        httpOperationResult.notAcceptable("not acceptable", HealthStateType.general(GLOBAL));
        Assert.assertThat(httpOperationResult.isSuccess(), Matchers.is(false));
        httpOperationResult.ok("message");
        Assert.assertThat(httpOperationResult.isSuccess(), Matchers.is(true));
        httpOperationResult.error("message", "desc", HealthStateType.general(GLOBAL));
        Assert.assertThat(httpOperationResult.isSuccess(), Matchers.is(false));
        httpOperationResult.accepted("Request to schedule pipeline 'baboon' accepted", "blah blah", HealthStateType.general(HealthStateScope.forPipeline("baboon")));
        Assert.assertThat(httpOperationResult.isSuccess(), Matchers.is(true));
    }

    @Test
    public void shouldReturnOnlyMessage_whenDescriptionIsBlank() {
        httpOperationResult.notAcceptable("message", "", HealthStateType.general(GLOBAL));
        Assert.assertThat(httpOperationResult.httpCode(), Matchers.is(406));
        Assert.assertThat(httpOperationResult.message(), Matchers.is("message"));
        Assert.assertThat(httpOperationResult.detailedMessage(), Matchers.is("message\n"));
    }

    @Test
    public void shouldReturnOnlyMessage_whenServerHealthStateDoesntExist() {
        httpOperationResult.ok("message");
        Assert.assertThat(httpOperationResult.httpCode(), Matchers.is(200));
        Assert.assertThat(httpOperationResult.message(), Matchers.is("message"));
        Assert.assertThat(httpOperationResult.detailedMessage(), Matchers.is("message\n"));
    }

    @Test
    public void shouldSet_generic400_forError_becauseKeepingStatus_0_isNotCivilizedByHttpStandards() {
        httpOperationResult.error("message", "desc", HealthStateType.general(GLOBAL));
        Assert.assertThat(httpOperationResult.httpCode(), Matchers.is(400));
        Assert.assertThat(httpOperationResult.message(), Matchers.is("message"));
        Assert.assertThat(httpOperationResult.detailedMessage(), Matchers.is("message { desc }\n"));
    }

    @Test
    public void shouldReturn500WhenInternalServerErrorOccurs() throws Exception {
        httpOperationResult.internalServerError("error occurred during deletion of agent. Could not delete.", null);
        Assert.assertThat(httpOperationResult.httpCode(), Matchers.is(500));
        Assert.assertThat(httpOperationResult.canContinue(), Matchers.is(false));
        Assert.assertThat(httpOperationResult.message(), Matchers.is("error occurred during deletion of agent. Could not delete."));
    }
}

