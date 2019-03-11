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
package com.thoughtworks.go.server.util;


import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Request;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class Jetty9RequestTest {
    private Jetty9Request jetty9Request;

    @Mock
    private Request request;

    @Test
    public void shouldGetUrl() {
        Assert.assertThat(jetty9Request.getUrl(), Matchers.is("http://junk/foo/bar/baz"));
    }

    @Test
    public void shouldGetUriPath() {
        Assert.assertThat(jetty9Request.getUriPath(), Matchers.is("foo/bar/baz"));
    }

    @Test
    public void shouldGetUriAsString() {
        Assert.assertThat(jetty9Request.getUriAsString(), Matchers.is("foo/bar/baz"));
    }

    @Test
    public void shouldSetRequestUri() {
        HttpURI requestUri = new HttpURI("foo/bar/baz");
        Mockito.when(request.getHttpURI()).thenReturn(requestUri);
        jetty9Request.setRequestURI("foo/junk?a=b&c=d");
        Assert.assertThat(requestUri.getPath(), Matchers.is("foo/junk?a=b&c=d"));
    }
}

