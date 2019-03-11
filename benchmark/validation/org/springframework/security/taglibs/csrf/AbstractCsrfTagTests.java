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
package org.springframework.security.taglibs.csrf;


import TagSupport.EVAL_PAGE;
import java.io.UnsupportedEncodingException;
import javax.servlet.jsp.JspException;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;


/**
 *
 *
 * @author Nick Williams
 */
public class AbstractCsrfTagTests {
    public AbstractCsrfTagTests.MockTag tag;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    @Test
    public void noCsrfDoesNotRender() throws UnsupportedEncodingException, JspException {
        this.tag.handleReturn = "shouldNotBeRendered";
        int returned = doEndTag();
        assertThat(returned).as("The returned value is not correct.").isEqualTo(EVAL_PAGE);
        assertThat(this.response.getContentAsString()).withFailMessage("The output value is not correct.").isEqualTo("");
    }

    @Test
    public void hasCsrfRendersReturnedValue() throws UnsupportedEncodingException, JspException {
        CsrfToken token = new DefaultCsrfToken("X-Csrf-Token", "_csrf", "abc123def456ghi789");
        this.request.setAttribute(CsrfToken.class.getName(), token);
        this.tag.handleReturn = "fooBarBazQux";
        int returned = doEndTag();
        assertThat(returned).as("The returned value is not correct.").isEqualTo(EVAL_PAGE);
        assertThat(this.response.getContentAsString()).withFailMessage("The output value is not correct.").isEqualTo("fooBarBazQux");
        assertThat(this.tag.token).as("The token is not correct.").isSameAs(token);
    }

    @Test
    public void hasCsrfRendersDifferentValue() throws UnsupportedEncodingException, JspException {
        CsrfToken token = new DefaultCsrfToken("X-Csrf-Token", "_csrf", "abc123def456ghi789");
        this.request.setAttribute(CsrfToken.class.getName(), token);
        this.tag.handleReturn = "<input type=\"hidden\" />";
        int returned = doEndTag();
        assertThat(returned).as("The returned value is not correct.").isEqualTo(EVAL_PAGE);
        assertThat(this.response.getContentAsString()).withFailMessage("The output value is not correct.").isEqualTo("<input type=\"hidden\" />");
        assertThat(this.tag.token).as("The token is not correct.").isSameAs(token);
    }

    private static class MockTag extends AbstractCsrfTag {
        private CsrfToken token;

        private String handleReturn;

        @Override
        protected String handleToken(CsrfToken token) {
            this.token = token;
            return this.handleReturn;
        }
    }
}

