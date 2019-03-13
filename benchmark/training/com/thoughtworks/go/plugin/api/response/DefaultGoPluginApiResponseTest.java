/**
 * Copyright 2017 ThoughtWorks, Inc.
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
package com.thoughtworks.go.plugin.api.response;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class DefaultGoPluginApiResponseTest {
    @Test
    public void shouldReturnResponseForBadRequest() throws Exception {
        DefaultGoPluginApiResponse response = DefaultGoPluginApiResponse.badRequest("responseBody");
        Assert.assertThat(response.responseCode(), Matchers.is(400));
        Assert.assertThat(response.responseBody(), Matchers.is("responseBody"));
    }

    @Test
    public void shouldReturnResponseForIncompleteRequest() throws Exception {
        DefaultGoPluginApiResponse response = DefaultGoPluginApiResponse.incompleteRequest("responseBody");
        Assert.assertThat(response.responseCode(), Matchers.is(412));
        Assert.assertThat(response.responseBody(), Matchers.is("responseBody"));
    }

    @Test
    public void shouldReturnResponseForErrorRequest() throws Exception {
        DefaultGoPluginApiResponse response = DefaultGoPluginApiResponse.error("responseBody");
        Assert.assertThat(response.responseCode(), Matchers.is(500));
        Assert.assertThat(response.responseBody(), Matchers.is("responseBody"));
    }

    @Test
    public void shouldReturnResponseForSuccessRequest() throws Exception {
        DefaultGoPluginApiResponse response = DefaultGoPluginApiResponse.success("responseBody");
        Assert.assertThat(response.responseCode(), Matchers.is(200));
        Assert.assertThat(response.responseBody(), Matchers.is("responseBody"));
    }
}

