/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.autoconfigure.web.servlet.error;


import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.testsupport.rule.OutputCapture;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.handler.DispatcherServletWebRequest;


/**
 * Tests for {@link ErrorMvcAutoConfiguration}.
 *
 * @author Brian Clozel
 */
public class ErrorMvcAutoConfigurationTests {
    private WebApplicationContextRunner contextRunner = new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(DispatcherServletAutoConfiguration.class, ErrorMvcAutoConfiguration.class));

    @Rule
    public final OutputCapture output = new OutputCapture();

    @Test
    public void renderContainsViewWithExceptionDetails() throws Exception {
        this.contextRunner.run(( context) -> {
            View errorView = context.getBean("error", .class);
            ErrorAttributes errorAttributes = context.getBean(.class);
            DispatcherServletWebRequest webRequest = createWebRequest(new IllegalStateException("Exception message"), false);
            errorView.render(errorAttributes.getErrorAttributes(webRequest, true), webRequest.getRequest(), webRequest.getResponse());
            String responseString = ((MockHttpServletResponse) (webRequest.getResponse())).getContentAsString();
            assertThat(responseString).contains("<p>This application has no explicit mapping for /error, so you are seeing this as a fallback.</p>").contains("<div>Exception message</div>").contains("<div style='white-space:pre-wrap;'>java.lang.IllegalStateException");
        });
    }

    @Test
    public void renderWhenAlreadyCommittedLogsMessage() {
        this.contextRunner.run(( context) -> {
            View errorView = context.getBean("error", .class);
            ErrorAttributes errorAttributes = context.getBean(.class);
            DispatcherServletWebRequest webRequest = createWebRequest(new IllegalStateException("Exception message"), true);
            errorView.render(errorAttributes.getErrorAttributes(webRequest, true), webRequest.getRequest(), webRequest.getResponse());
            assertThat(this.output.toString()).contains(("Cannot render error page for request [/path] " + (("and exception [Exception message] as the response has " + "already been committed. As a result, the response may ") + "have the wrong status code.")));
        });
    }
}

