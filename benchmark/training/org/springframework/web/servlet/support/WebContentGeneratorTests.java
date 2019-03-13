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
package org.springframework.web.servlet.support;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletResponse;


/**
 * Unit tests for {@link WebContentGenerator}.
 *
 * @author Rossen Stoyanchev
 */
public class WebContentGeneratorTests {
    @Test
    public void getAllowHeaderWithConstructorTrue() throws Exception {
        WebContentGenerator generator = new WebContentGeneratorTests.TestWebContentGenerator(true);
        Assert.assertEquals("GET,HEAD,POST,OPTIONS", generator.getAllowHeader());
    }

    @Test
    public void getAllowHeaderWithConstructorFalse() throws Exception {
        WebContentGenerator generator = new WebContentGeneratorTests.TestWebContentGenerator(false);
        Assert.assertEquals("GET,HEAD,POST,PUT,PATCH,DELETE,OPTIONS", generator.getAllowHeader());
    }

    @Test
    public void getAllowHeaderWithSupportedMethodsConstructor() throws Exception {
        WebContentGenerator generator = new WebContentGeneratorTests.TestWebContentGenerator("POST");
        Assert.assertEquals("POST,OPTIONS", generator.getAllowHeader());
    }

    @Test
    public void getAllowHeaderWithSupportedMethodsSetter() throws Exception {
        WebContentGenerator generator = new WebContentGeneratorTests.TestWebContentGenerator();
        generator.setSupportedMethods("POST");
        Assert.assertEquals("POST,OPTIONS", generator.getAllowHeader());
    }

    @Test
    public void getAllowHeaderWithSupportedMethodsSetterEmpty() throws Exception {
        WebContentGenerator generator = new WebContentGeneratorTests.TestWebContentGenerator();
        generator.setSupportedMethods();
        Assert.assertEquals("Effectively \"no restriction\" on supported methods", "GET,HEAD,POST,PUT,PATCH,DELETE,OPTIONS", generator.getAllowHeader());
    }

    @Test
    public void varyHeaderNone() throws Exception {
        WebContentGenerator generator = new WebContentGeneratorTests.TestWebContentGenerator();
        MockHttpServletResponse response = new MockHttpServletResponse();
        generator.prepareResponse(response);
        Assert.assertNull(response.getHeader("Vary"));
    }

    @Test
    public void varyHeader() throws Exception {
        String[] configuredValues = new String[]{ "Accept-Language", "User-Agent" };
        String[] responseValues = new String[]{  };
        String[] expected = new String[]{ "Accept-Language", "User-Agent" };
        testVaryHeader(configuredValues, responseValues, expected);
    }

    @Test
    public void varyHeaderWithExistingWildcard() throws Exception {
        String[] configuredValues = new String[]{ "Accept-Language" };
        String[] responseValues = new String[]{ "*" };
        String[] expected = new String[]{ "*" };
        testVaryHeader(configuredValues, responseValues, expected);
    }

    @Test
    public void varyHeaderWithExistingCommaValues() throws Exception {
        String[] configuredValues = new String[]{ "Accept-Language", "User-Agent" };
        String[] responseValues = new String[]{ "Accept-Encoding", "Accept-Language" };
        String[] expected = new String[]{ "Accept-Encoding", "Accept-Language", "User-Agent" };
        testVaryHeader(configuredValues, responseValues, expected);
    }

    @Test
    public void varyHeaderWithExistingCommaSeparatedValues() throws Exception {
        String[] configuredValues = new String[]{ "Accept-Language", "User-Agent" };
        String[] responseValues = new String[]{ "Accept-Encoding, Accept-Language" };
        String[] expected = new String[]{ "Accept-Encoding, Accept-Language", "User-Agent" };
        testVaryHeader(configuredValues, responseValues, expected);
    }

    private static class TestWebContentGenerator extends WebContentGenerator {
        public TestWebContentGenerator() {
        }

        public TestWebContentGenerator(boolean restrictDefaultSupportedMethods) {
            super(restrictDefaultSupportedMethods);
        }

        public TestWebContentGenerator(String... supportedMethods) {
            super(supportedMethods);
        }
    }
}

