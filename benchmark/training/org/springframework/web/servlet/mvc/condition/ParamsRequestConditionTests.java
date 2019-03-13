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
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition.ParamExpression;


/**
 * Unit tests for {@link ParamsRequestCondition}.
 *
 * @author Arjen Poutsma
 */
public class ParamsRequestConditionTests {
    @Test
    public void paramEquals() {
        Assert.assertEquals(new ParamsRequestCondition("foo"), new ParamsRequestCondition("foo"));
        Assert.assertFalse(new ParamsRequestCondition("foo").equals(new ParamsRequestCondition("bar")));
        Assert.assertFalse(new ParamsRequestCondition("foo").equals(new ParamsRequestCondition("FOO")));
        Assert.assertEquals(new ParamsRequestCondition("foo=bar"), new ParamsRequestCondition("foo=bar"));
        Assert.assertFalse(new ParamsRequestCondition("foo=bar").equals(new ParamsRequestCondition("FOO=bar")));
    }

    @Test
    public void paramPresent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("foo", "");
        Assert.assertNotNull(new ParamsRequestCondition("foo").getMatchingCondition(request));
    }

    // SPR-15831
    @Test
    public void paramPresentNullValue() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("foo", ((String) (null)));
        Assert.assertNotNull(new ParamsRequestCondition("foo").getMatchingCondition(request));
    }

    @Test
    public void paramPresentNoMatch() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("bar", "");
        Assert.assertNull(new ParamsRequestCondition("foo").getMatchingCondition(request));
    }

    @Test
    public void paramNotPresent() {
        ParamsRequestCondition condition = new ParamsRequestCondition("!foo");
        MockHttpServletRequest request = new MockHttpServletRequest();
        Assert.assertNotNull(condition.getMatchingCondition(request));
    }

    @Test
    public void paramValueMatch() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("foo", "bar");
        Assert.assertNotNull(new ParamsRequestCondition("foo=bar").getMatchingCondition(request));
    }

    @Test
    public void paramValueNoMatch() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("foo", "bazz");
        Assert.assertNull(new ParamsRequestCondition("foo=bar").getMatchingCondition(request));
    }

    @Test
    public void compareTo() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ParamsRequestCondition condition1 = new ParamsRequestCondition("foo", "bar", "baz");
        ParamsRequestCondition condition2 = new ParamsRequestCondition("foo=a", "bar");
        int result = condition1.compareTo(condition2, request);
        Assert.assertTrue(("Invalid comparison result: " + result), (result < 0));
        result = condition2.compareTo(condition1, request);
        Assert.assertTrue(("Invalid comparison result: " + result), (result > 0));
    }

    // SPR-16674
    @Test
    public void compareToWithMoreSpecificMatchByValue() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ParamsRequestCondition condition1 = new ParamsRequestCondition("response_type=code");
        ParamsRequestCondition condition2 = new ParamsRequestCondition("response_type");
        int result = condition1.compareTo(condition2, request);
        Assert.assertTrue(("Invalid comparison result: " + result), (result < 0));
    }

    @Test
    public void compareToWithNegatedMatch() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ParamsRequestCondition condition1 = new ParamsRequestCondition("response_type!=code");
        ParamsRequestCondition condition2 = new ParamsRequestCondition("response_type");
        Assert.assertEquals("Negated match should not count as more specific", 0, condition1.compareTo(condition2, request));
    }

    @Test
    public void combine() {
        ParamsRequestCondition condition1 = new ParamsRequestCondition("foo=bar");
        ParamsRequestCondition condition2 = new ParamsRequestCondition("foo=baz");
        ParamsRequestCondition result = condition1.combine(condition2);
        Collection<ParamExpression> conditions = result.getContent();
        Assert.assertEquals(2, conditions.size());
    }
}

