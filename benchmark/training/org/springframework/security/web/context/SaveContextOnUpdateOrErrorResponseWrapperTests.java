/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.security.web.context;


import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContext;


/**
 *
 *
 * @author Rob Winch
 */
@RunWith(MockitoJUnitRunner.class)
public class SaveContextOnUpdateOrErrorResponseWrapperTests {
    @Mock
    private SecurityContext securityContext;

    private MockHttpServletResponse response;

    private SaveContextOnUpdateOrErrorResponseWrapperTests.SaveContextOnUpdateOrErrorResponseWrapperStub wrappedResponse;

    @Test
    public void sendErrorSavesSecurityContext() throws Exception {
        int error = HttpServletResponse.SC_FORBIDDEN;
        wrappedResponse.sendError(error);
        assertThat(wrappedResponse.securityContext).isEqualTo(securityContext);
        assertThat(response.getStatus()).isEqualTo(error);
    }

    @Test
    public void sendErrorSkipsSaveSecurityContextDisables() throws Exception {
        final int error = HttpServletResponse.SC_FORBIDDEN;
        disableSaveOnResponseCommitted();
        wrappedResponse.sendError(error);
        assertThat(wrappedResponse.securityContext).isNull();
        assertThat(response.getStatus()).isEqualTo(error);
    }

    @Test
    public void sendErrorWithMessageSavesSecurityContext() throws Exception {
        int error = HttpServletResponse.SC_FORBIDDEN;
        String message = "Forbidden";
        sendError(error, message);
        assertThat(wrappedResponse.securityContext).isEqualTo(securityContext);
        assertThat(response.getStatus()).isEqualTo(error);
        assertThat(response.getErrorMessage()).isEqualTo(message);
    }

    @Test
    public void sendErrorWithMessageSkipsSaveSecurityContextDisables() throws Exception {
        final int error = HttpServletResponse.SC_FORBIDDEN;
        final String message = "Forbidden";
        disableSaveOnResponseCommitted();
        sendError(error, message);
        assertThat(wrappedResponse.securityContext).isNull();
        assertThat(response.getStatus()).isEqualTo(error);
        assertThat(response.getErrorMessage()).isEqualTo(message);
    }

    @Test
    public void sendRedirectSavesSecurityContext() throws Exception {
        String url = "/location";
        sendRedirect(url);
        assertThat(wrappedResponse.securityContext).isEqualTo(securityContext);
        assertThat(response.getRedirectedUrl()).isEqualTo(url);
    }

    @Test
    public void sendRedirectSkipsSaveSecurityContextDisables() throws Exception {
        final String url = "/location";
        disableSaveOnResponseCommitted();
        sendRedirect(url);
        assertThat(wrappedResponse.securityContext).isNull();
        assertThat(response.getRedirectedUrl()).isEqualTo(url);
    }

    @Test
    public void outputFlushSavesSecurityContext() throws Exception {
        getOutputStream().flush();
        assertThat(wrappedResponse.securityContext).isEqualTo(securityContext);
    }

    @Test
    public void outputFlushSkipsSaveSecurityContextDisables() throws Exception {
        disableSaveOnResponseCommitted();
        getOutputStream().flush();
        assertThat(wrappedResponse.securityContext).isNull();
    }

    @Test
    public void outputCloseSavesSecurityContext() throws Exception {
        getOutputStream().close();
        assertThat(wrappedResponse.securityContext).isEqualTo(securityContext);
    }

    @Test
    public void outputCloseSkipsSaveSecurityContextDisables() throws Exception {
        disableSaveOnResponseCommitted();
        getOutputStream().close();
        assertThat(wrappedResponse.securityContext).isNull();
    }

    @Test
    public void writerFlushSavesSecurityContext() throws Exception {
        getWriter().flush();
        assertThat(wrappedResponse.securityContext).isEqualTo(securityContext);
    }

    @Test
    public void writerFlushSkipsSaveSecurityContextDisables() throws Exception {
        disableSaveOnResponseCommitted();
        getWriter().flush();
        assertThat(wrappedResponse.securityContext).isNull();
    }

    @Test
    public void writerCloseSavesSecurityContext() throws Exception {
        getWriter().close();
        assertThat(wrappedResponse.securityContext).isEqualTo(securityContext);
    }

    @Test
    public void writerCloseSkipsSaveSecurityContextDisables() throws Exception {
        disableSaveOnResponseCommitted();
        getWriter().close();
        assertThat(wrappedResponse.securityContext).isNull();
    }

    @Test
    public void flushBufferSavesSecurityContext() throws Exception {
        flushBuffer();
        assertThat(wrappedResponse.securityContext).isEqualTo(securityContext);
    }

    @Test
    public void flushBufferSkipsSaveSecurityContextDisables() throws Exception {
        disableSaveOnResponseCommitted();
        flushBuffer();
        assertThat(wrappedResponse.securityContext).isNull();
    }

    private static class SaveContextOnUpdateOrErrorResponseWrapperStub extends SaveContextOnUpdateOrErrorResponseWrapper {
        private SecurityContext securityContext;

        public SaveContextOnUpdateOrErrorResponseWrapperStub(HttpServletResponse response, boolean disableUrlRewriting) {
            super(response, disableUrlRewriting);
        }

        @Override
        protected void saveContext(SecurityContext context) {
            securityContext = context;
        }
    }
}

