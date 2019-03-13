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


import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition.HeaderExpression;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class HeadersRequestConditionTests {
    @Test
    public void headerEquals() {
        Assert.assertEquals(new HeadersRequestCondition("foo"), new HeadersRequestCondition("foo"));
        Assert.assertEquals(new HeadersRequestCondition("foo"), new HeadersRequestCondition("FOO"));
        Assert.assertNotEquals(new HeadersRequestCondition("foo"), new HeadersRequestCondition("bar"));
        Assert.assertEquals(new HeadersRequestCondition("foo=bar"), new HeadersRequestCondition("foo=bar"));
        Assert.assertEquals(new HeadersRequestCondition("foo=bar"), new HeadersRequestCondition("FOO=bar"));
    }

    @Test
    public void headerPresent() {
        HeadersRequestCondition condition = new HeadersRequestCondition("accept");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Accept", "");
        Assert.assertNotNull(condition.getMatchingCondition(request));
    }

    @Test
    public void headerPresentNoMatch() {
        HeadersRequestCondition condition = new HeadersRequestCondition("foo");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("bar", "");
        Assert.assertNull(condition.getMatchingCondition(request));
    }

    @Test
    public void headerNotPresent() {
        HeadersRequestCondition condition = new HeadersRequestCondition("!accept");
        MockHttpServletRequest request = new MockHttpServletRequest();
        Assert.assertNotNull(condition.getMatchingCondition(request));
    }

    @Test
    public void headerValueMatch() {
        HeadersRequestCondition condition = new HeadersRequestCondition("foo=bar");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("foo", "bar");
        Assert.assertNotNull(condition.getMatchingCondition(request));
    }

    @Test
    public void headerValueNoMatch() {
        HeadersRequestCondition condition = new HeadersRequestCondition("foo=bar");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("foo", "bazz");
        Assert.assertNull(condition.getMatchingCondition(request));
    }

    @Test
    public void headerCaseSensitiveValueMatch() {
        HeadersRequestCondition condition = new HeadersRequestCondition("foo=Bar");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("foo", "bar");
        Assert.assertNull(condition.getMatchingCondition(request));
    }

    @Test
    public void headerValueMatchNegated() {
        HeadersRequestCondition condition = new HeadersRequestCondition("foo!=bar");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("foo", "baz");
        Assert.assertNotNull(condition.getMatchingCondition(request));
    }

    @Test
    public void headerValueNoMatchNegated() {
        HeadersRequestCondition condition = new HeadersRequestCondition("foo!=bar");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("foo", "bar");
        Assert.assertNull(condition.getMatchingCondition(request));
    }

    @Test
    public void compareTo() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        HeadersRequestCondition condition1 = new HeadersRequestCondition("foo", "bar", "baz");
        HeadersRequestCondition condition2 = new HeadersRequestCondition("foo=a", "bar");
        int result = condition1.compareTo(condition2, request);
        Assert.assertTrue(("Invalid comparison result: " + result), (result < 0));
        result = condition2.compareTo(condition1, request);
        Assert.assertTrue(("Invalid comparison result: " + result), (result > 0));
    }

    // SPR-16674
    @Test
    public void compareToWithMoreSpecificMatchByValue() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        HeadersRequestCondition condition1 = new HeadersRequestCondition("foo=a");
        HeadersRequestCondition condition2 = new HeadersRequestCondition("foo");
        int result = condition1.compareTo(condition2, request);
        Assert.assertTrue(("Invalid comparison result: " + result), (result < 0));
        result = condition2.compareTo(condition1, request);
        Assert.assertTrue(("Invalid comparison result: " + result), (result > 0));
    }

    @Test
    public void compareToWithNegatedMatch() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        HeadersRequestCondition condition1 = new HeadersRequestCondition("foo!=a");
        HeadersRequestCondition condition2 = new HeadersRequestCondition("foo");
        Assert.assertEquals("Negated match should not count as more specific", 0, condition1.compareTo(condition2, request));
    }

    @Test
    public void combine() {
        HeadersRequestCondition condition1 = new HeadersRequestCondition("foo=bar");
        HeadersRequestCondition condition2 = new HeadersRequestCondition("foo=baz");
        HeadersRequestCondition result = condition1.combine(condition2);
        Collection<HeaderExpression> conditions = result.getContent();
        Assert.assertEquals(2, conditions.size());
    }

    @Test
    public void getMatchingCondition() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("foo", "bar");
        HeadersRequestCondition condition = new HeadersRequestCondition("foo");
        HeadersRequestCondition result = condition.getMatchingCondition(request);
        Assert.assertEquals(condition, result);
        condition = new HeadersRequestCondition("bar");
        result = condition.getMatchingCondition(request);
        Assert.assertNull(result);
    }
}

