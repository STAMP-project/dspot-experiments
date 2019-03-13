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
package com.thoughtworks.go.agent.launcher;


import com.thoughtworks.go.agent.common.UrlConstructor;
import java.net.MalformedURLException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class UrlConstructorTest {
    @Test
    public void shouldGenerateCorrectUrlForGivenPath() throws MalformedURLException {
        UrlConstructor urlConstructor = new UrlConstructor("https://example.com:8443/go/");
        Assert.assertThat(urlConstructor.serverUrlFor(""), Matchers.is("https://example.com:8443/go"));
        Assert.assertThat(urlConstructor.serverUrlFor("foo/bar"), Matchers.is("https://example.com:8443/go/foo/bar"));
        Assert.assertThat(urlConstructor.serverUrlFor("admin/agent"), Matchers.is("https://example.com:8443/go/admin/agent"));
    }
}

