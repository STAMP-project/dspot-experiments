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
package com.thoughtworks.go.plugin.infra.commons;


import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PluginUploadResponseTest {
    @Test
    public void shouldCreateASuccessResponse() {
        PluginUploadResponse response = PluginUploadResponse.create(true, "success message", null);
        Assert.assertThat(response.success(), Matchers.is("success message"));
        Assert.assertThat(response.errors().size(), Matchers.is(0));
    }

    @Test
    public void shouldCreateAResponseWithErrors() {
        Map<Integer, String> errors = new HashMap<>();
        int errorCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
        String errorMessage = new FileNotFoundException().getMessage();
        errors.put(errorCode, errorMessage);
        PluginUploadResponse response = PluginUploadResponse.create(false, null, errors);
        Assert.assertThat(response.success(), Matchers.is(""));
        Assert.assertThat(response.errors().get(errorCode), Matchers.is(errorMessage));
    }
}

