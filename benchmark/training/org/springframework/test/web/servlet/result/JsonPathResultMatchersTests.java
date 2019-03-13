/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.test.web.servlet.result;


import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.StubMvcResult;


/**
 * Unit tests for {@link JsonPathResultMatchers}.
 *
 * @author Rossen Stoyanchev
 * @author Craig Andrews
 * @author Sam Brannen
 * @author Brian Clozel
 */
public class JsonPathResultMatchersTests {
    private static final String RESPONSE_CONTENT = "{"// 
     + (((((((("'str':         'foo',           "// 
     + "'num':         5,               ")// 
     + "'bool':        true,            ")// 
     + "'arr':         [42],            ")// 
     + "'colorMap':    {'red': 'rojo'}, ")// 
     + "'emptyString': '',              ")// 
     + "'emptyArray':  [],              ")// 
     + "'emptyMap':    {}               ")// 
     + "}");

    private static final StubMvcResult stubMvcResult;

    static {
        try {
            MockHttpServletResponse response = new MockHttpServletResponse();
            response.addHeader("Content-Type", "application/json");
            response.getWriter().print(new String(JsonPathResultMatchersTests.RESPONSE_CONTENT.getBytes("ISO-8859-1")));
            stubMvcResult = new StubMvcResult(null, null, null, null, null, null, response);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Test(expected = AssertionError.class)
    public void valueWithMismatch() throws Exception {
        new JsonPathResultMatchers("$.str").value("bogus").match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test
    public void valueWithDirectMatch() throws Exception {
        new JsonPathResultMatchers("$.str").value("foo").match(JsonPathResultMatchersTests.stubMvcResult);
    }

    // SPR-16587
    @Test
    public void valueWithNumberConversion() throws Exception {
        new JsonPathResultMatchers("$.num").value(5.0F).match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test
    public void valueWithMatcher() throws Exception {
        new JsonPathResultMatchers("$.str").value(Matchers.equalTo("foo")).match(JsonPathResultMatchersTests.stubMvcResult);
    }

    // SPR-16587
    @Test
    public void valueWithMatcherAndNumberConversion() throws Exception {
        new JsonPathResultMatchers("$.num").value(Matchers.equalTo(5.0F), Float.class).match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test(expected = AssertionError.class)
    public void valueWithMatcherAndMismatch() throws Exception {
        new JsonPathResultMatchers("$.str").value(Matchers.equalTo("bogus")).match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test
    public void exists() throws Exception {
        new JsonPathResultMatchers("$.str").exists().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test
    public void existsForAnEmptyArray() throws Exception {
        new JsonPathResultMatchers("$.emptyArray").exists().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test
    public void existsForAnEmptyMap() throws Exception {
        new JsonPathResultMatchers("$.emptyMap").exists().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test(expected = AssertionError.class)
    public void existsNoMatch() throws Exception {
        new JsonPathResultMatchers("$.bogus").exists().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test
    public void doesNotExist() throws Exception {
        new JsonPathResultMatchers("$.bogus").doesNotExist().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test(expected = AssertionError.class)
    public void doesNotExistNoMatch() throws Exception {
        new JsonPathResultMatchers("$.str").doesNotExist().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test(expected = AssertionError.class)
    public void doesNotExistForAnEmptyArray() throws Exception {
        new JsonPathResultMatchers("$.emptyArray").doesNotExist().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test(expected = AssertionError.class)
    public void doesNotExistForAnEmptyMap() throws Exception {
        new JsonPathResultMatchers("$.emptyMap").doesNotExist().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test
    public void isEmptyForAnEmptyString() throws Exception {
        new JsonPathResultMatchers("$.emptyString").isEmpty().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test
    public void isEmptyForAnEmptyArray() throws Exception {
        new JsonPathResultMatchers("$.emptyArray").isEmpty().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test
    public void isEmptyForAnEmptyMap() throws Exception {
        new JsonPathResultMatchers("$.emptyMap").isEmpty().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test
    public void isNotEmptyForString() throws Exception {
        new JsonPathResultMatchers("$.str").isNotEmpty().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test
    public void isNotEmptyForNumber() throws Exception {
        new JsonPathResultMatchers("$.num").isNotEmpty().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test
    public void isNotEmptyForBoolean() throws Exception {
        new JsonPathResultMatchers("$.bool").isNotEmpty().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test
    public void isNotEmptyForArray() throws Exception {
        new JsonPathResultMatchers("$.arr").isNotEmpty().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test
    public void isNotEmptyForMap() throws Exception {
        new JsonPathResultMatchers("$.colorMap").isNotEmpty().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test(expected = AssertionError.class)
    public void isNotEmptyForAnEmptyString() throws Exception {
        new JsonPathResultMatchers("$.emptyString").isNotEmpty().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test(expected = AssertionError.class)
    public void isNotEmptyForAnEmptyArray() throws Exception {
        new JsonPathResultMatchers("$.emptyArray").isNotEmpty().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test(expected = AssertionError.class)
    public void isNotEmptyForAnEmptyMap() throws Exception {
        new JsonPathResultMatchers("$.emptyMap").isNotEmpty().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test
    public void isArray() throws Exception {
        new JsonPathResultMatchers("$.arr").isArray().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test
    public void isArrayForAnEmptyArray() throws Exception {
        new JsonPathResultMatchers("$.emptyArray").isArray().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test(expected = AssertionError.class)
    public void isArrayNoMatch() throws Exception {
        new JsonPathResultMatchers("$.bar").isArray().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test
    public void isMap() throws Exception {
        new JsonPathResultMatchers("$.colorMap").isMap().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test
    public void isMapForAnEmptyMap() throws Exception {
        new JsonPathResultMatchers("$.emptyMap").isMap().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test(expected = AssertionError.class)
    public void isMapNoMatch() throws Exception {
        new JsonPathResultMatchers("$.str").isMap().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test
    public void isBoolean() throws Exception {
        new JsonPathResultMatchers("$.bool").isBoolean().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test(expected = AssertionError.class)
    public void isBooleanNoMatch() throws Exception {
        new JsonPathResultMatchers("$.str").isBoolean().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test
    public void isNumber() throws Exception {
        new JsonPathResultMatchers("$.num").isNumber().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test(expected = AssertionError.class)
    public void isNumberNoMatch() throws Exception {
        new JsonPathResultMatchers("$.str").isNumber().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test
    public void isString() throws Exception {
        new JsonPathResultMatchers("$.str").isString().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test(expected = AssertionError.class)
    public void isStringNoMatch() throws Exception {
        new JsonPathResultMatchers("$.arr").isString().match(JsonPathResultMatchersTests.stubMvcResult);
    }

    @Test(expected = AssertionError.class)
    public void valueWithJsonPrefixNotConfigured() throws Exception {
        String jsonPrefix = "prefix";
        StubMvcResult result = createPrefixedStubMvcResult(jsonPrefix);
        new JsonPathResultMatchers("$.str").value("foo").match(result);
    }

    @Test(expected = AssertionError.class)
    public void valueWithJsonWrongPrefix() throws Exception {
        String jsonPrefix = "prefix";
        StubMvcResult result = createPrefixedStubMvcResult(jsonPrefix);
        new JsonPathResultMatchers("$.str").prefix("wrong").value("foo").match(result);
    }

    @Test
    public void valueWithJsonPrefix() throws Exception {
        String jsonPrefix = "prefix";
        StubMvcResult result = createPrefixedStubMvcResult(jsonPrefix);
        new JsonPathResultMatchers("$.str").prefix(jsonPrefix).value("foo").match(result);
    }

    @Test(expected = AssertionError.class)
    public void prefixWithPayloadNotLongEnough() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.addHeader("Content-Type", "application/json");
        response.getWriter().print(new String("test".getBytes("ISO-8859-1")));
        StubMvcResult result = new StubMvcResult(null, null, null, null, null, null, response);
        new JsonPathResultMatchers("$.str").prefix("prefix").value("foo").match(result);
    }
}

