/**
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.http.server;


import java.net.URI;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link DefaultRequestPath}.
 *
 * @author Rossen Stoyanchev
 */
public class DefaultRequestPathTests {
    @Test
    public void requestPath() throws Exception {
        // basic
        testRequestPath("/app/a/b/c", "/app", "/a/b/c");
        // no context path
        testRequestPath("/a/b/c", "", "/a/b/c");
        // context path only
        testRequestPath("/a/b", "/a/b", "");
        // root path
        testRequestPath("/", "", "/");
        // empty path
        testRequestPath("", "", "");
        testRequestPath("", "/", "");
        // trailing slash
        testRequestPath("/app/a/", "/app", "/a/");
        testRequestPath("/app/a//", "/app", "/a//");
    }

    @Test
    public void updateRequestPath() throws Exception {
        URI uri = URI.create("http://localhost:8080/aA/bB/cC");
        RequestPath requestPath = RequestPath.parse(uri, null);
        Assert.assertEquals("", requestPath.contextPath().value());
        Assert.assertEquals("/aA/bB/cC", requestPath.pathWithinApplication().value());
        requestPath = requestPath.modifyContextPath("/aA");
        Assert.assertEquals("/aA", requestPath.contextPath().value());
        Assert.assertEquals("/bB/cC", requestPath.pathWithinApplication().value());
    }
}

