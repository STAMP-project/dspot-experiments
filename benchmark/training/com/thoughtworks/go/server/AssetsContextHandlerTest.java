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
package com.thoughtworks.go.server;


import AssetsContextHandler.AssetsHandler;
import com.thoughtworks.go.util.ReflectionUtil;
import com.thoughtworks.go.util.SystemEnvironment;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class AssetsContextHandlerTest {
    private AssetsContextHandler handler;

    @Mock
    private SystemEnvironment systemEnvironment;

    @Mock
    private WebAppContext webAppContext;

    @Test
    public void shouldSetHeadersAndBaseDirectory() throws IOException {
        Assert.assertThat(handler.getContextPath(), Matchers.is("/go/assets"));
        Assert.assertThat(((getHandler()) instanceof AssetsContextHandler.AssetsHandler), Matchers.is(true));
        AssetsContextHandler.AssetsHandler assetsHandler = ((AssetsContextHandler.AssetsHandler) (getHandler()));
        ResourceHandler resourceHandler = ((ResourceHandler) (ReflectionUtil.getField(assetsHandler, "resourceHandler")));
        Assert.assertThat(resourceHandler.getCacheControl(), Matchers.is("max-age=31536000,public"));
        Assert.assertThat(resourceHandler.getResourceBase(), isSameFileAs(new File("WEB-INF/rails.root/public/assets").toPath().toAbsolutePath().toUri().toString()));
    }

    @Test
    public void shouldPassOverHandlingToResourceHandler() throws Exception {
        Mockito.when(systemEnvironment.useCompressedJs()).thenReturn(true);
        String target = "/go/assets/junk";
        Request request = Mockito.mock(Request.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Request baseRequest = Mockito.mock(Request.class);
        AssetsContextHandler.AssetsHandler resourceHandler = Mockito.mock(AssetsHandler.class);
        handler.setHandler(resourceHandler);
        handler.getHandler().handle(target, baseRequest, request, response);
        Mockito.verify(resourceHandler).handle(target, baseRequest, request, response);
    }

    @Test
    public void shouldNotHandleForRails4DevelopmentMode() throws IOException, ServletException {
        Mockito.when(systemEnvironment.useCompressedJs()).thenReturn(false);
        String target = "/go/assets/junk";
        Request request = Mockito.mock(Request.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Request baseRequest = Mockito.mock(Request.class);
        ResourceHandler resourceHandler = Mockito.mock(ResourceHandler.class);
        ReflectionUtil.setField(getHandler(), "resourceHandler", resourceHandler);
        handler.getHandler().handle(target, baseRequest, request, response);
        Mockito.verify(resourceHandler, Mockito.never()).handle(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Request.class), ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class));
    }
}

