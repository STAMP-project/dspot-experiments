/**
 * Copyright 2016 ThoughtWorks, Inc.
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
package com.thoughtworks.go.server.util;


import org.eclipse.jetty.server.Response;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class Jetty9ResponseTest {
    private Jetty9Response jetty9Response;

    private Response response;

    @Test
    public void shouldGetResponseStatus() {
        Mockito.when(response.getStatus()).thenReturn(200);
        Assert.assertThat(jetty9Response.getStatus(), Matchers.is(200));
    }

    @Test
    public void shouldGetResponseContentCount() {
        Mockito.when(response.getContentCount()).thenReturn(2000L);
        Assert.assertThat(jetty9Response.getContentCount(), Matchers.is(2000L));
    }
}

