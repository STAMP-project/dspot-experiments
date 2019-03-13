/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.boot.web.servlet.error;


import HttpStatus.NOT_FOUND;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import javax.servlet.ServletException;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;


/**
 * Tests for {@link DefaultErrorAttributes}.
 *
 * @author Phillip Webb
 * @author Vedran Pavic
 */
public class DefaultErrorAttributesTests {
    private DefaultErrorAttributes errorAttributes = new DefaultErrorAttributes();

    private MockHttpServletRequest request = new MockHttpServletRequest();

    private WebRequest webRequest = new org.springframework.web.context.request.ServletWebRequest(this.request);

    @Test
    public void includeTimeStamp() {
        Map<String, Object> attributes = this.errorAttributes.getErrorAttributes(this.webRequest, false);
        assertThat(attributes.get("timestamp")).isInstanceOf(Date.class);
    }

    @Test
    public void specificStatusCode() {
        this.request.setAttribute("javax.servlet.error.status_code", 404);
        Map<String, Object> attributes = this.errorAttributes.getErrorAttributes(this.webRequest, false);
        assertThat(attributes.get("error")).isEqualTo(NOT_FOUND.getReasonPhrase());
        assertThat(attributes.get("status")).isEqualTo(404);
    }

    @Test
    public void missingStatusCode() {
        Map<String, Object> attributes = this.errorAttributes.getErrorAttributes(this.webRequest, false);
        assertThat(attributes.get("error")).isEqualTo("None");
        assertThat(attributes.get("status")).isEqualTo(999);
    }

    @Test
    public void mvcError() {
        RuntimeException ex = new RuntimeException("Test");
        ModelAndView modelAndView = this.errorAttributes.resolveException(this.request, null, null, ex);
        this.request.setAttribute("javax.servlet.error.exception", new RuntimeException("Ignored"));
        Map<String, Object> attributes = this.errorAttributes.getErrorAttributes(this.webRequest, false);
        assertThat(this.errorAttributes.getError(this.webRequest)).isSameAs(ex);
        assertThat(modelAndView).isNull();
        assertThat(attributes.get("exception")).isNull();
        assertThat(attributes.get("message")).isEqualTo("Test");
    }

    @Test
    public void servletError() {
        RuntimeException ex = new RuntimeException("Test");
        this.request.setAttribute("javax.servlet.error.exception", ex);
        Map<String, Object> attributes = this.errorAttributes.getErrorAttributes(this.webRequest, false);
        assertThat(this.errorAttributes.getError(this.webRequest)).isSameAs(ex);
        assertThat(attributes.get("exception")).isNull();
        assertThat(attributes.get("message")).isEqualTo("Test");
    }

    @Test
    public void servletMessage() {
        this.request.setAttribute("javax.servlet.error.message", "Test");
        Map<String, Object> attributes = this.errorAttributes.getErrorAttributes(this.webRequest, false);
        assertThat(attributes.get("exception")).isNull();
        assertThat(attributes.get("message")).isEqualTo("Test");
    }

    @Test
    public void nullMessage() {
        this.request.setAttribute("javax.servlet.error.exception", new RuntimeException());
        this.request.setAttribute("javax.servlet.error.message", "Test");
        Map<String, Object> attributes = this.errorAttributes.getErrorAttributes(this.webRequest, false);
        assertThat(attributes.get("exception")).isNull();
        assertThat(attributes.get("message")).isEqualTo("Test");
    }

    @Test
    public void unwrapServletException() {
        RuntimeException ex = new RuntimeException("Test");
        ServletException wrapped = new ServletException(new ServletException(ex));
        this.request.setAttribute("javax.servlet.error.exception", wrapped);
        Map<String, Object> attributes = this.errorAttributes.getErrorAttributes(this.webRequest, false);
        assertThat(this.errorAttributes.getError(this.webRequest)).isSameAs(wrapped);
        assertThat(attributes.get("exception")).isNull();
        assertThat(attributes.get("message")).isEqualTo("Test");
    }

    @Test
    public void getError() {
        Error error = new OutOfMemoryError("Test error");
        this.request.setAttribute("javax.servlet.error.exception", error);
        Map<String, Object> attributes = this.errorAttributes.getErrorAttributes(this.webRequest, false);
        assertThat(this.errorAttributes.getError(this.webRequest)).isSameAs(error);
        assertThat(attributes.get("exception")).isNull();
        assertThat(attributes.get("message")).isEqualTo("Test error");
    }

    @Test
    public void extractBindingResultErrors() {
        BindingResult bindingResult = new MapBindingResult(Collections.singletonMap("a", "b"), "objectName");
        bindingResult.addError(new ObjectError("c", "d"));
        Exception ex = new org.springframework.validation.BindException(bindingResult);
        testBindingResult(bindingResult, ex);
    }

    @Test
    public void extractMethodArgumentNotValidExceptionBindingResultErrors() {
        BindingResult bindingResult = new MapBindingResult(Collections.singletonMap("a", "b"), "objectName");
        bindingResult.addError(new ObjectError("c", "d"));
        Exception ex = new org.springframework.web.bind.MethodArgumentNotValidException(null, bindingResult);
        testBindingResult(bindingResult, ex);
    }

    @Test
    public void withExceptionAttribute() {
        DefaultErrorAttributes errorAttributes = new DefaultErrorAttributes(true);
        RuntimeException ex = new RuntimeException("Test");
        this.request.setAttribute("javax.servlet.error.exception", ex);
        Map<String, Object> attributes = errorAttributes.getErrorAttributes(this.webRequest, false);
        assertThat(attributes.get("exception")).isEqualTo(RuntimeException.class.getName());
        assertThat(attributes.get("message")).isEqualTo("Test");
    }

    @Test
    public void trace() {
        RuntimeException ex = new RuntimeException("Test");
        this.request.setAttribute("javax.servlet.error.exception", ex);
        Map<String, Object> attributes = this.errorAttributes.getErrorAttributes(this.webRequest, true);
        assertThat(attributes.get("trace").toString()).startsWith("java.lang");
    }

    @Test
    public void noTrace() {
        RuntimeException ex = new RuntimeException("Test");
        this.request.setAttribute("javax.servlet.error.exception", ex);
        Map<String, Object> attributes = this.errorAttributes.getErrorAttributes(this.webRequest, false);
        assertThat(attributes.get("trace")).isNull();
    }

    @Test
    public void path() {
        this.request.setAttribute("javax.servlet.error.request_uri", "path");
        Map<String, Object> attributes = this.errorAttributes.getErrorAttributes(this.webRequest, false);
        assertThat(attributes.get("path")).isEqualTo("path");
    }
}

