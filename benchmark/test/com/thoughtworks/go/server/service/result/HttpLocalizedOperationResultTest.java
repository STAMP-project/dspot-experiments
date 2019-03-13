/**
 * Copyright 2018 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.server.service.result;


import HealthStateScope.GLOBAL;
import com.thoughtworks.go.i18n.LocalizedMessage;
import com.thoughtworks.go.serverhealth.HealthStateType;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class HttpLocalizedOperationResultTest {
    @Test
    public void shouldReturnSuccessfulIfNothingChanged() {
        HttpLocalizedOperationResult result = new HttpLocalizedOperationResult();
        Assert.assertThat(result.isSuccessful(), Matchers.is(true));
        Assert.assertThat(result.httpCode(), Matchers.is(200));
    }

    @Test
    public void shouldNotReturnSuccessfulIfUnauthorized() {
        LocalizedOperationResult result = new HttpLocalizedOperationResult();
        result.forbidden(LocalizedMessage.forbiddenToViewPipeline("whateva"), HealthStateType.general(GLOBAL));
        Assert.assertThat(result.isSuccessful(), Matchers.is(false));
    }

    @Test
    public void shouldNotReturnSuccessfulIfNotFound() {
        LocalizedOperationResult result = new HttpLocalizedOperationResult();
        result.notFound(LocalizedMessage.forbiddenToViewPipeline("whateva"), HealthStateType.general(GLOBAL));
        Assert.assertThat(result.isSuccessful(), Matchers.is(false));
    }

    @Test
    public void shouldReturn404AndLocalizeWhenNotFound() {
        HttpLocalizedOperationResult result = new HttpLocalizedOperationResult();
        result.notFound("foo", HealthStateType.general(GLOBAL));
        Assert.assertThat(result.httpCode(), Matchers.is(404));
        Assert.assertThat(result.message(), Matchers.is("foo"));
    }

    @Test
    public void shouldReturn403WhenUserDoesNotHavePermissionToAccessResource() {
        String message = LocalizedMessage.forbiddenToViewPipeline("whateva");
        HttpLocalizedOperationResult result = new HttpLocalizedOperationResult();
        result.forbidden(message, HealthStateType.general(GLOBAL));
        Assert.assertThat(result.httpCode(), Matchers.is(403));
    }

    @Test
    public void shouldReturnMessageAndStatus501WhenNotImplemented() {
        HttpLocalizedOperationResult result = new HttpLocalizedOperationResult();
        result.notImplemented(null);
        Assert.assertThat(result.httpCode(), Matchers.is(501));
    }

    @Test
    public void shouldReturnAccepted() throws Exception {
        HttpLocalizedOperationResult result = new HttpLocalizedOperationResult();
        result.accepted("foo");
        Assert.assertThat(result.httpCode(), Matchers.is(202));
        Assert.assertThat(result.message(), Matchers.is("foo"));
    }

    @Test
    public void shouldNotFailWhenNoMessageSet() {
        HttpLocalizedOperationResult result = new HttpLocalizedOperationResult();
        Assert.assertThat(result.message(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldTellWhetherTheMessageIsPresentOrNot() throws Exception {
        HttpLocalizedOperationResult result = new HttpLocalizedOperationResult();
        Assert.assertFalse(result.hasMessage());
        result.setMessage("foo");
        Assert.assertTrue(result.hasMessage());
    }
}

