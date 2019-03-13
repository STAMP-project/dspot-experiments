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
package com.thoughtworks.go.server;


import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class JettyCustomErrorPageHandlerTest {
    JettyCustomErrorPageHandler errorHandler;

    HttpServletRequest request;

    PrintWriter writer;

    private ArgumentCaptor<String> captor;

    @Test
    public void shouldWriteErrorPageFor404WithMessage() throws Exception {
        errorHandler.writeErrorPage(request, writer, 404, null, false);
        Mockito.verify(writer).write(captor.capture());
        String fileContents = captor.getValue();
        Assert.assertThat(fileContents, Matchers.containsString("<h1>404</h1>"));
        Assert.assertThat(fileContents, Matchers.containsString("<h2>Not Found</h2>"));
    }

    @Test
    public void shouldNotUseErrorMessageFromResponse() throws Exception {
        errorHandler.writeErrorPage(request, writer, 500, "this message should not be rendered", false);
        Mockito.verify(writer).write(captor.capture());
        String fileContents = captor.getValue();
        Assert.assertThat(fileContents, Matchers.not(Matchers.containsString("this message should not be rendered")));
    }
}

