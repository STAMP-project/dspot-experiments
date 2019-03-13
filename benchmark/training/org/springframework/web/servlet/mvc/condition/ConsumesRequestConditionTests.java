/**
 * Copyright 2002-2012 the original author or authors.
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


import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletRequest;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class ConsumesRequestConditionTests {
    @Test
    public void consumesMatch() {
        ConsumesRequestCondition condition = new ConsumesRequestCondition("text/plain");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("text/plain");
        Assert.assertNotNull(condition.getMatchingCondition(request));
    }

    @Test
    public void negatedConsumesMatch() {
        ConsumesRequestCondition condition = new ConsumesRequestCondition("!text/plain");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("text/plain");
        Assert.assertNull(condition.getMatchingCondition(request));
    }

    @Test
    public void getConsumableMediaTypesNegatedExpression() {
        ConsumesRequestCondition condition = new ConsumesRequestCondition("!application/xml");
        Assert.assertEquals(Collections.emptySet(), condition.getConsumableMediaTypes());
    }

    @Test
    public void consumesWildcardMatch() {
        ConsumesRequestCondition condition = new ConsumesRequestCondition("text/*");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("text/plain");
        Assert.assertNotNull(condition.getMatchingCondition(request));
    }

    @Test
    public void consumesMultipleMatch() {
        ConsumesRequestCondition condition = new ConsumesRequestCondition("text/plain", "application/xml");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("text/plain");
        Assert.assertNotNull(condition.getMatchingCondition(request));
    }

    @Test
    public void consumesSingleNoMatch() {
        ConsumesRequestCondition condition = new ConsumesRequestCondition("text/plain");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/xml");
        Assert.assertNull(condition.getMatchingCondition(request));
    }

    @Test
    public void consumesParseError() {
        ConsumesRequestCondition condition = new ConsumesRequestCondition("text/plain");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("01");
        Assert.assertNull(condition.getMatchingCondition(request));
    }

    @Test
    public void consumesParseErrorWithNegation() {
        ConsumesRequestCondition condition = new ConsumesRequestCondition("!text/plain");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("01");
        Assert.assertNull(condition.getMatchingCondition(request));
    }

    @Test
    public void compareToSingle() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ConsumesRequestCondition condition1 = new ConsumesRequestCondition("text/plain");
        ConsumesRequestCondition condition2 = new ConsumesRequestCondition("text/*");
        int result = condition1.compareTo(condition2, request);
        Assert.assertTrue(("Invalid comparison result: " + result), (result < 0));
        result = condition2.compareTo(condition1, request);
        Assert.assertTrue(("Invalid comparison result: " + result), (result > 0));
    }

    @Test
    public void compareToMultiple() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ConsumesRequestCondition condition1 = new ConsumesRequestCondition("*/*", "text/plain");
        ConsumesRequestCondition condition2 = new ConsumesRequestCondition("text/*", "text/plain;q=0.7");
        int result = condition1.compareTo(condition2, request);
        Assert.assertTrue(("Invalid comparison result: " + result), (result < 0));
        result = condition2.compareTo(condition1, request);
        Assert.assertTrue(("Invalid comparison result: " + result), (result > 0));
    }

    @Test
    public void combine() {
        ConsumesRequestCondition condition1 = new ConsumesRequestCondition("text/plain");
        ConsumesRequestCondition condition2 = new ConsumesRequestCondition("application/xml");
        ConsumesRequestCondition result = condition1.combine(condition2);
        Assert.assertEquals(condition2, result);
    }

    @Test
    public void combineWithDefault() {
        ConsumesRequestCondition condition1 = new ConsumesRequestCondition("text/plain");
        ConsumesRequestCondition condition2 = new ConsumesRequestCondition();
        ConsumesRequestCondition result = condition1.combine(condition2);
        Assert.assertEquals(condition1, result);
    }

    @Test
    public void parseConsumesAndHeaders() {
        String[] consumes = new String[]{ "text/plain" };
        String[] headers = new String[]{ "foo=bar", "content-type=application/xml,application/pdf" };
        ConsumesRequestCondition condition = new ConsumesRequestCondition(consumes, headers);
        assertConditions(condition, "text/plain", "application/xml", "application/pdf");
    }

    @Test
    public void getMatchingCondition() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("text/plain");
        ConsumesRequestCondition condition = new ConsumesRequestCondition("text/plain", "application/xml");
        ConsumesRequestCondition result = condition.getMatchingCondition(request);
        assertConditions(result, "text/plain");
        condition = new ConsumesRequestCondition("application/xml");
        result = condition.getMatchingCondition(request);
        Assert.assertNull(result);
    }
}

