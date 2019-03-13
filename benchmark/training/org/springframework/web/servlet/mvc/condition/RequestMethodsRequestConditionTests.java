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
package org.springframework.web.servlet.mvc.condition;


import DispatcherType.ERROR;
import HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 *
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 */
public class RequestMethodsRequestConditionTests {
    @Test
    public void getMatchingCondition() {
        testMatch(new RequestMethodsRequestCondition(GET), GET);
        testMatch(new RequestMethodsRequestCondition(GET, POST), GET);
        testNoMatch(new RequestMethodsRequestCondition(GET), POST);
    }

    @Test
    public void getMatchingConditionWithHttpHead() {
        testMatch(new RequestMethodsRequestCondition(HEAD), HEAD);
        testMatch(new RequestMethodsRequestCondition(GET), GET);
        testNoMatch(new RequestMethodsRequestCondition(POST), HEAD);
    }

    @Test
    public void getMatchingConditionWithEmptyConditions() {
        RequestMethodsRequestCondition condition = new RequestMethodsRequestCondition();
        for (RequestMethod method : RequestMethod.values()) {
            if (method != (OPTIONS)) {
                HttpServletRequest request = new MockHttpServletRequest(method.name(), "");
                Assert.assertNotNull(condition.getMatchingCondition(request));
            }
        }
        testNoMatch(condition, OPTIONS);
    }

    @Test
    public void getMatchingConditionWithCustomMethod() {
        HttpServletRequest request = new MockHttpServletRequest("PROPFIND", "");
        Assert.assertNotNull(new RequestMethodsRequestCondition().getMatchingCondition(request));
        Assert.assertNull(new RequestMethodsRequestCondition(GET, POST).getMatchingCondition(request));
    }

    @Test
    public void getMatchingConditionWithCorsPreFlight() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "");
        request.addHeader("Origin", "http://example.com");
        request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "PUT");
        Assert.assertNotNull(new RequestMethodsRequestCondition().getMatchingCondition(request));
        Assert.assertNotNull(new RequestMethodsRequestCondition(PUT).getMatchingCondition(request));
        Assert.assertNull(new RequestMethodsRequestCondition(DELETE).getMatchingCondition(request));
    }

    // SPR-14410
    @Test
    public void getMatchingConditionWithHttpOptionsInErrorDispatch() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/path");
        request.setDispatcherType(ERROR);
        RequestMethodsRequestCondition condition = new RequestMethodsRequestCondition();
        RequestMethodsRequestCondition result = condition.getMatchingCondition(request);
        Assert.assertNotNull(result);
        Assert.assertSame(condition, result);
    }

    @Test
    public void compareTo() {
        RequestMethodsRequestCondition c1 = new RequestMethodsRequestCondition(GET, HEAD);
        RequestMethodsRequestCondition c2 = new RequestMethodsRequestCondition(POST);
        RequestMethodsRequestCondition c3 = new RequestMethodsRequestCondition();
        MockHttpServletRequest request = new MockHttpServletRequest();
        int result = c1.compareTo(c2, request);
        Assert.assertTrue(("Invalid comparison result: " + result), (result < 0));
        result = c2.compareTo(c1, request);
        Assert.assertTrue(("Invalid comparison result: " + result), (result > 0));
        result = c2.compareTo(c3, request);
        Assert.assertTrue(("Invalid comparison result: " + result), (result < 0));
        result = c1.compareTo(c1, request);
        Assert.assertEquals("Invalid comparison result ", 0, result);
    }

    @Test
    public void combine() {
        RequestMethodsRequestCondition condition1 = new RequestMethodsRequestCondition(GET);
        RequestMethodsRequestCondition condition2 = new RequestMethodsRequestCondition(POST);
        RequestMethodsRequestCondition result = condition1.combine(condition2);
        Assert.assertEquals(2, result.getContent().size());
    }
}

